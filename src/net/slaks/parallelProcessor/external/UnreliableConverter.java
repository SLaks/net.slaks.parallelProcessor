package net.slaks.parallelProcessor.external;


/**
 * A converter wrapper that will occasionally fail
 * 
 * @author SLaks
 */
public class UnreliableConverter implements HtmlConverter {
	private final double failRate;
	private final HtmlConverter inner;

	/**
	 * @param failRate
	 *            The chance that each conversion will fail, as a percentage
	 *            between 0 and 1.
	 * @param inner
	 *            The HtmlConverter implementation that performs the actual
	 *            conversion.
	 */
	public UnreliableConverter(double failRate, HtmlConverter inner) {
		this.failRate = failRate;
		this.inner = inner;
	}

	@Override
	public void convert(String sourcePath, String targetPath)
			throws ConversionFailedException {
		if (Math.random() < failRate)
			throw new ConversionFailedException("Random isolated failure");
		else
			inner.convert(sourcePath, targetPath);
	}

}
