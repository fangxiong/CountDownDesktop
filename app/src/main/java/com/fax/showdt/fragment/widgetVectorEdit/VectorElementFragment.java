package com.fax.showdt.fragment.widgetVectorEdit;

import android.content.Context;
import android.graphics.Path;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.SvgIconBean;
import com.fax.showdt.callback.ShapeElementCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.view.androipathview.PathImageView;
import com.fax.showdt.view.androipathview.PathView;

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
import io.reactivex.schedulers.Schedulers;

public class VectorElementFragment extends Fragment {
    private CommonAdapter<SvgIconBean> mStickerAdapter;
    private List<SvgIconBean> mCurrentStickerBean = new ArrayList<>();
    private RecyclerView mStickerContentRv;
    private List<Path> mPaths = new ArrayList<>();
    private Disposable disposable;
    private String jsonPath;
    private ShapeElementCallback elementCallback;


    public VectorElementFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vector_element_fragment, container, false);
        mStickerContentRv = view.findViewById(R.id.rv_content);
        initTextPlugSelectUI();
        reqData(getContext());
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
        if(TextUtils.isEmpty(jsonPath) || mCurrentStickerBean.size() > 0){
            return;
        }
        String str = FileExUtils.getJsonFromAssest(context, jsonPath);
        final List<SvgIconBean> mDatas = GsonUtils.parseJsonArrayWithGson(str, SvgIconBean.class);
        Log.e("test_icon_path:",jsonPath);
        Log.e("test_icon_size0:",String.valueOf(mDatas.size()));
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                mPaths.clear();
                for (SvgIconBean stickerBean : mDatas) {
                        String pathStr = stickerBean.getIcon().getPaths().get(0);
                        Log.i("test_time_path:",stickerBean.getIcon().getPaths().size() > 1 ? stickerBean.getProperties().getName() : "");
                        Path path = com.caverock.androidsvg1.SVG.parsePath(pathStr);
                        mPaths.add(path);
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
                        Log.e("test_icon_adapter:","刷新adapter2");
                        Log.e("test_icon_size1:",String.valueOf(mPaths.size()));

                        if (mStickerAdapter != null) {
                            mCurrentStickerBean.clear();
                            mCurrentStickerBean.addAll(mDatas);
                            mStickerAdapter.notifyDataSetChanged();
                        }

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
//        Log.i("test_init:", "initTextPlugSelectUI" + "draw size:" + mDrawables.size());
        mStickerAdapter = new CommonAdapter<SvgIconBean>(getActivity(), R.layout.widget_sticker_content_item, mCurrentStickerBean) {
            @Override
            protected void convert(ViewHolder holder, final SvgIconBean stickerBean, int position) {
                try {
                    final PathImageView mIv = holder.getView(R.id.iv_element);
                    mIv.setPath(mPaths.get(position));
                }catch (IndexOutOfBoundsException e){

                }

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
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 7, RecyclerView.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
//                return mCurrentStickerBean.get(position).getStickerCategory().equals("表情")? 1 : 2;
                return 1;
            }
        });

        mStickerContentRv.setLayoutManager(manager);
        mStickerContentRv.setAdapter(mStickerAdapter);
        Log.e("test_icon_adapter:","初始化adapter");

    }

    public void setElementCallback(ShapeElementCallback callback) {
        elementCallback = callback;
    }
}
