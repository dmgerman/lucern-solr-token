begin_unit
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
name|AbstractSolrTestCase
import|;
end_import
begin_class
DECL|class|IndexReaderFactoryTest
specifier|public
class|class
name|IndexReaderFactoryTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig-termindex.xml"
return|;
block|}
comment|/**    * Simple test to ensure that alternate IndexReaderFactory is being used.    */
DECL|method|testAltReaderUsed
specifier|public
name|void
name|testAltReaderUsed
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReaderFactory
name|readerFactory
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getIndexReaderFactory
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Factory is null"
argument_list|,
name|readerFactory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"readerFactory is not an instanceof "
operator|+
name|AlternateDirectoryTest
operator|.
name|TestIndexReaderFactory
operator|.
name|class
argument_list|,
name|readerFactory
operator|instanceof
name|StandardIndexReaderFactory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"termInfoIndexDivisor not set to 12"
argument_list|,
name|readerFactory
operator|.
name|getTermInfosIndexDivisor
argument_list|()
operator|==
literal|12
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
