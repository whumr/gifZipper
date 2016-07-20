package com.whumr.gifzipper.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import com.whumr.gifzipper.common.Globals;
import com.whumr.gifzipper.widget.PicPreviewActivity;
import com.whumr.gifzipper.widget.SelectPicActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class Tools {
	
	public static Bitmap toRoundCorner(Bitmap bitmap) {
		return toRoundCorner(bitmap, bitmap.getWidth() / 2);
	}
	
	/**
	 * 
	 * 图片圆角
	 * @param bitmap:需要转化成圆角的图片
	 * @param pixels:圆角的度数，数值越大，圆角越大
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}//end toRoundCorner
	
	public static String toHex(int value, int length) {
        String hex = Integer.toHexString(value);
        hex = hex.toUpperCase(Locale.getDefault());

        if (hex.length() < length) {
            while (hex.length() < length)
                hex = "0" + hex;
        } else if (hex.length() > length) {
            hex = hex.substring(hex.length() - length);
        }
        return hex;
    }

    public static byte[] streamToBytes(InputStream stream) throws IOException,
            OutOfMemoryError {
        byte[] buff = new byte[1024];
        int read;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        while ((read = stream.read(buff)) != -1) {
            bao.write(buff, 0, read);
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bao.toByteArray();
    }
	
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	public static void toastShort(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static void toastLeng(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
	
	public static void previewPics(Context context, ArrayList<String> pics, int index) {
		Intent intent = new Intent();
		intent.setClass(context, PicPreviewActivity.class);
		intent.putStringArrayListExtra(PicPreviewActivity.KEY_PICS, pics);
		intent.putExtra(PicPreviewActivity.KEY_INDEX, index);
		context.startActivity(intent);
	}
	
	public static Bitmap compressImage(Bitmap image, int kb) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > kb) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options /= 2;//每次都减半
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap compressImage(String path, int kb) {
		Bitmap image = BitmapFactory.decodeFile(path);
		return compressImage(image, kb);
	}
	
	public static void viewWeb(Context context, String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		context.startActivity(intent);
	}
	
	public static void selectSinglePic(Activity activity, int request_code) {
		selectSinglePic(activity, request_code, false, null);
	}

	public static void selectSinglePic(Activity activity, int request_code, String type) {
		selectSinglePic(activity, request_code, false, type);
	}
	
	public static void selectSinglePic(Activity activity, int request_code, boolean cut, String type) {
		Intent intent = new Intent();
		intent.setClass(activity, SelectPicActivity.class);
		intent.putExtra(SelectPicActivity.KEY_SINGLE, true);
		intent.putExtra(SelectPicActivity.KEY_CUT, cut);
		intent.putExtra(SelectPicActivity.KEY_TYPE, type);
		activity.startActivityForResult(intent, request_code);
	}
	
	public static void selectPics(Activity activity, int max_count, int request_code) {
		Intent intent = new Intent();
		intent.setClass(activity, SelectPicActivity.class);
		intent.putExtra(SelectPicActivity.KEY_MAX_COUNT, max_count);
		activity.startActivityForResult(intent, request_code);
	}
	
	public static double getDistance(double long1, double lat1, double long2, double lat2) {
		return Math.sqrt((long1 * 10000 - long2 * 10000) * (long1 * 10000 - long2 * 10000) 
				+ (lat1 * 10000 - lat2 * 10000) * (lat1 * 10000 - lat2 * 10000));
	}
	
	public static void writeLog(String log) {
		try {
			String log_path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
				+ Globals.PATH_BASE + File.separator + Globals.LOG_DIR;
			File dir = new File(log_path);
			if (!dir.exists())
				dir.mkdirs();
			FileOutputStream fos = new FileOutputStream(log_path + File.separator + System.currentTimeMillis() + ".log");
			fos.write(log.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
