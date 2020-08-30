package com.pointlessapps.mobileusos.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.pointlessapps.mobileusos.R;

/**
 * A custom horizontal indeterminate progress bar which displays a smooth colored animation.
 *
 * @author Christophe Beyls
 */
public class RefreshProgressBar extends View {

	private final Handler handler;
	boolean mIsRefreshing = false;
	SwipeProgressBar mProgressBar;

	public RefreshProgressBar(Context context) {
		this(context, null, 0);
	}

	public RefreshProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RefreshProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		handler = new Handler();
		if (!isInEditMode()) {
			mProgressBar = new SwipeProgressBar(this);
		}

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RefreshProgressBar, 0, 0);
		try {
			setColorSchemeColors(
					a.getColor(R.styleable.RefreshProgressBar_color1, ContextCompat.getColor(context, R.color.color1)),
					a.getColor(R.styleable.RefreshProgressBar_color2, ContextCompat.getColor(context, R.color.color2)),
					a.getColor(R.styleable.RefreshProgressBar_color3, ContextCompat.getColor(context, R.color.color3)),
					a.getColor(R.styleable.RefreshProgressBar_color4, ContextCompat.getColor(context, R.color.color4))
			);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Starts or tops the refresh animation. Animation is stopped by default. After the stop animation has completed,
	 * the progress bar will be fully transparent.
	 */
	public void setRefreshing(boolean refreshing) {
		if (mIsRefreshing != refreshing) {
			mIsRefreshing = refreshing;
			handler.removeCallbacks(mRefreshUpdateRunnable);
			handler.post(mRefreshUpdateRunnable);
		}
	}

	private final Runnable mRefreshUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			if (mIsRefreshing) {
				mProgressBar.start();
			} else {
				mProgressBar.stop();
			}
		}
	};

	@Override
	protected void onDetachedFromWindow() {
		handler.removeCallbacks(mRefreshUpdateRunnable);
		super.onDetachedFromWindow();
	}

	/**
	 * Set the four colors used in the progress animation from color resources.
	 */
	public void setColorSchemeResources(@ColorRes int colorRes1, @ColorRes int colorRes2, @ColorRes int colorRes3, @ColorRes int colorRes4) {
		final Context context = getContext();
		setColorSchemeColors(ContextCompat.getColor(context, colorRes1), ContextCompat.getColor(context, colorRes2),
				ContextCompat.getColor(context, colorRes3), ContextCompat.getColor(context, colorRes4));
	}

	/**
	 * Set the four colors used in the progress animation.
	 */
	public void setColorSchemeColors(@ColorInt int color1, @ColorInt int color2, @ColorInt int color3, @ColorInt int color4) {
		mProgressBar.setColorScheme(color1, color2, color3, color4);
	}

	/**
	 * @return Whether the progress bar is actively showing refresh progress.
	 */
	public boolean isRefreshing() {
		return mIsRefreshing;
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		super.draw(canvas);
		if (mProgressBar != null) {
			mProgressBar.draw(canvas);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (mProgressBar != null) {
			mProgressBar.setBounds(0, 0, getWidth(), getHeight());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
	}
}
