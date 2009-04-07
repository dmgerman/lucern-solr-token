begin_unit
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|SortField
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
name|FieldCache
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
name|ExtendedFieldCache
import|;
end_import
begin_comment
comment|/**  * This is a helper class to generate prefix-encoded representations for numerical values  * and supplies converters to represent float/double values as sortable integers/longs.  *<p>To quickly execute range queries in Apache Lucene, a range is divided recursively  * into multiple intervals for searching: The center of the range is searched only with  * the lowest possible precision in the trie, while the boundaries are matched  * more exactly. This reduces the number of terms dramatically.  *<p>This class generates terms to achive this: First the numerical integer values need to  * be converted to strings. For that integer values (32 bit or 64 bit) are made unsigned  * and the bits are converted to ASCII chars with each 7 bit. The resulting string is  * sortable like the original integer value. Each value is also prefixed  * (in the first char) by the<code>shift</code> value (number of bits removed) used  * during encoding.  *<p>To also index floating point numbers, this class supplies two methods to convert them  * to integer values by changing their bit layout: {@link #doubleToSortableLong},  * {@link #floatToSortableInt}. You will have no precision loss by  * converting floating point numbers to integers and back (only that the integer form  * is not usable). Other data types like dates can easily converted to longs or ints (e.g.  * date to long: {@link java.util.Date#getTime}).  *<p>Prefix encoded fields can also be sorted using the {@link SortField} factories  * {@link #getLongSortField} or {@link #getIntSortField}.  */
end_comment
begin_class
DECL|class|TrieUtils
specifier|public
specifier|final
class|class
name|TrieUtils
block|{
DECL|method|TrieUtils
specifier|private
name|TrieUtils
parameter_list|()
block|{}
comment|// no instance!
comment|/**    * Longs are stored at lower precision by shifting off lower bits. The shift count is    * stored as<code>SHIFT_START_LONG+shift</code> in the first character    */
DECL|field|SHIFT_START_LONG
specifier|public
specifier|static
specifier|final
name|char
name|SHIFT_START_LONG
init|=
operator|(
name|char
operator|)
literal|0x20
decl_stmt|;
comment|/** internal: maximum needed<code>char[]</code> buffer size for encoding */
DECL|field|LONG_BUF_SIZE
specifier|static
specifier|final
name|int
name|LONG_BUF_SIZE
init|=
literal|63
operator|/
literal|7
operator|+
literal|2
decl_stmt|;
comment|/**    * Integers are stored at lower precision by shifting off lower bits. The shift count is    * stored as<code>SHIFT_START_INT+shift</code> in the first character    */
DECL|field|SHIFT_START_INT
specifier|public
specifier|static
specifier|final
name|char
name|SHIFT_START_INT
init|=
operator|(
name|char
operator|)
literal|0x60
decl_stmt|;
comment|/** internal: maximum needed<code>char[]</code> buffer size for encoding */
DECL|field|INT_BUF_SIZE
specifier|static
specifier|final
name|int
name|INT_BUF_SIZE
init|=
literal|31
operator|/
literal|7
operator|+
literal|2
decl_stmt|;
comment|/**    * A parser instance for filling a {@link ExtendedFieldCache}, that parses prefix encoded fields as longs.    */
DECL|field|FIELD_CACHE_LONG_PARSER
specifier|public
specifier|static
specifier|final
name|ExtendedFieldCache
operator|.
name|LongParser
name|FIELD_CACHE_LONG_PARSER
init|=
operator|new
name|ExtendedFieldCache
operator|.
name|LongParser
argument_list|()
block|{
specifier|public
specifier|final
name|long
name|parseLong
parameter_list|(
specifier|final
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|63
condition|)
throw|throw
operator|new
name|FieldCache
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|prefixCodedToLong
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for filling a {@link FieldCache}, that parses prefix encoded fields as ints.    */
DECL|field|FIELD_CACHE_INT_PARSER
specifier|public
specifier|static
specifier|final
name|FieldCache
operator|.
name|IntParser
name|FIELD_CACHE_INT_PARSER
init|=
operator|new
name|FieldCache
operator|.
name|IntParser
argument_list|()
block|{
specifier|public
specifier|final
name|int
name|parseInt
parameter_list|(
specifier|final
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|31
condition|)
throw|throw
operator|new
name|FieldCache
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|prefixCodedToInt
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for filling a {@link ExtendedFieldCache}, that parses prefix encoded fields as doubles.    * This uses {@link #sortableLongToDouble} to convert the encoded long to a double.    */
DECL|field|FIELD_CACHE_DOUBLE_PARSER
specifier|public
specifier|static
specifier|final
name|ExtendedFieldCache
operator|.
name|DoubleParser
name|FIELD_CACHE_DOUBLE_PARSER
init|=
operator|new
name|ExtendedFieldCache
operator|.
name|DoubleParser
argument_list|()
block|{
specifier|public
specifier|final
name|double
name|parseDouble
parameter_list|(
specifier|final
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|63
condition|)
throw|throw
operator|new
name|FieldCache
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|sortableLongToDouble
argument_list|(
name|prefixCodedToLong
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for filling a {@link FieldCache}, that parses prefix encoded fields as floats.    * This uses {@link #sortableIntToFloat} to convert the encoded int to a float.    */
DECL|field|FIELD_CACHE_FLOAT_PARSER
specifier|public
specifier|static
specifier|final
name|FieldCache
operator|.
name|FloatParser
name|FIELD_CACHE_FLOAT_PARSER
init|=
operator|new
name|FieldCache
operator|.
name|FloatParser
argument_list|()
block|{
specifier|public
specifier|final
name|float
name|parseFloat
parameter_list|(
specifier|final
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|31
condition|)
throw|throw
operator|new
name|FieldCache
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|sortableIntToFloat
argument_list|(
name|prefixCodedToInt
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** internal */
DECL|method|longToPrefixCoded
specifier|static
name|int
name|longToPrefixCoded
parameter_list|(
specifier|final
name|long
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|char
index|[]
name|buffer
parameter_list|)
block|{
name|int
name|nChars
init|=
operator|(
literal|63
operator|-
name|shift
operator|)
operator|/
literal|7
operator|+
literal|1
decl_stmt|,
name|len
init|=
name|nChars
operator|+
literal|1
decl_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|SHIFT_START_LONG
operator|+
name|shift
argument_list|)
expr_stmt|;
name|long
name|sortableBits
init|=
name|val
operator|^
literal|0x8000000000000000L
decl_stmt|;
name|sortableBits
operator|>>>=
name|shift
expr_stmt|;
while|while
condition|(
name|nChars
operator|>=
literal|1
condition|)
block|{
comment|// Store 7 bits per character for good efficiency when UTF-8 encoding.
comment|// The whole number is right-justified so that lucene can prefix-encode
comment|// the terms more efficiently.
name|buffer
index|[
name|nChars
operator|--
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|sortableBits
operator|&
literal|0x7f
argument_list|)
expr_stmt|;
name|sortableBits
operator|>>>=
literal|7
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
comment|/**    * This is a convenience method, that returns prefix coded bits of a long without    * reducing the precision. It can be used to store the full precision value as a    * stored field in index.    *<p>To decode, use {@link #prefixCodedToLong}.    */
DECL|method|longToPrefixCoded
specifier|public
specifier|static
name|String
name|longToPrefixCoded
parameter_list|(
specifier|final
name|long
name|val
parameter_list|)
block|{
return|return
name|longToPrefixCoded
argument_list|(
name|val
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Expert: Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link LongRangeBuilder}.    */
DECL|method|longToPrefixCoded
specifier|public
specifier|static
name|String
name|longToPrefixCoded
parameter_list|(
specifier|final
name|long
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
if|if
condition|(
name|shift
operator|>
literal|63
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal shift value, must be 0..63"
argument_list|)
throw|;
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|LONG_BUF_SIZE
index|]
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|longToPrefixCoded
argument_list|(
name|val
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/** internal */
DECL|method|intToPrefixCoded
specifier|static
name|int
name|intToPrefixCoded
parameter_list|(
specifier|final
name|int
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|char
index|[]
name|buffer
parameter_list|)
block|{
name|int
name|nChars
init|=
operator|(
literal|31
operator|-
name|shift
operator|)
operator|/
literal|7
operator|+
literal|1
decl_stmt|,
name|len
init|=
name|nChars
operator|+
literal|1
decl_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|SHIFT_START_INT
operator|+
name|shift
argument_list|)
expr_stmt|;
name|int
name|sortableBits
init|=
name|val
operator|^
literal|0x80000000
decl_stmt|;
name|sortableBits
operator|>>>=
name|shift
expr_stmt|;
while|while
condition|(
name|nChars
operator|>=
literal|1
condition|)
block|{
comment|// Store 7 bits per character for good efficiency when UTF-8 encoding.
comment|// The whole number is right-justified so that lucene can prefix-encode
comment|// the terms more efficiently.
name|buffer
index|[
name|nChars
operator|--
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|sortableBits
operator|&
literal|0x7f
argument_list|)
expr_stmt|;
name|sortableBits
operator|>>>=
literal|7
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
comment|/**    * This is a convenience method, that returns prefix coded bits of an int without    * reducing the precision. It can be used to store the full precision value as a    * stored field in index.    *<p>To decode, use {@link #prefixCodedToInt}.    */
DECL|method|intToPrefixCoded
specifier|public
specifier|static
name|String
name|intToPrefixCoded
parameter_list|(
specifier|final
name|int
name|val
parameter_list|)
block|{
return|return
name|intToPrefixCoded
argument_list|(
name|val
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Expert: Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link IntRangeBuilder}.    */
DECL|method|intToPrefixCoded
specifier|public
specifier|static
name|String
name|intToPrefixCoded
parameter_list|(
specifier|final
name|int
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
if|if
condition|(
name|shift
operator|>
literal|31
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal shift value, must be 0..31"
argument_list|)
throw|;
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|INT_BUF_SIZE
index|]
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|intToPrefixCoded
argument_list|(
name|val
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/**    * Returns a long from prefixCoded characters.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode e.g. a stored field.    * @throws NumberFormatException if the supplied string is    * not correctly prefix encoded.    * @see #longToPrefixCoded(long)    */
DECL|method|prefixCodedToLong
specifier|public
specifier|static
name|long
name|prefixCodedToLong
parameter_list|(
specifier|final
name|String
name|prefixCoded
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|prefixCoded
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|63
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid shift value in prefixCoded string (is encoded value really a LONG?)"
argument_list|)
throw|;
name|long
name|sortableBits
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|,
name|len
init|=
name|prefixCoded
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|sortableBits
operator|<<=
literal|7
expr_stmt|;
specifier|final
name|char
name|ch
init|=
name|prefixCoded
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
literal|0x7f
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid prefixCoded numerical value representation (char "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
name|int
operator|)
name|ch
argument_list|)
operator|+
literal|" at position "
operator|+
name|i
operator|+
literal|" is invalid)"
argument_list|)
throw|;
block|}
name|sortableBits
operator||=
operator|(
name|long
operator|)
name|ch
expr_stmt|;
block|}
return|return
operator|(
name|sortableBits
operator|<<
name|shift
operator|)
operator|^
literal|0x8000000000000000L
return|;
block|}
comment|/**    * Returns an int from prefixCoded characters.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode e.g. a stored field.    * @throws NumberFormatException if the supplied string is    * not correctly prefix encoded.    * @see #intToPrefixCoded(int)    */
DECL|method|prefixCodedToInt
specifier|public
specifier|static
name|int
name|prefixCodedToInt
parameter_list|(
specifier|final
name|String
name|prefixCoded
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|prefixCoded
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|31
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid shift value in prefixCoded string (is encoded value really an INT?)"
argument_list|)
throw|;
name|int
name|sortableBits
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|,
name|len
init|=
name|prefixCoded
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|sortableBits
operator|<<=
literal|7
expr_stmt|;
specifier|final
name|char
name|ch
init|=
name|prefixCoded
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>
literal|0x7f
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid prefixCoded numerical value representation (char "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
name|int
operator|)
name|ch
argument_list|)
operator|+
literal|" at position "
operator|+
name|i
operator|+
literal|" is invalid)"
argument_list|)
throw|;
block|}
name|sortableBits
operator||=
operator|(
name|int
operator|)
name|ch
expr_stmt|;
block|}
return|return
operator|(
name|sortableBits
operator|<<
name|shift
operator|)
operator|^
literal|0x80000000
return|;
block|}
comment|/**    * Converts a<code>double</code> value to a sortable signed<code>long</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;double format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as long.    * By this the precision is not reduced, but the value can easily used as a long.    * @see #sortableLongToDouble    */
DECL|method|doubleToSortableLong
specifier|public
specifier|static
name|long
name|doubleToSortableLong
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
name|doubleToLongBits
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
name|f
return|;
block|}
comment|/**    * Converts a sortable<code>long</code> back to a<code>double</code>.    * @see #doubleToSortableLong    */
DECL|method|sortableLongToDouble
specifier|public
specifier|static
name|double
name|sortableLongToDouble
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|<
literal|0
condition|)
name|val
operator|^=
literal|0x7fffffffffffffffL
expr_stmt|;
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/**    * Converts a<code>float</code> value to a sortable signed<code>int</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;float format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as int.    * By this the precision is not reduced, but the value can easily used as an int.    * @see #sortableIntToFloat    */
DECL|method|floatToSortableInt
specifier|public
specifier|static
name|int
name|floatToSortableInt
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
name|floatToIntBits
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
name|f
return|;
block|}
comment|/**    * Converts a sortable<code>int</code> back to a<code>float</code>.    * @see #floatToSortableInt    */
DECL|method|sortableIntToFloat
specifier|public
specifier|static
name|float
name|sortableIntToFloat
parameter_list|(
name|int
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|<
literal|0
condition|)
name|val
operator|^=
literal|0x7fffffff
expr_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** A factory method, that generates a {@link SortField} instance for sorting prefix encoded long values. */
DECL|method|getLongSortField
specifier|public
specifier|static
name|SortField
name|getLongSortField
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|FIELD_CACHE_LONG_PARSER
argument_list|,
name|reverse
argument_list|)
return|;
block|}
comment|/** A factory method, that generates a {@link SortField} instance for sorting prefix encoded int values. */
DECL|method|getIntSortField
specifier|public
specifier|static
name|SortField
name|getIntSortField
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|FIELD_CACHE_INT_PARSER
argument_list|,
name|reverse
argument_list|)
return|;
block|}
comment|/**    * Expert: Splits a long range recursively.    * You may implement a builder that adds clauses to a    * {@link org.apache.lucene.search.BooleanQuery} for each call to its    * {@link LongRangeBuilder#addRange(String,String)}    * method.    *<p>This method is used by {@link LongTrieRangeFilter}.    */
DECL|method|splitLongRange
specifier|public
specifier|static
name|void
name|splitLongRange
parameter_list|(
specifier|final
name|LongRangeBuilder
name|builder
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
specifier|final
name|long
name|minBound
parameter_list|,
specifier|final
name|long
name|maxBound
parameter_list|)
block|{
if|if
condition|(
name|precisionStep
argument_list|<
literal|1
operator|||
name|precisionStep
argument_list|>
literal|64
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep may only be 1..64"
argument_list|)
throw|;
name|splitRange
argument_list|(
name|builder
argument_list|,
literal|64
argument_list|,
name|precisionStep
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: Splits an int range recursively.    * You may implement a builder that adds clauses to a    * {@link org.apache.lucene.search.BooleanQuery} for each call to its    * {@link IntRangeBuilder#addRange(String,String)}    * method.    *<p>This method is used by {@link IntTrieRangeFilter}.    */
DECL|method|splitIntRange
specifier|public
specifier|static
name|void
name|splitIntRange
parameter_list|(
specifier|final
name|IntRangeBuilder
name|builder
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
specifier|final
name|int
name|minBound
parameter_list|,
specifier|final
name|int
name|maxBound
parameter_list|)
block|{
if|if
condition|(
name|precisionStep
argument_list|<
literal|1
operator|||
name|precisionStep
argument_list|>
literal|32
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep may only be 1..32"
argument_list|)
throw|;
name|splitRange
argument_list|(
name|builder
argument_list|,
literal|32
argument_list|,
name|precisionStep
argument_list|,
operator|(
name|long
operator|)
name|minBound
argument_list|,
operator|(
name|long
operator|)
name|maxBound
argument_list|)
expr_stmt|;
block|}
comment|/** This helper does the splitting for both 32 and 64 bit. */
DECL|method|splitRange
specifier|private
specifier|static
name|void
name|splitRange
parameter_list|(
specifier|final
name|Object
name|builder
parameter_list|,
specifier|final
name|int
name|valSize
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|long
name|minBound
parameter_list|,
name|long
name|maxBound
parameter_list|)
block|{
for|for
control|(
name|int
name|shift
init|=
literal|0
init|;
condition|;
name|shift
operator|+=
name|precisionStep
control|)
block|{
comment|// calculate new bounds for inner precision
specifier|final
name|long
name|diff
init|=
literal|1L
operator|<<
operator|(
name|shift
operator|+
name|precisionStep
operator|)
decl_stmt|,
name|mask
init|=
operator|(
operator|(
literal|1L
operator|<<
name|precisionStep
operator|)
operator|-
literal|1L
operator|)
operator|<<
name|shift
decl_stmt|;
specifier|final
name|boolean
name|hasLower
init|=
operator|(
name|minBound
operator|&
name|mask
operator|)
operator|!=
literal|0L
decl_stmt|,
name|hasUpper
init|=
operator|(
name|maxBound
operator|&
name|mask
operator|)
operator|!=
name|mask
decl_stmt|;
specifier|final
name|long
name|nextMinBound
init|=
operator|(
name|hasLower
condition|?
operator|(
name|minBound
operator|+
name|diff
operator|)
else|:
name|minBound
operator|)
operator|&
operator|~
name|mask
decl_stmt|,
name|nextMaxBound
init|=
operator|(
name|hasUpper
condition|?
operator|(
name|maxBound
operator|-
name|diff
operator|)
else|:
name|maxBound
operator|)
operator|&
operator|~
name|mask
decl_stmt|;
if|if
condition|(
name|shift
operator|+
name|precisionStep
operator|>=
name|valSize
operator|||
name|nextMinBound
operator|>
name|nextMaxBound
condition|)
block|{
comment|// We are in the lowest precision or the next precision is not available.
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
comment|// exit the split recursion loop
break|break;
block|}
if|if
condition|(
name|hasLower
condition|)
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|minBound
argument_list|,
name|minBound
operator||
name|mask
argument_list|,
name|shift
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasUpper
condition|)
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|maxBound
operator|&
operator|~
name|mask
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
comment|// recurse to next precision
name|minBound
operator|=
name|nextMinBound
expr_stmt|;
name|maxBound
operator|=
name|nextMaxBound
expr_stmt|;
block|}
block|}
comment|/** Helper that delegates to correct range builder */
DECL|method|addRange
specifier|private
specifier|static
name|void
name|addRange
parameter_list|(
specifier|final
name|Object
name|builder
parameter_list|,
specifier|final
name|int
name|valSize
parameter_list|,
name|long
name|minBound
parameter_list|,
name|long
name|maxBound
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
comment|// for the max bound set all lower bits (that were shifted away):
comment|// this is important for testing or other usages of the splitted range
comment|// (e.g. to reconstruct the full range). The prefixEncoding will remove
comment|// the bits anyway, so they do not hurt!
name|maxBound
operator||=
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1L
expr_stmt|;
comment|// delegate to correct range builder
switch|switch
condition|(
name|valSize
condition|)
block|{
case|case
literal|64
case|:
operator|(
operator|(
name|LongRangeBuilder
operator|)
name|builder
operator|)
operator|.
name|addRange
argument_list|(
name|minBound
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
operator|(
operator|(
name|IntRangeBuilder
operator|)
name|builder
operator|)
operator|.
name|addRange
argument_list|(
operator|(
name|int
operator|)
name|minBound
argument_list|,
operator|(
name|int
operator|)
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Should not happen!
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valSize must be 32 or 64."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Expert: Callback for {@link #splitLongRange}.    * You need to overwrite only one of the methods.    *<p><font color="red">WARNING: This is a very low-level interface,    * the method signatures may change in later versions.</font>    */
DECL|class|LongRangeBuilder
specifier|public
specifier|static
specifier|abstract
class|class
name|LongRangeBuilder
block|{
comment|/**      * Overwrite this method, if you like to receive the already prefix encoded range bounds.      * You can directly build classical (inclusive) range queries from them.      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
name|String
name|minPrefixCoded
parameter_list|,
name|String
name|maxPrefixCoded
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Overwrite this method, if you like to receive the raw long range bounds.      * You can use this for e.g. debugging purposes (print out range bounds).      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
specifier|final
name|long
name|min
parameter_list|,
specifier|final
name|long
name|max
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
name|addRange
argument_list|(
name|longToPrefixCoded
argument_list|(
name|min
argument_list|,
name|shift
argument_list|)
argument_list|,
name|longToPrefixCoded
argument_list|(
name|max
argument_list|,
name|shift
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Expert: Callback for {@link #splitIntRange}.    * You need to overwrite only one of the methods.    *<p><font color="red">WARNING: This is a very low-level interface,    * the method signatures may change in later versions.</font>    */
DECL|class|IntRangeBuilder
specifier|public
specifier|static
specifier|abstract
class|class
name|IntRangeBuilder
block|{
comment|/**      * Overwrite this method, if you like to receive the already prefix encoded range bounds.      * You can directly build classical range (inclusive) queries from them.      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
name|String
name|minPrefixCoded
parameter_list|,
name|String
name|maxPrefixCoded
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Overwrite this method, if you like to receive the raw int range bounds.      * You can use this for e.g. debugging purposes (print out range bounds).      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
specifier|final
name|int
name|min
parameter_list|,
specifier|final
name|int
name|max
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
name|addRange
argument_list|(
name|intToPrefixCoded
argument_list|(
name|min
argument_list|,
name|shift
argument_list|)
argument_list|,
name|intToPrefixCoded
argument_list|(
name|max
argument_list|,
name|shift
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
