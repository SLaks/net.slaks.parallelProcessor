package net.slaks.parallelProcessor.external;

/**
 * Tests for, and attempts to fix, filesystem issues.
 * 
 * @author SLaks
 * 
 */
public interface FileSystem {
	/**
	 * Indicates whether the file system is functioning.
	 * 
	 * This method must be thread-safe
	 */
	boolean isUp();

	/**
	 * Attempts to fix the file system if it's down.
	 * 
	 * This method is not expected to be thread-safe; isUp() must not be called
	 * while this method is running.
	 */
	void tryFix();
}
