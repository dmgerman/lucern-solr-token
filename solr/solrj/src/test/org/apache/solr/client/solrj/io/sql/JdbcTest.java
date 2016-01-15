begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.io.sql
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
name|io
operator|.
name|sql
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
name|sql
operator|.
name|Connection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
operator|.
name|Slow
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|cloud
operator|.
name|AbstractZkTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
comment|/**  * All base tests will be done with CloudSolrStream. Under the covers CloudSolrStream uses SolrStream so  * SolrStream will get fully exercised through these tests.  **/
end_comment
begin_class
annotation|@
name|Slow
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Lucene45"
block|}
argument_list|)
DECL|class|JdbcTest
specifier|public
class|class
name|JdbcTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|SOLR_HOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME
init|=
name|getFile
argument_list|(
literal|"solrj"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
static|static
block|{
name|schemaString
operator|=
literal|"schema-sql.xml"
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{
name|AbstractZkTestCase
operator|.
name|SOLRHOME
operator|=
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{    }
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-sql.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"0"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello0"
argument_list|,
literal|"a_i"
argument_list|,
literal|"0"
argument_list|,
literal|"a_f"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello0"
argument_list|,
literal|"a_i"
argument_list|,
literal|"2"
argument_list|,
literal|"a_f"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello3"
argument_list|,
literal|"a_i"
argument_list|,
literal|"3"
argument_list|,
literal|"a_f"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello4"
argument_list|,
literal|"a_i"
argument_list|,
literal|"4"
argument_list|,
literal|"a_f"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello0"
argument_list|,
literal|"a_i"
argument_list|,
literal|"1"
argument_list|,
literal|"a_f"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello3"
argument_list|,
literal|"a_i"
argument_list|,
literal|"10"
argument_list|,
literal|"a_f"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello4"
argument_list|,
literal|"a_i"
argument_list|,
literal|"11"
argument_list|,
literal|"a_f"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello3"
argument_list|,
literal|"a_i"
argument_list|,
literal|"12"
argument_list|,
literal|"a_f"
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello3"
argument_list|,
literal|"a_i"
argument_list|,
literal|"13"
argument_list|,
literal|"a_f"
argument_list|,
literal|"9"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"a_s"
argument_list|,
literal|"hello0"
argument_list|,
literal|"a_i"
argument_list|,
literal|"14"
argument_list|,
literal|"a_f"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|String
name|zkHost
init|=
name|zkServer
operator|.
name|getZkAddress
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Connection
name|con
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://"
operator|+
name|zkHost
operator|+
literal|"?collection=collection1"
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|Statement
name|stmt
init|=
name|con
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select id, a_i, a_s, a_f from collection1 order by a_i desc limit 2"
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|14
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"a_f"
argument_list|)
operator|==
literal|10
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|13
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello3"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"a_f"
argument_list|)
operator|==
literal|9
operator|)
assert|;
assert|assert
operator|(
operator|!
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Test statement reuse
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select id, a_i, a_s, a_f from collection1 order by a_i asc limit 2"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|0
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"a_f"
argument_list|)
operator|==
literal|1
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|1
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"a_f"
argument_list|)
operator|==
literal|5
operator|)
assert|;
assert|assert
operator|(
operator|!
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Test connection reuse
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select id, a_i, a_s, a_f from collection1 order by a_i desc limit 2"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|14
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|13
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Test statement reuse
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select id, a_i, a_s, a_f from collection1 order by a_i asc limit 2"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|0
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getLong
argument_list|(
literal|"a_i"
argument_list|)
operator|==
literal|1
operator|)
assert|;
assert|assert
operator|(
operator|!
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Test simple loop
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select id, a_i, a_s, a_f from collection1 order by a_i asc limit 100"
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
operator|++
name|count
expr_stmt|;
block|}
assert|assert
operator|(
name|count
operator|==
literal|10
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Test facet aggregation
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"aggregationMode"
argument_list|,
literal|"facet"
argument_list|)
expr_stmt|;
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://"
operator|+
name|zkHost
operator|+
literal|"?collection=collection1"
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select a_s, sum(a_f) from collection1 group by a_s order by sum(a_f) desc"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello3"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|26
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|18
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello4"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|11
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Test map / reduce aggregation
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"aggregationMode"
argument_list|,
literal|"map_reduce"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"numWorkers"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://"
operator|+
name|zkHost
operator|+
literal|"?collection=collection1"
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select a_s, sum(a_f) from collection1 group by a_s order by sum(a_f) desc"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello3"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|26
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|18
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello4"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|11
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Test params on the url
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://"
operator|+
name|zkHost
operator|+
literal|"?collection=collection1&aggregationMode=map_reduce&numWorkers=2"
argument_list|)
expr_stmt|;
name|Properties
name|p
init|=
operator|(
operator|(
name|ConnectionImpl
operator|)
name|con
operator|)
operator|.
name|props
decl_stmt|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"aggregationMode"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"map_reduce"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"numWorkers"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
operator|)
assert|;
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select a_s, sum(a_f) from collection1 group by a_s order by sum(a_f) desc"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello3"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|26
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|18
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello4"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|11
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Test JDBC paramters in URL
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://"
operator|+
name|zkHost
operator|+
literal|"?collection=collection1&username=&password=&testKey1=testValue&testKey2"
argument_list|)
expr_stmt|;
name|p
operator|=
operator|(
operator|(
name|ConnectionImpl
operator|)
name|con
operator|)
operator|.
name|props
expr_stmt|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"username"
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"testKey1"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"testValue"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"testKey2"
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
assert|;
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select a_s, sum(a_f) from collection1 group by a_s order by sum(a_f) desc"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello3"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|26
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|18
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello4"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|11
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Test JDBC paramters in properties
name|Properties
name|providedProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|providedProperties
operator|.
name|put
argument_list|(
literal|"collection"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|providedProperties
operator|.
name|put
argument_list|(
literal|"username"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|providedProperties
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|providedProperties
operator|.
name|put
argument_list|(
literal|"testKey1"
argument_list|,
literal|"testValue"
argument_list|)
expr_stmt|;
name|providedProperties
operator|.
name|put
argument_list|(
literal|"testKey2"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|con
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:solr://"
operator|+
name|zkHost
argument_list|,
name|providedProperties
argument_list|)
expr_stmt|;
name|p
operator|=
operator|(
operator|(
name|ConnectionImpl
operator|)
name|con
operator|)
operator|.
name|props
expr_stmt|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"username"
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"testKey1"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"testValue"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"testKey2"
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
assert|;
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select a_s, sum(a_f) from collection1 group by a_s order by sum(a_f) desc"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello3"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|26
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello0"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|18
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|next
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getString
argument_list|(
literal|"a_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hello4"
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|rs
operator|.
name|getDouble
argument_list|(
literal|"sum(a_f)"
argument_list|)
operator|==
literal|11
operator|)
assert|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|testDriverMetadata
argument_list|()
expr_stmt|;
block|}
DECL|method|testDriverMetadata
specifier|private
name|void
name|testDriverMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collection
init|=
name|DEFAULT_COLLECTION
decl_stmt|;
name|String
name|connectionString
init|=
literal|"jdbc:solr://"
operator|+
name|zkServer
operator|.
name|getZkAddress
argument_list|()
operator|+
literal|"?collection="
operator|+
name|collection
operator|+
literal|"&username=&password=&testKey1=testValue&testKey2"
decl_stmt|;
try|try
init|(
name|Connection
name|con
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|connectionString
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
name|collection
argument_list|,
name|con
operator|.
name|getCatalog
argument_list|()
argument_list|)
expr_stmt|;
name|DatabaseMetaData
name|databaseMetaData
init|=
name|con
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|databaseMetaData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|connectionString
argument_list|,
name|databaseMetaData
operator|.
name|getURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
