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
begin_class
DECL|class|DefaultSolrCoreState
specifier|public
specifier|final
class|class
name|DefaultSolrCoreState
extends|extends
name|SolrCoreState
block|{
DECL|field|refCnt
specifier|private
name|int
name|refCnt
init|=
literal|1
decl_stmt|;
DECL|field|indexWriter
specifier|private
name|SolrIndexWriter
name|indexWriter
init|=
literal|null
decl_stmt|;
DECL|field|directoryFactory
specifier|private
name|DirectoryFactory
name|directoryFactory
decl_stmt|;
DECL|method|DefaultSolrCoreState
specifier|public
name|DefaultSolrCoreState
parameter_list|(
name|DirectoryFactory
name|directoryFactory
parameter_list|)
block|{
name|this
operator|.
name|directoryFactory
operator|=
name|directoryFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIndexWriter
specifier|public
specifier|synchronized
name|IndexWriter
name|getIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexWriter
operator|==
literal|null
condition|)
block|{
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|indexWriter
return|;
block|}
annotation|@
name|Override
DECL|method|newIndexWriter
specifier|public
specifier|synchronized
name|void
name|newIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decref
specifier|public
specifier|synchronized
name|void
name|decref
parameter_list|()
throws|throws
name|IOException
block|{
name|refCnt
operator|--
expr_stmt|;
if|if
condition|(
name|refCnt
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|directoryFactory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|incref
specifier|public
specifier|synchronized
name|void
name|incref
parameter_list|()
block|{
if|if
condition|(
name|refCnt
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
name|refCnt
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rollbackIndexWriter
specifier|public
specifier|synchronized
name|void
name|rollbackIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|newIndexWriter
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
DECL|method|createMainIndexWriter
specifier|protected
name|SolrIndexWriter
name|createMainIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|removeAllExisting
parameter_list|,
name|boolean
name|forceNewDirectory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SolrIndexWriter
argument_list|(
name|name
argument_list|,
name|core
operator|.
name|getNewIndexDir
argument_list|()
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|,
name|removeAllExisting
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|mainIndexConfig
argument_list|,
name|core
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|,
name|core
operator|.
name|getCodec
argument_list|()
argument_list|,
name|forceNewDirectory
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectoryFactory
specifier|public
name|DirectoryFactory
name|getDirectoryFactory
parameter_list|()
block|{
return|return
name|directoryFactory
return|;
block|}
block|}
end_class
end_unit
