package pocket4j.action.modify;

import java.util.Date;

public class UnfavoriteAction extends BaseModifyAction {

	public UnfavoriteAction(final int itemId) {
		super(itemId);
	}

	public UnfavoriteAction(final int itemId, final Date time) {
		super(itemId, time);
	}

	@Override
	public String getActionName() {
		return "unfavorite";
	}
}
