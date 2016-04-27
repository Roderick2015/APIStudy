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
	
	//native��������������д洢��ص�ջ������Ϣ
	private transient Object backtrace;
	private String detailMessage;
	
	private static class SentinelHolder {
		public static final StackTraceElementMe STACK_TRACE_ELEMENT_SENTINEL =
				new StackTraceElementMe("", "", null, Integer.MIN_VALUE); //Ϊʲô��������Сֵ
		public static final StackTraceElementMe[] STACK_TRACE_SENTINEL =
				new StackTraceElementMe[] {STACK_TRACE_ELEMENT_SENTINEL};
	}
	
	/**
	 * ��ջ
	 */
	public static final StackTraceElementMe[] UNASSIGNED_STACK = new StackTraceElementMe[0];
	private ThrowableMe cause = this;
	private StackTraceElementMe[] stackTrace = UNASSIGNED_STACK;
	
	/**
	 * �յ�suppressed�쳣�б�list����ֻ���ģ����������Ԫ�ػᱨ�쳣
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
	 * @param cause ��һ��������쳣����Ϣ��������õ�ǰ���������캯��������ͨ��initCause��ʼ��һ�Σ�ֻ�ܳ�ʼ��һ��
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
	 * ��������캯����1.7����
	 * ������캯��һ���Ǹ�try-with-resources�ṩ�ģ�
	 * ��׽suppressed�쳣��ӵ��쳣���У��������ʼ��֮ǰ���쳣
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression  ��Ϊfalse�Ļ����Ჶ׽suppressed�쳣
	 * @param writableStackTrace ��Ϊfalse�򲻻��ʼ������ջ
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
	 * ����Ҫ���ʻ�����֧��ʱ����д�÷��������򷵻�detailMessage������
	 */
	public String getLocalizedMessage() {
        return getMessage();
    }
	
	/**
	 * û�����������򷵻�null
	 */
	public synchronized ThrowableMe getCause() {
		return (cause == this ? null : cause);
	}
	
	public synchronized ThrowableMe initCause(ThrowableMe cause) {
		if (this.cause != this) //�Ѿ���ʼ�������򣬲����ٴ�ͨ���÷�����ֵ
			/*throw new IllegalStateException("Can't overwrite cause with " +
                    Objects.toString(cause, "a null"), this);*/
		if (cause == this) //���ܸ�ֵ�Լ�
		//	throw new IllegalArgumentException("Self-causation not permitted", this);
		this.cause = cause;
		return this;
	}
	
	@Override
	public String toString() {
		String s = getClass().getName();
		String message = getLocalizedMessage();
		return  (message != null) ? (s + ":" + message) : s; //messageΪ��ʱ��ֻ��������
	}
	
	public void printStackTrace() {
		printStackTrace(System.err);
	}
	
	/**
	 * ��װһ�£��Ӷ�ʹ��ͳһ�ĸ���    Stream������
	 */
	private void printStackTrace(PrintStream s) {
		printStackTrace(new WrappedPrintStream(s));
	}
	
	/**
	 * ��ӡ�쳣��Ϣ��Stream��Writer�Ĺ��÷���
	 */
	private void printStackTrace(PrintStreamOrWriter s) {
		//��ֹ������⸲дequals��������ʹ����IdentityHashMap����keyֻ����key1=key2ʱ���Ż���ȣ�������equals
		//newSetFromMap����map�����ɵ�set���ϣ�map�е�value�����ǲ���ֵ�����ڱ�ʶ��Ԫ���Ƿ����
		//��ʵ�����������ڲ�������Ƕ��һ��map
		//��ͨ��ConcurrentHashMapֱ������ConcurrentHashSet?
		Set<ThrowableMe> dejaVu = 
				Collections.newSetFromMap(new IdentityHashMap<ThrowableMe, Boolean>());
		dejaVu.add(this);
		
		synchronized (s.lock()) {
			s.println(this);
			StackTraceElementMe[] trace = getOurStackTrace();
			for (StackTraceElementMe traceElement : trace)
				s.println("\tat" + traceElement);
			
			for (ThrowableMe se : getSuppressed()) //����printEnclosedStackTrace�����󣬻�ݹ��ӡ�쳣������Ϣ
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
		assert Thread.holdsLock(s.lock()); //�жϵ�ǰ�߳��Ƿ�������
		if (dejaVu.contains(this)) { //����Ѿ����ù�
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
			int framesInCommon = trace.length - 1 - m; //ջ֡�Ĺ�ͬ����
			s.println(prefix + caption + this);
			//\tat��\t�Ƕ����õ���
			for (int i = 0; i <= m; i++)
				s.println(prefix + "\tat " + trace[i]);
			if (framesInCommon != 0)
				s.println(prefix + "\t... " + framesInCommon + " more");
			
			//��ӡ���������쳣��������Щ�쳣��printEnclosedStackTrace��������������쳣������ݹ��ӡ
			for (ThrowableMe se : getSuppressed())
				se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION, prefix + "\t", dejaVu);
			
			//��ӡ����
			ThrowableMe ourCause = getCause();
			if (ourCause != null) //���Ǵ��뵱ǰ��trace
				ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, prefix, dejaVu);
		}
		
	}

	/**
	 * ��writer��ӡ
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
	 * ��ʼ��ջ֡
	 */
	public synchronized ThrowableMe fillInStackTrace() {
		if (stackTrace != null ||
			backtrace != null) {
			fillInStackTrace(0);
			stackTrace = UNASSIGNED_STACK;
		}
		return this;
	}
	
	//��Щnative�������Ҫ���ԣ������б�дJNI����
	private native ThrowableMe fillInStackTrace(int dummy);
	
	/**
	 * clone�����ģ����Ըģ�������
	 */
	public StackTraceElementMe[] getStackTrace() {
		return getOurStackTrace().clone();
	}
	
	/**
	 * ��ȡ�쳣��ջ��Ϣ������������壿
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
			if (this.stackTrace == null && //stackTraceΪnull����Immutable stack?
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
	 * �ֶ���ָ����Suppressed Exception���뵽�쳣����
	 */
	public final synchronized void addSuppressed(ThrowableMe exception) {
		//if (exception == this)
          //  throw new IllegalArgumentException(SELF_SUPPRESSION_MESSAGE, exception);

        if (exception == null)
            throw new NullPointerException(NULL_CAUSE_MESSAGE);
		
        if (suppressedExceptions == null) //����null��ʾSuppressed Exception�����ã�����������캯��
        	return;
        
        if (suppressedExceptions == SUPPRESSED_SENTINEL)
        	suppressedExceptions = new ArrayList<>(1); //��ʼ��
        
        suppressedExceptions.add(exception);
	}
	
	private static final ThrowableMe[] EMPTY_THROWABLE_ARRAY = new ThrowableMe[0];

	/**
	 * ��ȡͨ��try-with-resources��׽�����쳣
	 */
	public final synchronized ThrowableMe[] getSuppressed() {
		if (suppressedExceptions == SUPPRESSED_SENTINEL ||
			suppressedExceptions == null) { //����null��ʾSuppressed Exception�����ã�����������캯��
			return EMPTY_THROWABLE_ARRAY;
		} else 
			return suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY); //תΪEMPTY_THROWABLE_ARRAY��ʽ������
	}
}
