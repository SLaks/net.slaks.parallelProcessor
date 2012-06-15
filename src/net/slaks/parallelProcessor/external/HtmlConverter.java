package net.slaks.parallelProcessor.external;


/**
 * Converts HTML source to an image.
 * 
 * This service is expected to be unreliable.
 * 
 * @author SLaks
 */
public interface HtmlConverter {
	void convert(String sourcePath, String targetPath)
			throws ConversionFailedException;
}
