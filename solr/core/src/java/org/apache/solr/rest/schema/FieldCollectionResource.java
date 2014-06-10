begin_unit
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
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
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|rest
operator|.
name|GETable
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
name|rest
operator|.
name|POSTable
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
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|ManagedIndexSchema
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
name|SchemaField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|MediaType
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
name|HashMap
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
name|Map
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
name|SortedSet
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
begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/fields  *<p/>  * Two query parameters are supported:  *<ul>  *<li>  * "fl": a comma- and/or space-separated list of fields to send properties  * for in the response, rather than the default: all of them.  *</li>  *<li>  * "includeDynamic": if the "fl" parameter is specified, matching dynamic  * fields are included in the response and identified with the "dynamicBase"  * property.  If the "fl" parameter is not specified, the "includeDynamic"  * query parameter is ignored.  *</li>  *</ul>  */
end_comment
begin_class
DECL|class|FieldCollectionResource
specifier|public
class|class
name|FieldCollectionResource
extends|extends
name|BaseFieldResource
implements|implements
name|GETable
implements|,
name|POSTable
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
name|FieldCollectionResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|includeDynamic
specifier|private
name|boolean
name|includeDynamic
decl_stmt|;
DECL|method|FieldCollectionResource
specifier|public
name|FieldCollectionResource
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
name|includeDynamic
operator|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|INCLUDE_DYNAMIC_PARAM
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|getRequestedFields
argument_list|()
condition|)
block|{
name|SortedSet
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|props
operator|.
name|add
argument_list|(
name|getFieldProperties
argument_list|(
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
literal|0
operator|==
name|getRequestedFields
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"Empty "
operator|+
name|CommonParams
operator|.
name|FL
operator|+
literal|" parameter value"
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
comment|// Use the same order as the fl parameter
for|for
control|(
name|String
name|fieldName
range|:
name|getRequestedFields
argument_list|()
control|)
block|{
specifier|final
name|SchemaField
name|field
decl_stmt|;
if|if
condition|(
name|includeDynamic
condition|)
block|{
name|field
operator|=
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|field
operator|=
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|field
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Requested field '"
operator|+
name|fieldName
operator|+
literal|"' not found."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|props
operator|.
name|add
argument_list|(
name|getFieldProperties
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|FIELDS
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
annotation|@
name|Override
DECL|method|post
specifier|public
name|Representation
name|post
parameter_list|(
name|Representation
name|entity
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|getSchema
argument_list|()
operator|.
name|isMutable
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"This IndexSchema is not mutable."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
literal|null
operator|==
name|entity
operator|.
name|getMediaType
argument_list|()
condition|)
block|{
name|entity
operator|.
name|setMediaType
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Only media type "
operator|+
name|MediaType
operator|.
name|APPLICATION_JSON
operator|.
name|toString
argument_list|()
operator|+
literal|" is accepted."
operator|+
literal|"  Request has media type "
operator|+
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|Object
name|object
init|=
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|entity
operator|.
name|getText
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|object
operator|instanceof
name|List
operator|)
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid JSON type "
operator|+
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", expected List of the form"
operator|+
literal|" (ignore the backslashes): [{\"name\":\"foo\",\"type\":\"text_general\", ...}, {...}, ...]"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|object
decl_stmt|;
name|List
argument_list|<
name|SchemaField
argument_list|>
name|newFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NewFieldArguments
argument_list|>
name|newFieldArguments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|IndexSchema
name|oldSchema
init|=
name|getSchema
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|copyFields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
range|:
name|list
control|)
block|{
name|String
name|fieldName
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|remove
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fieldName
condition|)
block|{
name|String
name|message
init|=
literal|"Missing '"
operator|+
name|IndexSchema
operator|.
name|NAME
operator|+
literal|"' mapping."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|String
name|fieldType
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|remove
argument_list|(
name|IndexSchema
operator|.
name|TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fieldType
condition|)
block|{
name|String
name|message
init|=
literal|"Missing '"
operator|+
name|IndexSchema
operator|.
name|TYPE
operator|+
literal|"' mapping."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
comment|// copyFields:"comma separated list of destination fields"
name|Object
name|copies
init|=
name|map
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|COPY_FIELDS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|copyTo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|copies
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|copies
operator|instanceof
name|List
condition|)
block|{
name|copyTo
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|copies
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|copies
operator|instanceof
name|String
condition|)
block|{
name|copyTo
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|copies
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|message
init|=
literal|"Invalid '"
operator|+
name|IndexSchema
operator|.
name|COPY_FIELDS
operator|+
literal|"' type."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|copyTo
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|IndexSchema
operator|.
name|COPY_FIELDS
argument_list|)
expr_stmt|;
name|copyFields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
block|}
name|newFields
operator|.
name|add
argument_list|(
name|oldSchema
operator|.
name|newField
argument_list|(
name|fieldName
argument_list|,
name|fieldType
argument_list|,
name|map
argument_list|)
argument_list|)
expr_stmt|;
name|newFieldArguments
operator|.
name|add
argument_list|(
operator|new
name|NewFieldArguments
argument_list|(
name|fieldName
argument_list|,
name|fieldType
argument_list|,
name|map
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|firstAttempt
init|=
literal|true
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|firstAttempt
condition|)
block|{
comment|// If this isn't the first attempt, we must have failed due to
comment|// the schema changing in Zk during optimistic concurrency control.
comment|// In that case, rerun creating the new fields, because they may
comment|// fail now due to changes in the schema.  This behavior is consistent
comment|// with what would happen if we locked the schema and the other schema
comment|// change went first.
name|newFields
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|NewFieldArguments
name|args
range|:
name|newFieldArguments
control|)
block|{
name|newFields
operator|.
name|add
argument_list|(
name|oldSchema
operator|.
name|newField
argument_list|(
name|args
operator|.
name|getName
argument_list|()
argument_list|,
name|args
operator|.
name|getType
argument_list|()
argument_list|,
name|args
operator|.
name|getMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|firstAttempt
operator|=
literal|false
expr_stmt|;
name|IndexSchema
name|newSchema
init|=
name|oldSchema
operator|.
name|addFields
argument_list|(
name|newFields
argument_list|,
name|copyFields
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|newSchema
condition|)
block|{
name|getSolrCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Failed to add fields."
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ManagedIndexSchema
operator|.
name|SchemaChangedInZkException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Schema changed while processing request, retrying"
argument_list|)
expr_stmt|;
name|oldSchema
operator|=
name|getSolrCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
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
DECL|class|NewFieldArguments
specifier|private
specifier|static
class|class
name|NewFieldArguments
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|map
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
DECL|method|NewFieldArguments
name|NewFieldArguments
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getMap
parameter_list|()
block|{
return|return
name|map
return|;
block|}
block|}
block|}
end_class
end_unit
