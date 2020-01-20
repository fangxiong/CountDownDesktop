package com.fax.showdt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.db.WidgetDao;
import com.fax.showdt.dialog.ios.interfaces.OnMenuItemClickListener;
import com.fax.showdt.dialog.ios.util.DialogSettings;
import com.fax.showdt.dialog.ios.v3.BottomMenu;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.fragment.MyWidgetFragment;
import com.fax.showdt.manager.CommonConfigManager;
import com.fax.showdt.manager.widget.CustomWidgetConfigDao;
import com.fax.showdt.manager.widget.WidgetManager;
import com.fax.showdt.service.NLService;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.TimeUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.fax.showdt.utils.WidgetDataHandlerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
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

public class WidgetSelectedActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRv;
    private CommonAdapter<CustomWidgetConfig> mAdapter;
    private List<CustomWidgetConfig> mData = new ArrayList<>();
    private Disposable disposable;
    private TipDialog mTipsDialog;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayout llTipContent;
    private String mWidgetId;
    private String[] menus = {"设置插件", "设置插件(包含壁纸)", "删除"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_selected_activity);
        mRv = findViewById(R.id.rv);
        mRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        llTipContent = findViewById(R.id.ll_make_tip_content);
        initData();
        restartWidgetService();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
        restartWidgetService();
    }

    @Override
    protected boolean isEnableImmersionBar() {
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                queryDataFromDataBase();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.iv_back) {
            finish();
        } else if (resId == R.id.iv_make) {
            startActivity(new Intent(WidgetSelectedActivity.this, DiyWidgetMakeActivity.class));

        } else if (resId == R.id.tv_make) {
            startActivity(new Intent(WidgetSelectedActivity.this, DiyWidgetMakeActivity.class));
        }
    }

    private void restartWidgetService(){
        WidgetUpdateService.startSelf(this);
        NLService.startSelf(this);
    }


    private void initData() {
        mWidgetId = getIntent().getStringExtra(ConstantString.widget_id);
        mAdapter = new CommonAdapter<CustomWidgetConfig>(this, R.layout.my_widget_mine_item, mData) {
            @Override
            protected void convert(ViewHolder holder, CustomWidgetConfig customWidgetConfig, int position) {
                ImageView ivWidget = holder.getView(R.id.iv_widget);
                ImageView ivMenu = holder.getView(R.id.iv_menu);
                TextView tvTitle = holder.getView(R.id.tv_title);
                tvTitle.setText(TimeUtils.stampToDate(customWidgetConfig.getCreatedTime()));
                GlideUtils.loadImage(WidgetSelectedActivity.this, customWidgetConfig.getCoverUrl(), ivWidget);
                ivMenu.setOnClickListener(new OnMenuClickListener(customWidgetConfig));
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(WidgetSelectedActivity.this, 2);
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
            public void onItemClick(View view, RecyclerView.ViewHolder holder, final int position) {
                final CustomWidgetConfig config = mData.get(position);
                config.setDrawWithBg(false);
                if (!TextUtils.isEmpty(mWidgetId)) {
                    Log.i("test_widget", "put widget" + mWidgetId);
                    sendConfigChangedBroadcast();
                    WidgetDataHandlerUtils.putWidgetDataWithId(mWidgetId, config.toJSONString(), ConstantString.widget_map_data_key);
                    ToastShowUtils.showCommonToast(WidgetSelectedActivity.this, "设置成功", Toasty.LENGTH_SHORT);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private void sendConfigChangedBroadcast() {
        Intent intent = new Intent();
        intent.setAction(WidgetUpdateService.WIDGET_CONFIG_CHANGED);
        sendBroadcast(intent);
    }

    private class OnMenuClickListener implements View.OnClickListener {
        private CustomWidgetConfig config;

        public OnMenuClickListener(CustomWidgetConfig customWidgetConfig) {
            this.config = customWidgetConfig;
        }

        @Override
        public void onClick(View v) {
            BottomMenu.show(WidgetSelectedActivity.this, menus, new OnMenuItemClickListener() {
                @Override
                public void onClick(String text, int index) {
                    switch (index) {
                        case 0: {
                            config.setDrawWithBg(false);
                            if (!TextUtils.isEmpty(mWidgetId)) {
                                Log.i("test_widget", "put widget" + mWidgetId);
                                WidgetDataHandlerUtils.putWidgetDataWithId(mWidgetId, config.toJSONString(), ConstantString.widget_map_data_key);
                                WidgetManager.getInstance().changeWidgetInfo();
                                ToastShowUtils.showCommonToast(WidgetSelectedActivity.this, "设置成功", Toasty.LENGTH_SHORT);
                            }
                            break;
                        }
                        case 1: {
                            config.setDrawWithBg(true);
                            if (!TextUtils.isEmpty(mWidgetId)) {
                                Log.i("test_widget", "put widget" + mWidgetId);
                                WidgetDataHandlerUtils.putWidgetDataWithId(mWidgetId, config.toJSONString(), ConstantString.widget_map_data_key);
                                WidgetManager.getInstance().changeWidgetInfo();
                                ToastShowUtils.showCommonToast(WidgetSelectedActivity.this, "设置成功", Toasty.LENGTH_SHORT);
                            }
                            break;
                        }
                        case 2: {
                            Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                                    CustomWidgetConfigDao.getInstance(WidgetSelectedActivity.this).delete(Collections.singletonList(config));
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
                                            ToastShowUtils.showCommonToast(WidgetSelectedActivity.this, "删除成功", Toasty.LENGTH_SHORT);
                                            mData.remove(config);
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ToastShowUtils.showCommonToast(WidgetSelectedActivity.this, "删除失败", Toasty.LENGTH_SHORT);
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    }
                }
            });
        }
    }

    private void queryDataFromDataBase() {
        CustomWidgetConfigDao.getInstance(WidgetSelectedActivity.this).getAllConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        DialogSettings.tipTheme = DialogSettings.THEME.LIGHT;
                        mTipsDialog = WaitDialog.show(WidgetSelectedActivity.this, "加载中...");
                    }
                })
                .subscribe(new SingleObserver<List<CustomWidgetConfig>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<CustomWidgetConfig> customWidgetConfigs) {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                        mData.clear();
                        mData.addAll(customWidgetConfigs);
                        Collections.sort(mData);
                        if (mData.isEmpty()) {
                            llTipContent.setVisibility(View.VISIBLE);
                        } else {
                            llTipContent.setVisibility(View.GONE);
                        }
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
