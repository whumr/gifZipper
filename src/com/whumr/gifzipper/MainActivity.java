package com.whumr.gifzipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jiggawatt.giffle.Giffle;

import com.whumr.gifzipper.util.GifDecoder;
import com.whumr.gifzipper.util.ImageFactory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static String TAG = MainActivity.class.getSimpleName();
	private Giffle giffle;
	private GifDecoder gifDecoder;
	
	private EditText name_txt, ratio_txt, size_txt;
	private static String PATH = "/gifZipper/", TEMP_PATH = "/gifZipper/tmp/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		giffle = new Giffle();
		findViewById(R.id.zip_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.i(TAG, giffle.test());
				decodeGif();
			}
		});
		name_txt = (EditText)findViewById(R.id.name_txt);
		ratio_txt = (EditText)findViewById(R.id.ratio_txt);
		size_txt = (EditText)findViewById(R.id.size_txt);
		checkDir();
	}
	
	private void checkDir() {
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + TEMP_PATH);
		if (!dir.exists())
			dir.mkdirs();
	}
	
	private void clearTmp() {
		deleteAllFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + TEMP_PATH));
	}

	private void deleteAllFiles(File root) {
		if (root.exists()) {
			File files[] = root.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) { // 判断是否为文件夹
						deleteAllFiles(f);
						try {
							f.delete();
						} catch (Exception e) {
						}
					} else {
						if (f.exists()) { // 判断是否存在
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
	
	private void decodeGif() {
		checkDir();
		File gifFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH + name_txt.getText().toString() + ".gif");
		if (!gifFile.exists()) {
			Toast.makeText(getBaseContext(), "文件不存在", Toast.LENGTH_SHORT).show();
		} else {
			int ratio = 1, fileSize = 0;
			try {
				ratio = Integer.parseInt(ratio_txt.getText().toString());
				fileSize = Integer.parseInt(size_txt.getText().toString());
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "参数不正确", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				gifDecoder = new GifDecoder();
	            gifDecoder.read(new FileInputStream(gifFile));
	            clearTmp();
	            int size = gifDecoder.getFrameCount();
				for (int i = 0; i < size; i++) {
					Bitmap bitmap = gifDecoder.getFrame(i);
					saveMyBitmap(bitmap, i + "", ratio, fileSize);
				}
	            Log.i(TAG, "size:" + size);
	            encodeGif(size);
	        } catch (Exception e) {
	            Log.e(TAG, "decodeGif error", e);
	            Toast.makeText(getBaseContext(), "压缩图片失败", Toast.LENGTH_SHORT).show();
	        }
		}
	}
	
	private void encodeGif(int size) {
		String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + TEMP_PATH;
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH;
		Bitmap[] bitmaps = new Bitmap[size];
		for (int i = 0; i < bitmaps.length; i++) {
			bitmaps[i] = BitmapFactory.decodeFile(tempPath + i + ".jpg");
		}
		giffle.Encode(path + "result.gif", bitmaps, 10);
		Toast.makeText(getApplicationContext(), "压缩完成", Toast.LENGTH_SHORT).show();
	}
	
	private void saveMyBitmap(Bitmap mBitmap, String bitName, int ratio, int fileSize) throws IOException {
		ImageFactory imageFactory = ImageFactory.getInstance();
//			imageFactory.ratioAndGenThumb(mBitmap, Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/" + bitName + ".jpg", 
//					mBitmap.getWidth() / 4, mBitmap.getHeight() / 4);
		mBitmap = imageFactory.ratio(mBitmap, mBitmap.getWidth() / ratio, mBitmap.getHeight() / ratio);
		imageFactory.compressAndGenImage(mBitmap, Environment.getExternalStorageDirectory().getAbsolutePath() + TEMP_PATH + bitName + ".jpg", fileSize);
	}
}
