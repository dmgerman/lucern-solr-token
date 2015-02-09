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
name|util
operator|.
name|ContentStream
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
name|SEVERE
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import
begin_comment
comment|/**  *<p> A data source implementation which can be used to read binary stream from content streams.</p><p> Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a> for more  * details.</p>  *<p>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 3.1  */
end_comment
begin_class
DECL|class|BinContentStreamDataSource
specifier|public
class|class
name|BinContentStreamDataSource
extends|extends
name|DataSource
argument_list|<
name|InputStream
argument_list|>
block|{
DECL|field|context
specifier|private
name|ContextImpl
name|context
decl_stmt|;
DECL|field|contentStream
specifier|private
name|ContentStream
name|contentStream
decl_stmt|;
DECL|field|in
specifier|private
name|InputStream
name|in
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
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
operator|(
name|ContextImpl
operator|)
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getData
specifier|public
name|InputStream
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|contentStream
operator|=
name|context
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|getReqParams
argument_list|()
operator|.
name|getContentStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|contentStream
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"No stream available. The request has no body"
argument_list|)
throw|;
try|try
block|{
return|return
name|in
operator|=
name|contentStream
operator|.
name|getStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|DataImportHandlerException
operator|.
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|contentStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
name|in
operator|=
name|contentStream
operator|.
name|getStream
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/*no op*/
block|}
block|}
block|}
block|}
end_class
end_unit
