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
    public static final int DATABASE_VERSION = 12;

    //column names

    //checklist[categories]
    public static final String COLUMN_CHECKLIST_ID = "checklist_id";
    public static final String COLUMN_CHECKLIST_TITLE = "checklist_title";
    public static final String COLUMN_CHECKLIST_COUNT = "checklist_count";
    public static final String COLUMN_CHECKLIST_REMOTE_ID = "checklist_remote_id";
    public static final String COLUMN_CHECKLIST_THUMBNAIL = "checklist_thumbnail";

    //questions
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_QUESTION_TEXT = "question_text";
    public static final String COLUMN_QUESTION_CHECKLIST = "question_checklist";
    public static final String COLUMN_QUESTION_REMOTE_ID = "question_remote_id";

    //stories
    public static final String COLUMN_STORY_ID = "story_id";
    public static final String COLUMN_STORY_TITLE = "story_title";
    public static final String COLUMN_STORY_DESCRIPTION = "story_description";
    public static final String COLUMN_STORY_CHECKLIST = "story_checklist";
    public static final String COLUMN_STORY_CHECKLIST_COUNT = "story_checklist_count";
    public static final String COLUMN_STORY_CHECKLIST_COUNT_FILLED = "story_checklist_count_filled";
    public static final String COLUMN_STORY_DATE = "story_checklist_date";

    //answers
    public static final String COLUMN_ANSWER_ID = "answer_id";
    public static final String COLUMN_ANSWER_STORY = "answer_story";
    public static final String COLUMN_ANSWER_QUESTION = "answer_question";
    public static final String COLUMN_ANSWER_NOTES = "answer_notes";

    //table creation strings
    public static final String TABLE_CHECKLISTS_CREATE = "create table "
            + TABLE_CHECKLISTS + "(" + COLUMN_CHECKLIST_ID
            + " integer primary key autoincrement, "
            + COLUMN_CHECKLIST_COUNT + " text not null, "
            + COLUMN_CHECKLIST_REMOTE_ID + " text not null, "
            + COLUMN_CHECKLIST_THUMBNAIL + " text not null, "
            + COLUMN_CHECKLIST_TITLE + " text not null);";

    public static final String TABLE_QUESTIONS_CREATE = "create table "
            + TABLE_QUESTIONS + "(" + COLUMN_QUESTION_ID
            + " integer primary key autoincrement, "
            + COLUMN_QUESTION_TEXT + " text, "
            + COLUMN_QUESTION_REMOTE_ID + " text not null, "
            + COLUMN_QUESTION_CHECKLIST + " text not null);";

    public static final String TABLE_STORIES_CREATE = "create table "
            + TABLE_STORIES + "(" + COLUMN_STORY_ID
            + " integer primary key autoincrement, "
            + COLUMN_STORY_TITLE + " text, "
            + COLUMN_STORY_DESCRIPTION + " text, "
            + COLUMN_STORY_CHECKLIST + " text not null, "
            + COLUMN_STORY_CHECKLIST_COUNT + " int, "
            + COLUMN_STORY_CHECKLIST_COUNT_FILLED + " int, "
            + COLUMN_STORY_DATE+ " text);";

    public static final String TABLE_ANSWERS_CREATE = "create table "
            + TABLE_ANSWERS + "(" + COLUMN_ANSWER_ID
            + " integer primary key autoincrement, "
            + COLUMN_ANSWER_STORY + " text not null, "
            + COLUMN_ANSWER_QUESTION + " text not null, "
            + COLUMN_ANSWER_NOTES + " text);";

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
