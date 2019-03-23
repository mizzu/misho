package com.miz.misho.Objects;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * TextView bubbles that contain the colored parts of speech of the first sense.
 */
public class PosTextView extends android.support.v7.widget.AppCompatTextView {
    public PosTextView(Context context, String text, Drawable d) {
        super(context);
        this.setBackgroundDrawable(d);
        this.setText("   "+text+"   ");
        //this.setGravity(TEXT_ALIGNMENT_CENTER);
        this.setTextSize(11);
        this.setPadding(3, 3, 3, 3);
    }
}
