begin_unit
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|RestTestBase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
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
name|restlet
operator|.
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
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
begin_comment
comment|/**  * Base class for Solr Restlet-based tests. Creates jetty and test harness  * with solrconfig.xml and schema-rest.xml, including "extra" servlets for  * all Solr Restlet Application subclasses.  *  * Use RestTestBase instead if you need to specialize the solrconfig,  * the schema, or jetty/test harness creation; otherwise you'll get  * imbalanced SolrIndexSearcher closes/opens and a suite-level failure  * for a zombie thread.  */
end_comment
begin_class
DECL|class|SolrRestletTestBase
specifier|abstract
specifier|public
class|class
name|SolrRestletTestBase
extends|extends
name|RestTestBase
block|{
comment|/**    * Creates test harness, including "extra" servlets for all    * Solr Restlet Application subclasses.    */
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrSchemaRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrSchemaRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrSchemaRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrSchemaRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrSchemaRestApi
argument_list|,
literal|"/schema/*"
argument_list|)
expr_stmt|;
comment|// '/schema/*' matches '/schema', '/schema/', and '/schema/whatever...'
specifier|final
name|ServletHolder
name|solrConfigRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrConfigRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrConfigRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrConfigRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrConfigRestApi
argument_list|,
literal|"/config/*"
argument_list|)
expr_stmt|;
name|createJettyAndHarness
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema-rest.xml"
argument_list|,
literal|"/solr"
argument_list|,
literal|true
argument_list|,
name|extraServlets
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
