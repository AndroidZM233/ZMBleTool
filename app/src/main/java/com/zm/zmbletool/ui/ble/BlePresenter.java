package com.zm.zmbletool.ui.ble;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.zm.zmbletool.MyApplication;
import com.zm.zmbletool.mvp.BasePresenterImpl;
import com.zm.zmbletool.services.BluetoothLeService;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.zm.zmbletool.services.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.zm.zmbletool.services.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.zm.zmbletool.services.BluetoothLeService.ACTION_GATT_DISCONNECTED;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BlePresenter extends BasePresenterImpl<BleContract.View> implements BleContract.Presenter {
    private final static String TAG = BleActivity.class.getSimpleName();
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    @Override
    public void registerReceiver(Context context, String mDeviceAddress) {
        this.mDeviceAddress = mDeviceAddress;
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void unbindService(Context context) {
        context.unbindService(mServiceConnection);
        mBluetoothLeService.disconnect();
        MyApplication.getInstance().setmBluetoothLeService(null);
        mBluetoothLeService = null;
    }

    @Override
    public void bindService(Context context) {
        //绑定服务
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                MyApplication.getInstance().setmBluetoothLeService(mBluetoothLeService);
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                mView.openScanAct();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // 显示用户界面上所有受支持的服务和特性
                List<BluetoothGattService> supportedGattServices =
                        mBluetoothLeService.getSupportedGattServices();
                mView.backList(supportedGattServices);
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                String stringExtra = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                mView.showToast(stringExtra);
            }
        }
    };


    // 管理服务生命周期的代码
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                mView.finishAct();
            }
            // 在成功启动初始化时自动连接到设备.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
