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
name|text
operator|.
name|Collator
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
name|index
operator|.
name|TermsEnum
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
name|Terms
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
name|MultiFields
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
name|ToStringUtils
import|;
end_import
begin_comment
comment|/**  * A Query that matches documents within an range of terms.  *  *<p>This query matches the documents looking for terms that fall into the  * supplied range according to {@link  * String#compareTo(String)}, unless a<code>Collator</code> is provided. It is not intended  * for numerical ranges; use {@link NumericRangeQuery} instead.  *  *<p>This query uses the {@link  * MultiTermQuery#CONSTANT_SCORE_AUTO_REWRITE_DEFAULT}  * rewrite method.  * @since 2.9  */
end_comment
begin_class
DECL|class|TermRangeQuery
specifier|public
class|class
name|TermRangeQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|lowerTerm
specifier|private
name|String
name|lowerTerm
decl_stmt|;
DECL|field|upperTerm
specifier|private
name|String
name|upperTerm
decl_stmt|;
DECL|field|collator
specifier|private
name|Collator
name|collator
decl_stmt|;
DECL|field|includeLower
specifier|private
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|private
name|boolean
name|includeUpper
decl_stmt|;
comment|/**    * Constructs a query selecting all terms greater/equal than<code>lowerTerm</code>    * but less/equal than<code>upperTerm</code>.     *     *<p>    * If an endpoint is null, it is said     * to be "open". Either or both endpoints may be open.  Open endpoints may not     * be exclusive (you can't select all but the first or last term without     * explicitly specifying the term to exclude.)    *     * @param field The field that holds both lower and upper terms.    * @param lowerTerm    *          The term text at the lower end of the range    * @param upperTerm    *          The term text at the upper end of the range    * @param includeLower    *          If true, the<code>lowerTerm</code> is    *          included in the range.    * @param includeUpper    *          If true, the<code>upperTerm</code> is    *          included in the range.    */
DECL|method|TermRangeQuery
specifier|public
name|TermRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|lowerTerm
argument_list|,
name|upperTerm
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a query selecting all terms greater/equal than    *<code>lowerTerm</code> but less/equal than<code>upperTerm</code>.    *<p>    * If an endpoint is null, it is said     * to be "open". Either or both endpoints may be open.  Open endpoints may not     * be exclusive (you can't select all but the first or last term without     * explicitly specifying the term to exclude.)    *<p>    * If<code>collator</code> is not null, it will be used to decide whether    * index terms are within the given range, rather than using the Unicode code    * point order in which index terms are stored.    *<p>    *<strong>WARNING:</strong> Using this constructor and supplying a non-null    * value in the<code>collator</code> parameter will cause every single     * index Term in the Field referenced by lowerTerm and/or upperTerm to be    * examined.  Depending on the number of index Terms in this Field, the     * operation could be very slow.    *    * @param lowerTerm The Term text at the lower end of the range    * @param upperTerm The Term text at the upper end of the range    * @param includeLower    *          If true, the<code>lowerTerm</code> is    *          included in the range.    * @param includeUpper    *          If true, the<code>upperTerm</code> is    *          included in the range.    * @param collator The collator to use to collate index Terms, to determine    *  their membership in the range bounded by<code>lowerTerm</code> and    *<code>upperTerm</code>.    */
DECL|method|TermRangeQuery
specifier|public
name|TermRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|lowerTerm
operator|=
name|lowerTerm
expr_stmt|;
name|this
operator|.
name|upperTerm
operator|=
name|upperTerm
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
name|this
operator|.
name|collator
operator|=
name|collator
expr_stmt|;
block|}
comment|/** Returns the lower value of this range query */
DECL|method|getLowerTerm
specifier|public
name|String
name|getLowerTerm
parameter_list|()
block|{
return|return
name|lowerTerm
return|;
block|}
comment|/** Returns the upper value of this range query */
DECL|method|getUpperTerm
specifier|public
name|String
name|getUpperTerm
parameter_list|()
block|{
return|return
name|upperTerm
return|;
block|}
comment|/** Returns<code>true</code> if the lower endpoint is inclusive */
DECL|method|includesLower
specifier|public
name|boolean
name|includesLower
parameter_list|()
block|{
return|return
name|includeLower
return|;
block|}
comment|/** Returns<code>true</code> if the upper endpoint is inclusive */
DECL|method|includesUpper
specifier|public
name|boolean
name|includesUpper
parameter_list|()
block|{
return|return
name|includeUpper
return|;
block|}
comment|/** Returns the collator used to determine range inclusion, if any. */
DECL|method|getCollator
specifier|public
name|Collator
name|getCollator
parameter_list|()
block|{
return|return
name|collator
return|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|collator
operator|==
literal|null
operator|&&
name|lowerTerm
operator|!=
literal|null
operator|&&
name|upperTerm
operator|!=
literal|null
operator|&&
name|lowerTerm
operator|.
name|compareTo
argument_list|(
name|upperTerm
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
block|}
if|if
condition|(
operator|(
name|lowerTerm
operator|==
literal|null
operator|||
operator|(
name|collator
operator|==
literal|null
operator|&&
name|includeLower
operator|&&
literal|""
operator|.
name|equals
argument_list|(
name|lowerTerm
argument_list|)
operator|)
operator|)
operator|&&
name|upperTerm
operator|==
literal|null
condition|)
block|{
comment|// NOTE: debateably, the caller should never pass in a
comment|// multi reader...
specifier|final
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|(
name|terms
operator|!=
literal|null
operator|)
condition|?
name|terms
operator|.
name|iterator
argument_list|()
else|:
literal|null
return|;
block|}
return|return
operator|new
name|TermRangeTermsEnum
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|lowerTerm
argument_list|,
name|upperTerm
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|,
name|collator
argument_list|)
return|;
block|}
comment|/** @deprecated */
annotation|@
name|Deprecated
DECL|method|field
specifier|public
name|String
name|field
parameter_list|()
block|{
return|return
name|getField
argument_list|()
return|;
block|}
comment|/** Prints a user-readable version of this query. */
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
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|includeLower
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|lowerTerm
operator|!=
literal|null
condition|?
name|lowerTerm
else|:
literal|"*"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|upperTerm
operator|!=
literal|null
condition|?
name|upperTerm
else|:
literal|"*"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|includeUpper
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|collator
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|collator
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
name|includeLower
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|includeUpper
condition|?
literal|1231
else|:
literal|1237
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
name|lowerTerm
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|lowerTerm
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
name|upperTerm
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|upperTerm
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
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
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TermRangeQuery
name|other
init|=
operator|(
name|TermRangeQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|collator
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|collator
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|collator
operator|.
name|equals
argument_list|(
name|other
operator|.
name|collator
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|includeLower
operator|!=
name|other
operator|.
name|includeLower
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|includeUpper
operator|!=
name|other
operator|.
name|includeUpper
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lowerTerm
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|lowerTerm
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|lowerTerm
operator|.
name|equals
argument_list|(
name|other
operator|.
name|lowerTerm
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|upperTerm
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|upperTerm
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|upperTerm
operator|.
name|equals
argument_list|(
name|other
operator|.
name|upperTerm
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
