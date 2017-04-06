package com.imer.DB;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.imer.Bean.ChatMessage;
import com.imer.Bean.Invitation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * Created by 丶 on 2017/3/3.
 */

public class ImerDB {

    public static final String DB_NAME="Imer";

    private static final int VERSION=1;
    private static ImerDB imerDB;
    private static SQLiteDatabase db;
    private static SharedPreferences prefs;



    private ImerDB(Context context){
        MyDatabaseHelper databaseHelper=new MyDatabaseHelper(context,DB_NAME,null,VERSION);
        db=databaseHelper.getWritableDatabase();
    }

    /**
     * 获取imerDB的实例。
     */
    public synchronized static ImerDB getInstance(Context context) {
        prefs= PreferenceManager.getDefaultSharedPreferences(context);
        if (imerDB == null) {
            imerDB = new ImerDB(context);
        }
        return imerDB;
    }

    /**
     * 储存对话信息
     */
    public static void saveMessage(ChatMessage message){
        if (message != null) {
            ContentValues values = new ContentValues();
            values.put("username",message.getName());
            values.put("fromusername", message.getFromUserName());
            values.put("msg", message.getMsg());
            values.put("type",message.getType());
            values.put("date",message.getDate());
            db.insert("Message", null, values);
        }
    }

    /**
     * 储存好友请求信息
     */
    public static void saveInvitation(Invitation invitation){
        if (invitation != null) {
            ContentValues values = new ContentValues();
            values.put("username",invitation.getUserName());
            values.put("fromusername", invitation.getFromUserName());
            db.insert("Invitation", null, values);
        }
    }

    /**
     * 加载对话信息
     */
    public static List<ChatMessage> loadMessage(String fromusername,String currentTime){
        String username=prefs.getString("username","");
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        Log.i("TAG", "loadMessage: "+username+" "+fromusername);
        Cursor cursor = db.query("Message", new String[]{"username","fromusername","msg","type","date"}
                , "username=? and fromusername=? and date<?", new String[]{username,fromusername,currentTime}, null, null, "date desc,id","5");
        if (cursor.moveToLast())
            do {
//                Log.i("TAG", "loadMessage: ");
                ChatMessage message = new ChatMessage();
                message.setFromUserName(cursor.getString(cursor.getColumnIndex("fromusername")));
                message.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
                message.setType(cursor.getInt(cursor.getColumnIndex("type")));
                message.setDate(cursor.getString(cursor.getColumnIndex("date")));
                list.add(message);
            } while (cursor.moveToPrevious());

        return list;
    }

    /**
     * 加载好友请求信息
     */
    public static  List<Invitation> loadInvitation(String username){
        List<Invitation> list = new ArrayList<Invitation>();
        Cursor cursor = db.query("Invitation", new String[]{"fromusername"}, "username=?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst())
            do {
                Invitation invitation = new Invitation();
                invitation.setFromUserName(cursor.getString(cursor.getColumnIndex("fromusername")));
                list.add(invitation);
            } while (cursor.moveToNext());

        return list;
    }

}
