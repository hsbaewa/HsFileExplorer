package kr.co.hs.fileexplorer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-26.
 */

public class HsFileView extends HsRecyclerView {
    private List<String> mFileList;
    private FileComparator mFileComparator;

    public HsFileView(Context context) {
        super(context);
        init();
    }

    public HsFileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HsFileView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mFileList = new ArrayList<>();
    }


    public List<String> getFileList() {
        return mFileList;
    }
    public FileComparator getFileComparator() {
        return mFileComparator;
    }



    public interface FileComparator{
        int compare(String dir, String fileName1, String fileName2);
    }
}
