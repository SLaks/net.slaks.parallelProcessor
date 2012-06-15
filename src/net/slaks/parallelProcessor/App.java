package net.slaks.parallelProcessor;

import net.slaks.parallelProcessor.external.*;

public class App {
	static final int maxRetries = 3;
	static final int numThreads = 5;

	static final String poison = "/evil/source/file";
	static final FileSource fileSource = new RandomFileSource("a", "b", "c",
			poison);

	static final HtmlConverter converter = new PoisonableConverter(poison,
			new UnreliableConverter(.3, new StubConverter()));

	public static void main(String[] args) {
		for (int i = 0; i < numThreads; i++) {
			new Thread(new Runner()).start();
		}
		// Let them run...
	}

	// A real logging framework seems like overkill
	static void log(String text) {
		System.out.println("Thread #" + Thread.currentThread().getId() + ": "
				+ text);
	}

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
				// TODO: Filesystem recovery

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
	}
}
