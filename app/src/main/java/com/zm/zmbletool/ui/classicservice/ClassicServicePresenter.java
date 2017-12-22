package com.zm.zmbletool.ui.classicservice;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.zm.utilslib.bean.MsgEvent;
import com.zm.zmbletool.mvp.BasePresenterImpl;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ClassicServicePresenter extends BasePresenterImpl<ClassicServiceContract.View> implements ClassicServiceContract.Presenter {

    private BluetoothAdapter defaultAdapter;
    private ServerSocketThread mServerSocketThread = null;
    private ReceiveThread mReceiveThread = null;
    private BluetoothServerSocket btServer = null;
    private BluetoothSocket transferSocket = null;
    //创建输入数据流
    private InputStream inputstream = null;
    //创建输出数据流
    private OutputStream outputStream = null;
    private static final String TAG = "ClassicService";

    @Override
    public void startBluetooth() {
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            if (!defaultAdapter.isEnabled()) {
                defaultAdapter.enable();
            }

            //启动服务器监听线程
            synchronized (this) {
                if (mServerSocketThread == null) {
                    mServerSocketThread = new ServerSocketThread();
                    mServerSocketThread.start();
                }
            }

        } else {
            mView.showStatus("该设备不支持蓝牙");
        }
    }


    /**
     * 服务器监听线程
     */
    private class ServerSocketThread extends Thread {

        @Override
        @SuppressLint("MissingPermission")
        public void run()//重写Thread的run方法
        {
            UUID uuid = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
            String name = "bluetoothServer";
            try {
                btServer = defaultAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!interrupted()) {
                try {
                    SystemClock.sleep(10);
                    //监听连接 ，如果无连接就会处于阻塞状态，一直在这等着
                    transferSocket = btServer.accept();
                    mView.showStatus("连接成功\n");
                    inputstream = transferSocket.getInputStream();
                    if (mReceiveThread == null) {
                        mReceiveThread = new ReceiveThread();
                        mReceiveThread.start();
                        Log.d(TAG, "mReceiveThreadStart ");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ServerSocketThread: IOException");
                    mView.showStatus("连接断开\n");
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
                int bytesRead = 0;
                try {
                    bytesRead = inputstream.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bytesRead < 0) {
                    mReceiveThread.interrupt();
                    mReceiveThread = null;
                    mView.showStatus("连接断开\n");
                    continue;
                }
                mView.showReceiveData(buffer);
            }
        }
    }


    //发送信息
    @Override
    public void sendMsg(byte[] msg) {
        try {
            //获取输出流
            outputStream = transferSocket.getOutputStream();
            //发送数据
            outputStream.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopBluetooth() {
        try {
            defaultAdapter=null;
            if (mServerSocketThread != null) {
                mServerSocketThread.interrupt();
                mServerSocketThread = null;
            }
            if (mReceiveThread != null) {
                mReceiveThread.interrupt();
                mReceiveThread = null;
            }
            if (inputstream != null) {
                inputstream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (transferSocket != null) {
                transferSocket.close();
                transferSocket = null;
            }
            if (btServer != null) {
                btServer.close();
                btServer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
