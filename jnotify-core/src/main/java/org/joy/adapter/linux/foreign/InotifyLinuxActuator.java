package org.joy.adapter.linux.foreign;

/**
 * linux 通知消息执行器，
 * 可通过继承和配置更改执行器
 *
 * @author tramp
 * @date 2023/2/16 10:40
 */
public interface InotifyLinuxActuator {

    /**
     * 解析linux notify 中消息
     *
     * @param wd     Watch descriptor
     * @param mask   Mask describing event
     * @param cookie Unique cookie associating related events (for rename(2))
     * @param len    Size of name field
     * @param name   Optional null-terminated name
     */
    void notify(Integer wd, Integer mask, Integer cookie, Integer len, String name);
}
