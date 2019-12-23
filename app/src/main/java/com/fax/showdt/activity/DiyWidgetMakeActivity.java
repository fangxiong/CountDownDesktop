package com.fax.showdt.activity;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fax.showdt.AppContext;
import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.WidgetEditShapeCallback;
import com.fax.showdt.callback.WidgetEditStickerCallback;
import com.fax.showdt.callback.WidgetEditTextCallback;
import com.fax.showdt.dialog.ios.interfaces.OnDialogButtonClickListener;
import com.fax.showdt.dialog.ios.interfaces.OnShowListener;
import com.fax.showdt.dialog.ios.util.BaseDialog;
import com.fax.showdt.dialog.ios.util.DialogSettings;
import com.fax.showdt.dialog.ios.v3.FullScreenDialog;
import com.fax.showdt.dialog.ios.v3.MessageDialog;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.fragment.WidgetProgressEditFragment;
import com.fax.showdt.fragment.WidgetShapeEditFragment;
import com.fax.showdt.fragment.WidgetStickerEditFragment;
import com.fax.showdt.fragment.WidgetTextEditFragment;
import com.fax.showdt.manager.location.LocationManager;
import com.fax.showdt.manager.musicPlug.KLWPSongUpdateManager;
import com.fax.showdt.manager.weather.WeatherManager;
import com.fax.showdt.manager.widget.CustomWidgetConfigConvertHelper;
import com.fax.showdt.manager.widget.CustomWidgetConfigDao;
import com.fax.showdt.manager.widget.WidgetConfig;
import com.fax.showdt.service.NLService;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.utils.BitmapUtils;
import com.fax.showdt.utils.CommonUtils;
import com.fax.showdt.utils.Constant;
import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.utils.ViewUtils;
import com.fax.showdt.view.sticker.BitmapStickerIcon;
import com.fax.showdt.view.sticker.DeleteIconEvent;
import com.fax.showdt.view.sticker.DrawableSticker;
import com.fax.showdt.view.sticker.Sticker;
import com.fax.showdt.view.sticker.StickerView;
import com.fax.showdt.view.sticker.TextSticker;
import com.fax.showdt.view.sticker.ZoomIconEvent;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;
import com.gyf.barlibrary.ImmersionBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-9
 * Description:
 */
public class DiyWidgetMakeActivity extends TakePhotoBaseActivity implements View.OnClickListener {

    private StickerView mStickerView;
    private ImageView mStickerViewBg;
    private RelativeLayout mRLEditBody;
    private boolean mEditPaneShowing = false;
    private RecyclerView mRvEditBg;
    private Bitmap mEditBitmap, mSystemBgBitmap;
    private List<String> mEditBgList = new ArrayList<>();
    private CommonAdapter<String> mEditBgAdapter;
    private WidgetTextEditFragment mTextEditFragment;
    private WidgetStickerEditFragment mStickerEditFragment;
    private WidgetShapeEditFragment mShapeEditFragment;
    private WidgetProgressEditFragment mProgressEditFragment;
    private Sticker mHandlingSticker;
    private LongSparseArray<Sticker> mStickerList;
    private FullScreenDialog mEditBgDialog;
    private boolean mIsGetSystemBgSuc = true;
    private CustomWidgetConfig customWidgetConfig;
    private TipDialog mWaitDialog;
    private UpdateLrcReceiver mUpdateLrcReceiver;


    enum EditType {
        EDIT_TEXT, EDIT_STICKER, EDIT_SHAPE, EDIT_PROGRESS
    }

    public static void startSelf(Context context,CustomWidgetConfig config){
        Intent intent = new Intent(context,DiyWidgetMakeActivity.class);
        intent.putExtra(ConstantString.widget_make_data,config.toJSONString());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diy_widget_make_activity);
        WeatherManager.getInstance().starGetWeather();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当服务开启后通知 通知监听器刷新歌曲信息
        Intent intent = new Intent();
        intent.putExtra("switch_flag", false);
        intent.setAction(NLService.NOTIFY_REFRESH_AUDIO_INFO);
        sendBroadcast(intent);
        unregisterReceiver(mUpdateLrcReceiver);
        LocationManager.getInstance().stopLocation();
    }

    @Override
    protected void initView() {
        super.initView();
        mStickerView = findViewById(R.id.sticker_view);
        mStickerViewBg = findViewById(R.id.iv_select_bg);
        mRLEditBody = findViewById(R.id.rl_edit_body);
        initStatusBar();
        intervalRefreshStickerView();
        initAllEditFragments();
        initStickerView();
        initStickerViewBg();
        initData();
    }

    private void initData() {
        mStickerList = new LongSparseArray<>();
        Intent intent = getIntent();
        String json = intent.getStringExtra(ConstantString.widget_make_data);
        if(!TextUtils.isEmpty(json)){
            customWidgetConfig = GsonUtils.parseJsonWithGson(json,CustomWidgetConfig.class);
            initStickers();
        }
    }

    private void initStickers(){
        if(customWidgetConfig != null){
            mEditBitmap = BitmapUtils.decodeFile(customWidgetConfig.getBgPath());
            mStickerViewBg.setImageBitmap(mEditBitmap);
            CustomWidgetConfigConvertHelper  mHelper = new CustomWidgetConfigConvertHelper();
            mHelper.initAllStickerPlugs( mStickerView, customWidgetConfig, mStickerList);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUpdateLrcReceiver = new UpdateLrcReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KLWPSongUpdateManager.ACTION_UPDATE_MEDIA_INFO);
        registerReceiver(mUpdateLrcReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.tv_text) {
            TextSticker textSticker = new TextSticker(System.currentTimeMillis());
            textSticker.setTextColor("#FFFFFF");
            textSticker.setFontPath("fonts/minijianqi.ttf");
            mTextEditFragment.setWidgetEditTextSticker(textSticker);
            mStickerView.addSticker(textSticker, Sticker.Position.TOP);
            switchToOneFragment(EditType.EDIT_TEXT);
        } else if (resId == R.id.tv_sticker) {
            switchToOneFragment(EditType.EDIT_STICKER);
        } else if (resId == R.id.tv_shape) {
            switchToOneFragment(EditType.EDIT_SHAPE);
        } else if (resId == R.id.tv_progress) {
            switchToOneFragment(EditType.EDIT_PROGRESS);
        } else if (resId == R.id.iv_back) {
            MessageDialog.show(this, "提示", "确定要退出编辑吗？")
                    .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                        @Override
                        public boolean onClick(BaseDialog baseDialog, View v) {
                            finish();
                            return false;
                        }
                    });
        } else if (resId == R.id.iv_change_bg) {
            showEditBgPanel();
        } else if(resId == R.id.iv_save){
            saveConfig();
        }
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        ImmersionBar.hideStatusBar(getWindow());
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.fullScreen(true);
        mImmersionBar.init();
    }

    private void initStickerView() {
        FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams) mStickerView.getLayoutParams();
        params.width = WidgetConfig.getWidgetWidth();
        params.height = WidgetConfig.getWidget4X4Height();
        mStickerView.setLayoutParams(params);
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_ic_close_icon),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.sticker_ic_scale_icon),
                BitmapStickerIcon.RIGHT_BOTTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());
        mStickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon));
        mStickerView.setConstrained(true);
        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                mHandlingSticker = sticker;
                mStickerList.put(sticker.getId(), sticker);
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                Log.i("test_click:", "click");
//                if(sticker instanceof TextSticker){
//                    switchToOneFragment(EditType.EDIT_TEXT);
//                }else if(sticker instanceof DrawableSticker){
//                    switchToOneFragment(EditType.EDIT_SHAPE);
//                }
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                mStickerList.delete(sticker.getId());
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                mHandlingSticker = sticker;
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
                Log.i("test_click:", "touch");
                mHandlingSticker = mStickerView.getCurrentSticker();
                if (sticker instanceof TextSticker) {
                    mTextEditFragment.setWidgetEditTextSticker((TextSticker) sticker);
                    switchToOneFragment(EditType.EDIT_TEXT);
                } else if (sticker instanceof DrawableSticker) {
                    if (((DrawableSticker) sticker).getmPicType() == DrawableSticker.SVG) {
                        switchToOneFragment(EditType.EDIT_SHAPE);
                        mShapeEditFragment.setWidgetEditShapeSticker((DrawableSticker) sticker);
                    } else if (((DrawableSticker) sticker).getmPicType() == DrawableSticker.ASSET) {
                        switchToOneFragment(EditType.EDIT_STICKER);
                    }
                }
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                if (sticker instanceof TextSticker) {
                    mTextEditFragment.showInputDialog(100);
                }
            }

            @Override
            public void onStickerNoTouched() {
                if (mEditPaneShowing) {
                    setEditBodySlideOutAnimation();
                    mEditPaneShowing = false;
                }
                mHandlingSticker = null;
            }

            @Override
            public void onClickedBindAppIcon(@NonNull Sticker sticker) {

            }

            @Override
            public void onCopySticker(@NonNull Sticker sticker) {

            }

            @Override
            public void onUnlock() {

            }
        });
    }


    private Bitmap getSystemBitmap() {
        Drawable drawable = WallpaperManager.getInstance(this).getDrawable();
        return BitmapUtils.drawableToBitmap(drawable);
    }

    private Bitmap clipWidgetSizeBitmap(Bitmap bitmap) {
        Bitmap resultBitmap = null;

        try {
            float w = bitmap.getWidth();
            float h = bitmap.getHeight();
            Log.i("test_size:", "height:" + bitmap.getHeight() + " width:" + bitmap.getWidth());
            int cropWidth = (int) (w * WidgetConfig.WIDGET_MAX_WIDTH_RATIO);// 裁切后所取的正方形区域边长
            int x = (int) ((w - cropWidth) * 1.0f / 2);
            int marginTop = ViewUtils.dp2px(60);
            if ((w < h || w == h) && w > h - marginTop) {
                marginTop = (int) (h - w);
            }else if(w > h){
                 cropWidth = (int)(cropWidth/2f);
            }
            resultBitmap = Bitmap.createBitmap(bitmap, x, marginTop, cropWidth, cropWidth, null, false);
        }catch (Exception e){
            resultBitmap = null;
        }
        return resultBitmap;
    }

    private void initStickerViewBg() {
        Bitmap bitmap = clipWidgetSizeBitmap(getSystemBitmap());
        if (bitmap != null) {
            mIsGetSystemBgSuc = true;
            mEditBitmap = bitmap;
            mSystemBgBitmap = bitmap;
            mStickerViewBg.setImageBitmap(mEditBitmap);
        } else {
            mIsGetSystemBgSuc = false;
            mEditBitmap = CommonUtils.getAssetPic(this, "file:///android_asset/widgetBg/template00.png");
        }
    }

    private void intervalRefreshStickerView() {
        addDisponsable(Observable.interval(30, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mStickerView.invalidate();
                    }
                }));
    }

    private void initAllEditFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mTextEditFragment = new WidgetTextEditFragment();
        mStickerEditFragment = new WidgetStickerEditFragment();
        mShapeEditFragment = new WidgetShapeEditFragment();
        mProgressEditFragment = new WidgetProgressEditFragment();
        transaction.add(R.id.rl_edit_body, mTextEditFragment);
        transaction.add(R.id.rl_edit_body, mStickerEditFragment);
        transaction.add(R.id.rl_edit_body, mShapeEditFragment);
        transaction.add(R.id.rl_edit_body, mProgressEditFragment);
        transaction.commitAllowingStateLoss();
        mTextEditFragment.setWidgetEditTextCallback(new WidgetEditTextCallback() {
            @Override
            public void onAddSticker() {
                TextSticker textSticker = new TextSticker(System.currentTimeMillis());
                textSticker.setTextColor("#FFFFFF");
                textSticker.setFontPath("fonts/minijianqi.ttf");
                mTextEditFragment.setWidgetEditTextSticker(textSticker);
                mStickerView.addSticker(textSticker, Sticker.Position.TOP);
            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }
        });
        mStickerEditFragment.setWidgetEditStickerCallback(new WidgetEditStickerCallback() {
            @Override
            public void onAddSticker(String path) {
                Drawable drawable = new BitmapDrawable(getResources(), CommonUtils.getAssetPic(DiyWidgetMakeActivity.this, path));
                DrawableSticker drawableSticker = new DrawableSticker(drawable, System.currentTimeMillis());
                drawableSticker.setmPicType(DrawableSticker.ASSET);
                drawableSticker.setDrawablePath(path);
                mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }

            @Override
            public void onPickPhoto() {
                startCropOneImg(1,1);
            }
        });
        mShapeEditFragment.setWidgetEditShapeCallback(new WidgetEditShapeCallback() {
            @Override
            public void onAddShapeSticker(WidgetShapeBean widgetShapeBean) {
                try {
                    SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN))
                            .readFromAsset(getAssets(), widgetShapeBean.getSvgPath()).build();
                    PictureDrawable drawable = svg.getDrawable();
                    DrawableSticker drawableSticker = new DrawableSticker(drawable, System.currentTimeMillis());
                    drawableSticker.setmPicType(DrawableSticker.SVG);
                    drawableSticker.setDrawablePath(widgetShapeBean.getSvgPath());
                    mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
                    Log.i("test_add_sticker:", "添加DrawSticker成功");
                    mShapeEditFragment.setWidgetEditShapeSticker(drawableSticker);
                } catch (IOException e) {

                }

            }

            @Override
            public void closePanel() {
                setEditBodySlideOutAnimation();
                mStickerView.clearCurrentSticker();
            }
        });

    }

    private void setEditBodySlideInAnimation() {
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        mRLEditBody.startAnimation(ctrlAnimation);
    }

    private void setEditBodySlideOutAnimation() {
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        ctrlAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRLEditBody.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRLEditBody.startAnimation(ctrlAnimation);
    }

    private void switchToOneFragment(EditType editType) {
        if (!mEditPaneShowing) {
            setEditBodySlideInAnimation();
        }
        mEditPaneShowing = true;
        mRLEditBody.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (editType) {
            case EDIT_TEXT: {
                transaction.hide(mStickerEditFragment);
                transaction.hide(mShapeEditFragment);
                transaction.hide(mProgressEditFragment);
                transaction.show(mTextEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case EDIT_STICKER: {
                transaction.hide(mTextEditFragment);
                transaction.hide(mShapeEditFragment);
                transaction.hide(mProgressEditFragment);
                transaction.show(mStickerEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case EDIT_SHAPE: {
                transaction.hide(mTextEditFragment);
                transaction.hide(mStickerEditFragment);
                transaction.hide(mProgressEditFragment);
                transaction.show(mShapeEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
            case EDIT_PROGRESS: {
                transaction.hide(mTextEditFragment);
                transaction.hide(mStickerEditFragment);
                transaction.hide(mShapeEditFragment);
                transaction.show(mProgressEditFragment);
                transaction.commitAllowingStateLoss();
                break;
            }
        }

    }

    private void showEditBgPanel() {
        FullScreenDialog.show(this, R.layout.widget_edit_bg_selector_panel, new FullScreenDialog.OnBindView() {
            @Override
            public void onBind(final FullScreenDialog dialog, View rootView) {
                Log.i("test_dialog:", "initEditBgPanel");
                mRvEditBg = rootView.findViewById(R.id.rv);
                TextView mTvClose = rootView.findViewById(R.id.tv_close);
                mEditBgDialog = dialog;
                mTvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.doDismiss();
                    }
                });
            }
        }).setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(BaseDialog dialog) {
                Log.i("test_dialog:", "initEditBgPanel");
                initEditBgPanel();
            }
        });
    }

    private void showEditDialog() {
        MessageDialog.show(this, "提示", "确定要退出编辑吗？")
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        finish();
                        return false;
                    }
                });
    }

    private void initEditBgPanel() {
        mEditBgList.clear();
        if (mIsGetSystemBgSuc) {
            mEditBgList.add(0, "");
        }
        Resources res = AppContext.get().getResources();
        String[] strs = res.getStringArray(R.array.widget_edit_bg);
        List<String> tempList = Arrays.asList(strs);
        mEditBgList.addAll(tempList);
        Log.i("test_dialog:", "mEditBgList.size:" + mEditBgList.size());
        mEditBgAdapter = new CommonAdapter<String>(this, R.layout.widget_make_edit_bg_item, mEditBgList) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                ImageView mIv = holder.getView(R.id.iv_item);
                if (position == 0 && mIsGetSystemBgSuc) {
                    mIv.setImageBitmap(mSystemBgBitmap);
                } else {
                    GlideUtils.loadImage(DiyWidgetMakeActivity.this, "file:///android_asset/" + s, mIv);
                }
            }
        };
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mRvEditBg.setLayoutManager(manager);
        mEditBgAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (position == 0 && mIsGetSystemBgSuc) {
                    mStickerViewBg.setImageBitmap(mSystemBgBitmap);
                } else {
                    mEditBitmap = CommonUtils.getAssetPic(DiyWidgetMakeActivity.this, mEditBgList.get(position));
                    mStickerViewBg.setImageBitmap(mEditBitmap);
                }
                if (mEditBgDialog != null) {
                    mEditBgDialog.doDismiss();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRvEditBg.setAdapter(mEditBgAdapter);
    }

    private void  sendConfigChangedBroadcast() {
        Intent intent =new Intent();
        intent.setAction(WidgetUpdateService.WIDGET_CONFIG_CHANGED);
        sendBroadcast(intent);
    }

    private void saveConfig() {
        final Bitmap bgBitmap = BitmapUtils.viewToBitmap(mStickerViewBg);
        mStickerView.setShowGrid(false);
        mStickerView.requestLayout();
        mStickerView.clearCurrentSticker();
        Bitmap stickerBitmap = BitmapUtils.getScreenShotsBitmap(mStickerView);
        final Bitmap coverBitmap = BitmapUtils.mergeBitmap(bgBitmap, stickerBitmap, 0);
        final String coverFileName = "widget_cover" + System.currentTimeMillis()+".png";
        final String bgFileName = "widget_bg" + System.currentTimeMillis()+".png";
        final String coverDir = Environment.getHomeDir()+File.separator+Constant.WIDGET_DATA_DIR+File.separator+"widget_screenshot";
        final String bgDir = Environment.getHomeDir()+File.separator+Constant.WIDGET_DATA_DIR+File.separator+"widget_bg";
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                BitmapUtils.saveFile(coverBitmap, coverFileName, coverDir);
                BitmapUtils.saveFile(bgBitmap,bgFileName,bgDir);
                customWidgetConfig = new CustomWidgetConfig();
                if(FileExUtils.exists(customWidgetConfig.getCoverUrl())){
                    FileExUtils.deleteSingleFile(customWidgetConfig.getCoverUrl());
                }
                if(FileExUtils.exists(customWidgetConfig.getBgPath())){
                    FileExUtils.deleteSingleFile(customWidgetConfig.getBgPath());
                }
                customWidgetConfig.setCoverUrl(coverDir+ File.separator+coverFileName);
                customWidgetConfig.setBgPath(bgDir+ File.separator+bgFileName);
                customWidgetConfig.setId(System.currentTimeMillis());
                customWidgetConfig.setBaseOnHeightPx(mStickerView.getHeight());
                customWidgetConfig.setBaseOnWidthPx(mStickerView.getWidth());
                CustomWidgetConfigConvertHelper mHelper = new CustomWidgetConfigConvertHelper();
                CustomWidgetConfig newConfig = mHelper.saveConfig(customWidgetConfig,mStickerList);
                Log.i("test_widget_config:","保存到数据库："+newConfig.toJSONString());
                CustomWidgetConfigDao.getInstance(DiyWidgetMakeActivity.this).insert(newConfig);
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        DialogSettings.tipTheme = DialogSettings.THEME.LIGHT;
                       mWaitDialog =  TipDialog.showWait(DiyWidgetMakeActivity.this,"加工数据中...");
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mWaitDialog.setTip(TipDialog.TYPE.SUCCESS);
                        mWaitDialog.setMessage("保存成功");
                        sendConfigChangedBroadcast();
                        Log.i("test_config:", GsonUtils.toJsonWithSerializeNulls(customWidgetConfig));
                        Log.i("test_config:", "保存成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.i("test_config:", e.getMessage());
                        mWaitDialog.setTip(TipDialog.TYPE.ERROR);
                        mWaitDialog.setMessage("保存失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEditPaneShowing) {
                mEditPaneShowing = false;
                setEditBodySlideOutAnimation();
            } else {
                showEditDialog();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void cropSuc(String path) {
        super.cropSuc(path);
        Drawable drawable = new BitmapDrawable(getResources(), path);
        DrawableSticker drawableSticker = new DrawableSticker(drawable, System.currentTimeMillis());
        drawableSticker.setmPicType(DrawableSticker.SDCARD);
        drawableSticker.setDrawablePath(path);
        mStickerView.addSticker(drawableSticker, Sticker.Position.CENTER);
    }

    @Override
    public void cropFail(Throwable throwable) {
        throwable.printStackTrace();
        Log.i("crop_fail:","msg:"+throwable.getMessage());
        super.cropFail(throwable);
    }

    /**
     * 接收第三方播放器播放时的歌词
     */
    class UpdateLrcReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (KLWPSongUpdateManager.ACTION_UPDATE_MEDIA_INFO.equals(action)) {
                CustomPlugUtil.lrc = intent.getStringExtra(KLWPSongUpdateManager.LRC_KEY);
                CustomPlugUtil.album = intent.getStringExtra(KLWPSongUpdateManager.ALBUM_KEY);
                CustomPlugUtil.singerName = intent.getStringExtra(KLWPSongUpdateManager.SINGER_KEY);
                CustomPlugUtil.songName = intent.getStringExtra(KLWPSongUpdateManager.SONGNAME_KEY);
                CustomPlugUtil.duration = intent.getLongExtra(KLWPSongUpdateManager.DURATION_KEY,0L);
                CustomPlugUtil.currentDuration = intent.getLongExtra(KLWPSongUpdateManager.CURRENT_DURATION_KEY,0L);
                Log.i("test_song:","lrc:"+CustomPlugUtil.lrc);
                Log.i("test_song:","album:"+CustomPlugUtil.album);
                Log.i("test_song:","singerName:"+CustomPlugUtil.singerName);
                Log.i("test_song:","songName:"+CustomPlugUtil.songName);
                Log.i("test_song:","duration:"+CustomPlugUtil.duration);
                Log.i("test_song:","currentDuration:"+CustomPlugUtil.currentDuration);
            }
        }

    }
}
