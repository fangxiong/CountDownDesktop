package com.fax.showdt.fragment.widgetVectorEdit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.ShapeElementCallback;
import com.fax.showdt.callback.WidgetEditVectorElementSelectedCallback;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WidgetVectorElementEditFragment extends Fragment {

    private CommonAdapter<String> mListAdapter;
    private RecyclerView mStickerListRv;
    private String[] titles = new String[]{"形状", "表情", "音乐", "天气", "logo"};
    private String[] jsonPathArray = new String[]{"widget_shape.json", "widget_emoji.json", "widget_media.json", "widget_weather.json", "widget_logo.json"};
    private int mCurrentSelectedPos = 0;
    private WidgetEditVectorElementSelectedCallback mElementSelectedCallback;
    private LongSparseArray<Fragment> mFragments = new LongSparseArray<>();


    public WidgetVectorElementEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_vector_element_edit_fragment, container, false);
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
            VectorElementFragment fragment = new VectorElementFragment();
            fragment.setElementCallback(new ShapeElementCallback() {
                @Override
                public void selectShapeElement(WidgetShapeBean widgetShapeBean) {
                    if(mElementSelectedCallback != null){
                        mElementSelectedCallback.selectVectorElement(widgetShapeBean);
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


    public void setWidgetShapeElementSelectedCallback(WidgetEditVectorElementSelectedCallback callback) {
        mElementSelectedCallback = callback;
    }
}
