begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|NumberUtils
specifier|public
class|class
name|NumberUtils
block|{
DECL|method|readableSize
specifier|public
specifier|static
name|String
name|readableSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|NumberFormat
name|formatter
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
operator|)
operator|>
literal|0
condition|)
block|{
return|return
name|formatter
operator|.
name|format
argument_list|(
name|size
operator|*
literal|1.0d
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
operator|+
literal|" GB"
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|>
literal|0
condition|)
block|{
return|return
name|formatter
operator|.
name|format
argument_list|(
name|size
operator|*
literal|1.0d
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
operator|+
literal|" MB"
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|/
literal|1024
operator|>
literal|0
condition|)
block|{
return|return
name|formatter
operator|.
name|format
argument_list|(
name|size
operator|*
literal|1.0d
operator|/
literal|1024
argument_list|)
operator|+
literal|" KB"
return|;
block|}
else|else
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
operator|+
literal|" bytes"
return|;
block|}
block|}
DECL|method|int2sortableStr
specifier|public
specifier|static
name|String
name|int2sortableStr
parameter_list|(
name|int
name|val
parameter_list|)
block|{
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
literal|3
index|]
decl_stmt|;
name|int2sortableStr
argument_list|(
name|val
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
return|;
block|}
DECL|method|int2sortableStr
specifier|public
specifier|static
name|String
name|int2sortableStr
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|int2sortableStr
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|SortableStr2int
specifier|public
specifier|static
name|String
name|SortableStr2int
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|int
name|ival
init|=
name|SortableStr2int
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|ival
argument_list|)
return|;
block|}
DECL|method|SortableStr2int
specifier|public
specifier|static
name|String
name|SortableStr2int
parameter_list|(
name|BytesRef
name|val
parameter_list|)
block|{
comment|// TODO: operate directly on BytesRef
return|return
name|SortableStr2int
argument_list|(
name|val
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|long2sortableStr
specifier|public
specifier|static
name|String
name|long2sortableStr
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
literal|5
index|]
decl_stmt|;
name|long2sortableStr
argument_list|(
name|val
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
return|;
block|}
DECL|method|long2sortableStr
specifier|public
specifier|static
name|String
name|long2sortableStr
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|long2sortableStr
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|SortableStr2long
specifier|public
specifier|static
name|String
name|SortableStr2long
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|long
name|ival
init|=
name|SortableStr2long
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
return|return
name|Long
operator|.
name|toString
argument_list|(
name|ival
argument_list|)
return|;
block|}
DECL|method|SortableStr2long
specifier|public
specifier|static
name|String
name|SortableStr2long
parameter_list|(
name|BytesRef
name|val
parameter_list|)
block|{
comment|// TODO: operate directly on BytesRef
return|return
name|SortableStr2long
argument_list|(
name|val
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
comment|//
comment|// IEEE floating point format is defined so that it sorts correctly
comment|// when interpreted as a signed integer (or signed long in the case
comment|// of a double) for positive values.  For negative values, all the bits except
comment|// the sign bit must be inverted.
comment|// This correctly handles all possible float values including -Infinity and +Infinity.
comment|// Note that in float-space, NaN<x is false, NaN>x is false, NaN==x is false, NaN!=x is true
comment|// for all x (including NaN itself).  Internal to Solr, NaN==NaN is true and NaN
comment|// sorts higher than Infinity, so a range query of [-Infinity TO +Infinity] will
comment|// exclude NaN values, but a query of "NaN" will find all NaN values.
comment|// Also, -0==0 in float-space but -0<0 after this transformation.
comment|//
DECL|method|float2sortableStr
specifier|public
specifier|static
name|String
name|float2sortableStr
parameter_list|(
name|float
name|val
parameter_list|)
block|{
name|int
name|f
init|=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|<
literal|0
condition|)
name|f
operator|^=
literal|0x7fffffff
expr_stmt|;
return|return
name|int2sortableStr
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|float2sortableStr
specifier|public
specifier|static
name|String
name|float2sortableStr
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|float2sortableStr
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|SortableStr2float
specifier|public
specifier|static
name|float
name|SortableStr2float
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|int
name|f
init|=
name|SortableStr2int
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|<
literal|0
condition|)
name|f
operator|^=
literal|0x7fffffff
expr_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|SortableStr2float
specifier|public
specifier|static
name|float
name|SortableStr2float
parameter_list|(
name|BytesRef
name|val
parameter_list|)
block|{
comment|// TODO: operate directly on BytesRef
return|return
name|SortableStr2float
argument_list|(
name|val
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|SortableStr2floatStr
specifier|public
specifier|static
name|String
name|SortableStr2floatStr
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|SortableStr2float
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|double2sortableStr
specifier|public
specifier|static
name|String
name|double2sortableStr
parameter_list|(
name|double
name|val
parameter_list|)
block|{
name|long
name|f
init|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|<
literal|0
condition|)
name|f
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|long2sortableStr
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|double2sortableStr
specifier|public
specifier|static
name|String
name|double2sortableStr
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|double2sortableStr
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|SortableStr2double
specifier|public
specifier|static
name|double
name|SortableStr2double
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|long
name|f
init|=
name|SortableStr2long
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|<
literal|0
condition|)
name|f
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|SortableStr2double
specifier|public
specifier|static
name|double
name|SortableStr2double
parameter_list|(
name|BytesRef
name|val
parameter_list|)
block|{
comment|// TODO: operate directly on BytesRef
return|return
name|SortableStr2double
argument_list|(
name|val
operator|.
name|utf8ToString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|SortableStr2doubleStr
specifier|public
specifier|static
name|String
name|SortableStr2doubleStr
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|SortableStr2double
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|// uses binary representation of an int to build a string of
comment|// chars that will sort correctly.  Only char ranges
comment|// less than 0xd800 will be used to avoid UCS-16 surrogates.
DECL|method|int2sortableStr
specifier|public
specifier|static
name|int
name|int2sortableStr
parameter_list|(
name|int
name|val
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|val
operator|+=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
name|out
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|out
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|val
operator|>>>
literal|12
operator|)
operator|&
literal|0x0fff
argument_list|)
expr_stmt|;
name|out
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|&
literal|0x0fff
argument_list|)
expr_stmt|;
return|return
literal|3
return|;
block|}
DECL|method|SortableStr2int
specifier|public
specifier|static
name|int
name|SortableStr2int
parameter_list|(
name|String
name|sval
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|val
init|=
name|sval
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
operator|<<
literal|24
decl_stmt|;
name|val
operator||=
name|sval
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
operator|<<
literal|12
expr_stmt|;
name|val
operator||=
name|sval
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
expr_stmt|;
name|val
operator|-=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
return|return
name|val
return|;
block|}
DECL|method|SortableStr2int
specifier|public
specifier|static
name|int
name|SortableStr2int
parameter_list|(
name|BytesRef
name|sval
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// TODO: operate directly on BytesRef
return|return
name|SortableStr2int
argument_list|(
name|sval
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|// uses binary representation of an int to build a string of
comment|// chars that will sort correctly.  Only char ranges
comment|// less than 0xd800 will be used to avoid UCS-16 surrogates.
comment|// we can use the lowest 15 bits of a char, (or a mask of 0x7fff)
DECL|method|long2sortableStr
specifier|public
specifier|static
name|int
name|long2sortableStr
parameter_list|(
name|long
name|val
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|val
operator|+=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
name|out
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|>>>
literal|60
argument_list|)
expr_stmt|;
name|out
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|>>>
literal|45
operator|&
literal|0x7fff
argument_list|)
expr_stmt|;
name|out
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|>>>
literal|30
operator|&
literal|0x7fff
argument_list|)
expr_stmt|;
name|out
index|[
name|offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|>>>
literal|15
operator|&
literal|0x7fff
argument_list|)
expr_stmt|;
name|out
index|[
name|offset
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|&
literal|0x7fff
argument_list|)
expr_stmt|;
return|return
literal|5
return|;
block|}
DECL|method|SortableStr2long
specifier|public
specifier|static
name|long
name|SortableStr2long
parameter_list|(
name|String
name|sval
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|long
name|val
init|=
call|(
name|long
call|)
argument_list|(
name|sval
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
argument_list|)
operator|<<
literal|60
decl_stmt|;
name|val
operator||=
operator|(
operator|(
name|long
operator|)
name|sval
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
operator|)
operator|<<
literal|45
expr_stmt|;
name|val
operator||=
operator|(
operator|(
name|long
operator|)
name|sval
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
operator|)
operator|<<
literal|30
expr_stmt|;
name|val
operator||=
name|sval
operator|.
name|charAt
argument_list|(
name|offset
operator|++
argument_list|)
operator|<<
literal|15
expr_stmt|;
name|val
operator||=
name|sval
operator|.
name|charAt
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|val
operator|-=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
return|return
name|val
return|;
block|}
DECL|method|SortableStr2long
specifier|public
specifier|static
name|long
name|SortableStr2long
parameter_list|(
name|BytesRef
name|sval
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// TODO: operate directly on BytesRef
return|return
name|SortableStr2long
argument_list|(
name|sval
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
end_class
end_unit
