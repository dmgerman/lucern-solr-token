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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A thread-safe pool of {@code int[]} and {@code float[]} arrays. One specifies  * the maximum number of arrays in the constructor. Calls to  * {@link #allocateFloatArray()} or {@link #allocateIntArray()} take an array  * from the pool, and if one is not available, allocate a new one. When you are  * done using the array, you should {@link #free(int[]) free} it.  *<p>  * This class is used by {@link ReusingFacetArrays} for temporal facet  * aggregation arrays, which can be reused across searches instead of being  * allocated afresh on every search.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|ArraysPool
specifier|public
specifier|final
class|class
name|ArraysPool
block|{
DECL|field|intsPool
specifier|private
specifier|final
name|ArrayBlockingQueue
argument_list|<
name|int
index|[]
argument_list|>
name|intsPool
decl_stmt|;
DECL|field|floatsPool
specifier|private
specifier|final
name|ArrayBlockingQueue
argument_list|<
name|float
index|[]
argument_list|>
name|floatsPool
decl_stmt|;
DECL|field|arrayLength
specifier|public
specifier|final
name|int
name|arrayLength
decl_stmt|;
comment|/**    * Specifies the max number of arrays to pool, as well as the length of each    * array to allocate.    *     * @param arrayLength the size of the arrays to allocate    * @param maxArrays the maximum number of arrays to pool, from each type    *     * @throws IllegalArgumentException if maxArrays is set to 0.    */
DECL|method|ArraysPool
specifier|public
name|ArraysPool
parameter_list|(
name|int
name|arrayLength
parameter_list|,
name|int
name|maxArrays
parameter_list|)
block|{
if|if
condition|(
name|maxArrays
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxArrays cannot be 0 - don't use this class if you don't intend to pool arrays"
argument_list|)
throw|;
block|}
name|this
operator|.
name|arrayLength
operator|=
name|arrayLength
expr_stmt|;
name|this
operator|.
name|intsPool
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|int
index|[]
argument_list|>
argument_list|(
name|maxArrays
argument_list|)
expr_stmt|;
name|this
operator|.
name|floatsPool
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|float
index|[]
argument_list|>
argument_list|(
name|maxArrays
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allocates a new {@code int[]}. If there's an available array in the pool,    * it is used, otherwise a new array is allocated.    */
DECL|method|allocateIntArray
specifier|public
specifier|final
name|int
index|[]
name|allocateIntArray
parameter_list|()
block|{
name|int
index|[]
name|arr
init|=
name|intsPool
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|int
index|[
name|arrayLength
index|]
return|;
block|}
name|Arrays
operator|.
name|fill
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// reset array
return|return
name|arr
return|;
block|}
comment|/**    * Allocates a new {@code float[]}. If there's an available array in the pool,    * it is used, otherwise a new array is allocated.    */
DECL|method|allocateFloatArray
specifier|public
specifier|final
name|float
index|[]
name|allocateFloatArray
parameter_list|()
block|{
name|float
index|[]
name|arr
init|=
name|floatsPool
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|float
index|[
name|arrayLength
index|]
return|;
block|}
name|Arrays
operator|.
name|fill
argument_list|(
name|arr
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
comment|// reset array
return|return
name|arr
return|;
block|}
comment|/**    * Frees a no-longer-needed array. If there's room in the pool, the array is    * added to it, otherwise discarded.    */
DECL|method|free
specifier|public
specifier|final
name|void
name|free
parameter_list|(
name|int
index|[]
name|arr
parameter_list|)
block|{
if|if
condition|(
name|arr
operator|!=
literal|null
condition|)
block|{
comment|// use offer - if there isn't room, we don't want to wait
name|intsPool
operator|.
name|offer
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Frees a no-longer-needed array. If there's room in the pool, the array is    * added to it, otherwise discarded.    */
DECL|method|free
specifier|public
specifier|final
name|void
name|free
parameter_list|(
name|float
index|[]
name|arr
parameter_list|)
block|{
if|if
condition|(
name|arr
operator|!=
literal|null
condition|)
block|{
comment|// use offer - if there isn't room, we don't want to wait
name|floatsPool
operator|.
name|offer
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
