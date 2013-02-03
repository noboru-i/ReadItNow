package pocket4j.action;

import java.util.Map;

public interface Action {

	/** HTTPメソッドの列挙体 */
	public enum Method {
		GET, POST
	}

	/**
	 * リクエストメソッドを返す。
	 *
	 * @return リクエストメソッド種別
	 */
	public abstract Method getMethod();

	/**
	 * リクエストパラメータを返す。
	 *
	 * @return リクエストパラメータ
	 */
	public abstract Map<String, String> getRequestParams();
}
