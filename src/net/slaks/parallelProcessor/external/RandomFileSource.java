package net.slaks.parallelProcessor.external;

import java.util.Arrays;
import java.util.List;

/**
 * A FileSource implementation that randomly selects from a set of hard-coded
 * strings.
 * 
 * @author SLaks
 * 
 */
public class RandomFileSource implements FileSource {
	private final List<String> names;

	public RandomFileSource(String... names) {
		this.names = Arrays.asList(names);
	}

	@Override
	public String getFile() {
		return names.get((int) (Math.random() * names.size()));
	}
}
