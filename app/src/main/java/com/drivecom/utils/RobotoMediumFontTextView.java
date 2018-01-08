
package com.drivecom.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class RobotoMediumFontTextView extends AppCompatTextView {
    public RobotoMediumFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf"));
    }
}
