begin_unit
begin_package
DECL|package|org.apache.solr.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
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
name|IOException
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
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|BinaryResponseParser
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
name|impl
operator|.
name|HttpSolrClient
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
name|request
operator|.
name|GenericSolrRequest
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
name|response
operator|.
name|SimpleSolrResponse
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
name|params
operator|.
name|ModifiableSolrParams
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
name|update
operator|.
name|UpdateShardHandler
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
name|CoreAdminParams
operator|.
name|ACTION
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
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|INVOKE
import|;
end_import
begin_comment
comment|/**  * This is the context provided to the snitches to interact with the system. This is a per-node-per-snitch  * instance.  */
end_comment
begin_class
DECL|class|SnitchContext
specifier|public
class|class
name|SnitchContext
implements|implements
name|RemoteCallback
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SnitchContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tags
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|node
specifier|private
name|String
name|node
decl_stmt|;
DECL|field|snitchInfo
specifier|final
name|SnitchInfo
name|snitchInfo
decl_stmt|;
DECL|field|exception
name|Exception
name|exception
decl_stmt|;
DECL|method|SnitchContext
name|SnitchContext
parameter_list|(
name|SnitchInfo
name|perSnitch
parameter_list|,
name|String
name|node
parameter_list|)
block|{
name|this
operator|.
name|snitchInfo
operator|=
name|perSnitch
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
DECL|method|getSnitchInfo
specifier|public
name|SnitchInfo
name|getSnitchInfo
parameter_list|()
block|{
return|return
name|snitchInfo
return|;
block|}
DECL|method|getTags
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getTags
parameter_list|()
block|{
return|return
name|tags
return|;
block|}
DECL|method|getNode
specifier|public
name|String
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
comment|/**    * make a call to solrnode/admin/cores with the given params and give a callback. This is designed to be    * asynchronous because the system would want to batch the calls made to any given node    *    * @param node     The node for which this call is made    * @param params   The params to be passed to the Snitch counterpart    * @param klas     The  name of the class to be invoked in the remote node    * @param callback The callback to be called when the response is obtained from remote node.    *                 If this is passed as null the entire response map will be added as tags    */
DECL|method|invokeRemote
specifier|public
name|void
name|invokeRemote
parameter_list|(
name|String
name|node
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|String
name|klas
parameter_list|,
name|RemoteCallback
name|callback
parameter_list|)
block|{
if|if
condition|(
name|callback
operator|==
literal|null
condition|)
name|callback
operator|=
name|this
expr_stmt|;
name|String
name|url
init|=
name|snitchInfo
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"class"
argument_list|,
name|klas
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|ACTION
argument_list|,
name|INVOKE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//todo batch all requests to the same server
try|try
block|{
name|SimpleSolrResponse
name|rsp
init|=
name|invoke
argument_list|(
name|snitchInfo
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getUpdateShardHandler
argument_list|()
argument_list|,
name|url
argument_list|,
name|CommonParams
operator|.
name|CORES_HANDLER_PATH
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|returnedVal
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|rsp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
name|klas
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
comment|//        log this
block|}
else|else
block|{
name|callback
operator|.
name|remoteCallback
argument_list|(
name|SnitchContext
operator|.
name|this
argument_list|,
name|returnedVal
argument_list|)
expr_stmt|;
block|}
name|callback
operator|.
name|remoteCallback
argument_list|(
name|this
argument_list|,
name|returnedVal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to invoke snitch counterpart"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
DECL|method|invoke
specifier|public
name|SimpleSolrResponse
name|invoke
parameter_list|(
name|UpdateShardHandler
name|shardHandler
parameter_list|,
specifier|final
name|String
name|url
parameter_list|,
name|String
name|path
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|GenericSolrRequest
name|request
init|=
operator|new
name|GenericSolrRequest
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|GET
argument_list|,
name|path
argument_list|,
name|params
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url
argument_list|,
name|shardHandler
operator|.
name|getHttpClient
argument_list|()
argument_list|,
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
init|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|request
operator|.
name|response
operator|.
name|nl
operator|=
name|rsp
expr_stmt|;
return|return
name|request
operator|.
name|response
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|remoteCallback
specifier|public
name|void
name|remoteCallback
parameter_list|(
name|SnitchContext
name|ctx
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|returnedVal
parameter_list|)
block|{
name|tags
operator|.
name|putAll
argument_list|(
name|returnedVal
argument_list|)
expr_stmt|;
block|}
DECL|method|getErrMsg
specifier|public
name|String
name|getErrMsg
parameter_list|()
block|{
return|return
name|exception
operator|==
literal|null
condition|?
literal|null
else|:
name|exception
operator|.
name|getMessage
argument_list|()
return|;
block|}
DECL|class|SnitchInfo
specifier|public
specifier|static
specifier|abstract
class|class
name|SnitchInfo
block|{
DECL|field|conf
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|conf
decl_stmt|;
DECL|method|SnitchInfo
name|SnitchInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|getTagNames
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getTagNames
parameter_list|()
function_decl|;
DECL|method|getCoreContainer
specifier|public
specifier|abstract
name|CoreContainer
name|getCoreContainer
parameter_list|()
function_decl|;
block|}
block|}
end_class
end_unit
