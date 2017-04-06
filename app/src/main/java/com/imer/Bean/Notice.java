package com.imer.Bean;

/**
 * Created by 丶 on 2017/3/9.
 */

public class Notice {
    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    private String Type;
    private String Title;
    private String content;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
