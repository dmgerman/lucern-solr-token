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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Represents char[], as a slice (offset + length) into an existing char[].  *   * @lucene.internal  */
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
block|{
DECL|field|EMPTY_ARRAY
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|char
index|[
literal|0
index|]
decl_stmt|;
DECL|field|chars
specifier|public
name|char
index|[]
name|chars
decl_stmt|;
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
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
name|EMPTY_ARRAY
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
assert|assert
name|chars
operator|!=
literal|null
assert|;
assert|assert
name|chars
operator|.
name|length
operator|>=
name|offset
operator|+
name|length
assert|;
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
comment|/**    * Creates a new {@link CharsRef} and copies the contents of the source into    * the new instance.    * @see #copy(CharsRef)    */
DECL|method|CharsRef
specifier|public
name|CharsRef
parameter_list|(
name|CharsRef
name|other
parameter_list|)
block|{
name|copy
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|CharsRef
argument_list|(
name|this
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
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
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
name|charsEquals
argument_list|(
operator|(
name|CharsRef
operator|)
name|other
argument_list|)
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|CharSequence
condition|)
block|{
specifier|final
name|CharSequence
name|seq
init|=
operator|(
name|CharSequence
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|length
operator|==
name|seq
operator|.
name|length
argument_list|()
condition|)
block|{
name|int
name|n
init|=
name|length
decl_stmt|;
name|int
name|i
init|=
name|offset
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|n
operator|--
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|chars
index|[
name|i
operator|++
index|]
operator|!=
name|seq
operator|.
name|charAt
argument_list|(
name|j
operator|++
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
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
comment|/**    * Copies the given {@link CharsRef} referenced content into this instance    * starting at offset 0.    *     * @param other    *          the {@link CharsRef} to copy    */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|CharsRef
name|other
parameter_list|)
block|{
name|chars
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|chars
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|chars
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|other
operator|.
name|length
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
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
comment|/**    * Copies the given array into this CharsRef starting at offset 0    */
DECL|method|copy
specifier|public
name|void
name|copy
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
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|append
argument_list|(
name|otherChars
argument_list|,
name|otherOffset
argument_list|,
name|otherLength
argument_list|)
expr_stmt|;
block|}
comment|/**    * Appends the given array to this CharsRef starting at the current offset    */
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
name|grow
argument_list|(
name|this
operator|.
name|offset
operator|+
name|otherLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|otherChars
argument_list|,
name|otherOffset
argument_list|,
name|this
operator|.
name|chars
argument_list|,
name|this
operator|.
name|offset
argument_list|,
name|otherLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|otherLength
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
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|chars
index|[
name|offset
operator|+
name|index
index|]
return|;
block|}
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
name|offset
operator|+
name|end
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
end_class
end_unit
