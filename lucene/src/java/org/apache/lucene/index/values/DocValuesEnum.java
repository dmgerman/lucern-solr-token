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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
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
name|AttributeSource
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
name|FloatsRef
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
name|LongsRef
import|;
end_import
begin_comment
comment|/**  * {@link DocValuesEnum} is a {@link DocIdSetIterator} iterating<tt>byte[]</tt>  * ,<tt>long</tt> and<tt>double</tt> stored per document. Depending on the  * enum's {@link Type} ({@link #type()}) the enum might skip over documents that  * have no value stored. Types like {@link Type#BYTES_VAR_STRAIGHT} might not  * skip over documents even if there is no value associated with a document. The  * value for document without values again depends on the types implementation  * although a reference for a {@link Type} returned from a accessor method  * {@link #getFloat()}, {@link #getInt()} or {@link #bytes()} will never be  *<code>null</code> even if a document has no value.  *<p>  * Note: Only the reference for the enum's type are initialized to non  *<code>null</code> ie. {@link #getInt()} will always return<code>null</code>  * if the enum's Type is {@link Type#SIMPLE_FLOAT_4BYTE}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValuesEnum
specifier|public
specifier|abstract
class|class
name|DocValuesEnum
extends|extends
name|DocIdSetIterator
block|{
DECL|field|source
specifier|private
name|AttributeSource
name|source
decl_stmt|;
DECL|field|enumType
specifier|private
specifier|final
name|Type
name|enumType
decl_stmt|;
DECL|field|bytesRef
specifier|protected
name|BytesRef
name|bytesRef
decl_stmt|;
DECL|field|floatsRef
specifier|protected
name|FloatsRef
name|floatsRef
decl_stmt|;
DECL|field|intsRef
specifier|protected
name|LongsRef
name|intsRef
decl_stmt|;
comment|/**    * Creates a new {@link DocValuesEnum} for the given type. The    * {@link AttributeSource} for this enum is set to<code>null</code>    */
DECL|method|DocValuesEnum
specifier|protected
name|DocValuesEnum
parameter_list|(
name|Type
name|enumType
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|enumType
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link DocValuesEnum} for the given type.    */
DECL|method|DocValuesEnum
specifier|protected
name|DocValuesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Type
name|enumType
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|enumType
operator|=
name|enumType
expr_stmt|;
switch|switch
condition|(
name|enumType
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|bytesRef
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
break|break;
case|case
name|PACKED_INTS
case|:
name|intsRef
operator|=
operator|new
name|LongsRef
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIMPLE_FLOAT_4BYTE
case|:
case|case
name|SIMPLE_FLOAT_8BYTE
case|:
name|floatsRef
operator|=
operator|new
name|FloatsRef
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|/**    * Returns the type of this enum    */
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|enumType
return|;
block|}
comment|/**    * Returns a {@link BytesRef} or<code>null</code> if this enum doesn't    * enumerate byte[] values    */
DECL|method|bytes
specifier|public
name|BytesRef
name|bytes
parameter_list|()
block|{
return|return
name|bytesRef
return|;
block|}
comment|/**    * Returns a {@link FloatsRef} or<code>null</code> if this enum doesn't    * enumerate floating point values    */
DECL|method|getFloat
specifier|public
name|FloatsRef
name|getFloat
parameter_list|()
block|{
return|return
name|floatsRef
return|;
block|}
comment|/**    * Returns a {@link LongsRef} or<code>null</code> if this enum doesn't    * enumerate integer values.    */
DECL|method|getInt
specifier|public
name|LongsRef
name|getInt
parameter_list|()
block|{
return|return
name|intsRef
return|;
block|}
comment|/**    * Copies the internal state from the given enum    */
DECL|method|copyFrom
specifier|protected
name|void
name|copyFrom
parameter_list|(
name|DocValuesEnum
name|valuesEnum
parameter_list|)
block|{
name|intsRef
operator|=
name|valuesEnum
operator|.
name|intsRef
expr_stmt|;
name|floatsRef
operator|=
name|valuesEnum
operator|.
name|floatsRef
expr_stmt|;
name|bytesRef
operator|=
name|valuesEnum
operator|.
name|bytesRef
expr_stmt|;
name|source
operator|=
name|valuesEnum
operator|.
name|source
expr_stmt|;
block|}
comment|/**    * Returns the {@link AttributeSource} associated with this enum.    *<p>    * Note: this method might create a new AttribueSource if no    * {@link AttributeSource} has been provided during enum creation.    */
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|source
operator|=
operator|new
name|AttributeSource
argument_list|()
expr_stmt|;
block|}
return|return
name|source
return|;
block|}
comment|/**    * Closes the enum    *     * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an empty {@link DocValuesEnum} for the given {@link Type}.    */
DECL|method|emptyEnum
specifier|public
specifier|static
name|DocValuesEnum
name|emptyEnum
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
return|return
operator|new
name|DocValuesEnum
argument_list|(
name|type
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{        }
block|}
return|;
block|}
block|}
end_class
end_unit
