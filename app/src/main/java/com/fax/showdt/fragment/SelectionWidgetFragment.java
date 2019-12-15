package com.fax.showdt.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.TestBen;
import com.fax.showdt.bean.TestData;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;

public class SelectionWidgetFragment extends Fragment {

    private RecyclerView mRv;
    private CommonAdapter<TestBen> mAdapter;
    private List<TestBen> mData = new ArrayList<>();
    private TipDialog mTipsDialog;
    private SwipeRefreshLayout mRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selection_widget_fragment,container,false);
        mRv = view.findViewById(R.id.rv);
        mRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        initView();
        reqHomeWidgetData();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initView(){
        mAdapter = new CommonAdapter<TestBen>(getActivity(),R.layout.widget_show_item,mData) {
            @Override
            protected void convert(ViewHolder holder, TestBen testBen, int position) {
                ImageView ivWidget = holder.getView(R.id.iv_widget);
                GlideUtils.loadRoundCircleImage(getActivity(),testBen.getUrl(),ivWidget, 10);
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        mRv.setLayoutManager(gridLayoutManager);
        mRv.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reqHomeWidgetData();
            }
        });
    }

    private void reqHomeWidgetData(){
        mTipsDialog = WaitDialog.show((AppCompatActivity) getActivity(),"加载中...");
        OkHttpUtils.get().url("http://q2cjxj02l.bkt.clouddn.com/test1.txt").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mTipsDialog.doDismiss();
                mRefreshLayout.setRefreshing(false);
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                mTipsDialog.doDismiss();
                Log.i("test_result;",response);
                if(response != null){
                    TestData testData = GsonUtils.parseJsonWithGson(response,TestData.class);
                    mData.clear();
                    mData.addAll(testData.getList());
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }




}
