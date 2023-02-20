package org.joy;

import org.joy.core.JnotifyListener;
import org.joy.core.exception.JnotifyException;
import org.joy.core.log.LoggerLevel;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        try {
//            Jnotify.setLoggerLevel(LoggerLevel.DEBUG);
            Jnotify.addWatch("/home/tramp/inotify", new JnotifyListener() {
                @Override
                public void fileCreated(int wd, String rootPath, String name) {
                    System.out.println(String.format("【fileCreated】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
                }

                @Override
                public void fileDeleted(int wd, String rootPath, String name) {
                    System.out.println(String.format("【fileDeleted】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
                }

                @Override
                public void fileModified(int wd, String rootPath, String name) {
                    System.out.println(String.format("【fileModified】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
                }

                @Override
                public void fileAttrib(int wd, String rootPath, String name) {
                    System.out.println(String.format("【fileAttrib】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
                }

                @Override
                public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
                    System.out.println(String.format("【fileRenamed】 wd is %d ,rootPath is %s, oldName is %s, newName is %s", wd, rootPath, oldName,
                            newName));
                }

                @Override
                public void fileMoveFrom(int wd, String rootPath, String name) {
                    System.out.println(String.format("【fileMoveFrom】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
                }

                @Override
                public void fileMoveTo(int wd, String rootPath, String name) {
                    System.out.println(String.format("【fileMoveTo】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
                }
            });

            while (true) {
                Thread.sleep(1000);
            }
        } catch (JnotifyException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
