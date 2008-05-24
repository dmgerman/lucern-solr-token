begin_unit
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|*
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
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|index
operator|.
name|IndexWriter
operator|.
name|MaxFieldLength
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
name|FSDirectory
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
name|NoLockFactory
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
name|analysis
operator|.
name|Analyzer
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|*
import|;
end_import
begin_class
DECL|class|ChainedFilterTest
specifier|public
class|class
name|ChainedFilterTest
extends|extends
name|TestCase
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
name|RAMDirectory
name|directory
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
comment|// private DateFilter dateFilter;   DateFilter was deprecated and removed
DECL|field|dateFilter
specifier|private
name|RangeFilter
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
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
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
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
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
name|UN_TOKENIZED
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
name|UN_TOKENIZED
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
name|UN_TOKENIZED
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
name|Date
name|pastTheEnd
init|=
name|parseDate
argument_list|(
literal|"2099 Jan 1"
argument_list|)
decl_stmt|;
comment|// dateFilter = DateFilter.Before("date", pastTheEnd);
comment|// just treat dates as strings and select the whole range for now...
name|dateFilter
operator|=
operator|new
name|RangeFilter
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
DECL|method|getChainWithOldFilters
specifier|private
name|Filter
index|[]
name|getChainWithOldFilters
parameter_list|(
name|Filter
index|[]
name|chain
parameter_list|)
block|{
name|Filter
index|[]
name|oldFilters
init|=
operator|new
name|Filter
index|[
name|chain
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
name|chain
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Filter
name|f
init|=
name|chain
index|[
name|i
index|]
decl_stmt|;
comment|// create old BitSet-based Filter as wrapper
name|oldFilters
index|[
name|i
index|]
operator|=
operator|new
name|Filter
argument_list|()
block|{
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|it
init|=
name|f
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|next
argument_list|()
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|it
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|bits
return|;
block|}
block|}
expr_stmt|;
block|}
return|return
name|oldFilters
return|;
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
parameter_list|,
name|boolean
name|old
parameter_list|)
block|{
if|if
condition|(
name|old
condition|)
block|{
name|chain
operator|=
name|getChainWithOldFilters
argument_list|(
name|chain
argument_list|)
expr_stmt|;
block|}
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
parameter_list|,
name|boolean
name|old
parameter_list|)
block|{
if|if
condition|(
name|old
condition|)
block|{
name|chain
operator|=
name|getChainWithOldFilters
argument_list|(
name|chain
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|int
name|mode
init|=
literal|0
init|;
name|mode
operator|<
literal|2
condition|;
name|mode
operator|++
control|)
block|{
name|boolean
name|old
init|=
operator|(
name|mode
operator|==
literal|0
operator|)
decl_stmt|;
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
argument_list|,
name|old
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MAX
argument_list|,
name|hits
operator|.
name|length
argument_list|()
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
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX
operator|/
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
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
argument_list|,
name|old
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX
operator|/
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bob"
argument_list|,
name|hits
operator|.
name|doc
argument_list|(
literal|0
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
argument_list|,
name|old
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX
operator|/
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sue"
argument_list|,
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOR
specifier|public
name|void
name|testOR
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|mode
init|=
literal|0
init|;
name|mode
operator|<
literal|2
condition|;
name|mode
operator|++
control|)
block|{
name|boolean
name|old
init|=
operator|(
name|mode
operator|==
literal|0
operator|)
decl_stmt|;
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
argument_list|,
name|old
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OR matches all"
argument_list|,
name|MAX
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAND
specifier|public
name|void
name|testAND
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|mode
init|=
literal|0
init|;
name|mode
operator|<
literal|2
condition|;
name|mode
operator|++
control|)
block|{
name|boolean
name|old
init|=
operator|(
name|mode
operator|==
literal|0
operator|)
decl_stmt|;
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
argument_list|,
name|old
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
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
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bob"
argument_list|,
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testXOR
specifier|public
name|void
name|testXOR
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|mode
init|=
literal|0
init|;
name|mode
operator|<
literal|2
condition|;
name|mode
operator|++
control|)
block|{
name|boolean
name|old
init|=
operator|(
name|mode
operator|==
literal|0
operator|)
decl_stmt|;
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
argument_list|,
name|old
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
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
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sue"
argument_list|,
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testANDNOT
specifier|public
name|void
name|testANDNOT
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|mode
init|=
literal|0
init|;
name|mode
operator|<
literal|2
condition|;
name|mode
operator|++
control|)
block|{
name|boolean
name|old
init|=
operator|(
name|mode
operator|==
literal|0
operator|)
decl_stmt|;
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
argument_list|,
name|old
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|chain
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
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bob"
argument_list|,
name|hits
operator|.
name|doc
argument_list|(
literal|0
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
argument_list|,
name|old
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
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sue"
argument_list|,
name|hits
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseDate
specifier|private
name|Date
name|parseDate
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy MMM dd"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|parse
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|testWithCachingFilter
specifier|public
name|void
name|testWithCachingFilter
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|mode
init|=
literal|0
init|;
name|mode
operator|<
literal|2
condition|;
name|mode
operator|++
control|)
block|{
name|boolean
name|old
init|=
operator|(
name|mode
operator|==
literal|0
operator|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|,
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
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
name|dir
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
block|}
block|}
block|}
end_class
end_unit
