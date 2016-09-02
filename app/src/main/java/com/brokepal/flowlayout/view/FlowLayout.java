package com.brokepal.flowlayout.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//如果有自定义属性，在构造函数中获取
		// 由于我们的自定义流布局控件没有自定义属性，所以不需要获得我自定义的样式属性
	}
	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public FlowLayout(Context context) {
		this(context, null);
	}

	@Override
	/**
	 * 设置FlowLayout的宽和高
	 * 如果给的值是具体的多少dp，就直接获取宽高
	 * 如果是wrap_content，则宽就铺满，高需要计算
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		int width = 0;//FlowLayout的宽
		int height = 0;//FlowLayout的高

		// 记录每一行的宽度与高度
		int lineWidth = 0;
		int lineHeight = 0;

		// 得到内部元素的个数
		int cCount = getChildCount();

		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			// 测量子View的宽和高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到LayoutParams
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();//通过lp可以拿到子控件的margin属性

			// 子View占据的宽度
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

			// 子View占据的高度
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

			// 当前行还可以放下这个子View，则不换行
			if (lineWidth + childWidth <= sizeWidth - getPaddingLeft() - getPaddingRight()) {
				// 叠加行宽
				lineWidth += childWidth;
				// 得到当前行子View的最大高度
				lineHeight = Math.max(lineHeight, childHeight);
			} else {// 换行
				// 对比得到最大的宽度
				width = Math.max(width, lineWidth);
				//叠加高度
				height += lineHeight;
				// 重置新行的行宽
				lineWidth = childWidth;
				// 重置新行的行高
				lineHeight = childHeight;
			}
			// 最后一个控件
			if (i == cCount - 1) {
				width = Math.max(lineWidth, width);
				height += lineHeight;
			}
		}

		setMeasuredDimension(
			modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
			modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop()+ getPaddingBottom()
		);
	}

	//存储所有的View
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	//每一行的高度
	private List<Integer> mLineHeight = new ArrayList<Integer>();

	@Override
	/**
	 * 设置View如何显示，分为两步
	 * 	1.先得到所有的View（即每一行行多少个子View，有多少行）和每一行的行高
	 * 	2.设置子View的位置
	 */
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		mAllViews.clear();
		mLineHeight.clear();
		int width = getWidth();// 当前FlowLayout的宽度

		//1.先得到所有的View（即每一行行多少个子View，有多少行）和每一行的行高
		int lineWidth = 0;
		int lineHeight = 0;
		List<View> lineViews = new ArrayList<View>();
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			// 如果需要换行
			if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
				// 记录LineHeight
				mLineHeight.add(lineHeight);
				// 记录当前行的Views
				mAllViews.add(lineViews);

				// 重置我们的行宽和行高
				lineWidth = 0;
				lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
				// 重置我们的View集合
				lineViews = new ArrayList<View>();
			}
			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
			lineViews.add(child);
		}// for end

		// 处理最后一行
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViews);


		// 2.设置子View的位置
		int left = getPaddingLeft();
		int top = getPaddingTop();

		// 行数
		int lineNum = mAllViews.size();
		for (int i = 0; i < lineNum; i++) {
			lineViews = mAllViews.get(i);// 当前行的所有的View
			lineHeight = mLineHeight.get(i);//当前行的行高

			//显示每一行
			for (int j = 0; j < lineViews.size(); j++) {
				View child = lineViews.get(j);
				// 判断child的状态，如果子View的Visibility为GONE，则不显示
				if (child.getVisibility() == View.GONE) {
					continue;
				}
				MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
				//获取子View的位置坐标（相对父控件的位置）
				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();
				// 为子View进行布局
				child.layout(lc, tc, rc, bc);
				left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;//下一个子View显示在该子View的右边
			}
			//一行显示完后,换行，回到最左边
			top += lineHeight ;
			left = getPaddingLeft() ;
		}
	}

	/**
	 * 与当前ViewGroup对应的LayoutParams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}
}
