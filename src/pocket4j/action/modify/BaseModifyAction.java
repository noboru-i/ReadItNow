package pocket4j.action.modify;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pocket4j.action.Action;

public abstract class BaseModifyAction implements Action {

	/** The id of the item to perform the action on. */
	protected int itemId;
	/** The time the action occurred. */
	protected Date time;

	public BaseModifyAction(final int itemId) {
		this(itemId, null);
	}

	public BaseModifyAction(final int itemId, final Date time) {
		this.itemId = itemId;
		this.time = time;
	}

	/**
	 * アクション名を返す。
	 *
	 * @return アクション名
	 */
	public abstract String getActionName();

	@Override
	public Method getMethod() {
		return Method.GET;
	}

	@Override
	public Map<String, String> getRequestParams() {

		final JSONArray actions = new JSONArray();
		try {
			final JSONObject actionObject = new JSONObject();
			actionObject.put("action", getActionName());
			actionObject.put("item_id", itemId);
			if (time != null) {
				actionObject.put("time", time.getTime());
			}
			actions.put(actionObject);
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}

		final Map<String, String> params = new HashMap<String, String>();
		params.put("actions", actions.toString());
		return params;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(final int itemId) {
		this.itemId = itemId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(final Date time) {
		this.time = time;
	}
}
