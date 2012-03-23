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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestIdentityHashSet
specifier|public
class|class
name|TestIdentityHashSet
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testCheck
specifier|public
name|void
name|testCheck
parameter_list|()
block|{
name|Random
name|rnd
init|=
name|random
decl_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|jdk
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|Object
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|RamUsageEstimator
operator|.
name|IdentityHashSet
argument_list|<
name|Object
argument_list|>
name|us
init|=
operator|new
name|RamUsageEstimator
operator|.
name|IdentityHashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|max
init|=
literal|100000
decl_stmt|;
name|int
name|threshold
init|=
literal|256
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
name|max
condition|;
name|i
operator|++
control|)
block|{
comment|// some of these will be interned and some will not so there will be collisions.
name|Integer
name|v
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
name|threshold
argument_list|)
decl_stmt|;
name|boolean
name|e1
init|=
name|jdk
operator|.
name|contains
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|boolean
name|e2
init|=
name|us
operator|.
name|contains
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|)
expr_stmt|;
name|e1
operator|=
name|jdk
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|e2
operator|=
name|us
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|Object
argument_list|>
name|collected
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|Object
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|us
control|)
block|{
name|collected
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|collected
argument_list|,
name|jdk
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
