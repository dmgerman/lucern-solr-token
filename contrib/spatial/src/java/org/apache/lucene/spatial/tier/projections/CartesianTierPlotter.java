begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier.projections
package|package
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
name|projections
package|;
end_package
begin_comment
comment|/**  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment
begin_class
DECL|class|CartesianTierPlotter
specifier|public
class|class
name|CartesianTierPlotter
block|{
DECL|field|DEFALT_FIELD_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DEFALT_FIELD_PREFIX
init|=
literal|"_tier_"
decl_stmt|;
DECL|field|tierLevel
specifier|final
name|int
name|tierLevel
decl_stmt|;
DECL|field|tierLength
name|int
name|tierLength
decl_stmt|;
DECL|field|tierBoxes
name|int
name|tierBoxes
decl_stmt|;
DECL|field|tierVerticalPosDivider
name|int
name|tierVerticalPosDivider
decl_stmt|;
DECL|field|projector
specifier|final
name|IProjector
name|projector
decl_stmt|;
DECL|field|fieldPrefix
specifier|final
name|String
name|fieldPrefix
decl_stmt|;
DECL|field|idd
name|Double
name|idd
init|=
operator|new
name|Double
argument_list|(
literal|180
argument_list|)
decl_stmt|;
DECL|method|CartesianTierPlotter
specifier|public
name|CartesianTierPlotter
parameter_list|(
name|int
name|tierLevel
parameter_list|,
name|IProjector
name|projector
parameter_list|,
name|String
name|fieldPrefix
parameter_list|)
block|{
name|this
operator|.
name|tierLevel
operator|=
name|tierLevel
expr_stmt|;
name|this
operator|.
name|projector
operator|=
name|projector
expr_stmt|;
name|this
operator|.
name|fieldPrefix
operator|=
name|fieldPrefix
expr_stmt|;
name|setTierLength
argument_list|()
expr_stmt|;
name|setTierBoxes
argument_list|()
expr_stmt|;
name|setTierVerticalPosDivider
argument_list|()
expr_stmt|;
block|}
DECL|method|setTierLength
specifier|private
name|void
name|setTierLength
parameter_list|()
block|{
name|this
operator|.
name|tierLength
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|this
operator|.
name|tierLevel
argument_list|)
expr_stmt|;
block|}
DECL|method|setTierBoxes
specifier|private
name|void
name|setTierBoxes
parameter_list|()
block|{
name|this
operator|.
name|tierBoxes
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
name|this
operator|.
name|tierLength
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get nearest max power of 10 greater than    * the tierlen    * e.g    * tierId of 13 has tierLen 8192    * nearest max power of 10 greater than tierLen     * would be 10,000    */
DECL|method|setTierVerticalPosDivider
specifier|private
name|void
name|setTierVerticalPosDivider
parameter_list|()
block|{
comment|// ceiling of log base 10 of tierLen
name|tierVerticalPosDivider
operator|=
operator|new
name|Double
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|Math
operator|.
name|log10
argument_list|(
operator|new
name|Integer
argument_list|(
name|this
operator|.
name|tierLength
argument_list|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
comment|//
name|tierVerticalPosDivider
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|10
argument_list|,
name|tierVerticalPosDivider
argument_list|)
expr_stmt|;
block|}
DECL|method|getTierVerticalPosDivider
specifier|public
name|double
name|getTierVerticalPosDivider
parameter_list|()
block|{
return|return
name|tierVerticalPosDivider
return|;
block|}
comment|/**    * TierBoxId is latitude box id + longitude box id    * where latitude box id, and longitude box id are transposed in to position    * coordinates.    *     * @param latitude    * @param longitude    */
DECL|method|getTierBoxId
specifier|public
name|double
name|getTierBoxId
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
name|double
index|[]
name|coords
init|=
name|projector
operator|.
name|coords
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
decl_stmt|;
name|double
name|id
init|=
name|getBoxId
argument_list|(
name|coords
index|[
literal|0
index|]
argument_list|)
operator|+
operator|(
name|getBoxId
argument_list|(
name|coords
index|[
literal|1
index|]
argument_list|)
operator|/
name|tierVerticalPosDivider
operator|)
decl_stmt|;
return|return
name|id
return|;
block|}
DECL|method|getBoxId
specifier|private
name|double
name|getBoxId
parameter_list|(
name|double
name|coord
parameter_list|)
block|{
return|return
name|Math
operator|.
name|floor
argument_list|(
name|coord
operator|/
operator|(
name|idd
operator|/
name|this
operator|.
name|tierLength
operator|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|getBoxId
specifier|private
name|double
name|getBoxId
parameter_list|(
name|double
name|coord
parameter_list|,
name|int
name|tierLen
parameter_list|)
block|{
return|return
name|Math
operator|.
name|floor
argument_list|(
name|coord
operator|/
operator|(
name|idd
operator|/
name|tierLen
operator|)
argument_list|)
return|;
block|}
comment|/**    * get the string name representing current tier    * _localTier&lt;tiedId&gt;    */
DECL|method|getTierFieldName
specifier|public
name|String
name|getTierFieldName
parameter_list|()
block|{
return|return
name|fieldPrefix
operator|+
name|this
operator|.
name|tierLevel
return|;
block|}
comment|/**    * get the string name representing tierId    * _localTier&lt;tierId&gt;    * @param tierId    */
DECL|method|getTierFieldName
specifier|public
name|String
name|getTierFieldName
parameter_list|(
name|int
name|tierId
parameter_list|)
block|{
return|return
name|fieldPrefix
operator|+
name|tierId
return|;
block|}
comment|/**    * Find the tier with the best fit for a bounding box    * Best fit is defined as the ceiling of    *  log2 (circumference of earth / distance)     *  distance is defined as the smallest box fitting    *  the corner between a radius and a bounding box.    *      *  Distances less than a mile return 15, finer granularity is    *  in accurate    */
DECL|method|bestFit
specifier|public
name|int
name|bestFit
parameter_list|(
name|double
name|miles
parameter_list|)
block|{
comment|//28,892 a rough circumference of the earth
name|int
name|circ
init|=
literal|28892
decl_stmt|;
name|double
name|r
init|=
name|miles
operator|/
literal|2.0
decl_stmt|;
name|double
name|corner
init|=
name|r
operator|-
name|Math
operator|.
name|sqrt
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
name|r
argument_list|,
literal|2
argument_list|)
operator|/
literal|2.0d
argument_list|)
decl_stmt|;
name|double
name|times
init|=
name|circ
operator|/
name|corner
decl_stmt|;
name|int
name|bestFit
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|log2
argument_list|(
name|times
argument_list|)
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|bestFit
operator|>
literal|15
condition|)
block|{
comment|// 15 is the granularity of about 1 mile
comment|// finer granularity isn't accurate with standard java math
return|return
literal|15
return|;
block|}
return|return
name|bestFit
return|;
block|}
comment|/**    * a log to the base 2 formula    *<code>Math.log(value) / Math.log(2)</code>    * @param value    */
DECL|method|log2
specifier|public
name|double
name|log2
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|Math
operator|.
name|log
argument_list|(
name|value
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
literal|2
argument_list|)
return|;
block|}
block|}
end_class
end_unit
