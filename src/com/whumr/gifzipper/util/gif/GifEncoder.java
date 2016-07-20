package com.whumr.gifzipper.util.gif;

import android.graphics.Bitmap;
import android.util.Log;

public class GifEncoder {
	
	private static String TAG = "GifEncoder";
	
	static {
		System.loadLibrary("gifflen");
	}

	public native String test();
	private native int Init(String gifName, int w, int h, int numColors, int quality, int frameDelay);
	private native void Close();
	private native int AddFrame(int[] inArray);


	// Filename, width, height, colors, quality, frame delay
//	if (Init("/sdcard/foo.gif", width, height, 256, 100, 4) != 0) {
//		Log.e("gifflen", "Init failed");
//	}
//
//	int[] pixels = new int[width*height];
//	// bitmap should be 32-bit ARGB, e.g. like the ones you get when decoding
//	// a JPEG using BitmapFactory
//	bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//
//	// Convert to 256 colors and add to foo.gif
//	AddFrame(pixels);
//
//	Close();
	
//	public boolean Encode(String fileName, Bitmap[] bitmaps, int delay, double scale) {
	public boolean Encode(String fileName, Bitmap[] bitmaps, int delay) {
		int width = bitmaps[0].getWidth();
		int height = bitmaps[0].getHeight();
		Log.i(TAG, "width * height :" + width + " * " + height);
		if (Init(fileName, width, height, 256, 50, delay) != 0) {
//			if (Init(fileName, (int)(width * scale), (int)(height * scale), 256, 100, delay) != 0) {
			Log.e(TAG, "GifUtil init failed");
			return false;
		}
		for (Bitmap bp : bitmaps) {
			int pixels[] = new int[width * height];
			bp.getPixels(pixels, 0, width, 0, 0, width, height);
			AddFrame(pixels);
		}
		Close();
		return true;
	}
}
