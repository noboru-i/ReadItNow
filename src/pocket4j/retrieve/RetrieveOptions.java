package pocket4j.retrieve;

import java.util.Map;

public class RetrieveOptions {
	private String state;
	private String favorite;
	private String tag;
	private String contentType;
	private String sort;
	private String search;

	public static RetrieveOptions createInstance(final Map<String, String> map) {
		final RetrieveOptions options = new RetrieveOptions();
		options.setState(map.get("state"));
		options.setFavorite(map.get("favorite"));
		options.setTag(map.get("tag"));
		options.setContentType(map.get("contentType"));
		options.setSort(map.get("sort"));
		options.setSearch(map.get("search"));

		return options;
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
