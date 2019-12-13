package com.fax.showdt.fragment.widgetTextEdit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.CustomTextPlugItemAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomPlugTextBean;
import com.fax.showdt.callback.WidgetEditTextElementSelectedCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-10
 * Description:
 */
public class WidgetTextElementEditFragment extends Fragment {
    private CustomTextPlugItemAdapter mCustomTextPlugItemAdapter;
    private CommonAdapter<String> mListAdapter;
    private List<CustomPlugTextBean> mAllTextPlugs, mCurrentTextPlugs;
    private Context mContext;
    private RecyclerView mPlugContentRv,mPlugListRv;
    private String[] titles = new String[]{"倒计时", "时间日期","天气信息","电量"};
    private int mCurrentSelectedPos = 0;
    private WidgetEditTextElementSelectedCallback mWidgetEditTextElementSelectedCallback;


    public WidgetTextElementEditFragment(Context context){
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_text_element_edit_fragment, container, false);
        mPlugContentRv = view.findViewById(R.id.rv_content);
        mPlugListRv = view.findViewById(R.id.rv_list);
        initTextPlugSelectUI();
        return view;
    }


    private void initTextPlugSelectUI() {
        mAllTextPlugs = new ArrayList<>();
        mCurrentTextPlugs = new ArrayList<>();
        String str = FileExUtils.getJsonFromAssest(mContext, "widget_plug.json");
        mAllTextPlugs = GsonUtils.parseJsonArrayWithGson(str, CustomPlugTextBean.class);
        mCustomTextPlugItemAdapter = new CustomTextPlugItemAdapter(mContext, mCurrentTextPlugs);
        final GridLayoutManager manager = new GridLayoutManager(mContext, 3, RecyclerView.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mCustomTextPlugItemAdapter.getItemViewType(position) == mCustomTextPlugItemAdapter.TAG_ITEM ? manager.getSpanCount() : 1;
            }
        });

        mPlugContentRv.setLayoutManager(manager);
        mPlugContentRv.setAdapter(mCustomTextPlugItemAdapter);
        mCustomTextPlugItemAdapter.setAddTextPlugListener(new CustomTextPlugItemAdapter.OnClickTextPlugListener() {
            @Override
            public void onClickTextPlug(CustomPlugTextBean bean) {
                if (bean.getCategory().equals("倒计时")) {
                    if(mWidgetEditTextElementSelectedCallback != null){
                        mWidgetEditTextElementSelectedCallback.selectTextElement(bean.getTag(),true);
                    }
                } else {
                    if(mWidgetEditTextElementSelectedCallback != null) {
                        mWidgetEditTextElementSelectedCallback.selectTextElement(bean.getTag(), false);
                    }
                }
            }
        });
        mPlugListRv.setLayoutManager(new LinearLayoutManager(mContext));
        mListAdapter = new CommonAdapter<String>(mContext,R.layout.widget_text_element_list_item,Arrays.asList(titles)) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView mTv = holder.getView(R.id.tv_element);
                mTv.setText(s);
                if(position == mCurrentSelectedPos){
                    mTv.setBackgroundResource(R.color.c_BDBDBA);
                }else {
                    mTv.setBackgroundResource(R.color.transparent);
                }
            }
        };
        mListAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                TextView mTv = view.findViewById(R.id.tv_element);
                mTv.setBackgroundResource(R.color.c_BDBDBA);
                dataSetChanged(position);
                mListAdapter.notifyItemChanged(mCurrentSelectedPos);
                mCurrentSelectedPos = position;
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mPlugListRv.setAdapter(mListAdapter);
        dataSetChanged(0);

    }
    private void dataSetChanged(int pos) {
        mCurrentTextPlugs.clear();
        for (CustomPlugTextBean bean : mAllTextPlugs) {
            if (titles[pos].equals(bean.getCategory())) {
                mCurrentTextPlugs.add(bean);
            }
        }
        mCustomTextPlugItemAdapter.notifyDataSetChanged();
    }

    public void setWidgetEditTextElementSelectedCallback(WidgetEditTextElementSelectedCallback widgetEditTextElementSelectedCallback) {
        mWidgetEditTextElementSelectedCallback = widgetEditTextElementSelectedCallback;
    }
}
