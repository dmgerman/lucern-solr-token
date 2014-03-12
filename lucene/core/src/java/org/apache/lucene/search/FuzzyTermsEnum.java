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
name|Comparator
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
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|index
operator|.
name|TermState
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
name|FilteredTermsEnum
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
name|Attribute
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
name|AttributeImpl
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
name|AttributeSource
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
name|BasicAutomata
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
name|BasicOperations
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
name|ByteRunAutomaton
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
name|CompiledAutomaton
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
begin_comment
comment|/** Subclass of TermsEnum for enumerating all terms that are similar  * to the specified filter term.  *  *<p>Term enumerations are always ordered by  * {@link BytesRef#compareTo}.  Each term in the enumeration is  * greater than all that precede it.</p>  */
end_comment
begin_class
DECL|class|FuzzyTermsEnum
specifier|public
class|class
name|FuzzyTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|actualEnum
specifier|private
name|TermsEnum
name|actualEnum
decl_stmt|;
DECL|field|actualBoostAtt
specifier|private
name|BoostAttribute
name|actualBoostAtt
decl_stmt|;
DECL|field|boostAtt
specifier|private
specifier|final
name|BoostAttribute
name|boostAtt
init|=
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|BoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxBoostAtt
specifier|private
specifier|final
name|MaxNonCompetitiveBoostAttribute
name|maxBoostAtt
decl_stmt|;
DECL|field|dfaAtt
specifier|private
specifier|final
name|LevenshteinAutomataAttribute
name|dfaAtt
decl_stmt|;
DECL|field|bottom
specifier|private
name|float
name|bottom
decl_stmt|;
DECL|field|bottomTerm
specifier|private
name|BytesRef
name|bottomTerm
decl_stmt|;
comment|// TODO: chicken-and-egg
DECL|field|termComparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComparator
init|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
decl_stmt|;
DECL|field|minSimilarity
specifier|protected
specifier|final
name|float
name|minSimilarity
decl_stmt|;
DECL|field|scale_factor
specifier|protected
specifier|final
name|float
name|scale_factor
decl_stmt|;
DECL|field|termLength
specifier|protected
specifier|final
name|int
name|termLength
decl_stmt|;
DECL|field|maxEdits
specifier|protected
name|int
name|maxEdits
decl_stmt|;
DECL|field|raw
specifier|protected
specifier|final
name|boolean
name|raw
decl_stmt|;
DECL|field|terms
specifier|protected
specifier|final
name|Terms
name|terms
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|termText
specifier|protected
specifier|final
name|int
name|termText
index|[]
decl_stmt|;
DECL|field|realPrefixLength
specifier|protected
specifier|final
name|int
name|realPrefixLength
decl_stmt|;
DECL|field|transpositions
specifier|private
specifier|final
name|boolean
name|transpositions
decl_stmt|;
comment|/**    * Constructor for enumeration of all terms from specified<code>reader</code> which share a prefix of    * length<code>prefixLength</code> with<code>term</code> and which have a fuzzy similarity&gt;    *<code>minSimilarity</code>.    *<p>    * After calling the constructor the enumeration is already pointing to the first     * valid term if such a term exists.     *     * @param terms Delivers terms.    * @param atts {@link AttributeSource} created by the rewrite method of {@link MultiTermQuery}    * thats contains information about competitive boosts during rewrite. It is also used    * to cache DFAs between segment transitions.    * @param term Pattern term.    * @param minSimilarity Minimum required similarity for terms from the reader. Pass an integer value    *        representing edit distance. Passing a fraction is deprecated.    * @param prefixLength Length of required common prefix. Default value is 0.    * @throws IOException if there is a low-level IO error    */
DECL|method|FuzzyTermsEnum
specifier|public
name|FuzzyTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|,
name|Term
name|term
parameter_list|,
specifier|final
name|float
name|minSimilarity
parameter_list|,
specifier|final
name|int
name|prefixLength
parameter_list|,
name|boolean
name|transpositions
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|minSimilarity
operator|>=
literal|1.0f
operator|&&
name|minSimilarity
operator|!=
operator|(
name|int
operator|)
name|minSimilarity
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fractional edit distances are not allowed"
argument_list|)
throw|;
if|if
condition|(
name|minSimilarity
operator|<
literal|0.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity cannot be less than 0"
argument_list|)
throw|;
if|if
condition|(
name|prefixLength
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefixLength cannot be less than 0"
argument_list|)
throw|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
comment|// convert the string into a utf32 int[] representation for fast comparisons
specifier|final
name|String
name|utf16
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
name|this
operator|.
name|termText
operator|=
operator|new
name|int
index|[
name|utf16
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|utf16
operator|.
name|length
argument_list|()
argument_list|)
index|]
expr_stmt|;
for|for
control|(
name|int
name|cp
init|,
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
name|utf16
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
name|termText
index|[
name|j
operator|++
index|]
operator|=
name|cp
operator|=
name|utf16
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|this
operator|.
name|termLength
operator|=
name|termText
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|dfaAtt
operator|=
name|atts
operator|.
name|addAttribute
argument_list|(
name|LevenshteinAutomataAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//The prefix could be longer than the word.
comment|//It's kind of silly though.  It means we must match the entire word.
name|this
operator|.
name|realPrefixLength
operator|=
name|prefixLength
operator|>
name|termLength
condition|?
name|termLength
else|:
name|prefixLength
expr_stmt|;
comment|// if minSimilarity>= 1, we treat it as number of edits
if|if
condition|(
name|minSimilarity
operator|>=
literal|1f
condition|)
block|{
name|this
operator|.
name|minSimilarity
operator|=
literal|0
expr_stmt|;
comment|// just driven by number of edits
name|maxEdits
operator|=
operator|(
name|int
operator|)
name|minSimilarity
expr_stmt|;
name|raw
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|minSimilarity
operator|=
name|minSimilarity
expr_stmt|;
comment|// calculate the maximum k edits for this similarity
name|maxEdits
operator|=
name|initialMaxDistance
argument_list|(
name|this
operator|.
name|minSimilarity
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
name|raw
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|transpositions
operator|&&
name|maxEdits
operator|>
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"with transpositions enabled, distances> "
operator|+
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
operator|+
literal|" are not supported "
argument_list|)
throw|;
block|}
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
name|this
operator|.
name|scale_factor
operator|=
literal|1.0f
operator|/
operator|(
literal|1.0f
operator|-
name|this
operator|.
name|minSimilarity
operator|)
expr_stmt|;
name|this
operator|.
name|maxBoostAtt
operator|=
name|atts
operator|.
name|addAttribute
argument_list|(
name|MaxNonCompetitiveBoostAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|bottom
operator|=
name|maxBoostAtt
operator|.
name|getMaxNonCompetitiveBoost
argument_list|()
expr_stmt|;
name|bottomTerm
operator|=
name|maxBoostAtt
operator|.
name|getCompetitiveTerm
argument_list|()
expr_stmt|;
name|bottomChanged
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * return an automata-based enum for matching up to editDistance from    * lastTerm, if possible    */
DECL|method|getAutomatonEnum
specifier|protected
name|TermsEnum
name|getAutomatonEnum
parameter_list|(
name|int
name|editDistance
parameter_list|,
name|BytesRef
name|lastTerm
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|CompiledAutomaton
argument_list|>
name|runAutomata
init|=
name|initAutomata
argument_list|(
name|editDistance
argument_list|)
decl_stmt|;
if|if
condition|(
name|editDistance
operator|<
name|runAutomata
operator|.
name|size
argument_list|()
condition|)
block|{
comment|//if (BlockTreeTermsWriter.DEBUG) System.out.println("FuzzyTE.getAEnum: ed=" + editDistance + " lastTerm=" + (lastTerm==null ? "null" : lastTerm.utf8ToString()));
specifier|final
name|CompiledAutomaton
name|compiled
init|=
name|runAutomata
operator|.
name|get
argument_list|(
name|editDistance
argument_list|)
decl_stmt|;
return|return
operator|new
name|AutomatonFuzzyTermsEnum
argument_list|(
name|terms
operator|.
name|intersect
argument_list|(
name|compiled
argument_list|,
name|lastTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|compiled
operator|.
name|floor
argument_list|(
name|lastTerm
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|runAutomata
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|editDistance
operator|+
literal|1
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|CompiledAutomaton
index|[
name|editDistance
operator|+
literal|1
index|]
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** initialize levenshtein DFAs up to maxDistance, if possible */
DECL|method|initAutomata
specifier|private
name|List
argument_list|<
name|CompiledAutomaton
argument_list|>
name|initAutomata
parameter_list|(
name|int
name|maxDistance
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|CompiledAutomaton
argument_list|>
name|runAutomata
init|=
name|dfaAtt
operator|.
name|automata
argument_list|()
decl_stmt|;
comment|//System.out.println("cached automata size: " + runAutomata.size());
if|if
condition|(
name|runAutomata
operator|.
name|size
argument_list|()
operator|<=
name|maxDistance
operator|&&
name|maxDistance
operator|<=
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
condition|)
block|{
name|LevenshteinAutomata
name|builder
init|=
operator|new
name|LevenshteinAutomata
argument_list|(
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|termText
argument_list|,
name|realPrefixLength
argument_list|,
name|termText
operator|.
name|length
operator|-
name|realPrefixLength
argument_list|)
argument_list|,
name|transpositions
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|runAutomata
operator|.
name|size
argument_list|()
init|;
name|i
operator|<=
name|maxDistance
condition|;
name|i
operator|++
control|)
block|{
name|Automaton
name|a
init|=
name|builder
operator|.
name|toAutomaton
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//System.out.println("compute automaton n=" + i);
comment|// constant prefix
if|if
condition|(
name|realPrefixLength
operator|>
literal|0
condition|)
block|{
name|Automaton
name|prefix
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|UnicodeUtil
operator|.
name|newString
argument_list|(
name|termText
argument_list|,
literal|0
argument_list|,
name|realPrefixLength
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|prefix
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
name|runAutomata
operator|.
name|add
argument_list|(
operator|new
name|CompiledAutomaton
argument_list|(
name|a
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|runAutomata
return|;
block|}
comment|/** swap in a new actual enum to proxy to */
DECL|method|setEnum
specifier|protected
name|void
name|setEnum
parameter_list|(
name|TermsEnum
name|actualEnum
parameter_list|)
block|{
name|this
operator|.
name|actualEnum
operator|=
name|actualEnum
expr_stmt|;
name|this
operator|.
name|actualBoostAtt
operator|=
name|actualEnum
operator|.
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|BoostAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * fired when the max non-competitive boost has changed. this is the hook to    * swap in a smarter actualEnum    */
DECL|method|bottomChanged
specifier|private
name|void
name|bottomChanged
parameter_list|(
name|BytesRef
name|lastTerm
parameter_list|,
name|boolean
name|init
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|oldMaxEdits
init|=
name|maxEdits
decl_stmt|;
comment|// true if the last term encountered is lexicographically equal or after the bottom term in the PQ
name|boolean
name|termAfter
init|=
name|bottomTerm
operator|==
literal|null
operator|||
operator|(
name|lastTerm
operator|!=
literal|null
operator|&&
name|termComparator
operator|.
name|compare
argument_list|(
name|lastTerm
argument_list|,
name|bottomTerm
argument_list|)
operator|>=
literal|0
operator|)
decl_stmt|;
comment|// as long as the max non-competitive boost is>= the max boost
comment|// for some edit distance, keep dropping the max edit distance.
while|while
condition|(
name|maxEdits
operator|>
literal|0
operator|&&
operator|(
name|termAfter
condition|?
name|bottom
operator|>=
name|calculateMaxBoost
argument_list|(
name|maxEdits
argument_list|)
else|:
name|bottom
operator|>
name|calculateMaxBoost
argument_list|(
name|maxEdits
argument_list|)
operator|)
condition|)
name|maxEdits
operator|--
expr_stmt|;
if|if
condition|(
name|oldMaxEdits
operator|!=
name|maxEdits
operator|||
name|init
condition|)
block|{
comment|// the maximum n has changed
name|maxEditDistanceChanged
argument_list|(
name|lastTerm
argument_list|,
name|maxEdits
argument_list|,
name|init
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maxEditDistanceChanged
specifier|protected
name|void
name|maxEditDistanceChanged
parameter_list|(
name|BytesRef
name|lastTerm
parameter_list|,
name|int
name|maxEdits
parameter_list|,
name|boolean
name|init
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|newEnum
init|=
name|getAutomatonEnum
argument_list|(
name|maxEdits
argument_list|,
name|lastTerm
argument_list|)
decl_stmt|;
comment|// instead of assert, we do a hard check in case someone uses our enum directly
comment|// assert newEnum != null;
if|if
condition|(
name|newEnum
operator|==
literal|null
condition|)
block|{
assert|assert
name|maxEdits
operator|>
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
assert|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxEdits cannot be> LevenshteinAutomata.MAXIMUM_SUPPORTED_DISTANCE"
argument_list|)
throw|;
block|}
name|setEnum
argument_list|(
name|newEnum
argument_list|)
expr_stmt|;
block|}
comment|// for some raw min similarity and input term length, the maximum # of edits
DECL|method|initialMaxDistance
specifier|private
name|int
name|initialMaxDistance
parameter_list|(
name|float
name|minimumSimilarity
parameter_list|,
name|int
name|termLen
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
operator|(
literal|1D
operator|-
name|minimumSimilarity
operator|)
operator|*
name|termLen
argument_list|)
return|;
block|}
comment|// for some number of edits, the maximum possible scaled boost
DECL|method|calculateMaxBoost
specifier|private
name|float
name|calculateMaxBoost
parameter_list|(
name|int
name|nEdits
parameter_list|)
block|{
specifier|final
name|float
name|similarity
init|=
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|nEdits
operator|/
call|(
name|float
call|)
argument_list|(
name|termLength
argument_list|)
operator|)
decl_stmt|;
return|return
operator|(
name|similarity
operator|-
name|minSimilarity
operator|)
operator|*
name|scale_factor
return|;
block|}
DECL|field|queuedBottom
specifier|private
name|BytesRef
name|queuedBottom
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|queuedBottom
operator|!=
literal|null
condition|)
block|{
name|bottomChanged
argument_list|(
name|queuedBottom
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|queuedBottom
operator|=
literal|null
expr_stmt|;
block|}
name|BytesRef
name|term
init|=
name|actualEnum
operator|.
name|next
argument_list|()
decl_stmt|;
name|boostAtt
operator|.
name|setBoost
argument_list|(
name|actualBoostAtt
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|float
name|bottom
init|=
name|maxBoostAtt
operator|.
name|getMaxNonCompetitiveBoost
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|bottomTerm
init|=
name|maxBoostAtt
operator|.
name|getCompetitiveTerm
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
operator|(
name|bottom
operator|!=
name|this
operator|.
name|bottom
operator|||
name|bottomTerm
operator|!=
name|this
operator|.
name|bottomTerm
operator|)
condition|)
block|{
name|this
operator|.
name|bottom
operator|=
name|bottom
expr_stmt|;
name|this
operator|.
name|bottomTerm
operator|=
name|bottomTerm
expr_stmt|;
comment|// clone the term before potentially doing something with it
comment|// this is a rare but wonderful occurrence anyway
name|queuedBottom
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
return|return
name|term
return|;
block|}
comment|// proxy all other enum calls to the actual enum
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|docFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|docs
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|docsAndPositions
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|actualEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|termState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|ord
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|seekExact
argument_list|(
name|text
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|seekCeil
argument_list|(
name|text
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
name|actualEnum
operator|.
name|seekExact
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|actualEnum
operator|.
name|term
argument_list|()
return|;
block|}
comment|/**    * Implement fuzzy enumeration with Terms.intersect.    *<p>    * This is the fastest method as opposed to LinearFuzzyTermsEnum:    * as enumeration is logarithmic to the number of terms (instead of linear)    * and comparison is linear to length of the term (rather than quadratic)    */
DECL|class|AutomatonFuzzyTermsEnum
specifier|private
class|class
name|AutomatonFuzzyTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|matchers
specifier|private
specifier|final
name|ByteRunAutomaton
name|matchers
index|[]
decl_stmt|;
DECL|field|termRef
specifier|private
specifier|final
name|BytesRef
name|termRef
decl_stmt|;
DECL|field|boostAtt
specifier|private
specifier|final
name|BoostAttribute
name|boostAtt
init|=
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|BoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AutomatonFuzzyTermsEnum
specifier|public
name|AutomatonFuzzyTermsEnum
parameter_list|(
name|TermsEnum
name|tenum
parameter_list|,
name|CompiledAutomaton
name|compiled
index|[]
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchers
operator|=
operator|new
name|ByteRunAutomaton
index|[
name|compiled
operator|.
name|length
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
name|compiled
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|this
operator|.
name|matchers
index|[
name|i
index|]
operator|=
name|compiled
index|[
name|i
index|]
operator|.
name|runAutomaton
expr_stmt|;
name|termRef
operator|=
operator|new
name|BytesRef
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** finds the smallest Lev(n) DFA that accepts the term. */
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
comment|//System.out.println("AFTE.accept term=" + term);
name|int
name|ed
init|=
name|matchers
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// we are wrapping either an intersect() TermsEnum or an AutomatonTermsENum,
comment|// so we know the outer DFA always matches.
comment|// now compute exact edit distance
while|while
condition|(
name|ed
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|term
argument_list|,
name|ed
operator|-
literal|1
argument_list|)
condition|)
block|{
name|ed
operator|--
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
comment|//System.out.println("CHECK term=" + term.utf8ToString() + " ed=" + ed);
comment|// scale to a boost and return (if similarity> minSimilarity)
if|if
condition|(
name|ed
operator|==
literal|0
condition|)
block|{
comment|// exact match
name|boostAtt
operator|.
name|setBoost
argument_list|(
literal|1.0F
argument_list|)
expr_stmt|;
comment|//System.out.println("  yes");
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
specifier|final
name|int
name|codePointCount
init|=
name|UnicodeUtil
operator|.
name|codePointCount
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|final
name|float
name|similarity
init|=
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|ed
operator|/
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|codePointCount
argument_list|,
name|termLength
argument_list|)
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|similarity
operator|>
name|minSimilarity
condition|)
block|{
name|boostAtt
operator|.
name|setBoost
argument_list|(
operator|(
name|similarity
operator|-
name|minSimilarity
operator|)
operator|*
name|scale_factor
argument_list|)
expr_stmt|;
comment|//System.out.println("  yes");
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
block|}
comment|/** returns true if term is within k edits of the query term */
DECL|method|matches
specifier|final
name|boolean
name|matches
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|k
parameter_list|)
block|{
return|return
name|k
operator|==
literal|0
condition|?
name|term
operator|.
name|equals
argument_list|(
name|termRef
argument_list|)
else|:
name|matchers
index|[
name|k
index|]
operator|.
name|run
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
argument_list|,
name|term
operator|.
name|length
argument_list|)
return|;
block|}
block|}
comment|/** @lucene.internal */
DECL|method|getMinSimilarity
specifier|public
name|float
name|getMinSimilarity
parameter_list|()
block|{
return|return
name|minSimilarity
return|;
block|}
comment|/** @lucene.internal */
DECL|method|getScaleFactor
specifier|public
name|float
name|getScaleFactor
parameter_list|()
block|{
return|return
name|scale_factor
return|;
block|}
comment|/**    * reuses compiled automata across different segments,    * because they are independent of the index    * @lucene.internal */
DECL|interface|LevenshteinAutomataAttribute
specifier|public
specifier|static
interface|interface
name|LevenshteinAutomataAttribute
extends|extends
name|Attribute
block|{
DECL|method|automata
specifier|public
name|List
argument_list|<
name|CompiledAutomaton
argument_list|>
name|automata
parameter_list|()
function_decl|;
block|}
comment|/**     * Stores compiled automata as a list (indexed by edit distance)    * @lucene.internal */
DECL|class|LevenshteinAutomataAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|LevenshteinAutomataAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|LevenshteinAutomataAttribute
block|{
DECL|field|automata
specifier|private
specifier|final
name|List
argument_list|<
name|CompiledAutomaton
argument_list|>
name|automata
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|automata
specifier|public
name|List
argument_list|<
name|CompiledAutomaton
argument_list|>
name|automata
parameter_list|()
block|{
return|return
name|automata
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|automata
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|automata
operator|.
name|hashCode
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
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|LevenshteinAutomataAttributeImpl
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|automata
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|LevenshteinAutomataAttributeImpl
operator|)
name|other
operator|)
operator|.
name|automata
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|CompiledAutomaton
argument_list|>
name|targetAutomata
init|=
operator|(
operator|(
name|LevenshteinAutomataAttribute
operator|)
name|target
operator|)
operator|.
name|automata
argument_list|()
decl_stmt|;
name|targetAutomata
operator|.
name|clear
argument_list|()
expr_stmt|;
name|targetAutomata
operator|.
name|addAll
argument_list|(
name|automata
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
