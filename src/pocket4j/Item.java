package pocket4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pocket4j.action.modify.ArchiveAction;
import pocket4j.action.modify.BaseModifyAction;
import pocket4j.action.modify.DeleteAction;
import pocket4j.action.modify.FavoriteAction;
import pocket4j.action.modify.ReaddAction;
import pocket4j.action.modify.UnfavoriteAction;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	private int itemId;
	private int resolvedId;
	private String givenUrl;
	private String resolvedUrl;
	private String givenTitle;
	private String resolvedTitle;
	private int favorite;
	private int status;
	private String excerpt;
	private int isArticle;
	private int hasImage;
	private int hasVideo;
	private int wordCount;
	private int sortId;
	private JSONObject tags;
	private Image image;

	public Item(final JSONObject source) {
		try {
			itemId = source.getInt("item_id");
			resolvedId = source.getInt("resolved_id");
			givenUrl = source.getString("given_url");
			resolvedUrl = source.getString("resolved_url");
			givenTitle = source.getString("given_title");
			resolvedTitle = source.getString("resolved_title");
			favorite = source.getInt("favorite");
			status = source.getInt("status");
			excerpt = source.getString("excerpt");
			isArticle = source.getInt("is_article");
			hasImage = source.getInt("has_image");
			hasVideo = source.getInt("has_video");
			wordCount = source.getInt("word_count");
			sortId = source.getInt("sort_id");
			// tags = source.getJSONObject("tags");
			image = new Image(source.optJSONObject("image"));
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public List<BaseModifyAction> getEnableModifyAction() {
		final List<BaseModifyAction> enableAction = new ArrayList<BaseModifyAction>();

		// statusの変更
		if (status == 1) {
			enableAction.add(new ReaddAction(itemId));
		} else {
			enableAction.add(new ArchiveAction(itemId));
		}

		// favoriteの変更
		if (favorite == 1) {
			enableAction.add(new UnfavoriteAction(itemId));
		} else {
			enableAction.add(new FavoriteAction(itemId));
		}

		// deleteの実行
		enableAction.add(new DeleteAction(itemId));
		return enableAction;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(final int itemId) {
		this.itemId = itemId;
	}

	public int getResolvedId() {
		return resolvedId;
	}

	public void setResolvedId(final int resolvedId) {
		this.resolvedId = resolvedId;
	}

	public String getGivenUrl() {
		return givenUrl;
	}

	public void setGivenUrl(final String givenUrl) {
		this.givenUrl = givenUrl;
	}

	public String getResolvedUrl() {
		return resolvedUrl;
	}

	public void setResolvedUrl(final String resolvedUrl) {
		this.resolvedUrl = resolvedUrl;
	}

	public String getGivenTitle() {
		return givenTitle;
	}

	public void setGivenTitle(final String givenTitle) {
		this.givenTitle = givenTitle;
	}

	public String getResolvedTitle() {
		return resolvedTitle;
	}

	public void setResolvedTitle(final String resolvedTitle) {
		this.resolvedTitle = resolvedTitle;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(final int favorite) {
		this.favorite = favorite;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(final int status) {
		this.status = status;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(final String excerpt) {
		this.excerpt = excerpt;
	}

	public int getIsArticle() {
		return isArticle;
	}

	public void setIsArticle(final int isArticle) {
		this.isArticle = isArticle;
	}

	public int getHasImage() {
		return hasImage;
	}

	public void setHasImage(final int hasImage) {
		this.hasImage = hasImage;
	}

	public int getHasVideo() {
		return hasVideo;
	}

	public void setHasVideo(final int hasVideo) {
		this.hasVideo = hasVideo;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(final int wordCount) {
		this.wordCount = wordCount;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(final int sortId) {
		this.sortId = sortId;
	}

	public JSONObject getTags() {
		return tags;
	}

	public void setTags(final JSONObject tags) {
		this.tags = tags;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(final Image image) {
		this.image = image;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Item [item_id=");
		builder.append(itemId);
		builder.append(", resolved_id=");
		builder.append(resolvedId);
		builder.append(", given_url=");
		builder.append(givenUrl);
		builder.append(", resolved_url=");
		builder.append(resolvedUrl);
		builder.append(", given_title=");
		builder.append(givenTitle);
		builder.append(", resolved_title=");
		builder.append(resolvedTitle);
		builder.append(", favorite=");
		builder.append(favorite);
		builder.append(", status=");
		builder.append(status);
		builder.append(", excerpt=");
		builder.append(excerpt);
		builder.append(", is_article=");
		builder.append(isArticle);
		builder.append(", has_image=");
		builder.append(hasImage);
		builder.append(", has_video=");
		builder.append(hasVideo);
		builder.append(", word_count=");
		builder.append(wordCount);
		builder.append(", tags=");
		builder.append(tags);
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
		final Item other = (Item) obj;
		if (itemId != other.itemId) {
			return false;
		}
		return true;
	}
}
