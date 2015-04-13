begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import
begin_class
annotation|@
name|Slow
DECL|class|TestTimSorterWorstCase
specifier|public
class|class
name|TestTimSorterWorstCase
extends|extends
name|LuceneTestCase
block|{
DECL|method|testWorstCaseStackSize
specifier|public
name|void
name|testWorstCaseStackSize
parameter_list|()
block|{
comment|// we need large arrays to be able to reproduce this bug
comment|// but not so big we blow up available heap.
specifier|final
name|int
name|length
decl_stmt|;
if|if
condition|(
name|TEST_NIGHTLY
condition|)
block|{
name|length
operator|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|140000000
argument_list|,
literal|400000000
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|length
operator|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|140000000
argument_list|,
literal|200000000
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PackedInts
operator|.
name|Mutable
name|arr
init|=
name|generateWorstCaseArray
argument_list|(
name|length
argument_list|)
decl_stmt|;
operator|new
name|TimSorter
argument_list|(
literal|0
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|long
name|tmp
init|=
name|arr
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|arr
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|arr
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|.
name|set
argument_list|(
name|j
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|arr
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|arr
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|save
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|len
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|restore
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|copy
parameter_list|(
name|int
name|src
parameter_list|,
name|int
name|dest
parameter_list|)
block|{
name|arr
operator|.
name|set
argument_list|(
name|dest
argument_list|,
name|arr
operator|.
name|get
argument_list|(
name|src
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compareSaved
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Create an array for the given list of runs. */
DECL|method|createArray
specifier|private
specifier|static
name|PackedInts
operator|.
name|Mutable
name|createArray
parameter_list|(
name|int
name|length
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|runs
parameter_list|)
block|{
name|PackedInts
operator|.
name|Mutable
name|array
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|length
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|endRun
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|long
name|len
range|:
name|runs
control|)
block|{
name|array
operator|.
name|set
argument_list|(
name|endRun
operator|+=
name|len
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|array
operator|.
name|set
argument_list|(
name|length
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
comment|/** Create an array that triggers a worst-case sequence of run lens. */
DECL|method|generateWorstCaseArray
specifier|public
specifier|static
name|PackedInts
operator|.
name|Mutable
name|generateWorstCaseArray
parameter_list|(
name|int
name|length
parameter_list|)
block|{
specifier|final
name|int
name|minRun
init|=
name|TimSorter
operator|.
name|minRun
argument_list|(
name|length
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|runs
init|=
name|runsWorstCase
argument_list|(
name|length
argument_list|,
name|minRun
argument_list|)
decl_stmt|;
return|return
name|createArray
argument_list|(
name|length
argument_list|,
name|runs
argument_list|)
return|;
block|}
comment|//
comment|// Code below is borrowed from https://github.com/abstools/java-timsort-bug/blob/master/TestTimSort.java
comment|//
comment|/**    * Fills<code>runs</code> with a sequence of run lengths of the form<br>    * Y_n     x_{n,1}   x_{n,2}   ... x_{n,l_n}<br>    * Y_{n-1} x_{n-1,1} x_{n-1,2} ... x_{n-1,l_{n-1}}<br>    * ...<br>    * Y_1     x_{1,1}   x_{1,2}   ... x_{1,l_1}<br>    * The Y_i's are chosen to satisfy the invariant throughout execution,    * but the x_{i,j}'s are merged (by<code>TimSort.mergeCollapse</code>)    * into an X_i that violates the invariant.    */
DECL|method|runsWorstCase
specifier|private
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|runsWorstCase
parameter_list|(
name|int
name|length
parameter_list|,
name|int
name|minRun
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|runs
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|runningTotal
init|=
literal|0
decl_stmt|,
name|Y
init|=
name|minRun
operator|+
literal|4
decl_stmt|,
name|X
init|=
name|minRun
decl_stmt|;
while|while
condition|(
operator|(
name|long
operator|)
name|runningTotal
operator|+
name|Y
operator|+
name|X
operator|<=
name|length
condition|)
block|{
name|runningTotal
operator|+=
name|X
operator|+
name|Y
expr_stmt|;
name|generateWrongElem
argument_list|(
name|X
argument_list|,
name|minRun
argument_list|,
name|runs
argument_list|)
expr_stmt|;
name|runs
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|Y
argument_list|)
expr_stmt|;
comment|// X_{i+1} = Y_i + x_{i,1} + 1, since runs.get(1) = x_{i,1}
name|X
operator|=
name|Y
operator|+
name|runs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|+
literal|1
expr_stmt|;
comment|// Y_{i+1} = X_{i+1} + Y_i + 1
name|Y
operator|+=
name|X
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|long
operator|)
name|runningTotal
operator|+
name|X
operator|<=
name|length
condition|)
block|{
name|runningTotal
operator|+=
name|X
expr_stmt|;
name|generateWrongElem
argument_list|(
name|X
argument_list|,
name|minRun
argument_list|,
name|runs
argument_list|)
expr_stmt|;
block|}
name|runs
operator|.
name|add
argument_list|(
name|length
operator|-
name|runningTotal
argument_list|)
expr_stmt|;
return|return
name|runs
return|;
block|}
comment|/**    * Adds a sequence x_1, ..., x_n of run lengths to<code>runs</code> such that:<br>    * 1. X = x_1 + ... + x_n<br>    * 2. x_j>= minRun for all j<br>    * 3. x_1 + ... + x_{j-2}<  x_j<  x_1 + ... + x_{j-1} for all j<br>    * These conditions guarantee that TimSort merges all x_j's one by one    * (resulting in X) using only merges on the second-to-last element.    * @param X  The sum of the sequence that should be added to runs.    */
DECL|method|generateWrongElem
specifier|private
specifier|static
name|void
name|generateWrongElem
parameter_list|(
name|int
name|X
parameter_list|,
name|int
name|minRun
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|runs
parameter_list|)
block|{
for|for
control|(
name|int
name|newTotal
init|;
name|X
operator|>=
literal|2
operator|*
name|minRun
operator|+
literal|1
condition|;
name|X
operator|=
name|newTotal
control|)
block|{
comment|//Default strategy
name|newTotal
operator|=
name|X
operator|/
literal|2
operator|+
literal|1
expr_stmt|;
comment|//Specialized strategies
if|if
condition|(
literal|3
operator|*
name|minRun
operator|+
literal|3
operator|<=
name|X
operator|&&
name|X
operator|<=
literal|4
operator|*
name|minRun
operator|+
literal|1
condition|)
block|{
comment|// add x_1=MIN+1, x_2=MIN, x_3=X-newTotal  to runs
name|newTotal
operator|=
literal|2
operator|*
name|minRun
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|5
operator|*
name|minRun
operator|+
literal|5
operator|<=
name|X
operator|&&
name|X
operator|<=
literal|6
operator|*
name|minRun
operator|+
literal|5
condition|)
block|{
comment|// add x_1=MIN+1, x_2=MIN, x_3=MIN+2, x_4=X-newTotal  to runs
name|newTotal
operator|=
literal|3
operator|*
name|minRun
operator|+
literal|3
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|8
operator|*
name|minRun
operator|+
literal|9
operator|<=
name|X
operator|&&
name|X
operator|<=
literal|10
operator|*
name|minRun
operator|+
literal|9
condition|)
block|{
comment|// add x_1=MIN+1, x_2=MIN, x_3=MIN+2, x_4=2MIN+2, x_5=X-newTotal  to runs
name|newTotal
operator|=
literal|5
operator|*
name|minRun
operator|+
literal|5
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|13
operator|*
name|minRun
operator|+
literal|15
operator|<=
name|X
operator|&&
name|X
operator|<=
literal|16
operator|*
name|minRun
operator|+
literal|17
condition|)
block|{
comment|// add x_1=MIN+1, x_2=MIN, x_3=MIN+2, x_4=2MIN+2, x_5=3MIN+4, x_6=X-newTotal  to runs
name|newTotal
operator|=
literal|8
operator|*
name|minRun
operator|+
literal|9
expr_stmt|;
block|}
name|runs
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|X
operator|-
name|newTotal
argument_list|)
expr_stmt|;
block|}
name|runs
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|X
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
