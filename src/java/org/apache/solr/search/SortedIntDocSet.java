begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|util
operator|.
name|OpenBitSet
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
name|Filter
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  *<code>SortedIntDocSet</code> represents a sorted set of Lucene Document Ids.  */
end_comment
begin_class
DECL|class|SortedIntDocSet
specifier|public
class|class
name|SortedIntDocSet
extends|extends
name|DocSetBase
block|{
DECL|field|docs
specifier|protected
specifier|final
name|int
index|[]
name|docs
decl_stmt|;
comment|/**    * @param docs  Sorted list of ids    */
DECL|method|SortedIntDocSet
specifier|public
name|SortedIntDocSet
parameter_list|(
name|int
index|[]
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
comment|// if (firstNonSorted(docs,0,docs.length)>=0) throw new RuntimeException("NON SORTED DOCS!!!");
block|}
comment|/**    * @param docs Sorted list of ids    * @param len  Number of ids in the list    */
DECL|method|SortedIntDocSet
specifier|public
name|SortedIntDocSet
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|this
argument_list|(
name|shrink
argument_list|(
name|docs
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocs
specifier|public
name|int
index|[]
name|getDocs
parameter_list|()
block|{
return|return
name|docs
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|docs
operator|.
name|length
return|;
block|}
DECL|method|memSize
specifier|public
name|long
name|memSize
parameter_list|()
block|{
return|return
operator|(
name|docs
operator|.
name|length
operator|<<
literal|2
operator|)
operator|+
literal|8
return|;
block|}
DECL|field|zeroInts
specifier|public
specifier|static
name|int
index|[]
name|zeroInts
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|field|zero
specifier|public
specifier|static
name|SortedIntDocSet
name|zero
init|=
operator|new
name|SortedIntDocSet
argument_list|(
name|zeroInts
argument_list|)
decl_stmt|;
DECL|method|shrink
specifier|public
specifier|static
name|int
index|[]
name|shrink
parameter_list|(
name|int
index|[]
name|arr
parameter_list|,
name|int
name|newSize
parameter_list|)
block|{
if|if
condition|(
name|arr
operator|.
name|length
operator|==
name|newSize
condition|)
return|return
name|arr
return|;
name|int
index|[]
name|newArr
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|newArr
argument_list|,
literal|0
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
return|return
name|newArr
return|;
block|}
comment|/** Returns the index of the first non-sorted element or -1 if they are all sorted */
DECL|method|firstNonSorted
specifier|public
specifier|static
name|int
name|firstNonSorted
parameter_list|(
name|int
index|[]
name|arr
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|<=
literal|1
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|lower
init|=
name|arr
index|[
name|offset
index|]
decl_stmt|;
name|int
name|end
init|=
name|offset
operator|+
name|len
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
operator|+
literal|1
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|int
name|next
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|next
operator|<=
name|lower
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
name|i
operator|-
literal|1
init|;
name|j
operator|>
name|offset
condition|;
name|j
operator|--
control|)
block|{
if|if
condition|(
name|arr
index|[
name|j
index|]
operator|<
name|next
condition|)
return|return
name|j
operator|+
literal|1
return|;
block|}
return|return
name|offset
return|;
block|}
name|lower
operator|=
name|next
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|intersectionSize
specifier|public
specifier|static
name|int
name|intersectionSize
parameter_list|(
name|int
index|[]
name|smallerSortedList
parameter_list|,
name|int
index|[]
name|biggerSortedList
parameter_list|)
block|{
specifier|final
name|int
name|a
index|[]
init|=
name|smallerSortedList
decl_stmt|;
specifier|final
name|int
name|b
index|[]
init|=
name|biggerSortedList
decl_stmt|;
comment|// The next doc we are looking for will be much closer to the last position we tried
comment|// than it will be to the midpoint between last and high... so probe ahead using
comment|// a function of the ratio of the sizes of the sets.
name|int
name|step
init|=
operator|(
name|b
operator|.
name|length
operator|/
name|a
operator|.
name|length
operator|)
operator|+
literal|1
decl_stmt|;
comment|// Since the majority of probes should be misses, we'll already be above the last probe
comment|// and shouldn't need to move larger than the step size on average to step over our target (and thus lower
comment|// the high upper bound a lot.)... but if we don't go over our target, it's a big miss... so double it.
name|step
operator|=
name|step
operator|+
name|step
expr_stmt|;
comment|// FUTURE: come up with a density such that target * density == likely position?
comment|// then check step on one side or the other?
comment|// (density could be cached in the DocSet)... length/maxDoc
comment|// FUTURE: try partitioning like a sort algorithm.  Pick the midpoint of the big
comment|// array, find where that should be in the small array, and then recurse with
comment|// the top and bottom half of both arrays until they are small enough to use
comment|// a fallback insersection method.
comment|// NOTE: I tried this and it worked, but it was actually slower than this current
comment|// highly optimized approach.
name|int
name|icount
init|=
literal|0
decl_stmt|;
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|max
init|=
name|b
operator|.
name|length
operator|-
literal|1
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|doca
init|=
name|a
index|[
name|i
index|]
decl_stmt|;
name|int
name|high
init|=
name|max
decl_stmt|;
name|int
name|probe
init|=
name|low
operator|+
name|step
decl_stmt|;
comment|// 40% improvement!
comment|// short linear probe to see if we can drop the high pointer in one big jump.
if|if
condition|(
name|probe
operator|<
name|high
condition|)
block|{
if|if
condition|(
name|b
index|[
name|probe
index|]
operator|>=
name|doca
condition|)
block|{
comment|// success!  we cut down the upper bound by a lot in one step!
name|high
operator|=
name|probe
expr_stmt|;
block|}
else|else
block|{
comment|// relative failure... we get to move the low pointer, but not my much
name|low
operator|=
name|probe
operator|+
literal|1
expr_stmt|;
comment|// reprobe worth it? it appears so!
name|probe
operator|=
name|low
operator|+
name|step
expr_stmt|;
if|if
condition|(
name|probe
operator|<
name|high
condition|)
block|{
if|if
condition|(
name|b
index|[
name|probe
index|]
operator|>=
name|doca
condition|)
block|{
name|high
operator|=
name|probe
expr_stmt|;
block|}
else|else
block|{
name|low
operator|=
name|probe
operator|+
literal|1
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// binary search the rest of the way
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|docb
init|=
name|b
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|docb
operator|<
name|doca
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docb
operator|>
name|doca
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|icount
operator|++
expr_stmt|;
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
comment|// found it, so start at next element
break|break;
block|}
block|}
comment|// Didn't find it... low is now positioned on the insertion point,
comment|// which is higher than what we were looking for, so continue using
comment|// the same low point.
block|}
return|return
name|icount
return|;
block|}
DECL|method|intersectionSize
specifier|public
name|int
name|intersectionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|SortedIntDocSet
operator|)
condition|)
block|{
comment|// assume other implementations are better at random access than we are,
comment|// true of BitDocSet and HashDocSet.
name|int
name|icount
init|=
literal|0
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|other
operator|.
name|exists
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
condition|)
name|icount
operator|++
expr_stmt|;
block|}
return|return
name|icount
return|;
block|}
comment|// make "a" the smaller set.
name|int
index|[]
name|otherDocs
init|=
operator|(
operator|(
name|SortedIntDocSet
operator|)
name|other
operator|)
operator|.
name|docs
decl_stmt|;
specifier|final
name|int
index|[]
name|a
init|=
name|docs
operator|.
name|length
operator|<
name|otherDocs
operator|.
name|length
condition|?
name|docs
else|:
name|otherDocs
decl_stmt|;
specifier|final
name|int
index|[]
name|b
init|=
name|docs
operator|.
name|length
operator|<
name|otherDocs
operator|.
name|length
condition|?
name|otherDocs
else|:
name|docs
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|0
return|;
comment|// if b is 8 times bigger than a, use the modified binary search.
if|if
condition|(
operator|(
name|b
operator|.
name|length
operator|>>
literal|3
operator|)
operator|>=
name|a
operator|.
name|length
condition|)
block|{
return|return
name|intersectionSize
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
comment|// if they are close in size, just do a linear walk of both.
name|int
name|icount
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|j
init|=
literal|0
decl_stmt|;
name|int
name|doca
init|=
name|a
index|[
name|i
index|]
decl_stmt|,
name|docb
init|=
name|b
index|[
name|j
index|]
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// switch on the sign bit somehow?  Hopefull JVM is smart enough to just test once.
comment|// Since set a is less dense then set b, doca is likely to be greater than docb so
comment|// check that case first.  This resulted in a 13% speedup.
if|if
condition|(
name|doca
operator|>
name|docb
condition|)
block|{
if|if
condition|(
operator|++
name|j
operator|>=
name|b
operator|.
name|length
condition|)
break|break;
name|docb
operator|=
name|b
index|[
name|j
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|doca
operator|<
name|docb
condition|)
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|a
operator|.
name|length
condition|)
break|break;
name|doca
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|icount
operator|++
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|>=
name|a
operator|.
name|length
condition|)
break|break;
name|doca
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|++
name|j
operator|>=
name|b
operator|.
name|length
condition|)
break|break;
name|docb
operator|=
name|b
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
return|return
name|icount
return|;
block|}
comment|/** puts the intersection of a and b into the target array and returns the size */
DECL|method|intersection
specifier|public
specifier|static
name|int
name|intersection
parameter_list|(
name|int
name|a
index|[]
parameter_list|,
name|int
name|lena
parameter_list|,
name|int
name|b
index|[]
parameter_list|,
name|int
name|lenb
parameter_list|,
name|int
index|[]
name|target
parameter_list|)
block|{
if|if
condition|(
name|lena
operator|>
name|lenb
condition|)
block|{
name|int
name|ti
init|=
name|lena
decl_stmt|;
name|lena
operator|=
name|lenb
expr_stmt|;
name|lenb
operator|=
name|ti
expr_stmt|;
name|int
index|[]
name|ta
init|=
name|a
decl_stmt|;
name|a
operator|=
name|b
expr_stmt|;
name|b
operator|=
name|ta
expr_stmt|;
block|}
if|if
condition|(
name|lena
operator|==
literal|0
condition|)
return|return
literal|0
return|;
comment|// if b is 8 times bigger than a, use the modified binary search.
if|if
condition|(
operator|(
name|lenb
operator|>>
literal|3
operator|)
operator|>=
name|lena
condition|)
block|{
return|return
name|intersectionBinarySearch
argument_list|(
name|a
argument_list|,
name|lena
argument_list|,
name|b
argument_list|,
name|lenb
argument_list|,
name|target
argument_list|)
return|;
block|}
name|int
name|icount
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|j
init|=
literal|0
decl_stmt|;
name|int
name|doca
init|=
name|a
index|[
name|i
index|]
decl_stmt|,
name|docb
init|=
name|b
index|[
name|j
index|]
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|doca
operator|>
name|docb
condition|)
block|{
if|if
condition|(
operator|++
name|j
operator|>=
name|lenb
condition|)
break|break;
name|docb
operator|=
name|b
index|[
name|j
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|doca
operator|<
name|docb
condition|)
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|lena
condition|)
break|break;
name|doca
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|target
index|[
name|icount
operator|++
index|]
operator|=
name|doca
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|>=
name|lena
condition|)
break|break;
name|doca
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|++
name|j
operator|>=
name|lenb
condition|)
break|break;
name|docb
operator|=
name|b
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
return|return
name|icount
return|;
block|}
comment|/** Puts the intersection of a and b into the target array and returns the size.    * lena should be smaller than lenb */
DECL|method|intersectionBinarySearch
specifier|protected
specifier|static
name|int
name|intersectionBinarySearch
parameter_list|(
name|int
index|[]
name|a
parameter_list|,
name|int
name|lena
parameter_list|,
name|int
index|[]
name|b
parameter_list|,
name|int
name|lenb
parameter_list|,
name|int
index|[]
name|target
parameter_list|)
block|{
name|int
name|step
init|=
operator|(
name|lenb
operator|/
name|lena
operator|)
operator|+
literal|1
decl_stmt|;
name|step
operator|=
name|step
operator|+
name|step
expr_stmt|;
name|int
name|icount
init|=
literal|0
decl_stmt|;
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|max
init|=
name|lenb
operator|-
literal|1
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
name|lena
condition|;
name|i
operator|++
control|)
block|{
name|int
name|doca
init|=
name|a
index|[
name|i
index|]
decl_stmt|;
name|int
name|high
init|=
name|max
decl_stmt|;
name|int
name|probe
init|=
name|low
operator|+
name|step
decl_stmt|;
comment|// 40% improvement!
comment|// short linear probe to see if we can drop the high pointer in one big jump.
if|if
condition|(
name|probe
operator|<
name|high
condition|)
block|{
if|if
condition|(
name|b
index|[
name|probe
index|]
operator|>=
name|doca
condition|)
block|{
comment|// success!  we cut down the upper bound by a lot in one step!
name|high
operator|=
name|probe
expr_stmt|;
block|}
else|else
block|{
comment|// relative failure... we get to move the low pointer, but not my much
name|low
operator|=
name|probe
operator|+
literal|1
expr_stmt|;
comment|// reprobe worth it? it appears so!
name|probe
operator|=
name|low
operator|+
name|step
expr_stmt|;
if|if
condition|(
name|probe
operator|<
name|high
condition|)
block|{
if|if
condition|(
name|b
index|[
name|probe
index|]
operator|>=
name|doca
condition|)
block|{
name|high
operator|=
name|probe
expr_stmt|;
block|}
else|else
block|{
name|low
operator|=
name|probe
operator|+
literal|1
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// binary search
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|docb
init|=
name|b
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|docb
operator|<
name|doca
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docb
operator|>
name|doca
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|target
index|[
name|icount
operator|++
index|]
operator|=
name|doca
expr_stmt|;
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
comment|// found it, so start at next element
break|break;
block|}
block|}
comment|// Didn't find it... low is now positioned on the insertion point,
comment|// which is higher than what we were looking for, so continue using
comment|// the same low point.
block|}
return|return
name|icount
return|;
block|}
annotation|@
name|Override
DECL|method|intersection
specifier|public
name|DocSet
name|intersection
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|SortedIntDocSet
operator|)
condition|)
block|{
name|int
name|icount
init|=
literal|0
decl_stmt|;
name|int
name|arr
index|[]
init|=
operator|new
name|int
index|[
name|docs
operator|.
name|length
index|]
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|doc
init|=
name|docs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
name|arr
index|[
name|icount
operator|++
index|]
operator|=
name|doc
expr_stmt|;
block|}
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|arr
argument_list|,
name|icount
argument_list|)
return|;
block|}
name|int
index|[]
name|otherDocs
init|=
operator|(
operator|(
name|SortedIntDocSet
operator|)
name|other
operator|)
operator|.
name|docs
decl_stmt|;
name|int
name|maxsz
init|=
name|Math
operator|.
name|min
argument_list|(
name|docs
operator|.
name|length
argument_list|,
name|otherDocs
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|maxsz
index|]
decl_stmt|;
name|int
name|sz
init|=
name|intersection
argument_list|(
name|docs
argument_list|,
name|docs
operator|.
name|length
argument_list|,
name|otherDocs
argument_list|,
name|otherDocs
operator|.
name|length
argument_list|,
name|arr
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|arr
argument_list|,
name|sz
argument_list|)
return|;
block|}
DECL|method|andNotBinarySearch
specifier|protected
specifier|static
name|int
name|andNotBinarySearch
parameter_list|(
name|int
name|a
index|[]
parameter_list|,
name|int
name|lena
parameter_list|,
name|int
name|b
index|[]
parameter_list|,
name|int
name|lenb
parameter_list|,
name|int
index|[]
name|target
parameter_list|)
block|{
name|int
name|step
init|=
operator|(
name|lenb
operator|/
name|lena
operator|)
operator|+
literal|1
decl_stmt|;
name|step
operator|=
name|step
operator|+
name|step
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|max
init|=
name|lenb
operator|-
literal|1
decl_stmt|;
name|outer
label|:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lena
condition|;
name|i
operator|++
control|)
block|{
name|int
name|doca
init|=
name|a
index|[
name|i
index|]
decl_stmt|;
name|int
name|high
init|=
name|max
decl_stmt|;
name|int
name|probe
init|=
name|low
operator|+
name|step
decl_stmt|;
comment|// 40% improvement!
comment|// short linear probe to see if we can drop the high pointer in one big jump.
if|if
condition|(
name|probe
operator|<
name|high
condition|)
block|{
if|if
condition|(
name|b
index|[
name|probe
index|]
operator|>=
name|doca
condition|)
block|{
comment|// success!  we cut down the upper bound by a lot in one step!
name|high
operator|=
name|probe
expr_stmt|;
block|}
else|else
block|{
comment|// relative failure... we get to move the low pointer, but not my much
name|low
operator|=
name|probe
operator|+
literal|1
expr_stmt|;
comment|// reprobe worth it? it appears so!
name|probe
operator|=
name|low
operator|+
name|step
expr_stmt|;
if|if
condition|(
name|probe
operator|<
name|high
condition|)
block|{
if|if
condition|(
name|b
index|[
name|probe
index|]
operator|>=
name|doca
condition|)
block|{
name|high
operator|=
name|probe
expr_stmt|;
block|}
else|else
block|{
name|low
operator|=
name|probe
operator|+
literal|1
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// binary search
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|docb
init|=
name|b
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|docb
operator|<
name|doca
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docb
operator|>
name|doca
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
comment|// found it, so start at next element
continue|continue
name|outer
continue|;
block|}
block|}
comment|// Didn't find it... low is now positioned on the insertion point,
comment|// which is higher than what we were looking for, so continue using
comment|// the same low point.
name|target
index|[
name|count
operator|++
index|]
operator|=
name|doca
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/** puts the intersection of a and not b into the target array and returns the size */
DECL|method|andNot
specifier|public
specifier|static
name|int
name|andNot
parameter_list|(
name|int
name|a
index|[]
parameter_list|,
name|int
name|lena
parameter_list|,
name|int
name|b
index|[]
parameter_list|,
name|int
name|lenb
parameter_list|,
name|int
index|[]
name|target
parameter_list|)
block|{
if|if
condition|(
name|lena
operator|==
literal|0
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|lenb
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|target
argument_list|,
literal|0
argument_list|,
name|lena
argument_list|)
expr_stmt|;
return|return
name|lena
return|;
block|}
comment|// if b is 8 times bigger than a, use the modified binary search.
if|if
condition|(
operator|(
name|lenb
operator|>>
literal|3
operator|)
operator|>=
name|lena
condition|)
block|{
return|return
name|andNotBinarySearch
argument_list|(
name|a
argument_list|,
name|lena
argument_list|,
name|b
argument_list|,
name|lenb
argument_list|,
name|target
argument_list|)
return|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|j
init|=
literal|0
decl_stmt|;
name|int
name|doca
init|=
name|a
index|[
name|i
index|]
decl_stmt|,
name|docb
init|=
name|b
index|[
name|j
index|]
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|doca
operator|>
name|docb
condition|)
block|{
if|if
condition|(
operator|++
name|j
operator|>=
name|lenb
condition|)
break|break;
name|docb
operator|=
name|b
index|[
name|j
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|doca
operator|<
name|docb
condition|)
block|{
name|target
index|[
name|count
operator|++
index|]
operator|=
name|doca
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|>=
name|lena
condition|)
break|break;
name|doca
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|lena
condition|)
break|break;
name|doca
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|++
name|j
operator|>=
name|lenb
condition|)
break|break;
name|docb
operator|=
name|b
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
name|int
name|leftover
init|=
name|lena
operator|-
name|i
decl_stmt|;
if|if
condition|(
name|leftover
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|target
argument_list|,
name|count
argument_list|,
name|leftover
argument_list|)
expr_stmt|;
name|count
operator|+=
name|leftover
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|andNot
specifier|public
name|DocSet
name|andNot
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
name|this
return|;
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|SortedIntDocSet
operator|)
condition|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|arr
index|[]
init|=
operator|new
name|int
index|[
name|docs
operator|.
name|length
index|]
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|doc
init|=
name|docs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|other
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
name|arr
index|[
name|count
operator|++
index|]
operator|=
name|doc
expr_stmt|;
block|}
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|arr
argument_list|,
name|count
argument_list|)
return|;
block|}
name|int
index|[]
name|otherDocs
init|=
operator|(
operator|(
name|SortedIntDocSet
operator|)
name|other
operator|)
operator|.
name|docs
decl_stmt|;
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|docs
operator|.
name|length
index|]
decl_stmt|;
name|int
name|sz
init|=
name|andNot
argument_list|(
name|docs
argument_list|,
name|docs
operator|.
name|length
argument_list|,
name|otherDocs
argument_list|,
name|otherDocs
operator|.
name|length
argument_list|,
name|arr
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|arr
argument_list|,
name|sz
argument_list|)
return|;
block|}
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// this could be faster by estimating where in the list the doc is likely to appear,
comment|// but we should get away from using exists() anyway.
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|docs
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// binary search
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|docb
init|=
name|docs
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|docb
operator|<
name|doc
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|docb
operator|>
name|doc
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
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
DECL|method|iterator
specifier|public
name|DocIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIterator
argument_list|()
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|docs
operator|.
name|length
return|;
block|}
specifier|public
name|Integer
name|next
parameter_list|()
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
comment|/**        * The remove  operation is not supported by this Iterator.        */
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"The remove  operation is not supported by this Iterator."
argument_list|)
throw|;
block|}
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|docs
index|[
name|pos
operator|++
index|]
return|;
block|}
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
literal|0.0f
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getBits
specifier|public
name|OpenBitSet
name|getBits
parameter_list|()
block|{
name|int
name|maxDoc
init|=
name|size
argument_list|()
operator|>
literal|0
condition|?
name|docs
index|[
name|size
argument_list|()
operator|-
literal|1
index|]
else|:
literal|0
decl_stmt|;
name|OpenBitSet
name|bs
init|=
operator|new
name|OpenBitSet
argument_list|(
name|maxDoc
operator|+
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
range|:
name|docs
control|)
block|{
name|bs
operator|.
name|fastSet
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|findIndex
specifier|public
specifier|static
name|int
name|findIndex
parameter_list|(
name|int
index|[]
name|arr
parameter_list|,
name|int
name|value
parameter_list|,
name|int
name|low
parameter_list|,
name|int
name|high
parameter_list|)
block|{
comment|// binary search
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|found
init|=
name|arr
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|found
operator|<
name|value
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|found
operator|>
name|value
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|mid
return|;
block|}
block|}
return|return
name|low
return|;
block|}
annotation|@
name|Override
DECL|method|getTopFilter
specifier|public
name|Filter
name|getTopFilter
parameter_list|()
block|{
return|return
operator|new
name|Filter
argument_list|()
block|{
name|int
name|lastEndIdx
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|SolrIndexReader
name|r
init|=
operator|(
name|SolrIndexReader
operator|)
name|reader
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|offset
operator|+=
name|r
operator|.
name|getBase
argument_list|()
expr_stmt|;
name|r
operator|=
name|r
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|base
init|=
name|offset
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|base
operator|+
name|maxDoc
decl_stmt|;
comment|// one past the max doc in this segment.
name|int
name|sidx
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|lastEndIdx
argument_list|)
decl_stmt|;
if|if
condition|(
name|sidx
operator|>
literal|0
operator|&&
name|docs
index|[
name|sidx
operator|-
literal|1
index|]
operator|>=
name|base
condition|)
block|{
comment|// oops, the lastEndIdx isn't correct... we must have been used
comment|// in a multi-threaded context, or the indexreaders are being
comment|// used out-of-order.  start at 0.
name|sidx
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|sidx
operator|<
name|docs
operator|.
name|length
operator|&&
name|docs
index|[
name|sidx
index|]
operator|<
name|base
condition|)
block|{
comment|// if docs[sidx] is< base, we need to seek to find the real start.
name|sidx
operator|=
name|findIndex
argument_list|(
name|docs
argument_list|,
name|base
argument_list|,
name|sidx
argument_list|,
name|docs
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|startIdx
init|=
name|sidx
decl_stmt|;
comment|// Largest possible end index is limited to the start index
comment|// plus the number of docs contained in the segment.  Subtract 1 since
comment|// the end index is inclusive.
name|int
name|eidx
init|=
name|Math
operator|.
name|min
argument_list|(
name|docs
operator|.
name|length
argument_list|,
name|startIdx
operator|+
name|maxDoc
argument_list|)
operator|-
literal|1
decl_stmt|;
comment|// find the real end
name|eidx
operator|=
name|findIndex
argument_list|(
name|docs
argument_list|,
name|max
argument_list|,
name|startIdx
argument_list|,
name|eidx
argument_list|)
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|endIdx
init|=
name|eidx
decl_stmt|;
name|lastEndIdx
operator|=
name|endIdx
expr_stmt|;
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
name|int
name|idx
init|=
name|startIdx
decl_stmt|;
name|int
name|adjustedDoc
decl_stmt|;
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|adjustedDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|adjustedDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|adjustedDoc
operator|=
operator|(
name|idx
operator|>
name|endIdx
operator|)
condition|?
name|NO_MORE_DOCS
else|:
operator|(
name|docs
index|[
name|idx
operator|++
index|]
operator|-
name|base
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|idx
operator|>
name|endIdx
operator|||
name|target
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|adjustedDoc
operator|=
name|NO_MORE_DOCS
return|;
name|target
operator|+=
name|base
expr_stmt|;
comment|// probe next
name|int
name|rawDoc
init|=
name|docs
index|[
name|idx
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|rawDoc
operator|>=
name|target
condition|)
return|return
name|adjustedDoc
operator|=
name|rawDoc
operator|-
name|base
return|;
name|int
name|high
init|=
name|endIdx
decl_stmt|;
comment|// TODO: probe more before resorting to binary search?
comment|// binary search
while|while
condition|(
name|idx
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|idx
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|rawDoc
operator|=
name|docs
index|[
name|mid
index|]
expr_stmt|;
if|if
condition|(
name|rawDoc
operator|<
name|target
condition|)
block|{
name|idx
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rawDoc
operator|>
name|target
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|idx
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
return|return
name|adjustedDoc
operator|=
name|rawDoc
operator|-
name|base
return|;
block|}
block|}
comment|// low is on the insertion point...
if|if
condition|(
name|idx
operator|<=
name|endIdx
condition|)
block|{
return|return
name|adjustedDoc
operator|=
name|docs
index|[
name|idx
operator|++
index|]
operator|-
name|base
return|;
block|}
else|else
block|{
return|return
name|adjustedDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
