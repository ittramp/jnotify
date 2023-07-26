/*******************************************************************************
 * JNotify - Allow java applications to register to File system events.
 *
 * Copyright (C) 2005 - Content Objects
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 ******************************************************************************
 *
 * You may also redistribute and/or modify this library under the terms of the
 * Eclipse Public License. See epl.html.
 *
 ******************************************************************************
 *
 * Content Objects, Inc., hereby disclaims all copyright interest in the
 * library `JNotify' (a Java library for file system events).
 *
 * Yahali Sherman, 21 November 2005
 *    Content Objects, VP R&D.
 *
 ******************************************************************************
 * Author : Omry Yadan
 ******************************************************************************/

package org.throne.core;


import org.throne.core.exception.JnotifyException;

import java.util.List;

/**
 * 监听接口
 *
 * @author tramp
 */
public interface IJnotify {
    /**
     * 添加监听
     *
     * @param path         路径
     * @param eventTypes   监听类型
     * @param watchSubtree 是否监听子文件夹
     * @param listener     监听器
     * @return 监听标识
     * @throws JnotifyException
     */
    Integer addWatch(String path, List<JnotifyEventTypeEnum> eventTypes, boolean watchSubtree, JnotifyListener listener) throws JnotifyException;

    /**
     * 移除监听
     *
     * @param wd 监听标识
     * @return 是否成功
     * @throws JnotifyException
     */
    boolean removeWatch(Integer wd) throws JnotifyException;
}
