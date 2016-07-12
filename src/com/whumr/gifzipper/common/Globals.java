package com.whumr.gifzipper.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.whumr.gifzipper.MainActivity;
import com.whumr.gifzipper.R;

import android.app.Activity;
import android.graphics.Bitmap;

public class Globals {

	/** 手机目录名称 **/
	public static final String PATH_BASE = "gifZipper", IMG_DIR = "img", LOG_DIR = "log";
	
	public static final String COMMON_RESULT = "result";
	
	public static final int CODE_SELECT_PIC = 0;
	
	public static final DisplayImageOptions IMAGE_OPTIONS = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_pic_empty)
			.showImageOnFail(R.drawable.icon_pic_empty)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	
	private static List<Activity> activity_list = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity) {
		activity_list.add(activity);
	}
	
	public static void removeActivity(Activity activity) {
		activity_list.remove(activity);
	}
	
	public static void clearActivityList(boolean all) {
		for (Iterator<Activity> it = activity_list.iterator(); it.hasNext(); ) {
			Activity activity = it.next();
			if (activity != null && (all || activity.getClass() != MainActivity.class)) {
				activity.finish();
				it.remove();
			}
		}
	}
	
	public int getActivitySize() {
		return activity_list.size();
	}
}
