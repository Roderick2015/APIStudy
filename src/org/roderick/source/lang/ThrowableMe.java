package org.roderick.source.lang;

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
	 * @param cause 上一个引起该异常的信息
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
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
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
	 * 为什么要包装一下?
	 */
	private void printStackTrace(PrintStream s) {
		printStackTrace(new WrappedPrintStream(s));
	}
	
	/**
	 * 打印异常信息
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
			
		}
	}
	
	/**
	 * 封闭的异常？
	 */
	private void printEnclosedStackTrace(PrintStreamOrWriter s,
										 StackTraceElementMe[] enclosingTrace,
										 String caption,
										 String prefix,
										 Set<ThrowableMe> dejaVu) {
		assert Thread.holdsLock(s.lock()); //作用？
		if (dejaVu.contains(this)) {
			s.println("\t[CIRCULAR REFERENCE:" + this + "]");
		} else {
			
		}
		
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
	
	private native ThrowableMe fillInStackTrace(int dummy);
	
	public StackTraceElementMe[] getStackTrace() {
		return getOurStackTrace().clone();
	}
	
	/**
	 * 获取栈轨迹，一个private方法也要加个锁？
	 */
	private synchronized StackTraceElementMe[] getOurStackTrace() {
		if (stackTrace == UNASSIGNED_STACK ||
			(stackTrace == null && backtrace != null)) {
			int depth = getStackTraceDepth();
			stackTrace = new StackTraceElementMe[depth];
			for (int i = 0; i < depth; i++)
				stackTrace[i] = getStackTraceElement(i);
		} else if (stackTrace == null) { //什么情况下触发
			return UNASSIGNED_STACK;
		}
		return stackTrace;
	}
	
	/**
     * Returns the number of elements in the stack trace (or 0 if the stack
     * trace is unavailable).
     */
	native int getStackTraceDepth();
	native StackTraceElementMe getStackTraceElement(int index);
}
