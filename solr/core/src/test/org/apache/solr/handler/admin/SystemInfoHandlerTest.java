begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|OperatingSystemMXBean
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_class
DECL|class|SystemInfoHandlerTest
specifier|public
class|class
name|SystemInfoHandlerTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testMagickGetter
specifier|public
name|void
name|testMagickGetter
parameter_list|()
throws|throws
name|Exception
block|{
name|OperatingSystemMXBean
name|os
init|=
name|ManagementFactory
operator|.
name|getOperatingSystemMXBean
argument_list|()
decl_stmt|;
comment|// make one directly
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|os
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
name|os
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"arch"
argument_list|,
name|os
operator|.
name|getArch
argument_list|()
argument_list|)
expr_stmt|;
comment|// make another using addMXBeanProperties()
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info2
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|SystemInfoHandler
operator|.
name|addMXBeanProperties
argument_list|(
name|os
argument_list|,
name|OperatingSystemMXBean
operator|.
name|class
argument_list|,
name|info2
argument_list|)
expr_stmt|;
comment|// make sure they got the same thing
for|for
control|(
name|String
name|p
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"name"
argument_list|,
literal|"version"
argument_list|,
literal|"arch"
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
name|info
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|,
name|info2
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
