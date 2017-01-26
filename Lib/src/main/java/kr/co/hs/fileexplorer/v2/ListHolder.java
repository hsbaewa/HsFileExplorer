package kr.co.hs.fileexplorer.v2;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import kr.co.hs.fileexplorer.R;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-26.
 */

public class ListHolder extends FileHolder {
    TextView mTextViewSecondary;
    TextView mTextViewThird;

    public ListHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.viewholder_filelist, parent, false));
        mTextViewSecondary = (TextView) findViewById(R.id.TextViewSecondary);
        mTextViewThird = (TextView) findViewById(R.id.TextViewThird);
    }

    public ListHolder(Context context, ViewGroup parent, int menu, HsRecyclerView.OnHolderMenuItemListener onHolderMenuItemListener) {
        super(LayoutInflater.from(context).inflate(R.layout.viewholder_filelist, parent, false), menu, onHolderMenuItemListener);
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
