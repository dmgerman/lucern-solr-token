begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
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
name|index
operator|.
name|DocValues
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
name|LeafReaderContext
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
name|search
operator|.
name|FieldComparator
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
name|search
operator|.
name|LeafFieldComparator
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
name|search
operator|.
name|Scorer
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoOutsideDistance
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
name|spatial3d
operator|.
name|geom
operator|.
name|DistanceStyle
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
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
import|;
end_import
begin_comment
comment|/**  * Compares documents by outside distance, using a GeoOutsideDistance to compute the distance  */
end_comment
begin_class
DECL|class|Geo3DPointOutsideDistanceComparator
class|class
name|Geo3DPointOutsideDistanceComparator
extends|extends
name|FieldComparator
argument_list|<
name|Double
argument_list|>
implements|implements
name|LeafFieldComparator
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|distanceShape
specifier|final
name|GeoOutsideDistance
name|distanceShape
decl_stmt|;
DECL|field|values
specifier|final
name|double
index|[]
name|values
decl_stmt|;
DECL|field|bottomDistance
name|double
name|bottomDistance
decl_stmt|;
DECL|field|topValue
name|double
name|topValue
decl_stmt|;
DECL|field|currentDocs
name|SortedNumericDocValues
name|currentDocs
decl_stmt|;
DECL|method|Geo3DPointOutsideDistanceComparator
specifier|public
name|Geo3DPointOutsideDistanceComparator
parameter_list|(
name|String
name|field
parameter_list|,
specifier|final
name|GeoOutsideDistance
name|distanceShape
parameter_list|,
name|int
name|numHits
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|distanceShape
operator|=
name|distanceShape
expr_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|double
index|[
name|numHits
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|values
index|[
name|slot1
index|]
argument_list|,
name|values
index|[
name|slot2
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|bottomDistance
operator|=
name|values
index|[
name|slot
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTopValue
specifier|public
name|void
name|setTopValue
parameter_list|(
name|Double
name|value
parameter_list|)
block|{
name|topValue
operator|=
name|value
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|currentDocs
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|numValues
init|=
name|currentDocs
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|numValues
operator|==
literal|0
condition|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|bottomDistance
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
return|;
block|}
name|int
name|cmp
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|long
name|encoded
init|=
name|currentDocs
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// Test against bounds.
comment|// First we need to decode...
specifier|final
name|double
name|x
init|=
name|Geo3DDocValuesField
operator|.
name|decodeXValue
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
specifier|final
name|double
name|y
init|=
name|Geo3DDocValuesField
operator|.
name|decodeYValue
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
specifier|final
name|double
name|z
init|=
name|Geo3DDocValuesField
operator|.
name|decodeZValue
argument_list|(
name|encoded
argument_list|)
decl_stmt|;
name|cmp
operator|=
name|Math
operator|.
name|max
argument_list|(
name|cmp
argument_list|,
name|Double
operator|.
name|compare
argument_list|(
name|bottomDistance
argument_list|,
name|distanceShape
operator|.
name|computeOutsideDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cmp
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|values
index|[
name|slot
index|]
operator|=
name|computeMinimumDistance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafComparator
specifier|public
name|LeafFieldComparator
name|getLeafComparator
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|FieldInfo
name|info
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|Geo3DDocValuesField
operator|.
name|checkCompatible
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|currentDocs
operator|=
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Double
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
comment|// Return the arc distance
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|values
index|[
name|slot
index|]
operator|*
name|PlanetModel
operator|.
name|WGS84_MEAN
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTop
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|topValue
argument_list|,
name|computeMinimumDistance
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
DECL|method|computeMinimumDistance
name|double
name|computeMinimumDistance
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
block|{
name|currentDocs
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|double
name|minValue
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|currentDocs
operator|.
name|count
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|encoded
init|=
name|currentDocs
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|double
name|distance
init|=
name|distanceShape
operator|.
name|computeOutsideDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|Geo3DDocValuesField
operator|.
name|decodeXValue
argument_list|(
name|encoded
argument_list|)
argument_list|,
name|Geo3DDocValuesField
operator|.
name|decodeYValue
argument_list|(
name|encoded
argument_list|)
argument_list|,
name|Geo3DDocValuesField
operator|.
name|decodeZValue
argument_list|(
name|encoded
argument_list|)
argument_list|)
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|distance
argument_list|)
expr_stmt|;
block|}
return|return
name|minValue
return|;
block|}
block|}
end_class
end_unit