package com.whumr.gifzipper.util.gif;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.whumr.gifzipper.common.Globals;
import com.whumr.gifzipper.util.FileUtil;
import com.whumr.gifzipper.util.ImageFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GifUtil {
	private static final String TAG = "GifUtil";
	public static final int GIF_NOT_EXISTS = 0, GIF_DECODE_SUCCESS = 1, GIF_DECODE_FAIL = 2, GIF_ENCODE_SUCCESS = 3, GIF_ENCEODE_FAIL = 4;

	public static void decodeGif(GifDecoder gifDecoder, Handler handler, Uri gifUri) {
		FileUtil.checkDir(Globals.GIF_TMP_PATH);
		File gifFile = new File(gifUri.getPath());
		Message msg = null;
		if (!gifFile.exists()) {
			msg = Message.obtain(handler, GIF_NOT_EXISTS);
		} else {
			int ratio = 1, fileSize = 500;
			try {
				gifDecoder = new GifDecoder();
	            gifDecoder.read(new FileInputStream(gifFile));
	            FileUtil.clearPath(Globals.GIF_TMP_PATH);
	            int size = gifDecoder.getFrameCount();
				for (int i = 0; i < size; i++) {
					Bitmap bitmap = gifDecoder.getFrame(i);
					saveBitmap(bitmap, i + "", ratio, fileSize);
				}
	            Log.i(TAG, "gif frame size:" + size);
	            msg = Message.obtain(handler, GIF_DECODE_SUCCESS);
	        } catch (Exception e) {
	            Log.e(TAG, "decodeGif error", e);
	            msg = Message.obtain(handler, GIF_DECODE_FAIL);
	        }
		}
		msg.sendToTarget();
	}
	
	public static void encodeGif(GifEncoder gifEncoder, Handler handler, String gifName) {
		String tempPath = FileUtil.getSDCardPath(Globals.GIF_TMP_PATH);
		String path = FileUtil.getSDCardPath(Globals.GIF_PATH);
		File tempDir = new File(tempPath);
		Message msg = null;
		try {
			Bitmap[] bitmaps = new Bitmap[tempDir.list().length];
			for (int i = 0; i < bitmaps.length; i++) {
				bitmaps[i] = BitmapFactory.decodeFile(tempPath + i + ".jpg");
			}
			gifEncoder.Encode(path + gifName, bitmaps, 10);
			msg = Message.obtain(handler, GIF_ENCODE_SUCCESS);
		} catch (Exception e) {
			Log.e(TAG, "encodeGif error", e);
			msg = Message.obtain(handler, GIF_ENCEODE_FAIL);
		}
		msg.sendToTarget();
	}
	
	private static void saveBitmap(Bitmap mBitmap, String bitName, int ratio, int fileSize) throws IOException {
		ImageFactory imageFactory = ImageFactory.getInstance();
		mBitmap = imageFactory.ratio(mBitmap, mBitmap.getWidth() / ratio, mBitmap.getHeight() / ratio);
		imageFactory.compressAndGenImage(mBitmap, FileUtil.getSDCardPath(Globals.GIF_TMP_PATH) + bitName + ".jpg", fileSize);
	}
}
