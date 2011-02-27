begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|XML
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
name|XMLErrorLogger
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
name|LocalSolrQueryRequest
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|processor
operator|.
name|UpdateRequestProcessorChain
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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
comment|/**  * Add documents to solr using the STAX XML parser.  */
end_comment
begin_class
DECL|class|XmlUpdateRequestHandler
specifier|public
class|class
name|XmlUpdateRequestHandler
extends|extends
name|ContentStreamHandlerBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|xmllog
specifier|private
specifier|static
specifier|final
name|XMLErrorLogger
name|xmllog
init|=
operator|new
name|XMLErrorLogger
argument_list|(
name|log
argument_list|)
decl_stmt|;
DECL|field|UPDATE_PROCESSOR
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_PROCESSOR
init|=
literal|"update.processor"
decl_stmt|;
comment|// XML Constants
DECL|field|ADD
specifier|public
specifier|static
specifier|final
name|String
name|ADD
init|=
literal|"add"
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|String
name|DELETE
init|=
literal|"delete"
decl_stmt|;
DECL|field|OPTIMIZE
specifier|public
specifier|static
specifier|final
name|String
name|OPTIMIZE
init|=
literal|"optimize"
decl_stmt|;
DECL|field|COMMIT
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT
init|=
literal|"commit"
decl_stmt|;
DECL|field|ROLLBACK
specifier|public
specifier|static
specifier|final
name|String
name|ROLLBACK
init|=
literal|"rollback"
decl_stmt|;
DECL|field|WAIT_SEARCHER
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_SEARCHER
init|=
literal|"waitSearcher"
decl_stmt|;
DECL|field|WAIT_FLUSH
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_FLUSH
init|=
literal|"waitFlush"
decl_stmt|;
DECL|field|OVERWRITE
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE
init|=
literal|"overwrite"
decl_stmt|;
DECL|field|COMMIT_WITHIN
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_WITHIN
init|=
literal|"commitWithin"
decl_stmt|;
DECL|field|inputFactory
name|XMLInputFactory
name|inputFactory
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inputFactory
operator|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
try|try
block|{
comment|// The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
comment|// XMLInputFactory, as that implementation tries to cache and reuse the
comment|// XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
comment|// prevents this.
comment|// All other known open-source stax parsers (and the bea ref impl)
comment|// have thread-safe factories.
name|inputFactory
operator|.
name|setProperty
argument_list|(
literal|"reuse-instance"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Other implementations will likely throw this exception since "reuse-instance"
comment|// isimplementation specific.
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to set the 'reuse-instance' property for the input chain: "
operator|+
name|inputFactory
argument_list|)
expr_stmt|;
block|}
name|inputFactory
operator|.
name|setXMLReporter
argument_list|(
name|xmllog
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newLoader
specifier|protected
name|ContentStreamLoader
name|newLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
return|return
operator|new
name|XMLLoader
argument_list|(
name|processor
argument_list|,
name|inputFactory
argument_list|)
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Add documents with XML"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
end_unit
