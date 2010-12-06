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
comment|/**  * @lucene.experimental  */
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
DECL|field|INDEX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_EXTENSION
init|=
literal|"idx"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dat"
decl_stmt|;
comment|/** Records the specfied value for the docID */
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
comment|/** Records the specfied value for the docID */
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
comment|/** Records the specfied value for the docID */
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
comment|/** Records the specfied value for the docID */
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
DECL|method|setNextEnum
specifier|protected
specifier|abstract
name|void
name|setNextEnum
parameter_list|(
name|ValuesEnum
name|valuesEnum
parameter_list|)
function_decl|;
comment|/** Finish writing, close any files */
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
comment|// enables bulk copies in subclasses per MergeState
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
specifier|final
name|ValuesEnum
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
name|ValuesEnum
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
name|ValuesEnum
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
DECL|method|create
specifier|public
specifier|static
name|Writer
name|create
parameter_list|(
name|Values
name|v
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
switch|switch
condition|(
name|v
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
name|v
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
