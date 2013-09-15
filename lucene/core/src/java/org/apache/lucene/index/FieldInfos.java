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
name|Collection
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
name|DocValuesType
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
begin_comment
comment|/**   * Collection of {@link FieldInfo}s (accessible by number or by name).  *  @lucene.experimental  */
end_comment
begin_class
DECL|class|FieldInfos
specifier|public
class|class
name|FieldInfos
implements|implements
name|Iterable
argument_list|<
name|FieldInfo
argument_list|>
block|{
DECL|field|hasFreq
specifier|private
specifier|final
name|boolean
name|hasFreq
decl_stmt|;
DECL|field|hasProx
specifier|private
specifier|final
name|boolean
name|hasProx
decl_stmt|;
DECL|field|hasPayloads
specifier|private
specifier|final
name|boolean
name|hasPayloads
decl_stmt|;
DECL|field|hasOffsets
specifier|private
specifier|final
name|boolean
name|hasOffsets
decl_stmt|;
DECL|field|hasVectors
specifier|private
specifier|final
name|boolean
name|hasVectors
decl_stmt|;
DECL|field|hasNorms
specifier|private
specifier|final
name|boolean
name|hasNorms
decl_stmt|;
DECL|field|hasDocValues
specifier|private
specifier|final
name|boolean
name|hasDocValues
decl_stmt|;
DECL|field|byNumber
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
name|byNumber
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|byName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|Collection
argument_list|<
name|FieldInfo
argument_list|>
name|values
decl_stmt|;
comment|// for an unmodifiable iterator
comment|/**    * Constructs a new FieldInfos from an array of FieldInfo objects    */
DECL|method|FieldInfos
specifier|public
name|FieldInfos
parameter_list|(
name|FieldInfo
index|[]
name|infos
parameter_list|)
block|{
name|boolean
name|hasVectors
init|=
literal|false
decl_stmt|;
name|boolean
name|hasProx
init|=
literal|false
decl_stmt|;
name|boolean
name|hasPayloads
init|=
literal|false
decl_stmt|;
name|boolean
name|hasOffsets
init|=
literal|false
decl_stmt|;
name|boolean
name|hasFreq
init|=
literal|false
decl_stmt|;
name|boolean
name|hasNorms
init|=
literal|false
decl_stmt|;
name|boolean
name|hasDocValues
init|=
literal|false
decl_stmt|;
for|for
control|(
name|FieldInfo
name|info
range|:
name|infos
control|)
block|{
name|FieldInfo
name|previous
init|=
name|byNumber
operator|.
name|put
argument_list|(
name|info
operator|.
name|number
argument_list|,
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"duplicate field numbers: "
operator|+
name|previous
operator|.
name|name
operator|+
literal|" and "
operator|+
name|info
operator|.
name|name
operator|+
literal|" have: "
operator|+
name|info
operator|.
name|number
argument_list|)
throw|;
block|}
name|previous
operator|=
name|byName
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"duplicate field names: "
operator|+
name|previous
operator|.
name|number
operator|+
literal|" and "
operator|+
name|info
operator|.
name|number
operator|+
literal|" have: "
operator|+
name|info
operator|.
name|name
argument_list|)
throw|;
block|}
name|hasVectors
operator||=
name|info
operator|.
name|hasVectors
argument_list|()
expr_stmt|;
name|hasProx
operator||=
name|info
operator|.
name|isIndexed
argument_list|()
operator|&&
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
expr_stmt|;
name|hasFreq
operator||=
name|info
operator|.
name|isIndexed
argument_list|()
operator|&&
name|info
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
name|hasOffsets
operator||=
name|info
operator|.
name|isIndexed
argument_list|()
operator|&&
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
expr_stmt|;
name|hasNorms
operator||=
name|info
operator|.
name|hasNorms
argument_list|()
expr_stmt|;
name|hasDocValues
operator||=
name|info
operator|.
name|hasDocValues
argument_list|()
expr_stmt|;
name|hasPayloads
operator||=
name|info
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|hasVectors
operator|=
name|hasVectors
expr_stmt|;
name|this
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|this
operator|.
name|hasPayloads
operator|=
name|hasPayloads
expr_stmt|;
name|this
operator|.
name|hasOffsets
operator|=
name|hasOffsets
expr_stmt|;
name|this
operator|.
name|hasFreq
operator|=
name|hasFreq
expr_stmt|;
name|this
operator|.
name|hasNorms
operator|=
name|hasNorms
expr_stmt|;
name|this
operator|.
name|hasDocValues
operator|=
name|hasDocValues
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|byNumber
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Returns true if any fields have freqs */
DECL|method|hasFreq
specifier|public
name|boolean
name|hasFreq
parameter_list|()
block|{
return|return
name|hasFreq
return|;
block|}
comment|/** Returns true if any fields have positions */
DECL|method|hasProx
specifier|public
name|boolean
name|hasProx
parameter_list|()
block|{
return|return
name|hasProx
return|;
block|}
comment|/** Returns true if any fields have payloads */
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|hasPayloads
return|;
block|}
comment|/** Returns true if any fields have offsets */
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
name|hasOffsets
return|;
block|}
comment|/** Returns true if any fields have vectors */
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
return|return
name|hasVectors
return|;
block|}
comment|/** Returns true if any fields have norms */
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
return|return
name|hasNorms
return|;
block|}
comment|/** Returns true if any fields have DocValues */
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|hasDocValues
return|;
block|}
comment|/** Returns the number of fields */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
assert|assert
name|byNumber
operator|.
name|size
argument_list|()
operator|==
name|byName
operator|.
name|size
argument_list|()
assert|;
return|return
name|byNumber
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns an iterator over all the fieldinfo objects present,    * ordered by ascending field number    */
comment|// TODO: what happens if in fact a different order is used?
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|FieldInfo
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|values
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Return the fieldinfo object referenced by the field name    * @return the FieldInfo object or null when the given fieldName    * doesn't exist.    */
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/**    * Return the fieldinfo object referenced by the fieldNumber.    * @param fieldNumber field's number. if this is negative, this method    *        always returns null.    * @return the FieldInfo object or null when the given fieldNumber    * doesn't exist.    */
comment|// TODO: fix this negative behavior, this was something related to Lucene3x?
comment|// if the field name is empty, i think it writes the fieldNumber as -1
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
operator|(
name|fieldNumber
operator|>=
literal|0
operator|)
condition|?
name|byNumber
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
else|:
literal|null
return|;
block|}
DECL|class|FieldNumbers
specifier|static
specifier|final
class|class
name|FieldNumbers
block|{
DECL|field|numberToName
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|numberToName
decl_stmt|;
DECL|field|nameToNumber
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nameToNumber
decl_stmt|;
comment|// We use this to enforce that a given field never
comment|// changes DV type, even across segments / IndexWriter
comment|// sessions:
DECL|field|docValuesType
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesType
argument_list|>
name|docValuesType
decl_stmt|;
comment|// TODO: we should similarly catch an attempt to turn
comment|// norms back on after they were already ommitted; today
comment|// we silently discard the norm but this is badly trappy
DECL|field|lowestUnassignedFieldNumber
specifier|private
name|int
name|lowestUnassignedFieldNumber
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|FieldNumbers
name|FieldNumbers
parameter_list|()
block|{
name|this
operator|.
name|nameToNumber
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberToName
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|docValuesType
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesType
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the global field number for the given field name. If the name      * does not exist yet it tries to add it with the given preferred field      * number assigned if possible otherwise the first unassigned field number      * is used as the field number.      */
DECL|method|addOrGet
specifier|synchronized
name|int
name|addOrGet
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|preferredFieldNumber
parameter_list|,
name|DocValuesType
name|dvType
parameter_list|)
block|{
if|if
condition|(
name|dvType
operator|!=
literal|null
condition|)
block|{
name|DocValuesType
name|currentDVType
init|=
name|docValuesType
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentDVType
operator|==
literal|null
condition|)
block|{
name|docValuesType
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|dvType
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentDVType
operator|!=
literal|null
operator|&&
name|currentDVType
operator|!=
name|dvType
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change DocValues type from "
operator|+
name|currentDVType
operator|+
literal|" to "
operator|+
name|dvType
operator|+
literal|" for field \""
operator|+
name|fieldName
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
name|Integer
name|fieldNumber
init|=
name|nameToNumber
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldNumber
operator|==
literal|null
condition|)
block|{
specifier|final
name|Integer
name|preferredBoxed
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|preferredFieldNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|preferredFieldNumber
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|numberToName
operator|.
name|containsKey
argument_list|(
name|preferredBoxed
argument_list|)
condition|)
block|{
comment|// cool - we can use this number globally
name|fieldNumber
operator|=
name|preferredBoxed
expr_stmt|;
block|}
else|else
block|{
comment|// find a new FieldNumber
while|while
condition|(
name|numberToName
operator|.
name|containsKey
argument_list|(
operator|++
name|lowestUnassignedFieldNumber
argument_list|)
condition|)
block|{
comment|// might not be up to date - lets do the work once needed
block|}
name|fieldNumber
operator|=
name|lowestUnassignedFieldNumber
expr_stmt|;
block|}
name|numberToName
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|nameToNumber
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|fieldNumber
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldNumber
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|// used by assert
DECL|method|containsConsistent
specifier|synchronized
name|boolean
name|containsConsistent
parameter_list|(
name|Integer
name|number
parameter_list|,
name|String
name|name
parameter_list|,
name|DocValuesType
name|dvType
parameter_list|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
name|numberToName
operator|.
name|get
argument_list|(
name|number
argument_list|)
argument_list|)
operator|&&
name|number
operator|.
name|equals
argument_list|(
name|nameToNumber
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
operator|&&
operator|(
name|dvType
operator|==
literal|null
operator|||
name|docValuesType
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|==
literal|null
operator|||
name|dvType
operator|==
name|docValuesType
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|)
return|;
block|}
comment|/**      * Returns true if the {@code fieldName} exists in the map and is of the      * same {@code dvType}.      */
DECL|method|contains
specifier|synchronized
name|boolean
name|contains
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|DocValuesType
name|dvType
parameter_list|)
block|{
comment|// used by IndexWriter.updateNumericDocValue
if|if
condition|(
operator|!
name|nameToNumber
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// only return true if the field has the same dvType as the requested one
return|return
name|dvType
operator|==
name|docValuesType
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
DECL|method|clear
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|numberToName
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nameToNumber
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docValuesType
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|setDocValuesType
specifier|synchronized
name|void
name|setDocValuesType
parameter_list|(
name|int
name|number
parameter_list|,
name|String
name|name
parameter_list|,
name|DocValuesType
name|dvType
parameter_list|)
block|{
assert|assert
name|containsConsistent
argument_list|(
name|number
argument_list|,
name|name
argument_list|,
name|dvType
argument_list|)
assert|;
name|docValuesType
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|dvType
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Builder
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|byName
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|byName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|globalFieldNumbers
specifier|final
name|FieldNumbers
name|globalFieldNumbers
decl_stmt|;
DECL|method|Builder
name|Builder
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|FieldNumbers
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new instance with the given {@link FieldNumbers}.       */
DECL|method|Builder
name|Builder
parameter_list|(
name|FieldNumbers
name|globalFieldNumbers
parameter_list|)
block|{
assert|assert
name|globalFieldNumbers
operator|!=
literal|null
assert|;
name|this
operator|.
name|globalFieldNumbers
operator|=
name|globalFieldNumbers
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|FieldInfos
name|other
parameter_list|)
block|{
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|other
control|)
block|{
name|add
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** NOTE: this method does not carry over termVector      *  booleans nor docValuesType; the indexer chain      *  (TermVectorsConsumerPerField, DocFieldProcessor) must      *  set these fields when they succeed in consuming      *  the document */
DECL|method|addOrUpdate
specifier|public
name|FieldInfo
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|fieldType
parameter_list|)
block|{
comment|// TODO: really, indexer shouldn't even call this
comment|// method (it's only called from DocFieldProcessor);
comment|// rather, each component in the chain should update
comment|// what it "owns".  EG fieldType.indexOptions() should
comment|// be updated by maybe FreqProxTermsWriterPerField:
return|return
name|addOrUpdateInternal
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
name|fieldType
operator|.
name|indexed
argument_list|()
argument_list|,
literal|false
argument_list|,
name|fieldType
operator|.
name|omitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|fieldType
operator|.
name|indexOptions
argument_list|()
argument_list|,
name|fieldType
operator|.
name|docValueType
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|addOrUpdateInternal
specifier|private
name|FieldInfo
name|addOrUpdateInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|preferredFieldNumber
parameter_list|,
name|boolean
name|isIndexed
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
name|normType
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// This field wasn't yet added to this in-RAM
comment|// segment's FieldInfo, so now we get a global
comment|// number for this field.  If the field was seen
comment|// before then we'll get the same name and number,
comment|// else we'll allocate a new one:
specifier|final
name|int
name|fieldNumber
init|=
name|globalFieldNumbers
operator|.
name|addOrGet
argument_list|(
name|name
argument_list|,
name|preferredFieldNumber
argument_list|,
name|docValues
argument_list|)
decl_stmt|;
name|fi
operator|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValues
argument_list|,
name|normType
argument_list|,
literal|null
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|byName
operator|.
name|containsKey
argument_list|(
name|fi
operator|.
name|name
argument_list|)
assert|;
assert|assert
name|globalFieldNumbers
operator|.
name|containsConsistent
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|fi
operator|.
name|number
argument_list|)
argument_list|,
name|fi
operator|.
name|name
argument_list|,
name|fi
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
assert|;
name|byName
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fi
operator|.
name|update
argument_list|(
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|docValues
operator|!=
literal|null
condition|)
block|{
comment|// only pay the synchronization cost if fi does not already have a DVType
name|boolean
name|updateGlobal
init|=
operator|!
name|fi
operator|.
name|hasDocValues
argument_list|()
decl_stmt|;
name|fi
operator|.
name|setDocValuesType
argument_list|(
name|docValues
argument_list|)
expr_stmt|;
comment|// this will also perform the consistency check.
if|if
condition|(
name|updateGlobal
condition|)
block|{
comment|// must also update docValuesType map so it's
comment|// aware of this field's DocValueType
name|globalFieldNumbers
operator|.
name|setDocValuesType
argument_list|(
name|fi
operator|.
name|number
argument_list|,
name|name
argument_list|,
name|docValues
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|fi
operator|.
name|omitsNorms
argument_list|()
operator|&&
name|normType
operator|!=
literal|null
condition|)
block|{
name|fi
operator|.
name|setNormValueType
argument_list|(
name|normType
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fi
return|;
block|}
DECL|method|add
specifier|public
name|FieldInfo
name|add
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
comment|// IMPORTANT - reuse the field number if possible for consistent field numbers across segments
return|return
name|addOrUpdateInternal
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
operator|.
name|number
argument_list|,
name|fi
operator|.
name|isIndexed
argument_list|()
argument_list|,
name|fi
operator|.
name|hasVectors
argument_list|()
argument_list|,
name|fi
operator|.
name|omitsNorms
argument_list|()
argument_list|,
name|fi
operator|.
name|hasPayloads
argument_list|()
argument_list|,
name|fi
operator|.
name|getIndexOptions
argument_list|()
argument_list|,
name|fi
operator|.
name|getDocValuesType
argument_list|()
argument_list|,
name|fi
operator|.
name|getNormType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|finish
specifier|final
name|FieldInfos
name|finish
parameter_list|()
block|{
return|return
operator|new
name|FieldInfos
argument_list|(
name|byName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|byName
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
