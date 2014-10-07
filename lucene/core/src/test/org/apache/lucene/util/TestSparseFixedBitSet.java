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
name|BitSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
begin_class
DECL|class|TestSparseFixedBitSet
specifier|public
class|class
name|TestSparseFixedBitSet
extends|extends
name|BaseDocIdSetTestCase
argument_list|<
name|SparseFixedBitSet
argument_list|>
block|{
annotation|@
name|Override
DECL|method|copyOf
specifier|public
name|SparseFixedBitSet
name|copyOf
parameter_list|(
name|BitSet
name|bs
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SparseFixedBitSet
name|set
init|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|length
argument_list|)
decl_stmt|;
comment|// SparseFixedBitSet can be sensitive to the order of insertion so
comment|// randomize insertion a bit
name|List
argument_list|<
name|Integer
argument_list|>
name|buffer
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|bs
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|doc
operator|!=
operator|-
literal|1
condition|;
name|doc
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
control|)
block|{
name|buffer
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|size
argument_list|()
operator|>=
literal|100000
condition|)
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|buffer
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
range|:
name|buffer
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|buffer
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
range|:
name|buffer
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
annotation|@
name|Override
DECL|method|assertEquals
specifier|public
name|void
name|assertEquals
parameter_list|(
name|int
name|numBits
parameter_list|,
name|BitSet
name|ds1
parameter_list|,
name|SparseFixedBitSet
name|ds2
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numBits
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|ds1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|ds2
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
name|ds1
operator|.
name|cardinality
argument_list|()
argument_list|,
name|ds2
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|ds1
argument_list|,
name|ds2
argument_list|)
expr_stmt|;
block|}
DECL|method|testApproximateCardinality
specifier|public
name|void
name|testApproximateCardinality
parameter_list|()
block|{
specifier|final
name|SparseFixedBitSet
name|set
init|=
operator|new
name|SparseFixedBitSet
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|first
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|interval
init|=
literal|200
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|first
init|;
name|i
operator|<
name|set
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
name|interval
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|set
operator|.
name|cardinality
argument_list|()
argument_list|,
name|set
operator|.
name|approximateCardinality
argument_list|()
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
DECL|method|testApproximateCardinalityOnDenseSet
specifier|public
name|void
name|testApproximateCardinalityOnDenseSet
parameter_list|()
block|{
comment|// this tests that things work as expected in approximateCardinality when
comment|// all longs are different than 0, in which case we divide by zero
specifier|final
name|int
name|numDocs
init|=
literal|70
decl_stmt|;
comment|//TestUtil.nextInt(random(), 1, 10000);
specifier|final
name|SparseFixedBitSet
name|set
init|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|numDocs
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
name|set
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|set
operator|.
name|approximateCardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
