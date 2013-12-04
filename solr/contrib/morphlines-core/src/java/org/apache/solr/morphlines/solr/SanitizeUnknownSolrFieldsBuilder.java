begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
package|;
end_package
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Command
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|CommandBuilder
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|AbstractCommand
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import
begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|Config
import|;
end_import
begin_comment
comment|/**  * Command that sanitizes record fields that are unknown to Solr schema.xml by either deleting them  * (renameToPrefix is absent or a zero length string), or by moving them to a field prefixed with  * the given renameToPrefix (e.g. renameToPrefix = "ignored_" to use typical dynamic Solr fields).  *<p>  * Recall that Solr throws an exception on any attempt to load a document that contains a field that  * isn't specified in schema.xml.  */
end_comment
begin_class
DECL|class|SanitizeUnknownSolrFieldsBuilder
specifier|public
specifier|final
class|class
name|SanitizeUnknownSolrFieldsBuilder
implements|implements
name|CommandBuilder
block|{
annotation|@
name|Override
DECL|method|getNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"sanitizeUnknownSolrFields"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Command
name|build
parameter_list|(
name|Config
name|config
parameter_list|,
name|Command
name|parent
parameter_list|,
name|Command
name|child
parameter_list|,
name|MorphlineContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|SanitizeUnknownSolrFields
argument_list|(
name|this
argument_list|,
name|config
argument_list|,
name|parent
argument_list|,
name|child
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|SanitizeUnknownSolrFields
specifier|private
specifier|static
specifier|final
class|class
name|SanitizeUnknownSolrFields
extends|extends
name|AbstractCommand
block|{
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|renameToPrefix
specifier|private
specifier|final
name|String
name|renameToPrefix
decl_stmt|;
DECL|method|SanitizeUnknownSolrFields
specifier|public
name|SanitizeUnknownSolrFields
parameter_list|(
name|CommandBuilder
name|builder
parameter_list|,
name|Config
name|config
parameter_list|,
name|Command
name|parent
parameter_list|,
name|Command
name|child
parameter_list|,
name|MorphlineContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|,
name|config
argument_list|,
name|parent
argument_list|,
name|child
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Config
name|solrLocatorConfig
init|=
name|getConfigs
argument_list|()
operator|.
name|getConfig
argument_list|(
name|config
argument_list|,
literal|"solrLocator"
argument_list|)
decl_stmt|;
name|SolrLocator
name|locator
init|=
operator|new
name|SolrLocator
argument_list|(
name|solrLocatorConfig
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"solrLocator: {}"
argument_list|,
name|locator
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|locator
operator|.
name|getIndexSchema
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Solr schema: \n{}"
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|join
argument_list|(
operator|new
name|TreeMap
argument_list|(
name|schema
operator|.
name|getFields
argument_list|()
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|str
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"renameToPrefix"
argument_list|,
literal|""
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|this
operator|.
name|renameToPrefix
operator|=
name|str
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|str
else|:
literal|null
expr_stmt|;
name|validateArguments
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doProcess
specifier|protected
name|boolean
name|doProcess
parameter_list|(
name|Record
name|record
parameter_list|)
block|{
name|Collection
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
argument_list|(
name|record
operator|.
name|getFields
argument_list|()
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|key
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sanitizing unknown Solr field: {}"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|Collection
name|values
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|renameToPrefix
operator|!=
literal|null
condition|)
block|{
name|record
operator|.
name|getFields
argument_list|()
operator|.
name|putAll
argument_list|(
name|renameToPrefix
operator|+
name|key
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// implicitly removes key from record
block|}
block|}
return|return
name|super
operator|.
name|doProcess
argument_list|(
name|record
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
