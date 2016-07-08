package com.ooxx;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

public class MyViewPager extends ViewPager{

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        Log.d("xxx", "getChildDrawingOrder");
        return super.getChildDrawingOrder(childCount, i);
    }
}
