package pocket4j.conf;

import java.util.Map;

public class ConfigurationFactory {

	public Configuration getInstance(final Map<String, String> config) {
		final Configuration conf = new MapConfiguration(config);
		return conf;
	}
}
