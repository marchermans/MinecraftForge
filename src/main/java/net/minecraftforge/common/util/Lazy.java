/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Proxy object for a value that is calculated on first access
 * @param <T> The type of the value
 */
public interface Lazy<T> extends Supplier<T>
{
    /**
     * Constructs a lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> Lazy<T> of(@Nonnull Supplier<T> supplier)
    {
        return new Lazy.Fast<>(supplier);
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> Lazy<T> concurrentOf(@Nonnull Supplier<T> supplier)
    {
        return new Lazy.Concurrent<>(supplier);
    }

    /**
     * Constructs a thread-safe resettable lazy-initialized object
     * @param supplier The supplier for the value, to be called the first time the value is needed, or after every reset.
     */
    static <T> Resettable<T> resettableOf(@Nonnull Supplier<T> supplier) {
        return new ConcurrentResettable<>(supplier);
    }

    /**
     * Marks the lazy as a resettable variant.
     * Resettable lazies are proxy objects which can clear their stored value and will then initialize the value on
     * the next call to {@link #get()}.
     *
     * @param <T> The type that is proxied.
     */
    interface Resettable<T> extends Lazy<T> {

        /**
         * Resets the lazy so that it can re-initialized.
         */
        void reset();
    }

    /**
     * Non-thread-safe implementation.
     */
    final class Fast<T> implements Lazy<T>
    {
        private Supplier<T> supplier;
        private T instance;

        private Fast(Supplier<T> supplier)
        {
            this.supplier = supplier;
        }

        @Nullable
        @Override
        public final T get()
        {
            if (supplier != null)
            {
                instance = supplier.get();
                supplier = null;
            }
            return instance;
        }
    }

    /**
     * Thread-safe implementation.
     */
    class Concurrent<T> implements Lazy<T>
    {
        protected volatile Object lock = new Object();
        protected volatile Supplier<T> supplier;
        private volatile T instance;

        private Concurrent(Supplier<T> supplier)
        {
            this.supplier = supplier;
        }

        @Nullable
        @Override
        public final T get()
        {
            // Copy the lock to a local variable to prevent NPEs if the lock field is set to null between the
            // null-check and the synchronization
            Object localLock = this.lock;
            if (supplier != null)
            {
                // localLock is not null here because supplier was non-null after we copied the lock and both of them
                // are volatile
                synchronized (localLock)
                {
                    if (supplier != null)
                    {
                        instance = supplier.get();
                        supplier = null;
                        this.lock = null;
                    }
                }
            }
            return instance;
        }
    }

    /**
     * Resettable thread safe implementation
     */
    class ConcurrentResettable<T> extends Concurrent<T> implements Resettable<T> {

        private final Supplier<T> initializer;

        private ConcurrentResettable(Supplier<T> supplier) {
            super(supplier);
            initializer = supplier;
        }

        @Override
        public void reset() {
            //lock needs to be done first, so that when get is called concurrently it is set after the supplier is not null check.
            this.lock = new Object();
            this.supplier = initializer;
        }
    }
}
