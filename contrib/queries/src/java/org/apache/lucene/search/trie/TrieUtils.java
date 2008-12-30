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
name|java
operator|.
name|util
operator|.
name|Date
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|ExtendedFieldCache
import|;
end_import
begin_comment
comment|/**  * This is a helper class to construct the trie-based index entries for numerical values.  *<p>For more information on how the algorithm works, see the package description {@link org.apache.lucene.search.trie}.  * The format of how the numerical values are stored in index is documented here:  *<p>All numerical values are first converted to special<code>unsigned long</code>s by applying some bit-wise transformations. This means:<ul>  *<li>{@link Date}s are casted to UNIX timestamps (milliseconds since 1970-01-01, this is how Java represents date/time  * internally): {@link Date#getTime()}. The resulting<code>signed long</code> is transformed to the unsigned form like so:</li>  *<li><code>signed long</code>s are shifted, so that {@link Long#MIN_VALUE} is mapped to<code>0x0000000000000000</code>,  * {@link Long#MAX_VALUE} is mapped to<code>0xffffffffffffffff</code>.</li>  *<li><code>double</code>s are converted by getting their IEEE 754 floating-point "double format" bit layout and then some bits  * are swapped, to be able to compare the result as<code>unsigned long</code>s.</li>  *</ul>  *<p>For each variant (you can choose between {@link #VARIANT_8BIT}, {@link #VARIANT_4BIT}, and {@link #VARIANT_2BIT}),  * the bitmap of this<code>unsigned long</code> is divided into parts of a number of bits (starting with the most-significant bits)  * and each part converted to characters between {@link #TRIE_CODED_SYMBOL_MIN} and {@link #TRIE_CODED_SYMBOL_MAX}.  * The resulting {@link String} is comparable like the corresponding<code>unsigned long</code>.  *<p>To store the different precisions of the long values (from one character [only the most significant one] to the full encoded length),  * each lower precision is prefixed by the length ({@link #TRIE_CODED_PADDING_START}<code>+precision == 0x20+precision</code>),  * in an extra "helper" field with a suffixed field name (i.e. fieldname "numeric" =&gt; lower precision's name "numeric#trie").  * The full long is not prefixed at all and indexed and stored according to the given flags in the original field name.  * By this it is possible to get the correct enumeration of terms in correct precision  * of the term list by just jumping to the correct fieldname and/or prefix. The full precision value may also be  * stored in the document. Having the full precision value as term in a separate field with the original name,  * sorting of query results agains such fields is possible using the original field name.  */
end_comment
begin_class
DECL|class|TrieUtils
specifier|public
specifier|final
class|class
name|TrieUtils
block|{
comment|/** Instance of TrieUtils using a trie factor of 8 bit. */
DECL|field|VARIANT_8BIT
specifier|public
specifier|static
specifier|final
name|TrieUtils
name|VARIANT_8BIT
init|=
operator|new
name|TrieUtils
argument_list|(
literal|8
argument_list|)
decl_stmt|;
comment|/** Instance of TrieUtils using a trie factor of 4 bit. */
DECL|field|VARIANT_4BIT
specifier|public
specifier|static
specifier|final
name|TrieUtils
name|VARIANT_4BIT
init|=
operator|new
name|TrieUtils
argument_list|(
literal|4
argument_list|)
decl_stmt|;
comment|/** Instance of TrieUtils using a trie factor of 2 bit. */
DECL|field|VARIANT_2BIT
specifier|public
specifier|static
specifier|final
name|TrieUtils
name|VARIANT_2BIT
init|=
operator|new
name|TrieUtils
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|/** Marker (PADDING)  before lower-precision trie entries to signal the precision value. See class description! */
DECL|field|TRIE_CODED_PADDING_START
specifier|public
specifier|static
specifier|final
name|char
name|TRIE_CODED_PADDING_START
init|=
operator|(
name|char
operator|)
literal|0x20
decl_stmt|;
comment|/** The "helper" field containing the lower precision terms is the original fieldname with this appended. */
DECL|field|LOWER_PRECISION_FIELD_NAME_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|LOWER_PRECISION_FIELD_NAME_SUFFIX
init|=
literal|"#trie"
decl_stmt|;
comment|/** Character used as lower end */
DECL|field|TRIE_CODED_SYMBOL_MIN
specifier|public
specifier|static
specifier|final
name|char
name|TRIE_CODED_SYMBOL_MIN
init|=
operator|(
name|char
operator|)
literal|0x100
decl_stmt|;
comment|/** 	 * A parser instance for filling a {@link ExtendedFieldCache}, that parses trie encoded fields as longs, 	 * auto detecting the trie encoding variant using the String length. 	 */
DECL|field|FIELD_CACHE_LONG_PARSER_AUTO
specifier|public
specifier|static
specifier|final
name|ExtendedFieldCache
operator|.
name|LongParser
name|FIELD_CACHE_LONG_PARSER_AUTO
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
name|String
name|val
parameter_list|)
block|{
return|return
name|trieCodedToLongAuto
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** 	 * A parser instance for filling a {@link ExtendedFieldCache}, that parses trie encoded fields as doubles, 	 * auto detecting the trie encoding variant using the String length. 	 */
DECL|field|FIELD_CACHE_DOUBLE_PARSER_AUTO
specifier|public
specifier|static
specifier|final
name|ExtendedFieldCache
operator|.
name|DoubleParser
name|FIELD_CACHE_DOUBLE_PARSER_AUTO
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
name|String
name|val
parameter_list|)
block|{
return|return
name|trieCodedToDoubleAuto
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|defaultTrieVariant
specifier|private
specifier|static
name|TrieUtils
name|defaultTrieVariant
init|=
name|TrieUtils
operator|.
name|VARIANT_8BIT
decl_stmt|;
comment|/** 	 * Sets the default variant used for generating trie values and ranges. 	 * It is used by the constructors of {@link TrieRangeQuery} and {@link TrieRangeFilter} without<code>TrieUtils</code> parameter 	 * and can be used to get a default value through your whole application. 	 */
DECL|method|setDefaultTrieVariant
specifier|public
specifier|synchronized
specifier|static
specifier|final
name|void
name|setDefaultTrieVariant
parameter_list|(
specifier|final
name|TrieUtils
name|variant
parameter_list|)
block|{
name|defaultTrieVariant
operator|=
name|variant
expr_stmt|;
block|}
comment|/** 	 * Gets the default variant used for generating trie values and ranges. 	 * It is used by the constructors of {@link TrieRangeQuery} and {@link TrieRangeFilter} without<code>TrieUtils</code> parameter 	 * and can be used to get a default value through your whole application. 	 *<p>The default, if not set by {@link #setDefaultTrieVariant}, is {@link #VARIANT_8BIT}. 	 */
DECL|method|getDefaultTrieVariant
specifier|public
specifier|synchronized
specifier|static
specifier|final
name|TrieUtils
name|getDefaultTrieVariant
parameter_list|()
block|{
return|return
name|defaultTrieVariant
return|;
block|}
comment|/** 	 * Detects and returns the variant of a trie encoded string using the length. 	 * @throws NumberFormatException if the length is not 8, 16, or 32 chars. 	 */
DECL|method|autoDetectVariant
specifier|public
specifier|static
specifier|final
name|TrieUtils
name|autoDetectVariant
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
specifier|final
name|int
name|l
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|==
name|VARIANT_8BIT
operator|.
name|TRIE_CODED_LENGTH
condition|)
block|{
return|return
name|VARIANT_8BIT
return|;
block|}
elseif|else
if|if
condition|(
name|l
operator|==
name|VARIANT_4BIT
operator|.
name|TRIE_CODED_LENGTH
condition|)
block|{
return|return
name|VARIANT_4BIT
return|;
block|}
elseif|else
if|if
condition|(
name|l
operator|==
name|VARIANT_2BIT
operator|.
name|TRIE_CODED_LENGTH
condition|)
block|{
return|return
name|VARIANT_2BIT
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid trie encoded numerical value representation (incompatible length)."
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Converts a encoded<code>String</code> value back to a<code>long</code>, 	 * auto detecting the trie encoding variant using the String length. 	 */
DECL|method|trieCodedToLongAuto
specifier|public
specifier|static
specifier|final
name|long
name|trieCodedToLongAuto
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
name|autoDetectVariant
argument_list|(
name|s
argument_list|)
operator|.
name|trieCodedToLong
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/** 	 * Converts a encoded<code>String</code> value back to a<code>double</code>, 	 * auto detecting the trie encoding variant using the String length. 	 */
DECL|method|trieCodedToDoubleAuto
specifier|public
specifier|static
specifier|final
name|double
name|trieCodedToDoubleAuto
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
name|autoDetectVariant
argument_list|(
name|s
argument_list|)
operator|.
name|trieCodedToDouble
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/** 	 * Converts a encoded<code>String</code> value back to a<code>Date</code>, 	 * auto detecting the trie encoding variant using the String length. 	 */
DECL|method|trieCodedToDateAuto
specifier|public
specifier|static
specifier|final
name|Date
name|trieCodedToDateAuto
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
name|autoDetectVariant
argument_list|(
name|s
argument_list|)
operator|.
name|trieCodedToDate
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/** 	 * A factory method, that generates a {@link SortField} instance for sorting trie encoded values, 	 * automatically detecting the trie encoding variant using the String length. 	 */
DECL|method|getSortFieldAuto
specifier|public
specifier|static
specifier|final
name|SortField
name|getSortFieldAuto
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|FIELD_CACHE_LONG_PARSER_AUTO
argument_list|)
return|;
block|}
comment|/** 	 * A factory method, that generates a {@link SortField} instance for sorting trie encoded values, 	 * automatically detecting the trie encoding variant using the String length. 	 */
DECL|method|getSortFieldAuto
specifier|public
specifier|static
specifier|final
name|SortField
name|getSortFieldAuto
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
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
name|FIELD_CACHE_LONG_PARSER_AUTO
argument_list|,
name|reverse
argument_list|)
return|;
block|}
comment|// TrieUtils instance's part
DECL|method|TrieUtils
specifier|private
name|TrieUtils
parameter_list|(
name|int
name|bits
parameter_list|)
block|{
assert|assert
literal|64
operator|%
name|bits
operator|==
literal|0
assert|;
comment|// helper variable for conversion
name|mask
operator|=
operator|(
literal|1L
operator|<<
name|bits
operator|)
operator|-
literal|1L
expr_stmt|;
comment|// init global "constants"
name|TRIE_BITS
operator|=
name|bits
expr_stmt|;
name|TRIE_CODED_LENGTH
operator|=
literal|64
operator|/
name|TRIE_BITS
expr_stmt|;
name|TRIE_CODED_SYMBOL_MAX
operator|=
call|(
name|char
call|)
argument_list|(
name|TRIE_CODED_SYMBOL_MIN
operator|+
name|mask
argument_list|)
expr_stmt|;
name|TRIE_CODED_NUMERIC_MIN
operator|=
name|longToTrieCoded
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|TRIE_CODED_NUMERIC_MAX
operator|=
name|longToTrieCoded
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|// internal conversion to/from strings
DECL|method|internalLongToTrieCoded
specifier|private
specifier|final
name|String
name|internalLongToTrieCoded
parameter_list|(
name|long
name|l
parameter_list|)
block|{
specifier|final
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
name|TRIE_CODED_LENGTH
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|TRIE_CODED_LENGTH
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|buf
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|TRIE_CODED_SYMBOL_MIN
operator|+
operator|(
name|l
operator|&
name|mask
operator|)
argument_list|)
expr_stmt|;
name|l
operator|=
name|l
operator|>>>
name|TRIE_BITS
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|)
return|;
block|}
DECL|method|internalTrieCodedToLong
specifier|private
specifier|final
name|long
name|internalTrieCodedToLong
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Trie encoded string may not be NULL"
argument_list|)
throw|;
specifier|final
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|TRIE_CODED_LENGTH
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid trie encoded numerical value representation (incompatible length, must be "
operator|+
name|TRIE_CODED_LENGTH
operator|+
literal|")"
argument_list|)
throw|;
name|long
name|l
init|=
literal|0L
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>=
name|TRIE_CODED_SYMBOL_MIN
operator|&&
name|ch
operator|<=
name|TRIE_CODED_SYMBOL_MAX
condition|)
block|{
name|l
operator|=
operator|(
name|l
operator|<<
name|TRIE_BITS
operator|)
operator||
call|(
name|long
call|)
argument_list|(
name|ch
operator|-
name|TRIE_CODED_SYMBOL_MIN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid trie encoded numerical value representation (char "
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
block|}
return|return
name|l
return|;
block|}
comment|// Long's
comment|/** Converts a<code>long</code> value encoded to a<code>String</code>. */
DECL|method|longToTrieCoded
specifier|public
name|String
name|longToTrieCoded
parameter_list|(
specifier|final
name|long
name|l
parameter_list|)
block|{
return|return
name|internalLongToTrieCoded
argument_list|(
name|l
operator|^
literal|0x8000000000000000L
argument_list|)
return|;
block|}
comment|/** Converts a encoded<code>String</code> value back to a<code>long</code>. */
DECL|method|trieCodedToLong
specifier|public
name|long
name|trieCodedToLong
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
name|internalTrieCodedToLong
argument_list|(
name|s
argument_list|)
operator|^
literal|0x8000000000000000L
return|;
block|}
comment|// Double's
comment|/** Converts a<code>double</code> value encoded to a<code>String</code>. */
DECL|method|doubleToTrieCoded
specifier|public
name|String
name|doubleToTrieCoded
parameter_list|(
specifier|final
name|double
name|d
parameter_list|)
block|{
name|long
name|l
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|d
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|l
operator|&
literal|0x8000000000000000L
operator|)
operator|==
literal|0L
condition|)
block|{
comment|//>0
name|l
operator||=
literal|0x8000000000000000L
expr_stmt|;
block|}
else|else
block|{
comment|//<0
name|l
operator|=
operator|~
name|l
expr_stmt|;
block|}
return|return
name|internalLongToTrieCoded
argument_list|(
name|l
argument_list|)
return|;
block|}
comment|/** Converts a encoded<code>String</code> value back to a<code>double</code>. */
DECL|method|trieCodedToDouble
specifier|public
name|double
name|trieCodedToDouble
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
name|long
name|l
init|=
name|internalTrieCodedToLong
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|l
operator|&
literal|0x8000000000000000L
operator|)
operator|!=
literal|0L
condition|)
block|{
comment|//>0
name|l
operator|&=
literal|0x7fffffffffffffffL
expr_stmt|;
block|}
else|else
block|{
comment|//<0
name|l
operator|=
operator|~
name|l
expr_stmt|;
block|}
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|l
argument_list|)
return|;
block|}
comment|// Date's
comment|/** Converts a<code>Date</code> value encoded to a<code>String</code>. */
DECL|method|dateToTrieCoded
specifier|public
name|String
name|dateToTrieCoded
parameter_list|(
specifier|final
name|Date
name|d
parameter_list|)
block|{
return|return
name|longToTrieCoded
argument_list|(
name|d
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
comment|/** Converts a encoded<code>String</code> value back to a<code>Date</code>. */
DECL|method|trieCodedToDate
specifier|public
name|Date
name|trieCodedToDate
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|trieCodedToLong
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
comment|// increment / decrement
comment|/** Increments an encoded String value by 1. Needed by {@link TrieRangeFilter}. */
DECL|method|incrementTrieCoded
specifier|public
name|String
name|incrementTrieCoded
parameter_list|(
specifier|final
name|String
name|v
parameter_list|)
block|{
specifier|final
name|int
name|l
init|=
name|v
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
name|l
index|]
decl_stmt|;
name|boolean
name|inc
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|l
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|b
init|=
name|v
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|-
name|TRIE_CODED_SYMBOL_MIN
decl_stmt|;
if|if
condition|(
name|inc
condition|)
name|b
operator|++
expr_stmt|;
if|if
condition|(
name|inc
operator|=
operator|(
name|b
operator|>
operator|(
name|int
operator|)
name|mask
operator|)
condition|)
name|b
operator|=
literal|0
expr_stmt|;
name|buf
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|TRIE_CODED_SYMBOL_MIN
operator|+
name|b
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|)
return|;
block|}
comment|/** Decrements an encoded String value by 1. Needed by {@link TrieRangeFilter}. */
DECL|method|decrementTrieCoded
specifier|public
name|String
name|decrementTrieCoded
parameter_list|(
specifier|final
name|String
name|v
parameter_list|)
block|{
specifier|final
name|int
name|l
init|=
name|v
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
name|l
index|]
decl_stmt|;
name|boolean
name|dec
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|l
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|b
init|=
name|v
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|-
name|TRIE_CODED_SYMBOL_MIN
decl_stmt|;
if|if
condition|(
name|dec
condition|)
name|b
operator|--
expr_stmt|;
if|if
condition|(
name|dec
operator|=
operator|(
name|b
operator|<
literal|0
operator|)
condition|)
name|b
operator|=
operator|(
name|int
operator|)
name|mask
expr_stmt|;
name|buf
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|TRIE_CODED_SYMBOL_MIN
operator|+
name|b
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|)
return|;
block|}
DECL|method|addConvertedTrieCodedDocumentField
specifier|private
name|void
name|addConvertedTrieCodedDocumentField
parameter_list|(
specifier|final
name|Document
name|ldoc
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|String
name|val
parameter_list|,
specifier|final
name|boolean
name|index
parameter_list|,
specifier|final
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|fieldname
argument_list|,
name|val
argument_list|,
name|store
argument_list|,
name|index
condition|?
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
else|:
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
condition|)
block|{
name|f
operator|.
name|setOmitTf
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ldoc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// add the lower precision values in the helper field with prefix
specifier|final
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|TRIE_CODED_LENGTH
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|sb
init|)
block|{
for|for
control|(
name|int
name|i
init|=
name|TRIE_CODED_LENGTH
operator|-
literal|1
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|Field
argument_list|(
name|fieldname
operator|+
name|LOWER_PRECISION_FIELD_NAME_SUFFIX
argument_list|,
name|sb
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
name|TRIE_CODED_PADDING_START
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
expr_stmt|;
name|f
operator|.
name|setOmitTf
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ldoc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|ldoc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Stores a double value in trie-form in document for indexing. 	 *<p>To store the different precisions of the long values (from one byte [only the most significant one] to the full eight bytes), 	 * each lower precision is prefixed by the length ({@link #TRIE_CODED_PADDING_START}<code>+precision</code>), 	 * in an extra "helper" field with a name of<code>fieldname+{@link #LOWER_PRECISION_FIELD_NAME_SUFFIX}</code> 	 * (i.e. fieldname "numeric" => lower precision's name "numeric#trie"). 	 * The full long is not prefixed at all and indexed and stored according to the given flags in the original field name. 	 * If the field should not be searchable, set<code>index</code> to<code>false</code>. It is then only stored (for convenience). 	 * Fields added to a document using this method can be queried by {@link TrieRangeQuery}.  	 */
DECL|method|addDoubleTrieCodedDocumentField
specifier|public
name|void
name|addDoubleTrieCodedDocumentField
parameter_list|(
specifier|final
name|Document
name|ldoc
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|double
name|val
parameter_list|,
specifier|final
name|boolean
name|index
parameter_list|,
specifier|final
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
name|addConvertedTrieCodedDocumentField
argument_list|(
name|ldoc
argument_list|,
name|fieldname
argument_list|,
name|doubleToTrieCoded
argument_list|(
name|val
argument_list|)
argument_list|,
name|index
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Stores a Date value in trie-form in document for indexing. 	 *<p>To store the different precisions of the long values (from one byte [only the most significant one] to the full eight bytes), 	 * each lower precision is prefixed by the length ({@link #TRIE_CODED_PADDING_START}<code>+precision</code>), 	 * in an extra "helper" field with a name of<code>fieldname+{@link #LOWER_PRECISION_FIELD_NAME_SUFFIX}</code> 	 * (i.e. fieldname "numeric" => lower precision's name "numeric#trie"). 	 * The full long is not prefixed at all and indexed and stored according to the given flags in the original field name. 	 * If the field should not be searchable, set<code>index</code> to<code>false</code>. It is then only stored (for convenience). 	 * Fields added to a document using this method can be queried by {@link TrieRangeQuery}.  	 */
DECL|method|addDateTrieCodedDocumentField
specifier|public
name|void
name|addDateTrieCodedDocumentField
parameter_list|(
specifier|final
name|Document
name|ldoc
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|Date
name|val
parameter_list|,
specifier|final
name|boolean
name|index
parameter_list|,
specifier|final
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
name|addConvertedTrieCodedDocumentField
argument_list|(
name|ldoc
argument_list|,
name|fieldname
argument_list|,
name|dateToTrieCoded
argument_list|(
name|val
argument_list|)
argument_list|,
name|index
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Stores a long value in trie-form in document for indexing. 	 *<p>To store the different precisions of the long values (from one byte [only the most significant one] to the full eight bytes), 	 * each lower precision is prefixed by the length ({@link #TRIE_CODED_PADDING_START}<code>+precision</code>), 	 * in an extra "helper" field with a name of<code>fieldname+{@link #LOWER_PRECISION_FIELD_NAME_SUFFIX}</code> 	 * (i.e. fieldname "numeric" => lower precision's name "numeric#trie"). 	 * The full long is not prefixed at all and indexed and stored according to the given flags in the original field name. 	 * If the field should not be searchable, set<code>index</code> to<code>false</code>. It is then only stored (for convenience). 	 * Fields added to a document using this method can be queried by {@link TrieRangeQuery}.  	 */
DECL|method|addLongTrieCodedDocumentField
specifier|public
name|void
name|addLongTrieCodedDocumentField
parameter_list|(
specifier|final
name|Document
name|ldoc
parameter_list|,
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|long
name|val
parameter_list|,
specifier|final
name|boolean
name|index
parameter_list|,
specifier|final
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
name|addConvertedTrieCodedDocumentField
argument_list|(
name|ldoc
argument_list|,
name|fieldname
argument_list|,
name|longToTrieCoded
argument_list|(
name|val
argument_list|)
argument_list|,
name|index
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
comment|/** A factory method, that generates a {@link SortField} instance for sorting trie encoded values. */
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|FIELD_CACHE_LONG_PARSER
argument_list|)
return|;
block|}
comment|/** A factory method, that generates a {@link SortField} instance for sorting trie encoded values. */
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
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
comment|/** A parser instance for filling a {@link ExtendedFieldCache}, that parses trie encoded fields as longs. */
DECL|field|FIELD_CACHE_LONG_PARSER
specifier|public
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
name|String
name|val
parameter_list|)
block|{
return|return
name|trieCodedToLong
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** A parser instance for filling a {@link ExtendedFieldCache}, that parses trie encoded fields as doubles. */
DECL|field|FIELD_CACHE_DOUBLE_PARSER
specifier|public
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
name|String
name|val
parameter_list|)
block|{
return|return
name|trieCodedToDouble
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|long
name|mask
decl_stmt|;
comment|/** Number of bits used in this trie variant (2, 4, or 8) */
DECL|field|TRIE_BITS
specifier|public
specifier|final
name|int
name|TRIE_BITS
decl_stmt|;
comment|/** Length (in chars) of an encoded value (8, 16, or 32 chars) */
DECL|field|TRIE_CODED_LENGTH
specifier|public
specifier|final
name|int
name|TRIE_CODED_LENGTH
decl_stmt|;
comment|/** Character used as upper end (depends on trie bits, its<code>{@link #TRIE_CODED_SYMBOL_MIN}+2^{@link #TRIE_BITS}-1</code>) */
DECL|field|TRIE_CODED_SYMBOL_MAX
specifier|public
specifier|final
name|char
name|TRIE_CODED_SYMBOL_MAX
decl_stmt|;
comment|/** minimum encoded value of a numerical index entry: {@link Long#MIN_VALUE} */
DECL|field|TRIE_CODED_NUMERIC_MIN
specifier|public
specifier|final
name|String
name|TRIE_CODED_NUMERIC_MIN
decl_stmt|;
comment|/** maximum encoded value of a numerical index entry: {@link Long#MAX_VALUE} */
DECL|field|TRIE_CODED_NUMERIC_MAX
specifier|public
specifier|final
name|String
name|TRIE_CODED_NUMERIC_MAX
decl_stmt|;
block|}
end_class
end_unit
