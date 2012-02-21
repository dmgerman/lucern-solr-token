begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|prefix
package|;
end_package
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
name|base
operator|.
name|context
operator|.
name|SpatialContext
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
name|base
operator|.
name|distance
operator|.
name|DistanceUnits
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
name|base
operator|.
name|distance
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
name|base
operator|.
name|prefix
operator|.
name|geohash
operator|.
name|GeohashPrefixTree
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
name|base
operator|.
name|prefix
operator|.
name|quad
operator|.
name|QuadPrefixTree
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
begin_class
DECL|class|SpatialPrefixTreeFactory
specifier|public
specifier|abstract
class|class
name|SpatialPrefixTreeFactory
block|{
DECL|field|DEFAULT_GEO_MAX_DETAIL_KM
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_GEO_MAX_DETAIL_KM
init|=
literal|0.001
decl_stmt|;
comment|//1m
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|maxLevels
specifier|protected
name|Integer
name|maxLevels
decl_stmt|;
comment|/**    * The factory  is looked up via "prefixTree" in args, expecting "geohash" or "quad".    * If its neither of these, then "geohash" is chosen for a geo context, otherwise "quad" is chosen.    */
DECL|method|makeSPT
specifier|public
specifier|static
name|SpatialPrefixTree
name|makeSPT
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|SpatialPrefixTreeFactory
name|instance
decl_stmt|;
name|String
name|cname
init|=
name|args
operator|.
name|get
argument_list|(
literal|"prefixTree"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
name|cname
operator|=
name|ctx
operator|.
name|isGeo
argument_list|()
condition|?
literal|"geohash"
else|:
literal|"quad"
expr_stmt|;
if|if
condition|(
literal|"geohash"
operator|.
name|equalsIgnoreCase
argument_list|(
name|cname
argument_list|)
condition|)
name|instance
operator|=
operator|new
name|GeohashPrefixTree
operator|.
name|Factory
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
literal|"quad"
operator|.
name|equalsIgnoreCase
argument_list|(
name|cname
argument_list|)
condition|)
name|instance
operator|=
operator|new
name|QuadPrefixTree
operator|.
name|Factory
argument_list|()
expr_stmt|;
else|else
block|{
try|try
block|{
name|Class
name|c
init|=
name|classLoader
operator|.
name|loadClass
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|instance
operator|=
operator|(
name|SpatialPrefixTreeFactory
operator|)
name|c
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|instance
operator|.
name|init
argument_list|(
name|args
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
return|return
name|instance
operator|.
name|newSPT
argument_list|()
return|;
block|}
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|initMaxLevels
argument_list|()
expr_stmt|;
block|}
DECL|method|initMaxLevels
specifier|protected
name|void
name|initMaxLevels
parameter_list|()
block|{
name|String
name|mlStr
init|=
name|args
operator|.
name|get
argument_list|(
literal|"maxLevels"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mlStr
operator|!=
literal|null
condition|)
block|{
name|maxLevels
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|mlStr
argument_list|)
expr_stmt|;
return|return;
block|}
name|double
name|degrees
decl_stmt|;
name|String
name|maxDetailDistStr
init|=
name|args
operator|.
name|get
argument_list|(
literal|"maxDetailDist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxDetailDistStr
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|ctx
operator|.
name|isGeo
argument_list|()
condition|)
block|{
return|return;
comment|//let default to max
block|}
name|degrees
operator|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
name|DEFAULT_GEO_MAX_DETAIL_KM
argument_list|,
name|DistanceUnits
operator|.
name|KILOMETERS
operator|.
name|earthRadius
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|degrees
operator|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|maxDetailDistStr
argument_list|)
argument_list|,
name|ctx
operator|.
name|getUnits
argument_list|()
operator|.
name|earthRadius
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|maxLevels
operator|=
name|getLevelForDistance
argument_list|(
name|degrees
argument_list|)
operator|+
literal|1
expr_stmt|;
comment|//returns 1 greater
block|}
comment|/** Calls {@link SpatialPrefixTree#getLevelForDistance(double)}. */
DECL|method|getLevelForDistance
specifier|protected
specifier|abstract
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|degrees
parameter_list|)
function_decl|;
DECL|method|newSPT
specifier|protected
specifier|abstract
name|SpatialPrefixTree
name|newSPT
parameter_list|()
function_decl|;
block|}
end_class
end_unit
