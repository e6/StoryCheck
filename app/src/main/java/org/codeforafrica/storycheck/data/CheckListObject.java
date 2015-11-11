package org.codeforafrica.storycheck.data;

import android.content.Context;

/**
 * Created by nickhargreaves on 11/11/15.
 */
public class CheckListObject {
    
    private String title;
    private String remote_id;
    private int count;
    private String id;

    private Context mContext;


    public CheckListObject(Context ctx){
        this.mContext = ctx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(String remote_id) {
        this.remote_id = remote_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
