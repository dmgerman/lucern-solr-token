begin_unit
begin_package
DECL|package|org.apache.lucene.facet.range
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|range
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
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
name|Map
import|;
end_import
begin_comment
comment|/** Counts how many times each range was seen;  *  per-hit it's just a binary search ({@link #add})  *  against the elementary intervals, and in the end we  *  rollup back to the original ranges. */
end_comment
begin_class
DECL|class|LongRangeCounter
specifier|final
class|class
name|LongRangeCounter
block|{
DECL|field|root
specifier|final
name|LongRangeNode
name|root
decl_stmt|;
DECL|field|boundaries
specifier|final
name|long
index|[]
name|boundaries
decl_stmt|;
DECL|field|leafCounts
specifier|final
name|int
index|[]
name|leafCounts
decl_stmt|;
comment|// Used during rollup
DECL|field|leafUpto
specifier|private
name|int
name|leafUpto
decl_stmt|;
DECL|field|missingCount
specifier|private
name|int
name|missingCount
decl_stmt|;
DECL|method|LongRangeCounter
specifier|public
name|LongRangeCounter
parameter_list|(
name|LongRange
index|[]
name|ranges
parameter_list|)
block|{
comment|// Maps all range inclusive endpoints to int flags; 1
comment|// = start of interval, 2 = end of interval.  We need to
comment|// track the start vs end case separately because if a
comment|// given point is both, then it must be its own
comment|// elementary interval:
name|Map
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|endsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|endsMap
operator|.
name|put
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|endsMap
operator|.
name|put
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|LongRange
name|range
range|:
name|ranges
control|)
block|{
name|Integer
name|cur
init|=
name|endsMap
operator|.
name|get
argument_list|(
name|range
operator|.
name|minIncl
argument_list|)
decl_stmt|;
if|if
condition|(
name|cur
operator|==
literal|null
condition|)
block|{
name|endsMap
operator|.
name|put
argument_list|(
name|range
operator|.
name|minIncl
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|endsMap
operator|.
name|put
argument_list|(
name|range
operator|.
name|minIncl
argument_list|,
name|cur
operator|.
name|intValue
argument_list|()
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
name|cur
operator|=
name|endsMap
operator|.
name|get
argument_list|(
name|range
operator|.
name|maxIncl
argument_list|)
expr_stmt|;
if|if
condition|(
name|cur
operator|==
literal|null
condition|)
block|{
name|endsMap
operator|.
name|put
argument_list|(
name|range
operator|.
name|maxIncl
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|endsMap
operator|.
name|put
argument_list|(
name|range
operator|.
name|maxIncl
argument_list|,
name|cur
operator|.
name|intValue
argument_list|()
operator||
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|endsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|endsMap
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|endsList
argument_list|)
expr_stmt|;
comment|// Build elementaryIntervals (a 1D Venn diagram):
name|List
argument_list|<
name|InclusiveRange
argument_list|>
name|elementaryIntervals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|upto0
init|=
literal|1
decl_stmt|;
name|long
name|v
init|=
name|endsList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|prev
decl_stmt|;
if|if
condition|(
name|endsMap
operator|.
name|get
argument_list|(
name|v
argument_list|)
operator|==
literal|3
condition|)
block|{
name|elementaryIntervals
operator|.
name|add
argument_list|(
operator|new
name|InclusiveRange
argument_list|(
name|v
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|prev
operator|=
name|v
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|prev
operator|=
name|v
expr_stmt|;
block|}
while|while
condition|(
name|upto0
operator|<
name|endsList
operator|.
name|size
argument_list|()
condition|)
block|{
name|v
operator|=
name|endsList
operator|.
name|get
argument_list|(
name|upto0
argument_list|)
expr_stmt|;
name|int
name|flags
init|=
name|endsMap
operator|.
name|get
argument_list|(
name|v
argument_list|)
decl_stmt|;
comment|//System.out.println("  v=" + v + " flags=" + flags);
if|if
condition|(
name|flags
operator|==
literal|3
condition|)
block|{
comment|// This point is both an end and a start; we need to
comment|// separate it:
if|if
condition|(
name|v
operator|>
name|prev
condition|)
block|{
name|elementaryIntervals
operator|.
name|add
argument_list|(
operator|new
name|InclusiveRange
argument_list|(
name|prev
argument_list|,
name|v
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|elementaryIntervals
operator|.
name|add
argument_list|(
operator|new
name|InclusiveRange
argument_list|(
name|v
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|prev
operator|=
name|v
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|flags
operator|==
literal|1
condition|)
block|{
comment|// This point is only the start of an interval;
comment|// attach it to next interval:
if|if
condition|(
name|v
operator|>
name|prev
condition|)
block|{
name|elementaryIntervals
operator|.
name|add
argument_list|(
operator|new
name|InclusiveRange
argument_list|(
name|prev
argument_list|,
name|v
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|prev
operator|=
name|v
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|flags
operator|==
literal|2
assert|;
comment|// This point is only the end of an interval; attach
comment|// it to last interval:
name|elementaryIntervals
operator|.
name|add
argument_list|(
operator|new
name|InclusiveRange
argument_list|(
name|prev
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|prev
operator|=
name|v
operator|+
literal|1
expr_stmt|;
block|}
comment|//System.out.println("    ints=" + elementaryIntervals);
name|upto0
operator|++
expr_stmt|;
block|}
comment|// Build binary tree on top of intervals:
name|root
operator|=
name|split
argument_list|(
literal|0
argument_list|,
name|elementaryIntervals
operator|.
name|size
argument_list|()
argument_list|,
name|elementaryIntervals
argument_list|)
expr_stmt|;
comment|// Set outputs, so we know which range to output for
comment|// each node in the tree:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ranges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|root
operator|.
name|addOutputs
argument_list|(
name|i
argument_list|,
name|ranges
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Set boundaries (ends of each elementary interval):
name|boundaries
operator|=
operator|new
name|long
index|[
name|elementaryIntervals
operator|.
name|size
argument_list|()
index|]
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
name|boundaries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boundaries
index|[
name|i
index|]
operator|=
name|elementaryIntervals
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|end
expr_stmt|;
block|}
name|leafCounts
operator|=
operator|new
name|int
index|[
name|boundaries
operator|.
name|length
index|]
expr_stmt|;
comment|//System.out.println("ranges: " + Arrays.toString(ranges));
comment|//System.out.println("intervals: " + elementaryIntervals);
comment|//System.out.println("boundaries: " + Arrays.toString(boundaries));
comment|//System.out.println("root:\n" + root);
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|v
parameter_list|)
block|{
comment|//System.out.println("add v=" + v);
comment|// NOTE: this works too, but it's ~6% slower on a simple
comment|// test with a high-freq TermQuery w/ range faceting on
comment|// wikimediumall:
comment|/*     int index = Arrays.binarySearch(boundaries, v);     if (index< 0) {       index = -index-1;     }     leafCounts[index]++;     */
comment|// Binary search to find matched elementary range; we
comment|// are guaranteed to find a match because the last
comment|// boundary is Long.MAX_VALUE:
name|int
name|lo
init|=
literal|0
decl_stmt|;
name|int
name|hi
init|=
name|boundaries
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
comment|//System.out.println("  cycle lo=" + lo + " hi=" + hi + " mid=" + mid + " boundary=" + boundaries[mid] + " to " + boundaries[mid+1]);
if|if
condition|(
name|v
operator|<=
name|boundaries
index|[
name|mid
index|]
condition|)
block|{
if|if
condition|(
name|mid
operator|==
literal|0
condition|)
block|{
name|leafCounts
index|[
literal|0
index|]
operator|++
expr_stmt|;
return|return;
block|}
else|else
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|v
operator|>
name|boundaries
index|[
name|mid
operator|+
literal|1
index|]
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|leafCounts
index|[
name|mid
operator|+
literal|1
index|]
operator|++
expr_stmt|;
comment|//System.out.println("  incr @ " + (mid+1) + "; now " + leafCounts[mid+1]);
return|return;
block|}
block|}
block|}
comment|/** Fills counts corresponding to the original input    *  ranges, returning the missing count (how many hits    *  didn't match any ranges). */
DECL|method|fillCounts
specifier|public
name|int
name|fillCounts
parameter_list|(
name|int
index|[]
name|counts
parameter_list|)
block|{
comment|//System.out.println("  rollup");
name|missingCount
operator|=
literal|0
expr_stmt|;
name|leafUpto
operator|=
literal|0
expr_stmt|;
name|rollup
argument_list|(
name|root
argument_list|,
name|counts
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|missingCount
return|;
block|}
DECL|method|rollup
specifier|private
name|int
name|rollup
parameter_list|(
name|LongRangeNode
name|node
parameter_list|,
name|int
index|[]
name|counts
parameter_list|,
name|boolean
name|sawOutputs
parameter_list|)
block|{
name|int
name|count
decl_stmt|;
name|sawOutputs
operator||=
name|node
operator|.
name|outputs
operator|!=
literal|null
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|left
operator|!=
literal|null
condition|)
block|{
name|count
operator|=
name|rollup
argument_list|(
name|node
operator|.
name|left
argument_list|,
name|counts
argument_list|,
name|sawOutputs
argument_list|)
expr_stmt|;
name|count
operator|+=
name|rollup
argument_list|(
name|node
operator|.
name|right
argument_list|,
name|counts
argument_list|,
name|sawOutputs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Leaf:
name|count
operator|=
name|leafCounts
index|[
name|leafUpto
index|]
expr_stmt|;
name|leafUpto
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|sawOutputs
condition|)
block|{
comment|// This is a missing count (no output ranges were
comment|// seen "above" us):
name|missingCount
operator|+=
name|count
expr_stmt|;
block|}
block|}
if|if
condition|(
name|node
operator|.
name|outputs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|rangeIndex
range|:
name|node
operator|.
name|outputs
control|)
block|{
name|counts
index|[
name|rangeIndex
index|]
operator|+=
name|count
expr_stmt|;
block|}
block|}
comment|//System.out.println("  rollup node=" + node.start + " to " + node.end + ": count=" + count);
return|return
name|count
return|;
block|}
DECL|method|split
specifier|private
specifier|static
name|LongRangeNode
name|split
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|List
argument_list|<
name|InclusiveRange
argument_list|>
name|elementaryIntervals
parameter_list|)
block|{
if|if
condition|(
name|start
operator|==
name|end
operator|-
literal|1
condition|)
block|{
comment|// leaf
name|InclusiveRange
name|range
init|=
name|elementaryIntervals
operator|.
name|get
argument_list|(
name|start
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongRangeNode
argument_list|(
name|range
operator|.
name|start
argument_list|,
name|range
operator|.
name|end
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|start
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|mid
init|=
operator|(
name|start
operator|+
name|end
operator|)
operator|>>>
literal|1
decl_stmt|;
name|LongRangeNode
name|left
init|=
name|split
argument_list|(
name|start
argument_list|,
name|mid
argument_list|,
name|elementaryIntervals
argument_list|)
decl_stmt|;
name|LongRangeNode
name|right
init|=
name|split
argument_list|(
name|mid
argument_list|,
name|end
argument_list|,
name|elementaryIntervals
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongRangeNode
argument_list|(
name|left
operator|.
name|start
argument_list|,
name|right
operator|.
name|end
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
DECL|class|InclusiveRange
specifier|private
specifier|static
specifier|final
class|class
name|InclusiveRange
block|{
DECL|field|start
specifier|public
specifier|final
name|long
name|start
decl_stmt|;
DECL|field|end
specifier|public
specifier|final
name|long
name|end
decl_stmt|;
DECL|method|InclusiveRange
specifier|public
name|InclusiveRange
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
block|{
assert|assert
name|end
operator|>=
name|start
assert|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|start
operator|+
literal|" to "
operator|+
name|end
return|;
block|}
block|}
comment|/** Holds one node of the segment tree. */
DECL|class|LongRangeNode
specifier|public
specifier|static
specifier|final
class|class
name|LongRangeNode
block|{
DECL|field|left
specifier|final
name|LongRangeNode
name|left
decl_stmt|;
DECL|field|right
specifier|final
name|LongRangeNode
name|right
decl_stmt|;
comment|// Our range, inclusive:
DECL|field|start
specifier|final
name|long
name|start
decl_stmt|;
DECL|field|end
specifier|final
name|long
name|end
decl_stmt|;
comment|// If we are a leaf, the index into elementary ranges that
comment|// we point to:
DECL|field|leafIndex
specifier|final
name|int
name|leafIndex
decl_stmt|;
comment|// Which range indices to output when a query goes
comment|// through this node:
DECL|field|outputs
name|List
argument_list|<
name|Integer
argument_list|>
name|outputs
decl_stmt|;
DECL|method|LongRangeNode
specifier|public
name|LongRangeNode
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|,
name|LongRangeNode
name|left
parameter_list|,
name|LongRangeNode
name|right
parameter_list|,
name|int
name|leafIndex
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
name|this
operator|.
name|leafIndex
operator|=
name|leafIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|toString
argument_list|(
name|sb
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|indent
specifier|static
name|void
name|indent
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|int
name|depth
parameter_list|)
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
name|depth
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Recursively assigns range outputs to each node. */
DECL|method|addOutputs
name|void
name|addOutputs
parameter_list|(
name|int
name|index
parameter_list|,
name|LongRange
name|range
parameter_list|)
block|{
if|if
condition|(
name|start
operator|>=
name|range
operator|.
name|minIncl
operator|&&
name|end
operator|<=
name|range
operator|.
name|maxIncl
condition|)
block|{
comment|// Our range is fully included in the incoming
comment|// range; add to our output list:
if|if
condition|(
name|outputs
operator|==
literal|null
condition|)
block|{
name|outputs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|outputs
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|left
operator|!=
literal|null
condition|)
block|{
assert|assert
name|right
operator|!=
literal|null
assert|;
comment|// Recurse:
name|left
operator|.
name|addOutputs
argument_list|(
name|index
argument_list|,
name|range
argument_list|)
expr_stmt|;
name|right
operator|.
name|addOutputs
argument_list|(
name|index
argument_list|,
name|range
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
name|void
name|toString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|indent
argument_list|(
name|sb
argument_list|,
name|depth
argument_list|)
expr_stmt|;
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
assert|assert
name|right
operator|==
literal|null
assert|;
name|sb
operator|.
name|append
argument_list|(
literal|"leaf: "
operator|+
name|start
operator|+
literal|" to "
operator|+
name|end
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"node: "
operator|+
name|start
operator|+
literal|" to "
operator|+
name|end
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|outputs
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" outputs="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|outputs
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|left
operator|!=
literal|null
condition|)
block|{
assert|assert
name|right
operator|!=
literal|null
assert|;
name|left
operator|.
name|toString
argument_list|(
name|sb
argument_list|,
name|depth
operator|+
literal|1
argument_list|)
expr_stmt|;
name|right
operator|.
name|toString
argument_list|(
name|sb
argument_list|,
name|depth
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
