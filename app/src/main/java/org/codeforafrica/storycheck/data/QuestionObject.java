package org.codeforafrica.storycheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by nickhargreaves on 11/11/15.
 */
public class QuestionObject {

    private String text;
    private String remote_id;
    private long checkListId;
    private String id;

    private Context mContext;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public QuestionObject(Context ctx){
        this.mContext = ctx;
        dbHelper = new DBHelper(mContext);
        db = dbHelper.getWritableDatabase();

    }

    public QuestionObject(){

    }

    public QuestionObject(Context ctx, String _id){
        this.mContext = ctx;
        this.id = _id;
        dbHelper = new DBHelper(mContext);
        db = dbHelper.getWritableDatabase();

        loadObject();
    }

    public void loadObject(){
        String queryString = "SELECT * FROM " + DBHelper.TABLE_QUESTIONS + " WHERE " + DBHelper.COLUMN_QUESTION_ID + "=?";

        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(id)});

        while (cursor.moveToNext()) {

            setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_QUESTION_TEXT)));
            setCheckListId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_QUESTION_CHECKLIST)));
            setRemote_id(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_QUESTION_REMOTE_ID)));

        }

        cursor.close();

    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCheckListId() {
        return checkListId;
    }

    public void setCheckListId(long checkListId) {
        this.checkListId = checkListId;
    }

    public String getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(String remote_id) {
        this.remote_id = remote_id;
    }

    public String getId() {
        return id;
    }

    //Database operations//

    /**
     * function to add new question to database
     * @return int id of inserted question
     */
    public long commit(){

        long insertId = isAdded();

        if(insertId > 0){
            //update
            update();
        }else{
            //insert
            insertId = insertNew();
        }

        db.close();

        return insertId;

    }

    /**
     * Update question details
     */
    public void update(){

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.COLUMN_QUESTION_TEXT, text);
        contentValues.put(DBHelper.COLUMN_QUESTION_CHECKLIST, checkListId);

        db.update(DBHelper.TABLE_QUESTIONS, contentValues, DBHelper.COLUMN_QUESTION_REMOTE_ID + "=" + remote_id, null);

    }

    /**
     * insert new question
     * @return
     */
    public long insertNew(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_QUESTION_TEXT, text);
        contentValues.put(DBHelper.COLUMN_QUESTION_CHECKLIST, checkListId);
        contentValues.put(DBHelper.COLUMN_QUESTION_REMOTE_ID, remote_id);

        return db.insert(DBHelper.TABLE_QUESTIONS, null, contentValues);
    }

    /**
     * If already added return id
     * @return long
     */
    public long isAdded(){
        String select_string =  "select count(*) from "+DBHelper.TABLE_QUESTIONS+" where " +DBHelper.COLUMN_QUESTION_REMOTE_ID+ "='" + remote_id + "'; ";

        SQLiteStatement s = db.compileStatement(select_string );

        return s.simpleQueryForLong();
    }

    public void setId(String id) {
        this.id = id;
    }
}
