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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|_TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Tests SlowCollatedStringComparator, SlowCollatedTermRangeQuery, and SlowCollatedTermRangeFilter  */
end_comment
begin_class
DECL|class|TestSlowCollationMethods
specifier|public
class|class
name|TestSlowCollationMethods
extends|extends
name|LuceneTestCase
block|{
DECL|field|collator
specifier|private
specifier|static
name|Collator
name|collator
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|dir
specifier|private
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|numDocs
specifier|private
specifier|static
name|int
name|numDocs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Locale
name|locale
init|=
name|LuceneTestCase
operator|.
name|randomLocale
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|collator
operator|=
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|IDENTICAL
argument_list|)
expr_stmt|;
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|NO_DECOMPOSITION
argument_list|)
expr_stmt|;
name|numDocs
operator|=
literal|1000
operator|*
name|RANDOM_MULTIPLIER
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
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
name|numDocs
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
name|String
name|value
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
name|value
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
name|NOT_ANALYZED_NO_NORMS
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
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
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|collator
operator|=
literal|null
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|Exception
block|{
name|SortField
name|sf
init|=
operator|new
name|SortField
argument_list|(
literal|"field"
argument_list|,
operator|new
name|FieldComparatorSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FieldComparator
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SlowCollatedStringComparator
argument_list|(
name|numHits
argument_list|,
name|fieldname
argument_list|,
name|collator
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|TopFieldDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|null
argument_list|,
name|numDocs
argument_list|,
operator|new
name|Sort
argument_list|(
name|sf
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|prev
init|=
literal|""
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|String
name|value
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|collator
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|prev
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|prev
operator|=
name|value
expr_stmt|;
block|}
block|}
DECL|method|doTestRanges
specifier|private
name|void
name|doTestRanges
parameter_list|(
name|String
name|startPoint
parameter_list|,
name|String
name|endPoint
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|Exception
block|{
comment|// positive test
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|String
name|value
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|collator
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|startPoint
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|collator
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|endPoint
argument_list|)
operator|<=
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// negative test
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|bq
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|String
name|value
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|collator
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|startPoint
argument_list|)
operator|<
literal|0
operator|||
name|collator
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|endPoint
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRangeQuery
specifier|public
name|void
name|testRangeQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numQueries
init|=
literal|50
operator|*
name|RANDOM_MULTIPLIER
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|startPoint
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|String
name|endPoint
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|SlowCollatedTermRangeQuery
argument_list|(
literal|"field"
argument_list|,
name|startPoint
argument_list|,
name|endPoint
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|collator
argument_list|)
decl_stmt|;
name|doTestRanges
argument_list|(
name|startPoint
argument_list|,
name|endPoint
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRangeFilter
specifier|public
name|void
name|testRangeFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numQueries
init|=
literal|50
operator|*
name|RANDOM_MULTIPLIER
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|startPoint
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|String
name|endPoint
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|SlowCollatedTermRangeFilter
argument_list|(
literal|"field"
argument_list|,
name|startPoint
argument_list|,
name|endPoint
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|collator
argument_list|)
argument_list|)
decl_stmt|;
name|doTestRanges
argument_list|(
name|startPoint
argument_list|,
name|endPoint
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
