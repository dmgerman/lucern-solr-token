begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|List
import|;
end_import
begin_comment
comment|/**  * A {@link DirectoryReader} that wraps all its subreaders with  * {@link AssertingAtomicReader}  */
end_comment
begin_class
DECL|class|AssertingDirectoryReader
specifier|public
class|class
name|AssertingDirectoryReader
extends|extends
name|DirectoryReader
block|{
DECL|field|in
specifier|protected
name|DirectoryReader
name|in
decl_stmt|;
DECL|method|AssertingDirectoryReader
specifier|public
name|AssertingDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|directory
argument_list|()
argument_list|,
name|wrap
argument_list|(
name|in
operator|.
name|getSequentialSubReaders
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|wrap
specifier|private
specifier|static
name|AtomicReader
index|[]
name|wrap
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|AtomicReader
argument_list|>
name|readers
parameter_list|)
block|{
name|AtomicReader
index|[]
name|wrapped
init|=
operator|new
name|AtomicReader
index|[
name|readers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|wrapped
index|[
name|i
index|]
operator|=
operator|new
name|AssertingAtomicReader
argument_list|(
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapped
return|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
name|DirectoryReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|IOException
block|{
name|DirectoryReader
name|d
init|=
name|in
operator|.
name|doOpenIfChanged
argument_list|()
decl_stmt|;
return|return
name|d
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|AssertingDirectoryReader
argument_list|(
name|d
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
name|DirectoryReader
name|doOpenIfChanged
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
name|DirectoryReader
name|d
init|=
name|in
operator|.
name|doOpenIfChanged
argument_list|(
name|commit
argument_list|)
decl_stmt|;
return|return
name|d
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|AssertingDirectoryReader
argument_list|(
name|d
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
name|DirectoryReader
name|doOpenIfChanged
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
name|DirectoryReader
name|d
init|=
name|in
operator|.
name|doOpenIfChanged
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|)
decl_stmt|;
return|return
name|d
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|AssertingDirectoryReader
argument_list|(
name|d
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|in
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|isCurrent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getIndexCommit
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCombinedCoreAndDeletesKey
argument_list|()
return|;
block|}
block|}
end_class
end_unit
