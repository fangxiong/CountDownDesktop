package com.fax.showdt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.dialog.ios.util.DialogSettings;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.manager.CommonConfigManager;
import com.fax.showdt.manager.widget.CustomWidgetConfigDao;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.utils.GlideUtils;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyWidgetFragment extends Fragment {
    private RecyclerView mRv;
    private CommonAdapter<CustomWidgetConfig> mAdapter;
    private List<CustomWidgetConfig> mData = new ArrayList<>();
    private Disposable disposable;
    private TipDialog mTipsDialog;
    private SwipeRefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_mine_fragment, container, false);
        mRv = view.findViewById(R.id.rv);
        mRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        initView();
        queryDataFromDataBase();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable != null){
            disposable.dispose();
        }
    }

    private void initView() {
        mAdapter = new CommonAdapter<CustomWidgetConfig>(getActivity(), R.layout.widget_mine_item, mData) {
            @Override
            protected void convert(ViewHolder holder, CustomWidgetConfig customWidgetConfig, int position) {
                ImageView ivWidget = holder.getView(R.id.iv_widget);
                GlideUtils.loadRoundCircleImage(getActivity(), customWidgetConfig.getCoverUrl(), ivWidget, 10);
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRv.setLayoutManager(gridLayoutManager);
        mRv.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryDataFromDataBase();
            }
        });
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                CustomWidgetConfig config = mData.get(position);
                CommonConfigManager.getInstance().setWidgetConfig(config);
                WidgetUpdateService.startSelf(getContext());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private void queryDataFromDataBase() {
        CustomWidgetConfigDao.getInstance(getActivity()).getAllConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        DialogSettings.tipTheme = DialogSettings.THEME.LIGHT;
                        mTipsDialog =WaitDialog.show((AppCompatActivity) getActivity(),"加载中...");
                    }
                })
                .subscribe(new SingleObserver<List<CustomWidgetConfig>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<CustomWidgetConfig> customWidgetConfigs) {
                        if(disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                        mData.clear();
                        mData.addAll(customWidgetConfigs);
                        mAdapter.notifyDataSetChanged();
                        mTipsDialog.doDismiss();
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTipsDialog.doDismiss();
                        mRefreshLayout.setRefreshing(false);
                    }
                });

    }


}
