package com.whumr.gifzipper.widget.pic;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PicPreviewAdapter extends PagerAdapter {
	private List<View> list_view;

	public PicPreviewAdapter(List<View> view) {
		this.list_view = view;
	}

	public void setAllViews(List<View> view) {
		this.list_view = view;
		notifyDataSetChanged();
	}

	public void addView(View view) {
		if (list_view == null) {
			this.list_view = new ArrayList<View>();
		}
		this.list_view.add(view);
	}

	public void removeAllViews() {
		if (this.list_view != null)
			this.list_view.clear();
	}

	@Override
	public int getCount() {
		return list_view.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(list_view.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(list_view.get(position), 0);
		return list_view.get(position);
	}
}
