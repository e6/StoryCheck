package org.codeforafrica.storycheck.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.zzt.inbox.interfaces.OnDragStateChangeListener;
import com.zzt.inbox.widget.InboxBackgroundScrollView;
import com.zzt.inbox.widget.InboxLayoutBase;
import com.zzt.inbox.widget.InboxLayoutListView;

import org.codeforafrica.storycheck.PageViewActivity;
import org.codeforafrica.storycheck.R;
import org.codeforafrica.storycheck.adapters.AnswersAdapter;
import org.codeforafrica.storycheck.data.AnswerObject;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.QuestionObject;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

public class InCompletePosts extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    static AvenirTextView toolbarTitle;
    static FloatingActionButton addFab;
    static AppCompatActivity parentActivity;
    static Context mContext;
    InboxLayoutListView inboxLayoutListView;
    InboxBackgroundScrollView inboxBackgroundScrollView;
    LinearLayout postsList;
    static List<StoryObject> stories;
    LinearLayout noPosts;

    public static Fragment newInstance(Context context, String message, AvenirTextView _toolbarTitle, FloatingActionButton _addFab, PageViewActivity _pageViewActivity, List<StoryObject> _stories) {
        toolbarTitle = _toolbarTitle;
        addFab = _addFab;
        parentActivity = _pageViewActivity;
        stories = _stories;
        mContext = context;

        InCompletePosts f = new InCompletePosts();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        View v = inflater.inflate(R.layout.my_fragment, container, false);

        inboxBackgroundScrollView = (InboxBackgroundScrollView) v.findViewById(R.id.scroll);
        inboxLayoutListView = (InboxLayoutListView) v.findViewById(R.id.inboxlayout);
        inboxLayoutListView.setBackgroundScrollView(inboxBackgroundScrollView);
        inboxLayoutListView.setCloseDistance(50);
        inboxLayoutListView.setOnDragStateChangeListener(new OnDragStateChangeListener() {
            @Override
            public void dragStateChange(InboxLayoutBase.DragState state) {
                switch (state) {
                    case CANCLOSE:
                        parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff5e5e5e));
                        toolbarTitle.setText(getResources().getString(R.string.back));
                        break;
                    case CANNOTCLOSE:
                        parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                        toolbarTitle.setText(getResources().getString(R.string.my_posts));
                        addFab.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        noPosts = (LinearLayout)v.findViewById(R.id.no_posts);
        //posts list
        postsList = (LinearLayout)v.findViewById(R.id.postsList);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(stories.size() < 1){
            noPosts.setVisibility(View.VISIBLE);
            inboxBackgroundScrollView.setVisibility(View.INVISIBLE);
        }else{
            noPosts.setVisibility(View.GONE);
            inboxBackgroundScrollView.setVisibility(View.VISIBLE);
        }
        postsList.removeAllViews();

        loadStories();

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
        final RelativeLayout relativeLayout = new RelativeLayout(parentActivity);

        // Defining the RelativeLayout layout parameters to fill the parent.
        RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 0, 0, 3);
        relativeLayout.setLayoutParams(llp);

        relativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        relativeLayout.setPadding(10, 30, 10, 30);
        relativeLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);

        //Create an icon view
        RelativeLayout.LayoutParams ivl = new RelativeLayout.LayoutParams(35, 35);
        ivl.addRule(RelativeLayout.ALIGN_PARENT_START);
        ivl.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageView iV = new ImageView(parentActivity);
        iV.setImageDrawable(getResources().getDrawable(iconResource));
        iV.setLayoutParams(ivl);
        iV.setId(View.generateViewId());
        //iV.setPadding(5, 5, 5, 5);
        relativeLayout.addView(iV);


        //create circle progress bar
        RelativeLayout.LayoutParams crl = new RelativeLayout.LayoutParams(60, 60);
        crl.addRule(RelativeLayout.ALIGN_PARENT_END);

        CircleProgress circleProgress = new CircleProgress(parentActivity);
        circleProgress.setLayoutParams(crl);
        circleProgress.setMax(checkListCount);
        circleProgress.setProgress(checkListCountFilled);
        circleProgress.setSuffixText("/" + checkListCount);
        circleProgress.setId(View.generateViewId());
        circleProgress.setPadding(5, 5, 5, 5);
        circleProgress.setFinishedColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        relativeLayout.addView(circleProgress);


        // Creating a new TextView
        AvenirTextView tv = new AvenirTextView(parentActivity);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryLight));
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

                inboxLayoutListView.setAdapter(new AnswersAdapter(mContext, answersList));
                toolbarTitle.setText(storyTitle);
            }
        });

        postsList.addView(relativeLayout);
    }

    public List<AnswerObject> getAnswers(long story_id){
        List<AnswerObject> answerObjects = new ArrayList<>();

        String queryString = "SELECT * FROM " + DBHelper.TABLE_ANSWERS + " WHERE " + DBHelper.COLUMN_ANSWER_STORY + "=?";

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{String.valueOf(story_id)});

        while (cursor.moveToNext()) {

            AnswerObject answerObject = new AnswerObject();

            String question_id = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ANSWER_QUESTION));

            QuestionObject questionObject = new QuestionObject(mContext, question_id);

            answerObject.setQuestion(questionObject.getText());

            answerObjects.add(answerObject);

        }

        cursor.close();

        return answerObjects;
    }


}