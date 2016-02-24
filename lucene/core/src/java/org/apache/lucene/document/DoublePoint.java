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
name|io
operator|.
name|IOException
import|;
end_import
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
name|BytesRefIterator
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
name|NumericUtils
import|;
end_import
begin_comment
comment|/**   * A double field that is indexed dimensionally such that finding  * all documents within an N-dimensional shape or range at search time is  * efficient.  Multiple values for the same field in one documents  * is allowed.  *<p>  * This field defines static factory methods for creating common queries:  *<ul>  *<li>{@link #newExactQuery newExactQuery()} for matching an exact 1D point.  *<li>{@link #newRangeQuery newRangeQuery()} for matching a 1D range.  *<li>{@link #newMultiRangeQuery newMultiRangeQuery()} for matching points/ranges in n-dimensional space.  *</ul>   */
end_comment
begin_class
DECL|class|DoublePoint
specifier|public
specifier|final
class|class
name|DoublePoint
extends|extends
name|Field
block|{
DECL|method|getType
specifier|private
specifier|static
name|FieldType
name|getType
parameter_list|(
name|int
name|numDims
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
name|Double
operator|.
name|BYTES
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
annotation|@
name|Override
DECL|method|setDoubleValue
specifier|public
name|void
name|setDoubleValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|setDoubleValues
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Change the values of this field */
DECL|method|setDoubleValues
specifier|public
name|void
name|setDoubleValues
parameter_list|(
name|double
modifier|...
name|point
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|!=
name|point
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this field (name="
operator|+
name|name
operator|+
literal|") uses "
operator|+
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|" dimensions; cannot change to (incoming) "
operator|+
name|point
operator|.
name|length
operator|+
literal|" dimensions"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|pack
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setBytesValue
specifier|public
name|void
name|setBytesValue
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from double to BytesRef"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
if|if
condition|(
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this field (name="
operator|+
name|name
operator|+
literal|") uses "
operator|+
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|" dimensions; cannot convert to a single numeric value"
argument_list|)
throw|;
block|}
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|==
name|Double
operator|.
name|BYTES
assert|;
return|return
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
return|;
block|}
DECL|method|pack
specifier|private
specifier|static
name|BytesRef
name|pack
parameter_list|(
name|double
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
name|byte
index|[]
name|packed
init|=
operator|new
name|byte
index|[
name|point
operator|.
name|length
operator|*
name|Double
operator|.
name|BYTES
index|]
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|point
operator|.
name|length
condition|;
name|dim
operator|++
control|)
block|{
name|encodeDimension
argument_list|(
name|point
index|[
name|dim
index|]
argument_list|,
name|packed
argument_list|,
name|dim
operator|*
name|Double
operator|.
name|BYTES
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
comment|/** Creates a new DoublePoint, indexing the    *  provided N-dimensional double point.    *    *  @param name field name    *  @param point double[] value    *  @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|DoublePoint
specifier|public
name|DoublePoint
parameter_list|(
name|String
name|name
parameter_list|,
name|double
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
operator|.
name|length
argument_list|)
argument_list|)
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
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|type
operator|.
name|pointDimensionCount
argument_list|()
condition|;
name|dim
operator|++
control|)
block|{
if|if
condition|(
name|dim
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
name|dim
operator|*
name|Double
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Encode n-dimensional double point into binary encoding */
DECL|method|encode
specifier|private
specifier|static
name|byte
index|[]
index|[]
name|encode
parameter_list|(
name|Double
name|value
index|[]
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|encoded
init|=
operator|new
name|byte
index|[
name|value
operator|.
name|length
index|]
index|[]
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
name|value
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|encoded
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|Double
operator|.
name|BYTES
index|]
expr_stmt|;
name|encodeDimension
argument_list|(
name|value
index|[
name|i
index|]
argument_list|,
name|encoded
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|encoded
return|;
block|}
comment|// public helper methods (e.g. for queries)
comment|/** Encode single double dimension */
DECL|method|encodeDimension
specifier|public
specifier|static
name|void
name|encodeDimension
parameter_list|(
name|Double
name|value
parameter_list|,
name|byte
name|dest
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|NumericUtils
operator|.
name|longToBytesDirect
argument_list|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|value
argument_list|)
argument_list|,
name|dest
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
comment|/** Decode single double dimension */
DECL|method|decodeDimension
specifier|public
specifier|static
name|Double
name|decodeDimension
parameter_list|(
name|byte
name|value
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|NumericUtils
operator|.
name|bytesToLongDirect
argument_list|(
name|value
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|// static methods for generating queries
comment|/**     * Create a query for matching an exact double value.    *<p>    * This is for simple one-dimension points, for multidimensional points use    * {@link #newMultiRangeQuery newMultiRangeQuery()} instead.    *    * @param field field name. must not be {@code null}.    * @param value double value    * @throws IllegalArgumentException if {@code field} is null.    * @return a query matching documents with this exact value    */
DECL|method|newExactQuery
specifier|public
specifier|static
name|PointRangeQuery
name|newExactQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
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
literal|true
argument_list|,
name|value
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**     * Create a range query for double values.    *<p>    * This is for simple one-dimension ranges, for multidimensional ranges use    * {@link #newMultiRangeQuery newMultiRangeQuery()} instead.    *<p>    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the {@code lowerValue} or {@code upperValue} to {@code null}.     *<p>    * By setting inclusive ({@code lowerInclusive} or {@code upperInclusive}) to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    *    * @param field field name. must not be {@code null}.    * @param lowerValue lower portion of the range. {@code null} means "open".    * @param lowerInclusive {@code true} if the lower portion of the range is inclusive, {@code false} if it should be excluded.    * @param upperValue upper portion of the range. {@code null} means "open".    * @param upperInclusive {@code true} if the upper portion of the range is inclusive, {@code false} if it should be excluded.    * @throws IllegalArgumentException if {@code field} is null.    * @return a query matching documents within this range.    */
DECL|method|newRangeQuery
specifier|public
specifier|static
name|PointRangeQuery
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Double
name|lowerValue
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|Double
name|upperValue
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|)
block|{
return|return
name|newMultiRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Double
index|[]
block|{
name|lowerValue
block|}
argument_list|,
operator|new
name|boolean
index|[]
block|{
name|lowerInclusive
block|}
argument_list|,
operator|new
name|Double
index|[]
block|{
name|upperValue
block|}
argument_list|,
operator|new
name|boolean
index|[]
block|{
name|upperInclusive
block|}
argument_list|)
return|;
block|}
comment|/**     * Create a multidimensional range query for double values.    *<p>    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting a {@code lowerValue} element or {@code upperValue} element to {@code null}.     *<p>    * By setting a dimension's inclusive ({@code lowerInclusive} or {@code upperInclusive}) to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    *    * @param field field name. must not be {@code null}.    * @param lowerValue lower portion of the range. {@code null} values mean "open" for that dimension.    * @param lowerInclusive {@code true} if the lower portion of the range is inclusive, {@code false} if it should be excluded.    * @param upperValue upper portion of the range. {@code null} values mean "open" for that dimension.    * @param upperInclusive {@code true} if the upper portion of the range is inclusive, {@code false} if it should be excluded.    * @throws IllegalArgumentException if {@code field} is null, or if {@code lowerValue.length != upperValue.length}    * @return a query matching documents within this range.    */
DECL|method|newMultiRangeQuery
specifier|public
specifier|static
name|PointRangeQuery
name|newMultiRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Double
index|[]
name|lowerValue
parameter_list|,
name|boolean
name|lowerInclusive
index|[]
parameter_list|,
name|Double
index|[]
name|upperValue
parameter_list|,
name|boolean
name|upperInclusive
index|[]
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
operator|new
name|PointRangeQuery
argument_list|(
name|field
argument_list|,
name|DoublePoint
operator|.
name|encode
argument_list|(
name|lowerValue
argument_list|)
argument_list|,
name|lowerInclusive
argument_list|,
name|DoublePoint
operator|.
name|encode
argument_list|(
name|upperValue
argument_list|)
argument_list|,
name|upperInclusive
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
name|DoublePoint
operator|.
name|decodeDimension
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Create a query matching any of the specified 1D values.  This is the points equivalent of {@code TermsQuery}.    *     * @param field field name. must not be {@code null}.    * @param valuesIn all int values to match    */
DECL|method|newSetQuery
specifier|public
specifier|static
name|Query
name|newSetQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
modifier|...
name|valuesIn
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Don't unexpectedly change the user's incoming values array:
name|double
index|[]
name|values
init|=
name|valuesIn
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|value
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
name|Double
operator|.
name|BYTES
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
name|Double
operator|.
name|BYTES
argument_list|,
operator|new
name|BytesRefIterator
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
name|values
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
name|encodeDimension
argument_list|(
name|values
index|[
name|upto
index|]
argument_list|,
name|value
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
return|return
name|value
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
assert|assert
name|value
operator|.
name|length
operator|==
name|Double
operator|.
name|BYTES
assert|;
return|return
name|Double
operator|.
name|toString
argument_list|(
name|decodeDimension
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
