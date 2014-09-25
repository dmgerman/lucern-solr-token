begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|codecs
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
name|codecs
operator|.
name|FieldInfosFormat
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
name|FieldInfosReader
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
name|FieldInfosWriter
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
operator|.
name|DocValuesType
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
name|store
operator|.
name|DataOutput
import|;
end_import
begin_comment
comment|/**  * Lucene 5.0 Field Infos format.  *<p>  *<p>Field names are stored in the field info file, with suffix<tt>.fnm</tt>.</p>  *<p>FieldInfos (.fnm) --&gt; Header,FieldsCount,&lt;FieldName,FieldNumber,  * FieldBits,DocValuesBits,DocValuesGen,Attributes&gt;<sup>FieldsCount</sup>,Footer</p>  *<p>Data types:  *<ul>  *<li>Header --&gt; {@link CodecUtil#checkHeader CodecHeader}</li>  *<li>SegmentID --&gt; {@link DataOutput#writeString String}</li>  *<li>FieldsCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>FieldName --&gt; {@link DataOutput#writeString String}</li>  *<li>FieldBits, DocValuesBits --&gt; {@link DataOutput#writeByte Byte}</li>  *<li>FieldNumber --&gt; {@link DataOutput#writeInt VInt}</li>  *<li>Attributes --&gt; {@link DataOutput#writeStringStringMap Map&lt;String,String&gt;}</li>  *<li>DocValuesGen --&gt; {@link DataOutput#writeLong(long) Int64}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *</p>  * Field Descriptions:  *<ul>  *<li>SegmentID: {@link SegmentInfo#getId()} this file belongs to</li>  *<li>FieldsCount: the number of fields in this file.</li>  *<li>FieldName: name of the field as a UTF-8 String.</li>  *<li>FieldNumber: the field's number. Note that unlike previous versions of  *       Lucene, the fields are not numbered implicitly by their order in the  *       file, instead explicitly.</li>  *<li>FieldBits: a byte containing field options.  *<ul>  *<li>The low-order bit is one for indexed fields, and zero for non-indexed  *             fields.</li>  *<li>The second lowest-order bit is one for fields that have term vectors  *             stored, and zero for fields without term vectors.</li>  *<li>If the third lowest order-bit is set (0x4), offsets are stored into  *             the postings list in addition to positions.</li>  *<li>Fourth bit is unused.</li>  *<li>If the fifth lowest-order bit is set (0x10), norms are omitted for the  *             indexed field.</li>  *<li>If the sixth lowest-order bit is set (0x20), payloads are stored for the  *             indexed field.</li>  *<li>If the seventh lowest-order bit is set (0x40), term frequencies and  *             positions omitted for the indexed field.</li>  *<li>If the eighth lowest-order bit is set (0x80), positions are omitted for the  *             indexed field.</li>  *</ul>  *</li>  *<li>DocValuesBits: a byte containing per-document value types. The type  *        recorded as two four-bit integers, with the high-order bits representing  *<code>norms</code> options, and the low-order bits representing   *        {@code DocValues} options. Each four-bit integer can be decoded as such:  *<ul>  *<li>0: no DocValues for this field.</li>  *<li>1: NumericDocValues. ({@link DocValuesType#NUMERIC})</li>  *<li>2: BinaryDocValues. ({@code DocValuesType#BINARY})</li>  *<li>3: SortedDocValues. ({@code DocValuesType#SORTED})</li>  *</ul>  *</li>  *<li>DocValuesGen is the generation count of the field's DocValues. If this is -1,  *        there are no DocValues updates to that field. Anything above zero means there   *        are updates stored by {@link DocValuesFormat}.</li>  *<li>Attributes: a key-value map of codec-private attributes.</li>  *</ul>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene50FieldInfosFormat
specifier|public
specifier|final
class|class
name|Lucene50FieldInfosFormat
extends|extends
name|FieldInfosFormat
block|{
DECL|field|reader
specifier|private
specifier|final
name|FieldInfosReader
name|reader
init|=
operator|new
name|Lucene50FieldInfosReader
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|FieldInfosWriter
name|writer
init|=
operator|new
name|Lucene50FieldInfosWriter
argument_list|()
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Lucene50FieldInfosFormat
specifier|public
name|Lucene50FieldInfosFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getFieldInfosReader
specifier|public
name|FieldInfosReader
name|getFieldInfosReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfosWriter
specifier|public
name|FieldInfosWriter
name|getFieldInfosWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|writer
return|;
block|}
comment|/** Extension of field infos */
DECL|field|EXTENSION
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|"fnm"
decl_stmt|;
comment|// Codec header
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene50FieldInfos"
decl_stmt|;
DECL|field|FORMAT_START
specifier|static
specifier|final
name|int
name|FORMAT_START
init|=
literal|0
decl_stmt|;
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_START
decl_stmt|;
comment|// Field flags
DECL|field|IS_INDEXED
specifier|static
specifier|final
name|byte
name|IS_INDEXED
init|=
literal|0x1
decl_stmt|;
DECL|field|STORE_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_TERMVECTOR
init|=
literal|0x2
decl_stmt|;
DECL|field|STORE_OFFSETS_IN_POSTINGS
specifier|static
specifier|final
name|byte
name|STORE_OFFSETS_IN_POSTINGS
init|=
literal|0x4
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|static
specifier|final
name|byte
name|OMIT_NORMS
init|=
literal|0x10
decl_stmt|;
DECL|field|STORE_PAYLOADS
specifier|static
specifier|final
name|byte
name|STORE_PAYLOADS
init|=
literal|0x20
decl_stmt|;
DECL|field|OMIT_TERM_FREQ_AND_POSITIONS
specifier|static
specifier|final
name|byte
name|OMIT_TERM_FREQ_AND_POSITIONS
init|=
literal|0x40
decl_stmt|;
DECL|field|OMIT_POSITIONS
specifier|static
specifier|final
name|byte
name|OMIT_POSITIONS
init|=
operator|-
literal|128
decl_stmt|;
block|}
end_class
end_unit
