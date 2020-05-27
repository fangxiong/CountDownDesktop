package com.fax.showdt.dialog.pop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.AppInfoAdapter;
import com.fax.showdt.bean.AppInfoData;
import com.fax.showdt.view.cnPinyin.CNPinyin;
import com.fax.showdt.view.cnPinyin.CharIndexView;
import com.fax.showdt.view.cnPinyin.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by fax on 19-5-5.
 */


public class BindAppPop extends BaseCustomPopup implements View.OnClickListener {

    private Context mContext;
    private ISelectOneCallback mSelectOneCallback;
    private ImageView mIvClose;
    private CharIndexView iv_main;
    private TextView tv_index;
    private LinearLayout mLoadingView;
    private AppInfoAdapter adapter;
    private List<CNPinyin<AppInfoData>> appInfoList = new ArrayList<>();

    public BindAppPop(Context context, ISelectOneCallback callback) {
        super(context);
        this.mContext = context;
        this.mSelectOneCallback = callback;
    }

    public void setAppInfoList(List<CNPinyin<AppInfoData>> appInfoList) {
        this.appInfoList.clear();
        if(appInfoList != null) {
            Collections.sort(appInfoList);
            this.appInfoList.addAll(appInfoList);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        mLoadingView.setVisibility(View.GONE);

    }

    @Override
    protected void initAttributes() {
        setContentView(R.layout.bind_app_pop, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusAndOutsideEnable(false)
                .setBackgroundDimEnable(false);
    }

    @Override
    protected void initViews(View view) {
        this.setAnimationStyle(R.style.PopupAnimation);
        mIvClose = view.findViewById(R.id.iv_close);
        mLoadingView = view.findViewById(R.id.ll_loading_view);
        iv_main = view.findViewById(R.id.iv_main);
        tv_index = view.findViewById(R.id.tv_index);
        mIvClose.setOnClickListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AppInfoAdapter(appInfoList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new StickyHeaderDecoration(adapter));
        adapter.setOnSelectOneListener(new AppInfoAdapter.OnSelectOneListener() {
            @Override
            public void selectOne(int pos) {
                mSelectOneCallback.select(appInfoList.get(pos).data);
                Toast.makeText(mContext,mContext.getString(R.string.custom_bind_app,appInfoList.get(pos).data.name), Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
        iv_main.setOnCharIndexChangedListener(new CharIndexView.OnCharIndexChangedListener() {
            @Override
            public void onCharIndexChanged(char currentIndex) {
                for (int i = 0; i < appInfoList.size(); i++) {
                    char firstChar = appInfoList.get(i).getFirstChar();
                    String str = String.valueOf(firstChar);
                    firstChar = str.matches("[a-zA-Z]+") ? firstChar : '#';
                    if (firstChar == currentIndex) {
                        linearLayoutManager.scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }

            @Override
            public void onCharIndexSelected(String currentIndex) {
                if (currentIndex == null) {
                    tv_index.setVisibility(View.INVISIBLE);
                } else {
                    tv_index.setVisibility(View.VISIBLE);
                    tv_index.setText(currentIndex);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == mIvClose) {
            dismiss();
        }
    }

    public interface ISelectOneCallback {
        void select(AppInfoData appInfo);
    }
}
