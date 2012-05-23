begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|index
operator|.
name|DocValues
operator|.
name|Type
import|;
end_import
begin_comment
comment|// TODO: maybe we should not automagically promote
end_comment
begin_comment
comment|// types... and instead require a given field always has the
end_comment
begin_comment
comment|// same type?
end_comment
begin_comment
comment|/**  * Type promoter that promotes {@link DocValues} during merge based on  * their {@link Type} and {@link #getValueSize()}  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|TypePromoter
class|class
name|TypePromoter
block|{
DECL|field|FLAGS_MAP
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|Type
argument_list|>
name|FLAGS_MAP
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Type
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|IDENTITY_PROMOTER
specifier|private
specifier|static
specifier|final
name|TypePromoter
name|IDENTITY_PROMOTER
init|=
operator|new
name|IdentityTypePromoter
argument_list|()
decl_stmt|;
DECL|field|VAR_TYPE_VALUE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|VAR_TYPE_VALUE_SIZE
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|IS_INT
specifier|private
specifier|static
specifier|final
name|int
name|IS_INT
init|=
literal|1
operator|<<
literal|0
decl_stmt|;
DECL|field|IS_BYTE
specifier|private
specifier|static
specifier|final
name|int
name|IS_BYTE
init|=
literal|1
operator|<<
literal|1
decl_stmt|;
DECL|field|IS_FLOAT
specifier|private
specifier|static
specifier|final
name|int
name|IS_FLOAT
init|=
literal|1
operator|<<
literal|2
decl_stmt|;
comment|/* VAR& FIXED == VAR */
DECL|field|IS_VAR
specifier|private
specifier|static
specifier|final
name|int
name|IS_VAR
init|=
literal|1
operator|<<
literal|3
decl_stmt|;
DECL|field|IS_FIXED
specifier|private
specifier|static
specifier|final
name|int
name|IS_FIXED
init|=
literal|1
operator|<<
literal|3
operator||
literal|1
operator|<<
literal|4
decl_stmt|;
comment|/* if we have FIXED& FIXED with different size we promote to VAR */
DECL|field|PROMOTE_TO_VAR_SIZE_MASK
specifier|private
specifier|static
specifier|final
name|int
name|PROMOTE_TO_VAR_SIZE_MASK
init|=
operator|~
operator|(
literal|1
operator|<<
literal|3
operator|)
decl_stmt|;
comment|/* STRAIGHT& DEREF == STRAIGHT (dense values win) */
DECL|field|IS_STRAIGHT
specifier|private
specifier|static
specifier|final
name|int
name|IS_STRAIGHT
init|=
literal|1
operator|<<
literal|5
decl_stmt|;
DECL|field|IS_DEREF
specifier|private
specifier|static
specifier|final
name|int
name|IS_DEREF
init|=
literal|1
operator|<<
literal|5
operator||
literal|1
operator|<<
literal|6
decl_stmt|;
DECL|field|IS_SORTED
specifier|private
specifier|static
specifier|final
name|int
name|IS_SORTED
init|=
literal|1
operator|<<
literal|7
decl_stmt|;
comment|/* more bits wins (int16& int32 == int32) */
DECL|field|IS_8_BIT
specifier|private
specifier|static
specifier|final
name|int
name|IS_8_BIT
init|=
literal|1
operator|<<
literal|8
operator||
literal|1
operator|<<
literal|9
operator||
literal|1
operator|<<
literal|10
operator||
literal|1
operator|<<
literal|11
decl_stmt|;
DECL|field|IS_16_BIT
specifier|private
specifier|static
specifier|final
name|int
name|IS_16_BIT
init|=
literal|1
operator|<<
literal|9
operator||
literal|1
operator|<<
literal|10
operator||
literal|1
operator|<<
literal|11
decl_stmt|;
DECL|field|IS_32_BIT
specifier|private
specifier|static
specifier|final
name|int
name|IS_32_BIT
init|=
literal|1
operator|<<
literal|10
operator||
literal|1
operator|<<
literal|11
decl_stmt|;
DECL|field|IS_64_BIT
specifier|private
specifier|static
specifier|final
name|int
name|IS_64_BIT
init|=
literal|1
operator|<<
literal|11
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|int
name|flags
decl_stmt|;
DECL|field|valueSize
specifier|private
specifier|final
name|int
name|valueSize
decl_stmt|;
comment|/**    * Returns a positive value size if this {@link TypePromoter} represents a    * fixed variant, otherwise<code>-1</code>    *     * @return a positive value size if this {@link TypePromoter} represents a    *         fixed variant, otherwise<code>-1</code>    */
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
name|valueSize
return|;
block|}
static|static
block|{
for|for
control|(
name|Type
name|type
range|:
name|Type
operator|.
name|values
argument_list|()
control|)
block|{
name|TypePromoter
name|create
init|=
name|create
argument_list|(
name|type
argument_list|,
name|VAR_TYPE_VALUE_SIZE
argument_list|)
decl_stmt|;
name|FLAGS_MAP
operator|.
name|put
argument_list|(
name|create
operator|.
name|flags
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a new {@link TypePromoter}    *     * @param type    *          the {@link Type} this promoter represents    * @param flags    *          the promoters flags    * @param valueSize    *          the value size if {@link #IS_FIXED} or<code>-1</code> otherwise.    */
DECL|method|TypePromoter
specifier|protected
name|TypePromoter
parameter_list|(
name|Type
name|type
parameter_list|,
name|int
name|flags
parameter_list|,
name|int
name|valueSize
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|valueSize
operator|=
name|valueSize
expr_stmt|;
block|}
comment|/**    * Creates a new promoted {@link TypePromoter} based on this and the given    * {@link TypePromoter} or<code>null</code> iff the {@link TypePromoter}     * aren't compatible.    *     * @param promoter    *          the incoming promoter    * @return a new promoted {@link TypePromoter} based on this and the given    *         {@link TypePromoter} or<code>null</code> iff the    *         {@link TypePromoter} aren't compatible.    */
DECL|method|promote
specifier|public
name|TypePromoter
name|promote
parameter_list|(
name|TypePromoter
name|promoter
parameter_list|)
block|{
name|int
name|promotedFlags
init|=
name|promoter
operator|.
name|flags
operator|&
name|this
operator|.
name|flags
decl_stmt|;
name|TypePromoter
name|promoted
init|=
name|create
argument_list|(
name|FLAGS_MAP
operator|.
name|get
argument_list|(
name|promotedFlags
argument_list|)
argument_list|,
name|valueSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|promoted
operator|==
literal|null
condition|)
block|{
return|return
name|promoted
return|;
block|}
if|if
condition|(
operator|(
name|promoted
operator|.
name|flags
operator|&
name|IS_BYTE
operator|)
operator|!=
literal|0
operator|&&
operator|(
name|promoted
operator|.
name|flags
operator|&
name|IS_FIXED
operator|)
operator|==
name|IS_FIXED
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|valueSize
operator|==
name|promoter
operator|.
name|valueSize
condition|)
block|{
return|return
name|promoted
return|;
block|}
return|return
name|create
argument_list|(
name|FLAGS_MAP
operator|.
name|get
argument_list|(
name|promoted
operator|.
name|flags
operator|&
name|PROMOTE_TO_VAR_SIZE_MASK
argument_list|)
argument_list|,
name|VAR_TYPE_VALUE_SIZE
argument_list|)
return|;
block|}
return|return
name|promoted
return|;
block|}
comment|/**    * Returns the {@link Type} of this {@link TypePromoter}    *     * @return the {@link Type} of this {@link TypePromoter}    */
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|type
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
return|return
literal|"TypePromoter [type="
operator|+
name|type
operator|+
literal|", sizeInBytes="
operator|+
name|valueSize
operator|+
literal|"]"
return|;
block|}
comment|/**    * Creates a new {@link TypePromoter} for the given type and size per value.    *     * @param type    *          the {@link Type} to create the promoter for    * @param valueSize    *          the size per value in bytes or<code>-1</code> iff the types have    *          variable length.    * @return a new {@link TypePromoter}    */
DECL|method|create
specifier|public
specifier|static
name|TypePromoter
name|create
parameter_list|(
name|Type
name|type
parameter_list|,
name|int
name|valueSize
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_BYTE
operator||
name|IS_FIXED
operator||
name|IS_DEREF
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_BYTE
operator||
name|IS_FIXED
operator||
name|IS_SORTED
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_BYTE
operator||
name|IS_FIXED
operator||
name|IS_STRAIGHT
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|BYTES_VAR_DEREF
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_BYTE
operator||
name|IS_VAR
operator||
name|IS_DEREF
argument_list|,
name|VAR_TYPE_VALUE_SIZE
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_BYTE
operator||
name|IS_VAR
operator||
name|IS_SORTED
argument_list|,
name|VAR_TYPE_VALUE_SIZE
argument_list|)
return|;
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_BYTE
operator||
name|IS_VAR
operator||
name|IS_STRAIGHT
argument_list|,
name|VAR_TYPE_VALUE_SIZE
argument_list|)
return|;
case|case
name|FIXED_INTS_16
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_INT
operator||
name|IS_FIXED
operator||
name|IS_STRAIGHT
operator||
name|IS_16_BIT
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|FIXED_INTS_32
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_INT
operator||
name|IS_FIXED
operator||
name|IS_STRAIGHT
operator||
name|IS_32_BIT
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|FIXED_INTS_64
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_INT
operator||
name|IS_FIXED
operator||
name|IS_STRAIGHT
operator||
name|IS_64_BIT
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|FIXED_INTS_8
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_INT
operator||
name|IS_FIXED
operator||
name|IS_STRAIGHT
operator||
name|IS_8_BIT
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_FLOAT
operator||
name|IS_FIXED
operator||
name|IS_STRAIGHT
operator||
name|IS_32_BIT
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|FLOAT_64
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_FLOAT
operator||
name|IS_FIXED
operator||
name|IS_STRAIGHT
operator||
name|IS_64_BIT
argument_list|,
name|valueSize
argument_list|)
return|;
case|case
name|VAR_INTS
case|:
return|return
operator|new
name|TypePromoter
argument_list|(
name|type
argument_list|,
name|IS_INT
operator||
name|IS_VAR
operator||
name|IS_STRAIGHT
argument_list|,
name|VAR_TYPE_VALUE_SIZE
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Returns a {@link TypePromoter} that always promotes to the type provided to    * {@link #promote(TypePromoter)}    */
DECL|method|getIdentityPromoter
specifier|public
specifier|static
name|TypePromoter
name|getIdentityPromoter
parameter_list|()
block|{
return|return
name|IDENTITY_PROMOTER
return|;
block|}
DECL|class|IdentityTypePromoter
specifier|private
specifier|static
class|class
name|IdentityTypePromoter
extends|extends
name|TypePromoter
block|{
DECL|method|IdentityTypePromoter
specifier|public
name|IdentityTypePromoter
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|promote
specifier|public
name|TypePromoter
name|promote
parameter_list|(
name|TypePromoter
name|promoter
parameter_list|)
block|{
return|return
name|promoter
return|;
block|}
block|}
block|}
end_class
end_unit
