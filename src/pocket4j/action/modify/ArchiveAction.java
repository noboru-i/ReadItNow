package pocket4j.action.modify;

import java.util.Date;

public class ArchiveAction extends BaseModifyAction {

	public ArchiveAction(final int itemId) {
		super(itemId);
	}

	public ArchiveAction(final int itemId, final Date time) {
		super(itemId, time);
	}

	@Override
	public String getActionName() {
		return "archive";
	}
}
