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
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;

import java.util.ArrayList;
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

public class SelectionFragment extends Fragment {

    private RecyclerView mRv;
    private CommonAdapter<WidgetConfig> mAdapter;
    private List<WidgetConfig> data = new ArrayList<>();
    private SwipeRefreshLayout mRefreshLayout;
    private String mCid;

    public SelectionFragment(){}
    private SelectionFragment(String cid){
        this.mCid = cid;
    }

    public static SelectionFragment newInstance(String cid){
        return new SelectionFragment(cid);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selection_fragment,container,false);
        mRv = view.findViewById(R.id.rv);
        mRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        initView();
        queryWidgetFromCid();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void queryWidgetFromCid(){
        BmobQuery<WidgetConfig> query = new BmobQuery<>();
        query.addWhereEqualTo("cid",mCid);
        query.findObjects(new FindListener<WidgetConfig>() {
            @Override
            public void done(List<WidgetConfig> list, BmobException e) {
                mRefreshLayout.setRefreshing(false);
                if(e == null) {
                    WaitDialog.dismiss();
                    data.clear();
                    data.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }else {
                    WaitDialog.show((AppCompatActivity) getActivity(),"请求失败", TipDialog.TYPE.ERROR);
                    Log.i("SelectionFragment", e.getMessage());
                }
            }
        });
    }

    private void initView(){
        mAdapter = new CommonAdapter<WidgetConfig>(getActivity(),R.layout.widget_show_item,data) {
            @Override
            protected void convert(ViewHolder holder, WidgetConfig widgetConfig, int position) {
                ImageView ivWidget = holder.getView(R.id.iv_widget);
                CustomWidgetConfig customWidgetConfig = GsonUtils.parseJsonWithGson(widgetConfig.getConfig(),CustomWidgetConfig.class);
                GlideUtils.loadRoundCircleImage(getActivity(),customWidgetConfig.getCoverUrl(),ivWidget, 10);
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
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
                WidgetConfig widgetConfig = data.get(position);
                DiyWidgetMakeActivity.startSelf(getActivity(),GsonUtils.toJsonWithSerializeNulls(widgetConfig),true);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

}
