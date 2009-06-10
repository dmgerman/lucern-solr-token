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
begin_comment
comment|/**   * This class keeps track of closing the underlying directory. It is used to wrap  * DirectoryReaders, that are created using a String/File parameter  * in IndexReader.open() with FSDirectory.getDirectory().  * @deprecated This helper class is removed with all String/File  * IndexReader.open() methods in Lucene 3.0  */
end_comment
begin_class
DECL|class|DirectoryOwningReader
specifier|final
class|class
name|DirectoryOwningReader
extends|extends
name|FilterIndexReader
implements|implements
name|Cloneable
block|{
DECL|method|DirectoryOwningReader
name|DirectoryOwningReader
parameter_list|(
specifier|final
name|IndexReader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|ref
operator|=
operator|new
name|SegmentReader
operator|.
name|Ref
argument_list|()
expr_stmt|;
assert|assert
name|this
operator|.
name|ref
operator|.
name|refCount
argument_list|()
operator|==
literal|1
assert|;
block|}
DECL|method|DirectoryOwningReader
specifier|private
name|DirectoryOwningReader
parameter_list|(
specifier|final
name|IndexReader
name|in
parameter_list|,
specifier|final
name|SegmentReader
operator|.
name|Ref
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|ref
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
DECL|method|reopen
specifier|public
name|IndexReader
name|reopen
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|in
operator|.
name|reopen
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|!=
name|in
condition|)
return|return
operator|new
name|DirectoryOwningReader
argument_list|(
name|r
argument_list|,
name|ref
argument_list|)
return|;
return|return
name|this
return|;
block|}
DECL|method|reopen
specifier|public
name|IndexReader
name|reopen
parameter_list|(
name|boolean
name|openReadOnly
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|in
operator|.
name|reopen
argument_list|(
name|openReadOnly
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
name|in
condition|)
return|return
operator|new
name|DirectoryOwningReader
argument_list|(
name|r
argument_list|,
name|ref
argument_list|)
return|;
return|return
name|this
return|;
block|}
DECL|method|reopen
specifier|public
name|IndexReader
name|reopen
parameter_list|(
specifier|final
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|in
operator|.
name|reopen
argument_list|(
name|commit
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
name|in
condition|)
return|return
operator|new
name|DirectoryOwningReader
argument_list|(
name|r
argument_list|,
name|ref
argument_list|)
return|;
return|return
name|this
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|DirectoryOwningReader
argument_list|(
operator|(
name|IndexReader
operator|)
name|in
operator|.
name|clone
argument_list|()
argument_list|,
name|ref
argument_list|)
return|;
block|}
DECL|method|clone
specifier|public
name|IndexReader
name|clone
parameter_list|(
name|boolean
name|openReadOnly
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|DirectoryOwningReader
argument_list|(
name|in
operator|.
name|clone
argument_list|(
name|openReadOnly
argument_list|)
argument_list|,
name|ref
argument_list|)
return|;
block|}
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
comment|// close the reader, record exception
try|try
block|{
name|super
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ioe
operator|=
name|e
expr_stmt|;
block|}
comment|// close the directory, record exception
if|if
condition|(
name|ref
operator|.
name|decRef
argument_list|()
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|in
operator|.
name|directory
argument_list|()
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
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
name|ioe
operator|=
name|e
expr_stmt|;
block|}
block|}
comment|// throw the first exception
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
throw|throw
name|ioe
throw|;
block|}
comment|/**    * This member contains the ref counter, that is passed to each instance after cloning/reopening,    * and is global to all DirectoryOwningReader derived from the original one.    * This reuses the class {@link SegmentReader.Ref}    */
DECL|field|ref
specifier|private
specifier|final
name|SegmentReader
operator|.
name|Ref
name|ref
decl_stmt|;
block|}
end_class
end_unit
