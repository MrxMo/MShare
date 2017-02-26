package com.mrmo.msharelib;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * ShareSdk 分享实现类
 * Created by moguangjian on 16/1/4 17:26.
 */
public class MShareShareSdk implements MShareAble {

    private static final String TAG = MShareShareSdk.class.getSimpleName();

    private final int SHARE_SUCCESS = 1;
    private final int SHARE_FAIL = 2;
    private final int SHARE_CANCEL = 3;

    private MShareListener mShareListener;

    private Context context;

    public MShareShareSdk(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        ShareSDK.initSDK(context);
    }

    @Override
    public void setOnMShareListener(MShareListener mShareListener) {
        this.mShareListener = mShareListener;
    }

    public void share(String title, String msg, String titleUrl, String imageUrl, String imageFilePath, MPlatform mShareType) {
        Platform.ShareParams shareParams = null;
        String platformName = "";

        switch (mShareType) {
            case QQ:
                shareParams = new QQ.ShareParams();
                platformName = QQ.NAME;
                break;

            case QQZone:
                shareParams = new QZone.ShareParams();
                platformName = QZone.NAME;
                break;

            case WECHAT:
                shareParams = new Wechat.ShareParams();
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                platformName = Wechat.NAME;
                break;

            case WECHAT_MOMENTS:
                shareParams = new WechatMoments.ShareParams();
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                platformName = WechatMoments.NAME;
                break;

            case SINA_WEIBO:
                shareParams = new SinaWeibo.ShareParams();
                platformName = SinaWeibo.NAME;
                break;

//            case FACE_BOOK:
//                shareParams = new Facebook.ShareParams();
//                platformName = Facebook.NAME;
//                break;

            default:
                if (mShareListener != null) {
                    mShareListener.onFailure("请设置分享平台信息");
                }
                return;
        }

        share(title, msg, titleUrl, imageUrl, imageFilePath, shareParams, platformName);
    }

    @Override
    public void share(String title, String msg, String titleUrl, String imageUrl, MPlatform mShareType) {
        share(title, msg, titleUrl, imageUrl, "", mShareType);
    }

    private void share(String title, String msg, String titleUrl, String imageUrl, String imageFilePath, Platform.ShareParams shareParams, String platformName) {
        init();
        shareParams.setTitle(title);
        shareParams.setTitleUrl(titleUrl);
        shareParams.setText(msg);
        shareParams.setUrl(titleUrl);
        shareParams.setImageUrl(imageUrl);

        Platform platform = ShareSDK.getPlatform(platformName);

        if (platformName.equals(SinaWeibo.NAME) && platform.isClientValid()) {//qq、qzone、明道、人人、新浪、腾讯微博和版本2.6+有效
            shareParams.setImageUrl(imageUrl);
        } else { //网页发送图片需要申请权限
            if (imageFilePath != null && imageFilePath.trim().length() > 0) {
                shareParams.setImagePath(imageFilePath);

            } else {
                Log.w(TAG, "share image path is null. setImagePath is failure.");
            }
        }

        platform.setPlatformActionListener(platformActionListener);
        platform.share(shareParams);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHARE_SUCCESS:
                    try {
                        Platform platform = (Platform) msg.obj;
//                        监听不到回调
//                        if ((platform.getName().equals(Wechat.NAME)) || platform.getName().equals(WechatMoments.NAME)) {
//                            return;
//                        }
//
//                        if ((platform.getName().equals(SinaWeibo.NAME))) {
//                            if (platform.isClientValid()) {
//                                return;
//                            }
//                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mShareListener != null) {
                        mShareListener.onSuccess();
                    }
                    break;
                case SHARE_FAIL:
                    String errorMsg = "分享失败,请稍候重试";
                    try {
                        String expName = msg.obj.getClass().getSimpleName();
                        if ("WechatClientNotExistException".equalsIgnoreCase(expName)
                                || "WechatTimelineNotSupportedException".equalsIgnoreCase(expName)
                                || "WechatFavoriteNotSupportedException".equalsIgnoreCase(expName)) {
                            errorMsg = "微信未安装或版本太低";
                        } else if ("QQClientNotExistException".equals(expName)) {
                            errorMsg = "QQ未安装或版本太低！";

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mShareListener != null) {
                        mShareListener.onFailure(errorMsg);
                    }
                    break;

                case SHARE_CANCEL:
                    if (mShareListener != null) {
                        mShareListener.onCancel();
                    }
                    break;
            }
        }
    };

    private PlatformActionListener platformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//            platform.removeAccount(true);
            Message message = new Message();
            message.what = SHARE_SUCCESS;
            message.obj = platform;
            handler.sendMessage(message);
        }

        @Override
        public void onError(final Platform platform, int i, final Throwable throwable) {
            Log.e(TAG, "error: " + i + " msg:" + throwable.getMessage());
            throwable.printStackTrace();
            Message message = new Message();
            message.what = SHARE_FAIL;
            message.obj = throwable;
            handler.sendMessage(message);
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Message message = new Message();
            message.what = SHARE_CANCEL;
            message.obj = platform;
            handler.sendMessage(message);
        }
    };

    /**
     * 分享qq
     * 注：
     * 1、QQ分享图文和音乐，在PC版本的QQ上可能只看到一条连接，因为PC版本的QQ只会对其白名单的连接作截图
     * 2、title：最多30个字符 text：最多40个字符
     */
    private void qq(String title, String msg, String titleUrl, String imageUrl) {
        String qqTitle = title;
        String qqMsg = msg;

        if (title.length() > 30) {
            qqTitle = title.substring(0, title.length() - 5) + "...";
        }

        if (msg.length() > 40) {
            qqMsg = msg.substring(0, msg.length() - 5) + "...";
        }

        QQ.ShareParams shareParams = new QQ.ShareParams();
        //imageUrl 可选
        shareParams.setTitle(qqTitle);
        shareParams.setTitleUrl(titleUrl);
        shareParams.setUrl(titleUrl);
        shareParams.setText(qqMsg);
        shareParams.setImageUrl(imageUrl);

        Platform platform = ShareSDK.getPlatform(QQ.NAME);
        platform.setPlatformActionListener(platformActionListener);
        platform.share(shareParams);

        share(title, msg, titleUrl, imageUrl, "", new QQ.ShareParams(), QQ.NAME);
    }

    /**
     * 分享qq空间
     * 注：title：最多200个字符 text：最多600个字符
     */
    private void qqZone(String title, String msg, String titleUrl, String imageUrl) {
        share(title, msg, titleUrl, imageUrl, "", new QZone.ShareParams(), QZone.NAME);
    }

    /**
     * title：512Bytes以内
     * text：1KB以内
     * imageData：10M以内
     * imagePath：10M以内(传递的imagePath路径不能超过10KB)
     * imageUrl：10KB以内 musicUrl：
     * 10KB以内 url：10KB以内
     * 微信好友可以进行文字或者单张图片或者文件进行分享,分享回调不会正确回调
     */
    private void wechat(String title, String msg, String titleUrl, String imageUrl) {
/*
        String weixinTitle = title;
        String weixinMsg = msg;
        int titleLength = 512 / 2;//一个汉字两个字节
        if (title.length() >= titleLength) {
            weixinTitle = title.substring(0, titleLength - 5) + "...";
        }

        int msgLength = 1024 / 2;//一个汉字两个字节
        if (msg.length() >= msgLength) {
            weixinMsg = msg.substring(0, msgLength - 5) + "...";
        }
*/
        share(title, msg, titleUrl, imageUrl, "", new Wechat.ShareParams(), Wechat.NAME);
    }

    /**
     * 分享微信朋友圈
     * 注：微信朋友圈可以分享单张图片或者图片与文字一起分享
     */
    private void wechatMoments(String title, String msg, String titleUrl, String imageUrl) {
/*
        String weixinTitle = msg;
        String weixinMsg = title;

        int titleLength = 512 / 2;//一个汉字两个字节
        if (title.length() >= titleLength) {
            weixinTitle = title.substring(0, titleLength - 5) + "...";
        }

        int msgLength = 1024 / 2;//一个汉字两个字节
        if (msg.length() >= msgLength) {
            weixinMsg = msg.substring(0, msgLength - 5) + "...";
        }
*/
        share(title, msg, titleUrl, imageUrl, "", new WechatMoments.ShareParams(), WechatMoments.NAME);
    }

    /**
     * 分享微博
     * 注：msg：不能超过140个汉字 image：图片最大5M，仅支持JPEG、GIF、PNG格式
     */
    private void sinaWeibo(String title, String msg, String titleUrl, String imageUrl) {
        String weiboMsg = msg;
        weiboMsg = weiboMsg + titleUrl;
/*

        if (msg.length() > 140) {
            weiboMsg = msg.substring(0, msg.length() - 5) + "...";
        }
*/

//        if (weibo.isClientValid()) {//qq、qzone、明道、人人、新浪、腾讯微博和版本2.6+有效
//            shareParams.setImageUrl(imageUrl);
//        } else { //网页发送图片需要申请权限
//            // TODO 网页发送图片需要申请权限...
//        }

        share(title, msg, titleUrl, imageUrl, "", new SinaWeibo.ShareParams(), SinaWeibo.NAME);
    }


}
