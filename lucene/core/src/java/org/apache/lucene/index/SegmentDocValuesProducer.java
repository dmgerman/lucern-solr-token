begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|Accountable
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
name|Accountables
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
comment|/**    * Creates a new producer that handles updated docvalues fields    * @param si commit point    * @param dir directory    * @param coreInfos fieldinfos for the segment    * @param allInfos all fieldinfos including updated ones    * @param segDocValues producer map    */
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
name|coreInfos
parameter_list|,
name|FieldInfos
name|allInfos
parameter_list|,
name|SegmentDocValues
name|segDocValues
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
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
name|allInfos
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
continue|continue;
block|}
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
comment|// the base producer gets the original fieldinfos it wrote
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
name|dir
argument_list|,
name|coreInfos
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
comment|// otherwise, producer sees only the one fieldinfo it wrote
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
name|dir
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
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
try|try
block|{
name|segDocValues
operator|.
name|decRef
argument_list|(
name|dvGens
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore so we keep throwing first exception
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
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|dvProducers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Accountable
name|producer
range|:
name|dvProducers
control|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"delegate"
argument_list|,
name|producer
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(producers="
operator|+
name|dvProducers
operator|.
name|size
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
