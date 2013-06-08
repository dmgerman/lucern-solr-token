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
begin_comment
comment|/** Acts like forever growing T[], but internally uses a  *  circular buffer to reuse instances of T.  *   *  @lucene.internal */
end_comment
begin_class
DECL|class|RollingBuffer
specifier|public
specifier|abstract
class|class
name|RollingBuffer
parameter_list|<
name|T
extends|extends
name|RollingBuffer
operator|.
name|Resettable
parameter_list|>
block|{
comment|/**    * Implement to reset an instance    */
DECL|interface|Resettable
specifier|public
specifier|static
interface|interface
name|Resettable
block|{
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
block|}
DECL|field|buffer
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|T
index|[]
name|buffer
init|=
operator|(
name|T
index|[]
operator|)
operator|new
name|RollingBuffer
operator|.
name|Resettable
index|[
literal|8
index|]
decl_stmt|;
comment|// Next array index to write to:
DECL|field|nextWrite
specifier|private
name|int
name|nextWrite
decl_stmt|;
comment|// Next position to write:
DECL|field|nextPos
specifier|private
name|int
name|nextPos
decl_stmt|;
comment|// How many valid Position are held in the
comment|// array:
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|method|RollingBuffer
specifier|public
name|RollingBuffer
parameter_list|()
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|buffer
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|buffer
index|[
name|idx
index|]
operator|=
name|newInstance
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newInstance
specifier|protected
specifier|abstract
name|T
name|newInstance
parameter_list|()
function_decl|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|nextWrite
operator|--
expr_stmt|;
while|while
condition|(
name|count
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|nextWrite
operator|==
operator|-
literal|1
condition|)
block|{
name|nextWrite
operator|=
name|buffer
operator|.
name|length
operator|-
literal|1
expr_stmt|;
block|}
name|buffer
index|[
name|nextWrite
operator|--
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
name|nextWrite
operator|=
literal|0
expr_stmt|;
name|nextPos
operator|=
literal|0
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
comment|// For assert:
DECL|method|inBounds
specifier|private
name|boolean
name|inBounds
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|<
name|nextPos
operator|&&
name|pos
operator|>=
name|nextPos
operator|-
name|count
return|;
block|}
DECL|method|getIndex
specifier|private
name|int
name|getIndex
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|index
init|=
name|nextWrite
operator|-
operator|(
name|nextPos
operator|-
name|pos
operator|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|index
operator|+=
name|buffer
operator|.
name|length
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
comment|/** Get T instance for this absolute position;    *  this is allowed to be arbitrarily far "in the    *  future" but cannot be before the last freeBefore. */
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
comment|//System.out.println("RA.get pos=" + pos + " nextPos=" + nextPos + " nextWrite=" + nextWrite + " count=" + count);
while|while
condition|(
name|pos
operator|>=
name|nextPos
condition|)
block|{
if|if
condition|(
name|count
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
index|[]
name|newBuffer
init|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Resettable
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|count
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
comment|//System.out.println("  grow length=" + newBuffer.length);
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|nextWrite
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|nextWrite
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|nextWrite
argument_list|,
name|nextWrite
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|buffer
operator|.
name|length
init|;
name|i
operator|<
name|newBuffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|newBuffer
index|[
name|i
index|]
operator|=
name|newInstance
argument_list|()
expr_stmt|;
block|}
name|nextWrite
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|buffer
operator|=
name|newBuffer
expr_stmt|;
block|}
if|if
condition|(
name|nextWrite
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
name|nextWrite
operator|=
literal|0
expr_stmt|;
block|}
comment|// Should have already been reset:
name|nextWrite
operator|++
expr_stmt|;
name|nextPos
operator|++
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
assert|assert
name|inBounds
argument_list|(
name|pos
argument_list|)
assert|;
specifier|final
name|int
name|index
init|=
name|getIndex
argument_list|(
name|pos
argument_list|)
decl_stmt|;
comment|//System.out.println("  pos=" + pos + " nextPos=" + nextPos + " -> index=" + index);
comment|//assert buffer[index].pos == pos;
return|return
name|buffer
index|[
name|index
index|]
return|;
block|}
comment|/** Returns the maximum position looked up, or -1 if no   *  position has been looked up sinc reset/init.  */
DECL|method|getMaxPos
specifier|public
name|int
name|getMaxPos
parameter_list|()
block|{
return|return
name|nextPos
operator|-
literal|1
return|;
block|}
DECL|method|freeBefore
specifier|public
name|void
name|freeBefore
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
specifier|final
name|int
name|toFree
init|=
name|count
operator|-
operator|(
name|nextPos
operator|-
name|pos
operator|)
decl_stmt|;
assert|assert
name|toFree
operator|>=
literal|0
assert|;
assert|assert
name|toFree
operator|<=
name|count
operator|:
literal|"toFree="
operator|+
name|toFree
operator|+
literal|" count="
operator|+
name|count
assert|;
name|int
name|index
init|=
name|nextWrite
operator|-
name|count
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|index
operator|+=
name|buffer
operator|.
name|length
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toFree
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|index
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
name|index
operator|=
literal|0
expr_stmt|;
block|}
comment|//System.out.println("  fb idx=" + index);
name|buffer
index|[
name|index
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
name|count
operator|-=
name|toFree
expr_stmt|;
block|}
block|}
end_class
end_unit
