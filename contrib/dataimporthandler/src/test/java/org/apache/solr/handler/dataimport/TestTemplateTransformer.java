begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  *<p>  * Test for TemplateTransformer  *</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestTemplateTransformer
specifier|public
class|class
name|TestTemplateTransformer
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testTransformRow
specifier|public
name|void
name|testTransformRow
parameter_list|()
block|{
name|List
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"firstName"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"lastName"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"middleName"
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"column"
argument_list|,
literal|"name"
argument_list|,
name|TemplateTransformer
operator|.
name|TEMPLATE
argument_list|,
literal|"${e.lastName}, ${e.firstName} ${e.middleName}"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|row
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"firstName"
argument_list|,
literal|"Shalin"
argument_list|,
literal|"middleName"
argument_list|,
literal|"Shekhar"
argument_list|,
literal|"lastName"
argument_list|,
literal|"Mangar"
argument_list|)
decl_stmt|;
name|VariableResolverImpl
name|resolver
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entityAttrs
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"name"
argument_list|,
literal|"e"
argument_list|)
decl_stmt|;
name|Context
name|context
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|fields
argument_list|,
name|entityAttrs
argument_list|)
decl_stmt|;
operator|new
name|TemplateTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|row
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Mangar, Shalin Shekhar"
argument_list|,
name|row
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
