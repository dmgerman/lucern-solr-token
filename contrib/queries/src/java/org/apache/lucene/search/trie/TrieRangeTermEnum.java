begin_unit
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
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
name|util
operator|.
name|LinkedList
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
name|FilteredTermEnum
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
name|MultiTermQuery
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
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
comment|/**  * Subclass of FilteredTermEnum for enumerating all terms that match the  * sub-ranges for trie range queries.  *<p>  * WARNING: Term enumerations is not guaranteed to be always ordered by  * {@link Term#compareTo}.  * The ordering depends on how {@link TrieUtils#splitLongRange} and  * {@link TrieUtils#splitIntRange} generates the sub-ranges. For  * the {@link MultiTermQuery} ordering is not relevant.  */
end_comment
begin_class
DECL|class|TrieRangeTermEnum
specifier|final
class|class
name|TrieRangeTermEnum
extends|extends
name|FilteredTermEnum
block|{
DECL|field|query
specifier|private
specifier|final
name|AbstractTrieRangeQuery
name|query
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|rangeBounds
specifier|private
specifier|final
name|LinkedList
comment|/*<String>*/
name|rangeBounds
init|=
operator|new
name|LinkedList
comment|/*<String>*/
argument_list|()
decl_stmt|;
DECL|field|currentUpperBound
specifier|private
name|String
name|currentUpperBound
init|=
literal|null
decl_stmt|;
DECL|method|TrieRangeTermEnum
name|TrieRangeTermEnum
parameter_list|(
name|AbstractTrieRangeQuery
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
comment|/** Returns a range builder that must be used to feed in the sub-ranges. */
DECL|method|getIntRangeBuilder
name|TrieUtils
operator|.
name|IntRangeBuilder
name|getIntRangeBuilder
parameter_list|()
block|{
return|return
operator|new
name|TrieUtils
operator|.
name|IntRangeBuilder
argument_list|()
block|{
comment|//@Override
specifier|public
specifier|final
name|void
name|addRange
parameter_list|(
name|String
name|minPrefixCoded
parameter_list|,
name|String
name|maxPrefixCoded
parameter_list|)
block|{
name|rangeBounds
operator|.
name|add
argument_list|(
name|minPrefixCoded
argument_list|)
expr_stmt|;
name|rangeBounds
operator|.
name|add
argument_list|(
name|maxPrefixCoded
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** Returns a range builder that must be used to feed in the sub-ranges. */
DECL|method|getLongRangeBuilder
name|TrieUtils
operator|.
name|LongRangeBuilder
name|getLongRangeBuilder
parameter_list|()
block|{
return|return
operator|new
name|TrieUtils
operator|.
name|LongRangeBuilder
argument_list|()
block|{
comment|//@Override
specifier|public
specifier|final
name|void
name|addRange
parameter_list|(
name|String
name|minPrefixCoded
parameter_list|,
name|String
name|maxPrefixCoded
parameter_list|)
block|{
name|rangeBounds
operator|.
name|add
argument_list|(
name|minPrefixCoded
argument_list|)
expr_stmt|;
name|rangeBounds
operator|.
name|add
argument_list|(
name|maxPrefixCoded
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** After feeding the range builder call this method to initialize the enum. */
DECL|method|init
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|next
argument_list|()
expr_stmt|;
block|}
comment|//@Override
DECL|method|difference
specifier|public
name|float
name|difference
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
comment|/** this is a dummy, it is not used by this class. */
comment|//@Override
DECL|method|endEnum
specifier|protected
name|boolean
name|endEnum
parameter_list|()
block|{
assert|assert
literal|false
assert|;
comment|// should never be called
return|return
operator|(
name|currentTerm
operator|!=
literal|null
operator|)
return|;
block|}
comment|/**    * Compares if current upper bound is reached,    * this also updates the term count for statistics.    * In contrast to {@link FilteredTermEnum}, a return value    * of<code>false</code> ends iterating the current enum    * and forwards to the next sub-range.    */
comment|//@Override
DECL|method|termCompare
specifier|protected
name|boolean
name|termCompare
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
operator|(
name|term
operator|.
name|field
argument_list|()
operator|==
name|query
operator|.
name|field
operator|&&
name|term
operator|.
name|text
argument_list|()
operator|.
name|compareTo
argument_list|(
name|currentUpperBound
argument_list|)
operator|<=
literal|0
operator|)
return|;
block|}
comment|/** Increments the enumeration to the next element.  True if one exists. */
comment|//@Override
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|// if a current term exists, the actual enum is initialized:
comment|// try change to next term, if no such term exists, fall-through
if|if
condition|(
name|currentTerm
operator|!=
literal|null
condition|)
block|{
assert|assert
name|actualEnum
operator|!=
literal|null
assert|;
if|if
condition|(
name|actualEnum
operator|.
name|next
argument_list|()
condition|)
block|{
name|currentTerm
operator|=
name|actualEnum
operator|.
name|term
argument_list|()
expr_stmt|;
if|if
condition|(
name|termCompare
argument_list|(
name|currentTerm
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
block|}
comment|// if all above fails, we go forward to the next enum,
comment|// if one is available
name|currentTerm
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|rangeBounds
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
return|return
literal|false
return|;
comment|// close the current enum and read next bounds
if|if
condition|(
name|actualEnum
operator|!=
literal|null
condition|)
block|{
name|actualEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|actualEnum
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|String
name|lowerBound
init|=
operator|(
name|String
operator|)
name|rangeBounds
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|this
operator|.
name|currentUpperBound
operator|=
operator|(
name|String
operator|)
name|rangeBounds
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
comment|// this call recursively uses next(), if no valid term in
comment|// next enum found.
comment|// if this behavior is changed/modified in the superclass,
comment|// this enum will not work anymore!
name|setEnum
argument_list|(
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|query
operator|.
name|field
argument_list|,
name|lowerBound
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|currentTerm
operator|!=
literal|null
operator|)
return|;
block|}
comment|/** Closes the enumeration to further activity, freeing resources.  */
comment|//@Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|rangeBounds
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentUpperBound
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
