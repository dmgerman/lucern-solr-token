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
name|util
operator|.
name|Calendar
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|CachingWrapperFilter
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
name|Filter
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
name|MatchAllDocsQuery
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
name|QueryWrapperFilter
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
name|Searcher
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
name|TermRangeFilter
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
name|TopDocs
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
name|store
operator|.
name|MockRAMDirectory
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
begin_class
DECL|class|ChainedFilterTest
specifier|public
class|class
name|ChainedFilterTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|MAX
specifier|public
specifier|static
specifier|final
name|int
name|MAX
init|=
literal|500
decl_stmt|;
DECL|field|directory
specifier|private
name|MockRAMDirectory
name|directory
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
comment|// private DateFilter dateFilter;   DateFilter was deprecated and removed
DECL|field|dateFilter
specifier|private
name|TermRangeFilter
name|dateFilter
decl_stmt|;
DECL|field|bobFilter
specifier|private
name|QueryWrapperFilter
name|bobFilter
decl_stmt|;
DECL|field|sueFilter
specifier|private
name|QueryWrapperFilter
name|sueFilter
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|random
operator|=
name|newRandom
argument_list|()
expr_stmt|;
name|directory
operator|=
operator|new
name|MockRAMDirectory
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
argument_list|)
decl_stmt|;
name|Calendar
name|cal
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|cal
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
literal|1041397200000L
argument_list|)
expr_stmt|;
comment|// 2003 January 01
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MAX
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
literal|"key"
argument_list|,
literal|""
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"owner"
argument_list|,
operator|(
name|i
operator|<
name|MAX
operator|/
literal|2
operator|)
condition|?
literal|"bob"
else|:
literal|"sue"
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"date"
argument_list|,
name|cal
operator|.
name|getTime
argument_list|()
operator|.
name|toString
argument_list|()
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
name|NOT_ANALYZED
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
name|cal
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|DATE
argument_list|,
literal|1
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
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// query for everything to make life easier
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
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"owner"
argument_list|,
literal|"bob"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"owner"
argument_list|,
literal|"sue"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|=
name|bq
expr_stmt|;
comment|// date filter matches everything too
comment|//Date pastTheEnd = parseDate("2099 Jan 1");
comment|// dateFilter = DateFilter.Before("date", pastTheEnd);
comment|// just treat dates as strings and select the whole range for now...
name|dateFilter
operator|=
operator|new
name|TermRangeFilter
argument_list|(
literal|"date"
argument_list|,
literal|""
argument_list|,
literal|"ZZZZ"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|bobFilter
operator|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"owner"
argument_list|,
literal|"bob"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sueFilter
operator|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"owner"
argument_list|,
literal|"sue"
argument_list|)
argument_list|)
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
DECL|method|getChainedFilter
specifier|private
name|ChainedFilter
name|getChainedFilter
parameter_list|(
name|Filter
index|[]
name|chain
parameter_list|,
name|int
index|[]
name|logic
parameter_list|)
block|{
if|if
condition|(
name|logic
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ChainedFilter
argument_list|(
name|chain
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ChainedFilter
argument_list|(
name|chain
argument_list|,
name|logic
argument_list|)
return|;
block|}
block|}
DECL|method|getChainedFilter
specifier|private
name|ChainedFilter
name|getChainedFilter
parameter_list|(
name|Filter
index|[]
name|chain
parameter_list|,
name|int
name|logic
parameter_list|)
block|{
return|return
operator|new
name|ChainedFilter
argument_list|(
name|chain
argument_list|,
name|logic
argument_list|)
return|;
block|}
DECL|method|testSingleFilter
specifier|public
name|void
name|testSingleFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|ChainedFilter
name|chain
init|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|dateFilter
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|numHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
name|MAX
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|chain
operator|=
operator|new
name|ChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|bobFilter
block|}
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX
operator|/
literal|2
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|chain
operator|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|bobFilter
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|ChainedFilter
operator|.
name|AND
block|}
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|numHits
operator|=
name|hits
operator|.
name|totalHits
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX
operator|/
literal|2
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bob"
argument_list|,
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
name|chain
operator|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|bobFilter
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|ChainedFilter
operator|.
name|ANDNOT
block|}
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|hits
operator|.
name|totalHits
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX
operator|/
literal|2
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sue"
argument_list|,
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOR
specifier|public
name|void
name|testOR
parameter_list|()
throws|throws
name|Exception
block|{
name|ChainedFilter
name|chain
init|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|sueFilter
block|,
name|bobFilter
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|numHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OR matches all"
argument_list|,
name|MAX
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testAND
specifier|public
name|void
name|testAND
parameter_list|()
throws|throws
name|Exception
block|{
name|ChainedFilter
name|chain
init|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|dateFilter
block|,
name|bobFilter
block|}
argument_list|,
name|ChainedFilter
operator|.
name|AND
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"AND matches just bob"
argument_list|,
name|MAX
operator|/
literal|2
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bob"
argument_list|,
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testXOR
specifier|public
name|void
name|testXOR
parameter_list|()
throws|throws
name|Exception
block|{
name|ChainedFilter
name|chain
init|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|dateFilter
block|,
name|bobFilter
block|}
argument_list|,
name|ChainedFilter
operator|.
name|XOR
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"XOR matches sue"
argument_list|,
name|MAX
operator|/
literal|2
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sue"
argument_list|,
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testANDNOT
specifier|public
name|void
name|testANDNOT
parameter_list|()
throws|throws
name|Exception
block|{
name|ChainedFilter
name|chain
init|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|dateFilter
block|,
name|sueFilter
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|ChainedFilter
operator|.
name|AND
block|,
name|ChainedFilter
operator|.
name|ANDNOT
block|}
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ANDNOT matches just bob"
argument_list|,
name|MAX
operator|/
literal|2
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bob"
argument_list|,
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
name|chain
operator|=
name|getChainedFilter
argument_list|(
operator|new
name|Filter
index|[]
block|{
name|bobFilter
block|,
name|bobFilter
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|ChainedFilter
operator|.
name|ANDNOT
block|,
name|ChainedFilter
operator|.
name|ANDNOT
block|}
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ANDNOT bob ANDNOT bob matches all sues"
argument_list|,
name|MAX
operator|/
literal|2
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sue"
argument_list|,
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*   private Date parseDate(String s) throws ParseException {     return new SimpleDateFormat("yyyy MMM dd", Locale.US).parse(s);   }   */
DECL|method|testWithCachingFilter
specifier|public
name|void
name|testWithCachingFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"none"
argument_list|,
literal|"none"
argument_list|)
argument_list|)
decl_stmt|;
name|QueryWrapperFilter
name|queryFilter
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|CachingWrapperFilter
name|cachingFilter
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|queryFilter
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|cachingFilter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|CachingWrapperFilter
name|cachingFilter2
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|queryFilter
argument_list|)
decl_stmt|;
name|Filter
index|[]
name|chain
init|=
operator|new
name|Filter
index|[
literal|2
index|]
decl_stmt|;
name|chain
index|[
literal|0
index|]
operator|=
name|cachingFilter
expr_stmt|;
name|chain
index|[
literal|1
index|]
operator|=
name|cachingFilter2
expr_stmt|;
name|ChainedFilter
name|cf
init|=
operator|new
name|ChainedFilter
argument_list|(
name|chain
argument_list|)
decl_stmt|;
comment|// throws java.lang.ClassCastException: org.apache.lucene.util.OpenBitSet cannot be cast to java.util.BitSet
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|cf
argument_list|,
literal|1
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class
end_unit
