package org.codeforafrica.storycheck.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE_CHECKLISTS = "table_checklists";
    private static final String TABLE_QUESTIONS = "table_questions";
    private static final String TABLE_STORIES = "table_stories";
    private static final String TABLE_ANSWERS = "table_answers";

    private static final String DATABASE_NAME = "storycheck";
    private static final int DATABASE_VERSION = 1;

    //column names

    //checklist[categories]
    private static final String COLUMN_CHECKLIST_ID = "checklist_id";
    private static final String COLUMN_CHECKLIST_TITLE = "checklist_title";

    //questions
    private static final String COLUMN_QUESTION_ID = "question_id";
    private static final String COLUMN_QUESTION_TEXT = "question_text";
    private static final String COLUMN_QUESTION_CHECKLIST = "question_checklist";

    //stories
    private static final String COLUMN_STORY_ID = "story_id";
    private static final String COLUMN_STORY_TITLE = "story_title";
    private static final String COLUMN_STORY_CHECKLIST = "story_checklist";

    //answers
    private static final String COLUMN_ANSWER_ID = "answer_id";
    private static final String COLUMN_ANSWER_STORY = "answer_story";
    private static final String COLUMN_ANSWER_NOTES = "answer_notes";

    //table creation strings
    private static final String TABLE_CHECKLISTS_CREATE = "create table "
            + TABLE_CHECKLISTS + "(" + COLUMN_CHECKLIST_ID
            + " integer primary key autoincrement, "
            + COLUMN_CHECKLIST_TITLE + " text not null);";

    private static final String TABLE_QUESTIONS_CREATE = "create table "
            + TABLE_QUESTIONS + "(" + COLUMN_QUESTION_ID
            + " integer primary key autoincrement, "
            + COLUMN_QUESTION_TEXT + " text, "
            + COLUMN_QUESTION_CHECKLIST + " text not null);";

    private static final String TABLE_STORIES_CREATE = "create table "
            + TABLE_STORIES + "(" + COLUMN_STORY_ID
            + " integer primary key autoincrement, "
            + COLUMN_STORY_TITLE + " text, "
            + COLUMN_STORY_CHECKLIST + " text not null);";

    private static final String TABLE_ANSWERS_CREATE = "create table "
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
