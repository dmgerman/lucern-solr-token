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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Serializable
import|;
end_import
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|standard
operator|.
name|StandardAnalyzer
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
name|DateTools
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
name|IndexWriterConfig
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Unit test for sorting code.  *  */
end_comment
begin_class
DECL|class|TestCustomSearcherSort
specifier|public
class|class
name|TestCustomSearcherSort
extends|extends
name|LuceneTestCase
implements|implements
name|Serializable
block|{
DECL|field|index
specifier|private
name|Directory
name|index
init|=
literal|null
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
init|=
literal|null
decl_stmt|;
comment|// reduced from 20000 to 2000 to speed up test...
DECL|field|INDEX_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|INDEX_SIZE
init|=
literal|2000
decl_stmt|;
DECL|method|TestCustomSearcherSort
specifier|public
name|TestCustomSearcherSort
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
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|suite
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|TestSuite
argument_list|(
name|TestCustomSearcherSort
operator|.
name|class
argument_list|)
return|;
block|}
comment|// create an index for testing
DECL|method|getIndex
specifier|private
name|Directory
name|getIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
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
name|indexStore
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RandomGen
name|random
init|=
operator|new
name|RandomGen
argument_list|(
name|newRandom
argument_list|()
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
name|INDEX_SIZE
condition|;
operator|++
name|i
control|)
block|{
comment|// don't decrease; if to low the problem doesn't show up
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|5
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// some documents must not have an entry in the first sort field
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"publicationDate_"
argument_list|,
name|random
operator|.
name|getLuceneDate
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
block|}
if|if
condition|(
operator|(
name|i
operator|%
literal|7
operator|)
operator|==
literal|0
condition|)
block|{
comment|// some documents to match the query (see below)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"test"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// every document has a defined 'mandant' field
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"mandant"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|3
argument_list|)
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
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|indexStore
return|;
block|}
comment|/**    * Create index and query for test cases.     */
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
name|index
operator|=
name|getIndex
argument_list|()
expr_stmt|;
name|query
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run the test using two CustomSearcher instances.     */
DECL|method|testFieldSortCustomSearcher
specifier|public
name|void
name|testFieldSortCustomSearcher
parameter_list|()
throws|throws
name|Exception
block|{
comment|// log("Run testFieldSortCustomSearcher");
comment|// define the sort criteria
name|Sort
name|custSort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"publicationDate_"
argument_list|,
name|SortField
operator|.
name|STRING
argument_list|)
argument_list|,
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|CustomSearcher
argument_list|(
name|index
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// search and check hits
name|matchHits
argument_list|(
name|searcher
argument_list|,
name|custSort
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run the test using one CustomSearcher wrapped by a MultiSearcher.     */
DECL|method|testFieldSortSingleSearcher
specifier|public
name|void
name|testFieldSortSingleSearcher
parameter_list|()
throws|throws
name|Exception
block|{
comment|// log("Run testFieldSortSingleSearcher");
comment|// define the sort criteria
name|Sort
name|custSort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"publicationDate_"
argument_list|,
name|SortField
operator|.
name|STRING
argument_list|)
argument_list|,
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
operator|new
name|Searcher
index|[]
block|{
operator|new
name|CustomSearcher
argument_list|(
name|index
argument_list|,
literal|2
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|// search and check hits
name|matchHits
argument_list|(
name|searcher
argument_list|,
name|custSort
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run the test using two CustomSearcher instances.     */
DECL|method|testFieldSortMultiCustomSearcher
specifier|public
name|void
name|testFieldSortMultiCustomSearcher
parameter_list|()
throws|throws
name|Exception
block|{
comment|// log("Run testFieldSortMultiCustomSearcher");
comment|// define the sort criteria
name|Sort
name|custSort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"publicationDate_"
argument_list|,
name|SortField
operator|.
name|STRING
argument_list|)
argument_list|,
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
decl_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|MultiSearcher
argument_list|(
operator|new
name|Searchable
index|[]
block|{
operator|new
name|CustomSearcher
argument_list|(
name|index
argument_list|,
literal|0
argument_list|)
block|,
operator|new
name|CustomSearcher
argument_list|(
name|index
argument_list|,
literal|2
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|// search and check hits
name|matchHits
argument_list|(
name|searcher
argument_list|,
name|custSort
argument_list|)
expr_stmt|;
block|}
comment|// make sure the documents returned by the search match the expected list
DECL|method|matchHits
specifier|private
name|void
name|matchHits
parameter_list|(
name|Searcher
name|searcher
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
comment|// make a query without sorting first
name|ScoreDoc
index|[]
name|hitsByRank
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|checkHits
argument_list|(
name|hitsByRank
argument_list|,
literal|"Sort by rank: "
argument_list|)
expr_stmt|;
comment|// check for duplicates
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|resultMap
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
comment|// store hits in TreeMap - TreeMap does not allow duplicates; existing entries are silently overwritten
for|for
control|(
name|int
name|hitid
init|=
literal|0
init|;
name|hitid
operator|<
name|hitsByRank
operator|.
name|length
condition|;
operator|++
name|hitid
control|)
block|{
name|resultMap
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|hitsByRank
index|[
name|hitid
index|]
operator|.
name|doc
argument_list|)
argument_list|,
comment|// Key:   Lucene Document ID
name|Integer
operator|.
name|valueOf
argument_list|(
name|hitid
argument_list|)
argument_list|)
expr_stmt|;
comment|// Value: Hits-Objekt Index
block|}
comment|// now make a query using the sort criteria
name|ScoreDoc
index|[]
name|resultSort
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|checkHits
argument_list|(
name|resultSort
argument_list|,
literal|"Sort by custom criteria: "
argument_list|)
expr_stmt|;
comment|// check for duplicates
comment|// besides the sorting both sets of hits must be identical
for|for
control|(
name|int
name|hitid
init|=
literal|0
init|;
name|hitid
operator|<
name|resultSort
operator|.
name|length
condition|;
operator|++
name|hitid
control|)
block|{
name|Integer
name|idHitDate
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|resultSort
index|[
name|hitid
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
comment|// document ID from sorted search
if|if
condition|(
operator|!
name|resultMap
operator|.
name|containsKey
argument_list|(
name|idHitDate
argument_list|)
condition|)
block|{
name|log
argument_list|(
literal|"ID "
operator|+
name|idHitDate
operator|+
literal|" not found. Possibliy a duplicate."
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|resultMap
operator|.
name|containsKey
argument_list|(
name|idHitDate
argument_list|)
argument_list|)
expr_stmt|;
comment|// same ID must be in the Map from the rank-sorted search
comment|// every hit must appear once in both result sets --> remove it from the Map.
comment|// At the end the Map must be empty!
name|resultMap
operator|.
name|remove
argument_list|(
name|idHitDate
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resultMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// log("All hits matched");
block|}
else|else
block|{
name|log
argument_list|(
literal|"Couldn't match "
operator|+
name|resultMap
operator|.
name|size
argument_list|()
operator|+
literal|" hits."
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|resultMap
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check the hits for duplicates.    * @param hits    */
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|ScoreDoc
index|[]
name|hits
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|hits
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|idMap
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docnum
init|=
literal|0
init|;
name|docnum
operator|<
name|hits
operator|.
name|length
condition|;
operator|++
name|docnum
control|)
block|{
name|Integer
name|luceneId
init|=
literal|null
decl_stmt|;
name|luceneId
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|hits
index|[
name|docnum
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|idMap
operator|.
name|containsKey
argument_list|(
name|luceneId
argument_list|)
condition|)
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"Duplicate key for hit index = "
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|docnum
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|", previous index = "
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
operator|(
name|idMap
operator|.
name|get
argument_list|(
name|luceneId
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|", Lucene ID = "
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|luceneId
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|idMap
operator|.
name|put
argument_list|(
name|luceneId
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|docnum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Simply write to console - choosen to be independant of log4j etc
DECL|method|log
specifier|private
name|void
name|log
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|class|CustomSearcher
specifier|public
class|class
name|CustomSearcher
extends|extends
name|IndexSearcher
block|{
DECL|field|switcher
specifier|private
name|int
name|switcher
decl_stmt|;
comment|/**          * @param directory          * @throws IOException          */
DECL|method|CustomSearcher
specifier|public
name|CustomSearcher
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|int
name|switcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|switcher
operator|=
name|switcher
expr_stmt|;
block|}
comment|/**          * @param r          */
DECL|method|CustomSearcher
specifier|public
name|CustomSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|int
name|switcher
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|this
operator|.
name|switcher
operator|=
name|switcher
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see org.apache.lucene.search.Searchable#search(org.apache.lucene.search.Query, org.apache.lucene.search.Filter, int, org.apache.lucene.search.Sort)          */
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
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
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
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
literal|"mandant"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|switcher
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|search
argument_list|(
name|bq
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|sort
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)          * @see org.apache.lucene.search.Searchable#search(org.apache.lucene.search.Query, org.apache.lucene.search.Filter, int)          */
annotation|@
name|Override
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
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
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
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
literal|"mandant"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|switcher
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|search
argument_list|(
name|bq
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|)
return|;
block|}
block|}
DECL|class|RandomGen
specifier|private
class|class
name|RandomGen
block|{
DECL|method|RandomGen
name|RandomGen
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|field|base
specifier|private
name|Calendar
name|base
init|=
operator|new
name|GregorianCalendar
argument_list|(
literal|1980
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// Just to generate some different Lucene Date strings
DECL|method|getLuceneDate
specifier|private
name|String
name|getLuceneDate
parameter_list|()
block|{
return|return
name|DateTools
operator|.
name|timeToString
argument_list|(
name|base
operator|.
name|getTimeInMillis
argument_list|()
operator|+
name|random
operator|.
name|nextInt
argument_list|()
operator|-
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|DAY
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
