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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
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
begin_class
DECL|class|PackedReaderIterator
specifier|final
class|class
name|PackedReaderIterator
extends|extends
name|PackedInts
operator|.
name|ReaderIteratorImpl
block|{
DECL|field|pending
specifier|private
name|long
name|pending
decl_stmt|;
DECL|field|pendingBitsLeft
specifier|private
name|int
name|pendingBitsLeft
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
comment|// masks[n-1] masks for bottom n bits
DECL|field|masks
specifier|private
specifier|final
name|long
index|[]
name|masks
decl_stmt|;
DECL|method|PackedReaderIterator
specifier|public
name|PackedReaderIterator
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|,
name|IndexInput
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|masks
operator|=
operator|new
name|long
index|[
name|bitsPerValue
index|]
expr_stmt|;
name|long
name|v
init|=
literal|1
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
name|bitsPerValue
condition|;
name|i
operator|++
control|)
block|{
name|v
operator|*=
literal|2
expr_stmt|;
name|masks
index|[
name|i
index|]
operator|=
name|v
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|next
specifier|public
name|long
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pendingBitsLeft
operator|==
literal|0
condition|)
block|{
name|pending
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|pendingBitsLeft
operator|=
literal|64
expr_stmt|;
block|}
specifier|final
name|long
name|result
decl_stmt|;
if|if
condition|(
name|pendingBitsLeft
operator|>=
name|bitsPerValue
condition|)
block|{
comment|// not split
name|result
operator|=
operator|(
name|pending
operator|>>
operator|(
name|pendingBitsLeft
operator|-
name|bitsPerValue
operator|)
operator|)
operator|&
name|masks
index|[
name|bitsPerValue
operator|-
literal|1
index|]
expr_stmt|;
name|pendingBitsLeft
operator|-=
name|bitsPerValue
expr_stmt|;
block|}
else|else
block|{
comment|// split
specifier|final
name|int
name|bits1
init|=
name|bitsPerValue
operator|-
name|pendingBitsLeft
decl_stmt|;
specifier|final
name|long
name|result1
init|=
operator|(
name|pending
operator|&
name|masks
index|[
name|pendingBitsLeft
operator|-
literal|1
index|]
operator|)
operator|<<
name|bits1
decl_stmt|;
name|pending
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
specifier|final
name|long
name|result2
init|=
operator|(
name|pending
operator|>>
operator|(
literal|64
operator|-
name|bits1
operator|)
operator|)
operator|&
name|masks
index|[
name|bits1
operator|-
literal|1
index|]
decl_stmt|;
name|pendingBitsLeft
operator|=
literal|64
operator|+
name|pendingBitsLeft
operator|-
name|bitsPerValue
expr_stmt|;
name|result
operator|=
name|result1
operator||
name|result2
expr_stmt|;
block|}
operator|++
name|position
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|ord
specifier|public
name|int
name|ord
parameter_list|()
block|{
return|return
name|position
return|;
block|}
DECL|method|advance
specifier|public
name|long
name|advance
parameter_list|(
specifier|final
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|ord
operator|<
name|valueCount
operator|:
literal|"ord must be less than valueCount"
assert|;
assert|assert
name|ord
operator|>
name|position
operator|:
literal|"ord must be greater than the current position"
assert|;
specifier|final
name|long
name|bits
init|=
operator|(
name|long
operator|)
name|bitsPerValue
decl_stmt|;
specifier|final
name|int
name|posToSkip
init|=
name|ord
operator|-
literal|1
operator|-
name|position
decl_stmt|;
specifier|final
name|long
name|bitsToSkip
init|=
operator|(
name|bits
operator|*
operator|(
name|long
operator|)
name|posToSkip
operator|)
decl_stmt|;
if|if
condition|(
name|bitsToSkip
operator|<
name|pendingBitsLeft
condition|)
block|{
comment|// enough bits left - no seek required
name|pendingBitsLeft
operator|-=
name|bitsToSkip
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|skip
init|=
name|bitsToSkip
operator|-
name|pendingBitsLeft
decl_stmt|;
specifier|final
name|long
name|closestByte
init|=
operator|(
name|skip
operator|>>
literal|6
operator|)
operator|<<
literal|3
decl_stmt|;
if|if
condition|(
name|closestByte
operator|!=
literal|0
condition|)
block|{
comment|// need to seek
specifier|final
name|long
name|filePointer
init|=
name|in
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|filePointer
operator|+
name|closestByte
argument_list|)
expr_stmt|;
block|}
name|pending
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|pendingBitsLeft
operator|=
literal|64
operator|-
call|(
name|int
call|)
argument_list|(
name|skip
operator|%
literal|64
argument_list|)
expr_stmt|;
block|}
name|position
operator|=
name|ord
operator|-
literal|1
expr_stmt|;
return|return
name|next
argument_list|()
return|;
block|}
block|}
end_class
end_unit
