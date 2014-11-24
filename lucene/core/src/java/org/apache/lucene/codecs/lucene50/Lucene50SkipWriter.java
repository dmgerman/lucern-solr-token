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
name|codecs
operator|.
name|MultiLevelSkipListWriter
import|;
end_import
begin_comment
comment|/**  * Write skip lists with multiple levels, and support skip within block ints.  *  * Assume that docFreq = 28, skipInterval = blockSize = 12  *  *  |       block#0       | |      block#1        | |vInts|  *  d d d d d d d d d d d d d d d d d d d d d d d d d d d d (posting list)  *                          ^                       ^       (level 0 skip point)  *  * Note that skipWriter will ignore first document in block#0, since   * it is useless as a skip point.  Also, we'll never skip into the vInts  * block, only record skip data at the start its start point(if it exist).  *  * For each skip point, we will record:   * 1. docID in former position, i.e. for position 12, record docID[11], etc.  * 2. its related file points(position, payload),   * 3. related numbers or uptos(position, payload).  * 4. start offset.  *  */
end_comment
begin_class
DECL|class|Lucene50SkipWriter
specifier|final
class|class
name|Lucene50SkipWriter
extends|extends
name|MultiLevelSkipListWriter
block|{
DECL|field|lastSkipDoc
specifier|private
name|int
index|[]
name|lastSkipDoc
decl_stmt|;
DECL|field|lastSkipDocPointer
specifier|private
name|long
index|[]
name|lastSkipDocPointer
decl_stmt|;
DECL|field|lastSkipPosPointer
specifier|private
name|long
index|[]
name|lastSkipPosPointer
decl_stmt|;
DECL|field|lastSkipPayPointer
specifier|private
name|long
index|[]
name|lastSkipPayPointer
decl_stmt|;
DECL|field|lastPayloadByteUpto
specifier|private
name|int
index|[]
name|lastPayloadByteUpto
decl_stmt|;
DECL|field|docOut
specifier|private
specifier|final
name|IndexOutput
name|docOut
decl_stmt|;
DECL|field|posOut
specifier|private
specifier|final
name|IndexOutput
name|posOut
decl_stmt|;
DECL|field|payOut
specifier|private
specifier|final
name|IndexOutput
name|payOut
decl_stmt|;
DECL|field|curDoc
specifier|private
name|int
name|curDoc
decl_stmt|;
DECL|field|curDocPointer
specifier|private
name|long
name|curDocPointer
decl_stmt|;
DECL|field|curPosPointer
specifier|private
name|long
name|curPosPointer
decl_stmt|;
DECL|field|curPayPointer
specifier|private
name|long
name|curPayPointer
decl_stmt|;
DECL|field|curPosBufferUpto
specifier|private
name|int
name|curPosBufferUpto
decl_stmt|;
DECL|field|curPayloadByteUpto
specifier|private
name|int
name|curPayloadByteUpto
decl_stmt|;
DECL|field|fieldHasPositions
specifier|private
name|boolean
name|fieldHasPositions
decl_stmt|;
DECL|field|fieldHasOffsets
specifier|private
name|boolean
name|fieldHasOffsets
decl_stmt|;
DECL|field|fieldHasPayloads
specifier|private
name|boolean
name|fieldHasPayloads
decl_stmt|;
DECL|method|Lucene50SkipWriter
specifier|public
name|Lucene50SkipWriter
parameter_list|(
name|int
name|maxSkipLevels
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|int
name|docCount
parameter_list|,
name|IndexOutput
name|docOut
parameter_list|,
name|IndexOutput
name|posOut
parameter_list|,
name|IndexOutput
name|payOut
parameter_list|)
block|{
name|super
argument_list|(
name|blockSize
argument_list|,
literal|8
argument_list|,
name|maxSkipLevels
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|docOut
operator|=
name|docOut
expr_stmt|;
name|this
operator|.
name|posOut
operator|=
name|posOut
expr_stmt|;
name|this
operator|.
name|payOut
operator|=
name|payOut
expr_stmt|;
name|lastSkipDoc
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|lastSkipDocPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
if|if
condition|(
name|posOut
operator|!=
literal|null
condition|)
block|{
name|lastSkipPosPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
if|if
condition|(
name|payOut
operator|!=
literal|null
condition|)
block|{
name|lastSkipPayPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
name|lastPayloadByteUpto
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|boolean
name|fieldHasPositions
parameter_list|,
name|boolean
name|fieldHasOffsets
parameter_list|,
name|boolean
name|fieldHasPayloads
parameter_list|)
block|{
name|this
operator|.
name|fieldHasPositions
operator|=
name|fieldHasPositions
expr_stmt|;
name|this
operator|.
name|fieldHasOffsets
operator|=
name|fieldHasOffsets
expr_stmt|;
name|this
operator|.
name|fieldHasPayloads
operator|=
name|fieldHasPayloads
expr_stmt|;
block|}
comment|// tricky: we only skip data for blocks (terms with more than 128 docs), but re-init'ing the skipper
comment|// is pretty slow for rare terms in large segments as we have to fill O(log #docs in segment) of junk.
comment|// this is the vast majority of terms (worst case: ID field or similar).  so in resetSkip() we save
comment|// away the previous pointers, and lazy-init only if we need to buffer skip data for the term.
DECL|field|initialized
specifier|private
name|boolean
name|initialized
decl_stmt|;
DECL|field|lastDocFP
name|long
name|lastDocFP
decl_stmt|;
DECL|field|lastPosFP
name|long
name|lastPosFP
decl_stmt|;
DECL|field|lastPayFP
name|long
name|lastPayFP
decl_stmt|;
annotation|@
name|Override
DECL|method|resetSkip
specifier|public
name|void
name|resetSkip
parameter_list|()
block|{
name|lastDocFP
operator|=
name|docOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
name|lastPosFP
operator|=
name|posOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldHasOffsets
operator|||
name|fieldHasPayloads
condition|)
block|{
name|lastPayFP
operator|=
name|payOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
block|}
name|initialized
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|initSkip
specifier|public
name|void
name|initSkip
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
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
name|lastSkipDocPointer
argument_list|,
name|lastDocFP
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipPosPointer
argument_list|,
name|lastPosFP
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastPayloadByteUpto
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
operator|||
name|fieldHasPayloads
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipPayPointer
argument_list|,
name|lastPayFP
argument_list|)
expr_stmt|;
block|}
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Sets the values for the current skip data.     */
DECL|method|bufferSkip
specifier|public
name|void
name|bufferSkip
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|long
name|posFP
parameter_list|,
name|long
name|payFP
parameter_list|,
name|int
name|posBufferUpto
parameter_list|,
name|int
name|payloadByteUpto
parameter_list|)
throws|throws
name|IOException
block|{
name|initSkip
argument_list|()
expr_stmt|;
name|this
operator|.
name|curDoc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|curDocPointer
operator|=
name|docOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|this
operator|.
name|curPosPointer
operator|=
name|posFP
expr_stmt|;
name|this
operator|.
name|curPayPointer
operator|=
name|payFP
expr_stmt|;
name|this
operator|.
name|curPosBufferUpto
operator|=
name|posBufferUpto
expr_stmt|;
name|this
operator|.
name|curPayloadByteUpto
operator|=
name|payloadByteUpto
expr_stmt|;
name|bufferSkip
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
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
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|lastSkipDoc
index|[
name|level
index|]
operator|=
name|curDoc
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|curDocPointer
operator|-
name|lastSkipDocPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipDocPointer
index|[
name|level
index|]
operator|=
name|curDocPointer
expr_stmt|;
if|if
condition|(
name|fieldHasPositions
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
name|curPosPointer
operator|-
name|lastSkipPosPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipPosPointer
index|[
name|level
index|]
operator|=
name|curPosPointer
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curPosBufferUpto
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curPayloadByteUpto
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
operator|||
name|fieldHasPayloads
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
name|curPayPointer
operator|-
name|lastSkipPayPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipPayPointer
index|[
name|level
index|]
operator|=
name|curPayPointer
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit