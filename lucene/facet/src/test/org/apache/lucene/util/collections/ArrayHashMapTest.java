begin_unit
begin_package
DECL|package|org.apache.lucene.util.collections
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|collections
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetTestCase
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|ArrayHashMapTest
specifier|public
class|class
name|ArrayHashMapTest
extends|extends
name|FacetTestCase
block|{
DECL|field|RANDOM_TEST_NUM_ITERATIONS
specifier|public
specifier|static
specifier|final
name|int
name|RANDOM_TEST_NUM_ITERATIONS
init|=
literal|100
decl_stmt|;
comment|// set to 100,000 for deeper test
annotation|@
name|Test
DECL|method|test0
specifier|public
name|void
name|test0
parameter_list|()
block|{
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
literal|100
operator|+
name|i
decl_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|map
operator|.
name|size
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
operator|+
name|i
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|90
condition|;
operator|++
name|i
control|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|map
operator|.
name|size
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|i
argument_list|)
argument_list|,
operator|!
operator|(
name|i
operator|>=
literal|10
operator|&&
name|i
operator|<
literal|90
operator|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<
literal|85
condition|;
operator|++
name|i
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|5
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|95
argument_list|,
name|map
operator|.
name|size
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|i
argument_list|)
argument_list|,
operator|!
operator|(
name|i
operator|>=
literal|85
operator|&&
name|i
operator|<
literal|90
operator|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
operator|(
literal|100
operator|+
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<
literal|85
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
operator|(
literal|5
operator|+
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|90
init|;
name|i
operator|<
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
operator|(
literal|100
operator|+
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|test1
specifier|public
name|void
name|test1
parameter_list|()
block|{
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|100
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
init|=
name|map
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|set
operator|.
name|size
argument_list|()
argument_list|,
name|map
operator|.
name|size
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|100
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
init|=
name|map
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Integer
name|integer
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|integer
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|set
operator|.
name|add
argument_list|(
name|integer
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|set
operator|.
name|size
argument_list|()
argument_list|,
name|map
operator|.
name|size
argument_list|()
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
literal|100
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|100
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|test2
specifier|public
name|void
name|test2
parameter_list|()
block|{
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
literal|128
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|i
operator|*
literal|4096
decl_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|map
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|map
operator|.
name|size
argument_list|()
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
literal|128
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|*
literal|4096
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|64
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|128
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|*
literal|4096
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|map
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test3
specifier|public
name|void
name|test3
parameter_list|()
block|{
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|length
init|=
literal|100
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|i
operator|*
literal|64
argument_list|,
literal|100
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|keySet
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iit
init|=
name|map
operator|.
name|keyIterator
argument_list|()
init|;
name|iit
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|keySet
operator|.
name|add
argument_list|(
name|iit
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|length
argument_list|,
name|keySet
operator|.
name|size
argument_list|()
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|keySet
operator|.
name|contains
argument_list|(
name|i
operator|*
literal|64
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|valueSet
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iit
init|=
name|map
operator|.
name|iterator
argument_list|()
init|;
name|iit
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|valueSet
operator|.
name|add
argument_list|(
name|iit
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|length
argument_list|,
name|valueSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Object
index|[]
name|array
init|=
name|map
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|array
control|)
block|{
name|assertTrue
argument_list|(
name|valueSet
operator|.
name|contains
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Integer
index|[]
name|array2
init|=
operator|new
name|Integer
index|[
literal|80
index|]
decl_stmt|;
name|array2
operator|=
name|map
operator|.
name|toArray
argument_list|(
name|array2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|value
range|:
name|array2
control|)
block|{
name|assertTrue
argument_list|(
name|valueSet
operator|.
name|contains
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Integer
index|[]
name|array3
init|=
operator|new
name|Integer
index|[
literal|120
index|]
decl_stmt|;
name|array3
operator|=
name|map
operator|.
name|toArray
argument_list|(
name|array3
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|valueSet
operator|.
name|contains
argument_list|(
name|array3
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|array3
index|[
name|length
index|]
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|map
operator|.
name|containsValue
argument_list|(
name|i
operator|+
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|i
operator|*
literal|64
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iit
init|=
name|map
operator|.
name|keyIterator
argument_list|()
init|;
name|iit
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|iit
operator|.
name|next
argument_list|()
expr_stmt|;
name|iit
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|map
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// now with random data.. and lots of it
annotation|@
name|Test
DECL|method|test4
specifier|public
name|void
name|test4
parameter_list|()
block|{
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|RANDOM_TEST_NUM_ITERATIONS
decl_stmt|;
comment|// for a repeatable random sequence
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|i
operator|*
literal|128
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|length
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// now repeat
name|random
operator|.
name|setSeed
argument_list|(
name|seed
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|int
name|value
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
name|i
operator|*
literal|128
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|,
name|map
operator|.
name|remove
argument_list|(
name|i
operator|*
literal|128
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Float
argument_list|>
name|map1
init|=
operator|new
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Float
argument_list|>
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Float
argument_list|>
name|map2
init|=
operator|new
name|ArrayHashMap
argument_list|<
name|Integer
argument_list|,
name|Float
argument_list|>
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Empty maps should be equal"
argument_list|,
name|map1
argument_list|,
name|map2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashCode() for empty maps should be equal"
argument_list|,
name|map1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|map2
operator|.
name|hashCode
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|map1
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
literal|1f
operator|/
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|map2
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
literal|1f
operator|/
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Identical maps should be equal"
argument_list|,
name|map1
argument_list|,
name|map2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashCode() for identical maps should be equal"
argument_list|,
name|map1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|map2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|map1
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"Different maps should not be equal"
argument_list|,
name|map1
operator|.
name|equals
argument_list|(
name|map2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|19
init|;
name|i
operator|>=
literal|10
condition|;
operator|--
name|i
control|)
block|{
name|map2
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Identical maps should be equal"
argument_list|,
name|map1
argument_list|,
name|map2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashCode() for identical maps should be equal"
argument_list|,
name|map1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|map2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|map1
operator|.
name|put
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1f
argument_list|)
expr_stmt|;
name|map2
operator|.
name|put
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1.1f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different maps should not be equal"
argument_list|,
name|map1
operator|.
name|equals
argument_list|(
name|map2
argument_list|)
argument_list|)
expr_stmt|;
name|map2
operator|.
name|put
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Identical maps should be equal"
argument_list|,
name|map1
argument_list|,
name|map2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashCode() for identical maps should be equal"
argument_list|,
name|map1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|map2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
