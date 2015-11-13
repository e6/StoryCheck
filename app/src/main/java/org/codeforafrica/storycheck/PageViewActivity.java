package org.codeforafrica.storycheck;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.LoadContentService;
import org.codeforafrica.storycheck.data.StoryObject;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

public class PageViewActivity extends AppCompatActivity {
    MyPageAdapter pageAdapter;

    FloatingActionButton addFab;
    private AvenirTextView toolbarTitle;
    List<StoryObject> stories;
    ViewPager pager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);

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

        addFab = (FloatingActionButton) findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(PageViewActivity.this, CreatePostActivity.class);
                startActivity(i);

            }
        });
        pager = (ViewPager)findViewById(R.id.viewpager);

        startService(new Intent(PageViewActivity.this, LoadContentService.class));

    }
    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(MyFragment.newInstance(getApplicationContext(),"Fragment 1", toolbarTitle, addFab, this, stories));
        fList.add(MyFragment.newInstance(getApplicationContext(),"Fragment 2", toolbarTitle, addFab, this, stories));
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
        //check if has posts
        stories = getStories();
        
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pageAdapter);
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
}