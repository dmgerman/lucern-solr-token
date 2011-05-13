begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.pulsing
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
name|pulsing
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
name|index
operator|.
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|TermState
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
name|PostingsReaderBase
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
name|BlockTermState
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
name|ByteArrayDataInput
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
name|util
operator|.
name|ArrayUtil
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
begin_comment
comment|/** Concrete class that reads the current doc/freq/skip  *  postings format   *  @lucene.experimental */
end_comment
begin_comment
comment|// TODO: -- should we switch "hasProx" higher up?  and
end_comment
begin_comment
comment|// create two separate docs readers, one that also reads
end_comment
begin_comment
comment|// prox and one that doesn't?
end_comment
begin_class
DECL|class|PulsingPostingsReaderImpl
specifier|public
class|class
name|PulsingPostingsReaderImpl
extends|extends
name|PostingsReaderBase
block|{
comment|// Fallback reader for non-pulsed terms:
DECL|field|wrappedPostingsReader
specifier|final
name|PostingsReaderBase
name|wrappedPostingsReader
decl_stmt|;
DECL|field|maxPositions
name|int
name|maxPositions
decl_stmt|;
DECL|method|PulsingPostingsReaderImpl
specifier|public
name|PulsingPostingsReaderImpl
parameter_list|(
name|PostingsReaderBase
name|wrappedPostingsReader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|wrappedPostingsReader
operator|=
name|wrappedPostingsReader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexInput
name|termsIn
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|termsIn
argument_list|,
name|PulsingPostingsWriterImpl
operator|.
name|CODEC
argument_list|,
name|PulsingPostingsWriterImpl
operator|.
name|VERSION_START
argument_list|,
name|PulsingPostingsWriterImpl
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|maxPositions
operator|=
name|termsIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|wrappedPostingsReader
operator|.
name|init
argument_list|(
name|termsIn
argument_list|)
expr_stmt|;
block|}
DECL|class|PulsingTermState
specifier|private
specifier|static
class|class
name|PulsingTermState
extends|extends
name|BlockTermState
block|{
DECL|field|postings
specifier|private
name|byte
index|[]
name|postings
decl_stmt|;
DECL|field|postingsSize
specifier|private
name|int
name|postingsSize
decl_stmt|;
comment|// -1 if this term was not inlined
DECL|field|wrappedTermState
specifier|private
name|BlockTermState
name|wrappedTermState
decl_stmt|;
DECL|field|inlinedBytesReader
name|ByteArrayDataInput
name|inlinedBytesReader
decl_stmt|;
DECL|field|inlinedBytes
specifier|private
name|byte
index|[]
name|inlinedBytes
decl_stmt|;
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|PulsingTermState
name|clone
init|=
operator|new
name|PulsingTermState
argument_list|()
decl_stmt|;
name|clone
operator|.
name|copyFrom
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
annotation|@
name|Override
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|TermState
name|_other
parameter_list|)
block|{
name|super
operator|.
name|copyFrom
argument_list|(
name|_other
argument_list|)
expr_stmt|;
name|PulsingTermState
name|other
init|=
operator|(
name|PulsingTermState
operator|)
name|_other
decl_stmt|;
name|postingsSize
operator|=
name|other
operator|.
name|postingsSize
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|postingsSize
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|postings
operator|==
literal|null
operator|||
name|postings
operator|.
name|length
operator|<
name|other
operator|.
name|postingsSize
condition|)
block|{
name|postings
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|other
operator|.
name|postingsSize
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|postings
argument_list|,
literal|0
argument_list|,
name|postings
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|postingsSize
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|wrappedTermState
operator|!=
literal|null
condition|)
block|{
name|wrappedTermState
operator|.
name|copyFrom
argument_list|(
name|other
operator|.
name|wrappedTermState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|wrappedTermState
operator|=
operator|(
name|BlockTermState
operator|)
name|other
operator|.
name|wrappedTermState
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
comment|// NOTE: we do not copy the
comment|// inlinedBytes/inlinedBytesReader; these are only
comment|// stored on the "primary" TermState.  They are
comment|// "transient" to cloned term states.
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|postingsSize
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|"PulsingTermState: not inlined: wrapped="
operator|+
name|wrappedTermState
return|;
block|}
else|else
block|{
return|return
literal|"PulsingTermState: inlined size="
operator|+
name|postingsSize
operator|+
literal|" "
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|readTermsBlock
specifier|public
name|void
name|readTermsBlock
parameter_list|(
name|IndexInput
name|termsIn
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|_termState
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PulsingTermState
name|termState
init|=
operator|(
name|PulsingTermState
operator|)
name|_termState
decl_stmt|;
if|if
condition|(
name|termState
operator|.
name|inlinedBytes
operator|==
literal|null
condition|)
block|{
name|termState
operator|.
name|inlinedBytes
operator|=
operator|new
name|byte
index|[
literal|128
index|]
expr_stmt|;
name|termState
operator|.
name|inlinedBytesReader
operator|=
operator|new
name|ByteArrayDataInput
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|int
name|len
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|termState
operator|.
name|inlinedBytes
operator|.
name|length
operator|<
name|len
condition|)
block|{
name|termState
operator|.
name|inlinedBytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|len
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|termsIn
operator|.
name|readBytes
argument_list|(
name|termState
operator|.
name|inlinedBytes
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|termState
operator|.
name|inlinedBytesReader
operator|.
name|reset
argument_list|(
name|termState
operator|.
name|inlinedBytes
argument_list|)
expr_stmt|;
name|termState
operator|.
name|wrappedTermState
operator|.
name|termCount
operator|=
literal|0
expr_stmt|;
name|wrappedPostingsReader
operator|.
name|readTermsBlock
argument_list|(
name|termsIn
argument_list|,
name|fieldInfo
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTermState
specifier|public
name|BlockTermState
name|newTermState
parameter_list|()
throws|throws
name|IOException
block|{
name|PulsingTermState
name|state
init|=
operator|new
name|PulsingTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|wrappedTermState
operator|=
name|wrappedPostingsReader
operator|.
name|newTermState
argument_list|()
expr_stmt|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|nextTerm
specifier|public
name|void
name|nextTerm
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|_termState
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("PR nextTerm");
name|PulsingTermState
name|termState
init|=
operator|(
name|PulsingTermState
operator|)
name|_termState
decl_stmt|;
comment|// total TF, but in the omitTFAP case its computed based on docFreq.
name|long
name|count
init|=
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
condition|?
name|termState
operator|.
name|docFreq
else|:
name|termState
operator|.
name|totalTermFreq
decl_stmt|;
comment|//System.out.println("  count=" + count + " threshold=" + maxPositions);
if|if
condition|(
name|count
operator|<=
name|maxPositions
condition|)
block|{
comment|//System.out.println("  inlined pos=" + termState.inlinedBytesReader.getPosition());
comment|// Inlined into terms dict -- just read the byte[] blob in,
comment|// but don't decode it now (we only decode when a DocsEnum
comment|// or D&PEnum is pulled):
name|termState
operator|.
name|postingsSize
operator|=
name|termState
operator|.
name|inlinedBytesReader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|termState
operator|.
name|postings
operator|==
literal|null
operator|||
name|termState
operator|.
name|postings
operator|.
name|length
operator|<
name|termState
operator|.
name|postingsSize
condition|)
block|{
name|termState
operator|.
name|postings
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|termState
operator|.
name|postingsSize
argument_list|,
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
comment|// TODO: sort of silly to copy from one big byte[]
comment|// (the blob holding all inlined terms' blobs for
comment|// current term block) into another byte[] (just the
comment|// blob for this term)...
name|termState
operator|.
name|inlinedBytesReader
operator|.
name|readBytes
argument_list|(
name|termState
operator|.
name|postings
argument_list|,
literal|0
argument_list|,
name|termState
operator|.
name|postingsSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("  not inlined");
name|termState
operator|.
name|postingsSize
operator|=
operator|-
literal|1
expr_stmt|;
comment|// TODO: should we do full copyFrom?  much heavier...?
name|termState
operator|.
name|wrappedTermState
operator|.
name|docFreq
operator|=
name|termState
operator|.
name|docFreq
expr_stmt|;
name|termState
operator|.
name|wrappedTermState
operator|.
name|totalTermFreq
operator|=
name|termState
operator|.
name|totalTermFreq
expr_stmt|;
name|wrappedPostingsReader
operator|.
name|nextTerm
argument_list|(
name|fieldInfo
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|)
expr_stmt|;
name|termState
operator|.
name|wrappedTermState
operator|.
name|termCount
operator|++
expr_stmt|;
block|}
block|}
comment|// TODO: we could actually reuse, by having TL that
comment|// holds the last wrapped reuse, and vice-versa
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|BlockTermState
name|_termState
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
name|PulsingTermState
name|termState
init|=
operator|(
name|PulsingTermState
operator|)
name|_termState
decl_stmt|;
if|if
condition|(
name|termState
operator|.
name|postingsSize
operator|!=
operator|-
literal|1
condition|)
block|{
name|PulsingDocsEnum
name|postings
decl_stmt|;
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsEnum
condition|)
block|{
name|postings
operator|=
operator|(
name|PulsingDocsEnum
operator|)
name|reuse
expr_stmt|;
if|if
condition|(
operator|!
name|postings
operator|.
name|canReuse
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|postings
operator|=
operator|new
name|PulsingDocsEnum
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|postings
operator|=
operator|new
name|PulsingDocsEnum
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
return|return
name|postings
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|termState
argument_list|)
return|;
block|}
else|else
block|{
comment|// TODO: not great that we lose reuse of PulsingDocsEnum in this case:
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsEnum
condition|)
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docs
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docs
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
block|}
block|}
comment|// TODO: -- not great that we can't always reuse
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|BlockTermState
name|_termState
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|field
operator|.
name|omitTermFreqAndPositions
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//System.out.println("D&P: field=" + field.name);
specifier|final
name|PulsingTermState
name|termState
init|=
operator|(
name|PulsingTermState
operator|)
name|_termState
decl_stmt|;
if|if
condition|(
name|termState
operator|.
name|postingsSize
operator|!=
operator|-
literal|1
condition|)
block|{
name|PulsingDocsAndPositionsEnum
name|postings
decl_stmt|;
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsAndPositionsEnum
condition|)
block|{
name|postings
operator|=
operator|(
name|PulsingDocsAndPositionsEnum
operator|)
name|reuse
expr_stmt|;
if|if
condition|(
operator|!
name|postings
operator|.
name|canReuse
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|postings
operator|=
operator|new
name|PulsingDocsAndPositionsEnum
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|postings
operator|=
operator|new
name|PulsingDocsAndPositionsEnum
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
return|return
name|postings
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|termState
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsAndPositionsEnum
condition|)
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docsAndPositions
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docsAndPositions
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|PulsingDocsEnum
specifier|private
specifier|static
class|class
name|PulsingDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|postings
specifier|private
specifier|final
name|ByteArrayDataInput
name|postings
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|omitTF
specifier|private
specifier|final
name|boolean
name|omitTF
decl_stmt|;
DECL|field|storePayloads
specifier|private
specifier|final
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|skipDocs
specifier|private
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|docID
specifier|private
name|int
name|docID
decl_stmt|;
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|field|payloadLength
specifier|private
name|int
name|payloadLength
decl_stmt|;
DECL|method|PulsingDocsEnum
specifier|public
name|PulsingDocsEnum
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|omitTF
operator|=
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
expr_stmt|;
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|storePayloads
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|PulsingDocsEnum
name|reset
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|PulsingTermState
name|termState
parameter_list|)
block|{
comment|//System.out.println("PR docsEnum termState=" + termState + " docFreq=" + termState.docFreq);
assert|assert
name|termState
operator|.
name|postingsSize
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|termState
operator|.
name|postingsSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termState
operator|.
name|postings
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|termState
operator|.
name|postingsSize
argument_list|)
expr_stmt|;
name|postings
operator|.
name|reset
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|docID
operator|=
literal|0
expr_stmt|;
name|payloadLength
operator|=
literal|0
expr_stmt|;
name|freq
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|skipDocs
operator|=
name|skipDocs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|canReuse
name|boolean
name|canReuse
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
name|omitTF
operator|==
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
operator|&&
name|storePayloads
operator|==
name|fieldInfo
operator|.
name|storePayloads
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("PR nextDoc this= "+ this);
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|postings
operator|.
name|eof
argument_list|()
condition|)
block|{
comment|//System.out.println("PR   END");
return|return
name|docID
operator|=
name|NO_MORE_DOCS
return|;
block|}
specifier|final
name|int
name|code
init|=
name|postings
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|omitTF
condition|)
block|{
name|docID
operator|+=
name|code
expr_stmt|;
block|}
else|else
block|{
name|docID
operator|+=
name|code
operator|>>>
literal|1
expr_stmt|;
comment|// shift off low bit
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// if low bit is set
name|freq
operator|=
literal|1
expr_stmt|;
comment|// freq is one
block|}
else|else
block|{
name|freq
operator|=
name|postings
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// else read freq
block|}
comment|// Skip positions
if|if
condition|(
name|storePayloads
condition|)
block|{
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|freq
condition|;
name|pos
operator|++
control|)
block|{
specifier|final
name|int
name|posCode
init|=
name|postings
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|posCode
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
name|payloadLength
operator|=
name|postings
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|!=
literal|0
condition|)
block|{
name|postings
operator|.
name|skipBytes
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|freq
condition|;
name|pos
operator|++
control|)
block|{
comment|// TODO: skipVInt
name|postings
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|skipDocs
operator|==
literal|null
operator|||
operator|!
name|skipDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
comment|//System.out.println("  return docID=" + docID + " freq=" + freq);
return|return
name|docID
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|advance
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
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|doc
operator|>=
name|target
condition|)
return|return
name|doc
return|;
block|}
return|return
name|docID
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
DECL|class|PulsingDocsAndPositionsEnum
specifier|private
specifier|static
class|class
name|PulsingDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|postings
specifier|private
specifier|final
name|ByteArrayDataInput
name|postings
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|storePayloads
specifier|private
specifier|final
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|skipDocs
specifier|private
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|docID
specifier|private
name|int
name|docID
decl_stmt|;
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|field|posPending
specifier|private
name|int
name|posPending
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
decl_stmt|;
DECL|field|payloadLength
specifier|private
name|int
name|payloadLength
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
DECL|field|payloadRetrieved
specifier|private
name|boolean
name|payloadRetrieved
decl_stmt|;
DECL|method|PulsingDocsAndPositionsEnum
specifier|public
name|PulsingDocsAndPositionsEnum
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|storePayloads
expr_stmt|;
block|}
DECL|method|canReuse
name|boolean
name|canReuse
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
name|storePayloads
operator|==
name|fieldInfo
operator|.
name|storePayloads
return|;
block|}
DECL|method|reset
specifier|public
name|PulsingDocsAndPositionsEnum
name|reset
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|PulsingTermState
name|termState
parameter_list|)
block|{
assert|assert
name|termState
operator|.
name|postingsSize
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|termState
operator|.
name|postingsSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termState
operator|.
name|postings
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|termState
operator|.
name|postingsSize
argument_list|)
expr_stmt|;
name|postings
operator|.
name|reset
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|skipDocs
operator|=
name|skipDocs
expr_stmt|;
name|payloadLength
operator|=
literal|0
expr_stmt|;
name|posPending
operator|=
literal|0
expr_stmt|;
name|docID
operator|=
literal|0
expr_stmt|;
comment|//System.out.println("PR d&p reset storesPayloads=" + storePayloads + " bytes=" + bytes.length + " this=" + this);
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("PR.nextDoc this=" + this);
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("  cycle skip posPending=" + posPending);
name|skipPositions
argument_list|()
expr_stmt|;
if|if
condition|(
name|postings
operator|.
name|eof
argument_list|()
condition|)
block|{
comment|//System.out.println("  END");
return|return
name|docID
operator|=
name|NO_MORE_DOCS
return|;
block|}
comment|//System.out.println("  read doc code");
specifier|final
name|int
name|code
init|=
name|postings
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|docID
operator|+=
name|code
operator|>>>
literal|1
expr_stmt|;
comment|// shift off low bit
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// if low bit is set
name|freq
operator|=
literal|1
expr_stmt|;
comment|// freq is one
block|}
else|else
block|{
comment|//System.out.println("  read freq");
name|freq
operator|=
name|postings
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// else read freq
block|}
name|posPending
operator|=
name|freq
expr_stmt|;
if|if
condition|(
name|skipDocs
operator|==
literal|null
operator|||
operator|!
name|skipDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
comment|//System.out.println("  return docID=" + docID + " freq=" + freq);
name|position
operator|=
literal|0
expr_stmt|;
return|return
name|docID
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|advance
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
comment|//System.out.println("PR.advance target=" + target);
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
comment|//System.out.println("  nextDoc got doc=" + doc);
if|if
condition|(
name|doc
operator|>=
name|target
condition|)
block|{
return|return
name|docID
operator|=
name|doc
return|;
block|}
block|}
return|return
name|docID
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("PR.nextPosition posPending=" + posPending + " vs freq=" + freq);
assert|assert
name|posPending
operator|>
literal|0
assert|;
name|posPending
operator|--
expr_stmt|;
if|if
condition|(
name|storePayloads
condition|)
block|{
if|if
condition|(
operator|!
name|payloadRetrieved
condition|)
block|{
comment|//System.out.println("PR     skip payload=" + payloadLength);
name|postings
operator|.
name|skipBytes
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("  read pos code");
specifier|final
name|int
name|code
init|=
name|postings
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//System.out.println("PR     code=" + code);
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
name|payloadLength
operator|=
name|postings
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|//System.out.println("PR     new payload len=" + payloadLength);
block|}
name|position
operator|+=
name|code
operator|>>
literal|1
expr_stmt|;
name|payloadRetrieved
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|position
operator|+=
name|postings
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("  return pos=" + position + " hasPayload=" + !payloadRetrieved + " posPending=" + posPending + " this=" + this);
return|return
name|position
return|;
block|}
DECL|method|skipPositions
specifier|private
name|void
name|skipPositions
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("PR.skipPositions: posPending=" + posPending);
while|while
condition|(
name|posPending
operator|!=
literal|0
condition|)
block|{
name|nextPosition
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|storePayloads
operator|&&
operator|!
name|payloadRetrieved
condition|)
block|{
comment|//System.out.println("  skip last payload len=" + payloadLength);
name|postings
operator|.
name|skipBytes
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
name|payloadRetrieved
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hasPayload
specifier|public
name|boolean
name|hasPayload
parameter_list|()
block|{
return|return
name|storePayloads
operator|&&
operator|!
name|payloadRetrieved
operator|&&
name|payloadLength
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("PR  getPayload payloadLength=" + payloadLength + " this=" + this);
if|if
condition|(
name|payloadRetrieved
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Either no payload exists at this term position or an attempt was made to load it more than once."
argument_list|)
throw|;
block|}
name|payloadRetrieved
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
operator|new
name|BytesRef
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|.
name|grow
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
name|postings
operator|.
name|readBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
name|payload
operator|.
name|length
operator|=
name|payloadLength
expr_stmt|;
return|return
name|payload
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
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
name|wrappedPostingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
