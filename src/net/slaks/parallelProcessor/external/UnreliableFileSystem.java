package net.slaks.parallelProcessor.external;

/**
 * A FileSystem that may go down whenever it is checked.
 * 
 * @author SLaks
 * 
 */
public class UnreliableFileSystem implements FileSystem {
	private final double failRate;

	private boolean isDown;

	/**
	 * @param failRate
	 *            The chance that the filesystem will go down (during each
	 *            isUp() call), as a percentage between 0 and 1.
	 */
	public UnreliableFileSystem(double failRate) {
		this.failRate = failRate;
	}

	// This is a Heisenberg filesystem - it cannot be checked without affecting
	// it.

	@Override
	public boolean isUp() {
		if (Math.random() < failRate)
			isDown = true;
		return !isDown;
	}

	@Override
	public void tryFix() {
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
		}
		isDown = false;
	}
}
