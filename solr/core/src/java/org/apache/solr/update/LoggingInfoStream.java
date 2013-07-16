begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|InfoStream
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * An {@link InfoStream} implementation which passes messages on to Solr's logging.  */
end_comment
begin_class
DECL|class|LoggingInfoStream
specifier|public
class|class
name|LoggingInfoStream
extends|extends
name|InfoStream
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LoggingInfoStream
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|message
specifier|public
name|void
name|message
parameter_list|(
name|String
name|component
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"["
operator|+
name|component
operator|+
literal|"]["
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|(
name|String
name|component
parameter_list|)
block|{
comment|// ignore testpoints so this can be used with tests without flooding logs with verbose messages
return|return
operator|!
literal|"TP"
operator|.
name|equals
argument_list|(
name|component
argument_list|)
operator|&&
name|log
operator|.
name|isInfoEnabled
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class
end_unit
