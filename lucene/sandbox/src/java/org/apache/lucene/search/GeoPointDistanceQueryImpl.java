begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|index
operator|.
name|TermsEnum
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
name|AttributeSource
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
name|GeoUtils
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
name|SloppyMath
import|;
end_import
begin_comment
comment|/** Package private implementation for the public facing GeoPointDistanceQuery delegate class.  *  *    @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointDistanceQueryImpl
specifier|final
class|class
name|GeoPointDistanceQueryImpl
extends|extends
name|GeoPointInBBoxQueryImpl
block|{
DECL|field|query
specifier|private
specifier|final
name|GeoPointDistanceQuery
name|query
decl_stmt|;
DECL|method|GeoPointDistanceQueryImpl
name|GeoPointDistanceQueryImpl
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|GeoPointDistanceQuery
name|q
parameter_list|,
specifier|final
name|GeoBoundingBox
name|bbox
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|bbox
operator|.
name|minLon
argument_list|,
name|bbox
operator|.
name|minLat
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|,
name|bbox
operator|.
name|maxLat
argument_list|)
expr_stmt|;
name|query
operator|=
name|q
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
specifier|final
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|GeoPointRadiusTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|,
name|this
operator|.
name|minLon
argument_list|,
name|this
operator|.
name|minLat
argument_list|,
name|this
operator|.
name|maxLon
argument_list|,
name|this
operator|.
name|maxLat
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setRewriteMethod
specifier|public
name|void
name|setRewriteMethod
parameter_list|(
name|RewriteMethod
name|method
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot change rewrite method"
argument_list|)
throw|;
block|}
DECL|class|GeoPointRadiusTermsEnum
specifier|private
specifier|final
class|class
name|GeoPointRadiusTermsEnum
extends|extends
name|GeoPointTermsEnum
block|{
DECL|method|GeoPointRadiusTermsEnum
name|GeoPointRadiusTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|,
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cellCrosses
specifier|protected
name|boolean
name|cellCrosses
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoUtils
operator|.
name|rectCrossesCircle
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|,
name|query
operator|.
name|centerLon
argument_list|,
name|query
operator|.
name|centerLat
argument_list|,
name|query
operator|.
name|radius
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cellWithin
specifier|protected
name|boolean
name|cellWithin
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoUtils
operator|.
name|rectWithinCircle
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|,
name|query
operator|.
name|centerLon
argument_list|,
name|query
operator|.
name|centerLat
argument_list|,
name|query
operator|.
name|radius
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cellIntersectsShape
specifier|protected
name|boolean
name|cellIntersectsShape
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
operator|(
name|cellContains
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
operator|||
name|cellWithin
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
operator|||
name|cellCrosses
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
operator|)
return|;
block|}
comment|/**      * The two-phase query approach. The parent {@link org.apache.lucene.search.GeoPointTermsEnum} class matches      * encoded terms that fall within the minimum bounding box of the point-radius circle. Those documents that pass      * the initial bounding box filter are then post filter compared to the provided distance using the      * {@link org.apache.lucene.util.SloppyMath#haversin} method.      */
annotation|@
name|Override
DECL|method|postFilter
specifier|protected
name|boolean
name|postFilter
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|)
block|{
return|return
operator|(
name|SloppyMath
operator|.
name|haversin
argument_list|(
name|query
operator|.
name|centerLat
argument_list|,
name|query
operator|.
name|centerLon
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
operator|*
literal|1000.0
operator|<=
name|query
operator|.
name|radius
operator|)
return|;
block|}
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
operator|!
operator|(
name|o
operator|instanceof
name|GeoPointDistanceQueryImpl
operator|)
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
name|GeoPointDistanceQueryImpl
name|that
init|=
operator|(
name|GeoPointDistanceQueryImpl
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|query
operator|.
name|equals
argument_list|(
name|that
operator|.
name|query
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
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
name|query
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
