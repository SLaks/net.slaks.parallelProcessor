package net.slaks.parallelProcessor;

import java.util.Calendar;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.slaks.parallelProcessor.external.*;

public class App {
	static final int maxRetries = 3;
	static final int numThreads = 5;

	static final String poison = "/evil/source/file";
	static final FileSource fileSource = new RandomFileSource("a", "b", "c",
			poison);

	static final FileSystem fileSystem = new UnreliableFileSystem(.2);

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
		System.out.format("Thread #%02d: [%1$tH:%1$tM:%1$tS:%1$tL] %3$s\n",
				Thread.currentThread().getId(), Calendar.getInstance(), text);
	}

	static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
				lock.readLock().lock();
				converter.convert(source, target);

				lock.readLock().unlock();
			} catch (ConversionFailedException e) {

				lock.readLock().unlock();
				if (!fileSystem.isUp()) {
					log("Filesystem is down");

					// If the file system is down, one thread should
					// enter the write lock and fix it, while the others wait
					// for it.

					// After the first thread repairs the filesystem, the other
					// threads will see that it's back up and do nothing. This
					// is not optimal, because each thread will still
					// acquire the lock, perform an extra isUp(), then exit.

					lock.writeLock().lock();
					tryFixFileSystem();
					lock.writeLock().unlock();
				}

				if (retries == 1) {
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

		static void tryFixFileSystem() {
			int tries = 0;
			while (!fileSystem.isUp()) {
				if (++tries > maxRetries) {
					log("Couldn't repair filesystem; exiting");
					System.exit(1);
				}
				log("Repairing filesystem");
				fileSystem.tryFix();
			}
		}
	}
}
