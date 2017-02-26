package com.mrmo.msharelib;

/**
 * Created by moguangjian on 16/1/4 15:41.
 */
public interface MShareAble {

    void init();

    void setOnMShareListener(MShareListener mShareListener);

    void share(String title, String msg, String titleUrl, String imageUrl, MPlatform mShareType);

}
