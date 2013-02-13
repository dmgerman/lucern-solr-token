begin_unit
begin_package
DECL|package|org.apache.lucene.facet.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
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
name|facet
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|search
operator|.
name|FacetArrays
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
name|facet
operator|.
name|search
operator|.
name|FacetRequest
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
name|facet
operator|.
name|search
operator|.
name|FacetsAggregator
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
name|facet
operator|.
name|search
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link FacetsAggregator} which computes the weight of a category as the sum  * of the integer values associated with it in the result documents. Assumes that  * the association encoded for each ordinal is {@link CategoryIntAssociation}.  */
end_comment
begin_class
DECL|class|SumIntAssociationFacetsAggregator
specifier|public
class|class
name|SumIntAssociationFacetsAggregator
implements|implements
name|FacetsAggregator
block|{
DECL|field|bytes
specifier|private
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|MatchingDocs
name|matchingDocs
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|dv
init|=
name|matchingDocs
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|clp
operator|.
name|field
operator|+
name|CategoryIntAssociation
operator|.
name|ASSOCIATION_LIST_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
return|return;
comment|// no int associations in this reader
block|}
specifier|final
name|int
name|length
init|=
name|matchingDocs
operator|.
name|bits
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|values
init|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|length
operator|&&
operator|(
name|doc
operator|=
name|matchingDocs
operator|.
name|bits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|dv
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// no associations for this document
block|}
comment|// aggreate association values for ordinals
name|int
name|bytesUpto
init|=
name|bytes
operator|.
name|offset
operator|+
name|bytes
operator|.
name|length
decl_stmt|;
name|int
name|pos
init|=
name|bytes
operator|.
name|offset
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|bytesUpto
condition|)
block|{
name|int
name|ordinal
init|=
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
name|int
name|value
init|=
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
operator|.
name|bytes
index|[
name|pos
operator|++
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
name|values
index|[
name|ordinal
index|]
operator|+=
name|value
expr_stmt|;
block|}
operator|++
name|doc
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|requiresDocScores
specifier|public
name|boolean
name|requiresDocScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|rollupValues
specifier|private
name|float
name|rollupValues
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|float
index|[]
name|scores
parameter_list|)
block|{
name|float
name|Value
init|=
literal|0f
decl_stmt|;
while|while
condition|(
name|ordinal
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|float
name|childValue
init|=
name|scores
index|[
name|ordinal
index|]
decl_stmt|;
name|childValue
operator|+=
name|rollupValues
argument_list|(
name|children
index|[
name|ordinal
index|]
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|scores
argument_list|)
expr_stmt|;
name|scores
index|[
name|ordinal
index|]
operator|=
name|childValue
expr_stmt|;
name|Value
operator|+=
name|childValue
expr_stmt|;
name|ordinal
operator|=
name|siblings
index|[
name|ordinal
index|]
expr_stmt|;
block|}
return|return
name|Value
return|;
block|}
annotation|@
name|Override
DECL|method|rollupValues
specifier|public
name|void
name|rollupValues
parameter_list|(
name|FacetRequest
name|fr
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|float
index|[]
name|values
init|=
name|facetArrays
operator|.
name|getFloatArray
argument_list|()
decl_stmt|;
name|values
index|[
name|ordinal
index|]
operator|+=
name|rollupValues
argument_list|(
name|children
index|[
name|ordinal
index|]
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
