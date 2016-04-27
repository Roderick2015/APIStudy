package org.roderick.source.lang;

public class ExceptionMe extends ThrowableMe {
	private static final long serialVersionUID = -2129107346927010366L;

	public ExceptionMe() {
		super();
	}

	public ExceptionMe(String message) {
		super(message);
	}

	public ExceptionMe(String message, ThrowableMe cause) {
		super(message, cause);
	}

	public ExceptionMe(ThrowableMe cause) {
		super(cause);
	}

	protected ExceptionMe(String message, ThrowableMe cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
