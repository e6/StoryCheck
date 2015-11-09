package org.codeforafrica.storycheck;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zzt.inbox.interfaces.OnDragStateChangeListener;
import com.zzt.inbox.widget.InboxBackgroundScrollView;
import com.zzt.inbox.widget.InboxLayoutBase;
import com.zzt.inbox.widget.InboxLayoutListView;

import org.codeforafrica.storycheck.view.AvenirTextView;

public class MyPostsActivity extends AppCompatActivity {
    InboxLayoutListView inboxLayoutListView;
    InboxBackgroundScrollView inboxBackgroundScrollView;
    LinearLayout postsList;
    FloatingActionButton addFab;
    private AvenirTextView toolbarTitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_posts);

        //show app tour
        //startTour();

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
        inboxLayoutListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 10;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = MyPostsActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.item, null);
                return view;
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

        //add some post items
        //TODO: use list with adapter
        TypedArray category_drawables = getResources().obtainTypedArray(R.array.category_drawables);
        TypedArray category_strings = getResources().obtainTypedArray(R.array.category_strings);

        for(int i = 0; i<14; i++){
            addPosts(category_drawables.getResourceId(i, -1), category_strings.getResourceId(i, -1));
        }

        category_drawables.recycle();
        category_strings.recycle();
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

    public void addPosts(int iconResource, int textResource){
        // Creating a new LinearLayout
        final LinearLayout linearLayout = new LinearLayout(this);

        // Setting the orientation to vertical
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Defining the LinearLayout layout parameters to fill the parent.
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 0, 0, 3);
        linearLayout.setLayoutParams(llp);

        linearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        linearLayout.setPadding(10, 30, 10, 30);
        linearLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);

        // Defining the layout parameters
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);

        //Create an icon view
        ImageView iV = new ImageView(this);
        iV.setImageDrawable(getResources().getDrawable(iconResource));
        iV.setLayoutParams(new LinearLayout.LayoutParams(35, 35));
        //iV.setPadding(5, 5, 5, 5);
        linearLayout.addView(iV);

        // Creating a new TextView
        AvenirTextView tv = new AvenirTextView(this);
        tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight));
        tv.setText("A post on " + getResources().getText(textResource));
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(23);

        // Setting the parameters on the TextView
        tv.setLayoutParams(lp);

        // Adding the TextView to the LinearLayout as a child
        linearLayout.addView(tv);

        //add click listener
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFab.setVisibility(View.GONE);
                inboxLayoutListView.openWithAnim(linearLayout);
                toolbarTitle.setText("Post title here");
            }
        });

        postsList.addView(linearLayout);
    }

}


