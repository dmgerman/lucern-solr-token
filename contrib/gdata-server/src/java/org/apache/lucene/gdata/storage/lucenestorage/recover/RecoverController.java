begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage.recover
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|recover
package|;
end_package
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
name|BufferedWriter
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
name|FileReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|Writer
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
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
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|gdata
operator|.
name|storage
operator|.
name|StorageException
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
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageEntryWrapper
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
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageModifier
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
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageEntryWrapper
operator|.
name|StorageOperation
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|RecoverController
specifier|public
class|class
name|RecoverController
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RecoverController
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recoverDirectory
specifier|private
specifier|final
name|File
name|recoverDirectory
decl_stmt|;
DECL|field|FILE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|FILE_SUFFIX
init|=
literal|".rec"
decl_stmt|;
DECL|field|currentRecoverFile
specifier|private
name|File
name|currentRecoverFile
decl_stmt|;
DECL|field|writer
specifier|private
name|RecoverWriter
name|writer
decl_stmt|;
DECL|field|fileWriter
specifier|private
name|Writer
name|fileWriter
decl_stmt|;
DECL|field|fileReader
specifier|private
name|BufferedReader
name|fileReader
decl_stmt|;
DECL|field|reader
specifier|private
name|RecoverReader
name|reader
decl_stmt|;
DECL|field|lock
specifier|private
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|recover
specifier|private
specifier|final
name|boolean
name|recover
decl_stmt|;
DECL|field|keepRecoverFiles
specifier|private
specifier|final
name|boolean
name|keepRecoverFiles
decl_stmt|;
comment|/**      * @param recoverDirectory      * @param recover      * @param keepRecoverFiles      */
DECL|method|RecoverController
specifier|public
name|RecoverController
parameter_list|(
specifier|final
name|File
name|recoverDirectory
parameter_list|,
name|boolean
name|recover
parameter_list|,
name|boolean
name|keepRecoverFiles
parameter_list|)
block|{
if|if
condition|(
name|recoverDirectory
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"directory must not be null"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|recoverDirectory
operator|.
name|exists
argument_list|()
condition|)
name|recoverDirectory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|recoverDirectory
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"the given File is not a directory -- "
operator|+
name|recoverDirectory
argument_list|)
throw|;
name|this
operator|.
name|recover
operator|=
name|recover
expr_stmt|;
name|this
operator|.
name|keepRecoverFiles
operator|=
name|keepRecoverFiles
expr_stmt|;
name|this
operator|.
name|recoverDirectory
operator|=
name|recoverDirectory
expr_stmt|;
block|}
comment|/**  * @param wrapper  * @throws RecoverException  */
DECL|method|storageModified
specifier|public
name|void
name|storageModified
parameter_list|(
name|StorageEntryWrapper
name|wrapper
parameter_list|)
throws|throws
name|RecoverException
block|{
comment|// prevent deadlock either recovering or writing
if|if
condition|(
name|this
operator|.
name|recover
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't write entry, Recovercontroller is initialized in recover mode"
argument_list|)
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|writer
operator|.
name|writeEntry
argument_list|(
name|wrapper
argument_list|,
name|this
operator|.
name|fileWriter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Writing entry failed -- create new recover file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Writing entry failed -- create new recover file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|this
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @param modifier      */
DECL|method|recoverEntries
specifier|public
name|void
name|recoverEntries
parameter_list|(
specifier|final
name|StorageModifier
name|modifier
parameter_list|)
block|{
comment|// prevent deadlock either recovering or writing
if|if
condition|(
operator|!
name|this
operator|.
name|recover
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't recover entries, Recovercontroller is initialized in write mode"
argument_list|)
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|reader
operator|=
operator|new
name|RecoverReader
argument_list|()
expr_stmt|;
name|File
index|[]
name|files
init|=
name|this
operator|.
name|recoverDirectory
operator|.
name|listFiles
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|files
index|[
name|i
index|]
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recover file -- "
operator|+
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileReader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|entryList
init|=
name|this
operator|.
name|reader
operator|.
name|recoverEntries
argument_list|(
name|this
operator|.
name|fileReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|entryList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|storeEntries
argument_list|(
name|entryList
argument_list|,
name|modifier
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileReader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|keepRecoverFiles
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering file -- "
operator|+
name|files
index|[
name|i
index|]
operator|+
literal|" successful, delete file"
argument_list|)
expr_stmt|;
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't store recover entries for file: "
operator|+
name|files
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|" -- keep file "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't recover entries for file: "
operator|+
name|files
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|" -- keep file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|storeEntries
specifier|protected
name|void
name|storeEntries
parameter_list|(
specifier|final
name|List
argument_list|<
name|StorageEntryWrapper
argument_list|>
name|entries
parameter_list|,
specifier|final
name|StorageModifier
name|modifier
parameter_list|)
throws|throws
name|StorageException
throws|,
name|IOException
block|{
for|for
control|(
name|StorageEntryWrapper
name|wrapper
range|:
name|entries
control|)
block|{
if|if
condition|(
name|wrapper
operator|.
name|getOperation
argument_list|()
operator|==
name|StorageOperation
operator|.
name|DELETE
condition|)
name|modifier
operator|.
name|deleteEntry
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|wrapper
operator|.
name|getOperation
argument_list|()
operator|==
name|StorageOperation
operator|.
name|INSERT
condition|)
name|modifier
operator|.
name|insertEntry
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|wrapper
operator|.
name|getOperation
argument_list|()
operator|==
name|StorageOperation
operator|.
name|UPDATE
condition|)
name|modifier
operator|.
name|updateEntry
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|forceWrite
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @throws IOException      */
DECL|method|initialize
specifier|public
specifier|synchronized
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|recover
condition|)
return|return;
name|String
name|filename
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|FILE_SUFFIX
decl_stmt|;
name|this
operator|.
name|currentRecoverFile
operator|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|recoverDirectory
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|RecoverWriter
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileWriter
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|this
operator|.
name|currentRecoverFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws RecoverException      */
DECL|method|destroy
specifier|public
specifier|synchronized
name|void
name|destroy
parameter_list|()
throws|throws
name|RecoverException
block|{
if|if
condition|(
name|this
operator|.
name|fileWriter
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|fileWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|keepRecoverFiles
operator|&&
name|this
operator|.
name|currentRecoverFile
operator|!=
literal|null
condition|)
name|this
operator|.
name|currentRecoverFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RecoverException
argument_list|(
literal|"Can't close recover writer "
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|this
operator|.
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @return<code>true</code> if the RecoverController is initialized in recover mode, otherwise<code>false</code>      */
DECL|method|isRecovering
specifier|public
name|boolean
name|isRecovering
parameter_list|()
block|{
return|return
name|this
operator|.
name|recover
return|;
block|}
block|}
end_class
end_unit
