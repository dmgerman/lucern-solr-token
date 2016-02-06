begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
comment|/** Represents long[], as a slice (offset + length) into an  *  existing long[].  The {@link #longs} member should never be null; use  *  {@link #EMPTY_LONGS} if necessary.  *  *  @lucene.internal */
end_comment
begin_class
DECL|class|LongsRef
specifier|public
specifier|final
class|class
name|LongsRef
implements|implements
name|Comparable
argument_list|<
name|LongsRef
argument_list|>
implements|,
name|Cloneable
block|{
comment|/** An empty long array for convenience */
DECL|field|EMPTY_LONGS
specifier|public
specifier|static
specifier|final
name|long
index|[]
name|EMPTY_LONGS
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
comment|/** The contents of the LongsRef. Should never be {@code null}. */
DECL|field|longs
specifier|public
name|long
index|[]
name|longs
decl_stmt|;
comment|/** Offset of first valid long. */
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
comment|/** Length of used longs. */
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
comment|/** Create a LongsRef with {@link #EMPTY_LONGS} */
DECL|method|LongsRef
specifier|public
name|LongsRef
parameter_list|()
block|{
name|longs
operator|=
name|EMPTY_LONGS
expr_stmt|;
block|}
comment|/**     * Create a LongsRef pointing to a new array of size<code>capacity</code>.    * Offset and length will both be zero.    */
DECL|method|LongsRef
specifier|public
name|LongsRef
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|longs
operator|=
operator|new
name|long
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|/** This instance will directly reference longs w/o making a copy.    * longs should not be null */
DECL|method|LongsRef
specifier|public
name|LongsRef
parameter_list|(
name|long
index|[]
name|longs
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|longs
operator|=
name|longs
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
assert|assert
name|isValid
argument_list|()
assert|;
block|}
comment|/**    * Returns a shallow clone of this instance (the underlying longs are    *<b>not</b> copied and will be shared by both the returned object and this    * object.    *     * @see #deepCopyOf    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|LongsRef
name|clone
parameter_list|()
block|{
return|return
operator|new
name|LongsRef
argument_list|(
name|longs
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|0
decl_stmt|;
specifier|final
name|long
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|longs
index|[
name|i
index|]
operator|^
operator|(
name|longs
index|[
name|i
index|]
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|LongsRef
condition|)
block|{
return|return
name|this
operator|.
name|longsEquals
argument_list|(
operator|(
name|LongsRef
operator|)
name|other
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|longsEquals
specifier|public
name|boolean
name|longsEquals
parameter_list|(
name|LongsRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
name|other
operator|.
name|length
condition|)
block|{
name|int
name|otherUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|long
index|[]
name|otherInts
init|=
name|other
operator|.
name|longs
decl_stmt|;
specifier|final
name|long
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|upto
init|=
name|offset
init|;
name|upto
operator|<
name|end
condition|;
name|upto
operator|++
operator|,
name|otherUpto
operator|++
control|)
block|{
if|if
condition|(
name|longs
index|[
name|upto
index|]
operator|!=
name|otherInts
index|[
name|otherUpto
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Signed int order comparison */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|LongsRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|0
return|;
specifier|final
name|long
index|[]
name|aInts
init|=
name|this
operator|.
name|longs
decl_stmt|;
name|int
name|aUpto
init|=
name|this
operator|.
name|offset
decl_stmt|;
specifier|final
name|long
index|[]
name|bInts
init|=
name|other
operator|.
name|longs
decl_stmt|;
name|int
name|bUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|long
name|aStop
init|=
name|aUpto
operator|+
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|length
argument_list|,
name|other
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|aUpto
operator|<
name|aStop
condition|)
block|{
name|long
name|aInt
init|=
name|aInts
index|[
name|aUpto
operator|++
index|]
decl_stmt|;
name|long
name|bInt
init|=
name|bInts
index|[
name|bUpto
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|aInt
operator|>
name|bInt
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|aInt
operator|<
name|bInt
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|this
operator|.
name|length
operator|-
name|other
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|long
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
name|offset
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toHexString
argument_list|(
name|longs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Creates a new LongsRef that points to a copy of the longs from     *<code>other</code>    *<p>    * The returned IntsRef will have a length of other.length    * and an offset of zero.    */
DECL|method|deepCopyOf
specifier|public
specifier|static
name|LongsRef
name|deepCopyOf
parameter_list|(
name|LongsRef
name|other
parameter_list|)
block|{
return|return
operator|new
name|LongsRef
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|other
operator|.
name|longs
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|other
operator|.
name|offset
operator|+
name|other
operator|.
name|length
argument_list|)
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**     * Performs internal consistency checks.    * Always returns true (or throws IllegalStateException)     */
DECL|method|isValid
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
if|if
condition|(
name|longs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"longs is null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"length is negative: "
operator|+
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|>
name|longs
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"length is out of bounds: "
operator|+
name|length
operator|+
literal|",longs.length="
operator|+
name|longs
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset is negative: "
operator|+
name|offset
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|>
name|longs
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset out of bounds: "
operator|+
name|offset
operator|+
literal|",longs.length="
operator|+
name|longs
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|+
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset+length is negative: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|+
name|length
operator|>
name|longs
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset+length out of bounds: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
operator|+
literal|",longs.length="
operator|+
name|longs
operator|.
name|length
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
