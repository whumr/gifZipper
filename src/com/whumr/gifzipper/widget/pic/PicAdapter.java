package com.whumr.gifzipper.widget.pic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.whumr.gifzipper.R;
import com.whumr.gifzipper.common.Globals;
import com.whumr.gifzipper.util.ImageCache;
import com.whumr.gifzipper.util.Tools;
import com.whumr.gifzipper.util.UriUtil;
import com.whumr.gifzipper.widget.SelectPicActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PicAdapter extends BaseAdapter implements OnItemClickListener {

	private static int CUT_WIDTH = 400, CUT_HEIGHT = 400;
	private SelectPicActivity activity;
	private ArrayList<String> pics = new ArrayList<String>();
	private ArrayList<String> select_pics = new ArrayList<String>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private boolean single;
	private boolean cut_pic;
	
	public PicAdapter(SelectPicActivity activity, ArrayList<String> pics, boolean single, boolean cut_pic) {
		this.activity = activity;
		this.pics = pics;
		this.single = single;
		this.cut_pic = cut_pic;
	}
	
	public void setPics(List<String> pics) {
		this.pics.clear();
		this.pics.addAll(pics);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return pics.size();
	}

	@Override
	public String getItem(int position) {
		return pics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_griditem_pic, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.img = (ImageView)convertView.findViewById(R.id.pic_img);
			viewHolder.check_img = (ImageView)convertView.findViewById(R.id.pic_select_img);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final String path = getItem(position);
		imageLoader.displayImage("file://" + path, viewHolder.img, Globals.IMAGE_OPTIONS);
		viewHolder.check_img.setTag(viewHolder);
		if (single)
			viewHolder.check_img.setVisibility(View.GONE);
		else {
			if (select_pics.contains(path)) {
				viewHolder.img.setColorFilter(activity.getResources().getColor(R.color.selected_img));
				viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_selected));
			} else {
				viewHolder.img.setColorFilter(null);
				viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_unselected));
				viewHolder.check_img.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewHolder viewHolder = (ViewHolder)v.getTag();
						boolean selected = false;
						if (select_pics.contains(path)) {
							select_pics.remove(path);
							viewHolder.img.setColorFilter(null);
							viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_unselected));
							selected = true;
							activity.selected(false);
						} else if (activity.canSelect()) {
							select_pics.add(path);
							viewHolder.img.setColorFilter(activity.getResources().getColor(R.color.selected_img));
							viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_selected));
							selected = true;
							activity.selected(true);
						}
						if (selected)
							viewHolder.checked = !viewHolder.checked;
					}
				});
			}
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String path = getItem(position);
		if (single) {
			if (cut_pic)
				cutPic(path);
			else
				activity.returnSinglePath(path);
		} else {
			ArrayList<String> preview_pics = new ArrayList<String>();
			preview_pics.add(path);
			Tools.previewPics(activity, preview_pics, 0);
		}
	}
	
	public ArrayList<String> getSelect_pics() {
		return select_pics;
	}
	
	private void cutPic(String path) {
		Uri uri = Uri.fromFile(new File(path));
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			path = UriUtil.getAbsolutePath(activity, uri);
			uri = Uri.fromFile(new File(path));
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CUT_WIDTH);
		intent.putExtra("outputY", CUT_HEIGHT);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(ImageCache.getCutImgPath())));
//		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true);
		activity.startActivityForResult(intent, SelectPicActivity.CUT_CODE);
	}

	class ViewHolder {
		ImageView img;
		ImageView check_img;
		boolean checked;
	}
}
