package com.imer.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imer.Adapter.FragmentAdapter;
import com.imer.Fragment.NoticeFragment;
import com.imer.Fragment.ContactFragment;
import com.imer.Interface.FragmentCallBack;
import com.imer.Service.JMService;
import com.imer.R;
import com.imer.Utils.ActivityController;
import com.imer.View.TitanicTextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {


    private Button Btn_search;
    private Button Btn_refresh;
    private Button Btn_setting;
    private TextView textView_username;
    /**
     * 注销按钮
     */
    private Button Btn_logout;
    private Intent intent = new Intent();
    //Fragment列表
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private NoticeFragment chatFragment;
    private ContactFragment contactFragment;

    private ViewPager viewPager;
    private FragmentAdapter adapter;
    //底部标签栏
    private TextView textView_msg;
    private TextView textView_contact;
    private SharedPreferences prefs;
    //侧滑栏
    private DrawerLayout drawerLayout;
    //开源框架TextView
    private TitanicTextView titanicTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent();
        ActivityController.addActivity(this);
        setContentView(R.layout.activity_main);
        Init();


    }

    public void Init() {


        Intent intent = new Intent();
        intent.setClass(this, JMService.class);
        startService(intent);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "");

        titanicTextView= (TitanicTextView) findViewById(R.id.titanic_tv);
        Titanic titanic=new Titanic();
        titanic.start(titanicTextView);

        textView_username = (TextView) findViewById(R.id.tv_username);
        textView_username.setText(username);

        Btn_search = (Button) findViewById(R.id.btn_search);
        Btn_search.setOnClickListener(this);

        Btn_setting = (Button) findViewById(R.id.btn_setting);
        Btn_setting.setOnClickListener(this);

        Btn_logout = (Button) findViewById(R.id.btn_logout);
        Btn_logout.setOnClickListener(this);

        textView_msg = (TextView) findViewById(R.id.tv_title_msg);
        textView_msg.setOnClickListener(this);
        textView_contact = (TextView) findViewById(R.id.tv_title_contact);
        textView_contact.setOnClickListener(this);
        textView_msg.setTextColor(Color.GREEN);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);

        //添加fragment
        chatFragment = new NoticeFragment();
        contactFragment = new ContactFragment();
        fragmentList.add(chatFragment);
        fragmentList.add(contactFragment);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOnPageChangeListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, fragmentList);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_search:
                intent.setClass(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_setting:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.tv_title_msg:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_title_contact:
                viewPager.setCurrentItem(1);
                break;
            case R.id.btn_logout:
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putBoolean("islogin", false);
                editor.clear();
                editor.commit();
                Intent serviceIntent = new Intent(this, JMService.class);
                stopService(serviceIntent);
                Intent intent = new Intent();
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("TAG", "onActivityResult: ");
        String username = data.getStringExtra("username");
        String msg = data.getStringExtra("msg");
        ((FragmentCallBack) chatFragment).CallBack(username, msg);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (position == 0) {
            textView_msg.setTextColor(Color.GREEN);
            textView_contact.setTextColor(Color.BLACK);
        } else {
            textView_contact.setTextColor(Color.GREEN);
            textView_msg.setTextColor(Color.BLACK);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
