package com.imer.Service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imer.Activity.ChattingActivity;
import com.imer.Activity.FriendRequestActivity;
import com.imer.Bean.ChatMessage;
import com.imer.Bean.Invitation;
import com.imer.DB.ImerDB;
import com.imer.DB.MyDatabaseHelper;
import com.imer.Entity.Contact;
import com.imer.Utils.MyApplication;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by 丶 on 2017/2/22.
 */

/**
 * 用于处理消息的服务
 */

public class JMService extends Service {

    private ChattingActivity MsgReceiver;
    private FriendRequestActivity RequestReceiver;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ImerDB imerDB;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        imerDB=ImerDB.getInstance(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor=PreferenceManager.getDefaultSharedPreferences(this).edit();
        JMessageClient.registerEventReceiver(this);


        Log.i("TAG","Service Create");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TAG","Service Start");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

        JMessageClient.unRegisterEventReceiver(this);

    }

    public void onEvent(MessageEvent event){

        String username=prefs.getString("username","");
        Message message=event.getMessage();
        String fromname=message.getFromName();
        String msg=null;
        String date= String.valueOf(message.getCreateTime());
//        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//        Date date=new Date(event.getMessage().getCreateTime());
//        Log.i("TAG", dateFormat.format(date)+"   1111");
        switch ((message.getContentType()))
        {
            case text:
                TextContent textContent= (TextContent) message.getContent();
                msg=textContent.getText();
                break;
        }
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setName(username);
        chatMessage.setFromUserName(fromname);
        chatMessage.setMsg(msg);
        chatMessage.setType(0);
        chatMessage.setDate(date);
        imerDB.saveMessage(chatMessage);
        /**
         * 发送广播
         */
        Intent intent=new Intent();
        intent.setAction("com.imer.ChattingActivity");
        intent.putExtra("chatmessage",(Serializable)chatMessage);
        intent.putExtra("id",fromname);
        sendBroadcast(intent);
        intent.setAction("com.imer.NoticeFragment");
        sendBroadcast(intent);
//        Log.i("TAG","消息接收成功:"+msg);

    }

    public void onEvent(ContactNotifyEvent event){
        String username=prefs.getString("username","");
        String fromUsername = event.getFromUsername();
        Invitation invitation=new Invitation();
        invitation.setUserName(username);
        invitation.setFromUserName(fromUsername);

        switch (event.getType()) {
            case invite_received://收到好友邀请
                imerDB.saveInvitation(invitation);
                Intent intent=new Intent();
                intent.setAction("com.imer.NoticeFragment");
                intent.putExtra("invitation", (Serializable) invitation);
                intent.putExtra("id",fromUsername);
                sendBroadcast(intent);
                break;
            case invite_accepted://对方接收了你的好友邀请
                //...
                break;
            case invite_declined://对方拒绝了你的好友邀请
                //...
                break;
            case contact_deleted://对方将你从好友中删除
                //...
                break;
            default:
                break;
        }

    }


}
