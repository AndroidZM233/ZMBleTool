package com.zm.zmbletool.ui.classicclient;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.zm.utilslib.utils.data.ByteUtils;
import com.zm.zmbletool.MyApplication;
import com.zm.zmbletool.mvp.BasePresenterImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ClassicClientPresenter extends BasePresenterImpl<ClassicClientContract.View> implements ClassicClientContract.Presenter {
    private Context mContext;
    //创建输入数据流
    private InputStream inputStream = null;
    //创建输出数据流
    private OutputStream outputStream = null;
    private static final String TAG = "ClassicClient";
    private ReceiveThread mReceiveThread = null;
    private BluetoothSocket bluetoothSocket = null;


    @Override
    public void startBluetooth(Context context) {
        this.mContext = context;
        bluetoothSocket = MyApplication.getInstance().getClientSocket();
        if (bluetoothSocket != null) {
            try {
                inputStream = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                if (mReceiveThread == null) {
                    mReceiveThread = new ReceiveThread();
                    mReceiveThread.start();
                }
            }
        }
    }

    /**
     * 接收线程
     */
    private class ReceiveThread extends Thread//继承Thread
    {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        @Override
        public void run()//重写run方法
        {
            while (!isInterrupted()) {
                SystemClock.sleep(10);
                try {
                    int bytesRead = 0;
                    try {
                        bytesRead = inputStream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bytesRead < 0 || bytesRead == 0) {
                        mReceiveThread.interrupt();
                        mReceiveThread = null;
                        mView.showStatus("服务器连接断开");
                        return;
                    }
                    byte[] arrayCopy = ByteUtils.arrayCopy(buffer, 0, bytesRead);
                    mView.showReceiveData(arrayCopy);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "mReceiveThread: " + e);
                }
            }
        }
    }

    @Override
    public void sendMsg(byte[] msg) {
        try {
            //获取输出流
            outputStream = bluetoothSocket.getOutputStream();
            //发送数据
            outputStream.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "sendMsg: " + e);
        }
    }

    @Override
    public void stopBluetooth() {
        try {
            if (mReceiveThread != null) {
                mReceiveThread.interrupt();
                mReceiveThread = null;
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
                MyApplication.getInstance().setClientSocket(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
