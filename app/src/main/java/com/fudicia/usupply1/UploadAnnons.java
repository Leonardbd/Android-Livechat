package com.fudicia.usupply1;

import android.net.Uri;

import com.firebase.ui.auth.data.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;

public class UploadAnnons {

private String chatId;
private String program;
private String channel;
private String time;
private String chatters;
private String creator;

private String image_path;
private String mImageUrl;


    public UploadAnnons()
    { //empty constructor needed don't delete
    }

    public UploadAnnons(String Id, String Program, String Kanal, String Tid, String Chatters, String Url, String Creator)
    {
        chatId = Id;
        program = Program;
        channel = Kanal;
        time = Tid;
        chatters = Chatters;
        mImageUrl = Url;
        creator = Creator;
    }

    public String getChatId(){
        return chatId;
    }

    public String getProgram()
    {
        return program;
    }

    public String getTime(){
        return time;
    }

    public String getChannel() {return channel;}

    public String getChatters() {return chatters;}

    public String getUrl()
    {
        return mImageUrl;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setProgram(String program2)
    {
        program = program2;
    }

    public void setChatters(String chatters2){chatters = chatters2;}

    public void setTime(String time2)
    {
        time = time2;
    }

    public void setUrl(String mImageUrl2)
    {
        mImageUrl = mImageUrl2;
    }

    public void setCreator(String creator2)
    {
        creator = creator2;
    }

}
class AdChattersComparator implements Comparator<UploadAnnons> {

    public int compare(UploadAnnons chat1, UploadAnnons chat2){
        int chatters1 = Integer.valueOf(chat1.getChatters());
        int chatters2 = Integer.valueOf(chat2.getChatters());

        return chatters2 - chatters1;
    }

}
