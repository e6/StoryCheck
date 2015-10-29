
package org.codeforafrica.storycheck.MaterialEditTextExtend;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by nick on 29/10/15.
 */
public class MinLengthValidator extends METValidator {

    public int minLength = 0;

    public MinLengthValidator(String errorMessage) {
        super(errorMessage);
    }

    public MinLengthValidator(String errorMessage, int i) {
        super(errorMessage);

        minLength = i;
    }

    @Override
    public boolean isValid(CharSequence text, boolean isEmpty) {
        if(text.length() < minLength){
            return false;
        }else{
            return true;
        }
    }
}
