package com.fax.showdt.fragment.widgetTextEdit;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.FontBean;
import com.fax.showdt.callback.WidgetEditTextFontSelectedCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-10
 * Description:
 */
public class WidgetTextFontEditFragment extends Fragment {
    private RecyclerView mFontRv;
    private List<FontBean> mFonts = new ArrayList<>();
    private CommonAdapter<FontBean> mFontAdapter;
    private Context mContext;
    private int mCurrentPos = 1;
    private WidgetEditTextFontSelectedCallback mCallback;

    public WidgetTextFontEditFragment(Context context){
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_text_font_edit_fragment, container, false);
        mFontRv = view.findViewById(R.id.rv_font);
        initView();
        return view;
    }

    private void initView(){
        if(mFonts.isEmpty()){
            getFontBeans();
        }
        mFontAdapter = new CommonAdapter<FontBean>(mContext,R.layout.widget_text_edit_font_item,mFonts) {
            @Override
            protected void convert(ViewHolder holder, FontBean fontBean, int position) {
                ImageView ivFont = holder.getView(R.id.iv_font);
                TextView tvFont = holder.getView(R.id.tv_font);
                RelativeLayout mBodyBg = holder.getView(R.id.rootView);
                if(position > 0) {
                    GlideUtils.loadImage(mContext,"file:///android_asset/"+fontBean.getFontIcon(),ivFont);
                    ivFont.setVisibility(View.VISIBLE);
                    tvFont.setVisibility(View.GONE);
                }else {
                    ivFont.setVisibility(View.GONE);
                    tvFont.setVisibility(View.VISIBLE);
                }
                if(mCurrentPos == position){
                    mBodyBg.setBackgroundResource(R.color.c_B5B5B5);
                }else {
                    mBodyBg.setBackgroundResource(R.color.c_F5F5F5);
                }
            }
        };
        mFontAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                RelativeLayout mBodyBg = view.findViewById(R.id.rootView);
                mFontAdapter.notifyItemChanged(mCurrentPos);
                mCurrentPos = position;
                mBodyBg.setBackgroundResource(R.color.c_B5B5B5);
                if(mCallback != null){
                    mCallback.selectTextFont(mFonts.get(position).getFontPath());
                }

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 5);
        mFontRv.setLayoutManager(manager);
        mFontRv.setAdapter(mFontAdapter);
    }

    public void getFontBeans(){
        mFonts = new ArrayList<>();
        String str = FileExUtils.getJsonFromAssest(mContext, "widget_font.json");
        mFonts = GsonUtils.parseJsonArrayWithGson(str,FontBean.class);
        mFonts.add(0,new FontBean());
    }
    public void setWidgetTextFontSeelctedCallback(WidgetEditTextFontSelectedCallback callback){
        this.mCallback = callback;
    }

    public void initFontSelectedPos(String fontPath){
        if(TextUtils.isEmpty(fontPath)){
            mCurrentPos = 1;
            mFontAdapter.notifyDataSetChanged();
            return;
        }
        if(mFonts.isEmpty()){
            getFontBeans();
        }
        getFontBeans();
        for (int i =0;i<mFonts.size();i++) {
            FontBean bean = mFonts.get(i);
            if(fontPath.equals(bean.getFontPath())){
                mCurrentPos = i;
                mFontAdapter.notifyDataSetChanged();
            }
        }
    }

}
