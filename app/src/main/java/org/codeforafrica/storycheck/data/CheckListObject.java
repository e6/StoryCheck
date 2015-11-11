package org.codeforafrica.storycheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

    /**
     * Database operations
     */

    /**
     * function to add new checklist to database
     * @return int id of inserted checklist
     */
    public long commit(){

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_CHECKLIST_TITLE, title);
        contentValues.put(DBHelper.COLUMN_CHECKLIST_COUNT, count);
        contentValues.put(DBHelper.COLUMN_CHECKLIST_REMOTE_ID, remote_id);

        return db.insert(DBHelper.TABLE_CHECKLISTS, null, contentValues);
    }
}
