package com.zm.zmbletool.ui.main;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.zm.utilslib.view.MovingView.MovingImageView;
import com.zm.utilslib.view.MovingView.MovingViewAnimator;
import com.zm.zmbletool.R;
import com.zm.zmbletool.mvp.MVPBaseActivity;
import com.zm.zmbletool.ui.classicclient.ClassicClientActivity;
import com.zm.zmbletool.ui.classicclient.ClassicScanActivity;
import com.zm.zmbletool.ui.classicclient.ClientSetUUIDActivity;
import com.zm.zmbletool.ui.classicservice.ClassicServiceActivity;
import com.zm.zmbletool.ui.classicservice.ServiceSetUUIDActivity;


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


}
