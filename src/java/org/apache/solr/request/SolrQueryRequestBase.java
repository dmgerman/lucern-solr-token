begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|search
operator|.
name|SolrIndexSearcher
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
name|util
operator|.
name|RefCounted
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
name|core
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|HashMap
import|;
end_import
begin_comment
comment|/**  * Base implementation of<code>SolrQueryRequest</code> that provides some  * convenience methods for accessing parameters, and manages an IndexSearcher  * reference.  *  *<p>  * The<code>close()</code> method must be called on any instance of this  * class once it is no longer in use.  *</p>  *  *  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|SolrQueryRequestBase
specifier|public
specifier|abstract
class|class
name|SolrQueryRequestBase
implements|implements
name|SolrQueryRequest
block|{
annotation|@
name|Deprecated
DECL|field|QUERY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_NAME
init|=
literal|"q"
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|START_NAME
specifier|public
specifier|static
specifier|final
name|String
name|START_NAME
init|=
literal|"start"
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|ROWS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ROWS_NAME
init|=
literal|"rows"
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|XSL_NAME
specifier|public
specifier|static
specifier|final
name|String
name|XSL_NAME
init|=
literal|"xsl"
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|QUERYTYPE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|QUERYTYPE_NAME
init|=
literal|"qt"
decl_stmt|;
DECL|field|core
specifier|protected
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|origParams
specifier|protected
specifier|final
name|SolrParams
name|origParams
decl_stmt|;
DECL|field|params
specifier|protected
name|SolrParams
name|params
decl_stmt|;
DECL|field|context
specifier|protected
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|context
decl_stmt|;
DECL|field|streams
specifier|protected
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
decl_stmt|;
DECL|method|SolrQueryRequestBase
specifier|public
name|SolrQueryRequestBase
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|this
operator|.
name|origParams
operator|=
name|params
expr_stmt|;
block|}
DECL|method|getContext
specifier|public
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|getContext
parameter_list|()
block|{
comment|// SolrQueryRequest as a whole isn't thread safe, and this isn't either.
if|if
condition|(
name|context
operator|==
literal|null
condition|)
name|context
operator|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
return|return
name|context
return|;
block|}
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
DECL|method|getOriginalParams
specifier|public
name|SolrParams
name|getOriginalParams
parameter_list|()
block|{
return|return
name|origParams
return|;
block|}
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
DECL|method|getParam
specifier|public
name|String
name|getParam
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getParams
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|params
operator|.
name|getParams
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * use getParams().required().getInt( name ) instead    */
annotation|@
name|Deprecated
DECL|method|getIntParam
specifier|public
name|int
name|getIntParam
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|s
init|=
name|getParam
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Missing required parameter '"
operator|+
name|name
operator|+
literal|"' from "
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**    * use getParams().required().getInt( name ) instead    */
annotation|@
name|Deprecated
DECL|method|getIntParam
specifier|public
name|int
name|getIntParam
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defval
parameter_list|)
block|{
name|String
name|s
init|=
name|getParam
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|defval
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**    * use getParams().required().getParam( name ) instead    */
annotation|@
name|Deprecated
DECL|method|getStrParam
specifier|public
name|String
name|getStrParam
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|s
init|=
name|getParam
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Missing required parameter '"
operator|+
name|name
operator|+
literal|"' from "
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * use getParams().required().getParam( name ) instead    */
annotation|@
name|Deprecated
DECL|method|getStrParam
specifier|public
name|String
name|getStrParam
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defval
parameter_list|)
block|{
name|String
name|s
init|=
name|getParam
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|defval
else|:
name|s
return|;
block|}
annotation|@
name|Deprecated
DECL|method|getQueryString
specifier|public
name|String
name|getQueryString
parameter_list|()
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|SolrParams
operator|.
name|Q
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
DECL|method|getQueryType
specifier|public
name|String
name|getQueryType
parameter_list|()
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|SolrParams
operator|.
name|QT
argument_list|)
return|;
block|}
comment|// starting position in matches to return to client
annotation|@
name|Deprecated
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|params
operator|.
name|getInt
argument_list|(
name|SolrParams
operator|.
name|START
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|// number of matching documents to return
annotation|@
name|Deprecated
DECL|method|getLimit
specifier|public
name|int
name|getLimit
parameter_list|()
block|{
return|return
name|params
operator|.
name|getInt
argument_list|(
name|SolrParams
operator|.
name|ROWS
argument_list|,
literal|10
argument_list|)
return|;
block|}
DECL|field|startTime
specifier|protected
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// Get the start time of this request in milliseconds
DECL|method|getStartTime
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
comment|// The index searcher associated with this request
DECL|field|searcherHolder
specifier|protected
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcherHolder
decl_stmt|;
DECL|method|getSearcher
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
comment|// should this reach out and get a searcher from the core singleton, or
comment|// should the core populate one in a factory method to create requests?
comment|// or there could be a setSearcher() method that Solr calls
if|if
condition|(
name|searcherHolder
operator|==
literal|null
condition|)
block|{
name|searcherHolder
operator|=
name|core
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
block|}
return|return
name|searcherHolder
operator|.
name|get
argument_list|()
return|;
block|}
comment|// The solr core (coordinator, etc) associated with this request
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
block|{
return|return
name|core
return|;
block|}
comment|// The index schema associated with this request
DECL|method|getSchema
specifier|public
name|IndexSchema
name|getSchema
parameter_list|()
block|{
return|return
name|core
operator|.
name|getSchema
argument_list|()
return|;
block|}
comment|/**    * Frees resources associated with this request, this method<b>must</b>    * be called when the object is no longer in use.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|searcherHolder
operator|!=
literal|null
condition|)
block|{
name|searcherHolder
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** A Collection of ContentStreams passed to the request    */
DECL|method|getContentStreams
specifier|public
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
block|{
return|return
name|streams
return|;
block|}
DECL|method|setContentStreams
specifier|public
name|void
name|setContentStreams
parameter_list|(
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|s
parameter_list|)
block|{
name|streams
operator|=
name|s
expr_stmt|;
block|}
DECL|method|getParamString
specifier|public
name|String
name|getParamString
parameter_list|()
block|{
return|return
name|origParams
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|'{'
operator|+
name|params
operator|+
literal|'}'
return|;
block|}
block|}
end_class
end_unit
