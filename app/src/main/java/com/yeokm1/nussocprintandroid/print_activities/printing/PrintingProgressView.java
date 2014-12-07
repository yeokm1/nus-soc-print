package com.yeokm1.nussocprintandroid.print_activities.printing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yeokm1.nussocprintandroid.R;

public class PrintingProgressView extends RelativeLayout {
    private TextView titleTextView;
    private TextView subtitleTextView;
    private ProgressBar progressBar;

    private ImageView doneIcon;
    private ImageView errorIcon;

    private int MAX_PROGRESS_VALUE = 100;

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
        progressBar = (ProgressBar) findViewById(R.id.printing_progress_progressbar);
        doneIcon = (ImageView) findViewById(R.id.printing_progress_done_icon);
        errorIcon = (ImageView) findViewById(R.id.printing_progress_error_icon);
    }

    public void setItem(PrintingProgressItem item) {
        titleTextView.setText(item.getTitle());
        subtitleTextView.setText(item.getSubtitle());

        if(item.isProgressBarActive()){
            if(item.isProgressIndeterminate()){
                progressBar.setIndeterminate(true);
            } else {
                progressBar.setIndeterminate(false);
                progressBar.setProgress((int) (item.getProgressValue() * MAX_PROGRESS_VALUE));
            }
        } else {
            progressBar.setIndeterminate(false);
            progressBar.setProgress((int) (item.getProgressValue() * MAX_PROGRESS_VALUE));
        }



        if(item.isShowDoneIcon()){
            doneIcon.setVisibility(View.VISIBLE);
        } else {
            doneIcon.setVisibility(View.INVISIBLE);
        }

        if(item.isShowErrorIcon()){
            errorIcon.setVisibility(View.VISIBLE);
        } else {
            errorIcon.setVisibility(View.INVISIBLE);
        }


    }

}
