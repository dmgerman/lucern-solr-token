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
name|Comparator
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
begin_comment
comment|/** Implements a {@link TermsEnum} wrapping a provided  * {@link SortedDocValues}. */
end_comment
begin_class
DECL|class|SortedDocValuesTermsEnum
specifier|public
class|class
name|SortedDocValuesTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedDocValues
name|values
decl_stmt|;
DECL|field|currentOrd
specifier|private
name|int
name|currentOrd
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|/** Creates a new TermsEnum over the provided values */
DECL|method|SortedDocValuesTermsEnum
specifier|public
name|SortedDocValuesTermsEnum
parameter_list|(
name|SortedDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
comment|/* ignored */
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|currentOrd
operator|=
name|ord
expr_stmt|;
name|term
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
comment|// TODO: is there a cleaner way?
comment|// term.bytes may be pointing to codec-private byte[]
comment|// storage, so we must force new byte[] allocation:
name|term
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|text
operator|.
name|length
index|]
expr_stmt|;
name|term
operator|.
name|copyBytes
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
else|else
block|{
name|currentOrd
operator|=
operator|-
name|ord
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|currentOrd
operator|==
name|values
operator|.
name|getValueCount
argument_list|()
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
comment|// TODO: hmm can we avoid this "extra" lookup?:
name|values
operator|.
name|lookupOrd
argument_list|(
name|currentOrd
argument_list|,
name|term
argument_list|)
expr_stmt|;
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
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|currentOrd
operator|=
name|ord
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
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
throws|throws
name|IOException
block|{
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|values
operator|.
name|getValueCount
argument_list|()
assert|;
name|currentOrd
operator|=
operator|(
name|int
operator|)
name|ord
expr_stmt|;
name|values
operator|.
name|lookupOrd
argument_list|(
name|currentOrd
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|currentOrd
operator|++
expr_stmt|;
if|if
condition|(
name|currentOrd
operator|>=
name|values
operator|.
name|getValueCount
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|values
operator|.
name|lookupOrd
argument_list|(
name|currentOrd
argument_list|,
name|term
argument_list|)
expr_stmt|;
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|currentOrd
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
operator|-
literal|1
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
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|!=
literal|null
operator|&&
name|state
operator|instanceof
name|OrdTermState
assert|;
name|this
operator|.
name|seekExact
argument_list|(
operator|(
operator|(
name|OrdTermState
operator|)
name|state
operator|)
operator|.
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
name|OrdTermState
name|state
init|=
operator|new
name|OrdTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|ord
operator|=
name|currentOrd
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
end_class
end_unit
