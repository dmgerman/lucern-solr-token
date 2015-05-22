begin_unit
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
name|params
operator|.
name|SolrParams
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
name|apache
operator|.
name|zookeeper
operator|.
name|WatchedEvent
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
name|Watcher
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_comment
comment|/**  * Manage the state of the update log buffer. It is responsible of synchronising the state  * through Zookeeper. The state of the buffer is stored in the zk node defined by {@link #getZnodePath()}.  */
end_comment
begin_class
DECL|class|CdcrBufferStateManager
class|class
name|CdcrBufferStateManager
extends|extends
name|CdcrStateManager
block|{
DECL|field|state
specifier|private
name|CdcrParams
operator|.
name|BufferState
name|state
init|=
name|DEFAULT_STATE
decl_stmt|;
DECL|field|wrappedWatcher
specifier|private
name|BufferStateWatcher
name|wrappedWatcher
decl_stmt|;
DECL|field|watcher
specifier|private
name|Watcher
name|watcher
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|field|DEFAULT_STATE
specifier|static
name|CdcrParams
operator|.
name|BufferState
name|DEFAULT_STATE
init|=
name|CdcrParams
operator|.
name|BufferState
operator|.
name|ENABLED
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CdcrBufferStateManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|CdcrBufferStateManager
name|CdcrBufferStateManager
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|,
name|SolrParams
name|bufferConfiguration
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
comment|// Ensure that the state znode exists
name|this
operator|.
name|createStateNode
argument_list|()
expr_stmt|;
comment|// set default state
if|if
condition|(
name|bufferConfiguration
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|defaultState
init|=
name|bufferConfiguration
operator|.
name|get
argument_list|(
name|CdcrParams
operator|.
name|DEFAULT_STATE_PARAM
argument_list|,
name|DEFAULT_STATE
operator|.
name|toLower
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|state
operator|=
name|CdcrParams
operator|.
name|BufferState
operator|.
name|get
argument_list|(
name|defaultState
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|// notify observers
comment|// Startup and register the watcher at startup
try|try
block|{
name|SolrZkClient
name|zkClient
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|watcher
operator|=
name|this
operator|.
name|initWatcher
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|setState
argument_list|(
name|CdcrParams
operator|.
name|BufferState
operator|.
name|get
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
name|this
operator|.
name|getZnodePath
argument_list|()
argument_list|,
name|watcher
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
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
name|log
operator|.
name|warn
argument_list|(
literal|"Failed fetching initial state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * SolrZkClient does not guarantee that a watch object will only be triggered once for a given notification    * if we does not wrap the watcher - see SOLR-6621.    */
DECL|method|initWatcher
specifier|private
name|Watcher
name|initWatcher
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|)
block|{
name|wrappedWatcher
operator|=
operator|new
name|BufferStateWatcher
argument_list|()
expr_stmt|;
return|return
name|zkClient
operator|.
name|wrapWatcher
argument_list|(
name|wrappedWatcher
argument_list|)
return|;
block|}
DECL|method|getZnodeBase
specifier|private
name|String
name|getZnodeBase
parameter_list|()
block|{
return|return
literal|"/collections/"
operator|+
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
operator|+
literal|"/cdcr/state"
return|;
block|}
DECL|method|getZnodePath
specifier|private
name|String
name|getZnodePath
parameter_list|()
block|{
return|return
name|getZnodeBase
argument_list|()
operator|+
literal|"/buffer"
return|;
block|}
DECL|method|setState
name|void
name|setState
parameter_list|(
name|CdcrParams
operator|.
name|BufferState
name|state
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|state
operator|!=
name|state
condition|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|callback
argument_list|()
expr_stmt|;
comment|// notify the observers of a state change
block|}
block|}
DECL|method|getState
name|CdcrParams
operator|.
name|BufferState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Synchronise the state to Zookeeper. This method must be called only by the handler receiving the    * action.    */
DECL|method|synchronize
name|void
name|synchronize
parameter_list|()
block|{
name|SolrZkClient
name|zkClient
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
try|try
block|{
name|zkClient
operator|.
name|setData
argument_list|(
name|this
operator|.
name|getZnodePath
argument_list|()
argument_list|,
name|this
operator|.
name|getState
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// check if nobody changed it in the meantime, and set a new watcher
name|this
operator|.
name|setState
argument_list|(
name|CdcrParams
operator|.
name|BufferState
operator|.
name|get
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
name|this
operator|.
name|getZnodePath
argument_list|()
argument_list|,
name|watcher
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
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
name|log
operator|.
name|warn
argument_list|(
literal|"Failed synchronising new state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createStateNode
specifier|private
name|void
name|createStateNode
parameter_list|()
block|{
name|SolrZkClient
name|zkClient
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
name|this
operator|.
name|getZnodePath
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
name|this
operator|.
name|getZnodeBase
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|this
operator|.
name|getZnodeBase
argument_list|()
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|zkClient
operator|.
name|create
argument_list|(
name|this
operator|.
name|getZnodePath
argument_list|()
argument_list|,
name|DEFAULT_STATE
operator|.
name|getBytes
argument_list|()
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created znode {}"
argument_list|,
name|this
operator|.
name|getZnodePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to create CDCR buffer state node"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutdown
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|wrappedWatcher
operator|!=
literal|null
condition|)
block|{
name|wrappedWatcher
operator|.
name|cancel
argument_list|()
expr_stmt|;
comment|// cancel the watcher to avoid spurious warn messages during shutdown
block|}
block|}
DECL|class|BufferStateWatcher
specifier|private
class|class
name|BufferStateWatcher
implements|implements
name|Watcher
block|{
DECL|field|isCancelled
specifier|private
name|boolean
name|isCancelled
init|=
literal|false
decl_stmt|;
comment|/**      * Cancel the watcher to avoid spurious warn messages during shutdown.      */
DECL|method|cancel
name|void
name|cancel
parameter_list|()
block|{
name|isCancelled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|isCancelled
condition|)
return|return;
comment|// if the watcher is cancelled, do nothing.
name|String
name|collectionName
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|String
name|shard
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getShardId
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"The CDCR buffer state has changed: {} @ {}:{}"
argument_list|,
name|event
argument_list|,
name|collectionName
argument_list|,
name|shard
argument_list|)
expr_stmt|;
if|if
condition|(
name|Event
operator|.
name|EventType
operator|.
name|None
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|SolrZkClient
name|zkClient
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
try|try
block|{
name|CdcrParams
operator|.
name|BufferState
name|state
init|=
name|CdcrParams
operator|.
name|BufferState
operator|.
name|get
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
name|CdcrBufferStateManager
operator|.
name|this
operator|.
name|getZnodePath
argument_list|()
argument_list|,
name|watcher
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Received new CDCR buffer state from watcher: {} @ {}:{}"
argument_list|,
name|state
argument_list|,
name|collectionName
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|CdcrBufferStateManager
operator|.
name|this
operator|.
name|setState
argument_list|(
name|state
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
name|log
operator|.
name|warn
argument_list|(
literal|"Failed synchronising new state @ "
operator|+
name|collectionName
operator|+
literal|":"
operator|+
name|shard
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
