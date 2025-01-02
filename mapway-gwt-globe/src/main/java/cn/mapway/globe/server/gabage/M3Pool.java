package cn.mapway.globe.server.gabage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vec2对象池
 */
public class M3Pool {
    private static final M3Pool M3_POOL = new M3Pool();
    private final List<M3> pool;

    public M3Pool() {
        pool = new ArrayList<>(10);
    }

    public static M3Pool getTempPool() {
        return M3_POOL;
    }

    /**
     * 从池中获取一个对象
     * 如果池中为空　就创建一个新对象
     *
     * @return
     */
    public static M3 poll() {
        return M3_POOL.get();
    }

    /**
     * 回收对象
     *
     * @param v
     */
    public static M3Pool push(M3 v) {
        if (v == null) {
            return M3_POOL;
        }
        return M3_POOL.put(v);
    }

    private M3 get() {
        if (pool.size() > 0) {
            return pool.remove(0);
        }
        return new M3(1 , 0, 0, 0, 1, 0, 0, 0, 1);
    }

    private M3Pool put(M3 v) {
        pool.add(v);
        return this;
    }
}
