package com.mrmo.mshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mrmo.msharelib.MOtherLoginShareSdk;
import com.mrmo.msharelib.MPlatform;
import com.mrmo.msharelib.MShareListener;
import com.mrmo.msharelib.MShareShareSdk;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnShareWeChat;
    private Button btnLoginWeChat;
    private Button btnLoginQQ;
    private MOtherLoginShareSdk mOtherLogin;

    private MShareShareSdk mShareShareSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOtherLogin = (MOtherLoginShareSdk) findViewById(R.id.mOtherLogin);
        btnLoginQQ = (Button) findViewById(R.id.btnLoginQQ);
        btnLoginWeChat = (Button) findViewById(R.id.btnLoginWeChat);
        btnShareWeChat = (Button) findViewById(R.id.btnShareWeChat);

        btnLoginWeChat.setOnClickListener(this);
        btnLoginQQ.setOnClickListener(this);
        btnShareWeChat.setOnClickListener(this);

        mShareShareSdk = new MShareShareSdk(this);
        mShareShareSdk.setOnMShareListener(new MShareListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(getApplicationContext(), "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }
        });

        mOtherLogin.setOnLoginStatusListener(new MOtherLoginShareSdk.LoginStatusListener() {
            @Override
            public void onSuccess(MPlatform mPlatform, String accessToken, String openId, String name, String avatar) {
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onCancel() {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShareWeChat:
                mShareShareSdk.share("title", "msg", "titleUrl", "imageUrl", "imageFilePath", MPlatform.WECHAT.WECHAT);
                break;

            case R.id.btnLoginWeChat:
                mOtherLogin.wechat();
                break;

            case R.id.btnLoginQQ:
                mOtherLogin.qq();
                break;
        }
    }
}
