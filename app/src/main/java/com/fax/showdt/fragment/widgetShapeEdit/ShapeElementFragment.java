package com.fax.showdt.fragment.widgetShapeEdit;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.ShapeElementCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class ShapeElementFragment extends Fragment {
    private CommonAdapter<WidgetShapeBean> mStickerAdapter;
    private List<WidgetShapeBean> mCurrentStickerBean = new ArrayList<>();
    private RecyclerView mStickerContentRv;
    private List<Drawable> mDrawables = new ArrayList<>();
    private Disposable disposable;
    private String jsonPath;
    private ShapeElementCallback elementCallback;


    public ShapeElementFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shape_element_fragment, container, false);
        mStickerContentRv = view.findViewById(R.id.rv_content);
        initTextPlugSelectUI();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        reqData(context);
    }

    public void setCurrentStickerBean(final Context context, String jsonPath) {
        this.jsonPath = jsonPath;
    }

    private void reqData(final Context context) {
        if(TextUtils.isEmpty(jsonPath)){
            return;
        }
        String str = FileExUtils.getJsonFromAssest(context, jsonPath);
        final List<WidgetShapeBean> mDatas = GsonUtils.parseJsonArrayWithGson(str, WidgetShapeBean.class);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                for (WidgetShapeBean stickerBean : mDatas) {
                    try {
                        SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.c_A0A0A0), PorterDuff.Mode.SRC_IN))
                                .readFromAsset(context.getAssets(), stickerBean.getSvgPath()).build();
                        mDrawables.add(svg.getDrawable());
                    } catch (IOException e) {

                    }
                }
                emitter.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mStickerAdapter != null) {
                            mCurrentStickerBean.clear();
                            mCurrentStickerBean.addAll(mDatas);
                            mStickerAdapter.notifyDataSetChanged();
                        }
                        Log.i("test_init:", "setCurrentStickerBean" + "size:" + mDrawables.size());

                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("test_init:", "setCurrentStickerBean" + "error:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initTextPlugSelectUI() {
        Log.i("test_init:", "initTextPlugSelectUI" + "draw size:" + mDrawables.size());
        mStickerAdapter = new CommonAdapter<WidgetShapeBean>(getActivity(), R.layout.widget_sticker_content_item, mCurrentStickerBean) {
            @Override
            protected void convert(ViewHolder holder, final WidgetShapeBean stickerBean, int position) {
                final ImageView mIv = holder.getView(R.id.iv_element);
                Log.i("test_time0:", System.currentTimeMillis() + "");
                mIv.setImageDrawable(mDrawables.get(position));
                Log.i("test_time1:", System.currentTimeMillis() + "");


//                    com.caverock.androidsvg.SVG mSVG = com.caverock.androidsvg.SVG.getFromAsset(mContext.getAssets(), stickerBean.getSvgPath());
//                    Picture picture = mSVG.renderToPicture();
//                    PictureDrawable pictureDrawable = new PictureDrawable(picture);
//                    pictureDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.c_A0A0A0), PorterDuff.Mode.SRC_IN));
//                    mIv.setImageDrawable(pictureDrawable);

            }
        };
        mStickerAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if(elementCallback != null) {
                    elementCallback.selectShapeElement(mCurrentStickerBean.get(position));
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 8, RecyclerView.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
//                return mCurrentStickerBean.get(position).getStickerCategory().equals("表情")? 1 : 2;
                return 1;
            }
        });

        mStickerContentRv.setLayoutManager(manager);
        mStickerContentRv.setAdapter(mStickerAdapter);

    }

    public void setElementCallback(ShapeElementCallback callback) {
        elementCallback = callback;
    }
}
