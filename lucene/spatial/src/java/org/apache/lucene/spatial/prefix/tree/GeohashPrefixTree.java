begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.prefix.tree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|tree
package|;
end_package
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|io
operator|.
name|GeohashUtils
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
name|List
import|;
end_import
begin_comment
comment|/**  * A SpatialPrefixGrid based on Geohashes.  Uses {@link GeohashUtils} to do all the geohash work.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeohashPrefixTree
specifier|public
class|class
name|GeohashPrefixTree
extends|extends
name|SpatialPrefixTree
block|{
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|SpatialPrefixTreeFactory
block|{
annotation|@
name|Override
DECL|method|getLevelForDistance
specifier|protected
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|degrees
parameter_list|)
block|{
name|GeohashPrefixTree
name|grid
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|degrees
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newSPT
specifier|protected
name|SpatialPrefixTree
name|newSPT
parameter_list|()
block|{
return|return
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLevels
operator|!=
literal|null
condition|?
name|maxLevels
else|:
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|GeohashPrefixTree
specifier|public
name|GeohashPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|int
name|maxLevels
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|maxLevels
argument_list|)
expr_stmt|;
name|Rectangle
name|bounds
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|getMinX
argument_list|()
operator|!=
operator|-
literal|180
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Geohash only supports lat-lon world bounds. Got "
operator|+
name|bounds
argument_list|)
throw|;
name|int
name|MAXP
init|=
name|getMaxLevelsPossible
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxLevels
operator|<=
literal|0
operator|||
name|maxLevels
operator|>
name|MAXP
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxLen must be [1-"
operator|+
name|MAXP
operator|+
literal|"] but got "
operator|+
name|maxLevels
argument_list|)
throw|;
block|}
comment|/** Any more than this and there's no point (double lat& lon are the same). */
DECL|method|getMaxLevelsPossible
specifier|public
specifier|static
name|int
name|getMaxLevelsPossible
parameter_list|()
block|{
return|return
name|GeohashUtils
operator|.
name|MAX_PRECISION
return|;
block|}
annotation|@
name|Override
DECL|method|getLevelForDistance
specifier|public
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|dist
parameter_list|)
block|{
specifier|final
name|int
name|level
init|=
name|GeohashUtils
operator|.
name|lookupHashLenForWidthHeight
argument_list|(
name|dist
argument_list|,
name|dist
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|level
argument_list|,
name|maxLevels
argument_list|)
argument_list|,
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNode
specifier|public
name|Node
name|getNode
parameter_list|(
name|Point
name|p
parameter_list|,
name|int
name|level
parameter_list|)
block|{
return|return
operator|new
name|GhCell
argument_list|(
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|p
operator|.
name|getY
argument_list|()
argument_list|,
name|p
operator|.
name|getX
argument_list|()
argument_list|,
name|level
argument_list|)
argument_list|)
return|;
comment|//args are lat,lon (y,x)
block|}
annotation|@
name|Override
DECL|method|getNode
specifier|public
name|Node
name|getNode
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
operator|new
name|GhCell
argument_list|(
name|token
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNode
specifier|public
name|Node
name|getNode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
operator|new
name|GhCell
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNodes
specifier|public
name|List
argument_list|<
name|Node
argument_list|>
name|getNodes
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|boolean
name|inclParents
parameter_list|)
block|{
return|return
name|shape
operator|instanceof
name|Point
condition|?
name|super
operator|.
name|getNodesAltPoint
argument_list|(
operator|(
name|Point
operator|)
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|inclParents
argument_list|)
else|:
name|super
operator|.
name|getNodes
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|inclParents
argument_list|)
return|;
block|}
DECL|class|GhCell
class|class
name|GhCell
extends|extends
name|Node
block|{
DECL|method|GhCell
name|GhCell
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|super
argument_list|(
name|GeohashPrefixTree
operator|.
name|this
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
DECL|method|GhCell
name|GhCell
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|super
argument_list|(
name|GeohashPrefixTree
operator|.
name|this
argument_list|,
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|super
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|shape
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubCells
specifier|public
name|Collection
argument_list|<
name|Node
argument_list|>
name|getSubCells
parameter_list|()
block|{
name|String
index|[]
name|hashes
init|=
name|GeohashUtils
operator|.
name|getSubGeohashes
argument_list|(
name|getGeohash
argument_list|()
argument_list|)
decl_stmt|;
comment|//sorted
name|List
argument_list|<
name|Node
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|hashes
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|hash
range|:
name|hashes
control|)
block|{
name|cells
operator|.
name|add
argument_list|(
operator|new
name|GhCell
argument_list|(
name|hash
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cells
return|;
block|}
annotation|@
name|Override
DECL|method|getSubCellsSize
specifier|public
name|int
name|getSubCellsSize
parameter_list|()
block|{
return|return
literal|32
return|;
comment|//8x4
block|}
annotation|@
name|Override
DECL|method|getSubCell
specifier|public
name|Node
name|getSubCell
parameter_list|(
name|Point
name|p
parameter_list|)
block|{
return|return
name|GeohashPrefixTree
operator|.
name|this
operator|.
name|getNode
argument_list|(
name|p
argument_list|,
name|getLevel
argument_list|()
operator|+
literal|1
argument_list|)
return|;
comment|//not performant!
block|}
DECL|field|shape
specifier|private
name|Shape
name|shape
decl_stmt|;
comment|//cache
annotation|@
name|Override
DECL|method|getShape
specifier|public
name|Shape
name|getShape
parameter_list|()
block|{
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
name|shape
operator|=
name|GeohashUtils
operator|.
name|decodeBoundary
argument_list|(
name|getGeohash
argument_list|()
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
return|return
name|shape
return|;
block|}
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|Point
name|getCenter
parameter_list|()
block|{
return|return
name|GeohashUtils
operator|.
name|decode
argument_list|(
name|getGeohash
argument_list|()
argument_list|,
name|ctx
argument_list|)
return|;
block|}
DECL|method|getGeohash
specifier|private
name|String
name|getGeohash
parameter_list|()
block|{
return|return
name|getTokenString
argument_list|()
return|;
block|}
block|}
comment|//class GhCell
block|}
end_class
end_unit
