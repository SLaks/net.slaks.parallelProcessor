package net.slaks.parallelProcessor.external;


/**
 * A stub converter that just sleeps
 * @author SSL
 *
 */
public class StubConverter implements HtmlConverter {
	
	//I decided to use a system of wrapper classes to provide different failure modes.
	//Yes; that's massive overkill
	
	@Override
	public void convert(String sourcePath, String targetPath)
			throws ConversionFailedException {
		try {
			Thread.sleep((long) (Math.random() * 1500));
		} catch (InterruptedException e) {
		}
	}

}
