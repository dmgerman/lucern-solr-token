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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|queryParser
operator|.
name|ParseException
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
name|queryParser
operator|.
name|QueryParser
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Tests {@link MultiSearcher} ranking, i.e. makes sure this bug is fixed:  * http://issues.apache.org/bugzilla/show_bug.cgi?id=31841  *  * @version $Id: TestMultiSearcher.java 150492 2004-09-06 22:01:49Z dnaber $  */
end_comment
begin_class
DECL|class|TestMultiSearcherRanking
specifier|public
class|class
name|TestMultiSearcherRanking
extends|extends
name|TestCase
block|{
DECL|field|verbose
specifier|private
specifier|final
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
comment|// set to true to output hits
DECL|field|FIELD_NAME
specifier|private
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"body"
decl_stmt|;
DECL|field|multiSearcher
specifier|private
name|Searcher
name|multiSearcher
decl_stmt|;
DECL|field|singleSearcher
specifier|private
name|Searcher
name|singleSearcher
decl_stmt|;
DECL|method|testOneTermQuery
specifier|public
name|void
name|testOneTermQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|checkQuery
argument_list|(
literal|"three"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoTermQuery
specifier|public
name|void
name|testTwoTermQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|checkQuery
argument_list|(
literal|"three foo"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrefixQuery
specifier|public
name|void
name|testPrefixQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|checkQuery
argument_list|(
literal|"multi*"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFuzzyQuery
specifier|public
name|void
name|testFuzzyQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|checkQuery
argument_list|(
literal|"multiThree~"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeQuery
specifier|public
name|void
name|testRangeQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|checkQuery
argument_list|(
literal|"{multiA TO multiP}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiPhraseQuery
specifier|public
name|void
name|testMultiPhraseQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|checkQuery
argument_list|(
literal|"\"blueberry pi*\""
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoMatchQuery
specifier|public
name|void
name|testNoMatchQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|checkQuery
argument_list|(
literal|"+three +nomatch"
argument_list|)
expr_stmt|;
block|}
comment|/*   public void testTermRepeatedQuery() throws IOException, ParseException {     // TODO: this corner case yields different results.     checkQuery("multi* multi* foo");   }   */
comment|/**    * checks if a query yields the same result when executed on    * a single IndexSearcher containing all documents and on a    * MultiSearcher aggregating sub-searchers    * @param queryStr  the query to check.    * @throws IOException    * @throws ParseException    */
DECL|method|checkQuery
specifier|private
name|void
name|checkQuery
parameter_list|(
name|String
name|queryStr
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
comment|// check result hit ranking
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|queryStr
argument_list|)
expr_stmt|;
name|QueryParser
name|queryParser
init|=
operator|new
name|QueryParser
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|queryParser
operator|.
name|parse
argument_list|(
name|queryStr
argument_list|)
decl_stmt|;
name|Hits
name|multiSearcherHits
init|=
name|multiSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Hits
name|singleSearcherHits
init|=
name|singleSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|multiSearcherHits
operator|.
name|length
argument_list|()
argument_list|,
name|singleSearcherHits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|multiSearcherHits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|docMulti
init|=
name|multiSearcherHits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Document
name|docSingle
init|=
name|singleSearcherHits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Multi:  "
operator|+
name|docMulti
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
operator|+
literal|" score="
operator|+
name|multiSearcherHits
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Single: "
operator|+
name|docSingle
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
operator|+
literal|" score="
operator|+
name|singleSearcherHits
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|multiSearcherHits
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|,
name|singleSearcherHits
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.001f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docMulti
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|,
name|docSingle
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
comment|/**    * initializes multiSearcher and singleSearcher with the same document set    */
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create MultiSearcher from two seperate searchers
name|Directory
name|d1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw1
init|=
operator|new
name|IndexWriter
argument_list|(
name|d1
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|addCollection1
argument_list|(
name|iw1
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|d2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw2
init|=
operator|new
name|IndexWriter
argument_list|(
name|d2
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|addCollection2
argument_list|(
name|iw2
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|close
argument_list|()
expr_stmt|;
name|Searchable
index|[]
name|s
init|=
operator|new
name|Searchable
index|[
literal|2
index|]
decl_stmt|;
name|s
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|s
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|multiSearcher
operator|=
operator|new
name|MultiSearcher
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|// create IndexSearcher which contains all documents
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|addCollection1
argument_list|(
name|iw
argument_list|)
expr_stmt|;
name|addCollection2
argument_list|(
name|iw
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|singleSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|addCollection1
specifier|private
name|void
name|addCollection1
parameter_list|(
name|IndexWriter
name|iw
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
literal|"one blah three"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"one foo three multiOne"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"one foobar three multiThree"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry pie"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry strudel"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry pizza"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
block|}
DECL|method|addCollection2
specifier|private
name|void
name|addCollection2
parameter_list|(
name|IndexWriter
name|iw
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
literal|"two blah three"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"two foo xxx multiTwo"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"two foobar xxx multiThreee"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry chewing gum"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"bluebird pizza"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"bluebird foobar pizza"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"piccadilly circus"
argument_list|,
name|iw
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|String
name|value
parameter_list|,
name|IndexWriter
name|iw
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_NAME
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
