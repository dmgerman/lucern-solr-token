begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
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
name|MergeState
import|;
end_import
begin_comment
comment|/** Abstract API to write points  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PointWriter
specifier|public
specifier|abstract
class|class
name|PointWriter
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|PointWriter
specifier|protected
name|PointWriter
parameter_list|()
block|{   }
comment|/** Write all values contained in the provided reader */
DECL|method|writeField
specifier|public
specifier|abstract
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|PointReader
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Default naive merge implementation for one field: it just re-indexes all the values    *  from the incoming segment.  The default codec overrides this for 1D fields and uses    *  a faster but more complex implementation. */
DECL|method|mergeOneField
specifier|protected
name|void
name|mergeOneField
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|writeField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|PointReader
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
name|mergedVisitor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field name must match the field being merged"
argument_list|)
throw|;
block|}
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
name|pointReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PointReader
name|pointReader
init|=
name|mergeState
operator|.
name|pointReaders
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|pointReader
operator|==
literal|null
condition|)
block|{
comment|// This segment has no points
continue|continue;
block|}
name|MergeState
operator|.
name|DocMap
name|docMap
init|=
name|mergeState
operator|.
name|docMaps
index|[
name|i
index|]
decl_stmt|;
name|int
name|docBase
init|=
name|mergeState
operator|.
name|docBase
index|[
name|i
index|]
decl_stmt|;
name|pointReader
operator|.
name|intersect
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|// Should never be called because our compare method never returns Relation.CELL_INSIDE_QUERY
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|newDocID
init|=
name|docMap
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDocID
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Not deleted:
name|mergedVisitor
operator|.
name|visit
argument_list|(
name|docBase
operator|+
name|newDocID
argument_list|,
name|packedValue
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
comment|// Forces this segment's PointReader to always visit all docs + values:
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{                  }
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocCount
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Default merge implementation to merge incoming points readers by visiting all their points and    *  adding to this writer */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|mergeState
operator|.
name|mergeFieldInfos
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|mergeOneField
argument_list|(
name|mergeState
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
block|}
name|finish
argument_list|()
expr_stmt|;
block|}
comment|/** Called once at the end before close */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
