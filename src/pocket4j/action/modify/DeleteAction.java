package pocket4j.action.modify;

import java.util.Date;

public class DeleteAction extends BaseModifyAction {

	public DeleteAction(final int itemId) {
		super(itemId);
	}

	public DeleteAction(final int itemId, final Date time) {
		super(itemId, time);
	}

	@Override
	public String getActionName() {
		return "delete";
	}
}
