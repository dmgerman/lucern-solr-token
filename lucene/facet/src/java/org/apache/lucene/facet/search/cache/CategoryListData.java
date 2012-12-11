begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.cache
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
operator|.
name|cache
package|;
end_package
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|search
operator|.
name|CategoryListIterator
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
name|taxonomy
operator|.
name|TaxonomyReader
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
name|collections
operator|.
name|IntArray
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Category list data maintained in RAM.  *<p>  * Speeds up facets accumulation when more RAM is available.  *<p>  * Note that this will consume more memory: one int (4 bytes) for each category  * of each document.  *<p>  * Note: at the moment this class is insensitive to updates of the index, and,  * in particular, does not make use of Lucene's ability to refresh a single  * segment.  *<p>  * See {@link CategoryListCache#register(CategoryListParams, CategoryListData)}  * and  * {@link CategoryListCache#loadAndRegister(CategoryListParams, IndexReader, TaxonomyReader, FacetIndexingParams)}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CategoryListData
specifier|public
class|class
name|CategoryListData
block|{
comment|// TODO (Facet): experiment with different orders - p-d-c vs. current d-p-c.
DECL|field|docPartitionCategories
specifier|private
specifier|transient
specifier|volatile
name|int
index|[]
index|[]
index|[]
name|docPartitionCategories
decl_stmt|;
comment|/**    * Empty constructor for extensions with modified computation of the data.    */
DECL|method|CategoryListData
specifier|protected
name|CategoryListData
parameter_list|()
block|{   }
comment|/**    * Compute category list data for caching for faster iteration.    */
DECL|method|CategoryListData
name|CategoryListData
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|TaxonomyReader
name|taxo
parameter_list|,
name|FacetIndexingParams
name|iparams
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
index|[]
index|[]
index|[]
name|dpf
init|=
operator|new
name|int
index|[
name|maxDoc
index|]
index|[]
index|[]
decl_stmt|;
name|int
name|numPartitions
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|taxo
operator|.
name|getSize
argument_list|()
operator|/
operator|(
name|double
operator|)
name|iparams
operator|.
name|getPartitionSize
argument_list|()
argument_list|)
decl_stmt|;
name|IntArray
name|docCategories
init|=
operator|new
name|IntArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|part
init|=
literal|0
init|;
name|part
operator|<
name|numPartitions
condition|;
name|part
operator|++
control|)
block|{
name|CategoryListIterator
name|cli
init|=
name|clp
operator|.
name|createCategoryListIterator
argument_list|(
name|reader
argument_list|,
name|part
argument_list|)
decl_stmt|;
if|if
condition|(
name|cli
operator|.
name|init
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
control|)
block|{
if|if
condition|(
name|cli
operator|.
name|skipTo
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|docCategories
operator|.
name|clear
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|dpf
index|[
name|doc
index|]
operator|==
literal|null
condition|)
block|{
name|dpf
index|[
name|doc
index|]
operator|=
operator|new
name|int
index|[
name|numPartitions
index|]
index|[]
expr_stmt|;
block|}
name|long
name|category
decl_stmt|;
while|while
condition|(
operator|(
name|category
operator|=
name|cli
operator|.
name|nextCategory
argument_list|()
operator|)
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|docCategories
operator|.
name|addToArray
argument_list|(
operator|(
name|int
operator|)
name|category
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|size
init|=
name|docCategories
operator|.
name|size
argument_list|()
decl_stmt|;
name|dpf
index|[
name|doc
index|]
index|[
name|part
index|]
operator|=
operator|new
name|int
index|[
name|size
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|dpf
index|[
name|doc
index|]
index|[
name|part
index|]
index|[
name|i
index|]
operator|=
name|docCategories
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|docPartitionCategories
operator|=
name|dpf
expr_stmt|;
block|}
comment|/**    * Iterate on the category list data for the specified partition.    */
DECL|method|iterator
specifier|public
name|CategoryListIterator
name|iterator
parameter_list|(
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RAMCategoryListIterator
argument_list|(
name|partition
argument_list|,
name|docPartitionCategories
argument_list|)
return|;
block|}
comment|/**    * Internal: category list iterator over uncompressed category info in RAM    */
DECL|class|RAMCategoryListIterator
specifier|private
specifier|static
class|class
name|RAMCategoryListIterator
implements|implements
name|CategoryListIterator
block|{
DECL|field|part
specifier|private
specifier|final
name|int
name|part
decl_stmt|;
DECL|field|dpc
specifier|private
specifier|final
name|int
index|[]
index|[]
index|[]
name|dpc
decl_stmt|;
DECL|field|currDoc
specifier|private
name|int
name|currDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|nextCategoryIndex
specifier|private
name|int
name|nextCategoryIndex
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|RAMCategoryListIterator
name|RAMCategoryListIterator
parameter_list|(
name|int
name|part
parameter_list|,
name|int
index|[]
index|[]
index|[]
name|docPartitionCategories
parameter_list|)
block|{
name|this
operator|.
name|part
operator|=
name|part
expr_stmt|;
name|dpc
operator|=
name|docPartitionCategories
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dpc
operator|!=
literal|null
operator|&&
name|dpc
operator|.
name|length
operator|>
name|part
return|;
block|}
annotation|@
name|Override
DECL|method|nextCategory
specifier|public
name|long
name|nextCategory
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nextCategoryIndex
operator|>=
name|dpc
index|[
name|currDoc
index|]
index|[
name|part
index|]
operator|.
name|length
condition|)
block|{
return|return
literal|1L
operator|+
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
return|return
name|dpc
index|[
name|currDoc
index|]
index|[
name|part
index|]
index|[
name|nextCategoryIndex
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|res
init|=
name|dpc
operator|.
name|length
operator|>
name|docId
operator|&&
name|dpc
index|[
name|docId
index|]
operator|!=
literal|null
operator|&&
name|dpc
index|[
name|docId
index|]
index|[
name|part
index|]
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|res
condition|)
block|{
name|currDoc
operator|=
name|docId
expr_stmt|;
name|nextCategoryIndex
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
block|}
end_class
end_unit
