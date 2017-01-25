package kr.co.hs.fileexplorer.sample;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;

import kr.co.hs.fileexplorer.HsFileExplorerFragment;

/**
 * Created by Bae on 2017-01-25.
 */

public class SampleFileFragment extends HsFileExplorerFragment {

    public static SampleFileFragment newInstance(String path, int mode) {

        Bundle args = new Bundle();
        args.putString(EXTRA_PATH, path);
        args.putInt(EXTRA_MODE, mode);

        SampleFileFragment fragment = new SampleFileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(getMode() == MODE_GRID)
            inflater.inflate(kr.co.hs.fileexplorer.R.menu.menu_filegrid, menu);
        else if(getMode() == MODE_LIST)
            inflater.inflate(kr.co.hs.fileexplorer.R.menu.menu_filelist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == kr.co.hs.fileexplorer.R.id.Menu_ModeGrid){
            setGridMode();
        }else if(item.getItemId() == kr.co.hs.fileexplorer.R.id.Menu_ModeList){
            setListMode();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean accept(File dir, String name) {
        File file = new File(dir, name);
        if(file.isHidden())
            return false;
        else
            return true;
    }

    @Override
    public int compare(String dir, String fileName1, String fileName2) {
        File file1 = new File(dir, fileName1);
        File file2 = new File(dir, fileName2);

        if(file1.isDirectory() && !file2.isDirectory())
            return -1;
        else if(!file1.isDirectory() && file2.isDirectory())
            return 1;
        else
            return file1.compareTo(file2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem, int i) {
        return false;
    }
}
