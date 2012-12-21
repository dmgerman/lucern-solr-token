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
name|DataOutput
import|;
end_import
begin_comment
comment|/**  * A {@link DataOutput} wrapper to write unaligned, variable-length packed  * integers.  * @see PackedDataInput  * @lucene.internal  */
end_comment
begin_class
DECL|class|PackedDataOutput
specifier|public
specifier|final
class|class
name|PackedDataOutput
block|{
DECL|field|out
specifier|final
name|DataOutput
name|out
decl_stmt|;
DECL|field|current
name|long
name|current
decl_stmt|;
DECL|field|remainingBits
name|int
name|remainingBits
decl_stmt|;
comment|/**    * Create a new instance that wraps<code>out</code>.    */
DECL|method|PackedDataOutput
specifier|public
name|PackedDataOutput
parameter_list|(
name|DataOutput
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|current
operator|=
literal|0
expr_stmt|;
name|remainingBits
operator|=
literal|8
expr_stmt|;
block|}
comment|/**    * Write a value using exactly<code>bitsPerValue</code> bits.    */
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|value
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bitsPerValue
operator|==
literal|64
operator|||
operator|(
name|value
operator|>=
literal|0
operator|&&
name|value
operator|<=
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
operator|)
assert|;
while|while
condition|(
name|bitsPerValue
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|remainingBits
operator|==
literal|0
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|current
argument_list|)
expr_stmt|;
name|current
operator|=
literal|0L
expr_stmt|;
name|remainingBits
operator|=
literal|8
expr_stmt|;
block|}
specifier|final
name|int
name|bits
init|=
name|Math
operator|.
name|min
argument_list|(
name|remainingBits
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|current
operator|=
name|current
operator||
operator|(
operator|(
operator|(
name|value
operator|>>>
operator|(
name|bitsPerValue
operator|-
name|bits
operator|)
operator|)
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|bits
operator|)
operator|-
literal|1
operator|)
operator|)
operator|<<
operator|(
name|remainingBits
operator|-
name|bits
operator|)
operator|)
expr_stmt|;
name|bitsPerValue
operator|-=
name|bits
expr_stmt|;
name|remainingBits
operator|-=
name|bits
expr_stmt|;
block|}
block|}
comment|/**    * Flush pending bits to the underlying {@link DataOutput}.    */
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|remainingBits
operator|<
literal|8
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|current
argument_list|)
expr_stmt|;
block|}
name|remainingBits
operator|=
literal|8
expr_stmt|;
name|current
operator|=
literal|0L
expr_stmt|;
block|}
block|}
end_class
end_unit
