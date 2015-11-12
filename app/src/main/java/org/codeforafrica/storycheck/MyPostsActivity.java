package org.codeforafrica.storycheck;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.zzt.inbox.interfaces.OnDragStateChangeListener;
import com.zzt.inbox.widget.InboxBackgroundScrollView;
import com.zzt.inbox.widget.InboxLayoutBase;
import com.zzt.inbox.widget.InboxLayoutListView;

import org.codeforafrica.storycheck.adapters.AnswersAdapter;
import org.codeforafrica.storycheck.data.AnswerObject;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.LoadContentService;
import org.codeforafrica.storycheck.data.QuestionObject;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {
    InboxLayoutListView inboxLayoutListView;
    InboxBackgroundScrollView inboxBackgroundScrollView;
    LinearLayout postsList;
    FloatingActionButton addFab;
    private AvenirTextView toolbarTitle;
    List<StoryObject> stories;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_posts);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (AvenirTextView) toolbar.findViewById(R.id.toolbar_title);

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbarTitle.setText(getResources().getString(R.string.my_posts));

        //inbox layout list
        inboxBackgroundScrollView = (InboxBackgroundScrollView) findViewById(R.id.scroll);
        inboxLayoutListView = (InboxLayoutListView) findViewById(R.id.inboxlayout);
        inboxLayoutListView.setBackgroundScrollView(inboxBackgroundScrollView);//绑定scrollview
        inboxLayoutListView.setCloseDistance(50);
        inboxLayoutListView.setOnDragStateChangeListener(new OnDragStateChangeListener() {
            @Override
            public void dragStateChange(InboxLayoutBase.DragState state) {
                switch (state) {
                    case CANCLOSE:
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff5e5e5e));
                        toolbarTitle.setText(getResources().getString(R.string.back));
                        break;
                    case CANNOTCLOSE:
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                        toolbarTitle.setText(getResources().getString(R.string.my_posts));
                        addFab.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        addFab = (FloatingActionButton) findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(MyPostsActivity.this, CreatePostActivity.class);
                startActivity(i);

            }
        });

        //posts list
        postsList = (LinearLayout)findViewById(R.id.postsList);

        startService(new Intent(MyPostsActivity.this, LoadContentService.class));
    }

    @Override
    public void onBackPressed(){
        if(inboxLayoutListView.isShown()){
            inboxLayoutListView.closeWithAnim();
            inboxLayoutListView.setVisibility(View.INVISIBLE);
        }else{
            super.onBackPressed();
        }
    }


    public void loadStories(){

        //add some post items
        //TODO: use list with adapter
        TypedArray category_drawables = getResources().obtainTypedArray(R.array.category_drawables);
        TypedArray category_strings = getResources().obtainTypedArray(R.array.category_strings);

        for(int i = 0; i<stories.size(); i++){
            StoryObject thisStory = stories.get(i);

            Log.d("story: ", thisStory.getTitle() + ":" + thisStory.getDescription());

            addPosts(category_drawables.getResourceId(i, -1), thisStory.getTitle(), thisStory.getId(), thisStory.getChecklist_count(), thisStory.getChecklist_count_filled());
        }

        category_drawables.recycle();
        category_strings.recycle();
    }

    public void addPosts(int iconResource, final String storyTitle, final long story_id, int checkListCount, int checkListCountFilled){
        // Creating a new RelativeLayout
        final RelativeLayout relativeLayout = new RelativeLayout(this);

        // Defining the RelativeLayout layout parameters to fill the parent.
        RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 0, 0, 3);
        relativeLayout.setLayoutParams(llp);

        relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        relativeLayout.setPadding(10, 30, 10, 30);
        relativeLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);

        //Create an icon view
        RelativeLayout.LayoutParams ivl = new RelativeLayout.LayoutParams(35, 35);
        ivl.addRule(RelativeLayout.ALIGN_PARENT_START);
        ivl.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView iV = new ImageView(this);
        iV.setImageDrawable(getResources().getDrawable(iconResource));
        iV.setLayoutParams(ivl);
        iV.setId(View.generateViewId());
        //iV.setPadding(5, 5, 5, 5);
        relativeLayout.addView(iV);


        //create circle progress bar
        RelativeLayout.LayoutParams crl = new RelativeLayout.LayoutParams(60, 60);
        crl.addRule(RelativeLayout.ALIGN_PARENT_END);

        CircleProgress circleProgress = new CircleProgress(this);
        circleProgress.setLayoutParams(crl);
        circleProgress.setMax(checkListCount);
        circleProgress.setProgress(checkListCountFilled);
        circleProgress.setSuffixText("/" + checkListCount);
        circleProgress.setId(View.generateViewId());
        circleProgress.setPadding(5, 5, 5, 5);
        circleProgress.setFinishedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        relativeLayout.addView(circleProgress);


        // Creating a new TextView
        AvenirTextView tv = new AvenirTextView(this);
        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight));
        tv.setText(storyTitle);
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(23);

        // Setting the parameters on the TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);
        lp.addRule(RelativeLayout.RIGHT_OF, iV.getId());
        lp.addRule(RelativeLayout.LEFT_OF, circleProgress.getId());
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        tv.setLayoutParams(lp);

        // Adding the TextView to the RelativeLayout as a child
        relativeLayout.addView(tv);

        //add click listener
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFab.setVisibility(View.GONE);
                inboxLayoutListView.openWithAnim(relativeLayout);

                //get answers for this post
                List<AnswerObject> answersList = getAnswers(story_id);

                inboxLayoutListView.setAdapter(new AnswersAdapter(getApplicationContext(), answersList));
                toolbarTitle.setText(storyTitle);
            }
        });

        postsList.addView(relativeLayout);
    }

    public List<AnswerObject> getAnswers(long story_id){
        List<AnswerObject> answerObjects = new ArrayList<>();

        String queryString = "SELECT * FROM " + DBHelper.TABLE_ANSWERS + " WHERE " + DBHelper.COLUMN_ANSWER_STORY + "=?";

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(story_id)});

        while (cursor.moveToNext()) {

            AnswerObject answerObject = new AnswerObject();

            String question_id = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ANSWER_QUESTION));

            QuestionObject questionObject = new QuestionObject(getApplicationContext(), question_id);

            answerObject.setQuestion(questionObject.getText());

            answerObjects.add(answerObject);

        }

        cursor.close();

        return answerObjects;
    }

    public List<StoryObject> getStories(){

        List<StoryObject> storyObjects = new ArrayList<>();

        String queryString = "SELECT * FROM " + DBHelper.TABLE_STORIES + " ORDER BY " + DBHelper.COLUMN_STORY_ID + " DESC";

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{});

        while (cursor.moveToNext()) {

            StoryObject storyObject = new StoryObject(0);

            storyObject.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STORY_ID)));
            storyObject.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STORY_TITLE)));
            storyObject.setDescription(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STORY_DESCRIPTION)));
            storyObject.setChecklist_count(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STORY_CHECKLIST_COUNT)));
            storyObject.setChecklist_count_filled(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STORY_CHECKLIST_COUNT_FILLED)));

            storyObjects.add(storyObject);

        }

        cursor.close();

        return storyObjects;
    }

    @Override
    public void onResume(){
        super.onResume();

        //check if has posts
        stories = getStories();

        if(stories.size() < 1){
            (findViewById(R.id.no_posts)).setVisibility(View.VISIBLE);
            inboxBackgroundScrollView.setVisibility(View.INVISIBLE);

        }else{
            (findViewById(R.id.no_posts)).setVisibility(View.GONE);
            inboxBackgroundScrollView.setVisibility(View.VISIBLE);
        }
        postsList.removeAllViews();

        loadStories();

    }
}


