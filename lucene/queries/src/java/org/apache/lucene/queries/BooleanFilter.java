begin_unit
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
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
name|LeafReaderContext
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
name|search
operator|.
name|BitsFilteredDocIdSet
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|search
operator|.
name|DocIdSet
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
name|search
operator|.
name|DocIdSetIterator
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
name|search
operator|.
name|Filter
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
name|BitDocIdSet
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
name|Bits
import|;
end_import
begin_comment
comment|/**  * A container Filter that allows Boolean composition of Filters.  * Filters are allocated into one of three logical constructs;  * SHOULD, MUST NOT, MUST  * The results Filter BitSet is constructed as follows:  * SHOULD Filters are OR'd together  * The resulting Filter is NOT'd with the NOT Filters  * The resulting Filter is AND'd with the MUST Filters  */
end_comment
begin_class
DECL|class|BooleanFilter
specifier|public
class|class
name|BooleanFilter
extends|extends
name|Filter
implements|implements
name|Iterable
argument_list|<
name|FilterClause
argument_list|>
block|{
DECL|field|clauses
specifier|private
specifier|final
name|List
argument_list|<
name|FilterClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Returns the a DocIdSetIterator representing the Boolean composition    * of the filters that have been added.    */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|BitDocIdSet
operator|.
name|Builder
name|res
init|=
literal|null
decl_stmt|;
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|boolean
name|hasShouldClauses
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|FilterClause
name|fc
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|fc
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|SHOULD
condition|)
block|{
name|hasShouldClauses
operator|=
literal|true
expr_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|getDISI
argument_list|(
name|fc
operator|.
name|getFilter
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
continue|continue;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|res
operator|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|res
operator|.
name|or
argument_list|(
name|disi
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasShouldClauses
operator|&&
name|res
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
specifier|final
name|FilterClause
name|fc
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|fc
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|MUST_NOT
condition|)
block|{
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
assert|assert
operator|!
name|hasShouldClauses
assert|;
name|res
operator|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// NOTE: may set bits on deleted docs
block|}
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|getDISI
argument_list|(
name|fc
operator|.
name|getFilter
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|disi
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|andNot
argument_list|(
name|disi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
specifier|final
name|FilterClause
name|fc
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|fc
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|MUST
condition|)
block|{
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|getDISI
argument_list|(
name|fc
operator|.
name|getFilter
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// no documents can match
block|}
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
name|res
operator|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|or
argument_list|(
name|disi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|and
argument_list|(
name|disi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|res
operator|.
name|build
argument_list|()
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
DECL|method|getDISI
specifier|private
specifier|static
name|DocIdSetIterator
name|getDISI
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we dont pass acceptDocs, we will filter at the end using an additional filter
specifier|final
name|DocIdSet
name|set
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|set
operator|==
literal|null
condition|?
literal|null
else|:
name|set
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**   * Adds a new FilterClause to the Boolean Filter container   * @param filterClause A FilterClause object containing a Filter and an Occur parameter   */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|FilterClause
name|filterClause
parameter_list|)
block|{
name|clauses
operator|.
name|add
argument_list|(
name|filterClause
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|Occur
name|occur
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|filter
argument_list|,
name|occur
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**   * Returns the list of clauses   */
DECL|method|clauses
specifier|public
name|List
argument_list|<
name|FilterClause
argument_list|>
name|clauses
parameter_list|()
block|{
return|return
name|clauses
return|;
block|}
comment|/** Returns an iterator on the clauses in this query. It implements the {@link Iterable} interface to    * make it possible to do:    *<pre class="prettyprint">for (FilterClause clause : booleanFilter) {}</pre>    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
specifier|final
name|Iterator
argument_list|<
name|FilterClause
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|clauses
argument_list|()
operator|.
name|iterator
argument_list|()
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
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|BooleanFilter
name|other
init|=
operator|(
name|BooleanFilter
operator|)
name|obj
decl_stmt|;
return|return
name|clauses
operator|.
name|equals
argument_list|(
name|other
operator|.
name|clauses
argument_list|)
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
return|return
literal|657153718
operator|^
name|clauses
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Prints a user-readable version of this Filter. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"BooleanFilter("
argument_list|)
decl_stmt|;
specifier|final
name|int
name|minLen
init|=
name|buffer
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|FilterClause
name|c
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
name|minLen
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
