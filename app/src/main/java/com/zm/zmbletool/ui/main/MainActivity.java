package com.zm.zmbletool.ui.main;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zm.utilslib.view.MovingView.MovingImageView;
import com.zm.utilslib.view.MovingView.MovingViewAnimator;
import com.zm.zmbletool.R;
import com.zm.zmbletool.mvp.MVPBaseActivity;
import com.zm.zmbletool.ui.about.AboutActivity;
import com.zm.zmbletool.ui.ble.BleScanActivity;
import com.zm.zmbletool.ui.classicclient.ClientSetUUIDActivity;
import com.zm.zmbletool.ui.classicservice.ServiceSetUUIDActivity;

import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter>
        implements MainContract.View {

    private Toolbar mToolbar;
    private AppBarLayout mAppBar;
    private NavigationView mNvMenu;
    private DrawerLayout mDlRoot;
    private LinearLayout mLlClassicClient;
    private LinearLayout mLlClassicService;
    private LinearLayout mLlLow;
    private MovingImageView mivMenu;

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mAppBar = findViewById(R.id.app_bar);
        mDlRoot = findViewById(R.id.dl_root);
        mNvMenu = findViewById(R.id.nv_menu);
        mToolbar = findViewById(R.id.toolbar);
        mLlClassicClient = findViewById(R.id.ll_classic_client);
        mLlClassicClient.setOnClickListener(this);
        mLlClassicService = findViewById(R.id.ll_classic_service);
        mLlClassicService.setOnClickListener(this);
        mLlLow = findViewById(R.id.ll_low);
        mLlLow.setOnClickListener(this);

        //图片移动view移动
        mivMenu = mNvMenu.getHeaderView(0).findViewById(R.id.miv_menu);
        mDlRoot.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mivMenu.pauseMoving();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (mivMenu.getMovingState() == MovingViewAnimator.MovingState.stop) {
                    mivMenu.startMoving();
                } else if (mivMenu.getMovingState() == MovingViewAnimator.MovingState.pause) {
                    mivMenu.resumeMoving();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mivMenu.stopMoving();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (mivMenu.getMovingState() == MovingViewAnimator.MovingState.stop) {
                    mivMenu.startMoving();
                } else if (mivMenu.getMovingState() == MovingViewAnimator.MovingState.pause) {
                    mivMenu.resumeMoving();
                }
            }
        });


        //监听toolbar点击事件开发侧栏
        mToolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpen();
            }
        });


        mNvMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.group_item_share_project:
                        break;
                    case R.id.item_about:
                        openAct(getApplicationContext(), AboutActivity.class);
                        break;
                    default:
                        break;
                }
                item.setCheckable(false);
                mDlRoot.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.ll_classic_client:
                openAct(this, ClientSetUUIDActivity.class);
                break;
            case R.id.ll_classic_service:
                openAct(this, ServiceSetUUIDActivity.class);
                break;
            case R.id.ll_low:
                openAct(this, BleScanActivity.class);
                break;
            default:
                break;
        }
    }


    private void onOpen() {
        if (!mDlRoot.isDrawerOpen(GravityCompat.START)) {
            mDlRoot.openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(getApplicationContext()).onAppStart();
        permission();
        MobclickAgent.setDebugMode(true);
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

    private void permission() {
        AndPermission.with(MainActivity.this)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(listener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    }
                }).start();
    }

    PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                AndPermission.defaultSettingDialog(MainActivity.this, 300).show();
            }
        }
    };

}
