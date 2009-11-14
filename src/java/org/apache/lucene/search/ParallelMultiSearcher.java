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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
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
name|concurrent
operator|.
name|ExecutionException
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
name|Executors
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
name|Future
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
name|Callable
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
name|index
operator|.
name|IndexReader
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
name|Term
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
name|NamedThreadFactory
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
name|PriorityQueue
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
comment|/** Implements parallel search over a set of<code>Searchables</code>.  *  *<p>Applications usually need only call the inherited {@link #search(Query,int)}  * or {@link #search(Query,Filter,int)} methods.  */
end_comment
begin_class
DECL|class|ParallelMultiSearcher
specifier|public
class|class
name|ParallelMultiSearcher
extends|extends
name|MultiSearcher
block|{
DECL|field|executor
specifier|private
specifier|final
name|ExecutorService
name|executor
decl_stmt|;
DECL|field|searchables
specifier|private
specifier|final
name|Searchable
index|[]
name|searchables
decl_stmt|;
DECL|field|starts
specifier|private
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
comment|/** Creates a {@link Searchable} which searches<i>searchables</i>. */
DECL|method|ParallelMultiSearcher
specifier|public
name|ParallelMultiSearcher
parameter_list|(
name|Searchable
modifier|...
name|searchables
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|searchables
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchables
operator|=
name|searchables
expr_stmt|;
name|this
operator|.
name|starts
operator|=
name|getStarts
argument_list|()
expr_stmt|;
name|executor
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|NamedThreadFactory
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Executes each {@link Searchable}'s docFreq() in its own thread and waits for each search to complete and merge    * the results back together.    */
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
specifier|final
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Future
argument_list|<
name|Integer
argument_list|>
index|[]
name|searchThreads
init|=
operator|new
name|Future
index|[
name|searchables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searchable
specifier|final
name|Searchable
name|searchable
init|=
name|searchables
index|[
name|i
index|]
decl_stmt|;
name|searchThreads
index|[
name|i
index|]
operator|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|searchable
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountDocFreq
name|func
init|=
operator|new
name|CountDocFreq
argument_list|()
decl_stmt|;
name|foreach
argument_list|(
name|func
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|searchThreads
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|func
operator|.
name|docFreq
return|;
block|}
comment|/**    * A search implementation which executes each     * {@link Searchable} in its own thread and waits for each search to complete and merge    * the results back together.    */
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HitQueue
name|hq
init|=
operator|new
name|HitQueue
argument_list|(
name|nDocs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Future
argument_list|<
name|TopDocs
argument_list|>
index|[]
name|searchThreads
init|=
operator|new
name|Future
index|[
name|searchables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searchable
name|searchThreads
index|[
name|i
index|]
operator|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|MultiSearcherCallableNoSort
argument_list|(
name|lock
argument_list|,
name|searchables
index|[
name|i
index|]
argument_list|,
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|hq
argument_list|,
name|i
argument_list|,
name|starts
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountTotalHits
argument_list|<
name|TopDocs
argument_list|>
name|func
init|=
operator|new
name|CountTotalHits
argument_list|<
name|TopDocs
argument_list|>
argument_list|()
decl_stmt|;
name|foreach
argument_list|(
name|func
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|searchThreads
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
operator|new
name|TopDocs
argument_list|(
name|func
operator|.
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|func
operator|.
name|maxScore
argument_list|)
return|;
block|}
comment|/**    * A search implementation allowing sorting which spans a new thread for each    * Searchable, waits for each search to complete and merges    * the results back together.    */
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
specifier|final
name|FieldDocSortedHitQueue
name|hq
init|=
operator|new
name|FieldDocSortedHitQueue
argument_list|(
name|nDocs
argument_list|)
decl_stmt|;
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Future
argument_list|<
name|TopFieldDocs
argument_list|>
index|[]
name|searchThreads
init|=
operator|new
name|Future
index|[
name|searchables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each searchable
name|searchThreads
index|[
name|i
index|]
operator|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|MultiSearcherCallableWithSort
argument_list|(
name|lock
argument_list|,
name|searchables
index|[
name|i
index|]
argument_list|,
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|hq
argument_list|,
name|sort
argument_list|,
name|i
argument_list|,
name|starts
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountTotalHits
argument_list|<
name|TopFieldDocs
argument_list|>
name|func
init|=
operator|new
name|CountTotalHits
argument_list|<
name|TopFieldDocs
argument_list|>
argument_list|()
decl_stmt|;
name|foreach
argument_list|(
name|func
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|searchThreads
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hq
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
operator|new
name|TopFieldDocs
argument_list|(
name|func
operator|.
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|hq
operator|.
name|getFields
argument_list|()
argument_list|,
name|func
operator|.
name|maxScore
argument_list|)
return|;
block|}
comment|/** Lower-level search API.   *   *<p>{@link Collector#collect(int)} is called for every matching document.   *   *<p>Applications should only use this if they need<i>all</i> of the   * matching documents.  The high-level search API ({@link   * Searcher#search(Query,int)}) is usually more efficient, as it skips   * non-high-scoring hits.   *    *<p>This method cannot be parallelized, because {@link Collector}   * supports no concurrent access.   *   * @param weight to match documents   * @param filter if non-null, a bitset used to eliminate some documents   * @param collector to receive hits   */
annotation|@
name|Override
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
specifier|final
name|Weight
name|weight
parameter_list|,
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|start
init|=
name|starts
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Collector
name|hc
init|=
operator|new
name|Collector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
specifier|final
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|start
operator|+
name|docBase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|collector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|searchables
index|[
name|i
index|]
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|hc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * apply the function to each element of the list. This method encapsulates the logic how     * to wait for concurrently executed searchables.      */
DECL|method|foreach
specifier|private
parameter_list|<
name|T
parameter_list|>
name|void
name|foreach
parameter_list|(
name|Function
argument_list|<
name|T
argument_list|>
name|func
parameter_list|,
name|List
argument_list|<
name|Future
argument_list|<
name|T
argument_list|>
argument_list|>
name|list
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Future
argument_list|<
name|T
argument_list|>
name|future
range|:
name|list
control|)
block|{
try|try
block|{
name|func
operator|.
name|apply
argument_list|(
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
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
block|}
comment|// Both functions could be reduced to Int as other values of TopDocs
comment|// are not needed. Using sep. functions is more self documenting.
comment|/**    * A function with one argument    * @param<T> the argument type    */
DECL|interface|Function
specifier|private
specifier|static
interface|interface
name|Function
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|apply
specifier|abstract
name|void
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
function_decl|;
block|}
comment|/**    * Counts the total number of hits for all {@link TopDocs} instances    * provided.     */
DECL|class|CountTotalHits
specifier|private
specifier|static
specifier|final
class|class
name|CountTotalHits
parameter_list|<
name|T
extends|extends
name|TopDocs
parameter_list|>
implements|implements
name|Function
argument_list|<
name|T
argument_list|>
block|{
DECL|field|totalHits
name|int
name|totalHits
init|=
literal|0
decl_stmt|;
DECL|field|maxScore
name|float
name|maxScore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|method|apply
specifier|public
name|void
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
block|{
name|totalHits
operator|+=
name|t
operator|.
name|totalHits
expr_stmt|;
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxScore
argument_list|,
name|t
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Accumulates the document frequency for a term.    */
DECL|class|CountDocFreq
specifier|private
specifier|static
specifier|final
class|class
name|CountDocFreq
implements|implements
name|Function
argument_list|<
name|Integer
argument_list|>
block|{
DECL|field|docFreq
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
DECL|method|apply
specifier|public
name|void
name|apply
parameter_list|(
name|Integer
name|t
parameter_list|)
block|{
name|docFreq
operator|+=
name|t
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
