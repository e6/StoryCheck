package org.codeforafrica.storycheck;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.codeforafrica.storycheck.MaterialEditTextExtend.MinLengthValidator;
import org.codeforafrica.storycheck.adapters.QuestionsListAdapter;
import org.codeforafrica.storycheck.fabprogresscircle.executor.ThreadExecutor;
import org.codeforafrica.storycheck.fabprogresscircle.interactor.MockAction;
import org.codeforafrica.storycheck.fabprogresscircle.interactor.MockActionCallback;
import org.codeforafrica.storycheck.helpers.OnSwipeTouchListener;
import org.codeforafrica.storycheck.view.AvenirTextView;

public class CreatePostActivity extends AppCompatActivity implements MockActionCallback, FABProgressListener {

    private FABProgressCircle fabProgressCircle;
    private boolean taskRunning;
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";


    private SweetSheet mReportCategoriesSheet;
    private RelativeLayout rl;
    private MaterialEditText editTitle;
    private RelativeLayout categoryPicker;
    private ListView questionsList;
    private AvenirTextView categoryName;
    private ImageView categoryThumb;
    private ShowcaseView sv;
    private AvenirTextView toolbarTitle;
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

        if(actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbarTitle.setText(getResources().getString(R.string.create_post));

        rl = (RelativeLayout) findViewById(R.id.rl);
        editTitle = (MaterialEditText) findViewById(R.id.edit_title);
        categoryPicker = (RelativeLayout) findViewById(R.id.category);
        questionsList = (ListView) findViewById(R.id.questions_list);
        categoryName = (AvenirTextView) findViewById(R.id.categoryName);
        categoryThumb = (ImageView) findViewById(R.id.categoryThumb);

        questionsList.setAdapter(new QuestionsListAdapter(getApplicationContext()));
        //set ontouch listener
        questionsList.setOnTouchListener(new OnSwipeTouchListener(this, questionsList) {

            @Override
            public void onSwipeRight(int pos) {

                showCheckedButton(pos, true);

            }

            @Override
            public void onSwipeLeft(int pos) {

                showCheckedButton(pos, false);

            }
        });

        //showcase
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ViewTarget target = new ViewTarget(R.id.uploadFab, this);
        sv = new ShowcaseView.Builder(this)
                .setTarget(target)
                .setContentTitle(R.string.upload_post)
                .setContentText(R.string.upload_post_description)
                .setStyle(R.style.CustomShowcaseTheme)
                .build();
        sv.setButtonPosition(lps);
        sv.setButtonText(getResources().getString(android.R.string.ok));

        setupViewpager();

        categoryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTitle.addValidator(new MinLengthValidator(getResources().getString(R.string.minimum_chars) + 5, 5));

                if (editTitle.validate()) {
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTitle.getWindowToken(), 0);

                    mReportCategoriesSheet.toggle();

                }
            }
        });

        //show animation while uploading
        fabProgressCircle = (FABProgressCircle) findViewById(R.id.uploadFab);
        attachListeners();

    }

    int position;
    ImageView checkedButton;
    private boolean showCheckedButton(int pos, boolean show) {
        position = pos;
        View child = questionsList.getChildAt(pos - questionsList.getFirstVisiblePosition());
        if (child != null) {

            checkedButton = (ImageView) child.findViewById(R.id.checked);
            if (checkedButton != null) {

                if(show) {
                    if (checkedButton.getVisibility() == View.GONE) {
                        Animation animation =
                                AnimationUtils.loadAnimation(this,
                                        R.anim.checked_slide_in_right);
                        checkedButton.startAnimation(animation);
                        checkedButton.setVisibility(View.VISIBLE);
                    } else {
                    /*
                    Animation animation =
                            AnimationUtils.loadAnimation(this,
                                    R.anim.checked_slide_out_left);
                    checkedButton.startAnimation(animation);
                    checkedButton.setVisibility(View.GONE);
                    */
                    }
                }else{
                        if (checkedButton.getVisibility() == View.VISIBLE) {

                            Animation animation =
                                    AnimationUtils.loadAnimation(this,
                                            R.anim.checked_slide_out_left);
                            checkedButton.startAnimation(animation);
                            checkedButton.setVisibility(View.GONE);

                        }
                }

                //click to edit
                checkedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreatePostActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View alertDialogView = inflater.inflate(R.layout.checklist_item_content, null);
                        alertDialog.setView(alertDialogView);

                        final EditText editContent = (EditText) alertDialogView.findViewById(R.id.editText);


                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(editContent!=null) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(editContent.getWindowToken(), 0);
                                }
                                dialog.cancel();
                            }
                        });

                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(editContent!=null) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(editContent.getWindowToken(), 0);
                                }
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();

                    }
                });
            }
            return true;
        }
        return false;
    }

    private void setupViewpager() {

        mReportCategoriesSheet = new SweetSheet(rl);

        mReportCategoriesSheet.setMenuList(R.menu.menu_sweet);
        mReportCategoriesSheet.setDelegate(new ViewPagerDelegate());
        mReportCategoriesSheet.setBackgroundEffect(new DimEffect(0.5f));
        mReportCategoriesSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {

                questionsList.setVisibility(View.VISIBLE);
                categoryName.setText(menuEntity1.title);
                categoryThumb.setImageDrawable(menuEntity1.icon);

                return true;
            }
        });

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
        fabProgressCircle.attachListener(this);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                if(sv.isShown()){
                    sv.hide();
                }else {
                    if (!taskRunning) {
                        fabProgressCircle.show();
                        runMockInteractor();
                    }
                }
            }
        });
    }

    private void runMockInteractor() {
        ThreadExecutor executor = new ThreadExecutor();
        executor.run(new MockAction(this));
        taskRunning = true;
    }

    @Override public void onMockActionComplete() {
        taskRunning = false;
        fabProgressCircle.beginFinalAnimation();
        //fabProgressCircle.hide();
    }

    @Override public void onFABProgressAnimationEnd() {
        Snackbar.make(fabProgressCircle, R.string.uploaded, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

}