package com.example.yihuier_phone.ListView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class My_Set_ListView extends ListView {
    public My_Set_ListView(Context context) {
        super(context);
    }
    public My_Set_ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量的大小由一个32位的数字表示，前两位表示测量模式，后30位表示大小，这里需要右移两位才能拿到测量的大小
        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
