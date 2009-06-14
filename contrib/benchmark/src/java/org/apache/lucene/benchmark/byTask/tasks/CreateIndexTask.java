begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|IndexDeletionPolicy
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
name|lucene
operator|.
name|index
operator|.
name|MergeScheduler
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
name|MergePolicy
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
comment|/**  * Create an index.  *<br>Other side effects: index writer object in perfRunData is set.  *<br>Relevant properties:<code>merge.factor, max.buffered,  *  max.field.length, ram.flush.mb [default 0], autocommit  *  [default true]</code>.  */
end_comment
begin_class
DECL|class|CreateIndexTask
specifier|public
class|class
name|CreateIndexTask
extends|extends
name|PerfTask
block|{
DECL|method|CreateIndexTask
specifier|public
name|CreateIndexTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|setIndexWriterConfig
specifier|public
specifier|static
name|void
name|setIndexWriterConfig
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|Config
name|config
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|mergeScheduler
init|=
name|config
operator|.
name|get
argument_list|(
literal|"merge.scheduler"
argument_list|,
literal|"org.apache.lucene.index.ConcurrentMergeScheduler"
argument_list|)
decl_stmt|;
name|RuntimeException
name|err
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|setMergeScheduler
argument_list|(
operator|(
name|MergeScheduler
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|mergeScheduler
argument_list|)
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|mergeScheduler
operator|+
literal|"' as merge scheduler"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|iae
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|mergeScheduler
operator|+
literal|"' as merge scheduler"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to load class '"
operator|+
name|mergeScheduler
operator|+
literal|"' as merge scheduler"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|cnfe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
throw|throw
name|err
throw|;
specifier|final
name|String
name|mergePolicy
init|=
name|config
operator|.
name|get
argument_list|(
literal|"merge.policy"
argument_list|,
literal|"org.apache.lucene.index.LogByteSizeMergePolicy"
argument_list|)
decl_stmt|;
name|err
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|setMergePolicy
argument_list|(
operator|(
name|MergePolicy
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|mergePolicy
argument_list|)
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|mergePolicy
operator|+
literal|"' as merge policy"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|iae
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|mergePolicy
operator|+
literal|"' as merge policy"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to load class '"
operator|+
name|mergePolicy
operator|+
literal|"' as merge policy"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|cnfe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
throw|throw
name|err
throw|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"compound"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"merge.factor"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_MERGE_PFACTOR
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxFieldLength
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"max.field.length"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_MAX_FIELD_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|double
name|ramBuffer
init|=
name|config
operator|.
name|get
argument_list|(
literal|"ram.flush.mb"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_RAM_FLUSH_MB
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxBuffered
init|=
name|config
operator|.
name|get
argument_list|(
literal|"max.buffered"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_MAX_BUFFERED
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxBuffered
operator|==
name|IndexWriter
operator|.
name|DISABLE_AUTO_FLUSH
condition|)
block|{
name|writer
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramBuffer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBuffered
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBuffered
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramBuffer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getIndexDeletionPolicy
specifier|public
specifier|static
name|IndexDeletionPolicy
name|getIndexDeletionPolicy
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|String
name|deletionPolicyName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"deletion.policy"
argument_list|,
literal|"org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy"
argument_list|)
decl_stmt|;
name|IndexDeletionPolicy
name|indexDeletionPolicy
init|=
literal|null
decl_stmt|;
name|RuntimeException
name|err
init|=
literal|null
decl_stmt|;
try|try
block|{
name|indexDeletionPolicy
operator|=
operator|(
operator|(
name|IndexDeletionPolicy
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|deletionPolicyName
argument_list|)
operator|.
name|newInstance
argument_list|()
operator|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|deletionPolicyName
operator|+
literal|"' as IndexDeletionPolicy"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|iae
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|deletionPolicyName
operator|+
literal|"' as IndexDeletionPolicy"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|err
operator|=
operator|new
name|RuntimeException
argument_list|(
literal|"unable to load class '"
operator|+
name|deletionPolicyName
operator|+
literal|"' as IndexDeletionPolicy"
argument_list|)
expr_stmt|;
name|err
operator|.
name|initCause
argument_list|(
name|cnfe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
throw|throw
name|err
throw|;
return|return
name|indexDeletionPolicy
return|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|IOException
block|{
name|PerfRunData
name|runData
init|=
name|getRunData
argument_list|()
decl_stmt|;
name|Config
name|config
init|=
name|runData
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|IndexDeletionPolicy
name|indexDeletionPolicy
init|=
name|getIndexDeletionPolicy
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|runData
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|runData
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"autocommit"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_AUTO_COMMIT
argument_list|)
argument_list|,
name|runData
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|indexDeletionPolicy
argument_list|)
decl_stmt|;
name|CreateIndexTask
operator|.
name|setIndexWriterConfig
argument_list|(
name|writer
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|runData
operator|.
name|setIndexWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
end_class
end_unit
