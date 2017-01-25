package kr.co.hs.fileexplorer;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;

import kr.co.hs.app.HsFragment;
import kr.co.hs.app.OnRequestPermissionResult;
import kr.co.hs.content.HsPermissionChecker;
import kr.co.hs.icon.ExtensionIconManager;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-25.
 */

public abstract class HsFileExplorerFragment extends HsFragment implements HsRecyclerFileView.OnFileItemClickListener, OnRequestPermissionResult, HsRecyclerView.OnHolderMenuItemListener, FilenameFilter, HsRecyclerFileView.FileComparator{

    public static final int MODE_GRID = 1;
    public static final int MODE_LIST = 2;

    public static final String EXTRA_PATH = "Path";
    public static final String EXTRA_MODE = "Mode";


    HsRecyclerFileView mHsRecyclerFileView;
    GridLayoutManager mGridLayoutManager;
    Adapter mAdapter;

    String mPath;

    int mMode = MODE_LIST;

    @Override
    public void onCreateView(@Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        setContentView(R.layout.hs_fileexplorer_fragment_fileexplorer);

        Fresco.initialize(getContext());

        Bundle args = getArguments();
        mPath = args.getString(EXTRA_PATH);
        mMode = args.getInt(EXTRA_MODE, MODE_LIST);


        mHsRecyclerFileView = (HsRecyclerFileView) findViewById(R.id.HsRecyclerFileView);
        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mHsRecyclerFileView.setLayoutManager(mGridLayoutManager);



        String[] permission = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        HsPermissionChecker.requestPermissions(this, permission, 0 ,this);


        mAdapter = new Adapter();
        mHsRecyclerFileView.setAdapter(mAdapter);
        mHsRecyclerFileView.setOnFileItemClickListener(this);

        setHasOptionsMenu(true);
    }

    protected void setListMode(){
        mMode = MODE_LIST;
        mGridLayoutManager.setSpanCount(1);
        mAdapter.notifyItemRangeChanged(0, mAdapter.getHsItemCount());
        getHsActivity().supportInvalidateOptionsMenu();
    }

    protected void setGridMode(){
        mMode = MODE_GRID;
        mGridLayoutManager.setSpanCount(3);
        mAdapter.notifyItemRangeChanged(0, mAdapter.getHsItemCount());
        getHsActivity().supportInvalidateOptionsMenu();
    }

    public int getMode() {
        return mMode;
    }




    @Override
    public void onResult(int i, @NonNull String[] strings, @NonNull int[] ints, boolean b) {
        if(b){
            mHsRecyclerFileView.setCurrentPath(mPath, this, this);
        }
    }


    @Override
    public void onFileItemClick(HsRecyclerFileView var1, HsRecyclerFileView.FileViewHolder var2, View var3, int var4, File item) {
        if(item.isDirectory()){
            mHsRecyclerFileView.setCurrentPath(item.getAbsolutePath(), this, this);
        }
    }

    @Override
    public boolean onBackPressed() {
        if(mHsRecyclerFileView.getCurrentPath().equals(mPath)){
            return true;
        }else{
            File file = new File(mHsRecyclerFileView.getCurrentPath());
            mHsRecyclerFileView.setCurrentPath(file.getParent(), this, this);
            return false;
        }
    }

    class Adapter extends HsRecyclerFileView.FileAdapter<Holder>{

        @Override
        public Holder onCreateHsViewHolder(ViewGroup viewGroup, int i) {
            if(i == MODE_GRID)
                return new GridHolder(LayoutInflater.from(getContext()).inflate(R.layout.viewholder_filegrid, viewGroup, false));
            else if(i == MODE_LIST)
                return new ListHolder(LayoutInflater.from(getContext()).inflate(R.layout.viewholder_filelist, viewGroup, false));
            else
                return null;
        }

        @Override
        public void onBindHsViewHolder(Holder holder, int i, boolean b) {
            holder.onBind();
        }

        @Override
        public int getItemViewType(int position) {
            if(mMode == MODE_GRID)
                return MODE_GRID;
            else if(mMode == MODE_LIST)
                return MODE_LIST;

            return super.getItemViewType(position);
        }
    }



    class GridHolder extends Holder{
        CardView mCardView;

        public GridHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) findViewById(R.id.CardView);
            mCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mSimpleDraweeView.post(new Runnable() {
                        @Override
                        public void run() {
                            int w = mCardView.getMeasuredWidth();
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,w);
                            mSimpleDraweeView.setLayoutParams(params);
                        }
                    });
                }
            });
        }

        @Override
        public void setIcon(int res) {
            super.setIcon(res);
            mSimpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        }

        @Override
        public void onBind() {
            super.onBind();
            mSimpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        }
    }

    class ListHolder extends Holder{
        TextView mTextViewSecondary;
        TextView mTextViewThird;
        public ListHolder(View itemView) {
            super(itemView);
            mTextViewSecondary = (TextView) findViewById(R.id.TextViewSecondary);
            mTextViewThird = (TextView) findViewById(R.id.TextViewThird);
        }

        @Override
        public void onBind() {
            super.onBind();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(getFile().isDirectory()){
                mTextViewSecondary.setVisibility(View.INVISIBLE);
            }else{
                mTextViewSecondary.setText(Formatter.formatFileSize(getContext(), getFile().length()));
                mTextViewSecondary.setVisibility(View.VISIBLE);
            }
            mTextViewThird.setText(simpleDateFormat.format(getFile().lastModified()));
        }
    }


    class Holder extends HsRecyclerFileView.FileViewHolder{
        SimpleDraweeView mSimpleDraweeView;
        TextView mTextViewLabel;
        Toolbar mToolbar;
        public Holder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.SimpleDraweeViewIcon);
            mTextViewLabel = (TextView) findViewById(R.id.TextViewLabel);
            mToolbar = (Toolbar) findViewById(R.id.Menubar);
            mToolbar.inflateMenu(R.menu.menu_viewholder_filelist);
            setToolbar(mToolbar, HsFileExplorerFragment.this);
        }

        public void setIcon(int res){
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                    .path(String.valueOf(res))
                    .build();
            mSimpleDraweeView.setImageURI(uri);
        }

        public void onBind(){
            if(getFile().isDirectory()){
                setIcon(R.drawable.ic_folder);
            }else{
                String path = getFile().getAbsolutePath();
                Cursor cursor = getContext().getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.DATA},
                        MediaStore.Images.Media.DATA+"=?",
                        new String[]{path},
                        null
                );
                if(cursor != null && cursor.moveToFirst()){
                    Uri dataUri = Uri.fromFile(getFile());
                    ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(dataUri)
                            .setResizeOptions(new ResizeOptions(300, 300))
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(mSimpleDraweeView.getController())
                            .build();
                    mSimpleDraweeView.setController(controller);
                }else{
                    int icon = ExtensionIconManager.getInstance().getResource(getFile().getAbsolutePath());
                    setIcon(icon);
                }
            }

            mTextViewLabel.setText(getFile().getName());

        }
    }
}
