begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|core
operator|.
name|SolrCore
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
comment|/**  * This is a simple test to make sure the unique key is not required   * when it is specified as 'false'   *   * It needs its own file so it can load a special schema file  */
end_comment
begin_class
DECL|class|NotRequiredUniqueKeyTest
specifier|public
class|class
name|NotRequiredUniqueKeyTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
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
comment|// usecase doesn't work with updateLog
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema-not-required-unique-key.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSchemaLoading
specifier|public
name|void
name|testSchemaLoading
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
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|uniqueKey
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|uniqueKey
operator|.
name|isRequired
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getRequiredFields
argument_list|()
operator|.
name|contains
argument_list|(
name|uniqueKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
