package com.joy.jnotifyspringbootdemo.demo.common;

import jdk.jfr.Label;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.joy.core.JnotifyListener;

/**
 * @author tramp
 * @date 2023/2/20 16:34
 */
@Slf4j
public class JnotifyListenerSample implements JnotifyListener {
    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        System.err.println("fileCreated");
        log.error(String.format("【fileCreated】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
    }

    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        System.err.println("fileDeleted");
        log.error(String.format("【fileDeleted】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
    }

    @Override
    public void fileModified(int wd, String rootPath, String name) {
        System.err.println("fileModified");
        log.error(String.format("【fileModified】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
    }

    @Override
    public void fileAttrib(int wd, String rootPath, String name) {
        System.err.println("fileAttrib");
        log.error(String.format("【fileAttrib】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
    }

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        System.err.println("fileRenamed");
        log.error(String.format("【fileRenamed】 wd is %d ,rootPath is %s, oldName is %s, newName is %s", wd, rootPath, oldName,
                newName));
    }

    @Override
    public void fileMoveFrom(int wd, String rootPath, String name) {
        System.err.println("fileMoveFrom");
        log.error(String.format("【fileMoveFrom】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
    }

    @Override
    public void fileMoveTo(int wd, String rootPath, String name) {
        System.err.println("fileMoveTo");
        log.error(String.format("【fileMoveTo】 wd is %d ,rootPath is %s, name is %s", wd, rootPath, name));
    }
}
