package com.darkprograms.svzmanager.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

public class SZStatsUtil extends Application {

	public static SZStatsUtil instance;

	public static SZStatsUtil getInstance() {
		if (instance == null) {
			instance = new SZStatsUtil();
		}
		return instance;
	}

	public SZStatsUtil() {

	}

	private static Context context;

	@Override
	public void onCreate() {
		SZStatsUtil.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return context;
	}

	private static final String SZSTATS_LOCATION = "/system/app/SZStats.apk";
	private static final File SZSTATS_FILE = new File(SZSTATS_LOCATION);
	private static final String SZTEMP_FILE = "SZStats.apk";

	/**
	 * Gets the current installed state of the SZStats.apk app
	 * 
	 * @return Returns true if exists, false otherwise.
	 */
	public boolean getInstallState() {
		if (SZSTATS_FILE.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Deletes the SZStats.apk app
	 */
	public void deleteApp() throws Exception {
		Process p = Runtime.getRuntime().exec("su");

		DataOutputStream os = new DataOutputStream(p.getOutputStream());

		os.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system\n");
		os.flush();

		os.writeBytes("rm -f " + SZSTATS_LOCATION + "\n");
		os.flush();

		os.writeBytes("mount -o ro,remount -t yaffs2 /dev/block/mtdblock3 /system\n");
		os.flush();

		os.writeBytes("exit\n");
		os.flush();
		p.waitFor();
		os.close();
	}

	public void installApp() throws Exception {
		AssetManager assetManager = SZStatsUtil.getAppContext().getAssets();
		InputStream is = assetManager.open(SZTEMP_FILE);

		FileOutputStream fos = SZStatsUtil.getAppContext().openFileOutput(
				SZTEMP_FILE, Context.MODE_PRIVATE);
		byte[] buffer = new byte[256];

		int read = 0;
		while ((read = is.read(buffer)) > 0) {
			fos.write(buffer, 0, read);
		}

		fos.flush();
		fos.close();
		is.close();

		Process p = Runtime.getRuntime().exec("su");

		DataOutputStream os = new DataOutputStream(p.getOutputStream());

		String path = SZStatsUtil.getAppContext()
				.getFileStreamPath(SZTEMP_FILE).getAbsolutePath();

		os.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system\n");
		os.flush();

		os.writeBytes("mv " + path + " " + SZSTATS_LOCATION + "\n");
		os.flush();

		os.writeBytes("chown root.root " + SZSTATS_LOCATION + "\n");
		os.writeBytes("chmod 0644 " + SZSTATS_LOCATION + "\n");
		os.flush();

		os.writeBytes("mount -o ro,remount -t yaffs2 /dev/block/mtdblock3 /system\n");
		os.flush();

		os.writeBytes("exit\n");
		os.flush();
		p.waitFor();

		os.close();

	}

}
