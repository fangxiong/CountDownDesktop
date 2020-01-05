package com.fax.showdt.fragment.widgetTextEdit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fax.showdt.R;
import com.fax.showdt.activity.DiyWidgetMakeActivity;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.CustomTextPlugItemAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomPlugTextBean;
import com.fax.showdt.callback.WidgetEditTextElementSelectedCallback;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.service.NLService;
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
    private RecyclerView mPlugContentRv,mPlugListRv;
    private String[] titles = new String[]{"倒计时", "音乐","时间日期","天气信息","进度"};
    private int mCurrentSelectedPos = 0;
    private WidgetEditTextElementSelectedCallback mWidgetEditTextElementSelectedCallback;

    public WidgetTextElementEditFragment(){}

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
        String str = FileExUtils.getJsonFromAssest(getActivity(), "widget_plug.json");
        mAllTextPlugs = GsonUtils.parseJsonArrayWithGson(str, CustomPlugTextBean.class);
        mCustomTextPlugItemAdapter = new CustomTextPlugItemAdapter(getActivity(), mCurrentTextPlugs);
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false);
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
                }else if(bean.getCategory().equals("音乐")){
                    showOpenNotifyPermissionDialog(bean.getTag());
                } else {
                    if(mWidgetEditTextElementSelectedCallback != null) {
                        mWidgetEditTextElementSelectedCallback.selectTextElement(bean.getTag(), false);
                    }
                }
            }
        });
        mPlugListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListAdapter = new CommonAdapter<String>(getActivity(),R.layout.widget_text_element_list_item,Arrays.asList(titles)) {
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
                TextView mTv = view.findViewById(R.id.tv_element);
                mTv.setBackgroundResource(R.color.c_FCF43C);
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

    private void showOpenNotifyPermissionDialog(String element) {
        if (!NLService.isNotificationListenerEnabled(getActivity())) {
            MessageDialog.show((AppCompatActivity) getActivity(),"提示","该插件需要开启通知栏权限才可使用").setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                @Override
                public boolean onClick(BaseDialog baseDialog, View v) {
                    gotoOpen();
                    return false;
                }
            });
        } else {
          if(mWidgetEditTextElementSelectedCallback != null){
              mWidgetEditTextElementSelectedCallback.selectTextElement(element,false);
          }
        }
    }

    private void gotoOpen() {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            getActivity().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setWidgetEditTextElementSelectedCallback(WidgetEditTextElementSelectedCallback widgetEditTextElementSelectedCallback) {
        mWidgetEditTextElementSelectedCallback = widgetEditTextElementSelectedCallback;
    }
}
