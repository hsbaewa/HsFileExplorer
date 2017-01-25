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

public class HsRecyclerFileView extends HsFileView implements HsFileObserver.OnEventListener, Comparator<String>{

    private String mCurrentPath;
    private FilenameFilter mFilenameFilter;
    private HsFileObserver mHsFileObserver;
    private OnFileItemClickListener mOnFileItemClickListener;


    public HsRecyclerFileView(Context context) {
        super(context);
    }

    public HsRecyclerFileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HsRecyclerFileView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    public void setOnFileItemClickListener(OnFileItemClickListener onFileItemClickListener) {
        mOnFileItemClickListener = onFileItemClickListener;
    }

    @Override
    protected void itemClick(ViewHolder viewHolder, View view, int position) {
        super.itemClick(viewHolder, view, position);
        if(mOnFileItemClickListener != null){
            File file = new File(mCurrentPath, getFileList().get(position));
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
        if(getFileComparator() != null)
            return getFileComparator().compare(mCurrentPath, o1, o2);
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





    public FilenameFilter getFilenameFilter() {
        return mFilenameFilter;
    }



    public void setCurrentPath(String currentPath, OnCurrentPathResultListener listener){
        setCurrentPath(currentPath, null, null, listener);
    }

    public void setCurrentPath(String currentPath, FilenameFilter filenameFilter, OnCurrentPathResultListener listener){
        setCurrentPath(currentPath, filenameFilter, null, listener);
    }

    public void setCurrentPath(String currentPath, FileComparator fileComparator, OnCurrentPathResultListener listener){
        setCurrentPath(currentPath, null, fileComparator, listener);
    }

    public void setCurrentPath(String currentPath, FilenameFilter filenameFilter, FileComparator fileComparator, OnCurrentPathResultListener listener) {
        if(getAdapter() != null)
            getAdapter().notifyItemRangeRemoved(0, getFileList().size());


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
                getFileList().clear();
                getFileList().addAll(Arrays.asList(items));
            }
        }else{
            String[] items = currentFile.list();
            if(items != null){
                getFileList().clear();
                getFileList().addAll(Arrays.asList(items));
            }
        }

        if(fileComparator != null){
            getFileComparator() = fileComparator;
            Collections.sort(getFileList(), this);
        }

        if(mHsFileObserver != null){
            mHsFileObserver.stopWatching();
            mHsFileObserver = null;
        }

        mHsFileObserver = new HsFileObserver(mCurrentPath, this);
        mHsFileObserver.startWatching();

        if(getAdapter() != null)
            getAdapter().notifyItemRangeInserted(0, getFileList().size());

        listener.onCurrentPathResult(mCurrentPath, getFileList());
    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    private int getFileItemPosition(String name){
        int idx = getFileList().indexOf(name);
        if(idx < 0){
            File file = new File(name);
            name = file.getName();
            idx = getFileList().indexOf(name);
        }
        return idx;
    }
    private File getFileItem(int position){
        String fileItem = getFileList().get(position);
        File file = new File(getCurrentPath(), fileItem);
        return file;
    }

    private void addFileItem(String name){
        getFileList().add(name);
        if(getFileComparator() != null)
            Collections.sort(getFileList(), this);
        final int pos = getFileItemPosition(name);
        post(new Runnable() {
            @Override
            public void run() {
                getAdapter().notifyItemInserted(pos);
            }
        });
    }

    private void removeFileItem(String name){
        final int pos = getFileList().indexOf(name);
        if(pos >= 0){
            boolean result = getFileList().remove(name);
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
        final int pos = getFileList().indexOf(name);
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
                    return ((HsRecyclerFileView) hsRecyclerView).getFileList().size();
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
                String name = ((HsRecyclerFileView) hsRecyclerView).getFileList().get(position);
                return new File(currentPath, name);
            }else{
                return null;
            }
        }
    }



    public interface OnCurrentPathResultListener{
        void onCurrentPathResult(String currentPath, List<String> filelist);
    }
}
