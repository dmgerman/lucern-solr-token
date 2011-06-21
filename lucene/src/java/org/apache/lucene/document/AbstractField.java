begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|PhraseQuery
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
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
name|spans
operator|.
name|SpanQuery
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
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
name|index
operator|.
name|FieldInvertState
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
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
name|values
operator|.
name|PerDocFieldValues
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
name|values
operator|.
name|ValueType
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|AbstractField
specifier|public
specifier|abstract
class|class
name|AbstractField
implements|implements
name|Fieldable
block|{
DECL|field|name
specifier|protected
name|String
name|name
init|=
literal|"body"
decl_stmt|;
DECL|field|storeTermVector
specifier|protected
name|boolean
name|storeTermVector
init|=
literal|false
decl_stmt|;
DECL|field|storeOffsetWithTermVector
specifier|protected
name|boolean
name|storeOffsetWithTermVector
init|=
literal|false
decl_stmt|;
DECL|field|storePositionWithTermVector
specifier|protected
name|boolean
name|storePositionWithTermVector
init|=
literal|false
decl_stmt|;
DECL|field|omitNorms
specifier|protected
name|boolean
name|omitNorms
init|=
literal|false
decl_stmt|;
DECL|field|isStored
specifier|protected
name|boolean
name|isStored
init|=
literal|false
decl_stmt|;
DECL|field|isIndexed
specifier|protected
name|boolean
name|isIndexed
init|=
literal|true
decl_stmt|;
DECL|field|isTokenized
specifier|protected
name|boolean
name|isTokenized
init|=
literal|true
decl_stmt|;
DECL|field|isBinary
specifier|protected
name|boolean
name|isBinary
init|=
literal|false
decl_stmt|;
DECL|field|lazy
specifier|protected
name|boolean
name|lazy
init|=
literal|false
decl_stmt|;
DECL|field|omitTermFreqAndPositions
specifier|protected
name|boolean
name|omitTermFreqAndPositions
init|=
literal|false
decl_stmt|;
DECL|field|boost
specifier|protected
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|// the data object for all different kind of field values
DECL|field|fieldsData
specifier|protected
name|Object
name|fieldsData
init|=
literal|null
decl_stmt|;
comment|// pre-analyzed tokenStream for indexed fields
DECL|field|tokenStream
specifier|protected
name|TokenStream
name|tokenStream
decl_stmt|;
comment|// length/offset for all primitive types
DECL|field|binaryLength
specifier|protected
name|int
name|binaryLength
decl_stmt|;
DECL|field|binaryOffset
specifier|protected
name|int
name|binaryOffset
decl_stmt|;
DECL|field|docValues
specifier|protected
name|PerDocFieldValues
name|docValues
decl_stmt|;
DECL|method|AbstractField
specifier|protected
name|AbstractField
parameter_list|()
block|{   }
DECL|method|AbstractField
specifier|protected
name|AbstractField
parameter_list|(
name|String
name|name
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|Field
operator|.
name|Index
name|index
parameter_list|,
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|isStored
operator|=
name|store
operator|.
name|isStored
argument_list|()
expr_stmt|;
name|this
operator|.
name|isIndexed
operator|=
name|index
operator|.
name|isIndexed
argument_list|()
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
name|index
operator|.
name|isAnalyzed
argument_list|()
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|index
operator|.
name|omitNorms
argument_list|()
expr_stmt|;
name|this
operator|.
name|isBinary
operator|=
literal|false
expr_stmt|;
name|setStoreTermVector
argument_list|(
name|termVector
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the boost factor hits on this field.  This value will be    * multiplied into the score of all hits on this this field of this    * document.    *    *<p>The boost is multiplied by {@link org.apache.lucene.document.Document#getBoost()} of the document    * containing this field.  If a document has multiple fields with the same    * name, all such values are multiplied together.  This product is then    * used to compute the norm factor for the field.  By    * default, in the {@link    * org.apache.lucene.search.Similarity#computeNorm(FieldInvertState)} method, the boost value is multiplied    * by the length normalization factor and then    * rounded by {@link org.apache.lucene.search.Similarity#encodeNormValue(float)} before it is stored in the    * index.  One should attempt to ensure that this product does not overflow    * the range of that encoding.    *    * @see org.apache.lucene.document.Document#setBoost(float)    * @see org.apache.lucene.search.Similarity#computeNorm(FieldInvertState)    * @see org.apache.lucene.search.Similarity#encodeNormValue(float)    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/** Returns the boost factor for hits for this field.    *    *<p>The default value is 1.0.    *    *<p>Note: this value is not stored directly with the document in the index.    * Documents returned from {@link org.apache.lucene.index.IndexReader#document(int)} and    * {@link org.apache.lucene.search.IndexSearcher#doc(int)} may thus not have the same value present as when    * this field was indexed.    *    * @see #setBoost(float)    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** Returns the name of the field.    * For example "date", "title", "body", ...    */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setStoreTermVector
specifier|protected
name|void
name|setStoreTermVector
parameter_list|(
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
name|termVector
operator|.
name|isStored
argument_list|()
expr_stmt|;
name|this
operator|.
name|storePositionWithTermVector
operator|=
name|termVector
operator|.
name|withPositions
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeOffsetWithTermVector
operator|=
name|termVector
operator|.
name|withOffsets
argument_list|()
expr_stmt|;
block|}
comment|/** True iff the value of the field is to be stored in the index for return     with search hits.  It is an error for this to be true if a field is     Reader-valued. */
DECL|method|isStored
specifier|public
specifier|final
name|boolean
name|isStored
parameter_list|()
block|{
return|return
name|isStored
return|;
block|}
comment|/** True iff the value of the field is to be indexed, so that it may be     searched on. */
DECL|method|isIndexed
specifier|public
specifier|final
name|boolean
name|isIndexed
parameter_list|()
block|{
return|return
name|isIndexed
return|;
block|}
comment|/** True iff the value of the field should be tokenized as text prior to     indexing.  Un-tokenized fields are indexed as a single word and may not be     Reader-valued. */
DECL|method|isTokenized
specifier|public
specifier|final
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
name|isTokenized
return|;
block|}
comment|/** True iff the term or terms used to index this field are stored as a term    *  vector, available from {@link org.apache.lucene.index.IndexReader#getTermFreqVector(int,String)}.    *  These methods do not provide access to the original content of the field,    *  only to terms used to index it. If the original content must be    *  preserved, use the<code>stored</code> attribute instead.    *    * @see org.apache.lucene.index.IndexReader#getTermFreqVector(int, String)    */
DECL|method|isTermVectorStored
specifier|public
specifier|final
name|boolean
name|isTermVectorStored
parameter_list|()
block|{
return|return
name|storeTermVector
return|;
block|}
comment|/**    * True iff terms are stored as term vector together with their offsets     * (start and end position in source text).    */
DECL|method|isStoreOffsetWithTermVector
specifier|public
name|boolean
name|isStoreOffsetWithTermVector
parameter_list|()
block|{
return|return
name|storeOffsetWithTermVector
return|;
block|}
comment|/**    * True iff terms are stored as term vector together with their token positions.    */
DECL|method|isStorePositionWithTermVector
specifier|public
name|boolean
name|isStorePositionWithTermVector
parameter_list|()
block|{
return|return
name|storePositionWithTermVector
return|;
block|}
comment|/** True iff the value of the filed is stored as binary */
DECL|method|isBinary
specifier|public
specifier|final
name|boolean
name|isBinary
parameter_list|()
block|{
return|return
name|isBinary
return|;
block|}
comment|/**    * Return the raw byte[] for the binary field.  Note that    * you must also call {@link #getBinaryLength} and {@link    * #getBinaryOffset} to know which range of bytes in this    * returned array belong to the field.    * @return reference to the Field value as byte[].    */
DECL|method|getBinaryValue
specifier|public
name|byte
index|[]
name|getBinaryValue
parameter_list|()
block|{
return|return
name|getBinaryValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|getBinaryValue
specifier|public
name|byte
index|[]
name|getBinaryValue
parameter_list|(
name|byte
index|[]
name|result
parameter_list|)
block|{
if|if
condition|(
name|isBinary
operator|||
name|fieldsData
operator|instanceof
name|byte
index|[]
condition|)
return|return
operator|(
name|byte
index|[]
operator|)
name|fieldsData
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/**    * Returns length of byte[] segment that is used as value, if Field is not binary    * returned value is undefined    * @return length of byte[] segment that represents this Field value    */
DECL|method|getBinaryLength
specifier|public
name|int
name|getBinaryLength
parameter_list|()
block|{
if|if
condition|(
name|isBinary
condition|)
block|{
return|return
name|binaryLength
return|;
block|}
elseif|else
if|if
condition|(
name|fieldsData
operator|instanceof
name|byte
index|[]
condition|)
return|return
operator|(
operator|(
name|byte
index|[]
operator|)
name|fieldsData
operator|)
operator|.
name|length
return|;
else|else
return|return
literal|0
return|;
block|}
comment|/**    * Returns offset into byte[] segment that is used as value, if Field is not binary    * returned value is undefined    * @return index of the first character in byte[] segment that represents this Field value    */
DECL|method|getBinaryOffset
specifier|public
name|int
name|getBinaryOffset
parameter_list|()
block|{
return|return
name|binaryOffset
return|;
block|}
comment|/** True if norms are omitted for this indexed field */
DECL|method|getOmitNorms
specifier|public
name|boolean
name|getOmitNorms
parameter_list|()
block|{
return|return
name|omitNorms
return|;
block|}
comment|/** @see #setOmitTermFreqAndPositions */
DECL|method|getOmitTermFreqAndPositions
specifier|public
name|boolean
name|getOmitTermFreqAndPositions
parameter_list|()
block|{
return|return
name|omitTermFreqAndPositions
return|;
block|}
comment|/** Expert:    *    * If set, omit normalization factors associated with this indexed field.    * This effectively disables indexing boosts and length normalization for this field.    */
DECL|method|setOmitNorms
specifier|public
name|void
name|setOmitNorms
parameter_list|(
name|boolean
name|omitNorms
parameter_list|)
block|{
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
block|}
comment|/** Expert:    *    * If set, omit term freq, positions and payloads from    * postings for this field.    *    *<p><b>NOTE</b>: While this option reduces storage space    * required in the index, it also means any query    * requiring positional information, such as {@link    * PhraseQuery} or {@link SpanQuery} subclasses will    * silently fail to find results.    */
DECL|method|setOmitTermFreqAndPositions
specifier|public
name|void
name|setOmitTermFreqAndPositions
parameter_list|(
name|boolean
name|omitTermFreqAndPositions
parameter_list|)
block|{
name|this
operator|.
name|omitTermFreqAndPositions
operator|=
name|omitTermFreqAndPositions
expr_stmt|;
block|}
DECL|method|isLazy
specifier|public
name|boolean
name|isLazy
parameter_list|()
block|{
return|return
name|lazy
return|;
block|}
comment|/** Prints a Field for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|isStored
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"stored"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isIndexed
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"indexed"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isTokenized
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"tokenized"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVector
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"termVector"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeOffsetWithTermVector
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"termVectorOffsets"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storePositionWithTermVector
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"termVectorPosition"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isBinary
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"binary"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omitNorms
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",omitNorms"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omitTermFreqAndPositions
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",omitTermFreqAndPositions"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lazy
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",lazy"
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsData
operator|!=
literal|null
operator|&&
name|lazy
operator|==
literal|false
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|fieldsData
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getDocValues
specifier|public
name|PerDocFieldValues
name|getDocValues
parameter_list|()
block|{
return|return
name|docValues
return|;
block|}
DECL|method|setDocValues
specifier|public
name|void
name|setDocValues
parameter_list|(
name|PerDocFieldValues
name|docValues
parameter_list|)
block|{
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
block|}
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|docValues
operator|!=
literal|null
operator|&&
name|docValues
operator|.
name|type
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|docValuesType
specifier|public
name|ValueType
name|docValuesType
parameter_list|()
block|{
return|return
name|docValues
operator|==
literal|null
condition|?
literal|null
else|:
name|docValues
operator|.
name|type
argument_list|()
return|;
block|}
block|}
end_class
end_unit
