//PS：在目前Android手机中，是不支持在飞行模式下开启蓝牙的。如果蓝牙已经开启，那么蓝牙的开关状态会随着飞行模式的状态而发生改变
package com.zm.zmbletool.ui.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.zm.utilslib.base.BaseActivity;
import com.zm.utilslib.utils.SharedXmlUtil;
import com.zm.zmbletool.MyApplication;
import com.zm.zmbletool.R;
import com.zm.zmbletool.adapter.RVAdapter;
import com.zm.zmbletool.bean.RVBean;
import com.zm.zmbletool.ui.classicclient.ClassicClientActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.reginer.baseadapter.CommonRvAdapter;

@SuppressLint("NewApi")
public class BleScanActivity extends BaseActivity implements CommonRvAdapter.OnItemClickListener {
    private Toolbar mToolbar;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private RVAdapter mAdapter;
    private List<RVBean> mList;
    private LinearLayoutManager layoutManager;
    private static final int REQUEST_ENABLE_BT = 1;
    private RecyclerView rv_content;
    // 10秒后停止查找搜索.
    private static final long SCAN_PERIOD = 10000;
    private Handler mHandler;
    private BluetoothLeScanner mBluetoothLeScanner;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_device;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mHandler = new Handler();
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("搜索");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleScanActivity.this.finish();
            }
        });

        rv_content = findViewById(R.id.rv_content);
        if (checkBle()) {
            return;
        }
        mList = new ArrayList<RVBean>();
        initRV();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        scanLeDevice(true);
    }

    private boolean checkBle() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "你的手机不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();


        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "你的手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    showToast("自Android 6.0开始需要打开位置权限才可以搜索到Ble设备");
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        }
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner==null){
            return true;
        }
        return false;
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {

    }

    private void initRV() {
        mAdapter = new RVAdapter(BleScanActivity.this, R.layout.item_info, mList);
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
                mList.clear();
                scanLeDevice(true);
                Log.d("ZM", "onOptionsItemSelected: scan");
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                Log.d("ZM", "onOptionsItemSelected: stop");
                break;
            default:
                break;
        }
        return true;
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if (mBluetoothLeScanner != null) {
                        mBluetoothLeScanner.stopScan(mScanCallback);
                    }
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            if (mBluetoothLeScanner != null) {
                mBluetoothLeScanner.startScan(mScanCallback);
            }

        } else {
            mScanning = false;
            if (mBluetoothLeScanner != null) {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }
        //更新menu
        invalidateOptionsMenu();
    }

    // 5.0+.返蓝牙信息更新到界面
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            int rssi = result.getRssi();
            BluetoothDevice bluetoothDevice = result.getDevice();
            for (RVBean rvBean : mList) {
                String address = rvBean.getAddress();
                if (address.equals(bluetoothDevice.getAddress())) {
                    return;
                }
            }
            RVBean rvBean = new RVBean();
            rvBean.setName(bluetoothDevice.getName());
            rvBean.setAddress(bluetoothDevice.getAddress());
            rvBean.setRssi(String.valueOf(rssi));
            rvBean.setBluetoothDevice(bluetoothDevice);
            mList.add(rvBean);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        scanLeDevice(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, final int position) {
        final BluetoothDevice bluetoothDevice = mList.get(position).getBluetoothDevice();
        final KProgressHUD kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        scanLeDevice(false);

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                if (bluetoothDevice == null) {
                    e.onError(new Throwable(""));
                } else {
//                    MyApplication.getInstance().setBluetoothDevice(bluetoothDevice);
                    e.onComplete();
                }
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
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
                        Toast.makeText(BleScanActivity.this, "连接失败" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        kProgressHUD.dismiss();
                        d.dispose();
                    }

                    @Override
                    public void onComplete() {
                        kProgressHUD.dismiss();
                        final Intent intent = new Intent(BleScanActivity.this, BleActivity.class);
                        intent.putExtra(BleActivity.EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
                        intent.putExtra(BleActivity.EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());
                        startActivity(intent);
                        BleScanActivity.this.finish();
                        d.dispose();
                    }
                });

    }


}