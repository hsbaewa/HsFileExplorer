package kr.co.hs.fileexplorer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * 생성된 시간 2017-01-24, Bae 에 의해 생성됨
 * 프로젝트 이름 : HsFileExplorer
 * 패키지명 : kr.co.hs.fileexplorer
 */

public class HsRecyclerFileView extends HsRecyclerView implements HsFileObserver.OnEventListener, Comparator<String>{

    private String mCurrentPath;
    private List<String> mFileList;
    private FilenameFilter mFilenameFilter;
    private FileComparator mFileComparator;
    private HsFileObserver mHsFileObserver;
    private OnFileItemClickListener mOnFileItemClickListener;


    public HsRecyclerFileView(Context context) {
        super(context);
        init();
    }

    public HsRecyclerFileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HsRecyclerFileView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mFileList = new ArrayList<>();
    }


    public void setOnFileItemClickListener(OnFileItemClickListener onFileItemClickListener) {
        mOnFileItemClickListener = onFileItemClickListener;
    }

    @Override
    protected void itemClick(ViewHolder viewHolder, View view, int position) {
        super.itemClick(viewHolder, view, position);
        if(mOnFileItemClickListener != null){
            File file = new File(mCurrentPath, mFileList.get(position));
            mOnFileItemClickListener.onFileItemClick(this, (FileViewHolder) viewHolder, view, position, file);
        }
    }


    @Override
    public void onEvent(HsFileObserver observer, int event, String path) {
        switch (event){
            case CREATE:
            {
                if(path != null){
                    File file = new File(path);
                    addFileItem(file.getName());
                }
                break;
            }
            case DELETE:
            case DELETE_SELF:
            {
                if(path != null){
                    File file = new File(path);
                    removeFileItem(file.getName());
                }
                break;
            }
            case CLOSE_WRITE:
            case CLOSE_NOWRITE:
            {
                if(path != null){
                    File file = new File(path);
                    changeFileItem(file.getName());
                }
                break;
            }
            case MODIFY:
            {
                if(path != null){
                    File file = new File(path);
                    changeFileItem(file.getName());
                }
                break;
            }
        }
    }

    @Override
    public int compare(String o1, String o2) {
        if(mFileComparator != null)
            return mFileComparator.compare(mCurrentPath, o1, o2);
        else
            return 0;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    public interface OnFileItemClickListener{
        void onFileItemClick(HsRecyclerFileView var1, FileViewHolder var2, View var3, int var4, File item);
    }



    public List<String> getFileList() {
        return mFileList;
    }

    public FilenameFilter getFilenameFilter() {
        return mFilenameFilter;
    }

    public FileComparator getFileComparator() {
        return mFileComparator;
    }

    public void setCurrentPath(String currentPath){
        setCurrentPath(currentPath, null, null);
    }

    public void setCurrentPath(String currentPath, FilenameFilter filenameFilter){
        setCurrentPath(currentPath, filenameFilter, null);
    }

    public void setCurrentPath(String currentPath, FileComparator fileComparator){
        setCurrentPath(currentPath, null, fileComparator);
    }

    public void setCurrentPath(String currentPath, FilenameFilter filenameFilter, FileComparator fileComparator) {
        post(new Runnable() {
            @Override
            public void run() {

            }
        });

        if(getAdapter() != null)
            getAdapter().notifyItemRangeRemoved(0, mFileList.size());


        File currentFile = new File(currentPath);
        if(currentFile.isDirectory()){
            mCurrentPath = currentPath;
        }else{
            mCurrentPath = currentFile.getParentFile().getAbsolutePath();
        }

        if(filenameFilter != null){
            mFilenameFilter = filenameFilter;
            String[] items = currentFile.list(filenameFilter);
            if(items != null){
                mFileList.clear();
                mFileList.addAll(Arrays.asList(items));
            }
        }else{
            String[] items = currentFile.list();
            if(items != null){
                mFileList.clear();
                mFileList.addAll(Arrays.asList(items));
            }
        }

        if(fileComparator != null){
            mFileComparator = fileComparator;
            Collections.sort(mFileList, this);
        }

        if(mHsFileObserver != null){
            mHsFileObserver.stopWatching();
            mHsFileObserver = null;
        }

        mHsFileObserver = new HsFileObserver(mCurrentPath, this);
        mHsFileObserver.startWatching();

        post(new Runnable() {
            @Override
            public void run() {

            }
        });

        if(getAdapter() != null)
            getAdapter().notifyItemRangeInserted(0, mFileList.size());


    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    private int getFileItemPosition(String name){
        int idx = mFileList.indexOf(name);
        if(idx < 0){
            File file = new File(name);
            name = file.getName();
            idx = mFileList.indexOf(name);
        }
        return idx;
    }
    private File getFileItem(int position){
        String fileItem = mFileList.get(position);
        File file = new File(getCurrentPath(), fileItem);
        return file;
    }

    private void addFileItem(String name){
        mFileList.add(name);
        if(mFileComparator != null)
            Collections.sort(mFileList, this);
        final int pos = getFileItemPosition(name);
        post(new Runnable() {
            @Override
            public void run() {
                getAdapter().notifyItemInserted(pos);
            }
        });
    }

    private void removeFileItem(String name){
        final int pos = mFileList.indexOf(name);
        if(pos >= 0){
            boolean result = mFileList.remove(name);
            if(result){
                post(new Runnable() {
                    @Override
                    public void run() {
                        getAdapter().notifyItemRemoved(pos);
                    }
                });

            }
        }
    }

    private void changeFileItem(String name){
        final int pos = mFileList.indexOf(name);
        if(pos >= 0){
            post(new Runnable() {
                @Override
                public void run() {
                    getAdapter().notifyItemChanged(pos);
                }
            });

        }
    }


    public abstract static class FileAdapter<ViewHoler extends FileViewHolder> extends HsAdapter<ViewHoler>{
        @Override
        public int getHsItemCount() {
            if(getRecyclerView() == null)
                return 0;
            else{
                HsRecyclerView hsRecyclerView = getRecyclerView();
                if(hsRecyclerView instanceof HsRecyclerFileView){
                    return ((HsRecyclerFileView) hsRecyclerView).mFileList.size();
                }else{
                    return 0;
                }
            }
        }
    }


    public abstract static class FileViewHolder extends HsViewHolder{
        public FileViewHolder(View itemView) {
            super(itemView);
        }
        public File getFile() {
            int position = getAdapterPosition();
            HsRecyclerView hsRecyclerView = getHsRecyclerView();
            if(hsRecyclerView instanceof HsRecyclerFileView){
                String currentPath = ((HsRecyclerFileView) hsRecyclerView).mCurrentPath;
                String name = ((HsRecyclerFileView) hsRecyclerView).mFileList.get(position);
                return new File(currentPath, name);
            }else{
                return null;
            }
        }
    }

    public interface FileComparator{
        int compare(String dir, String fileName1, String fileName2);
    }
}
