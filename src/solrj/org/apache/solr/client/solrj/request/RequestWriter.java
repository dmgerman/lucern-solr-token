begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
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
name|common
operator|.
name|util
operator|.
name|ContentStream
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
name|Collection
import|;
end_import
begin_comment
comment|/**  * A RequestWriter is used to write requests to Solr.  *<p/>  * A subclass can override the methods in this class to supply a custom format in which a request can be sent.  * @since solr 1.4  * @version $Id$  */
end_comment
begin_class
DECL|class|RequestWriter
specifier|public
class|class
name|RequestWriter
block|{
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|(
name|SolrRequest
name|req
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|req
operator|.
name|getContentStreams
argument_list|()
return|;
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|(
name|SolrRequest
name|req
parameter_list|)
block|{
return|return
name|req
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
end_class
end_unit
