package com.fax.showdt.fragment.widgetVectorEdit;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.WidgetShapeBean;
import com.fax.showdt.callback.WidgetEditVectorElementSelectedCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.view.svg.SVG;
import com.fax.showdt.view.svg.SVGBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-12
 * Description:
 */
public class WidgetVectorElementEditFragment1 extends Fragment {
    private CommonAdapter<WidgetShapeBean> mListAdapter;
    private List<WidgetShapeBean> mList;
    private RecyclerView mRv;
    private WidgetEditVectorElementSelectedCallback mCallback;


    public WidgetVectorElementEditFragment1(){}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_shape_element_edit_fragment, container, false);
        mRv = view.findViewById(R.id.rv_content);
        initTextPlugSelectUI();
        return view;
    }


    private void initTextPlugSelectUI() {
        mList = new ArrayList<>();
        String str = FileExUtils.getJsonFromAssest(getActivity(), "widget_shape.json");
        mList = GsonUtils.parseJsonArrayWithGson(str, WidgetShapeBean.class);
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 4, RecyclerView.VERTICAL, false);

        mRv.setLayoutManager(manager);
        mListAdapter = new CommonAdapter<WidgetShapeBean>(getActivity(),R.layout.widget_shape_element_item, mList) {
            @Override
            protected void convert(ViewHolder holder, WidgetShapeBean bean, int position) {
                ImageView mIv = holder.getView(R.id.iv_element);
                try {
                    SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.c_A0A0A0), PorterDuff.Mode.SRC_IN))
                            .readFromAsset(mContext.getAssets(), bean.getSvgPath()).build();
                    mIv.setImageDrawable(svg.getDrawable());
                }catch (IOException e){
                }
            }
        };
        mListAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mCallback.selectVectorElement(mList.get(position));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRv.setAdapter(mListAdapter);

    }

    public void setWidgetEditShapeElementSelectedCallback(WidgetEditVectorElementSelectedCallback widgetEditShapeElementSelectedCallback) {
        mCallback = widgetEditShapeElementSelectedCallback;
    }
}
