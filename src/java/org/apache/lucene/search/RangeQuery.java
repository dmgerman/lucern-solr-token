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
name|text
operator|.
name|Collator
import|;
end_import
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
name|index
operator|.
name|Term
import|;
end_import
begin_comment
comment|/**  * A Query that matches documents within an exclusive range of terms.  *  *<p>This query matches the documents looking for terms that fall into the  * supplied range according to {@link Term#compareTo(Term)}. It is not intended  * for numerical ranges, use {@link NumericRangeQuery} instead.  *  *<p>This query is in  * {@linkplain MultiTermQuery#setConstantScoreRewrite(boolean) boolean query rewrite mode}.  * If you want to change this, use the new {@link TermRangeQuery} instead.  *  * @deprecated Use {@link TermRangeQuery} for term ranges or  * {@link NumericRangeQuery} for numeric ranges instead.  * This class will be removed in Lucene 3.0.  */
end_comment
begin_class
DECL|class|RangeQuery
specifier|public
class|class
name|RangeQuery
extends|extends
name|Query
block|{
DECL|field|delegate
specifier|private
specifier|final
name|TermRangeQuery
name|delegate
decl_stmt|;
comment|/** Constructs a query selecting all terms greater than    *<code>lowerTerm</code> but less than<code>upperTerm</code>.    * There must be at least one term and either term may be null,    * in which case there is no bound on that side, but if there are    * two terms, both terms<b>must</b> be for the same field.    *    * @param lowerTerm The Term at the lower end of the range    * @param upperTerm The Term at the upper end of the range    * @param inclusive If true, both<code>lowerTerm</code> and    *<code>upperTerm</code> will themselves be included in the range.    */
DECL|method|RangeQuery
specifier|public
name|RangeQuery
parameter_list|(
name|Term
name|lowerTerm
parameter_list|,
name|Term
name|upperTerm
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
block|{
name|this
argument_list|(
name|lowerTerm
argument_list|,
name|upperTerm
argument_list|,
name|inclusive
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a query selecting all terms greater than    *<code>lowerTerm</code> but less than<code>upperTerm</code>.    * There must be at least one term and either term may be null,    * in which case there is no bound on that side, but if there are    * two terms, both terms<b>must</b> be for the same field.    *<p>    * If<code>collator</code> is not null, it will be used to decide whether    * index terms are within the given range, rather than using the Unicode code    * point order in which index terms are stored.    *<p>    *<strong>WARNING:</strong> Using this constructor and supplying a non-null    * value in the<code>collator</code> parameter will cause every single     * index Term in the Field referenced by lowerTerm and/or upperTerm to be    * examined.  Depending on the number of index Terms in this Field, the     * operation could be very slow.    *    * @param lowerTerm The Term at the lower end of the range    * @param upperTerm The Term at the upper end of the range    * @param inclusive If true, both<code>lowerTerm</code> and    *<code>upperTerm</code> will themselves be included in the range.    * @param collator The collator to use to collate index Terms, to determine    *  their membership in the range bounded by<code>lowerTerm</code> and    *<code>upperTerm</code>.    */
DECL|method|RangeQuery
specifier|public
name|RangeQuery
parameter_list|(
name|Term
name|lowerTerm
parameter_list|,
name|Term
name|upperTerm
parameter_list|,
name|boolean
name|inclusive
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
if|if
condition|(
name|lowerTerm
operator|==
literal|null
operator|&&
name|upperTerm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"At least one term must be non-null"
argument_list|)
throw|;
if|if
condition|(
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
name|field
argument_list|()
operator|!=
name|upperTerm
operator|.
name|field
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Both terms must have the same field"
argument_list|)
throw|;
name|delegate
operator|=
operator|new
name|TermRangeQuery
argument_list|(
operator|(
name|lowerTerm
operator|==
literal|null
operator|)
condition|?
name|upperTerm
operator|.
name|field
argument_list|()
else|:
name|lowerTerm
operator|.
name|field
argument_list|()
argument_list|,
operator|(
name|lowerTerm
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|lowerTerm
operator|.
name|text
argument_list|()
argument_list|,
operator|(
name|upperTerm
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|upperTerm
operator|.
name|text
argument_list|()
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|,
name|collator
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|setConstantScoreRewrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|b
parameter_list|)
block|{
name|super
operator|.
name|setBoost
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|setBoost
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/** Returns the field name for this query */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getField
argument_list|()
return|;
block|}
comment|/** Returns the lower term of this range query. */
DECL|method|getLowerTerm
specifier|public
name|Term
name|getLowerTerm
parameter_list|()
block|{
specifier|final
name|String
name|term
init|=
name|delegate
operator|.
name|getLowerTerm
argument_list|()
decl_stmt|;
return|return
operator|(
name|term
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|Term
argument_list|(
name|getField
argument_list|()
argument_list|,
name|term
argument_list|)
return|;
block|}
comment|/** Returns the upper term of this range query. */
DECL|method|getUpperTerm
specifier|public
name|Term
name|getUpperTerm
parameter_list|()
block|{
specifier|final
name|String
name|term
init|=
name|delegate
operator|.
name|getUpperTerm
argument_list|()
decl_stmt|;
return|return
operator|(
name|term
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|Term
argument_list|(
name|getField
argument_list|()
argument_list|,
name|term
argument_list|)
return|;
block|}
comment|/** Returns<code>true</code> if the range query is inclusive */
DECL|method|isInclusive
specifier|public
name|boolean
name|isInclusive
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|includesLower
argument_list|()
operator|&&
name|delegate
operator|.
name|includesUpper
argument_list|()
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
name|delegate
operator|.
name|getCollator
argument_list|()
return|;
block|}
comment|/** Prints a user-readable version of this query. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|RangeQuery
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|RangeQuery
name|other
init|=
operator|(
name|RangeQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|delegate
operator|.
name|equals
argument_list|(
name|other
operator|.
name|delegate
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object.*/
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
