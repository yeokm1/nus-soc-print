package com.yeokm1.nussocprintandroid.print_activities.printing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class PrintingProgressItemAdapter extends ArrayAdapter<PrintingProgressItem> {
    
    public PrintingProgressItemAdapter(Context c, List<PrintingProgressItem> items) {
        super(c, 0, items);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PrintingProgressView itemView = (PrintingProgressView)convertView;
        if (null == itemView)
            itemView = PrintingProgressView.inflate(parent);
        itemView.setItem(getItem(position));
        return itemView;
    }

}
