package pocket4j;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Image implements Serializable {
	private static final long serialVersionUID = 1L;

	private int itemId;
	private String src;
	private int width;
	private int height;

	public Image(final JSONObject source) {
		if (source == null) {
			return;
		}

		try {
			itemId = source.getInt("item_id");
			src = source.getString("src");
			width = source.getInt("width");
			height = source.getInt("height");
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(final int itemId) {
		this.itemId = itemId;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(final String src) {
		this.src = src;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Image [itemId=");
		builder.append(itemId);
		builder.append(", src=");
		builder.append(src);
		builder.append(", width=");
		builder.append(width);
		builder.append(", height=");
		builder.append(height);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + itemId;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Image other = (Image) obj;
		if (itemId != other.itemId) {
			return false;
		}
		return true;
	}
}
