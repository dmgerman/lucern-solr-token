begin_unit
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
name|Collections
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
name|index
operator|.
name|BinaryDocValues
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
name|FieldInfos
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
name|Fields
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
name|LeafReader
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
name|NumericDocValues
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
name|SortedDocValues
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
name|SortedNumericDocValues
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
name|SortedSetDocValues
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
name|StoredFieldVisitor
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
name|Terms
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
comment|/**  * Wraps a Terms with a {@link org.apache.lucene.index.LeafReader}, typically from term vectors.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermVectorLeafReader
specifier|public
class|class
name|TermVectorLeafReader
extends|extends
name|LeafReader
block|{
DECL|field|fields
specifier|private
specifier|final
name|Fields
name|fields
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|method|TermVectorLeafReader
specifier|public
name|TermVectorLeafReader
parameter_list|(
name|String
name|field
parameter_list|,
name|Terms
name|terms
parameter_list|)
block|{
name|fields
operator|=
operator|new
name|Fields
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
name|Collections
operator|.
name|singletonList
argument_list|(
name|field
argument_list|)
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
name|fld
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|fld
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|terms
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
literal|1
return|;
block|}
block|}
expr_stmt|;
name|IndexOptions
name|indexOptions
decl_stmt|;
if|if
condition|(
operator|!
name|terms
operator|.
name|hasFreqs
argument_list|()
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|terms
operator|.
name|hasPositions
argument_list|()
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|terms
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
block|}
else|else
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
expr_stmt|;
block|}
name|FieldInfo
name|fieldInfo
init|=
operator|new
name|FieldInfo
argument_list|(
name|field
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|terms
operator|.
name|hasPayloads
argument_list|()
argument_list|,
name|indexOptions
argument_list|,
name|DocValuesType
operator|.
name|NONE
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
operator|new
name|FieldInfo
index|[]
block|{
name|fieldInfo
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addCoreClosedListener
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|addCoreClosedListenerAsReaderClosedListener
argument_list|(
name|this
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeCoreClosedListener
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|removeCoreClosedListenerAsReaderClosedListener
argument_list|(
name|this
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedNumericDocValues
specifier|public
name|SortedNumericDocValues
name|getSortedNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getNormValues
specifier|public
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
comment|//Is this needed?  See MemoryIndex for a way to do it.
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
return|return
literal|null
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
block|{   }
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docID
operator|!=
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fields
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{   }
block|}
end_class
end_unit
