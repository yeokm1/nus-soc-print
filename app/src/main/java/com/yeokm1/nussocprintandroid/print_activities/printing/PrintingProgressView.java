package com.yeokm1.nussocprintandroid.print_activities.printing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yeokm1.nussocprintandroid.R;

public class PrintingProgressView extends RelativeLayout {
    private TextView titleTextView;
    private TextView subtitleTextView;

    public static PrintingProgressView inflate(ViewGroup parent) {
        PrintingProgressView itemView = (PrintingProgressView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.printing_progress_view, parent, false);
        return itemView;
    }

    public PrintingProgressView(Context c) {
        this(c, null);
    }

    public PrintingProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrintingProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.printing_progress_view_children, this, true);
        setupChildren();
    }

    private void setupChildren() {
       titleTextView = (TextView) findViewById(R.id.printing_progress_title);
       subtitleTextView= (TextView) findViewById(R.id.printing_progress_subtitle);
    }

    public void setItem(PrintingProgressItem item) {
        titleTextView.setText(item.getTitle());
        subtitleTextView.setText(item.getDescription());
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public TextView getDescriptionTextView() {
        return subtitleTextView;
    }
}
