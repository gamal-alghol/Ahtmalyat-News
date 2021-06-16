package com.karkor.ahtmalyat.Model;

import androidx.annotation.Keep;

import java.util.Date;


@Keep
public class Msg {

    private String sender;
    private String message;
    private String messageReplay;
    private String senderReplay;
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private Date time;

    public String getSenderReplay() {
        return senderReplay;
    }

    public void setSenderReplay(String senderReplay) {
        this.senderReplay = senderReplay;
    }

    public Msg() {
    }

    public String getSender() {
        return sender;
    }

    public Msg(String sender, String message,String messageReplay,String senderReplay,String uri, Date time) {
        this.sender = sender;
        this.message = message;
        this.time=time;
        this.messageReplay=messageReplay;
        this.senderReplay=senderReplay;
        this.uri=uri;
    }




    public void setSender(String sender) {
        this.sender = sender;
    }



    public String getMessageReplay() {
        return messageReplay;
    }

    public void setMessageReplay(String messageReplay) {
        this.messageReplay = messageReplay;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
