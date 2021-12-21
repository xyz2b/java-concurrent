package org.xyz.concurrent.concurrent.resultcache;

import java.util.Map;
import java.util.concurrent.*;

public class Memoizer4<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer4(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if (f == null) {
            Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return c.compute(arg);
                }
            };
            FutureTask<V> ft = new FutureTask<>(eval);
            /**
             * putIfAbsent() 方法会先判断指定的键（key）是否存在，不存在则将键/值对插入到 HashMap 中。
             *
             * 如果所指定的 key 已经在 HashMap 中存在，返回和这个 key 值对应的 value, 如果所指定的 key 不在 HashMap 中存在，则返回 null。
             * 注意：如果指定 key 之前已经和一个 null 值相关联了 ，则该方法也返回 null。
             * */
            f = cache.putIfAbsent(arg, ft);
            if (f == null) {
                f = ft;
                ft.run();
            }
        }

        try {
            return f.get();
        } catch (CancellationException e) {
            cache.remove(arg, f);
            throw e;
        } catch (ExecutionException e) {
            cache.remove(arg, f);
            throw launderThrowable(e.getCause());
        }
    }

    /**
     * 强制将未检查的Throwable转换为RuntimeException
     * 如果Throwable是Error，则抛出它；如果是RuntimeException，那么返回它；否则抛出IllegalStateException
     * */
    private static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException("Not unchecked", t);
        }
    }
}
