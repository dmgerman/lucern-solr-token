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
name|search
operator|.
name|DocIdSetIterator
import|;
end_import
begin_comment
comment|/**  * Base implementation for a bit set.  * @lucene.internal  */
end_comment
begin_class
DECL|class|BitSet
specifier|public
specifier|abstract
class|class
name|BitSet
implements|implements
name|MutableBits
implements|,
name|Accountable
block|{
comment|/** Set the bit at<code>i</code>. */
DECL|method|set
specifier|public
specifier|abstract
name|void
name|set
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
comment|/** Clears a range of bits.    *    * @param startIndex lower index    * @param endIndex one-past the last bit to clear    */
DECL|method|clear
specifier|public
specifier|abstract
name|void
name|clear
parameter_list|(
name|int
name|startIndex
parameter_list|,
name|int
name|endIndex
parameter_list|)
function_decl|;
comment|/**    * Return the number of bits that are set.    * NOTE: this method is likely to run in linear time    */
DECL|method|cardinality
specifier|public
specifier|abstract
name|int
name|cardinality
parameter_list|()
function_decl|;
comment|/**    * Return an approximation of the cardinality of this set. Some    * implementations may trade accuracy for speed if they have the ability to    * estimate the cardinality of the set without iterating over all the data.    * The default implementation returns {@link #cardinality()}.    */
DECL|method|approximateCardinality
specifier|public
name|int
name|approximateCardinality
parameter_list|()
block|{
return|return
name|cardinality
argument_list|()
return|;
block|}
comment|/** Returns the index of the last set bit before or on the index specified.    *  -1 is returned if there are no more set bits.    */
DECL|method|prevSetBit
specifier|public
specifier|abstract
name|int
name|prevSetBit
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** Returns the index of the first set bit starting at the index specified.    *  {@link DocIdSetIterator#NO_MORE_DOCS} is returned if there are no more set bits.    */
DECL|method|nextSetBit
specifier|public
specifier|abstract
name|int
name|nextSetBit
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** Assert that the current doc is -1. */
DECL|method|assertUnpositioned
specifier|protected
specifier|final
name|void
name|assertUnpositioned
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
block|{
if|if
condition|(
name|iter
operator|.
name|docID
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This operation only works with an unpositioned iterator, got current position = "
operator|+
name|iter
operator|.
name|docID
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Does in-place OR of the bits provided by the iterator. The state of the    *  iterator after this operation terminates is undefined. */
DECL|method|or
specifier|public
name|void
name|or
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|assertUnpositioned
argument_list|(
name|iter
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LeapFrogCallBack
specifier|private
specifier|static
specifier|abstract
class|class
name|LeapFrogCallBack
block|{
DECL|method|onMatch
specifier|abstract
name|void
name|onMatch
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
DECL|method|finish
name|void
name|finish
parameter_list|()
block|{}
block|}
comment|/** Performs a leap frog between this and the provided iterator in order to find common documents. */
DECL|method|leapFrog
specifier|private
name|void
name|leapFrog
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|,
name|LeapFrogCallBack
name|callback
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|length
init|=
name|length
argument_list|()
decl_stmt|;
name|int
name|bitSetDoc
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|disiDoc
init|=
name|iter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// invariant: bitSetDoc<= disiDoc
assert|assert
name|bitSetDoc
operator|<=
name|disiDoc
assert|;
if|if
condition|(
name|disiDoc
operator|>=
name|length
condition|)
block|{
name|callback
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|bitSetDoc
operator|<
name|disiDoc
condition|)
block|{
name|bitSetDoc
operator|=
name|nextSetBit
argument_list|(
name|disiDoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitSetDoc
operator|==
name|disiDoc
condition|)
block|{
name|callback
operator|.
name|onMatch
argument_list|(
name|bitSetDoc
argument_list|)
expr_stmt|;
name|disiDoc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|disiDoc
operator|=
name|iter
operator|.
name|advance
argument_list|(
name|bitSetDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Does in-place AND of the bits provided by the iterator. The state of the    *  iterator after this operation terminates is undefined. */
DECL|method|and
specifier|public
name|void
name|and
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|assertUnpositioned
argument_list|(
name|iter
argument_list|)
expr_stmt|;
name|leapFrog
argument_list|(
name|iter
argument_list|,
operator|new
name|LeapFrogCallBack
argument_list|()
block|{
name|int
name|previous
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|clear
argument_list|(
name|previous
operator|+
literal|1
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|previous
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
name|previous
operator|+
literal|1
operator|<
name|length
argument_list|()
condition|)
block|{
name|clear
argument_list|(
name|previous
operator|+
literal|1
argument_list|,
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** this = this AND NOT other. The state of the iterator after this operation    *  terminates is undefined. */
DECL|method|andNot
specifier|public
name|void
name|andNot
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
name|assertUnpositioned
argument_list|(
name|iter
argument_list|)
expr_stmt|;
name|leapFrog
argument_list|(
name|iter
argument_list|,
operator|new
name|LeapFrogCallBack
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|clear
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
