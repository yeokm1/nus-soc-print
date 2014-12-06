package com.yeokm1.nussocprintandroid.print_activities.printing;

public class PrintingProgressItem {

    private String title;
    private String subtitle;
    private boolean progressIndeterminate;
    private int progressValue;

    public PrintingProgressItem(String title, String subtitle) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.progressIndeterminate = true;
    }

    public PrintingProgressItem(String title, String subtitle, int progressValue) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.progressValue = progressValue;
        this.progressIndeterminate = false;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isProgressIndeterminate() {
        return progressIndeterminate;
    }

    public int getProgressValue() {
        return progressValue;
    }

}
