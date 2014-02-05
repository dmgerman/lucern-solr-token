begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|TokenFilterFactory
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
begin_comment
comment|/**  * Token filter factory that misbehaves on command.  */
end_comment
begin_class
DECL|class|ThrowingMockTokenFilterFactory
specifier|public
class|class
name|ThrowingMockTokenFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|exceptionClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|RuntimeException
argument_list|>
name|exceptionClass
decl_stmt|;
comment|/**    * Initialize this factory via a set of key-value pairs.    *    * @param args the options.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ThrowingMockTokenFilterFactory
specifier|public
name|ThrowingMockTokenFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|exceptionClassName
init|=
name|args
operator|.
name|get
argument_list|(
literal|"exceptionClassName"
argument_list|)
decl_stmt|;
if|if
condition|(
name|exceptionClassName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Required parameter exceptionClassName is missing"
argument_list|)
throw|;
block|}
try|try
block|{
name|exceptionClass
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|RuntimeException
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|exceptionClassName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|TokenFilter
argument_list|(
name|input
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
try|try
block|{
throw|throw
name|exceptionClass
operator|.
name|newInstance
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InstantiationException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|iae
argument_list|)
throw|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
