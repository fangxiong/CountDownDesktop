package com.fax.showdt.fragment.widgetStickerEdit;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.AppInfoAdapter;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.AppInfo;
import com.fax.showdt.bean.AppInfoData;
import com.fax.showdt.callback.AppIconSelectCallback;
import com.fax.showdt.utils.AppIconUtils;
import com.fax.showdt.view.cnPinyin.CNPinyin;
import com.fax.showdt.view.cnPinyin.CNPinyinFactory;
import com.fax.showdt.view.cnPinyin.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:     本地桌面图标
 * Author:          fax
 * CreateDate:      2020-05-26 19:52
 * Email:           fxiong1995@gmail.com
 */
public class WidgetLocalAppIconFragment extends Fragment {

    RecyclerView rvContent;
    ProgressBar progressBar;
    private CommonAdapter<CNPinyin<AppInfoData>> mAdapter;
    private AppIconSelectCallback callback;
    private List<CNPinyin<AppInfoData>> appInfoList = new ArrayList<>();


    public WidgetLocalAppIconFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_local_app_icon_fragment, container, false);
        rvContent = view.findViewById(R.id.rv_content);
        progressBar = view.findViewById(R.id.pb);
        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        reqData(context);
    }


    private void reqData(final Context context) {
        if (appInfoList.size() > 0) {
            return;
        }

        Observable.create(new ObservableOnSubscribe<List<AppInfoData>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AppInfoData>> emitter) throws Exception {
                emitter.onNext(AppIconUtils.getInstallApps(getActivity()));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppInfoData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AppInfoData> appInfos) {
                        appInfoList.clear();
                        List<CNPinyin<AppInfoData>> list = CNPinyinFactory.createCNPinyinList(appInfos);
                        if(list != null) {
                            Collections.sort(list);
                            appInfoList.addAll(list);
                            progressBar.setVisibility(View.GONE);
                            initUI();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    private void initUI() {
        mAdapter = new CommonAdapter<CNPinyin<AppInfoData>>(getActivity(), R.layout.widget_app_icon_item, appInfoList) {
            @Override
            protected void convert(ViewHolder holder, final CNPinyin<AppInfoData> appInfo, int position) {
                try {
                    ImageView imageView = holder.getView(R.id.iv_element);
                    imageView.setImageDrawable(appInfo.data.getIcon());
                } catch (IndexOutOfBoundsException e) {

                }

            }
        };
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (callback != null) {
                    callback.select(appInfoList.get(position).data);
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 7, RecyclerView.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
//                return mCurrentStickerBean.get(position).getStickerCategory().equals("表情")? 1 : 2;
                return 1;
            }
        });

        rvContent.setLayoutManager(manager);
        rvContent.setAdapter(mAdapter);
        Log.e("test_icon_adapter:", "初始化adapter");

    }

    public void setElementCallback(AppIconSelectCallback callback) {
        this.callback = callback;
    }
}
