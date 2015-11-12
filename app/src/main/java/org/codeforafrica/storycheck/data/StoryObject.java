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
    private String id;
    
    private String stories_table = DBHelper.TABLE_STORIES;

    private Context mContext;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public StoryObject(Context ctx, String _id){
        this.mContext = ctx;
        dbHelper = new DBHelper(mContext);
        db = dbHelper.getWritableDatabase();
        id = _id;
    }

    public StoryObject(String _id){
        id = _id;
    }
    //Database operations//

    /**
     * function to add new question to database
     * @return int id of inserted question
     */
    public long commit(){

        long insertId = -1;

        if(id == null){
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

        return db.insert(stories_table, null, contentValues);
    }
    
    //get/set attributes
    
    public void setId(String id) {
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

    public String getId() {
        return id;
    }
}
