package org.codeforafrica.storycheck;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.zzt.inbox.interfaces.OnDragStateChangeListener;
import com.zzt.inbox.widget.InboxBackgroundScrollView;
import com.zzt.inbox.widget.InboxLayoutBase;
import com.zzt.inbox.widget.InboxLayoutListView;

import org.codeforafrica.storycheck.intro.TourActivity;


/**
 * Created by zzt on 2015/1/19.
 */
public class MyPostsActivity extends AppCompatActivity {
    InboxLayoutListView inboxLayoutListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_posts);

        //show app tour
        startTour();


        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_launcher));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.my_posts));
        }


        //showcase
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ViewTarget target = new ViewTarget(R.id.addFab, this);
        ShowcaseView sv = new ShowcaseView.Builder(this)
                .setTarget(target)
                .setContentTitle(R.string.create_post)
                .setContentText(R.string.create_post_description)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        sv.setButtonPosition(lps);
        sv.setButtonText(getResources().getString(android.R.string.ok));

        final InboxBackgroundScrollView inboxBackgroundScrollView = (InboxBackgroundScrollView) findViewById(R.id.scroll);
        inboxLayoutListView = (InboxLayoutListView) findViewById(R.id.inboxlayout);
        inboxLayoutListView.setBackgroundScrollView(inboxBackgroundScrollView);//绑定scrollview
        inboxLayoutListView.setCloseDistance(50);
        inboxLayoutListView.setOnDragStateChangeListener(new OnDragStateChangeListener() {
            @Override
            public void dragStateChange(InboxLayoutBase.DragState state) {
                switch (state) {
                    case CANCLOSE:
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff5e5e5e));
                        getSupportActionBar().setTitle("back");
                        break;
                    case CANNOTCLOSE:
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xdd000000));
                        getSupportActionBar().setTitle("InboxLayout");
                        break;
                }
            }
        });
        inboxLayoutListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 50;
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
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.addFab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MyPostsActivity.this, CreatePostActivity.class);
                startActivity(i);
            }
        });


        init();

    }

    public void startTour(){
        startActivity(new Intent(MyPostsActivity.this, TourActivity.class));
    }


    private void init() {
        final LinearLayout dingdan = (LinearLayout) findViewById(R.id.ding_dan);
        dingdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxLayoutListView.openWithAnim(dingdan);
            }
        });

        final LinearLayout yuding = (LinearLayout) findViewById(R.id.yuding);
        yuding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxLayoutListView.openWithAnim(yuding);
            }
        });

        final LinearLayout tuijian = (LinearLayout) findViewById(R.id.tuijian);
        tuijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxLayoutListView.openWithAnim(tuijian);
            }
        });

        final LinearLayout member = (LinearLayout) findViewById(R.id.member);
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxLayoutListView.openWithAnim(member);
            }
        });

        final LinearLayout choujiang = (LinearLayout) findViewById(R.id.choujiang);
        choujiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxLayoutListView.openWithAnim(choujiang);
            }
        });

        final LinearLayout diyongquan = (LinearLayout) findViewById(R.id.diyongquan);
        diyongquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxLayoutListView.openWithAnim(diyongquan);//bottom item set true
            }
        });
    }
}


