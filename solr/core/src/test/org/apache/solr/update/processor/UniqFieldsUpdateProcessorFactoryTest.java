begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
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
name|params
operator|.
name|MultiMapSolrParams
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
name|params
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
name|common
operator|.
name|params
operator|.
name|UpdateParams
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
name|util
operator|.
name|ContentStream
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
name|util
operator|.
name|ContentStreamBase
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
name|handler
operator|.
name|UpdateRequestHandler
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
name|request
operator|.
name|SolrQueryRequestBase
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
begin_comment
comment|/**  *   */
end_comment
begin_class
DECL|class|UniqFieldsUpdateProcessorFactoryTest
specifier|public
class|class
name|UniqFieldsUpdateProcessorFactoryTest
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
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
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUniqFields
specifier|public
name|void
name|testUniqFields
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
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"uniq-fields"
argument_list|)
decl_stmt|;
name|UniqFieldsUpdateProcessorFactory
name|factory
init|=
operator|(
operator|(
name|UniqFieldsUpdateProcessorFactory
operator|)
name|chained
operator|.
name|getProcessors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|chained
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1a"
argument_list|,
literal|"uniq"
argument_list|,
literal|"value1"
argument_list|,
literal|"uniq"
argument_list|,
literal|"value1"
argument_list|,
literal|"uniq"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2a"
argument_list|,
literal|"uniq2"
argument_list|,
literal|"value1"
argument_list|,
literal|"uniq2"
argument_list|,
literal|"value2"
argument_list|,
literal|"uniq2"
argument_list|,
literal|"value1"
argument_list|,
literal|"uniq2"
argument_list|,
literal|"value3"
argument_list|,
literal|"uniq"
argument_list|,
literal|"value1"
argument_list|,
literal|"uniq"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1b"
argument_list|,
literal|"uniq3"
argument_list|,
literal|"value1"
argument_list|,
literal|"uniq3"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1c"
argument_list|,
literal|"nouniq"
argument_list|,
literal|"value1"
argument_list|,
literal|"nouniq"
argument_list|,
literal|"value1"
argument_list|,
literal|"nouniq"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2c"
argument_list|,
literal|"nouniq"
argument_list|,
literal|"value1"
argument_list|,
literal|"nouniq"
argument_list|,
literal|"value1"
argument_list|,
literal|"nouniq"
argument_list|,
literal|"value2"
argument_list|,
literal|"uniq2"
argument_list|,
literal|"value1"
argument_list|,
literal|"uniq2"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1a"
argument_list|)
argument_list|,
literal|"count(//*[@name='uniq']/*)=2"
argument_list|,
literal|"//arr[@name='uniq']/str[1][.='value1']"
argument_list|,
literal|"//arr[@name='uniq']/str[2][.='value2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2a"
argument_list|)
argument_list|,
literal|"count(//*[@name='uniq2']/*)=3"
argument_list|,
literal|"//arr[@name='uniq2']/str[1][.='value1']"
argument_list|,
literal|"//arr[@name='uniq2']/str[2][.='value2']"
argument_list|,
literal|"//arr[@name='uniq2']/str[3][.='value3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2a"
argument_list|)
argument_list|,
literal|"count(//*[@name='uniq']/*)=1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1b"
argument_list|)
argument_list|,
literal|"count(//*[@name='uniq3'])=1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1c"
argument_list|)
argument_list|,
literal|"count(//*[@name='nouniq']/*)=3"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2c"
argument_list|)
argument_list|,
literal|"count(//*[@name='nouniq']/*)=3"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2c"
argument_list|)
argument_list|,
literal|"count(//*[@name='uniq2']/*)=1"
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|String
name|doc
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|MultiMapSolrParams
name|mmparams
init|=
operator|new
name|MultiMapSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"uniq-fields"
block|}
argument_list|)
expr_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
operator|(
name|SolrParams
operator|)
name|mmparams
argument_list|)
block|{     }
decl_stmt|;
name|UpdateRequestHandler
name|handler
init|=
operator|new
name|UpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
