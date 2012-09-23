begin_unit
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|JSONWriter
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
name|Collections
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
comment|/**  * Class to partition int range into n ranges.  *  */
end_comment
begin_class
DECL|class|HashPartitioner
specifier|public
class|class
name|HashPartitioner
block|{
comment|// Hash ranges can't currently "wrap" - i.e. max must be greater or equal to min.
comment|// TODO: ranges may not be all contiguous in the future (either that or we will
comment|// need an extra class to model a collection of ranges)
DECL|class|Range
specifier|public
specifier|static
class|class
name|Range
implements|implements
name|JSONWriter
operator|.
name|Writable
block|{
DECL|field|min
specifier|public
name|int
name|min
decl_stmt|;
comment|// inclusive
DECL|field|max
specifier|public
name|int
name|max
decl_stmt|;
comment|// inclusive
DECL|method|Range
specifier|public
name|Range
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
assert|assert
name|min
operator|<=
name|max
assert|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
DECL|method|includes
specifier|public
name|boolean
name|includes
parameter_list|(
name|int
name|hash
parameter_list|)
block|{
return|return
name|hash
operator|>=
name|min
operator|&&
name|hash
operator|<=
name|max
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toHexString
argument_list|(
name|min
argument_list|)
operator|+
literal|'-'
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|max
argument_list|)
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
comment|// difficult numbers to hash... only the highest bits will tend to differ.
comment|// ranges will only overlap during a split, so we can just hash the lower range.
return|return
operator|(
name|min
operator|>>
literal|28
operator|)
operator|+
operator|(
name|min
operator|>>
literal|25
operator|)
operator|+
operator|(
name|min
operator|>>
literal|21
operator|)
operator|+
name|min
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Range
name|other
init|=
operator|(
name|Range
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|min
operator|==
name|other
operator|.
name|min
operator|&&
name|this
operator|.
name|max
operator|==
name|other
operator|.
name|max
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fromString
specifier|public
name|Range
name|fromString
parameter_list|(
name|String
name|range
parameter_list|)
block|{
name|int
name|middle
init|=
name|range
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
name|String
name|minS
init|=
name|range
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|middle
argument_list|)
decl_stmt|;
name|String
name|maxS
init|=
name|range
operator|.
name|substring
argument_list|(
name|middle
operator|+
literal|1
argument_list|)
decl_stmt|;
name|long
name|min
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|minS
argument_list|,
literal|16
argument_list|)
decl_stmt|;
comment|// use long to prevent the parsing routines from potentially worrying about overflow
name|long
name|max
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|maxS
argument_list|,
literal|16
argument_list|)
decl_stmt|;
return|return
operator|new
name|Range
argument_list|(
operator|(
name|int
operator|)
name|min
argument_list|,
operator|(
name|int
operator|)
name|max
argument_list|)
return|;
block|}
DECL|method|fullRange
specifier|public
name|Range
name|fullRange
parameter_list|()
block|{
return|return
operator|new
name|Range
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|partitionRange
specifier|public
name|List
argument_list|<
name|Range
argument_list|>
name|partitionRange
parameter_list|(
name|int
name|partitions
parameter_list|,
name|Range
name|range
parameter_list|)
block|{
return|return
name|partitionRange
argument_list|(
name|partitions
argument_list|,
name|range
operator|.
name|min
argument_list|,
name|range
operator|.
name|max
argument_list|)
return|;
block|}
comment|/**    * Returns the range for each partition    */
DECL|method|partitionRange
specifier|public
name|List
argument_list|<
name|Range
argument_list|>
name|partitionRange
parameter_list|(
name|int
name|partitions
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
assert|assert
name|max
operator|>=
name|min
assert|;
if|if
condition|(
name|partitions
operator|==
literal|0
condition|)
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
name|long
name|range
init|=
operator|(
name|long
operator|)
name|max
operator|-
operator|(
name|long
operator|)
name|min
decl_stmt|;
name|long
name|srange
init|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|range
operator|/
name|partitions
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<
name|Range
argument_list|>
argument_list|(
name|partitions
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|min
decl_stmt|;
name|long
name|end
init|=
name|start
decl_stmt|;
while|while
condition|(
name|end
operator|<
name|max
condition|)
block|{
name|end
operator|=
name|start
operator|+
name|srange
expr_stmt|;
comment|// make last range always end exactly on MAX_VALUE
if|if
condition|(
name|ranges
operator|.
name|size
argument_list|()
operator|==
name|partitions
operator|-
literal|1
condition|)
block|{
name|end
operator|=
name|max
expr_stmt|;
block|}
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
operator|(
name|int
operator|)
name|start
argument_list|,
operator|(
name|int
operator|)
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|end
operator|+
literal|1L
expr_stmt|;
block|}
return|return
name|ranges
return|;
block|}
block|}
end_class
end_unit
