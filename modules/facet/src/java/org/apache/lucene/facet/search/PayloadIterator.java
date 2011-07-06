begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package
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
name|IndexReader
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
name|MultiFields
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
name|Term
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
name|DocIdSetIterator
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A utility class for iterating through a posting list of a given term and  * retrieving the payload of the first occurrence in every document. Comes with  * its own working space (buffer).  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|PayloadIterator
specifier|public
class|class
name|PayloadIterator
block|{
DECL|field|buffer
specifier|protected
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|payloadLength
specifier|protected
name|int
name|payloadLength
decl_stmt|;
DECL|field|tp
name|DocsAndPositionsEnum
name|tp
decl_stmt|;
DECL|field|hasMore
specifier|private
name|boolean
name|hasMore
decl_stmt|;
DECL|method|PayloadIterator
specifier|public
name|PayloadIterator
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|indexReader
argument_list|,
name|term
argument_list|,
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|PayloadIterator
specifier|public
name|PayloadIterator
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Term
name|term
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
comment|// TODO (Facet): avoid Multi*?
name|Bits
name|liveDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|this
operator|.
name|tp
operator|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|indexReader
argument_list|,
name|liveDocs
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * (re)initialize the iterator. Should be done before the first call to    * {@link #setdoc(int)}. Returns false if there is no category list found    * (no setdoc() will never return true).    */
DECL|method|init
specifier|public
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|hasMore
operator|=
name|tp
operator|!=
literal|null
operator|&&
name|tp
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
return|return
name|hasMore
return|;
block|}
comment|/**    * Skip forward to document docId. Return true if this document exists and    * has any payload.    *<P>    * Users should call this method with increasing docIds, and implementations    * can assume that this is the case.    */
DECL|method|setdoc
specifier|public
name|boolean
name|setdoc
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hasMore
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|tp
operator|.
name|docID
argument_list|()
operator|>
name|docId
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// making sure we have the requested document
if|if
condition|(
name|tp
operator|.
name|docID
argument_list|()
operator|<
name|docId
condition|)
block|{
comment|// Skipping to requested document
if|if
condition|(
name|tp
operator|.
name|advance
argument_list|(
name|docId
argument_list|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|this
operator|.
name|hasMore
operator|=
literal|false
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// If document not found (skipped to much)
if|if
condition|(
name|tp
operator|.
name|docID
argument_list|()
operator|!=
name|docId
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Prepare for payload extraction
name|tp
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
comment|// TODO: fix bug in SepCodec and then remove this check (the null check should be enough)
if|if
condition|(
operator|!
name|tp
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BytesRef
name|br
init|=
name|tp
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|br
operator|==
literal|null
operator|||
name|br
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|this
operator|.
name|payloadLength
operator|=
name|br
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|payloadLength
operator|>
name|this
operator|.
name|buffer
operator|.
name|length
condition|)
block|{
comment|// Growing if necessary.
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|payloadLength
operator|*
literal|2
operator|+
literal|1
index|]
expr_stmt|;
block|}
comment|// Loading the payload
name|System
operator|.
name|arraycopy
argument_list|(
name|br
operator|.
name|bytes
argument_list|,
name|br
operator|.
name|offset
argument_list|,
name|this
operator|.
name|buffer
argument_list|,
literal|0
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Get the buffer with the content of the last read payload.    */
DECL|method|getBuffer
specifier|public
name|byte
index|[]
name|getBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
comment|/**    * Get the length of the last read payload.    */
DECL|method|getPayloadLength
specifier|public
name|int
name|getPayloadLength
parameter_list|()
block|{
return|return
name|payloadLength
return|;
block|}
block|}
end_class
end_unit
