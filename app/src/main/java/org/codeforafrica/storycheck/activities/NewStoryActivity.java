package org.codeforafrica.storycheck.activities;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.codeforafrica.storycheck.App;
import org.codeforafrica.storycheck.GlobalConstants;
import org.codeforafrica.storycheck.MaterialEditTextExtend.MinLengthValidator;
import org.codeforafrica.storycheck.R;
import org.codeforafrica.storycheck.adapters.QuestionsListAdapter;
import org.codeforafrica.storycheck.data.AnswerObject;
import org.codeforafrica.storycheck.data.CheckListObject;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.QuestionObject;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.view.AvenirTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nickhargreaves on 11/20/15.
 */
public class NewStoryActivity extends AppCompatActivity {

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
    private ImageView doneButton;
    private QuestionsListAdapter currentQuestionsAdapter;
    private CircleProgress progressBar;
    private ProgressDialog progressDialog;
    private long storyId = 0;
    private StoryObject storyObject;
    private List<AnswerObject> answersList;

    private int mode;

    private static final int CREATE_MODE = 0;
    private static final int CHECKLIST_MODE = 1;
    private static final int EDIT_MODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);

        setContentView(R.layout.new_story_activity);

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

        doneButton = (ImageView)toolbar.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTitle.addValidator(new MinLengthValidator(getResources().getString(R.string.minimum_chars) + 5, 5));

                if (editTitle.validate()) {

                        save_checkList();

                }
            }
        });

        editTitle = (MaterialEditText) findViewById(R.id.edit_title);
        editTitle.addValidator(new MinLengthValidator(getResources().getString(R.string.minimum_chars) + 5, 5));

        editDescription = (MaterialEditText) findViewById(R.id.edit_description);

        categoryPicker = (MaterialBetterSpinner) findViewById(R.id.spinner);
        categoryPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //First get db id of the selected checklist
                selected_checklist_id = checkLists.get(position).getId();
            }
        });

        checkLists = getCheckLists();

        if(checkLists.size() > 0){

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, checkListTitles);

            categoryPicker.setAdapter(adapter);
        }else{
            progressDialog = new ProgressDialog(NewStoryActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("You have no checklists");
            progressDialog.setMessage("Getting checklists...");
            progressDialog.show();

            requestContent();
        }

    }
    public void save_checkList(){
        //save checklist to database
        String title = editTitle.getText().toString().trim();

        String description = editDescription.getText().toString().trim();

        //find questions
        currentQuestionsAdapter = new QuestionsListAdapter(getApplicationContext(), selected_checklist_id);

        //save story
        storyObject = new StoryObject(getApplicationContext(), storyId);
        storyObject.setTitle(title);
        storyObject.setDescription(description);
        storyObject.setChecklist(selected_checklist_id);
        storyObject.setChecklist_count(currentQuestionsAdapter.getCount());

        //save date if added for the first time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
        String captureDate = dateFormat.format(new Date());

        storyObject.setDate(captureDate);


        storyId = storyObject.commit();

        //update with answers
        storyObject = new StoryObject(getApplicationContext(), storyId);

        //update with total filled
        storyObject.setChecklist_count_filled(0);
        storyObject.commit();

        //toast
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved), Toast.LENGTH_LONG).show();

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
                                //checkListObject.setThumbnail(thisChecklist.getString("thumbnail"));
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

                            progressDialog.dismiss();
                            if(checkLists.size() < 1) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_getting_checklists), Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_getting_checklists), Toast.LENGTH_LONG).show();
                            finish();
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
}
