package com.mrmo.msharelib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 第三方登录
 * Created by moguangjian on 15/8/12 20:58.
 */
public class MOtherLoginShareSdk extends LinearLayout {
    private static final String TAG = MOtherLoginShareSdk.class.getSimpleName();

    private LoginStatusListener loginStatusListener;
    private PlatformActionListener platformActionListener;

    public MOtherLoginShareSdk(Context context) {
        super(context);
        init();
    }

    public MOtherLoginShareSdk(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MOtherLoginShareSdk(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ShareSDK.initSDK(getContext());
        platformActionListener = new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.i(TAG, platform.toString());
                MPlatform mLoginType = MPlatform.QQ;
                String accessToken = platform.getDb().getToken(); // 获取授权token
                String openId = platform.getDb().getUserId();// 获取用户在此平台的ID
                String name = platform.getDb().get("nickname");
                String avatar = platform.getDb().getUserIcon();

                if (platform.getName().equals(QQ.NAME)) {
                    mLoginType = MPlatform.QQ;
                } else if (platform.getName().equals(Wechat.NAME)) {
                    mLoginType = MPlatform.WECHAT;
                } else if (platform.getName().equals(SinaWeibo.NAME)) {
                    mLoginType = MPlatform.SINA_WEIBO;
                }
//                else if (platform.getName().equals(Facebook.NAME)) {
//                    mLoginType = MLoginType.FACE_BOOK;
//                }

                dealOauthLoginSuccessStatus(mLoginType, accessToken, openId, name, avatar);
                cleanLoginMsg();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e(TAG, "platform:"+platform.toString()+ " code:"+i+" msg:"+throwable.getMessage());
                throwable.printStackTrace();
                dealOauthLoginFailureStatus();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                dealOauthLoginCancelStatus();
            }
        };
    }

    public interface LoginStatusListener {
        public void onSuccess(MPlatform mPlatform, String accessToken, String openId, String name, String avatar);

        public void onFailure();

        public void onStart();

        public void onCancel();
    }


    public void setOnLoginStatusListener(LoginStatusListener loginStatusListener) {
        this.loginStatusListener = loginStatusListener;
    }


    private void dealOauthLoginFailureStatus() {
        if (loginStatusListener == null) {
            return;
        }

        loginStatusListener.onFailure();
    }

    private void dealOauthLoginStartStatus() {
        if (loginStatusListener == null) {
            return;
        }

        loginStatusListener.onStart();
    }

    private void dealOauthLoginCancelStatus() {
        if (loginStatusListener == null) {
            return;
        }

        loginStatusListener.onCancel();
    }

    private void dealOauthLoginSuccessStatus(MPlatform mPlatform, String accessToken, String openId, String name, String avatar) {
        if (loginStatusListener == null) {
            return;
        }

        loginStatusListener.onSuccess(mPlatform, accessToken, openId, name, avatar);
    }

    public void cleanLoginMsg() {
        Platform[] platform = new Platform[]{
                ShareSDK.getPlatform(SinaWeibo.NAME),
                ShareSDK.getPlatform(Wechat.NAME),
                ShareSDK.getPlatform(QQ.NAME)
        };

        for (int i = 0; i < platform.length; i++) {
            platform[i].removeAccount(true);
        }
    }

    public void sinaWeibo() {
        login(ShareSDK.getPlatform(SinaWeibo.NAME));
    }

    public void qq() {
        login(ShareSDK.getPlatform(QQ.NAME));
    }

    public void wechat() {
        login(ShareSDK.getPlatform(Wechat.NAME));
    }

    private void login(Platform platform) {
        platform.SSOSetting(true);
        platform.setPlatformActionListener(platformActionListener);
//        platform.authorize();
        platform.showUser(null);

    }

}
