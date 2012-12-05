package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class ButtonPreference extends Preference {

	private View.OnClickListener mListener;

	private String mText;

	public ButtonPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.button_preference);
		mText = attrs.getAttributeValue(null, "button_text");
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		Button button = (Button) view.findViewById(R.id.complate_button);
		button.setText(mText);
		button.setOnClickListener(mListener);
	}

	public void setOnClickListener(View.OnClickListener listener) {
		mListener = listener;
	}
}