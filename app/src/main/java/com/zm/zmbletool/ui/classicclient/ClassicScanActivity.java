//PS：在目前Android手机中，是不支持在飞行模式下开启蓝牙的。如果蓝牙已经开启，那么蓝牙的开关状态会随着飞行模式的状态而发生改变
package com.zm.zmbletool.ui.classicclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.umeng.analytics.MobclickAgent;
import com.zm.utilslib.base.BaseActivity;
import com.zm.utilslib.bean.MsgEvent;
import com.zm.utilslib.utils.SharedXmlUtil;
import com.zm.zmbletool.MyApplication;
import com.zm.zmbletool.R;
import com.zm.zmbletool.adapter.RVAdapter;
import com.zm.zmbletool.bean.RVBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.reginer.baseadapter.CommonRvAdapter;

public class ClassicScanActivity extends BaseActivity implements CommonRvAdapter.OnItemClickListener {
    private Toolbar mToolbar;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private RVAdapter mAdapter;
    private List<RVBean> mList = new ArrayList<RVBean>();
    private LinearLayoutManager layoutManager;
    private static final int REQUEST_ENABLE_BT = 1;
    private RecyclerView rv_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_device;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("搜索");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassicScanActivity.this.finish();
            }
        });

        rv_content = findViewById(R.id.rv_content);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        initRV();
        //开始搜索
        startDiscovery();
//        addBoundDevice(mBluetoothAdapter);
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {

    }

    private void initRV() {
        mAdapter = new RVAdapter(ClassicScanActivity.this, R.layout.item_info, mList);
        rv_content.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        layoutManager = new LinearLayoutManager(this);
        rv_content.setLayoutManager(layoutManager);
        rv_content.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //开始搜索
                startDiscovery();
                Log.d("ZM", "onOptionsItemSelected: scan");
                break;
            case R.id.menu_stop:
                stopDiscovery();
                Log.d("ZM", "onOptionsItemSelected: stop");
                break;
            default:
                break;
        }
        return true;
    }

    //开始扫描
    private void startDiscovery() {
        // TODO Auto-generated method stub
        mList.clear();
        registerReceiver(discoveryReceiver, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));
        boolean startDiscovery = mBluetoothAdapter.startDiscovery();
        if (startDiscovery) {
            mScanning = true;
        } else {
            mScanning = false;
        }
        invalidateOptionsMenu();
    }

    //停止扫描
    private void stopDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
        mScanning = false;
        //刷新Menu
        invalidateOptionsMenu();
    }

    BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            RVBean rvBean = new RVBean();
            BluetoothDevice bluetoothDevice = arg1
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String uuids = String.valueOf(bluetoothDevice.getUuids());
            Bundle extras = arg1.getExtras();
            if (extras != null) {
                short rssi = extras.getShort(BluetoothDevice.EXTRA_RSSI);
                rvBean.setRssi(rssi + "");
            }
            rvBean.setBluetoothDevice(bluetoothDevice);
            rvBean.setName(arg1.getStringExtra(BluetoothDevice.EXTRA_NAME));
            rvBean.setAddress(bluetoothDevice.getAddress());
            mList.add(rvBean);
            mAdapter.notifyDataSetChanged();
        }
    };

    //得到已绑定的设备
    private void addBoundDevice(BluetoothAdapter bAdapter) {
        Set<BluetoothDevice> set = bAdapter.getBondedDevices();
        Log.d("test", "set:" + set.size());
        for (BluetoothDevice device : set) {
            RVBean rvBean = new RVBean();
            rvBean.setName(device.getName());
            rvBean.setAddress(device.getAddress());
            rvBean.setRssi("已匹配");
            rvBean.setBluetoothDevice(device);
            mList.add(rvBean);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }else {
            //开始搜索
            startDiscovery();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
        final BluetoothDevice bluetoothDevice = mList.get(position).getBluetoothDevice();
        final KProgressHUD kProgressHUD=KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        stopDiscovery();

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                String uuid = SharedXmlUtil.getInstance(getApplicationContext())
                        .read("UUID", "00001101-0000-1000-8000-00805F9B34FB");
                BluetoothSocket clientSocket = bluetoothDevice
                        .createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                clientSocket.connect();
                MyApplication.getInstance().setClientSocket(clientSocket);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    private Disposable d;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                    }

                    @Override
                    public void onNext(Object value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ClassicScanActivity.this, "连接失败" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("zm_log", "客户端连接蓝牙onError: " + e.toString());
                        kProgressHUD.dismiss();
                        d.dispose();
                    }

                    @Override
                    public void onComplete() {
                        kProgressHUD.dismiss();
                        openAct(getApplicationContext(), ClassicClientActivity.class);
                        ClassicScanActivity.this.finish();
                        d.dispose();
                    }
                });

    }


}