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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ToStringUtils
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
name|Operations
import|;
end_import
begin_comment
comment|/** Implements the wildcard search query. Supported wildcards are<code>*</code>, which  * matches any character sequence (including the empty one), and<code>?</code>,  * which matches any single character. '\' is the escape character.  *<p>  * Note this query can be slow, as it  * needs to iterate over many terms. In order to prevent extremely slow WildcardQueries,  * a Wildcard term should not start with the wildcard<code>*</code>  *   *<p>This query uses the {@link  * MultiTermQuery#CONSTANT_SCORE_FILTER_REWRITE}  * rewrite method.  *  * @see AutomatonQuery  */
end_comment
begin_class
DECL|class|WildcardQuery
specifier|public
class|class
name|WildcardQuery
extends|extends
name|AutomatonQuery
block|{
comment|/** String equality with support for wildcards */
DECL|field|WILDCARD_STRING
specifier|public
specifier|static
specifier|final
name|char
name|WILDCARD_STRING
init|=
literal|'*'
decl_stmt|;
comment|/** Char equality with support for wildcards */
DECL|field|WILDCARD_CHAR
specifier|public
specifier|static
specifier|final
name|char
name|WILDCARD_CHAR
init|=
literal|'?'
decl_stmt|;
comment|/** Escape character */
DECL|field|WILDCARD_ESCAPE
specifier|public
specifier|static
specifier|final
name|char
name|WILDCARD_ESCAPE
init|=
literal|'\\'
decl_stmt|;
comment|/**    * Constructs a query for terms matching<code>term</code>.     */
DECL|method|WildcardQuery
specifier|public
name|WildcardQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|,
name|toAutomaton
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a query for terms matching<code>term</code>.    * @param maxDeterminizedStates maximum number of states in the resulting    *   automata.  If the automata would need more than this many states    *   TooComplextToDeterminizeException is thrown.  Higher number require more    *   space but can process more complex automata.    */
DECL|method|WildcardQuery
specifier|public
name|WildcardQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|,
name|toAutomaton
argument_list|(
name|term
argument_list|)
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert Lucene wildcard syntax into an automaton.    * @lucene.internal    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|toAutomaton
specifier|public
specifier|static
name|Automaton
name|toAutomaton
parameter_list|(
name|Term
name|wildcardquery
parameter_list|)
block|{
name|List
argument_list|<
name|Automaton
argument_list|>
name|automata
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|wildcardText
init|=
name|wildcardquery
operator|.
name|text
argument_list|()
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
name|wildcardText
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|c
init|=
name|wildcardText
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|Character
operator|.
name|charCount
argument_list|(
name|c
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
name|WILDCARD_STRING
case|:
name|automata
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeAnyString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|WILDCARD_CHAR
case|:
name|automata
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeAnyChar
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|WILDCARD_ESCAPE
case|:
comment|// add the next codepoint instead, if it exists
if|if
condition|(
name|i
operator|+
name|length
operator|<
name|wildcardText
operator|.
name|length
argument_list|()
condition|)
block|{
specifier|final
name|int
name|nextChar
init|=
name|wildcardText
operator|.
name|codePointAt
argument_list|(
name|i
operator|+
name|length
argument_list|)
decl_stmt|;
name|length
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|nextChar
argument_list|)
expr_stmt|;
name|automata
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeChar
argument_list|(
name|nextChar
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// else fallthru, lenient parsing with a trailing \
default|default:
name|automata
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeChar
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|i
operator|+=
name|length
expr_stmt|;
block|}
return|return
name|Operations
operator|.
name|concatenate
argument_list|(
name|automata
argument_list|)
return|;
block|}
comment|/**    * Returns the pattern term.    */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
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
name|term
operator|.
name|text
argument_list|()
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
block|}
end_class
end_unit
