begin_unit
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystem
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|LinkOption
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|WatchEvent
operator|.
name|Kind
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|WatchEvent
operator|.
name|Modifier
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|WatchKey
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|WatchService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_comment
comment|/**    * A {@code FilterPath} contains another   * {@code Path}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   */
end_comment
begin_class
DECL|class|FilterPath
specifier|public
class|class
name|FilterPath
implements|implements
name|Path
block|{
comment|/**     * The underlying {@code Path} instance.     */
DECL|field|delegate
specifier|protected
specifier|final
name|Path
name|delegate
decl_stmt|;
comment|/**     * The parent {@code FileSystem} for this path.     */
DECL|field|fileSystem
specifier|protected
specifier|final
name|FileSystem
name|fileSystem
decl_stmt|;
comment|/**    * Construct a {@code FilterPath} with parent    * {@code fileSystem}, based on the specified base path.    * @param delegate specified base path.    * @param fileSystem parent fileSystem.    */
DECL|method|FilterPath
specifier|public
name|FilterPath
parameter_list|(
name|Path
name|delegate
parameter_list|,
name|FileSystem
name|fileSystem
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|fileSystem
operator|=
name|fileSystem
expr_stmt|;
block|}
comment|/**     * Get the underlying wrapped path.    * @return wrapped path.    */
DECL|method|getDelegate
specifier|public
name|Path
name|getDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
annotation|@
name|Override
DECL|method|getFileSystem
specifier|public
name|FileSystem
name|getFileSystem
parameter_list|()
block|{
return|return
name|fileSystem
return|;
block|}
annotation|@
name|Override
DECL|method|isAbsolute
specifier|public
name|boolean
name|isAbsolute
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isAbsolute
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRoot
specifier|public
name|Path
name|getRoot
parameter_list|()
block|{
name|Path
name|root
init|=
name|delegate
operator|.
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|wrap
argument_list|(
name|root
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileName
specifier|public
name|Path
name|getFileName
parameter_list|()
block|{
name|Path
name|fileName
init|=
name|delegate
operator|.
name|getFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|wrap
argument_list|(
name|fileName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParent
specifier|public
name|Path
name|getParent
parameter_list|()
block|{
name|Path
name|parent
init|=
name|delegate
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|wrap
argument_list|(
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNameCount
specifier|public
name|int
name|getNameCount
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getNameCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|Path
name|getName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|getName
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|subpath
specifier|public
name|Path
name|subpath
parameter_list|(
name|int
name|beginIndex
parameter_list|,
name|int
name|endIndex
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|subpath
argument_list|(
name|beginIndex
argument_list|,
name|endIndex
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|startsWith
specifier|public
name|boolean
name|startsWith
parameter_list|(
name|Path
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|FilterPath
condition|)
block|{
name|FilterPath
name|f
init|=
operator|(
name|FilterPath
operator|)
name|other
decl_stmt|;
return|return
name|fileSystem
operator|==
name|f
operator|.
name|fileSystem
operator|&&
name|delegate
operator|.
name|startsWith
argument_list|(
name|f
operator|.
name|delegate
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|startsWith
specifier|public
name|boolean
name|startsWith
parameter_list|(
name|String
name|other
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|startsWith
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|endsWith
specifier|public
name|boolean
name|endsWith
parameter_list|(
name|Path
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|FilterPath
condition|)
block|{
name|FilterPath
name|f
init|=
operator|(
name|FilterPath
operator|)
name|other
decl_stmt|;
return|return
name|fileSystem
operator|==
name|f
operator|.
name|fileSystem
operator|&&
name|delegate
operator|.
name|endsWith
argument_list|(
name|f
operator|.
name|delegate
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|endsWith
specifier|public
name|boolean
name|endsWith
parameter_list|(
name|String
name|other
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|startsWith
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|Path
name|normalize
parameter_list|()
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|normalize
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resolve
specifier|public
name|Path
name|resolve
parameter_list|(
name|Path
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|FilterPath
condition|)
block|{
name|other
operator|=
operator|(
operator|(
name|FilterPath
operator|)
name|other
operator|)
operator|.
name|delegate
expr_stmt|;
block|}
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|resolve
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resolve
specifier|public
name|Path
name|resolve
parameter_list|(
name|String
name|other
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|resolve
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resolveSibling
specifier|public
name|Path
name|resolveSibling
parameter_list|(
name|Path
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|FilterPath
condition|)
block|{
name|other
operator|=
operator|(
operator|(
name|FilterPath
operator|)
name|other
operator|)
operator|.
name|delegate
expr_stmt|;
block|}
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|resolveSibling
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resolveSibling
specifier|public
name|Path
name|resolveSibling
parameter_list|(
name|String
name|other
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|resolveSibling
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|relativize
specifier|public
name|Path
name|relativize
parameter_list|(
name|Path
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|FilterPath
condition|)
block|{
name|other
operator|=
operator|(
operator|(
name|FilterPath
operator|)
name|other
operator|)
operator|.
name|delegate
expr_stmt|;
block|}
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|relativize
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
comment|// TODO: should these methods not expose delegate result directly?
comment|// it could allow code to "escape" the sandbox...
annotation|@
name|Override
DECL|method|toUri
specifier|public
name|URI
name|toUri
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|toUri
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toAbsolutePath
specifier|public
name|Path
name|toAbsolutePath
parameter_list|()
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toRealPath
specifier|public
name|Path
name|toRealPath
parameter_list|(
name|LinkOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|wrap
argument_list|(
name|delegate
operator|.
name|toRealPath
argument_list|(
name|options
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toFile
specifier|public
name|File
name|toFile
parameter_list|()
block|{
comment|// TODO: should we throw exception here?
return|return
name|delegate
operator|.
name|toFile
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|public
name|WatchKey
name|register
parameter_list|(
name|WatchService
name|watcher
parameter_list|,
name|Kind
argument_list|<
name|?
argument_list|>
index|[]
name|events
parameter_list|,
name|Modifier
modifier|...
name|modifiers
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|register
argument_list|(
name|watcher
argument_list|,
name|events
argument_list|,
name|modifiers
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|public
name|WatchKey
name|register
parameter_list|(
name|WatchService
name|watcher
parameter_list|,
name|Kind
argument_list|<
name|?
argument_list|>
modifier|...
name|events
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|register
argument_list|(
name|watcher
argument_list|,
name|events
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Path
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Path
argument_list|>
name|iterator
init|=
name|delegate
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Path
name|next
parameter_list|()
block|{
return|return
name|wrap
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Path
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|FilterPath
condition|)
block|{
name|other
operator|=
operator|(
operator|(
name|FilterPath
operator|)
name|other
operator|)
operator|.
name|delegate
expr_stmt|;
block|}
return|return
name|delegate
operator|.
name|compareTo
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/**    * Unwraps all {@code FilterPath}s, returning    * the innermost {@code Path}.    *<p>    * WARNING: this is exposed for testing only!    * @param path specified path.    * @return innermost Path instance    */
DECL|method|unwrap
specifier|public
specifier|static
name|Path
name|unwrap
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
while|while
condition|(
name|path
operator|instanceof
name|FilterPath
condition|)
block|{
name|path
operator|=
operator|(
operator|(
name|FilterPath
operator|)
name|path
operator|)
operator|.
name|delegate
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/** Override this to customize the return wrapped    *  path from various operations */
DECL|method|wrap
specifier|protected
name|Path
name|wrap
parameter_list|(
name|Path
name|other
parameter_list|)
block|{
return|return
operator|new
name|FilterPath
argument_list|(
name|other
argument_list|,
name|fileSystem
argument_list|)
return|;
block|}
block|}
end_class
end_unit
