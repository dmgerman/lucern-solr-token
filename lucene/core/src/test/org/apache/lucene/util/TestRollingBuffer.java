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
name|Random
import|;
end_import
begin_class
DECL|class|TestRollingBuffer
specifier|public
class|class
name|TestRollingBuffer
extends|extends
name|LuceneTestCase
block|{
DECL|class|Position
specifier|private
specifier|static
class|class
name|Position
implements|implements
name|RollingBuffer
operator|.
name|Resettable
block|{
DECL|field|pos
specifier|public
name|int
name|pos
decl_stmt|;
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|pos
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
specifier|final
name|RollingBuffer
argument_list|<
name|Position
argument_list|>
name|buffer
init|=
operator|new
name|RollingBuffer
argument_list|<
name|Position
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Position
name|newInstance
parameter_list|()
block|{
specifier|final
name|Position
name|pos
init|=
operator|new
name|Position
argument_list|()
decl_stmt|;
name|pos
operator|.
name|pos
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|pos
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|freeBeforePos
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|maxPos
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
name|posSet
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxPos
operator|+
literal|1000
argument_list|)
decl_stmt|;
name|int
name|posUpto
init|=
literal|0
decl_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
while|while
condition|(
name|freeBeforePos
operator|<
name|maxPos
condition|)
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|1
condition|)
block|{
specifier|final
name|int
name|limit
init|=
name|rarely
argument_list|()
condition|?
literal|1000
else|:
literal|20
decl_stmt|;
specifier|final
name|int
name|inc
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|limit
argument_list|)
decl_stmt|;
specifier|final
name|int
name|pos
init|=
name|freeBeforePos
operator|+
name|inc
decl_stmt|;
name|posUpto
operator|=
name|Math
operator|.
name|max
argument_list|(
name|posUpto
argument_list|,
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  check pos="
operator|+
name|pos
operator|+
literal|" posUpto="
operator|+
name|posUpto
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Position
name|posData
init|=
name|buffer
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|posSet
operator|.
name|getAndSet
argument_list|(
name|pos
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|posData
operator|.
name|pos
argument_list|)
expr_stmt|;
name|posData
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|posData
operator|.
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|posUpto
operator|>
name|freeBeforePos
condition|)
block|{
name|freeBeforePos
operator|+=
name|random
operator|.
name|nextInt
argument_list|(
name|posUpto
operator|-
name|freeBeforePos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  freeBeforePos="
operator|+
name|freeBeforePos
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|freeBefore
argument_list|(
name|freeBeforePos
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
