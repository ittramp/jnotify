package org.throne.other;

/**
 *
 * @author Administrator
 */
public class InotifyEvent {

    /**
     * File was accessed.
     */
    public static int IN_ACCESS = 0x00000001;
    /**
     * File was modified.
     */
    public static int IN_MODIFY = 0x00000002;
    /**
     * Metadata changed.
     */
    public static int IN_ATTRIB = 0x00000004;
    /**
     * Writtable file was closed.
     */
    public static int IN_CLOSE_WRITE = 0x00000008;
    /**
     * Unwrittable file closed.
     */
    public static int IN_CLOSE_NOWRITE = 0x00000010;
    /**
     * Close.
     */
    public static int IN_CLOSE = (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE);
    /**
     * File was opened.
     */
    public static int IN_OPEN = 0x00000020;
    /**
     * File was moved from X.
     */
    public static int IN_MOVED_FROM = 0x00000040;
    /**
     * File was moved to Y.
     */
    public static int IN_MOVED_TO = 0x00000080;
    /**
     * Moves.
     */
    public static int IN_MOVE = (IN_MOVED_FROM | IN_MOVED_TO);
    /**
     * Subfile was created.
     */
    public static int IN_CREATE = 0x00000100;
    /**
     * Subfile was deleted.
     */
    public static int IN_DELETE = 0x00000200;
    /**
     * Self was deleted.
     */
    public static int IN_DELETE_SELF = 0x00000400;
    /**
     * Self was moved.
     */
    public static int IN_MOVE_SELF = 0x00000800;

    /** =======================Events sent by the kernel.  ===============*/

    /**
     * Backing fs was unmounted.
     */
    public static int IN_UNMOUNT = 0x00002000;
    /**
     * Event queued overflowed.
     */
    public static int IN_Q_OVERFLOW = 0x00004000;
    /**
     * File was ignored.
     */
    public static int IN_IGNORED = 0x00008000;

    /** =======================Special flags.=====================  */
    /**
     * Only watch the path if it is a directory.
     */
    public static int IN_ONLYDIR = 0x01000000;
    /**
     * Do not follow a sym link.
     */
    public static int IN_DONT_FOLLOW = 0x02000000;
    /**
     * Exclude events on unlinked objects.
     */
    public static int IN_EXCL_UNLINK = 0x04000000;
    /**
     * Add to the mask of an already existing watch.
     */
    public static int IN_MASK_ADD = 0x20000000;
    /**
     * Event occurred against dir.
     */
    public static int IN_ISDIR = 0x40000000;
    /**
     * Only send event once.
     */
    public static int IN_ONESHOT = 0x80000000;

    /**
     * All events which a program can wait on.
     */
    public static int IN_ALL_EVENTS = (IN_ACCESS | IN_MODIFY | IN_ATTRIB | IN_CLOSE_WRITE
            | IN_CLOSE_NOWRITE | IN_OPEN | IN_MOVED_FROM
            | IN_MOVED_TO | IN_CREATE | IN_DELETE
            | IN_DELETE_SELF | IN_MOVE_SELF);


    private Integer wd;
    private Integer mask;
    private Integer cookie;
    private Integer len;
    private String name;

    public InotifyEvent() {

    }

    public InotifyEvent(Integer wd, Integer mask, Integer cookie, Integer len, String name) {
        this.wd = wd;
        this.mask = wd;
        this.cookie = cookie;
        this.len = len;
        this.name = name;
    }

    public Integer getWd() {
        return wd;
    }

    public void setWd(Integer wd) {
        this.wd = wd;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }

    public Integer getCookie() {
        return cookie;
    }

    public void setCookie(Integer cookie) {
        this.cookie = cookie;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
