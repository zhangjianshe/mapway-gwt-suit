package cn.mapway.globe.server.gabage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vec2对象池
 */
public  class Vec3Pool {
    private static Vec3Pool VEC3_POOL = new Vec3Pool();
    public static Vec3Pool getTempPool() {
        return VEC3_POOL;
    }
    private List<Vec3> pool;
    /**
     * 从池中获取一个对象
     * 如果池中为空　就创建一个新对象
     * @return
     */
    public static Vec3 poll() {
        return VEC3_POOL.get();
    }

    /**
     * 回收对象
     * @param v
     */
    public static Vec3Pool push(Vec3 v) {
        if (v == null) {
            return VEC3_POOL;
        }
     return     VEC3_POOL.put(v);
    }

    public Vec3Pool() {
        pool=new ArrayList<>(10);
    }

    private Vec3 get() {
        if (pool.size() > 0) {
            return pool.remove(0);
        }
        return new Vec3();
    }

    private Vec3Pool put(Vec3 v) {
        pool.add(v);
        return this;
    }
}
