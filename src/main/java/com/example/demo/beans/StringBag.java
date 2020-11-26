package com.example.demo.beans;

import java.beans.JavaBean;

/**
 * 文件的实体类，用于建立索引，包含标题和内容
 *@author dhx
 * */
@JavaBean
public class StringBag {
    private String content,title;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title=title;
    }

}

