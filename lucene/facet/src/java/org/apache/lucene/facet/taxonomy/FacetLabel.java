begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
import|;
end_import
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
name|writercache
operator|.
name|LruTaxonomyWriterCache
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
name|writercache
operator|.
name|NameHashIntCacheLRU
import|;
end_import
begin_comment
comment|/**  * Holds a sequence of string components, specifying the hierarchical name of a  * category.  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|FacetLabel
specifier|public
class|class
name|FacetLabel
implements|implements
name|Comparable
argument_list|<
name|FacetLabel
argument_list|>
block|{
comment|/*    * copied from DocumentWriterPerThread -- if a FacetLabel is resolved to a    * drill-down term which is encoded to a larger term than that length, it is    * silently dropped! Therefore we limit the number of characters to MAX/4 to    * be on the safe side.    */
comment|/**    * The maximum number of characters a {@link FacetLabel} can have.    */
DECL|field|MAX_CATEGORY_PATH_LENGTH
specifier|public
specifier|final
specifier|static
name|int
name|MAX_CATEGORY_PATH_LENGTH
init|=
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
operator|/
literal|4
decl_stmt|;
comment|/**    * The components of this {@link FacetLabel}. Note that this array may be    * shared with other {@link FacetLabel} instances, e.g. as a result of    * {@link #subpath(int)}, therefore you should traverse the array up to    * {@link #length} for this path's components.    */
DECL|field|components
specifier|public
specifier|final
name|String
index|[]
name|components
decl_stmt|;
comment|/** The number of components of this {@link FacetLabel}. */
DECL|field|length
specifier|public
specifier|final
name|int
name|length
decl_stmt|;
comment|// Used by subpath
DECL|method|FacetLabel
specifier|private
name|FacetLabel
parameter_list|(
specifier|final
name|FacetLabel
name|copyFrom
parameter_list|,
specifier|final
name|int
name|prefixLen
parameter_list|)
block|{
comment|// while the code which calls this method is safe, at some point a test
comment|// tripped on AIOOBE in toString, but we failed to reproduce. adding the
comment|// assert as a safety check.
assert|assert
name|prefixLen
operator|>=
literal|0
operator|&&
name|prefixLen
operator|<=
name|copyFrom
operator|.
name|components
operator|.
name|length
operator|:
literal|"prefixLen cannot be negative nor larger than the given components' length: prefixLen="
operator|+
name|prefixLen
operator|+
literal|" components.length="
operator|+
name|copyFrom
operator|.
name|components
operator|.
name|length
assert|;
name|this
operator|.
name|components
operator|=
name|copyFrom
operator|.
name|components
expr_stmt|;
name|length
operator|=
name|prefixLen
expr_stmt|;
block|}
comment|/** Construct from the given path components. */
DECL|method|FacetLabel
specifier|public
name|FacetLabel
parameter_list|(
specifier|final
name|String
modifier|...
name|components
parameter_list|)
block|{
name|this
operator|.
name|components
operator|=
name|components
expr_stmt|;
name|length
operator|=
name|components
operator|.
name|length
expr_stmt|;
name|checkComponents
argument_list|()
expr_stmt|;
block|}
comment|/** Construct from the dimension plus the given path components. */
DECL|method|FacetLabel
specifier|public
name|FacetLabel
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
index|[]
name|path
parameter_list|)
block|{
name|components
operator|=
operator|new
name|String
index|[
literal|1
operator|+
name|path
operator|.
name|length
index|]
expr_stmt|;
name|components
index|[
literal|0
index|]
operator|=
name|dim
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|components
argument_list|,
literal|1
argument_list|,
name|path
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|components
operator|.
name|length
expr_stmt|;
name|checkComponents
argument_list|()
expr_stmt|;
block|}
DECL|method|checkComponents
specifier|private
name|void
name|checkComponents
parameter_list|()
block|{
name|long
name|len
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|comp
range|:
name|components
control|)
block|{
if|if
condition|(
name|comp
operator|==
literal|null
operator|||
name|comp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"empty or null components not allowed: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|components
argument_list|)
argument_list|)
throw|;
block|}
name|len
operator|+=
name|comp
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|len
operator|+=
name|components
operator|.
name|length
operator|-
literal|1
expr_stmt|;
comment|// add separators
if|if
condition|(
name|len
operator|>
name|MAX_CATEGORY_PATH_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"category path exceeds maximum allowed path length: max="
operator|+
name|MAX_CATEGORY_PATH_LENGTH
operator|+
literal|" len="
operator|+
name|len
operator|+
literal|" path="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|components
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|30
argument_list|)
operator|+
literal|"..."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Compares this path with another {@link FacetLabel} for lexicographic    * order.    */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|FacetLabel
name|other
parameter_list|)
block|{
specifier|final
name|int
name|len
init|=
name|length
operator|<
name|other
operator|.
name|length
condition|?
name|length
else|:
name|other
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
operator|,
name|j
operator|++
control|)
block|{
name|int
name|cmp
init|=
name|components
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|components
index|[
name|j
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// this is 'before'
block|}
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
return|return
literal|1
return|;
comment|// this is 'after'
block|}
block|}
comment|// one is a prefix of the other
return|return
name|length
operator|-
name|other
operator|.
name|length
return|;
block|}
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
operator|!
operator|(
name|obj
operator|instanceof
name|FacetLabel
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FacetLabel
name|other
init|=
operator|(
name|FacetLabel
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|length
operator|!=
name|other
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
comment|// not same length, cannot be equal
block|}
comment|// CategoryPaths are more likely to differ at the last components, so start
comment|// from last-first
for|for
control|(
name|int
name|i
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|!
name|components
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|other
operator|.
name|components
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|hash
init|=
name|length
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
name|i
operator|++
control|)
block|{
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|components
index|[
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/** Calculate a 64-bit hash function for this path.  This    *  is necessary for {@link NameHashIntCacheLRU} (the    *  default cache impl for {@link    *  LruTaxonomyWriterCache}) to reduce the chance of    *  "silent but deadly" collisions. */
DECL|method|longHashCode
specifier|public
name|long
name|longHashCode
parameter_list|()
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|hash
init|=
name|length
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
name|i
operator|++
control|)
block|{
name|hash
operator|=
name|hash
operator|*
literal|65599
operator|+
name|components
index|[
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/** Returns a sub-path of this path up to {@code length} components. */
DECL|method|subpath
specifier|public
name|FacetLabel
name|subpath
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|>=
name|this
operator|.
name|length
operator|||
name|length
operator|<
literal|0
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
operator|new
name|FacetLabel
argument_list|(
name|this
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns a string representation of the path.    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|"FacetLabel: []"
return|;
block|}
name|String
index|[]
name|parts
init|=
operator|new
name|String
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
name|parts
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
literal|"FacetLabel: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|parts
argument_list|)
return|;
block|}
block|}
end_class
end_unit
