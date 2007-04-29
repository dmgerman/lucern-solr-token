begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|LocalSolrQueryRequest
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
name|MapSolrParams
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
name|SolrParams
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
begin_comment
comment|/**  * Most of the tests for StandardRequestHandler are in ConvertedLegacyTest  *   */
end_comment
begin_class
DECL|class|StandardRequestHandlerTest
specifier|public
class|class
name|StandardRequestHandlerTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
DECL|method|testSorting
specifier|public
name|void
name|testSorting
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"title"
argument_list|,
literal|"test"
argument_list|,
literal|"val_s"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"title"
argument_list|,
literal|"test"
argument_list|,
literal|"val_s"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"title"
argument_list|,
literal|"test"
argument_list|,
literal|"val_s"
argument_list|,
literal|"ccc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|Q
argument_list|,
literal|"title:test"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Make sure they got in"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|SORT
argument_list|,
literal|"val_s asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"with sort param [asc]"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='11']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='12']"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|SORT
argument_list|,
literal|"val_s desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"with sort param [desc]"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='12']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='11']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
comment|// Using legacy ';' param
name|args
operator|.
name|remove
argument_list|(
name|SolrParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|Q
argument_list|,
literal|"title:test; val_s desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"with sort param [desc]"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='12']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='11']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|SolrParams
operator|.
name|Q
argument_list|,
literal|"title:test; val_s asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"with sort param [desc]"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='11']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='12']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
