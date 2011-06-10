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
name|Arrays
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
name|CodecProvider
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
begin_comment
comment|/**  * SegmentCodecs maintains an ordered list of distinct codecs used within a  * segment. Within a segment on codec is used to write multiple fields while  * each field could be written by a different codec. To enable codecs per field  * within a single segment we need to record the distinct codecs and map them to  * each field present in the segment. SegmentCodecs is created together with  * {@link SegmentWriteState} for each flush and is maintained in the  * corresponding {@link SegmentInfo} until it is committed.  *<p>  * During indexing {@link FieldInfos} uses {@link SegmentCodecsBuilder} to incrementally  * build the {@link SegmentCodecs} mapping. Once a segment is flushed  * DocumentsWriter creates a {@link SegmentCodecs} instance from  * {@link FieldInfos#buildSegmentCodecs(boolean)} The {@link FieldInfo#codecId}  * assigned by {@link SegmentCodecsBuilder} refers to the codecs ordinal  * maintained inside {@link SegmentCodecs}. This ord is later used to get the  * right codec when the segment is opened in a reader.The {@link Codec} returned  * from {@link SegmentCodecs#codec()} in turn uses {@link SegmentCodecs}  * internal structure to select and initialize the right codec for a fields when  * it is written.  *<p>  * Once a flush succeeded the {@link SegmentCodecs} is maintained inside the  * {@link SegmentInfo} for the flushed segment it was created for.  * {@link SegmentInfo} writes the name of each codec in {@link SegmentCodecs}  * for each segment and maintains the order. Later if a segment is opened by a  * reader this mapping is deserialized and used to create the codec per field.  *   *   * @lucene.internal  */
end_comment
begin_class
DECL|class|SegmentCodecs
specifier|final
class|class
name|SegmentCodecs
implements|implements
name|Cloneable
block|{
comment|/**    * internal structure to map codecs to fields - don't modify this from outside    * of this class!    */
DECL|field|codecs
specifier|final
name|Codec
index|[]
name|codecs
decl_stmt|;
DECL|field|provider
specifier|final
name|CodecProvider
name|provider
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
init|=
operator|new
name|PerFieldCodecWrapper
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|method|SegmentCodecs
name|SegmentCodecs
parameter_list|(
name|CodecProvider
name|provider
parameter_list|,
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|provider
argument_list|,
name|read
argument_list|(
name|input
argument_list|,
name|provider
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|SegmentCodecs
name|SegmentCodecs
parameter_list|(
name|CodecProvider
name|provider
parameter_list|,
name|Codec
modifier|...
name|codecs
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|this
operator|.
name|codecs
operator|=
name|codecs
expr_stmt|;
block|}
DECL|method|codec
name|Codec
name|codec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
DECL|method|write
name|void
name|write
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|codecs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Codec
name|codec
range|:
name|codecs
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|codec
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|private
specifier|static
name|Codec
index|[]
name|read
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|CodecProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|Codec
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Codec
argument_list|>
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|codecName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|Codec
name|lookup
init|=
name|provider
operator|.
name|lookup
argument_list|(
name|codecName
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|lookup
argument_list|)
expr_stmt|;
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
name|Codec
operator|.
name|EMPTY
argument_list|)
return|;
block|}
DECL|method|files
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
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
specifier|final
name|Codec
index|[]
name|codecArray
init|=
name|codecs
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
name|codecArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|codecArray
index|[
name|i
index|]
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|i
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SegmentCodecs [codecs="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|codecs
argument_list|)
operator|+
literal|", provider="
operator|+
name|provider
operator|+
literal|"]"
return|;
block|}
comment|/**    * Used in {@link FieldInfos} to incrementally build the codec ID mapping for    * {@link FieldInfo} instances.    *<p>    * Note: this class is not thread-safe    *</p>    * @see FieldInfo#getCodecId()    */
DECL|class|SegmentCodecsBuilder
specifier|final
specifier|static
class|class
name|SegmentCodecsBuilder
block|{
DECL|field|codecRegistry
specifier|private
specifier|final
name|Map
argument_list|<
name|Codec
argument_list|,
name|Integer
argument_list|>
name|codecRegistry
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Codec
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|Codec
argument_list|>
name|codecs
init|=
operator|new
name|ArrayList
argument_list|<
name|Codec
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|provider
specifier|private
specifier|final
name|CodecProvider
name|provider
decl_stmt|;
DECL|method|SegmentCodecsBuilder
specifier|private
name|SegmentCodecsBuilder
parameter_list|(
name|CodecProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
block|}
DECL|method|create
specifier|static
name|SegmentCodecsBuilder
name|create
parameter_list|(
name|CodecProvider
name|provider
parameter_list|)
block|{
return|return
operator|new
name|SegmentCodecsBuilder
argument_list|(
name|provider
argument_list|)
return|;
block|}
DECL|method|tryAddAndSet
name|SegmentCodecsBuilder
name|tryAddAndSet
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
if|if
condition|(
name|fi
operator|.
name|getCodecId
argument_list|()
operator|==
name|FieldInfo
operator|.
name|UNASSIGNED_CODEC_ID
condition|)
block|{
specifier|final
name|Codec
name|fieldCodec
init|=
name|provider
operator|.
name|lookup
argument_list|(
name|provider
operator|.
name|getFieldCodec
argument_list|(
name|fi
operator|.
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|Integer
name|ord
init|=
name|codecRegistry
operator|.
name|get
argument_list|(
name|fieldCodec
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|null
condition|)
block|{
name|ord
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|codecs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|codecRegistry
operator|.
name|put
argument_list|(
name|fieldCodec
argument_list|,
name|ord
argument_list|)
expr_stmt|;
name|codecs
operator|.
name|add
argument_list|(
name|fieldCodec
argument_list|)
expr_stmt|;
block|}
name|fi
operator|.
name|setCodecId
argument_list|(
name|ord
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|build
name|SegmentCodecs
name|build
parameter_list|()
block|{
return|return
operator|new
name|SegmentCodecs
argument_list|(
name|provider
argument_list|,
name|codecs
operator|.
name|toArray
argument_list|(
name|Codec
operator|.
name|EMPTY
argument_list|)
argument_list|)
return|;
block|}
DECL|method|clear
name|SegmentCodecsBuilder
name|clear
parameter_list|()
block|{
name|codecRegistry
operator|.
name|clear
argument_list|()
expr_stmt|;
name|codecs
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class
end_unit
