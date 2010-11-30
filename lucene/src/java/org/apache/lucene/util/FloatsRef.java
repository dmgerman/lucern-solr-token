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
comment|/**  * Represents float[], as a slice (offset + length) into an existing float[].  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|FloatsRef
specifier|public
specifier|final
class|class
name|FloatsRef
implements|implements
name|Cloneable
block|{
DECL|field|floats
specifier|public
name|double
index|[]
name|floats
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
DECL|method|FloatsRef
specifier|public
name|FloatsRef
parameter_list|()
block|{   }
DECL|method|FloatsRef
specifier|public
name|FloatsRef
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|floats
operator|=
operator|new
name|double
index|[
name|capacity
index|]
expr_stmt|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|floats
index|[
name|offset
index|]
operator|=
name|value
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|double
name|get
parameter_list|()
block|{
return|return
name|floats
index|[
name|offset
index|]
return|;
block|}
DECL|method|FloatsRef
specifier|public
name|FloatsRef
parameter_list|(
name|double
index|[]
name|floats
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
name|floats
operator|=
name|floats
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
DECL|method|FloatsRef
specifier|public
name|FloatsRef
parameter_list|(
name|FloatsRef
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
name|FloatsRef
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
name|long
name|value
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|floats
index|[
name|i
index|]
argument_list|)
decl_stmt|;
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
name|value
operator|^
operator|(
name|value
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
return|return
name|other
operator|instanceof
name|FloatsRef
operator|&&
name|this
operator|.
name|floatsEquals
argument_list|(
operator|(
name|FloatsRef
operator|)
name|other
argument_list|)
return|;
block|}
DECL|method|floatsEquals
specifier|public
name|boolean
name|floatsEquals
parameter_list|(
name|FloatsRef
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
name|double
index|[]
name|otherFloats
init|=
name|other
operator|.
name|floats
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
name|floats
index|[
name|upto
index|]
operator|!=
name|otherFloats
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
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|FloatsRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|floats
operator|==
literal|null
condition|)
block|{
name|floats
operator|=
operator|new
name|double
index|[
name|other
operator|.
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|floats
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|floats
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|floats
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|floats
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
name|floats
operator|.
name|length
operator|<
name|newLength
condition|)
block|{
name|floats
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|floats
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
