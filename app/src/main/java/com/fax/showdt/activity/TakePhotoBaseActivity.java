package com.fax.showdt.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.fax.lib_photo.photopick.Matisse;
import com.fax.lib_photo.photopick.MimeType;
import com.fax.lib_photo.photopick.engine.impl.GlideEngine;
import com.fax.lib_photo.photopick.filter.Filter;
import com.fax.lib_photo.photopick.listener.OnCheckedListener;
import com.fax.lib_photo.photopick.listener.OnSelectedListener;
import com.fax.showdt.BuildConfig;
import com.fax.showdt.ConstantString;
import com.fax.showdt.R;
import com.fax.showdt.callback.OnCropImgCallback;
import com.fax.showdt.utils.Constant;
import com.fax.showdt.utils.Environment;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GifSizeFilter;
import com.fax.showdt.utils.TimeUtils;
import com.fax.showdt.utils.ToastShowUtils;
import com.fax.showdt.utils.ViewUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import es.dmoral.toasty.Toasty;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.yalantis.ucrop.UCrop.EXTRA_INPUT_URI;
import static com.yalantis.ucrop.UCrop.Options.EXTRA_IS_JUMP_OVER;

public abstract class TakePhotoBaseActivity extends BaseActivity implements OnCropImgCallback {
    private File mTmpUCropDir;
    private UCrop.Options mUCropOption;
    private String mCompressPath;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final String COMPRESS_DIR = ".compress";
    private static final String CROP_IMG_DIR = ".crop";
    private static final float ratioX = 1.0f;
    private static final float ratioY = 1.0f;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUCrop();
    }

    /**
     * 选取图片并裁剪
     * @param x 图片在x轴的比值
     * @param y 图片在y轴的比值
     */
    public void startCropOneImg(float x,float y){
        //调用知乎图片选取框架 在onActivityResult会调用uCrop框架来裁剪
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(false)
                .maxSelectable(1)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        ViewUtils.dpToPx(120,this))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {

                    }
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(new OnCheckedListener() {
                    @Override
                    public void onCheck(boolean isChecked) {

                    }
                })
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void startCropWithRatio(Uri sourceUri,float x,float y){
        Log.i("test_crop:","destination uri:"+mTmpUCropDir.getPath());
        UCrop.of(sourceUri, getDestinationUri())
                .withAspectRatio(x, y)
                .withOptions(getUCropOptions())
                .start(this);
    }


    private void initUCrop(){
        if (BuildConfig.DEBUG){
            mTmpUCropDir = new File(Environment.getHomeDir().getAbsolutePath(), Constant.WIDGET_CROP_DIR);
            Log.i("test_crop:","mTmpUCropDir :"+mTmpUCropDir);
            mCompressPath = Environment.getHomeDir() + File.separator + COMPRESS_DIR;
        }else {
            mTmpUCropDir = new File(getFilesDir(), CROP_IMG_DIR);
            mCompressPath = getFilesDir() + File.separator + COMPRESS_DIR;
        }
        FileExUtils.checkDir(mTmpUCropDir);
        FileExUtils.checkDir(mCompressPath);

        if (mUCropOption == null){
            mUCropOption = new UCrop.Options();
            mUCropOption.setHideBottomControls(true);
            mUCropOption.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.ALL);
            mUCropOption.setStatusBarColor(getResources().getColor(R.color.c_171925));
            mUCropOption.setToolbarColor(getResources().getColor(R.color.c_171925));
//            mUCropOption.setCircleDimmedLayer(true);
            mUCropOption.setShowCropFrame(false);
            mUCropOption.setShowCropGrid(false);
            mUCropOption.setJumpOver(true);
        }
    }
    protected Uri getDestinationUri(){
        File destination = new File(mTmpUCropDir, "tmp_"+ TimeUtils.currentTimeMillis() +".jpeg");
        return Uri.fromFile(destination);
    }


    protected String getCompressPath(){
        return mCompressPath;
    }

    protected File getUcropFileName(){
        return mTmpUCropDir;
    }

    protected UCrop.Options getUCropOptions(){
        return mUCropOption;
    }

    protected void addCompressListener(@NonNull String picPath, final CompressCallback callback) {
        if (!TextUtils.isEmpty(picPath)) {
            Luban.with(this)
                    .load(picPath)
                    .ignoreBy(ConstantString.CROP_IMG_MIN_SIZE)
                    .setTargetDir(getCompressPath())
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
//                            ToastShowUtils.showCommonToast(TakePhotoBaseActivity.this,"图片压缩中...",Toasty.LENGTH_SHORT);
                        }

                        @Override
                        public void onSuccess(File file) {
                            if (callback != null) {
                                callback.compressResult(file);
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
//                            ToastShowUtils.showCommonToast(TakePhotoBaseActivity.this,"图片压缩失败"+e.getMessage(),Toasty.LENGTH_SHORT);
                            if (callback != null) {
                                callback.compressResult(null);
                            }
                        }
                    }).launch();
        }
    }

    public interface CompressCallback {
        void compressResult(File file);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSourceUriList = Matisse.obtainResult(data);
            startCropWithRatio(mSourceUriList.get(0),ratioX,ratioY);
        }
        else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                addCompressListener(resultUri.getPath(), new CompressCallback() {
                    @Override
                    public void compressResult(File file) {
                        if(file != null) {
                            cropSuc(file.getPath());
                            Log.e("test_compress_path:",file.getAbsolutePath());
                        }else {
                            cropSuc(resultUri.getPath());
                        }
                    }
                });
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            cropFail(cropError);
            ToastShowUtils.showCommonToast(this,getResources().getString(R.string.crop_picture_error), Toasty.LENGTH_LONG);
        }
    }

    @Override
    public void cropFail(Throwable throwable) {

    }

    @Override
    public void cropSuc(String path) {

    }

}
