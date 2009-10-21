begin_unit
begin_package
DECL|package|org.apache.lucene.wordnet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|wordnet
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
name|File
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestWordnet
specifier|public
class|class
name|TestWordnet
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|dataDir
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|testFile
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"org/apache/lucene/wordnet/testSynonyms.txt"
argument_list|)
decl_stmt|;
DECL|field|storePathName
name|String
name|storePathName
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"testLuceneWordnet"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
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
comment|// create a temporary synonym index
name|String
name|commandLineArgs
index|[]
init|=
block|{
name|testFile
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|storePathName
block|}
decl_stmt|;
try|try
block|{
name|Syns2Index
operator|.
name|main
argument_list|(
name|commandLineArgs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|storePathName
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testExpansion
specifier|public
name|void
name|testExpansion
parameter_list|()
throws|throws
name|IOException
block|{
name|assertExpandsTo
argument_list|(
literal|"woods"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"woods"
block|,
literal|"forest"
block|,
literal|"wood"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testExpansionSingleQuote
specifier|public
name|void
name|testExpansionSingleQuote
parameter_list|()
throws|throws
name|IOException
block|{
name|assertExpandsTo
argument_list|(
literal|"king"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"king"
block|,
literal|"baron"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExpandsTo
specifier|private
name|void
name|assertExpandsTo
parameter_list|(
name|String
name|term
parameter_list|,
name|String
name|expected
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|expandedQuery
init|=
name|SynExpand
operator|.
name|expand
argument_list|(
name|term
argument_list|,
name|searcher
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|"field"
argument_list|,
literal|1F
argument_list|)
decl_stmt|;
name|BooleanQuery
name|expectedQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|t
range|:
name|expected
control|)
name|expectedQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|t
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
name|assertEquals
argument_list|(
name|expectedQuery
argument_list|,
name|expandedQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
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
name|rmDir
argument_list|(
name|storePathName
argument_list|)
expr_stmt|;
comment|// delete our temporary synonym index
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|rmDir
specifier|private
name|void
name|rmDir
parameter_list|(
name|String
name|directory
parameter_list|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
