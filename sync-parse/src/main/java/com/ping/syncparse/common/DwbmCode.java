package com.ping.syncparse.common;


import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * <h3>单位编码工具类</h3><br>
 * 单位编码为由6位数字组成，每一段有2位数字， 表示省、市、区。<br>
 */
@SuppressWarnings("serial")
@Data
public class DwbmCode implements Serializable {

    /**
     * 编码规定长度
     */
    private static final int LENGTH = 6;
    /**
     * 省后缀
     */
    private static final String SUFFIX_PR = "0000";
    /**
     * 市后缀
     */
    private static final String SUFFIX_CI = "00";

    /**
     * 对象缓存
     */
    private static final Map<String, DwbmCode> CACHE = new WeakHashMap<>();

    private final String code;
    private final String prefix;
    private final DwLvEnum level;

    private DwbmCode(String code, String prefix, DwLvEnum level) {
        this.code = code;
        this.prefix = prefix;
        this.level = level;
    }

    /**
     * 返回单位编码对象
     *
     * @param dwbm 单位编码
     * @return 单位编码对象
     */
    public static final DwbmCode valueOf(String dwbm) {
        if (dwbm == null) {
            return null;
        }
        return CACHE.computeIfAbsent(dwbm, k -> create(dwbm));
    }

    private static DwbmCode create(String dwbm) {
        // 不符合规则的单位编码
        int length = dwbm == null ? 0 : dwbm.length();
        if (length != LENGTH) {
            return new DwbmCode(dwbm, dwbm, DwLvEnum.区检);
        }
        // 省
        if (dwbm.endsWith(SUFFIX_PR)) {
            return new DwbmCode(dwbm, dwbm.substring(0, LENGTH - SUFFIX_PR.length()), DwLvEnum.省检);
        }
        // 市
        if (dwbm.endsWith(SUFFIX_CI)) {
            return new DwbmCode(dwbm, dwbm.substring(0, LENGTH - SUFFIX_CI.length()), DwLvEnum.市检);
        }
        // 区
        return new DwbmCode(dwbm, dwbm, DwLvEnum.区检);
    }
}
