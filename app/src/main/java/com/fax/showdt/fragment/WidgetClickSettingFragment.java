package com.fax.showdt.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.AppInfo;
import com.fax.showdt.callback.WidgetEditClickCallback;
import com.fax.showdt.dialog.ios.interfaces.OnInputDialogButtonClickListener;
import com.fax.showdt.dialog.ios.interfaces.OnShowListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.util.InputInfo;
import com.fax.showdt.dialog.ios.util.TextInfo;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
import com.fax.showdt.dialog.ios.v3.FullScreenDialog;
import com.fax.showdt.dialog.ios.v3.InputDialog;
import com.fax.showdt.manager.widget.WidgetClickType;
import com.fax.showdt.manager.widget.WidgetMusicActionType;
import com.fax.showdt.utils.AppIconUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.fax.showdt.dialog.ios.v3.TipDialog.dismiss;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-28
 * Description:
 */
public class WidgetClickSettingFragment extends Fragment implements View.OnClickListener {

    private TextView mTvClickAction, mTvClickTypeTitle, mTvClickTypeContent;
    private LinearLayout llClickContent;
    private String[] actions = {"无", "启动应用程序", "音乐控制", "打开链接"};
    private String[] actionTypes = {"应用", "音乐", "网址"};
    private String[] musicActions = {"播放/暂停", "下一首", "上一首", "音量+", "音量-", "打开播放器"};
    private CustomDialog clickActionDialog, musicControlDialog;
    private FullScreenDialog appIconDialog;
    private WidgetEditClickCallback editClickCallback;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private List<AppInfo> appInfoList = new ArrayList<>();
    private ImageView mIvClose;
    private CommonAdapter<AppInfo> adapter;
    private RecyclerView mRecyclerView;

    public WidgetClickSettingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_click_setting_fragment, container, false);
        mTvClickAction = view.findViewById(R.id.tv_click_action);
        mTvClickTypeTitle = view.findViewById(R.id.tv_click_type_title);
        mTvClickTypeContent = view.findViewById(R.id.tv_click_type_content);
        llClickContent = view.findViewById(R.id.ll_click_content);
        mTvClickAction.setOnClickListener(this);
        mTvClickTypeTitle.setOnClickListener(this);
        mTvClickTypeContent.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    @Override
    public void onClick(View v) {
        if (v == mTvClickAction) {
            showClickActionDialog();
        } else if (v == mTvClickTypeContent) {
            if (actions[1].equals(mTvClickAction.getText().toString())) {
                getAppTask(getActivity());
                showAppSelectDialog();
            } else if (actions[2].equals(mTvClickAction.getText().toString())) {
                showMusicActionDialog();
            } else if (actions[3].equals(mTvClickAction.getText().toString())) {
                showInputUrlDialog(mTvClickTypeContent.getText().toString());
            }
        } else if (v.getId() == R.id.tv_none) {
            mTvClickAction.setText(actions[0]);
            llClickContent.setVisibility(View.GONE);
            mTvClickTypeContent.setText("");
            clickActionDialog.doDismiss();
            if (editClickCallback != null) {
                editClickCallback.onActionType(WidgetClickType.CLICK_NONE);
            }
        } else if (v.getId() == R.id.tv_application) {
            if(actions[1].equals(mTvClickAction.getText().toString())){
                clickActionDialog.doDismiss();
                return;
            }
            mTvClickAction.setText(actions[1]);
            mTvClickTypeTitle.setText(actionTypes[0]);
            clickActionDialog.doDismiss();
            mTvClickTypeContent.setText("");
            llClickContent.setVisibility(View.VISIBLE);
            if (editClickCallback != null) {
                editClickCallback.onActionType(WidgetClickType.CLICK_APPLICATION);
            }
        } else if (v.getId() == R.id.tv_music_controller) {
            if(actions[2].equals(mTvClickAction.getText().toString())){
                clickActionDialog.doDismiss();
                return;
            }
            mTvClickAction.setText(actions[2]);
            mTvClickTypeTitle.setText(actionTypes[1]);
            clickActionDialog.doDismiss();
            mTvClickTypeContent.setText("");
            llClickContent.setVisibility(View.VISIBLE);
            if (editClickCallback != null) {
                editClickCallback.onActionType(WidgetClickType.CLICK_MUSIC);
            }
        } else if (v.getId() == R.id.tv_open_url) {
            if(actions[3].equals(mTvClickAction.getText().toString())){
                clickActionDialog.doDismiss();
                return;
            }
            mTvClickAction.setText(actions[3]);
            mTvClickTypeTitle.setText(actionTypes[2]);
            clickActionDialog.doDismiss();
            mTvClickTypeContent.setText("");
            llClickContent.setVisibility(View.VISIBLE);
            if (editClickCallback != null) {
                editClickCallback.onActionType(WidgetClickType.CLICK_URL);
            }
        } else if (v.getId() == R.id.tv_playOrPause) {
            mTvClickTypeContent.setText(musicActions[0]);
            musicControlDialog.doDismiss();
            if (editClickCallback != null) {
                editClickCallback.onActionContent(WidgetMusicActionType.PLAY_OR_PAUSE,"");
            }
        } else if (v.getId() == R.id.tv_next) {
            mTvClickTypeContent.setText(musicActions[1]);
            musicControlDialog.doDismiss();
            if (editClickCallback != null) {
                editClickCallback.onActionContent(WidgetMusicActionType.NEXT,"");
            }
        } else if (v.getId() == R.id.tv_previous) {
            mTvClickTypeContent.setText(musicActions[2]);
            musicControlDialog.doDismiss();
            if (editClickCallback != null) {
                editClickCallback.onActionContent(WidgetMusicActionType.PREVIOUS,"");
            }
        } else if (v.getId() == R.id.tv_voice_add) {
            mTvClickTypeContent.setText(musicActions[3]);
            musicControlDialog.doDismiss();
            if (editClickCallback != null) {
                editClickCallback.onActionContent(WidgetMusicActionType.VOICE_ADD,"");
            }
        } else if (v.getId() == R.id.tv_voice_multi) {
            mTvClickTypeContent.setText(musicActions[4]);
            musicControlDialog.doDismiss();
            if (editClickCallback != null) {
                editClickCallback.onActionContent(WidgetMusicActionType.VOICE_MULTI,"");
            }
        } else if (v.getId() == R.id.tv_open_app) {
            mTvClickTypeContent.setText(musicActions[5]);
            musicControlDialog.doDismiss();
            if (editClickCallback != null) {
                editClickCallback.onActionContent(WidgetMusicActionType.OPEN_APP,"");
            }
        } else if (v.getId() == R.id.iv_close) {
            appIconDialog.doDismiss();
        }
    }

    public void initActionUI(String actionType, String actionContent,String appName) {
        if (TextUtils.isEmpty(actionType)) {
            mTvClickAction.setText(actions[0]);
            return;
        }
        switch (actionType) {
            case WidgetClickType.CLICK_NONE: {
                mTvClickAction.setText(actions[0]);
                llClickContent.setVisibility(View.GONE);
                break;
            }
            case WidgetClickType.CLICK_APPLICATION: {
                mTvClickAction.setText(actions[1]);
                mTvClickTypeTitle.setText(actionTypes[0]);
                mTvClickTypeContent.setText(appName);
                break;
            }
            case WidgetClickType.CLICK_MUSIC: {
                mTvClickAction.setText(actions[2]);
                mTvClickTypeTitle.setText(actionTypes[1]);
                break;
            }
            case WidgetClickType.CLICK_URL: {
                mTvClickAction.setText(actions[3]);
                mTvClickTypeTitle.setText(actionTypes[2]);
                mTvClickTypeContent.setText(actionContent);
                break;
            }
        }
        if (!TextUtils.isEmpty(actionContent)) {
            llClickContent.setVisibility(View.VISIBLE);
            if (WidgetClickType.CLICK_MUSIC.equals(actionType)) {
                switch (actionContent) {
                    case WidgetMusicActionType.PLAY_OR_PAUSE: {
                        mTvClickTypeContent.setText(musicActions[0]);
                        break;
                    }
                    case WidgetMusicActionType.NEXT: {
                        mTvClickTypeContent.setText(musicActions[1]);
                        break;
                    }
                    case WidgetMusicActionType.PREVIOUS: {
                        mTvClickTypeContent.setText(musicActions[2]);
                        break;
                    }
                    case WidgetMusicActionType.VOICE_ADD: {
                        mTvClickTypeContent.setText(musicActions[3]);
                        break;
                    }
                    case WidgetMusicActionType.VOICE_MULTI: {
                        mTvClickTypeContent.setText(musicActions[4]);
                        break;
                    }
                    case WidgetMusicActionType.OPEN_APP: {
                        mTvClickTypeContent.setText(musicActions[5]);
                        break;
                    }
                }
            } else if (WidgetClickType.CLICK_APPLICATION.equals(actionType)) {
//                mTvClickTypeContent.setText(AppIconUtils.getAppName(getActivity(),actionContent));
            }
        } else {
            llClickContent.setVisibility(View.GONE);
        }

    }

    public void setEditClickCallback(WidgetEditClickCallback callback) {
        this.editClickCallback = callback;
    }

    private void showClickActionDialog() {
        Log.i("test_show_dialog:", "showdialog");
        clickActionDialog = CustomDialog.build(((AppCompatActivity) getActivity()), R.layout.widget_click_action_selected_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                TextView tvNone = v.findViewById(R.id.tv_none);
                TextView tvApp = v.findViewById(R.id.tv_application);
                TextView tvMusicCtl = v.findViewById(R.id.tv_music_controller);
                TextView tvUrl = v.findViewById(R.id.tv_open_url);
                tvNone.setText(actions[0]);
                tvApp.setText(actions[1]);
                tvMusicCtl.setText(actions[2]);
                tvUrl.setText(actions[3]);
                tvNone.setOnClickListener(WidgetClickSettingFragment.this);
                tvApp.setOnClickListener(WidgetClickSettingFragment.this);
                tvMusicCtl.setOnClickListener(WidgetClickSettingFragment.this);
                tvUrl.setOnClickListener(WidgetClickSettingFragment.this);
            }
        });
        clickActionDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(false).show();
    }

    private void showMusicActionDialog() {
        Log.i("test_show_dialog:", "showdialog");
        musicControlDialog = CustomDialog.build(((AppCompatActivity) getActivity()), R.layout.widget_click_action_music_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                TextView tvPlay = v.findViewById(R.id.tv_playOrPause);
                TextView tvNext = v.findViewById(R.id.tv_next);
                TextView tvPrevious = v.findViewById(R.id.tv_previous);
                TextView tvVoiceAdd = v.findViewById(R.id.tv_voice_add);
                TextView tvVoiceMuiti = v.findViewById(R.id.tv_voice_multi);
                TextView tvOpenApp = v.findViewById(R.id.tv_open_app);
                tvPlay.setText(musicActions[0]);
                tvNext.setText(musicActions[1]);
                tvPrevious.setText(musicActions[2]);
                tvVoiceAdd.setText(musicActions[3]);
                tvVoiceMuiti.setText(musicActions[4]);
                tvOpenApp.setText(musicActions[5]);
                tvPlay.setOnClickListener(WidgetClickSettingFragment.this);
                tvNext.setOnClickListener(WidgetClickSettingFragment.this);
                tvPrevious.setOnClickListener(WidgetClickSettingFragment.this);
                tvVoiceAdd.setOnClickListener(WidgetClickSettingFragment.this);
                tvVoiceMuiti.setOnClickListener(WidgetClickSettingFragment.this);
                tvOpenApp.setOnClickListener(WidgetClickSettingFragment.this);
            }
        });
        musicControlDialog.setAlign(CustomDialog.ALIGN.DEFAULT).setCancelable(false).show();
    }

    private void showInputUrlDialog(final String initText) {
        InputDialog.build(((AppCompatActivity) getActivity()))
                .setButtonTextInfo(new TextInfo().setFontColor(getResources().getColor(R.color.c_A0A0A0)))
                .setTitle("提示").setMessage("请输入链接")
                .setInputText(initText)
                .setOkButton("确定", new OnInputDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                        if (!Patterns.WEB_URL.matcher(inputStr).matches()) {
//                            ToastShowUtils.showCommonToast(getActivity(),"链接格式错误", Toasty.LENGTH_SHORT);
                            return true;
                        }
                        mTvClickTypeContent.setText(inputStr);
                        if (editClickCallback != null) {
                            editClickCallback.onActionContent(inputStr,"");
                        }
                        return false;
                    }
                })
                .setCancelButton("取消")
                .setHintText("请输入链接")
                .setInputInfo(new InputInfo()
                        .setInputType(InputType.TYPE_TEXT_VARIATION_URI)
                        .setTextInfo(new TextInfo()
                                .setFontColor(getResources().getColor(R.color.c_A0A0A0))
                        )
                )
                .setCancelable(false)
                .show();
    }

    private void getAppTask(final Context mContext) {
        mCompositeDisposable.add(Observable.create(new ObservableOnSubscribe<List<AppInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AppInfo>> e) throws Exception {
                List<AppInfo> list = AppIconUtils.getInstallApps(mContext);
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AppInfo>>() {
                    @Override
                    public void accept(List<AppInfo> appInfos) throws Exception {
                        if (adapter != null) {
                            appInfoList.clear();
                            appInfoList.addAll(appInfos);
                            adapter.notifyDataSetChanged();
                        }
                        if(mRecyclerView!= null){
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }));

    }

    private void showAppSelectDialog() {
        appIconDialog = FullScreenDialog.show(((AppCompatActivity) getActivity()), R.layout.widget_click_app_list_dialog, new FullScreenDialog.OnBindView() {
            @Override
            public void onBind(final FullScreenDialog dialog, View rootView) {
                mIvClose = rootView.findViewById(R.id.iv_close);
                mIvClose.setOnClickListener(WidgetClickSettingFragment.this);
                mRecyclerView = rootView.findViewById(R.id.recyclerView);
                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));

                adapter = new CommonAdapter<AppInfo>(getContext(), R.layout.diy_shortcut_icon_item, appInfoList) {
                    @Override
                    protected void convert(ViewHolder holder, AppInfo appInfo, final int position) {
                        final ImageView ivIcon = holder.getView(R.id.iv_icon);
                        TextView tvName = holder.getView(R.id.tv_name);
                        TextView line = holder.getView(R.id.tv_line);
                        tvName.setText(appInfo.getName());
                        if (TextUtils.isEmpty(appInfo.getDrawablePath())) {
                            ivIcon.setImageDrawable(appInfo.icon);
                        } else {
                            Glide.with(getActivity())
                                    .load(appInfo.getDrawablePath())
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            ivIcon.setImageDrawable(resource);
//                                            appInfoList.get(position).setIcon(resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });

                        }
                        if ((appInfoList.size() / 5) * 5 <= position) {
                            line.setVisibility(View.GONE);
                        } else {
                            line.setVisibility(View.VISIBLE);
                        }
                    }
                };

                adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                        AppInfo appInfo = appInfoList.get(position);
                        mTvClickTypeContent.setText(appInfo.getName());
                        if (editClickCallback != null) {
                            editClickCallback.onActionContent(appInfo.getPackageName(),appInfo.getName());
                        }
                        appIconDialog.doDismiss();
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                        return false;
                    }
                });

                mRecyclerView.setAdapter(adapter);
            }
        }).setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(BaseDialog dialog) {

            }
        });
    }
}
