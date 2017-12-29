package com.zm.zmbletool.ui.classicclient;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.zm.zmbletool.MyApplication;
import com.zm.zmbletool.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ClassicClientPresenter extends BasePresenterImpl<ClassicClientContract.View> implements ClassicClientContract.Presenter {
    private Context mContext;

    @Override
    public void startBluetooth(Context context) {
        this.mContext = context;
        BluetoothSocket clientSocket = MyApplication.getInstance().getClientSocket();
        if (clientSocket != null) {

        }
    }

    @Override
    public void sendMsg(byte[] msg) {

    }

    @Override
    public void stopBluetooth() {

    }
}
