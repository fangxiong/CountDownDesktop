package com.fax.showdt.dialog.pop;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.fax.showdt.R;
import com.fax.showdt.utils.ScreenUtils;


public class EverywherePopup extends BasePopup<EverywherePopup> implements View.OnClickListener{

    public OnClickCallback onClickCallback;

    public static EverywherePopup create(Context context) {
        return new EverywherePopup(context);
    }

    private EverywherePopup(Context context) {
        setContext(context);
    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.widget_edit_top_dialog)
                .setAnimationStyle(R.style.LeftTopPopAnim);
    }

    @Override
    protected void initViews(View view, EverywherePopup basePopup) {
        TextView tvCopy = view.findViewById(R.id.tv_copy);
        TextView tvTop = view.findViewById(R.id.tv_top);
        TextView tvBottom = view.findViewById(R.id.tv_bottom);
        tvCopy.setOnClickListener(this);
        tvTop.setOnClickListener(this);
        tvBottom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if(resId == R.id.tv_copy){
            if(onClickCallback != null){
                onClickCallback.copy();
            }
            dismiss();
        }else if(resId == R.id.tv_top){
            if(onClickCallback != null){
                onClickCallback.top();
            }
            dismiss();
        }else if(resId == R.id.tv_bottom){
            if(onClickCallback != null){
                onClickCallback.bottom();
            }
            dismiss();
        }
    }

    /**
     * 自适应触摸点 弹出
     * @param parent
     * @param touchX
     * @param touchY
     * @return
     */
    public EverywherePopup showEverywhere(View parent, int touchX, int touchY) {
//        if (isRealWHAlready()) {
            int screenHeight = ScreenUtils.getScreenHeight();
            int screenWidth = ScreenUtils.getScreenWidth();
            int offsetX=touchX-getWidth();
            int offsetY=touchY;
            if (touchX<getWidth() && screenHeight-touchY<getHeight()){
                //左下弹出动画
                getPopupWindow().setAnimationStyle(R.style.LeftBottomPopAnim);
                offsetY=touchY-getHeight();
            }else if (touchX+getWidth()>screenWidth && touchY+getHeight()>screenHeight){
                //右下弹出动画
                getPopupWindow().setAnimationStyle(R.style.RightBottomPopAnim);
                offsetX=(touchX-getWidth());
                offsetY=touchY-getHeight();
            }else if (touchX+getWidth()>screenWidth){
                getPopupWindow().setAnimationStyle(R.style.RightTopPopAnim);
                offsetX=(touchX-getWidth());
            }else {
                getPopupWindow().setAnimationStyle(R.style.RightTopPopAnim);
            }

            showAtLocation(parent, Gravity.NO_GRAVITY,offsetX,offsetY);
//        }
        return this;
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public interface OnClickCallback{
        void copy();
        void top();
        void bottom();
    }
}
