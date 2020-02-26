package com.fax.showdt.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fax.showdt.R;
import com.fax.showdt.activity.PushWidgetActivity;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.CommonViewPagerAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.widgetClassification;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.utils.ToastShowUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

public class SelectionWidgetFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TipDialog mTipsDialog;
    protected List<Fragment> mFragmentList = new ArrayList<>();
    protected List<String> mTabList = new ArrayList<>();
    public SelectionWidgetFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selection_widget_fragment,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryWidgetClassificationData();
    }



    private void queryWidgetClassificationData(){
        mTipsDialog = WaitDialog.show((AppCompatActivity) getActivity(),"加载中...");
        BmobQuery<widgetClassification> query = new BmobQuery<>();
        query.findObjects(new FindListener<widgetClassification>() {
            @Override
            public void done(List<widgetClassification> mDatas, BmobException e) {
                mTipsDialog.doDismiss();
//                Log.i("test_req:",String.valueOf(mDatas.size()));
                if (e == null) {
                    if (mDatas != null && mDatas.size() > 0) {
                        for (widgetClassification ccb : mDatas) {
                            mTabList.add(ccb.getcName());
                            mTabLayout.addTab(mTabLayout.newTab());
                        }
                        for(int i=0;i<mDatas.size();i++){
                            mTabLayout.getTabAt(i).setText(mTabList.get(i));
                        }
                        for (int i = 0; i < mDatas.size(); i++) {
                            mFragmentList.add(SelectionFragment.newInstance(mDatas.get(i).getCid()));
                        }
                        mViewPager.setAdapter(new CommonViewPagerAdapter(getChildFragmentManager(), mFragmentList, mTabList));
                        mTabLayout.setupWithViewPager(mViewPager);
                    }
                }else {
                    Log.i("test_req_error:",e.getMessage());
                    e.printStackTrace();
                    ToastShowUtils.showCommonToast(getActivity(),e.getMessage(), Toasty.LENGTH_SHORT);
                }
            }
        });
    }

    private void initView(View view){
        mTabLayout = view.findViewById(R.id.tablayout);
        mViewPager = view.findViewById(R.id.vg);
    }

}
