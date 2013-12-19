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
name|io
operator|.
name|IOException
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
name|Map
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
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
name|org
operator|.
name|kitesdk
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
name|org
operator|.
name|kitesdk
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
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineRuntimeException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
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
name|org
operator|.
name|kitesdk
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
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Configs
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Metrics
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Notifications
import|;
end_import
begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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
begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|ConfigFactory
import|;
end_import
begin_comment
comment|/**  * A command that loads a record into a SolrServer or MapReduce SolrOutputFormat.  */
end_comment
begin_class
DECL|class|LoadSolrBuilder
specifier|public
specifier|final
class|class
name|LoadSolrBuilder
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
literal|"loadSolr"
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
name|LoadSolr
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
DECL|class|LoadSolr
specifier|private
specifier|static
specifier|final
class|class
name|LoadSolr
extends|extends
name|AbstractCommand
block|{
DECL|field|loader
specifier|private
specifier|final
name|DocumentLoader
name|loader
decl_stmt|;
DECL|field|boosts
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|boosts
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|elapsedTime
specifier|private
specifier|final
name|Timer
name|elapsedTime
decl_stmt|;
DECL|method|LoadSolr
specifier|public
name|LoadSolr
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
name|loader
operator|=
name|locator
operator|.
name|getLoader
argument_list|()
expr_stmt|;
name|Config
name|boostsConfig
init|=
name|getConfigs
argument_list|()
operator|.
name|getConfig
argument_list|(
name|config
argument_list|,
literal|"boosts"
argument_list|,
name|ConfigFactory
operator|.
name|empty
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
name|Object
argument_list|>
name|entry
range|:
operator|new
name|Configs
argument_list|()
operator|.
name|getEntrySet
argument_list|(
name|boostsConfig
argument_list|)
control|)
block|{
name|String
name|fieldName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|float
name|boost
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|boosts
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
name|validateArguments
argument_list|()
expr_stmt|;
name|this
operator|.
name|elapsedTime
operator|=
name|getTimer
argument_list|(
name|Metrics
operator|.
name|ELAPSED_TIME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doNotify
specifier|protected
name|void
name|doNotify
parameter_list|(
name|Record
name|notification
parameter_list|)
block|{
for|for
control|(
name|Object
name|event
range|:
name|Notifications
operator|.
name|getLifecycleEvents
argument_list|(
name|notification
argument_list|)
control|)
block|{
if|if
condition|(
name|event
operator|==
name|Notifications
operator|.
name|LifecycleEvent
operator|.
name|BEGIN_TRANSACTION
condition|)
block|{
try|try
block|{
name|loader
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|Notifications
operator|.
name|LifecycleEvent
operator|.
name|COMMIT_TRANSACTION
condition|)
block|{
try|try
block|{
name|loader
operator|.
name|commitTransaction
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|Notifications
operator|.
name|LifecycleEvent
operator|.
name|ROLLBACK_TRANSACTION
condition|)
block|{
try|try
block|{
name|loader
operator|.
name|rollbackTransaction
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|Notifications
operator|.
name|LifecycleEvent
operator|.
name|SHUTDOWN
condition|)
block|{
try|try
block|{
name|loader
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
name|super
operator|.
name|doNotify
argument_list|(
name|notification
argument_list|)
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
name|Timer
operator|.
name|Context
name|timerContext
init|=
name|elapsedTime
operator|.
name|time
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|convert
argument_list|(
name|record
argument_list|)
decl_stmt|;
try|try
block|{
name|loader
operator|.
name|load
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|timerContext
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// pass record to next command in chain:
return|return
name|super
operator|.
name|doProcess
argument_list|(
name|record
argument_list|)
return|;
block|}
DECL|method|convert
specifier|private
name|SolrInputDocument
name|convert
parameter_list|(
name|Record
name|record
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
argument_list|>
name|map
init|=
name|record
operator|.
name|getFields
argument_list|()
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|(
operator|new
name|HashMap
argument_list|(
literal|2
operator|*
name|map
operator|.
name|size
argument_list|()
argument_list|)
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
name|map
operator|.
name|entrySet
argument_list|()
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
name|doc
operator|.
name|setField
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|getBoost
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|getBoost
specifier|private
name|float
name|getBoost
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|boosts
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Float
name|boost
init|=
name|boosts
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
return|return
name|boost
operator|.
name|floatValue
argument_list|()
return|;
block|}
block|}
return|return
literal|1.0f
return|;
block|}
block|}
block|}
end_class
end_unit
