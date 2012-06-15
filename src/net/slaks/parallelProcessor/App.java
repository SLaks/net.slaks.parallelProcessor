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
		//Let them run...
	}

	static class Runner implements Runnable {

		@Override
		public void run() {
			while (true) {
				String source = fileSource.getFile();
				String target = "";	//TODO: Create target path

				tryConvert(source, target, maxRetries);
			}
		}

		static boolean tryConvert(String source, String target, int retries) {
			if (retries <= 0)
				throw new IllegalArgumentException();
			try {
				converter.convert(source, target);
				return true;
			} catch (ConversionFailedException e) {
				// TODO: Filesystem recovery

				if (retries == 1)
					return false;
				else
					return tryConvert(source, target, retries - 1);
			}
		}
	}
}
