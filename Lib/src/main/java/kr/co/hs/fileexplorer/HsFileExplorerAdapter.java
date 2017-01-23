package kr.co.hs.fileexplorer;

import android.view.ViewGroup;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-23.
 */

public abstract class HsFileExplorerAdapter<ItemViewHolder extends HsFileViewHolder> extends HsRecyclerView.HsAdapter<ItemViewHolder> {
    public static final int VIEWTYPE_FOLDER = 1000;
    public static final int VIEWTYPE_FILE = 2000;

    private File mRootFile;
    private FileFilter mFileFilter;
    private Comparator<File> mComparator;
    private File[] mItems;

    public HsFileExplorerAdapter(File rootFile) {
        this(rootFile, null, null);
    }

    public HsFileExplorerAdapter(File rootFile, FileFilter fileFilter){
        this(rootFile, fileFilter, null);
    }

    public HsFileExplorerAdapter(File rootFile, Comparator<File> fileComparator){
        this(rootFile, null, fileComparator);
    }

    public HsFileExplorerAdapter(File rootFile, FileFilter fileFilter, Comparator<File> fileComparator) {
        mRootFile = rootFile;
        mFileFilter = fileFilter;
        mComparator = fileComparator;

        if(mFileFilter != null)
            mItems = mRootFile.listFiles(mFileFilter);
        else
            mItems = mRootFile.listFiles();

        if(mComparator != null){
            Arrays.sort(mItems, mComparator);
        }
    }

    @Override
    public ItemViewHolder onCreateHsViewHolder(ViewGroup viewGroup, int i) {
        return onCreateFileViewHolder(viewGroup, i);
    }

    @Override
    public void onBindHsViewHolder(ItemViewHolder holder, int i, boolean b) {
        onBindFileViewHolder(holder, i, b, mItems[i]);
    }

    @Override
    public int getHsItemCount() {
        return mItems.length;
    }

    @Override
    protected Object getItem(int position) {
        return mItems[position];
    }

    @Override
    public int getItemViewType(int position) {
        File file = (File) getItem(position);
        if(file.isDirectory())
            return VIEWTYPE_FOLDER;
        else
            return VIEWTYPE_FILE;
    }

    public abstract ItemViewHolder onCreateFileViewHolder(ViewGroup viewGroup, int fileType);
    public abstract void onBindFileViewHolder(ItemViewHolder holder, int position, boolean check, File file);
}
