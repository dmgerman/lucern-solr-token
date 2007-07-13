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
name|File
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
name|Random
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
name|ArrayList
import|;
end_import
begin_comment
comment|/**  * This is a subclass of RAMDirectory that adds methods  * intented to be used only by unit tests.  * @version $Id: RAMDirectory.java 437897 2006-08-29 01:13:10Z yonik $  */
end_comment
begin_class
DECL|class|MockRAMDirectory
specifier|public
class|class
name|MockRAMDirectory
extends|extends
name|RAMDirectory
block|{
DECL|field|maxSize
name|long
name|maxSize
decl_stmt|;
comment|// Max actual bytes used. This is set by MockRAMOutputStream:
DECL|field|maxUsedSize
name|long
name|maxUsedSize
decl_stmt|;
DECL|field|randomIOExceptionRate
name|double
name|randomIOExceptionRate
decl_stmt|;
DECL|field|randomState
name|Random
name|randomState
decl_stmt|;
DECL|field|noDeleteOpenFile
name|boolean
name|noDeleteOpenFile
init|=
literal|true
decl_stmt|;
comment|// NOTE: we cannot initialize the Map here due to the
comment|// order in which our constructor actually does this
comment|// member initialization vs when it calls super.  It seems
comment|// like super is called, then our members are initialized:
DECL|field|openFiles
name|Map
name|openFiles
decl_stmt|;
DECL|method|MockRAMDirectory
specifier|public
name|MockRAMDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
block|{
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|MockRAMDirectory
specifier|public
name|MockRAMDirectory
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
block|{
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|MockRAMDirectory
specifier|public
name|MockRAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
block|{
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|MockRAMDirectory
specifier|public
name|MockRAMDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
block|{
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setMaxSizeInBytes
specifier|public
name|void
name|setMaxSizeInBytes
parameter_list|(
name|long
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
DECL|method|getMaxSizeInBytes
specifier|public
name|long
name|getMaxSizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxSize
return|;
block|}
comment|/**    * Returns the peek actual storage used (bytes) in this    * directory.    */
DECL|method|getMaxUsedSizeInBytes
specifier|public
name|long
name|getMaxUsedSizeInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxUsedSize
return|;
block|}
DECL|method|resetMaxUsedSizeInBytes
specifier|public
name|void
name|resetMaxUsedSizeInBytes
parameter_list|()
block|{
name|this
operator|.
name|maxUsedSize
operator|=
name|getRecomputedActualSizeInBytes
argument_list|()
expr_stmt|;
block|}
comment|/**    * Emulate windows whereby deleting an open file is not    * allowed (raise IOException).   */
DECL|method|setNoDeleteOpenFile
specifier|public
name|void
name|setNoDeleteOpenFile
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|noDeleteOpenFile
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getNoDeleteOpenFile
specifier|public
name|boolean
name|getNoDeleteOpenFile
parameter_list|()
block|{
return|return
name|noDeleteOpenFile
return|;
block|}
comment|/**    * If 0.0, no exceptions will be thrown.  Else this should    * be a double 0.0 - 1.0.  We will randomly throw an    * IOException on the first write to an OutputStream based    * on this probability.    */
DECL|method|setRandomIOExceptionRate
specifier|public
name|void
name|setRandomIOExceptionRate
parameter_list|(
name|double
name|rate
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
name|randomIOExceptionRate
operator|=
name|rate
expr_stmt|;
comment|// seed so we have deterministic behaviour:
name|randomState
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomIOExceptionRate
specifier|public
name|double
name|getRandomIOExceptionRate
parameter_list|()
block|{
return|return
name|randomIOExceptionRate
return|;
block|}
DECL|method|maybeThrowIOException
name|void
name|maybeThrowIOException
parameter_list|()
throws|throws
name|IOException
block|{
name|maybeThrowDeterministicException
argument_list|()
expr_stmt|;
if|if
condition|(
name|randomIOExceptionRate
operator|>
literal|0.0
condition|)
block|{
name|int
name|number
init|=
name|Math
operator|.
name|abs
argument_list|(
name|randomState
operator|.
name|nextInt
argument_list|()
operator|%
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|number
operator|<
name|randomIOExceptionRate
operator|*
literal|1000
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"a random IOException"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|deleteFile
specifier|public
specifier|synchronized
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|openFiles
init|)
block|{
if|if
condition|(
name|noDeleteOpenFile
operator|&&
name|openFiles
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MockRAMDirectory: file \""
operator|+
name|name
operator|+
literal|"\" is still open: cannot delete"
argument_list|)
throw|;
block|}
block|}
name|super
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
block|{
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|openFiles
init|)
block|{
if|if
condition|(
name|noDeleteOpenFile
operator|&&
name|openFiles
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// RuntimeException instead of IOException because
comment|// super() does not throw IOException currently:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"MockRAMDirectory: file \""
operator|+
name|name
operator|+
literal|"\" is still open: cannot overwrite"
argument_list|)
throw|;
block|}
block|}
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
name|MockRAMOutputStream
argument_list|(
name|this
argument_list|,
name|file
argument_list|)
return|;
block|}
DECL|method|openInput
specifier|public
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
else|else
block|{
synchronized|synchronized
init|(
name|openFiles
init|)
block|{
if|if
condition|(
name|openFiles
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Integer
name|v
init|=
operator|(
name|Integer
operator|)
name|openFiles
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|v
operator|=
operator|new
name|Integer
argument_list|(
name|v
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openFiles
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|MockRAMInputStream
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|file
argument_list|)
return|;
block|}
comment|/** Provided for testing purposes.  Use sizeInBytes() instead. */
DECL|method|getRecomputedSizeInBytes
specifier|public
specifier|synchronized
specifier|final
name|long
name|getRecomputedSizeInBytes
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
name|Iterator
name|it
init|=
name|fileMap
operator|.
name|values
argument_list|()
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
name|size
operator|+=
operator|(
operator|(
name|RAMFile
operator|)
name|it
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getSizeInBytes
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
comment|/** Like getRecomputedSizeInBytes(), but, uses actual file    * lengths rather than buffer allocations (which are    * quantized up to nearest    * RAMOutputStream.BUFFER_SIZE (now 1024) bytes.    */
DECL|method|getRecomputedActualSizeInBytes
specifier|final
name|long
name|getRecomputedActualSizeInBytes
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
name|Iterator
name|it
init|=
name|fileMap
operator|.
name|values
argument_list|()
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
name|size
operator|+=
operator|(
operator|(
name|RAMFile
operator|)
name|it
operator|.
name|next
argument_list|()
operator|)
operator|.
name|length
expr_stmt|;
return|return
name|size
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|openFiles
operator|==
literal|null
condition|)
block|{
name|openFiles
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|openFiles
init|)
block|{
if|if
condition|(
name|noDeleteOpenFile
operator|&&
name|openFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// RuntimeException instead of IOException because
comment|// super() does not throw IOException currently:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"MockRAMDirectory: cannot close: there are still open files: "
operator|+
name|openFiles
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Objects that represent fail-able conditions. Objects of a derived    * class are created and registered with the mock directory. After    * register, each object will be invoked once for each first write    * of a file, giving the object a chance to throw an IOException.    */
DECL|class|Failure
specifier|public
specifier|static
class|class
name|Failure
block|{
comment|/**      * eval is called on the first write of every new file.      */
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockRAMDirectory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{ }
comment|/**      * reset should set the state of the failure to its default      * (freshly constructed) state. Reset is convenient for tests      * that want to create one failure object and then reuse it in      * multiple cases. This, combined with the fact that Failure      * subclasses are often anonymous classes makes reset difficult to      * do otherwise.      *      * A typical example of use is      * Failure failure = new Failure() { ... };      * ...      * mock.failOn(failure.reset())      */
DECL|method|reset
specifier|public
name|Failure
name|reset
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
DECL|field|failures
name|ArrayList
name|failures
decl_stmt|;
comment|/**    * add a Failure object to the list of objects to be evaluated    * at every potential failure point    */
DECL|method|failOn
specifier|public
name|void
name|failOn
parameter_list|(
name|Failure
name|fail
parameter_list|)
block|{
if|if
condition|(
name|failures
operator|==
literal|null
condition|)
block|{
name|failures
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
name|failures
operator|.
name|add
argument_list|(
name|fail
argument_list|)
expr_stmt|;
block|}
comment|/**    * Itterate through the failures list, giving each object a    * chance to throw an IOE    */
DECL|method|maybeThrowDeterministicException
name|void
name|maybeThrowDeterministicException
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|failures
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|failures
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|Failure
operator|)
name|failures
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|eval
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
