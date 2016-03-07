begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Locale
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|ImmutableSet
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
name|cloud
operator|.
name|ZkSolrResourceLoader
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
name|ContentStream
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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrRequestHandler
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
name|response
operator|.
name|SolrQueryResponse
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
name|SchemaManager
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
name|ZkIndexSchemaReader
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
import|import static
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
operator|.
name|JSON
import|;
end_import
begin_class
DECL|class|SchemaHandler
specifier|public
class|class
name|SchemaHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|isImmutableConfigSet
specifier|private
name|boolean
name|isImmutableConfigSet
init|=
literal|false
decl_stmt|;
DECL|field|level2
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|level2
decl_stmt|;
static|static
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|s
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|IndexSchema
operator|.
name|FIELD_TYPES
argument_list|,
name|IndexSchema
operator|.
name|FIELDS
argument_list|,
name|IndexSchema
operator|.
name|DYNAMIC_FIELDS
argument_list|,
name|IndexSchema
operator|.
name|COPY_FIELDS
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s1
range|:
name|s
control|)
block|{
name|m
operator|.
name|put
argument_list|(
name|s1
argument_list|,
name|s1
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|s1
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|s1
argument_list|)
expr_stmt|;
block|}
name|level2
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrConfigHandler
operator|.
name|setWt
argument_list|(
name|req
argument_list|,
name|JSON
argument_list|)
expr_stmt|;
name|String
name|httpMethod
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"httpMethod"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"POST"
operator|.
name|equals
argument_list|(
name|httpMethod
argument_list|)
condition|)
block|{
if|if
condition|(
name|isImmutableConfigSet
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
literal|"ConfigSet is immutable"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|req
operator|.
name|getContentStreams
argument_list|()
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
literal|"no stream"
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|ContentStream
name|stream
range|:
name|req
operator|.
name|getContentStreams
argument_list|()
control|)
block|{
try|try
block|{
name|List
name|errs
init|=
operator|new
name|SchemaManager
argument_list|(
name|req
argument_list|)
operator|.
name|performOperations
argument_list|(
name|stream
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|errs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"Error reading input String "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
else|else
block|{
name|handleGET
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleGET
specifier|private
name|void
name|handleGET
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
try|try
block|{
name|String
name|path
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|path
condition|)
block|{
case|case
literal|"/schema"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|SCHEMA
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getNamedPropertyValues
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/version"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|VERSION
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/uniquekey"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|UNIQUE_KEY
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/similarity"
case|:
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|SIMILARITY
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getSimilarityFactory
argument_list|()
operator|.
name|getNamedPropertyValues
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"/schema/name"
case|:
block|{
specifier|final
name|String
name|schemaName
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getSchemaName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|schemaName
condition|)
block|{
name|String
name|message
init|=
literal|"Schema has no name"
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|,
name|schemaName
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|"/schema/defaultsearchfield"
case|:
block|{
specifier|final
name|String
name|defaultSearchFieldName
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getDefaultSearchFieldName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|defaultSearchFieldName
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"undefined "
operator|+
name|IndexSchema
operator|.
name|DEFAULT_SEARCH_FIELD
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|DEFAULT_SEARCH_FIELD
argument_list|,
name|defaultSearchFieldName
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|"/schema/solrqueryparser"
case|:
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|props
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|props
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|DEFAULT_OPERATOR
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getQueryParserDefaultOperator
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|SOLR_QUERY_PARSER
argument_list|,
name|props
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|"/schema/zkversion"
case|:
block|{
name|int
name|refreshIfBelowVersion
init|=
operator|-
literal|1
decl_stmt|;
name|Object
name|refreshParam
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"refreshIfBelowVersion"
argument_list|)
decl_stmt|;
if|if
condition|(
name|refreshParam
operator|!=
literal|null
condition|)
name|refreshIfBelowVersion
operator|=
operator|(
name|refreshParam
operator|instanceof
name|Number
operator|)
condition|?
operator|(
operator|(
name|Number
operator|)
name|refreshParam
operator|)
operator|.
name|intValue
argument_list|()
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|refreshParam
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|zkVersion
init|=
operator|-
literal|1
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|schema
operator|instanceof
name|ManagedIndexSchema
condition|)
block|{
name|ManagedIndexSchema
name|managed
init|=
operator|(
name|ManagedIndexSchema
operator|)
name|schema
decl_stmt|;
name|zkVersion
operator|=
name|managed
operator|.
name|getSchemaZkVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|refreshIfBelowVersion
operator|!=
operator|-
literal|1
operator|&&
name|zkVersion
operator|<
name|refreshIfBelowVersion
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"REFRESHING SCHEMA (refreshIfBelowVersion="
operator|+
name|refreshIfBelowVersion
operator|+
literal|", currentVersion="
operator|+
name|zkVersion
operator|+
literal|") before returning version!"
argument_list|)
expr_stmt|;
name|ZkSolrResourceLoader
name|zkSolrResourceLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|ZkIndexSchemaReader
name|zkIndexSchemaReader
init|=
name|zkSolrResourceLoader
operator|.
name|getZkIndexSchemaReader
argument_list|()
decl_stmt|;
name|managed
operator|=
name|zkIndexSchemaReader
operator|.
name|refreshSchemaFromZk
argument_list|(
name|refreshIfBelowVersion
argument_list|)
expr_stmt|;
name|zkVersion
operator|=
name|managed
operator|.
name|getSchemaZkVersion
argument_list|()
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"zkversion"
argument_list|,
name|zkVersion
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|"/schema/solrqueryparser/defaultoperator"
case|:
block|{
name|rsp
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|DEFAULT_OPERATOR
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getQueryParserDefaultOperator
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|path
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
name|parts
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|&&
name|level2
operator|.
name|containsKey
argument_list|(
name|parts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|realName
init|=
name|level2
operator|.
name|get
argument_list|(
name|parts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|propertyValues
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getNamedPropertyValues
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|propertyValues
operator|.
name|get
argument_list|(
name|realName
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|String
name|name
init|=
name|parts
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|list
control|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|SimpleOrderedMap
condition|)
block|{
name|SimpleOrderedMap
name|simpleOrderedMap
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|simpleOrderedMap
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
name|realName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|realName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|simpleOrderedMap
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"No such path "
operator|+
name|path
argument_list|)
throw|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
name|realName
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"No such path "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|subPaths
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|subPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"version"
argument_list|,
literal|"uniquekey"
argument_list|,
literal|"name"
argument_list|,
literal|"similarity"
argument_list|,
literal|"defaultsearchfield"
argument_list|,
literal|"solrqueryparser"
argument_list|,
literal|"zkversion"
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
name|subPaths
operator|.
name|addAll
argument_list|(
name|level2
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubHandler
specifier|public
name|SolrRequestHandler
name|getSubHandler
parameter_list|(
name|String
name|subPath
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|subPath
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
name|parts
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|prefix
init|=
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|subPaths
operator|.
name|contains
argument_list|(
name|prefix
argument_list|)
condition|)
return|return
name|this
return|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"CRUD operations over the Solr schema"
return|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|isImmutableConfigSet
operator|=
name|SolrConfigHandler
operator|.
name|getImmutable
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
