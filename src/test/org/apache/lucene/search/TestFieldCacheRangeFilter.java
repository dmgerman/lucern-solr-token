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
name|text
operator|.
name|Collator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|analysis
operator|.
name|SimpleAnalyzer
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
begin_comment
comment|/**  * A basic 'positive' Unit test class for the RangeFilter class.  *  *<p>  * NOTE: at the moment, this class only tests for 'positive' results,  * it does not verify the results to ensure there are no 'false positives',  * nor does it adequately test 'negative' results.  It also does not test  * that garbage in results in an Exception.  */
end_comment
begin_class
DECL|class|TestFieldCacheRangeFilter
specifier|public
class|class
name|TestFieldCacheRangeFilter
extends|extends
name|BaseTestRangeFilter
block|{
DECL|method|TestFieldCacheRangeFilter
specifier|public
name|TestFieldCacheRangeFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|TestFieldCacheRangeFilter
specifier|public
name|TestFieldCacheRangeFilter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|testRangeFilterId
specifier|public
name|void
name|testRangeFilterId
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|signedIndex
operator|.
name|index
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|medId
init|=
operator|(
operator|(
name|maxId
operator|-
name|minId
operator|)
operator|/
literal|2
operator|)
decl_stmt|;
name|String
name|minIP
init|=
name|pad
argument_list|(
name|minId
argument_list|)
decl_stmt|;
name|String
name|maxIP
init|=
name|pad
argument_list|(
name|maxId
argument_list|)
decl_stmt|;
name|String
name|medIP
init|=
name|pad
argument_list|(
name|medId
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"num of docs"
argument_list|,
name|numDocs
argument_list|,
literal|1
operator|+
name|maxId
operator|-
name|minId
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|result
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|)
argument_list|)
decl_stmt|;
comment|// test id, bounded on both ends
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"find all"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but last"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but first"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but ends"
argument_list|,
name|numDocs
operator|-
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med and up"
argument_list|,
literal|1
operator|+
name|maxId
operator|-
name|medId
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|medIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"up to med"
argument_list|,
literal|1
operator|+
name|medId
operator|-
name|minId
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// unbounded id
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min and up"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max and down"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
literal|null
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not min, but up"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not max, but down"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med and up, not max"
argument_list|,
name|maxId
operator|-
name|medId
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|medIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not min, up to med"
argument_list|,
name|medId
operator|-
name|minId
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// very small sets
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|minIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|medIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med,med,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|maxIP
argument_list|,
name|maxIP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|minIP
argument_list|,
name|minIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|,
name|minIP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nul,min,F,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|maxIP
argument_list|,
name|maxIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|maxIP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,nul,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"id"
argument_list|,
name|medIP
argument_list|,
name|medIP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"med,med,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldCacheRangeFilterRand
specifier|public
name|void
name|testFieldCacheRangeFilterRand
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|signedIndex
operator|.
name|index
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|String
name|minRP
init|=
name|pad
argument_list|(
name|signedIndex
operator|.
name|minR
argument_list|)
decl_stmt|;
name|String
name|maxRP
init|=
name|pad
argument_list|(
name|signedIndex
operator|.
name|maxR
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"num of docs"
argument_list|,
name|numDocs
argument_list|,
literal|1
operator|+
name|maxId
operator|-
name|minId
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|result
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|)
argument_list|)
decl_stmt|;
comment|// test extremes, bounded on both ends
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"find all"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but biggest"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but smallest"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all but extremes"
argument_list|,
name|numDocs
operator|-
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// unbounded
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"smallest and up"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
literal|null
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"biggest and down"
argument_list|,
name|numDocs
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
literal|null
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not smallest, but up"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
literal|null
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not biggest, but down"
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// very small sets
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|minRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|maxRP
argument_list|,
name|maxRP
argument_list|,
name|F
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,F,F"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|minRP
argument_list|,
name|minRP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"min,min,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
literal|null
argument_list|,
name|minRP
argument_list|,
name|F
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nul,min,F,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|maxRP
argument_list|,
name|maxRP
argument_list|,
name|T
argument_list|,
name|T
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,max,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|FieldCacheRangeFilter
argument_list|(
literal|"rand"
argument_list|,
name|maxRP
argument_list|,
literal|null
argument_list|,
name|T
argument_list|,
name|F
argument_list|)
argument_list|,
name|numDocs
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"max,nul,T,T"
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
