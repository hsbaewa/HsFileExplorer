package kr.co.hs.fileexplorer.v2;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;

import kr.co.hs.fileexplorer.R;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-26.
 */

public class GridHolder extends FileHolder {
    CardView mCardView;

    public GridHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.viewholder_filegrid, parent, false));
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

    public GridHolder(Context context, ViewGroup parent, int menu, HsRecyclerView.OnHolderMenuItemListener onHolderMenuItemListener) {
        super(LayoutInflater.from(context).inflate(R.layout.viewholder_filegrid, parent, false), menu, onHolderMenuItemListener);
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
