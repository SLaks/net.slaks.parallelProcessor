package net.slaks.parallelProcessor.external;

/**
 * Provides file paths to pass to the converter.
 * 
 * @author SSL
 * 
 */
public interface FileSource {
	/*
	 * Returns the next file path from the queue.
	 * 
	 * This is expected to be a synchronous but quick method; we assume that the
	 * queue will never be empty.
	 */
	String getFile();
}
