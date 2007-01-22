begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * A memory-resident {@link Directory} implementation.  Locking  * implementation is by default the {@link SingleInstanceLockFactory}  * but can be changed with {@link #setLockFactory}.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|RAMDirectory
specifier|public
class|class
name|RAMDirectory
extends|extends
name|Directory
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1l
decl_stmt|;
DECL|field|fileMap
name|HashMap
name|fileMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|fileNames
specifier|private
name|Set
name|fileNames
init|=
name|fileMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
DECL|field|files
name|Collection
name|files
init|=
name|fileMap
operator|.
name|values
argument_list|()
decl_stmt|;
DECL|field|sizeInBytes
name|long
name|sizeInBytes
init|=
literal|0
decl_stmt|;
comment|// *****
comment|// Lock acquisition sequence:  RAMDirectory, then RAMFile
comment|// *****
comment|/** Constructs an empty {@link Directory}. */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|()
block|{
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new<code>RAMDirectory</code> instance from a different    *<code>Directory</code> implementation.  This can be used to load    * a disk-based index into memory.    *<P>    * This should be used only with indices that can fit into memory.    *<P>    * Note that the resulting<code>RAMDirectory</code> instance is fully    * independent from the original<code>Directory</code> (it is a    * complete copy).  Any subsequent changes to the    * original<code>Directory</code> will not be visible in the    *<code>RAMDirectory</code> instance.    *    * @param dir a<code>Directory</code> value    * @exception IOException if an error occurs    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|RAMDirectory
specifier|private
name|RAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|boolean
name|closeDir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|()
expr_stmt|;
name|Directory
operator|.
name|copy
argument_list|(
name|dir
argument_list|,
name|this
argument_list|,
name|closeDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new<code>RAMDirectory</code> instance from the {@link FSDirectory}.    *    * @param dir a<code>File</code> specifying the index directory    *    * @see #RAMDirectory(Directory)    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dir
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new<code>RAMDirectory</code> instance from the {@link FSDirectory}.    *    * @param dir a<code>String</code> specifying the full index directory path    *    * @see #RAMDirectory(Directory)    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dir
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Returns an array of strings, one for each file in the directory. */
DECL|method|list
specifier|public
specifier|synchronized
specifier|final
name|String
index|[]
name|list
parameter_list|()
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|fileNames
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Iterator
name|it
init|=
name|fileNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
name|result
index|[
name|i
operator|++
index|]
operator|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Returns true iff the named file exists in this directory. */
DECL|method|fileExists
specifier|public
specifier|final
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|file
operator|!=
literal|null
return|;
block|}
comment|/** Returns the time the named file was last modified.    * @throws IOException if the file does not exist    */
DECL|method|fileModified
specifier|public
specifier|final
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|file
operator|.
name|getLastModified
argument_list|()
return|;
block|}
comment|/** Set the modified time of an existing file to now.    * @throws IOException if the file does not exist    */
DECL|method|touchFile
specifier|public
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
name|long
name|ts2
decl_stmt|,
name|ts1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
do|do
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
name|ts2
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|ts1
operator|==
name|ts2
condition|)
do|;
name|file
operator|.
name|setLastModified
argument_list|(
name|ts2
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the length in bytes of a file in the directory.    * @throws IOException if the file does not exist    */
DECL|method|fileLength
specifier|public
specifier|final
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|file
operator|.
name|getLength
argument_list|()
return|;
block|}
comment|/** Return total size in bytes of all files in this    * directory.  This is currently quantized to    * BufferedIndexOutput.BUFFER_SIZE. */
DECL|method|sizeInBytes
specifier|public
specifier|synchronized
specifier|final
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|sizeInBytes
return|;
block|}
comment|/** Removes an existing file in the directory.    * @throws IOException if the file does not exist    */
DECL|method|deleteFile
specifier|public
specifier|synchronized
specifier|final
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMFile
name|file
init|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|fileMap
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|file
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
name|sizeInBytes
operator|-=
name|file
operator|.
name|sizeInBytes
expr_stmt|;
comment|// updates to RAMFile.sizeInBytes synchronized on directory
block|}
else|else
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
comment|/** Removes an existing file in the directory.    * @throws IOException if from does not exist    */
DECL|method|renameFile
specifier|public
specifier|synchronized
specifier|final
name|void
name|renameFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMFile
name|fromFile
init|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|from
argument_list|)
decl_stmt|;
if|if
condition|(
name|fromFile
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|from
argument_list|)
throw|;
name|RAMFile
name|toFile
init|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|to
argument_list|)
decl_stmt|;
if|if
condition|(
name|toFile
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|-=
name|toFile
operator|.
name|sizeInBytes
expr_stmt|;
comment|// updates to RAMFile.sizeInBytes synchronized on directory
name|toFile
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
block|}
name|fileMap
operator|.
name|remove
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|fileMap
operator|.
name|put
argument_list|(
name|to
argument_list|,
name|fromFile
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new, empty file in the directory with the given name. Returns a stream writing this file. */
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|RAMFile
name|file
init|=
operator|new
name|RAMFile
argument_list|(
name|this
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|RAMFile
name|existing
init|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|-=
name|existing
operator|.
name|sizeInBytes
expr_stmt|;
name|existing
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
block|}
name|fileMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/** Returns a stream reading an existing file. */
DECL|method|openInput
specifier|public
specifier|final
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMFile
name|file
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|file
operator|=
operator|(
name|RAMFile
operator|)
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
operator|new
name|RAMInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/** Closes the store to future operations, releasing associated memory. */
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
block|{
name|fileMap
operator|=
literal|null
expr_stmt|;
name|fileNames
operator|=
literal|null
expr_stmt|;
name|files
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
