package com.imer.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.imer.Adapter.MessageListViewAdapter;
import com.imer.Bean.ChatMessage;
import com.imer.DB.ImerDB;
import com.imer.DB.MyDatabaseHelper;
import com.imer.Interface.ListViewCallBack;
import com.imer.R;
import com.imer.Utils.ActivityController;
import com.imer.View.MyListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;


/**
 * Created by 丶 on 2017/2/22.
 */

public class ChattingActivity extends Activity implements View.OnClickListener, ListViewCallBack {

    private MyListView mListView;
    private EditText Edt_msg;
    private Button Btn_send;
    private MessageListViewAdapter mAdapter;
    private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    private String objectId;
    private String msg;
    private String username;
    private String toUserName;
    private MyDatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private ImerDB imerDB;
    private Receiver receiver = new Receiver();
    private Date currentTime;
    private String current_msg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(R.layout.chatiing_page);
        toUserName = getIntent().getStringExtra("toUserName");
        Init();


    }

    public void Init() {

        imerDB = ImerDB.getInstance(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        username = prefs.getString("username", "");
        currentTime = new Date();
        String date = String.valueOf(currentTime.getTime());
        mDatas = imerDB.loadMessage(toUserName, date);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.imer.ChattingActivity");
        this.registerReceiver(receiver, filter);

        mListView = (MyListView) findViewById(R.id.lv_msg);

        /**
         * 设置ListView自动滚动到最后一条item
         */
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        /**
         * // 设置ListView滑动条不可见
         */
        mListView.setVerticalScrollBarEnabled(false);


        mAdapter = new MessageListViewAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);

        Edt_msg = (EditText) findViewById(R.id.edt_msg);

        Btn_send = (Button) findViewById(R.id.btn_send);
        Btn_send.setOnClickListener(this);


    }


    public void SendMessage() {

        msg = Edt_msg.getText() + "";
        Message message = JMessageClient.createSingleTextMessage(toUserName, msg);
        JMessageClient.sendMessage(message);
        Edt_msg.setText("");
        current_msg = msg;

        String date = String.valueOf(new Date().getTime());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setName(username);
        chatMessage.setFromUserName(toUserName);
        chatMessage.setMsg(msg);
        chatMessage.setType(1);
        chatMessage.setDate(date);
        imerDB.saveMessage(chatMessage);

        mDatas.add(chatMessage);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send:
                SendMessage();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("msg",current_msg);
        intent.putExtra("username",toUserName);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        ActivityController.removeActivity(this);
    }

    @Override
    public void Refresh() {

        String date = mDatas.get(0).getDate();
        int count = mDatas.size();
        mDatas.addAll(0, imerDB.loadMessage(toUserName, date));
        mAdapter.notifyDataSetChanged();
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mDatas.size() - count);
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityController.isActivityInStack(ChattingActivity.this)) {
                mDatas.add((ChatMessage) intent.getSerializableExtra("chatmessage"));
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
