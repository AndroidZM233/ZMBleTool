package com.zm.zmbletool.ui.bleclient;

import android.content.Context;

import com.zm.zmbletool.mvp.BasePresenter;
import com.zm.zmbletool.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BleClientContract {
    interface View extends BaseView {
        void finishAct();
    }

    interface Presenter extends BasePresenter<View> {
        void registerReceiver(Context context);

        void unregisterReceiver(Context context);
    }
}
