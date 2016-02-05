begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|AlreadyClosedException
import|;
end_import
begin_comment
comment|/**  * Utility class to safely share instances of a certain type across multiple  * threads, while periodically refreshing them. This class ensures each  * reference is closed only once all threads have finished using it. It is  * recommended to consult the documentation of {@link ReferenceManager}  * implementations for their {@link #maybeRefresh()} semantics.  *   * @param<G>  *          the concrete type that will be {@link #acquire() acquired} and  *          {@link #release(Object) released}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|ReferenceManager
specifier|public
specifier|abstract
class|class
name|ReferenceManager
parameter_list|<
name|G
parameter_list|>
implements|implements
name|Closeable
block|{
DECL|field|REFERENCE_MANAGER_IS_CLOSED_MSG
specifier|private
specifier|static
specifier|final
name|String
name|REFERENCE_MANAGER_IS_CLOSED_MSG
init|=
literal|"this ReferenceManager is closed"
decl_stmt|;
DECL|field|current
specifier|protected
specifier|volatile
name|G
name|current
decl_stmt|;
DECL|field|refreshLock
specifier|private
specifier|final
name|Lock
name|refreshLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|refreshListeners
specifier|private
specifier|final
name|List
argument_list|<
name|RefreshListener
argument_list|>
name|refreshListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
name|REFERENCE_MANAGER_IS_CLOSED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|method|swapReference
specifier|private
specifier|synchronized
name|void
name|swapReference
parameter_list|(
name|G
name|newReference
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|G
name|oldReference
init|=
name|current
decl_stmt|;
name|current
operator|=
name|newReference
expr_stmt|;
name|release
argument_list|(
name|oldReference
argument_list|)
expr_stmt|;
block|}
comment|/**    * Decrement reference counting on the given reference.     * @throws IOException if reference decrement on the given resource failed.    * */
DECL|method|decRef
specifier|protected
specifier|abstract
name|void
name|decRef
parameter_list|(
name|G
name|reference
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Refresh the given reference if needed. Returns {@code null} if no refresh    * was needed, otherwise a new refreshed reference.    * @throws AlreadyClosedException if the reference manager has been {@link #close() closed}.    * @throws IOException if the refresh operation failed    */
DECL|method|refreshIfNeeded
specifier|protected
specifier|abstract
name|G
name|refreshIfNeeded
parameter_list|(
name|G
name|referenceToRefresh
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Try to increment reference counting on the given reference. Return true if    * the operation was successful.    * @throws AlreadyClosedException if the reference manager has been {@link #close() closed}.     */
DECL|method|tryIncRef
specifier|protected
specifier|abstract
name|boolean
name|tryIncRef
parameter_list|(
name|G
name|reference
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Obtain the current reference. You must match every call to acquire with one    * call to {@link #release}; it's best to do so in a finally clause, and set    * the reference to {@code null} to prevent accidental usage after it has been    * released.    * @throws AlreadyClosedException if the reference manager has been {@link #close() closed}.     */
DECL|method|acquire
specifier|public
specifier|final
name|G
name|acquire
parameter_list|()
throws|throws
name|IOException
block|{
name|G
name|ref
decl_stmt|;
do|do
block|{
if|if
condition|(
operator|(
name|ref
operator|=
name|current
operator|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
name|REFERENCE_MANAGER_IS_CLOSED_MSG
argument_list|)
throw|;
block|}
if|if
condition|(
name|tryIncRef
argument_list|(
name|ref
argument_list|)
condition|)
block|{
return|return
name|ref
return|;
block|}
if|if
condition|(
name|getRefCount
argument_list|(
name|ref
argument_list|)
operator|==
literal|0
operator|&&
name|current
operator|==
name|ref
condition|)
block|{
assert|assert
name|ref
operator|!=
literal|null
assert|;
comment|/* if we can't increment the reader but we are            still the current reference the RM is in a            illegal states since we can't make any progress            anymore. The reference is closed but the RM still            holds on to it as the actual instance.            This can only happen if somebody outside of the RM            decrements the refcount without a corresponding increment            since the RM assigns the new reference before counting down            the reference. */
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The managed reference has already closed - this is likely a bug when the reference count is modified outside of the ReferenceManager"
argument_list|)
throw|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
comment|/**     *<p>     * Closes this ReferenceManager to prevent future {@link #acquire() acquiring}. A     * reference manager should be closed if the reference to the managed resource     * should be disposed or the application using the {@link ReferenceManager}     * is shutting down. The managed resource might not be released immediately,     * if the {@link ReferenceManager} user is holding on to a previously     * {@link #acquire() acquired} reference. The resource will be released once     * when the last reference is {@link #release(Object) released}. Those     * references can still be used as if the manager was still active.     *</p>     *<p>     * Applications should not {@link #acquire() acquire} new references from this     * manager once this method has been called. {@link #acquire() Acquiring} a     * resource on a closed {@link ReferenceManager} will throw an     * {@link AlreadyClosedException}.     *</p>     *      * @throws IOException     *           if the underlying reader of the current reference could not be closed    */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|final
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
comment|// make sure we can call this more than once
comment|// closeable javadoc says:
comment|// if this is already closed then invoking this method has no effect.
name|swapReference
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|afterClose
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the current reference count of the given reference.    */
DECL|method|getRefCount
specifier|protected
specifier|abstract
name|int
name|getRefCount
parameter_list|(
name|G
name|reference
parameter_list|)
function_decl|;
comment|/**    *  Called after close(), so subclass can free any resources.    *  @throws IOException if the after close operation in a sub-class throws an {@link IOException}     * */
DECL|method|afterClose
specifier|protected
name|void
name|afterClose
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|doMaybeRefresh
specifier|private
name|void
name|doMaybeRefresh
parameter_list|()
throws|throws
name|IOException
block|{
comment|// it's ok to call lock() here (blocking) because we're supposed to get here
comment|// from either maybeRefreh() or maybeRefreshBlocking(), after the lock has
comment|// already been obtained. Doing that protects us from an accidental bug
comment|// where this method will be called outside the scope of refreshLock.
comment|// Per ReentrantLock's javadoc, calling lock() by the same thread more than
comment|// once is ok, as long as unlock() is called a matching number of times.
name|refreshLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|boolean
name|refreshed
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|G
name|reference
init|=
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
name|notifyRefreshListenersBefore
argument_list|()
expr_stmt|;
name|G
name|newReference
init|=
name|refreshIfNeeded
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|newReference
operator|!=
literal|null
condition|)
block|{
assert|assert
name|newReference
operator|!=
name|reference
operator|:
literal|"refreshIfNeeded should return null if refresh wasn't needed"
assert|;
try|try
block|{
name|swapReference
argument_list|(
name|newReference
argument_list|)
expr_stmt|;
name|refreshed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|refreshed
condition|)
block|{
name|release
argument_list|(
name|newReference
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|release
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|notifyRefreshListenersRefreshed
argument_list|(
name|refreshed
argument_list|)
expr_stmt|;
block|}
name|afterMaybeRefresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|refreshLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * You must call this (or {@link #maybeRefreshBlocking()}), periodically, if    * you want that {@link #acquire()} will return refreshed instances.    *     *<p>    *<b>Threads</b>: it's fine for more than one thread to call this at once.    * Only the first thread will attempt the refresh; subsequent threads will see    * that another thread is already handling refresh and will return    * immediately. Note that this means if another thread is already refreshing    * then subsequent threads will return right away without waiting for the    * refresh to complete.    *     *<p>    * If this method returns true it means the calling thread either refreshed or    * that there were no changes to refresh. If it returns false it means another    * thread is currently refreshing.    *</p>    * @throws IOException if refreshing the resource causes an {@link IOException}    * @throws AlreadyClosedException if the reference manager has been {@link #close() closed}.     */
DECL|method|maybeRefresh
specifier|public
specifier|final
name|boolean
name|maybeRefresh
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// Ensure only 1 thread does refresh at once; other threads just return immediately:
specifier|final
name|boolean
name|doTryRefresh
init|=
name|refreshLock
operator|.
name|tryLock
argument_list|()
decl_stmt|;
if|if
condition|(
name|doTryRefresh
condition|)
block|{
try|try
block|{
name|doMaybeRefresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|refreshLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|doTryRefresh
return|;
block|}
comment|/**    * You must call this (or {@link #maybeRefresh()}), periodically, if you want    * that {@link #acquire()} will return refreshed instances.    *     *<p>    *<b>Threads</b>: unlike {@link #maybeRefresh()}, if another thread is    * currently refreshing, this method blocks until that thread completes. It is    * useful if you want to guarantee that the next call to {@link #acquire()}    * will return a refreshed instance. Otherwise, consider using the    * non-blocking {@link #maybeRefresh()}.    * @throws IOException if refreshing the resource causes an {@link IOException}    * @throws AlreadyClosedException if the reference manager has been {@link #close() closed}.     */
DECL|method|maybeRefreshBlocking
specifier|public
specifier|final
name|void
name|maybeRefreshBlocking
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// Ensure only 1 thread does refresh at once
name|refreshLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doMaybeRefresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|refreshLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Called after a refresh was attempted, regardless of    *  whether a new reference was in fact created.    *  @throws IOException if a low level I/O exception occurs      **/
DECL|method|afterMaybeRefresh
specifier|protected
name|void
name|afterMaybeRefresh
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|/**    * Release the reference previously obtained via {@link #acquire()}.    *<p>    *<b>NOTE:</b> it's safe to call this after {@link #close()}.    * @throws IOException if the release operation on the given resource throws an {@link IOException}    */
DECL|method|release
specifier|public
specifier|final
name|void
name|release
parameter_list|(
name|G
name|reference
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reference
operator|!=
literal|null
assert|;
name|decRef
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
DECL|method|notifyRefreshListenersBefore
specifier|private
name|void
name|notifyRefreshListenersBefore
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|RefreshListener
name|refreshListener
range|:
name|refreshListeners
control|)
block|{
name|refreshListener
operator|.
name|beforeRefresh
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|notifyRefreshListenersRefreshed
specifier|private
name|void
name|notifyRefreshListenersRefreshed
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|RefreshListener
name|refreshListener
range|:
name|refreshListeners
control|)
block|{
name|refreshListener
operator|.
name|afterRefresh
argument_list|(
name|didRefresh
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Adds a listener, to be notified when a reference is refreshed/swapped.    */
DECL|method|addListener
specifier|public
name|void
name|addListener
parameter_list|(
name|RefreshListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Listener cannot be null"
argument_list|)
throw|;
block|}
name|refreshListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove a listener added with {@link #addListener(RefreshListener)}.    */
DECL|method|removeListener
specifier|public
name|void
name|removeListener
parameter_list|(
name|RefreshListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Listener cannot be null"
argument_list|)
throw|;
block|}
name|refreshListeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/** Use to receive notification when a refresh has    *  finished.  See {@link #addListener}. */
DECL|interface|RefreshListener
specifier|public
interface|interface
name|RefreshListener
block|{
comment|/** Called right before a refresh attempt starts. */
DECL|method|beforeRefresh
name|void
name|beforeRefresh
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Called after the attempted refresh; if the refresh      * did open a new reference then didRefresh will be true      * and {@link #acquire()} is guaranteed to return the new      * reference. */
DECL|method|afterRefresh
name|void
name|afterRefresh
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class
end_unit
