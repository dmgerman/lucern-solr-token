begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|embedded
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|MultiCoreExampleTestBase
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|CoreContainer
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
begin_comment
comment|/**  * This runs SolrServer test using   *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|MultiCoreEmbeddedTest
specifier|public
class|class
name|MultiCoreEmbeddedTest
extends|extends
name|MultiCoreExampleTestBase
block|{
DECL|method|setUp
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: fix this test to use MockDirectoryFactory
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|setupCoreContainer
argument_list|()
expr_stmt|;
name|SolrCore
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"CORES="
operator|+
name|cores
operator|+
literal|" : "
operator|+
name|cores
operator|.
name|getCoreNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setupCoreContainer
specifier|protected
name|void
name|setupCoreContainer
parameter_list|()
block|{
name|cores
operator|=
operator|new
name|CoreContainer
argument_list|()
expr_stmt|;
name|cores
operator|.
name|load
argument_list|()
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
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSolrCore0
specifier|protected
name|SolrServer
name|getSolrCore0
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core0"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrCore1
specifier|protected
name|SolrServer
name|getSolrCore1
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core1"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrCore
specifier|protected
name|SolrServer
name|getSolrCore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrAdmin
specifier|protected
name|SolrServer
name|getSolrAdmin
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core0"
argument_list|)
return|;
block|}
block|}
end_class
end_unit
