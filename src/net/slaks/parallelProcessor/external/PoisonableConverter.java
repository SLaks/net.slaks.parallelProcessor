package net.slaks.parallelProcessor.external;

/**
 * A converter that will always fail if it receives a specific "poison" input
 * 
 * @author SLaks
 * 
 */
public class PoisonableConverter implements HtmlConverter {

	private final String poison;
	private final HtmlConverter inner;

	/**
	 * @param poison
	 *            The specific source path that will trigger a failure.
	 * @param inner
	 *            The HtmlConverter implementation that performs the actual
	 *            conversion.
	 */
	public PoisonableConverter(String poison, HtmlConverter inner) {
		this.poison = poison; // Pick your poison...
		this.inner = inner;
	}

	@Override
	public void convert(String sourcePath, String targetPath)
			throws ConversionFailedException {
		if (sourcePath.equals(poison))
			throw new ConversionFailedException("Conversion source "
					+ sourcePath + " is poisoned");
		else
			inner.convert(sourcePath, targetPath);
	}
}
