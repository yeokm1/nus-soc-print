package com.yeokm1.nussocprintandroid.print_activities.printing;

public class PrintingProgressItem {

    private String title;
    private String subtitle;
    private boolean progressIndeterminate;
    private int progressValue;

    private boolean showDoneIcon;
    private boolean showErrorIcon;

    public PrintingProgressItem(String title, String subtitle, boolean showDoneIcon, boolean showErrorIcon) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.progressIndeterminate = true;
        this.showDoneIcon = showDoneIcon;
        this.showErrorIcon = showErrorIcon;
    }

    public PrintingProgressItem(String title, String subtitle, int progressValue, boolean showDoneIcon, boolean showErrorIcon) {
        this(title, subtitle, showDoneIcon, showErrorIcon);
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

    public boolean isShowDoneIcon() {
        return showDoneIcon;
    }

    public boolean isShowErrorIcon() {
        return showErrorIcon;
    }

}
