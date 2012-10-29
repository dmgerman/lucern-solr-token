begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|ConcurrentHashMap
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
name|search
operator|.
name|NRTManager
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Keeps track of current plus old IndexSearchers, closing  * the old ones once they have timed out.  *  * Use it like this:  *  *<pre class="prettyprint">  *   SearcherLifetimeManager mgr = new SearcherLifetimeManager();  *</pre>  *  * Per search-request, if it's a "new" search request, then  * obtain the latest searcher you have (for example, by  * using {@link SearcherManager} or {@link NRTManager}), and  * then record this searcher:  *  *<pre class="prettyprint">  *   // Record the current searcher, and save the returend  *   // token into user's search results (eg as a  hidden  *   // HTML form field):  *   long token = mgr.record(searcher);  *</pre>  *  * When a follow-up search arrives, for example the user  * clicks next page, drills down/up, etc., take the token  * that you saved from the previous search and:  *  *<pre class="prettyprint">  *   // If possible, obtain the same searcher as the last  *   // search:  *   IndexSearcher searcher = mgr.acquire(token);  *   if (searcher != null) {  *     // Searcher is still here  *     try {  *       // do searching...  *     } finally {  *       mgr.release(searcher);  *       // Do not use searcher after this!  *       searcher = null;  *     }  *   } else {  *     // Searcher was pruned -- notify user session timed  *     // out, or, pull fresh searcher again  *   }  *</pre>  *  * Finally, in a separate thread, ideally the same thread  * that's periodically reopening your searchers, you should  * periodically prune old searchers:  *  *<pre class="prettyprint">  *   mgr.prune(new PruneByAge(600.0));  *</pre>  *  *<p><b>NOTE</b>: keeping many searchers around means  * you'll use more resources (open files, RAM) than a single  * searcher.  However, as long as you are using {@link  * DirectoryReader#openIfChanged(DirectoryReader)}, the searchers  * will usually share almost all segments and the added resource usage  * is contained.  When a large merge has completed, and  * you reopen, because that is a large change, the new  * searcher will use higher additional RAM than other  * searchers; but large merges don't complete very often and  * it's unlikely you'll hit two of them in your expiration  * window.  Still you should budget plenty of heap in the  * JVM to have a good safety margin.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SearcherLifetimeManager
specifier|public
class|class
name|SearcherLifetimeManager
implements|implements
name|Closeable
block|{
DECL|field|NANOS_PER_SEC
specifier|static
specifier|final
name|double
name|NANOS_PER_SEC
init|=
literal|1000000000.0
decl_stmt|;
DECL|class|SearcherTracker
specifier|private
specifier|static
class|class
name|SearcherTracker
implements|implements
name|Comparable
argument_list|<
name|SearcherTracker
argument_list|>
implements|,
name|Closeable
block|{
DECL|field|searcher
specifier|public
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|recordTimeSec
specifier|public
specifier|final
name|double
name|recordTimeSec
decl_stmt|;
DECL|field|version
specifier|public
specifier|final
name|long
name|version
decl_stmt|;
DECL|method|SearcherTracker
specifier|public
name|SearcherTracker
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|version
operator|=
operator|(
operator|(
name|DirectoryReader
operator|)
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|)
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|incRef
argument_list|()
expr_stmt|;
comment|// Use nanoTime not currentTimeMillis since it [in
comment|// theory] reduces risk from clock shift
name|recordTimeSec
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
name|NANOS_PER_SEC
expr_stmt|;
block|}
comment|// Newer searchers are sort before older ones:
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|SearcherTracker
name|other
parameter_list|)
block|{
comment|// Be defensive: cannot subtract since it could
comment|// technically overflow long, though, we'd never hit
comment|// that in practice:
if|if
condition|(
name|recordTimeSec
operator|<
name|other
operator|.
name|recordTimeSec
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|recordTimeSec
operator|<
name|recordTimeSec
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
decl_stmt|;
comment|// TODO: we could get by w/ just a "set"; need to have
comment|// Tracker hash by its version and have compareTo(Long)
comment|// compare to its version
DECL|field|searchers
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|SearcherTracker
argument_list|>
name|searchers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|SearcherTracker
argument_list|>
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
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this SearcherLifetimeManager instance is closed"
argument_list|)
throw|;
block|}
block|}
comment|/** Records that you are now using this IndexSearcher.    *  Always call this when you've obtained a possibly new    *  {@link IndexSearcher}, for example from one of the    *<code>get</code> methods in {@link NRTManager} or {@link    *  SearcherManager}.  It's fine if you already passed the    *  same searcher to this method before.    *    *<p>This returns the long token that you can later pass    *  to {@link #acquire} to retrieve the same IndexSearcher.    *  You should record this long token in the search results    *  sent to your user, such that if the user performs a    *  follow-on action (clicks next page, drills down, etc.)    *  the token is returned. */
DECL|method|record
specifier|public
name|long
name|record
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// TODO: we don't have to use IR.getVersion to track;
comment|// could be risky (if it's buggy); we could get better
comment|// bug isolation if we assign our own private ID:
specifier|final
name|long
name|version
init|=
operator|(
operator|(
name|DirectoryReader
operator|)
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|)
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|SearcherTracker
name|tracker
init|=
name|searchers
operator|.
name|get
argument_list|(
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|tracker
operator|==
literal|null
condition|)
block|{
comment|//System.out.println("RECORD version=" + version + " ms=" + System.currentTimeMillis());
name|tracker
operator|=
operator|new
name|SearcherTracker
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchers
operator|.
name|putIfAbsent
argument_list|(
name|version
argument_list|,
name|tracker
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// Another thread beat us -- must decRef to undo
comment|// incRef done by SearcherTracker ctor:
name|tracker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|tracker
operator|.
name|searcher
operator|!=
name|searcher
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the provided searcher has the same underlying reader version yet the searcher instance differs from before (new="
operator|+
name|searcher
operator|+
literal|" vs old="
operator|+
name|tracker
operator|.
name|searcher
argument_list|)
throw|;
block|}
return|return
name|version
return|;
block|}
comment|/** Retrieve a previously recorded {@link IndexSearcher}, if it    *  has not yet been closed    *    *<p><b>NOTE</b>: this may return null when the    *  requested searcher has already timed out.  When this    *  happens you should notify your user that their session    *  timed out and that they'll have to restart their    *  search.    *    *<p>If this returns a non-null result, you must match    *  later call {@link #release} on this searcher, best    *  from a finally clause. */
DECL|method|acquire
specifier|public
name|IndexSearcher
name|acquire
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|SearcherTracker
name|tracker
init|=
name|searchers
operator|.
name|get
argument_list|(
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|tracker
operator|!=
literal|null
operator|&&
name|tracker
operator|.
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|tryIncRef
argument_list|()
condition|)
block|{
return|return
name|tracker
operator|.
name|searcher
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Release a searcher previously obtained from {@link    *  #acquire}.    *     *<p><b>NOTE</b>: it's fine to call this after close. */
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
comment|/** See {@link #prune}. */
DECL|interface|Pruner
specifier|public
interface|interface
name|Pruner
block|{
comment|/** Return true if this searcher should be removed.       *  @param ageSec how much time has passed since this      *         searcher was the current (live) searcher      *  @param searcher Searcher      **/
DECL|method|doPrune
specifier|public
name|boolean
name|doPrune
parameter_list|(
name|double
name|ageSec
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
function_decl|;
block|}
comment|/** Simple pruner that drops any searcher older by    *  more than the specified seconds, than the newest    *  searcher. */
DECL|class|PruneByAge
specifier|public
specifier|final
specifier|static
class|class
name|PruneByAge
implements|implements
name|Pruner
block|{
DECL|field|maxAgeSec
specifier|private
specifier|final
name|double
name|maxAgeSec
decl_stmt|;
DECL|method|PruneByAge
specifier|public
name|PruneByAge
parameter_list|(
name|double
name|maxAgeSec
parameter_list|)
block|{
if|if
condition|(
name|maxAgeSec
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxAgeSec must be> 0 (got "
operator|+
name|maxAgeSec
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxAgeSec
operator|=
name|maxAgeSec
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPrune
specifier|public
name|boolean
name|doPrune
parameter_list|(
name|double
name|ageSec
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
block|{
return|return
name|ageSec
operator|>
name|maxAgeSec
return|;
block|}
block|}
comment|/** Calls provided {@link Pruner} to prune entries.  The    *  entries are passed to the Pruner in sorted (newest to    *  oldest IndexSearcher) order.    *     *<p><b>NOTE</b>: you must peridiocally call this, ideally    *  from the same background thread that opens new    *  searchers. */
DECL|method|prune
specifier|public
specifier|synchronized
name|void
name|prune
parameter_list|(
name|Pruner
name|pruner
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Cannot just pass searchers.values() to ArrayList ctor
comment|// (not thread-safe since the values can change while
comment|// ArrayList is init'ing itself); must instead iterate
comment|// ourselves:
specifier|final
name|List
argument_list|<
name|SearcherTracker
argument_list|>
name|trackers
init|=
operator|new
name|ArrayList
argument_list|<
name|SearcherTracker
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SearcherTracker
name|tracker
range|:
name|searchers
operator|.
name|values
argument_list|()
control|)
block|{
name|trackers
operator|.
name|add
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|trackers
argument_list|)
expr_stmt|;
name|double
name|lastRecordTimeSec
init|=
literal|0.0
decl_stmt|;
specifier|final
name|double
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
name|NANOS_PER_SEC
decl_stmt|;
for|for
control|(
name|SearcherTracker
name|tracker
range|:
name|trackers
control|)
block|{
specifier|final
name|double
name|ageSec
decl_stmt|;
if|if
condition|(
name|lastRecordTimeSec
operator|==
literal|0.0
condition|)
block|{
name|ageSec
operator|=
literal|0.0
expr_stmt|;
block|}
else|else
block|{
name|ageSec
operator|=
name|now
operator|-
name|lastRecordTimeSec
expr_stmt|;
block|}
comment|// First tracker is always age 0.0 sec, since it's
comment|// still "live"; second tracker's age (= seconds since
comment|// it was "live") is now minus first tracker's
comment|// recordTime, etc:
if|if
condition|(
name|pruner
operator|.
name|doPrune
argument_list|(
name|ageSec
argument_list|,
name|tracker
operator|.
name|searcher
argument_list|)
condition|)
block|{
comment|//System.out.println("PRUNE version=" + tracker.version + " age=" + ageSec + " ms=" + System.currentTimeMillis());
name|searchers
operator|.
name|remove
argument_list|(
name|tracker
operator|.
name|version
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|lastRecordTimeSec
operator|=
name|tracker
operator|.
name|recordTimeSec
expr_stmt|;
block|}
block|}
comment|/** Close this to future searching; any searches still in    *  process in other threads won't be affected, and they    *  should still call {@link #release} after they are    *  done.    *    *<p><b>NOTE</b>: you must ensure no other threads are    *  calling {@link #record} while you call close();    *  otherwise it's possible not all searcher references    *  will be freed. */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SearcherTracker
argument_list|>
name|toClose
init|=
operator|new
name|ArrayList
argument_list|<
name|SearcherTracker
argument_list|>
argument_list|(
name|searchers
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
comment|// Remove up front in case exc below, so we don't
comment|// over-decRef on double-close:
for|for
control|(
name|SearcherTracker
name|tracker
range|:
name|toClose
control|)
block|{
name|searchers
operator|.
name|remove
argument_list|(
name|tracker
operator|.
name|version
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|toClose
argument_list|)
expr_stmt|;
comment|// Make some effort to catch mis-use:
if|if
condition|(
name|searchers
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"another thread called record while this SearcherLifetimeManager instance was being closed; not all searchers were closed"
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
