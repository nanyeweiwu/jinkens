package com.dzn.pointupdemo.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullRefreshView extends RelativeLayout {
    TextView headView;
    float downY;

    public PullRefreshView(Context context) {
        this(context, null);
    }

    public PullRefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeadView();
    }

    private void initHeadView() {
        headView = new TextView(getContext());
        headView.setText("这是个刷新头视图");
        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getChildCount() > 0) {
                    params.addRule(RelativeLayout.ABOVE, getChildAt(0).getId());
                    addView(headView, params);
                } else {
                    throw new IllegalStateException("不能没有自View");
                }
                if (getViewTreeObserver() != null && getViewTreeObserver().isAlive()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = ev.getRawY();
                onDragMove(offset);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = event.getRawY();
                onDragMove(offset);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onDragMove(float offset) {
        if (getChildCount() > 0 && offset < 0) {
            ((RelativeLayout.LayoutParams) getChildAt(1).getLayoutParams()).setMargins(0, -(int) offset, 0, 0);
            requestLayout();
        }
    }

}
