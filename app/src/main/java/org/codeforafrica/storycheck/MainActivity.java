package org.codeforafrica.storycheck;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;

import org.codeforafrica.storycheck.view.AvenirEditTextView;
import org.codeforafrica.storycheck.view.AvenirMaterialTextField;

public class MainActivity extends AppCompatActivity{

    private SweetSheet mReportCategoriesSheet;
    private RelativeLayout rl;
    private AvenirEditTextView editTitle;
    private RelativeLayout categoryPicker;
    private AvenirMaterialTextField editTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        rl = (RelativeLayout) findViewById(R.id.rl);
        editTitle = (AvenirEditTextView) findViewById(R.id.edit_title);
        categoryPicker = (RelativeLayout) findViewById(R.id.category);

        editTitleView = (AvenirMaterialTextField) findViewById(R.id.edit_title_view);
        editTitleView.getImage().setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        
        setupViewpager();

        categoryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = editTitle.getText().toString().length();
                if (count > 0) {
                    mReportCategoriesSheet.toggle();
                } else {
                    editTitleView.expand();
                    editTitleView.getImage().setColorFilter(Color.RED);
                }
            }
        });

        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = editTitle.getText().toString().length();
                if (count > 0) {
                    editTitleView.getImage().setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

    }

    private void setupViewpager() {

        mReportCategoriesSheet = new SweetSheet(rl);

        mReportCategoriesSheet.setMenuList(R.menu.menu_sweet);
        mReportCategoriesSheet.setDelegate(new ViewPagerDelegate());
        mReportCategoriesSheet.setBackgroundEffect(new DimEffect(0.5f));
        mReportCategoriesSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {

                Toast.makeText(MainActivity.this, menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    @Override
    public void onBackPressed() {

        if (mReportCategoriesSheet.isShow()) {
                mReportCategoriesSheet.dismiss();
        } else {
            super.onBackPressed();
        }


    }

}