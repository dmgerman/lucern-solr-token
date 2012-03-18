begin_unit
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
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
name|DataInput
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
name|RamUsageEstimator
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
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Direct wrapping of 8 bit values to a backing array of bytes.  * @lucene.internal  */
end_comment
begin_class
DECL|class|Direct8
class|class
name|Direct8
extends|extends
name|PackedInts
operator|.
name|ReaderImpl
implements|implements
name|PackedInts
operator|.
name|Mutable
block|{
DECL|field|values
specifier|private
name|byte
index|[]
name|values
decl_stmt|;
DECL|field|BITS_PER_VALUE
specifier|private
specifier|static
specifier|final
name|int
name|BITS_PER_VALUE
init|=
literal|8
decl_stmt|;
DECL|method|Direct8
specifier|public
name|Direct8
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|byte
index|[
name|valueCount
index|]
expr_stmt|;
block|}
DECL|method|Direct8
specifier|public
name|Direct8
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|valueCount
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|values
init|=
operator|new
name|byte
index|[
name|valueCount
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
name|valueCount
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|mod
init|=
name|valueCount
operator|%
literal|8
decl_stmt|;
if|if
condition|(
name|mod
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
name|pad
init|=
literal|8
operator|-
name|mod
decl_stmt|;
comment|// round out long
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pad
condition|;
name|i
operator|++
control|)
block|{
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
comment|/**    * Creates an array backed by the given values.    *</p><p>    * Note: The values are used directly, so changes to the given values will    * affect the structure.    * @param values used as the internal backing array.    */
DECL|method|Direct8
specifier|public
name|Direct8
parameter_list|(
name|byte
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|values
operator|.
name|length
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|size
argument_list|()
assert|;
return|return
literal|0xFFL
operator|&
name|values
index|[
name|index
index|]
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
specifier|final
name|int
name|index
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
name|values
index|[
name|index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|values
argument_list|)
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
