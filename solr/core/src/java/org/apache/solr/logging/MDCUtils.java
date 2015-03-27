begin_unit
begin_package
DECL|package|org.apache.solr.logging
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
package|;
end_package
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
name|slf4j
operator|.
name|MDC
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|COLLECTION_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|REPLICA_PROP
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|MDCUtils
specifier|public
class|class
name|MDCUtils
block|{
DECL|method|cleanupMDC
specifier|public
specifier|static
name|void
name|cleanupMDC
parameter_list|(
name|Map
name|previousMDCContext
parameter_list|)
block|{
if|if
condition|(
name|previousMDCContext
operator|!=
literal|null
condition|)
name|MDC
operator|.
name|setContextMap
argument_list|(
name|previousMDCContext
argument_list|)
expr_stmt|;
block|}
DECL|method|setMDC
specifier|public
specifier|static
name|void
name|setMDC
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shard
parameter_list|,
name|String
name|replica
parameter_list|,
name|String
name|core
parameter_list|)
block|{
name|setCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|setShard
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|setReplica
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|setCore
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
DECL|method|setCollection
specifier|public
specifier|static
name|void
name|setCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|MDC
operator|.
name|put
argument_list|(
name|COLLECTION_PROP
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
DECL|method|setShard
specifier|public
specifier|static
name|void
name|setShard
parameter_list|(
name|String
name|shard
parameter_list|)
block|{
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
name|MDC
operator|.
name|put
argument_list|(
name|SHARD_ID_PROP
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
DECL|method|setReplica
specifier|public
specifier|static
name|void
name|setReplica
parameter_list|(
name|String
name|replica
parameter_list|)
block|{
if|if
condition|(
name|replica
operator|!=
literal|null
condition|)
name|MDC
operator|.
name|put
argument_list|(
name|REPLICA_PROP
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
DECL|method|setCore
specifier|public
specifier|static
name|void
name|setCore
parameter_list|(
name|String
name|core
parameter_list|)
block|{
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
name|MDC
operator|.
name|put
argument_list|(
name|CORE_NAME_PROP
argument_list|,
name|core
argument_list|)
expr_stmt|;
block|}
DECL|method|clearMDC
specifier|public
specifier|static
name|void
name|clearMDC
parameter_list|()
block|{
name|MDC
operator|.
name|remove
argument_list|(
name|COLLECTION_PROP
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|remove
argument_list|(
name|CORE_NAME_PROP
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|remove
argument_list|(
name|REPLICA_PROP
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|remove
argument_list|(
name|SHARD_ID_PROP
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit