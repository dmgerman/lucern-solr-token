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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|OutputStream
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Combines multiple files into a single compound file.  * The file format:<br>  *<ul>  *<li>VInt fileCount</li>  *<li>{Directory}  *         fileCount entries with the following structure:</li>  *<ul>  *<li>long dataOffset</li>  *<li>UTFString extension</li>  *</ul>  *<li>{File Data}  *         fileCount entries with the raw data of the corresponding file</li>  *</ul>  *  * The fileCount integer indicates how many files are contained in this compound  * file. The {directory} that follows has that many entries. Each directory entry  * contains an encoding identifier, an long pointer to the start of this file's  * data section, and a UTF String with that file's extension.  *  * @author Dmitry Serebrennikov  * @version $Id$  */
end_comment
begin_class
DECL|class|CompoundFileWriter
specifier|final
class|class
name|CompoundFileWriter
block|{
DECL|class|FileEntry
specifier|private
specifier|static
specifier|final
class|class
name|FileEntry
block|{
comment|/** source file */
DECL|field|file
name|String
name|file
decl_stmt|;
comment|/** temporary holder for the start of directory entry for this file */
DECL|field|directoryOffset
name|long
name|directoryOffset
decl_stmt|;
comment|/** temporary holder for the start of this file's data section */
DECL|field|dataOffset
name|long
name|dataOffset
decl_stmt|;
block|}
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
DECL|field|ids
specifier|private
name|HashSet
name|ids
decl_stmt|;
DECL|field|entries
specifier|private
name|LinkedList
name|entries
decl_stmt|;
DECL|field|merged
specifier|private
name|boolean
name|merged
init|=
literal|false
decl_stmt|;
comment|/** Create the compound stream in the specified file. The file name is the      *  entire name (no extensions are added).      */
DECL|method|CompoundFileWriter
specifier|public
name|CompoundFileWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing directory"
argument_list|)
throw|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing name"
argument_list|)
throw|;
name|directory
operator|=
name|dir
expr_stmt|;
name|fileName
operator|=
name|name
expr_stmt|;
name|ids
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|entries
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the directory of the compound file. */
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
comment|/** Returns the name of the compound file. */
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
comment|/** Add a source stream.<code>file</code> is the string by which the       *  sub-stream will be known in the compound stream.      *       *  @throws IllegalStateException if this writer is closed      *  @throws IllegalArgumentException if<code>file</code> is null      *   or if a file with the same name has been added already      */
DECL|method|addFile
specifier|public
name|void
name|addFile
parameter_list|(
name|String
name|file
parameter_list|)
block|{
if|if
condition|(
name|merged
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't add extensions after merge has been called"
argument_list|)
throw|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing source file"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|ids
operator|.
name|add
argument_list|(
name|file
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File "
operator|+
name|file
operator|+
literal|" already added"
argument_list|)
throw|;
name|FileEntry
name|entry
init|=
operator|new
name|FileEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
comment|/** Merge files with the extensions added up to now.      *  All files with these extensions are combined sequentially into the      *  compound stream. After successful merge, the source files      *  are deleted.      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|merged
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Merge already performed"
argument_list|)
throw|;
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No entries to merge have been defined"
argument_list|)
throw|;
name|merged
operator|=
literal|true
expr_stmt|;
comment|// open the compound stream
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|os
operator|=
name|directory
operator|.
name|createFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
comment|// Write the number of entries
name|os
operator|.
name|writeVInt
argument_list|(
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Write the directory with all offsets at 0.
comment|// Remember the positions of directory entries so that we can
comment|// adjust the offsets later
name|Iterator
name|it
init|=
name|entries
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
block|{
name|FileEntry
name|fe
init|=
operator|(
name|FileEntry
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|fe
operator|.
name|directoryOffset
operator|=
name|os
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// for now
name|os
operator|.
name|writeString
argument_list|(
name|fe
operator|.
name|file
argument_list|)
expr_stmt|;
block|}
comment|// Open the files and copy their data into the stream.
comment|// Remember the locations of each file's data section.
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|it
operator|=
name|entries
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FileEntry
name|fe
init|=
operator|(
name|FileEntry
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|fe
operator|.
name|dataOffset
operator|=
name|os
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|copyFile
argument_list|(
name|fe
argument_list|,
name|os
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|// Write the data offsets into the directory of the compound stream
name|it
operator|=
name|entries
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FileEntry
name|fe
init|=
operator|(
name|FileEntry
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|os
operator|.
name|seek
argument_list|(
name|fe
operator|.
name|directoryOffset
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|fe
operator|.
name|dataOffset
argument_list|)
expr_stmt|;
block|}
comment|// Close the output stream. Set the os to null before trying to
comment|// close so that if an exception occurs during the close, the
comment|// finally clause below will not attempt to close the stream
comment|// the second time.
name|OutputStream
name|tmp
init|=
name|os
decl_stmt|;
name|os
operator|=
literal|null
expr_stmt|;
name|tmp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
try|try
block|{
name|os
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
comment|/** Copy the contents of the file with specified extension into the      *  provided output stream. Use the provided buffer for moving data      *  to reduce memory allocation.      */
DECL|method|copyFile
specifier|private
name|void
name|copyFile
parameter_list|(
name|FileEntry
name|source
parameter_list|,
name|OutputStream
name|os
parameter_list|,
name|byte
name|buffer
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|startPtr
init|=
name|os
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|is
operator|=
name|directory
operator|.
name|openFile
argument_list|(
name|source
operator|.
name|file
argument_list|)
expr_stmt|;
name|long
name|length
init|=
name|is
operator|.
name|length
argument_list|()
decl_stmt|;
name|long
name|remainder
init|=
name|length
decl_stmt|;
name|int
name|chunk
init|=
name|buffer
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|remainder
operator|>
literal|0
condition|)
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|chunk
argument_list|,
name|remainder
argument_list|)
decl_stmt|;
name|is
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|remainder
operator|-=
name|len
expr_stmt|;
block|}
comment|// Verify that remainder is 0
if|if
condition|(
name|remainder
operator|!=
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Non-zero remainder length after copying: "
operator|+
name|remainder
operator|+
literal|" (id: "
operator|+
name|source
operator|.
name|file
operator|+
literal|", length: "
operator|+
name|length
operator|+
literal|", buffer size: "
operator|+
name|chunk
operator|+
literal|")"
argument_list|)
throw|;
comment|// Verify that the output length diff is equal to original file
name|long
name|endPtr
init|=
name|os
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|diff
init|=
name|endPtr
operator|-
name|startPtr
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Difference in the output file offsets "
operator|+
name|diff
operator|+
literal|" does not match the original file length "
operator|+
name|length
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
