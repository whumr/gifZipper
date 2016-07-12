package com.whumr.gifzipper.widget;

import com.whumr.gifzipper.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 指示器（如viewpager下面的点）
 * @author Administrator
 *
 */
public class DotPageDirector extends LinearLayout{
	private int count;
	private int dotNormal = R.drawable.dot_null;
	private int dotLighted = R.drawable.dot_solid;
	
	private ImageView[] imageViews;
	private int paddingLeftRight = 0;
	private int paddingTopBottom = 0;
	
	public DotPageDirector(Context context) {
		super(context);
		
	}
	public DotPageDirector(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public void setDotPadding(int paddingLeftRigth, int paddingTopBottom){
		this.paddingLeftRight = paddingLeftRigth;
		this.paddingTopBottom = paddingTopBottom;
	}
	
	public void setDotRes(int dotNormal, int dotLight){
		this.dotNormal = dotNormal;
		this.dotLighted = dotLight;
	}
	
	public void setCount(int count, int current){
		this.count = count;
		imageViews = new ImageView[count];
		for(int i = 0; i < count; i++){
			ImageView imageView = new ImageView(getContext());
			imageView.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
			addView(imageView);
			imageViews[i] = imageView;
			
		}
		setDotControl(current);
	}
	
	public void setCurrent(int current){
		setDotControl(current);
	}
	
	private void setDotControl(int currentIndex){
		for(int i = 0; i < count; i++){
			ImageView imageView = imageViews[i];
			if(currentIndex == i){
				imageView.setImageResource(dotLighted);
			}else{
				imageView.setImageResource(dotNormal);
			}
		}
	}

}
