package org.throne.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件监听类型
 *
 * @author tramp
 * @date 2023/2/10 10:51
 */
public enum JnotifyEventTypeEnum {

    /**
     * 文件创建
     */
    FILE_CREATED,
    /**
     * 文件删除
     */
    FILE_DELETED,
    /**
     * 文件内容修改
     */
    FILE_MODIFIED,

    /**
     * 文件属性被修改
     */
    FILE_ATTRIB,
    /**
     * 文件重命名
     */
    FILE_RENAMED,
    /**
     * 文件从监听文件中移出
     */
    FILE_MOVED_FROM,
    /**
     * 文件由外部移入到监听的文件夹中
     */
    FILE_MOVED_TO,
    ;

    public static List<JnotifyEventTypeEnum> allMask() {
        List<JnotifyEventTypeEnum> all = new ArrayList<>();
        all.add(FILE_CREATED);
        all.add(FILE_DELETED);
        all.add(FILE_MODIFIED);
        all.add(FILE_RENAMED);
        all.add(FILE_MOVED_FROM);
        all.add(FILE_MOVED_TO);
        all.add(FILE_ATTRIB);

        return all;
    }
}
