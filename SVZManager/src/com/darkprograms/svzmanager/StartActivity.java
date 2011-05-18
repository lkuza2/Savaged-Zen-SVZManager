package com.darkprograms.svzmanager;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.darkprograms.svzmanager.util.SBCUtil;
import com.darkprograms.svzmanager.util.SZStatsUtil;

public class StartActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button = (Button) findViewById(R.id.sbctoggle);
		CheckBox toggle = (CheckBox) findViewById(R.id.onstart);
		Button szstatsbutton = (Button) findViewById(R.id.szstats);

		button.setOnClickListener(this);
		szstatsbutton.setOnClickListener(this);
		toggle.setOnClickListener(this);

		// view.setOnFocusChangeListener(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation/keyboard change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSBCState();
		getSBCOnStartState();
		getSZStatsState();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private void getSZStatsState() {
		SZStatsUtil sz = SZStatsUtil.getInstance();

		Button szstatsbutton = (Button) findViewById(R.id.szstats);

		if (sz.getInstallState()) {
			szstatsbutton.setText(R.string.szstatsbuttonremove);
		} else {
			szstatsbutton.setText(R.string.szstatsbuttoninstall);
		}
	}

	private void getSBCOnStartState() {
		CheckBox toggle = (CheckBox) findViewById(R.id.onstart);
		if (getOnStart()) {
			toggle.setChecked(true);
		} else {
			toggle.setChecked(false);
		}
	}

	private void getSBCState() {
		TextView view = (TextView) findViewById(R.id.sbcstate);
		if (SBCUtil.getInstance().getSBCState()) {
			view.setText("Enabled");
			view.setTextColor(getResources().getColor(R.color.green));
		} else {
			view.setText("Disabled");
			view.setTextColor(getResources().getColor(R.color.red));
		}
	}

	public void setOnStart(boolean state) {
		try {
			String file = "apponstart";
			String onstart;
			if (state) {
				onstart = "1\n";
			} else {
				onstart = "0\n";
			}

			String sbcstate;

			if (SBCUtil.getInstance().getSBCState()) {
				sbcstate = "1";
			} else {
				sbcstate = "0";
			}

			FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
			fos.write(onstart.getBytes());
			fos.flush();
			fos.write(sbcstate.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	public boolean getOnStart() {
		try {
			String file = "apponstart";

			BufferedReader br = new BufferedReader(new InputStreamReader(
					openFileInput(file)));

			String str;
			str = br.readLine();

			br.close();

			if (str.equals("1")) {
				return true;
			} else {
				return false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private void toggleSBCButton() {
		SBCUtil util = SBCUtil.getInstance();
		if (util.getSBCState()) {
			if (util.toggleSBC(false)) {
				Toast.makeText(this, "SBC Disabled", 10000).show();
			} else {
				Toast.makeText(this, "Failed to set SBC", 10000).show();
			}
		} else {
			if (util.toggleSBC(true)) {
				Toast.makeText(this, "SBC Enabled", 10000).show();
			} else {
				Toast.makeText(this, "Failed to set SBC", 10000).show();
			}
		}
	}

	private void saveOnStart() {
		CheckBox toggle = (CheckBox) findViewById(R.id.onstart);
		if (toggle.isChecked()) {
			setOnStart(true);
		} else {
			setOnStart(false);
		}
	}

	private void SZStatsAction() {
		try {
			SZStatsUtil sz = SZStatsUtil.getInstance();
			if (sz.getInstallState()) {
				sz.deleteApp();
				Toast.makeText(this, "SZStats app removed", 10000).show();
			} else {
				sz.installApp();
				Toast.makeText(this, "SZStats app installed", 10000).show();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Failed to remove/install SZStats", 10000)
					.show();
		}

	}

	// Implement the OnClickListener callback
	public void onClick(View v) {

		if (v.getId() == R.id.sbctoggle) {
			toggleSBCButton();
		} else if (v.getId() == R.id.szstats) {

			SZStatsAction();
		}

		saveOnStart();
		getSBCState();
		getSZStatsState();
	}

}
