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
name|IndexInput
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
name|BufferedIndexInput
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
name|IndexOutput
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
name|Lock
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Class for accessing a compound stream.  * This class implements a directory, but is limited to only read operations.  * Directory methods that would normally modify data throw an exception.  */
end_comment
begin_class
DECL|class|CompoundFileReader
class|class
name|CompoundFileReader
extends|extends
name|Directory
block|{
DECL|field|readBufferSize
specifier|private
name|int
name|readBufferSize
decl_stmt|;
DECL|class|FileEntry
specifier|private
specifier|static
specifier|final
class|class
name|FileEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
block|}
comment|// Base info
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
DECL|field|stream
specifier|private
name|IndexInput
name|stream
decl_stmt|;
DECL|field|entries
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|CompoundFileReader
specifier|public
name|CompoundFileReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|name
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundFileReader
specifier|public
name|CompoundFileReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|directory
operator|=
name|dir
expr_stmt|;
name|fileName
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|readBufferSize
operator|=
name|readBufferSize
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
comment|// read the directory and init files
name|int
name|count
init|=
name|stream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|FileEntry
name|entry
init|=
literal|null
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|long
name|offset
init|=
name|stream
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|stream
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
comment|// set length of the previous entry
name|entry
operator|.
name|length
operator|=
name|offset
operator|-
name|entry
operator|.
name|offset
expr_stmt|;
block|}
name|entry
operator|=
operator|new
name|FileEntry
argument_list|()
expr_stmt|;
name|entry
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
comment|// set the length of the final entry
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|length
operator|=
name|stream
operator|.
name|length
argument_list|()
operator|-
name|entry
operator|.
name|offset
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
operator|&&
operator|(
name|stream
operator|!=
literal|null
operator|)
condition|)
block|{
try|try
block|{
name|stream
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
block|{ }
block|}
block|}
block|}
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Already closed"
argument_list|)
throw|;
name|entries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|openInput
specifier|public
specifier|synchronized
name|IndexInput
name|openInput
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Default to readBufferSize passed in when we were opened
return|return
name|openInput
argument_list|(
name|id
argument_list|,
name|readBufferSize
argument_list|)
return|;
block|}
DECL|method|openInput
specifier|public
specifier|synchronized
name|IndexInput
name|openInput
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream closed"
argument_list|)
throw|;
name|FileEntry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No sub-file with id "
operator|+
name|id
operator|+
literal|" found"
argument_list|)
throw|;
return|return
operator|new
name|CSIndexInput
argument_list|(
name|stream
argument_list|,
name|entry
operator|.
name|offset
argument_list|,
name|entry
operator|.
name|length
argument_list|,
name|readBufferSize
argument_list|)
return|;
block|}
comment|/** Returns an array of strings, one for each file in the directory. */
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
block|{
name|String
name|res
index|[]
init|=
operator|new
name|String
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
name|res
argument_list|)
return|;
block|}
comment|/** Returns true iff a file with the given name exists. */
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|entries
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Returns the time the compound file was last modified. */
DECL|method|fileModified
specifier|public
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|directory
operator|.
name|fileModified
argument_list|(
name|fileName
argument_list|)
return|;
block|}
comment|/** Set the modified time of the compound file to now. */
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
name|directory
operator|.
name|touchFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
comment|/** Not implemented      * @throws UnsupportedOperationException */
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Not implemented      * @throws UnsupportedOperationException */
DECL|method|renameFile
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Returns the length of a file in the directory.      * @throws IOException if the file does not exist */
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|FileEntry
name|e
init|=
name|entries
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File "
operator|+
name|name
operator|+
literal|" does not exist"
argument_list|)
throw|;
return|return
name|e
operator|.
name|length
return|;
block|}
comment|/** Not implemented      * @throws UnsupportedOperationException */
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Not implemented      * @throws UnsupportedOperationException */
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Implementation of an IndexInput that reads from a portion of the      *  compound file. The visibility is left as "package" *only* because      *  this helps with testing since JUnit test cases in a different class      *  can then access package fields of this class.      */
DECL|class|CSIndexInput
specifier|static
specifier|final
class|class
name|CSIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|field|base
name|IndexInput
name|base
decl_stmt|;
DECL|field|fileOffset
name|long
name|fileOffset
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
DECL|method|CSIndexInput
name|CSIndexInput
parameter_list|(
specifier|final
name|IndexInput
name|base
parameter_list|,
specifier|final
name|long
name|fileOffset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|)
block|{
name|this
argument_list|(
name|base
argument_list|,
name|fileOffset
argument_list|,
name|length
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|CSIndexInput
name|CSIndexInput
parameter_list|(
specifier|final
name|IndexInput
name|base
parameter_list|,
specifier|final
name|long
name|fileOffset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|readBufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
operator|(
name|IndexInput
operator|)
name|base
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileOffset
operator|=
name|fileOffset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|CSIndexInput
name|clone
init|=
operator|(
name|CSIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|base
operator|=
operator|(
name|IndexInput
operator|)
name|base
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|fileOffset
operator|=
name|fileOffset
expr_stmt|;
name|clone
operator|.
name|length
operator|=
name|length
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/** Expert: implements buffer refill.  Reads bytes from the current          *  position in the input.          * @param b the array to read bytes into          * @param offset the offset in the array to start storing bytes          * @param len the number of bytes to read          */
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|+
name|len
operator|>
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
name|base
operator|.
name|seek
argument_list|(
name|fileOffset
operator|+
name|start
argument_list|)
expr_stmt|;
name|base
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: implements seek.  Sets current position in this file, where          *  the next {@link #readInternal(byte[],int,int)} will occur.          * @see #readInternal(byte[],int,int)          */
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
block|{}
comment|/** Closes the stream to further operations. */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
block|}
end_class
end_unit
