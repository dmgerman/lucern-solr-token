begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.ramonly
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|ramonly
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashMap
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
name|SortedMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|PostingsConsumer
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
name|TermStats
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
name|IOUtils
import|;
end_import
begin_comment
comment|/** Stores all postings data in RAM, but writes a small  *  token (header + single int) to identify which "slot" the  *  index is using in RAM HashMap.  *  *  NOTE: this codec sorts terms by reverse-unicode-order! */
end_comment
begin_class
DECL|class|RAMOnlyPostingsFormat
specifier|public
specifier|final
class|class
name|RAMOnlyPostingsFormat
extends|extends
name|PostingsFormat
block|{
comment|// For fun, test that we can override how terms are
comment|// sorted, and basic things still work -- this comparator
comment|// sorts in reversed unicode code point order:
DECL|field|reverseUnicodeComparator
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|reverseUnicodeComparator
init|=
operator|new
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|t1
parameter_list|,
name|BytesRef
name|t2
parameter_list|)
block|{
name|byte
index|[]
name|b1
init|=
name|t1
operator|.
name|bytes
decl_stmt|;
name|byte
index|[]
name|b2
init|=
name|t2
operator|.
name|bytes
decl_stmt|;
name|int
name|b1Stop
decl_stmt|;
name|int
name|b1Upto
init|=
name|t1
operator|.
name|offset
decl_stmt|;
name|int
name|b2Upto
init|=
name|t2
operator|.
name|offset
decl_stmt|;
if|if
condition|(
name|t1
operator|.
name|length
operator|<
name|t2
operator|.
name|length
condition|)
block|{
name|b1Stop
operator|=
name|t1
operator|.
name|offset
operator|+
name|t1
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|b1Stop
operator|=
name|t1
operator|.
name|offset
operator|+
name|t2
operator|.
name|length
expr_stmt|;
block|}
while|while
condition|(
name|b1Upto
operator|<
name|b1Stop
condition|)
block|{
specifier|final
name|int
name|bb1
init|=
name|b1
index|[
name|b1Upto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
specifier|final
name|int
name|bb2
init|=
name|b2
index|[
name|b2Upto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
name|bb1
operator|!=
name|bb2
condition|)
block|{
comment|//System.out.println("cmp 1=" + t1 + " 2=" + t2 + " return " + (bb2-bb1));
return|return
name|bb2
operator|-
name|bb1
return|;
block|}
block|}
comment|// One is prefix of another, or they are equal
return|return
name|t2
operator|.
name|length
operator|-
name|t1
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|this
operator|==
name|other
return|;
block|}
block|}
decl_stmt|;
DECL|method|RAMOnlyPostingsFormat
specifier|public
name|RAMOnlyPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"RAMOnly"
argument_list|)
expr_stmt|;
block|}
comment|// Postings state:
DECL|class|RAMPostings
specifier|static
class|class
name|RAMPostings
extends|extends
name|FieldsProducer
block|{
DECL|field|fieldToTerms
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RAMField
argument_list|>
name|fieldToTerms
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|RAMField
argument_list|>
argument_list|()
decl_stmt|;
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
block|{
return|return
name|fieldToTerms
operator|.
name|get
argument_list|(
name|field
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
name|fieldToTerms
operator|.
name|size
argument_list|()
return|;
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
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|fieldToTerms
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|iterator
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
block|{     }
block|}
DECL|class|RAMField
specifier|static
class|class
name|RAMField
extends|extends
name|Terms
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|termToDocs
specifier|final
name|SortedMap
argument_list|<
name|String
argument_list|,
name|RAMTerm
argument_list|>
name|termToDocs
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|RAMTerm
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|sumTotalTermFreq
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
DECL|field|info
specifier|final
name|FieldInfo
name|info
decl_stmt|;
DECL|method|RAMField
name|RAMField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|termToDocs
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
return|return
name|sumTotalTermFreq
return|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|sumDocFreq
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|docCount
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
block|{
return|return
operator|new
name|RAMTermsEnum
argument_list|(
name|RAMOnlyPostingsFormat
operator|.
name|RAMField
operator|.
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|reverseUnicodeComparator
return|;
block|}
annotation|@
name|Override
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
name|info
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasPositions
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
return|return
name|info
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|info
operator|.
name|hasPayloads
argument_list|()
return|;
block|}
block|}
DECL|class|RAMTerm
specifier|static
class|class
name|RAMTerm
block|{
DECL|field|term
specifier|final
name|String
name|term
decl_stmt|;
DECL|field|totalTermFreq
name|long
name|totalTermFreq
decl_stmt|;
DECL|field|docs
specifier|final
name|List
argument_list|<
name|RAMDoc
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|RAMDoc
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|RAMTerm
specifier|public
name|RAMTerm
parameter_list|(
name|String
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
block|}
DECL|class|RAMDoc
specifier|static
class|class
name|RAMDoc
block|{
DECL|field|docID
specifier|final
name|int
name|docID
decl_stmt|;
DECL|field|positions
specifier|final
name|int
index|[]
name|positions
decl_stmt|;
DECL|field|payloads
name|byte
index|[]
index|[]
name|payloads
decl_stmt|;
DECL|method|RAMDoc
specifier|public
name|RAMDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|this
operator|.
name|docID
operator|=
name|docID
expr_stmt|;
name|positions
operator|=
operator|new
name|int
index|[
name|freq
index|]
expr_stmt|;
block|}
block|}
comment|// Classes for writing to the postings state
DECL|class|RAMFieldsConsumer
specifier|private
specifier|static
class|class
name|RAMFieldsConsumer
extends|extends
name|FieldsConsumer
block|{
DECL|field|postings
specifier|private
specifier|final
name|RAMPostings
name|postings
decl_stmt|;
DECL|field|termsConsumer
specifier|private
specifier|final
name|RAMTermsConsumer
name|termsConsumer
init|=
operator|new
name|RAMTermsConsumer
argument_list|()
decl_stmt|;
DECL|method|RAMFieldsConsumer
specifier|public
name|RAMFieldsConsumer
parameter_list|(
name|RAMPostings
name|postings
parameter_list|)
block|{
name|this
operator|.
name|postings
operator|=
name|postings
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
block|{
if|if
condition|(
name|field
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec cannot index offsets"
argument_list|)
throw|;
block|}
name|RAMField
name|ramField
init|=
operator|new
name|RAMField
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|postings
operator|.
name|fieldToTerms
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|ramField
argument_list|)
expr_stmt|;
name|termsConsumer
operator|.
name|reset
argument_list|(
name|ramField
argument_list|)
expr_stmt|;
return|return
name|termsConsumer
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// TODO: finalize stuff
block|}
block|}
DECL|class|RAMTermsConsumer
specifier|private
specifier|static
class|class
name|RAMTermsConsumer
extends|extends
name|TermsConsumer
block|{
DECL|field|field
specifier|private
name|RAMField
name|field
decl_stmt|;
DECL|field|postingsWriter
specifier|private
specifier|final
name|RAMPostingsWriterImpl
name|postingsWriter
init|=
operator|new
name|RAMPostingsWriterImpl
argument_list|()
decl_stmt|;
DECL|field|current
name|RAMTerm
name|current
decl_stmt|;
DECL|method|reset
name|void
name|reset
parameter_list|(
name|RAMField
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
block|{
specifier|final
name|String
name|term
init|=
name|text
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|current
operator|=
operator|new
name|RAMTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|postingsWriter
operator|.
name|reset
argument_list|(
name|current
argument_list|)
expr_stmt|;
return|return
name|postingsWriter
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
block|{
assert|assert
name|stats
operator|.
name|docFreq
operator|>
literal|0
assert|;
assert|assert
name|stats
operator|.
name|docFreq
operator|==
name|current
operator|.
name|docs
operator|.
name|size
argument_list|()
assert|;
name|current
operator|.
name|totalTermFreq
operator|=
name|stats
operator|.
name|totalTermFreq
expr_stmt|;
name|field
operator|.
name|termToDocs
operator|.
name|put
argument_list|(
name|current
operator|.
name|term
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
block|{
name|field
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|field
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|field
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
block|}
block|}
DECL|class|RAMPostingsWriterImpl
specifier|static
class|class
name|RAMPostingsWriterImpl
extends|extends
name|PostingsConsumer
block|{
DECL|field|term
specifier|private
name|RAMTerm
name|term
decl_stmt|;
DECL|field|current
specifier|private
name|RAMDoc
name|current
decl_stmt|;
DECL|field|posUpto
specifier|private
name|int
name|posUpto
init|=
literal|0
decl_stmt|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|RAMTerm
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|current
operator|=
operator|new
name|RAMDoc
argument_list|(
name|docID
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|term
operator|.
name|docs
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|posUpto
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
assert|assert
name|startOffset
operator|==
operator|-
literal|1
assert|;
assert|assert
name|endOffset
operator|==
operator|-
literal|1
assert|;
name|current
operator|.
name|positions
index|[
name|posUpto
index|]
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|payloads
operator|==
literal|null
condition|)
block|{
name|current
operator|.
name|payloads
operator|=
operator|new
name|byte
index|[
name|current
operator|.
name|positions
operator|.
name|length
index|]
index|[]
expr_stmt|;
block|}
name|byte
index|[]
name|bytes
init|=
name|current
operator|.
name|payloads
index|[
name|posUpto
index|]
operator|=
operator|new
name|byte
index|[
name|payload
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|posUpto
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
block|{
assert|assert
name|posUpto
operator|==
name|current
operator|.
name|positions
operator|.
name|length
assert|;
block|}
block|}
DECL|class|RAMTermsEnum
specifier|static
class|class
name|RAMTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|it
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
decl_stmt|;
DECL|field|current
name|String
name|current
decl_stmt|;
DECL|field|ramField
specifier|private
specifier|final
name|RAMField
name|ramField
decl_stmt|;
DECL|method|RAMTermsEnum
specifier|public
name|RAMTermsEnum
parameter_list|(
name|RAMField
name|field
parameter_list|)
block|{
name|this
operator|.
name|ramField
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|it
operator|=
name|ramField
operator|.
name|termToDocs
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|it
operator|=
name|ramField
operator|.
name|termToDocs
operator|.
name|tailMap
argument_list|(
name|current
argument_list|)
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
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
return|return
operator|new
name|BytesRef
argument_list|(
name|current
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
block|{
name|current
operator|=
name|term
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
name|it
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|ramField
operator|.
name|termToDocs
operator|.
name|containsKey
argument_list|(
name|current
argument_list|)
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
else|else
block|{
if|if
condition|(
name|current
operator|.
name|compareTo
argument_list|(
name|ramField
operator|.
name|termToDocs
operator|.
name|lastKey
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
else|else
block|{
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
comment|// TODO: reuse BytesRef
return|return
operator|new
name|BytesRef
argument_list|(
name|current
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|ramField
operator|.
name|termToDocs
operator|.
name|get
argument_list|(
name|current
argument_list|)
operator|.
name|docs
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
block|{
return|return
name|ramField
operator|.
name|termToDocs
operator|.
name|get
argument_list|(
name|current
argument_list|)
operator|.
name|totalTermFreq
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
return|return
operator|new
name|RAMDocsEnum
argument_list|(
name|ramField
operator|.
name|termToDocs
operator|.
name|get
argument_list|(
name|current
argument_list|)
argument_list|,
name|liveDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
return|return
operator|new
name|RAMDocsAndPositionsEnum
argument_list|(
name|ramField
operator|.
name|termToDocs
operator|.
name|get
argument_list|(
name|current
argument_list|)
argument_list|,
name|liveDocs
argument_list|)
return|;
block|}
block|}
DECL|class|RAMDocsEnum
specifier|private
specifier|static
class|class
name|RAMDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|ramTerm
specifier|private
specifier|final
name|RAMTerm
name|ramTerm
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|current
specifier|private
name|RAMDoc
name|current
decl_stmt|;
DECL|field|upto
name|int
name|upto
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|posUpto
name|int
name|posUpto
init|=
literal|0
decl_stmt|;
DECL|method|RAMDocsEnum
specifier|public
name|RAMDocsEnum
parameter_list|(
name|RAMTerm
name|ramTerm
parameter_list|,
name|Bits
name|liveDocs
parameter_list|)
block|{
name|this
operator|.
name|ramTerm
operator|=
name|ramTerm
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|targetDocID
parameter_list|)
block|{
do|do
block|{
name|nextDoc
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|upto
operator|<
name|ramTerm
operator|.
name|docs
operator|.
name|size
argument_list|()
operator|&&
name|current
operator|.
name|docID
operator|<
name|targetDocID
condition|)
do|;
return|return
name|NO_MORE_DOCS
return|;
block|}
comment|// TODO: override bulk read, for better perf
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|<
name|ramTerm
operator|.
name|docs
operator|.
name|size
argument_list|()
condition|)
block|{
name|current
operator|=
name|ramTerm
operator|.
name|docs
operator|.
name|get
argument_list|(
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|current
operator|.
name|docID
argument_list|)
condition|)
block|{
name|posUpto
operator|=
literal|0
expr_stmt|;
return|return
name|current
operator|.
name|docID
return|;
block|}
block|}
else|else
block|{
return|return
name|NO_MORE_DOCS
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
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|positions
operator|.
name|length
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
name|current
operator|.
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|ramTerm
operator|.
name|docs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|class|RAMDocsAndPositionsEnum
specifier|private
specifier|static
class|class
name|RAMDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|ramTerm
specifier|private
specifier|final
name|RAMTerm
name|ramTerm
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|current
specifier|private
name|RAMDoc
name|current
decl_stmt|;
DECL|field|upto
name|int
name|upto
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|posUpto
name|int
name|posUpto
init|=
literal|0
decl_stmt|;
DECL|method|RAMDocsAndPositionsEnum
specifier|public
name|RAMDocsAndPositionsEnum
parameter_list|(
name|RAMTerm
name|ramTerm
parameter_list|,
name|Bits
name|liveDocs
parameter_list|)
block|{
name|this
operator|.
name|ramTerm
operator|=
name|ramTerm
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|targetDocID
parameter_list|)
block|{
do|do
block|{
name|nextDoc
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|upto
operator|<
name|ramTerm
operator|.
name|docs
operator|.
name|size
argument_list|()
operator|&&
name|current
operator|.
name|docID
operator|<
name|targetDocID
condition|)
do|;
return|return
name|NO_MORE_DOCS
return|;
block|}
comment|// TODO: override bulk read, for better perf
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|<
name|ramTerm
operator|.
name|docs
operator|.
name|size
argument_list|()
condition|)
block|{
name|current
operator|=
name|ramTerm
operator|.
name|docs
operator|.
name|get
argument_list|(
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|current
operator|.
name|docID
argument_list|)
condition|)
block|{
name|posUpto
operator|=
literal|0
expr_stmt|;
return|return
name|current
operator|.
name|docID
return|;
block|}
block|}
else|else
block|{
return|return
name|NO_MORE_DOCS
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
throws|throws
name|IOException
block|{
return|return
name|current
operator|.
name|positions
operator|.
name|length
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
name|current
operator|.
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
block|{
return|return
name|current
operator|.
name|positions
index|[
name|posUpto
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
block|{
if|if
condition|(
name|current
operator|.
name|payloads
operator|!=
literal|null
operator|&&
name|current
operator|.
name|payloads
index|[
name|posUpto
operator|-
literal|1
index|]
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|current
operator|.
name|payloads
index|[
name|posUpto
operator|-
literal|1
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|ramTerm
operator|.
name|docs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|// Holds all indexes created, keyed by the ID assigned in fieldsConsumer
DECL|field|state
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|RAMPostings
argument_list|>
name|state
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|RAMPostings
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nextID
specifier|private
specifier|final
name|AtomicInteger
name|nextID
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|RAM_ONLY_NAME
specifier|private
specifier|final
name|String
name|RAM_ONLY_NAME
init|=
literal|"RAMOnly"
decl_stmt|;
DECL|field|VERSION_START
specifier|private
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_LATEST
specifier|private
specifier|final
specifier|static
name|int
name|VERSION_LATEST
init|=
name|VERSION_START
decl_stmt|;
DECL|field|ID_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|ID_EXTENSION
init|=
literal|"id"
decl_stmt|;
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|id
init|=
name|nextID
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
comment|// TODO -- ok to do this up front instead of
comment|// on close....?  should be ok?
comment|// Write our ID:
specifier|final
name|String
name|idFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|writeState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|writeState
operator|.
name|segmentSuffix
argument_list|,
name|ID_EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
name|writeState
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|idFileName
argument_list|,
name|writeState
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
name|RAM_ONLY_NAME
argument_list|,
name|VERSION_LATEST
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|id
argument_list|)
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
specifier|final
name|RAMPostings
name|postings
init|=
operator|new
name|RAMPostings
argument_list|()
decl_stmt|;
specifier|final
name|RAMFieldsConsumer
name|consumer
init|=
operator|new
name|RAMFieldsConsumer
argument_list|(
name|postings
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|state
init|)
block|{
name|state
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|postings
argument_list|)
expr_stmt|;
block|}
return|return
name|consumer
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
name|readState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Load our ID:
specifier|final
name|String
name|idFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|readState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|readState
operator|.
name|segmentSuffix
argument_list|,
name|ID_EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|in
init|=
name|readState
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|idFileName
argument_list|,
name|readState
operator|.
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|int
name|id
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|RAM_ONLY_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_LATEST
argument_list|)
expr_stmt|;
name|id
operator|=
name|in
operator|.
name|readVInt
argument_list|()
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
synchronized|synchronized
init|(
name|state
init|)
block|{
return|return
name|state
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
