package com.zm.zmbletool.ui.main;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zm.zmbletool.R;
import com.zm.zmbletool.mvp.MVPBaseActivity;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View {

    private Toolbar mToolbar;
    private AppBarLayout mAppBar;
    private NavigationView mNvMenu;
    private DrawerLayout mDlRoot;

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

        mToolbar.setTitle("首页");
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

    }


    private void onOpen() {
        if (!mDlRoot.isDrawerOpen(GravityCompat.START)) {
            mDlRoot.openDrawer(GravityCompat.START);
        }
    }
}
