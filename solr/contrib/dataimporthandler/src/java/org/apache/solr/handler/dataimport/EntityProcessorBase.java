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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|*
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
name|*
import|;
end_import
begin_comment
comment|/**  *<p> Base class for all implementations of {@link EntityProcessor}</p><p/><p> Most implementations of {@link EntityProcessor}  * extend this base class which provides common functionality.</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|EntityProcessorBase
specifier|public
class|class
name|EntityProcessorBase
extends|extends
name|EntityProcessor
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
name|EntityProcessorBase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|isFirstInit
specifier|protected
name|boolean
name|isFirstInit
init|=
literal|true
decl_stmt|;
DECL|field|entityName
specifier|protected
name|String
name|entityName
decl_stmt|;
DECL|field|context
specifier|protected
name|Context
name|context
decl_stmt|;
DECL|field|rowIterator
specifier|protected
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowIterator
decl_stmt|;
DECL|field|query
specifier|protected
name|String
name|query
decl_stmt|;
DECL|field|onError
specifier|protected
name|String
name|onError
init|=
name|ABORT
decl_stmt|;
DECL|field|cacheSupport
specifier|protected
name|DIHCacheSupport
name|cacheSupport
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
if|if
condition|(
name|isFirstInit
condition|)
block|{
name|firstInit
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cacheSupport
operator|!=
literal|null
condition|)
block|{
name|cacheSupport
operator|.
name|initNewParent
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**first time init call. do one-time operations here    */
DECL|method|firstInit
specifier|protected
name|void
name|firstInit
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|entityName
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|ON_ERROR
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
name|onError
operator|=
name|s
expr_stmt|;
name|initCache
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|isFirstInit
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|initCache
specifier|protected
name|void
name|initCache
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|String
name|cacheImplName
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_IMPL
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheImplName
operator|!=
literal|null
condition|)
block|{
name|cacheSupport
operator|=
operator|new
name|DIHCacheSupport
argument_list|(
name|context
argument_list|,
name|cacheImplName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextModifiedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedRowKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|nextDeletedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextDeletedRowKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|nextModifiedParentRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedParentRowKey
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * For a simple implementation, this is the only method that the sub-class should implement. This is intended to    * stream rows one-by-one. Return null to signal end of rows    *    * @return a row where the key is the name of the field and value can be any Object or a Collection of objects. Return    *         null to signal end of rows    */
annotation|@
name|Override
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// do not do anything
block|}
DECL|method|getNext
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getNext
parameter_list|()
block|{
if|if
condition|(
name|cacheSupport
operator|==
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|rowIterator
operator|.
name|hasNext
argument_list|()
condition|)
return|return
name|rowIterator
operator|.
name|next
argument_list|()
return|;
name|query
operator|=
literal|null
expr_stmt|;
name|rowIterator
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"getNext() failed for query '"
operator|+
name|query
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|query
operator|=
literal|null
expr_stmt|;
name|rowIterator
operator|=
literal|null
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|WARN
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
name|cacheSupport
operator|.
name|getCacheData
argument_list|(
name|context
argument_list|,
name|query
argument_list|,
name|rowIterator
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|query
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|cacheSupport
operator|!=
literal|null
condition|)
block|{
name|cacheSupport
operator|.
name|destroyAll
argument_list|()
expr_stmt|;
block|}
name|cacheSupport
operator|=
literal|null
expr_stmt|;
block|}
DECL|field|TRANSFORMER
specifier|public
specifier|static
specifier|final
name|String
name|TRANSFORMER
init|=
literal|"transformer"
decl_stmt|;
DECL|field|TRANSFORM_ROW
specifier|public
specifier|static
specifier|final
name|String
name|TRANSFORM_ROW
init|=
literal|"transformRow"
decl_stmt|;
DECL|field|ON_ERROR
specifier|public
specifier|static
specifier|final
name|String
name|ON_ERROR
init|=
literal|"onError"
decl_stmt|;
DECL|field|ABORT
specifier|public
specifier|static
specifier|final
name|String
name|ABORT
init|=
literal|"abort"
decl_stmt|;
DECL|field|CONTINUE
specifier|public
specifier|static
specifier|final
name|String
name|CONTINUE
init|=
literal|"continue"
decl_stmt|;
DECL|field|SKIP
specifier|public
specifier|static
specifier|final
name|String
name|SKIP
init|=
literal|"skip"
decl_stmt|;
block|}
end_class
end_unit
