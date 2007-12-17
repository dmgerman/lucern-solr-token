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
name|Random
import|;
end_import
begin_class
DECL|class|TestPriorityQueue
specifier|public
class|class
name|TestPriorityQueue
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestPriorityQueue
specifier|public
name|TestPriorityQueue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|class|IntegerQueue
specifier|private
specifier|static
class|class
name|IntegerQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|IntegerQueue
specifier|public
name|IntegerQueue
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|initialize
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Integer
operator|)
name|a
operator|)
operator|.
name|intValue
argument_list|()
operator|<
operator|(
operator|(
name|Integer
operator|)
name|b
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
block|}
DECL|method|testPQ
specifier|public
name|void
name|testPQ
parameter_list|()
throws|throws
name|Exception
block|{
name|testPQ
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|method|testPQ
specifier|public
specifier|static
name|void
name|testPQ
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|PriorityQueue
name|pq
init|=
operator|new
name|IntegerQueue
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|Random
name|gen
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|sum
init|=
literal|0
decl_stmt|,
name|sum2
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|next
init|=
name|gen
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|sum
operator|+=
name|next
expr_stmt|;
name|pq
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
name|next
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//      Date end = new Date();
comment|//      System.out.print(((float)(end.getTime()-start.getTime()) / count) * 1000);
comment|//      System.out.println(" microseconds/put");
comment|//      start = new Date();
name|int
name|last
init|=
name|Integer
operator|.
name|MIN_VALUE
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
name|Integer
name|next
init|=
operator|(
name|Integer
operator|)
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|next
operator|.
name|intValue
argument_list|()
operator|>=
name|last
argument_list|)
expr_stmt|;
name|last
operator|=
name|next
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|sum2
operator|+=
name|last
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|sum
argument_list|,
name|sum2
argument_list|)
expr_stmt|;
comment|//      end = new Date();
comment|//      System.out.print(((float)(end.getTime()-start.getTime()) / count) * 1000);
comment|//      System.out.println(" microseconds/pop");
block|}
DECL|method|testClear
specifier|public
name|void
name|testClear
parameter_list|()
block|{
name|PriorityQueue
name|pq
init|=
operator|new
name|IntegerQueue
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|pq
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|pq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|pq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFixedSize
specifier|public
name|void
name|testFixedSize
parameter_list|()
block|{
name|PriorityQueue
name|pq
init|=
operator|new
name|IntegerQueue
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|pq
operator|.
name|insert
argument_list|(
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|insert
argument_list|(
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|insert
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|insert
argument_list|(
operator|new
name|Integer
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|insert
argument_list|(
operator|new
name|Integer
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|insert
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|pq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|pq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInsertWithOverflow
specifier|public
name|void
name|testInsertWithOverflow
parameter_list|()
block|{
name|int
name|size
init|=
literal|4
decl_stmt|;
name|PriorityQueue
name|pq
init|=
operator|new
name|IntegerQueue
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|Integer
name|i1
init|=
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Integer
name|i2
init|=
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|Integer
name|i3
init|=
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Integer
name|i4
init|=
operator|new
name|Integer
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Integer
name|i5
init|=
operator|new
name|Integer
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|Integer
name|i6
init|=
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|i1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|i2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|i3
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|i4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|i5
argument_list|)
operator|==
name|i3
argument_list|)
expr_stmt|;
comment|// i3 should have been dropped
name|assertTrue
argument_list|(
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|i6
argument_list|)
operator|==
name|i6
argument_list|)
expr_stmt|;
comment|// i6 should not have been inserted
name|assertEquals
argument_list|(
name|size
argument_list|,
name|pq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|pq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
