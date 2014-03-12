begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  *<p>  * A {@link Transformer} which can put values into a column by resolving an expression  * containing other columns  *</p>  *<p/>  *<p>  * For example:<br />  *&lt;field column="name" template="${e.lastName}, ${e.firstName}  * ${e.middleName}" /&gt; will produce the name by combining values from  * lastName, firstName and middleName fields as given in the template attribute.  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TemplateTransformer
specifier|public
class|class
name|TemplateTransformer
extends|extends
name|Transformer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TemplateTransformer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|templateVsVars
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|templateVsVars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|VariableResolver
name|resolver
init|=
operator|(
name|VariableResolver
operator|)
name|context
operator|.
name|getVariableResolver
argument_list|()
decl_stmt|;
comment|// Add current row to the copy of resolver map
comment|//    for (Map.Entry<String, Object> entry : row.entrySet())
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
name|String
name|expr
init|=
name|map
operator|.
name|get
argument_list|(
name|TEMPLATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
continue|continue;
name|String
name|column
init|=
name|map
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
comment|// Verify if all variables can be resolved or not
name|boolean
name|resolvable
init|=
literal|true
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|variables
init|=
name|this
operator|.
name|templateVsVars
operator|.
name|get
argument_list|(
name|expr
argument_list|)
decl_stmt|;
if|if
condition|(
name|variables
operator|==
literal|null
condition|)
block|{
name|variables
operator|=
name|resolver
operator|.
name|getVariables
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|this
operator|.
name|templateVsVars
operator|.
name|put
argument_list|(
name|expr
argument_list|,
name|variables
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|v
range|:
name|variables
control|)
block|{
if|if
condition|(
name|resolver
operator|.
name|resolve
argument_list|(
name|v
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to resolve variable: "
operator|+
name|v
operator|+
literal|" while parsing expression: "
operator|+
name|expr
argument_list|)
expr_stmt|;
name|resolvable
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|resolvable
condition|)
continue|continue;
if|if
condition|(
name|variables
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|expr
operator|.
name|startsWith
argument_list|(
literal|"${"
argument_list|)
operator|&&
name|expr
operator|.
name|endsWith
argument_list|(
literal|"}"
argument_list|)
condition|)
block|{
name|row
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
name|variables
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|row
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|row
return|;
block|}
DECL|field|TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|TEMPLATE
init|=
literal|"template"
decl_stmt|;
block|}
end_class
end_unit
