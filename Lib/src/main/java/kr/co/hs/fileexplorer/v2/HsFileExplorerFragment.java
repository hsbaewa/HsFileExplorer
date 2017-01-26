package kr.co.hs.fileexplorer.v2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Comparator;

import kr.co.hs.app.HsFragment;
import kr.co.hs.app.OnRequestPermissionResult;
import kr.co.hs.content.HsDialogInterface;
import kr.co.hs.content.HsPermissionChecker;
import kr.co.hs.fileexplorer.R;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-26.
 */

public abstract class HsFileExplorerFragment extends HsFragment implements OnRequestPermissionResult, HsRecyclerView.OnHolderMenuItemListener, HsFileRecyclerView.OnFileItemClickListener, FileFilter, Comparator<File>{

    private static final int HD_ITEM_ADDED = 5000;
    private static final int HD_ITEM_REMOVED = 5001;

    public static final String EXTRA_PATH = "Path";
    public static final String EXTRA_MODE = "Mode";


    HsFileTreeRecyclerView mHsRecyclerFileView;
    Adapter mAdapter;
    TextView mTextViewEmpty;

    String mPath;
    int mMode;


    @Override
    public void onCreateView(@Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        setContentView(R.layout.hs_fileexplorer_fragment_fileexplorer);

        Fresco.initialize(getContext());

        Bundle args = getArguments();
        mPath = args.getString(EXTRA_PATH);
        mMode = args.getInt(EXTRA_MODE, HsFileRecyclerView.MODE_LIST);


        mHsRecyclerFileView = (HsFileTreeRecyclerView) findViewById(R.id.HsRecyclerFileView);
        mAdapter = new Adapter();
        mHsRecyclerFileView.setAdapter(mAdapter);
        mHsRecyclerFileView.setOnFileItemClickListener(this);
        mHsRecyclerFileView.setFileComparator(this);


        mTextViewEmpty = (TextView) findViewById(R.id.TextViewEmpty);
        mTextViewEmpty.setVisibility(View.GONE);


        String[] permission = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        HsPermissionChecker.requestPermissions(this, permission, 0 ,this);


        setHasOptionsMenu(true);
    }


    @Override
    public void onResult(int i, @NonNull String[] strings, @NonNull int[] ints, boolean b) {
        if(b){
            setCurrentPath(new File(mPath));
        }
    }

    private void setVisibleEmptyMessage(final int visible){
        mTextViewEmpty.post(new Runnable() {
            @Override
            public void run() {
                mTextViewEmpty.setText(R.string.EmptyFiles);
                if(mTextViewEmpty.getVisibility() != visible)
                    mTextViewEmpty.setVisibility(visible);
            }
        });

    }

    private void setCurrentPath(File path){
        mHsRecyclerFileView.setFiles(path, this);
        if(mHsRecyclerFileView.getFiles().size() == 0)
            setVisibleEmptyMessage(View.VISIBLE);
        else
            setVisibleEmptyMessage(View.GONE);
    }

    @Override
    public boolean onBackPressed() {
        if(mPath.equals(mHsRecyclerFileView.getCurrentPath().getAbsolutePath()))
            return true;
        else{
            setCurrentPath(mHsRecyclerFileView.getCurrentPath().getParentFile());
            return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(mHsRecyclerFileView.getMode() == HsFileRecyclerView.MODE_LIST){
            inflater.inflate(R.menu.menu_filelist, menu);
        }else if(mHsRecyclerFileView.getMode() == HsFileRecyclerView.MODE_GRID){
            inflater.inflate(R.menu.menu_filegrid, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.Menu_ModeList) {

            mHsRecyclerFileView.setMode(HsFileRecyclerView.MODE_LIST);
            getHsActivity().supportInvalidateOptionsMenu();

            return true;
        }else if(i == R.id.Menu_ModeGrid){

            mHsRecyclerFileView.setMode(HsFileRecyclerView.MODE_GRID);
            getHsActivity().supportInvalidateOptionsMenu();
            return true;
        }else if(i == R.id.Menu_CreateFolder){

            createFolder(mHsRecyclerFileView.getCurrentPath());
            return true;
        }else if(i==R.id.Menu_CreateFile){

            createFile(mHsRecyclerFileView.getCurrentPath());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(super.handleMessage(msg))
            return true;

        switch (msg.what){
            case HD_ITEM_ADDED:{
                String path = (String) msg.obj;

                mHsRecyclerFileView.addFileItem(new File(path));


                return true;
            }
            case HD_ITEM_REMOVED:{
                String path = (String) msg.obj;
                mHsRecyclerFileView.removeFileItem(new File(path));
                return true;
            }
            default:return false;
        }
    }

    protected void createFile(final File parentPath){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.CreateFileTitle);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hs_fileexplorer_layout_edittext, null);
        final EditText editText = (EditText) view.findViewById(R.id.EditText);
        editText.setHint(R.string.CreateFileHint);
        builder.setView(view);
        builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folderName = editText.getText().toString();
                File newFolder = new File(parentPath, folderName);
                boolean isSuccess;
                try {
                    isSuccess = newFolder.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }
                if(isSuccess){
                    sendMessage(HD_ITEM_ADDED, newFolder.getAbsolutePath());
                }else{
                    showAlertDialog(getString(R.string.CreateFileError));
                }
            }
        });
        builder.setNegativeButton(R.string.common_cancel, null);
        setDialog(builder.create());
        getDialog().show();
    }


    protected void createFolder(final File parentPath){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.CreateFolderTitle);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hs_fileexplorer_layout_edittext, null);
        final EditText editText = (EditText) view.findViewById(R.id.EditText);
        editText.setHint(R.string.CreateFolderHint);
        builder.setView(view);
        builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folderName = editText.getText().toString();
                File newFolder = new File(parentPath, folderName);
                if(newFolder.mkdir()){
                    sendMessage(HD_ITEM_ADDED, newFolder.getAbsolutePath());
                }else{
                    showAlertDialog(getString(R.string.CreateFolderError));
                }
            }
        });
        builder.setNegativeButton(R.string.common_cancel, null);
        setDialog(builder.create());
        getDialog().show();
    }


    private void deleteFile(final File target){
        String title = getString(R.string.DeleteFileTitle);
        String content = getString(R.string.DeleteFileDescription, target.getName());
        showAlertDialog(title, content, getString(R.string.common_ok), getString(R.string.common_cancel), new HsDialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:{
                        if(target.delete()){
                            sendMessage(HD_ITEM_REMOVED, target.getAbsolutePath());
                        }else{
                            showAlertDialog(getString(R.string.DeleteFileError));
                        }
                        break;
                    }
                }
            }
        });
    }

    private void renameFile(final File target){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.RenameFileTitle);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hs_fileexplorer_layout_edittext, null);
        final EditText editText = (EditText) view.findViewById(R.id.EditText);
        editText.setHint(R.string.RenameFileHint);
        String name = target.getName();
        editText.setText(name);

        int end = name.lastIndexOf(".");
        if(end >= 0){
            editText.setSelection(0, end);
        }else{
            editText.setSelection(0, name.length());
        }

        editText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        });


        builder.setView(view);
        builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
//                File newFolder = new File(parentPath, folderName);
                File renameToFile = new File(target, name);
                boolean success = target.renameTo(renameToFile);
                if(success){
                    sendMessage(HD_ITEM_REMOVED, target.getAbsolutePath());
                    sendMessage(HD_ITEM_ADDED, renameToFile.getAbsolutePath());
                }else{
                    showAlertDialog(getString(R.string.CreateFolderError));
                }
            }
        });
        builder.setNegativeButton(R.string.common_cancel, null);
        setDialog(builder.create());
        getDialog().show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem, int i) {
        if(menuItem.getItemId() == R.id.Menu_Delete){
            File item = mHsRecyclerFileView.getFile(i);
            deleteFile(item);
        }else if(menuItem.getItemId() == R.id.Menu_Rename){
            File item = mHsRecyclerFileView.getFile(i);
            renameFile(item);
        }
        return false;
    }

    @Override
    public void onFileItemClick(HsRecyclerView hsRecyclerView, RecyclerView.ViewHolder viewHolder, View view, int i, File file) {
        if(file.isDirectory())
            setCurrentPath(file);
    }



    class Adapter extends HsFileRecyclerView.FileAdapter<FileHolder>{

        @Override
        public FileHolder onCreateGridHolder(ViewGroup viewGroup) {
            return new GridHolder(getContext(), viewGroup, R.menu.menu_viewholder_filelist, HsFileExplorerFragment.this);
        }

        @Override
        public FileHolder onCreateListHolder(ViewGroup viewGroup) {
            return new ListHolder(getContext(), viewGroup, R.menu.menu_viewholder_filelist, HsFileExplorerFragment.this);
        }

        @Override
        public void onBindHsViewHolder(FileHolder holder, int i, boolean b) {
            holder.onBind();
        }
    }
}
