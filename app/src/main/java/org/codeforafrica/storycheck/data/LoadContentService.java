package org.codeforafrica.storycheck.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.codeforafrica.storycheck.App;
import org.codeforafrica.storycheck.GlobalConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoadContentService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        requestContent();

    }

    /**
     * Method to retreive data from API and store on db
     * @return void
     */
    public void requestContent(){

        // Tag used to cancel the request
        String tag_json_obj = "retrieve_checklists";

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
                            //when done
                            stopSelf();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Service", "Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
}
