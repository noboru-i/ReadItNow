package pocket4j.action.modify;

import java.util.Date;

public class FavoriteAction extends BaseModifyAction {

	public FavoriteAction(final int itemId) {
		super(itemId);
	}

	public FavoriteAction(final int itemId, final Date time) {
		super(itemId, time);
	}

	@Override
	public String getActionName() {
		return "favorite";
	}
}
