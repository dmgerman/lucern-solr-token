begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * Test QueryComponent.doFieldSortValues  */
end_comment
begin_class
DECL|class|TestFieldSortValues
specifier|public
class|class
name|TestFieldSortValues
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
literal|"solrconfig-minimal.xml"
argument_list|,
literal|"schema-field-sort-values.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomComparator
specifier|public
name|void
name|testCustomComparator
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"payload"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"payload"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"payload"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"payload"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"payload"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// payload is backed by a custom sort field which returns the payload value mod 3
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc, id asc"
argument_list|,
literal|"fsv"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result/doc[int='2'  and position()=1]"
argument_list|,
literal|"//result/doc[int='3'  and position()=2]"
argument_list|,
literal|"//result/doc[int='5'  and position()=3]"
argument_list|,
literal|"//result/doc[int='1'  and position()=4]"
argument_list|,
literal|"//result/doc[int='4'  and position()=5]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
