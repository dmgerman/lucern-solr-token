begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.docvalues
package|package
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
name|ArrayList
import|;
end_import
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|FieldInfo
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
name|FieldInfos
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
name|FieldsEnum
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
name|SegmentInfo
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
name|SegmentReadState
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
name|SegmentWriteState
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
name|Terms
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
name|TermsEnum
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
name|Codec
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
name|FieldsConsumer
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
name|FieldsProducer
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
name|TermsConsumer
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
name|values
operator|.
name|DocValues
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
name|values
operator|.
name|Writer
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
name|AttributeSource
import|;
end_import
begin_comment
comment|/**  * A codec that adds DocValues support to a given codec transparently.  */
end_comment
begin_class
DECL|class|DocValuesCodec
specifier|public
class|class
name|DocValuesCodec
extends|extends
name|Codec
block|{
DECL|field|consumers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|WrappingFieldsConsumer
argument_list|>
name|consumers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|WrappingFieldsConsumer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|other
specifier|private
specifier|final
name|Codec
name|other
decl_stmt|;
DECL|method|DocValuesCodec
specifier|public
name|DocValuesCodec
parameter_list|(
name|Codec
name|other
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
literal|"docvalues_"
operator|+
name|other
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|WrappingFieldsConsumer
name|consumer
decl_stmt|;
if|if
condition|(
operator|(
name|consumer
operator|=
name|consumers
operator|.
name|get
argument_list|(
name|state
operator|.
name|segmentName
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|consumer
operator|=
operator|new
name|WrappingFieldsConsumer
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|state
operator|=
name|state
expr_stmt|;
comment|// nocommit this is a hack and only necessary since
comment|// we want to initialized the wrapped
comment|// fieldsConsumer lazily with a SegmentWriteState created after the docvalue
comment|// ones is. We should fix this in DocumentWriter I guess. See
comment|// DocFieldProcessor too!
return|return
name|consumer
return|;
block|}
DECL|class|WrappingFieldsConsumer
specifier|private
specifier|static
class|class
name|WrappingFieldsConsumer
extends|extends
name|FieldsConsumer
block|{
DECL|field|state
name|SegmentWriteState
name|state
decl_stmt|;
DECL|field|docValuesConsumers
specifier|private
specifier|final
name|List
argument_list|<
name|DocValuesConsumer
argument_list|>
name|docValuesConsumers
init|=
operator|new
name|ArrayList
argument_list|<
name|DocValuesConsumer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|wrappedConsumer
specifier|private
name|FieldsConsumer
name|wrappedConsumer
decl_stmt|;
DECL|field|other
specifier|private
specifier|final
name|Codec
name|other
decl_stmt|;
DECL|method|WrappingFieldsConsumer
specifier|public
name|WrappingFieldsConsumer
parameter_list|(
name|Codec
name|other
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|wrappedConsumer
operator|!=
literal|null
condition|)
name|wrappedConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addValuesField
specifier|public
specifier|synchronized
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesConsumer
name|consumer
init|=
name|DocValuesConsumer
operator|.
name|create
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
name|state
operator|.
name|directory
argument_list|,
name|field
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// TODO: set comparator here
name|docValuesConsumers
operator|.
name|add
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
return|return
name|consumer
return|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|wrappedConsumer
operator|==
literal|null
condition|)
name|wrappedConsumer
operator|=
name|other
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
return|return
name|wrappedConsumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|state
operator|.
name|dir
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|other
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|files
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|string
range|:
name|files
control|)
block|{
if|if
condition|(
name|dir
operator|.
name|fileExists
argument_list|(
name|string
argument_list|)
condition|)
return|return
operator|new
name|WrappingFielsdProducer
argument_list|(
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|other
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|WrappingFielsdProducer
argument_list|(
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|FieldsProducer
operator|.
name|EMPTY
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|otherFiles
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|other
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|otherFiles
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|string
range|:
name|otherFiles
control|)
block|{
comment|// under some circumstances we only write DocValues
comment|// so other files will be added even if they don't exist
if|if
condition|(
name|dir
operator|.
name|fileExists
argument_list|(
name|string
argument_list|)
condition|)
name|files
operator|.
name|add
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|)
operator|&&
operator|(
name|file
operator|.
name|endsWith
argument_list|(
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
operator|||
name|file
operator|.
name|endsWith
argument_list|(
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
operator|)
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|other
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|Writer
operator|.
name|INDEX_EXTENSION
argument_list|)
expr_stmt|;
block|}
DECL|class|WrappingFielsdProducer
specifier|static
class|class
name|WrappingFielsdProducer
extends|extends
name|DocValuesProducerBase
block|{
DECL|field|other
specifier|private
specifier|final
name|FieldsProducer
name|other
decl_stmt|;
DECL|method|WrappingFielsdProducer
name|WrappingFielsdProducer
parameter_list|(
name|SegmentInfo
name|si
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfo
parameter_list|,
name|FieldsProducer
name|other
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|si
argument_list|,
name|dir
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|other
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadTermsIndex
specifier|public
name|void
name|loadTermsIndex
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|other
operator|.
name|loadTermsIndex
argument_list|(
name|indexDivisor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|WrappingFieldsEnum
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|,
name|docValues
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|other
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
DECL|class|NameValue
specifier|static
specifier|abstract
class|class
name|NameValue
parameter_list|<
name|V
parameter_list|>
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|value
name|V
name|value
decl_stmt|;
DECL|method|smaller
name|NameValue
argument_list|<
name|?
argument_list|>
name|smaller
parameter_list|(
name|NameValue
argument_list|<
name|?
argument_list|>
name|other
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|other
operator|.
name|name
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|name
operator|==
literal|null
condition|)
block|{
return|return
name|other
return|;
block|}
specifier|final
name|int
name|res
init|=
name|this
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|<
literal|0
condition|)
return|return
name|this
return|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
name|other
operator|.
name|name
operator|=
name|this
operator|.
name|name
expr_stmt|;
return|return
name|other
return|;
block|}
DECL|method|next
specifier|abstract
name|NameValue
argument_list|<
name|V
argument_list|>
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|FieldsEnumNameValue
specifier|static
class|class
name|FieldsEnumNameValue
extends|extends
name|NameValue
argument_list|<
name|FieldsEnum
argument_list|>
block|{
annotation|@
name|Override
DECL|method|next
name|NameValue
argument_list|<
name|FieldsEnum
argument_list|>
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|name
operator|=
name|value
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|class|DocValueNameValue
specifier|static
class|class
name|DocValueNameValue
extends|extends
name|NameValue
argument_list|<
name|DocValues
argument_list|>
block|{
DECL|field|iter
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|>
name|iter
decl_stmt|;
annotation|@
name|Override
DECL|method|next
name|NameValue
argument_list|<
name|DocValues
argument_list|>
name|next
parameter_list|()
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|value
operator|=
name|next
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|name
operator|=
name|next
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
block|}
DECL|class|WrappingFieldsEnum
specifier|static
class|class
name|WrappingFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|docValues
specifier|private
specifier|final
name|DocValueNameValue
name|docValues
init|=
operator|new
name|DocValueNameValue
argument_list|()
decl_stmt|;
DECL|field|fieldsEnum
specifier|private
specifier|final
name|NameValue
argument_list|<
name|FieldsEnum
argument_list|>
name|fieldsEnum
init|=
operator|new
name|FieldsEnumNameValue
argument_list|()
decl_stmt|;
DECL|field|coordinator
specifier|private
name|NameValue
argument_list|<
name|?
argument_list|>
name|coordinator
decl_stmt|;
annotation|@
name|Override
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
return|return
name|fieldsEnum
operator|.
name|value
operator|.
name|attributes
argument_list|()
return|;
block|}
DECL|method|WrappingFieldsEnum
specifier|public
name|WrappingFieldsEnum
parameter_list|(
name|FieldsEnum
name|wrapped
parameter_list|,
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|>
name|docValues
parameter_list|)
block|{
name|this
operator|.
name|docValues
operator|.
name|iter
operator|=
name|docValues
expr_stmt|;
name|this
operator|.
name|fieldsEnum
operator|.
name|value
operator|=
name|wrapped
expr_stmt|;
name|coordinator
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|docValues
operator|.
name|name
operator|==
name|coordinator
operator|.
name|name
condition|)
return|return
name|docValues
operator|.
name|value
return|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|coordinator
operator|==
literal|null
condition|)
block|{
name|coordinator
operator|=
name|fieldsEnum
operator|.
name|next
argument_list|()
operator|.
name|smaller
argument_list|(
name|docValues
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// old = coordinator.name;
block|}
else|else
block|{
name|String
name|current
init|=
name|coordinator
operator|.
name|name
decl_stmt|;
if|if
condition|(
name|current
operator|==
name|docValues
operator|.
name|name
condition|)
block|{
name|docValues
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|current
operator|==
name|fieldsEnum
operator|.
name|name
condition|)
block|{
name|fieldsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|coordinator
operator|=
name|docValues
operator|.
name|smaller
argument_list|(
name|fieldsEnum
argument_list|)
expr_stmt|;
block|}
return|return
name|coordinator
operator|==
literal|null
condition|?
literal|null
else|:
name|coordinator
operator|.
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|TermsEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldsEnum
operator|.
name|name
operator|==
name|coordinator
operator|.
name|name
condition|)
return|return
name|fieldsEnum
operator|.
name|value
operator|.
name|terms
argument_list|()
return|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
