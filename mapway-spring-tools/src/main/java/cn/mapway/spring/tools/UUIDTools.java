package cn.mapway.spring.tools;

import de.huxhorn.sulky.ulid.ULID;

public class UUIDTools {

    private static ULID ulid;

    static {
        ulid = new ULID();
    }

    /**
     * 这个UUID是按照时间排序
     * @return
     */
    public static String uuid()
    {
        return ulid.nextULID();
    }
}
