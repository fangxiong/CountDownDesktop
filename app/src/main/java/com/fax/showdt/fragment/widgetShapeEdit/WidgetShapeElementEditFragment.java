package com.fax.showdt.fragment.widgetShapeEdit;

import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseLongArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.caverock.androidsvg.SVGParseException;
import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.StickerBean;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.ShapeElementCallback;
import com.fax.showdt.callback.WidgetEditShapeElementSelectedCallback;
import com.fax.showdt.callback.WidgetEditStickerElementSelectedCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WidgetShapeElementEditFragment extends Fragment {

    private CommonAdapter<String> mListAdapter;
    private RecyclerView mStickerListRv;
    private String[] titles = new String[]{"形状", "表情", "音乐", "天气", "logo"};
    private String[] jsonPathArray = new String[]{"widget_shape.json", "widget_emoji.json", "widget_media.json", "widget_weather.json", "widget_logo.json"};
    private int mCurrentSelectedPos = 0;
    private WidgetEditShapeElementSelectedCallback mElementSelectedCallback;
    private LongSparseArray<Fragment> mFragments = new LongSparseArray<>();


    public WidgetShapeElementEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_sticker_element_edit_fragment, container, false);
        mStickerListRv = view.findViewById(R.id.rv_list);
        initTextPlugSelectUI();
        return view;
    }


    private void initTextPlugSelectUI() {
        mStickerListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListAdapter = new CommonAdapter<String>(getActivity(), R.layout.widget_text_element_list_item, Arrays.asList(titles)) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView mTv = holder.getView(R.id.tv_element);
                mTv.setText(s);
                if (position == mCurrentSelectedPos) {
                    mTv.setBackgroundResource(R.color.c_FCF43C);
                } else {
                    mTv.setBackgroundResource(R.color.transparent);
                }
            }
        };
        mListAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                TextView mTv = view.findViewById(R.id.tv_element);
                mTv.setBackgroundResource(R.color.c_FCF43C);
                mListAdapter.notifyItemChanged(mCurrentSelectedPos);
                dataSetChange(position);
                mCurrentSelectedPos = position;
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mStickerListRv.setAdapter(mListAdapter);
        dataSetChange(0);

    }

    private void dataSetChange(int pos) {
        String jsonPath = jsonPathArray[pos];
        if (!mFragments.containsKey(pos)) {
            ShapeElementFragment fragment = new ShapeElementFragment();
            fragment.setElementCallback(new ShapeElementCallback() {
                @Override
                public void selectShapeElement(WidgetShapeBean widgetShapeBean) {
                    if(mElementSelectedCallback != null){
                        mElementSelectedCallback.selectShapeElement(widgetShapeBean);
                    }
                }
            });
            fragment.setCurrentStickerBean(getActivity(),jsonPath);
            mFragments.put(pos, fragment);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.fl_body, fragment);
            transaction.commitAllowingStateLoss();
        }
        showFragment(pos);
    }

    private void showFragment(int pos) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            long key = mFragments.keyAt(i);
            Log.i("test_show:","pos: "+pos);
            Log.i("test_show:","key: "+key);
            Log.i("test_show:","mFragment size: "+mFragments.size());
            Fragment fragment = mFragments.get(mFragments.keyAt(i));
            if (pos == key) {
                Log.i("test_show:","show: "+key);
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }


    public void setWidgetShapeElementSelectedCallback(WidgetEditShapeElementSelectedCallback callback) {
        mElementSelectedCallback = callback;
    }
}
