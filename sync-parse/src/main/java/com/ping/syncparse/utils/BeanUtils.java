package com.ping.syncparse.utils;

import org.springframework.util.Assert;

import java.util.function.Supplier;

public class BeanUtils {

    public static <T, E> E convert(T from, Supplier<E> supplier) {
        Assert.notNull(from, "源对象不能为空");
        E target = supplier.get();
        org.springframework.beans.BeanUtils.copyProperties(from, target);
        return target;
    }
}
