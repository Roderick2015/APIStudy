package org.roderick.source.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class ThrowableMe implements Serializable {
	private static final long serialVersionUID = 561728905951546684L;
	
	//native方法在这个变量中存储相关的栈回溯信息
	private transient Object backtrace;
	private String detailMessage;
	
	private static class SentinelHolder {
		public static final StackTraceElementMe STACK_TRACE_ELEMENT_SENTINEL =
				new StackTraceElementMe("", "", null, Integer.MIN_VALUE); //为什么传整型最小值
		public static final StackTraceElementMe[] STACK_TRACE_SENTINEL =
				new StackTraceElementMe[] {STACK_TRACE_ELEMENT_SENTINEL};
	}
	
	/**
	 * 空栈
	 */
	public static final StackTraceElementMe[] UNASSIGNED_STACK = new StackTraceElementMe[0];
	private ThrowableMe cause = this;
	private StackTraceElementMe[] stackTrace = UNASSIGNED_STACK;
	
	/**
	 * 空的suppressed异常列表list，是只读的，向其中添加元素会报异常
	 */
	private static final List<ThrowableMe> SUPPRESSED_SENTINEL = 
			Collections.unmodifiableList(new ArrayList<ThrowableMe>(0));
	private List<ThrowableMe> suppressedExceptions = SUPPRESSED_SENTINEL;
	
	/** Message for trying to suppress a null exception. */
    private static final String NULL_CAUSE_MESSAGE = "Cannot suppress a null exception.";

    /** Message for trying to suppress oneself. */
    private static final String SELF_SUPPRESSION_MESSAGE = "Self-suppression not permitted";

    /** Caption  for labeling causative exception stack traces */
    private static final String CAUSE_CAPTION = "Caused by: ";

    /** Caption for labeling suppressed exception stack traces */
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";
	
	public ThrowableMe() {
		fillInStackTrace();
	}
	
	public ThrowableMe(String message) {
		fillInStackTrace();
		detailMessage = message;
	}
	
	/**
	 * @param message
	 * @param cause 上一个引起该异常的信息，如果调用的前面两个构造函数，可以通过initCause初始化一次，只能初始化一次
	 */
	public ThrowableMe(String message, ThrowableMe cause) {
		fillInStackTrace();
		detailMessage = message;
		this.cause = cause;
	}
	
	public ThrowableMe(ThrowableMe cause) {
		fillInStackTrace();
		detailMessage = (cause == null) ? null : cause.toString();
		this.cause = cause;
	}
	
	/**
	 * 第五个构造函数，1.7新增
	 * 这个构造函数一般是给try-with-resources提供的，
	 * 捕捉suppressed异常添加到异常链中，而不会初始化之前的异常
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression  设为false的话不会捕捉suppressed异常
	 * @param writableStackTrace 设为false则不会初始化操作栈
	 */
	public ThrowableMe(String message, ThrowableMe cause,
					   boolean enableSuppression,
					   boolean writableStackTrace) {
		if (writableStackTrace)
			fillInStackTrace();
		else
			stackTrace = null;
		
		detailMessage = message;
		this.cause = cause;
		if (!enableSuppression)
			suppressedExceptions = null;
	}
	
	public String getMessage() {
        return detailMessage;
    }
	
	/**
	 * 在需要国际化语言支持时，覆写该方法，否则返回detailMessage的内容
	 */
	public String getLocalizedMessage() {
        return getMessage();
    }
	
	/**
	 * 没有设置起因，则返回null
	 */
	public synchronized ThrowableMe getCause() {
		return (cause == this ? null : cause);
	}
	
	public synchronized ThrowableMe initCause(ThrowableMe cause) {
		if (this.cause != this) //已经初始化过起因，不能再次通过该方法赋值
			/*throw new IllegalStateException("Can't overwrite cause with " +
                    Objects.toString(cause, "a null"), this);*/
		if (cause == this) //不能赋值自己
		//	throw new IllegalArgumentException("Self-causation not permitted", this);
		this.cause = cause;
		return this;
	}
	
	@Override
	public String toString() {
		String s = getClass().getName();
		String message = getLocalizedMessage();
		return  (message != null) ? (s + ":" + message) : s; //message为空时，只返回类名
	}
	
	public void printStackTrace() {
		printStackTrace(System.err);
	}
	
	/**
	 * 包装一下，从而使用统一的父类    Stream流方法
	 */
	private void printStackTrace(PrintStream s) {
		printStackTrace(new WrappedPrintStream(s));
	}
	
	/**
	 * 打印异常信息，Stream和Writer的公用方法
	 */
	private void printStackTrace(PrintStreamOrWriter s) {
		//防止子类恶意覆写equals方法，而使用了IdentityHashMap，即key只有在key1=key2时，才会相等，而不是equals
		//newSetFromMap根据map而生成的set集合，map中的value必须是布尔值，用于标识该元素是否存在
		//从实现来看，该内部类中内嵌了一个map
		//可通过ConcurrentHashMap直接生成ConcurrentHashSet?
		Set<ThrowableMe> dejaVu = 
				Collections.newSetFromMap(new IdentityHashMap<ThrowableMe, Boolean>());
		dejaVu.add(this);
		
		synchronized (s.lock()) {
			s.println(this);
			StackTraceElementMe[] trace = getOurStackTrace();
			for (StackTraceElementMe traceElement : trace)
				s.println("\tat" + traceElement);
			
			for (ThrowableMe se : getSuppressed()) //进入printEnclosedStackTrace方法后，会递归打印异常链的信息
				se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);
			
			ThrowableMe ourCause = getCause();
			if (ourCause != null)
				ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, "", dejaVu);
		}
	}
	
	/**
	 * enclosed StackTrace
	 */
	private void printEnclosedStackTrace(PrintStreamOrWriter s,
										 StackTraceElementMe[] enclosingTrace,
										 String caption,
										 String prefix,
										 Set<ThrowableMe> dejaVu) {
		assert Thread.holdsLock(s.lock()); //判断当前线程是否拿着锁
		if (dejaVu.contains(this)) { //如果已经调用过
			s.println("\t[CIRCULAR REFERENCE:" + this + "]");
		} else {
			dejaVu.add(this);
			// Compute number of frames in common between this and enclosing trace
			StackTraceElementMe[] trace = getOurStackTrace();
			int m = trace.length - 1;
			int n = enclosingTrace.length - 1;
			while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
				m--;
				n--;
			}
			int framesInCommon = trace.length - 1 - m; //栈帧的共同区域？
			s.println(prefix + caption + this);
			//\tat和\t是对齐用的吗
			for (int i = 0; i <= m; i++)
				s.println(prefix + "\tat " + trace[i]);
			if (framesInCommon != 0)
				s.println(prefix + "\t... " + framesInCommon + " more");
			
			//打印被抛弃的异常，调用这些异常的printEnclosedStackTrace方法，如果存在异常链，会递归打印
			for (ThrowableMe se : getSuppressed())
				se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION, prefix + "\t", dejaVu);
			
			//打印起因
			ThrowableMe ourCause = getCause();
			if (ourCause != null) //如是传入当前的trace
				ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, prefix, dejaVu);
		}
		
	}

	/**
	 * 用writer打印
	 */
	private void printStackTrace(PrintWriter s) {
		printStackTrace(new WrappedPrintWriter(s));
	}
	
	private abstract static class PrintStreamOrWriter {
		abstract Object lock();
		abstract void println(Object o);
	}
	
	private static class WrappedPrintStream extends PrintStreamOrWriter {
		private final PrintStream printStream;
		
		WrappedPrintStream(PrintStream printStream) {
			this.printStream = printStream;
		}
		
		@Override
		Object lock() {
			return this.printStream;
		}

		@Override
		void println(Object o) {
			this.printStream.println(o);
		}
	}
	
	private static class WrappedPrintWriter extends PrintStreamOrWriter {
		private final PrintWriter printWriter;

		WrappedPrintWriter(PrintWriter printWriter) {
			this.printWriter = printWriter;
		}
		
		@Override
		Object lock() {
			return this.printWriter;
		}

		@Override
		void println(Object o) {
			this.printWriter.println(o);
		}
	}
	
	/**
	 * 初始化栈帧
	 */
	public synchronized ThrowableMe fillInStackTrace() {
		if (stackTrace != null ||
			backtrace != null) {
			fillInStackTrace(0);
			stackTrace = UNASSIGNED_STACK;
		}
		return this;
	}
	
	//这些native方法如果要调试，可自行编写JNI代码
	private native ThrowableMe fillInStackTrace(int dummy);
	
	/**
	 * clone出来的，可以改，给你用
	 */
	public StackTraceElementMe[] getStackTrace() {
		return getOurStackTrace().clone();
	}
	
	/**
	 * 获取异常的栈信息，这个锁的意义？
	 */
	private synchronized StackTraceElementMe[] getOurStackTrace() {
		if (stackTrace == UNASSIGNED_STACK ||
			(stackTrace == null && backtrace != null)) {
			int depth = getStackTraceDepth();
			stackTrace = new StackTraceElementMe[depth];
			for (int i = 0; i < depth; i++)
				stackTrace[i] = getStackTraceElement(i);
		} else if (stackTrace == null) {
			return UNASSIGNED_STACK;
		}
		return stackTrace;
	}
	
	public void setStackTrace(StackTraceElementMe[] stackTrace) {
		StackTraceElementMe[] defensiveCopy = stackTrace.clone();
		for (int i = 0; i < defensiveCopy.length; i++) {
			if (defensiveCopy[i] == null)
				throw new NullPointerException("stackTrace[" + i + "]");
		}
		
		synchronized (this) {
			if (this.stackTrace == null && //stackTrace为null代表Immutable stack?
				backtrace == null)
				return;
			this.stackTrace = defensiveCopy;
		}
	}
	
	/**
     * Returns the number of elements in the stack trace (or 0 if the stack
     * trace is unavailable).
     */
	native int getStackTraceDepth();
	native StackTraceElementMe getStackTraceElement(int index);
	
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject(); // read in all fields
		if (suppressedExceptions != null) {
			List<ThrowableMe> suppressed = null;
			if (suppressedExceptions.isEmpty()) {
				// Use the sentinel for a zero-length list
				suppressed = SUPPRESSED_SENTINEL;
			} else { // Copy Throwables to new list
				suppressed = new ArrayList<>(1);
				for (ThrowableMe t : suppressedExceptions) {
					// Enforce constraints on suppressed exceptions in
					// case of corrupt or malicious stream.
					if (t == null)
						throw new NullPointerException(NULL_CAUSE_MESSAGE);
					if (t == this)
						throw new IllegalArgumentException(SELF_SUPPRESSION_MESSAGE);
					suppressed.add(t);
				}
			}
			suppressedExceptions = suppressed;
		} // else a null suppressedExceptions field remains null

		/*
		 * For zero-length stack traces, use a clone of UNASSIGNED_STACK rather
		 * than UNASSIGNED_STACK itself to allow identity comparison against
		 * UNASSIGNED_STACK in getOurStackTrace. The identity of
		 * UNASSIGNED_STACK in stackTrace indicates to the getOurStackTrace
		 * method that the stackTrace needs to be constructed from the
		 * information in backtrace.
		 */
		if (stackTrace != null) {
			if (stackTrace.length == 0) {
				stackTrace = UNASSIGNED_STACK.clone();
			} else if (stackTrace.length == 1 &&
			// Check for the marker of an immutable stack trace
					SentinelHolder.STACK_TRACE_ELEMENT_SENTINEL.equals(stackTrace[0])) {
				stackTrace = null;
			} else { // Verify stack trace elements are non-null.
				for (StackTraceElementMe ste : stackTrace) {
					if (ste == null)
						throw new NullPointerException("null StackTraceElement in serial stream. ");
				}
			}
		} else {
			// A null stackTrace field in the serial form can result
			// from an exception serialized without that field in
			// older JDK releases; treat such exceptions as having
			// empty stack traces.
			stackTrace = UNASSIGNED_STACK.clone();
		}
	}

	private synchronized void writeObject(ObjectOutputStream s) throws IOException {
		// Ensure that the stackTrace field is initialized to a
		// non-null value, if appropriate. As of JDK 7, a null stack
		// trace field is a valid value indicating the stack trace
		// should not be set.
		getOurStackTrace();

		StackTraceElementMe[] oldStackTrace = stackTrace;
		try {
			if (stackTrace == null)
				stackTrace = SentinelHolder.STACK_TRACE_SENTINEL;
			s.defaultWriteObject();
		} finally {
			stackTrace = oldStackTrace;
		}
	}
	
	/**
	 * 手动将指定的Suppressed Exception加入到异常链中
	 */
	public final synchronized void addSuppressed(ThrowableMe exception) {
		//if (exception == this)
          //  throw new IllegalArgumentException(SELF_SUPPRESSION_MESSAGE, exception);

        if (exception == null)
            throw new NullPointerException(NULL_CAUSE_MESSAGE);
		
        if (suppressedExceptions == null) //等于null表示Suppressed Exception不可用，见第五个构造函数
        	return;
        
        if (suppressedExceptions == SUPPRESSED_SENTINEL)
        	suppressedExceptions = new ArrayList<>(1); //初始化
        
        suppressedExceptions.add(exception);
	}
	
	private static final ThrowableMe[] EMPTY_THROWABLE_ARRAY = new ThrowableMe[0];

	/**
	 * 获取通过try-with-resources捕捉到的异常
	 */
	public final synchronized ThrowableMe[] getSuppressed() {
		if (suppressedExceptions == SUPPRESSED_SENTINEL ||
			suppressedExceptions == null) { //等于null表示Suppressed Exception不可用，见第五个构造函数
			return EMPTY_THROWABLE_ARRAY;
		} else 
			return suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY); //转为EMPTY_THROWABLE_ARRAY格式的数组
	}
}
