package org.codeforafrica.storycheck;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import org.codeforafrica.storycheck.activities.NewStoryActivity;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.LoadContentService;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.fragments.CompletedPosts;
import org.codeforafrica.storycheck.fragments.InCompletePosts;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

public class PageViewActivity extends AppCompatActivity {
    MyPageAdapter pageAdapter;

    FloatingActionButton addFab;
    private AvenirTextView toolbarTitle;
    List<StoryObject> stories_finished = new ArrayList<>();
    List<StoryObject> stories_incomplete = new ArrayList<>();
    ViewPager pager;
    private TabLayout tabLayout;

    private Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);

        //set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (AvenirTextView) toolbar.findViewById(R.id.toolbar_title);

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_launcher));

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbarTitle.setText(getResources().getString(R.string.my_posts));

        addFab = (FloatingActionButton) findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(PageViewActivity.this, NewStoryActivity.class);
                startActivity(i);

            }
        });
        pager = (ViewPager)findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        startService(new Intent(PageViewActivity.this, LoadContentService.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.action_new:
                addFab.performClick();
                return true;
            case R.id.action_feedback:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                return true;
            case R.id.action_about:
                showAbout();
                return true;
        }

        return true;
    }

    private void showAbout(){
        Dialog dialog_about = new Dialog(this);
        dialog_about.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_about.setTitle("About " + GlobalConstants.TAG);
        dialog_about.setContentView(R.layout.dialog_about);
        dialog_about.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openURL("http://codeforafrica.org");
            }

        });
        dialog_about.findViewById(R.id.rlWebsite).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openURL("http://nqabile.co.za/");
            }

        });
        dialog_about.show();
    }

    public void openURL(String url){

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(InCompletePosts.newInstance(toolbar, getApplicationContext(), "Fragment 1", toolbarTitle, addFab, this, stories_incomplete, tabLayout));
        fList.add(CompletedPosts.newInstance(toolbar, getApplicationContext(), "Fragment 2", toolbarTitle, addFab, this, stories_finished, tabLayout));
        return fList;
    }

    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }
        @Override
        public int getCount() {
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            if(position == 1){
                return getResources().getString(R.string.completed_posts);
            }else{
                return getResources().getString(R.string.pending_posts);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        stories_finished.clear();
        stories_incomplete.clear();

        //check if has posts
        getStories();

        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(pager);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getStories(){

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
            storyObject.setDate(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STORY_DATE)));
            //check if completed
            if(storyObject.getChecklist_count() > storyObject.getChecklist_count_filled()){
                stories_incomplete.add(storyObject);
            }else{
                stories_finished.add(storyObject);
            }

        }

        if((stories_finished.size() + stories_incomplete.size()) > 0){
            tabLayout.setVisibility(View.VISIBLE);
        }else{
            tabLayout.setVisibility(View.GONE);
        }

        cursor.close();
    }
}