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
	 * @param cause ��һ��������쳣����Ϣ
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
	 * ΪʲôҪ��װһ��?
	 */
	private void printStackTrace(PrintStream s) {
		printStackTrace(new WrappedPrintStream(s));
	}
	
	/**
	 * ��ӡ�쳣��Ϣ
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
			
		}
	}
	
	/**
	 * ��յ��쳣��
	 */
	private void printEnclosedStackTrace(PrintStreamOrWriter s,
										 StackTraceElementMe[] enclosingTrace,
										 String caption,
										 String prefix,
										 Set<ThrowableMe> dejaVu) {
		assert Thread.holdsLock(s.lock()); //���ã�
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
	
	private native ThrowableMe fillInStackTrace(int dummy);
	
	public StackTraceElementMe[] getStackTrace() {
		return getOurStackTrace().clone();
	}
	
	/**
	 * ��ȡջ�켣��һ��private����ҲҪ�Ӹ�����
	 */
	private synchronized StackTraceElementMe[] getOurStackTrace() {
		if (stackTrace == UNASSIGNED_STACK ||
			(stackTrace == null && backtrace != null)) {
			int depth = getStackTraceDepth();
			stackTrace = new StackTraceElementMe[depth];
			for (int i = 0; i < depth; i++)
				stackTrace[i] = getStackTraceElement(i);
		} else if (stackTrace == null) { //ʲô����´���
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
