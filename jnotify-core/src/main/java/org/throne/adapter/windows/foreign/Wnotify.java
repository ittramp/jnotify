package org.throne.adapter.windows.foreign;

import jdk.incubator.foreign.MemoryAddress;

/**
 * windows下的文件监听通知
 *
 * @author tramp
 * @date 2023/2/18 16:41
 */
public class Wnotify {

    /**
     * 获取文件夹的句柄
     *
     * @return
     */
    public MemoryAddress CreateFileW() {

        return null;
    }

    /**
     * 关闭文件句柄
     *
     * @param handleDir
     */
    public void CloseHandle(MemoryAddress handleDir) {

    }

    /**
     * 关闭文件读取
     *
     * @param handleDir
     */
    public void CancelIo(MemoryAddress handleDir) {

    }

    /**
     * 获取错误信息
     */
    public void GetLastError() {
    }

    /**
     * 创建 一个目录的监听
     */
    public void ReadDirectoryChangesW() {

    }

}
