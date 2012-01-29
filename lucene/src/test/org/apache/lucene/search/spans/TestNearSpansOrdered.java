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
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|TextField
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
name|AtomicReader
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
operator|.
name|ReaderContext
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
name|RandomIndexWriter
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
name|CheckHits
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
name|Explanation
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
name|Weight
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
name|Scorer
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
name|Directory
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
name|LuceneTestCase
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
name|ReaderUtil
import|;
end_import
begin_class
DECL|class|TestNearSpansOrdered
specifier|public
class|class
name|TestNearSpansOrdered
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|protected
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|directory
specifier|protected
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|protected
name|IndexReader
name|reader
decl_stmt|;
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
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
name|docFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|FIELD
argument_list|,
name|docFields
index|[
name|i
index|]
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|field|docFields
specifier|protected
name|String
index|[]
name|docFields
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3 zz"
block|,
literal|"w1 xx w2 yy w3"
block|,
literal|"w1 w3 xx w2 yy w3 zz"
block|}
decl_stmt|;
DECL|method|makeQuery
specifier|protected
name|SpanNearQuery
name|makeQuery
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|,
name|String
name|s3
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s1
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s2
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s3
argument_list|)
argument_list|)
block|}
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
DECL|method|makeQuery
specifier|protected
name|SpanNearQuery
name|makeQuery
parameter_list|()
block|{
return|return
name|makeQuery
argument_list|(
literal|"w1"
argument_list|,
literal|"w2"
argument_list|,
literal|"w3"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|testSpanNearQuery
specifier|public
name|void
name|testSpanNearQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|CheckHits
operator|.
name|checkHits
argument_list|(
name|random
argument_list|,
name|q
argument_list|,
name|FIELD
argument_list|,
name|searcher
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|s
specifier|public
name|String
name|s
parameter_list|(
name|Spans
name|span
parameter_list|)
block|{
return|return
name|s
argument_list|(
name|span
operator|.
name|doc
argument_list|()
argument_list|,
name|span
operator|.
name|start
argument_list|()
argument_list|,
name|span
operator|.
name|end
argument_list|()
argument_list|)
return|;
block|}
DECL|method|s
specifier|public
name|String
name|s
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
literal|"s("
operator|+
name|doc
operator|+
literal|","
operator|+
name|start
operator|+
literal|","
operator|+
name|end
operator|+
literal|")"
return|;
block|}
DECL|method|testNearSpansNext
specifier|public
name|void
name|testNearSpansNext
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * test does not imply that skipTo(doc+1) should work exactly the    * same as next -- it's only applicable in this case since we know doc    * does not contain more than one span    */
DECL|method|testNearSpansSkipToLikeNext
specifier|public
name|void
name|testNearSpansSkipToLikeNext
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNearSpansNextThenSkipTo
specifier|public
name|void
name|testNearSpansNextThenSkipTo
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNearSpansNextThenSkipPast
specifier|public
name|void
name|testNearSpansNextThenSkipPast
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNearSpansSkipPast
specifier|public
name|void
name|testNearSpansSkipPast
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNearSpansSkipTo0
specifier|public
name|void
name|testNearSpansSkipTo0
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNearSpansSkipTo1
specifier|public
name|void
name|testNearSpansSkipTo1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Spans
name|span
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|span
operator|.
name|skipTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|s
argument_list|(
name|span
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * not a direct test of NearSpans, but a demonstration of how/when    * this causes problems    */
DECL|method|testSpanNearScorerSkipTo1
specifier|public
name|void
name|testSpanNearScorerSkipTo1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Weight
name|w
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|ReaderContext
name|topReaderContext
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|topReaderContext
argument_list|)
decl_stmt|;
name|Scorer
name|s
init|=
name|w
operator|.
name|scorer
argument_list|(
name|leaves
index|[
literal|0
index|]
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|leaves
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|advance
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * not a direct test of NearSpans, but a demonstration of how/when    * this causes problems    */
DECL|method|testSpanNearScorerExplain
specifier|public
name|void
name|testSpanNearScorerExplain
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanNearQuery
name|q
init|=
name|makeQuery
argument_list|()
decl_stmt|;
name|Explanation
name|e
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Scorer explanation value for doc#1 isn't positive: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
literal|0.0f
operator|<
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
