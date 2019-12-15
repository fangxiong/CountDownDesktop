package com.fax.showdt.fragment.widgetStickerEdit;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.StickerBean;
import com.fax.showdt.callback.WidgetEditStickerElementSelectedCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WidgetStickerElementEditFragment extends Fragment {

    private CommonAdapter<String> mListAdapter;
    private CommonAdapter<StickerBean> mStickerAdapter;
    private List<StickerBean> mAllStickerBean=new ArrayList<>();
    private List<StickerBean> mCurrentStickerBean =new ArrayList<>();
    private Context mContext;
    private RecyclerView mStickerContentRv,mStickerListRv;
    private String[] titles = new String[]{"表情","love","对话框","便签","课程表"};
    private int mCurrentSelectedPos = 0;
    private WidgetEditStickerElementSelectedCallback mWidgetEditStickertElementSelectedCallback;


    public WidgetStickerElementEditFragment(Context context){
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_sticker_element_edit_fragment, container, false);
        mStickerContentRv = view.findViewById(R.id.rv_content);
        mStickerListRv = view.findViewById(R.id.rv_list);
        initTextPlugSelectUI();
        return view;
    }


    private void initTextPlugSelectUI() {
        mStickerAdapter = new CommonAdapter<StickerBean>(mContext,R.layout.widget_sticker_content_item, mCurrentStickerBean) {
            @Override
            protected void convert(ViewHolder holder, StickerBean stickerBean, int position) {
                ImageView mIvElement = holder.getView(R.id.iv_element);
                Log.i("test_img:","file:///android_asset/"+stickerBean.getStickerPath());
                GlideUtils.loadImage(mContext,"file:///android_asset/"+stickerBean.getStickerPath(),mIvElement);
            }
        };
        mStickerAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                StickerBean bean = mCurrentStickerBean.get(position);
                mWidgetEditStickertElementSelectedCallback.selectSticker(bean.getStickerPath());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        final GridLayoutManager manager = new GridLayoutManager(mContext, 8, RecyclerView.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mCurrentStickerBean.get(position).getStickerCategory().equals("表情")? 1 : 2;
            }
        });

        mStickerContentRv.setLayoutManager(manager);
        mStickerContentRv.setAdapter(mStickerAdapter);

        mStickerListRv.setLayoutManager(new LinearLayoutManager(mContext));
        mListAdapter = new CommonAdapter<String>(mContext,R.layout.widget_text_element_list_item, Arrays.asList(titles)) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView mTv = holder.getView(R.id.tv_element);
                mTv.setText(s);
                if(position == mCurrentSelectedPos){
                    mTv.setBackgroundResource(R.color.c_FCF43C);
                }else {
                    mTv.setBackgroundResource(R.color.transparent);
                }
            }
        };
        mListAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                dataSetChange(position);
                TextView mTv = view.findViewById(R.id.tv_element);
                mTv.setBackgroundResource(R.color.c_FCF43C);
                mListAdapter.notifyItemChanged(mCurrentSelectedPos);
                mCurrentSelectedPos = position;
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mStickerListRv.setAdapter(mListAdapter);
        dataSetChange(0);

    }

    private void dataSetChange(int pos){
        if(mAllStickerBean.isEmpty()) {
            String str = FileExUtils.getJsonFromAssest(mContext, "widget_sticker.json");
            mAllStickerBean = GsonUtils.parseJsonArrayWithGson(str, StickerBean.class);
        }
        mCurrentStickerBean.clear();
        for(StickerBean bean: mAllStickerBean){
            if(bean.getStickerCategory().equals(titles[pos])) {
                mCurrentStickerBean.add(bean);
            }
        }
        mStickerAdapter.notifyDataSetChanged();
    }


    public void setmWidgetEditStickertElementSelectedCallback(WidgetEditStickerElementSelectedCallback callback) {
        mWidgetEditStickertElementSelectedCallback= callback;
    }
}
