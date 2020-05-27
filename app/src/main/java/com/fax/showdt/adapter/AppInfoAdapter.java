package com.fax.showdt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fax.showdt.R;
import com.fax.showdt.bean.AppInfo;
import com.fax.showdt.bean.AppInfoData;
import com.fax.showdt.view.cnPinyin.CNPinyin;
import com.fax.showdt.view.cnPinyin.StickyHeaderAdapter;

import java.util.List;


public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.AppInfoHolder> implements StickyHeaderAdapter<AppInfoAdapter.HeaderHolder> {

    private final List<CNPinyin<AppInfoData>> cnPinyinList;
    private OnSelectOneListener mOnSelectOneListener;

    public AppInfoAdapter(List<CNPinyin<AppInfoData>> cnPinyinList) {
        this.cnPinyinList = cnPinyinList;
    }

    public void setOnSelectOneListener(OnSelectOneListener onSelectOneListener){
        this.mOnSelectOneListener = onSelectOneListener;
    }

    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }

    @Override
    public AppInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bind_app_item_app_info, parent, false));
    }


    @Override
    public void onBindViewHolder(AppInfoHolder holder, final int position) {
        AppInfoData appInfo = cnPinyinList.get(position).data;
        holder.ll_rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSelectOneListener.selectOne(position);
            }
        });
        holder.iv_header.setImageDrawable(appInfo.getIcon());
        holder.tv_name.setText(appInfo.name);
    }

    @Override
    public long getHeaderId(int childAdapterPosition) {
        char firstChar = cnPinyinList.get(childAdapterPosition).getFirstChar();
        String str = String.valueOf(firstChar);
        return str.matches("[a-zA-Z]+") ? firstChar : '#';
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder holder, int childAdapterPosition) {
        char firstChar = cnPinyinList.get(childAdapterPosition).getFirstChar();
        String str = String.valueOf(firstChar);
        holder.tv_header.setText(String.valueOf(str.matches("[a-zA-Z]+") ? firstChar : '#'));
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bind_app_item_header, parent, false));
    }

    class AppInfoHolder extends RecyclerView.ViewHolder {
        final LinearLayout ll_rootView;
        final ImageView iv_header;
        final TextView tv_name;

        AppInfoHolder(View itemView) {
            super(itemView);
            ll_rootView = (LinearLayout) itemView.findViewById(R.id.rootView);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        final TextView tv_header;

        HeaderHolder(View itemView) {
            super(itemView);
            tv_header = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }

    public interface OnSelectOneListener{
        void selectOne(int pos);
    }

}
