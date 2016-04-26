package org.roderick.source.lang;

import java.io.Serializable;
import java.util.Objects;

/**
 * final类
 */
public final class StackTraceElementMe implements Serializable {
	private static final long serialVersionUID = 6630897168768029754L;
	
	private String declaringClass;
	private String methodName;
	private String fileName;
	private int lineNumber;
	
	public StackTraceElementMe(String declaringClass, String methodName,
            String fileName, int lineNumber) {
		this.declaringClass = Objects.requireNonNull(declaringClass, "Declaring class is null");
		this.methodName     = Objects.requireNonNull(methodName, "Method name is null");
        this.fileName       = fileName;
        this.lineNumber     = lineNumber;
	}

	public String getClassName() {
		return declaringClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * 为什么等于-2，-1和0呢？
	 */
	public boolean isNativeMethod() {
		return lineNumber == -2;
	}

	@Override
	public String toString() {
        return getClassName() + "." + methodName +
            (isNativeMethod() ? "(Native Method)" :
             (fileName != null && lineNumber >= 0 ?
              "(" + fileName + ":" + lineNumber + ")" :
              (fileName != null ?  "("+fileName+")" : "(Unknown Source)")));
    }
	
	@Override
	public boolean equals(Object obj) {
        if (obj==this)
            return true;
        if (!(obj instanceof StackTraceElementMe))
            return false;
        StackTraceElementMe e = (StackTraceElementMe)obj;
        return e.declaringClass.equals(declaringClass) &&
            e.lineNumber == lineNumber &&
            Objects.equals(methodName, e.methodName) &&
            Objects.equals(fileName, e.fileName);
    }
	
	@Override
	public int hashCode() {
		int result = 31*declaringClass.hashCode() + methodName.hashCode(); //31?
		result = 31*result + Objects.hashCode(fileName);
		result = 31*result + lineNumber;
		return result;
	}
	
}
