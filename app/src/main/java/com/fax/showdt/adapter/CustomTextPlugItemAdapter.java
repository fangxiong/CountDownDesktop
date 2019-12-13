package com.fax.showdt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.fax.showdt.R;
import com.fax.showdt.bean.CustomPlugTextBean;
import com.fax.showdt.utils.CustomPlugUtil;

import java.util.List;

public class CustomTextPlugItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<CustomPlugTextBean> datas;
    private LayoutInflater mInflater;
    private OnClickTextPlugListener mListener;
    public final int TAG_ITEM = 0;
    public final int TEXT_ITEM = 1;

    public CustomTextPlugItemAdapter(Context context, List<CustomPlugTextBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.datas = list;
    }

    public void setAddTextPlugListener(OnClickTextPlugListener listener) {
        this.mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TAG_ITEM) {
            return new CustomTextPlugTagHolder(mInflater.inflate(R.layout.custom_text_plug_tag, viewGroup, false));
        } else {
            return new CustomTextPlugContentHolder(mInflater.inflate(R.layout.custom_text_plug_content_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        final CustomPlugTextBean bean = datas.get(position);
        if (type == TAG_ITEM) {
//            CustomTextPlugTagHolder holder = (CustomTextPlugTagHolder) viewHolder;
//            holder.mTvTag.setText(bean.getTag());
        } else {
            CustomTextPlugContentHolder holder = (CustomTextPlugContentHolder) viewHolder;
            holder.mTvContent.setText(CustomPlugUtil.getPlugTextFromSigns(bean.getTag()));
            if (bean.isSingleLine()) {
                holder.mTopLine.setVisibility(View.GONE);
                holder.mRightLine.setVisibility(View.VISIBLE);
            } else if (bean.isDoubleLine()) {
                holder.mTopLine.setVisibility(View.VISIBLE);
                holder.mRightLine.setVisibility(View.VISIBLE);
            } else {
                holder.mTopLine.setVisibility(View.GONE);
                holder.mRightLine.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickTextPlug(bean);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (datas.get(position).isHead()) {
            return TAG_ITEM;
        } else {
            return TEXT_ITEM;
        }
    }

    private class CustomTextPlugTagHolder extends RecyclerView.ViewHolder {


        private CustomTextPlugTagHolder(View itemView) {
            super(itemView);
        }

    }

    private class CustomTextPlugContentHolder extends RecyclerView.ViewHolder {
        final TextView mTvContent;
        final TextView mTopLine;
        final TextView mRightLine;

        private CustomTextPlugContentHolder(View rootView) {
            super(rootView);
            mTvContent = (TextView) rootView.findViewById(R.id.tv_content);
            mTopLine = (TextView) rootView.findViewById(R.id.top_line);
            mRightLine = (TextView) rootView.findViewById(R.id.right_line);
        }


    }

    public interface OnClickTextPlugListener {
        void onClickTextPlug(CustomPlugTextBean bean);
    }
}
