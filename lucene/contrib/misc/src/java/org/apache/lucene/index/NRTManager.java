begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|concurrent
operator|.
name|ExecutorService
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
name|atomic
operator|.
name|AtomicLong
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
name|List
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
name|analysis
operator|.
name|Analyzer
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
name|index
operator|.
name|IndexReader
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
name|document
operator|.
name|Document
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
name|IndexSearcher
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
name|Query
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
name|ThreadInterruptedException
import|;
end_import
begin_comment
comment|// TODO
end_comment
begin_comment
comment|//   - we could make this work also w/ "normal" reopen/commit?
end_comment
begin_comment
comment|/**  * Utility class to manage sharing near-real-time searchers  * across multiple searching threads.  *  *<p>NOTE: to use this class, you must call reopen  * periodically.  The {@link NRTManagerReopenThread} is a  * simple class to do this on a periodic basis.  If you  * implement your own reopener, be sure to call {@link  * #addWaitingListener} so your reopener is notified when a  * caller is waiting for a specific generation searcher.</p>  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|NRTManager
specifier|public
class|class
name|NRTManager
implements|implements
name|Closeable
block|{
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|es
specifier|private
specifier|final
name|ExecutorService
name|es
decl_stmt|;
DECL|field|indexingGen
specifier|private
specifier|final
name|AtomicLong
name|indexingGen
decl_stmt|;
DECL|field|searchingGen
specifier|private
specifier|final
name|AtomicLong
name|searchingGen
decl_stmt|;
DECL|field|noDeletesSearchingGen
specifier|private
specifier|final
name|AtomicLong
name|noDeletesSearchingGen
decl_stmt|;
DECL|field|waitingListeners
specifier|private
specifier|final
name|List
argument_list|<
name|WaitingListener
argument_list|>
name|waitingListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|WaitingListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|currentSearcher
specifier|private
specifier|volatile
name|IndexSearcher
name|currentSearcher
decl_stmt|;
DECL|field|noDeletesCurrentSearcher
specifier|private
specifier|volatile
name|IndexSearcher
name|noDeletesCurrentSearcher
decl_stmt|;
comment|/**    * Create new NRTManager.  Note that this installs a    * merged segment warmer on the provided IndexWriter's    * config.    *     *  @param writer IndexWriter to open near-real-time    *         readers   */
DECL|method|NRTManager
specifier|public
name|NRTManager
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|writer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create new NRTManager.  Note that this installs a    * merged segment warmer on the provided IndexWriter's    * config.    *     *  @param writer IndexWriter to open near-real-time    *         readers    *  @param es ExecutorService to pass to the IndexSearcher   */
DECL|method|NRTManager
specifier|public
name|NRTManager
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|es
operator|=
name|es
expr_stmt|;
name|indexingGen
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|searchingGen
operator|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|noDeletesSearchingGen
operator|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Create initial reader:
name|swapSearcher
argument_list|(
operator|new
name|IndexSearcher
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
argument_list|,
name|es
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|setMergedSegmentWarmer
argument_list|(
operator|new
name|IndexWriter
operator|.
name|IndexReaderWarmer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|warm
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|NRTManager
operator|.
name|this
operator|.
name|warm
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** NRTManager invokes this interface to notify it when a    *  caller is waiting for a specific generation searcher    *  to be visible. */
DECL|interface|WaitingListener
specifier|public
specifier|static
interface|interface
name|WaitingListener
block|{
DECL|method|waiting
specifier|public
name|void
name|waiting
parameter_list|(
name|boolean
name|requiresDeletes
parameter_list|,
name|long
name|targetGen
parameter_list|)
function_decl|;
block|}
comment|/** Adds a listener, to be notified when a caller is    *  waiting for a specific generation searcher to be    *  visible. */
DECL|method|addWaitingListener
specifier|public
name|void
name|addWaitingListener
parameter_list|(
name|WaitingListener
name|l
parameter_list|)
block|{
name|waitingListeners
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
comment|/** Remove a listener added with {@link    *  #addWaitingListener}. */
DECL|method|removeWaitingListener
specifier|public
name|void
name|removeWaitingListener
parameter_list|(
name|WaitingListener
name|l
parameter_list|)
block|{
name|waitingListeners
operator|.
name|remove
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|updateDocument
specifier|public
name|long
name|updateDocument
parameter_list|(
name|Term
name|t
parameter_list|,
name|Document
name|d
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|t
argument_list|,
name|d
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|updateDocument
specifier|public
name|long
name|updateDocument
parameter_list|(
name|Term
name|t
parameter_list|,
name|Document
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|t
argument_list|,
name|d
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|updateDocuments
specifier|public
name|long
name|updateDocuments
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocuments
argument_list|(
name|t
argument_list|,
name|docs
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|updateDocuments
specifier|public
name|long
name|updateDocuments
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocuments
argument_list|(
name|t
argument_list|,
name|docs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|t
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|q
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocument
specifier|public
name|long
name|addDocument
parameter_list|(
name|Document
name|d
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocuments
specifier|public
name|long
name|addDocuments
parameter_list|(
name|Iterable
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocument
specifier|public
name|long
name|addDocument
parameter_list|(
name|Document
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|addDocuments
specifier|public
name|long
name|addDocuments
parameter_list|(
name|Iterable
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Returns the most current searcher.  If you require a    *  certain indexing generation be visible in the returned    *  searcher, call {@link #get(long)}    *  instead.    */
DECL|method|get
specifier|public
specifier|synchronized
name|IndexSearcher
name|get
parameter_list|()
block|{
return|return
name|get
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/** Just like {@link #get}, but by passing<code>false</code> for    *  requireDeletes, you can get faster reopen time, but    *  the returned reader is allowed to not reflect all    *  deletions.  See {@link IndexReader#open(IndexWriter,boolean)}  */
DECL|method|get
specifier|public
specifier|synchronized
name|IndexSearcher
name|get
parameter_list|(
name|boolean
name|requireDeletes
parameter_list|)
block|{
specifier|final
name|IndexSearcher
name|s
decl_stmt|;
if|if
condition|(
name|requireDeletes
condition|)
block|{
name|s
operator|=
name|currentSearcher
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|noDeletesSearchingGen
operator|.
name|get
argument_list|()
operator|>
name|searchingGen
operator|.
name|get
argument_list|()
condition|)
block|{
name|s
operator|=
name|noDeletesCurrentSearcher
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|currentSearcher
expr_stmt|;
block|}
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|incRef
argument_list|()
expr_stmt|;
return|return
name|s
return|;
block|}
comment|/** Call this if you require a searcher reflecting all    *  changes as of the target generation.    *    * @param targetGen Returned searcher must reflect changes    * as of this generation    */
DECL|method|get
specifier|public
specifier|synchronized
name|IndexSearcher
name|get
parameter_list|(
name|long
name|targetGen
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|targetGen
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Call this if you require a searcher reflecting all    *  changes as of the target generation, and you don't    *  require deletions to be reflected.  Note that the    *  returned searcher may still reflect some or all    *  deletions.    *    * @param targetGen Returned searcher must reflect changes    * as of this generation    *    * @param requireDeletes If true, the returned searcher must    * reflect all deletions.  This can be substantially more    * costly than not applying deletes.  Note that if you    * pass false, it's still possible that some or all    * deletes may have been applied.    **/
DECL|method|get
specifier|public
specifier|synchronized
name|IndexSearcher
name|get
parameter_list|(
name|long
name|targetGen
parameter_list|,
name|boolean
name|requireDeletes
parameter_list|)
block|{
assert|assert
name|noDeletesSearchingGen
operator|.
name|get
argument_list|()
operator|>=
name|searchingGen
operator|.
name|get
argument_list|()
operator|:
literal|"noDeletesSearchingGen="
operator|+
name|noDeletesSearchingGen
operator|.
name|get
argument_list|()
operator|+
literal|" searchingGen="
operator|+
name|searchingGen
operator|.
name|get
argument_list|()
assert|;
if|if
condition|(
name|targetGen
operator|>
name|getCurrentSearchingGen
argument_list|(
name|requireDeletes
argument_list|)
condition|)
block|{
comment|// Must wait
comment|//final long t0 = System.nanoTime();
for|for
control|(
name|WaitingListener
name|listener
range|:
name|waitingListeners
control|)
block|{
name|listener
operator|.
name|waiting
argument_list|(
name|requireDeletes
argument_list|,
name|targetGen
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|targetGen
operator|>
name|getCurrentSearchingGen
argument_list|(
name|requireDeletes
argument_list|)
condition|)
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": wait fresh searcher targetGen=" + targetGen + " vs searchingGen=" + getCurrentSearchingGen(requireDeletes) + " requireDeletes=" + requireDeletes);
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
comment|//final long waitNS = System.nanoTime()-t0;
comment|//System.out.println(Thread.currentThread().getName() + ": done wait fresh searcher targetGen=" + targetGen + " vs searchingGen=" + getCurrentSearchingGen(requireDeletes) + " requireDeletes=" + requireDeletes + " WAIT msec=" + (waitNS/1000000.0));
block|}
return|return
name|get
argument_list|(
name|requireDeletes
argument_list|)
return|;
block|}
comment|/** Returns generation of current searcher. */
DECL|method|getCurrentSearchingGen
specifier|public
name|long
name|getCurrentSearchingGen
parameter_list|(
name|boolean
name|requiresDeletes
parameter_list|)
block|{
return|return
name|requiresDeletes
condition|?
name|searchingGen
operator|.
name|get
argument_list|()
else|:
name|noDeletesSearchingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Release the searcher obtained from {@link    *  #get()} or {@link #get(long)}. */
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
comment|/** Call this when you need the NRT reader to reopen.    *    * @param applyDeletes If true, the newly opened reader    *        will reflect all deletes    */
DECL|method|reopen
specifier|public
name|boolean
name|reopen
parameter_list|(
name|boolean
name|applyDeletes
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Mark gen as of when reopen started:
specifier|final
name|long
name|newSearcherGen
init|=
name|indexingGen
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|applyDeletes
operator|&&
name|currentSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
comment|//System.out.println("reopen: skip: isCurrent both force gen=" + newSearcherGen + " vs current gen=" + searchingGen);
name|searchingGen
operator|.
name|set
argument_list|(
name|newSearcherGen
argument_list|)
expr_stmt|;
name|noDeletesSearchingGen
operator|.
name|set
argument_list|(
name|newSearcherGen
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("reopen: skip: return");
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|applyDeletes
operator|&&
name|noDeletesCurrentSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
comment|//System.out.println("reopen: skip: isCurrent force gen=" + newSearcherGen + " vs current gen=" + noDeletesSearchingGen);
name|noDeletesSearchingGen
operator|.
name|set
argument_list|(
name|newSearcherGen
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("reopen: skip: return");
return|return
literal|false
return|;
block|}
comment|//System.out.println("indexingGen now " + indexingGen);
comment|// .reopen() returns a new reference:
comment|// Start from whichever searcher is most current:
specifier|final
name|IndexSearcher
name|startSearcher
init|=
name|noDeletesSearchingGen
operator|.
name|get
argument_list|()
operator|>
name|searchingGen
operator|.
name|get
argument_list|()
condition|?
name|noDeletesCurrentSearcher
else|:
name|currentSearcher
decl_stmt|;
specifier|final
name|IndexReader
name|nextReader
init|=
name|startSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|reopen
argument_list|(
name|writer
argument_list|,
name|applyDeletes
argument_list|)
decl_stmt|;
name|warm
argument_list|(
name|nextReader
argument_list|)
expr_stmt|;
comment|// Transfer reference to swapSearcher:
name|swapSearcher
argument_list|(
operator|new
name|IndexSearcher
argument_list|(
name|nextReader
argument_list|,
name|es
argument_list|)
argument_list|,
name|newSearcherGen
argument_list|,
name|applyDeletes
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Override this to warm the newly opened reader before    *  it's swapped in.  Note that this is called both for    *  newly merged segments and for new top-level readers    *  opened by #reopen. */
DECL|method|warm
specifier|protected
name|void
name|warm
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|// Steals a reference from newSearcher:
DECL|method|swapSearcher
specifier|private
specifier|synchronized
name|void
name|swapSearcher
parameter_list|(
name|IndexSearcher
name|newSearcher
parameter_list|,
name|long
name|newSearchingGen
parameter_list|,
name|boolean
name|applyDeletes
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": swap searcher gen=" + newSearchingGen + " applyDeletes=" + applyDeletes);
comment|// Always replace noDeletesCurrentSearcher:
if|if
condition|(
name|noDeletesCurrentSearcher
operator|!=
literal|null
condition|)
block|{
name|noDeletesCurrentSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
name|noDeletesCurrentSearcher
operator|=
name|newSearcher
expr_stmt|;
assert|assert
name|newSearchingGen
operator|>
name|noDeletesSearchingGen
operator|.
name|get
argument_list|()
operator|:
literal|"newSearchingGen="
operator|+
name|newSearchingGen
operator|+
literal|" noDeletesSearchingGen="
operator|+
name|noDeletesSearchingGen
assert|;
name|noDeletesSearchingGen
operator|.
name|set
argument_list|(
name|newSearchingGen
argument_list|)
expr_stmt|;
if|if
condition|(
name|applyDeletes
condition|)
block|{
comment|// Deletes were applied, so we also update currentSearcher:
if|if
condition|(
name|currentSearcher
operator|!=
literal|null
condition|)
block|{
name|currentSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
name|currentSearcher
operator|=
name|newSearcher
expr_stmt|;
if|if
condition|(
name|newSearcher
operator|!=
literal|null
condition|)
block|{
name|newSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
assert|assert
name|newSearchingGen
operator|>
name|searchingGen
operator|.
name|get
argument_list|()
operator|:
literal|"newSearchingGen="
operator|+
name|newSearchingGen
operator|+
literal|" searchingGen="
operator|+
name|searchingGen
assert|;
name|searchingGen
operator|.
name|set
argument_list|(
name|newSearchingGen
argument_list|)
expr_stmt|;
block|}
name|notifyAll
argument_list|()
expr_stmt|;
comment|//System.out.println(Thread.currentThread().getName() + ": done");
block|}
comment|/** NOTE: caller must separately close the writer. */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|swapSearcher
argument_list|(
literal|null
argument_list|,
name|indexingGen
operator|.
name|getAndIncrement
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
