package kr.co.hs.fileexplorer.v2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-26.
 */

public class HsFileRecyclerView extends HsRecyclerView{
    public static final int MODE_GRID = 1;
    public static final int MODE_LIST = 2;


    private ArrayList<File> mFiles;
    private Comparator<File> mFileComparator;
    private GridLayoutManager mGridLayoutManager;
    private int mMode;
    private OnFileItemClickListener mOnFileItemClickListener;

    public HsFileRecyclerView(Context context) {
        super(context);
        init();
    }

    public HsFileRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HsFileRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mFiles = new ArrayList<>();
        setMode(MODE_LIST);
    }

    public void setMode(final int mode){
        mMode = mode;
        post(new Runnable() {
            @Override
            public void run() {
                if(mGridLayoutManager == null){
                    mGridLayoutManager = new GridLayoutManager(getContext(), 1);
                    mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    setLayoutManager(mGridLayoutManager);
                }


                switch (mode){
                    case MODE_LIST:{
                        mGridLayoutManager.setSpanCount(1);
                        if(getAdapter() != null)
                            getAdapter().notifyItemRangeChanged(0, getFiles().size());
                        break;
                    }
                    case MODE_GRID:{
                        mGridLayoutManager.setSpanCount(3);
                        if(getAdapter() != null)
                            getAdapter().notifyItemRangeChanged(0, getFiles().size());
                        break;
                    }
                }
            }
        });



    }

    public int getMode() {
        return mMode;
    }

    public File getFile(int position){
        return mFiles.get(position);
    }

    public ArrayList<File> getFiles() {
        return mFiles;
    }

    public Comparator<File> getFileComparator() {
        return mFileComparator;
    }

    public void setFileComparator(Comparator<File> fileComparator) {
        mFileComparator = fileComparator;

        Collections.sort(getFiles(), mFileComparator);

        notifyItemRangeChanged(0, getFiles().size());
    }

    public void setOnFileItemClickListener(OnFileItemClickListener onFileItemClickListener) {
        mOnFileItemClickListener = onFileItemClickListener;
    }

    @Override
    protected void itemClick(ViewHolder viewHolder, View view, int position) {
        super.itemClick(viewHolder, view, position);
        if(mOnFileItemClickListener != null)
            mOnFileItemClickListener.onFileItemClick(this, viewHolder, view, position, getFile(position));
    }

    protected void notifyItemRangeRemoved(final int start, final int range){
        post(new Runnable() {
            @Override
            public void run() {
                if(getAdapter() != null)
                    getAdapter().notifyItemRangeRemoved(start, range);
            }
        });
    }

    protected void notifyItemRangeInserted(final int start, final int range){
        post(new Runnable() {
            @Override
            public void run() {
                if(getAdapter() != null)
                    getAdapter().notifyItemRangeInserted(start, range);
            }
        });
    }

    protected void notifyItemRangeChanged(final int start, final int range){
        post(new Runnable() {
            @Override
            public void run() {
                if(getAdapter() != null)
                    getAdapter().notifyItemRangeChanged(start, range);
            }
        });
    }

    public void setFiles(ArrayList<File> list){
        if(list == null)
            return;

        if(getFiles() == null)
            return;

        int cnt = getFiles().size();
        if(getAdapter() != null)
            getAdapter().notifyItemRangeRemoved(0, cnt);
        getFiles().clear();

        getFiles().addAll(list);
        if(getFileComparator() != null)
            Collections.sort(getFiles(), getFileComparator());

        cnt = getFiles().size();
        if(getAdapter() != null)
            getAdapter().notifyItemRangeInserted(0, cnt);
    }

    protected void addFileItem(File file){
        boolean result = getFiles().add(file);
        if(result){
            if(getFileComparator() != null)
                Collections.sort(getFiles(), getFileComparator());

            final int idx = getFiles().indexOf(file);

            post(new Runnable() {
                @Override
                public void run() {
                    if(getAdapter() != null){
                        getAdapter().notifyItemInserted(idx);
                        scrollToPosition(idx);
                    }
                }
            });
        }
    }

    protected void removeFileItem(File file){
        int idx = getFiles().indexOf(file);
        if(idx >= 0){
            removeFileItem(idx);
        }
    }

    protected void removeFileItem(final int position){
        File file = getFiles().remove(position);
        if(file != null){
            post(new Runnable() {
                @Override
                public void run() {
                    if(getAdapter() != null)
                        getAdapter().notifyItemRemoved(position);
                }
            });
        }
    }

    protected void changeFileItem(File file){
        int idx = getFiles().indexOf(file);
        if(idx >= 0){
            changeFileItem(idx);
        }
    }

    protected void changeFileItem(final int position){
        File file = getFile(position);
        if(file != null){
            post(new Runnable() {
                @Override
                public void run() {
                    if(getAdapter() != null){
                        getAdapter().notifyItemChanged(position);
                        scrollToPosition(position);
                    }
                }
            });
        }
    }



    public abstract static class FileAdapter<ViewHoler extends FileHolder> extends HsAdapter<ViewHoler>{
        @Override
        public ViewHoler onCreateHsViewHolder(ViewGroup viewGroup, int i) {
            if(i == MODE_GRID)
                return onCreateGridHolder(viewGroup);
            else if(i == MODE_LIST)
                return onCreateListHolder(viewGroup);

            return null;
        }

        public abstract ViewHoler onCreateGridHolder(ViewGroup viewGroup);
        public abstract ViewHoler onCreateListHolder(ViewGroup viewGroup);

        @Override
        public int getItemViewType(int position) {
            HsRecyclerView hsRecyclerView = getRecyclerView();
            if(hsRecyclerView instanceof HsFileRecyclerView){
                return ((HsFileRecyclerView) hsRecyclerView).getMode();
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getHsItemCount() {
            if(getRecyclerView() == null)
                return 0;
            else{
                HsRecyclerView hsRecyclerView = getRecyclerView();
                if(hsRecyclerView instanceof HsFileRecyclerView){
                    return ((HsFileRecyclerView) hsRecyclerView).getFiles().size();
                }else{
                    return 0;
                }
            }
        }

        @Override
        protected Object getItem(int position) {
            HsRecyclerView recyclerView;
            if(getRecyclerView() != null && (recyclerView = getRecyclerView()) != null){
                if(recyclerView instanceof HsFileRecyclerView){
                    return ((HsFileRecyclerView) recyclerView).getFile(position);
                }
            }
            return super.getItem(position);
        }
    }

    public abstract static class FileViewHolder extends HsViewHolder{
        public FileViewHolder(View itemView) {
            super(itemView);
        }
        public File getFile() {
            int position = getAdapterPosition();
            HsRecyclerView hsRecyclerView = getHsRecyclerView();
            if(hsRecyclerView instanceof HsFileRecyclerView){
                return ((HsFileRecyclerView) hsRecyclerView).getFile(position);
            }else{
                return null;
            }
        }
    }


    public interface OnFileItemClickListener {
        void onFileItemClick(HsRecyclerView hsRecyclerView, ViewHolder viewHolder, View view, int i, File file);
    }
}
