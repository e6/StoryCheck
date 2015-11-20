package org.codeforafrica.storycheck;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.codeforafrica.storycheck.MaterialEditTextExtend.MinLengthValidator;
import org.codeforafrica.storycheck.adapters.QuestionsListAdapter;
import org.codeforafrica.storycheck.data.AnswerObject;
import org.codeforafrica.storycheck.data.CheckListObject;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.QuestionObject;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.helpers.OnSwipeTouchListener;
import org.codeforafrica.storycheck.view.AvenirTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    private RelativeLayout rl;
    private MaterialEditText editTitle;
    private MaterialEditText editDescription;

    private MaterialBetterSpinner categoryPicker;
    private RelativeLayout categorySection;
    private ImageView categoryThumb;

    private ListView questionsList;
    private AvenirTextView toolbarTitle;
    private List<CheckListObject> checkLists;
    private List<String>checkListTitles = new ArrayList<>();
    private String selected_checklist_id;
    private FloatingActionButton doneButton;
    private QuestionsListAdapter currentQuestionsAdapter;
    private CircleProgress progressBar;
    private ProgressDialog progressDialog;
    private long storyId = 0;
    private StoryObject storyObject;
    private List<AnswerObject> answersList;
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

        //progressbar
        progressBar = (CircleProgress)toolbar.findViewById(R.id.circle_progress);
        progressBar.setSuffixText("");

        rl = (RelativeLayout) findViewById(R.id.rl);

        editTitle = (MaterialEditText) findViewById(R.id.edit_title);
        editTitle.addValidator(new MinLengthValidator(getResources().getString(R.string.minimum_chars) + 5, 5));

        editDescription = (MaterialEditText) findViewById(R.id.edit_description);
        questionsList = (ListView) findViewById(R.id.questions_list);
        categoryThumb = (ImageView) findViewById(R.id.categoryThumb);
        categorySection = (RelativeLayout) findViewById(R.id.category);

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
                if (!checkBox.isChecked()) {
                    checkButton(position, true);
                } else {
                    checkButton(position, false);
                }
            }
        });

        categoryPicker = (MaterialBetterSpinner) findViewById(R.id.spinner);
        categoryPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //First get db id of the selected checklist
                selected_checklist_id = checkLists.get(position).getId();
                setUpCheckListQuestions();
            }
        });

        attachListeners();

        //first check if has checklists
            //get all checklists from db
            checkLists = getCheckLists();

        if(checkLists.size() > 0){

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, checkListTitles);

            categoryPicker.setAdapter(adapter);
        }else{
            rl.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(CreatePostActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("You have no checklists");
            progressDialog.setMessage("Getting checklists...");
            progressDialog.show();

            requestContent();
        }

        //check if updating story
        if(getIntent().hasExtra("story_id")){
            storyId = getIntent().getLongExtra("story_id", 0);

            //load existing
            storyObject = new StoryObject(getApplicationContext(), storyId);
            editTitle.setText(storyObject.getTitle());
            editDescription.setText(storyObject.getDescription());

            toolbarTitle.setText(getResources().getString(R.string.edit_post));

            //load the checklist selected
            selected_checklist_id = storyObject.getChecklist();
            setUpCheckListQuestions();

            //get answers for this story
            answersList = storyObject.getAnswers();

            //hide category picker in edit mode
            categorySection.setVisibility(View.GONE);

            //TODO: make it work without handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    setUpAnswers();
                }
            }, 500);

        }else{
            toolbarTitle.setText(getResources().getString(R.string.create_post));
        }

    }

    public String checkListName(){

        return storyObject.getChecklistName();

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

            /*if()

            if(checkBox.isChecked()){
                String question_id = currentQuestionsAdapter.getQuestion(i).getId();

                //save question
                storyObject.saveAnswer(question_id);

                totalFilled++;

            }*/


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
            }
        }
    }

    /**
     * Function to load checklist items based on selection
     * @return void
     */
    public void setUpCheckListQuestions(){

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
        progressBar.setFinishedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        progressBar.setVisibility(View.VISIBLE);
    }

    public Drawable drawableFromFile(String pathName){
        Drawable d = Drawable.createFromPath(pathName);
        if(d == null)
            d = getResources().getDrawable(R.mipmap.ic_featured);
        return d;
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
            checkList.setCount(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CHECKLIST_COUNT)));

            checkListObjects.add(checkList);

            checkListTitles.add(checkList.getTitle());
        }

        cursor.close();

        return checkListObjects;
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

                    if(selected_checklist_id != null && currentQuestionsAdapter !=null){
                        save_checkList();
                    }else{
                        categoryThumb.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red));
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
        storyObject = new StoryObject(getApplicationContext(), storyId);
        storyObject.setTitle(title);
        storyObject.setDescription(description);
        storyObject.setChecklist(selected_checklist_id);
        storyObject.setChecklist_count(questionsList.getCount());

        if(storyId < 1) {
            //save date if added for the first time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
            String captureDate = dateFormat.format(new Date());

            storyObject.setDate(captureDate);

        }
        storyId = storyObject.commit();


        //update with answers
        storyObject = new StoryObject(getApplicationContext(), storyId);

        //loop through checklist items and find what's checked?
        int totalFilled = 0;

        for (int i = 0; i < questionsList.getCount(); i++) {
            View v = questionsList.getChildAt(i - questionsList.getFirstVisiblePosition());

            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            if(checkBox.isChecked()){
                String question_id = currentQuestionsAdapter.getQuestion(i).getId();

                //save question
                storyObject.saveAnswer(question_id);

                totalFilled++;

            }
        }

        //update with total filled
        storyObject.setChecklist_count_filled(totalFilled);
        storyObject.commit();

        String count = "Saved with : " + totalFilled + " / " + storyObject.getChecklist_count() + " filled!";

        //toast
        Toast.makeText(getApplicationContext(), count, Toast.LENGTH_LONG).show();

        finish();

    }

    public void requestContent(){

        // Tag used to cancel the request
        String tag_json_obj = "retrieve_checklists_oncreate";

        String url = GlobalConstants.CHECKLISTS_API;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray checklistsArray = response.getJSONArray("checklists");

                            //loop through array and load to db
                            for(int j = 0; j < checklistsArray.length(); j++){
                                JSONObject thisChecklist = checklistsArray.getJSONObject(j);

                                JSONArray thisChecklistItems = thisChecklist.getJSONArray("items");
                                int checkListCount = thisChecklistItems.length();

                                //add checklist to db
                                CheckListObject checkListObject = new CheckListObject(getApplicationContext());
                                checkListObject.setTitle(thisChecklist.getString("name"));
                                checkListObject.setThumbnail(thisChecklist.getString("thumbnail"));
                                checkListObject.setCount(checkListCount);
                                checkListObject.setRemote_id(thisChecklist.getString("id"));

                                //commit and return id
                                long checkListId = checkListObject.commit();

                                //add each checklist item
                                for(int k = 0; k<thisChecklistItems.length(); k++){
                                    JSONObject thisItem = thisChecklistItems.getJSONObject(k);

                                    QuestionObject questionObject = new QuestionObject(getApplicationContext());
                                    questionObject.setRemote_id(thisItem.getString("id"));
                                    questionObject.setText(thisItem.getString("text"));
                                    questionObject.setCheckListId(checkListId);
                                    questionObject.commit();

                                }

                            }

                            checkLists = getCheckLists();

                            if(checkLists.size() > 0) {
                                rl.setVisibility(View.VISIBLE);

                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), "Could not get checklists.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Service", "Error: " + error.getMessage());

                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Could not get checklists. Check connection!", Toast.LENGTH_LONG).show();

                finish();
            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

}