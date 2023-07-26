package org.throne.adapter.linux.foreign;

import jdk.incubator.foreign.*;
import org.throne.Jnotify;
import org.throne.adapter.linux.exception.JnotifySystemFunctionErrorException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteOrder;

import static jdk.incubator.foreign.CLinker.C_INT;
import static jdk.incubator.foreign.CLinker.C_POINTER;

/**
 * @author tramp
 * @date 2023/2/15 18:19
 */
public class SystemErrorLinux {
    /**
     * 获取系统错误信息
     * 只有当调用系统函数中出错才需要执行此函数
     */
    public static int getSysErrorNo() {
        MemoryAddress memoryAddress = CLinker.systemLookup().lookup("__errno_location").get();

        MethodType methodType = MethodType.methodType(MemoryAddress.class);
        FunctionDescriptor functionDescriptor = FunctionDescriptor.of(C_POINTER);
        MethodHandle handle = CLinker.getInstance().downcallHandle(memoryAddress, methodType, functionDescriptor);
        try {
            var result = handle.invoke();
            MemoryAddress resultMemoryAddress = (MemoryAddress) result;
            try (ResourceScope scope = ResourceScope.newConfinedScope()) {
                MemorySegment memorySegment = resultMemoryAddress.asSegment(4, scope);
                int errorNo = MemoryAccess.getInt(memorySegment, ByteOrder.LITTLE_ENDIAN);
                System.out.println("sys errorno is ：" + errorNo);
                return errorNo;
            }
        } catch (Throwable e) {
            throw new RuntimeException("sys_errlist", e);
        }
    }

    /**
     * 根据系统错误标识获取错误说明信息
     * 首先调用获取系统错误，获取到错误代码之后再获取系统信息
     *
     * @return
     */
    public static String getErrorMessage(int errorCode) {
        MemoryAddress inotifyRemoveWatch = CLinker.systemLookup().lookup("strerror").get();
        MethodType methodType = MethodType.methodType(MemoryAddress.class, int.class);
        FunctionDescriptor functionDescriptor = FunctionDescriptor.of(C_POINTER, C_INT);
        MethodHandle handle = CLinker.getInstance().downcallHandle(inotifyRemoveWatch, methodType, functionDescriptor);
        try {
            Object result = handle.invoke(errorCode);
            MemoryAddress memoryAddress = (MemoryAddress) result;
            String errorMessage = CLinker.toJavaString(memoryAddress);
            return errorMessage;
        } catch (Throwable e) {
            throw new RuntimeException("sys_errlist", e);
        }
    }

    public static JnotifySystemFunctionErrorException getJnotifySystemFunctionErrorException() {
        int errorCode = getSysErrorNo();
        String errorMessage = getErrorMessage(errorCode);
        JnotifySystemFunctionErrorException exception = new JnotifySystemFunctionErrorException(errorMessage, errorCode);
        Jnotify.getLogger().debug("调用系统函数错误，系统错误码为：" + errorCode + ",错误信息为：" + errorMessage);
        return exception;
    }

}
