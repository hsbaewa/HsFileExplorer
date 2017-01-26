package kr.co.hs.fileexplorer.v2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import kr.co.hs.fileexplorer.HsFileObserver;

/**
 * Created by Bae on 2017-01-27.
 */

public class HsFileTreeRecyclerView extends HsFileRecyclerView
//        implements HsFileObserver.OnEventListener
{

    private File mCurrentPath;
//    private HsFileObserver mHsFileObserver;

    public HsFileTreeRecyclerView(Context context) {
        super(context);
    }

    public HsFileTreeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HsFileTreeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFiles(File currentPath, FileFilter fileFilter){
        mCurrentPath = currentPath;

        /*
        if(mHsFileObserver != null){
            mHsFileObserver.stopWatching();
        }
        try{
            mHsFileObserver = new HsFileObserver(mCurrentPath, this);
            mHsFileObserver.startWatching();
        }catch (Exception e){

        }
        */

        File[] fileArr = mCurrentPath.listFiles(fileFilter);
        ArrayList<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(fileArr));
        setFiles(files);
    }

    public File getCurrentPath() {
        return mCurrentPath;
    }

    /*
    @Override
    public void onEvent(HsFileObserver observer, int event, String path) {
        switch (event){
            case CREATE:
            {
                if(path != null){
                    File file = new File(path);
                    addFileItem(file);
                }
                break;
            }
            case DELETE:
            case DELETE_SELF:
            {
                if(path != null){
                    File file = new File(path);
                    removeFileItem(file);
                }
                break;
            }
            case CLOSE_WRITE:
            case CLOSE_NOWRITE:
            {
                if(path != null){
                    File file = new File(path);
                    changeFileItem(file);
                }
                break;
            }
            case MODIFY:
            {
                if(path != null){
                    File file = new File(path);
                    changeFileItem(file);
                }
                break;
            }
        }
    }
    */

}
