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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|DirectoryFactory
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
name|util
operator|.
name|RefCounted
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
begin_comment
comment|/**  * The state in this class can be easily shared between SolrCores across  * SolrCore reloads.  *   */
end_comment
begin_class
DECL|class|SolrCoreState
specifier|public
specifier|abstract
class|class
name|SolrCoreState
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
name|SolrCoreState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|deleteLock
specifier|private
specifier|final
name|Object
name|deleteLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|getUpdateLock
specifier|public
name|Object
name|getUpdateLock
parameter_list|()
block|{
return|return
name|deleteLock
return|;
block|}
DECL|field|solrCoreStateRefCnt
specifier|private
name|int
name|solrCoreStateRefCnt
init|=
literal|1
decl_stmt|;
DECL|method|getSolrCoreStateRefCnt
specifier|public
specifier|synchronized
name|int
name|getSolrCoreStateRefCnt
parameter_list|()
block|{
return|return
name|solrCoreStateRefCnt
return|;
block|}
DECL|method|increfSolrCoreState
specifier|public
name|void
name|increfSolrCoreState
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|solrCoreStateRefCnt
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexWriter has been closed"
argument_list|)
throw|;
block|}
name|solrCoreStateRefCnt
operator|++
expr_stmt|;
block|}
block|}
DECL|method|decrefSolrCoreState
specifier|public
name|void
name|decrefSolrCoreState
parameter_list|(
name|IndexWriterCloser
name|closer
parameter_list|)
block|{
name|boolean
name|close
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|solrCoreStateRefCnt
operator|--
expr_stmt|;
if|if
condition|(
name|solrCoreStateRefCnt
operator|==
literal|0
condition|)
block|{
name|close
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|close
condition|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Closing SolrCoreState"
argument_list|)
expr_stmt|;
name|close
argument_list|(
name|closer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error closing SolrCoreState"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getCommitLock
specifier|public
specifier|abstract
name|Lock
name|getCommitLock
parameter_list|()
function_decl|;
comment|/**    * Force the creation of a new IndexWriter using the settings from the given    * SolrCore.    *     * @param rollback close IndexWriter if false, else rollback    * @throws IOException If there is a low-level I/O error.    */
DECL|method|newIndexWriter
specifier|public
specifier|abstract
name|void
name|newIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|boolean
name|rollback
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the current IndexWriter. If a new IndexWriter must be created, use the    * settings from the given {@link SolrCore}.    *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|getIndexWriter
specifier|public
specifier|abstract
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|getIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Rollback the current IndexWriter. When creating the new IndexWriter use the    * settings from the given {@link SolrCore}.    *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|rollbackIndexWriter
specifier|public
specifier|abstract
name|void
name|rollbackIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the {@link DirectoryFactory} that should be used.    */
DECL|method|getDirectoryFactory
specifier|public
specifier|abstract
name|DirectoryFactory
name|getDirectoryFactory
parameter_list|()
function_decl|;
DECL|interface|IndexWriterCloser
specifier|public
interface|interface
name|IndexWriterCloser
block|{
DECL|method|closeWriter
specifier|public
name|void
name|closeWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|method|doRecovery
specifier|public
specifier|abstract
name|void
name|doRecovery
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd
parameter_list|)
function_decl|;
DECL|method|cancelRecovery
specifier|public
specifier|abstract
name|void
name|cancelRecovery
parameter_list|()
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|(
name|IndexWriterCloser
name|closer
parameter_list|)
function_decl|;
block|}
end_class
end_unit
