begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|handler
operator|.
name|StandardRequestHandler
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
name|SolrRequestHandler
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
DECL|class|RequestHandlersTest
specifier|public
class|class
name|RequestHandlersTest
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLazyLoading
specifier|public
name|void
name|testLazyLoading
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"lazy"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|handler
operator|instanceof
name|StandardRequestHandler
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"name"
argument_list|,
literal|"Zapp Brannigan"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"title"
argument_list|,
literal|"Democratic Order of Planets"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|,
literal|"name"
argument_list|,
literal|"The Zapper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"45"
argument_list|,
literal|"title"
argument_list|,
literal|"25 star General"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"46"
argument_list|,
literal|"subject"
argument_list|,
literal|"Defeated the pacifists of the Gandhi nebula"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"47"
argument_list|,
literal|"text"
argument_list|,
literal|"line up and fly directly at the enemy death cannons, clogging them with wreckage!"
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
literal|"lazy request handler returns all matches"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[42 TO 47]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6]"
argument_list|)
expr_stmt|;
comment|// But it should behave just like the 'defaults' request handler above
name|assertQ
argument_list|(
literal|"lazy handler returns fewer matches"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[42 TO 47]"
argument_list|,
literal|"qt"
argument_list|,
literal|"lazy"
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"lazy handler includes highlighting"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"name:Zapp OR title:General"
argument_list|,
literal|"qt"
argument_list|,
literal|"lazy"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPathNormalization
specifier|public
name|void
name|testPathNormalization
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrRequestHandler
name|h1
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/csv"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|h1
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|h2
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/csv/"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|h2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|h1
argument_list|,
name|h2
argument_list|)
expr_stmt|;
comment|// the same object
name|assertNull
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/csv/asdgadsgas"
argument_list|)
argument_list|)
expr_stmt|;
comment|// prefix
block|}
block|}
end_class
end_unit
