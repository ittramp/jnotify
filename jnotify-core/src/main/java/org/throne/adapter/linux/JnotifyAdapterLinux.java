package org.throne.adapter.linux;

import org.throne.Jnotify;
import org.throne.adapter.linux.exception.JnotifyLinuxRuntimeException;
import org.throne.adapter.linux.exception.JnotifySystemFunctionErrorException;
import org.throne.adapter.linux.foreign.InotifyLinux;
import org.throne.adapter.linux.foreign.InotifyLinuxActuator;
import org.throne.adapter.linux.foreign.Util;
import org.throne.core.IJnotify;
import org.throne.core.JnotifyEventTypeEnum;
import org.throne.core.JnotifyListener;
import org.throne.core.exception.JnotifyException;
import org.throne.core.log.LoggerLevel;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * linux 监听执行器
 */
public class JnotifyAdapterLinux implements IJnotify {
    private final ConcurrentHashMap<Integer, Integer> watchDescriptor2WatchDataId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, WatchData> watchDataId2WatchData = new ConcurrentHashMap<>();

    /**
     * A set of files which was added by registerToSubTree (auto-watches)
     */
    private final ConcurrentHashMap<String, String> subTreePathsOfAutoWatches = new ConcurrentHashMap<>();
    private static Integer watchIDCounter = 0;

    public JnotifyAdapterLinux() {
        String actuator = System.getProperty("jnotify.impl.linux.actuator.override");
        if (actuator != null) {
            try {
                InotifyLinux.setNotifyLinuxActuator((InotifyLinuxActuator) Class.forName(actuator).getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            InotifyLinux.setNotifyLinuxActuator((wd, mask, cookie, len, name) -> {
                try {
                    notifyChangeEvent(name, wd, mask, cookie);
                } catch (RuntimeException | JnotifySystemFunctionErrorException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public Integer addWatch(String path, List<JnotifyEventTypeEnum> eventTypes, boolean watchSubtree, JnotifyListener listener)
            throws JnotifyException {


        WatchData watchData = createWatch(null, new File(path), eventTypes, watchSubtree, listener);
        if (watchSubtree) {
            try {
                File file = new File(path);
                registerToSubTree(true, watchData, file, false);
            } catch (JnotifyException e) {
                // cleanup
                removeWatch(watchData.watchDataId);
                // and throw.
                throw e;
            }
        }
        return watchData.watchDataId;
    }

    private WatchData createWatch(WatchData parentWatchData, File path, List<JnotifyEventTypeEnum> eventTypes
            , boolean watchSubtree, JnotifyListener listener) throws JnotifySystemFunctionErrorException {
        String absPath = path.getPath();
        WatchData watchData = new WatchData(parentWatchData, absPath, eventTypes, watchSubtree, listener);
        watchDescriptor2WatchDataId.put(watchData.watchDescriptor, watchData.watchDataId);
        watchDataId2WatchData.put(watchData.watchDataId, watchData);
        if (null != parentWatchData) {
            subTreePathsOfAutoWatches.put(absPath, absPath);
            Jnotify.getLogger().debug(subTreePathsOfAutoWatches.toString());
        }
        return watchData;
    }


    private void registerToSubTree(boolean userSpecified, WatchData parentWatch, File root, boolean fireCreatedEvents) throws JnotifySystemFunctionErrorException {
        if (!parentWatch.userSpecified) {
            throw new JnotifyLinuxRuntimeException("!parentWatch.userSpecified， code error");
        }

        // make sure user really requested to be notified on this event.
        // (in case of recursive listening, this IN_CREATE flag is always on, even if
        // the user is not interested in creation events).
        if (fireCreatedEvents && Boolean.TRUE.equals(parentWatch.hasCreateEvent())) {
            String name = root.toString().substring(parentWatch.path.length() + 1);
            parentWatch.notifyFileCreated(name);
        }

        if (root.isDirectory()) {
            // root was already registered by the calling method.
            if (!userSpecified) {
                createWatch(parentWatch, root, parentWatch.eventTypes, parentWatch.watchSubtree, parentWatch.listener);
            }

            String[] files = root.list();
            if (files != null) {
                for (String file : files) {
                    registerToSubTree(false, parentWatch, new File(root, file), fireCreatedEvents);
                }
            }
        }
    }

    @Override
    public boolean removeWatch(Integer watchDataId) throws JnotifyException {
        Jnotify.getLogger().debug("JNotifyAdapterLinux.removeWatch(" + watchDataId + ")");

        synchronized (watchDataId2WatchData) {
            if (watchDataId2WatchData.containsKey(watchDataId)) {
                WatchData watchData = watchDataId2WatchData.get(watchDataId);
                unwatch(watchData);
                return true;
            } else {
                return false;
            }
        }
    }


    private void unwatch(WatchData data) throws JnotifyException {
        JnotifyException ex = null;
        boolean ok = true;
        try {
            InotifyLinux.removeWatch(data.watchDescriptor);
        } catch (JnotifyException e) {
            e.printStackTrace();
            ex = e;
            ok = false;
        }

        if (data.userSpecified) {
            for (Integer watchDescriptor : data.subWatchDescriptor) {
                try {
                    InotifyLinux.removeWatch(watchDescriptor);
                } catch (JnotifyException e) {
                    e.printStackTrace();
                    ex = e;
                    ok = false;
                }
            }
        }
        if (!ok) {
            throw ex;
        }
    }

    /**
     * 默认的内部事件整合处理器
     *
     * @param name            文件名
     * @param watchDescriptor 句柄
     * @param mask            监听类型标识
     * @param cookie          缓存标识
     * @throws JnotifySystemFunctionErrorException 调用系统函数异常
     */
    protected void notifyChangeEvent(String name, Integer watchDescriptor, Integer mask, Integer cookie) throws JnotifySystemFunctionErrorException {

        if (Jnotify.getLoggerLevel().equals(LoggerLevel.DEBUG)) {
            debugLinux(name, watchDescriptor, mask, cookie);
        }
        synchronized (watchDataId2WatchData) {
            Integer watchDataId = watchDescriptor2WatchDataId.get(watchDescriptor);
            if (watchDataId == null) {
                // This happens if an exception is thrown because used too many watches.
                Jnotify.getLogger().warn("JNotifyAdapterLinux: warning, received event for an unregister watchDescriptor " + watchDescriptor + " ignoring...");
                return;
            }
            WatchData watchData = watchDataId2WatchData.get(watchDataId);
            if (watchData != null) {
                doDispatchNotify(name, mask, cookie, watchData);
            } else {
                Jnotify.getLogger().warn("JNotifyAdapterLinux: warning, received event for an unregister WD " + watchDataId + ". ignoring...");
            }
        }
    }

    /**
     * 根据数据实际派发时间
     *
     * @param name      文件名
     * @param mask      监听文件类型
     * @param cookie    cookie
     * @param watchData 监听文件内部结构体
     * @throws JnotifySystemFunctionErrorException 调用系统函数异常
     */
    protected void doDispatchNotify(String name, Integer mask, Integer cookie, WatchData watchData) throws JnotifySystemFunctionErrorException {
        if ((mask & InotifyLinux.IN_CREATE) != 0) {
            doDispatchCreateNotify(name, watchData, mask);
        } else if ((mask & InotifyLinux.IN_DELETE_SELF) != 0) {
            watchData.notifyFileDeleted(name);
        } else if ((mask & InotifyLinux.IN_DELETE) != 0) {
            watchData.notifyFileDeleted(name);
        } else if ((mask & InotifyLinux.IN_MODIFY) != 0) {
            watchData.notifyFileModified(name);
        } else if ((mask & InotifyLinux.IN_ATTRIB) != 0) {
            watchData.notifyFileAttrib(name);
        } else if ((mask & InotifyLinux.IN_MOVED_FROM) != 0) {
            watchData.fileMoveFrom(name);
            watchData.renaming(cookie, name);
        } else if ((mask & InotifyLinux.IN_MOVED_TO) != 0) {
            watchData.fileMoveTo(name);
            watchData.notifyFileRenamed(name, cookie);
        } else if ((mask & InotifyLinux.IN_IGNORED) != 0) {
            watchDescriptor2WatchDataId.remove(watchData.watchDescriptor);
            watchDataId2WatchData.remove(watchData.watchDataId);
            if (!watchData.userSpecified) {
                subTreePathsOfAutoWatches.remove(watchData.path);
                watchData.removeFromParent();
            }
        }
    }

    /**
     * 派发新增事件
     *
     * @param name      文件名
     * @param watchData 监视数据缓存
     * @throws JnotifySystemFunctionErrorException 调用系统函数异常
     */
    private void doDispatchCreateNotify(String name, WatchData watchData, Integer mask) throws JnotifySystemFunctionErrorException {
        boolean isDirectory = false;
        // 使用事件返回值判断是否是文件夹比File的方法要快
        if ((mask & InotifyLinux.IN_ISDIR) != 0) {
            Jnotify.getLogger().debug("name is directory [" + name + "]");
            isDirectory = true;
        }
        File newRootFile = new File(watchData.path, name);
        if (watchData.watchSubtree && isDirectory) {
            createWatch(watchData.getParentWatch(), newRootFile, watchData.eventTypes, true,
                    watchData.listener);
            // fire events for newly found directories under the new root.
            WatchData parent = watchData.getParentWatch();
            registerToSubTree(true, parent, newRootFile, true);
        } else {
            if (Boolean.TRUE.equals(watchData.hasCreateEvent())) {
                watchData.notifyFileCreated(name);
            }
        }
    }

    private void debugLinux(String name, Integer watchDescriptor, Integer mask, Integer cookie) {
        String desc = Util.getMaskDesc(mask);
        Integer wd = watchDescriptor2WatchDataId.get(watchDescriptor);
        WatchData watchData = watchDataId2WatchData.get(wd);
        String path;
        if (watchData != null) {
            path = watchData.path;
            if (path != null && "".equals(name)) {
                path += File.separator + name;
            }
        } else {
            path = name;
        }
        Jnotify.getLogger().debug("Linux event : wd=" + watchDescriptor + " | " + desc + " path: " + path + (cookie != 0 ? ", cookie=" + cookie : ""));
    }

    /**
     * 内部类，用来记录监听文件夹的基本信息，以及递归监听的信息（如果配置了递归监听）
     */
    private static class WatchData {
        /**
         * 是否是用户句柄，如果为递归监听生成的，则这里为false
         */
        boolean userSpecified;
        /**
         * 数据唯一标识
         */
        Integer watchDataId;
        /**
         * 监听文件的句柄
         */
        private final Integer watchDescriptor;
        /**
         * 递归监听时，子文件夹的监听句柄
         */
        private final ArrayList<Integer> subWatchDescriptor = new ArrayList<>();
        /**
         * 用户设定的监听事件类型
         */
        List<JnotifyEventTypeEnum> eventTypes;

        /**
         *
         */
        Integer mask;
        boolean watchSubtree;
        JnotifyListener listener;
        static LinkedHashMap<Integer, String> cookieToOldName = new LinkedHashMap<>();
        String path;
        WatchData parentWatchData;

        WatchData(WatchData parentWatchData, String path, List<JnotifyEventTypeEnum> eventTypes, boolean watchSubtree,
                  JnotifyListener listener) throws JnotifySystemFunctionErrorException {
            if (listener == null) {
                throw new IllegalArgumentException("Null listener");
            }
            this.parentWatchData = parentWatchData;
            this.userSpecified = (null == parentWatchData);
            this.path = path;
            this.watchDataId = watchIDCounter++;
            this.mask = Util.getMaskFromEventType(eventTypes, watchSubtree);
            this.watchDescriptor = InotifyLinux.addWatch(path, mask);
            this.eventTypes = eventTypes;
            this.watchSubtree = watchSubtree;
            this.listener = listener;

            if (parentWatchData != null) {
                parentWatchData.addSubWatch(watchDescriptor);
            }
        }

        /**
         * 获取上层监听数据结构体
         *
         * @return 上层监听数据结构体
         */
        public WatchData getParentWatch() {
            return userSpecified ? this : parentWatchData;
        }

        /**
         * 将本数据在父数据中的相关数据删除
         */
        public void removeFromParent() {
            if (parentWatchData == null) {
                Jnotify.getLogger().error(String.format("watch data  has no parent, watchDescriptor is [%d], watchDataId is [%d] %n "
                        , watchDescriptor
                        , watchDataId));
            } else {
                parentWatchData.removeSubWatch(watchDescriptor);
            }
        }

        /**
         * 文件move_from 事件记录，为重命名功能寻找原始文件名做准备
         *
         * @param cookie Unique cookie associating related events
         * @param name   name
         */
        public void renaming(Integer cookie, String name) {
            // 如果超过一定数量，则删除头部之后在添加，这里定义了1W，应该足够用了
            int maxCacheSize = 10000;
            if (cookieToOldName.size() > maxCacheSize) {
                cookieToOldName.remove(cookieToOldName.entrySet().iterator().next().getKey());
            }
            cookieToOldName.put(cookie, getOutName(name));
        }

        /**
         * 文件重命名事件派发
         *
         * @param name   name
         * @param cookie 被修改的文件标识
         */
        public void notifyFileRenamed(String name, Integer cookie) {
            String oldName = cookieToOldName.remove(cookie);
            String outRoot = getOutRoot();
            String outNewName = getOutName(name);
            if (oldName != null) {
                listener.fileRenamed(getParentWatchId(), outRoot, oldName, outNewName);
            } else {
                listener.fileCreated(getParentWatchId(), outRoot, outNewName);
            }
        }

        /**
         * 文件从监听文件中移出事件派发
         *
         * @param name 相对于根路径的名称
         */
        public void fileMoveFrom(String name) {
            if (eventTypes.contains(JnotifyEventTypeEnum.FILE_MOVED_FROM)) {
                String outRoot = getOutRoot();
                String outName = getOutName(name);
                listener.fileMoveFrom(getParentWatchId(), outRoot, outName);
            }
        }

        /**
         * 文件由外部移入到监听的文件夹中事件派发
         *
         * @param name 相对于根路径的名称
         */
        public void fileMoveTo(String name) {
            if (eventTypes.contains(JnotifyEventTypeEnum.FILE_MOVED_TO)) {
                String outRoot = getOutRoot();
                String outName = getOutName(name);
                listener.fileMoveTo(getParentWatchId(), outRoot, outName);
            }
        }

        /**
         * 文件内容被修改事件派发
         *
         * @param name name
         */
        public void notifyFileModified(String name) {
            String outRoot = getOutRoot();
            String outName = getOutName(name);
            listener.fileModified(getParentWatchId(), outRoot, outName);
        }

        /**
         * 文件属性被修改事件派发
         *
         * @param name name
         */
        public void notifyFileAttrib(String name) {
            String outRoot = getOutRoot();
            String outName = getOutName(name);
            listener.fileAttrib(getParentWatchId(), outRoot, outName);
        }

        /**
         * 文件删除事件派发
         *
         * @param name name
         */
        public void notifyFileDeleted(String name) {
            String outRoot = getOutRoot();
            String outName = getOutName(name);
            listener.fileDeleted(getParentWatchId(), outRoot, outName);
        }

        /**
         * 文件创建事件派发
         *
         * @param name name
         */
        public void notifyFileCreated(String name) {
            String outRoot = getOutRoot();
            String outName = getOutName(name);
            listener.fileCreated(getParentWatchId(), outRoot, outName);
        }

        /**
         * 底层目录监听句柄的删除
         *
         * @param watchDescriptor 监听句柄
         */
        void removeSubWatch(Integer watchDescriptor) {
            if (!subWatchDescriptor.remove(watchDescriptor)) {
                Jnotify.getLogger().error(String.format("Error removing watchDescriptor from list, watchDescriptor is [%d], watchDataId is [%d] %n "
                        , watchDescriptor
                        , watchDataId));
            }
        }

        /**
         * 底层目录监听句柄的添加
         *
         * @param watchDescriptor 监听句柄
         */
        void addSubWatch(Integer watchDescriptor) {
            subWatchDescriptor.add(watchDescriptor);
        }

        @Override
        public String toString() {
            return "WatchData " + path + ", wd=" + watchDataId + ", watchDescriptor=" + watchDescriptor + (watchSubtree ? ", recursive" : "") + (userSpecified ? ", userSpecified" : ", auto");
        }

        /**
         * 获取监听目录的路径
         *
         * @return 监听目录的路径
         */
        private String getOutRoot() {
            String outRoot;
            if (userSpecified) {
                outRoot = path;
            } else // auto watch.
            {
                outRoot = getParentWatch().path;
            }
            return outRoot;

        }

        /**
         * 获取相对目录的名称
         *
         * @param name 文件名
         * @return 相对目录的名称
         */
        private String getOutName(String name) {
            String outName;
            if (userSpecified) {
                outName = name;
            } else // auto watch.
            {
                outName = path.substring(getParentWatch().path.length() + 1);
                if (!"".equals(name)) {
                    outName += File.separatorChar + name;
                }
            }
            return outName;
        }

        /**
         * 获取根目录文件监听的句柄
         *
         * @return 根目录文件监听的句柄
         */
        public Integer getParentWatchId() {
            return parentWatchData == null ? this.watchDataId : parentWatchData.watchDataId;
        }

        /**
         * 用户是否需要新建文件的事件
         * 原因是如果配置了递归监听，则系统会自动触发文件创建事件，从而实现下层目录的监听
         *
         * @return 用户是否需要新建文件的事件
         */
        public Boolean hasCreateEvent() {
            return this.eventTypes.contains(JnotifyEventTypeEnum.FILE_CREATED);
        }
    }
}
