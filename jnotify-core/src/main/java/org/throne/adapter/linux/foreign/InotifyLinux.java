package org.throne.adapter.linux.foreign;

import jdk.incubator.foreign.*;
import org.throne.Jnotify;
import org.throne.adapter.linux.exception.JnotifyExceptionLinux;
import org.throne.adapter.linux.exception.JnotifySystemFunctionErrorException;
import org.throne.core.exception.JnotifyException;
import org.throne.core.exception.JnotifyFunctionNotExist;
import org.throne.core.exception.JnotifyHandleInvokeErrorException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Optional;

import static jdk.incubator.foreign.CLinker.C_INT;
import static jdk.incubator.foreign.CLinker.C_POINTER;

/**
 * linux notify 函数封装类
 * 通过调取Linux的inotify方法以及相关方法实现对应功能
 *
 * @author tramp
 */
public final class InotifyLinux implements AutoCloseable {

    private static final int FILE_DESCRIPTOR;

    static {
        try {
            FILE_DESCRIPTOR = init();
            loopRead(FILE_DESCRIPTOR);
        } catch (JnotifyException e) {
            throw new RuntimeException(e);
        }
    }

    /**===== the following are legal, implemented events that user-space can watch for ====*/
    /**
     * File was accessed
     */
    public static final int IN_ACCESS = 0x00000001;
    /**
     * File was modified
     */
    public static final int IN_MODIFY = 0x00000002;
    /**
     * Metadata changed
     */
    public static final int IN_ATTRIB = 0x00000004;
    /**
     * Writtable file was closed
     */
    public static final int IN_CLOSE_WRITE = 0x00000008;
    /**
     * Unwrittable file closed
     */
    public static final int IN_CLOSE_NOWRITE = 0x00000010;
    /**
     * File was opened
     */
    public static final int IN_OPEN = 0x00000020;
    /**
     * File was moved from X
     */
    public static final int IN_MOVED_FROM = 0x00000040;
    /**
     * File was moved to Y
     */
    public static final int IN_MOVED_TO = 0x00000080;
    /**
     * Subfile was created
     */
    public static final int IN_CREATE = 0x00000100;
    /**
     * Subfile was deleted
     */
    public static final int IN_DELETE = 0x00000200;
    /**
     * Self was deleted
     */
    public static final int IN_DELETE_SELF = 0x00000400;
    /**
     * Self was moved
     */
    public static final int IN_MOVE_SELF = 0x00000800;

    /**=========== the following are legal events. they are sent as needed to any watch============ */
    /**
     * Backing fs was unmounted
     */
    public static final int IN_UNMOUNT = 0x00002000;
    /**
     * Event queued overflowed
     */
    public static final int IN_Q_OVERFLOW = 0x00004000;
    /**
     * File was ignored
     * file was remove from inotify or file system unmount or else
     */
    public static final int IN_IGNORED = 0x00008000;

    /** helper events */
    /**
     * close
     */
    public static final int IN_CLOSE = (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE);
    /**
     * moves
     */
    public static final int IN_MOVE = (IN_MOVED_FROM | IN_MOVED_TO);

    /** special flags */
    /**
     * event occurred against
     * dir
     */
    public static final int IN_ISDIR = 0x40000000;
    /**
     * only send event once
     */
    public static final int IN_ONESHOT = 0x80000000;

    /**
     * All of the events - we build the list by hand so that we can add flags in
     * the future and not break backward compatibility. Apps will get only the
     * events that they originally wanted. Be sure to add new events here!
     */
    public static final int IN_ALL_EVENT = (IN_ACCESS | IN_MODIFY | IN_ATTRIB | IN_CLOSE_WRITE
            | IN_CLOSE_NOWRITE | IN_OPEN | IN_MOVED_FROM | IN_MOVED_TO | IN_DELETE | IN_CREATE | IN_DELETE_SELF);


    private static InotifyLinuxActuator inotifyLinuxActuator;


    /**
     * 初始化文件监听器
     *
     * @return
     */
    private static Integer init() throws JnotifyExceptionLinux, JnotifySystemFunctionErrorException {
        Jnotify.getLogger().debug("start inotify_init %n");
        MemoryAddress inotifyInit = CLinker.systemLookup().lookup("inotify_init").get();
        if (inotifyInit.equals(Optional.empty())) {
            throw new JnotifyFunctionNotExist("inotify_init");
        }
        MethodHandle inotifyInitHandle = CLinker.getInstance().downcallHandle(
                inotifyInit,
                MethodType.methodType(int.class),
                FunctionDescriptor.of(C_INT)
        );
        Integer fd = 0;
        try {
            var result = inotifyInitHandle.invoke();
            fd = (Integer) result;
            Jnotify.getLogger().debug(String.format("result is %d%n", fd));

        } catch (Throwable e) {
            throw new JnotifyExceptionLinux("inotify_init error ;" + e, 1000);
        }
        if (fd == -1) {
            throw SystemErrorLinux.getJnotifySystemFunctionErrorException();
        } else {
            return fd;
        }
    }

    public static Integer addWatch(String watchPath, Integer mask) throws JnotifySystemFunctionErrorException {
        Jnotify.getLogger().debug("start inotify_add_watch %n");
        MemoryAddress inotifyAddWatch = CLinker.systemLookup().lookup("inotify_add_watch").get();
        if (inotifyAddWatch.equals(Optional.empty())) {
            throw new JnotifyFunctionNotExist("inotify_add_watch");
        }
        MethodType methodType = MethodType.methodType(int.class, int.class, MemoryAddress.class, int.class);
        FunctionDescriptor functionDescriptor = FunctionDescriptor.of(C_INT, C_INT, C_POINTER, C_INT);
        MethodHandle handle = CLinker.getInstance().downcallHandle(inotifyAddWatch, methodType, functionDescriptor);
        MemorySegment path = CLinker.toCString(watchPath, ResourceScope.newImplicitScope());
        int watchId = 0;
        try {
            var out = handle.invoke(FILE_DESCRIPTOR, path.address(), mask);
            watchId = (int) out;
            Jnotify.getLogger().debug(String.format(" inotify_add_watch result is  [%d]%n", out));
        } catch (Throwable e) {
            throw new JnotifyHandleInvokeErrorException("inotify_add_watch", e);
        }
        if (watchId == -1) {
            throw SystemErrorLinux.getJnotifySystemFunctionErrorException();
        }
        return watchId;
    }

    public static void removeWatch(Integer watchDescriptor) throws JnotifySystemFunctionErrorException {
        Jnotify.getLogger().debug("start inotify_rm_watch %n");
        MemoryAddress inotifyRemoveWatch = CLinker.systemLookup().lookup("inotify_rm_watch").get();
        if (inotifyRemoveWatch.equals(Optional.empty())) {
            throw new JnotifyFunctionNotExist("inotify_rm_watch");
        }
        MethodType methodType = MethodType.methodType(int.class, int.class, int.class);
        FunctionDescriptor functionDescriptor = FunctionDescriptor.of(C_INT, C_INT, C_INT);
        MethodHandle handle = CLinker.getInstance().downcallHandle(inotifyRemoveWatch, methodType, functionDescriptor);
        int result = 0;
        try {
            var out = handle.invoke(FILE_DESCRIPTOR, watchDescriptor);
            result = (int) out;
        } catch (Throwable e) {
            throw new JnotifyHandleInvokeErrorException("inotify_rm_watch", e);
        }
        if (result == -1) {
            throw SystemErrorLinux.getJnotifySystemFunctionErrorException();
        }
    }

    /**
     * 循环读取
     *
     * @param fd
     */
    private static void loopRead(Integer fd) {
        Thread thread = new Thread("jnotify thread") {
            @Override
            public void run() {
                try (ResourceScope scope = ResourceScope.newConfinedScope()) {
                    Integer bufLength = 4096;
                    MemorySegment memorySegment = MemorySegment.allocateNative(bufLength, scope);
                    MemoryAddress read = CLinker.systemLookup().lookup("read").get();
                    MethodType methodType = MethodType.methodType(int.class, int.class, MemoryAddress.class, int.class);
                    if (methodType.equals(Optional.empty())) {
                        throw new JnotifyFunctionNotExist("read");
                    }
                    FunctionDescriptor functionDescriptor = FunctionDescriptor.of(C_INT, C_INT, C_POINTER, C_INT);
                    MethodHandle handle = CLinker.getInstance().downcallHandle(read, methodType, functionDescriptor);
                    while (true) {
                        Integer length = 0;
                        try {
                            length = (Integer) handle.invoke(fd, memorySegment.address(), bufLength);
                            Jnotify.getLogger().debug(String.format("读取后的消息结果长度：【%d】%n", length));
                        } catch (Throwable e) {
                            throw new JnotifyHandleInvokeErrorException("read", e);
                        }
                        Integer offset = 0;
                        while (offset < length) {
                            try {
                                offset = parseOneEvent(memorySegment, offset);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("解析消息内存出错，不可预知且不应发生的异常" + e.getMessage());
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (Exception e) {
                    Jnotify.getLogger().debug(e.toString());
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 解析一个事件内容，并返回下一个事件的字节偏移量
     *
     * @param memorySegment 内存块
     * @param offset        初始偏移量
     * @return
     */
    private static Integer parseOneEvent(MemorySegment memorySegment, Integer offset) {
        int wd = MemoryAccess.getIntAtOffset(memorySegment, offset + 0);
        Jnotify.getLogger().debug(String.format("尝试获取的wd 是：%d%n", wd));
        int mask = MemoryAccess.getIntAtOffset(memorySegment, offset + 4);
        Jnotify.getLogger().debug(String.format("尝试获取的mask 是：%d%n", mask));
        int cookie = MemoryAccess.getIntAtOffset(memorySegment, offset + 8);
        Jnotify.getLogger().debug(String.format("try get cookie is:%d%n", cookie));
        int len = MemoryAccess.getIntAtOffset(memorySegment, offset + 12);
        Jnotify.getLogger().debug(String.format("try get len is :%d%n", len));
        MemoryAddress memoryAddress = memorySegment.address().addOffset(offset + 16);
        String name = CLinker.toJavaString(memoryAddress);
        Jnotify.getLogger().debug(String.format("try get name is :%s%n", name));

        if (inotifyLinuxActuator != null && !Util.isIgnoreFileName(name)) {
            inotifyLinuxActuator.notify(wd, mask, cookie, len, name);
        }
        return offset + 16 + len;
    }

    /**
     * 关闭文件监听
     *
     * @return
     */
    private static int closeFileDescriptor() {
        MemoryAddress inotifyRemoveWatch = CLinker.systemLookup().lookup("close").get();
        MethodType methodType = MethodType.methodType(int.class, int.class);
        FunctionDescriptor functionDescriptor = FunctionDescriptor.of(C_INT, C_INT);
        MethodHandle handle = CLinker.getInstance().downcallHandle(inotifyRemoveWatch, methodType, functionDescriptor);
        try {
            Object result = handle.invoke(FILE_DESCRIPTOR);
            if ((int) result < 0) {
                Jnotify.getLogger().debug("文件关闭失败，结果为：" + result);
            } else {
                Jnotify.getLogger().debug("文件关闭成功，结果为：" + result);
            }
            return (int) result;
        } catch (Throwable e) {
            throw new JnotifyHandleInvokeErrorException("close", e);
        }
    }

    /**
     * 配置linux 通知事件的处理器
     *
     * @param notifyLinuxActuator
     */
    public static void setNotifyLinuxActuator(InotifyLinuxActuator notifyLinuxActuator) {
        if (InotifyLinux.inotifyLinuxActuator == null) {
            InotifyLinux.inotifyLinuxActuator = notifyLinuxActuator;
        } else {
            throw new RuntimeException("Notify listener is already set. multiple notify listeners are not supported.");
        }
    }

    @Override
    public void close() {
        if (FILE_DESCRIPTOR != -1) {
            closeFileDescriptor();
        }
    }
}
