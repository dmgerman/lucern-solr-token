begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
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
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Set
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
name|SolrResponse
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
name|cloud
operator|.
name|SolrZkClient
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
name|cloud
operator|.
name|ZkConfigManager
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
name|cloud
operator|.
name|ZkNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|ConfigSetParams
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
name|NamedList
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
name|core
operator|.
name|ConfigSetProperties
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
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
name|cloud
operator|.
name|OverseerMessageHandler
operator|.
name|ExclusiveMarking
operator|.
name|NONEXCLUSIVE
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
name|cloud
operator|.
name|OverseerMessageHandler
operator|.
name|ExclusiveMarking
operator|.
name|NOTDETERMINED
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
name|ConfigSetParams
operator|.
name|ConfigSetAction
operator|.
name|CREATE
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
name|ConfigSetParams
operator|.
name|ConfigSetAction
operator|.
name|DELETE
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
name|NAME
import|;
end_import
begin_comment
comment|/**  * A {@link OverseerMessageHandler} that handles ConfigSets API related  * overseer messages.  */
end_comment
begin_class
DECL|class|OverseerConfigSetMessageHandler
specifier|public
class|class
name|OverseerConfigSetMessageHandler
implements|implements
name|OverseerMessageHandler
block|{
comment|/**    * Prefix to specify an action should be handled by this handler.    */
DECL|field|CONFIGSETS_ACTION_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGSETS_ACTION_PREFIX
init|=
literal|"configsets:"
decl_stmt|;
comment|/**    * Name of the ConfigSet to copy from for CREATE    */
DECL|field|BASE_CONFIGSET
specifier|public
specifier|static
specifier|final
name|String
name|BASE_CONFIGSET
init|=
literal|"baseConfigSet"
decl_stmt|;
comment|/**    * Prefix for properties that should be applied to the ConfigSet for CREATE    */
DECL|field|PROPERTY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_PREFIX
init|=
literal|"configSetProp"
decl_stmt|;
DECL|field|zkStateReader
specifier|private
name|ZkStateReader
name|zkStateReader
decl_stmt|;
comment|// we essentially implement a read/write lock for the ConfigSet exclusivity as follows:
comment|// WRITE: CREATE/DELETE on the ConfigSet under operation
comment|// READ: for the Base ConfigSet being copied in CREATE.
comment|// in this way, we prevent a Base ConfigSet from being deleted while it is being copied
comment|// but don't prevent different ConfigSets from being created with the same Base ConfigSet
comment|// at the same time.
DECL|field|configSetWriteWip
specifier|final
specifier|private
name|Set
name|configSetWriteWip
decl_stmt|;
DECL|field|configSetReadWip
specifier|final
specifier|private
name|Set
name|configSetReadWip
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OverseerConfigSetMessageHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OverseerConfigSetMessageHandler
specifier|public
name|OverseerConfigSetMessageHandler
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|)
block|{
name|this
operator|.
name|zkStateReader
operator|=
name|zkStateReader
expr_stmt|;
name|this
operator|.
name|configSetWriteWip
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|this
operator|.
name|configSetReadWip
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processMessage
specifier|public
name|SolrResponse
name|processMessage
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|,
name|String
name|operation
parameter_list|)
block|{
name|NamedList
name|results
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|operation
operator|.
name|startsWith
argument_list|(
name|CONFIGSETS_ACTION_PREFIX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Operation does not contain proper prefix: "
operator|+
name|operation
operator|+
literal|" expected: "
operator|+
name|CONFIGSETS_ACTION_PREFIX
argument_list|)
throw|;
block|}
name|operation
operator|=
name|operation
operator|.
name|substring
argument_list|(
name|CONFIGSETS_ACTION_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"OverseerConfigSetMessageHandler.processMessage : "
operator|+
name|operation
operator|+
literal|" , "
operator|+
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigSetParams
operator|.
name|ConfigSetAction
name|action
init|=
name|ConfigSetParams
operator|.
name|ConfigSetAction
operator|.
name|get
argument_list|(
name|operation
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown operation:"
operator|+
name|operation
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|CREATE
case|:
name|createConfigSet
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|deleteConfigSet
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown operation:"
operator|+
name|operation
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|configSetName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|configSetName
operator|==
literal|null
condition|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Operation "
operator|+
name|operation
operator|+
literal|" failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"ConfigSet: "
operator|+
name|configSetName
operator|+
literal|" operation: "
operator|+
name|operation
operator|+
literal|" failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|results
operator|.
name|add
argument_list|(
literal|"Operation "
operator|+
name|operation
operator|+
literal|" caused exception:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
name|nl
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"msg"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"rspCode"
argument_list|,
name|e
operator|instanceof
name|SolrException
condition|?
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|code
argument_list|()
else|:
operator|-
literal|1
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
literal|"exception"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|OverseerSolrResponse
argument_list|(
name|results
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Overseer ConfigSet Message Handler"
return|;
block|}
annotation|@
name|Override
DECL|method|getTimerName
specifier|public
name|String
name|getTimerName
parameter_list|(
name|String
name|operation
parameter_list|)
block|{
return|return
literal|"configset_"
operator|+
name|operation
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskKey
specifier|public
name|String
name|getTaskKey
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|)
block|{
return|return
name|message
operator|.
name|getStr
argument_list|(
name|NAME
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|markExclusiveTask
specifier|public
name|void
name|markExclusiveTask
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|String
name|baseConfigSet
init|=
name|getBaseConfigSetIfCreate
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|markExclusive
argument_list|(
name|configSetName
argument_list|,
name|baseConfigSet
argument_list|)
expr_stmt|;
block|}
DECL|method|markExclusive
specifier|private
name|void
name|markExclusive
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|String
name|baseConfigSetName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|configSetWriteWip
init|)
block|{
name|configSetWriteWip
operator|.
name|add
argument_list|(
name|configSetName
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseConfigSetName
operator|!=
literal|null
condition|)
name|configSetReadWip
operator|.
name|add
argument_list|(
name|baseConfigSetName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|unmarkExclusiveTask
specifier|public
name|void
name|unmarkExclusiveTask
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|String
name|operation
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|String
name|baseConfigSet
init|=
name|getBaseConfigSetIfCreate
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|unmarkExclusiveConfigSet
argument_list|(
name|configSetName
argument_list|,
name|baseConfigSet
argument_list|)
expr_stmt|;
block|}
DECL|method|unmarkExclusiveConfigSet
specifier|private
name|void
name|unmarkExclusiveConfigSet
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|String
name|baseConfigSetName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|configSetWriteWip
init|)
block|{
name|configSetWriteWip
operator|.
name|remove
argument_list|(
name|configSetName
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseConfigSetName
operator|!=
literal|null
condition|)
name|configSetReadWip
operator|.
name|remove
argument_list|(
name|baseConfigSetName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkExclusiveMarking
specifier|public
name|ExclusiveMarking
name|checkExclusiveMarking
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|String
name|baseConfigSet
init|=
name|getBaseConfigSetIfCreate
argument_list|(
name|message
argument_list|)
decl_stmt|;
return|return
name|checkExclusiveMarking
argument_list|(
name|configSetName
argument_list|,
name|baseConfigSet
argument_list|)
return|;
block|}
DECL|method|checkExclusiveMarking
specifier|private
name|ExclusiveMarking
name|checkExclusiveMarking
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|String
name|baseConfigSetName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|configSetWriteWip
init|)
block|{
comment|// need to acquire:
comment|// 1) write lock on ConfigSet
comment|// 2) read lock on Base ConfigSet
if|if
condition|(
name|configSetWriteWip
operator|.
name|contains
argument_list|(
name|configSetName
argument_list|)
operator|||
name|configSetReadWip
operator|.
name|contains
argument_list|(
name|configSetName
argument_list|)
condition|)
block|{
return|return
name|NONEXCLUSIVE
return|;
block|}
if|if
condition|(
name|baseConfigSetName
operator|!=
literal|null
operator|&&
name|configSetWriteWip
operator|.
name|contains
argument_list|(
name|baseConfigSetName
argument_list|)
condition|)
block|{
return|return
name|NONEXCLUSIVE
return|;
block|}
block|}
return|return
name|NOTDETERMINED
return|;
block|}
DECL|method|getBaseConfigSetIfCreate
specifier|private
name|String
name|getBaseConfigSetIfCreate
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|String
name|operation
init|=
name|message
operator|.
name|getStr
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|operation
operator|!=
literal|null
condition|)
block|{
name|operation
operator|=
name|operation
operator|.
name|substring
argument_list|(
name|CONFIGSETS_ACTION_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigSetParams
operator|.
name|ConfigSetAction
name|action
init|=
name|ConfigSetParams
operator|.
name|ConfigSetAction
operator|.
name|get
argument_list|(
name|operation
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|==
name|CREATE
condition|)
block|{
return|return
name|message
operator|.
name|getStr
argument_list|(
name|BASE_CONFIGSET
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getConfigSetProperties
specifier|private
name|NamedList
name|getConfigSetProperties
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|oldPropsData
init|=
literal|null
decl_stmt|;
try|try
block|{
name|oldPropsData
operator|=
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"no existing ConfigSet properties found"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error reading old properties"
argument_list|,
name|SolrZkClient
operator|.
name|checkInterrupted
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|oldPropsData
operator|!=
literal|null
condition|)
block|{
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|oldPropsData
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|ConfigSetProperties
operator|.
name|readFromInputStream
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getNewProperties
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getNewProperties
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
init|=
literal|null
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
name|message
operator|.
name|getProperties
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PROPERTY_PREFIX
operator|+
literal|"."
argument_list|)
condition|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|properties
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
operator|(
name|PROPERTY_PREFIX
operator|+
literal|"."
operator|)
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|properties
return|;
block|}
DECL|method|mergeOldProperties
specifier|private
name|void
name|mergeOldProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newProps
parameter_list|,
name|NamedList
name|oldProps
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|it
init|=
name|oldProps
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|oldEntry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|newProps
operator|.
name|containsKey
argument_list|(
name|oldEntry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|newProps
operator|.
name|put
argument_list|(
name|oldEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|oldEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPropertyData
specifier|private
name|byte
index|[]
name|getPropertyData
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newProps
parameter_list|)
block|{
if|if
condition|(
name|newProps
operator|!=
literal|null
condition|)
block|{
name|String
name|propertyDataStr
init|=
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|newProps
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyDataStr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid property specification"
argument_list|)
throw|;
block|}
return|return
name|propertyDataStr
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getPropertyPath
specifier|private
name|String
name|getPropertyPath
parameter_list|(
name|String
name|configName
parameter_list|,
name|String
name|propertyPath
parameter_list|)
block|{
return|return
name|ZkConfigManager
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|configName
operator|+
literal|"/"
operator|+
name|propertyPath
return|;
block|}
DECL|method|createConfigSet
specifier|private
name|void
name|createConfigSet
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|configSetName
init|=
name|getTaskKey
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|configSetName
operator|==
literal|null
operator|||
name|configSetName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ConfigSet name not specified"
argument_list|)
throw|;
block|}
name|String
name|baseConfigSetName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|BASE_CONFIGSET
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseConfigSetName
operator|==
literal|null
operator|||
name|baseConfigSetName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Base ConfigSet name not specified"
argument_list|)
throw|;
block|}
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|configManager
operator|.
name|configExists
argument_list|(
name|configSetName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ConfigSet already exists: "
operator|+
name|configSetName
argument_list|)
throw|;
block|}
comment|// is there a base config that already exists
if|if
condition|(
operator|!
name|configManager
operator|.
name|configExists
argument_list|(
name|baseConfigSetName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Base ConfigSet does not exist: "
operator|+
name|baseConfigSetName
argument_list|)
throw|;
block|}
name|String
name|propertyPath
init|=
name|ConfigSetProperties
operator|.
name|DEFAULT_FILENAME
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|getNewProperties
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
comment|// read the old config properties and do a merge, if necessary
name|NamedList
name|oldProps
init|=
name|getConfigSetProperties
argument_list|(
name|getPropertyPath
argument_list|(
name|baseConfigSetName
argument_list|,
name|propertyPath
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldProps
operator|!=
literal|null
condition|)
block|{
name|mergeOldProperties
argument_list|(
name|props
argument_list|,
name|oldProps
argument_list|)
expr_stmt|;
block|}
block|}
name|byte
index|[]
name|propertyData
init|=
name|getPropertyData
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|copiedToZkPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|configManager
operator|.
name|copyConfigDir
argument_list|(
name|baseConfigSetName
argument_list|,
name|configSetName
argument_list|,
name|copiedToZkPaths
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyData
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|makePath
argument_list|(
name|getPropertyPath
argument_list|(
name|configSetName
argument_list|,
name|propertyPath
argument_list|)
argument_list|,
name|propertyData
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error writing new properties"
argument_list|,
name|SolrZkClient
operator|.
name|checkInterrupted
argument_list|(
name|e
argument_list|)
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
comment|// copying the config dir or writing the properties file may have failed.
comment|// we should delete the ConfigSet because it may be invalid,
comment|// assuming we actually wrote something.  E.g. could be
comment|// the entire baseConfig set with the old properties, including immutable,
comment|// that would make it impossible for the user to delete.
try|try
block|{
if|if
condition|(
name|configManager
operator|.
name|configExists
argument_list|(
name|configSetName
argument_list|)
operator|&&
name|copiedToZkPaths
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|deleteConfigSet
argument_list|(
name|configSetName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while trying to delete partially created ConfigSet"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|deleteConfigSet
specifier|private
name|void
name|deleteConfigSet
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|configSetName
init|=
name|getTaskKey
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|configSetName
operator|==
literal|null
operator|||
name|configSetName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ConfigSet name not specified"
argument_list|)
throw|;
block|}
name|deleteConfigSet
argument_list|(
name|configSetName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteConfigSet
specifier|private
name|void
name|deleteConfigSet
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|configManager
operator|.
name|configExists
argument_list|(
name|configSetName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ConfigSet does not exist to delete: "
operator|+
name|configSetName
argument_list|)
throw|;
block|}
name|String
name|propertyPath
init|=
name|ConfigSetProperties
operator|.
name|DEFAULT_FILENAME
decl_stmt|;
name|NamedList
name|properties
init|=
name|getConfigSetProperties
argument_list|(
name|getPropertyPath
argument_list|(
name|configSetName
argument_list|,
name|propertyPath
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
name|Object
name|immutable
init|=
name|properties
operator|.
name|get
argument_list|(
name|ConfigSetProperties
operator|.
name|IMMUTABLE_CONFIGSET_ARG
argument_list|)
decl_stmt|;
name|boolean
name|isImmutableConfigSet
init|=
name|immutable
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|immutable
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|force
operator|&&
name|isImmutableConfigSet
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Requested delete of immutable ConfigSet: "
operator|+
name|configSetName
argument_list|)
throw|;
block|}
block|}
name|configManager
operator|.
name|deleteConfigDir
argument_list|(
name|configSetName
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit