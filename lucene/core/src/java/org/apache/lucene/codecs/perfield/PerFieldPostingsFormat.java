begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.perfield
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|perfield
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
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|TreeMap
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
name|InvertedFieldsConsumer
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
name|InvertedFieldsProducer
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
name|PostingsFormat
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
name|IndexFileNames
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
name|store
operator|.
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|CodecUtil
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Enables per field format support.  *<p>  * Note, when extending this class, the name ({@link #getName}) is   * written into the index. In order for the field to be read, the  * name must resolve to your implementation via {@link #forName(String)}.  * This method uses Java's   * {@link ServiceLoader Service Provider Interface} to resolve format names.  *<p>  * @see ServiceLoader  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PerFieldPostingsFormat
specifier|public
specifier|abstract
class|class
name|PerFieldPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|PER_FIELD_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|PER_FIELD_EXTENSION
init|=
literal|"per"
decl_stmt|;
DECL|field|PER_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PER_FIELD_NAME
init|=
literal|"PerField40"
decl_stmt|;
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_LATEST
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_LATEST
init|=
name|VERSION_START
decl_stmt|;
DECL|method|PerFieldPostingsFormat
specifier|public
name|PerFieldPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
name|PER_FIELD_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|InvertedFieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsWriter
argument_list|(
name|state
argument_list|)
return|;
block|}
comment|// NOTE: not private to avoid $accessN at runtime!!
DECL|class|FieldsConsumerAndID
specifier|static
class|class
name|FieldsConsumerAndID
implements|implements
name|Closeable
block|{
DECL|field|fieldsConsumer
specifier|final
name|InvertedFieldsConsumer
name|fieldsConsumer
decl_stmt|;
DECL|field|segmentSuffix
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
DECL|method|FieldsConsumerAndID
specifier|public
name|FieldsConsumerAndID
parameter_list|(
name|InvertedFieldsConsumer
name|fieldsConsumer
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
name|this
operator|.
name|fieldsConsumer
operator|=
name|fieldsConsumer
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
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
name|fieldsConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|class|FieldsWriter
specifier|private
class|class
name|FieldsWriter
extends|extends
name|InvertedFieldsConsumer
block|{
DECL|field|formats
specifier|private
specifier|final
name|Map
argument_list|<
name|PostingsFormat
argument_list|,
name|FieldsConsumerAndID
argument_list|>
name|formats
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|PostingsFormat
argument_list|,
name|FieldsConsumerAndID
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Records all fields we wrote. */
DECL|field|fieldToFormat
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PostingsFormat
argument_list|>
name|fieldToFormat
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PostingsFormat
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|segmentWriteState
specifier|private
specifier|final
name|SegmentWriteState
name|segmentWriteState
decl_stmt|;
DECL|method|FieldsWriter
specifier|public
name|FieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|segmentWriteState
operator|=
name|state
expr_stmt|;
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
specifier|final
name|PostingsFormat
name|format
init|=
name|getPostingsFormatForField
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|format
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid null PostingsFormat for field=\""
operator|+
name|field
operator|.
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
assert|assert
operator|!
name|fieldToFormat
operator|.
name|containsKey
argument_list|(
name|field
operator|.
name|name
argument_list|)
assert|;
name|fieldToFormat
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|format
argument_list|)
expr_stmt|;
name|FieldsConsumerAndID
name|consumerAndId
init|=
name|formats
operator|.
name|get
argument_list|(
name|format
argument_list|)
decl_stmt|;
if|if
condition|(
name|consumerAndId
operator|==
literal|null
condition|)
block|{
comment|// First time we are seeing this format; assign
comment|// next id and init it:
specifier|final
name|String
name|segmentSuffix
init|=
name|getFullSegmentSuffix
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|segmentWriteState
operator|.
name|segmentSuffix
argument_list|,
literal|""
operator|+
name|formats
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|consumerAndId
operator|=
operator|new
name|FieldsConsumerAndID
argument_list|(
name|format
operator|.
name|fieldsConsumer
argument_list|(
operator|new
name|SegmentWriteState
argument_list|(
name|segmentWriteState
argument_list|,
name|segmentSuffix
argument_list|)
argument_list|)
argument_list|,
name|segmentSuffix
argument_list|)
expr_stmt|;
name|formats
operator|.
name|put
argument_list|(
name|format
argument_list|,
name|consumerAndId
argument_list|)
expr_stmt|;
block|}
return|return
name|consumerAndId
operator|.
name|fieldsConsumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
return|;
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
comment|// Close all subs
name|IOUtils
operator|.
name|close
argument_list|(
name|formats
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
comment|// Write _X.per: maps field name -> format name and
comment|// format name -> format id
specifier|final
name|String
name|mapFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentWriteState
operator|.
name|segmentName
argument_list|,
name|segmentWriteState
operator|.
name|segmentSuffix
argument_list|,
name|PER_FIELD_EXTENSION
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|out
init|=
name|segmentWriteState
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|mapFileName
argument_list|,
name|segmentWriteState
operator|.
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|PER_FIELD_NAME
argument_list|,
name|VERSION_LATEST
argument_list|)
expr_stmt|;
comment|// format name -> int id
name|out
operator|.
name|writeVInt
argument_list|(
name|formats
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PostingsFormat
argument_list|,
name|FieldsConsumerAndID
argument_list|>
name|ent
range|:
name|formats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// field name -> format name
name|out
operator|.
name|writeVInt
argument_list|(
name|fieldToFormat
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PostingsFormat
argument_list|>
name|ent
range|:
name|fieldToFormat
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getFullSegmentSuffix
specifier|static
name|String
name|getFullSegmentSuffix
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|outerSegmentSuffix
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
if|if
condition|(
name|outerSegmentSuffix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|segmentSuffix
return|;
block|}
else|else
block|{
comment|// TODO: support embedding; I think it should work but
comment|// we need a test confirm to confirm
comment|// return outerSegmentSuffix + "_" + segmentSuffix;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot embed PerFieldPostingsFormat inside itself (field \""
operator|+
name|fieldName
operator|+
literal|"\" returned PerFieldPostingsFormat)"
argument_list|)
throw|;
block|}
block|}
DECL|class|FieldsReader
specifier|private
class|class
name|FieldsReader
extends|extends
name|InvertedFieldsProducer
block|{
DECL|field|fields
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|InvertedFieldsProducer
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|InvertedFieldsProducer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|formats
specifier|private
specifier|final
name|Map
argument_list|<
name|PostingsFormat
argument_list|,
name|InvertedFieldsProducer
argument_list|>
name|formats
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|PostingsFormat
argument_list|,
name|InvertedFieldsProducer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FieldsReader
specifier|public
name|FieldsReader
parameter_list|(
specifier|final
name|SegmentReadState
name|readState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Read _X.per and init each format:
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|VisitPerFieldFile
argument_list|(
name|readState
operator|.
name|dir
argument_list|,
name|readState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|readState
operator|.
name|segmentSuffix
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|visitOneFormat
parameter_list|(
name|String
name|segmentSuffix
parameter_list|,
name|PostingsFormat
name|postingsFormat
parameter_list|)
throws|throws
name|IOException
block|{
name|formats
operator|.
name|put
argument_list|(
name|postingsFormat
argument_list|,
name|postingsFormat
operator|.
name|fieldsProducer
argument_list|(
operator|new
name|SegmentReadState
argument_list|(
name|readState
argument_list|,
name|segmentSuffix
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitOneField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|PostingsFormat
name|postingsFormat
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|formats
operator|.
name|containsKey
argument_list|(
name|postingsFormat
argument_list|)
assert|;
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|formats
operator|.
name|get
argument_list|(
name|postingsFormat
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|formats
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|FieldsIterator
specifier|private
specifier|final
class|class
name|FieldsIterator
extends|extends
name|FieldsEnum
block|{
DECL|field|it
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
decl_stmt|;
DECL|field|current
specifier|private
name|String
name|current
decl_stmt|;
DECL|method|FieldsIterator
specifier|public
name|FieldsIterator
parameter_list|()
block|{
name|it
operator|=
name|fields
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
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
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|current
argument_list|)
operator|.
name|terms
argument_list|(
name|current
argument_list|)
return|;
block|}
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
name|FieldsIterator
argument_list|()
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
name|InvertedFieldsProducer
name|fieldsProducer
init|=
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|fieldsProducer
operator|==
literal|null
condition|?
literal|null
else|:
name|fieldsProducer
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueFieldCount
specifier|public
name|int
name|getUniqueFieldCount
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
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
name|IOUtils
operator|.
name|close
argument_list|(
name|formats
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|InvertedFieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsReader
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|class|VisitPerFieldFile
specifier|private
specifier|abstract
class|class
name|VisitPerFieldFile
block|{
DECL|method|VisitPerFieldFile
specifier|public
name|VisitPerFieldFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|String
name|outerSegmentSuffix
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|mapFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
name|outerSegmentSuffix
argument_list|,
name|PER_FIELD_EXTENSION
argument_list|)
decl_stmt|;
specifier|final
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|mapFileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|PER_FIELD_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_LATEST
argument_list|)
expr_stmt|;
comment|// Read format name -> format id
specifier|final
name|int
name|formatCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|formatIDX
init|=
literal|0
init|;
name|formatIDX
operator|<
name|formatCount
condition|;
name|formatIDX
operator|++
control|)
block|{
specifier|final
name|String
name|segmentSuffix
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|formatName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|PostingsFormat
name|postingsFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|formatName
argument_list|)
decl_stmt|;
comment|//System.out.println("do lookup " + formatName + " -> " + postingsFormat);
if|if
condition|(
name|postingsFormat
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unable to lookup PostingsFormat for name=\""
operator|+
name|formatName
operator|+
literal|"\": got null"
argument_list|)
throw|;
block|}
comment|// Better be defined, because it was defined
comment|// during indexing:
name|visitOneFormat
argument_list|(
name|segmentSuffix
argument_list|,
name|postingsFormat
argument_list|)
expr_stmt|;
block|}
comment|// Read field name -> format name
specifier|final
name|int
name|fieldCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|fieldIDX
init|=
literal|0
init|;
name|fieldIDX
operator|<
name|fieldCount
condition|;
name|fieldIDX
operator|++
control|)
block|{
specifier|final
name|String
name|fieldName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|formatName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|visitOneField
argument_list|(
name|fieldName
argument_list|,
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|formatName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// This is called first, for all formats:
DECL|method|visitOneFormat
specifier|protected
specifier|abstract
name|void
name|visitOneFormat
parameter_list|(
name|String
name|segmentSuffix
parameter_list|,
name|PostingsFormat
name|format
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// ... then this is called, for all fields:
DECL|method|visitOneField
specifier|protected
specifier|abstract
name|void
name|visitOneField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|PostingsFormat
name|format
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
specifier|final
name|SegmentInfo
name|info
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Directory
name|dir
init|=
name|info
operator|.
name|dir
decl_stmt|;
specifier|final
name|String
name|mapFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|PER_FIELD_EXTENSION
argument_list|)
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|mapFileName
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|VisitPerFieldFile
argument_list|(
name|dir
argument_list|,
name|info
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|visitOneFormat
parameter_list|(
name|String
name|segmentSuffix
parameter_list|,
name|PostingsFormat
name|format
parameter_list|)
throws|throws
name|IOException
block|{
name|format
operator|.
name|files
argument_list|(
name|info
argument_list|,
name|segmentSuffix
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitOneField
parameter_list|(
name|String
name|field
parameter_list|,
name|PostingsFormat
name|format
parameter_list|)
block|{         }
block|}
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
comment|// TODO: this is somewhat shady... if we can't open
comment|// the .per file then most likely someone is calling
comment|// .files() after this segment was deleted, so, they
comment|// wouldn't be able to do anything with the files even
comment|// if we could return them, so we don't add any files
comment|// in this case.
block|}
block|}
comment|// NOTE: only called during writing; for reading we read
comment|// all we need from the index (ie we save the field ->
comment|// format mapping)
DECL|method|getPostingsFormatForField
specifier|public
specifier|abstract
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
block|}
end_class
end_unit
