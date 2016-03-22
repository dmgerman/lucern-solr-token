begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
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
name|PointValues
operator|.
name|IntersectVisitor
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
name|PointValues
operator|.
name|Relation
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
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|IndexSearcher
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
name|Query
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
name|search
operator|.
name|TwoPhaseIterator
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
name|Weight
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
name|PointValues
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
name|util
operator|.
name|BitSet
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
name|DocIdSetBuilder
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
name|FixedBitSet
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
name|SparseFixedBitSet
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
name|spatial
operator|.
name|util
operator|.
name|GeoRelationUtils
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
name|spatial
operator|.
name|util
operator|.
name|GeoUtils
import|;
end_import
begin_comment
comment|/** Finds all previously indexed points that fall within the specified polygon.  *  *<p>The field must be indexed with using {@link org.apache.lucene.document.LatLonPoint} added per document.  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|LatLonPointInPolygonQuery
specifier|final
class|class
name|LatLonPointInPolygonQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|minLat
specifier|final
name|double
name|minLat
decl_stmt|;
DECL|field|maxLat
specifier|final
name|double
name|maxLat
decl_stmt|;
DECL|field|minLon
specifier|final
name|double
name|minLon
decl_stmt|;
DECL|field|maxLon
specifier|final
name|double
name|maxLon
decl_stmt|;
DECL|field|polyLats
specifier|final
name|double
index|[]
name|polyLats
decl_stmt|;
DECL|field|polyLons
specifier|final
name|double
index|[]
name|polyLons
decl_stmt|;
comment|/** The lats/lons must be clockwise or counter-clockwise. */
DECL|method|LatLonPointInPolygonQuery
specifier|public
name|LatLonPointInPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
index|[]
name|polyLats
parameter_list|,
name|double
index|[]
name|polyLons
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLons
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLons cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|!=
name|polyLons
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats and polyLons must be equal length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|<
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least 4 polygon points required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
index|[
literal|0
index|]
operator|!=
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLats[0]="
operator|+
name|polyLats
index|[
literal|0
index|]
operator|+
literal|" polyLats["
operator|+
operator|(
name|polyLats
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLons
index|[
literal|0
index|]
operator|!=
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLons[0]="
operator|+
name|polyLons
index|[
literal|0
index|]
operator|+
literal|" polyLons["
operator|+
operator|(
name|polyLons
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
name|this
operator|.
name|polyLats
operator|=
name|polyLats
expr_stmt|;
name|this
operator|.
name|polyLons
operator|=
name|polyLons
expr_stmt|;
comment|// TODO: we could also compute the maximal inner bounding box, to make relations faster to compute?
name|double
name|minLon
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|minLat
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLon
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|maxLat
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
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
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lat
init|=
name|polyLats
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|lat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|lat
operator|+
literal|" is not a valid latitude"
argument_list|)
throw|;
block|}
name|minLat
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minLat
argument_list|,
name|lat
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLat
argument_list|,
name|lat
argument_list|)
expr_stmt|;
name|double
name|lon
init|=
name|polyLons
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|lon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLons["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|lat
operator|+
literal|" is not a valid longitude"
argument_list|)
throw|;
block|}
name|minLon
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minLon
argument_list|,
name|lon
argument_list|)
expr_stmt|;
name|maxLon
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLon
argument_list|,
name|lon
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|minLon
operator|=
name|minLon
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|maxLon
expr_stmt|;
name|this
operator|.
name|minLat
operator|=
name|minLat
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|maxLat
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
comment|// I don't use RandomAccessWeight here: it's no good to approximate with "match all docs"; this is an inverted structure and should be
comment|// used in the first pass:
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
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
name|PointValues
name|values
init|=
name|reader
operator|.
name|getPointValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment had any points fields
return|return
literal|null
return|;
block|}
name|FieldInfo
name|fieldInfo
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
name|fieldInfo
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment indexed this field at all
return|return
literal|null
return|;
block|}
name|LatLonPoint
operator|.
name|checkCompatible
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
comment|// approximation (postfiltering has not yet been applied)
name|DocIdSetBuilder
name|result
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// subset of documents that need no postfiltering, this is purely an optimization
specifier|final
name|BitSet
name|preApproved
decl_stmt|;
comment|// dumb heuristic: if the field is really sparse, use a sparse impl
if|if
condition|(
name|values
operator|.
name|getDocCount
argument_list|(
name|field
argument_list|)
operator|*
literal|100L
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|preApproved
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|preApproved
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|intersect
argument_list|(
name|field
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
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|preApproved
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
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
block|{
comment|// TODO: range checks
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
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
name|double
name|cellMinLat
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|minPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|cellMinLon
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|minPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|cellMaxLat
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|maxPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|cellMaxLon
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
name|cellMinLat
operator|<=
name|minLat
operator|&&
name|cellMaxLat
operator|>=
name|maxLat
operator|&&
name|cellMinLon
operator|<=
name|minLon
operator|&&
name|cellMaxLon
operator|>=
name|maxLon
condition|)
block|{
comment|// Cell fully encloses the query
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
elseif|else
if|if
condition|(
name|GeoRelationUtils
operator|.
name|rectWithinPolyPrecise
argument_list|(
name|cellMinLat
argument_list|,
name|cellMaxLat
argument_list|,
name|cellMinLon
argument_list|,
name|cellMaxLon
argument_list|,
name|polyLats
argument_list|,
name|polyLons
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
elseif|else
if|if
condition|(
name|GeoRelationUtils
operator|.
name|rectCrossesPolyPrecise
argument_list|(
name|cellMinLat
argument_list|,
name|cellMaxLat
argument_list|,
name|cellMinLon
argument_list|,
name|cellMaxLon
argument_list|,
name|polyLats
argument_list|,
name|polyLons
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
else|else
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|DocIdSet
name|set
init|=
name|result
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// return two-phase iterator using docvalues to postfilter candidates
name|SortedNumericDocValues
name|docValues
init|=
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|TwoPhaseIterator
name|iterator
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|disi
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|docId
init|=
name|disi
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|preApproved
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|docValues
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|docValues
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|long
name|encoded
init|=
name|docValues
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|double
name|docLatitude
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|encoded
operator|>>
literal|32
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|docLongitude
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|encoded
operator|&
literal|0xFFFFFFFF
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|GeoRelationUtils
operator|.
name|pointInPolygon
argument_list|(
name|polyLats
argument_list|,
name|polyLons
argument_list|,
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|20
operator|*
name|polyLons
operator|.
name|length
return|;
comment|// TODO: make this fancier, but currently linear with number of vertices
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|iterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getMinLat
specifier|public
name|double
name|getMinLat
parameter_list|()
block|{
return|return
name|minLat
return|;
block|}
DECL|method|getMaxLat
specifier|public
name|double
name|getMaxLat
parameter_list|()
block|{
return|return
name|maxLat
return|;
block|}
DECL|method|getMinLon
specifier|public
name|double
name|getMinLon
parameter_list|()
block|{
return|return
name|minLon
return|;
block|}
DECL|method|getMaxLon
specifier|public
name|double
name|getMaxLon
parameter_list|()
block|{
return|return
name|maxLon
return|;
block|}
DECL|method|getPolyLats
specifier|public
name|double
index|[]
name|getPolyLats
parameter_list|()
block|{
return|return
name|polyLats
return|;
block|}
DECL|method|getPolyLons
specifier|public
name|double
index|[]
name|getPolyLons
parameter_list|()
block|{
return|return
name|polyLons
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|LatLonPointInPolygonQuery
name|that
init|=
operator|(
name|LatLonPointInPolygonQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|polyLons
argument_list|,
name|that
operator|.
name|polyLons
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|polyLats
argument_list|,
name|that
operator|.
name|polyLats
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|polyLons
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|polyLats
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" Points: "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|polyLons
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
