package com.whumr.gifzipper;

import java.io.File;

import com.whumr.gifzipper.common.BaseActivity;
import com.whumr.gifzipper.common.Globals;
import com.whumr.gifzipper.util.FileUtil;
import com.whumr.gifzipper.util.Tools;
import com.whumr.gifzipper.util.gif.GifDecoder;
import com.whumr.gifzipper.util.gif.GifEncoder;
import com.whumr.gifzipper.util.gif.GifUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

@SuppressLint("HandlerLeak")
public class MainActivity extends BaseActivity {
	
	private static String TAG = MainActivity.class.getSimpleName();
	private GifEncoder gifEncoder;
	private GifDecoder gifDecoder;
	private Uri gifUri;
	private String gifName;
	
	private Handler decodeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case GifUtil.GIF_NOT_EXISTS :
					toastShort("gif文件不存在");
					break;
				case GifUtil.GIF_DECODE_SUCCESS :
					toastShort("解码成功");
					break;
				case GifUtil.GIF_DECODE_FAIL :
					toastShort("解码失败");
					break;
			}
		};
	};

	private Handler encodeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case GifUtil.GIF_ENCODE_SUCCESS :
					toastShort("编码成功");
					break;
				case GifUtil.GIF_ENCEODE_FAIL :
					toastShort("编码失败");
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gifDecoder = new GifDecoder();
		gifEncoder = new GifEncoder();
		//选择图片
		findViewById(R.id.choose_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Tools.selectSinglePic(MainActivity.this, Globals.CODE_SELECT_PIC, "gif");
			}
		});
		//解码
		findViewById(R.id.decode_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gifUri == null) {
					toastShort("请选择gif文件");
				} else {
					FileUtil.checkDir(Globals.GIF_TMP_PATH);
					new Thread(new Runnable() {
						@Override
						public void run() {
							GifUtil.decodeGif(gifDecoder, decodeHandler, gifUri);
						}
					}).start();
				}
			}
		});
		//编码
		findViewById(R.id.encode_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gifUri == null) {
					toastShort("请选择gif文件");
				} else {
					Log.i(TAG, gifEncoder.test());
					new Thread(new Runnable() {
						@Override
						public void run() {
							GifUtil.encodeGif(gifEncoder, encodeHandler, gifName);
						}
					}).start();
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null && requestCode == Globals.CODE_SELECT_PIC) {
			Uri img_uri = data.getData();   
            if (img_uri != null) {   
                try {   
            		toastShort(img_uri.toString());
            		File file = new File(img_uri.getPath());
            		toastShort("" + file.exists());
            		gifUri = img_uri;
            		gifName = file.getName();
                } catch (Exception e) {   
                    e.printStackTrace();   
                    toastShort("无法选择该图片\n" + e.getMessage());
                }   
            } 
		}
	}
}
