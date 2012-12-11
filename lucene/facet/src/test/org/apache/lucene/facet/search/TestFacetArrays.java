begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package
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
name|LuceneTestCase
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
DECL|class|TestFacetArrays
specifier|public
class|class
name|TestFacetArrays
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testFacetArrays
specifier|public
name|void
name|testFacetArrays
parameter_list|()
block|{
for|for
control|(
name|boolean
name|reusing
range|:
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
specifier|final
name|FacetArrays
name|arrays
decl_stmt|;
if|if
condition|(
name|reusing
condition|)
block|{
name|arrays
operator|=
operator|new
name|ReusingFacetArrays
argument_list|(
operator|new
name|ArraysPool
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|arrays
operator|=
operator|new
name|FacetArrays
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
index|[]
name|intArray
init|=
name|arrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
comment|// Set the element, then free
name|intArray
index|[
literal|0
index|]
operator|=
literal|1
expr_stmt|;
name|arrays
operator|.
name|free
argument_list|()
expr_stmt|;
comment|// We should expect a cleared array back
name|int
index|[]
name|newIntArray
init|=
name|arrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected a cleared array back, but the array is still filled"
argument_list|,
literal|0
argument_list|,
name|newIntArray
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|float
index|[]
name|floatArray
init|=
name|arrays
operator|.
name|getFloatArray
argument_list|()
decl_stmt|;
comment|// Set the element, then free
name|floatArray
index|[
literal|0
index|]
operator|=
literal|1.0f
expr_stmt|;
name|arrays
operator|.
name|free
argument_list|()
expr_stmt|;
comment|// We should expect a cleared array back
name|float
index|[]
name|newFloatArray
init|=
name|arrays
operator|.
name|getFloatArray
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected a cleared array back, but the array is still filled"
argument_list|,
literal|0.0f
argument_list|,
name|newFloatArray
index|[
literal|0
index|]
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
if|if
condition|(
name|reusing
condition|)
block|{
comment|// same instance should be returned after free()
name|assertSame
argument_list|(
literal|"ReusingFacetArrays did not reuse the array!"
argument_list|,
name|intArray
argument_list|,
name|newIntArray
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"ReusingFacetArrays did not reuse the array!"
argument_list|,
name|floatArray
argument_list|,
name|newFloatArray
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
