begin_unit
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLongArray
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
name|LongBitSet
import|;
end_import
begin_class
DECL|class|BlockLocks
specifier|public
class|class
name|BlockLocks
block|{
DECL|field|bits
specifier|private
name|AtomicLongArray
name|bits
decl_stmt|;
DECL|field|wlen
specifier|private
name|int
name|wlen
decl_stmt|;
DECL|method|BlockLocks
specifier|public
name|BlockLocks
parameter_list|(
name|long
name|numBits
parameter_list|)
block|{
name|int
name|length
init|=
name|LongBitSet
operator|.
name|bits2words
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|bits
operator|=
operator|new
name|AtomicLongArray
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|wlen
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * Find the next clear bit in the bit set.    *     * @param index    *          index    * @return next next bit    */
DECL|method|nextClearBit
specifier|public
name|int
name|nextClearBit
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|int
name|i
init|=
name|index
operator|>>
literal|6
decl_stmt|;
if|if
condition|(
name|i
operator|>=
name|wlen
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|subIndex
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// index within the word
name|long
name|word
init|=
operator|~
name|bits
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|>>
name|subIndex
decl_stmt|;
comment|// skip all the bits to the right of
comment|// index
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
name|subIndex
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
while|while
condition|(
operator|++
name|i
operator|<
name|wlen
condition|)
block|{
name|word
operator|=
operator|~
name|bits
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Thread safe set operation that will set the bit if and only if the bit was    * not previously set.    *     * @param index    *          the index position to set.    * @return returns true if the bit was set and false if it was already set.    */
DECL|method|set
specifier|public
name|boolean
name|set
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|int
name|wordNum
init|=
name|index
operator|>>
literal|6
decl_stmt|;
comment|// div 64
name|int
name|bit
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// mod 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|bit
decl_stmt|;
name|long
name|word
decl_stmt|,
name|oword
decl_stmt|;
do|do
block|{
name|word
operator|=
name|bits
operator|.
name|get
argument_list|(
name|wordNum
argument_list|)
expr_stmt|;
comment|// if set another thread stole the lock
if|if
condition|(
operator|(
name|word
operator|&
name|bitmask
operator|)
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|oword
operator|=
name|word
expr_stmt|;
name|word
operator||=
name|bitmask
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|bits
operator|.
name|compareAndSet
argument_list|(
name|wordNum
argument_list|,
name|oword
argument_list|,
name|word
argument_list|)
condition|)
do|;
return|return
literal|true
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|int
name|wordNum
init|=
name|index
operator|>>
literal|6
decl_stmt|;
name|int
name|bit
init|=
name|index
operator|&
literal|0x03f
decl_stmt|;
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|bit
decl_stmt|;
name|long
name|word
decl_stmt|,
name|oword
decl_stmt|;
do|do
block|{
name|word
operator|=
name|bits
operator|.
name|get
argument_list|(
name|wordNum
argument_list|)
expr_stmt|;
name|oword
operator|=
name|word
expr_stmt|;
name|word
operator|&=
operator|~
name|bitmask
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|bits
operator|.
name|compareAndSet
argument_list|(
name|wordNum
argument_list|,
name|oword
argument_list|,
name|word
argument_list|)
condition|)
do|;
block|}
block|}
end_class
end_unit
