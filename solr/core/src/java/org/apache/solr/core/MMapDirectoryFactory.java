begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|LockFactory
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|MMapDirectory
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
begin_comment
comment|/**  *  Directly provide MMapDirectory instead of relying on {@link org.apache.lucene.store.FSDirectory#open}  *  * Can set the following parameters:  *<ul>  *<li>unmap -- See {@link MMapDirectory#setUseUnmap(boolean)}</li>  *<li>maxChunkSize -- The Max chunk size.  See {@link MMapDirectory#MMapDirectory(File, LockFactory, int)}</li>  *</ul>  *  **/
end_comment
begin_class
DECL|class|MMapDirectoryFactory
specifier|public
class|class
name|MMapDirectoryFactory
extends|extends
name|StandardDirectoryFactory
block|{
DECL|field|log
specifier|private
specifier|transient
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MMapDirectoryFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|unmapHack
name|boolean
name|unmapHack
decl_stmt|;
DECL|field|maxChunk
specifier|private
name|int
name|maxChunk
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
name|SolrParams
name|params
init|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|maxChunk
operator|=
name|params
operator|.
name|getInt
argument_list|(
literal|"maxChunkSize"
argument_list|,
name|MMapDirectory
operator|.
name|DEFAULT_MAX_BUFF
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxChunk
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxChunk must be greater than 0"
argument_list|)
throw|;
block|}
name|unmapHack
operator|=
name|params
operator|.
name|getBool
argument_list|(
literal|"unmap"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|MMapDirectory
name|mapDirectory
init|=
operator|new
name|MMapDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|,
literal|null
argument_list|,
name|maxChunk
argument_list|)
decl_stmt|;
try|try
block|{
name|mapDirectory
operator|.
name|setUseUnmap
argument_list|(
name|unmapHack
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
name|warn
argument_list|(
literal|"Unmap not supported on this JVM, continuing on without setting unmap"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|mapDirectory
return|;
block|}
block|}
end_class
end_unit
