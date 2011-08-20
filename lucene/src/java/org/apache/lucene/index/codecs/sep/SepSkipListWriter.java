begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.sep
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
name|sep
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
name|Arrays
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
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
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
name|MultiLevelSkipListWriter
import|;
end_import
begin_comment
comment|// TODO: -- skip data should somehow be more local to the
end_comment
begin_comment
comment|// particular stream (doc, freq, pos, payload)
end_comment
begin_comment
comment|/**  * Implements the skip list writer for the default posting list format  * that stores positions and payloads.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SepSkipListWriter
class|class
name|SepSkipListWriter
extends|extends
name|MultiLevelSkipListWriter
block|{
DECL|field|lastSkipDoc
specifier|private
name|int
index|[]
name|lastSkipDoc
decl_stmt|;
DECL|field|lastSkipPayloadLength
specifier|private
name|int
index|[]
name|lastSkipPayloadLength
decl_stmt|;
DECL|field|lastSkipPayloadPointer
specifier|private
name|long
index|[]
name|lastSkipPayloadPointer
decl_stmt|;
DECL|field|docIndex
specifier|private
name|IntIndexOutput
operator|.
name|Index
index|[]
name|docIndex
decl_stmt|;
DECL|field|freqIndex
specifier|private
name|IntIndexOutput
operator|.
name|Index
index|[]
name|freqIndex
decl_stmt|;
DECL|field|posIndex
specifier|private
name|IntIndexOutput
operator|.
name|Index
index|[]
name|posIndex
decl_stmt|;
DECL|field|freqOutput
specifier|private
name|IntIndexOutput
name|freqOutput
decl_stmt|;
comment|// TODO: -- private again
DECL|field|posOutput
name|IntIndexOutput
name|posOutput
decl_stmt|;
comment|// TODO: -- private again
DECL|field|payloadOutput
name|IndexOutput
name|payloadOutput
decl_stmt|;
DECL|field|curDoc
specifier|private
name|int
name|curDoc
decl_stmt|;
DECL|field|curStorePayloads
specifier|private
name|boolean
name|curStorePayloads
decl_stmt|;
DECL|field|curPayloadLength
specifier|private
name|int
name|curPayloadLength
decl_stmt|;
DECL|field|curPayloadPointer
specifier|private
name|long
name|curPayloadPointer
decl_stmt|;
DECL|method|SepSkipListWriter
name|SepSkipListWriter
parameter_list|(
name|int
name|skipInterval
parameter_list|,
name|int
name|numberOfSkipLevels
parameter_list|,
name|int
name|docCount
parameter_list|,
name|IntIndexOutput
name|freqOutput
parameter_list|,
name|IntIndexOutput
name|docOutput
parameter_list|,
name|IntIndexOutput
name|posOutput
parameter_list|,
name|IndexOutput
name|payloadOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|skipInterval
argument_list|,
name|numberOfSkipLevels
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|freqOutput
operator|=
name|freqOutput
expr_stmt|;
name|this
operator|.
name|posOutput
operator|=
name|posOutput
expr_stmt|;
name|this
operator|.
name|payloadOutput
operator|=
name|payloadOutput
expr_stmt|;
name|lastSkipDoc
operator|=
operator|new
name|int
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
name|lastSkipPayloadLength
operator|=
operator|new
name|int
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
comment|// TODO: -- also cutover normal IndexOutput to use getIndex()?
name|lastSkipPayloadPointer
operator|=
operator|new
name|long
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
name|freqIndex
operator|=
operator|new
name|IntIndexOutput
operator|.
name|Index
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
name|docIndex
operator|=
operator|new
name|IntIndexOutput
operator|.
name|Index
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
name|posIndex
operator|=
operator|new
name|IntIndexOutput
operator|.
name|Index
index|[
name|numberOfSkipLevels
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfSkipLevels
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|freqOutput
operator|!=
literal|null
condition|)
block|{
name|freqIndex
index|[
name|i
index|]
operator|=
name|freqOutput
operator|.
name|index
argument_list|()
expr_stmt|;
block|}
name|docIndex
index|[
name|i
index|]
operator|=
name|docOutput
operator|.
name|index
argument_list|()
expr_stmt|;
if|if
condition|(
name|posOutput
operator|!=
literal|null
condition|)
block|{
name|posIndex
index|[
name|i
index|]
operator|=
name|posOutput
operator|.
name|index
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|indexOptions
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|method|setIndexOptions
name|void
name|setIndexOptions
parameter_list|(
name|IndexOptions
name|v
parameter_list|)
block|{
name|indexOptions
operator|=
name|v
expr_stmt|;
block|}
DECL|method|setPosOutput
name|void
name|setPosOutput
parameter_list|(
name|IntIndexOutput
name|posOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|posOutput
operator|=
name|posOutput
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfSkipLevels
condition|;
name|i
operator|++
control|)
block|{
name|posIndex
index|[
name|i
index|]
operator|=
name|posOutput
operator|.
name|index
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setPayloadOutput
name|void
name|setPayloadOutput
parameter_list|(
name|IndexOutput
name|payloadOutput
parameter_list|)
block|{
name|this
operator|.
name|payloadOutput
operator|=
name|payloadOutput
expr_stmt|;
block|}
comment|/**    * Sets the values for the current skip data.     */
comment|// Called @ every index interval (every 128th (by default)
comment|// doc)
DECL|method|setSkipData
name|void
name|setSkipData
parameter_list|(
name|int
name|doc
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|int
name|payloadLength
parameter_list|)
block|{
name|this
operator|.
name|curDoc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|curStorePayloads
operator|=
name|storePayloads
expr_stmt|;
name|this
operator|.
name|curPayloadLength
operator|=
name|payloadLength
expr_stmt|;
if|if
condition|(
name|payloadOutput
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|curPayloadPointer
operator|=
name|payloadOutput
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Called @ start of new term
DECL|method|resetSkip
specifier|protected
name|void
name|resetSkip
parameter_list|(
name|IntIndexOutput
operator|.
name|Index
name|topDocIndex
parameter_list|,
name|IntIndexOutput
operator|.
name|Index
name|topFreqIndex
parameter_list|,
name|IntIndexOutput
operator|.
name|Index
name|topPosIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipDoc
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipPayloadLength
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// we don't have to write the first length in the skip list
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfSkipLevels
condition|;
name|i
operator|++
control|)
block|{
name|docIndex
index|[
name|i
index|]
operator|.
name|copyFrom
argument_list|(
name|topDocIndex
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|freqOutput
operator|!=
literal|null
condition|)
block|{
name|freqIndex
index|[
name|i
index|]
operator|.
name|copyFrom
argument_list|(
name|topFreqIndex
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|posOutput
operator|!=
literal|null
condition|)
block|{
name|posIndex
index|[
name|i
index|]
operator|.
name|copyFrom
argument_list|(
name|topPosIndex
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|payloadOutput
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipPayloadPointer
argument_list|,
name|payloadOutput
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeSkipData
specifier|protected
name|void
name|writeSkipData
parameter_list|(
name|int
name|level
parameter_list|,
name|IndexOutput
name|skipBuffer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// To efficiently store payloads in the posting lists we do not store the length of
comment|// every payload. Instead we omit the length for a payload if the previous payload had
comment|// the same length.
comment|// However, in order to support skipping the payload length at every skip point must be known.
comment|// So we use the same length encoding that we use for the posting lists for the skip data as well:
comment|// Case 1: current field does not store payloads
comment|//           SkipDatum                 --> DocSkip, FreqSkip, ProxSkip
comment|//           DocSkip,FreqSkip,ProxSkip --> VInt
comment|//           DocSkip records the document number before every SkipInterval th  document in TermFreqs.
comment|//           Document numbers are represented as differences from the previous value in the sequence.
comment|// Case 2: current field stores payloads
comment|//           SkipDatum                 --> DocSkip, PayloadLength?, FreqSkip,ProxSkip
comment|//           DocSkip,FreqSkip,ProxSkip --> VInt
comment|//           PayloadLength             --> VInt
comment|//         In this case DocSkip/2 is the difference between
comment|//         the current and the previous value. If DocSkip
comment|//         is odd, then a PayloadLength encoded as VInt follows,
comment|//         if DocSkip is even, then it is assumed that the
comment|//         current payload length equals the length at the previous
comment|//         skip point
assert|assert
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
operator|||
operator|!
name|curStorePayloads
assert|;
if|if
condition|(
name|curStorePayloads
condition|)
block|{
name|int
name|delta
init|=
name|curDoc
operator|-
name|lastSkipDoc
index|[
name|level
index|]
decl_stmt|;
if|if
condition|(
name|curPayloadLength
operator|==
name|lastSkipPayloadLength
index|[
name|level
index|]
condition|)
block|{
comment|// the current payload length equals the length at the previous skip point,
comment|// so we don't store the length again
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the payload length is different from the previous one. We shift the DocSkip,
comment|// set the lowest bit and store the current payload length as VInt.
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curPayloadLength
argument_list|)
expr_stmt|;
name|lastSkipPayloadLength
index|[
name|level
index|]
operator|=
name|curPayloadLength
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// current field does not store payloads
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curDoc
operator|-
name|lastSkipDoc
index|[
name|level
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|freqIndex
index|[
name|level
index|]
operator|.
name|mark
argument_list|()
expr_stmt|;
name|freqIndex
index|[
name|level
index|]
operator|.
name|write
argument_list|(
name|skipBuffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|docIndex
index|[
name|level
index|]
operator|.
name|mark
argument_list|()
expr_stmt|;
name|docIndex
index|[
name|level
index|]
operator|.
name|write
argument_list|(
name|skipBuffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
name|posIndex
index|[
name|level
index|]
operator|.
name|mark
argument_list|()
expr_stmt|;
name|posIndex
index|[
name|level
index|]
operator|.
name|write
argument_list|(
name|skipBuffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|curStorePayloads
condition|)
block|{
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|curPayloadPointer
operator|-
name|lastSkipPayloadPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|lastSkipDoc
index|[
name|level
index|]
operator|=
name|curDoc
expr_stmt|;
name|lastSkipPayloadPointer
index|[
name|level
index|]
operator|=
name|curPayloadPointer
expr_stmt|;
block|}
block|}
end_class
end_unit
