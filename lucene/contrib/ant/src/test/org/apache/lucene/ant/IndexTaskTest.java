begin_unit
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
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
name|core
operator|.
name|StopAnalyzer
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
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|FileSet
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
comment|/**  *  Test cases for index task  *  */
end_comment
begin_class
DECL|class|IndexTaskTest
specifier|public
class|class
name|IndexTaskTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|docHandler
specifier|private
specifier|final
specifier|static
name|String
name|docHandler
init|=
literal|"org.apache.lucene.ant.FileExtensionDocumentHandler"
decl_stmt|;
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|dir
specifier|private
name|FSDirectory
name|dir
decl_stmt|;
comment|/**      *  The JUnit setup method      *      *@exception  IOException  Description of Exception      */
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
comment|// slightly hackish way to get the src/test dir
name|String
name|docsDir
init|=
name|getDataFile
argument_list|(
literal|"test.txt"
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|File
name|indexDir
init|=
name|TEMP_DIR
decl_stmt|;
name|Project
name|project
init|=
operator|new
name|Project
argument_list|()
decl_stmt|;
name|IndexTask
name|task
init|=
operator|new
name|IndexTask
argument_list|()
decl_stmt|;
name|FileSet
name|fs
init|=
operator|new
name|FileSet
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setDir
argument_list|(
operator|new
name|File
argument_list|(
name|docsDir
argument_list|)
argument_list|)
expr_stmt|;
name|task
operator|.
name|addFileset
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|task
operator|.
name|setOverwrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|task
operator|.
name|setDocumentHandler
argument_list|(
name|docHandler
argument_list|)
expr_stmt|;
name|task
operator|.
name|setIndex
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|task
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|task
operator|.
name|execute
argument_list|()
expr_stmt|;
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|StopAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
operator|.
name|parse
argument_list|(
literal|"test"
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Find document(s)"
argument_list|,
literal|2
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
comment|/**      *  The teardown method for JUnit      * TODO: remove indexDir?      */
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
name|dir
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
block|}
end_class
end_unit
