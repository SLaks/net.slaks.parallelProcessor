package net.slaks.parallelProcessor.external;

/**
 * An HtmlConverter wrapper which will only succeed if a FileSystem is up.
 * 
 * @author SLaks
 * 
 */
public class FileSystemLinkedConverter implements HtmlConverter {

	private final FileSystem fileSystem;
	private final HtmlConverter inner;

	/**
	 * @param fileSystem
	 *            The filesystem to check when converting
	 * @param inner
	 *            The HtmlConverter implementation that performs the actual
	 *            conversion.
	 */
	public FileSystemLinkedConverter(FileSystem fileSystem, HtmlConverter inner) {
		this.fileSystem = fileSystem;
		this.inner = inner;
	}

	@Override
	public void convert(String sourcePath, String targetPath)
			throws ConversionFailedException {
		if (!fileSystem.isUp())
			throw new ConversionFailedException();
		else
			inner.convert(sourcePath, targetPath);
	}
}
