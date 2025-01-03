package cn.mapway.globe.server.gabage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vec2对象池
 */
public class M2Pool {
    private static final M2Pool M2_POOL = new M2Pool();
    private final List<M2> pool;

    public M2Pool() {
        pool = new ArrayList<>(10);
    }

    public static M2Pool getTempPool() {
        return M2_POOL;
    }

    /**
     * 从池中获取一个对象
     * 如果池中为空　就创建一个新对象
     *
     * @return
     */
    public static M2 poll() {
        return M2_POOL.get();
    }

    /**
     * 回收对象
     *
     * @param v
     */
    public static M2Pool push(M2 v) {
        if (v == null) {
            return M2_POOL;
        }
        return M2_POOL.put(v);
    }

    private M2 get() {
        if (pool.size() > 0) {
            return pool.remove(0);
        }
        return new M2(1,0,0,1);
    }

    private M2Pool put(M2 v) {
        pool.add(v);
        return this;
    }
}
