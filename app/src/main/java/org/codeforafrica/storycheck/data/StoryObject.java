package org.codeforafrica.storycheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by nickhargreaves on 11/11/15.
 */
public class StoryObject {

    private String title;
    private String description;
    private String checklist;
    private long id;

    private int checklist_count;
    private int checklist_count_filled;
    
    private String stories_table = DBHelper.TABLE_STORIES;

    private Context mContext;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public StoryObject(Context ctx, long _id){
        this.mContext = ctx;
        dbHelper = new DBHelper(mContext);
        db = dbHelper.getWritableDatabase();
        id = _id;
    }

    public StoryObject(long _id){
        id = _id;
    }
    //Database operations//

    /**
     * function to add new question to database
     * @return int id of inserted question
     */
    public long commit(){

        long insertId = -1;

        if(!(id > 0) ){
            insertId = insertNew();
        }else{
            insertId = update();
        }

        db.close();

        return insertId;

    }

    /**
     * Update question details
     */
    public long update(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.COLUMN_STORY_TITLE, title);
        contentValues.put(DBHelper.COLUMN_STORY_DESCRIPTION, description);
        contentValues.put(DBHelper.COLUMN_STORY_CHECKLIST_COUNT, checklist_count);
        contentValues.put(DBHelper.COLUMN_STORY_CHECKLIST_COUNT_FILLED, checklist_count_filled);
        //contentValues.put(DBHelper.COLUMN_STORY_CHECKLIST, checklist);

        return db.update(stories_table, contentValues, DBHelper.COLUMN_STORY_ID + "=" + id, null);

    }

    /**
     * insert new question
     * @return
     */
    public long insertNew(){
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.COLUMN_STORY_TITLE, title);
        contentValues.put(DBHelper.COLUMN_STORY_DESCRIPTION, description);
        contentValues.put(DBHelper.COLUMN_STORY_CHECKLIST, checklist);
        contentValues.put(DBHelper.COLUMN_STORY_CHECKLIST_COUNT, checklist_count);
        contentValues.put(DBHelper.COLUMN_STORY_CHECKLIST_COUNT_FILLED, checklist_count_filled);

        return db.insert(stories_table, null, contentValues);
    }
    
    //get/set attributes
    
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public long getId() {
        return id;
    }

    public int getChecklist_count() {
        return checklist_count;
    }

    public void setChecklist_count(int checklist_count) {
        this.checklist_count = checklist_count;
    }

    public int getChecklist_count_filled() {
        return checklist_count_filled;
    }

    public void setChecklist_count_filled(int checklist_count_filled) {
        this.checklist_count_filled = checklist_count_filled;
    }
}
