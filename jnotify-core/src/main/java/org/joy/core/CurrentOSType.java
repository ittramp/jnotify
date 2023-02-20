/*
 *  Copyright (c) 2020, 2021, Oracle and/or its affiliates. All rights reserved.
 *  ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.joy.core;

import static jdk.incubator.foreign.MemoryLayouts.ADDRESS;

/**
 * @author tramp
 */

public enum CurrentOSType {
    SysV,
    Win64,
    LinuxAArch64,
    MacOsAArch64;

    private static final CurrentOSType current;

    static {
        String arch = System.getProperty("os.arch");
        String os = System.getProperty("os.name");
        System.err.println("arch is " + arch);
        System.err.println("os is  " + os);
        long addressSize = ADDRESS.bitSize();
        // might be running in a 32-bit VM on a 64-bit platform.
        // addressSize will be correctly 32
        if ((arch.equals("amd64") || arch.equals("x86_64")) && addressSize == 64) {
            if (os.startsWith("Windows")) {
                current = Win64;
            } else {
                current = SysV;
            }
        } else if (arch.equals("aarch64")) {
            if (os.startsWith("Mac")) {
                current = MacOsAArch64;
            } else {
                // The Linux ABI follows the standard AAPCS ABI
                current = LinuxAArch64;
            }
        } else {
            throw new ExceptionInInitializerError(
                    "Unsupported os, arch, or address size: " + os + ", " + arch + ", " + addressSize);
        }
    }

    public static CurrentOSType current() {
        return current;
    }
}
