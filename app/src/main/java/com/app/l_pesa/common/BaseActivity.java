package com.app.l_pesa.common;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.l_pesa.R;


public abstract class BaseActivity extends AppCompatActivity implements  View.OnClickListener,FragmentDrawer.FragmentDrawerListener {

    public boolean isAddSlidingMenu = false;
   /* public static final int container = R.id.ll_fragment_container;*/
  //  public static final int container = R.id.container_body;
    private ImageView btn_menu,btn_back; private TextView tv_title;
    public static TextView tv_toolBar;
    public static TextView tv_credit_score_in_navigation;
    private DrawerLayout navigation_drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar tool_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationTheme();
    }
    private void setNavigationTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.colorAccent));
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    public void setTv_title(String title) {
        //this.tv_title.setText(title);
    }

    protected void setToolBar(DrawerLayout drawerLayout,Toolbar too_bar,View headerView) {
        this.tool_bar = too_bar;
        this.navigation_drawer = drawerLayout;
        setSupportActionBar(tool_bar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, navigation_drawer, tool_bar, R.string.app_name, R.string.app_name);
        /*{
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };*/
        actionBarDrawerToggle.syncState();
        navigation_drawer.addDrawerListener(actionBarDrawerToggle);



        tool_bar.setNavigationIcon(null);
        tool_bar.addView(headerView);
        //initHeader(getMenuId(),getBackBtnId(),getTitleTxtId());

        initDrawerMenuView();


    }

    public void hideToolbar(){
        tool_bar.setVisibility(View.GONE);
    }
    public void showToolBar(){
        tool_bar.setVisibility(View.VISIBLE);
    }

    protected abstract void initDrawerMenuView();

   /* protected abstract int getMenuId();
    protected abstract int getBackBtnId();
    protected abstract int getTitleTxtId();*/

    private void initHeader(int menu_id,int back_btn_id,int title_txt_id) {
       /* btn_menu = (ImageView) tool_bar.findViewById(menu_id);
        btn_back = (ImageView) tool_bar.findViewById(back_btn_id);
        tv_title = (TextView) tool_bar.findViewById(title_txt_id);
        tv_toolBar = (TextView) tool_bar.findViewById(R.id.tv_credit_score);
        tv_toolBar.setVisibility(View.GONE);
        tv_credit_score_in_navigation = (TextView) navigation_drawer.findViewById(R.id.tv_credit_score_in_navigation);
        if (btn_menu != null) {
            btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigation_drawer.openDrawer(Gravity.LEFT);
                }
            });
        }

        if (btn_back != null) {
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doBack();
                }
            });
        }*/

    }


    public DrawerLayout navigationDrawr()
    {
        return navigation_drawer;
    }


    public void doBack(){
        //commented for temporarily
        /*if(navigation_drawer.isDrawerOpen(Gravity.LEFT)){
            navigation_drawer.closeDrawer(Gravity.LEFT);
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() <= 1) {
                finish();
            } else {
                fragmentManager.popBackStack();
            }
        }*/
        //commented for temporarily
        finish();
    }

    public void startFragment(int fragment_container, Fragment fragment, boolean clearBackStack) {
        doStartFragment(fragment_container, fragment, clearBackStack);
    }

    public void startFragment(int fragment_container , Fragment fragment) {
        doStartFragment(fragment_container, fragment, false);
    }

    public void startFragment(Fragment fragment) {
        //doStartFragment(BaseActivity.container, fragment, false);
    }
    private void doStartFragment(int fragment_container, Fragment fragment, boolean clearBackStack) {

        if(navigation_drawer != null && navigation_drawer.isDrawerOpen(Gravity.START)){
            navigation_drawer.closeDrawer(Gravity.START);
        }
        if (clearBackStack)
        {
            clearBackStack();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //setCustomAnimations(int enter, int exit, int popEnter, int popExit)
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        String tag_name = fragment.getClass().getName();
        fragmentTransaction.replace(fragment_container, fragment, tag_name);
        fragmentTransaction.addToBackStack(tag_name);
       // fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
        //after transaction you must call the executePendingTransaction
        fragmentManager.executePendingTransactions();  //commented for temporarily

    }



    public void showBackButton() {
        showHideBack(true);
    }

    public void hideBackButton(){
        showHideBack(false);
    }

    public void showHideBack(boolean isShowBack) {
        if (isShowBack) {
            btn_back.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.INVISIBLE);
            navigation_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            navigation_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            btn_back.setVisibility(View.GONE);
            btn_menu.setVisibility(View.VISIBLE);
        }
    }


    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null) {
            super.onActivityResult(requestCode, resultCode, data);
        }
//        System.out.println("BASE_ACTIVITY " + requestCode + "  " + resultCode + "  " + data);
        FragmentManager manager = getSupportFragmentManager();
        int last_fragment_index = manager.getBackStackEntryCount() - 1;
        if(last_fragment_index > 0){
            FragmentManager.BackStackEntry last = manager.getBackStackEntryAt(last_fragment_index);
//            System.out.println("BASE_ACTIVITY index " + last_fragment_index);
//            System.out.println("BASE_ACTIVITY fragment " + last.getName());
            Fragment fragment =  manager.findFragmentByTag(last.getName());
//            System.out.println("BASE_ACTIVITY fragment " + fragment);
            if(fragment != null){
                if(data!=null)
                    fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }



    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
       // doBack();
        /*if(navigation_drawer.isDrawerOpen(Gravity.LEFT)){
            navigation_drawer.closeDrawer(Gravity.LEFT);
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() <= 1) {
                finish();
            } else {
                fragmentManager.popBackStack();
            }
        }*/

        finish();
    }
}

