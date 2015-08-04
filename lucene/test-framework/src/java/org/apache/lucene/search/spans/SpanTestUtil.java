begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|IOException
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
name|search
operator|.
name|QueryUtils
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import
begin_comment
comment|/** Some utility methods used for testing span queries */
end_comment
begin_class
DECL|class|SpanTestUtil
specifier|public
class|class
name|SpanTestUtil
block|{
comment|/**     * Adds additional asserts to a spanquery. Highly recommended     * if you want tests to actually be debuggable.    */
DECL|method|spanQuery
specifier|public
specifier|static
name|SpanQuery
name|spanQuery
parameter_list|(
name|SpanQuery
name|query
parameter_list|)
block|{
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
operator|new
name|AssertingSpanQuery
argument_list|(
name|query
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanTermQuery (with additional asserts).    */
DECL|method|spanTermQuery
specifier|public
specifier|static
name|SpanQuery
name|spanTermQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|term
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanOrQuery (with additional asserts) from the provided {@code terms}.    */
DECL|method|spanOrQuery
specifier|public
specifier|static
name|SpanQuery
name|spanOrQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
modifier|...
name|terms
parameter_list|)
block|{
name|SpanQuery
index|[]
name|subqueries
init|=
operator|new
name|SpanQuery
index|[
name|terms
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subqueries
index|[
name|i
index|]
operator|=
name|spanTermQuery
argument_list|(
name|field
argument_list|,
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|spanOrQuery
argument_list|(
name|subqueries
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanOrQuery (with additional asserts).    */
DECL|method|spanOrQuery
specifier|public
specifier|static
name|SpanQuery
name|spanOrQuery
parameter_list|(
name|SpanQuery
modifier|...
name|subqueries
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanOrQuery
argument_list|(
name|subqueries
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanNotQuery (with additional asserts).    */
DECL|method|spanNotQuery
specifier|public
specifier|static
name|SpanQuery
name|spanNotQuery
parameter_list|(
name|SpanQuery
name|include
parameter_list|,
name|SpanQuery
name|exclude
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanNotQuery
argument_list|(
name|include
argument_list|,
name|exclude
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanNotQuery (with additional asserts).    */
DECL|method|spanNotQuery
specifier|public
specifier|static
name|SpanQuery
name|spanNotQuery
parameter_list|(
name|SpanQuery
name|include
parameter_list|,
name|SpanQuery
name|exclude
parameter_list|,
name|int
name|pre
parameter_list|,
name|int
name|post
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanNotQuery
argument_list|(
name|include
argument_list|,
name|exclude
argument_list|,
name|pre
argument_list|,
name|post
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanFirstQuery (with additional asserts).    */
DECL|method|spanFirstQuery
specifier|public
specifier|static
name|SpanQuery
name|spanFirstQuery
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanFirstQuery
argument_list|(
name|query
argument_list|,
name|end
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanPositionRangeQuery (with additional asserts).    */
DECL|method|spanPositionRangeQuery
specifier|public
specifier|static
name|SpanQuery
name|spanPositionRangeQuery
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|query
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanContainingQuery (with additional asserts).    */
DECL|method|spanContainingQuery
specifier|public
specifier|static
name|SpanQuery
name|spanContainingQuery
parameter_list|(
name|SpanQuery
name|big
parameter_list|,
name|SpanQuery
name|little
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanContainingQuery
argument_list|(
name|big
argument_list|,
name|little
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new SpanWithinQuery (with additional asserts).    */
DECL|method|spanWithinQuery
specifier|public
specifier|static
name|SpanQuery
name|spanWithinQuery
parameter_list|(
name|SpanQuery
name|big
parameter_list|,
name|SpanQuery
name|little
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanWithinQuery
argument_list|(
name|big
argument_list|,
name|little
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new ordered SpanNearQuery (with additional asserts) from the provided {@code terms}    */
DECL|method|spanNearOrderedQuery
specifier|public
specifier|static
name|SpanQuery
name|spanNearOrderedQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|slop
parameter_list|,
name|String
modifier|...
name|terms
parameter_list|)
block|{
name|SpanQuery
index|[]
name|subqueries
init|=
operator|new
name|SpanQuery
index|[
name|terms
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subqueries
index|[
name|i
index|]
operator|=
name|spanTermQuery
argument_list|(
name|field
argument_list|,
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|spanNearOrderedQuery
argument_list|(
name|slop
argument_list|,
name|subqueries
argument_list|)
return|;
block|}
comment|/**    * Makes a new ordered SpanNearQuery (with additional asserts)    */
DECL|method|spanNearOrderedQuery
specifier|public
specifier|static
name|SpanQuery
name|spanNearOrderedQuery
parameter_list|(
name|int
name|slop
parameter_list|,
name|SpanQuery
modifier|...
name|subqueries
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subqueries
argument_list|,
name|slop
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Makes a new unordered SpanNearQuery (with additional asserts) from the provided {@code terms}    */
DECL|method|spanNearUnorderedQuery
specifier|public
specifier|static
name|SpanQuery
name|spanNearUnorderedQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|slop
parameter_list|,
name|String
modifier|...
name|terms
parameter_list|)
block|{
name|SpanNearQuery
operator|.
name|Builder
name|builder
init|=
name|SpanNearQuery
operator|.
name|newUnorderedNearQuery
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|term
range|:
name|terms
control|)
block|{
name|builder
operator|.
name|addClause
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|spanQuery
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Makes a new unordered SpanNearQuery (with additional asserts)    */
DECL|method|spanNearUnorderedQuery
specifier|public
specifier|static
name|SpanQuery
name|spanNearUnorderedQuery
parameter_list|(
name|int
name|slop
parameter_list|,
name|SpanQuery
modifier|...
name|subqueries
parameter_list|)
block|{
return|return
name|spanQuery
argument_list|(
operator|new
name|SpanNearQuery
argument_list|(
name|subqueries
argument_list|,
name|slop
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Assert the next iteration from {@code spans} is a match    * from {@code start} to {@code end} in {@code doc}.    */
DECL|method|assertNext
specifier|public
specifier|static
name|void
name|assertNext
parameter_list|(
name|Spans
name|spans
parameter_list|,
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|spans
operator|.
name|docID
argument_list|()
operator|>=
name|doc
condition|)
block|{
name|assertEquals
argument_list|(
literal|"docId"
argument_list|,
name|doc
argument_list|,
name|spans
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// nextDoc needed before testing start/end
if|if
condition|(
name|spans
operator|.
name|docID
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
literal|"nextStartPosition of previous doc"
argument_list|,
name|Spans
operator|.
name|NO_MORE_POSITIONS
argument_list|,
name|spans
operator|.
name|nextStartPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"endPosition of previous doc"
argument_list|,
name|Spans
operator|.
name|NO_MORE_POSITIONS
argument_list|,
name|spans
operator|.
name|endPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"nextDoc"
argument_list|,
name|doc
argument_list|,
name|spans
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|!=
name|Spans
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|assertEquals
argument_list|(
literal|"first startPosition"
argument_list|,
operator|-
literal|1
argument_list|,
name|spans
operator|.
name|startPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first endPosition"
argument_list|,
operator|-
literal|1
argument_list|,
name|spans
operator|.
name|endPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|doc
operator|!=
name|Spans
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|assertEquals
argument_list|(
literal|"nextStartPosition"
argument_list|,
name|start
argument_list|,
name|spans
operator|.
name|nextStartPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"startPosition"
argument_list|,
name|start
argument_list|,
name|spans
operator|.
name|startPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"endPosition"
argument_list|,
name|end
argument_list|,
name|spans
operator|.
name|endPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Assert that {@code spans} is exhausted.    */
DECL|method|assertFinished
specifier|public
specifier|static
name|void
name|assertFinished
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|spans
operator|!=
literal|null
condition|)
block|{
comment|// null Spans is empty
name|assertNext
argument_list|(
name|spans
argument_list|,
name|Spans
operator|.
name|NO_MORE_DOCS
argument_list|,
operator|-
literal|2
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|// start and end positions will be ignored
block|}
block|}
block|}
end_class
end_unit
