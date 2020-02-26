package com.fax.showdt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fax.showdt.EventMsg;
import com.fax.showdt.R;
import com.fax.showdt.adapter.CommonAdapter;
import com.fax.showdt.adapter.CommonViewPagerAdapter;
import com.fax.showdt.adapter.MultiItemTypeAdapter;
import com.fax.showdt.adapter.ViewHolder;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.LoginRepoUserInfo;
import com.fax.showdt.bean.User;
import com.fax.showdt.bean.WidgetConfig;
import com.fax.showdt.bean.widgetClassification;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
import com.fax.showdt.dialog.ios.v3.TipDialog;
import com.fax.showdt.dialog.ios.v3.WaitDialog;
import com.fax.showdt.manager.FaxUserManager;
import com.fax.showdt.manager.widget.WidgetPushManger;
import com.fax.showdt.utils.GlideUtils;
import com.fax.showdt.utils.GsonUtils;
import com.fax.showdt.utils.ToastShowUtils;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import es.dmoral.toasty.Toasty;

public class PushWidgetActivity extends BaseActivity implements View.OnClickListener {

    private final static String supportVersion = "0";
    private ImageView mIvCover;
    private EditText editText;
    private TextView mTvCaterogy;
    private TextView mTvPUsh;
    private CustomWidgetConfig customWidgetConfig;
    private CommonAdapter<widgetClassification> mAdapter;
    private String cid;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_widget_activity);
        mIvCover = findViewById(R.id.iv_cover);
        editText = findViewById(R.id.et_title);
        mTvCaterogy = findViewById(R.id.tv_category);
        mTvPUsh = findViewById(R.id.tv_push);
        initData();
    }

    @Override
    protected boolean isEnableImmersionBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if(resId == R.id.tv_category){
            queryWidgetClassificationData();
        }else if(resId == R.id.tv_push){
            if(BmobUser.isLogin()){
                pushWidget();
            }else {
                startActivity(new Intent(this,SignInActivity.class));
            }
        }else if(resId == R.id.iv_back){
            finish();
        }
    }

    private void initData(){
        Intent intent = getIntent();
        String widget =  intent.getStringExtra(EventMsg.widget_data_to_push);
        if(!TextUtils.isEmpty(widget)){
            customWidgetConfig = GsonUtils.parseJsonWithGson(widget,CustomWidgetConfig.class);
            String coverUrl = customWidgetConfig.getCoverUrl();
            GlideUtils.loadImage(this,coverUrl,mIvCover);
        }
    }

    private void pushWidget(){
        String title = editText.getText().toString();
        if(TextUtils.isEmpty(title)){
            ToastShowUtils.showCommonToast(this,"请输入标题",Toasty.LENGTH_SHORT);
            return;
        }else {
            customWidgetConfig.setTitle(title);
            customWidgetConfig.setVersion(Integer.valueOf(supportVersion));
        }
        WidgetPushManger.getInstance().startPost(this, customWidgetConfig, new WidgetPushManger.PushWidgetCallback() {
            @Override
            public void pushStart() {
                WaitDialog.show(PushWidgetActivity.this,"上传中...");
            }

            @Override
            public void pushSuc() {
                insertWidget();
            }

            @Override
            public void pushFail(String errorMsg) {
                Log.i("PushWidgetActivity:",errorMsg);
                WaitDialog.show(PushWidgetActivity.this,"上传失败", TipDialog.TYPE.ERROR);
            }
        });
    }

    private void insertWidget(){
        WidgetConfig widgetConfig = new WidgetConfig();
        widgetConfig.setTitle(customWidgetConfig.getTitle());
        widgetConfig.setDesc(customWidgetConfig.getDesc());
        widgetConfig.setConfig(customWidgetConfig.toJSONString());
        widgetConfig.setSupportVersion(supportVersion);
        widgetConfig.setCid(cid);
        User user = FaxUserManager.getInstance().getUserBean();
        widgetConfig.setUserIcon(user.getAvatarUrl());
        widgetConfig.setUserNick(user.getUserNick());
        widgetConfig.setUserUid(user.getObjectId());
        widgetConfig.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    WaitDialog.show(PushWidgetActivity.this,"上传成功", TipDialog.TYPE.SUCCESS);
                }else{
                    Log.i("PushWidgetActivity:",e.getMessage());
                    e.printStackTrace();
                    WaitDialog.show(PushWidgetActivity.this,"上传失败", TipDialog.TYPE.ERROR);
                }
            }
        });
    }

    private void showCategoryDialog(final  List<widgetClassification> mData){
        mAdapter = new CommonAdapter<widgetClassification>(PushWidgetActivity.this,R.layout.push_widget_category_item,mData) {
            @Override
            protected void convert(ViewHolder holder, widgetClassification o, int position) {
                TextView tvCategory = holder.getView(R.id.tv_category);
                tvCategory.setText(o.getcName());
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }
        };
        final CustomDialog dialog = CustomDialog.build(PushWidgetActivity.this, R.layout.push_widget_category_dialog, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                RecyclerView recyclerView = v.findViewById(R.id.rv);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(PushWidgetActivity.this));
                recyclerView.setAdapter(mAdapter);
            }
        });
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                dialog.doDismiss();
                cid = mData.get(position).getCid();
                mTvCaterogy.setText(mData.get(position).getcName());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        dialog.show();
    }
    private void queryWidgetClassificationData(){
        BmobQuery<widgetClassification> query = new BmobQuery<>();
        query.findObjects(new FindListener<widgetClassification>() {
            @Override
            public void done(List<widgetClassification> mDatas, BmobException e) {
//                Log.i("test_req:",String.valueOf(mDatas.size()));
                if (e == null) {
                    showCategoryDialog(mDatas);
                }else {
                    Log.i("test_req_error:",e.getMessage());
                    e.printStackTrace();
                    ToastShowUtils.showCommonToast(PushWidgetActivity.this,e.getMessage(), Toasty.LENGTH_SHORT);
                }
            }
        });
    }

}
