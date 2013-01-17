package hm.orz.chaos114.android.readitnow.ui;

import hm.orz.chaos114.android.readitnow.R;
import hm.orz.chaos114.android.readitnow.util.WidgetUtil;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class SettingActivity extends PreferenceActivity {
	private static final String TAG = SettingActivity.class.getSimpleName();

	private int mAppWidgetId;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		Log.d(TAG, "#onCreate");
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.query_preference);

		mAppWidgetId = getIntent().getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			// デフォルト値が返却された場合、終了
			finish();
			return;
		}

		final ButtonPreference preference = (ButtonPreference) findPreference("complete_button");
		preference.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finishConfigure();
			}
		});

		initValues();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(changeListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(changeListener);
	}

	@Override
	public boolean dispatchKeyEvent(final KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				// 戻るボタン押下時

				new AlertDialog.Builder(this)
						.setMessage(R.string.dialog_quit_without_saving)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											final DialogInterface dialog,
											final int which) {
										finish();
									}
								})
						.setNegativeButton(android.R.string.cancel, null)
						.show();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	private void initValues() {
		for (final String key : keys) {
			final Preference preference = findPreference(key);
			if (preference instanceof ListPreference) {
				final CharSequence summary = ((ListPreference) preference)
						.getEntry();
				preference.setSummary(summary);
			} else if (preference instanceof EditTextPreference) {
				final String summary = ((EditTextPreference) preference)
						.getText();
				preference.setSummary(summary);
			}
		}
	}

	private void finishConfigure() {
		Log.d(TAG, "#finishConfigure");

		// 保持している情報を一旦削除
		final ArticleListFileUtil fileUtil = new ArticleListFileUtil(this);
		fileUtil.deleteList();

		// widgetの件数を更新
		WidgetUtil.update(this, mAppWidgetId);

		// Activityを正常終了
		final Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	private static final String[] keys = { "state", "favorite", "tag",
			"contentType", "sort", "search" };

	private final OnSharedPreferenceChangeListener changeListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			Log.d(TAG, "#onSharedPreferenceChanged");

			final Preference preference = findPreference(key);
			if (preference instanceof ListPreference) {
				final CharSequence summary = ((ListPreference) preference)
						.getEntry();
				preference.setSummary(summary);
			} else {
				preference.setSummary(sharedPreferences.getString(key, ""));
			}
		}
	};
}
