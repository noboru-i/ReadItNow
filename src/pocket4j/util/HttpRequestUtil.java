package pocket4j.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;
/**
 * HTTP通信用のユーティリティクラス
 */
public final class HttpRequestUtil {

	private static final String TAG = HttpRequestUtil.class.getSimpleName();

	public static String get(final String endpoint,
			final Map<String, String> params) throws IOException {
		Log.i(TAG, "endpoint = " + endpoint);

		// URLの組み立て
		final Uri.Builder builder = Uri.parse(endpoint).buildUpon();
		for (final String name : params.keySet()) {
			final String value = params.get(name);
			builder.appendQueryParameter(name, value);
		}

		final DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpGet httpGet = new HttpGet(builder.build().toString());
		final HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000); // 接続のタイムアウトは5秒
		HttpConnectionParams.setSoTimeout(httpParams, 10000); // データ取得のタイムアウトは10秒

		try {
			final String response = httpClient.execute(httpGet,
					new PocketHandler());
			return response;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * APサーバにPOSTリクエストを発行する。
	 *
	 * @param endpoint リクエストURL
	 * @param params リクエストパラメータ
	 * @return レスポンス文字列
	 * @throws IOException 通信例外
	 */
	public static String postJson(final String endpoint, final JSONObject params)
			throws IOException {
		Log.i(TAG, "endpoint = " + endpoint);
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpParams httpParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000); // 接続のタイムアウトは5秒
		HttpConnectionParams.setSoTimeout(httpParams, 10000); // データ取得のタイムアウトは10秒

		final HttpPost httpPost = new HttpPost(endpoint);

		try {
			httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
			httpPost.setHeader("X-Accept", "application/json");
			final StringEntity se = new StringEntity(params.toString(), "utf-8");
			httpPost.setEntity(se);
		} catch (final UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			final String responseString = httpClient.execute(httpPost,
					new PocketHandler());
			Log.i(TAG, "response = " + responseString);

			return responseString;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	private static class PocketHandler implements ResponseHandler<String> {
		@Override
		public String handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {
			// ステータスコードの確認
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new HttpResponseException(statusCode, "");
			}

			// レスポンスを文字列にし、返す
			final String responseString = EntityUtils.toString(response
					.getEntity());
			Log.i(TAG, "response = " + responseString);
			return responseString;
		}
	}
}
