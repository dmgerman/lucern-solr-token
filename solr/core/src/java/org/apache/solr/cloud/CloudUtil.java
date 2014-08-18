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
name|File
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|Replica
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
name|Slice
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
name|core
operator|.
name|CoreContainer
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
name|CoreDescriptor
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
begin_class
DECL|class|CloudUtil
specifier|public
class|class
name|CloudUtil
block|{
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
name|CloudUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * See if coreNodeName has been taken over by another baseUrl and unload core    * + throw exception if it has been.    */
DECL|method|checkSharedFSFailoverReplaced
specifier|public
specifier|static
name|void
name|checkSharedFSFailoverReplaced
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|desc
parameter_list|)
block|{
name|ZkController
name|zkController
init|=
name|cc
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|String
name|thisCnn
init|=
name|zkController
operator|.
name|getCoreNodeName
argument_list|(
name|desc
argument_list|)
decl_stmt|;
name|String
name|thisBaseUrl
init|=
name|zkController
operator|.
name|getBaseUrl
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"checkSharedFSFailoverReplaced running for coreNodeName={} baseUrl={}"
argument_list|,
name|thisCnn
argument_list|,
name|thisBaseUrl
argument_list|)
expr_stmt|;
comment|// if we see our core node name on a different base url, unload
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slicesMap
init|=
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlicesMap
argument_list|(
name|desc
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|slicesMap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|slicesMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|String
name|cnn
init|=
name|replica
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|baseUrl
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"compare against coreNodeName={} baseUrl={}"
argument_list|,
name|cnn
argument_list|,
name|baseUrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|thisCnn
operator|!=
literal|null
operator|&&
name|thisCnn
operator|.
name|equals
argument_list|(
name|cnn
argument_list|)
operator|&&
operator|!
name|thisBaseUrl
operator|.
name|equals
argument_list|(
name|baseUrl
argument_list|)
condition|)
block|{
if|if
condition|(
name|cc
operator|.
name|getCoreNames
argument_list|()
operator|.
name|contains
argument_list|(
name|desc
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|cc
operator|.
name|unload
argument_list|(
name|desc
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|instanceDir
init|=
operator|new
name|File
argument_list|(
name|desc
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Failed to delete instance dir for core:"
operator|+
name|desc
operator|.
name|getName
argument_list|()
operator|+
literal|" dir:"
operator|+
name|instanceDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Will not load SolrCore "
operator|+
name|desc
operator|.
name|getName
argument_list|()
operator|+
literal|" because it has been replaced due to failover."
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Will not load SolrCore "
operator|+
name|desc
operator|.
name|getName
argument_list|()
operator|+
literal|" because it has been replaced due to failover."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class
end_unit