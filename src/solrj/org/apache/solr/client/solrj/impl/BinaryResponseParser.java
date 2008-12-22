begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
name|impl
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
name|ResponseParser
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
name|JavaBinCodec
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
name|InputStream
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
begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|BinaryResponseParser
specifier|public
class|class
name|BinaryResponseParser
extends|extends
name|ResponseParser
block|{
DECL|method|getWriterType
specifier|public
name|String
name|getWriterType
parameter_list|()
block|{
return|return
literal|"javabin"
return|;
block|}
DECL|method|processResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|InputStream
name|body
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|body
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"parsing error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|processResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot handle character stream"
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
