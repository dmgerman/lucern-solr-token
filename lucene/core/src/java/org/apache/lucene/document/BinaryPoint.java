begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|PointValues
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
name|MatchNoDocsQuery
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
name|PointInSetQuery
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
name|PointRangeQuery
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
name|Query
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
name|StringHelper
import|;
end_import
begin_comment
comment|/**   * An indexed binary field.  *<p>  * Finding all documents within an N-dimensional shape or range at search time is  * efficient.  Multiple values for the same field in one document  * is allowed.  *<p>  * This field defines static factory methods for creating common queries:  *<ul>  *<li>{@link #newExactQuery(String, byte[])} for matching an exact 1D point.  *<li>{@link #newSetQuery(String, byte[][]) newSetQuery(String, byte[]...)} for matching a set of 1D values.  *<li>{@link #newRangeQuery(String, byte[], byte[])} for matching a 1D range.  *<li>{@link #newRangeQuery(String, byte[][], byte[][])} for matching points/ranges in n-dimensional space.  *</ul>   * @see PointValues  */
end_comment
begin_class
DECL|class|BinaryPoint
specifier|public
specifier|final
class|class
name|BinaryPoint
extends|extends
name|Field
block|{
DECL|method|getType
specifier|private
specifier|static
name|FieldType
name|getType
parameter_list|(
name|byte
index|[]
index|[]
name|point
parameter_list|)
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|point
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot be 0 dimensions"
argument_list|)
throw|;
block|}
name|int
name|bytesPerDim
init|=
operator|-
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
name|point
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|oneDim
init|=
name|point
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|oneDim
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot have null values"
argument_list|)
throw|;
block|}
if|if
condition|(
name|oneDim
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot have 0-length values"
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytesPerDim
operator|==
operator|-
literal|1
condition|)
block|{
name|bytesPerDim
operator|=
name|oneDim
operator|.
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytesPerDim
operator|!=
name|oneDim
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all dimensions must have same bytes length; got "
operator|+
name|bytesPerDim
operator|+
literal|" and "
operator|+
name|oneDim
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
return|return
name|getType
argument_list|(
name|point
operator|.
name|length
argument_list|,
name|bytesPerDim
argument_list|)
return|;
block|}
DECL|method|getType
specifier|private
specifier|static
name|FieldType
name|getType
parameter_list|(
name|int
name|numDims
parameter_list|,
name|int
name|bytesPerDim
parameter_list|)
block|{
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|type
operator|.
name|setDimensions
argument_list|(
name|numDims
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
return|return
name|type
return|;
block|}
DECL|method|pack
specifier|private
specifier|static
name|BytesRef
name|pack
parameter_list|(
name|byte
index|[]
modifier|...
name|point
parameter_list|)
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|point
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot be 0 dimensions"
argument_list|)
throw|;
block|}
if|if
condition|(
name|point
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|point
index|[
literal|0
index|]
argument_list|)
return|;
block|}
name|int
name|bytesPerDim
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|dim
range|:
name|point
control|)
block|{
if|if
condition|(
name|dim
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot have null values"
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytesPerDim
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|dim
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot have 0-length values"
argument_list|)
throw|;
block|}
name|bytesPerDim
operator|=
name|dim
operator|.
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dim
operator|.
name|length
operator|!=
name|bytesPerDim
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all dimensions must have same bytes length; got "
operator|+
name|bytesPerDim
operator|+
literal|" and "
operator|+
name|dim
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
name|byte
index|[]
name|packed
init|=
operator|new
name|byte
index|[
name|bytesPerDim
operator|*
name|point
operator|.
name|length
index|]
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
name|point
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|point
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|packed
argument_list|,
name|i
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|packed
argument_list|)
return|;
block|}
comment|/** General purpose API: creates a new BinaryPoint, indexing the    *  provided N-dimensional binary point.    *    *  @param name field name    *  @param point byte[][] value    *  @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|BinaryPoint
specifier|public
name|BinaryPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
modifier|...
name|point
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pack
argument_list|(
name|point
argument_list|)
argument_list|,
name|getType
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Expert API */
DECL|method|BinaryPoint
specifier|public
name|BinaryPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|packedPoint
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|packedPoint
argument_list|,
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|packedPoint
operator|.
name|length
operator|!=
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|*
name|type
operator|.
name|pointNumBytes
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"packedPoint is length="
operator|+
name|packedPoint
operator|.
name|length
operator|+
literal|" but type.pointDimensionCount()="
operator|+
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|" and type.pointNumBytes()="
operator|+
name|type
operator|.
name|pointNumBytes
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// static methods for generating queries
comment|/**     * Create a query for matching an exact binary value.    *<p>    * This is for simple one-dimension points, for multidimensional points use    * {@link #newRangeQuery(String, byte[][], byte[][])} instead.    *    * @param field field name. must not be {@code null}.    * @param value binary value    * @throws IllegalArgumentException if {@code field} is null or {@code value} is null    * @return a query matching documents with this exact value    */
DECL|method|newExactQuery
specifier|public
specifier|static
name|Query
name|newExactQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**     * Create a range query for binary values.    *<p>    * This is for simple one-dimension ranges, for multidimensional ranges use    * {@link #newRangeQuery(String, byte[][], byte[][])} instead.    *    * @param field field name. must not be {@code null}.    * @param lowerValue lower portion of the range (inclusive). must not be {@code null}    * @param upperValue upper portion of the range (inclusive). must not be {@code null}    * @throws IllegalArgumentException if {@code field} is null, if {@code lowerValue} is null,    *                                  or if {@code upperValue} is null    * @return a query matching documents within this range.    */
DECL|method|newRangeQuery
specifier|public
specifier|static
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|lowerValue
parameter_list|,
name|byte
index|[]
name|upperValue
parameter_list|)
block|{
name|PointRangeQuery
operator|.
name|checkArgs
argument_list|(
name|field
argument_list|,
name|lowerValue
argument_list|,
name|upperValue
argument_list|)
expr_stmt|;
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{
name|lowerValue
block|}
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{
name|upperValue
block|}
argument_list|)
return|;
block|}
comment|/**     * Create a range query for n-dimensional binary values.    *    * @param field field name. must not be {@code null}.    * @param lowerValue lower portion of the range (inclusive). must not be null.    * @param upperValue upper portion of the range (inclusive). must not be null.    * @throws IllegalArgumentException if {@code field} is null, if {@code lowerValue} is null, if {@code upperValue} is null,     *                                  or if {@code lowerValue.length != upperValue.length}    * @return a query matching documents within this range.    */
DECL|method|newRangeQuery
specifier|public
specifier|static
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
index|[]
name|lowerValue
parameter_list|,
name|byte
index|[]
index|[]
name|upperValue
parameter_list|)
block|{
return|return
operator|new
name|PointRangeQuery
argument_list|(
name|field
argument_list|,
name|lowerValue
argument_list|,
name|upperValue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toString
parameter_list|(
name|int
name|dimension
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
assert|assert
name|value
operator|!=
literal|null
assert|;
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
literal|"binary("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
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
name|Integer
operator|.
name|toHexString
argument_list|(
name|value
index|[
name|i
index|]
operator|&
literal|0xFF
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Create a query matching any of the specified 1D values.  This is the points equivalent of {@code TermsQuery}.    *     * @param field field name. must not be {@code null}.    * @param values all values to match    */
DECL|method|newSetQuery
specifier|public
specifier|static
name|Query
name|newSetQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
modifier|...
name|values
parameter_list|)
block|{
comment|// Make sure all byte[] have the same length
name|int
name|bytesPerDim
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|bytesPerDim
operator|==
operator|-
literal|1
condition|)
block|{
name|bytesPerDim
operator|=
name|value
operator|.
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|length
operator|!=
name|bytesPerDim
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all byte[] must be the same length, but saw "
operator|+
name|bytesPerDim
operator|+
literal|" and "
operator|+
name|value
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|bytesPerDim
operator|==
operator|-
literal|1
condition|)
block|{
comment|// There are no points, and we cannot guess the bytesPerDim here, so we return an equivalent query:
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
comment|// Don't unexpectedly change the user's incoming values array:
name|byte
index|[]
index|[]
name|sortedValues
init|=
name|values
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|sortedValues
argument_list|,
operator|new
name|Comparator
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|a
parameter_list|,
name|byte
index|[]
name|b
parameter_list|)
block|{
return|return
name|StringHelper
operator|.
name|compare
argument_list|(
name|a
operator|.
name|length
argument_list|,
name|a
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|encoded
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
name|bytesPerDim
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|PointInSetQuery
argument_list|(
name|field
argument_list|,
literal|1
argument_list|,
name|bytesPerDim
argument_list|,
operator|new
name|PointInSetQuery
operator|.
name|Stream
argument_list|()
block|{
name|int
name|upto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|upto
operator|==
name|sortedValues
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|encoded
operator|.
name|bytes
operator|=
name|sortedValues
index|[
name|upto
index|]
expr_stmt|;
name|upto
operator|++
expr_stmt|;
return|return
name|encoded
return|;
block|}
block|}
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toString
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
