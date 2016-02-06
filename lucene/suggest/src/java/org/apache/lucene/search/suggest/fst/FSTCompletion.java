begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
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
name|fst
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
name|java
operator|.
name|util
operator|.
name|*
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
name|*
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|FST
operator|.
name|Arc
import|;
end_import
begin_comment
comment|/**  * Finite state automata based implementation of "autocomplete" functionality.  *   * @see FSTCompletionBuilder  * @lucene.experimental  */
end_comment
begin_comment
comment|// TODO: we could store exact weights as outputs from the FST (int4 encoded
end_comment
begin_comment
comment|// floats). This would provide exact outputs from this method and to some
end_comment
begin_comment
comment|// degree allowed post-sorting on a more fine-grained weight.
end_comment
begin_comment
comment|// TODO: support for Analyzers (infix suggestions, synonyms?)
end_comment
begin_class
DECL|class|FSTCompletion
specifier|public
class|class
name|FSTCompletion
block|{
comment|/**    * A single completion for a given key.    */
DECL|class|Completion
specifier|public
specifier|static
specifier|final
class|class
name|Completion
implements|implements
name|Comparable
argument_list|<
name|Completion
argument_list|>
block|{
comment|/** UTF-8 bytes of the suggestion */
DECL|field|utf8
specifier|public
specifier|final
name|BytesRef
name|utf8
decl_stmt|;
comment|/** source bucket (weight) of the suggestion */
DECL|field|bucket
specifier|public
specifier|final
name|int
name|bucket
decl_stmt|;
DECL|method|Completion
name|Completion
parameter_list|(
name|BytesRef
name|key
parameter_list|,
name|int
name|bucket
parameter_list|)
block|{
name|this
operator|.
name|utf8
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|this
operator|.
name|bucket
operator|=
name|bucket
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|utf8
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|"/"
operator|+
name|bucket
return|;
block|}
comment|/** @see BytesRef#compareTo(BytesRef) */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Completion
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|utf8
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|utf8
argument_list|)
return|;
block|}
block|}
comment|/**     * Default number of buckets.    */
DECL|field|DEFAULT_BUCKETS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BUCKETS
init|=
literal|10
decl_stmt|;
comment|/**    * An empty result. Keep this an {@link ArrayList} to keep all the returned    * lists of single type (monomorphic calls).    */
DECL|field|EMPTY_RESULT
specifier|private
specifier|static
specifier|final
name|ArrayList
argument_list|<
name|Completion
argument_list|>
name|EMPTY_RESULT
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Finite state automaton encoding all the lookup terms. See class notes for    * details.    */
DECL|field|automaton
specifier|private
specifier|final
name|FST
argument_list|<
name|Object
argument_list|>
name|automaton
decl_stmt|;
comment|/**    * An array of arcs leaving the root automaton state and encoding weights of    * all completions in their sub-trees.    */
DECL|field|rootArcs
specifier|private
specifier|final
name|Arc
argument_list|<
name|Object
argument_list|>
index|[]
name|rootArcs
decl_stmt|;
comment|/**    * @see #FSTCompletion(FST, boolean, boolean)    */
DECL|field|exactFirst
specifier|private
name|boolean
name|exactFirst
decl_stmt|;
comment|/**    * @see #FSTCompletion(FST, boolean, boolean)    */
DECL|field|higherWeightsFirst
specifier|private
name|boolean
name|higherWeightsFirst
decl_stmt|;
comment|/**    * Constructs an FSTCompletion, specifying higherWeightsFirst and exactFirst.    * @param automaton    *          Automaton with completions. See {@link FSTCompletionBuilder}.    * @param higherWeightsFirst    *          Return most popular suggestions first. This is the default    *          behavior for this implementation. Setting it to<code>false</code>    *          has no effect (use constant term weights to sort alphabetically    *          only).    * @param exactFirst    *          Find and push an exact match to the first position of the result    *          list if found.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|FSTCompletion
specifier|public
name|FSTCompletion
parameter_list|(
name|FST
argument_list|<
name|Object
argument_list|>
name|automaton
parameter_list|,
name|boolean
name|higherWeightsFirst
parameter_list|,
name|boolean
name|exactFirst
parameter_list|)
block|{
name|this
operator|.
name|automaton
operator|=
name|automaton
expr_stmt|;
if|if
condition|(
name|automaton
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|rootArcs
operator|=
name|cacheRootArcs
argument_list|(
name|automaton
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|rootArcs
operator|=
operator|new
name|Arc
index|[
literal|0
index|]
expr_stmt|;
block|}
name|this
operator|.
name|higherWeightsFirst
operator|=
name|higherWeightsFirst
expr_stmt|;
name|this
operator|.
name|exactFirst
operator|=
name|exactFirst
expr_stmt|;
block|}
comment|/**    * Defaults to higher weights first and exact first.    * @see #FSTCompletion(FST, boolean, boolean)    */
DECL|method|FSTCompletion
specifier|public
name|FSTCompletion
parameter_list|(
name|FST
argument_list|<
name|Object
argument_list|>
name|automaton
parameter_list|)
block|{
name|this
argument_list|(
name|automaton
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cache the root node's output arcs starting with completions with the    * highest weights.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|cacheRootArcs
specifier|private
specifier|static
name|Arc
argument_list|<
name|Object
argument_list|>
index|[]
name|cacheRootArcs
parameter_list|(
name|FST
argument_list|<
name|Object
argument_list|>
name|automaton
parameter_list|)
block|{
try|try
block|{
name|List
argument_list|<
name|Arc
argument_list|<
name|Object
argument_list|>
argument_list|>
name|rootArcs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
init|=
name|automaton
operator|.
name|getFirstArc
argument_list|(
operator|new
name|Arc
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|automaton
operator|.
name|getBytesReader
argument_list|()
decl_stmt|;
name|automaton
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|arc
argument_list|,
name|fstReader
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|rootArcs
operator|.
name|add
argument_list|(
operator|new
name|Arc
argument_list|<>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|arc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
break|break;
name|automaton
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|,
name|fstReader
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|reverse
argument_list|(
name|rootArcs
argument_list|)
expr_stmt|;
comment|// we want highest weights first.
return|return
name|rootArcs
operator|.
name|toArray
argument_list|(
operator|new
name|Arc
index|[
name|rootArcs
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the first exact match by traversing root arcs, starting from the    * arc<code>rootArcIndex</code>.    *     * @param rootArcIndex    *          The first root arc index in {@link #rootArcs} to consider when    *          matching.    *     * @param utf8    *          The sequence of utf8 bytes to follow.    *     * @return Returns the bucket number of the match or<code>-1</code> if no    *         match was found.    */
DECL|method|getExactMatchStartingFromRootArc
specifier|private
name|int
name|getExactMatchStartingFromRootArc
parameter_list|(
name|int
name|rootArcIndex
parameter_list|,
name|BytesRef
name|utf8
parameter_list|)
block|{
comment|// Get the UTF-8 bytes representation of the input key.
try|try
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|scratch
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<>
argument_list|()
decl_stmt|;
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|automaton
operator|.
name|getBytesReader
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|rootArcIndex
operator|<
name|rootArcs
operator|.
name|length
condition|;
name|rootArcIndex
operator|++
control|)
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|rootArc
init|=
name|rootArcs
index|[
name|rootArcIndex
index|]
decl_stmt|;
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
init|=
name|scratch
operator|.
name|copyFrom
argument_list|(
name|rootArc
argument_list|)
decl_stmt|;
comment|// Descend into the automaton using the key as prefix.
if|if
condition|(
name|descendWithPrefix
argument_list|(
name|arc
argument_list|,
name|utf8
argument_list|)
condition|)
block|{
name|automaton
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|arc
argument_list|,
name|fstReader
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
comment|// Normalize prefix-encoded weight.
return|return
name|rootArc
operator|.
name|label
return|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Should never happen, but anyway.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// No match.
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Lookup suggestions to<code>key</code>.    *     * @param key    *          The prefix to which suggestions should be sought.    * @param num    *          At most this number of suggestions will be returned.    * @return Returns the suggestions, sorted by their approximated weight first    *         (decreasing) and then alphabetically (UTF-8 codepoint order).    */
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|Completion
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|automaton
operator|==
literal|null
condition|)
block|{
return|return
name|EMPTY_RESULT
return|;
block|}
try|try
block|{
name|BytesRef
name|keyUtf8
init|=
operator|new
name|BytesRef
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|higherWeightsFirst
operator|&&
name|rootArcs
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// We could emit a warning here (?). An optimal strategy for
comment|// alphabetically sorted
comment|// suggestions would be to add them with a constant weight -- this saves
comment|// unnecessary
comment|// traversals and sorting.
return|return
name|lookupSortedAlphabetically
argument_list|(
name|keyUtf8
argument_list|,
name|num
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|lookupSortedByWeight
argument_list|(
name|keyUtf8
argument_list|,
name|num
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Should never happen, but anyway.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Lookup suggestions sorted alphabetically<b>if weights are not    * constant</b>. This is a workaround: in general, use constant weights for    * alphabetically sorted result.    */
DECL|method|lookupSortedAlphabetically
specifier|private
name|List
argument_list|<
name|Completion
argument_list|>
name|lookupSortedAlphabetically
parameter_list|(
name|BytesRef
name|key
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Greedily get num results from each weight branch.
name|List
argument_list|<
name|Completion
argument_list|>
name|res
init|=
name|lookupSortedByWeight
argument_list|(
name|key
argument_list|,
name|num
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Sort and trim.
name|Collections
operator|.
name|sort
argument_list|(
name|res
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|size
argument_list|()
operator|>
name|num
condition|)
block|{
name|res
operator|=
name|res
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Lookup suggestions sorted by weight (descending order).    *     * @param collectAll    *          If<code>true</code>, the routine terminates immediately when    *<code>num</code> suggestions have been collected. If    *<code>false</code>, it will collect suggestions from all weight    *          arcs (needed for {@link #lookupSortedAlphabetically}.    */
DECL|method|lookupSortedByWeight
specifier|private
name|ArrayList
argument_list|<
name|Completion
argument_list|>
name|lookupSortedByWeight
parameter_list|(
name|BytesRef
name|key
parameter_list|,
name|int
name|num
parameter_list|,
name|boolean
name|collectAll
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Don't overallocate the results buffers. This also serves the purpose of
comment|// allowing the user of this class to request all matches using Integer.MAX_VALUE as
comment|// the number of results.
specifier|final
name|ArrayList
argument_list|<
name|Completion
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|10
argument_list|,
name|num
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|output
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|key
argument_list|)
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
name|rootArcs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|rootArc
init|=
name|rootArcs
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|rootArc
argument_list|)
decl_stmt|;
comment|// Descend into the automaton using the key as prefix.
if|if
condition|(
name|descendWithPrefix
argument_list|(
name|arc
argument_list|,
name|key
argument_list|)
condition|)
block|{
comment|// A subgraph starting from the current node has the completions
comment|// of the key prefix. The arc we're at is the last key's byte,
comment|// so we will collect it too.
name|output
operator|.
name|length
operator|=
name|key
operator|.
name|length
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|collect
argument_list|(
name|res
argument_list|,
name|num
argument_list|,
name|rootArc
operator|.
name|label
argument_list|,
name|output
argument_list|,
name|arc
argument_list|)
operator|&&
operator|!
name|collectAll
condition|)
block|{
comment|// We have enough suggestions to return immediately. Keep on looking
comment|// for an
comment|// exact match, if requested.
if|if
condition|(
name|exactFirst
condition|)
block|{
if|if
condition|(
operator|!
name|checkExistingAndReorder
argument_list|(
name|res
argument_list|,
name|key
argument_list|)
condition|)
block|{
name|int
name|exactMatchBucket
init|=
name|getExactMatchStartingFromRootArc
argument_list|(
name|i
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|exactMatchBucket
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Insert as the first result and truncate at num.
while|while
condition|(
name|res
operator|.
name|size
argument_list|()
operator|>=
name|num
condition|)
block|{
name|res
operator|.
name|remove
argument_list|(
name|res
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|res
operator|.
name|add
argument_list|(
literal|0
argument_list|,
operator|new
name|Completion
argument_list|(
name|key
argument_list|,
name|exactMatchBucket
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
break|break;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
comment|/**    * Checks if the list of    * {@link org.apache.lucene.search.suggest.Lookup.LookupResult}s already has a    *<code>key</code>. If so, reorders that    * {@link org.apache.lucene.search.suggest.Lookup.LookupResult} to the first    * position.    *     * @return Returns<code>true<code> if and only if<code>list</code> contained    *<code>key</code>.    */
DECL|method|checkExistingAndReorder
specifier|private
name|boolean
name|checkExistingAndReorder
parameter_list|(
name|ArrayList
argument_list|<
name|Completion
argument_list|>
name|list
parameter_list|,
name|BytesRef
name|key
parameter_list|)
block|{
comment|// We assume list does not have duplicates (because of how the FST is created).
for|for
control|(
name|int
name|i
init|=
name|list
operator|.
name|size
argument_list|()
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|utf8
argument_list|)
condition|)
block|{
comment|// Key found. Unless already at i==0, remove it and push up front so
comment|// that the ordering
comment|// remains identical with the exception of the exact match.
name|list
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|list
operator|.
name|remove
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Descend along the path starting at<code>arc</code> and going through bytes    * in the argument.    *     * @param arc    *          The starting arc. This argument is modified in-place.    * @param utf8    *          The term to descend along.    * @return If<code>true</code>,<code>arc</code> will be set to the arc    *         matching last byte of<code>term</code>.<code>false</code> is    *         returned if no such prefix exists.    */
DECL|method|descendWithPrefix
specifier|private
name|boolean
name|descendWithPrefix
parameter_list|(
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
parameter_list|,
name|BytesRef
name|utf8
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|max
init|=
name|utf8
operator|.
name|offset
operator|+
name|utf8
operator|.
name|length
decl_stmt|;
comment|// Cannot save as instance var since multiple threads
comment|// can use FSTCompletion at once...
specifier|final
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|automaton
operator|.
name|getBytesReader
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|utf8
operator|.
name|offset
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|automaton
operator|.
name|findTargetArc
argument_list|(
name|utf8
operator|.
name|bytes
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|,
name|arc
argument_list|,
name|arc
argument_list|,
name|fstReader
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// No matching prefixes, return an empty result.
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Recursive collect lookup results from the automaton subgraph starting at    *<code>arc</code>.    *     * @param num    *          Maximum number of results needed (early termination).    */
DECL|method|collect
specifier|private
name|boolean
name|collect
parameter_list|(
name|List
argument_list|<
name|Completion
argument_list|>
name|res
parameter_list|,
name|int
name|num
parameter_list|,
name|int
name|bucket
parameter_list|,
name|BytesRef
name|output
parameter_list|,
name|Arc
argument_list|<
name|Object
argument_list|>
name|arc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|output
operator|.
name|length
operator|==
name|output
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
name|output
operator|.
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|output
operator|.
name|bytes
argument_list|)
expr_stmt|;
block|}
assert|assert
name|output
operator|.
name|offset
operator|==
literal|0
assert|;
name|output
operator|.
name|bytes
index|[
name|output
operator|.
name|length
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|arc
operator|.
name|label
expr_stmt|;
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|automaton
operator|.
name|getBytesReader
argument_list|()
decl_stmt|;
name|automaton
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|arc
argument_list|,
name|fstReader
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
operator|new
name|Completion
argument_list|(
name|output
argument_list|,
name|bucket
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|size
argument_list|()
operator|>=
name|num
condition|)
return|return
literal|true
return|;
block|}
else|else
block|{
name|int
name|save
init|=
name|output
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|collect
argument_list|(
name|res
argument_list|,
name|num
argument_list|,
name|bucket
argument_list|,
name|output
argument_list|,
operator|new
name|Arc
argument_list|<>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|arc
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|output
operator|.
name|length
operator|=
name|save
expr_stmt|;
block|}
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
break|break;
block|}
name|automaton
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|,
name|fstReader
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Returns the bucket count (discretization thresholds).    */
DECL|method|getBucketCount
specifier|public
name|int
name|getBucketCount
parameter_list|()
block|{
return|return
name|rootArcs
operator|.
name|length
return|;
block|}
comment|/**    * Returns the bucket assigned to a given key (if found) or<code>-1</code> if    * no exact match exists.    */
DECL|method|getBucket
specifier|public
name|int
name|getBucket
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
return|return
name|getExactMatchStartingFromRootArc
argument_list|(
literal|0
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the internal automaton.    */
DECL|method|getFST
specifier|public
name|FST
argument_list|<
name|Object
argument_list|>
name|getFST
parameter_list|()
block|{
return|return
name|automaton
return|;
block|}
block|}
end_class
end_unit
