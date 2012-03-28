begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.compound.hyphenation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
operator|.
name|hyphenation
package|;
end_package
begin_comment
comment|/**  * This class implements a simple char vector with access to the underlying  * array.  *   * This class has been taken from the Apache FOP project (http://xmlgraphics.apache.org/fop/). They have been slightly modified.   */
end_comment
begin_class
DECL|class|CharVector
specifier|public
class|class
name|CharVector
implements|implements
name|Cloneable
block|{
comment|/**    * Capacity increment size    */
DECL|field|DEFAULT_BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|2048
decl_stmt|;
DECL|field|blockSize
specifier|private
name|int
name|blockSize
decl_stmt|;
comment|/**    * The encapsulated array    */
DECL|field|array
specifier|private
name|char
index|[]
name|array
decl_stmt|;
comment|/**    * Points to next free item    */
DECL|field|n
specifier|private
name|int
name|n
decl_stmt|;
DECL|method|CharVector
specifier|public
name|CharVector
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|CharVector
specifier|public
name|CharVector
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
if|if
condition|(
name|capacity
operator|>
literal|0
condition|)
block|{
name|blockSize
operator|=
name|capacity
expr_stmt|;
block|}
else|else
block|{
name|blockSize
operator|=
name|DEFAULT_BLOCK_SIZE
expr_stmt|;
block|}
name|array
operator|=
operator|new
name|char
index|[
name|blockSize
index|]
expr_stmt|;
name|n
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|CharVector
specifier|public
name|CharVector
parameter_list|(
name|char
index|[]
name|a
parameter_list|)
block|{
name|blockSize
operator|=
name|DEFAULT_BLOCK_SIZE
expr_stmt|;
name|array
operator|=
name|a
expr_stmt|;
name|n
operator|=
name|a
operator|.
name|length
expr_stmt|;
block|}
DECL|method|CharVector
specifier|public
name|CharVector
parameter_list|(
name|char
index|[]
name|a
parameter_list|,
name|int
name|capacity
parameter_list|)
block|{
if|if
condition|(
name|capacity
operator|>
literal|0
condition|)
block|{
name|blockSize
operator|=
name|capacity
expr_stmt|;
block|}
else|else
block|{
name|blockSize
operator|=
name|DEFAULT_BLOCK_SIZE
expr_stmt|;
block|}
name|array
operator|=
name|a
expr_stmt|;
name|n
operator|=
name|a
operator|.
name|length
expr_stmt|;
block|}
comment|/**    * Reset Vector but don't resize or clear elements    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|n
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|CharVector
name|clone
parameter_list|()
block|{
name|CharVector
name|cv
init|=
operator|new
name|CharVector
argument_list|(
name|array
operator|.
name|clone
argument_list|()
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|cv
operator|.
name|n
operator|=
name|this
operator|.
name|n
expr_stmt|;
return|return
name|cv
return|;
block|}
DECL|method|getArray
specifier|public
name|char
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|array
return|;
block|}
comment|/**    * return number of items in array    */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|n
return|;
block|}
comment|/**    * returns current capacity of array    */
DECL|method|capacity
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|array
operator|.
name|length
return|;
block|}
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|int
name|index
parameter_list|,
name|char
name|val
parameter_list|)
block|{
name|array
index|[
name|index
index|]
operator|=
name|val
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|char
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|array
index|[
name|index
index|]
return|;
block|}
DECL|method|alloc
specifier|public
name|int
name|alloc
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|int
name|index
init|=
name|n
decl_stmt|;
name|int
name|len
init|=
name|array
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|n
operator|+
name|size
operator|>=
name|len
condition|)
block|{
name|char
index|[]
name|aux
init|=
operator|new
name|char
index|[
name|len
operator|+
name|blockSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|aux
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|array
operator|=
name|aux
expr_stmt|;
block|}
name|n
operator|+=
name|size
expr_stmt|;
return|return
name|index
return|;
block|}
DECL|method|trimToSize
specifier|public
name|void
name|trimToSize
parameter_list|()
block|{
if|if
condition|(
name|n
operator|<
name|array
operator|.
name|length
condition|)
block|{
name|char
index|[]
name|aux
init|=
operator|new
name|char
index|[
name|n
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|aux
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|array
operator|=
name|aux
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
