package com.zm.zmbletool.ui.classicservice;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.zm.utilslib.bean.MsgEvent;
import com.zm.utilslib.utils.SharedXmlUtil;
import com.zm.utilslib.utils.data.ByteUtils;
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
    private Context mContext;

    @Override
    public void startBluetooth(Context context) {
        this.mContext = context;
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
            String uuidStr = SharedXmlUtil.getInstance(mContext)
                    .read("SERVICE_UUID", "00001101-0000-1000-8000-00805F9B34FB");
            UUID uuid = UUID.fromString(uuidStr);
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
                    if (btServer != null) {
                        transferSocket = btServer.accept();
                    }
                    inputstream = transferSocket.getInputStream();
                    BluetoothDevice device = transferSocket.getRemoteDevice();
                    mView.showStatus(device.getAddress() + "连接成功");
                    if (mReceiveThread == null) {
                        mReceiveThread = new ReceiveThread();
                        mReceiveThread.start();
                        Log.d(TAG, "mReceiveThreadStart ");
                    } else {
                        mReceiveThread.interrupt();
                        mReceiveThread = null;
                        mReceiveThread = new ReceiveThread();
                        mReceiveThread.start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ServerSocketThread: IOException");
//                    mView.showStatus("连接断开");
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
                        bytesRead = inputstream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bytesRead < 0 || bytesRead == 0) {
                        mReceiveThread.interrupt();
                        mReceiveThread = null;
                        mView.showStatus("连接断开");
                        return;
                    }
                    byte[] arrayCopy = ByteUtils.arrayCopy(buffer, 0, bytesRead);
                    mView.showReceiveData(arrayCopy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            defaultAdapter = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
