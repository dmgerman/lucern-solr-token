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
comment|/** An IndexReader which reads multiple indexes, appending  *  their content. */
end_comment
begin_class
DECL|class|MultiReader
specifier|public
class|class
name|MultiReader
extends|extends
name|BaseMultiReader
argument_list|<
name|IndexReader
argument_list|>
block|{
DECL|field|decrefOnClose
specifier|private
specifier|final
name|boolean
index|[]
name|decrefOnClose
decl_stmt|;
comment|// remember which subreaders to decRef on close
comment|/**   *<p>Construct a MultiReader aggregating the named set of (sub)readers.   *<p>Note that all subreaders are closed if this Multireader is closed.</p>   * @param subReaders set of (sub)readers   */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
modifier|...
name|subReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|subReaders
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>Construct a MultiReader aggregating the named set of (sub)readers.    * @param subReaders set of (sub)readers    * @param closeSubReaders indicates whether the subreaders should be closed    * when this MultiReader is closed    */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|,
name|boolean
name|closeSubReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|subReaders
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
name|decrefOnClose
operator|=
operator|new
name|boolean
index|[
name|subReaders
operator|.
name|length
index|]
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
name|subReaders
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
name|closeSubReaders
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|decrefOnClose
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|decrefOnClose
index|[
name|i
index|]
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
comment|// used only by openIfChaged
DECL|method|MultiReader
specifier|private
name|MultiReader
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|,
name|boolean
index|[]
name|decrefOnClose
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|subReaders
argument_list|)
expr_stmt|;
name|this
operator|.
name|decrefOnClose
operator|=
name|decrefOnClose
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|synchronized
name|IndexReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|IndexReader
index|[]
name|newSubReaders
init|=
operator|new
name|IndexReader
index|[
name|subReaders
operator|.
name|length
index|]
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|IndexReader
name|newSubReader
init|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSubReader
operator|!=
literal|null
condition|)
block|{
name|newSubReaders
index|[
name|i
index|]
operator|=
name|newSubReader
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|newSubReaders
index|[
name|i
index|]
operator|=
name|subReaders
index|[
name|i
index|]
expr_stmt|;
block|}
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
name|changed
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
name|newSubReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|newSubReaders
index|[
name|i
index|]
operator|!=
name|subReaders
index|[
name|i
index|]
condition|)
block|{
try|try
block|{
name|newSubReaders
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
comment|// keep going - we want to clean up as much as possible
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|changed
condition|)
block|{
name|boolean
index|[]
name|newDecrefOnClose
init|=
operator|new
name|boolean
index|[
name|subReaders
operator|.
name|length
index|]
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|newSubReaders
index|[
name|i
index|]
operator|==
name|subReaders
index|[
name|i
index|]
condition|)
block|{
name|newSubReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|newDecrefOnClose
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MultiReader
argument_list|(
name|newSubReaders
argument_list|,
name|newDecrefOnClose
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
specifier|synchronized
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
name|decrefOnClose
index|[
name|i
index|]
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
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
name|subReaders
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
name|subReaders
index|[
name|i
index|]
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// all subreaders are up to date
return|return
literal|true
return|;
block|}
comment|/** Not implemented.    * @throws UnsupportedOperationException    */
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"MultiReader does not support this method."
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
