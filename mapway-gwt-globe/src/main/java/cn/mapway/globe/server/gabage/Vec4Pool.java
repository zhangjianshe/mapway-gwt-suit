package cn.mapway.globe.server.gabage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vec2对象池
 */
public class Vec4Pool {
    private static final Vec4Pool VEC4_POOL = new Vec4Pool();
    private final List<Vec4> pool;

    public Vec4Pool() {
        pool = new ArrayList<>(10);
    }

    public static Vec4Pool getTempPool() {
        return VEC4_POOL;
    }

    /**
     * 从池中获取一个对象
     * 如果池中为空　就创建一个新对象
     *
     * @return
     */
    public static Vec4 poll() {
        return VEC4_POOL.get();
    }

    /**
     * 回收对象
     *
     * @param v
     */
    public static Vec4Pool push(Vec4 v) {
        if (v == null) {
            return VEC4_POOL;
        }
        return VEC4_POOL.put(v);
    }

    private Vec4 get() {
        if (pool.size() > 0) {
            return pool.remove(0);
        }
        return new Vec4();
    }

    private Vec4Pool put(Vec4 v) {
        pool.add(v);
        return this;
    }
}
