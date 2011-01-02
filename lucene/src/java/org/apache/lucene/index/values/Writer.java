begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|codecs
operator|.
name|docvalues
operator|.
name|DocValuesConsumer
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
name|store
operator|.
name|Directory
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
name|Bits
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
comment|/**  * Abstract API for per-document stored primitive values of type<tt>byte[]</tt>  * ,<tt>long</tt> or<tt>double</tt>. The API accepts a single value for each  * document. The underlying storage mechanism, file formats, data-structures and  * representations depend on the actual implementation.  *<p>  * Document IDs passed to this API must always be increasing unless stated  * otherwise.  *</p>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Writer
specifier|public
specifier|abstract
class|class
name|Writer
extends|extends
name|DocValuesConsumer
block|{
comment|/**    * Creates a new {@link Writer}.    *     * @param bytesUsed    *          bytes-usage tracking reference used by implementation to track    *          internally allocated memory. All tracked bytes must be released    *          once {@link #finish(int)} has been called.    */
DECL|method|Writer
specifier|protected
name|Writer
parameter_list|(
name|AtomicLong
name|bytesUsed
parameter_list|)
block|{
name|super
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
comment|/**    * Filename extension for index files    */
DECL|field|INDEX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_EXTENSION
init|=
literal|"idx"
decl_stmt|;
comment|/**    * Filename extension for data files.    */
DECL|field|DATA_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dat"
decl_stmt|;
comment|/**    * Records the specified<tt>long</tt> value for the docID or throws an    * {@link UnsupportedOperationException} if this {@link Writer} doesn't record    *<tt>long</tt> values.    *     * @throws UnsupportedOperationException    *           if this writer doesn't record<tt>long</tt> values    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Records the specified<tt>double</tt> value for the docID or throws an    * {@link UnsupportedOperationException} if this {@link Writer} doesn't record    *<tt>double</tt> values.    *     * @throws UnsupportedOperationException    *           if this writer doesn't record<tt>double</tt> values    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Records the specified {@link BytesRef} value for the docID or throws an    * {@link UnsupportedOperationException} if this {@link Writer} doesn't record    * {@link BytesRef} values.    *     * @throws UnsupportedOperationException    *           if this writer doesn't record {@link BytesRef} values    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Records a value from the given document id. The methods implementation    * obtains the value for the document id from the last {@link DocValuesEnum}    * set to {@link #setNextEnum(DocValuesEnum)}.    *<p>    * This method is used during merging to provide implementation agnostic    * default merge implementation.    *</p>    *<p>    * The given document id must be the same document id returned from    * {@link DocValuesEnum#docID()} when this method is called. All documents IDs    * between the given ID and the previously given ID or<tt>0</tt> if the    * method is call the first time are filled with default values depending on    * the {@link Writer} implementation. The given document ID must always be    * greater than the previous ID or<tt>0</tt> if called the first time.    */
DECL|method|add
specifier|protected
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the next {@link DocValuesEnum} to consume values from on calls to    * {@link #add(int)}    *     * @param valuesEnum    *          the next {@link DocValuesEnum}, this must not be null    */
DECL|method|setNextEnum
specifier|protected
specifier|abstract
name|void
name|setNextEnum
parameter_list|(
name|DocValuesEnum
name|valuesEnum
parameter_list|)
function_decl|;
comment|/**    * Finish writing and close any files and resources used by this Writer.    *     * @param docCount    *          the total number of documents for this writer. This must be    *          greater that or equal to the largest document id passed to one of    *          the add methods after the {@link Writer} was created.    */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|MergeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This enables bulk copies in subclasses per MergeState, subclasses can
comment|// simply override this and decide if they want to merge
comment|// segments using this generic implementation or if a bulk merge is possible
comment|// / feasible.
specifier|final
name|DocValuesEnum
name|valEnum
init|=
name|state
operator|.
name|reader
operator|.
name|getEnum
argument_list|()
decl_stmt|;
assert|assert
name|valEnum
operator|!=
literal|null
assert|;
try|try
block|{
name|setNextEnum
argument_list|(
name|valEnum
argument_list|)
expr_stmt|;
comment|// set the current enum we are working on - the
comment|// impl. will get the correct reference for the type
comment|// it supports
name|int
name|docID
init|=
name|state
operator|.
name|docBase
decl_stmt|;
specifier|final
name|Bits
name|bits
init|=
name|state
operator|.
name|bits
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|state
operator|.
name|docCount
decl_stmt|;
name|int
name|currentDocId
decl_stmt|;
if|if
condition|(
operator|(
name|currentDocId
operator|=
name|valEnum
operator|.
name|advance
argument_list|(
literal|0
argument_list|)
operator|)
operator|!=
name|DocValuesEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|bits
operator|==
literal|null
operator|||
operator|!
name|bits
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
if|if
condition|(
name|currentDocId
operator|<
name|i
condition|)
block|{
if|if
condition|(
operator|(
name|currentDocId
operator|=
name|valEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
operator|)
operator|==
name|DocValuesEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
comment|// advance can jump over default values
block|}
block|}
if|if
condition|(
name|currentDocId
operator|==
name|i
condition|)
block|{
comment|// we are on the doc to merge
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
operator|++
name|docID
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|valEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Factory method to create a {@link Writer} instance for a given type. This    * method returns default implementations for each of the different types    * defined in the {@link Type} enumeration.    *     * @param type    *          the {@link Type} to create the {@link Writer} for    * @param id    *          the file name id used to create files within the writer.    * @param directory    *          the {@link Directory} to create the files from.    * @param comp    *          a {@link BytesRef} comparator used for {@link Bytes} variants. If    *<code>null</code>    *          {@link BytesRef#getUTF8SortedAsUnicodeComparator()} is used as the    *          default.    * @param bytesUsed    *          a byte-usage tracking reference    * @return a new {@link Writer} instance for the given {@link Type}    * @throws IOException    */
DECL|method|create
specifier|public
specifier|static
name|Writer
name|create
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|comp
operator|==
literal|null
condition|)
block|{
name|comp
operator|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
expr_stmt|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PACKED_INTS
case|:
return|return
name|Ints
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
literal|true
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|SIMPLE_FLOAT_4BYTE
case|:
return|return
name|Floats
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
literal|4
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|SIMPLE_FLOAT_8BYTE
case|:
return|return
name|Floats
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
literal|8
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
name|comp
argument_list|,
literal|true
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|BYTES_FIXED_DEREF
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
name|comp
argument_list|,
literal|true
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
name|comp
argument_list|,
literal|true
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
name|comp
argument_list|,
literal|false
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|BYTES_VAR_DEREF
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
name|comp
argument_list|,
literal|false
argument_list|,
name|bytesUsed
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
name|comp
argument_list|,
literal|false
argument_list|,
name|bytesUsed
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown Values: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
