package com.zm.zmbletool.ui.classicclient;

import android.content.Context;

import com.zm.zmbletool.mvp.BasePresenter;
import com.zm.zmbletool.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ClassicClientContract {
    interface View extends BaseView {
        void showReceiveData(byte[] msg);

        void showStatus(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void startBluetooth(Context context);

        void sendMsg(byte[] msg);

        void stopBluetooth();
    }
}
