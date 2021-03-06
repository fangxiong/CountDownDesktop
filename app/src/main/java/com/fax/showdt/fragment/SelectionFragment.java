package com.fax.showdt.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.activity.DiyWidgetMakeActivity;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.WidgetConfig;
import com.fax.showdt.bean.widgetClassification;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SelectionFragment extends BaseFragment {

    private static final String CID_KEY = "cid_key";
    private RecyclerView mRv;
    private CommonAdapter<WidgetConfig> mAdapter;
    private List<WidgetConfig> data = new ArrayList<>();
    private SwipeRefreshLayout mRefreshLayout;
    private String mCid;


    public SelectionFragment() {
    }

    private SelectionFragment(String cid) {
        this.mCid = cid;
    }

    public static SelectionFragment newInstance(String cid) {
        return new SelectionFragment(cid);
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        initView();
    }

    @Override
    protected void initData() {
        queryWidgetFromCid();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.selection_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCid = savedInstanceState.getString(CID_KEY);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CID_KEY, mCid);

    }

    private void queryWidgetFromCid() {
        WaitDialog.show((AppCompatActivity) getActivity(), "加载中");
        BmobQuery<WidgetConfig> query = new BmobQuery<>();
        query.addWhereEqualTo("cid", mCid);
        query.findObjects(new FindListener<WidgetConfig>() {
            @Override
            public void done(List<WidgetConfig> list, final BmobException e) {
                mRefreshLayout.setRefreshing(false);
                if (e == null) {
                    WaitDialog.dismiss();
                    data.clear();
                    Collections.sort(list, new Comparator<WidgetConfig>() {
                        @Override
                        public int compare(WidgetConfig o1, WidgetConfig o2) {
                            long result = o2.getSort() - o1.getSort();
                            if (result > 0) {
                                return 1;
                            } else if (result < 0) {
                                return -1;
                            } else
                                return 0;
                        }
                    });
                    data.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
                    WaitDialog.show((AppCompatActivity) getActivity(), "请求失败", TipDialog.TYPE.ERROR);
                    Log.i("SelectionFragment", e.getMessage());
                }
            }
        });
    }

    private void initView() {
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mAdapter = new CommonAdapter<WidgetConfig>(getActivity(), R.layout.widget_show_item, data) {
            @Override
            protected void convert(ViewHolder holder, WidgetConfig widgetConfig, int position) {
                ImageView ivWidget = holder.getView(R.id.iv_widget);
                CustomWidgetConfig customWidgetConfig = GsonUtils.parseJsonWithGson(widgetConfig.getConfig(), CustomWidgetConfig.class);
                GlideUtils.loadRoundCircleImage(getActivity(), customWidgetConfig.getCoverUrl(), ivWidget, 10);
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRv.setLayoutManager(gridLayoutManager);
        mRv.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryWidgetFromCid();
            }
        });
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                try {
                    WidgetConfig widgetConfig = data.get(position);
                    DiyWidgetMakeActivity.startSelf(getActivity(), GsonUtils.toJsonWithSerializeNulls(widgetConfig), true);
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

}
