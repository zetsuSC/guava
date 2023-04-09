/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.cache.LocalCache.ValueReference;
import javax.annotation.CheckForNull;

/**
 * An entry in a reference map.
 *
 * <p>Entries in the map can be in the following states:
 *
 * <p>Valid:
 *
 * <ul>
 *   <li>Live: valid key/value are set
 *   <li>Loading: loading is pending
 * </ul>
 *
 * <p>Invalid:
 *
 * <ul>
 *   <li>Expired: time expired (key/value may still be set)
 *   <li>Collected: key/value was partially collected, but not yet cleaned up
 *   <li>Unset: marked as unset, awaiting cleanup or reuse
 * </ul>
 */
@GwtIncompatible
@ElementTypesAreNonnullByDefault
interface ReferenceEntry<K, V> {

  final int hash;
  @CheckForNull final ReferenceEntry<K, V> next;
  volatile ValueReference<K, V> valueReference = unset();

  /** Returns the value reference from this entry. */
  default public ValueReference<K, V> getValueReference() {
    return valueReference;
  }

  /** Sets the value reference for this entry. */
  default public void setValueReference(ValueReference<K, V> valueReference) {
    this.valueReference = valueReference;
  }

  /** Returns the next entry in the chain. */
  @CheckForNull
  default public ReferenceEntry<K, V> getNext() {
    return next;
  }

  /** Returns the entry's hash. */
  default public int getHash() {
    return hash;
  }

  /** Returns the key for this entry. */
  @CheckForNull
  K getKey();

  /*
   * Used by entries that use access order. Access entries are maintained in a doubly-linked list.
   * New entries are added at the tail of the list at write time; stale entries are expired from
   * the head of the list.
   */

  /** Returns the time that this entry was last accessed, in ns. */
  @SuppressWarnings("GoodTime")
  long getAccessTime();

  /** Sets the entry access time in ns. */
  @SuppressWarnings("GoodTime") // b/122668874
  void setAccessTime(long time);

  /** Returns the next entry in the access queue. */
  ReferenceEntry<K, V> getNextInAccessQueue();

  /** Sets the next entry in the access queue. */
  void setNextInAccessQueue(ReferenceEntry<K, V> next);

  /** Returns the previous entry in the access queue. */
  ReferenceEntry<K, V> getPreviousInAccessQueue();

  /** Sets the previous entry in the access queue. */
  void setPreviousInAccessQueue(ReferenceEntry<K, V> previous);

  /*
   * Implemented by entries that use write order. Write entries are maintained in a doubly-linked
   * list. New entries are added at the tail of the list at write time and stale entries are
   * expired from the head of the list.
   */

  @SuppressWarnings("GoodTime")
  /** Returns the time that this entry was last written, in ns. */
  long getWriteTime();

  /** Sets the entry write time in ns. */
  @SuppressWarnings("GoodTime") // b/122668874
  void setWriteTime(long time);

  /** Returns the next entry in the write queue. */
  ReferenceEntry<K, V> getNextInWriteQueue();

  /** Sets the next entry in the write queue. */
  void setNextInWriteQueue(ReferenceEntry<K, V> next);

  /** Returns the previous entry in the write queue. */
  ReferenceEntry<K, V> getPreviousInWriteQueue();

  /** Sets the previous entry in the write queue. */
  void setPreviousInWriteQueue(ReferenceEntry<K, V> previous);
}
