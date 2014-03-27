begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|TestUtil
import|;
end_import
begin_comment
comment|//import org.apache.lucene.util.LuceneTestCase;
end_comment
begin_comment
comment|//import org.apache.solr.util.AbstractSolrTestCase;
end_comment
begin_comment
comment|//import org.apache.solr.client.solrj.embedded.JettySolrRunner;
end_comment
begin_comment
comment|//import org.apache.solr.client.solrj.impl.HttpSolrServer;
end_comment
begin_comment
comment|//import org.apache.solr.client.solrj.SolrServer;
end_comment
begin_comment
comment|//import org.apache.solr.client.solrj.SolrServerException;
end_comment
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
name|response
operator|.
name|QueryResponse
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
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import
begin_comment
comment|/**  *<p> Test for Loading core properties from a properties file</p>  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestSolrCoreProperties
specifier|public
class|class
name|TestSolrCoreProperties
extends|extends
name|SolrJettyTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|homeDir
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
name|TestSolrCoreProperties
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|collDir
init|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"collection1"
argument_list|)
decl_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|collDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|collDir
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|homeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|collDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|src_dir
init|=
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|src_dir
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|src_dir
argument_list|,
literal|"solrconfig-solcoreproperties.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|src_dir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"foo.foo1"
argument_list|,
literal|"f1"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"foo.foo2"
argument_list|,
literal|"f2"
argument_list|)
expr_stmt|;
name|Writer
name|fos
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrcore.properties"
argument_list|)
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
decl_stmt|;
name|p
operator|.
name|store
argument_list|(
name|fos
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|createJetty
argument_list|(
name|homeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"echoParams"
argument_list|,
literal|"all"
argument_list|)
decl_stmt|;
name|QueryResponse
name|res
init|=
name|getSolrServer
argument_list|()
operator|.
name|query
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
name|echoedParams
init|=
operator|(
name|NamedList
operator|)
name|res
operator|.
name|getHeader
argument_list|()
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f1"
argument_list|,
name|echoedParams
operator|.
name|get
argument_list|(
literal|"p1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f2"
argument_list|,
name|echoedParams
operator|.
name|get
argument_list|(
literal|"p2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
