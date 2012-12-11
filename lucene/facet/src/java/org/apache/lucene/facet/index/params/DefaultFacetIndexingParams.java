begin_unit
begin_package
DECL|package|org.apache.lucene.facet.index.params
package|package
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
name|List
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
name|categorypolicy
operator|.
name|OrdinalPolicy
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
name|categorypolicy
operator|.
name|PathPolicy
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
name|CategoryPath
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Default implementation for {@link FacetIndexingParams}.  *<p>  * Getters for<em>partition-size</em>, {@link OrdinalPolicy} and  * {@link PathPolicy} are all final, and so the proper way to modify them when  * extending this class is through {@link #fixedPartitionSize()},  * {@link #fixedOrdinalPolicy()} or {@link #fixedPathPolicy()} accordingly.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DefaultFacetIndexingParams
specifier|public
class|class
name|DefaultFacetIndexingParams
implements|implements
name|FacetIndexingParams
block|{
comment|/**    * delimiter between a categories in a path, e.g. Products FACET_DELIM    * Consumer FACET_DELIM Tv. This should be a character not found in any path    * component    */
DECL|field|DEFAULT_FACET_DELIM_CHAR
specifier|public
specifier|static
specifier|final
name|char
name|DEFAULT_FACET_DELIM_CHAR
init|=
literal|'\uF749'
decl_stmt|;
DECL|field|clpParams
specifier|private
specifier|final
name|CategoryListParams
name|clpParams
decl_stmt|;
DECL|field|ordinalPolicy
specifier|private
specifier|final
name|OrdinalPolicy
name|ordinalPolicy
decl_stmt|;
DECL|field|pathPolicy
specifier|private
specifier|final
name|PathPolicy
name|pathPolicy
decl_stmt|;
DECL|field|partitionSize
specifier|private
specifier|final
name|int
name|partitionSize
decl_stmt|;
DECL|method|DefaultFacetIndexingParams
specifier|public
name|DefaultFacetIndexingParams
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|CategoryListParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DefaultFacetIndexingParams
specifier|public
name|DefaultFacetIndexingParams
parameter_list|(
name|CategoryListParams
name|categoryListParams
parameter_list|)
block|{
name|clpParams
operator|=
name|categoryListParams
expr_stmt|;
name|ordinalPolicy
operator|=
name|fixedOrdinalPolicy
argument_list|()
expr_stmt|;
name|pathPolicy
operator|=
name|fixedPathPolicy
argument_list|()
expr_stmt|;
name|partitionSize
operator|=
name|fixedPartitionSize
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCategoryListParams
specifier|public
name|CategoryListParams
name|getCategoryListParams
parameter_list|(
name|CategoryPath
name|category
parameter_list|)
block|{
return|return
name|clpParams
return|;
block|}
annotation|@
name|Override
DECL|method|drillDownTermText
specifier|public
name|int
name|drillDownTermText
parameter_list|(
name|CategoryPath
name|path
parameter_list|,
name|char
index|[]
name|buffer
parameter_list|)
block|{
return|return
name|path
operator|.
name|copyToCharArray
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
name|getFacetDelimChar
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * "fixed" partition size.     * @see #getPartitionSize()    */
DECL|method|fixedPartitionSize
specifier|protected
name|int
name|fixedPartitionSize
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/**    * "fixed" ordinal policy.     * @see #getOrdinalPolicy()    */
DECL|method|fixedOrdinalPolicy
specifier|protected
name|OrdinalPolicy
name|fixedOrdinalPolicy
parameter_list|()
block|{
return|return
name|OrdinalPolicy
operator|.
name|ALL_PARENTS
return|;
block|}
comment|/**    * "fixed" path policy.     * @see #getPathPolicy()    */
DECL|method|fixedPathPolicy
specifier|protected
name|PathPolicy
name|fixedPathPolicy
parameter_list|()
block|{
return|return
name|PathPolicy
operator|.
name|ALL_CATEGORIES
return|;
block|}
annotation|@
name|Override
DECL|method|getPartitionSize
specifier|public
specifier|final
name|int
name|getPartitionSize
parameter_list|()
block|{
return|return
name|partitionSize
return|;
block|}
comment|/*    * (non-Javadoc)    *     * @see    * org.apache.lucene.facet.index.params.FacetIndexingParams#getAllCategoryListParams    * ()    */
annotation|@
name|Override
DECL|method|getAllCategoryListParams
specifier|public
name|Iterable
argument_list|<
name|CategoryListParams
argument_list|>
name|getAllCategoryListParams
parameter_list|()
block|{
name|List
argument_list|<
name|CategoryListParams
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryListParams
argument_list|>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|clpParams
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|getOrdinalPolicy
specifier|public
specifier|final
name|OrdinalPolicy
name|getOrdinalPolicy
parameter_list|()
block|{
return|return
name|ordinalPolicy
return|;
block|}
annotation|@
name|Override
DECL|method|getPathPolicy
specifier|public
specifier|final
name|PathPolicy
name|getPathPolicy
parameter_list|()
block|{
return|return
name|pathPolicy
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#hashCode()    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|clpParams
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|clpParams
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|ordinalPolicy
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|ordinalPolicy
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|partitionSize
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|pathPolicy
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|pathPolicy
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
for|for
control|(
name|CategoryListParams
name|clp
range|:
name|getAllCategoryListParams
argument_list|()
control|)
block|{
name|result
operator|^=
name|clp
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#equals(java.lang.Object)    */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|DefaultFacetIndexingParams
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DefaultFacetIndexingParams
name|other
init|=
operator|(
name|DefaultFacetIndexingParams
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|clpParams
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|clpParams
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|clpParams
operator|.
name|equals
argument_list|(
name|other
operator|.
name|clpParams
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|ordinalPolicy
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|ordinalPolicy
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|ordinalPolicy
operator|.
name|equals
argument_list|(
name|other
operator|.
name|ordinalPolicy
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|partitionSize
operator|!=
name|other
operator|.
name|partitionSize
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|pathPolicy
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|pathPolicy
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|pathPolicy
operator|.
name|equals
argument_list|(
name|other
operator|.
name|pathPolicy
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Iterable
argument_list|<
name|CategoryListParams
argument_list|>
name|cLs
init|=
name|getAllCategoryListParams
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|CategoryListParams
argument_list|>
name|otherCLs
init|=
name|other
operator|.
name|getAllCategoryListParams
argument_list|()
decl_stmt|;
return|return
name|cLs
operator|.
name|equals
argument_list|(
name|otherCLs
argument_list|)
return|;
block|}
comment|/**    * Use {@link #DEFAULT_FACET_DELIM_CHAR} as the delimiter.    */
annotation|@
name|Override
DECL|method|getFacetDelimChar
specifier|public
name|char
name|getFacetDelimChar
parameter_list|()
block|{
return|return
name|DEFAULT_FACET_DELIM_CHAR
return|;
block|}
block|}
end_class
end_unit
