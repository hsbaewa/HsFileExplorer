package kr.co.hs.fileexplorer;

import android.os.FileObserver;

import java.io.File;
import java.io.IOException;

import kr.co.hs.util.Logger;


/**
 * Created by Bae on 2016-11-23.
 */
public class HsFileObserver extends FileObserver{
    private File targetFile;
    private File parentFile;
    private OnEventListener listener;


    public HsFileObserver(File path, OnEventListener listener) throws IOException {
        this(path.getCanonicalPath(), listener);
    }
    public HsFileObserver(File path, int mask, OnEventListener listener) throws IOException {
        this(path.getCanonicalPath(), mask, listener);
    }
    public HsFileObserver(String path, OnEventListener listener) {
        this(path, ALL_EVENTS, listener);
    }
    public HsFileObserver(String path, int mask, OnEventListener listener) {
        super(path, mask);
        this.listener = listener;
        targetFile = new File(path);
        if(targetFile.isDirectory()){
            setParentFile(targetFile);
        }else{
            setParentFile(targetFile.getParentFile());
        }
    }


    private void setFile(File file){
        this.targetFile = file;
    }
    private void setParentFile(File file){
        this.parentFile = file;
    }
    public File getParentFile(){
        return this.parentFile;
    }
    public String getParent(){
        return this.parentFile.getAbsolutePath();
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof HsFileObserver){
            HsFileObserver observer = (HsFileObserver) obj;
            if(observer.toString().equals(toString())){
                return true;
            }else{
                return false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return this.targetFile.getAbsolutePath();
    }

    @Override
    public void onEvent(int i, String s) {
        if(s == null)
            return;
        if(listener != null){
            String path = String.format("%s/%s", getParent(), s);
            Logger.d(String.format("path : %s", path));
            listener.onEvent(this, i, path);
        }
    }

    public interface OnEventListener extends HsFileObserverContant{
        void onEvent(HsFileObserver observer, int event, String path);
    }
}
