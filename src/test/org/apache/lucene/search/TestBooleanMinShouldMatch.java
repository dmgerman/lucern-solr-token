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
comment|/**  * Copyright 2005 Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|queryParser
operator|.
name|ParseException
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
name|text
operator|.
name|DecimalFormat
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
begin_comment
comment|/** Test that BooleanQuery.setMinimumNumberShouldMatch works.  */
end_comment
begin_class
DECL|class|TestBooleanMinShouldMatch
specifier|public
class|class
name|TestBooleanMinShouldMatch
extends|extends
name|TestCase
block|{
DECL|field|index
specifier|public
name|Directory
name|index
decl_stmt|;
DECL|field|r
specifier|public
name|IndexReader
name|r
decl_stmt|;
DECL|field|s
specifier|public
name|IndexSearcher
name|s
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|data
init|=
operator|new
name|String
index|[]
block|{
literal|"A 1 2 3 4 5 6"
block|,
literal|"Z       4 5 6"
block|,
literal|null
block|,
literal|"B   2   4 5 6"
block|,
literal|"Y     3   5 6"
block|,
literal|null
block|,
literal|"C     3     6"
block|,
literal|"X       4 5 6"
block|}
decl_stmt|;
name|index
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
name|index
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
name|data
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
name|Field
operator|.
name|Keyword
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Keyword
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|data
index|[
name|i
index|]
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"data"
argument_list|,
name|data
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
expr_stmt|;
comment|//System.out.println("Set up " + getName());
block|}
DECL|method|verifyNrHits
specifier|public
name|void
name|verifyNrHits
parameter_list|(
name|Query
name|q
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Hits
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|!=
name|h
operator|.
name|length
argument_list|()
condition|)
block|{
name|printHits
argument_list|(
name|getName
argument_list|()
argument_list|,
name|h
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"result count"
argument_list|,
name|expected
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllOptional
specifier|public
name|void
name|testAllOptional
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// match at least two of 4
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneReqAndSomeOptional
specifier|public
name|void
name|testOneReqAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* one required, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// 2 of 3 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testSomeReqAndSomeOptional
specifier|public
name|void
name|testSomeReqAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two required, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// 2 of 3 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneProhibAndSomeOptional
specifier|public
name|void
name|testOneProhibAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* one prohibited, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// 2 of 3 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testSomeProhibAndSomeOptional
specifier|public
name|void
name|testSomeProhibAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two prohibited, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"C"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// 2 of 3 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneReqOneProhibAndSomeOptional
specifier|public
name|void
name|testOneReqOneProhibAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* one required, one prohibited, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// 3 of 4 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testSomeReqOneProhibAndSomeOptional
specifier|public
name|void
name|testSomeReqOneProhibAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two required, one prohibited, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// 3 of 4 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneReqSomeProhibAndSomeOptional
specifier|public
name|void
name|testOneReqSomeProhibAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* one required, two prohibited, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"C"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// 3 of 4 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testSomeReqSomeProhibAndSomeOptional
specifier|public
name|void
name|testSomeReqSomeProhibAndSomeOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two required, two prohibited, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"C"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// 3 of 4 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinHigherThenNumOptional
specifier|public
name|void
name|testMinHigherThenNumOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two required, two prohibited, some optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"C"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|90
argument_list|)
expr_stmt|;
comment|// 90 of 4 optional ?!?!?!
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinEqualToNumOptional
specifier|public
name|void
name|testMinEqualToNumOptional
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two required, two optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// 2 of 2 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneOptionalEqualToMin
specifier|public
name|void
name|testOneOptionalEqualToMin
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two required, one optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// 1 of 1 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoOptionalButMin
specifier|public
name|void
name|testNoOptionalButMin
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* two required, no optional */
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"all"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// 1 of 0 optional
name|verifyNrHits
argument_list|(
name|q
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomQueries
specifier|public
name|void
name|testRandomQueries
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|field
init|=
literal|"data"
decl_stmt|;
name|String
index|[]
name|vals
init|=
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|,
literal|"5"
block|,
literal|"6"
block|,
literal|"A"
block|,
literal|"Z"
block|,
literal|"B"
block|,
literal|"Y"
block|,
literal|"Z"
block|,
literal|"X"
block|,
literal|"foo"
block|}
decl_stmt|;
name|int
name|maxLev
init|=
literal|4
decl_stmt|;
comment|// callback object to set a random setMinimumNumberShouldMatch
name|TestBoolean2
operator|.
name|Callback
name|minNrCB
init|=
operator|new
name|TestBoolean2
operator|.
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|postCreate
parameter_list|(
name|BooleanQuery
name|q
parameter_list|)
block|{
name|BooleanClause
index|[]
name|c
init|=
name|q
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|int
name|opt
init|=
literal|0
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
name|c
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|c
index|[
name|i
index|]
operator|.
name|getOccur
argument_list|()
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
condition|)
name|opt
operator|++
expr_stmt|;
block|}
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|opt
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|int
name|tot
init|=
literal|0
decl_stmt|;
comment|// increase number of iterations for more complete testing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|lev
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxLev
argument_list|)
decl_stmt|;
name|BooleanQuery
name|q1
init|=
name|TestBoolean2
operator|.
name|randBoolQuery
argument_list|(
operator|new
name|Random
argument_list|(
name|i
argument_list|)
argument_list|,
name|lev
argument_list|,
name|field
argument_list|,
name|vals
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// BooleanQuery q2 = TestBoolean2.randBoolQuery(new Random(i), lev, field, vals, minNrCB);
name|BooleanQuery
name|q2
init|=
name|TestBoolean2
operator|.
name|randBoolQuery
argument_list|(
operator|new
name|Random
argument_list|(
name|i
argument_list|)
argument_list|,
name|lev
argument_list|,
name|field
argument_list|,
name|vals
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// only set minimumNumberShouldMatch on the top level query since setting
comment|// at a lower level can change the score.
name|minNrCB
operator|.
name|postCreate
argument_list|(
name|q2
argument_list|)
expr_stmt|;
comment|// Can't use Hits because normalized scores will mess things
comment|// up.  The non-sorting version of search() that returns TopDocs
comment|// will not normalize scores.
name|TopDocs
name|top1
init|=
name|s
operator|.
name|search
argument_list|(
name|q1
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|TopDocs
name|top2
init|=
name|s
operator|.
name|search
argument_list|(
name|q2
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|tot
operator|+=
name|top2
operator|.
name|totalHits
expr_stmt|;
comment|// The constrained query
comment|// should be a superset to the unconstrained query.
if|if
condition|(
name|top2
operator|.
name|totalHits
operator|>
name|top1
operator|.
name|totalHits
condition|)
block|{
name|TestCase
operator|.
name|fail
argument_list|(
literal|"Constrained results not a subset:\n"
operator|+
name|CheckHits
operator|.
name|topdocsString
argument_list|(
name|top1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|+
name|CheckHits
operator|.
name|topdocsString
argument_list|(
name|top2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|+
literal|"for query:"
operator|+
name|q2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|hit
init|=
literal|0
init|;
name|hit
operator|<
name|top2
operator|.
name|totalHits
condition|;
name|hit
operator|++
control|)
block|{
name|int
name|id
init|=
name|top2
operator|.
name|scoreDocs
index|[
name|hit
index|]
operator|.
name|doc
decl_stmt|;
name|float
name|score
init|=
name|top2
operator|.
name|scoreDocs
index|[
name|hit
index|]
operator|.
name|score
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
comment|// find this doc in other hits
for|for
control|(
name|int
name|other
init|=
literal|0
init|;
name|other
operator|<
name|top1
operator|.
name|totalHits
condition|;
name|other
operator|++
control|)
block|{
if|if
condition|(
name|top1
operator|.
name|scoreDocs
index|[
name|other
index|]
operator|.
name|doc
operator|==
name|id
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|float
name|otherScore
init|=
name|top1
operator|.
name|scoreDocs
index|[
name|other
index|]
operator|.
name|score
decl_stmt|;
comment|// check if scores match
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|otherScore
operator|-
name|score
argument_list|)
operator|>
literal|1.0e-6f
condition|)
block|{
name|TestCase
operator|.
name|fail
argument_list|(
literal|"Doc "
operator|+
name|id
operator|+
literal|" scores don't match\n"
operator|+
name|CheckHits
operator|.
name|topdocsString
argument_list|(
name|top1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|+
name|CheckHits
operator|.
name|topdocsString
argument_list|(
name|top2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|+
literal|"for query:"
operator|+
name|q2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// check if subset
if|if
condition|(
operator|!
name|found
condition|)
name|TestCase
operator|.
name|fail
argument_list|(
literal|"Doc "
operator|+
name|id
operator|+
literal|" not found\n"
operator|+
name|CheckHits
operator|.
name|topdocsString
argument_list|(
name|top1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|+
name|CheckHits
operator|.
name|topdocsString
argument_list|(
name|top2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|+
literal|"for query:"
operator|+
name|q2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// System.out.println("Total hits:"+tot);
block|}
DECL|method|printHits
specifier|protected
name|void
name|printHits
parameter_list|(
name|String
name|test
parameter_list|,
name|Hits
name|h
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"------- "
operator|+
name|test
operator|+
literal|" -------"
argument_list|)
expr_stmt|;
name|DecimalFormat
name|f
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"0.000000"
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
name|h
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|float
name|score
init|=
name|h
operator|.
name|score
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"#"
operator|+
name|i
operator|+
literal|": "
operator|+
name|f
operator|.
name|format
argument_list|(
name|score
argument_list|)
operator|+
literal|" - "
operator|+
name|d
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" - "
operator|+
name|d
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
