package pocket4j.auth;

import java.io.Serializable;

public class Authorization implements Serializable {
	private static final long serialVersionUID = 1L;

	private String accessToken;

	public Authorization() {
		this(null);
	}

	public Authorization(final String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}
}
