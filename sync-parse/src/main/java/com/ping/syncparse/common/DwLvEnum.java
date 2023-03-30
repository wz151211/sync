package com.ping.syncparse.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum DwLvEnum {

    省检(1), 市检(2), 区检(3);

    private final int value;
}
