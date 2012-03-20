begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * An FST {@link Outputs} implementation where each output  * is a non-negative long value.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PositiveIntOutputs
specifier|public
specifier|final
class|class
name|PositiveIntOutputs
extends|extends
name|Outputs
argument_list|<
name|Long
argument_list|>
block|{
DECL|field|NO_OUTPUT
specifier|private
specifier|final
specifier|static
name|Long
name|NO_OUTPUT
init|=
operator|new
name|Long
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|doShare
specifier|private
specifier|final
name|boolean
name|doShare
decl_stmt|;
DECL|field|singletonShare
specifier|private
specifier|final
specifier|static
name|PositiveIntOutputs
name|singletonShare
init|=
operator|new
name|PositiveIntOutputs
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|singletonNoShare
specifier|private
specifier|final
specifier|static
name|PositiveIntOutputs
name|singletonNoShare
init|=
operator|new
name|PositiveIntOutputs
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|PositiveIntOutputs
specifier|private
name|PositiveIntOutputs
parameter_list|(
name|boolean
name|doShare
parameter_list|)
block|{
name|this
operator|.
name|doShare
operator|=
name|doShare
expr_stmt|;
block|}
DECL|method|getSingleton
specifier|public
specifier|static
name|PositiveIntOutputs
name|getSingleton
parameter_list|(
name|boolean
name|doShare
parameter_list|)
block|{
return|return
name|doShare
condition|?
name|singletonShare
else|:
name|singletonNoShare
return|;
block|}
annotation|@
name|Override
DECL|method|common
specifier|public
name|Long
name|common
parameter_list|(
name|Long
name|output1
parameter_list|,
name|Long
name|output2
parameter_list|)
block|{
assert|assert
name|valid
argument_list|(
name|output1
argument_list|)
assert|;
assert|assert
name|valid
argument_list|(
name|output2
argument_list|)
assert|;
if|if
condition|(
name|output1
operator|==
name|NO_OUTPUT
operator|||
name|output2
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
elseif|else
if|if
condition|(
name|doShare
condition|)
block|{
assert|assert
name|output1
operator|>
literal|0
assert|;
assert|assert
name|output2
operator|>
literal|0
assert|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|output1
argument_list|,
name|output2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|output1
operator|.
name|equals
argument_list|(
name|output2
argument_list|)
condition|)
block|{
return|return
name|output1
return|;
block|}
else|else
block|{
return|return
name|NO_OUTPUT
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|Long
name|subtract
parameter_list|(
name|Long
name|output
parameter_list|,
name|Long
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
assert|assert
name|output
operator|>=
name|inc
assert|;
if|if
condition|(
name|inc
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|output
return|;
block|}
elseif|else
if|if
condition|(
name|output
operator|.
name|equals
argument_list|(
name|inc
argument_list|)
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
return|return
name|output
operator|-
name|inc
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Long
name|add
parameter_list|(
name|Long
name|prefix
parameter_list|,
name|Long
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
if|if
condition|(
name|prefix
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|output
return|;
block|}
elseif|else
if|if
condition|(
name|output
operator|==
name|NO_OUTPUT
condition|)
block|{
return|return
name|prefix
return|;
block|}
else|else
block|{
return|return
name|prefix
operator|+
name|output
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
name|Long
name|output
parameter_list|,
name|DataOutput
name|out
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
name|out
operator|.
name|writeVLong
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|Long
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|v
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
return|return
name|v
return|;
block|}
block|}
DECL|method|valid
specifier|private
name|boolean
name|valid
parameter_list|(
name|Long
name|o
parameter_list|)
block|{
assert|assert
name|o
operator|!=
literal|null
assert|;
assert|assert
name|o
operator|instanceof
name|Long
assert|;
assert|assert
name|o
operator|==
name|NO_OUTPUT
operator|||
name|o
operator|>
literal|0
assert|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|Long
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
name|Long
name|output
parameter_list|)
block|{
return|return
name|output
operator|.
name|toString
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
literal|"PositiveIntOutputs(doShare="
operator|+
name|doShare
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
