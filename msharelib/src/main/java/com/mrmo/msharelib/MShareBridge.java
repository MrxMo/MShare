package com.mrmo.msharelib;

/**
 * 分享
 * Created by moguangjian on 16/1/4 15:01.
 */
public class MShareBridge implements MShareAble {

    private MShareAble mShareAble;

    public MShareAble getmShareAble() {
        return mShareAble;
    }

    public void setmShareAble(MShareAble mShareAble) {
        this.mShareAble = mShareAble;
    }

    @Override
    public void init() {
        mShareAble.init();
    }

    @Override
    public void setOnMShareListener(MShareListener mShareListener) {
        mShareAble.setOnMShareListener(mShareListener);
    }

    @Override
    public void share(String title, String msg, String titleUrl, String imageUrl, MPlatform mShareType) {
        mShareAble.share(title, msg, titleUrl, imageUrl, mShareType);
    }
}
