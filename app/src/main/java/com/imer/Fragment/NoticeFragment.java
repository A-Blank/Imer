package com.imer.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imer.Activity.ChattingActivity;
import com.imer.Activity.FriendRequestActivity;
import com.imer.Activity.LoginActivity;
import com.imer.Adapter.NoticeListViewAdapter;
import com.imer.Bean.ChatMessage;
import com.imer.Bean.Invitation;
import com.imer.Bean.Notice;
import com.imer.Interface.FragmentCallBack;
import com.imer.R;
import com.imer.Utils.Utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by 丶 on 2017/3/9.
 */

public class NoticeFragment extends Fragment implements AdapterView.OnItemClickListener,FragmentCallBack{

    private ListView listView;
    private Set<String> Datas;
    private String fromUserName;
    private List<Notice> NoticeList;
    private NoticeListViewAdapter adapter;

    private SharedPreferences prefs;

    private Receiver receiver;

    private View view;
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.chat_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.lv_notice);
        adapter = new NoticeListViewAdapter(getContext(), NoticeList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitDatas();
        receiver = new Receiver();
        IntentFilter filter = new IntentFilter("com.imer.NoticeFragment");
        getContext().registerReceiver(receiver, filter);

    }

    public void InitDatas() {

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Datas = prefs.getStringSet("notice", null);
        if (Datas != null) {
            NoticeList = Utility.NoticeResultHandle(Datas);
        } else {
            NoticeList = new ArrayList<Notice>();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("TAG", "onAttach: ");
        String username=getActivity().getIntent().getStringExtra("username");
        String msg=getActivity().getIntent().getStringExtra("msg");
        if (msg == null) {
            return;
        }
        for (Notice n : NoticeList) {
            if (n.getTitle().equals(username)) {
                n.setContent(msg.replace("\n", ""));
                adapter.notifyDataSetChanged();
            }
            return;
        }
        Notice notice=new Notice();
        notice.setType("system");
        notice.setTitle("好友请求");
        notice.setContent(msg);
        NoticeList.add(notice);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        getContext().unregisterReceiver(receiver);
        Datas = Utility.NoticeHandle(NoticeList);
        editor.putStringSet("notice", Datas);
        editor.commit();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (NoticeList.get(position).getType().equals("system")) {
            fromUserName = NoticeList.get(position).getContent();
            builder = new AlertDialog.Builder(getContext());
            Dialog dialog = builder.create();
            dialog.show();
            builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContactManager.acceptInvitation(fromUserName.replace("请求添加你为好友", ""), "", new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Toast.makeText(getContext(), "添加好友成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent("com.imer.ContactFragment");
                                getContext().sendBroadcast(intent);
                            } else {
                                Toast.makeText(getContext(), "添加好友失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    NoticeList.remove(NoticeList.get(position));
                }
            });
            builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContactManager.declineInvitation(fromUserName.replace("请求添加你为好友", ""), "", "", new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {

                        }
                    });
                    NoticeList.remove(NoticeList.get(position));
                }
            });


        } else {
            Intent intent = new Intent(getContext(), ChattingActivity.class);
            intent.putExtra("toUserName", NoticeList.get(position).getTitle());
            startActivityForResult(intent,0);
        }
    }

    @Override
    public void CallBack(String username, String msg) {

        if (msg == null) {
            return;
        }
        for (Notice n : NoticeList) {
            if (n.getTitle().equals(username)) {
                n.setContent(msg.replace("\n", ""));
                adapter.notifyDataSetChanged();
            }
            return;
        }
        Notice notice=new Notice();
        notice.setType("user");
        notice.setTitle(username);
        notice.setContent(msg);
        NoticeList.add(notice);
        adapter.notifyDataSetChanged();
    }

    /**
     * 广播接收notice列表变化
     */
    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra("chatmessage");
            Invitation invitation = (Invitation) intent.getSerializableExtra("invitation");
            String id = intent.getStringExtra("id");
            for (Notice n : NoticeList) {
                if (n.getTitle().equals(id)) {
                    if (chatMessage != null) {
                        n.setContent(chatMessage.getMsg().replace("\n", ""));
                        adapter.notifyDataSetChanged();
                    }
                    return;
                }
            }
            Notice notice = new Notice();
            if (chatMessage != null) {
                notice.setType("user");
                notice.setTitle(chatMessage.getFromUserName());
                notice.setContent(chatMessage.getMsg().replace("\n", ""));
            } else {
                notice.setType("system");
                notice.setTitle("好友请求");
                notice.setContent(invitation.getFromUserName() + "请求添加你为好友");
            }
            NoticeList.add(notice);
            adapter.notifyDataSetChanged();
        }
    }


}
