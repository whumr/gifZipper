package com.whumr.gifzipper.util;

import java.io.File;

import android.os.Environment;

public class FileUtil {

	public static String getSDCardPath(String path) {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
	}
	
	public static void checkDir(String path) {
		File dir = new File(getSDCardPath(path));
		if (!dir.exists())
			dir.mkdirs();
	}
	
	public static void clearPath(String path) {
		deleteAllFiles(new File(getSDCardPath(path)));
	}

	public static void deleteAllFiles(File root) {
		if (root.exists()) {
			File files[] = root.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) { // �ж��Ƿ�Ϊ�ļ���
						deleteAllFiles(f);
						try {
							f.delete();
						} catch (Exception e) {
						}
					} else {
						if (f.exists()) { // �ж��Ƿ����
							try {
								f.delete();
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}
	}
}
