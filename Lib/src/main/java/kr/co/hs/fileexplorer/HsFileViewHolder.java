package kr.co.hs.fileexplorer;

import android.view.View;

import java.io.File;

import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-23.
 */

public class HsFileViewHolder extends HsRecyclerView.HsViewHolder {
    private File mFile;

    public HsFileViewHolder(View itemView) {
        super(itemView);
    }

    public void setFile(File file) {
        mFile = file;
    }

    public File getFile() {
        return mFile;
    }
}
