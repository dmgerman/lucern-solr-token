begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/*  * Forked from https://github.com/codahale/metrics  */
end_comment
begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLongArray
import|;
end_import
begin_comment
comment|/**  * A random sample of a stream of {@code long}s. Uses Vitter's Algorithm R to produce a  * statistically representative sample.  *  * @see<a href="http://www.cs.umd.edu/~samir/498/vitter.pdf">Random Sampling with a Reservoir</a>  */
end_comment
begin_class
DECL|class|UniformSample
specifier|public
class|class
name|UniformSample
implements|implements
name|Sample
block|{
DECL|field|BITS_PER_LONG
specifier|private
specifier|static
specifier|final
name|int
name|BITS_PER_LONG
init|=
literal|63
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|AtomicLong
name|count
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|AtomicLongArray
name|values
decl_stmt|;
comment|//TODO: Maybe replace with a Mersenne twister for better distribution
DECL|field|random
specifier|private
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|/**    * Creates a new {@link UniformSample}.    *    * @param reservoirSize the number of samples to keep in the sampling reservoir    */
DECL|method|UniformSample
specifier|public
name|UniformSample
parameter_list|(
name|int
name|reservoirSize
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
operator|new
name|AtomicLongArray
argument_list|(
name|reservoirSize
argument_list|)
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|values
operator|.
name|set
argument_list|(
name|i
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|count
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
specifier|final
name|long
name|c
init|=
name|count
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|>
name|values
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|values
operator|.
name|length
argument_list|()
return|;
block|}
return|return
operator|(
name|int
operator|)
name|c
return|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|long
name|value
parameter_list|)
block|{
specifier|final
name|long
name|c
init|=
name|count
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|<=
name|values
operator|.
name|length
argument_list|()
condition|)
block|{
name|values
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|c
operator|-
literal|1
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|r
init|=
name|nextLong
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|<
name|values
operator|.
name|length
argument_list|()
condition|)
block|{
name|values
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|r
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get a pseudo-random long uniformly between 0 and n-1. Stolen from    * {@link java.util.Random#nextInt()}.    *    * @param n the bound    * @return a value select randomly from the range {@code [0..n)}.    */
DECL|method|nextLong
specifier|private
specifier|static
name|long
name|nextLong
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|long
name|bits
decl_stmt|,
name|val
decl_stmt|;
do|do
block|{
name|bits
operator|=
name|random
operator|.
name|nextLong
argument_list|()
operator|&
operator|(
operator|~
operator|(
literal|1L
operator|<<
name|BITS_PER_LONG
operator|)
operator|)
expr_stmt|;
name|val
operator|=
name|bits
operator|%
name|n
expr_stmt|;
block|}
do|while
condition|(
name|bits
operator|-
name|val
operator|+
operator|(
name|n
operator|-
literal|1
operator|)
operator|<
literal|0L
condition|)
do|;
return|return
name|val
return|;
block|}
annotation|@
name|Override
DECL|method|getSnapshot
specifier|public
name|Snapshot
name|getSnapshot
parameter_list|()
block|{
specifier|final
name|int
name|s
init|=
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|copy
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|s
argument_list|)
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
name|s
condition|;
name|i
operator|++
control|)
block|{
name|copy
operator|.
name|add
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Snapshot
argument_list|(
name|copy
argument_list|)
return|;
block|}
block|}
end_class
end_unit
