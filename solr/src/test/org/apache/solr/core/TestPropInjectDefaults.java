begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|index
operator|.
name|ConcurrentMergeScheduler
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
name|LogByteSizeMergePolicy
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
name|update
operator|.
name|DirectUpdateHandler2
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestPropInjectDefaults
specifier|public
class|class
name|TestPropInjectDefaults
extends|extends
name|SolrTestCaseJ4
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
literal|"solrconfig-propinject-indexdefault.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|class|ExposeWriterHandler
class|class
name|ExposeWriterHandler
extends|extends
name|DirectUpdateHandler2
block|{
DECL|method|ExposeWriterHandler
specifier|public
name|ExposeWriterHandler
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getWriter
specifier|public
name|IndexWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|forceOpenWriter
argument_list|()
expr_stmt|;
return|return
name|writer
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMergePolicyDefaults
specifier|public
name|void
name|testMergePolicyDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|ExposeWriterHandler
name|uh
init|=
operator|new
name|ExposeWriterHandler
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
name|uh
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|LogByteSizeMergePolicy
name|mp
init|=
operator|(
name|LogByteSizeMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|32.0
argument_list|,
name|mp
operator|.
name|getMaxMergeMB
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|uh
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPropsDefaults
specifier|public
name|void
name|testPropsDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|ExposeWriterHandler
name|uh
init|=
operator|new
name|ExposeWriterHandler
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
name|uh
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|ConcurrentMergeScheduler
name|cms
init|=
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|cms
operator|.
name|getMaxThreadCount
argument_list|()
argument_list|)
expr_stmt|;
name|uh
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
