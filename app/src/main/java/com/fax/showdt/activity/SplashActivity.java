package com.fax.showdt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fax.showdt.R;
import com.fax.showdt.dialog.ios.v3.CustomDialog;
import com.fax.showdt.manager.CommonConfigManager;
import com.fax.showdt.utils.Constant;
import com.fax.showdt.utils.NotchUtils;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private CustomDialog privacyDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotchUtils.openFullScreenModel(this);
        showPrivacyDialog();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_allow){
            CommonConfigManager.getInstance().setHadAllowedPrivacy();
            if(privacyDialog != null){
                privacyDialog.doDismiss();
            }
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }else if(v.getId() == R.id.tv_not_allow){
            if(privacyDialog != null){
                privacyDialog.doDismiss();
            }
            finish();
        }else if(v.getId() == R.id.tv_privacy_protocol){

            Intent intent = new Intent(SplashActivity.this, WebActivity.class);
            intent.putExtra(WebActivity.URL_KEY, Constant.WIDGET_PRIVACY_URL);
            startActivity(intent);
        }else if(v.getId() == R.id.tv_service_protocol){
            Intent intent = new Intent(SplashActivity.this, WebActivity.class);
            intent.putExtra(WebActivity.URL_KEY, Constant.WIDGET_PRIVACY_URL);
            startActivity(intent);
        }
    }

    private void showPrivacyDialog(){
        if(CommonConfigManager.getInstance().isHadAllowedPrivacy()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
            },1500);
        }else {
            privacyDialog = CustomDialog.build(this, R.layout.privacy_dialog, new CustomDialog.OnBindView() {
                @Override
                public void onBind(CustomDialog dialog, View v) {
                    TextView tvAllow = v.findViewById(R.id.tv_allow);
                    TextView tvNotAllow = v.findViewById(R.id.tv_not_allow);
                    TextView tvPrivacyProtocol = v.findViewById(R.id.tv_privacy_protocol);
                    tvPrivacyProtocol.setOnClickListener(SplashActivity.this);
                    TextView tvServiceProtocol = v.findViewById(R.id.tv_service_protocol);
                    tvServiceProtocol.setOnClickListener(SplashActivity.this);
                    tvPrivacyProtocol.setText(Html.fromHtml(SplashActivity.this.getResources().getString(R.string.privacy_protocol)));
                    tvServiceProtocol.setText(Html.fromHtml(SplashActivity.this.getResources().getString(R.string.service_protocol)));
                    tvAllow.setOnClickListener(SplashActivity.this);
                    tvNotAllow.setOnClickListener(SplashActivity.this);
                }
            });
            privacyDialog.setCancelable(false);
            privacyDialog.show();
        }
    }

}
