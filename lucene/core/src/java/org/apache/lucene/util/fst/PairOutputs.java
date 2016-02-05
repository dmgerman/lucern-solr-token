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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/**  * An FST {@link Outputs} implementation, holding two other outputs.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PairOutputs
specifier|public
class|class
name|PairOutputs
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
extends|extends
name|Outputs
argument_list|<
name|PairOutputs
operator|.
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
argument_list|>
block|{
DECL|field|NO_OUTPUT
specifier|private
specifier|final
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|NO_OUTPUT
decl_stmt|;
DECL|field|outputs1
specifier|private
specifier|final
name|Outputs
argument_list|<
name|A
argument_list|>
name|outputs1
decl_stmt|;
DECL|field|outputs2
specifier|private
specifier|final
name|Outputs
argument_list|<
name|B
argument_list|>
name|outputs2
decl_stmt|;
comment|/** Holds a single pair of two outputs. */
DECL|class|Pair
specifier|public
specifier|static
class|class
name|Pair
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
block|{
DECL|field|output1
specifier|public
specifier|final
name|A
name|output1
decl_stmt|;
DECL|field|output2
specifier|public
specifier|final
name|B
name|output2
decl_stmt|;
comment|// use newPair
DECL|method|Pair
specifier|private
name|Pair
parameter_list|(
name|A
name|output1
parameter_list|,
name|B
name|output2
parameter_list|)
block|{
name|this
operator|.
name|output1
operator|=
name|output1
expr_stmt|;
name|this
operator|.
name|output2
operator|=
name|output2
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|instanceof
name|Pair
condition|)
block|{
name|Pair
name|pair
init|=
operator|(
name|Pair
operator|)
name|other
decl_stmt|;
return|return
name|output1
operator|.
name|equals
argument_list|(
name|pair
operator|.
name|output1
argument_list|)
operator|&&
name|output2
operator|.
name|equals
argument_list|(
name|pair
operator|.
name|output2
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|output1
operator|.
name|hashCode
argument_list|()
operator|+
name|output2
operator|.
name|hashCode
argument_list|()
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
literal|"Pair("
operator|+
name|output1
operator|+
literal|","
operator|+
name|output2
operator|+
literal|")"
return|;
block|}
block|}
empty_stmt|;
DECL|method|PairOutputs
specifier|public
name|PairOutputs
parameter_list|(
name|Outputs
argument_list|<
name|A
argument_list|>
name|outputs1
parameter_list|,
name|Outputs
argument_list|<
name|B
argument_list|>
name|outputs2
parameter_list|)
block|{
name|this
operator|.
name|outputs1
operator|=
name|outputs1
expr_stmt|;
name|this
operator|.
name|outputs2
operator|=
name|outputs2
expr_stmt|;
name|NO_OUTPUT
operator|=
operator|new
name|Pair
argument_list|<>
argument_list|(
name|outputs1
operator|.
name|getNoOutput
argument_list|()
argument_list|,
name|outputs2
operator|.
name|getNoOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new Pair */
DECL|method|newPair
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|newPair
parameter_list|(
name|A
name|a
parameter_list|,
name|B
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|equals
argument_list|(
name|outputs1
operator|.
name|getNoOutput
argument_list|()
argument_list|)
condition|)
block|{
name|a
operator|=
name|outputs1
operator|.
name|getNoOutput
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|.
name|equals
argument_list|(
name|outputs2
operator|.
name|getNoOutput
argument_list|()
argument_list|)
condition|)
block|{
name|b
operator|=
name|outputs2
operator|.
name|getNoOutput
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|==
name|outputs1
operator|.
name|getNoOutput
argument_list|()
operator|&&
name|b
operator|==
name|outputs2
operator|.
name|getNoOutput
argument_list|()
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
specifier|final
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|p
init|=
operator|new
name|Pair
argument_list|<>
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
decl_stmt|;
assert|assert
name|valid
argument_list|(
name|p
argument_list|)
assert|;
return|return
name|p
return|;
block|}
block|}
comment|// for assert
DECL|method|valid
specifier|private
name|boolean
name|valid
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|pair
parameter_list|)
block|{
specifier|final
name|boolean
name|noOutput1
init|=
name|pair
operator|.
name|output1
operator|.
name|equals
argument_list|(
name|outputs1
operator|.
name|getNoOutput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|noOutput2
init|=
name|pair
operator|.
name|output2
operator|.
name|equals
argument_list|(
name|outputs2
operator|.
name|getNoOutput
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|noOutput1
operator|&&
name|pair
operator|.
name|output1
operator|!=
name|outputs1
operator|.
name|getNoOutput
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|noOutput2
operator|&&
name|pair
operator|.
name|output2
operator|!=
name|outputs2
operator|.
name|getNoOutput
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|noOutput1
operator|&&
name|noOutput2
condition|)
block|{
if|if
condition|(
name|pair
operator|!=
name|NO_OUTPUT
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|common
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|common
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|pair1
parameter_list|,
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|pair2
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|pair1
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|pair2
argument_list|)
assert|;
return|return
name|newPair
argument_list|(
name|outputs1
operator|.
name|common
argument_list|(
name|pair1
operator|.
name|output1
argument_list|,
name|pair2
operator|.
name|output1
argument_list|)
argument_list|,
name|outputs2
operator|.
name|common
argument_list|(
name|pair1
operator|.
name|output2
argument_list|,
name|pair2
operator|.
name|output2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|subtract
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|,
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|inc
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|output
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|inc
argument_list|)
assert|;
return|return
name|newPair
argument_list|(
name|outputs1
operator|.
name|subtract
argument_list|(
name|output
operator|.
name|output1
argument_list|,
name|inc
operator|.
name|output1
argument_list|)
argument_list|,
name|outputs2
operator|.
name|subtract
argument_list|(
name|output
operator|.
name|output2
argument_list|,
name|inc
operator|.
name|output2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|add
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|prefix
parameter_list|,
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|prefix
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|output
argument_list|)
assert|;
return|return
name|newPair
argument_list|(
name|outputs1
operator|.
name|add
argument_list|(
name|prefix
operator|.
name|output1
argument_list|,
name|output
operator|.
name|output1
argument_list|)
argument_list|,
name|outputs2
operator|.
name|add
argument_list|(
name|prefix
operator|.
name|output2
argument_list|,
name|output
operator|.
name|output2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|,
name|DataOutput
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|valid
argument_list|(
name|output
argument_list|)
assert|;
name|outputs1
operator|.
name|write
argument_list|(
name|output
operator|.
name|output1
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|outputs2
operator|.
name|write
argument_list|(
name|output
operator|.
name|output2
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|A
name|output1
init|=
name|outputs1
operator|.
name|read
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|B
name|output2
init|=
name|outputs2
operator|.
name|read
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|newPair
argument_list|(
name|output1
argument_list|,
name|output2
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
name|outputs1
operator|.
name|skipOutput
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|outputs2
operator|.
name|skipOutput
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|getNoOutput
parameter_list|()
block|{
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|outputToString
specifier|public
name|String
name|outputToString
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|output
argument_list|)
assert|;
return|return
literal|"<pair:"
operator|+
name|outputs1
operator|.
name|outputToString
argument_list|(
name|output
operator|.
name|output1
argument_list|)
operator|+
literal|","
operator|+
name|outputs2
operator|.
name|outputToString
argument_list|(
name|output
operator|.
name|output2
argument_list|)
operator|+
literal|">"
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
literal|"PairOutputs<"
operator|+
name|outputs1
operator|+
literal|","
operator|+
name|outputs2
operator|+
literal|">"
return|;
block|}
DECL|field|BASE_NUM_BYTES
specifier|private
specifier|static
specifier|final
name|long
name|BASE_NUM_BYTES
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
operator|new
name|Pair
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|)
block|{
name|long
name|ramBytesUsed
init|=
name|BASE_NUM_BYTES
decl_stmt|;
if|if
condition|(
name|output
operator|.
name|output1
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|+=
name|outputs1
operator|.
name|ramBytesUsed
argument_list|(
name|output
operator|.
name|output1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|output
operator|.
name|output2
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|+=
name|outputs2
operator|.
name|ramBytesUsed
argument_list|(
name|output
operator|.
name|output2
argument_list|)
expr_stmt|;
block|}
return|return
name|ramBytesUsed
return|;
block|}
block|}
end_class
end_unit
