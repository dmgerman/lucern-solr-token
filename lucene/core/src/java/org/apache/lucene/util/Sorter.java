begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_comment
comment|/** Base class for sorting algorithms implementations.  * @lucene.internal */
end_comment
begin_class
DECL|class|Sorter
specifier|public
specifier|abstract
class|class
name|Sorter
block|{
DECL|field|THRESHOLD
specifier|static
specifier|final
name|int
name|THRESHOLD
init|=
literal|20
decl_stmt|;
comment|/** Sole constructor, used for inheritance. */
DECL|method|Sorter
specifier|protected
name|Sorter
parameter_list|()
block|{}
comment|/** Compare entries found in slots<code>i</code> and<code>j</code>.    *  The contract for the returned value is the same as    *  {@link Comparator#compare(Object, Object)}. */
DECL|method|compare
specifier|protected
specifier|abstract
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
function_decl|;
comment|/** Swap values at slots<code>i</code> and<code>j</code>. */
DECL|method|swap
specifier|protected
specifier|abstract
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
function_decl|;
comment|/** Sort the slice which starts at<code>from</code> (inclusive) and ends at    *<code>to</code> (exclusive). */
DECL|method|sort
specifier|public
specifier|abstract
name|void
name|sort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
function_decl|;
DECL|method|checkRange
name|void
name|checkRange
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|<
name|from
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'to' must be>= 'from', got from="
operator|+
name|from
operator|+
literal|" and to="
operator|+
name|to
argument_list|)
throw|;
block|}
block|}
DECL|method|mergeInPlace
name|void
name|mergeInPlace
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|mid
parameter_list|,
name|int
name|to
parameter_list|)
block|{
if|if
condition|(
name|from
operator|==
name|mid
operator|||
name|mid
operator|==
name|to
operator|||
name|compare
argument_list|(
name|mid
operator|-
literal|1
argument_list|,
name|mid
argument_list|)
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|to
operator|-
name|from
operator|==
literal|2
condition|)
block|{
name|swap
argument_list|(
name|mid
operator|-
literal|1
argument_list|,
name|mid
argument_list|)
expr_stmt|;
return|return;
block|}
while|while
condition|(
name|compare
argument_list|(
name|from
argument_list|,
name|mid
argument_list|)
operator|<=
literal|0
condition|)
block|{
operator|++
name|from
expr_stmt|;
block|}
while|while
condition|(
name|compare
argument_list|(
name|mid
operator|-
literal|1
argument_list|,
name|to
operator|-
literal|1
argument_list|)
operator|<=
literal|0
condition|)
block|{
operator|--
name|to
expr_stmt|;
block|}
name|int
name|first_cut
decl_stmt|,
name|second_cut
decl_stmt|;
name|int
name|len11
decl_stmt|,
name|len22
decl_stmt|;
if|if
condition|(
name|mid
operator|-
name|from
operator|>
name|to
operator|-
name|mid
condition|)
block|{
name|len11
operator|=
operator|(
name|mid
operator|-
name|from
operator|)
operator|>>>
literal|1
expr_stmt|;
name|first_cut
operator|=
name|from
operator|+
name|len11
expr_stmt|;
name|second_cut
operator|=
name|lower
argument_list|(
name|mid
argument_list|,
name|to
argument_list|,
name|first_cut
argument_list|)
expr_stmt|;
name|len22
operator|=
name|second_cut
operator|-
name|mid
expr_stmt|;
block|}
else|else
block|{
name|len22
operator|=
operator|(
name|to
operator|-
name|mid
operator|)
operator|>>>
literal|1
expr_stmt|;
name|second_cut
operator|=
name|mid
operator|+
name|len22
expr_stmt|;
name|first_cut
operator|=
name|upper
argument_list|(
name|from
argument_list|,
name|mid
argument_list|,
name|second_cut
argument_list|)
expr_stmt|;
name|len11
operator|=
name|first_cut
operator|-
name|from
expr_stmt|;
block|}
name|rotate
argument_list|(
name|first_cut
argument_list|,
name|mid
argument_list|,
name|second_cut
argument_list|)
expr_stmt|;
specifier|final
name|int
name|new_mid
init|=
name|first_cut
operator|+
name|len22
decl_stmt|;
name|mergeInPlace
argument_list|(
name|from
argument_list|,
name|first_cut
argument_list|,
name|new_mid
argument_list|)
expr_stmt|;
name|mergeInPlace
argument_list|(
name|new_mid
argument_list|,
name|second_cut
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
DECL|method|lower
name|int
name|lower
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|int
name|len
init|=
name|to
operator|-
name|from
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|half
init|=
name|len
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|mid
init|=
name|from
operator|+
name|half
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|mid
argument_list|,
name|val
argument_list|)
operator|<
literal|0
condition|)
block|{
name|from
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
name|len
operator|=
name|len
operator|-
name|half
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|len
operator|=
name|half
expr_stmt|;
block|}
block|}
return|return
name|from
return|;
block|}
DECL|method|upper
name|int
name|upper
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|int
name|len
init|=
name|to
operator|-
name|from
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|half
init|=
name|len
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|mid
init|=
name|from
operator|+
name|half
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|val
argument_list|,
name|mid
argument_list|)
operator|<
literal|0
condition|)
block|{
name|len
operator|=
name|half
expr_stmt|;
block|}
else|else
block|{
name|from
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
name|len
operator|=
name|len
operator|-
name|half
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|from
return|;
block|}
comment|// faster than lower when val is at the end of [from:to[
DECL|method|lower2
name|int
name|lower2
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|int
name|f
init|=
name|to
operator|-
literal|1
decl_stmt|,
name|t
init|=
name|to
decl_stmt|;
while|while
condition|(
name|f
operator|>
name|from
condition|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|f
argument_list|,
name|val
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|lower
argument_list|(
name|f
argument_list|,
name|t
argument_list|,
name|val
argument_list|)
return|;
block|}
specifier|final
name|int
name|delta
init|=
name|t
operator|-
name|f
decl_stmt|;
name|t
operator|=
name|f
expr_stmt|;
name|f
operator|-=
name|delta
operator|<<
literal|1
expr_stmt|;
block|}
return|return
name|lower
argument_list|(
name|from
argument_list|,
name|t
argument_list|,
name|val
argument_list|)
return|;
block|}
comment|// faster than upper when val is at the beginning of [from:to[
DECL|method|upper2
name|int
name|upper2
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|int
name|f
init|=
name|from
decl_stmt|,
name|t
init|=
name|f
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|t
operator|<
name|to
condition|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|t
argument_list|,
name|val
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|upper
argument_list|(
name|f
argument_list|,
name|t
argument_list|,
name|val
argument_list|)
return|;
block|}
specifier|final
name|int
name|delta
init|=
name|t
operator|-
name|f
decl_stmt|;
name|f
operator|=
name|t
expr_stmt|;
name|t
operator|+=
name|delta
operator|<<
literal|1
expr_stmt|;
block|}
return|return
name|upper
argument_list|(
name|f
argument_list|,
name|to
argument_list|,
name|val
argument_list|)
return|;
block|}
DECL|method|reverse
specifier|final
name|void
name|reverse
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
for|for
control|(
operator|--
name|to
init|;
name|from
operator|<
name|to
condition|;
operator|++
name|from
operator|,
operator|--
name|to
control|)
block|{
name|swap
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|rotate
specifier|final
name|void
name|rotate
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|mid
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
assert|assert
name|lo
operator|<=
name|mid
operator|&&
name|mid
operator|<=
name|hi
assert|;
if|if
condition|(
name|lo
operator|==
name|mid
operator|||
name|mid
operator|==
name|hi
condition|)
block|{
return|return;
block|}
name|doRotate
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
DECL|method|doRotate
name|void
name|doRotate
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|mid
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
if|if
condition|(
name|mid
operator|-
name|lo
operator|==
name|hi
operator|-
name|mid
condition|)
block|{
comment|// happens rarely but saves n/2 swaps
while|while
condition|(
name|mid
operator|<
name|hi
condition|)
block|{
name|swap
argument_list|(
name|lo
operator|++
argument_list|,
name|mid
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|reverse
argument_list|(
name|lo
argument_list|,
name|mid
argument_list|)
expr_stmt|;
name|reverse
argument_list|(
name|mid
argument_list|,
name|hi
argument_list|)
expr_stmt|;
name|reverse
argument_list|(
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|insertionSort
name|void
name|insertionSort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|from
operator|+
literal|1
init|;
name|i
operator|<
name|to
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|>
name|from
condition|;
operator|--
name|j
control|)
block|{
if|if
condition|(
name|compare
argument_list|(
name|j
operator|-
literal|1
argument_list|,
name|j
argument_list|)
operator|>
literal|0
condition|)
block|{
name|swap
argument_list|(
name|j
operator|-
literal|1
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
DECL|method|binarySort
name|void
name|binarySort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
name|binarySort
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|from
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|binarySort
name|void
name|binarySort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|int
name|i
parameter_list|)
block|{
for|for
control|(
init|;
name|i
operator|<
name|to
condition|;
operator|++
name|i
control|)
block|{
name|int
name|l
init|=
name|from
decl_stmt|;
name|int
name|h
init|=
name|i
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|l
operator|<=
name|h
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|l
operator|+
name|h
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|cmp
init|=
name|compare
argument_list|(
name|i
argument_list|,
name|mid
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|h
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|l
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|i
operator|-
name|l
condition|)
block|{
case|case
literal|2
case|:
name|swap
argument_list|(
name|l
operator|+
literal|1
argument_list|,
name|l
operator|+
literal|2
argument_list|)
expr_stmt|;
name|swap
argument_list|(
name|l
argument_list|,
name|l
operator|+
literal|1
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|swap
argument_list|(
name|l
argument_list|,
name|l
operator|+
literal|1
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0
case|:
break|break;
default|default:
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|>
name|l
condition|;
operator|--
name|j
control|)
block|{
name|swap
argument_list|(
name|j
operator|-
literal|1
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
DECL|method|heapSort
name|void
name|heapSort
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|-
name|from
operator|<=
literal|1
condition|)
block|{
return|return;
block|}
name|heapify
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|end
init|=
name|to
operator|-
literal|1
init|;
name|end
operator|>
name|from
condition|;
operator|--
name|end
control|)
block|{
name|swap
argument_list|(
name|from
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|siftDown
argument_list|(
name|from
argument_list|,
name|from
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|heapify
name|void
name|heapify
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|heapParent
argument_list|(
name|from
argument_list|,
name|to
operator|-
literal|1
argument_list|)
init|;
name|i
operator|>=
name|from
condition|;
operator|--
name|i
control|)
block|{
name|siftDown
argument_list|(
name|i
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|siftDown
name|void
name|siftDown
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
for|for
control|(
name|int
name|leftChild
init|=
name|heapChild
argument_list|(
name|from
argument_list|,
name|i
argument_list|)
init|;
name|leftChild
operator|<
name|to
condition|;
name|leftChild
operator|=
name|heapChild
argument_list|(
name|from
argument_list|,
name|i
argument_list|)
control|)
block|{
specifier|final
name|int
name|rightChild
init|=
name|leftChild
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|compare
argument_list|(
name|i
argument_list|,
name|leftChild
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|rightChild
operator|<
name|to
operator|&&
name|compare
argument_list|(
name|leftChild
argument_list|,
name|rightChild
argument_list|)
operator|<
literal|0
condition|)
block|{
name|swap
argument_list|(
name|i
argument_list|,
name|rightChild
argument_list|)
expr_stmt|;
name|i
operator|=
name|rightChild
expr_stmt|;
block|}
else|else
block|{
name|swap
argument_list|(
name|i
argument_list|,
name|leftChild
argument_list|)
expr_stmt|;
name|i
operator|=
name|leftChild
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|rightChild
operator|<
name|to
operator|&&
name|compare
argument_list|(
name|i
argument_list|,
name|rightChild
argument_list|)
operator|<
literal|0
condition|)
block|{
name|swap
argument_list|(
name|i
argument_list|,
name|rightChild
argument_list|)
expr_stmt|;
name|i
operator|=
name|rightChild
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
DECL|method|heapParent
specifier|static
name|int
name|heapParent
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
operator|(
name|i
operator|-
literal|1
operator|-
name|from
operator|)
operator|>>>
literal|1
operator|)
operator|+
name|from
return|;
block|}
DECL|method|heapChild
specifier|static
name|int
name|heapChild
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
operator|(
name|i
operator|-
name|from
operator|)
operator|<<
literal|1
operator|)
operator|+
literal|1
operator|+
name|from
return|;
block|}
block|}
end_class
end_unit
