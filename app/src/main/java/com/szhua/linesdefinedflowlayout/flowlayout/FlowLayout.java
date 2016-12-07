package com.szhua.linesdefinedflowlayout.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.szhua.linesdefinedflowlayout.R;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout";
    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;
    private int maxLines;
    private int height;

    //设置最大的行数为MAX_INTEGER；
    private static  final int DEFAULT_MAXLINES=Integer.MAX_VALUE ;

    protected List<List<View>> mAllViews = new ArrayList<List<View>>();
    protected List<Integer> mLineHeight = new ArrayList<Integer>();
    private List<View> lineViews = new ArrayList<>();
    private OnLinesChangeListener onLinesChangeListener;
    private OnLinesUpToMaxListener onLinesUpToMaxListener;

    public void setMinLines(int minLines) {
        this.maxLines = minLines;
    }


    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        maxLines = ta.getInt(R.styleable.TagFlowLayout_maxLines, 0);
        if(maxLines<=0){
            throw  new IllegalArgumentException("Your maxLines must greater than 0 !");
        }
        ta.recycle();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

         //子view占的实际空间宽度；
        int containerWidth =sizeWidth - getPaddingLeft() - getPaddingRight() ;

        /**
         * 自适应时候的width和height ;
         */
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;
        int lines = 0;
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            //第一个childView，直接测量赋值；
            if(i==0){
                lineHeight =childHeight ;
                lineWidth +=childWidth;
                continue;
            }


            if (lineWidth + childWidth >containerWidth) {
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                if ( lines < maxLines) {
                    height += lineHeight;
                    lines = lines + 1;

                }
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

        }

        this.height = lines;
        setMeasuredDimension(
                //AtMost的处理;自适应的测量;
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()//
        );

    }


    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        requestLayout();
    }

    //规定子控件的布局位置 ；
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        lineViews.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);

                if (onLinesChangeListener != null) {
                    this.onLinesChangeListener.onlinesChanged(mLineHeight.size());
                }
                if(onLinesUpToMaxListener!=null){
                if(mLineHeight.size()==maxLines){
                  onLinesUpToMaxListener.onlinesUptoMax(maxLines);
                }else if(mLineHeight.size()+1==maxLines){
                    onLinesUpToMaxListener.onlinesGreaterThanMaxFisrt(maxLines+1);
                }
                }

                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                lineViews = new ArrayList<View>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
                    + lp.bottomMargin);
            lineViews.add(child);

        }
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);



        int top = getPaddingTop();

        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);


            int  left = getPaddingLeft();

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin
                        + lp.rightMargin;
            }
            top += lineHeight;
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public void setOnLinesChangeListener(OnLinesChangeListener onLinesChangeListener) {
        this.onLinesChangeListener = onLinesChangeListener;
    }
    public void setOnLinesUpToMaxListener(OnLinesUpToMaxListener onLinesUpToMaxListener) {
        this.onLinesUpToMaxListener = onLinesUpToMaxListener;
    }

}
