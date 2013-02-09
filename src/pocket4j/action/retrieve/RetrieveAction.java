package pocket4j.action.retrieve;

import java.util.HashMap;
import java.util.Map;

import pocket4j.action.Action;

public class RetrieveAction implements Action {
	private String state;
	private String favorite;
	private String tag;
	private String contentType;
	private String sort;
	private String search;

	public static RetrieveAction createInstance(final Map<String, String> map) {
		final RetrieveAction options = new RetrieveAction();
		options.setState(map.get("state"));
		options.setFavorite(map.get("favorite"));
		options.setTag(map.get("tag"));
		options.setContentType(map.get("contentType"));
		options.setSort(map.get("sort"));
		options.setSearch(map.get("search"));

		return options;
	}

	@Override
	public Method getMethod() {
		return Method.POST;
	}

	@Override
	public Map<String, String> getRequestParams() {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("detailType", "complete");
		params.put("count", "999"); // widgetに表示可能な最大数
		params.put("state", state);
		params.put("favorite", favorite);
		params.put("tag", tag);
		params.put("contentType", contentType);
		params.put("sort", sort);
		params.put("search", search);
		return params;
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public String getFavorite() {
		return favorite;
	}

	public void setFavorite(final String favorite) {
		this.favorite = favorite;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(final String tag) {
		this.tag = tag;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(final String sort) {
		this.sort = sort;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(final String search) {
		this.search = search;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RetrieveOptions [state=");
		builder.append(state);
		builder.append(", favorite=");
		builder.append(favorite);
		builder.append(", tag=");
		builder.append(tag);
		builder.append(", contentType=");
		builder.append(contentType);
		builder.append(", sort=");
		builder.append(sort);
		builder.append(", search=");
		builder.append(search);
		builder.append("]");
		return builder.toString();
	}

}
