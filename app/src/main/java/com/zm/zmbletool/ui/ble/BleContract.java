package com.zm.zmbletool.ui.ble;

import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.zm.zmbletool.mvp.BasePresenter;
import com.zm.zmbletool.mvp.BaseView;

import java.util.List;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BleContract {
    interface View extends BaseView {
        void finishAct();

        void openScanAct();
        void showToast(String msg);

        void backList(List<BluetoothGattService> supportedGattServices);
    }

    interface Presenter extends BasePresenter<View> {
        void registerReceiver(Context context, String mDeviceAddress);

        void unregisterReceiver(Context context);

        void unbindService(Context context);

        void bindService(Context context);
    }
}
