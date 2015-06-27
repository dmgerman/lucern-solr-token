begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|DirectoryStream
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
name|FileStore
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
name|FileVisitResult
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
name|FileVisitor
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
name|Files
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
name|StandardOpenOption
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
name|attribute
operator|.
name|BasicFileAttributes
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|LinkedHashMap
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
name|FSDirectory
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
name|FileSwitchDirectory
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
name|FilterDirectory
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
name|RAMDirectory
import|;
end_import
begin_comment
comment|/** This class emulates the new Java 7 "Try-With-Resources" statement.  * Remove once Lucene is on Java 7.  * @lucene.internal */
end_comment
begin_class
DECL|class|IOUtils
specifier|public
specifier|final
class|class
name|IOUtils
block|{
comment|/**    * UTF-8 charset string.    *<p>Where possible, use {@link StandardCharsets#UTF_8} instead,    * as using the String constant may slow things down.    * @see StandardCharsets#UTF_8    */
DECL|field|UTF_8
specifier|public
specifier|static
specifier|final
name|String
name|UTF_8
init|=
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
decl_stmt|;
DECL|method|IOUtils
specifier|private
name|IOUtils
parameter_list|()
block|{}
comment|// no instance
comment|/**    * Closes all given<tt>Closeable</tt>s.  Some of the    *<tt>Closeable</tt>s may be null; they are    * ignored.  After everything is closed, the method either    * throws the first exception it hit while closing, or    * completes normally if there were no exceptions.    *     * @param objects    *          objects to call<tt>close()</tt> on    */
DECL|method|close
specifier|public
specifier|static
name|void
name|close
parameter_list|(
name|Closeable
modifier|...
name|objects
parameter_list|)
throws|throws
name|IOException
block|{
name|close
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|objects
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes all given<tt>Closeable</tt>s.    * @see #close(Closeable...)    */
DECL|method|close
specifier|public
specifier|static
name|void
name|close
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|objects
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
name|th
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
name|reThrow
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions.    * Some of the<tt>Closeable</tt>s may be null, they are ignored.    *     * @param objects    *          objects to call<tt>close()</tt> on    */
DECL|method|closeWhileHandlingException
specifier|public
specifier|static
name|void
name|closeWhileHandlingException
parameter_list|(
name|Closeable
modifier|...
name|objects
parameter_list|)
block|{
name|closeWhileHandlingException
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|objects
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes all given<tt>Closeable</tt>s, suppressing all thrown exceptions.    * @see #closeWhileHandlingException(Closeable...)    */
DECL|method|closeWhileHandlingException
specifier|public
specifier|static
name|void
name|closeWhileHandlingException
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|objects
parameter_list|)
block|{
for|for
control|(
name|Closeable
name|object
range|:
name|objects
control|)
block|{
try|try
block|{
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
name|object
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
block|}
block|}
comment|/** adds a Throwable to the list of suppressed Exceptions of the first Throwable    * @param exception this exception should get the suppressed one added    * @param suppressed the suppressed exception    */
DECL|method|addSuppressed
specifier|private
specifier|static
name|void
name|addSuppressed
parameter_list|(
name|Throwable
name|exception
parameter_list|,
name|Throwable
name|suppressed
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|!=
literal|null
operator|&&
name|suppressed
operator|!=
literal|null
condition|)
block|{
name|exception
operator|.
name|addSuppressed
argument_list|(
name|suppressed
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Wrapping the given {@link InputStream} in a reader using a {@link CharsetDecoder}.    * Unlike Java's defaults this reader will throw an exception if your it detects     * the read charset doesn't match the expected {@link Charset}.     *<p>    * Decoding readers are useful to load configuration files, stopword lists or synonym files    * to detect character set problems. However, it's not recommended to use as a common purpose     * reader.    *     * @param stream the stream to wrap in a reader    * @param charSet the expected charset    * @return a wrapping reader    */
DECL|method|getDecodingReader
specifier|public
specifier|static
name|Reader
name|getDecodingReader
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Charset
name|charSet
parameter_list|)
block|{
specifier|final
name|CharsetDecoder
name|charSetDecoder
init|=
name|charSet
operator|.
name|newDecoder
argument_list|()
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
decl_stmt|;
return|return
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|charSetDecoder
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Opens a Reader for the given resource using a {@link CharsetDecoder}.    * Unlike Java's defaults this reader will throw an exception if your it detects     * the read charset doesn't match the expected {@link Charset}.     *<p>    * Decoding readers are useful to load configuration files, stopword lists or synonym files    * to detect character set problems. However, it's not recommended to use as a common purpose     * reader.    * @param clazz the class used to locate the resource    * @param resource the resource name to load    * @param charSet the expected charset    * @return a reader to read the given file    *     */
DECL|method|getDecodingReader
specifier|public
specifier|static
name|Reader
name|getDecodingReader
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|resource
parameter_list|,
name|Charset
name|charSet
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
expr_stmt|;
specifier|final
name|Reader
name|reader
init|=
name|getDecodingReader
argument_list|(
name|stream
argument_list|,
name|charSet
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|reader
return|;
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
name|close
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Deletes all given files, suppressing all thrown IOExceptions.    *<p>    * Note that the files should not be null.    */
DECL|method|deleteFilesIgnoringExceptions
specifier|public
specifier|static
name|void
name|deleteFilesIgnoringExceptions
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
modifier|...
name|files
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|files
control|)
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignored
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
comment|/**    * Deletes all given files, suppressing all thrown IOExceptions.    *<p>    * Some of the files may be null, if so they are ignored.    */
DECL|method|deleteFilesIgnoringExceptions
specifier|public
specifier|static
name|void
name|deleteFilesIgnoringExceptions
parameter_list|(
name|Path
modifier|...
name|files
parameter_list|)
block|{
name|deleteFilesIgnoringExceptions
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|files
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes all given files, suppressing all thrown IOExceptions.    *<p>    * Some of the files may be null, if so they are ignored.    */
DECL|method|deleteFilesIgnoringExceptions
specifier|public
specifier|static
name|void
name|deleteFilesIgnoringExceptions
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Path
argument_list|>
name|files
parameter_list|)
block|{
for|for
control|(
name|Path
name|name
range|:
name|files
control|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignored
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
comment|/**    * Deletes all given<tt>Path</tt>s, if they exist.  Some of the    *<tt>File</tt>s may be null; they are    * ignored.  After everything is deleted, the method either    * throws the first exception it hit while deleting, or    * completes normally if there were no exceptions.    *     * @param files files to delete    */
DECL|method|deleteFilesIfExist
specifier|public
specifier|static
name|void
name|deleteFilesIfExist
parameter_list|(
name|Path
modifier|...
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteFilesIfExist
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|files
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes all given<tt>Path</tt>s, if they exist.  Some of the    *<tt>File</tt>s may be null; they are    * ignored.  After everything is deleted, the method either    * throws the first exception it hit while deleting, or    * completes normally if there were no exceptions.    *     * @param files files to delete    */
DECL|method|deleteFilesIfExist
specifier|public
specifier|static
name|void
name|deleteFilesIfExist
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Path
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Path
name|file
range|:
name|files
control|)
block|{
try|try
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|addSuppressed
argument_list|(
name|th
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|==
literal|null
condition|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
name|reThrow
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes one or more files or directories (and everything underneath it).    *     * @throws IOException if any of the given files (or their subhierarchy files in case    * of directories) cannot be removed.    */
DECL|method|rm
specifier|public
specifier|static
name|void
name|rm
parameter_list|(
name|Path
modifier|...
name|locations
parameter_list|)
throws|throws
name|IOException
block|{
name|LinkedHashMap
argument_list|<
name|Path
argument_list|,
name|Throwable
argument_list|>
name|unremoved
init|=
name|rm
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|Path
argument_list|,
name|Throwable
argument_list|>
argument_list|()
argument_list|,
name|locations
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|unremoved
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Could not remove the following files (in the order of attempts):\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|Throwable
argument_list|>
name|kv
range|:
name|unremoved
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"   "
argument_list|)
operator|.
name|append
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|kv
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|rm
specifier|private
specifier|static
name|LinkedHashMap
argument_list|<
name|Path
argument_list|,
name|Throwable
argument_list|>
name|rm
parameter_list|(
specifier|final
name|LinkedHashMap
argument_list|<
name|Path
argument_list|,
name|Throwable
argument_list|>
name|unremoved
parameter_list|,
name|Path
modifier|...
name|locations
parameter_list|)
block|{
if|if
condition|(
name|locations
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Path
name|location
range|:
name|locations
control|)
block|{
comment|// TODO: remove this leniency!
if|if
condition|(
name|location
operator|!=
literal|null
operator|&&
name|Files
operator|.
name|exists
argument_list|(
name|location
argument_list|)
condition|)
block|{
try|try
block|{
name|Files
operator|.
name|walkFileTree
argument_list|(
name|location
argument_list|,
operator|new
name|FileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|preVisitDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|postVisitDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|IOException
name|impossible
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|impossible
operator|==
literal|null
assert|;
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|unremoved
operator|.
name|put
argument_list|(
name|dir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|exc
parameter_list|)
block|{
name|unremoved
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|exc
argument_list|)
expr_stmt|;
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFileFailed
parameter_list|(
name|Path
name|file
parameter_list|,
name|IOException
name|exc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|exc
operator|!=
literal|null
condition|)
block|{
name|unremoved
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|exc
argument_list|)
expr_stmt|;
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|impossible
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"visitor threw exception"
argument_list|,
name|impossible
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|unremoved
return|;
block|}
comment|/**    * Simple utility method that takes a previously caught    * {@code Throwable} and rethrows either {@code    * IOException} or an unchecked exception.  If the    * argument is null then this method does nothing.    */
DECL|method|reThrow
specifier|public
specifier|static
name|void
name|reThrow
parameter_list|(
name|Throwable
name|th
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|th
throw|;
block|}
name|reThrowUnchecked
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Simple utility method that takes a previously caught    * {@code Throwable} and rethrows it as an unchecked exception.    * If the argument is null then this method does nothing.    */
DECL|method|reThrowUnchecked
specifier|public
specifier|static
name|void
name|reThrowUnchecked
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|th
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|th
throw|;
block|}
if|if
condition|(
name|th
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|th
throw|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
comment|/**    * Ensure that any writes to the given file is written to the storage device that contains it.    * @param fileToSync the file to fsync    * @param isDir if true, the given file is a directory (we open for read and ignore IOExceptions,    *  because not all file systems and operating systems allow to fsync on a directory)    */
DECL|method|fsync
specifier|public
specifier|static
name|void
name|fsync
parameter_list|(
name|Path
name|fileToSync
parameter_list|,
name|boolean
name|isDir
parameter_list|)
throws|throws
name|IOException
block|{
name|IOException
name|exc
init|=
literal|null
decl_stmt|;
comment|// If the file is a directory we have to open read-only, for regular files we must open r/w for the fsync to have an effect.
comment|// See http://blog.httrack.com/blog/2013/11/15/everything-you-always-wanted-to-know-about-fsync/
try|try
init|(
specifier|final
name|FileChannel
name|file
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|fileToSync
argument_list|,
name|isDir
condition|?
name|StandardOpenOption
operator|.
name|READ
else|:
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|retry
init|=
literal|0
init|;
name|retry
operator|<
literal|5
condition|;
name|retry
operator|++
control|)
block|{
try|try
block|{
name|file
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|exc
operator|==
literal|null
condition|)
block|{
name|exc
operator|=
name|ioe
expr_stmt|;
block|}
try|try
block|{
comment|// Pause 5 msec
name|Thread
operator|.
name|sleep
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|ThreadInterruptedException
name|ex
init|=
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
decl_stmt|;
name|ex
operator|.
name|addSuppressed
argument_list|(
name|exc
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|exc
operator|==
literal|null
condition|)
block|{
name|exc
operator|=
name|ioe
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isDir
condition|)
block|{
comment|// TODO: LUCENE-6169 - Fix this assert once Java 9 problems are solved!
assert|assert
operator|(
name|Constants
operator|.
name|LINUX
operator|||
name|Constants
operator|.
name|MAC_OS_X
operator|)
operator|==
literal|false
operator|||
name|Constants
operator|.
name|JRE_IS_MINIMUM_JAVA9
operator|:
literal|"On Linux and MacOSX fsyncing a directory should not throw IOException, "
operator|+
literal|"we just don't want to rely on that in production (undocumented). Got: "
operator|+
name|exc
assert|;
comment|// Ignore exception if it is a directory
return|return;
block|}
comment|// Throw original exception
throw|throw
name|exc
throw|;
block|}
comment|/** If the dir is an {@link FSDirectory} or wraps one via possibly    *  nested {@link FilterDirectory} or {@link FileSwitchDirectory},    *  this returns {@link #spins(Path)} for the wrapped directory,    *  else, true.    *    *  @throws IOException if {@code path} does not exist.    *    *  @lucene.internal */
DECL|method|spins
specifier|public
specifier|static
name|boolean
name|spins
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|FilterDirectory
operator|.
name|unwrap
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|FileSwitchDirectory
condition|)
block|{
name|FileSwitchDirectory
name|fsd
init|=
operator|(
name|FileSwitchDirectory
operator|)
name|dir
decl_stmt|;
comment|// Spinning is contagious:
return|return
name|spins
argument_list|(
name|fsd
operator|.
name|getPrimaryDir
argument_list|()
argument_list|)
operator|||
name|spins
argument_list|(
name|fsd
operator|.
name|getSecondaryDir
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dir
operator|instanceof
name|RAMDirectory
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|dir
operator|instanceof
name|FSDirectory
condition|)
block|{
return|return
name|spins
argument_list|(
operator|(
operator|(
name|FSDirectory
operator|)
name|dir
operator|)
operator|.
name|getDirectory
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/** Rough Linux-only heuristics to determine whether the provided    *  {@code Path} is backed by spinning storage.  For example, this    *  returns false if the disk is a solid-state disk.    *    *  @param path a location to check which must exist. the mount point will be determined from this location.    *  @return false if the storage is non-rotational (e.g. an SSD), or true if it is spinning or could not be determined    *  @throws IOException if {@code path} does not exist.    *    *  @lucene.internal */
DECL|method|spins
specifier|public
specifier|static
name|boolean
name|spins
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// resolve symlinks (this will throw exception if the path does not exist)
name|path
operator|=
name|path
operator|.
name|toRealPath
argument_list|()
expr_stmt|;
comment|// Super cowboy approach, but seems to work!
if|if
condition|(
operator|!
name|Constants
operator|.
name|LINUX
condition|)
block|{
return|return
literal|true
return|;
comment|// no detection
block|}
try|try
block|{
return|return
name|spinsLinux
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
comment|// our crazy heuristics can easily trigger SecurityException, AIOOBE, etc ...
return|return
literal|true
return|;
block|}
block|}
comment|// following methods are package-private for testing ONLY
comment|// note: requires a real or fake linux filesystem!
DECL|method|spinsLinux
specifier|static
name|boolean
name|spinsLinux
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
name|getFileStore
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// if fs type is tmpfs, it doesn't spin.
comment|// this won't have a corresponding block device
if|if
condition|(
literal|"tmpfs"
operator|.
name|equals
argument_list|(
name|store
operator|.
name|type
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// get block device name
name|String
name|devName
init|=
name|store
operator|.
name|name
argument_list|()
decl_stmt|;
comment|// not a device (e.g. NFS server)
if|if
condition|(
operator|!
name|devName
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// resolve any symlinks to real block device (e.g. LVM)
comment|// /dev/sda0 -> sda0
comment|// /devices/XXX -> sda0
name|devName
operator|=
name|path
operator|.
name|getRoot
argument_list|()
operator|.
name|resolve
argument_list|(
name|devName
argument_list|)
operator|.
name|toRealPath
argument_list|()
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// now try to find the longest matching device folder in /sys/block
comment|// (that starts with our dev name):
name|Path
name|sysinfo
init|=
name|path
operator|.
name|getRoot
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"sys"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"block"
argument_list|)
decl_stmt|;
name|Path
name|devsysinfo
init|=
literal|null
decl_stmt|;
name|int
name|matchlen
init|=
literal|0
decl_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|sysinfo
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|device
range|:
name|stream
control|)
block|{
name|String
name|name
init|=
name|device
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|>
name|matchlen
operator|&&
name|devName
operator|.
name|startsWith
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|devsysinfo
operator|=
name|device
expr_stmt|;
name|matchlen
operator|=
name|name
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|devsysinfo
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
comment|// give up
block|}
comment|// read first byte from rotational, it's a 1 if it spins.
name|Path
name|rotational
init|=
name|devsysinfo
operator|.
name|resolve
argument_list|(
literal|"queue"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"rotational"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|rotational
argument_list|)
init|)
block|{
return|return
name|stream
operator|.
name|read
argument_list|()
operator|==
literal|'1'
return|;
block|}
block|}
comment|// Files.getFileStore(Path) useless here!
comment|// don't complain, just try it yourself
DECL|method|getFileStore
specifier|static
name|FileStore
name|getFileStore
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
name|Files
operator|.
name|getFileStore
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|mount
init|=
name|getMountPoint
argument_list|(
name|store
argument_list|)
decl_stmt|;
comment|// find the "matching" FileStore from system list, it's the one we want, but only return
comment|// that if it's unambiguous (only one matching):
name|FileStore
name|sameMountPoint
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FileStore
name|fs
range|:
name|path
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getFileStores
argument_list|()
control|)
block|{
if|if
condition|(
name|mount
operator|.
name|equals
argument_list|(
name|getMountPoint
argument_list|(
name|fs
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|sameMountPoint
operator|==
literal|null
condition|)
block|{
name|sameMountPoint
operator|=
name|fs
expr_stmt|;
block|}
else|else
block|{
comment|// more than one filesystem has the same mount point; something is wrong!
comment|// fall back to crappy one we got from Files.getFileStore
return|return
name|store
return|;
block|}
block|}
block|}
if|if
condition|(
name|sameMountPoint
operator|!=
literal|null
condition|)
block|{
comment|// ok, we found only one, use it:
return|return
name|sameMountPoint
return|;
block|}
else|else
block|{
comment|// fall back to crappy one we got from Files.getFileStore
return|return
name|store
return|;
block|}
block|}
comment|// these are hacks that are not guaranteed, may change across JVM versions, etc.
DECL|method|getMountPoint
specifier|static
name|String
name|getMountPoint
parameter_list|(
name|FileStore
name|store
parameter_list|)
block|{
name|String
name|desc
init|=
name|store
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|desc
operator|.
name|lastIndexOf
argument_list|(
literal|" ("
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|desc
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|desc
return|;
block|}
block|}
block|}
end_class
end_unit
