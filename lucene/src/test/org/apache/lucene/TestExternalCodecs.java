begin_unit
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|*
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
name|index
operator|.
name|*
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
name|document
operator|.
name|*
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
name|search
operator|.
name|*
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
name|analysis
operator|.
name|*
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
name|*
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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_comment
comment|/* Intentionally outside of oal.index to verify fully    external codecs work fine */
end_comment
begin_class
DECL|class|TestExternalCodecs
specifier|public
class|class
name|TestExternalCodecs
extends|extends
name|LuceneTestCase
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
comment|// TODO
comment|//   - good improvement would be to write through to disk,
comment|//     and then load into ram from disk
DECL|class|RAMOnlyCodec
specifier|public
specifier|static
class|class
name|RAMOnlyCodec
extends|extends
name|Codec
block|{
DECL|method|RAMOnlyCodec
specifier|public
name|RAMOnlyCodec
parameter_list|()
block|{
name|super
argument_list|(
literal|"RamOnly"
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
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|RAMFieldsEnum
argument_list|(
name|this
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
block|{       }
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
DECL|method|RAMField
name|RAMField
parameter_list|(
name|String
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
DECL|method|getUniqueTermCount
specifier|public
name|long
name|getUniqueTermCount
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
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|RAMTermsEnum
argument_list|(
name|RAMOnlyCodec
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
name|RAMField
name|ramField
init|=
operator|new
name|RAMField
argument_list|(
name|field
operator|.
name|name
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
block|}
block|}
DECL|class|RAMPostingsWriterImpl
specifier|public
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
parameter_list|)
block|{
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
comment|// Classes for reading from the postings state
DECL|class|RAMFieldsEnum
specifier|static
class|class
name|RAMFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|postings
specifier|private
specifier|final
name|RAMPostings
name|postings
decl_stmt|;
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
DECL|method|RAMFieldsEnum
specifier|public
name|RAMFieldsEnum
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
name|this
operator|.
name|it
operator|=
name|postings
operator|.
name|fieldToTerms
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
name|TermsEnum
name|terms
parameter_list|()
block|{
return|return
operator|new
name|RAMTermsEnum
argument_list|(
name|postings
operator|.
name|fieldToTerms
operator|.
name|get
argument_list|(
name|current
argument_list|)
argument_list|)
return|;
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
DECL|method|hasPayload
specifier|public
name|boolean
name|hasPayload
parameter_list|()
block|{
return|return
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
block|}
comment|// Holds all indexes created
DECL|field|state
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RAMPostings
argument_list|>
name|state
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RAMPostings
argument_list|>
argument_list|()
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
block|{
name|RAMPostings
name|postings
init|=
operator|new
name|RAMPostings
argument_list|()
decl_stmt|;
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
name|writeState
operator|.
name|segmentName
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
name|readState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|docsConsumer
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|docsProducer
specifier|public
name|PerDocValues
name|docsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
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
block|{     }
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
name|int
name|codecId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{     }
block|}
comment|// tests storing "id" and "field2" fields as pulsing codec,
comment|// whose term sort is backwards unicode code point, and
comment|// storing "field1" as a custom entirely-in-RAM codec
DECL|method|testPerFieldCodec
specifier|public
name|void
name|testPerFieldCodec
parameter_list|()
throws|throws
name|Exception
block|{
name|CodecProvider
name|provider
init|=
operator|new
name|CoreCodecProvider
argument_list|()
decl_stmt|;
name|provider
operator|.
name|register
argument_list|(
operator|new
name|RAMOnlyCodec
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|setDefaultFieldCodec
argument_list|(
literal|"RamOnly"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|173
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we use a custom codec provider
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setCodecProvider
argument_list|(
name|provider
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// uses default codec:
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field1"
argument_list|,
literal|"this field uses the standard codec as the test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// uses pulsing codec:
name|Field
name|field2
init|=
name|newField
argument_list|(
literal|"field2"
argument_list|,
literal|"this field uses the pulsing codec as the test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|provider
operator|.
name|setFieldCodec
argument_list|(
name|field2
operator|.
name|name
argument_list|()
argument_list|,
literal|"Pulsing"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field2
argument_list|)
expr_stmt|;
name|Field
name|idField
init|=
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|provider
operator|.
name|setFieldCodec
argument_list|(
name|idField
operator|.
name|name
argument_list|()
argument_list|,
literal|"Pulsing"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|idField
operator|.
name|setValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now delete id=77"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"77"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexReader
index|[]
name|subs
init|=
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"standard"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"pulsing"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: now delete 2nd doc"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|=
name|newSearcher
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"standard"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|-
literal|2
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"pulsing"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"76"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"77"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
