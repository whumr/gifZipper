package com.whumr.gifzipper.util;

import java.io.File;

import com.whumr.gifzipper.common.Globals;

import android.os.Environment;

public class ImageCache {

	private static String IMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
			+ Globals.PATH_BASE + File.separator + Globals.IMG_DIR + File.separator;
	
	private static String IMG_CUT = "cut.u";

	public static String getCutImgPath() {
		File dir = new File(IMG_PATH);
		if (!dir.exists())
			dir.mkdirs();
		return IMG_PATH + IMG_CUT;
	}
}
