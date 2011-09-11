begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
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
name|index
operator|.
name|MultiDocsEnum
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
name|MultiDocsAndPositionsEnum
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
name|FixedBitSet
import|;
end_import
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermsConsumer
specifier|public
specifier|abstract
class|class
name|TermsConsumer
block|{
comment|/** Starts a new term in this field; this may be called    *  with no corresponding call to finish if the term had    *  no docs. */
DECL|method|startTerm
specifier|public
specifier|abstract
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Finishes the current term; numDocs must be> 0. */
DECL|method|finishTerm
specifier|public
specifier|abstract
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when we are done adding terms to this field */
DECL|method|finish
specifier|public
specifier|abstract
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
throws|throws
name|IOException
function_decl|;
comment|/** Return the BytesRef Comparator used to sort terms    *  before feeding to this API. */
DECL|method|getComparator
specifier|public
specifier|abstract
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Default merge impl */
DECL|field|docsEnum
specifier|private
name|MappingMultiDocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
DECL|field|postingsEnum
specifier|private
name|MappingMultiDocsAndPositionsEnum
name|postingsEnum
init|=
literal|null
decl_stmt|;
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|term
decl_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
name|long
name|sumTotalTermFreq
init|=
literal|0
decl_stmt|;
name|long
name|sumDocFreq
init|=
literal|0
decl_stmt|;
name|long
name|sumDFsinceLastAbortCheck
init|=
literal|0
decl_stmt|;
name|FixedBitSet
name|visitedDocs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|mergeState
operator|.
name|mergedDocCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
name|docsEnum
operator|=
operator|new
name|MappingMultiDocsEnum
argument_list|()
expr_stmt|;
block|}
name|docsEnum
operator|.
name|setMergeState
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|MultiDocsEnum
name|docsEnumIn
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// We can pass null for liveDocs, because the
comment|// mapping enum will skip the non-live docs:
name|docsEnumIn
operator|=
operator|(
name|MultiDocsEnum
operator|)
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnumIn
argument_list|)
expr_stmt|;
if|if
condition|(
name|docsEnumIn
operator|!=
literal|null
condition|)
block|{
name|docsEnum
operator|.
name|reset
argument_list|(
name|docsEnumIn
argument_list|)
expr_stmt|;
specifier|final
name|PostingsConsumer
name|postingsConsumer
init|=
name|startTerm
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|final
name|TermStats
name|stats
init|=
name|postingsConsumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|docsEnum
argument_list|,
name|visitedDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|.
name|docFreq
operator|>
literal|0
condition|)
block|{
name|finishTerm
argument_list|(
name|term
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|sumTotalTermFreq
operator|+=
name|stats
operator|.
name|totalTermFreq
expr_stmt|;
name|sumDFsinceLastAbortCheck
operator|+=
name|stats
operator|.
name|docFreq
expr_stmt|;
name|sumDocFreq
operator|+=
name|stats
operator|.
name|docFreq
expr_stmt|;
if|if
condition|(
name|sumDFsinceLastAbortCheck
operator|>
literal|60000
condition|)
block|{
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
name|sumDFsinceLastAbortCheck
operator|/
literal|5.0
argument_list|)
expr_stmt|;
name|sumDFsinceLastAbortCheck
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|postingsEnum
operator|==
literal|null
condition|)
block|{
name|postingsEnum
operator|=
operator|new
name|MappingMultiDocsAndPositionsEnum
argument_list|()
expr_stmt|;
block|}
name|postingsEnum
operator|.
name|setMergeState
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|MultiDocsAndPositionsEnum
name|postingsEnumIn
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// We can pass null for liveDocs, because the
comment|// mapping enum will skip the non-live docs:
name|postingsEnumIn
operator|=
operator|(
name|MultiDocsAndPositionsEnum
operator|)
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|postingsEnumIn
argument_list|)
expr_stmt|;
if|if
condition|(
name|postingsEnumIn
operator|!=
literal|null
condition|)
block|{
name|postingsEnum
operator|.
name|reset
argument_list|(
name|postingsEnumIn
argument_list|)
expr_stmt|;
comment|// set PayloadProcessor
if|if
condition|(
name|mergeState
operator|.
name|hasPayloadProcessorProvider
condition|)
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
name|mergeState
operator|.
name|readerCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|mergeState
operator|.
name|dirPayloadProcessor
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|mergeState
operator|.
name|currentPayloadProcessor
index|[
name|i
index|]
operator|=
name|mergeState
operator|.
name|dirPayloadProcessor
index|[
name|i
index|]
operator|.
name|getProcessor
argument_list|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|name
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|PostingsConsumer
name|postingsConsumer
init|=
name|startTerm
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|final
name|TermStats
name|stats
init|=
name|postingsConsumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|postingsEnum
argument_list|,
name|visitedDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|.
name|docFreq
operator|>
literal|0
condition|)
block|{
name|finishTerm
argument_list|(
name|term
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|sumTotalTermFreq
operator|+=
name|stats
operator|.
name|totalTermFreq
expr_stmt|;
name|sumDFsinceLastAbortCheck
operator|+=
name|stats
operator|.
name|docFreq
expr_stmt|;
name|sumDocFreq
operator|+=
name|stats
operator|.
name|docFreq
expr_stmt|;
if|if
condition|(
name|sumDFsinceLastAbortCheck
operator|>
literal|60000
condition|)
block|{
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
name|sumDFsinceLastAbortCheck
operator|/
literal|5.0
argument_list|)
expr_stmt|;
name|sumDFsinceLastAbortCheck
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|finish
argument_list|(
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|visitedDocs
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
