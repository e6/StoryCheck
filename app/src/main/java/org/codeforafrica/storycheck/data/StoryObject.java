package org.codeforafrica.storycheck.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickhargreaves on 11/11/15.
 */
public class StoryObject {

    private String date;
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

        if(id>0){
            //set values from db

            loadObject();

        }
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

        //db.close();

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
        contentValues.put(DBHelper.COLUMN_STORY_DATE, date);
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
        contentValues.put(DBHelper.COLUMN_STORY_DATE, date);

        return db.insert(stories_table, null, contentValues);
    }


    public long saveAnswer(String question){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_ANSWER_STORY, id);
        contentValues.put(DBHelper.COLUMN_ANSWER_QUESTION, question);

        return db.insert(DBHelper.TABLE_ANSWERS, null, contentValues);
    }


    public void loadObject(){
        String queryString = "SELECT * FROM " + DBHelper.TABLE_STORIES + " WHERE " + DBHelper.COLUMN_STORY_ID + "=?";

        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(id)});

        while (cursor.moveToNext()) {

            setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STORY_TITLE)));
            setDescription(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STORY_DESCRIPTION)));
            setChecklist(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STORY_CHECKLIST)));
            setChecklist_count(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STORY_CHECKLIST_COUNT)));
            setChecklist_count_filled(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STORY_CHECKLIST_COUNT_FILLED)));
            setDate(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STORY_DATE)));

        }

        cursor.close();

    }

    public String getChecklistName(){
        String queryString = "SELECT * FROM " + DBHelper.TABLE_CHECKLISTS + " WHERE " + DBHelper.COLUMN_CHECKLIST_ID + "=?";

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(checklist)});

        String checklist_name = "";

        while (cursor.moveToNext()) {

            checklist_name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHECKLIST_TITLE));

        }
        cursor.close();

        return  checklist_name;
    }

    public List<AnswerObject> getAnswers(){
        List<AnswerObject> answerObjects = new ArrayList<>();

        String queryString = "SELECT * FROM " + DBHelper.TABLE_ANSWERS + " WHERE " + DBHelper.COLUMN_ANSWER_STORY + "=?";

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(id)});

        while (cursor.moveToNext()) {

            AnswerObject answerObject = new AnswerObject();

            String question_id = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ANSWER_QUESTION));

            //QuestionObject questionObject = new QuestionObject(mContext, question_id);

            answerObject.setQuestion(question_id);

            answerObjects.add(answerObject);

        }

        cursor.close();

        return answerObjects;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
