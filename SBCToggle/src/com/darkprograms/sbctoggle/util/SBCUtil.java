package com.darkprograms.sbctoggle.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SBCUtil extends BroadcastReceiver {

	public static SBCUtil instance;

	public static SBCUtil getInstance() {
		if (instance == null) {
			instance = new SBCUtil();
		}
		return instance;
	}

	public SBCUtil() {

	}

	/**
	 * Toggles SBC state.
	 * 
	 * @param state
	 *            True to turn on, false to turn off
	 * @return Returns true if it was successful, false if otherwise
	 */
	public boolean toggleSBC(boolean state) {
		try {
			if (state == true) {
				toggle(true);
				return true;
			} else {
				toggle(false);

				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Gets current SBC state
	 * 
	 * @return Returns true if set, false if otherwise
	 */
	public boolean getSBCState() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					"/sys/kernel/batt_options/sbc/sysctl_batt_sbc")));
			String str = br.readLine();
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

	/**
	 * Method that actually toggles the state
	 * 
	 * @param state
	 *            True to turn on, false to turn off
	 * @throws Exception
	 *             Throws exception if there is a problem
	 */
	private void toggle(boolean state) throws Exception {
		Process p = Runtime.getRuntime().exec("su");

		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		if (state) {
			os.writeBytes("echo \"1\" > /sys/kernel/batt_options/sbc/sysctl_batt_sbc\n");
		} else {
			os.writeBytes("echo \"0\" > /sys/kernel/batt_options/sbc/sysctl_batt_sbc\n");
		}
		os.writeBytes("exit\n");
		os.flush();
		p.waitFor();
		os.close();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		getOnStart(context);
	}

	private void getOnStart(Context context) {
		try {
			String file = "apponstart";

			BufferedReader br = new BufferedReader(new InputStreamReader(
					context.openFileInput(file)));

			String onStart;
			String sbcstate;
			onStart = br.readLine();
			sbcstate = br.readLine();

			br.close();

			if (onStart.equals("1")) {

				if (sbcstate.equals("1")) {
					toggleSBC(true);
				} else {
					toggleSBC(false);
				}

			} else {
				return;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

}
