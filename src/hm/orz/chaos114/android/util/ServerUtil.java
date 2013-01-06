package hm.orz.chaos114.android.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

/**
 * HTTP通信用のユーティリティクラス
 */
public final class ServerUtil {

	private static final String TAG = ServerUtil.class.getSimpleName();

	/** cookie情報を保持 */
	private static List<Cookie> cookies = new ArrayList<Cookie>();

	public static String get(final String endpoint,
			final Map<String, String> params) throws IOException {
		Log.i(TAG, "endpoint = " + endpoint);
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpGet httpGet = new HttpGet(endpoint);
		final BasicHttpParams getParams = new BasicHttpParams();
		for (final String name : params.keySet()) {
			final String value = params.get(name);
			getParams.setParameter(name, value);
		}
		httpGet.setParams(getParams);

		// Cookieの登録
		for (final Cookie c : cookies) {
			httpClient.getCookieStore().addCookie(c);
		}
		final String response;
		try {
			response = httpClient.execute(httpGet,
					new ResponseHandler<String>() {
						@Override
						public String handleResponse(final HttpResponse response)
								throws ClientProtocolException, IOException {
							final String responseText = EntityUtils
									.toString(response.getEntity());
							Log.i(TAG, "response = " + responseText);
							final int statusCode = response.getStatusLine()
									.getStatusCode();
							if (statusCode != 200) {
								throw new IOException("GET failed. statusCode="
										+ statusCode);
							}
							return responseText;
						}
					});
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		// Cookie取得
		cookies = httpClient.getCookieStore().getCookies();
		Log.d(TAG, "cookies = " + cookies);
		return response;
	}

	/**
	 * APサーバにPOSTリクエストを発行する。
	 *
	 * @param endpoint リクエストURL
	 * @param params リクエストパラメータ
	 * @return レスポンス文字列
	 * @throws IOException 通信例外
	 */
	public static String post(final String endpoint,
			final Map<String, String> params) throws IOException {
		Log.i(TAG, "endpoint = " + endpoint);
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(endpoint);
		final List<NameValuePair> post_params = new ArrayList<NameValuePair>();
		for (final String name : params.keySet()) {
			final String value = params.get(name);
			post_params.add(new BasicNameValuePair(name, value));
		}

		// Cookieの登録
		for (final Cookie c : cookies) {
			httpClient.getCookieStore().addCookie(c);
		}

		try {
			// 送信パラメータのエンコードを指定
			httpPost.setEntity(new UrlEncodedFormEntity(post_params, "UTF-8"));
		} catch (final UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		final String responseString;
		try {
			responseString = httpClient.execute(httpPost,
					new ResponseHandler<String>() {

						@Override
						public String handleResponse(final HttpResponse response)
								throws ClientProtocolException, IOException {

							final String responseString = EntityUtils
									.toString(response.getEntity());
							Log.i(TAG, "response = " + responseString);
							final int statusCode = response.getStatusLine()
									.getStatusCode();
							if (statusCode != 200) {
								throw new IOException(
										"Post failed. statusCode=" + statusCode);
							}
							return responseString;
						}
					});
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		// Cookie取得
		cookies = httpClient.getCookieStore().getCookies();
		Log.d(TAG, "cookies = " + cookies);
		return responseString;
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
			final StringEntity se = new StringEntity(params.toString());
			httpPost.setEntity(se);
		} catch (final UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Log.d(TAG, "preExecute");
		final HttpResponse response = httpClient.execute(httpPost);
		Log.d(TAG, "postExecute");
		final String responseString = EntityUtils
				.toString(response.getEntity());
		Log.i(TAG, "response = " + responseString);
		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new IOException("Post failed. statusCode=" + statusCode);
		}

		return responseString;
	}
}
