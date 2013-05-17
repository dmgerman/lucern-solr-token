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
name|util
operator|.
name|Comparator
import|;
end_import
begin_comment
comment|/**  * Represents char[], as a slice (offset + length) into an existing char[].  * The {@link #chars} member should never be null; use  * {@link #EMPTY_CHARS} if necessary.  * @lucene.internal  */
end_comment
begin_class
DECL|class|CharsRef
specifier|public
specifier|final
class|class
name|CharsRef
implements|implements
name|Comparable
argument_list|<
name|CharsRef
argument_list|>
implements|,
name|CharSequence
implements|,
name|Cloneable
block|{
comment|/** An empty character array for convenience */
DECL|field|EMPTY_CHARS
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|EMPTY_CHARS
init|=
operator|new
name|char
index|[
literal|0
index|]
decl_stmt|;
comment|/** The contents of the CharsRef. Should never be {@code null}. */
DECL|field|chars
specifier|public
name|char
index|[]
name|chars
decl_stmt|;
comment|/** Offset of first valid character. */
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
comment|/** Length of used characters. */
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
comment|/**    * Creates a new {@link CharsRef} initialized an empty array zero-length    */
DECL|method|CharsRef
specifier|public
name|CharsRef
parameter_list|()
block|{
name|this
argument_list|(
name|EMPTY_CHARS
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link CharsRef} initialized with an array of the given    * capacity    */
DECL|method|CharsRef
specifier|public
name|CharsRef
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|chars
operator|=
operator|new
name|char
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|/**    * Creates a new {@link CharsRef} initialized with the given array, offset and    * length    */
DECL|method|CharsRef
specifier|public
name|CharsRef
parameter_list|(
name|char
index|[]
name|chars
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
name|chars
operator|=
name|chars
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
comment|/**    * Creates a new {@link CharsRef} initialized with the given Strings character    * array    */
DECL|method|CharsRef
specifier|public
name|CharsRef
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|this
operator|.
name|chars
operator|=
name|string
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|chars
operator|.
name|length
expr_stmt|;
block|}
comment|/**    * Returns a shallow clone of this instance (the underlying characters are    *<b>not</b> copied and will be shared by both the returned object and this    * object.    *     * @see #deepCopyOf    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|CharsRef
name|clone
parameter_list|()
block|{
return|return
operator|new
name|CharsRef
argument_list|(
name|chars
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
name|int
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
name|chars
index|[
name|i
index|]
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
name|CharsRef
condition|)
block|{
return|return
name|this
operator|.
name|charsEquals
argument_list|(
operator|(
name|CharsRef
operator|)
name|other
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|charsEquals
specifier|public
name|boolean
name|charsEquals
parameter_list|(
name|CharsRef
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
name|char
index|[]
name|otherChars
init|=
name|other
operator|.
name|chars
decl_stmt|;
specifier|final
name|int
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
name|chars
index|[
name|upto
index|]
operator|!=
name|otherChars
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
name|CharsRef
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
name|char
index|[]
name|aChars
init|=
name|this
operator|.
name|chars
decl_stmt|;
name|int
name|aUpto
init|=
name|this
operator|.
name|offset
decl_stmt|;
specifier|final
name|char
index|[]
name|bChars
init|=
name|other
operator|.
name|chars
decl_stmt|;
name|int
name|bUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
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
name|int
name|aInt
init|=
name|aChars
index|[
name|aUpto
operator|++
index|]
decl_stmt|;
name|int
name|bInt
init|=
name|bChars
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
comment|/**    * Copies the given {@link CharsRef} referenced content into this instance.    *     * @param other    *          the {@link CharsRef} to copy    */
DECL|method|copyChars
specifier|public
name|void
name|copyChars
parameter_list|(
name|CharsRef
name|other
parameter_list|)
block|{
name|copyChars
argument_list|(
name|other
operator|.
name|chars
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**     * Used to grow the reference array.     *     * In general this should not be used as it does not take the offset into account.    * @lucene.internal */
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
assert|assert
name|offset
operator|==
literal|0
assert|;
if|if
condition|(
name|chars
operator|.
name|length
operator|<
name|newLength
condition|)
block|{
name|chars
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|chars
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Copies the given array into this CharsRef.    */
DECL|method|copyChars
specifier|public
name|void
name|copyChars
parameter_list|(
name|char
index|[]
name|otherChars
parameter_list|,
name|int
name|otherOffset
parameter_list|,
name|int
name|otherLength
parameter_list|)
block|{
if|if
condition|(
name|chars
operator|.
name|length
operator|-
name|offset
operator|<
name|otherLength
condition|)
block|{
name|chars
operator|=
operator|new
name|char
index|[
name|otherLength
index|]
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|otherChars
argument_list|,
name|otherOffset
argument_list|,
name|chars
argument_list|,
name|offset
argument_list|,
name|otherLength
argument_list|)
expr_stmt|;
name|length
operator|=
name|otherLength
expr_stmt|;
block|}
comment|/**    * Appends the given array to this CharsRef    */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|char
index|[]
name|otherChars
parameter_list|,
name|int
name|otherOffset
parameter_list|,
name|int
name|otherLength
parameter_list|)
block|{
name|int
name|newLen
init|=
name|length
operator|+
name|otherLength
decl_stmt|;
if|if
condition|(
name|chars
operator|.
name|length
operator|-
name|offset
operator|<
name|newLen
condition|)
block|{
name|char
index|[]
name|newChars
init|=
operator|new
name|char
index|[
name|newLen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|newChars
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|chars
operator|=
name|newChars
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|otherChars
argument_list|,
name|otherOffset
argument_list|,
name|chars
argument_list|,
name|length
operator|+
name|offset
argument_list|,
name|otherLength
argument_list|)
expr_stmt|;
name|length
operator|=
name|newLen
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
comment|// NOTE: must do a real check here to meet the specs of CharSequence
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|length
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
return|return
name|chars
index|[
name|offset
operator|+
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|subSequence
specifier|public
name|CharSequence
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
comment|// NOTE: must do a real check here to meet the specs of CharSequence
if|if
condition|(
name|start
argument_list|<
literal|0
operator|||
name|end
argument_list|>
name|length
operator|||
name|start
operator|>
name|end
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
return|return
operator|new
name|CharsRef
argument_list|(
name|chars
argument_list|,
name|offset
operator|+
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
block|}
comment|/** @deprecated This comparator is only a transition mechanism */
annotation|@
name|Deprecated
DECL|field|utf16SortedAsUTF8SortOrder
specifier|private
specifier|final
specifier|static
name|Comparator
argument_list|<
name|CharsRef
argument_list|>
name|utf16SortedAsUTF8SortOrder
init|=
operator|new
name|UTF16SortedAsUTF8Comparator
argument_list|()
decl_stmt|;
comment|/** @deprecated This comparator is only a transition mechanism */
annotation|@
name|Deprecated
DECL|method|getUTF16SortedAsUTF8Comparator
specifier|public
specifier|static
name|Comparator
argument_list|<
name|CharsRef
argument_list|>
name|getUTF16SortedAsUTF8Comparator
parameter_list|()
block|{
return|return
name|utf16SortedAsUTF8SortOrder
return|;
block|}
comment|/** @deprecated This comparator is only a transition mechanism */
annotation|@
name|Deprecated
DECL|class|UTF16SortedAsUTF8Comparator
specifier|private
specifier|static
class|class
name|UTF16SortedAsUTF8Comparator
implements|implements
name|Comparator
argument_list|<
name|CharsRef
argument_list|>
block|{
comment|// Only singleton
DECL|method|UTF16SortedAsUTF8Comparator
specifier|private
name|UTF16SortedAsUTF8Comparator
parameter_list|()
block|{}
empty_stmt|;
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|CharsRef
name|a
parameter_list|,
name|CharsRef
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
name|b
condition|)
return|return
literal|0
return|;
specifier|final
name|char
index|[]
name|aChars
init|=
name|a
operator|.
name|chars
decl_stmt|;
name|int
name|aUpto
init|=
name|a
operator|.
name|offset
decl_stmt|;
specifier|final
name|char
index|[]
name|bChars
init|=
name|b
operator|.
name|chars
decl_stmt|;
name|int
name|bUpto
init|=
name|b
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|aStop
init|=
name|aUpto
operator|+
name|Math
operator|.
name|min
argument_list|(
name|a
operator|.
name|length
argument_list|,
name|b
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
name|char
name|aChar
init|=
name|aChars
index|[
name|aUpto
operator|++
index|]
decl_stmt|;
name|char
name|bChar
init|=
name|bChars
index|[
name|bUpto
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|aChar
operator|!=
name|bChar
condition|)
block|{
comment|// http://icu-project.org/docs/papers/utf16_code_point_order.html
comment|/* aChar != bChar, fix up each one if they're both in or above the surrogate range, then compare them */
if|if
condition|(
name|aChar
operator|>=
literal|0xd800
operator|&&
name|bChar
operator|>=
literal|0xd800
condition|)
block|{
if|if
condition|(
name|aChar
operator|>=
literal|0xe000
condition|)
block|{
name|aChar
operator|-=
literal|0x800
expr_stmt|;
block|}
else|else
block|{
name|aChar
operator|+=
literal|0x2000
expr_stmt|;
block|}
if|if
condition|(
name|bChar
operator|>=
literal|0xe000
condition|)
block|{
name|bChar
operator|-=
literal|0x800
expr_stmt|;
block|}
else|else
block|{
name|bChar
operator|+=
literal|0x2000
expr_stmt|;
block|}
block|}
comment|/* now aChar and bChar are in code point order */
return|return
operator|(
name|int
operator|)
name|aChar
operator|-
operator|(
name|int
operator|)
name|bChar
return|;
comment|/* int must be 32 bits wide */
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|a
operator|.
name|length
operator|-
name|b
operator|.
name|length
return|;
block|}
block|}
comment|/**    * Creates a new CharsRef that points to a copy of the chars from     *<code>other</code>    *<p>    * The returned CharsRef will have a length of other.length    * and an offset of zero.    */
DECL|method|deepCopyOf
specifier|public
specifier|static
name|CharsRef
name|deepCopyOf
parameter_list|(
name|CharsRef
name|other
parameter_list|)
block|{
name|CharsRef
name|clone
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
name|clone
operator|.
name|copyChars
argument_list|(
name|other
argument_list|)
expr_stmt|;
return|return
name|clone
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
name|chars
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"chars is null"
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
name|chars
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
literal|",chars.length="
operator|+
name|chars
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
name|chars
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
literal|",chars.length="
operator|+
name|chars
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
name|chars
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
literal|",chars.length="
operator|+
name|chars
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
