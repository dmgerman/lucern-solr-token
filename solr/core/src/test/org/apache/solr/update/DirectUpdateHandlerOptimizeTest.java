begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileFilter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
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
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|DirectUpdateHandlerOptimizeTest
specifier|public
class|class
name|DirectUpdateHandlerOptimizeTest
extends|extends
name|AbstractSolrTestCase
block|{
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOptimize
specifier|public
name|void
name|testOptimize
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
comment|//add just under the merge factor, so no segments are merged
comment|//the merge factor is 100 and the maxBufferedDocs is 2, so there should be 50 segments
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|99
condition|;
name|i
operator|++
control|)
block|{
comment|// Add a valid document
name|cmd
operator|.
name|solrDoc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"id_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|,
literal|"subject_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
name|CommitUpdateCommand
name|cmtCmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
comment|// commit twice to give systems such as windows a chance to delete the old files
name|String
name|indexDir
init|=
name|core
operator|.
name|getIndexDir
argument_list|()
decl_stmt|;
name|assertNumSegments
argument_list|(
name|indexDir
argument_list|,
literal|50
argument_list|)
expr_stmt|;
comment|//now do an optimize
name|cmtCmd
operator|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cmtCmd
operator|.
name|maxOptimizeSegments
operator|=
literal|25
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|assertNumSegments
argument_list|(
name|indexDir
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|cmtCmd
operator|.
name|maxOptimizeSegments
operator|=
operator|-
literal|1
expr_stmt|;
try|try
block|{
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{     }
name|cmtCmd
operator|.
name|maxOptimizeSegments
operator|=
literal|1
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|assertNumSegments
argument_list|(
name|indexDir
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertNumSegments
specifier|private
name|void
name|assertNumSegments
parameter_list|(
name|String
name|indexDir
parameter_list|,
name|int
name|numSegs
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|File
index|[]
name|segs
init|=
name|file
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"cfs"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|//
comment|// TODO: we need a method that does not rely on physical inspection of the directory.
comment|//
comment|// assertTrue("Wrong number of segments: " + segs.length + " does not equal: " + numSegs, segs.length == numSegs);
block|}
block|}
end_class
end_unit
