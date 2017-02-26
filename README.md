# MShare
安卓一键整合第三方分享。默认整合Share SDK。
<br/>

## 支持
Android 2.2+
<br/>

## Gradle [下载aar](https://github.com/MrxMo/MShare/raw/master/release/msharelib-v1.0.aar)

在项目的gradle中添加
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

在app的gradle中添加
 ```
 compile 'com.github.MrxMo:MShare:v1.0'
 ```
 <br/>

## 使用
* 配置AndroidManifest.xml

```
<activity
            android:name="com.mob.tools.MobUIShell"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:windowSoftInputMode="stateHidden|adjustResize" >

            <!--必须设置tencent*** 否则无法正常回调-->
            <intent-filter>
                <data android:scheme="tencent100371282" />
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.BROWSABLE" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- 调用新浪原生SDK，需要注册的回调activity -->
            <intent-filter>
                <action android:name="com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>

        <activity
            android:name=".wxapi.WXEntryActivity"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true" />
```

* 在app Module --> src --> main，添加assets目录，并添加ShareSdk.xml文件

* 分享

```
MShareShareSdk mShareShareSdk = new MShareShareSdk(this);
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

        mShareShareSdk.share("title", "msg", "titleUrl", "imageUrl", "imageFilePath", MPlatform.WECHAT.WECHAT);
```

* 第三方登录

```
MOtherLoginShareSdk mOtherLogin = (MOtherLoginShareSdk) findViewById(R.id.mOtherLogin);
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
        mOtherLogin.wechat();
```
<br/>

## 作者
莫先生 Mr-Mo 



 