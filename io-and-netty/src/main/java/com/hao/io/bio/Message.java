package com.hao.io.bio;

import java.io.Serializable;

// 通信消息，序列化
public class Message implements Serializable {

    private String content;

    public Message() {
    }

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
