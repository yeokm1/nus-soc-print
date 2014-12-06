package com.yeokm1.nussocprintandroid.print_activities.printing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yeokm1.nussocprintandroid.R;

public class PrintingProgressView extends RelativeLayout {
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private ImageView mImageView;

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
        mTitleTextView = (TextView) findViewById(R.id.item_titleTextView);
        mDescriptionTextView = (TextView) findViewById(R.id.item_descriptionTextView);
        mImageView = (ImageView) findViewById(R.id.item_imageView);
    }

    public void setItem(PrintingProgressItem item) {
        mTitleTextView.setText(item.getTitle());
        mDescriptionTextView.setText(item.getDescription());
        // TODO: set up image URL
    }
    
    public ImageView getImageView () {
        return mImageView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getDescriptionTextView() {
        return mDescriptionTextView;
    }
}
