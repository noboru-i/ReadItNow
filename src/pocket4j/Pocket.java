package pocket4j;

import hm.orz.chaos114.android.util.ServerUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pocket4j.auth.Authorization;
import pocket4j.conf.Configuration;
import android.util.Log;

public class Pocket implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String TAG = Pocket.class.getSimpleName();

	public static final String URL_V3_GET = "https://getpocket.com/v3/get";

	private Authorization authorization;
	private final Configuration configuration;

	public Pocket(final Authorization authorization,
			final Configuration configuration) {
		this.authorization = authorization;
		this.configuration = configuration;
	}

	private String fetch(final RetrieveOptions options,
			final Map<String, Object> map) {

		final String url = URL_V3_GET;
		final String consumerKey = configuration.getApiKey();
		final String accessToken = authorization.getAccessToken();

		final JSONObject params = new JSONObject();
		try {
			params.put("consumer_key", consumerKey);
			params.put("access_token", accessToken);
			params.put("detailType", "complete");
			params.put("count", 999); // widgetに表示可能な最大数

			params.put("state", options.getState());
			params.put("favorite", options.getFavorite());
			params.put("tag", options.getTag());
			params.put("contentType", options.getContentType());
			params.put("sort", options.getSort());
			params.put("search", options.getSearch());
			if (map != null) {
				for (final String key : map.keySet()) {
					params.put(key, map.get(key));
				}
			}
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}

		final String response;
		try {
			response = ServerUtil.postJson(url, params);
		} catch (final IOException e) {
			return "";
		}

		return response;
	}

	public List<Item> get(final RetrieveOptions options) {
		Log.d(TAG, "#get");
		final List<Item> items = new ArrayList<Item>();

		final String response = fetch(options, null);

		JSONObject object;
		try {
			object = new JSONObject(response);
		} catch (final JSONException e) {
			// 空のリストを返す
			return items;
		}
		final JSONObject list = object.optJSONObject("list");
		if (list == null) {
			// listが取得できなかったため、空のリストを返す
			return items;
		}
		final Iterator<?> ite = list.keys();
		while (ite.hasNext()) {
			final Item item = new Item(list.optJSONObject((String) ite.next()));
			items.add(item);
		}

		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(final Item lhs, final Item rhs) {
				return lhs.getSortId() - rhs.getSortId();
			}
		});

		return items;
	}

	public int count(final RetrieveOptions options) {
		Log.d(TAG, "#count");

		final List<Item> list = get(options);

		return list.size();
	}

	public void setAuthorization(final Authorization authorization) {
		this.authorization = authorization;
	}
}
