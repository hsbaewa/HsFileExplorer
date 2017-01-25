package kr.co.hs.fileexplorer;

import android.os.FileObserver;

/**
 * Created by Bae on 2016-11-25.
 */
public interface HsFileObserverContant {
    int ACCESS = FileObserver.ACCESS;
    int ALL_EVENTS = FileObserver.ALL_EVENTS;
    int ATTRIB = FileObserver.ATTRIB;
    int CLOSE_NOWRITE = FileObserver.CLOSE_NOWRITE;
    int CLOSE_WRITE = FileObserver.CLOSE_WRITE;
    int CREATE = FileObserver.CREATE;
    int DELETE = FileObserver.DELETE;
    int DELETE_SELF = FileObserver.DELETE_SELF;
    int MODIFY = FileObserver.MODIFY;
    int MOVED_FROM = FileObserver.MOVED_FROM;
    int MOVED_TO = FileObserver.MOVED_TO;
    int MOVE_SELF = FileObserver.MOVE_SELF;
    int OPEN = FileObserver.OPEN;
}
