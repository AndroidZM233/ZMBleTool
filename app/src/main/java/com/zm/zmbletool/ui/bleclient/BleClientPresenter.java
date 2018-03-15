package com.zm.zmbletool.ui.bleclient;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.zm.zmbletool.MyApplication;
import com.zm.zmbletool.mvp.BasePresenterImpl;
import com.zm.zmbletool.services.BluetoothLeService;
import com.zm.zmbletool.ui.ble.BleActivity;

import java.util.List;

import static com.zm.zmbletool.services.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.zm.zmbletool.services.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.zm.zmbletool.services.BluetoothLeService.ACTION_GATT_DISCONNECTED;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BleClientPresenter extends BasePresenterImpl<BleClientContract.View> implements BleClientContract.Presenter {
    private final static String TAG = BleActivity.class.getSimpleName();

    @Override
    public void registerReceiver(Context context) {
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mGattUpdateReceiver);
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                mView.finishAct();
            }
        }
    };


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }
}
