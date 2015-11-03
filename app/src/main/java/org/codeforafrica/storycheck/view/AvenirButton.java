/**
 * Copyright (C) 2014 Twitter Inc and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codeforafrica.storycheck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import org.codeforafrica.storycheck.App;

public class AvenirButton extends Button {

    public AvenirButton(Context context) {
        super(context);
        init();
    }

    public AvenirButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvenirButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        if (isInEditMode()){
            return;
        }
        setTypeface(App.getInstance().getTypeface());
    }
}
