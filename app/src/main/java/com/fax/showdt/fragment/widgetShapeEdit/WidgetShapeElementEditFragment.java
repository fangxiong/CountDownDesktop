package com.fax.showdt.fragment.widgetShapeEdit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.fax.showdt.callback.WidgetEditShapeElementSelectedCallback;
import com.fax.showdt.utils.FileExUtils;
import com.fax.showdt.utils.GsonUtils;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-12
 * Description:
 */
public class WidgetShapeElementEditFragment extends Fragment {
    private CommonAdapter<WidgetShapeBean> mListAdapter;
    private List<WidgetShapeBean> mList;
    private Context mContext;
    private RecyclerView mRv;
    private WidgetEditShapeElementSelectedCallback mCallback;


    public WidgetShapeElementEditFragment(Context context){
        mContext = context;
    }

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
        String str = FileExUtils.getJsonFromAssest(mContext, "widget_shape.json");
        mList = GsonUtils.parseJsonArrayWithGson(str, WidgetShapeBean.class);
        final GridLayoutManager manager = new GridLayoutManager(mContext, 4, RecyclerView.VERTICAL, false);

        mRv.setLayoutManager(manager);
        mListAdapter = new CommonAdapter<WidgetShapeBean>(mContext,R.layout.widget_shape_element_item, mList) {
            @Override
            protected void convert(ViewHolder holder, WidgetShapeBean bean, int position) {
                ImageView mIv = holder.getView(R.id.iv_element);
                Log.i("test_item:",bean.getSvgPath()+" uri:"+Uri.parse(bean.getSvgPath()));
                GlideToVectorYou.justLoadImage(getActivity(), Uri.parse("file:///android_asset/"+bean.getSvgPath()), mIv);
            }
        };
        mListAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mCallback.selectShapeElement(mList.get(position));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRv.setAdapter(mListAdapter);

    }

    public void setWidgetEditShapeElementSelectedCallback(WidgetEditShapeElementSelectedCallback widgetEditShapeElementSelectedCallback) {
        mCallback = widgetEditShapeElementSelectedCallback;
    }
}
