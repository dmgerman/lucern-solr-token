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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|Set
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
name|DocValuesFormat
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
name|DocValuesProducer
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
name|Directory
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
name|IOContext
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
name|RamUsageEstimator
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
name|Version
import|;
end_import
begin_comment
comment|/** Encapsulates multiple producers when there are docvalues updates as one producer */
end_comment
begin_comment
comment|// TODO: try to clean up close? no-op?
end_comment
begin_comment
comment|// TODO: add shared base class (also used by per-field-pf?) to allow "punching thru" to low level producer?
end_comment
begin_class
DECL|class|SegmentDocValuesProducer
class|class
name|SegmentDocValuesProducer
extends|extends
name|DocValuesProducer
block|{
DECL|field|LONG_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|LONG_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|SegmentDocValuesProducer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dvProducersByField
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesProducer
argument_list|>
name|dvProducersByField
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|dvProducers
specifier|final
name|Set
argument_list|<
name|DocValuesProducer
argument_list|>
name|dvProducers
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|DocValuesProducer
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|dvGens
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|dvGens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|SegmentDocValuesProducer
name|SegmentDocValuesProducer
parameter_list|(
name|SegmentCommitInfo
name|si
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|SegmentDocValues
name|segDocValues
parameter_list|,
name|DocValuesFormat
name|dvFormat
parameter_list|)
throws|throws
name|IOException
block|{
name|Version
name|ver
init|=
name|si
operator|.
name|info
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|ver
operator|!=
literal|null
operator|&&
name|ver
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_9_0
argument_list|)
condition|)
block|{
name|DocValuesProducer
name|baseProducer
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
operator|!
name|fi
operator|.
name|hasDocValues
argument_list|()
condition|)
continue|continue;
name|long
name|docValuesGen
init|=
name|fi
operator|.
name|getDocValuesGen
argument_list|()
decl_stmt|;
if|if
condition|(
name|docValuesGen
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|baseProducer
operator|==
literal|null
condition|)
block|{
comment|// the base producer gets all the fields, so the Codec can validate properly
name|baseProducer
operator|=
name|segDocValues
operator|.
name|getDocValuesProducer
argument_list|(
name|docValuesGen
argument_list|,
name|si
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|,
name|dir
argument_list|,
name|dvFormat
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
name|dvGens
operator|.
name|add
argument_list|(
name|docValuesGen
argument_list|)
expr_stmt|;
name|dvProducers
operator|.
name|add
argument_list|(
name|baseProducer
argument_list|)
expr_stmt|;
block|}
name|dvProducersByField
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|baseProducer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
operator|!
name|dvGens
operator|.
name|contains
argument_list|(
name|docValuesGen
argument_list|)
assert|;
specifier|final
name|DocValuesProducer
name|dvp
init|=
name|segDocValues
operator|.
name|getDocValuesProducer
argument_list|(
name|docValuesGen
argument_list|,
name|si
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|,
name|dir
argument_list|,
name|dvFormat
argument_list|,
operator|new
name|FieldInfos
argument_list|(
operator|new
name|FieldInfo
index|[]
block|{
name|fi
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|dvGens
operator|.
name|add
argument_list|(
name|docValuesGen
argument_list|)
expr_stmt|;
name|dvProducers
operator|.
name|add
argument_list|(
name|dvp
argument_list|)
expr_stmt|;
name|dvProducersByField
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|dvp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// For pre-4.9 indexes, especially with doc-values updates, multiple
comment|// FieldInfos could belong to the same dvGen. Therefore need to make sure
comment|// we initialize each DocValuesProducer once per gen.
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|genInfos
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
operator|!
name|fi
operator|.
name|hasDocValues
argument_list|()
condition|)
continue|continue;
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|genFieldInfos
init|=
name|genInfos
operator|.
name|get
argument_list|(
name|fi
operator|.
name|getDocValuesGen
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|genFieldInfos
operator|==
literal|null
condition|)
block|{
name|genFieldInfos
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|genInfos
operator|.
name|put
argument_list|(
name|fi
operator|.
name|getDocValuesGen
argument_list|()
argument_list|,
name|genFieldInfos
argument_list|)
expr_stmt|;
block|}
name|genFieldInfos
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
argument_list|>
name|e
range|:
name|genInfos
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|docValuesGen
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|infos
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|DocValuesProducer
name|dvp
decl_stmt|;
if|if
condition|(
name|docValuesGen
operator|==
operator|-
literal|1
condition|)
block|{
comment|// we need to send all FieldInfos to gen=-1, but later we need to
comment|// record the DVP only for the "true" gen=-1 fields (not updated)
name|dvp
operator|=
name|segDocValues
operator|.
name|getDocValuesProducer
argument_list|(
name|docValuesGen
argument_list|,
name|si
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|,
name|dir
argument_list|,
name|dvFormat
argument_list|,
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dvp
operator|=
name|segDocValues
operator|.
name|getDocValuesProducer
argument_list|(
name|docValuesGen
argument_list|,
name|si
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|,
name|dir
argument_list|,
name|dvFormat
argument_list|,
operator|new
name|FieldInfos
argument_list|(
name|infos
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|infos
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dvGens
operator|.
name|add
argument_list|(
name|docValuesGen
argument_list|)
expr_stmt|;
name|dvProducers
operator|.
name|add
argument_list|(
name|dvp
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|infos
control|)
block|{
name|dvProducersByField
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|dvp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducersByField
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
return|return
name|dvProducer
operator|.
name|getNumeric
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducersByField
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
return|return
name|dvProducer
operator|.
name|getBinary
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducersByField
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
return|return
name|dvProducer
operator|.
name|getSorted
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedNumeric
specifier|public
name|SortedNumericDocValues
name|getSortedNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducersByField
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
return|return
name|dvProducer
operator|.
name|getSortedNumeric
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSet
specifier|public
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducersByField
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
return|return
name|dvProducer
operator|.
name|getSortedSet
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesProducer
name|dvProducer
init|=
name|dvProducersByField
operator|.
name|get
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
assert|assert
name|dvProducer
operator|!=
literal|null
assert|;
return|return
name|dvProducer
operator|.
name|getDocsWithField
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|DocValuesProducer
name|producer
range|:
name|dvProducers
control|)
block|{
name|producer
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// there is separate ref tracking
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|ramBytesUsed
init|=
name|BASE_RAM_BYTES_USED
decl_stmt|;
name|ramBytesUsed
operator|+=
name|dvGens
operator|.
name|size
argument_list|()
operator|*
name|LONG_RAM_BYTES_USED
expr_stmt|;
name|ramBytesUsed
operator|+=
name|dvProducers
operator|.
name|size
argument_list|()
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
expr_stmt|;
name|ramBytesUsed
operator|+=
name|dvProducersByField
operator|.
name|size
argument_list|()
operator|*
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
expr_stmt|;
for|for
control|(
name|DocValuesProducer
name|producer
range|:
name|dvProducers
control|)
block|{
name|ramBytesUsed
operator|+=
name|producer
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|ramBytesUsed
return|;
block|}
block|}
end_class
end_unit
