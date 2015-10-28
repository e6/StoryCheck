package org.codeforafrica.storycheck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import org.codeforafrica.storycheck.App;

/**
 * Created by nick on 28/10/15.
 */
public class AvenirEditTextView extends EditText {

    public AvenirEditTextView(Context context) {
        super(context);
        init();
    }

    public AvenirEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvenirEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()){
            return;
        }

        setTypeface(App.getInstance().getTypeface());
    }
}