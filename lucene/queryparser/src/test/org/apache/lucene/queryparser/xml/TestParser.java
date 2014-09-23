begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.xml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|analysis
operator|.
name|MockTokenFilter
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
name|MockTokenizer
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
name|document
operator|.
name|IntField
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
name|LeafReaderContext
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
name|DirectoryReader
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
name|StoredDocument
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
name|ScoreDoc
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
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
begin_class
DECL|class|TestParser
specifier|public
class|class
name|TestParser
extends|extends
name|LuceneTestCase
block|{
DECL|field|builder
specifier|private
specifier|static
name|CoreParser
name|builder
decl_stmt|;
DECL|field|dir
specifier|private
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
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
comment|// TODO: rewrite test (this needs to set QueryParser.enablePositionIncrements, too, for work with CURRENT):
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|)
decl_stmt|;
comment|//initialize the parser
name|builder
operator|=
operator|new
name|CorePlusExtensionsParser
argument_list|(
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|BufferedReader
name|d
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|TestParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"reuters21578.txt"
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
argument_list|)
decl_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|d
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|int
name|endOfDate
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'\t'
argument_list|)
decl_stmt|;
name|String
name|date
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|endOfDate
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|line
operator|.
name|substring
argument_list|(
name|endOfDate
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
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
name|newTextField
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"contents"
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"date2"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|date
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|line
operator|=
name|d
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
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
name|reader
operator|=
literal|null
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|builder
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testSimpleXML
specifier|public
name|void
name|testSimpleXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"TermQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleTermsQueryXML
specifier|public
name|void
name|testSimpleTermsQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermsQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"TermsQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanQueryXML
specifier|public
name|void
name|testBooleanQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BooleanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"BooleanQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisjunctionMaxQueryXML
specifier|public
name|void
name|testDisjunctionMaxQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"DisjunctionMaxQuery.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|DisjunctionMaxQuery
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|d
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|q
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.0f
argument_list|,
name|d
operator|.
name|getTieBreakerMultiplier
argument_list|()
argument_list|,
literal|0.0001f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|d
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DisjunctionMaxQuery
name|ndq
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|d
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.2f
argument_list|,
name|ndq
operator|.
name|getTieBreakerMultiplier
argument_list|()
argument_list|,
literal|0.0001f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ndq
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeFilterQueryXML
specifier|public
name|void
name|testRangeFilterQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"RangeFilterQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"RangeFilter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testUserQueryXML
specifier|public
name|void
name|testUserQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"UserInputQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"UserInput with Filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomFieldUserQueryXML
specifier|public
name|void
name|testCustomFieldUserQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"UserInputQueryCustomField.xml"
argument_list|)
decl_stmt|;
name|int
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
literal|"UserInputQueryCustomField should produce 0 result "
argument_list|,
literal|0
argument_list|,
name|h
argument_list|)
expr_stmt|;
block|}
DECL|method|testLikeThisQueryXML
specifier|public
name|void
name|testLikeThisQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"LikeThisQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"like this"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostingQueryXML
specifier|public
name|void
name|testBoostingQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BoostingQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"boosting "
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testFuzzyLikeThisQueryXML
specifier|public
name|void
name|testFuzzyLikeThisQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"FuzzyLikeThisQuery.xml"
argument_list|)
decl_stmt|;
comment|//show rewritten fuzzyLikeThisQuery - see what is being matched on
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|q
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dumpResults
argument_list|(
literal|"FuzzyLikeThis"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermsFilterXML
specifier|public
name|void
name|testTermsFilterXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermsFilterQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Terms Filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostingTermQueryXML
specifier|public
name|void
name|testBoostingTermQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BoostingTermQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"BoostingTermQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanTermXML
specifier|public
name|void
name|testSpanTermXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"SpanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Span Query"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstantScoreQueryXML
specifier|public
name|void
name|testConstantScoreQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"ConstantScoreQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"ConstantScoreQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testMatchAllDocsPlusFilterXML
specifier|public
name|void
name|testMatchAllDocsPlusFilterXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"MatchAllDocsQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"MatchAllDocsQuery with range filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanFilterXML
specifier|public
name|void
name|testBooleanFilterXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BooleanFilter.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Boolean filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedBooleanQuery
specifier|public
name|void
name|testNestedBooleanQuery
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"NestedBooleanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Nested Boolean query"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testCachedFilterXML
specifier|public
name|void
name|testCachedFilterXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"CachedFilter.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Cached filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicateFilterQueryXML
specifier|public
name|void
name|testDuplicateFilterQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"DuplicateFilterQuery.xml"
argument_list|)
decl_stmt|;
name|int
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DuplicateFilterQuery should produce 1 result "
argument_list|,
literal|1
argument_list|,
name|h
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumericRangeFilterQueryXML
specifier|public
name|void
name|testNumericRangeFilterQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"NumericRangeFilterQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"NumericRangeFilter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumericRangeQueryQueryXML
specifier|public
name|void
name|testNumericRangeQueryQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"NumericRangeQueryQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"NumericRangeQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
comment|//================= Helper methods ===================================
DECL|method|parse
specifier|private
name|Query
name|parse
parameter_list|(
name|String
name|xmlFileName
parameter_list|)
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|InputStream
name|xmlStream
init|=
name|TestParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|xmlFileName
argument_list|)
decl_stmt|;
name|Query
name|result
init|=
name|builder
operator|.
name|parse
argument_list|(
name|xmlStream
argument_list|)
decl_stmt|;
name|xmlStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|dumpResults
specifier|private
name|void
name|dumpResults
parameter_list|(
name|String
name|qType
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: query="
operator|+
name|q
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|qType
operator|+
literal|" should produce results "
argument_list|,
name|hits
operator|.
name|totalHits
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"========="
operator|+
name|qType
operator|+
literal|"============"
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|hits
operator|.
name|scoreDocs
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
name|Math
operator|.
name|min
argument_list|(
name|numDocs
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|StoredDocument
name|ldoc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|ldoc
operator|.
name|get
argument_list|(
literal|"date"
argument_list|)
operator|+
literal|"]"
operator|+
name|ldoc
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
