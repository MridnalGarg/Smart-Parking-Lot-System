package org.mridnal.smartparkinglotsystem.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class LockService {

    private static class LockRef {
        final ReentrantLock lock;
        final AtomicInteger refs = new AtomicInteger(1);

        LockRef(boolean fair) {
            this.lock = new ReentrantLock(fair);
        }
    }

    // Fair locks ensure queued (FIFO) acquisition order for waiting threads
    private final boolean fair = true;
    private final ConcurrentHashMap<String, LockRef> locks = new ConcurrentHashMap<>();

    /**
     * Acquire the lock for the key, blocking (and queued) until available.
     * This method is interruptible.
     */
    public void lock(String key) throws InterruptedException {
        LockRef ref = locks.compute(key, (k, existing) -> {
            if (existing == null) return new LockRef(fair);
            existing.refs.incrementAndGet();
            return existing;
        });

        // lockInterruptibly participates in the fair queue
        try {
            ref.lock.lockInterruptibly();
        } catch (InterruptedException e) {
            // If interrupted while waiting, decrement refs and remove entry if unused to avoid leak
            locks.computeIfPresent(key, (k, v) -> (v.refs.decrementAndGet() <= 0) ? null : v);
            throw e;
        }
    }

    /**
     * Try to acquire lock within timeout. Returns true if acquired.
     */
    public boolean tryLock(String key, long timeout, java.util.concurrent.TimeUnit unit) throws InterruptedException {
        LockRef ref = locks.compute(key, (k, existing) -> {
            if (existing == null) return new LockRef(fair);
            existing.refs.incrementAndGet();
            return existing;
        });

        boolean acquired = ref.lock.tryLock(timeout, unit);
        if (!acquired) {
            // decrement refs since we couldn't acquire
            locks.computeIfPresent(key, (k, v) -> (v.refs.decrementAndGet() <= 0) ? null : v);
        }
        return acquired;
    }

    /**
     * Unlock the given key and cleanup the lock entry when no longer referenced.
     */
    public void unlock(String key) {
        locks.computeIfPresent(key, (k, v) -> {
            try {
                if (v.lock.isHeldByCurrentThread()) v.lock.unlock();
            } catch (IllegalMonitorStateException ignored) {
            }

            if (v.refs.decrementAndGet() <= 0) return null;
            return v;
        });
    }
}
