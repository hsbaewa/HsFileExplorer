package kr.co.hs.fileexplorer.v2;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

import kr.co.hs.fileexplorer.*;
import kr.co.hs.icon.ExtensionIconManager;
import kr.co.hs.util.Logger;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-26.
 */

public class FileHolder extends HsFileRecyclerView.FileViewHolder {
    SimpleDraweeView mSimpleDraweeView;
    TextView mTextViewLabel;
    Toolbar mToolbar;


    public FileHolder(View itemView) {
        super(itemView);
        mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.SimpleDraweeViewIcon);
        mTextViewLabel = (TextView) findViewById(R.id.TextViewLabel);
        mToolbar = (Toolbar) findViewById(R.id.Menubar);
        mToolbar.setVisibility(View.GONE);
    }

    public FileHolder(View itemView, int menu, HsRecyclerView.OnHolderMenuItemListener onHolderMenuItemListener) {
        super(itemView);
        mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.SimpleDraweeViewIcon);
        mTextViewLabel = (TextView) findViewById(R.id.TextViewLabel);
        mToolbar = (Toolbar) findViewById(R.id.Menubar);
        setMenu(menu, onHolderMenuItemListener);
    }

    public void setMenu(int menu, HsRecyclerView.OnHolderMenuItemListener onHolderMenuItemListener){
        if(menu >= 0){
            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(menu);
        }
        if(onHolderMenuItemListener != null)
            setToolbar(mToolbar, onHolderMenuItemListener);

        mToolbar.setVisibility(View.VISIBLE);
    }

    public void setIcon(int res){
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(res))
                .build();
        mSimpleDraweeView.setImageURI(uri);
    }

    private String getImageThumbnailUri(String path){
        Cursor cursor = getContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media.DATA+"=?",
                new String[]{path},
                null
        );
        if(cursor != null && cursor.moveToFirst()){
            long idx = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));

            Cursor thumbCursor = getContext().getContentResolver().query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Images.Thumbnails.IMAGE_ID+"=?",
                    new String[]{String.valueOf(idx)},
                    null
            );
            if(thumbCursor != null && thumbCursor.moveToFirst()){
                String data = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                return data;
            }else{
                thumbCursor = getContext().getContentResolver().query(
                        MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI,
                        null,
                        MediaStore.Images.Thumbnails.IMAGE_ID+"=?",
                        new String[]{String.valueOf(idx)},
                        null
                );

                if(thumbCursor != null && thumbCursor.moveToFirst()){
                    String data = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    return data;
                }
            }
        }else{
            Logger.d("a");
        }

        return null;
    }

    private String getVideoThumbnailUri(String path){
        Cursor cursor = getContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Video.Media.DATA+"=?",
                new String[]{path},
                null
        );
        if(cursor != null && cursor.moveToFirst()){
            long idx = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));

            Cursor thumbCursor = getContext().getContentResolver().query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Video.Thumbnails.VIDEO_ID+"=?",
                    new String[]{String.valueOf(idx)},
                    null
            );
            if(thumbCursor != null && thumbCursor.moveToFirst()){
                String data = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                return data;
            }else{
                Logger.d("a");
            }
        }else{
            Logger.d("a");
        }
        return null;
    }

    public void onBind(){
        if(getFile().isDirectory()){
            setIcon(R.drawable.ic_folder);
        }else{
            /*
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
            */
//            String imgThumbnail = getImageThumbnailUri(getFile().getAbsolutePath());
            String thumbnail = null;
            if((thumbnail = getImageThumbnailUri(getFile().getAbsolutePath())) != null){
                Uri dataUri = Uri.fromFile(new File(thumbnail));
                ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(dataUri)
                        .setResizeOptions(new ResizeOptions(300, 300))
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(mSimpleDraweeView.getController())
                        .build();
                mSimpleDraweeView.setController(controller);
            }else if((thumbnail = getVideoThumbnailUri(getFile().getAbsolutePath())) != null){
                Uri dataUri = Uri.fromFile(new File(thumbnail));
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
