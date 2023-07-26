/******************************************************************************
 * Author : tramp
 ******************************************************************************/
package org.throne.core;

/**
 * @author tramp
 */
public interface JnotifyListener {
    /**
     * 文件创建事件处理函数
     *
     * @param wd       文件描述符
     * @param rootPath 根路径
     * @param name     相对于根路径的名称
     */
    default void fileCreated(int wd, String rootPath, String name) {
    }

    /**
     * 文件删除事件处理函数
     *
     * @param wd       文件描述符
     * @param rootPath 根路径
     * @param name     相对于根路径的名称
     */
    default void fileDeleted(int wd, String rootPath, String name) {
    }

    /**
     * 文件修改事件处理函数
     *
     * @param wd       文件描述符
     * @param rootPath 根路径
     * @param name     相对于根路径的名称
     */
    default void fileModified(int wd, String rootPath, String name) {
    }

    /**
     * 文件属性被修改处理函数
     *
     * @param wd       文件描述符
     * @param rootPath 根路径
     * @param name     相对于根路径的名称
     */
    default void fileAttrib(int wd, String rootPath, String name) {
    }

    /**
     * 文件重命名事件处理函数
     *
     * @param wd       文件描述符
     * @param rootPath 根路径
     * @param oldName  旧文件名
     * @param newName  新文件名
     */
    default void fileRenamed(int wd, String rootPath, String oldName, String newName) {
    }

    /**
     * 文件从监听文件中移出
     *
     * @param wd       文件描述符
     * @param rootPath 根路径
     * @param name     相对于根路径的名称
     */
    default void fileMoveFrom(int wd, String rootPath, String name) {
    }

    /**
     * 文件由外部移入到监听的文件夹中
     *
     * @param wd       文件描述符
     * @param rootPath 根路径
     * @param name     相对于根路径的名称
     */
    default void fileMoveTo(int wd, String rootPath, String name) {
    }

}