begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|FileUtils
specifier|public
class|class
name|FileUtils
block|{
comment|/**    * Resolves a path relative a base directory.    *    *<p>    * This method does what "new File(base,path)"<b>Should</b> do, if it wasn't    * completely lame: If path is absolute, then a File for that path is returned;    * if it's not absolute, then a File is returned using "path" as a child    * of "base")    *</p>    */
DECL|method|resolvePath
specifier|public
specifier|static
name|File
name|resolvePath
parameter_list|(
name|File
name|base
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|File
name|r
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|isAbsolute
argument_list|()
condition|?
name|r
else|:
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|method|copyFile
specifier|public
specifier|static
name|void
name|copyFile
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|FileChannel
name|in
init|=
literal|null
decl_stmt|;
name|FileChannel
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|destination
argument_list|)
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|in
operator|.
name|transferTo
argument_list|(
literal|0
argument_list|,
name|in
operator|.
name|size
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
name|in
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
block|{}
try|try
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
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
block|{}
block|}
block|}
comment|/**    * Copied from Lucene's FSDirectory.fsync(String)    *    * @param fullFile the File to be synced to disk    * @throws IOException if the file could not be synced    */
DECL|method|sync
specifier|public
specifier|static
name|void
name|sync
parameter_list|(
name|File
name|fullFile
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fullFile
operator|==
literal|null
operator|||
operator|!
name|fullFile
operator|.
name|exists
argument_list|()
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File does not exist "
operator|+
name|fullFile
argument_list|)
throw|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
name|IOException
name|exc
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
name|retryCount
operator|<
literal|5
condition|)
block|{
name|retryCount
operator|++
expr_stmt|;
name|RandomAccessFile
name|file
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|fullFile
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
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
name|file
operator|!=
literal|null
condition|)
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|exc
operator|=
name|ioe
expr_stmt|;
try|try
block|{
comment|// Pause 5 msec
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|success
condition|)
comment|// Throw original exception
throw|throw
name|exc
throw|;
block|}
DECL|method|fileExists
specifier|public
specifier|static
name|boolean
name|fileExists
parameter_list|(
name|String
name|filePathString
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|filePathString
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
end_class
end_unit
