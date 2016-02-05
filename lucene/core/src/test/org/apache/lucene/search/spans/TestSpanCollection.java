begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|FieldType
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
name|IndexOptions
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
name|NoMergePolicy
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
name|PostingsEnum
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
name|junit
operator|.
name|Test
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
name|HashSet
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
begin_class
DECL|class|TestSpanCollection
specifier|public
class|class
name|TestSpanCollection
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
DECL|field|OFFSETS
specifier|public
specifier|static
name|FieldType
name|OFFSETS
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
static|static
block|{
name|OFFSETS
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
block|}
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
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
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
name|OFFSETS
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
DECL|class|TermCollector
specifier|private
specifier|static
class|class
name|TermCollector
implements|implements
name|SpanCollector
block|{
DECL|field|terms
specifier|final
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|collectLeaf
specifier|public
name|void
name|collectLeaf
parameter_list|(
name|PostingsEnum
name|postings
parameter_list|,
name|int
name|position
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|terms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
literal|"w1 xx w2 yy w4"
block|,
literal|"w1 w2 w1 w4 w2 w3"
block|}
decl_stmt|;
DECL|method|checkCollectedTerms
specifier|private
name|void
name|checkCollectedTerms
parameter_list|(
name|Spans
name|spans
parameter_list|,
name|TermCollector
name|collector
parameter_list|,
name|Term
modifier|...
name|expectedTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|reset
argument_list|()
expr_stmt|;
name|spans
operator|.
name|collect
argument_list|(
name|collector
argument_list|)
expr_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|expectedTerms
control|)
block|{
name|assertTrue
argument_list|(
literal|"Missing term "
operator|+
name|t
argument_list|,
name|collector
operator|.
name|terms
operator|.
name|contains
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Unexpected terms found"
argument_list|,
name|expectedTerms
operator|.
name|length
argument_list|,
name|collector
operator|.
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedNearQuery
specifier|public
name|void
name|testNestedNearQuery
parameter_list|()
throws|throws
name|IOException
block|{
comment|// near(w1, near(w2, or(w3, w4)))
name|SpanTermQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q3
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q4
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w4"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanOrQuery
name|q5
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|q4
argument_list|,
name|q3
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|q6
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q2
block|,
name|q5
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|q7
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
name|q6
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TermCollector
name|collector
init|=
operator|new
name|TermCollector
argument_list|()
decl_stmt|;
name|Spans
name|spans
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|,
name|q7
argument_list|,
name|SpanWeight
operator|.
name|Postings
operator|.
name|POSITIONS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spans
operator|.
name|advance
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|spans
operator|.
name|advance
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w4"
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOrQuery
specifier|public
name|void
name|testOrQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|SpanTermQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q3
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanOrQuery
name|orQuery
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|q2
argument_list|,
name|q3
argument_list|)
decl_stmt|;
name|TermCollector
name|collector
init|=
operator|new
name|TermCollector
argument_list|()
decl_stmt|;
name|Spans
name|spans
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|,
name|orQuery
argument_list|,
name|SpanWeight
operator|.
name|Postings
operator|.
name|POSITIONS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spans
operator|.
name|advance
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|spans
operator|.
name|advance
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpanNotQuery
specifier|public
name|void
name|testSpanNotQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|SpanTermQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q3
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|nq
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
name|q2
block|}
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanNotQuery
name|notq
init|=
operator|new
name|SpanNotQuery
argument_list|(
name|nq
argument_list|,
name|q3
argument_list|)
decl_stmt|;
name|TermCollector
name|collector
init|=
operator|new
name|TermCollector
argument_list|()
decl_stmt|;
name|Spans
name|spans
init|=
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|,
name|notq
argument_list|,
name|SpanWeight
operator|.
name|Postings
operator|.
name|POSITIONS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|spans
operator|.
name|advance
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|checkCollectedTerms
argument_list|(
name|spans
argument_list|,
name|collector
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
