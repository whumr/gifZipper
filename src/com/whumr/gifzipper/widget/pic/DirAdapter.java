package com.whumr.gifzipper.widget.pic;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.whumr.gifzipper.R;
import com.whumr.gifzipper.widget.pic.DirPopupWindow.OnDirSelectListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DirAdapter extends BaseAdapter implements OnItemClickListener {

	private Context context;
	private List<PicFolder> folders;
	private int selected;
	private ImageView selected_img;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private OnDirSelectListener onDirSelectListener;
	
	public DirAdapter(Context context, List<PicFolder> folders, int selected, OnDirSelectListener onDirSelectListener) {
		this.context = context;
		this.folders = folders;
		this.selected = selected;
		this.onDirSelectListener = onDirSelectListener;
		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_pic_empty)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	}

	@Override
	public int getCount() {
		return folders.size();
	}

	@Override
	public PicFolder getItem(int position) {
		return folders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_listitem_pic_dir, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.id_dir_item_image = (ImageView)convertView.findViewById(R.id.dir_title_img);
			viewHolder.id_dir_item_choose = (ImageView)convertView.findViewById(R.id.dir_select_img);
			viewHolder.id_dir_item_name = (TextView)convertView.findViewById(R.id.dir_name_txt);
			viewHolder.id_dir_item_count = (TextView)convertView.findViewById(R.id.dir_count_txt);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final PicFolder folder = getItem(position);
		imageLoader.displayImage("file://" + folder.getPics().get(0), viewHolder.id_dir_item_image, options);
		if (selected == position) {
			viewHolder.id_dir_item_choose.setVisibility(View.VISIBLE);
			selected_img = viewHolder.id_dir_item_choose;
		} else
			viewHolder.id_dir_item_choose.setVisibility(View.INVISIBLE);
		viewHolder.id_dir_item_name.setText(folder.getName());
		viewHolder.id_dir_item_count.setText(folder.getCount() + "уе");
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		if (selected_img != null) {
			try {
				selected_img.setVisibility(View.INVISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("selected_img", e.getMessage(), e);
			}
		}
		selected = position;
		viewHolder.id_dir_item_choose.setVisibility(View.VISIBLE);
		if (onDirSelectListener != null)
			onDirSelectListener.OnDirSelect(position);
	}

	class ViewHolder {
		ImageView id_dir_item_image;
		ImageView id_dir_item_choose;
		TextView id_dir_item_name;
		TextView id_dir_item_count;
	}
}
