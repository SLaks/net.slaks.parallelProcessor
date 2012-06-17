package net.slaks.parallelProcessor;

import java.util.Calendar;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import net.slaks.parallelProcessor.external.*;

public class App {
	static final int maxRetries = 3;
	static final int numThreads = 5;

	static final String poison = "/evil/source/file";
	static final FileSource fileSource = new RandomFileSource("a", "b", "c",
			poison);

	static final FileSystem fileSystem = new UnreliableFileSystem(.1);

	static final HtmlConverter converter = new FileSystemLinkedConverter(
			fileSystem, new PoisonableConverter(poison,
					new UnreliableConverter(.3, new StubConverter())));

	public static void main(String[] args) {
		for (int i = 0; i < numThreads; i++) {
			new Thread(new Runner()).start();
		}
		// Let them run...
	}

	// A real logging framework seems like overkill
	static void log(String text) {
		System.out.format("Thread #%02d\t[%2$tH:%2$tM:%2$tS:%2$tL]\t%3$s\n",
				Thread.currentThread().getId(), Calendar.getInstance(), text);
	}

	static final CyclicBarrier repairSyncer = new CyclicBarrier(numThreads,
			new Runnable() {
				@Override
				public void run() {
					tryFixFileSystem();
				}
			});

	static class Runner implements Runnable {
		@Override
		public void run() {
			log("Starting thread");

			while (true) {
				String source = fileSource.getFile();
				String target = ""; // TODO: Create target path

				log("Converting " + source);
				tryConvert(source, target, maxRetries);
			}
		}

		static boolean tryConvert(String source, String target, int retries) {
			if (retries <= 0)
				throw new IllegalArgumentException();
			try {
				converter.convert(source, target);
			} catch (ConversionFailedException e) {

				if (repairSyncer.getNumberWaiting() > 0 || !fileSystem.isUp()) {
					log("Filesystem is down");

					// If the file system is down, one thread should
					// repair it, but only after all of the threads
					// finish the conversion.
					try {
						repairSyncer.await();
					} catch (InterruptedException | BrokenBarrierException e1) {
						e1.printStackTrace();
					} // Neither of these should ever happen

					// We will only get here after one of the threads runs the
					// CyclicBarrier's barrierAction and repairs the filesystem.

					log(" Filesystem repaired; retrying");
					// Repairing the filesystem does not count as a failed
					// conversion to give up on a poison message
					return tryConvert(source, target, retries);
				} else if (retries == 1) {
					log(" Conversion failed; giving up");
					return false;
				} else {
					log(" Conversion failed; trying again");
					return tryConvert(source, target, retries - 1);
				}
			}

			log(" Conversion succeeded");
			return true;
		}
	}

	static void tryFixFileSystem() {
		int tries = 0;
		do {
			if (++tries > maxRetries) {
				log("Couldn't repair filesystem; exiting");
				System.exit(1);
			}
			log("Repairing filesystem");
			fileSystem.tryFix();
		} while (!fileSystem.isUp());
	}
}
