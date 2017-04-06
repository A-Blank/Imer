package com.imer.Utils;

import com.imer.Bean.Notice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 丶 on 2017/3/9.
 */

public class Utility {

    public static List<Notice> NoticeResultHandle(Set<String> list){

        List<Notice> noticeList=new ArrayList<>();
        for (String str: list) {
            String []result= str.split(";",3);
            Notice notice=new Notice();
            notice.setType(result[0]);
            notice.setTitle(result[1]);
            notice.setContent(result[2]);
            noticeList.add(notice);
        }
        return noticeList;

    }

    public static Set<String> NoticeHandle(List<Notice> list){

        Set<String> set=new HashSet<>();
        for (Notice notice : list) {
            String str=notice.getType()+";"+notice.getTitle()+";"+notice.getContent();
            set.add(str);
        }
        return set;

    }

}
