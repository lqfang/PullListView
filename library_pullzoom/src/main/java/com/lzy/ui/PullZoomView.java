package com.lzy.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/3/13
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PullZoomView extends ScrollView {

    private static final String TAG_HEADER = "header";        //头布局Tag
    private static final String TAG_ZOOM = "zoom";            //缩放布局Tag
    private static final String TAG_CONTENT = "content";      //内容布局Tag
    private static final int DIRECTION_UP = 0;                //向上滑动
    private static final int DIRECTION_DOWN = 1;              //向下滑动

    private float sensitive = 1.5f;         //放大的敏感系数
    private boolean isParallax = true;      //是否让头部具有视差动画
    private boolean isZoomEnable = true;    //是否允许头部放大
    private int zoomTime = 500;             //头部缩放时间，单位 毫秒

    private Scroller scroller;              //辅助缩放的对象
    private boolean isActionDown = false;   //第一次接收的事件是否是Down事件
    private boolean isZooming = false;      //是否正在被缩放
    private MarginLayoutParams headerParams;//头部的参数
    private int headerHeight;               //头部的原始高度
    private ViewGroup headerView;           //头布局
    private View zoomView;                  //用于缩放的View
    private View contentView;               //主体内容View
    private float downX;                    //第一次按下的x坐标
    private float downY;                    //第一次按下的y坐标
    private float lastEventX;               //Move事件最后一次发生时的X坐标
    private float lastEventY;               //Move事件最后一次发生时的Y坐标
    private int maxY;                       //允许的最大滑出距离
    private int curY;                       //当前已经滑动的距离

    public PullZoomView(Context context) {
        this(context, null);
    }

    public PullZoomView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    public PullZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PullZoomView);
        sensitive = a.getFloat(R.styleable.PullZoomView_pzv_sensitive, sensitive);
        isParallax = a.getBoolean(R.styleable.PullZoomView_pzv_isParallax, isParallax);
        isZoomEnable = a.getBoolean(R.styleable.PullZoomView_pzv_isZoomEnable, isZoomEnable);
        zoomTime = a.getInt(R.styleable.PullZoomView_pzv_zoomTime, zoomTime);
        a.recycle();

        scroller = new Scroller(getContext());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() > 0) {
            ViewGroup rootView = (ViewGroup) getChildAt(0);
            for (int i = 0; i < rootView.getChildCount(); i++) {
                Object tag = rootView.getChildAt(i).getTag();
                if (tag != null) {
                    if (TAG_CONTENT.equals(tag) && contentView == null) {
                        contentView = rootView.getChildAt(i);
                    }
                    if (TAG_HEADER.equals(tag) && headerView == null) {
                        headerView = (ViewGroup) rootView.getChildAt(i);
                        for (int j = 0; j < headerView.getChildCount(); j++) {
                            Object zoomTag = headerView.getChildAt(j).getTag();
                            if (TAG_ZOOM.equals(zoomTag) && zoomView == null) {
                                zoomView = headerView.getChildAt(j);
                            }
                        }
                    }
                }
            }
        }
        if (headerView == null || zoomView == null || contentView == null) {
            throw new IllegalStateException("content, header, zoom 都不允许为空,请在Xml布局中设置Tag，或者使用属性设置");
        }
        maxY = contentView.getTop();
        headerParams = (MarginLayoutParams) headerView.getLayoutParams();
        headerHeight = headerParams.height;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (isParallax) {
            float scrollY = getScrollY();
            if (scrollY > 0f && scrollY < headerHeight) {
                headerView.scrollTo(0, -(int) (0.65 * scrollY));
            } else {
                headerView.scrollTo(0, 0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isZoomEnable) return super.onTouchEvent(ev);

        float currentX = ev.getX();                   //当前手指相对于当前view的X坐标
        float currentY = ev.getY();                   //当前手指相对于当前view的Y坐标
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = lastEventX = currentX;
                downY = lastEventY = currentY;
                isActionDown = true;
                scroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isActionDown) {
                    downX = lastEventX = currentX;
                    downY = lastEventY = currentY;
                    isActionDown = true;
                    scroller.abortAnimation();
                }
                float dy = currentY - lastEventY;
                lastEventX = currentX;
                lastEventY = currentY;
                if (isTop()) {
                    int height = (int) (headerParams.height + dy / sensitive + 0.5);
                    if (height <= headerHeight) {
                        height = headerHeight;
                        isZooming = false;
                    } else {
                        isZooming = true;
                    }
                    headerParams.height = height;
                    headerView.setLayoutParams(headerParams);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isZooming) {
                    scroller.startScroll(0, headerParams.height, 0, -(headerParams.height - headerHeight), zoomTime);
                    isZooming = false;
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                break;
        }
        return isZooming || super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            headerParams.height = scroller.getCurrY();
            headerView.setLayoutParams(headerParams);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean isTop() {
        return getScrollY() <= 0;
    }
}
