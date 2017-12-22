package com.zm.zmbletool.ui.classicservice;

import android.content.Context;

import com.zm.zmbletool.mvp.BasePresenter;
import com.zm.zmbletool.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ClassicServiceContract {
    interface View extends BaseView {
        void showToast(String msg);

        void setText(String msg);

        void showReceiveData(byte[] msg);

        void showStatus(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void startBluetooth();

        void sendMsg(byte[] msg);

        void stopBluetooth();
    }
}
