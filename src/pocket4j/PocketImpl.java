package pocket4j;

import java.util.Map;

import org.json.JSONObject;

import pocket4j.auth.Authorization;
import pocket4j.conf.Configuration;

public class PocketImpl implements Pocket {

	private static final long serialVersionUID = -3901733870895905166L;

	protected Configuration conf;
	protected Authorization auth;

	public PocketImpl(final Configuration conf, final Authorization auth) {
		this.conf = conf;
		this.auth = auth;
	}

	public void get(final Map<String, String> params) {
		final String url = "https://getpocket.com/v3/get";
		final JSONObject object = new JSONObject(params);

	}
}
