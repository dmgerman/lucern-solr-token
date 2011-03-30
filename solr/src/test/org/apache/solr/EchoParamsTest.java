begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
comment|/** Test SOLR-59, echo of query parameters */
end_comment
begin_class
DECL|class|EchoParamsTest
specifier|public
class|class
name|EchoParamsTest
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
literal|"solr/crazy-path-to-config.xml"
argument_list|,
literal|"solr/crazy-path-to-schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|field|HEADER_XPATH
specifier|private
specifier|static
specifier|final
name|String
name|HEADER_XPATH
init|=
literal|"/response/lst[@name='responseHeader']"
decl_stmt|;
annotation|@
name|Test
DECL|method|testDefaultEchoParams
specifier|public
name|void
name|testDefaultEchoParams
parameter_list|()
block|{
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|HEADER_XPATH
operator|+
literal|"/int[@name='status']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"not(//lst[@name='params'])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultEchoParamsDefaultVersion
specifier|public
name|void
name|testDefaultEchoParamsDefaultVersion
parameter_list|()
block|{
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|HEADER_XPATH
operator|+
literal|"/int[@name='status']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"not(//lst[@name='params'])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExplicitEchoParams
specifier|public
name|void
name|testExplicitEchoParams
parameter_list|()
block|{
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"echoParams"
argument_list|,
literal|"explicit"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|HEADER_XPATH
operator|+
literal|"/int[@name='status']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|HEADER_XPATH
operator|+
literal|"/lst[@name='params']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|HEADER_XPATH
operator|+
literal|"/lst[@name='params']/str[@name='wt'][.='xml']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllEchoParams
specifier|public
name|void
name|testAllEchoParams
parameter_list|()
block|{
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"crazy_custom_qt"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
name|CommonParams
operator|.
name|VERSION
argument_list|,
literal|"2.2"
argument_list|,
literal|"wt"
argument_list|,
literal|"xml"
argument_list|,
literal|"echoParams"
argument_list|,
literal|"all"
argument_list|,
literal|"echoHandler"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|HEADER_XPATH
operator|+
literal|"/lst[@name='params']/str[@name='fl'][.='implicit']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|HEADER_XPATH
operator|+
literal|"/str[@name='handler'][.='org.apache.solr.handler.StandardRequestHandler']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
