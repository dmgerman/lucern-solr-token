begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
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
name|CachingTokenFilter
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
name|TokenStream
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
name|FilterIndexReader
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
name|index
operator|.
name|TermEnum
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
name|memory
operator|.
name|MemoryIndex
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
name|BooleanClause
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
name|BooleanQuery
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
name|ConstantScoreRangeQuery
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
name|DisjunctionMaxQuery
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
name|FilteredQuery
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
name|MultiPhraseQuery
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
name|PhraseQuery
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
name|search
operator|.
name|TermQuery
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanTermQuery
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
name|spans
operator|.
name|Spans
import|;
end_import
begin_comment
comment|/**  * Class used to extract {@link WeightedSpanTerm}s from a {@link Query} based on whether Terms from the query are contained in a supplied TokenStream.  */
end_comment
begin_class
DECL|class|WeightedSpanTermExtractor
specifier|public
class|class
name|WeightedSpanTermExtractor
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|cachedTokenFilter
specifier|private
name|CachingTokenFilter
name|cachedTokenFilter
decl_stmt|;
DECL|field|readers
specifier|private
name|Map
name|readers
init|=
operator|new
name|HashMap
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// Map<String, IndexReader>
DECL|field|defaultField
specifier|private
name|String
name|defaultField
decl_stmt|;
DECL|field|highlightCnstScrRngQuery
specifier|private
name|boolean
name|highlightCnstScrRngQuery
decl_stmt|;
DECL|method|WeightedSpanTermExtractor
specifier|public
name|WeightedSpanTermExtractor
parameter_list|()
block|{   }
DECL|method|WeightedSpanTermExtractor
specifier|public
name|WeightedSpanTermExtractor
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
if|if
condition|(
name|defaultField
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|defaultField
operator|=
name|defaultField
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|closeReaders
specifier|private
name|void
name|closeReaders
parameter_list|()
block|{
name|Collection
name|readerSet
init|=
name|readers
operator|.
name|values
argument_list|()
decl_stmt|;
name|Iterator
name|it
init|=
name|readerSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// alert?
block|}
block|}
block|}
comment|/**    * Fills a<code>Map</code> with<@link WeightedSpanTerm>s using the terms from the supplied<code>Query</code>.    *     * @param query    *          Query to extract Terms from    * @param terms    *          Map to place created WeightedSpanTerms in    * @throws IOException    */
DECL|method|extract
specifier|private
name|void
name|extract
parameter_list|(
name|Query
name|query
parameter_list|,
name|Map
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanClause
index|[]
name|queryClauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|query
operator|)
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|Map
name|booleanTerms
init|=
operator|new
name|HashMap
argument_list|()
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
name|queryClauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|queryClauses
index|[
name|i
index|]
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|extract
argument_list|(
name|queryClauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
argument_list|,
name|booleanTerms
argument_list|)
expr_stmt|;
block|}
block|}
name|terms
operator|.
name|putAll
argument_list|(
name|booleanTerms
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
name|Term
index|[]
name|phraseQueryTerms
init|=
operator|(
operator|(
name|PhraseQuery
operator|)
name|query
operator|)
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[
name|phraseQueryTerms
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
name|phraseQueryTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
index|[
name|i
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
name|phraseQueryTerms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|slop
init|=
operator|(
operator|(
name|PhraseQuery
operator|)
name|query
operator|)
operator|.
name|getSlop
argument_list|()
decl_stmt|;
name|boolean
name|inorder
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
block|{
name|inorder
operator|=
literal|true
expr_stmt|;
block|}
name|SpanNearQuery
name|sp
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|clauses
argument_list|,
name|slop
argument_list|,
name|inorder
argument_list|)
decl_stmt|;
name|sp
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|extractWeightedSpanTerms
argument_list|(
name|terms
argument_list|,
name|sp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
block|{
name|extractWeightedTerms
argument_list|(
name|terms
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|SpanQuery
condition|)
block|{
name|extractWeightedSpanTerms
argument_list|(
name|terms
argument_list|,
operator|(
name|SpanQuery
operator|)
name|query
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|FilteredQuery
condition|)
block|{
name|extract
argument_list|(
operator|(
operator|(
name|FilteredQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|DisjunctionMaxQuery
condition|)
block|{
name|Map
name|disjunctTerms
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
operator|(
operator|(
name|DisjunctionMaxQuery
operator|)
name|query
operator|)
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|extract
argument_list|(
operator|(
name|Query
operator|)
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|disjunctTerms
argument_list|)
expr_stmt|;
block|}
name|terms
operator|.
name|putAll
argument_list|(
name|disjunctTerms
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|MultiPhraseQuery
condition|)
block|{
specifier|final
name|MultiPhraseQuery
name|mpq
init|=
operator|(
name|MultiPhraseQuery
operator|)
name|query
decl_stmt|;
specifier|final
name|List
name|termArrays
init|=
name|mpq
operator|.
name|getTermArrays
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|positions
init|=
name|mpq
operator|.
name|getPositions
argument_list|()
decl_stmt|;
if|if
condition|(
name|positions
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|maxPosition
init|=
name|positions
index|[
name|positions
operator|.
name|length
operator|-
literal|1
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
name|positions
operator|.
name|length
operator|-
literal|1
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|positions
index|[
name|i
index|]
operator|>
name|maxPosition
condition|)
block|{
name|maxPosition
operator|=
name|positions
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
specifier|final
name|List
index|[]
name|disjunctLists
init|=
operator|new
name|List
index|[
name|maxPosition
operator|+
literal|1
index|]
decl_stmt|;
name|int
name|distinctPositions
init|=
literal|0
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
name|termArrays
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Term
index|[]
name|termArray
init|=
operator|(
name|Term
index|[]
operator|)
name|termArrays
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
name|disjuncts
init|=
name|disjunctLists
index|[
name|positions
index|[
name|i
index|]
index|]
decl_stmt|;
if|if
condition|(
name|disjuncts
operator|==
literal|null
condition|)
block|{
name|disjuncts
operator|=
operator|(
name|disjunctLists
index|[
name|positions
index|[
name|i
index|]
index|]
operator|=
operator|new
name|ArrayList
argument_list|(
name|termArray
operator|.
name|length
argument_list|)
operator|)
expr_stmt|;
operator|++
name|distinctPositions
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|termArray
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|disjuncts
operator|.
name|add
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
name|termArray
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|positionGaps
init|=
literal|0
decl_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
specifier|final
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[
name|distinctPositions
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
name|disjunctLists
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|List
name|disjuncts
init|=
name|disjunctLists
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|disjuncts
operator|!=
literal|null
condition|)
block|{
name|clauses
index|[
name|position
operator|++
index|]
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|(
name|SpanQuery
index|[]
operator|)
name|disjuncts
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|disjuncts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|++
name|positionGaps
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|slop
init|=
name|mpq
operator|.
name|getSlop
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|inorder
init|=
operator|(
name|slop
operator|==
literal|0
operator|)
decl_stmt|;
name|SpanNearQuery
name|sp
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|clauses
argument_list|,
name|slop
operator|+
name|positionGaps
argument_list|,
name|inorder
argument_list|)
decl_stmt|;
name|sp
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|extractWeightedSpanTerms
argument_list|(
name|terms
argument_list|,
name|sp
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreRangeQuery
condition|)
block|{
name|ConstantScoreRangeQuery
name|q
init|=
operator|(
name|ConstantScoreRangeQuery
operator|)
name|query
decl_stmt|;
name|Term
name|lower
init|=
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|q
operator|.
name|getLowerVal
argument_list|()
argument_list|)
decl_stmt|;
name|Term
name|upper
init|=
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|q
operator|.
name|getUpperVal
argument_list|()
argument_list|)
decl_stmt|;
name|FilterIndexReader
name|fir
init|=
operator|new
name|FilterIndexReader
argument_list|(
name|getReaderForField
argument_list|(
name|fieldName
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|TermEnum
name|te
init|=
name|fir
operator|.
name|terms
argument_list|(
name|lower
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
do|do
block|{
name|Term
name|term
init|=
name|te
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|upper
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
condition|)
do|;
name|extract
argument_list|(
name|bq
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// NO-OP
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"found none"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Fills a<code>Map</code> with<@link WeightedSpanTerm>s using the terms from the supplied<code>SpanQuery</code>.    *     * @param terms    *          Map to place created WeightedSpanTerms in    * @param spanQuery    *          SpanQuery to extract Terms from    * @throws IOException    */
DECL|method|extractWeightedSpanTerms
specifier|private
name|void
name|extractWeightedSpanTerms
parameter_list|(
name|Map
name|terms
parameter_list|,
name|SpanQuery
name|spanQuery
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
name|nonWeightedTerms
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|spanQuery
operator|.
name|extractTerms
argument_list|(
name|nonWeightedTerms
argument_list|)
expr_stmt|;
name|Set
name|fieldNames
decl_stmt|;
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
name|fieldNames
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|nonWeightedTerms
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Term
name|queryTerm
init|=
operator|(
name|Term
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|fieldNames
operator|.
name|add
argument_list|(
name|queryTerm
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fieldNames
operator|=
operator|new
name|HashSet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fieldNames
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
comment|// To support the use of the default field name
if|if
condition|(
name|defaultField
operator|!=
literal|null
condition|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|defaultField
argument_list|)
expr_stmt|;
block|}
name|Iterator
name|it
init|=
name|fieldNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|List
name|spanPositions
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|field
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|getReaderForField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|Spans
name|spans
init|=
name|spanQuery
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// collect span positions
while|while
condition|(
name|spans
operator|.
name|next
argument_list|()
condition|)
block|{
name|spanPositions
operator|.
name|add
argument_list|(
operator|new
name|PositionSpan
argument_list|(
name|spans
operator|.
name|start
argument_list|()
argument_list|,
name|spans
operator|.
name|end
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|cachedTokenFilter
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|spanPositions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// no spans found
return|return;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|nonWeightedTerms
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Term
name|queryTerm
init|=
operator|(
name|Term
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldNameComparator
argument_list|(
name|queryTerm
operator|.
name|field
argument_list|()
argument_list|)
condition|)
block|{
name|WeightedSpanTerm
name|weightedSpanTerm
init|=
operator|(
name|WeightedSpanTerm
operator|)
name|terms
operator|.
name|get
argument_list|(
name|queryTerm
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|weightedSpanTerm
operator|==
literal|null
condition|)
block|{
name|weightedSpanTerm
operator|=
operator|new
name|WeightedSpanTerm
argument_list|(
name|spanQuery
operator|.
name|getBoost
argument_list|()
argument_list|,
name|queryTerm
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|weightedSpanTerm
operator|.
name|addPositionSpans
argument_list|(
name|spanPositions
argument_list|)
expr_stmt|;
name|weightedSpanTerm
operator|.
name|positionSensitive
operator|=
literal|true
expr_stmt|;
name|terms
operator|.
name|put
argument_list|(
name|queryTerm
operator|.
name|text
argument_list|()
argument_list|,
name|weightedSpanTerm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|spanPositions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|weightedSpanTerm
operator|.
name|addPositionSpans
argument_list|(
name|spanPositions
argument_list|)
expr_stmt|;
name|weightedSpanTerm
operator|.
name|positionSensitive
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Fills a<code>Map</code> with<@link WeightedSpanTerm>s using the terms from the supplied<code>Query</code>.    *     * @param terms    *          Map to place created WeightedSpanTerms in    * @param query    *          Query to extract Terms from    * @throws IOException    */
DECL|method|extractWeightedTerms
specifier|private
name|void
name|extractWeightedTerms
parameter_list|(
name|Map
name|terms
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
name|nonWeightedTerms
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|query
operator|.
name|extractTerms
argument_list|(
name|nonWeightedTerms
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|nonWeightedTerms
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Term
name|queryTerm
init|=
operator|(
name|Term
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldNameComparator
argument_list|(
name|queryTerm
operator|.
name|field
argument_list|()
argument_list|)
condition|)
block|{
name|WeightedSpanTerm
name|weightedSpanTerm
init|=
operator|new
name|WeightedSpanTerm
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|,
name|queryTerm
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|terms
operator|.
name|put
argument_list|(
name|queryTerm
operator|.
name|text
argument_list|()
argument_list|,
name|weightedSpanTerm
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Necessary to implement matches for queries against<code>defaultField</code>    */
DECL|method|fieldNameComparator
specifier|private
name|boolean
name|fieldNameComparator
parameter_list|(
name|String
name|fieldNameToCheck
parameter_list|)
block|{
name|boolean
name|rv
init|=
name|fieldName
operator|==
literal|null
operator|||
name|fieldNameToCheck
operator|==
name|fieldName
operator|||
name|fieldNameToCheck
operator|==
name|defaultField
decl_stmt|;
return|return
name|rv
return|;
block|}
DECL|method|getReaderForField
specifier|private
name|IndexReader
name|getReaderForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|MemoryIndex
name|indexer
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|indexer
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|cachedTokenFilter
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|indexer
operator|.
name|createSearcher
argument_list|()
decl_stmt|;
name|reader
operator|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
name|readers
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
comment|/**    * Creates a Map of<code>WeightedSpanTerms</code> from the given<code>Query</code> and<code>TokenStream</code>.    *     *<p>    *     * @param query    *          that caused hit    * @param tokenStream    *          of text to be highlighted    * @return    * @throws IOException    */
DECL|method|getWeightedSpanTerms
specifier|public
name|Map
name|getWeightedSpanTerms
parameter_list|(
name|Query
name|query
parameter_list|,
name|CachingTokenFilter
name|cachingTokenFilter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fieldName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|cachedTokenFilter
operator|=
name|cachingTokenFilter
expr_stmt|;
name|Map
name|terms
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
try|try
block|{
name|extract
argument_list|(
name|query
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeReaders
argument_list|()
expr_stmt|;
block|}
return|return
name|terms
return|;
block|}
comment|/**    * Creates a Map of<code>WeightedSpanTerms</code> from the given<code>Query</code> and<code>TokenStream</code>.    *     *<p>    *     * @param query    *          that caused hit    * @param tokenStream    *          of text to be highlighted    * @param fieldName    *          restricts Term's used based on field name    * @return    * @throws IOException    */
DECL|method|getWeightedSpanTerms
specifier|public
name|Map
name|getWeightedSpanTerms
parameter_list|(
name|Query
name|query
parameter_list|,
name|CachingTokenFilter
name|cachingTokenFilter
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
name|Map
name|terms
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|this
operator|.
name|cachedTokenFilter
operator|=
name|cachingTokenFilter
expr_stmt|;
try|try
block|{
name|extract
argument_list|(
name|query
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeReaders
argument_list|()
expr_stmt|;
block|}
return|return
name|terms
return|;
block|}
comment|/**    * Creates a Map of<code>WeightedSpanTerms</code> from the given<code>Query</code> and<code>TokenStream</code>. Uses a supplied    *<code>IndexReader</code> to properly weight terms (for gradient highlighting).    *     *<p>    *     * @param query    *          that caused hit    * @param tokenStream    *          of text to be highlighted    * @param fieldName    *          restricts Term's used based on field name    * @param reader    *          to use for scoring    * @return    * @throws IOException    */
DECL|method|getWeightedSpanTermsWithScores
specifier|public
name|Map
name|getWeightedSpanTermsWithScores
parameter_list|(
name|Query
name|query
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|cachedTokenFilter
operator|=
operator|new
name|CachingTokenFilter
argument_list|(
name|tokenStream
argument_list|)
expr_stmt|;
name|Map
name|terms
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|extract
argument_list|(
name|query
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|int
name|totalNumDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|Set
name|weightedTerms
init|=
name|terms
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|Iterator
name|it
init|=
name|weightedTerms
operator|.
name|iterator
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|WeightedSpanTerm
name|weightedSpanTerm
init|=
operator|(
name|WeightedSpanTerm
operator|)
name|terms
operator|.
name|get
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|docFreq
init|=
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|weightedSpanTerm
operator|.
name|term
argument_list|)
argument_list|)
decl_stmt|;
comment|// IDF algorithm taken from DefaultSimilarity class
name|float
name|idf
init|=
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
operator|(
name|float
operator|)
name|totalNumDocs
operator|/
call|(
name|double
call|)
argument_list|(
name|docFreq
operator|+
literal|1
argument_list|)
argument_list|)
operator|+
literal|1.0
argument_list|)
decl_stmt|;
name|weightedSpanTerm
operator|.
name|weight
operator|*=
name|idf
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|closeReaders
argument_list|()
expr_stmt|;
block|}
return|return
name|terms
return|;
block|}
DECL|method|isHighlightCnstScrRngQuery
specifier|public
name|boolean
name|isHighlightCnstScrRngQuery
parameter_list|()
block|{
return|return
name|highlightCnstScrRngQuery
return|;
block|}
DECL|method|setHighlightCnstScrRngQuery
specifier|public
name|void
name|setHighlightCnstScrRngQuery
parameter_list|(
name|boolean
name|highlightCnstScrRngQuery
parameter_list|)
block|{
name|this
operator|.
name|highlightCnstScrRngQuery
operator|=
name|highlightCnstScrRngQuery
expr_stmt|;
block|}
block|}
end_class
end_unit
