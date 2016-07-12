package com.whumr.gifzipper.widget.pic;

import java.util.List;

import com.whumr.gifzipper.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.PopupWindow;

public class DirPopupWindow extends PopupWindow {
	
	private Context context;
	private View view;
	private ListView list_view;
	
	private OnDirSelectListener onDirSelectListener;
	private List<PicFolder> folders;
	private int selected;
	private DirAdapter adapter;

	public DirPopupWindow(View view, int width, int height, List<PicFolder> folders, int selected, OnDirSelectListener onDirSelectListener) {
		super(view, width, height, true);
		this.view = view;
		this.context = view.getContext();
		this.folders = folders;
		this.selected = selected;
		this.onDirSelectListener = onDirSelectListener;
		init();
		initViews();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchInterceptor(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});
	}
	
	public void initViews() {
		list_view = (ListView) view.findViewById(R.id.listview_dir);
		adapter = new DirAdapter(context, folders, selected, onDirSelectListener);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(adapter);
	}

	public interface OnDirSelectListener {
		public void OnDirSelect(int position);
	}
}
