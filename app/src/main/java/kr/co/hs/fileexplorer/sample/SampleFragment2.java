package kr.co.hs.fileexplorer.sample;

import android.os.Bundle;

import java.io.File;

import kr.co.hs.fileexplorer.v2.HsFileExplorerFragment;

/**
 * Created by Bae on 2017-01-26.
 */

public class SampleFragment2 extends HsFileExplorerFragment {

    public static HsFileExplorerFragment newInstance(String path, int mode) {

        Bundle args = new Bundle();
        args.putString(EXTRA_PATH, path);
        args.putInt(EXTRA_MODE, mode);

        SampleFragment2 fragment = new SampleFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean accept(File pathname) {
        if(pathname.isHidden())
            return false;
        else
            return true;
    }

    @Override
    public int compare(File o1, File o2) {
        if(o1.isDirectory() && !o2.isDirectory())
            return -1;
        else if(!o1.isDirectory() && o2.isDirectory())
            return 1;
        else
            return o1.compareTo(o2);
    }
}
