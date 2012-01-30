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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CorruptIndexException
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
name|IndexFileNames
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
name|util
operator|.
name|IOUtils
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
name|Collections
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
name|Map
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
name|IOException
import|;
end_import
begin_comment
comment|/**  * Class for accessing a compound stream.  * This class implements a directory, but is limited to only read operations.  * Directory methods that would normally modify data throw an exception.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CompoundFileDirectory
specifier|public
specifier|final
class|class
name|CompoundFileDirectory
extends|extends
name|Directory
block|{
comment|/** Offset/Length for a slice inside of a compound file */
DECL|class|FileEntry
specifier|public
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
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|readBufferSize
specifier|protected
specifier|final
name|int
name|readBufferSize
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|entries
decl_stmt|;
DECL|field|openForWrite
specifier|private
specifier|final
name|boolean
name|openForWrite
decl_stmt|;
DECL|field|SENTINEL
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|SENTINEL
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|CompoundFileWriter
name|writer
decl_stmt|;
DECL|field|handle
specifier|private
specifier|final
name|IndexInputSlicer
name|handle
decl_stmt|;
comment|/**    * Create a new CompoundFileDirectory.    */
DECL|method|CompoundFileDirectory
specifier|public
name|CompoundFileDirectory
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|boolean
name|openForWrite
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|readBufferSize
operator|=
name|BufferedIndexInput
operator|.
name|bufferSize
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|isOpen
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|openForWrite
operator|=
name|openForWrite
expr_stmt|;
if|if
condition|(
operator|!
name|openForWrite
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|handle
operator|=
name|directory
operator|.
name|createSlicer
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|entries
operator|=
name|readEntries
argument_list|(
name|handle
argument_list|,
name|directory
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
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
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|isOpen
operator|=
literal|true
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
operator|!
operator|(
name|directory
operator|instanceof
name|CompoundFileDirectory
operator|)
operator|:
literal|"compound file inside of compound file: "
operator|+
name|fileName
assert|;
name|this
operator|.
name|entries
operator|=
name|SENTINEL
expr_stmt|;
name|this
operator|.
name|isOpen
operator|=
literal|true
expr_stmt|;
name|writer
operator|=
operator|new
name|CompoundFileWriter
argument_list|(
name|directory
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|handle
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Helper method that reads CFS entries from an input stream */
DECL|method|readEntries
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|readEntries
parameter_list|(
name|IndexInputSlicer
name|handle
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read the first VInt. If it is negative, it's the version number
comment|// otherwise it's the count (pre-3.1 indexes)
specifier|final
name|IndexInput
name|stream
init|=
name|handle
operator|.
name|openFullSlice
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|mapping
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|firstInt
init|=
name|stream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstInt
operator|==
name|CompoundFileWriter
operator|.
name|FORMAT_CURRENT
condition|)
block|{
name|IndexInput
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|entriesFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|stripExtension
argument_list|(
name|name
argument_list|)
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
decl_stmt|;
name|input
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|entriesFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
specifier|final
name|int
name|readInt
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// unused right now
assert|assert
name|readInt
operator|==
name|CompoundFileWriter
operator|.
name|ENTRY_FORMAT_CURRENT
assert|;
specifier|final
name|int
name|numEntries
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|mapping
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CompoundFileDirectory
operator|.
name|FileEntry
argument_list|>
argument_list|(
name|numEntries
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numEntries
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FileEntry
name|fileEntry
init|=
operator|new
name|FileEntry
argument_list|()
decl_stmt|;
specifier|final
name|String
name|id
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|mapping
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
operator|:
literal|"id="
operator|+
name|id
operator|+
literal|" was written multiple times in the CFS"
assert|;
name|mapping
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|fileEntry
argument_list|)
expr_stmt|;
name|fileEntry
operator|.
name|offset
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|fileEntry
operator|.
name|length
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
return|return
name|mapping
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// TODO remove once 3.x is not supported anymore
name|mapping
operator|=
name|readLegacyEntries
argument_list|(
name|stream
argument_list|,
name|firstInt
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|mapping
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readLegacyEntries
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|FileEntry
argument_list|>
name|readLegacyEntries
parameter_list|(
name|IndexInput
name|stream
parameter_list|,
name|int
name|firstInt
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|Map
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
specifier|final
name|int
name|count
decl_stmt|;
specifier|final
name|boolean
name|stripSegmentName
decl_stmt|;
if|if
condition|(
name|firstInt
operator|<
name|CompoundFileWriter
operator|.
name|FORMAT_PRE_VERSION
condition|)
block|{
if|if
condition|(
name|firstInt
operator|<
name|CompoundFileWriter
operator|.
name|FORMAT_CURRENT
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Incompatible format version: "
operator|+
name|firstInt
operator|+
literal|" expected "
operator|+
name|CompoundFileWriter
operator|.
name|FORMAT_CURRENT
operator|+
literal|" (resource: "
operator|+
name|stream
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// It's a post-3.1 index, read the count.
name|count
operator|=
name|stream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|stripSegmentName
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
name|firstInt
expr_stmt|;
name|stripSegmentName
operator|=
literal|true
expr_stmt|;
block|}
comment|// read the directory and init files
name|long
name|streamLength
init|=
name|stream
operator|.
name|length
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
if|if
condition|(
name|offset
argument_list|<
literal|0
operator|||
name|offset
argument_list|>
name|streamLength
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid CFS entry offset: "
operator|+
name|offset
operator|+
literal|" (resource: "
operator|+
name|stream
operator|+
literal|")"
argument_list|)
throw|;
block|}
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
name|stripSegmentName
condition|)
block|{
comment|// Fix the id to not include the segment names. This is relevant for
comment|// pre-3.1 indexes.
name|id
operator|=
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
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
assert|assert
operator|!
name|entries
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
assert|;
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
name|streamLength
operator|-
name|entry
operator|.
name|offset
expr_stmt|;
block|}
return|return
name|entries
return|;
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
annotation|@
name|Override
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
operator|!
name|isOpen
condition|)
block|{
comment|// allow double close - usually to be consistent with other closeables
return|return;
comment|// already closed
block|}
name|isOpen
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
assert|assert
name|openForWrite
assert|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
specifier|synchronized
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
assert|assert
operator|!
name|openForWrite
assert|;
specifier|final
name|String
name|id
init|=
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
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
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No sub-file with id "
operator|+
name|id
operator|+
literal|" found (fileName="
operator|+
name|name
operator|+
literal|" files: "
operator|+
name|entries
operator|.
name|keySet
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|handle
operator|.
name|openSlice
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|offset
argument_list|,
name|entry
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Returns an array of strings, one for each file in the directory. */
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
index|[]
name|res
decl_stmt|;
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|res
operator|=
name|writer
operator|.
name|listAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|res
operator|=
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
comment|// Add the segment name
name|String
name|seg
init|=
name|fileName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|fileName
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
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
name|res
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|seg
operator|+
name|res
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/** Returns true iff a file with the given name exists. */
annotation|@
name|Override
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|writer
operator|!=
literal|null
condition|)
block|{
return|return
name|writer
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
name|entries
operator|.
name|containsKey
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/** Not implemented    * @throws UnsupportedOperationException */
annotation|@
name|Override
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
comment|/** Not implemented    * @throws UnsupportedOperationException */
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
comment|/** Returns the length of a file in the directory.    * @throws IOException if the file does not exist */
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|writer
operator|!=
literal|null
condition|)
block|{
return|return
name|writer
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
name|FileEntry
name|e
init|=
name|entries
operator|.
name|get
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
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
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
return|return
name|e
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|writer
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Not implemented    * @throws UnsupportedOperationException */
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|createSlicer
specifier|public
name|IndexInputSlicer
name|createSlicer
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
assert|assert
operator|!
name|openForWrite
assert|;
specifier|final
name|String
name|id
init|=
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
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
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No sub-file with id "
operator|+
name|id
operator|+
literal|" found (fileName="
operator|+
name|name
operator|+
literal|" files: "
operator|+
name|entries
operator|.
name|keySet
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
operator|new
name|IndexInputSlicer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|IndexInput
name|openSlice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|handle
operator|.
name|openSlice
argument_list|(
name|sliceDescription
argument_list|,
name|entry
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openFullSlice
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|openSlice
argument_list|(
literal|"full-slice"
argument_list|,
literal|0
argument_list|,
name|entry
operator|.
name|length
argument_list|)
return|;
block|}
block|}
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
literal|"CompoundFileDirectory(file=\""
operator|+
name|fileName
operator|+
literal|"\" in dir="
operator|+
name|directory
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
