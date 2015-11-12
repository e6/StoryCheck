package org.codeforafrica.storycheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class CheckListObject {
    
    private String title;
    private String remote_id;
    private int count;
    private String id;

    private Context mContext;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public CheckListObject(Context ctx){
        this.mContext = ctx;
        dbHelper = new DBHelper(mContext);
        db = dbHelper.getWritableDatabase();

    }

    public CheckListObject(){

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

    //Database operations//

    /**
     * function to add new checklist to database
     * @return int id of inserted checklist
     */
    public long commit(){

        long insertId = isAdded();

        if(insertId > 0){
            //update
            Log.d("insert log", "update" + insertId);
            update();
        }else{
            //insert
            insertId = insertNew();
            Log.d("insert log", "insert" + insertId);
        }

        db.close();

        return insertId;

    }

    /**
     * Update Checklist details
     */
    public void update(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.COLUMN_CHECKLIST_TITLE, title);
        contentValues.put(DBHelper.COLUMN_CHECKLIST_COUNT, count);

        db.update(DBHelper.TABLE_CHECKLISTS, contentValues, DBHelper.COLUMN_CHECKLIST_REMOTE_ID + "=" + remote_id, null);

    }

    /**
     * insert new checklist
     * @return
     */
    public long insertNew(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_CHECKLIST_TITLE, title);
        contentValues.put(DBHelper.COLUMN_CHECKLIST_COUNT, count);
        contentValues.put(DBHelper.COLUMN_CHECKLIST_REMOTE_ID, remote_id);

        return db.insert(DBHelper.TABLE_CHECKLISTS, null, contentValues);
    }

    /**
     * If already added return id
     * @return long
     */
    public long isAdded(){
        String select_string =  "select count(*) from "+DBHelper.TABLE_CHECKLISTS+" where " +DBHelper.COLUMN_CHECKLIST_REMOTE_ID+ "='" + remote_id + "'; ";

        SQLiteStatement s = db.compileStatement(select_string );

        return s.simpleQueryForLong();
    }

    public void setId(String id) {
        this.id = id;
    }
}
