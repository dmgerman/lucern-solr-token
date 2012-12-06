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
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Hash
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
comment|//
end_comment
begin_comment
comment|// user!uniqueid
end_comment
begin_comment
comment|// user/4!uniqueid
end_comment
begin_comment
comment|//
end_comment
begin_class
DECL|class|CompositeIdRouter
specifier|public
class|class
name|CompositeIdRouter
extends|extends
name|HashBasedRouter
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"compositeId"
decl_stmt|;
DECL|field|separator
specifier|private
name|int
name|separator
init|=
literal|'!'
decl_stmt|;
comment|// separator used to optionally specify number of bits to allocate toward first part.
DECL|field|bitsSepartor
specifier|private
name|int
name|bitsSepartor
init|=
literal|'/'
decl_stmt|;
DECL|field|bits
specifier|private
name|int
name|bits
init|=
literal|16
decl_stmt|;
DECL|field|mask1
specifier|private
name|int
name|mask1
init|=
literal|0xffff0000
decl_stmt|;
DECL|field|mask2
specifier|private
name|int
name|mask2
init|=
literal|0x0000ffff
decl_stmt|;
DECL|method|setBits
specifier|protected
name|void
name|setBits
parameter_list|(
name|int
name|bits
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|mask1
operator|=
operator|-
literal|1
operator|<<
operator|(
literal|32
operator|-
name|bits
operator|)
expr_stmt|;
name|mask2
operator|=
operator|-
literal|1
operator|>>>
name|bits
expr_stmt|;
block|}
DECL|method|getBits
specifier|protected
name|int
name|getBits
parameter_list|(
name|String
name|firstPart
parameter_list|,
name|int
name|commaIdx
parameter_list|)
block|{
name|int
name|v
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|commaIdx
operator|+
literal|1
init|;
name|idx
operator|<
name|firstPart
operator|.
name|length
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|char
name|ch
init|=
name|firstPart
operator|.
name|charAt
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
argument_list|<
literal|'0'
operator|||
name|ch
argument_list|>
literal|'9'
condition|)
return|return
operator|-
literal|1
return|;
name|v
operator|=
name|v
operator|*
literal|10
operator|+
operator|(
name|ch
operator|-
literal|'0'
operator|)
expr_stmt|;
block|}
return|return
name|v
operator|>
literal|32
condition|?
operator|-
literal|1
else|:
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|sliceHash
specifier|protected
name|int
name|sliceHash
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|doc
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|int
name|idx
init|=
name|id
operator|.
name|indexOf
argument_list|(
name|separator
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
name|int
name|m1
init|=
name|mask1
decl_stmt|;
name|int
name|m2
init|=
name|mask2
decl_stmt|;
name|String
name|part1
init|=
name|id
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|int
name|commaIdx
init|=
name|part1
operator|.
name|indexOf
argument_list|(
name|bitsSepartor
argument_list|)
decl_stmt|;
if|if
condition|(
name|commaIdx
operator|>
literal|0
condition|)
block|{
name|int
name|firstBits
init|=
name|getBits
argument_list|(
name|part1
argument_list|,
name|commaIdx
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstBits
operator|>=
literal|0
condition|)
block|{
name|m1
operator|=
operator|-
literal|1
operator|<<
operator|(
literal|32
operator|-
name|firstBits
operator|)
expr_stmt|;
name|m2
operator|=
operator|-
literal|1
operator|>>>
name|firstBits
expr_stmt|;
name|part1
operator|=
name|part1
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|commaIdx
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|part2
init|=
name|id
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|hash1
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|part1
argument_list|,
literal|0
argument_list|,
name|part1
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|part2
argument_list|,
literal|0
argument_list|,
name|part2
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|(
name|hash1
operator|&
name|m1
operator|)
operator||
operator|(
name|hash2
operator|&
name|m2
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSearchSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSearchSlices
parameter_list|(
name|String
name|shardKey
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
if|if
condition|(
name|shardKey
operator|==
literal|null
condition|)
block|{
comment|// search across whole collection
comment|// TODO: this may need modification in the future when shard splitting could cause an overlap
return|return
name|collection
operator|.
name|getSlices
argument_list|()
return|;
block|}
name|String
name|id
init|=
name|shardKey
decl_stmt|;
name|int
name|idx
init|=
name|shardKey
operator|.
name|indexOf
argument_list|(
name|separator
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
comment|// shardKey is a simple id, so don't do a range
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|hashToSlice
argument_list|(
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|collection
argument_list|)
argument_list|)
return|;
block|}
name|int
name|m1
init|=
name|mask1
decl_stmt|;
name|int
name|m2
init|=
name|mask2
decl_stmt|;
name|String
name|part1
init|=
name|id
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|int
name|bitsSepIdx
init|=
name|part1
operator|.
name|indexOf
argument_list|(
name|bitsSepartor
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitsSepIdx
operator|>
literal|0
condition|)
block|{
name|int
name|firstBits
init|=
name|getBits
argument_list|(
name|part1
argument_list|,
name|bitsSepIdx
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstBits
operator|>=
literal|0
condition|)
block|{
name|m1
operator|=
operator|-
literal|1
operator|<<
operator|(
literal|32
operator|-
name|firstBits
operator|)
expr_stmt|;
name|m2
operator|=
operator|-
literal|1
operator|>>>
name|firstBits
expr_stmt|;
name|part1
operator|=
name|part1
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|bitsSepIdx
argument_list|)
expr_stmt|;
block|}
block|}
comment|//  If the upper bits are 0xF0000000, the range we want to cover is
comment|//  0xF0000000 0xFfffffff
name|int
name|hash1
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|part1
argument_list|,
literal|0
argument_list|,
name|part1
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|upperBits
init|=
name|hash1
operator|&
name|m1
decl_stmt|;
name|int
name|lowerBound
init|=
name|upperBits
decl_stmt|;
name|int
name|upperBound
init|=
name|upperBits
operator||
name|m2
decl_stmt|;
name|Range
name|completeRange
init|=
operator|new
name|Range
argument_list|(
name|lowerBound
argument_list|,
name|upperBound
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|Range
name|range
init|=
name|slice
operator|.
name|getRange
argument_list|()
decl_stmt|;
if|if
condition|(
name|range
operator|!=
literal|null
operator|&&
name|range
operator|.
name|overlaps
argument_list|(
name|completeRange
argument_list|)
condition|)
block|{
name|slices
operator|.
name|add
argument_list|(
name|slice
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|slices
return|;
block|}
block|}
end_class
end_unit
