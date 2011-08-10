begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
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
begin_interface
DECL|interface|IndexWriterProvider
specifier|public
interface|interface
name|IndexWriterProvider
block|{
DECL|method|newIndexWriter
specifier|public
name|void
name|newIndexWriter
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getIndexWriter
specifier|public
name|IndexWriter
name|getIndexWriter
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|decref
specifier|public
name|void
name|decref
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|incref
specifier|public
name|void
name|incref
parameter_list|()
function_decl|;
DECL|method|rollbackIndexWriter
specifier|public
name|void
name|rollbackIndexWriter
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|updateCore
specifier|public
name|void
name|updateCore
parameter_list|(
name|SolrCore
name|core
parameter_list|)
function_decl|;
block|}
end_interface
end_unit
