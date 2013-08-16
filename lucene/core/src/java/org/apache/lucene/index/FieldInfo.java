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
name|Map
import|;
end_import
begin_comment
comment|/**  *  Access to the Field Info file that describes document fields and whether or  *  not they are indexed. Each segment has a separate Field Info file. Objects  *  of this class are thread-safe for multiple readers, but only one thread can  *  be adding documents at a time, with no other reader or writer threads  *  accessing this object.  **/
end_comment
begin_class
DECL|class|FieldInfo
specifier|public
specifier|final
class|class
name|FieldInfo
block|{
comment|/** Field's name */
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Internal field number */
DECL|field|number
specifier|public
specifier|final
name|int
name|number
decl_stmt|;
DECL|field|indexed
specifier|private
name|boolean
name|indexed
decl_stmt|;
DECL|field|docValueType
specifier|private
name|DocValuesType
name|docValueType
decl_stmt|;
comment|// True if any document indexed term vectors
DECL|field|storeTermVector
specifier|private
name|boolean
name|storeTermVector
decl_stmt|;
DECL|field|normType
specifier|private
name|DocValuesType
name|normType
decl_stmt|;
DECL|field|omitNorms
specifier|private
name|boolean
name|omitNorms
decl_stmt|;
comment|// omit norms associated with indexed fields
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|field|storePayloads
specifier|private
name|boolean
name|storePayloads
decl_stmt|;
comment|// whether this field stores payloads together with term positions
DECL|field|attributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
comment|/**    * Controls how much information is stored in the postings lists.    * @lucene.experimental    */
DECL|enum|IndexOptions
specifier|public
specifier|static
enum|enum
name|IndexOptions
block|{
comment|// NOTE: order is important here; FieldInfo uses this
comment|// order to merge two conflicting IndexOptions (always
comment|// "downgrades" by picking the lowest).
comment|/**       * Only documents are indexed: term frequencies and positions are omitted.      * Phrase and other positional queries on the field will throw an exception, and scoring      * will behave as if any term in the document appears only once.      */
comment|// TODO: maybe rename to just DOCS?
DECL|enum constant|DOCS_ONLY
name|DOCS_ONLY
block|,
comment|/**       * Only documents and term frequencies are indexed: positions are omitted.       * This enables normal scoring, except Phrase and other positional queries      * will throw an exception.      */
DECL|enum constant|DOCS_AND_FREQS
name|DOCS_AND_FREQS
block|,
comment|/**       * Indexes documents, frequencies and positions.      * This is a typical default for full-text search: full scoring is enabled      * and positional queries are supported.      */
DECL|enum constant|DOCS_AND_FREQS_AND_POSITIONS
name|DOCS_AND_FREQS_AND_POSITIONS
block|,
comment|/**       * Indexes documents, frequencies, positions and offsets.      * Character offsets are encoded alongside the positions.       */
DECL|enum constant|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
block|,   }
empty_stmt|;
comment|/**    * DocValues types.    * Note that DocValues is strongly typed, so a field cannot have different types    * across different documents.    */
DECL|enum|DocValuesType
specifier|public
specifier|static
enum|enum
name|DocValuesType
block|{
comment|/**       * A per-document Number      */
DECL|enum constant|NUMERIC
name|NUMERIC
block|,
comment|/**      * A per-document byte[].  Values may be larger than      * 32766 bytes, but different codecs may enforce their own limits.      */
DECL|enum constant|BINARY
name|BINARY
block|,
comment|/**       * A pre-sorted byte[]. Fields with this type only store distinct byte values       * and store an additional offset pointer per document to dereference the shared       * byte[]. The stored byte[] is presorted and allows access via document id,       * ordinal and by-value.  Values must be<= 32766 bytes.      */
DECL|enum constant|SORTED
name|SORTED
block|,
comment|/**       * A pre-sorted Set&lt;byte[]&gt;. Fields with this type only store distinct byte values       * and store additional offset pointers per document to dereference the shared       * byte[]s. The stored byte[] is presorted and allows access via document id,       * ordinal and by-value.  Values must be<= 32766 bytes.      */
DECL|enum constant|SORTED_SET
name|SORTED_SET
block|}
empty_stmt|;
comment|/**    * Sole Constructor.    *    * @lucene.experimental    */
DECL|method|FieldInfo
specifier|public
name|FieldInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|indexed
parameter_list|,
name|int
name|number
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|,
name|DocValuesType
name|docValues
parameter_list|,
name|DocValuesType
name|normsType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|indexed
operator|=
name|indexed
expr_stmt|;
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|docValueType
operator|=
name|docValues
expr_stmt|;
if|if
condition|(
name|indexed
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
name|storePayloads
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|indexOptions
expr_stmt|;
name|this
operator|.
name|normType
operator|=
operator|!
name|omitNorms
condition|?
name|normsType
else|:
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// for non-indexed fields, leave defaults
name|this
operator|.
name|storeTermVector
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|normType
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
DECL|method|checkConsistency
specifier|private
name|boolean
name|checkConsistency
parameter_list|()
block|{
if|if
condition|(
operator|!
name|indexed
condition|)
block|{
assert|assert
operator|!
name|storeTermVector
assert|;
assert|assert
operator|!
name|storePayloads
assert|;
assert|assert
operator|!
name|omitNorms
assert|;
assert|assert
name|normType
operator|==
literal|null
assert|;
assert|assert
name|indexOptions
operator|==
literal|null
assert|;
block|}
else|else
block|{
assert|assert
name|indexOptions
operator|!=
literal|null
assert|;
if|if
condition|(
name|omitNorms
condition|)
block|{
assert|assert
name|normType
operator|==
literal|null
assert|;
block|}
comment|// Cannot store payloads unless positions are indexed:
assert|assert
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
operator|||
operator|!
name|this
operator|.
name|storePayloads
assert|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|IndexableFieldType
name|ft
parameter_list|)
block|{
name|update
argument_list|(
name|ft
operator|.
name|indexed
argument_list|()
argument_list|,
literal|false
argument_list|,
name|ft
operator|.
name|omitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|ft
operator|.
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// should only be called by FieldInfos#addOrUpdate
DECL|method|update
name|void
name|update
parameter_list|(
name|boolean
name|indexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|)
block|{
comment|//System.out.println("FI.update field=" + name + " indexed=" + indexed + " omitNorms=" + omitNorms + " this.omitNorms=" + this.omitNorms);
if|if
condition|(
name|this
operator|.
name|indexed
operator|!=
name|indexed
condition|)
block|{
name|this
operator|.
name|indexed
operator|=
literal|true
expr_stmt|;
comment|// once indexed, always index
block|}
if|if
condition|(
name|indexed
condition|)
block|{
comment|// if updated field data is not for indexing, leave the updates out
if|if
condition|(
name|this
operator|.
name|storeTermVector
operator|!=
name|storeTermVector
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|storePayloads
operator|!=
name|storePayloads
condition|)
block|{
name|this
operator|.
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|omitNorms
operator|!=
name|omitNorms
condition|)
block|{
name|this
operator|.
name|omitNorms
operator|=
literal|true
expr_stmt|;
comment|// if one require omitNorms at least once, it remains off for life
name|this
operator|.
name|normType
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|!=
name|indexOptions
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|indexOptions
operator|=
name|indexOptions
expr_stmt|;
block|}
else|else
block|{
comment|// downgrade
name|this
operator|.
name|indexOptions
operator|=
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|indexOptions
argument_list|)
operator|<
literal|0
condition|?
name|this
operator|.
name|indexOptions
else|:
name|indexOptions
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// cannot store payloads if we don't store positions:
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
DECL|method|setDocValuesType
name|void
name|setDocValuesType
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
block|{
if|if
condition|(
name|docValueType
operator|!=
literal|null
operator|&&
name|docValueType
operator|!=
name|type
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change DocValues type from "
operator|+
name|docValueType
operator|+
literal|" to "
operator|+
name|type
operator|+
literal|" for field \""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|docValueType
operator|=
name|type
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/** Returns IndexOptions for the field, or null if the field is not indexed */
DECL|method|getIndexOptions
specifier|public
name|IndexOptions
name|getIndexOptions
parameter_list|()
block|{
return|return
name|indexOptions
return|;
block|}
comment|/**    * Returns true if this field has any docValues.    */
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|docValueType
operator|!=
literal|null
return|;
block|}
comment|/**    * Returns {@link DocValuesType} of the docValues. this may be null if the field has no docvalues.    */
DECL|method|getDocValuesType
specifier|public
name|DocValuesType
name|getDocValuesType
parameter_list|()
block|{
return|return
name|docValueType
return|;
block|}
comment|/**    * Returns {@link DocValuesType} of the norm. this may be null if the field has no norms.    */
DECL|method|getNormType
specifier|public
name|DocValuesType
name|getNormType
parameter_list|()
block|{
return|return
name|normType
return|;
block|}
DECL|method|setStoreTermVectors
name|void
name|setStoreTermVectors
parameter_list|()
block|{
name|storeTermVector
operator|=
literal|true
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
DECL|method|setStorePayloads
name|void
name|setStorePayloads
parameter_list|()
block|{
if|if
condition|(
name|indexed
operator|&&
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
DECL|method|setNormValueType
name|void
name|setNormValueType
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
block|{
if|if
condition|(
name|normType
operator|!=
literal|null
operator|&&
name|normType
operator|!=
name|type
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change Norm type from "
operator|+
name|normType
operator|+
literal|" to "
operator|+
name|type
operator|+
literal|" for field \""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|normType
operator|=
name|type
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/**    * Returns true if norms are explicitly omitted for this field    */
DECL|method|omitsNorms
specifier|public
name|boolean
name|omitsNorms
parameter_list|()
block|{
return|return
name|omitNorms
return|;
block|}
comment|/**    * Returns true if this field actually has any norms.    */
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
return|return
name|normType
operator|!=
literal|null
return|;
block|}
comment|/**    * Returns true if this field is indexed.    */
DECL|method|isIndexed
specifier|public
name|boolean
name|isIndexed
parameter_list|()
block|{
return|return
name|indexed
return|;
block|}
comment|/**    * Returns true if any payloads exist for this field.    */
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|storePayloads
return|;
block|}
comment|/**    * Returns true if any term vectors exist for this field.    */
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
return|return
name|storeTermVector
return|;
block|}
comment|/**    * Get a codec attribute value, or null if it does not exist    */
DECL|method|getAttribute
specifier|public
name|String
name|getAttribute
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * Puts a codec attribute value.    *<p>    * This is a key-value mapping for the field that the codec can use    * to store additional metadata, and will be available to the codec    * when reading the segment via {@link #getAttribute(String)}    *<p>    * If a value already exists for the field, it will be replaced with     * the new value.    */
DECL|method|putAttribute
specifier|public
name|String
name|putAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|attributes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * Returns internal codec attributes map. May be null if no mappings exist.    */
DECL|method|attributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
block|}
end_class
end_unit
