package com.fax.showdt.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.activity.DiyWidgetMakeActivity;
import com.fax.showdt.activity.WidgetSelectedActivity;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.dialog.ios.interfaces.OnMenuItemClickListener;
import com.fax.showdt.dialog.ios.util.DialogSettings;
import com.fax.showdt.dialog.ios.util.ShareUtils;
import com.fax.showdt.dialog.ios.v3.BottomMenu;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.manager.CommonConfigManager;
import com.fax.showdt.manager.widget.CustomWidgetConfigDao;
import com.fax.showdt.manager.widget.WidgetManager;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.TimeUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.fax.showdt.utils.WidgetDataHandlerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyWidgetFragment extends Fragment {
    private final String[] menus = {"编辑","删除","分享"};
    private RecyclerView mRv;
    private CommonAdapter<CustomWidgetConfig> mAdapter;
    private List<CustomWidgetConfig> mData = new ArrayList<>();
    private Disposable disposable;
    private TipDialog mTipsDialog;
    private SwipeRefreshLayout mRefreshLayout;

    public MyWidgetFragment(){}
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
        mAdapter = new CommonAdapter<CustomWidgetConfig>(getActivity(), R.layout.my_widget_mine_item, mData) {
            @Override
            protected void convert(ViewHolder holder, CustomWidgetConfig customWidgetConfig, int position) {
                ImageView ivWidget = holder.getView(R.id.iv_widget);
                ImageView ivMenu = holder.getView(R.id.iv_menu);
                TextView tvTitle = holder.getView(R.id.tv_title);
                tvTitle.setText(TimeUtils.stampToDate(customWidgetConfig.getCreatedTime()));
                GlideUtils.loadImage(getActivity(), customWidgetConfig.getCoverUrl(), ivWidget);
                ivMenu.setOnClickListener(new OnMenuClickListener(customWidgetConfig));
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
                DiyWidgetMakeActivity.startSelf(getActivity(),config);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private class OnMenuClickListener implements View.OnClickListener{
        private CustomWidgetConfig config;
        public OnMenuClickListener(CustomWidgetConfig customWidgetConfig){
            this.config = customWidgetConfig;
        }
        @Override
        public void onClick(View v) {
            BottomMenu.show((AppCompatActivity)getActivity(), menus, new OnMenuItemClickListener() {
                @Override
                public void onClick(String text, int index) {
                    switch (index){
                        case 0:{
                            DiyWidgetMakeActivity.startSelf(getActivity(),config);
                            break;
                        }
                        case 1:{
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    CustomWidgetConfigDao.getInstance(getActivity()).delete(Collections.singletonList(config));
                                    FileExUtils.deleteSingleFile(config.getCoverUrl());
                                    FileExUtils.deleteSingleFile(config.getBgPath());
                                    e.onNext(true);
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Boolean>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            ToastShowUtils.showCommonToast(getActivity(),"删除成功",Toasty.LENGTH_SHORT);
                                            mData.remove(config);
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ToastShowUtils.showCommonToast(getActivity(),"删除失败",Toasty.LENGTH_SHORT);
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                            break;
                        }
                        case 2:{
                            ShareUtils.shareImg(getActivity(),"我用《插件秀》做的插件，很好用哦，推荐给你...",config.getCoverUrl());
                            break;
                        }
                    }
                }
            });
        }
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
                        Collections.sort(mData);
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
