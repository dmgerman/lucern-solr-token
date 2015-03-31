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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** A {@link CompositeReader} which reads multiple indexes, appending  *  their content. It can be used to create a view on several  *  sub-readers (like {@link DirectoryReader}) and execute searches on it.  *   *<p> For efficiency, in this API documents are often referred to via  *<i>document numbers</i>, non-negative integers which each name a unique  * document in the index.  These document numbers are ephemeral -- they may change  * as documents are added to and deleted from an index.  Clients should thus not  * rely on a given document having the same number between sessions.  *   *<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  * IndexReader} instances are completely thread  * safe, meaning multiple threads can call any of its methods,  * concurrently.  If your application requires external  * synchronization, you should<b>not</b> synchronize on the  *<code>IndexReader</code> instance; use your own  * (non-Lucene) objects instead.  */
end_comment
begin_class
DECL|class|MultiReader
specifier|public
class|class
name|MultiReader
extends|extends
name|BaseCompositeReader
argument_list|<
name|IndexReader
argument_list|>
block|{
DECL|field|closeSubReaders
specifier|private
specifier|final
name|boolean
name|closeSubReaders
decl_stmt|;
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
comment|/**    *<p>Construct a MultiReader aggregating the named set of (sub)readers.    * @param subReaders set of (sub)readers; this array will be cloned.    * @param closeSubReaders indicates whether the subreaders should be closed    * when this MultiReader is closed    */
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
name|this
operator|.
name|closeSubReaders
operator|=
name|closeSubReaders
expr_stmt|;
if|if
condition|(
operator|!
name|closeSubReaders
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
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
specifier|final
name|IndexReader
name|r
range|:
name|getSequentialSubReaders
argument_list|()
control|)
block|{
try|try
block|{
if|if
condition|(
name|closeSubReaders
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|r
operator|.
name|decRef
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
block|}
end_class
end_unit
