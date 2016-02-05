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
name|ArrayList
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
name|List
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
import|;
end_import
begin_class
DECL|class|TestMergedIterator
specifier|public
class|class
name|TestMergedIterator
extends|extends
name|LuceneTestCase
block|{
DECL|field|REPEATS
specifier|private
specifier|static
specifier|final
name|int
name|REPEATS
init|=
literal|2
decl_stmt|;
DECL|field|VALS_TO_MERGE
specifier|private
specifier|static
specifier|final
name|int
name|VALS_TO_MERGE
init|=
literal|15000
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|testMergeEmpty
specifier|public
name|void
name|testMergeEmpty
parameter_list|()
block|{
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|merged
init|=
operator|new
name|MergedIterator
argument_list|<>
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|merged
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|merged
operator|=
operator|new
name|MergedIterator
argument_list|<>
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|merged
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
index|[]
name|itrs
init|=
operator|new
name|Iterator
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
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
name|itrs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|itrs
index|[
name|i
index|]
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|merged
operator|=
operator|new
name|MergedIterator
argument_list|<>
argument_list|(
name|itrs
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|merged
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testNoDupsRemoveDups
specifier|public
name|void
name|testNoDupsRemoveDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testOffItrDupsRemoveDups
specifier|public
name|void
name|testOffItrDupsRemoveDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testOnItrDupsRemoveDups
specifier|public
name|void
name|testOnItrDupsRemoveDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testOnItrRandomDupsRemoveDups
specifier|public
name|void
name|testOnItrRandomDupsRemoveDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|1
argument_list|,
operator|-
literal|3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testBothDupsRemoveDups
specifier|public
name|void
name|testBothDupsRemoveDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|3
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testBothDupsWithRandomDupsRemoveDups
specifier|public
name|void
name|testBothDupsWithRandomDupsRemoveDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|3
argument_list|,
operator|-
literal|3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testNoDupsKeepDups
specifier|public
name|void
name|testNoDupsKeepDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testOffItrDupsKeepDups
specifier|public
name|void
name|testOffItrDupsKeepDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testOnItrDupsKeepDups
specifier|public
name|void
name|testOnItrDupsKeepDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testOnItrRandomDupsKeepDups
specifier|public
name|void
name|testOnItrRandomDupsKeepDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|1
argument_list|,
operator|-
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testBothDupsKeepDups
specifier|public
name|void
name|testBothDupsKeepDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|3
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
name|REPEATS
argument_list|)
DECL|method|testBothDupsWithRandomDupsKeepDups
specifier|public
name|void
name|testBothDupsWithRandomDupsKeepDups
parameter_list|()
block|{
name|testCase
argument_list|(
literal|3
argument_list|,
operator|-
literal|3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testCase
specifier|private
name|void
name|testCase
parameter_list|(
name|int
name|itrsWithVal
parameter_list|,
name|int
name|specifiedValsOnItr
parameter_list|,
name|boolean
name|removeDups
parameter_list|)
block|{
comment|// Build a random number of lists
name|List
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numLists
init|=
name|itrsWithVal
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|1000
operator|-
name|itrsWithVal
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
name|List
argument_list|<
name|Integer
argument_list|>
index|[]
name|lists
init|=
operator|new
name|List
index|[
name|numLists
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
name|numLists
condition|;
name|i
operator|++
control|)
block|{
name|lists
index|[
name|i
index|]
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|int
name|start
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|VALS_TO_MERGE
operator|/
name|itrsWithVal
operator|/
name|Math
operator|.
name|abs
argument_list|(
name|specifiedValsOnItr
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
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
name|maxList
init|=
name|lists
operator|.
name|length
decl_stmt|;
name|int
name|maxValsOnItr
init|=
literal|0
decl_stmt|;
name|int
name|sumValsOnItr
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|itrWithVal
init|=
literal|0
init|;
name|itrWithVal
operator|<
name|itrsWithVal
condition|;
name|itrWithVal
operator|++
control|)
block|{
name|int
name|list
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxList
argument_list|)
decl_stmt|;
name|int
name|valsOnItr
init|=
name|specifiedValsOnItr
operator|<
literal|0
condition|?
operator|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
operator|-
name|specifiedValsOnItr
argument_list|)
operator|)
else|:
name|specifiedValsOnItr
decl_stmt|;
name|maxValsOnItr
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxValsOnItr
argument_list|,
name|valsOnItr
argument_list|)
expr_stmt|;
name|sumValsOnItr
operator|+=
name|valsOnItr
expr_stmt|;
for|for
control|(
name|int
name|valOnItr
init|=
literal|0
init|;
name|valOnItr
operator|<
name|valsOnItr
condition|;
name|valOnItr
operator|++
control|)
block|{
name|lists
index|[
name|list
index|]
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|maxList
operator|=
name|maxList
operator|-
literal|1
expr_stmt|;
name|ArrayUtil
operator|.
name|swap
argument_list|(
name|lists
argument_list|,
name|list
argument_list|,
name|maxList
argument_list|)
expr_stmt|;
block|}
name|int
name|maxCount
init|=
name|removeDups
condition|?
name|maxValsOnItr
else|:
name|sumValsOnItr
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|maxCount
condition|;
name|count
operator|++
control|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now check that they get merged cleanly
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
name|Iterator
argument_list|<
name|Integer
argument_list|>
index|[]
name|itrs
init|=
operator|new
name|Iterator
index|[
name|numLists
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
name|numLists
condition|;
name|i
operator|++
control|)
block|{
name|itrs
index|[
name|i
index|]
operator|=
name|lists
index|[
name|i
index|]
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|MergedIterator
argument_list|<
name|Integer
argument_list|>
name|mergedItr
init|=
operator|new
name|MergedIterator
argument_list|<>
argument_list|(
name|removeDups
argument_list|,
name|itrs
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|expectedItr
init|=
name|expected
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|expectedItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|mergedItr
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedItr
operator|.
name|next
argument_list|()
argument_list|,
name|mergedItr
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|mergedItr
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
