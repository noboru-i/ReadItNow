package pocket4j.conf;

import java.util.Map;

public class MapConfiguration implements Configuration {

	private static final long serialVersionUID = -3579764992507461417L;
	private String oAuthConsumerKey;
	private String oAuthConsumerSecret;

	public static final String OAUTH_CONSUMER_KEY = "oauth.consumerKey";
	public static final String OAUTH_CONSUMER_SECRET = "oauth.consumerSecret";

	public MapConfiguration(final Map<String, String> config) {
		oAuthConsumerKey = getString(config, OAUTH_CONSUMER_KEY);
		oAuthConsumerSecret = getString(config, OAUTH_CONSUMER_SECRET);
	}

	public String getoAuthConsumerKey() {
		return oAuthConsumerKey;
	}

	public void setoAuthConsumerKey(final String oAuthConsumerKey) {
		this.oAuthConsumerKey = oAuthConsumerKey;
	}

	public String getoAuthConsumerSecret() {
		return oAuthConsumerSecret;
	}

	public void setoAuthConsumerSecret(final String oAuthConsumerSecret) {
		this.oAuthConsumerSecret = oAuthConsumerSecret;
	}

	private String getString(final Map<String, String> config, final String name) {
		return config.get(name);
	}
}
