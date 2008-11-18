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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Fieldable
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
name|tokenattributes
operator|.
name|PayloadAttribute
import|;
end_import
begin_comment
comment|// TODO: break into separate freq and prox writers as
end_comment
begin_comment
comment|// codecs; make separate container (tii/tis/skip/*) that can
end_comment
begin_comment
comment|// be configured as any number of files 1..N
end_comment
begin_class
DECL|class|FreqProxTermsWriterPerField
specifier|final
class|class
name|FreqProxTermsWriterPerField
extends|extends
name|TermsHashConsumerPerField
implements|implements
name|Comparable
block|{
DECL|field|perThread
specifier|final
name|FreqProxTermsWriterPerThread
name|perThread
decl_stmt|;
DECL|field|termsHashPerField
specifier|final
name|TermsHashPerField
name|termsHashPerField
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|field|omitTf
name|boolean
name|omitTf
decl_stmt|;
DECL|field|payloadAttribute
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
DECL|method|FreqProxTermsWriterPerField
specifier|public
name|FreqProxTermsWriterPerField
parameter_list|(
name|TermsHashPerField
name|termsHashPerField
parameter_list|,
name|FreqProxTermsWriterPerThread
name|perThread
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|termsHashPerField
operator|=
name|termsHashPerField
expr_stmt|;
name|this
operator|.
name|perThread
operator|=
name|perThread
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|termsHashPerField
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
name|termsHashPerField
operator|.
name|fieldState
expr_stmt|;
name|omitTf
operator|=
name|fieldInfo
operator|.
name|omitTf
expr_stmt|;
block|}
DECL|method|getStreamCount
name|int
name|getStreamCount
parameter_list|()
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|omitTf
condition|)
return|return
literal|1
return|;
else|else
return|return
literal|2
return|;
block|}
DECL|method|finish
name|void
name|finish
parameter_list|()
block|{}
DECL|field|hasPayloads
name|boolean
name|hasPayloads
decl_stmt|;
DECL|method|skippingLongTerm
name|void
name|skippingLongTerm
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other0
parameter_list|)
block|{
name|FreqProxTermsWriterPerField
name|other
init|=
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|other0
decl_stmt|;
return|return
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
return|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
comment|// Record, up front, whether our in-RAM format will be
comment|// with or without term freqs:
name|omitTf
operator|=
name|fieldInfo
operator|.
name|omitTf
expr_stmt|;
name|payloadAttribute
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|start
name|boolean
name|start
parameter_list|(
name|Fieldable
index|[]
name|fields
parameter_list|,
name|int
name|count
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|isIndexed
argument_list|()
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
DECL|method|start
name|void
name|start
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
if|if
condition|(
name|fieldState
operator|.
name|attributeSource
operator|.
name|hasAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|payloadAttribute
operator|=
operator|(
name|PayloadAttribute
operator|)
name|fieldState
operator|.
name|attributeSource
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payloadAttribute
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|writeProx
specifier|final
name|void
name|writeProx
parameter_list|(
name|FreqProxTermsWriter
operator|.
name|PostingList
name|p
parameter_list|,
name|int
name|proxCode
parameter_list|)
block|{
specifier|final
name|Payload
name|payload
decl_stmt|;
if|if
condition|(
name|payloadAttribute
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|=
name|payloadAttribute
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
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
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
operator|(
name|proxCode
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeBytes
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|data
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|hasPayloads
operator|=
literal|true
expr_stmt|;
block|}
else|else
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|proxCode
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|lastPosition
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
DECL|method|newTerm
specifier|final
name|void
name|newTerm
parameter_list|(
name|RawPostingList
name|p0
parameter_list|)
block|{
comment|// First time we're seeing this term since the last
comment|// flush
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"FreqProxTermsWriterPerField.newTerm start"
argument_list|)
assert|;
name|FreqProxTermsWriter
operator|.
name|PostingList
name|p
init|=
operator|(
name|FreqProxTermsWriter
operator|.
name|PostingList
operator|)
name|p0
decl_stmt|;
name|p
operator|.
name|lastDocID
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
if|if
condition|(
name|omitTf
condition|)
block|{
name|p
operator|.
name|lastDocCode
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
block|}
else|else
block|{
name|p
operator|.
name|lastDocCode
operator|=
name|docState
operator|.
name|docID
operator|<<
literal|1
expr_stmt|;
name|p
operator|.
name|docFreq
operator|=
literal|1
expr_stmt|;
name|writeProx
argument_list|(
name|p
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addTerm
specifier|final
name|void
name|addTerm
parameter_list|(
name|RawPostingList
name|p0
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"FreqProxTermsWriterPerField.addTerm start"
argument_list|)
assert|;
name|FreqProxTermsWriter
operator|.
name|PostingList
name|p
init|=
operator|(
name|FreqProxTermsWriter
operator|.
name|PostingList
operator|)
name|p0
decl_stmt|;
assert|assert
name|omitTf
operator|||
name|p
operator|.
name|docFreq
operator|>
literal|0
assert|;
if|if
condition|(
name|omitTf
condition|)
block|{
if|if
condition|(
name|docState
operator|.
name|docID
operator|!=
name|p
operator|.
name|lastDocID
condition|)
block|{
assert|assert
name|docState
operator|.
name|docID
operator|>
name|p
operator|.
name|lastDocID
assert|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|lastDocCode
argument_list|)
expr_stmt|;
name|p
operator|.
name|lastDocCode
operator|=
name|docState
operator|.
name|docID
operator|-
name|p
operator|.
name|lastDocID
expr_stmt|;
name|p
operator|.
name|lastDocID
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|docState
operator|.
name|docID
operator|!=
name|p
operator|.
name|lastDocID
condition|)
block|{
assert|assert
name|docState
operator|.
name|docID
operator|>
name|p
operator|.
name|lastDocID
assert|;
comment|// Term not yet seen in the current doc but previously
comment|// seen in other doc(s) since the last flush
comment|// Now that we know doc freq for previous doc,
comment|// write it& lastDocCode
if|if
condition|(
literal|1
operator|==
name|p
operator|.
name|docFreq
condition|)
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|lastDocCode
operator||
literal|1
argument_list|)
expr_stmt|;
else|else
block|{
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|lastDocCode
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|docFreq
operator|=
literal|1
expr_stmt|;
name|p
operator|.
name|lastDocCode
operator|=
operator|(
name|docState
operator|.
name|docID
operator|-
name|p
operator|.
name|lastDocID
operator|)
operator|<<
literal|1
expr_stmt|;
name|p
operator|.
name|lastDocID
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
name|writeProx
argument_list|(
name|p
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|p
operator|.
name|docFreq
operator|++
expr_stmt|;
name|writeProx
argument_list|(
name|p
argument_list|,
name|fieldState
operator|.
name|position
operator|-
name|p
operator|.
name|lastPosition
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{}
block|}
end_class
end_unit
