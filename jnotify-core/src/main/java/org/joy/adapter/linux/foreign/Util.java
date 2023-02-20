package org.joy.adapter.linux.foreign;

import org.joy.core.JnotifyEventTypeEnum;

import java.util.List;

/**
 * @author tramp
 * @date 2023/2/17 9:48
 */
public class Util {

    /**
     * 获取事件说明
     *
     * @param mask Mask describing event
     * @return
     */
    public static String getMaskDesc(int mask) {
        String s = "";
        if ((mask & InotifyLinux.IN_ACCESS) != 0) {
            s += "IN_ACCESS, ";
        }
        if ((mask & InotifyLinux.IN_MODIFY) != 0) {
            s += "IN_MODIFY, ";
        }
        if ((mask & InotifyLinux.IN_ATTRIB) != 0) {
            s += "IN_ATTRIB, ";
        }
        if ((mask & InotifyLinux.IN_CLOSE_WRITE) != 0) {
            s += "IN_CLOSE_WRITE, ";
        }
        if ((mask & InotifyLinux.IN_CLOSE_NOWRITE) != 0) {
            s += "IN_CLOSE_NOWRITE, ";
        }
        if ((mask & InotifyLinux.IN_OPEN) != 0) {
            s += "IN_OPEN, ";
        }
        if ((mask & InotifyLinux.IN_MOVED_FROM) != 0) {
            s += "IN_MOVED_FROM, ";
        }
        if ((mask & InotifyLinux.IN_MOVED_TO) != 0) {
            s += "IN_MOVED_TO, ";
        }
        if ((mask & InotifyLinux.IN_CREATE) != 0) {
            s += "IN_CREATE, ";
        }
        if ((mask & InotifyLinux.IN_DELETE) != 0) {
            s += "IN_DELETE, ";
        }
        if ((mask & InotifyLinux.IN_DELETE_SELF) != 0) {
            s += "IN_DELETE_SELF, ";
        }
        if ((mask & InotifyLinux.IN_MOVE_SELF) != 0) {
            s += "IN_MOVE_SELF, ";
        }
        if ((mask & InotifyLinux.IN_UNMOUNT) != 0) {
            s += "IN_UNMOUNT, ";
        }
        if ((mask & InotifyLinux.IN_Q_OVERFLOW) != 0) {
            s += "IN_Q_OVERFLOW, ";
        }
        if ((mask & InotifyLinux.IN_IGNORED) != 0) {
            s += "IN_IGNORED, ";
        }
        return s;
    }

    /**
     * 通过用户参数获取linux中文件监听类型标识
     *
     * @param eventTypes
     * @param watchSubtree
     * @return
     */
    public static int getMaskFromEventType(List<JnotifyEventTypeEnum> eventTypes, boolean watchSubtree) {
        int mask = 0;
        for (JnotifyEventTypeEnum eventType : eventTypes) {
            switch (eventType) {
                case FILE_CREATED -> mask |= InotifyLinux.IN_CREATE;
                case FILE_DELETED -> {
                    mask |= InotifyLinux.IN_DELETE;
                    mask |= InotifyLinux.IN_DELETE_SELF;
                }
                case FILE_MODIFIED -> mask |= InotifyLinux.IN_MODIFY;
                case FILE_ATTRIB -> mask |= InotifyLinux.IN_ATTRIB;
                case FILE_RENAMED -> {
                    mask |= InotifyLinux.IN_MOVED_FROM;
                    mask |= InotifyLinux.IN_MOVED_TO;
                }
                case FILE_MOVED_FROM -> mask |= InotifyLinux.IN_MOVED_FROM;
                case FILE_MOVED_TO -> mask |= InotifyLinux.IN_MOVED_TO;
                default -> {
                    //do nothing
                }
            }
        }

        // if watching sub-dirs, listen on create anyway.
        // to know when new subdirectories are created.
        // these events should not reach the client code.
        if (watchSubtree) {
            mask |= InotifyLinux.IN_CREATE;
        }
        return mask;
    }

    public static boolean isIgnoreFileName(String fileName) {
        return fileName.equals("4913")
                || fileName.endsWith("~")
                || fileName.endsWith("swp")
                || fileName.endsWith("swx")
                ;
    }
}
