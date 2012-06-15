package net.slaks.parallelProcessor.external;

/**
 * The exception thrown when a conversion process fails. The exception message
 * must not be relied on in code.
 * 
 * @author SLaks
 * 
 */
public class ConversionFailedException extends Exception {

	private static final long serialVersionUID = -5188005063060721686L;

	public ConversionFailedException(String message) {
		super(message);
	}

	public ConversionFailedException() {
	}

}
