package org.codeforafrica.storycheck.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.rey.material.widget.CheckBox;

import org.codeforafrica.storycheck.R;
import org.codeforafrica.storycheck.adapters.QuestionsListAdapter;
import org.codeforafrica.storycheck.data.AnswerObject;
import org.codeforafrica.storycheck.data.CheckListObject;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.QuestionObject;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.helpers.OnSwipeTouchListener;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.List;

public class QuestionsChecklistActivity extends AppCompatActivity {

    private ListView questionsList;
    private AvenirTextView toolbarTitle;
    private CheckListObject checkList;
    private String selected_checklist_id;
    private ImageView editButton;
    private QuestionsListAdapter currentQuestionsAdapter;
    private long storyId = 0;
    private StoryObject storyObject;
    private List<AnswerObject> answersList;
    private AvenirTextView titleView;
    private AvenirTextView descriptionView;
    private AvenirTextView dateView;
    private CircleProgress progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);

        setContentView(R.layout.activity_checklists);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (AvenirTextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.my_posts));

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_back));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        progressBar = (CircleProgress) findViewById(R.id.circle_progress);
        progressBar.setSuffixText("");
        progressBar.setFinishedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        questionsList = (ListView) findViewById(R.id.questions_list);

        titleView = (AvenirTextView)findViewById(R.id.title_view);
        descriptionView = (AvenirTextView) findViewById(R.id.description_view);
        dateView = (AvenirTextView) findViewById(R.id.date_view);


        editButton = (ImageView)toolbar.findViewById(R.id.edit_button);
        //set ontouch listener
        questionsList.setOnTouchListener(new OnSwipeTouchListener(this, questionsList) {

            @Override
            public void onSwipeRight(int pos) {

                checkButton(pos, true);

            }

            @Override
            public void onSwipeLeft(int pos) {

                checkButton(pos, false);

            }
        });

        questionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                if (!checkBox.isChecked()) {
                    checkButton(position, true);
                } else {
                    checkButton(position, false);
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuestionsChecklistActivity.this, NewStoryActivity.class);
                i.putExtra("story_id", storyId);
                startActivity(i);
                finish();
            }
        });

        //get details
        storyId = getIntent().getLongExtra("story_id", 0);
        //load existing
        storyObject = new StoryObject(getApplicationContext(), storyId);

        titleView.setText(storyObject.getTitle());
        descriptionView.setText(storyObject.getDescription());
        dateView.setText(storyObject.getDate());

        //load the checklist selected
        selected_checklist_id = storyObject.getChecklist();

        //Then use the id to load adapter
        currentQuestionsAdapter = new QuestionsListAdapter(getApplicationContext(), selected_checklist_id);
        questionsList.setAdapter(currentQuestionsAdapter);

        //set progressbar max
        progressBar.setMax(questionsList.getCount());
        progressBar.setSuffixText("/" + questionsList.getCount());

        //get answers for this story
        answersList = storyObject.getAnswers();

        //TODO: make it work without handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setUpAnswers();

            }
        }, 500);

        //set checklist details
        setCheckList();

    }

    public void setCheckList(){

        checkList = new CheckListObject();

        String queryString = "SELECT * FROM " + DBHelper.TABLE_CHECKLISTS + " WHERE " + DBHelper.COLUMN_CHECKLIST_ID + "=?" ;

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{selected_checklist_id});

        if (cursor != null) {
            cursor.moveToFirst();

            checkList.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHECKLIST_TITLE)));
            checkList.setId(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHECKLIST_ID)));
            checkList.setCount(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CHECKLIST_COUNT)));

            cursor.close();
        }

    }
    public void setUpAnswers(){

        for (int i = 0; i < questionsList.getCount(); i++) {

            QuestionObject question = currentQuestionsAdapter.getQuestion(i);

            for (int j = 0; j < answersList.size(); j++) {
                AnswerObject answerObject = answersList.get(j);

                if((answerObject.getQuestion()).equals(question.getId())){
                    checkButton(i, true);
                }
            }

        }
    }

    int position;
    CheckBox checkBox;

    private void checkButton(int pos, boolean check){
        position = pos;
        View child = questionsList.getChildAt(pos - questionsList.getFirstVisiblePosition());
        if (child != null) {

            checkBox = (CheckBox) child.findViewById(R.id.checkBox);
            if (checkBox != null) {
                checkBox.setChecked(check);
                if(check){
                    progressBar.setProgress(progressBar.getProgress() + 1);
                }else{
                    progressBar.setProgress(progressBar.getProgress() - 1);
                }
                save_checkList();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        int selectedItem = menuItem.getItemId();

        switch (selectedItem){
            case android.R.id.home:
                closeCreateStory();
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void closeCreateStory(){
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void save_checkList(){

        storyObject.setChecklist(selected_checklist_id);
        storyObject.setChecklist_count(questionsList.getCount());

        storyObject.commit();

        int totalFilled = 0;

        //loop through checklist items and find what's checked?

            for (int i = 0; i < questionsList.getCount(); i++) {
                View v = questionsList.getChildAt(i - questionsList.getFirstVisiblePosition());

                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                if (checkBox.isChecked()) {
                    String question_id = currentQuestionsAdapter.getQuestion(i).getId();

                    //save question
                    storyObject.saveAnswer(question_id);

                    totalFilled++;

                }
            }

        //update with total filled
        storyObject.setChecklist_count_filled(totalFilled);
        storyObject.commit();

    }


}