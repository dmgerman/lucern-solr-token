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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|store
operator|.
name|RAMDirectory
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
name|IndexWriter
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|Field
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
begin_class
DECL|class|TestSpans
specifier|public
class|class
name|TestSpans
extends|extends
name|TestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|field
specifier|public
specifier|static
specifier|final
name|String
name|field
init|=
literal|"field"
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
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
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|docFields
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
comment|//System.out.println("set up " + getName());
block|}
DECL|field|docFields
specifier|private
name|String
index|[]
name|docFields
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3"
block|,
literal|"w1 xx w2 yy w3"
block|,
literal|"w1 w3 xx w2 yy w3"
block|,
literal|"u2 u2 u1"
block|,
literal|"u2 xx u2 u1"
block|,
literal|"u2 u2 xx u1"
block|,
literal|"u2 xx u2 yy u1"
block|,
literal|"u2 xx u1 u2"
block|,
literal|"u2 u1 xx u2"
block|,
literal|"u1 u2 xx u2"
block|,
literal|"t1 t2 t1 t3 t2 t3"
block|}
decl_stmt|;
DECL|method|makeSpanTermQuery
specifier|public
name|SpanTermQuery
name|makeSpanTermQuery
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
argument_list|)
return|;
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
index|[]
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|CheckHits
operator|.
name|checkHits
argument_list|(
name|query
argument_list|,
name|field
argument_list|,
name|searcher
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
DECL|method|orderedSlopTest3SQ
specifier|private
name|void
name|orderedSlopTest3SQ
parameter_list|(
name|SpanQuery
name|q1
parameter_list|,
name|SpanQuery
name|q2
parameter_list|,
name|SpanQuery
name|q3
parameter_list|,
name|int
name|slop
parameter_list|,
name|int
index|[]
name|expectedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|ordered
init|=
literal|true
decl_stmt|;
name|SpanNearQuery
name|snq
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
block|,
name|q3
block|}
argument_list|,
name|slop
argument_list|,
name|ordered
argument_list|)
decl_stmt|;
name|checkHits
argument_list|(
name|snq
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|orderedSlopTest3
specifier|public
name|void
name|orderedSlopTest3
parameter_list|(
name|int
name|slop
parameter_list|,
name|int
index|[]
name|expectedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|orderedSlopTest3SQ
argument_list|(
name|makeSpanTermQuery
argument_list|(
literal|"w1"
argument_list|)
argument_list|,
name|makeSpanTermQuery
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|makeSpanTermQuery
argument_list|(
literal|"w3"
argument_list|)
argument_list|,
name|slop
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|orderedSlopTest3Equal
specifier|public
name|void
name|orderedSlopTest3Equal
parameter_list|(
name|int
name|slop
parameter_list|,
name|int
index|[]
name|expectedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|orderedSlopTest3SQ
argument_list|(
name|makeSpanTermQuery
argument_list|(
literal|"w1"
argument_list|)
argument_list|,
name|makeSpanTermQuery
argument_list|(
literal|"w3"
argument_list|)
argument_list|,
name|makeSpanTermQuery
argument_list|(
literal|"w3"
argument_list|)
argument_list|,
name|slop
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|orderedSlopTest1Equal
specifier|public
name|void
name|orderedSlopTest1Equal
parameter_list|(
name|int
name|slop
parameter_list|,
name|int
index|[]
name|expectedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|orderedSlopTest3SQ
argument_list|(
name|makeSpanTermQuery
argument_list|(
literal|"u2"
argument_list|)
argument_list|,
name|makeSpanTermQuery
argument_list|(
literal|"u2"
argument_list|)
argument_list|,
name|makeSpanTermQuery
argument_list|(
literal|"u1"
argument_list|)
argument_list|,
name|slop
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrdered01
specifier|public
name|void
name|testSpanNearOrdered01
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3
argument_list|(
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrdered02
specifier|public
name|void
name|testSpanNearOrdered02
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3
argument_list|(
literal|1
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
DECL|method|testSpanNearOrdered03
specifier|public
name|void
name|testSpanNearOrdered03
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3
argument_list|(
literal|2
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrdered04
specifier|public
name|void
name|testSpanNearOrdered04
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3
argument_list|(
literal|3
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrdered05
specifier|public
name|void
name|testSpanNearOrdered05
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3
argument_list|(
literal|4
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual01
specifier|public
name|void
name|testSpanNearOrderedEqual01
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3Equal
argument_list|(
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual02
specifier|public
name|void
name|testSpanNearOrderedEqual02
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3Equal
argument_list|(
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual03
specifier|public
name|void
name|testSpanNearOrderedEqual03
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3Equal
argument_list|(
literal|2
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual04
specifier|public
name|void
name|testSpanNearOrderedEqual04
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest3Equal
argument_list|(
literal|3
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual11
specifier|public
name|void
name|testSpanNearOrderedEqual11
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest1Equal
argument_list|(
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual12
specifier|public
name|void
name|testSpanNearOrderedEqual12
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest1Equal
argument_list|(
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual13
specifier|public
name|void
name|testSpanNearOrderedEqual13
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest1Equal
argument_list|(
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|5
block|,
literal|6
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual14
specifier|public
name|void
name|testSpanNearOrderedEqual14
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest1Equal
argument_list|(
literal|2
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedEqual15
specifier|public
name|void
name|testSpanNearOrderedEqual15
parameter_list|()
throws|throws
name|Exception
block|{
name|orderedSlopTest1Equal
argument_list|(
literal|3
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanNearOrderedOverlap
specifier|public
name|void
name|testSpanNearOrderedOverlap
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|ordered
init|=
literal|true
decl_stmt|;
name|int
name|slop
init|=
literal|1
decl_stmt|;
name|SpanNearQuery
name|snq
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|makeSpanTermQuery
argument_list|(
literal|"t1"
argument_list|)
block|,
name|makeSpanTermQuery
argument_list|(
literal|"t2"
argument_list|)
block|,
name|makeSpanTermQuery
argument_list|(
literal|"t3"
argument_list|)
block|}
argument_list|,
name|slop
argument_list|,
name|ordered
argument_list|)
decl_stmt|;
name|Spans
name|spans
init|=
name|snq
operator|.
name|getSpans
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"first range"
argument_list|,
name|spans
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first doc"
argument_list|,
literal|11
argument_list|,
name|spans
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first start"
argument_list|,
literal|0
argument_list|,
name|spans
operator|.
name|start
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first end"
argument_list|,
literal|4
argument_list|,
name|spans
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"second range"
argument_list|,
name|spans
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second doc"
argument_list|,
literal|11
argument_list|,
name|spans
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second start"
argument_list|,
literal|2
argument_list|,
name|spans
operator|.
name|start
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second end"
argument_list|,
literal|6
argument_list|,
name|spans
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"third range"
argument_list|,
name|spans
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
