begin_unit
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import
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
name|List
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|*
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
name|Ignore
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
DECL|class|TestEphemeralCache
specifier|public
class|class
name|TestEphemeralCache
extends|extends
name|AbstractDataImportHandlerTestCase
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
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|DestroyCountCache
operator|.
name|destroyed
operator|.
name|clear
argument_list|()
expr_stmt|;
name|setupMockData
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFullImport
argument_list|(
name|getDataConfigDotXml
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setupMockData
specifier|private
name|void
name|setupMockData
parameter_list|()
block|{
name|List
name|parentRows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|parentRows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"1"
argument_list|)
argument_list|,
literal|"parent_s"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|parentRows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"2"
argument_list|)
argument_list|,
literal|"parent_s"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|parentRows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"parent_s"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|parentRows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"4"
argument_list|)
argument_list|,
literal|"parent_s"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|parentRows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"5"
argument_list|)
argument_list|,
literal|"parent_s"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|child1Rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"6"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"this is the number six."
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"5"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"this is the number five."
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"6"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"let's sing a song of six."
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"III"
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"|||"
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"1"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"1"
argument_list|)
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"uno"
argument_list|)
argument_list|)
expr_stmt|;
name|child1Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"2"
argument_list|)
argument_list|,
literal|"child1b_s"
argument_list|,
literal|"CHILD1B"
argument_list|,
literal|"child1a_mult_s"
argument_list|,
literal|"this is the number two."
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|child2Rows
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"6"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'this is the number six.'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"5"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'this is the number five.'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"6"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'let's sing a song of six.'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'three'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'III'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child2b_s"
argument_list|,
literal|"CHILD2B"
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, '3'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"3"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, '|||'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"1"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'one'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"1"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'uno'"
argument_list|)
argument_list|)
expr_stmt|;
name|child2Rows
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|"2"
argument_list|)
argument_list|,
literal|"child2a_mult_s"
argument_list|,
literal|"Child 2 says, 'this is the number two.'"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"SELECT * FROM PARENT"
argument_list|,
name|parentRows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"SELECT * FROM CHILD_1"
argument_list|,
name|child1Rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"SELECT * FROM CHILD_2"
argument_list|,
name|child2Rows
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getDataConfigDotXml
specifier|private
name|String
name|getDataConfigDotXml
parameter_list|()
block|{
return|return
literal|"<dataConfig>"
operator|+
literal|"<dataSource type=\"MockDataSource\" />"
operator|+
literal|"<document>"
operator|+
literal|"<entity "
operator|+
literal|"     name=\"PARENT\""
operator|+
literal|"     processor=\"SqlEntityProcessor\""
operator|+
literal|"     cacheImpl=\"org.apache.solr.handler.dataimport.DestroyCountCache\""
operator|+
literal|"     cacheName=\"PARENT\""
operator|+
literal|"     query=\"SELECT * FROM PARENT\"  "
operator|+
literal|">"
operator|+
literal|"<entity"
operator|+
literal|"       name=\"CHILD_1\""
operator|+
literal|"       processor=\"SqlEntityProcessor\""
operator|+
literal|"       cacheImpl=\"org.apache.solr.handler.dataimport.DestroyCountCache\""
operator|+
literal|"       cacheName=\"CHILD\""
operator|+
literal|"       cachePk=\"id\""
operator|+
literal|"       cacheLookup=\"PARENT.id\""
operator|+
literal|"       fieldNames=\"id,         child1a_mult_s, child1b_s\""
operator|+
literal|"       fieldTypes=\"BIGDECIMAL, STRING,         STRING\""
operator|+
literal|"       query=\"SELECT * FROM CHILD_1\"       "
operator|+
literal|"     />"
operator|+
literal|"<entity"
operator|+
literal|"       name=\"CHILD_2\""
operator|+
literal|"       processor=\"SqlEntityProcessor\""
operator|+
literal|"       cacheImpl=\"org.apache.solr.handler.dataimport.DestroyCountCache\""
operator|+
literal|"       cachePk=\"id\""
operator|+
literal|"       cacheLookup=\"PARENT.id\""
operator|+
literal|"       query=\"SELECT * FROM CHILD_2\"       "
operator|+
literal|"     />"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
return|;
block|}
DECL|method|assertFullImport
specifier|private
name|void
name|assertFullImport
parameter_list|(
name|String
name|dataConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|dataConfig
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='5']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:6"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"parent_s:four"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"child1a_mult_s:this\\ is\\ the\\ numbe*"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"child2a_mult_s:Child\\ 2\\ say*"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"child1b_s:CHILD1B"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"child2b_s:CHILD2B"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"child1a_mult_s:one"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"child1a_mult_s:uno"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"child1a_mult_s:(uno OR one)"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DestroyCountCache
operator|.
name|destroyed
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
