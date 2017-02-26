package com.mrmo.msharelib;

/**
 * Created by moguangjian on 16/1/4 17:22.
 */
public interface MShareListener {

    void onSuccess();

    void onFailure(String msg);

    void onCancel();
}
