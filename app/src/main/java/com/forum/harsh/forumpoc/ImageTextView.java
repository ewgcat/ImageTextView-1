package com.forum.harsh.forumpoc;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Harsh Rastogi on 11/26/17.
 */

public class ImageTextView extends ScrollView {
    private MediaTextView textView;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textView = new MediaTextView(getContext());
        addView(textView);
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void cancelNetworkCalls() {
        textView.cancelNetworkCall();
    }
}
