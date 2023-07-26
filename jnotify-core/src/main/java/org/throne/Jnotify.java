package org.throne;

import org.throne.adapter.linux.JnotifyAdapterLinux;
import org.throne.core.CurrentOSType;
import org.throne.core.IJnotify;
import org.throne.core.JnotifyEventTypeEnum;
import org.throne.core.JnotifyListener;
import org.throne.core.exception.JnotifyException;
import org.throne.core.log.ILogger;
import org.throne.core.log.LoggerImpl;
import org.throne.core.log.LoggerLevel;

import java.util.List;

/**
 * 文件监听主类
 *
 * @author tramp
 * @date 2023/2/10 10:17
 */
public class Jnotify {

    private static LoggerLevel loggerLevel = LoggerLevel.ERROR;
    private static IJnotify instance;

    private static ILogger logger;

    static {
        try {
            String loggerImpl = System.getProperty("jnotify.log.impl.override");
            if (loggerImpl != null) {
                logger = (ILogger) Class.forName(loggerImpl).getDeclaredConstructor().newInstance();
            } else {
                logger = new LoggerImpl();
            }
            String overrideClass = System.getProperty("jnotify.impl.override");
            if (overrideClass != null) {
                instance = (IJnotify) Class.forName(overrideClass).getDeclaredConstructor().newInstance();
            } else {
                logger.error(CurrentOSType.current().name());
                switch (CurrentOSType.current()) {
                    case LinuxAArch64, SysV -> instance = new JnotifyAdapterLinux();
                    case MacOsAArch64, Win64 -> instance = (IJnotify) Class.forName("jnotify.win32.JNotifyAdapterWin32")
                            .getDeclaredConstructor().newInstance();
                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加文件路径监听
     * TODO 在添加监听开始时需要判断监听文件路径是否存在，
     *
     * @param path     文件路径
     * @param listener 监听器
     * @return 监听标识
     * @throws JnotifyException
     */
    public static int addWatch(String path, JnotifyListener listener) throws JnotifyException {
        return addWatch(path, JnotifyEventTypeEnum.allMask(), true, listener);
    }

    /**
     * 添加文件路径监听
     *
     * @param path         文件路径
     * @param eventTypes   监控类型
     * @param watchSubtree 是否监听子文件夹
     * @param listener     监听器
     * @return 监听标识
     * @throws JnotifyException
     */
    public static int addWatch(String path, List<JnotifyEventTypeEnum> eventTypes, boolean watchSubtree, JnotifyListener listener) throws JnotifyException {
        return instance.addWatch(path, eventTypes, watchSubtree, listener);
    }

    /**
     * 根据监听标识删除文件监听
     *
     * @param watchId 监听标识
     * @return
     * @throws JnotifyException
     */
    public static boolean removeWatch(int watchId) throws JnotifyException {
        return instance.removeWatch(watchId);
    }

    /**
     * 设定日志级别
     *
     * @param loggerLevel
     */
    public static void setLoggerLevel(LoggerLevel loggerLevel) {
        Jnotify.loggerLevel = loggerLevel;
    }

    public static LoggerLevel getLoggerLevel() {
        return Jnotify.loggerLevel;
    }

    public static ILogger getLogger(){
        return logger;
    }

}
