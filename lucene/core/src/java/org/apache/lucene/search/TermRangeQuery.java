begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|automaton
operator|.
name|Automata
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
name|automaton
operator|.
name|Automaton
import|;
end_import
begin_comment
comment|/**  * A Query that matches documents within an range of terms.  *  *<p>This query matches the documents looking for terms that fall into the  * supplied range according to {@link BytesRef#compareTo(BytesRef)}.  *  *<p>This query uses the {@link  * MultiTermQuery#CONSTANT_SCORE_REWRITE}  * rewrite method.  * @since 2.9  */
end_comment
begin_class
DECL|class|TermRangeQuery
specifier|public
class|class
name|TermRangeQuery
extends|extends
name|AutomatonQuery
block|{
DECL|field|lowerTerm
specifier|private
specifier|final
name|BytesRef
name|lowerTerm
decl_stmt|;
DECL|field|upperTerm
specifier|private
specifier|final
name|BytesRef
name|upperTerm
decl_stmt|;
DECL|field|includeLower
specifier|private
specifier|final
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|private
specifier|final
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
name|BytesRef
name|lowerTerm
parameter_list|,
name|BytesRef
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|lowerTerm
argument_list|)
argument_list|,
name|toAutomaton
argument_list|(
name|lowerTerm
argument_list|,
name|upperTerm
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|true
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
block|}
DECL|method|toAutomaton
specifier|public
specifier|static
name|Automaton
name|toAutomaton
parameter_list|(
name|BytesRef
name|lowerTerm
parameter_list|,
name|BytesRef
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
if|if
condition|(
name|lowerTerm
operator|==
literal|null
condition|)
block|{
comment|// makeBinaryInterval is more picky than we are:
name|includeLower
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|upperTerm
operator|==
literal|null
condition|)
block|{
comment|// makeBinaryInterval is more picky than we are:
name|includeUpper
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|Automata
operator|.
name|makeBinaryInterval
argument_list|(
name|lowerTerm
argument_list|,
name|includeLower
argument_list|,
name|upperTerm
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
comment|/**    * Factory that creates a new TermRangeQuery using Strings for term text.    */
DECL|method|newStringRange
specifier|public
specifier|static
name|TermRangeQuery
name|newStringRange
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
name|BytesRef
name|lower
init|=
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|lowerTerm
argument_list|)
decl_stmt|;
name|BytesRef
name|upper
init|=
name|upperTerm
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|upperTerm
argument_list|)
decl_stmt|;
return|return
operator|new
name|TermRangeQuery
argument_list|(
name|field
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
comment|/** Returns the lower value of this range query */
DECL|method|getLowerTerm
specifier|public
name|BytesRef
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
name|BytesRef
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
comment|// TODO: all these toStrings for queries should just output the bytes, it might not be UTF-8!
name|buffer
operator|.
name|append
argument_list|(
name|lowerTerm
operator|!=
literal|null
condition|?
operator|(
literal|"*"
operator|.
name|equals
argument_list|(
name|Term
operator|.
name|toString
argument_list|(
name|lowerTerm
argument_list|)
argument_list|)
condition|?
literal|"\\*"
else|:
name|Term
operator|.
name|toString
argument_list|(
name|lowerTerm
argument_list|)
operator|)
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
operator|(
literal|"*"
operator|.
name|equals
argument_list|(
name|Term
operator|.
name|toString
argument_list|(
name|upperTerm
argument_list|)
argument_list|)
condition|?
literal|"\\*"
else|:
name|Term
operator|.
name|toString
argument_list|(
name|upperTerm
argument_list|)
operator|)
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
