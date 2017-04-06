package com.imer.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.imer.Activity.ChattingActivity;
import com.imer.Adapter.ContactListViewAdapter;
import com.imer.Bean.Notice;
import com.imer.Entity.Contact;
import com.imer.Interface.FragmentCallBack;
import com.imer.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by 丶 on 2017/3/9.
 */

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<Contact> Datas=new ArrayList<Contact>();
    private ContactListViewAdapter mAdapter;
    private ListView listView;
    private Button Btn_search;
    private View view;
    private Recevier receiver;
    //接口回掉
    private FragmentCallBack callBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       view= inflater.inflate(R.layout.contact_fragment,container,false);

        listView= (ListView) view.findViewById(R.id.lv_contactname);

        mAdapter=new ContactListViewAdapter(getContext(),Datas);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitDatas();
        receiver=new Recevier();
        IntentFilter filter=new IntentFilter("com.imer.ContactFragment");
        getContext().registerReceiver(receiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    public void InitDatas(){
        getFriendList();
    }

    public void getFriendList(){

        Datas.clear();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {

                if(i==0){

                    for(int num=0;num<list.size();num++){
                        Contact contact=new Contact();
                        String name=list.get(num).getUserName();
                        String id=String.valueOf(list.get(num).getUserID());
                        contact.setUserId(id);
                        contact.setUserName(name);
                        Datas.add(contact);
                    }
                    mAdapter.notifyDataSetChanged();

                }
                else{
                    Toast.makeText(getContext(),"获取好友列表失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent=new Intent();
        intent.setClass(getContext(),ChattingActivity.class);
        intent.putExtra("toUserName",Datas.get(position).getUserName());
        startActivityForResult(intent,0);

    }

    class Recevier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            getFriendList();
        }
    }


}
