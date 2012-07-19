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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
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
name|codecs
operator|.
name|DocValuesFormat
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
name|ByteDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|DerefBytesDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|DoubleDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|FloatDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|IntDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|LongDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|PackedLongDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|ShortDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|SortedBytesDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|StraightBytesDocValuesField
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataOutput
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|/**  * {@link DocValues} provides a dense per-document typed storage for fast  * value access based on the lucene internal document id. {@link DocValues}  * exposes two distinct APIs:  *<ul>  *<li>via {@link #getSource()} providing RAM resident random access</li>  *<li>via {@link #getDirectSource()} providing on disk random access</li>  *</ul> {@link DocValues} are exposed via  * {@link AtomicReader#docValues(String)} on a per-segment basis. For best  * performance {@link DocValues} should be consumed per-segment just like  * IndexReader.  *<p>  * {@link DocValues} are fully integrated into the {@link DocValuesFormat} API.  *<p>  * NOTE: DocValues is a strongly typed per-field API. Type changes within an  * indexing session can result in exceptions if the type has changed in a way that  * the previously give type for a field can't promote the value without losing  * information. For instance a field initially indexed with {@link Type#FIXED_INTS_32}  * can promote a value with {@link Type#FIXED_INTS_8} but can't promote  * {@link Type#FIXED_INTS_64}. During segment merging type-promotion exceptions are suppressed.   * Fields will be promoted to their common denominator or automatically transformed  * into a 3rd type like {@link Type#BYTES_VAR_STRAIGHT} to prevent data loss and merge exceptions.  * This behavior is considered<i>best-effort</i> might change in future releases.  *</p>  *   * @see Type for limitations and default implementation documentation  * @see ByteDocValuesField for adding byte values to the index  * @see ShortDocValuesField for adding short values to the index  * @see IntDocValuesField for adding int values to the index  * @see LongDocValuesField for adding long values to the index  * @see FloatDocValuesField for adding float values to the index  * @see DoubleDocValuesField for adding double values to the index  * @see PackedLongDocValuesField for adding packed long values to the index  * @see SortedBytesDocValuesField for adding sorted {@link BytesRef} values to the index  * @see StraightBytesDocValuesField for adding straight {@link BytesRef} values to the index  * @see DerefBytesDocValuesField for adding deref {@link BytesRef} values to the index  * @see DocValuesFormat#docsConsumer(org.apache.lucene.index.PerDocWriteState) for  *      customization  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValues
specifier|public
specifier|abstract
class|class
name|DocValues
implements|implements
name|Closeable
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|DocValues
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|DocValues
index|[
literal|0
index|]
decl_stmt|;
DECL|field|cache
specifier|private
specifier|volatile
name|SourceCache
name|cache
init|=
operator|new
name|SourceCache
operator|.
name|DirectSourceCache
argument_list|()
decl_stmt|;
DECL|field|cacheLock
specifier|private
specifier|final
name|Object
name|cacheLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/**    * Loads a new {@link Source} instance for this {@link DocValues} field    * instance. Source instances returned from this method are not cached. It is    * the callers responsibility to maintain the instance and release its    * resources once the source is not needed anymore.    *<p>    * For managed {@link Source} instances see {@link #getSource()}.    *     * @see #getSource()    * @see #setCache(SourceCache)    */
DECL|method|load
specifier|public
specifier|abstract
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a {@link Source} instance through the current {@link SourceCache}.    * Iff no {@link Source} has been loaded into the cache so far the source will    * be loaded through {@link #load()} and passed to the {@link SourceCache}.    * The caller of this method should not close the obtained {@link Source}    * instance unless it is not needed for the rest of its life time.    *<p>    * {@link Source} instances obtained from this method are closed / released    * from the cache once this {@link DocValues} instance is closed by the    * {@link IndexReader}, {@link Fields} or {@link FieldsEnum} the    * {@link DocValues} was created from.    */
DECL|method|getSource
specifier|public
name|Source
name|getSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns a disk resident {@link Source} instance. Direct Sources are not    * cached in the {@link SourceCache} and should not be shared between threads.    */
DECL|method|getDirectSource
specifier|public
specifier|abstract
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the {@link Type} of this {@link DocValues} instance    */
DECL|method|getType
specifier|public
specifier|abstract
name|Type
name|getType
parameter_list|()
function_decl|;
comment|/**    * Closes this {@link DocValues} instance. This method should only be called    * by the creator of this {@link DocValues} instance. API users should not    * close {@link DocValues} instances.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|cache
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the size per value in bytes or<code>-1</code> iff size per value    * is variable.    *     * @return the size per value in bytes or<code>-1</code> iff size per value    * is variable.    */
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Sets the {@link SourceCache} used by this {@link DocValues} instance. This    * method should be called before {@link #load()} is called. All {@link Source} instances in the currently used cache will be closed    * before the new cache is installed.    *<p>    * Note: All instances previously obtained from {@link #load()} will be lost.    *     * @throws IllegalArgumentException    *           if the given cache is<code>null</code>    *     */
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|SourceCache
name|cache
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cache must not be null"
argument_list|)
throw|;
synchronized|synchronized
init|(
name|cacheLock
init|)
block|{
name|SourceCache
name|toClose
init|=
name|this
operator|.
name|cache
decl_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|toClose
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Source of per document values like long, double or {@link BytesRef}    * depending on the {@link DocValues} fields {@link Type}. Source    * implementations provide random access semantics similar to array lookups    *<p>    * @see DocValues#getSource()    * @see DocValues#getDirectSource()    */
DECL|class|Source
specifier|public
specifier|static
specifier|abstract
class|class
name|Source
block|{
DECL|field|type
specifier|protected
specifier|final
name|Type
name|type
decl_stmt|;
DECL|method|Source
specifier|protected
name|Source
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Returns a<tt>long</tt> for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>long</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>long</tt> values.      */
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ints are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns a<tt>double</tt> for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>double</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>double</tt> values.      */
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"floats are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns a {@link BytesRef} for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>byte[]</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>byte[]</tt> values.      */
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"bytes are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns the {@link Type} of this source.      *       * @return the {@link Type} of this source.      */
DECL|method|getType
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Returns<code>true</code> iff this {@link Source} exposes an array via      * {@link #getArray()} otherwise<code>false</code>.      *       * @return<code>true</code> iff this {@link Source} exposes an array via      *         {@link #getArray()} otherwise<code>false</code>.      */
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns the internal array representation iff this {@link Source} uses an      * array as its inner representation, otherwise<code>UOE</code>.      */
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"getArray is not supported"
argument_list|)
throw|;
block|}
comment|/**      * If this {@link Source} is sorted this method will return an instance of      * {@link SortedSource} otherwise<code>UOE</code>      */
DECL|method|asSortedSource
specifier|public
name|SortedSource
name|asSortedSource
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"asSortedSource is not supported"
argument_list|)
throw|;
block|}
block|}
comment|/**    * A sorted variant of {@link Source} for<tt>byte[]</tt> values per document.    *<p>    */
DECL|class|SortedSource
specifier|public
specifier|static
specifier|abstract
class|class
name|SortedSource
extends|extends
name|Source
block|{
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
DECL|method|SortedSource
specifier|protected
name|SortedSource
parameter_list|(
name|Type
name|type
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|ord
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
comment|// Negative ord means doc was missing?
name|bytesRef
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|getByOrd
argument_list|(
name|ord
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
return|return
name|bytesRef
return|;
block|}
comment|/**      * Returns ord for specified docID. Ord is dense, ie, starts at 0, then increments by 1      * for the next (as defined by {@link Comparator} value.      */
DECL|method|ord
specifier|public
specifier|abstract
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Returns value for specified ord. */
DECL|method|getByOrd
specifier|public
specifier|abstract
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
function_decl|;
comment|/** Return true if it's safe to call {@link      *  #getDocToOrd}. */
DECL|method|hasPackedDocToOrd
specifier|public
name|boolean
name|hasPackedDocToOrd
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns the PackedInts.Reader impl that maps document to ord.      */
DECL|method|getDocToOrd
specifier|public
specifier|abstract
name|PackedInts
operator|.
name|Reader
name|getDocToOrd
parameter_list|()
function_decl|;
comment|/**      * Returns the comparator used to order the BytesRefs.      */
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
comment|/**      * Lookup ord by value.      *       * @param value      *          the value to look up      * @param spare      *          a spare {@link BytesRef} instance used to compare internal      *          values to the given value. Must not be<code>null</code>      * @return the given values ordinal if found or otherwise      *<code>(-(ord)-1)</code>, defined as the ordinal of the first      *         element that is greater than the given value (the insertion      *         point). This guarantees that the return value will always be      *&gt;= 0 if the given value is found.      */
DECL|method|getOrdByValue
specifier|public
name|int
name|getOrdByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|BytesRef
name|spare
parameter_list|)
block|{
return|return
name|binarySearch
argument_list|(
name|value
argument_list|,
name|spare
argument_list|,
literal|0
argument_list|,
name|getValueCount
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|binarySearch
specifier|private
name|int
name|binarySearch
parameter_list|(
name|BytesRef
name|b
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|,
name|int
name|low
parameter_list|,
name|int
name|high
parameter_list|)
block|{
name|int
name|mid
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
expr_stmt|;
name|getByOrd
argument_list|(
name|mid
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
specifier|final
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|mid
return|;
block|}
block|}
assert|assert
name|comparator
operator|.
name|compare
argument_list|(
name|bytesRef
argument_list|,
name|b
argument_list|)
operator|!=
literal|0
assert|;
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|asSortedSource
specifier|public
name|SortedSource
name|asSortedSource
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/**      * Returns the number of unique values in this sorted source      */
DECL|method|getValueCount
specifier|public
specifier|abstract
name|int
name|getValueCount
parameter_list|()
function_decl|;
block|}
comment|/** Returns a Source that always returns default (missing)    *  values for all documents. */
DECL|method|getDefaultSource
specifier|public
specifier|static
name|Source
name|getDefaultSource
parameter_list|(
specifier|final
name|Type
name|type
parameter_list|)
block|{
return|return
operator|new
name|Source
argument_list|(
name|type
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0.0
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
name|ref
operator|.
name|length
operator|=
literal|0
expr_stmt|;
return|return
name|ref
return|;
block|}
block|}
return|;
block|}
comment|/** Returns a SortedSource that always returns default (missing)    *  values for all documents. */
DECL|method|getDefaultSortedSource
specifier|public
specifier|static
name|SortedSource
name|getDefaultSortedSource
parameter_list|(
specifier|final
name|Type
name|type
parameter_list|,
specifier|final
name|int
name|size
parameter_list|)
block|{
specifier|final
name|PackedInts
operator|.
name|Reader
name|docToOrd
init|=
operator|new
name|PackedInts
operator|.
name|Reader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getBitsPerValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|size
argument_list|()
operator|-
name|index
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|off
operator|+
name|len
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|SortedSource
argument_list|(
name|type
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
name|ref
operator|.
name|length
operator|=
literal|0
expr_stmt|;
return|return
name|ref
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
assert|assert
name|ord
operator|==
literal|0
assert|;
name|bytesRef
operator|.
name|length
operator|=
literal|0
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPackedDocToOrd
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|PackedInts
operator|.
name|Reader
name|getDocToOrd
parameter_list|()
block|{
return|return
name|docToOrd
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOrdByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|BytesRef
name|spare
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
return|;
block|}
comment|/**    *<code>Type</code> specifies the {@link DocValues} type for a    * certain field. A<code>Type</code> only defines the data type for a field    * while the actual implementation used to encode and decode the values depends    * on the the {@link DocValuesFormat#docsConsumer} and {@link DocValuesFormat#docsProducer} methods.    *     * @lucene.experimental    */
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
comment|/**      * A variable bit signed integer value. By default this type uses      * {@link PackedInts} to compress the values, as an offset      * from the minimum value, as long as the value range      * fits into 2<sup>63</sup>-1. Otherwise,      * the default implementation falls back to fixed size 64bit      * integers ({@link #FIXED_INTS_64}).      *<p>      * NOTE: this type uses<tt>0</tt> as the default value without any      * distinction between provided<tt>0</tt> values during indexing. All      * documents without an explicit value will use<tt>0</tt> instead.      * Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|VAR_INTS
name|VAR_INTS
block|,
comment|/**      * A 8 bit signed integer value. {@link Source} instances of      * this type return a<tt>byte</tt> array from {@link Source#getArray()}      *<p>      * NOTE: this type uses<tt>0</tt> as the default value without any      * distinction between provided<tt>0</tt> values during indexing. All      * documents without an explicit value will use<tt>0</tt> instead.      * Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|FIXED_INTS_8
name|FIXED_INTS_8
block|,
comment|/**      * A 16 bit signed integer value. {@link Source} instances of      * this type return a<tt>short</tt> array from {@link Source#getArray()}      *<p>      * NOTE: this type uses<tt>0</tt> as the default value without any      * distinction between provided<tt>0</tt> values during indexing. All      * documents without an explicit value will use<tt>0</tt> instead.      * Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|FIXED_INTS_16
name|FIXED_INTS_16
block|,
comment|/**      * A 32 bit signed integer value. {@link Source} instances of      * this type return a<tt>int</tt> array from {@link Source#getArray()}      *<p>      * NOTE: this type uses<tt>0</tt> as the default value without any      * distinction between provided<tt>0</tt> values during indexing. All      * documents without an explicit value will use<tt>0</tt> instead.       * Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|FIXED_INTS_32
name|FIXED_INTS_32
block|,
comment|/**      * A 64 bit signed integer value. {@link Source} instances of      * this type return a<tt>long</tt> array from {@link Source#getArray()}      *<p>      * NOTE: this type uses<tt>0</tt> as the default value without any      * distinction between provided<tt>0</tt> values during indexing. All      * documents without an explicit value will use<tt>0</tt> instead.      * Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|FIXED_INTS_64
name|FIXED_INTS_64
block|,
comment|/**      * A 32 bit floating point value. By default there is no compression      * applied. To fit custom float values into less than 32bit either a custom      * implementation is needed or values must be encoded into a      * {@link #BYTES_FIXED_STRAIGHT} type. {@link Source} instances of      * this type return a<tt>float</tt> array from {@link Source#getArray()}      *<p>      * NOTE: this type uses<tt>0.0f</tt> as the default value without any      * distinction between provided<tt>0.0f</tt> values during indexing. All      * documents without an explicit value will use<tt>0.0f</tt> instead.      * Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|FLOAT_32
name|FLOAT_32
block|,
comment|/**      *       * A 64 bit floating point value. By default there is no compression      * applied. To fit custom float values into less than 64bit either a custom      * implementation is needed or values must be encoded into a      * {@link #BYTES_FIXED_STRAIGHT} type. {@link Source} instances of      * this type return a<tt>double</tt> array from {@link Source#getArray()}      *<p>      * NOTE: this type uses<tt>0.0d</tt> as the default value without any      * distinction between provided<tt>0.0d</tt> values during indexing. All      * documents without an explicit value will use<tt>0.0d</tt> instead.      * Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|FLOAT_64
name|FLOAT_64
block|,
comment|// TODO(simonw): -- shouldn't lucene decide/detect straight vs
comment|// deref, as well fixed vs var?
comment|/**      * A fixed length straight byte[]. All values added to      * such a field must be of the same length. All bytes are stored sequentially      * for fast offset access.      *<p>      * NOTE: this type uses<tt>0 byte</tt> filled byte[] based on the length of the first seen      * value as the default value without any distinction between explicitly      * provided values during indexing. All documents without an explicit value      * will use the default instead.Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|BYTES_FIXED_STRAIGHT
name|BYTES_FIXED_STRAIGHT
block|,
comment|/**      * A fixed length dereferenced byte[] variant. Fields with      * this type only store distinct byte values and store an additional offset      * pointer per document to dereference the shared byte[].      * Use this type if your documents may share the same byte[].      *<p>      * NOTE: Fields of this type will not store values for documents without an      * explicitly provided value. If a documents value is accessed while no      * explicit value is stored the returned {@link BytesRef} will be a 0-length      * reference. Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|BYTES_FIXED_DEREF
name|BYTES_FIXED_DEREF
block|,
comment|/**      * Variable length straight stored byte[] variant. All bytes are      * stored sequentially for compactness. Usage of this type via the      * disk-resident API might yield performance degradation since no additional      * index is used to advance by more than one document value at a time.      *<p>      * NOTE: Fields of this type will not store values for documents without an      * explicitly provided value. If a documents value is accessed while no      * explicit value is stored the returned {@link BytesRef} will be a 0-length      * byte[] reference. Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|BYTES_VAR_STRAIGHT
name|BYTES_VAR_STRAIGHT
block|,
comment|/**      * A variable length dereferenced byte[]. Just like      * {@link #BYTES_FIXED_DEREF}, but allowing each      * document's value to be a different length.      *<p>      * NOTE: Fields of this type will not store values for documents without an      * explicitly provided value. If a documents value is accessed while no      * explicit value is stored the returned {@link BytesRef} will be a 0-length      * reference. Custom default values must be assigned explicitly.      *</p>      */
DECL|enum constant|BYTES_VAR_DEREF
name|BYTES_VAR_DEREF
block|,
comment|/**      * A variable length pre-sorted byte[] variant. Just like      * {@link #BYTES_FIXED_SORTED}, but allowing each      * document's value to be a different length.      *<p>      * NOTE: Fields of this type will not store values for documents without an      * explicitly provided value. If a documents value is accessed while no      * explicit value is stored the returned {@link BytesRef} will be a 0-length      * reference.Custom default values must be assigned explicitly.      *</p>      *       * @see SortedSource      */
DECL|enum constant|BYTES_VAR_SORTED
name|BYTES_VAR_SORTED
block|,
comment|/**      * A fixed length pre-sorted byte[] variant. Fields with this type only      * store distinct byte values and store an additional offset pointer per      * document to dereference the shared byte[]. The stored      * byte[] is presorted, by default by unsigned byte order,      * and allows access via document id, ordinal and by-value.      * Use this type if your documents may share the same byte[].      *<p>      * NOTE: Fields of this type will not store values for documents without an      * explicitly provided value. If a documents value is accessed while no      * explicit value is stored the returned {@link BytesRef} will be a 0-length      * reference. Custom default values must be assigned      * explicitly.      *</p>      *       * @see SortedSource      */
DECL|enum constant|BYTES_FIXED_SORTED
name|BYTES_FIXED_SORTED
block|}
comment|/**    * Abstract base class for {@link DocValues} {@link Source} cache.    *<p>    * {@link Source} instances loaded via {@link DocValues#load()} are entirely memory resident    * and need to be maintained by the caller. Each call to    * {@link DocValues#load()} will cause an entire reload of    * the underlying data. Source instances obtained from    * {@link DocValues#getSource()} and {@link DocValues#getSource()}    * respectively are maintained by a {@link SourceCache} that is closed (    * {@link #close(DocValues)}) once the {@link IndexReader} that created the    * {@link DocValues} instance is closed.    *<p>    * Unless {@link Source} instances are managed by another entity it is    * recommended to use the cached variants to obtain a source instance.    *<p>    * Implementation of this API must be thread-safe.    *     * @see DocValues#setCache(SourceCache)    * @see DocValues#getSource()    *     * @lucene.experimental    */
DECL|class|SourceCache
specifier|public
specifier|static
specifier|abstract
class|class
name|SourceCache
block|{
comment|/**      * Atomically loads a {@link Source} into the cache from the given      * {@link DocValues} and returns it iff no other {@link Source} has already      * been cached. Otherwise the cached source is returned.      *<p>      * This method will not return<code>null</code>      */
DECL|method|load
specifier|public
specifier|abstract
name|Source
name|load
parameter_list|(
name|DocValues
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Atomically invalidates the cached {@link Source}       * instances if any and empties the cache.      */
DECL|method|invalidate
specifier|public
specifier|abstract
name|void
name|invalidate
parameter_list|(
name|DocValues
name|values
parameter_list|)
function_decl|;
comment|/**      * Atomically closes the cache and frees all resources.      */
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|DocValues
name|values
parameter_list|)
block|{
name|invalidate
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**      * Simple per {@link DocValues} instance cache implementation that holds a      * {@link Source} a member variable.      *<p>      * If a {@link DirectSourceCache} instance is closed or invalidated the cached      * reference are simply set to<code>null</code>      */
DECL|class|DirectSourceCache
specifier|public
specifier|static
specifier|final
class|class
name|DirectSourceCache
extends|extends
name|SourceCache
block|{
DECL|field|ref
specifier|private
name|Source
name|ref
decl_stmt|;
DECL|method|load
specifier|public
specifier|synchronized
name|Source
name|load
parameter_list|(
name|DocValues
name|values
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
name|ref
operator|=
name|values
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
return|return
name|ref
return|;
block|}
DECL|method|invalidate
specifier|public
specifier|synchronized
name|void
name|invalidate
parameter_list|(
name|DocValues
name|values
parameter_list|)
block|{
name|ref
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
