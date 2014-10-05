begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|Iterator
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
name|index
operator|.
name|AssertingLeafReader
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
name|Fields
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
name|util
operator|.
name|Accountable
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
name|BytesRefBuilder
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Just like the default postings format but with additional asserts.  */
end_comment
begin_class
DECL|class|AssertingPostingsFormat
specifier|public
specifier|final
class|class
name|AssertingPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|PostingsFormat
name|in
init|=
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
decl_stmt|;
DECL|method|AssertingPostingsFormat
specifier|public
name|AssertingPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Asserting"
argument_list|)
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
return|return
operator|new
name|AssertingFieldsConsumer
argument_list|(
name|state
argument_list|,
name|in
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
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
return|return
operator|new
name|AssertingFieldsProducer
argument_list|(
name|in
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AssertingFieldsProducer
specifier|static
class|class
name|AssertingFieldsProducer
extends|extends
name|FieldsProducer
block|{
DECL|field|in
specifier|private
specifier|final
name|FieldsProducer
name|in
decl_stmt|;
DECL|method|AssertingFieldsProducer
name|AssertingFieldsProducer
parameter_list|(
name|FieldsProducer
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
comment|// do a few simple checks on init
assert|assert
name|toString
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|ramBytesUsed
argument_list|()
operator|>=
literal|0
assert|;
assert|assert
name|getChildResources
argument_list|()
operator|!=
literal|null
assert|;
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|in
operator|.
name|iterator
argument_list|()
decl_stmt|;
assert|assert
name|iterator
operator|!=
literal|null
assert|;
return|return
name|iterator
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
name|Terms
name|terms
init|=
name|in
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|terms
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|AssertingLeafReader
operator|.
name|AssertingTerms
argument_list|(
name|terms
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|in
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|v
init|=
name|in
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
assert|assert
name|v
operator|>=
literal|0
assert|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|res
init|=
name|in
operator|.
name|getChildResources
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|checkIterator
argument_list|(
name|res
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergeInstance
specifier|public
name|FieldsProducer
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingFieldsProducer
argument_list|(
name|in
operator|.
name|getMergeInstance
argument_list|()
argument_list|)
return|;
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
DECL|class|AssertingFieldsConsumer
specifier|static
class|class
name|AssertingFieldsConsumer
extends|extends
name|FieldsConsumer
block|{
DECL|field|in
specifier|private
specifier|final
name|FieldsConsumer
name|in
decl_stmt|;
DECL|field|writeState
specifier|private
specifier|final
name|SegmentWriteState
name|writeState
decl_stmt|;
DECL|method|AssertingFieldsConsumer
name|AssertingFieldsConsumer
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|,
name|FieldsConsumer
name|in
parameter_list|)
block|{
name|this
operator|.
name|writeState
operator|=
name|writeState
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|write
argument_list|(
name|fields
argument_list|)
expr_stmt|;
comment|// TODO: more asserts?  can we somehow run a
comment|// "limited" CheckIndex here???  Or ... can we improve
comment|// AssertingFieldsProducer and us it also to wrap the
comment|// incoming Fields here?
name|String
name|lastField
init|=
literal|null
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|FieldInfo
name|fieldInfo
init|=
name|writeState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|fieldInfo
operator|!=
literal|null
assert|;
assert|assert
name|lastField
operator|==
literal|null
operator|||
name|lastField
operator|.
name|compareTo
argument_list|(
name|field
argument_list|)
operator|<
literal|0
assert|;
name|lastField
operator|=
name|field
expr_stmt|;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
assert|assert
name|terms
operator|!=
literal|null
assert|;
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
name|BytesRefBuilder
name|lastTerm
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
name|DocsAndPositionsEnum
name|posEnum
init|=
literal|null
decl_stmt|;
name|boolean
name|hasFreqs
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|hasPositions
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|hasOffsets
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|hasPayloads
init|=
name|terms
operator|.
name|hasPayloads
argument_list|()
decl_stmt|;
assert|assert
name|hasPositions
operator|==
name|terms
operator|.
name|hasPositions
argument_list|()
assert|;
assert|assert
name|hasOffsets
operator|==
name|terms
operator|.
name|hasOffsets
argument_list|()
assert|;
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
assert|assert
name|lastTerm
operator|==
literal|null
operator|||
name|lastTerm
operator|.
name|get
argument_list|()
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|<
literal|0
assert|;
if|if
condition|(
name|lastTerm
operator|==
literal|null
condition|)
block|{
name|lastTerm
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
name|lastTerm
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lastTerm
operator|.
name|copyBytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|hasPositions
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|hasFreqs
condition|)
block|{
name|flags
operator|=
name|flags
operator||
name|DocsEnum
operator|.
name|FLAG_FREQS
expr_stmt|;
block|}
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnum
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|flags
operator||=
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
expr_stmt|;
block|}
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|flags
operator|=
name|flags
operator||
name|DocsAndPositionsEnum
operator|.
name|FLAG_OFFSETS
expr_stmt|;
block|}
name|posEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posEnum
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|docsEnum
operator|=
name|posEnum
expr_stmt|;
block|}
assert|assert
name|docsEnum
operator|!=
literal|null
operator|:
literal|"termsEnum="
operator|+
name|termsEnum
operator|+
literal|" hasPositions="
operator|+
name|hasPositions
assert|;
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|docID
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
assert|assert
name|docID
operator|>
name|lastDocID
assert|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
if|if
condition|(
name|hasFreqs
condition|)
block|{
name|int
name|freq
init|=
name|docsEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
assert|assert
name|freq
operator|>
literal|0
assert|;
if|if
condition|(
name|hasPositions
condition|)
block|{
name|int
name|lastPos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastStartOffset
init|=
operator|-
literal|1
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|int
name|pos
init|=
name|posEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
assert|assert
name|pos
operator|>=
name|lastPos
operator|:
literal|"pos="
operator|+
name|pos
operator|+
literal|" vs lastPos="
operator|+
name|lastPos
operator|+
literal|" i="
operator|+
name|i
operator|+
literal|" freq="
operator|+
name|freq
assert|;
name|lastPos
operator|=
name|pos
expr_stmt|;
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|posEnum
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|posEnum
operator|.
name|endOffset
argument_list|()
decl_stmt|;
assert|assert
name|endOffset
operator|>=
name|startOffset
assert|;
assert|assert
name|startOffset
operator|>=
name|lastStartOffset
assert|;
name|lastStartOffset
operator|=
name|startOffset
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
