package kr.co.hs.fileexplorer.sample;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import kr.co.hs.app.HsActivity;
import kr.co.hs.app.OnRequestPermissionResult;
import kr.co.hs.content.HsPermissionChecker;
import kr.co.hs.fileexplorer.HsFileExplorerFragment;
import kr.co.hs.fileexplorer.HsRecyclerFileView;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * 생성된 시간 2017-01-24, Bae 에 의해 생성됨
 * 프로젝트 이름 : HsFileExplorer
 * 패키지명 : kr.co.hs.fileexplorer.sample
 */

public class SampleActivity extends HsActivity implements HsRecyclerView.OnItemClickListener{

    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mToolbar = (Toolbar) findViewById(R.id.Toolbar);
        setSupportActionBar(mToolbar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.LinearLayoutContents, HsFileExplorerFragment.newInstance(Environment.getExternalStorageDirectory().getAbsolutePath()));
        transaction.commit();

    }

    @Override
    public void onItemClick(HsRecyclerView hsRecyclerView, RecyclerView.ViewHolder viewHolder, View view, int i) {

    }


    class SampleFileFilter implements FileFilter{
        @Override
        public boolean accept(File pathname) {
            if(pathname.isHidden())
                return false;
            else
                return true;
        }
    }

    class SampleFileNameFilter implements FilenameFilter{

        @Override
        public boolean accept(File dir, String name) {
            File item = new File(dir, name);
            if(item.isHidden())
                return false;
            else
                return true;
        }
    }

    class FileCompare implements HsRecyclerFileView.FileComparator{

        @Override
        public int compare(String dir, String fileName1, String fileName2) {
            File file1 = new File(dir, fileName1);
            File file2 = new File(dir, fileName2);
            if(file1.isDirectory() && !file2.isDirectory()){
                return -1;
            }else if(!file1.isDirectory() && file2.isDirectory()){
                return 1;
            }else{
                return file1.compareTo(file2);
            }
        }
    }




    class Adapter extends HsRecyclerFileView.FileAdapter<FileItemHolder> {

        @Override
        public FileItemHolder onCreateHsViewHolder(ViewGroup viewGroup, int i) {
            return new FileItemHolder(LayoutInflater.from(getContext()).inflate(R.layout.viewholder_item, viewGroup, false));
        }

        @Override
        public void onBindHsViewHolder(FileItemHolder holder, int i, boolean b) {
            holder.onBind();
        }
    }

    class FileItemHolder extends HsRecyclerFileView.FileViewHolder{
        TextView mTextView;
        public FileItemHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) findViewById(R.id.TextViewLabel);
        }

        public void onBind(){
            mTextView.setText(getFile().getName());
        }
    }
}
