package com.whumr.gifzipper.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.whumr.gifzipper.R;
import com.whumr.gifzipper.common.BaseActivity;
import com.whumr.gifzipper.widget.pic.DirPopupWindow;
import com.whumr.gifzipper.widget.pic.DirPopupWindow.OnDirSelectListener;
import com.whumr.gifzipper.widget.pic.PicAdapter;
import com.whumr.gifzipper.widget.pic.PicFolder;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPicActivity extends BaseActivity implements OnDirSelectListener {
	
	public static String KEY_MAX_COUNT = "max_count", KEY_PICS = "pics", KEY_SINGLE = "single", KEY_CUT = "cut", KEY_TYPE = "type";
	public static int DEFAULT_MAX_COUNT = 9, CUT_CODE = 100;
	
	private ProgressDialog progress_dialog;
	private GridView gird_view;
	private PicAdapter adapter;
	private RelativeLayout bottom_layout;

	private TextView selected_dir_txt;
	private TextView selected_count_txt;
	private Button ok_btn;
	
	private ArrayList<String> pics = new ArrayList<String>();
	private ArrayList<PicFolder> pic_folders = new ArrayList<PicFolder>();
	private HashMap<String, PicFolder> dir_map = new HashMap<String, PicFolder>();


	private int mScreenHeight;
	private int max_count = DEFAULT_MAX_COUNT;
	private int selected_count = 0;
	private int selected_folder_index = 0;
	
	private boolean single = false;
	private boolean cut_pic = false;
	private String select_type = null;

	private DirPopupWindow dir_popup;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			progress_dialog.dismiss();
			// 为View绑定数据
			setGridView();
			// 初始化展示文件夹的popupWindw
			initDirPopupWindw();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pics);
		initConfig();
		initView();
		getImages();
		initEvent();
	}
	
	private void initConfig() {
		Intent intent = getIntent();
		if (intent.hasExtra(KEY_SINGLE))
			single = true;
		else if (intent.hasExtra(KEY_MAX_COUNT))
			max_count = intent.getIntExtra(KEY_MAX_COUNT, DEFAULT_MAX_COUNT);
		if (intent.hasExtra(KEY_CUT) && intent.getBooleanExtra(KEY_CUT, false))
			cut_pic = true;
		if (intent.hasExtra(KEY_TYPE))
			select_type = intent.getStringExtra(KEY_TYPE);
	}

	private void initView() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		
		gird_view = (GridView) findViewById(R.id.gridView);
		selected_dir_txt = (TextView) findViewById(R.id.selected_dir_txt);
		selected_count_txt = (TextView) findViewById(R.id.selected_count_txt);
		bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
		ok_btn = (Button) findViewById(R.id.submit_btn);
		if (!single)
			selected_count_txt.setText("已选(0/" + max_count + ")张");
		else
			selected_count_txt.setVisibility(View.GONE);
	}
	
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "无外部存储", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		// 显示进度条
		progress_dialog = ProgressDialog.show(this, null, "正在加载...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				ContentResolver mContentResolver = getContentResolver();
				// 只查询jpeg和png的图片
				Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {MediaStore.Images.Media.DATA},
						getQueryString(), getQueryArgs(), MediaStore.Images.Media.DATE_MODIFIED + " desc");
				while (cursor.moveToNext()) {
					// 获取图片的路径
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dir_path = parentFile.getAbsolutePath();
					pics.add(path);
					PicFolder pic_folder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (dir_map.containsKey(dir_path)) {
						dir_map.get(dir_path).getPics().add(path);
						continue;
					} else {
						// 初始化imageFloder
						pic_folder = new PicFolder();
						pic_folder.setDir(dir_path);
						pic_folder.getPics().add(path);
						dir_map.put(dir_path, pic_folder);
						pic_folders.add(pic_folder);
					}
				}
				cursor.close();
				PicFolder all_folder = new PicFolder();
				all_folder.setDir("/所有图片");
				all_folder.getPics().addAll(pics);
				dir_map.put("", all_folder);
				pic_folders.add(0, all_folder);
				// 通知Handler扫描图片完成
				handler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	private String getQueryString() {
		if (select_type == null)
			return MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
		else {
			int size = select_type.split(",").length;
			StringBuilder buffer = new StringBuilder(MediaStore.Images.Media.MIME_TYPE + "=?");
			for (int i = 1; i < size; i++) {
				buffer.append(" or " + MediaStore.Images.Media.MIME_TYPE + "=?");
			}
			return buffer.toString();
		}
	}

	private String[] getQueryArgs() {
		if (select_type == null)
			return new String[] {"image/jpeg", "image/png"};
		else {
			String[] args = select_type.split(",");
			for (int i = 0; i < args.length; i++) {
				args[i] = "image/" + args[i];
			}
			return args;
		}
	}
	
	private void setGridView() {
		if (pic_folders.isEmpty()) {
			Toast.makeText(getApplicationContext(), "没有图片", Toast.LENGTH_SHORT).show();
			return;
		}
		adapter = new PicAdapter(this, pics, single, cut_pic);
		gird_view.setAdapter(adapter);
		gird_view.setOnItemClickListener(adapter);
	};

	@SuppressLint("InflateParams")
	private void initDirPopupWindw() {
		dir_popup = new DirPopupWindow(LayoutInflater.from(this).inflate(R.layout.listview_popup_img_dir, null), 
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), pic_folders, selected_folder_index, this);
		dir_popup.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
	}

	private void initEvent() {
		bottom_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dir_popup != null) {
					dir_popup.setAnimationStyle(R.style.anim_popup_dir);
					dir_popup.showAsDropDown(bottom_layout, 0, 0);
					// 设置背景颜色变暗
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 0.3f;
					getWindow().setAttributes(lp);
				}
			}
		});

		if (!single) {
			ok_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (adapter != null) {
						Intent data = new Intent();
						Bundle bundle = new Bundle();
						bundle.putStringArrayList(KEY_PICS, adapter.getSelect_pics());
						data.putExtras(bundle);
						setResult(RESULT_OK, data);
					}
					finish();
				}
			});
		} else
			ok_btn.setVisibility(View.GONE);
	}
	
	public void selected(boolean select) {
		if (!single) {
			selected_count = select ? selected_count + 1 : selected_count - 1;
			selected_count_txt.setText("已选(" + selected_count + "/" + max_count + ")张");
		}
	}

	public boolean canSelect() {
		if (selected_count < max_count)
			return true;
		Toast.makeText(this, "你最多只能选择" + max_count + "张图片", Toast.LENGTH_SHORT).show();
		return false;
	}
	
	@Override
	public void OnDirSelect(int position) {
		if (selected_folder_index != position) {
			selected_dir_txt.setText(pic_folders.get(position).getName());
			adapter.setPics(pic_folders.get(position).getPics());
			selected_folder_index = position;
		}
		dir_popup.dismiss();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
		pics.clear();
		pics = null;
		pic_folders.clear();
		pic_folders = null;
		dir_map.clear();
		dir_map = null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null) {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	public void returnSinglePath(String path) {
		Intent data = new Intent();
		data.setData(Uri.fromFile(new File(path)));
		setResult(RESULT_OK, data);
		finish();
	}
}
