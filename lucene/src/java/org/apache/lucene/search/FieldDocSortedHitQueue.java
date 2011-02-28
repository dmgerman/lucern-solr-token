begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|PriorityQueue
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * Expert: Collects sorted results from Searchable's and collates them.  * The elements put into this queue must be of type FieldDoc.  *  *<p>Created: Feb 11, 2004 2:04:21 PM  *  * @since   lucene 1.4  */
end_comment
begin_class
DECL|class|FieldDocSortedHitQueue
class|class
name|FieldDocSortedHitQueue
extends|extends
name|PriorityQueue
argument_list|<
name|FieldDoc
argument_list|>
block|{
DECL|field|fields
specifier|volatile
name|SortField
index|[]
name|fields
init|=
literal|null
decl_stmt|;
comment|/**    * Creates a hit queue sorted by the given list of fields.    * @param fields Fieldable names, in priority order (highest priority first).    * @param size  The number of hits to retain.  Must be greater than zero.    */
DECL|method|FieldDocSortedHitQueue
name|FieldDocSortedHitQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allows redefinition of sort fields if they are<code>null</code>.    * This is to handle the case using ParallelMultiSearcher where the    * original list contains AUTO and we don't know the actual sort    * type until the values come back.  The fields can only be set once.    * This method should be synchronized external like all other PQ methods.    * @param fields    */
DECL|method|setFields
name|void
name|setFields
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
comment|/** Returns the fields being used to sort. */
DECL|method|getFields
name|SortField
index|[]
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/**    * Returns whether<code>a</code> is less relevant than<code>b</code>.    * @param a ScoreDoc    * @param b ScoreDoc    * @return<code>true</code> if document<code>a</code> should be sorted after document<code>b</code>.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|FieldDoc
name|docA
parameter_list|,
specifier|final
name|FieldDoc
name|docB
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|fields
operator|.
name|length
decl_stmt|;
name|int
name|c
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
name|n
operator|&&
name|c
operator|==
literal|0
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|type
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|STRING
condition|)
block|{
specifier|final
name|BytesRef
name|s1
init|=
operator|(
name|BytesRef
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|BytesRef
name|s2
init|=
operator|(
name|BytesRef
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
comment|// null values need to be sorted first, because of how FieldCache.getStringIndex()
comment|// works - in that routine, any documents without a value in the given field are
comment|// put first.  If both are null, the next SortField is used
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
block|{
name|c
operator|=
operator|(
name|s2
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s2
operator|==
literal|null
condition|)
block|{
name|c
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|c
operator|=
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|c
operator|=
name|docA
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|docB
operator|.
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|SCORE
condition|)
block|{
name|c
operator|=
operator|-
name|c
expr_stmt|;
block|}
block|}
comment|// reverse sort
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|)
block|{
name|c
operator|=
operator|-
name|c
expr_stmt|;
block|}
block|}
comment|// avoid random sort order that could lead to duplicates (bug #31241):
if|if
condition|(
name|c
operator|==
literal|0
condition|)
return|return
name|docA
operator|.
name|doc
operator|>
name|docB
operator|.
name|doc
return|;
return|return
name|c
operator|>
literal|0
return|;
block|}
block|}
end_class
end_unit
