package com.fax.showdt.fragment.widgetStickerEdit;

import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVGParseException;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.StickerBean;
import com.fax.showdt.bean.WidgetShapeBean;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WidgetShapeElementEditFragment extends Fragment {

    private CommonAdapter<String> mListAdapter;
    private CommonAdapter<WidgetShapeBean> mStickerAdapter;
    private List<WidgetShapeBean> mCurrentStickerBean = new ArrayList<>();
    private RecyclerView mStickerContentRv, mStickerListRv;
    private String[] titles = new String[]{"形状", "表情", "音乐", "天气", "logo"};
    private String[] jsonPathArray = new String[]{"widget_shape.json", "widget_emoji.json", "widget_media.json", "widget_weather.json", "widget_logo.json"};
    private int mCurrentSelectedPos = 0;
    private WidgetEditShapeElementSelectedCallback mElementSelectedCallback;


    public WidgetShapeElementEditFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_sticker_element_edit_fragment, container, false);
        mStickerContentRv = view.findViewById(R.id.rv_content);
        mStickerListRv = view.findViewById(R.id.rv_list);
        initTextPlugSelectUI();
        return view;
    }


    private void initTextPlugSelectUI() {
        mStickerAdapter = new CommonAdapter<WidgetShapeBean>(getActivity(), R.layout.widget_sticker_content_item, mCurrentStickerBean) {
            @Override
            protected void convert(ViewHolder holder, WidgetShapeBean stickerBean, int position) {
                ImageView mIv = holder.getView(R.id.iv_element);
                try {
                    SVG svg = new SVGBuilder().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.c_A0A0A0), PorterDuff.Mode.SRC_IN))
                            .readFromAsset(mContext.getAssets(), stickerBean.getSvgPath()).build();
                    mIv.setImageDrawable(svg.getDrawable());

//                    com.caverock.androidsvg.SVG mSVG = com.caverock.androidsvg.SVG.getFromAsset(mContext.getAssets(), stickerBean.getSvgPath());
//                    Picture picture = mSVG.renderToPicture();
//                    PictureDrawable pictureDrawable = new PictureDrawable(picture);
//                    pictureDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.c_A0A0A0), PorterDuff.Mode.SRC_IN));
//                    mIv.setImageDrawable(pictureDrawable);

                }catch (IOException e){
                }
            }
        };
        mStickerAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mElementSelectedCallback.selectShapeElement(mCurrentStickerBean.get(position));

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
                dataSetChange(position);
                TextView mTv = view.findViewById(R.id.tv_element);
                mTv.setBackgroundResource(R.color.c_FCF43C);
                mListAdapter.notifyItemChanged(mCurrentSelectedPos);
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
        mCurrentStickerBean.clear();
        String jsonPath = jsonPathArray[pos];
        String str = FileExUtils.getJsonFromAssest(getActivity(), jsonPath);
        mCurrentStickerBean.addAll(GsonUtils.parseJsonArrayWithGson(str, WidgetShapeBean.class));
        Log.i("test_img_update:","size:"+mCurrentStickerBean.size());
        mStickerAdapter.notifyDataSetChanged();
    }


    public void setWidgetShapeElementSelectedCallback(WidgetEditShapeElementSelectedCallback callback) {
        mElementSelectedCallback = callback;
    }
}
