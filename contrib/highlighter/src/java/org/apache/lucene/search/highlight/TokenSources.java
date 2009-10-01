begin_unit
begin_comment
comment|/*  * Created on 28-Oct-2004  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|io
operator|.
name|StringReader
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
name|Arrays
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
name|analysis
operator|.
name|Analyzer
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
name|Token
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
name|TokenStream
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
name|OffsetAttribute
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
name|TermAttribute
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
name|Document
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
name|TermFreqVector
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
name|TermPositionVector
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
name|TermVectorOffsetInfo
import|;
end_import
begin_comment
comment|/**  * Hides implementation issues associated with obtaining a TokenStream for use with  * the higlighter - can obtain from TermFreqVectors with offsets and (optionally) positions or  * from Analyzer class reparsing the stored content.  */
end_comment
begin_class
DECL|class|TokenSources
specifier|public
class|class
name|TokenSources
block|{
comment|/**    * A convenience method that tries to first get a TermPositionVector for the specified docId, then, falls back to    * using the passed in {@link org.apache.lucene.document.Document} to retrieve the TokenStream.  This is useful when    * you already have the document, but would prefer to use the vector first.    * @param reader The {@link org.apache.lucene.index.IndexReader} to use to try and get the vector from    * @param docId The docId to retrieve.    * @param field The field to retrieve on the document    * @param doc The document to fall back on    * @param analyzer The analyzer to use for creating the TokenStream if the vector doesn't exist    * @return The {@link org.apache.lucene.analysis.TokenStream} for the {@link org.apache.lucene.document.Fieldable} on the {@link org.apache.lucene.document.Document}    * @throws IOException if there was an error loading    */
DECL|method|getAnyTokenStream
specifier|public
specifier|static
name|TokenStream
name|getAnyTokenStream
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|Document
name|doc
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
literal|null
decl_stmt|;
name|TermFreqVector
name|tfv
init|=
operator|(
name|TermFreqVector
operator|)
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|tfv
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tfv
operator|instanceof
name|TermPositionVector
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
operator|(
name|TermPositionVector
operator|)
name|tfv
argument_list|)
expr_stmt|;
block|}
block|}
comment|//No token info stored so fall back to analyzing raw content
if|if
condition|(
name|ts
operator|==
literal|null
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
comment|/**      * A convenience method that tries a number of approaches to getting a token stream.      * The cost of finding there are no termVectors in the index is minimal (1000 invocations still       * registers 0 ms). So this "lazy" (flexible?) approach to coding is probably acceptable      * @param reader      * @param docId      * @param field      * @param analyzer      * @return null if field not stored correctly       * @throws IOException      */
DECL|method|getAnyTokenStream
specifier|public
specifier|static
name|TokenStream
name|getAnyTokenStream
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
literal|null
decl_stmt|;
name|TermFreqVector
name|tfv
init|=
operator|(
name|TermFreqVector
operator|)
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|tfv
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tfv
operator|instanceof
name|TermPositionVector
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
operator|(
name|TermPositionVector
operator|)
name|tfv
argument_list|)
expr_stmt|;
block|}
block|}
comment|//No token info stored so fall back to analyzing raw content
if|if
condition|(
name|ts
operator|==
literal|null
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|TermPositionVector
name|tpv
parameter_list|)
block|{
comment|//assumes the worst and makes no assumptions about token position sequences.
return|return
name|getTokenStream
argument_list|(
name|tpv
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Low level api.      * Returns a token stream or null if no offset info available in index.      * This can be used to feed the highlighter with a pre-parsed token stream       *       * In my tests the speeds to recreate 1000 token streams using this method are:      * - with TermVector offset only data stored - 420  milliseconds       * - with TermVector offset AND position data stored - 271 milliseconds      *  (nb timings for TermVector with position data are based on a tokenizer with contiguous      *  positions - no overlaps or gaps)      * The cost of not using TermPositionVector to store      * pre-parsed content and using an analyzer to re-parse the original content:       * - reanalyzing the original content - 980 milliseconds      *       * The re-analyze timings will typically vary depending on -      * 	1) The complexity of the analyzer code (timings above were using a       * 	   stemmer/lowercaser/stopword combo)      *  2) The  number of other fields (Lucene reads ALL fields off the disk       *     when accessing just one document field - can cost dear!)      *  3) Use of compression on field storage - could be faster due to compression (less disk IO)      *     or slower (more CPU burn) depending on the content.      *      * @param tpv      * @param tokenPositionsGuaranteedContiguous true if the token position numbers have no overlaps or gaps. If looking      * to eek out the last drops of performance, set to true. If in doubt, set to false.      */
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|TermPositionVector
name|tpv
parameter_list|,
name|boolean
name|tokenPositionsGuaranteedContiguous
parameter_list|)
block|{
comment|//an object used to iterate across an array of tokens
class|class
name|StoredTokenStream
extends|extends
name|TokenStream
block|{
name|Token
name|tokens
index|[]
decl_stmt|;
name|int
name|currentToken
init|=
literal|0
decl_stmt|;
name|TermAttribute
name|termAtt
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
name|StoredTokenStream
parameter_list|(
name|Token
name|tokens
index|[]
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentToken
operator|>=
name|tokens
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Token
name|token
init|=
name|tokens
index|[
name|currentToken
operator|++
index|]
decl_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|token
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|//code to reconstruct the original sequence of Tokens
name|String
index|[]
name|terms
init|=
name|tpv
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|int
index|[]
name|freq
init|=
name|tpv
operator|.
name|getTermFrequencies
argument_list|()
decl_stmt|;
name|int
name|totalTokens
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|freq
operator|.
name|length
condition|;
name|t
operator|++
control|)
block|{
name|totalTokens
operator|+=
name|freq
index|[
name|t
index|]
expr_stmt|;
block|}
name|Token
name|tokensInOriginalOrder
index|[]
init|=
operator|new
name|Token
index|[
name|totalTokens
index|]
decl_stmt|;
name|ArrayList
name|unsortedTokens
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|freq
operator|.
name|length
condition|;
name|t
operator|++
control|)
block|{
name|TermVectorOffsetInfo
index|[]
name|offsets
init|=
name|tpv
operator|.
name|getOffsets
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
index|[]
name|pos
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tokenPositionsGuaranteedContiguous
condition|)
block|{
comment|//try get the token position info to speed up assembly of tokens into sorted sequence
name|pos
operator|=
name|tpv
operator|.
name|getTermPositions
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|==
literal|null
condition|)
block|{
comment|//tokens NOT stored with positions or not guaranteed contiguous - must add to list and sort later
if|if
condition|(
name|unsortedTokens
operator|==
literal|null
condition|)
block|{
name|unsortedTokens
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|tp
init|=
literal|0
init|;
name|tp
operator|<
name|offsets
operator|.
name|length
condition|;
name|tp
operator|++
control|)
block|{
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|offsets
index|[
name|tp
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|offsets
index|[
name|tp
index|]
operator|.
name|getEndOffset
argument_list|()
argument_list|)
decl_stmt|;
name|token
operator|.
name|setTermBuffer
argument_list|(
name|terms
index|[
name|t
index|]
argument_list|)
expr_stmt|;
name|unsortedTokens
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//We have positions stored and a guarantee that the token position information is contiguous
comment|// This may be fast BUT wont work if Tokenizers used which create>1 token in same position or
comment|// creates jumps in position numbers - this code would fail under those circumstances
comment|//tokens stored with positions - can use this to index straight into sorted array
for|for
control|(
name|int
name|tp
init|=
literal|0
init|;
name|tp
operator|<
name|pos
operator|.
name|length
condition|;
name|tp
operator|++
control|)
block|{
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|terms
index|[
name|t
index|]
argument_list|,
name|offsets
index|[
name|tp
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|offsets
index|[
name|tp
index|]
operator|.
name|getEndOffset
argument_list|()
argument_list|)
decl_stmt|;
name|tokensInOriginalOrder
index|[
name|pos
index|[
name|tp
index|]
index|]
operator|=
name|token
expr_stmt|;
block|}
block|}
block|}
comment|//If the field has been stored without position data we must perform a sort
if|if
condition|(
name|unsortedTokens
operator|!=
literal|null
condition|)
block|{
name|tokensInOriginalOrder
operator|=
operator|(
name|Token
index|[]
operator|)
name|unsortedTokens
operator|.
name|toArray
argument_list|(
operator|new
name|Token
index|[
name|unsortedTokens
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|tokensInOriginalOrder
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|Token
name|t1
init|=
operator|(
name|Token
operator|)
name|o1
decl_stmt|;
name|Token
name|t2
init|=
operator|(
name|Token
operator|)
name|o2
decl_stmt|;
if|if
condition|(
name|t1
operator|.
name|startOffset
argument_list|()
operator|>
name|t2
operator|.
name|endOffset
argument_list|()
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|startOffset
argument_list|()
operator|<
name|t2
operator|.
name|startOffset
argument_list|()
condition|)
return|return
operator|-
literal|1
return|;
return|return
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StoredTokenStream
argument_list|(
name|tokensInOriginalOrder
argument_list|)
return|;
block|}
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|TermFreqVector
name|tfv
init|=
operator|(
name|TermFreqVector
operator|)
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|tfv
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|field
operator|+
literal|" in doc #"
operator|+
name|docId
operator|+
literal|"does not have any term position data stored"
argument_list|)
throw|;
block|}
if|if
condition|(
name|tfv
operator|instanceof
name|TermPositionVector
condition|)
block|{
name|TermPositionVector
name|tpv
init|=
operator|(
name|TermPositionVector
operator|)
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
name|getTokenStream
argument_list|(
name|tpv
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|field
operator|+
literal|" in doc #"
operator|+
name|docId
operator|+
literal|"does not have any term position data stored"
argument_list|)
throw|;
block|}
comment|//convenience method
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docId
argument_list|)
decl_stmt|;
return|return
name|getTokenStream
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|String
name|contents
init|=
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|contents
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field "
operator|+
name|field
operator|+
literal|" in document is not stored and cannot be analyzed"
argument_list|)
throw|;
block|}
return|return
name|getTokenStream
argument_list|(
name|field
argument_list|,
name|contents
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
comment|//convenience method
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|contents
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
return|return
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|contents
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
