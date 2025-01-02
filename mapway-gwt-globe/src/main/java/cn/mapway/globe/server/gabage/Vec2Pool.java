package cn.mapway.globe.server.gabage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vec2对象池
 */
public class Vec2Pool {
    private static final Vec2Pool VEC2_POOL = new Vec2Pool();
    private final List<Vec2> pool;

    public Vec2Pool() {
        pool = new ArrayList<>(10);
    }

    public static Vec2Pool getTempPool() {
        return VEC2_POOL;
    }

    /**
     * 从池中获取一个对象
     * 如果池中为空　就创建一个新对象
     * @return
     */
    public static Vec2 poll() {
        return VEC2_POOL.get();
    }

    /**
     * 回收对象
     * @param v
     */
    public static Vec2Pool push(Vec2 v) {
        if (v == null) {
            return VEC2_POOL;
        }
        return VEC2_POOL.put(v);
    }

    private Vec2 get() {
        if (pool.size() > 0) {
            return pool.remove(0);
        }
        return new Vec2();
    }

    private Vec2Pool put(Vec2 v) {
        pool.add(v);
        return this;
    }
}
