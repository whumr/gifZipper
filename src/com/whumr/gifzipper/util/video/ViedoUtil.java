package com.whumr.gifzipper.util.video;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

public class ViedoUtil {
    
	public static void getImg(String filePath) {
//		File f = new File(Environment.getExternalStorageDirectory() + "/gifZipper/vtmp/a.jpg");
//		long length = f.length();
		// 获取当前视频路径
		String dataPath = Environment.getExternalStorageDirectory() + "/gifZipper/b.mp4";
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(dataPath);
		// 取得视频的长度(单位为毫秒)
		String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// 取得视频的长度(单位为秒)
		int seconds = Integer.valueOf(time) / 1000;
		String dir = Environment.getExternalStorageDirectory() + "/gifZipper/vtmp/";
		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();
		// 得到每一秒时刻的bitmap比如第一秒,第二秒
		long index = 1;
		for (int i = 0; i <= seconds; i++) {
			index = i * 1000 * 1000L;
			Bitmap bitmap = retriever.getFrameAtTime(index, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			if (bitmap != null) {
				String path = dir + index + "c.jpg";
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(path);
					bitmap.compress(CompressFormat.JPEG, 80, fos);
					fos.close();
					Log.i("ViedoUtil", "get " + index + "c.jpg");
				} catch (Exception e) {
					Log.e("ViedoUtil", e.getMessage());
				}
//				bitmap.recycle();
			}
//			bitmap = retriever.getFrameAtTime(index, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
//			if (bitmap != null) {
//				String path = dir + index + "p.jpg";
//				FileOutputStream fos = null;
//				try {
//					fos = new FileOutputStream(path);
//					bitmap.compress(CompressFormat.JPEG, 80, fos);
//					fos.close();
//					Log.i("ViedoUtil", "get " + index + "p.jpg");
//				} catch (Exception e) {
//					Log.e("ViedoUtil", e.getMessage());
//				}
////				bitmap.recycle();
//			}
//			bitmap = retriever.getFrameAtTime(index, MediaMetadataRetriever.OPTION_NEXT_SYNC);
//			if (bitmap != null) {
//				String path = dir + index + "n.jpg";
//				FileOutputStream fos = null;
//				try {
//					fos = new FileOutputStream(path);
//					bitmap.compress(CompressFormat.JPEG, 80, fos);
//					fos.close();
//					Log.i("ViedoUtil", "get " + index + "n.jpg");
//				} catch (Exception e) {
//					Log.e("ViedoUtil", e.getMessage());
//				}
////				bitmap.recycle();
//			}
		}
		Log.i("ViedoUtil", "finished...");
	}
}
