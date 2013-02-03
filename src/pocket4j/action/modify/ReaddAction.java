package pocket4j.action.modify;

import java.util.Date;

public class ReaddAction extends BaseModifyAction {

	public ReaddAction(final int itemId) {
		super(itemId);
	}

	public ReaddAction(final int itemId, final Date time) {
		super(itemId, time);
	}

	@Override
	public String getActionName() {
		return "readd";
	}
}
