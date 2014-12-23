package com.yeokm1.nussocprintandroid.core;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.yeokm1.nussocprintandroid.R;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public class MyApplication extends Application{

    private String currentDocumentPath;
    private static final String TAG = "MyApplication";

    public String getCurrentDocumentPath() {
        return currentDocumentPath;
    }

    public void setCurrentDocumentUri(Uri newDocumentUri) {

        try {
            String path = FileUtils.getPath(this, newDocumentUri);

            if(path != null && isFileFormatSupported(path)) {
                currentDocumentPath = path;
            } else {
                Toast.makeText(this, R.string.print_file_not_supported, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            Toast.makeText(this, R.string.print_file_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFileFormatSupported(String path){
        if(path.contains(".")) {
            String extension = path.substring(path.lastIndexOf("."));

            if(extension == null || extension.length() == 0){
                return false;
            }

            if(extension.equalsIgnoreCase(".pdf")
                    || (extension.equalsIgnoreCase(".doc"))
                    || (extension.equalsIgnoreCase(".docx"))
                    || (extension.equalsIgnoreCase(".ppt"))
                    || (extension.equalsIgnoreCase(".pptx"))
                    || (extension.equalsIgnoreCase(".odt"))){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }




    }

    public void onCreate(){
        super.onCreate();
        FlurryFunctions.initFlurry(this);
        new Storage(getApplicationContext());
    }



}
