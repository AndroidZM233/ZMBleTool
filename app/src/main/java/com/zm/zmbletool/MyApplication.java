package com.zm.zmbletool;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;

import com.zm.utilslib.bean.MsgEvent;
import com.zm.utilslib.utils.LogToFileUtils;
import com.zm.utilslib.utils.SharedXmlUtil;

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
    private volatile BluetoothSocket bleClientSocket = null;

    public BluetoothSocket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(BluetoothSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public BluetoothSocket getbleClientSocket() {
        return bleClientSocket;
    }

    public void setbleClientSocket(BluetoothSocket bleClientSocket) {
        this.bleClientSocket = bleClientSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        m_application = this;
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
        if (bleClientSocket != null) {
            try {
                bleClientSocket.close();
                bleClientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static MyApplication getInstance() {
        return m_application;
    }


}
