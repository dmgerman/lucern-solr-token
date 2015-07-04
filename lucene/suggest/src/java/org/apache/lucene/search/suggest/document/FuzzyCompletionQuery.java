begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|analysis
operator|.
name|Analyzer
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
name|search
operator|.
name|IndexSearcher
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
name|Weight
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
name|suggest
operator|.
name|BitsProducer
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
name|IntsRef
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
name|UnicodeUtil
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
name|FiniteStringsIterator
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
name|LevenshteinAutomata
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
name|UTF32ToUTF8
import|;
end_import
begin_comment
comment|/**  * A {@link CompletionQuery} that match documents containing terms  * within an edit distance of the specified prefix.  *<p>  * This query boost documents relative to how similar the indexed terms are to the  * provided prefix.  *<p>  * Example usage of querying an analyzed prefix within an edit distance of 1 of 'subg'  * against a field 'suggest_field' is as follows:  *  *<pre class="prettyprint">  *  CompletionQuery query = new FuzzyCompletionQuery(analyzer, new Term("suggest_field", "subg"));  *</pre>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FuzzyCompletionQuery
specifier|public
class|class
name|FuzzyCompletionQuery
extends|extends
name|PrefixCompletionQuery
block|{
comment|/**    * Measure maxEdits, minFuzzyLength, transpositions and nonFuzzyPrefix    * parameters in Unicode code points (actual letters)    * instead of bytes.    * */
DECL|field|DEFAULT_UNICODE_AWARE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_UNICODE_AWARE
init|=
literal|false
decl_stmt|;
comment|/**    * The default minimum length of the key before any edits are allowed.    */
DECL|field|DEFAULT_MIN_FUZZY_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_FUZZY_LENGTH
init|=
literal|3
decl_stmt|;
comment|/**    * The default prefix length where edits are not allowed.    */
DECL|field|DEFAULT_NON_FUZZY_PREFIX
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NON_FUZZY_PREFIX
init|=
literal|1
decl_stmt|;
comment|/**    * The default maximum number of edits for fuzzy    * suggestions.    */
DECL|field|DEFAULT_MAX_EDITS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_EDITS
init|=
literal|1
decl_stmt|;
comment|/**    * The default transposition value passed to {@link LevenshteinAutomata}    */
DECL|field|DEFAULT_TRANSPOSITIONS
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_TRANSPOSITIONS
init|=
literal|true
decl_stmt|;
DECL|field|maxEdits
specifier|private
specifier|final
name|int
name|maxEdits
decl_stmt|;
DECL|field|transpositions
specifier|private
specifier|final
name|boolean
name|transpositions
decl_stmt|;
DECL|field|nonFuzzyPrefix
specifier|private
specifier|final
name|int
name|nonFuzzyPrefix
decl_stmt|;
DECL|field|minFuzzyLength
specifier|private
specifier|final
name|int
name|minFuzzyLength
decl_stmt|;
DECL|field|unicodeAware
specifier|private
specifier|final
name|boolean
name|unicodeAware
decl_stmt|;
DECL|field|maxDeterminizedStates
specifier|private
specifier|final
name|int
name|maxDeterminizedStates
decl_stmt|;
comment|/**    * Calls {@link FuzzyCompletionQuery#FuzzyCompletionQuery(Analyzer, Term, BitsProducer)}    * with no filter    */
DECL|method|FuzzyCompletionQuery
specifier|public
name|FuzzyCompletionQuery
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Term
name|term
parameter_list|)
block|{
name|this
argument_list|(
name|analyzer
argument_list|,
name|term
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link FuzzyCompletionQuery#FuzzyCompletionQuery(Analyzer, Term, BitsProducer,    * int, boolean, int, int, boolean, int)}    * with defaults for<code>maxEdits</code>,<code>transpositions</code>,    *<code>nonFuzzyPrefix</code>,<code>minFuzzyLength</code>,    *<code>unicodeAware</code> and<code>maxDeterminizedStates</code>    *    * See {@link #DEFAULT_MAX_EDITS}, {@link #DEFAULT_TRANSPOSITIONS},    * {@link #DEFAULT_NON_FUZZY_PREFIX}, {@link #DEFAULT_MIN_FUZZY_LENGTH},    * {@link #DEFAULT_UNICODE_AWARE} and {@link Operations#DEFAULT_MAX_DETERMINIZED_STATES}    * for defaults    */
DECL|method|FuzzyCompletionQuery
specifier|public
name|FuzzyCompletionQuery
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Term
name|term
parameter_list|,
name|BitsProducer
name|filter
parameter_list|)
block|{
name|this
argument_list|(
name|analyzer
argument_list|,
name|term
argument_list|,
name|filter
argument_list|,
name|DEFAULT_MAX_EDITS
argument_list|,
name|DEFAULT_TRANSPOSITIONS
argument_list|,
name|DEFAULT_NON_FUZZY_PREFIX
argument_list|,
name|DEFAULT_MIN_FUZZY_LENGTH
argument_list|,
name|DEFAULT_UNICODE_AWARE
argument_list|,
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs an analyzed fuzzy prefix completion query    *    * @param analyzer used to analyze the provided {@link Term#text()}    * @param term query is run against {@link Term#field()} and {@link Term#text()}    *             is analyzed with<code>analyzer</code>    * @param filter used to query on a sub set of documents    * @param maxEdits maximum number of acceptable edits    * @param transpositions value passed to {@link LevenshteinAutomata}    * @param nonFuzzyPrefix prefix length where edits are not allowed    * @param minFuzzyLength minimum prefix length before any edits are allowed    * @param unicodeAware treat prefix as unicode rather than bytes    * @param maxDeterminizedStates maximum automaton states allowed for {@link LevenshteinAutomata}    */
DECL|method|FuzzyCompletionQuery
specifier|public
name|FuzzyCompletionQuery
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Term
name|term
parameter_list|,
name|BitsProducer
name|filter
parameter_list|,
name|int
name|maxEdits
parameter_list|,
name|boolean
name|transpositions
parameter_list|,
name|int
name|nonFuzzyPrefix
parameter_list|,
name|int
name|minFuzzyLength
parameter_list|,
name|boolean
name|unicodeAware
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|)
block|{
name|super
argument_list|(
name|analyzer
argument_list|,
name|term
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxEdits
operator|=
name|maxEdits
expr_stmt|;
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
name|this
operator|.
name|nonFuzzyPrefix
operator|=
name|nonFuzzyPrefix
expr_stmt|;
name|this
operator|.
name|minFuzzyLength
operator|=
name|minFuzzyLength
expr_stmt|;
name|this
operator|.
name|unicodeAware
operator|=
name|unicodeAware
expr_stmt|;
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|maxDeterminizedStates
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|CompletionTokenStream
name|stream
init|=
operator|(
name|CompletionTokenStream
operator|)
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|getField
argument_list|()
argument_list|,
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|refs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Automaton
name|automaton
init|=
name|toLevenshteinAutomata
argument_list|(
name|stream
operator|.
name|toAutomaton
argument_list|(
name|unicodeAware
argument_list|)
argument_list|,
name|refs
argument_list|)
decl_stmt|;
if|if
condition|(
name|unicodeAware
condition|)
block|{
name|Automaton
name|utf8automaton
init|=
operator|new
name|UTF32ToUTF8
argument_list|()
operator|.
name|convert
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
name|utf8automaton
operator|=
name|Operations
operator|.
name|determinize
argument_list|(
name|utf8automaton
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
name|automaton
operator|=
name|utf8automaton
expr_stmt|;
block|}
comment|// TODO Accumulating all refs is bad, because the resulting set may be very big.
comment|// TODO Better iterate over automaton again inside FuzzyCompletionWeight?
return|return
operator|new
name|FuzzyCompletionWeight
argument_list|(
name|this
argument_list|,
name|automaton
argument_list|,
name|refs
argument_list|)
return|;
block|}
DECL|method|toLevenshteinAutomata
specifier|private
name|Automaton
name|toLevenshteinAutomata
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|refs
parameter_list|)
block|{
name|List
argument_list|<
name|Automaton
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|FiniteStringsIterator
name|finiteStrings
init|=
operator|new
name|FiniteStringsIterator
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
for|for
control|(
name|IntsRef
name|string
init|;
operator|(
name|string
operator|=
name|finiteStrings
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|string
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|string
operator|.
name|length
operator|<=
name|nonFuzzyPrefix
operator|||
name|string
operator|.
name|length
operator|<
name|minFuzzyLength
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|string
operator|.
name|ints
argument_list|,
name|string
operator|.
name|offset
argument_list|,
name|string
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|ints
index|[]
init|=
operator|new
name|int
index|[
name|string
operator|.
name|length
operator|-
name|nonFuzzyPrefix
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|string
operator|.
name|ints
argument_list|,
name|string
operator|.
name|offset
operator|+
name|nonFuzzyPrefix
argument_list|,
name|ints
argument_list|,
literal|0
argument_list|,
name|ints
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// TODO: maybe add alphaMin to LevenshteinAutomata,
comment|// and pass 1 instead of 0?  We probably don't want
comment|// to allow the trailing dedup bytes to be
comment|// edited... but then 0 byte is "in general" allowed
comment|// on input (but not in UTF8).
name|LevenshteinAutomata
name|lev
init|=
operator|new
name|LevenshteinAutomata
argument_list|(
name|ints
argument_list|,
name|unicodeAware
condition|?
name|Character
operator|.
name|MAX_CODE_POINT
else|:
literal|255
argument_list|,
name|transpositions
argument_list|)
decl_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|lev
operator|.
name|toAutomaton
argument_list|(
name|maxEdits
argument_list|,
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|string
operator|.
name|ints
argument_list|,
name|string
operator|.
name|offset
argument_list|,
name|nonFuzzyPrefix
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|subs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// automaton is empty, there is no accepted paths through it
return|return
name|Automata
operator|.
name|makeEmpty
argument_list|()
return|;
comment|// matches nothing
block|}
elseif|else
if|if
condition|(
name|subs
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// no synonyms or anything: just a single path through the tokenstream
return|return
name|subs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
comment|// multiple paths: this is really scary! is it slow?
comment|// maybe we should not do this and throw UOE?
name|Automaton
name|a
init|=
name|Operations
operator|.
name|union
argument_list|(
name|subs
argument_list|)
decl_stmt|;
comment|// TODO: we could call toLevenshteinAutomata() before det?
comment|// this only happens if you have multiple paths anyway (e.g. synonyms)
return|return
name|Operations
operator|.
name|determinize
argument_list|(
name|a
argument_list|,
name|maxDeterminizedStates
argument_list|)
return|;
block|}
block|}
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
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'~'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|maxEdits
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"filter"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getFilter
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|FuzzyCompletionWeight
specifier|private
specifier|static
class|class
name|FuzzyCompletionWeight
extends|extends
name|CompletionWeight
block|{
DECL|field|refs
specifier|private
specifier|final
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|refs
decl_stmt|;
DECL|field|currentBoost
name|int
name|currentBoost
init|=
literal|0
decl_stmt|;
DECL|method|FuzzyCompletionWeight
specifier|public
name|FuzzyCompletionWeight
parameter_list|(
name|CompletionQuery
name|query
parameter_list|,
name|Automaton
name|automaton
parameter_list|,
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|refs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|automaton
argument_list|)
expr_stmt|;
name|this
operator|.
name|refs
operator|=
name|refs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextMatch
specifier|protected
name|void
name|setNextMatch
parameter_list|(
name|IntsRef
name|pathPrefix
parameter_list|)
block|{
comment|// NOTE: the last letter of the matched prefix for the exact
comment|// match never makes it through here
comment|// so an exact match and a match with only a edit at the
comment|// end is boosted the same
name|int
name|maxCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IntsRef
name|ref
range|:
name|refs
control|)
block|{
name|int
name|minLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|ref
operator|.
name|length
argument_list|,
name|pathPrefix
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|count
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
name|minLength
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ref
operator|.
name|ints
index|[
name|i
operator|+
name|ref
operator|.
name|offset
index|]
operator|==
name|pathPrefix
operator|.
name|ints
index|[
name|i
operator|+
name|pathPrefix
operator|.
name|offset
index|]
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|maxCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxCount
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|currentBoost
operator|=
name|maxCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|boost
specifier|protected
name|float
name|boost
parameter_list|()
block|{
return|return
name|currentBoost
return|;
block|}
block|}
block|}
end_class
end_unit
