/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.core;

/**
 * IAtomicLong is a redundant and highly available distributed alternative to the
 * {@link java.util.concurrent.atomic.AtomicLong java.util.concurrent.atomic.AtomicLong}.
 *
 * @see IAtomicReference
 */
public interface IAtomicLong extends DistributedObject {
    /**
     * Returns the name of this IAtomicLong instance.
     *
     * @return name of this instance
     */
    String getName();

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the updated value
     */
    long addAndGet(long delta);

    /**
     * Atomically sets the value to the given updated value
     * only if the current value {@code ==} the expected value.
     *
     * @param expect the expected value
     * @param update the new value
     * @return true if successful; or false if the actual value
     *         was not equal to the expected value.
     */
    boolean compareAndSet(long expect, long update);

    /**
     * Atomically decrements the current value by one.
     *
     * @return the updated value
     */
    long decrementAndGet();

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    long get();

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add
     * @return the old value before the add
     */
    long getAndAdd(long delta);

    /**
     * Atomically sets the given value and returns the old value.
     *
     * @param newValue the new value
     * @return the old value
     */
    long getAndSet(long newValue);

    /**
     * Atomically increments the current value by one.
     *
     * @return the updated value
     */
    long incrementAndGet();

    /**
     * Atomically increments the current value by one.
     *
     * @return the old value
     */
    long getAndIncrement();

    /**
     * Atomically sets the given value.
     *
     * @param newValue the new value
     */
    void set(long newValue);

    /**
     * Alters the currently stored value by applying a function on it.
     *
     * @param function the function
     * @throws IllegalArgumentException if function is null.
     */
    void alter(Function<Long, Long> function);

    /**
     * Alters the currently stored value by applying a function on it and gets the result.
     *
     * @param function the function
     * @return the new value.
     * @throws IllegalArgumentException if function is null.
     */
    long alterAndGet(Function<Long, Long> function);

    /**
     * Alters the currently stored value by applying a function on it on and gets the old value.
     *
     * @param function the function
     * @return  the old value
     * @throws IllegalArgumentException if function is null.
     */
    long getAndAlter(Function<Long, Long> function);

    /**
     * Applies a function on the value, the actual stored value will not change.
     *
     * @param function the function
     * @return  the result of the function application
     * @throws IllegalArgumentException if function is null.
     */
    <R> R apply(Function<Long, R> function);
}
