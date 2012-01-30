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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
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
name|Bits
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
name|ToStringUtils
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
name|Set
import|;
end_import
begin_comment
comment|/**  * A query that applies a filter to the results of another query.  *  *<p>Note: the bits are retrieved from the filter each time this  * query is used in a search - use a CachingWrapperFilter to avoid  * regenerating the bits every time.  * @since   1.4  * @see     CachingWrapperFilter  */
end_comment
begin_class
DECL|class|FilteredQuery
specifier|public
class|class
name|FilteredQuery
extends|extends
name|Query
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
comment|/**    * Constructs a new query which applies a filter to the results of the original query.    * {@link Filter#getDocIdSet} will be called every time this query is used in a search.    * @param query  Query to be filtered, cannot be<code>null</code>.    * @param filter Filter to apply to query results, cannot be<code>null</code>.    */
DECL|method|FilteredQuery
specifier|public
name|FilteredQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
operator|||
name|filter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Query and filter cannot be null."
argument_list|)
throw|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**    * Expert: decides if a filter should be executed as "random-access" or not.    * random-access means the filter "filters" in a similar way as deleted docs are filtered    * in lucene. This is faster when the filter accepts many documents.    * However, when the filter is very sparse, it can be faster to execute the query+filter    * as a conjunction in some cases.    *     * The default implementation returns true if the first document accepted by the    * filter is< 100.    *     * @lucene.internal    */
DECL|method|useRandomAccess
specifier|protected
name|boolean
name|useRandomAccess
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|int
name|firstFilterDoc
parameter_list|)
block|{
return|return
name|firstFilterDoc
operator|<
literal|100
return|;
block|}
comment|/**    * Returns a Weight that applies the filter to the enclosed query's Weight.    * This is accomplished by overriding the Scorer returned by the Weight.    */
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
specifier|final
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|weight
init|=
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
comment|// TODO: Support out-of-order scoring!
comment|// For now we return false here, as we always get the scorer in order
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|getValueForNormalization
argument_list|()
operator|*
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
return|;
comment|// boost sub-weight
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|weight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
operator|*
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
comment|// incorporate boost
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|ir
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|inner
init|=
name|weight
operator|.
name|explain
argument_list|(
name|ir
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|Filter
name|f
init|=
name|FilteredQuery
operator|.
name|this
operator|.
name|filter
decl_stmt|;
name|DocIdSet
name|docIdSet
init|=
name|f
operator|.
name|getDocIdSet
argument_list|(
name|ir
argument_list|,
name|ir
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|docIdSetIterator
init|=
name|docIdSet
operator|==
literal|null
condition|?
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
else|:
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|docIdSetIterator
operator|==
literal|null
condition|)
block|{
name|docIdSetIterator
operator|=
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|i
argument_list|)
operator|==
name|i
condition|)
block|{
return|return
name|inner
return|;
block|}
else|else
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"failure to match filter: "
operator|+
name|f
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|inner
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|// return this query
annotation|@
name|Override
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|FilteredQuery
operator|.
name|this
return|;
block|}
comment|// return a filtering scorer
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|filter
operator|!=
literal|null
assert|;
specifier|final
name|DocIdSet
name|filterDocIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterDocIdSet
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|filterIter
init|=
name|filterDocIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|filterIter
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|firstFilterDoc
init|=
name|filterIter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstFilterDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Bits
name|filterAcceptDocs
init|=
name|filterDocIdSet
operator|.
name|bits
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|useRandomAccess
init|=
operator|(
name|filterAcceptDocs
operator|!=
literal|null
operator|&&
name|FilteredQuery
operator|.
name|this
operator|.
name|useRandomAccess
argument_list|(
name|filterAcceptDocs
argument_list|,
name|firstFilterDoc
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|useRandomAccess
condition|)
block|{
comment|// if we are using random access, we return the inner scorer, just with other acceptDocs
comment|// TODO, replace this by when BooleanWeight is fixed to be consistent with its scorer implementations:
comment|// return weight.scorer(context, scoreDocsInOrder, topScorer, filterAcceptDocs);
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
name|topScorer
argument_list|,
name|filterAcceptDocs
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|firstFilterDoc
operator|>
operator|-
literal|1
assert|;
comment|// we are gonna advance() this scorer, so we set inorder=true/toplevel=false
comment|// we pass null as acceptDocs, as our filter has already respected acceptDocs, no need to do twice
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|(
name|scorer
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|Scorer
argument_list|(
name|this
argument_list|)
block|{
specifier|private
name|int
name|scorerDoc
init|=
operator|-
literal|1
decl_stmt|,
name|filterDoc
init|=
name|firstFilterDoc
decl_stmt|;
comment|// optimization: we are topScorer and collect directly using short-circuited algo
annotation|@
name|Override
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|filterDoc
init|=
name|firstFilterDoc
decl_stmt|;
name|int
name|scorerDoc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
decl_stmt|;
comment|// the normalization trick already applies the boost of this query,
comment|// so we can use the wrapped scorer directly:
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|scorerDoc
operator|==
name|filterDoc
condition|)
block|{
comment|// Check if scorer has exhausted, only before collecting.
if|if
condition|(
name|scorerDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|collector
operator|.
name|collect
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
name|filterDoc
operator|=
name|filterIter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scorerDoc
operator|>
name|filterDoc
condition|)
block|{
name|filterDoc
operator|=
name|filterIter
operator|.
name|advance
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|int
name|advanceToNextCommonDoc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|scorerDoc
operator|<
name|filterDoc
condition|)
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scorerDoc
operator|==
name|filterDoc
condition|)
block|{
return|return
name|scorerDoc
return|;
block|}
else|else
block|{
name|filterDoc
operator|=
name|filterIter
operator|.
name|advance
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|// don't go to next doc on first call
comment|// (because filterIter is already on first doc):
if|if
condition|(
name|scorerDoc
operator|!=
operator|-
literal|1
condition|)
block|{
name|filterDoc
operator|=
name|filterIter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|advanceToNextCommonDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>
name|filterDoc
condition|)
block|{
name|filterDoc
operator|=
name|filterIter
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
return|return
name|advanceToNextCommonDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|scorerDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
block|}
return|;
block|}
comment|/** Rewrites the query. If the wrapped is an instance of    * {@link MatchAllDocsQuery} it returns a {@link ConstantScoreQuery}. Otherwise    * it returns a new {@code FilteredQuery} wrapping the rewritten query. */
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|queryRewritten
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryRewritten
operator|instanceof
name|MatchAllDocsQuery
condition|)
block|{
comment|// Special case: If the query is a MatchAllDocsQuery, we only
comment|// return a CSQ(filter).
specifier|final
name|Query
name|rewritten
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
comment|// Combine boost of MatchAllDocsQuery and the wrapped rewritten query:
name|rewritten
operator|.
name|setBoost
argument_list|(
name|this
operator|.
name|getBoost
argument_list|()
operator|*
name|queryRewritten
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rewritten
return|;
block|}
if|if
condition|(
name|queryRewritten
operator|!=
name|query
condition|)
block|{
comment|// rewrite to a new FilteredQuery wrapping the rewritten query
specifier|final
name|Query
name|rewritten
init|=
operator|new
name|FilteredQuery
argument_list|(
name|queryRewritten
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|rewritten
operator|.
name|setBoost
argument_list|(
name|this
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rewritten
return|;
block|}
else|else
block|{
comment|// nothing to rewrite, we are done!
return|return
name|this
return|;
block|}
block|}
DECL|method|getQuery
specifier|public
specifier|final
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|getFilter
specifier|public
specifier|final
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|getQuery
argument_list|()
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"filtered("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")->"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
assert|assert
name|o
operator|instanceof
name|FilteredQuery
assert|;
specifier|final
name|FilteredQuery
name|fq
init|=
operator|(
name|FilteredQuery
operator|)
name|o
decl_stmt|;
return|return
name|fq
operator|.
name|query
operator|.
name|equals
argument_list|(
name|this
operator|.
name|query
argument_list|)
operator|&&
name|fq
operator|.
name|filter
operator|.
name|equals
argument_list|(
name|this
operator|.
name|filter
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|query
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|filter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
block|}
end_class
end_unit
