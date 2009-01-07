begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.geohash
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geohash
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
name|BitSet
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|IndexReader
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
name|FieldCache
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
name|tier
operator|.
name|DistanceFilter
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
name|tier
operator|.
name|DistanceUtils
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
name|tier
operator|.
name|DistanceHandler
operator|.
name|Precision
import|;
end_import
begin_class
DECL|class|GeoHashDistanceFilter
specifier|public
class|class
name|GeoHashDistanceFilter
extends|extends
name|DistanceFilter
block|{
comment|/**    *     */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|distance
specifier|private
name|double
name|distance
decl_stmt|;
DECL|field|lat
specifier|private
name|double
name|lat
decl_stmt|;
DECL|field|lng
specifier|private
name|double
name|lng
decl_stmt|;
DECL|field|geoHashField
specifier|private
name|String
name|geoHashField
decl_stmt|;
DECL|field|log
specifier|private
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|distances
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
name|distances
init|=
literal|null
decl_stmt|;
DECL|field|precise
specifier|private
name|Precision
name|precise
init|=
literal|null
decl_stmt|;
comment|/**    * Provide a distance filter based from a center point with a radius    * in miles    * @param lat    * @param lng    * @param miles    * @param latField    * @param lngField    */
DECL|method|GeoHashDistanceFilter
specifier|public
name|GeoHashDistanceFilter
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lng
parameter_list|,
name|double
name|miles
parameter_list|,
name|String
name|geoHashField
parameter_list|)
block|{
name|distance
operator|=
name|miles
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lng
operator|=
name|lng
expr_stmt|;
name|this
operator|.
name|geoHashField
operator|=
name|geoHashField
expr_stmt|;
block|}
DECL|method|getDistances
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
name|getDistances
parameter_list|()
block|{
return|return
name|distances
return|;
block|}
DECL|method|getDistance
specifier|public
name|Double
name|getDistance
parameter_list|(
name|int
name|docid
parameter_list|)
block|{
return|return
name|distances
operator|.
name|get
argument_list|(
name|docid
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* Create a BitSet to store the result */
name|int
name|maxdocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|maxdocs
argument_list|)
decl_stmt|;
name|setPrecision
argument_list|(
name|maxdocs
argument_list|)
expr_stmt|;
comment|// create an intermediate cache to avoid recomputing
comment|//   distances for the same point
comment|//   TODO: Why is this a WeakHashMap?
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|cdistance
init|=
operator|new
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|(
name|maxdocs
argument_list|)
decl_stmt|;
name|String
index|[]
name|geoHashCache
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStrings
argument_list|(
name|reader
argument_list|,
name|geoHashField
argument_list|)
decl_stmt|;
comment|/* store calculated distances for reuse by other components */
name|distances
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
argument_list|(
name|maxdocs
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
name|maxdocs
condition|;
name|i
operator|++
control|)
block|{
name|String
name|geoHash
init|=
name|geoHashCache
index|[
name|i
index|]
decl_stmt|;
name|double
index|[]
name|coords
init|=
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geoHash
argument_list|)
decl_stmt|;
name|double
name|x
init|=
name|coords
index|[
literal|0
index|]
decl_stmt|;
name|double
name|y
init|=
name|coords
index|[
literal|1
index|]
decl_stmt|;
comment|// round off lat / longs if necessary
comment|//      x = DistanceHandler.getPrecision(x, precise);
comment|//      y = DistanceHandler.getPrecision(y, precise);
name|Double
name|cachedDistance
init|=
name|cdistance
operator|.
name|get
argument_list|(
name|geoHash
argument_list|)
decl_stmt|;
name|double
name|d
decl_stmt|;
if|if
condition|(
name|cachedDistance
operator|!=
literal|null
condition|)
block|{
name|d
operator|=
name|cachedDistance
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|d
operator|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
expr_stmt|;
name|cdistance
operator|.
name|put
argument_list|(
name|geoHash
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|distances
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
name|d
operator|<
name|distance
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bits
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|BitSet
name|bits
parameter_list|)
throws|throws
name|Exception
block|{
comment|/* Create a BitSet to store the result */
name|int
name|size
init|=
name|bits
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|BitSet
name|result
init|=
operator|new
name|BitSet
argument_list|(
name|size
argument_list|)
decl_stmt|;
comment|/* create an intermediate cache to avoid recomputing          distances for the same point  */
name|HashMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|cdistance
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|(
name|size
argument_list|)
decl_stmt|;
comment|/* store calculated distances for reuse by other components */
name|distances
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
index|[]
name|geoHashCache
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getStrings
argument_list|(
name|reader
argument_list|,
name|geoHashField
argument_list|)
decl_stmt|;
comment|/* loop over all set bits (hits from the boundary box filters) */
name|int
name|i
init|=
name|bits
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
while|while
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
comment|// if we have a completed
comment|// filter chain, lat / lngs can be retrived from
comment|// memory rather than document base.
name|String
name|geoHash
init|=
name|geoHashCache
index|[
name|i
index|]
decl_stmt|;
name|double
index|[]
name|coords
init|=
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geoHash
argument_list|)
decl_stmt|;
name|double
name|x
init|=
name|coords
index|[
literal|0
index|]
decl_stmt|;
name|double
name|y
init|=
name|coords
index|[
literal|1
index|]
decl_stmt|;
comment|// round off lat / longs if necessary
comment|//      x = DistanceHandler.getPrecision(x, precise);
comment|//      y = DistanceHandler.getPrecision(y, precise);
name|Double
name|cachedDistance
init|=
name|cdistance
operator|.
name|get
argument_list|(
name|geoHash
argument_list|)
decl_stmt|;
name|double
name|d
decl_stmt|;
if|if
condition|(
name|cachedDistance
operator|!=
literal|null
condition|)
block|{
name|d
operator|=
name|cachedDistance
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|d
operator|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
expr_stmt|;
comment|//d = DistanceUtils.getLLMDistance(lat, lng, x, y);
name|cdistance
operator|.
name|put
argument_list|(
name|geoHash
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|distances
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
name|d
operator|<
name|distance
condition|)
block|{
name|result
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|i
operator|=
name|bits
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|log
operator|.
name|fine
argument_list|(
literal|"Time taken : "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|", results : "
operator|+
name|distances
operator|.
name|size
argument_list|()
operator|+
literal|", cached : "
operator|+
name|cdistance
operator|.
name|size
argument_list|()
operator|+
literal|", incoming size: "
operator|+
name|size
argument_list|)
expr_stmt|;
name|cdistance
operator|=
literal|null
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
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
operator|!
operator|(
name|o
operator|instanceof
name|GeoHashDistanceFilter
operator|)
condition|)
return|return
literal|false
return|;
name|GeoHashDistanceFilter
name|other
init|=
operator|(
name|GeoHashDistanceFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|distance
operator|!=
name|other
operator|.
name|distance
operator|||
name|this
operator|.
name|lat
operator|!=
name|other
operator|.
name|lat
operator|||
name|this
operator|.
name|lng
operator|!=
name|other
operator|.
name|lng
operator|||
operator|!
name|this
operator|.
name|geoHashField
operator|.
name|equals
argument_list|(
name|other
operator|.
name|geoHashField
argument_list|)
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
comment|/** Returns a hash code value for this object.*/
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
operator|new
name|Double
argument_list|(
name|distance
argument_list|)
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|new
name|Double
argument_list|(
name|lat
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|^=
operator|new
name|Double
argument_list|(
name|lng
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|^=
name|geoHashField
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
DECL|method|setPrecision
specifier|private
name|void
name|setPrecision
parameter_list|(
name|int
name|maxDocs
parameter_list|)
block|{
name|precise
operator|=
name|Precision
operator|.
name|EXACT
expr_stmt|;
if|if
condition|(
name|maxDocs
operator|>
literal|1000
operator|&&
name|distance
operator|>
literal|10
condition|)
block|{
name|precise
operator|=
name|Precision
operator|.
name|TWENTYFEET
expr_stmt|;
block|}
if|if
condition|(
name|maxDocs
operator|>
literal|10000
operator|&&
name|distance
operator|>
literal|10
condition|)
block|{
name|precise
operator|=
name|Precision
operator|.
name|TWOHUNDREDFEET
expr_stmt|;
block|}
block|}
DECL|method|setDistances
specifier|public
name|void
name|setDistances
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
name|distances
parameter_list|)
block|{
name|this
operator|.
name|distances
operator|=
name|distances
expr_stmt|;
block|}
block|}
end_class
end_unit
