package pocket4j.conf;

import hm.orz.chaos114.android.readitnow.R;

import java.io.Serializable;

import android.content.Context;

public class Configuration implements Serializable {
	private static final long serialVersionUID = 1L;

	private String apiKey;

	public Configuration(final Context context) {

		apiKey = context.getString(R.string.pocket_api_key);
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(final String apiKey) {
		this.apiKey = apiKey;
	}

}
