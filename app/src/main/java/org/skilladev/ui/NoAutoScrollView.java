package org.skilladev.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;

/**
 * Created by administrador on 21/05/14.
 */
public class NoAutoScrollView extends ScrollView {

	public interface OnEndScrollListener {
		public void onEndScroll(boolean home, boolean end);
	}

	private boolean mIsFling;
	private OnEndScrollListener mOnEndScrollListener;

	public NoAutoScrollView(Context context) {
		super(context);
	}

	public NoAutoScrollView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public NoAutoScrollView(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
	}

	@Override
	public void requestChildFocus(View child, View focused) {
		if (focused instanceof WebView )
			return;
		super.requestChildFocus(child, focused);
	}

	@Override
	public void fling(int velocityY) {
		super.fling(velocityY);
		mIsFling = true;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldX, int oldY) {
		super.onScrollChanged(x, y, oldX, oldY);

		int height = getMeasuredHeight();
		int child  = getChildAt(0).getHeight();
		if (mOnEndScrollListener != null) {
			mOnEndScrollListener.onEndScroll( y<=0, y>=(child-height-2) );
		}
	}

	public OnEndScrollListener getOnEndScrollListener() {
		return mOnEndScrollListener;
	}

	public void setOnEndScrollListener(OnEndScrollListener mOnEndScrollListener) {
		this.mOnEndScrollListener = mOnEndScrollListener;
	}
}
