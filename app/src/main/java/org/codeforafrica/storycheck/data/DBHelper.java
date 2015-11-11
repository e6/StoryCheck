package org.codeforafrica.storycheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_CHECKLISTS = "table_checklists";
    public static final String TABLE_QUESTIONS = "table_questions";
    public static final String TABLE_STORIES = "table_stories";
    public static final String TABLE_ANSWERS = "table_answers";

    public static final String DATABASE_NAME = "storycheck";
    public static final int DATABASE_VERSION = 2;

    //column names

    //checklist[categories]
    public static final String COLUMN_CHECKLIST_ID = "checklist_id";
    public static final String COLUMN_CHECKLIST_TITLE = "checklist_title";
    public static final String COLUMN_CHECKLIST_COUNT = "checklist_count";
    public static final String COLUMN_CHECKLIST_REMOTE_ID = "checklist_remote_id";

    //questions
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_QUESTION_TEXT = "question_text";
    public static final String COLUMN_QUESTION_CHECKLIST = "question_checklist";

    //stories
    public static final String COLUMN_STORY_ID = "story_id";
    public static final String COLUMN_STORY_TITLE = "story_title";
    public static final String COLUMN_STORY_CHECKLIST = "story_checklist";

    //answers
    public static final String COLUMN_ANSWER_ID = "answer_id";
    public static final String COLUMN_ANSWER_STORY = "answer_story";
    public static final String COLUMN_ANSWER_NOTES = "answer_notes";

    //table creation strings
    public static final String TABLE_CHECKLISTS_CREATE = "create table "
            + TABLE_CHECKLISTS + "(" + COLUMN_CHECKLIST_ID
            + " integer primary key autoincrement, "
            + COLUMN_CHECKLIST_COUNT + " text not null, "
            + COLUMN_CHECKLIST_REMOTE_ID + " text not null, "
            + COLUMN_CHECKLIST_TITLE + " text not null);";

    public static final String TABLE_QUESTIONS_CREATE = "create table "
            + TABLE_QUESTIONS + "(" + COLUMN_QUESTION_ID
            + " integer primary key autoincrement, "
            + COLUMN_QUESTION_TEXT + " text, "
            + COLUMN_QUESTION_CHECKLIST + " text not null);";

    public static final String TABLE_STORIES_CREATE = "create table "
            + TABLE_STORIES + "(" + COLUMN_STORY_ID
            + " integer primary key autoincrement, "
            + COLUMN_STORY_TITLE + " text, "
            + COLUMN_STORY_CHECKLIST + " text not null);";

    public static final String TABLE_ANSWERS_CREATE = "create table "
            + TABLE_ANSWERS + "(" + COLUMN_ANSWER_ID
            + " integer primary key autoincrement, "
            + COLUMN_ANSWER_STORY + " text, "
            + COLUMN_ANSWER_NOTES + " text not null);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create tables
        db.execSQL(TABLE_CHECKLISTS_CREATE);
        db.execSQL(TABLE_QUESTIONS_CREATE);
        db.execSQL(TABLE_STORIES_CREATE);
        db.execSQL(TABLE_ANSWERS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop tables if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWERS);

        onCreate(db);
    }

}
