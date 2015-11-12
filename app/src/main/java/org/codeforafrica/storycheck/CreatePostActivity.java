package org.codeforafrica.storycheck;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import org.codeforafrica.storycheck.MaterialEditTextExtend.MinLengthValidator;
import org.codeforafrica.storycheck.adapters.QuestionsListAdapter;
import org.codeforafrica.storycheck.data.CheckListObject;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.helpers.OnSwipeTouchListener;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    private SweetSheet mReportCategoriesSheet;
    private RelativeLayout rl;
    private MaterialEditText editTitle;
    private MaterialEditText editDescription;
    private RelativeLayout categoryPicker;
    private ListView questionsList;
    private AvenirTextView categoryName;
    private ImageView categoryThumb;
    private AvenirTextView toolbarTitle;
    private List<CheckListObject> checkLists;
    private String selected_checklist_id;
    private FloatingActionButton doneButton;
    private QuestionsListAdapter currentQuestionsAdapter;
    private CircleProgress progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);

        setContentView(R.layout.activity_main);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (AvenirTextView) toolbar.findViewById(R.id.toolbar_title);

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_back));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbarTitle.setText(getResources().getString(R.string.create_post));

        //progressbar
        progressBar = (CircleProgress)toolbar.findViewById(R.id.circle_progress);
        progressBar.setSuffixText("");

        rl = (RelativeLayout) findViewById(R.id.rl);
        editTitle = (MaterialEditText) findViewById(R.id.edit_title);
        editDescription = (MaterialEditText) findViewById(R.id.edit_description);
        categoryPicker = (RelativeLayout) findViewById(R.id.category);
        questionsList = (ListView) findViewById(R.id.questions_list);
        categoryName = (AvenirTextView) findViewById(R.id.categoryName);
        categoryThumb = (ImageView) findViewById(R.id.categoryThumb);
        doneButton = (FloatingActionButton)findViewById(R.id.done_button);
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
                if(!checkBox.isChecked()){
                    checkButton(position, true);
                }else{
                    checkButton(position, false);
                }
            }
        });


        setupViewpager();

        categoryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTitle.addValidator(new MinLengthValidator(getResources().getString(R.string.minimum_chars) + 5, 5));

                if (editTitle.validate()) {
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTitle.getWindowToken(), 0);

                    mReportCategoriesSheet.toggle();

                }
            }
        });

        //show animation while uploading
        attachListeners();

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
            }
        }
    }

    private void setupViewpager() {

        mReportCategoriesSheet = new SweetSheet(rl);

        mReportCategoriesSheet.setMenuList(questionsListMenu());
        mReportCategoriesSheet.setDelegate(new ViewPagerDelegate());
        mReportCategoriesSheet.setBackgroundEffect(new DimEffect(0.5f));
        mReportCategoriesSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {

                setUpCheckListQuestions(position);

                //restore color if in error mode
                categoryName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

                categoryName.setText(menuEntity1.title);
                categoryThumb.setImageDrawable(menuEntity1.icon);

                return true;
            }
        });

    }

    /**
     * Function to load checklist items based on selection
     * @param checklist
     * @return void
     */
    public void setUpCheckListQuestions(int checklist){

        //First get db id of the selected checklist
        selected_checklist_id = checkLists.get(checklist).getId();

        //Then use the id to load adapter
        currentQuestionsAdapter = new QuestionsListAdapter(getApplicationContext(), selected_checklist_id);
        questionsList.setAdapter(currentQuestionsAdapter);

        questionsList.setVisibility(View.VISIBLE);

        //show done button
        doneButton.setVisibility(View.VISIBLE);

        //show progress bar
        showProgressBar();
    }

    public void showProgressBar(){
        progressBar.setMax(questionsList.getCount());
        progressBar.setSuffixText("/" + questionsList.getCount());
        progressBar.setVisibility(View.VISIBLE);
    }

    public List<MenuEntity> questionsListMenu(){
        List<MenuEntity> menuEntities = new ArrayList<>();

        //get all checklists from db
        checkLists = getCheckLists();

        for(CheckListObject checkListObject_: checkLists){

            MenuEntity menuEntity = new MenuEntity();
            menuEntity.title = checkListObject_.getTitle();
            menuEntity.icon = getResources().getDrawable(R.drawable.ic_launcher);
            menuEntities.add(menuEntity);
        }

        return menuEntities;
    }

    public List<CheckListObject> getCheckLists(){

        List<CheckListObject> checkListObjects = new ArrayList<>();

        String queryString = "SELECT * FROM " + DBHelper.TABLE_CHECKLISTS;

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{});

        while (cursor.moveToNext()) {

            CheckListObject checkList = new CheckListObject();
            checkList.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHECKLIST_TITLE)));
            checkList.setId(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CHECKLIST_ID)));
            checkListObjects.add(checkList);

        }

        cursor.close();

        return checkListObjects;
    }

    @Override
    public void onBackPressed() {

        if (mReportCategoriesSheet.isShow()) {
                mReportCategoriesSheet.dismiss();
        } else {
            closeCreateStory();
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



    //upload
    private void attachListeners() {

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTitle.addValidator(new MinLengthValidator(getResources().getString(R.string.minimum_chars) + 5, 5));

                if (editTitle.validate()) {

                    if (categoryName.getText().toString().equals(getResources().getString(R.string.choose_category))) {
                        categoryName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    } else {
                        save_checkList();
                    }

                }
            }
        });
    }


    public void save_checkList(){
        //save checklist to database

        String title = editTitle.getText().toString().trim();

        String description = editDescription.getText().toString().trim();

        //save story
        StoryObject storyObject = new StoryObject(getApplicationContext(), 0);
        storyObject.setTitle(title);
        storyObject.setDescription(description);
        storyObject.setChecklist(selected_checklist_id);
        storyObject.setChecklist_count(questionsList.getCount());

        long storyId = storyObject.commit();

        StoryObject newStoryObject = new StoryObject(getApplicationContext(), storyId);

        //what answers: loop through checklist items and find what's checked?
        int totalFilled = 0;

        for (int i = 0; i < questionsList.getCount(); i++) {
            View v = questionsList.getChildAt(i - questionsList.getFirstVisiblePosition());

            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            if(checkBox.isChecked()){
                String question_id = currentQuestionsAdapter.getQuestion(i).getId();

                //save question
                newStoryObject.saveAnswer(question_id);

                totalFilled++;

            }
        }

        //update with total filled
        newStoryObject.setChecklist_count_filled(totalFilled);
        newStoryObject.commit();

        String count = "Saved with : " + totalFilled + " / " + storyObject.getChecklist_count() + " filled!";

        //toast
        Toast.makeText(getApplicationContext(), count, Toast.LENGTH_LONG).show();

        finish();

    }

}