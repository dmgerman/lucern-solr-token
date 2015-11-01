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
name|Iterator
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
name|DimensionalReader
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
name|codecs
operator|.
name|FieldsProducer
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
name|NormsProducer
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
name|StoredFieldsReader
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
name|TermVectorsReader
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
begin_comment
comment|/**  * Wraps arbitrary readers for merging. Note that this can cause slow  * and memory-intensive merges. Consider using {@link FilterCodecReader}  * instead.  */
end_comment
begin_class
DECL|class|SlowCodecReaderWrapper
specifier|public
specifier|final
class|class
name|SlowCodecReaderWrapper
block|{
comment|/** No instantiation */
DECL|method|SlowCodecReaderWrapper
specifier|private
name|SlowCodecReaderWrapper
parameter_list|()
block|{}
comment|/**    * Returns a {@code CodecReader} view of reader.     *<p>    * If {@code reader} is already a {@code CodecReader}, it is returned    * directly. Otherwise, a (slow) view is returned.    */
DECL|method|wrap
specifier|public
specifier|static
name|CodecReader
name|wrap
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|instanceof
name|CodecReader
condition|)
block|{
return|return
operator|(
name|CodecReader
operator|)
name|reader
return|;
block|}
else|else
block|{
comment|// simulate it slowly, over the leafReader api:
name|reader
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
return|return
operator|new
name|CodecReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TermVectorsReader
name|getTermVectorsReader
parameter_list|()
block|{
name|reader
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerToTermVectorsReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoredFieldsReader
name|getFieldsReader
parameter_list|()
block|{
name|reader
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerToStoredFieldsReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NormsProducer
name|getNormsReader
parameter_list|()
block|{
name|reader
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerToNormsProducer
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocValuesProducer
name|getDocValuesReader
parameter_list|()
block|{
name|reader
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerToDocValuesProducer
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldsProducer
name|getPostingsReader
parameter_list|()
block|{
name|reader
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|readerToFieldsProducer
argument_list|(
name|reader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|reader
operator|.
name|getFieldInfos
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DimensionalValues
name|getDimensionalValues
parameter_list|()
block|{
return|return
name|reader
operator|.
name|getDimensionalValues
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DimensionalReader
name|getDimensionalReader
parameter_list|()
block|{
return|return
name|dimensionalValuesToReader
argument_list|(
name|reader
operator|.
name|getDimensionalValues
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
name|reader
operator|.
name|getLiveDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|reader
operator|.
name|numDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|reader
operator|.
name|maxDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|reader
operator|.
name|addCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|reader
operator|.
name|removeCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
DECL|method|dimensionalValuesToReader
specifier|private
specifier|static
name|DimensionalReader
name|dimensionalValuesToReader
parameter_list|(
name|DimensionalValues
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|DimensionalReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|.
name|intersect
argument_list|(
name|fieldName
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToNormsProducer
specifier|private
specifier|static
name|NormsProducer
name|readerToNormsProducer
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|NormsProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getNormValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToDocValuesProducer
specifier|private
specifier|static
name|DocValuesProducer
name|readerToDocValuesProducer
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|DocValuesProducer
argument_list|()
block|{
annotation|@
name|Override
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
return|return
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|reader
operator|.
name|getBinaryDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|reader
operator|.
name|getSortedNumericDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|reader
operator|.
name|getDocsWithField
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToStoredFieldsReader
specifier|private
specifier|static
name|StoredFieldsReader
name|readerToStoredFieldsReader
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|StoredFieldsReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visitDocument
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|StoredFieldsReader
name|clone
parameter_list|()
block|{
return|return
name|readerToStoredFieldsReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToTermVectorsReader
specifier|private
specifier|static
name|TermVectorsReader
name|readerToTermVectorsReader
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Fields
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermVectorsReader
name|clone
parameter_list|()
block|{
return|return
name|readerToTermVectorsReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToFieldsProducer
specifier|private
specifier|static
name|FieldsProducer
name|readerToFieldsProducer
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
return|return
operator|new
name|FieldsProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fields
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
