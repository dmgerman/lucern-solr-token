begin_unit
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|CopyField
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
name|org
operator|.
name|restlet
operator|.
name|representation
operator|.
name|Representation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
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
name|Arrays
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
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/copyfields  *   *<p/>  *   * To restrict the set of copyFields in the response, specify one or both  * of the following as query parameters, with values as space and/or comma  * separated dynamic or explicit field names:  *   *<ul>  *<li>dest.fl: include copyFields that have one of these as a destination</li>  *<li>source.fl: include copyFields that have one of these as a source</li>  *</ul>  *   * If both dest.fl and source.fl are given as query parameters, the copyfields  * in the response will be restricted to those that match any of the destinations  * in dest.fl and also match any of the sources in source.fl.  */
end_comment
begin_class
DECL|class|CopyFieldCollectionResource
specifier|public
class|class
name|CopyFieldCollectionResource
extends|extends
name|BaseFieldResource
implements|implements
name|GETable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CopyFieldCollectionResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOURCE
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE
init|=
literal|"source"
decl_stmt|;
DECL|field|DESTINATION
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION
init|=
literal|"dest"
decl_stmt|;
DECL|field|SOURCE_FIELD_LIST
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_FIELD_LIST
init|=
name|SOURCE
operator|+
literal|"."
operator|+
name|CommonParams
operator|.
name|FL
decl_stmt|;
DECL|field|DESTINATION_FIELD_LIST
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION_FIELD_LIST
init|=
name|DESTINATION
operator|+
literal|"."
operator|+
name|CommonParams
operator|.
name|FL
decl_stmt|;
DECL|field|MAX_CHARS
specifier|private
specifier|static
specifier|final
name|String
name|MAX_CHARS
init|=
literal|"maxChars"
decl_stmt|;
DECL|field|SOURCE_DYNAMIC_BASE
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_DYNAMIC_BASE
init|=
literal|"sourceDynamicBase"
decl_stmt|;
DECL|field|DESTINATION_DYNAMIC_BASE
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION_DYNAMIC_BASE
init|=
literal|"destDynamicBase"
decl_stmt|;
DECL|field|SOURCE_EXPLICIT_FIELDS
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_EXPLICIT_FIELDS
init|=
literal|"sourceExplicitFields"
decl_stmt|;
DECL|field|requestedSourceFields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|requestedSourceFields
decl_stmt|;
DECL|field|requestedDestinationFields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|requestedDestinationFields
decl_stmt|;
DECL|method|CopyFieldCollectionResource
specifier|public
name|CopyFieldCollectionResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
if|if
condition|(
name|isExisting
argument_list|()
condition|)
block|{
name|String
name|sourceFieldListParam
init|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|SOURCE_FIELD_LIST
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|sourceFieldListParam
condition|)
block|{
name|String
index|[]
name|fields
init|=
name|sourceFieldListParam
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"[,\\s]+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|requestedSourceFields
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|requestedSourceFields
operator|.
name|remove
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// Remove empty values, if any
block|}
block|}
name|String
name|destinationFieldListParam
init|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|DESTINATION_FIELD_LIST
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|destinationFieldListParam
condition|)
block|{
name|String
index|[]
name|fields
init|=
name|destinationFieldListParam
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"[,\\s]+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|requestedDestinationFields
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|requestedDestinationFields
operator|.
name|remove
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// Remove empty values, if any
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Representation
name|get
parameter_list|()
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|props
init|=
operator|new
name|ArrayList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CopyField
argument_list|>
argument_list|>
name|sortedCopyFields
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CopyField
argument_list|>
argument_list|>
argument_list|(
name|getSchema
argument_list|()
operator|.
name|getCopyFieldsMap
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|CopyField
argument_list|>
name|copyFields
range|:
name|sortedCopyFields
operator|.
name|values
argument_list|()
control|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|copyFields
argument_list|,
operator|new
name|Comparator
argument_list|<
name|CopyField
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|CopyField
name|cf1
parameter_list|,
name|CopyField
name|cf2
parameter_list|)
block|{
comment|// source should all be the same => already sorted
return|return
name|cf1
operator|.
name|getDestination
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|cf2
operator|.
name|getDestination
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|CopyField
name|copyField
range|:
name|copyFields
control|)
block|{
specifier|final
name|String
name|source
init|=
name|copyField
operator|.
name|getSource
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|destination
init|=
name|copyField
operator|.
name|getDestination
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
literal|null
operator|==
name|requestedSourceFields
operator|||
name|requestedSourceFields
operator|.
name|contains
argument_list|(
name|source
argument_list|)
operator|)
operator|&&
operator|(
literal|null
operator|==
name|requestedDestinationFields
operator|||
name|requestedDestinationFields
operator|.
name|contains
argument_list|(
name|destination
argument_list|)
operator|)
condition|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|copyFieldProps
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|copyFieldProps
operator|.
name|add
argument_list|(
name|SOURCE
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|copyFieldProps
operator|.
name|add
argument_list|(
name|DESTINATION
argument_list|,
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|!=
name|copyField
operator|.
name|getMaxChars
argument_list|()
condition|)
block|{
name|copyFieldProps
operator|.
name|add
argument_list|(
name|MAX_CHARS
argument_list|,
name|copyField
operator|.
name|getMaxChars
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|props
operator|.
name|add
argument_list|(
name|copyFieldProps
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|IndexSchema
operator|.
name|DynamicCopy
name|dynamicCopy
range|:
name|getSchema
argument_list|()
operator|.
name|getDynamicCopyFields
argument_list|()
control|)
block|{
specifier|final
name|String
name|source
init|=
name|dynamicCopy
operator|.
name|getRegex
argument_list|()
decl_stmt|;
specifier|final
name|String
name|destination
init|=
name|dynamicCopy
operator|.
name|getDestFieldName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
literal|null
operator|==
name|requestedSourceFields
operator|||
name|requestedSourceFields
operator|.
name|contains
argument_list|(
name|source
argument_list|)
operator|)
operator|&&
operator|(
literal|null
operator|==
name|requestedDestinationFields
operator|||
name|requestedDestinationFields
operator|.
name|contains
argument_list|(
name|destination
argument_list|)
operator|)
condition|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|dynamicCopyProps
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|dynamicCopyProps
operator|.
name|add
argument_list|(
name|SOURCE
argument_list|,
name|dynamicCopy
operator|.
name|getRegex
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchema
operator|.
name|DynamicField
name|sourceDynamicBase
init|=
name|dynamicCopy
operator|.
name|getSourceDynamicBase
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|sourceDynamicBase
condition|)
block|{
name|dynamicCopyProps
operator|.
name|add
argument_list|(
name|SOURCE_DYNAMIC_BASE
argument_list|,
name|sourceDynamicBase
operator|.
name|getRegex
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|source
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|sourceExplicitFields
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|source
operator|.
name|replace
argument_list|(
literal|"*"
argument_list|,
literal|".*"
argument_list|)
argument_list|)
decl_stmt|;
comment|// glob->regex
for|for
control|(
name|String
name|field
range|:
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|pattern
operator|.
name|matcher
argument_list|(
name|field
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|sourceExplicitFields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sourceExplicitFields
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|sourceExplicitFields
argument_list|)
expr_stmt|;
name|dynamicCopyProps
operator|.
name|add
argument_list|(
name|SOURCE_EXPLICIT_FIELDS
argument_list|,
name|sourceExplicitFields
argument_list|)
expr_stmt|;
block|}
block|}
name|dynamicCopyProps
operator|.
name|add
argument_list|(
name|DESTINATION
argument_list|,
name|dynamicCopy
operator|.
name|getDestFieldName
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchema
operator|.
name|DynamicField
name|destDynamicBase
init|=
name|dynamicCopy
operator|.
name|getDestDynamicBase
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|destDynamicBase
condition|)
block|{
name|dynamicCopyProps
operator|.
name|add
argument_list|(
name|DESTINATION_DYNAMIC_BASE
argument_list|,
name|destDynamicBase
operator|.
name|getRegex
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|!=
name|dynamicCopy
operator|.
name|getMaxChars
argument_list|()
condition|)
block|{
name|dynamicCopyProps
operator|.
name|add
argument_list|(
name|MAX_CHARS
argument_list|,
name|dynamicCopy
operator|.
name|getMaxChars
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|props
operator|.
name|add
argument_list|(
name|dynamicCopyProps
argument_list|)
expr_stmt|;
block|}
block|}
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|SchemaRestApi
operator|.
name|COPY_FIELDS
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
block|}
end_class
end_unit
