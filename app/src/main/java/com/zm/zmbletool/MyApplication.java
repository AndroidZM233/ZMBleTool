package com.zm.zmbletool;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.zm.utilslib.bean.MsgEvent;
import com.zm.utilslib.utils.LogToFileUtils;
import com.zm.utilslib.utils.SharedXmlUtil;
import com.zm.zmbletool.services.BluetoothLeService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 张明_ on 2017/8/17.
 * Email 741183142@qq.com
 */

public class MyApplication extends Application {
    private static MyApplication m_application; // 单例
    private volatile BluetoothSocket clientSocket = null;
    private BluetoothLeService mBluetoothLeService = null;
    private BluetoothGattCharacteristic characteristic = null;

    public BluetoothSocket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(BluetoothSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    public void setmBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        m_application = this;

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        closeSocket();
    }

    /**
     * 关闭BluetoothSocket
     */
    private void closeSocket() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
                clientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
            mBluetoothLeService = null;
        }
        if (characteristic != null) {
            characteristic = null;
        }
    }

    public static MyApplication getInstance() {
        return m_application;
    }


}
