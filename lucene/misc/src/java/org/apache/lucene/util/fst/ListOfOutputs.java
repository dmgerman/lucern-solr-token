begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
package|;
end_package
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|DataOutput
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
name|IntsRef
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/**  * Wraps another Outputs implementation and encodes one or  * more of its output values.  You can use this when a single  * input may need to map to more than one output,  * maintaining order: pass the same input with a different  * output by calling {@link Builder#add(IntsRef,Object)} multiple  * times.  The builder will then combine the outputs using  * the {@link Outputs#merge(Object,Object)} method.  *  *<p>The resulting FST may not be minimal when an input has  * more than one output, as this requires pushing all  * multi-output values to a final state.  *  *<p>NOTE: the only way to create multiple outputs is to  * add the same input to the FST multiple times in a row.  This is  * how the FST maps a single input to multiple outputs (e.g. you  * cannot pass a List&lt;Object&gt; to {@link Builder#add}).  If  * your outputs are longs, and you need at most 2, then use  * {@link UpToTwoPositiveIntOutputs} instead since it stores  * the outputs more compactly (by stealing a bit from each  * long value).  *  *<p>NOTE: this cannot wrap itself (ie you cannot make an  * FST with List&lt;List&lt;Object&gt;&gt; outputs using this).  *  * @lucene.experimental  */
end_comment
begin_comment
comment|// NOTE: i think we could get a more compact FST if, instead
end_comment
begin_comment
comment|// of adding the same input multiple times with a different
end_comment
begin_comment
comment|// output each time, we added it only once with a
end_comment
begin_comment
comment|// pre-constructed List<T> output.  This way the "multiple
end_comment
begin_comment
comment|// values" is fully opaque to the Builder/FST.  It would
end_comment
begin_comment
comment|// require implementing the full algebra using set
end_comment
begin_comment
comment|// arithmetic (I think?); maybe SetOfOutputs is a good name.
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|ListOfOutputs
specifier|public
specifier|final
class|class
name|ListOfOutputs
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Outputs
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|outputs
specifier|private
specifier|final
name|Outputs
argument_list|<
name|T
argument_list|>
name|outputs
decl_stmt|;
DECL|method|ListOfOutputs
specifier|public
name|ListOfOutputs
parameter_list|(
name|Outputs
argument_list|<
name|T
argument_list|>
name|outputs
parameter_list|)
block|{
name|this
operator|.
name|outputs
operator|=
name|outputs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|common
specifier|public
name|Object
name|common
parameter_list|(
name|Object
name|output1
parameter_list|,
name|Object
name|output2
parameter_list|)
block|{
comment|// These will never be a list:
return|return
name|outputs
operator|.
name|common
argument_list|(
operator|(
name|T
operator|)
name|output1
argument_list|,
operator|(
name|T
operator|)
name|output2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|Object
name|subtract
parameter_list|(
name|Object
name|object
parameter_list|,
name|Object
name|inc
parameter_list|)
block|{
comment|// These will never be a list:
return|return
name|outputs
operator|.
name|subtract
argument_list|(
operator|(
name|T
operator|)
name|object
argument_list|,
operator|(
name|T
operator|)
name|inc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Object
name|add
parameter_list|(
name|Object
name|prefix
parameter_list|,
name|Object
name|output
parameter_list|)
block|{
assert|assert
operator|!
operator|(
name|prefix
operator|instanceof
name|List
operator|)
assert|;
if|if
condition|(
operator|!
operator|(
name|output
operator|instanceof
name|List
operator|)
condition|)
block|{
return|return
name|outputs
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|prefix
argument_list|,
operator|(
name|T
operator|)
name|output
argument_list|)
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|T
argument_list|>
name|outputList
init|=
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|output
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|addedList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|outputList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|T
name|_output
range|:
name|outputList
control|)
block|{
name|addedList
operator|.
name|add
argument_list|(
name|outputs
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|prefix
argument_list|,
name|_output
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|addedList
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Object
name|output
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
operator|(
name|output
operator|instanceof
name|List
operator|)
assert|;
name|outputs
operator|.
name|write
argument_list|(
operator|(
name|T
operator|)
name|output
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFinalOutput
specifier|public
name|void
name|writeFinalOutput
parameter_list|(
name|Object
name|output
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|output
operator|instanceof
name|List
operator|)
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|outputs
operator|.
name|write
argument_list|(
operator|(
name|T
operator|)
name|output
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|T
argument_list|>
name|outputList
init|=
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|output
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|outputList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|T
name|eachOutput
range|:
name|outputList
control|)
block|{
name|outputs
operator|.
name|write
argument_list|(
name|eachOutput
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|Object
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|outputs
operator|.
name|read
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|skipOutput
specifier|public
name|void
name|skipOutput
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|outputs
operator|.
name|skipOutput
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFinalOutput
specifier|public
name|Object
name|readFinalOutput
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
return|return
name|outputs
operator|.
name|read
argument_list|(
name|in
argument_list|)
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|T
argument_list|>
name|outputList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|outputList
operator|.
name|add
argument_list|(
name|outputs
operator|.
name|read
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|outputList
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|skipFinalOutput
specifier|public
name|void
name|skipFinalOutput
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|outputs
operator|.
name|skipOutput
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|Object
name|getNoOutput
parameter_list|()
block|{
return|return
name|outputs
operator|.
name|getNoOutput
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|outputToString
specifier|public
name|String
name|outputToString
parameter_list|(
name|Object
name|output
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|output
operator|instanceof
name|List
operator|)
condition|)
block|{
return|return
name|outputs
operator|.
name|outputToString
argument_list|(
operator|(
name|T
operator|)
name|output
argument_list|)
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|T
argument_list|>
name|outputList
init|=
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|output
decl_stmt|;
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
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
name|outputList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|outputs
operator|.
name|outputToString
argument_list|(
name|outputList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|Object
name|merge
parameter_list|(
name|Object
name|first
parameter_list|,
name|Object
name|second
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|outputList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|first
operator|instanceof
name|List
operator|)
condition|)
block|{
name|outputList
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|first
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outputList
operator|.
name|addAll
argument_list|(
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|first
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|second
operator|instanceof
name|List
operator|)
condition|)
block|{
name|outputList
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|second
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outputList
operator|.
name|addAll
argument_list|(
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|second
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("MERGE: now " + outputList.size() + " first=" + outputToString(first) + " second=" + outputToString(second));
comment|//System.out.println("  return " + outputToString(outputList));
return|return
name|outputList
return|;
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
literal|"OneOrMoreOutputs("
operator|+
name|outputs
operator|+
literal|")"
return|;
block|}
DECL|method|asList
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|asList
parameter_list|(
name|Object
name|output
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|output
operator|instanceof
name|List
operator|)
condition|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|output
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
else|else
block|{
return|return
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|output
return|;
block|}
block|}
DECL|field|BASE_LIST_NUM_BYTES
specifier|private
specifier|static
specifier|final
name|long
name|BASE_LIST_NUM_BYTES
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|(
name|Object
name|output
parameter_list|)
block|{
name|long
name|bytes
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|output
operator|instanceof
name|List
condition|)
block|{
name|bytes
operator|+=
name|BASE_LIST_NUM_BYTES
expr_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|outputList
init|=
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|output
decl_stmt|;
for|for
control|(
name|T
name|_output
range|:
name|outputList
control|)
block|{
name|bytes
operator|+=
name|outputs
operator|.
name|ramBytesUsed
argument_list|(
name|_output
argument_list|)
expr_stmt|;
block|}
comment|// 2 * to allow for ArrayList's oversizing:
name|bytes
operator|+=
literal|2
operator|*
name|outputList
operator|.
name|size
argument_list|()
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
expr_stmt|;
block|}
else|else
block|{
name|bytes
operator|+=
name|outputs
operator|.
name|ramBytesUsed
argument_list|(
operator|(
name|T
operator|)
name|output
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
block|}
end_class
end_unit
