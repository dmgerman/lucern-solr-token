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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
name|TermContext
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
name|util
operator|.
name|ArrayUtil
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
begin_comment
comment|/**  * Base rewrite method for collecting only the top terms  * via a priority queue.  * @lucene.internal Only public to be accessible by spans package.  */
end_comment
begin_class
DECL|class|TopTermsRewrite
specifier|public
specifier|abstract
class|class
name|TopTermsRewrite
parameter_list|<
name|Q
extends|extends
name|Query
parameter_list|>
extends|extends
name|TermCollectingRewrite
argument_list|<
name|Q
argument_list|>
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
comment|/**     * Create a TopTermsBooleanQueryRewrite for     * at most<code>size</code> terms.    *<p>    * NOTE: if {@link BooleanQuery#getMaxClauseCount} is smaller than     *<code>size</code>, then it will be used instead.     */
DECL|method|TopTermsRewrite
specifier|public
name|TopTermsRewrite
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/** return the maximum priority queue size */
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/** return the maximum size of the priority queue (for boolean rewrites this is BooleanQuery#getMaxClauseCount). */
DECL|method|getMaxSize
specifier|protected
specifier|abstract
name|int
name|getMaxSize
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|rewrite
specifier|public
specifier|final
name|Q
name|rewrite
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|,
name|getMaxSize
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|PriorityQueue
argument_list|<
name|ScoreTerm
argument_list|>
name|stQueue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|()
decl_stmt|;
name|collectTerms
argument_list|(
name|reader
argument_list|,
name|query
argument_list|,
operator|new
name|TermCollector
argument_list|()
block|{
specifier|private
specifier|final
name|MaxNonCompetitiveBoostAttribute
name|maxBoostAtt
init|=
name|attributes
operator|.
name|addAttribute
argument_list|(
name|MaxNonCompetitiveBoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|ScoreTerm
argument_list|>
name|visitedTerms
init|=
operator|new
name|HashMap
argument_list|<
name|BytesRef
argument_list|,
name|ScoreTerm
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|TermsEnum
name|termsEnum
decl_stmt|;
specifier|private
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
specifier|private
name|BoostAttribute
name|boostAtt
decl_stmt|;
specifier|private
name|ScoreTerm
name|st
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setNextEnum
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|)
block|{
name|this
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
name|this
operator|.
name|termComp
operator|=
name|termsEnum
operator|.
name|getComparator
argument_list|()
expr_stmt|;
assert|assert
name|compareToLastTerm
argument_list|(
literal|null
argument_list|)
assert|;
comment|// lazy init the initial ScoreTerm because comparator is not known on ctor:
if|if
condition|(
name|st
operator|==
literal|null
condition|)
name|st
operator|=
operator|new
name|ScoreTerm
argument_list|(
name|this
operator|.
name|termComp
argument_list|,
operator|new
name|TermContext
argument_list|(
name|topReaderContext
argument_list|)
argument_list|)
expr_stmt|;
name|boostAtt
operator|=
name|termsEnum
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
comment|// for assert:
specifier|private
name|BytesRef
name|lastTerm
decl_stmt|;
specifier|private
name|boolean
name|compareToLastTerm
parameter_list|(
name|BytesRef
name|t
parameter_list|)
block|{
if|if
condition|(
name|lastTerm
operator|==
literal|null
operator|&&
name|t
operator|!=
literal|null
condition|)
block|{
name|lastTerm
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|lastTerm
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|termsEnum
operator|.
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|lastTerm
argument_list|,
name|t
argument_list|)
operator|<
literal|0
operator|:
literal|"lastTerm="
operator|+
name|lastTerm
operator|+
literal|" t="
operator|+
name|t
assert|;
name|lastTerm
operator|.
name|copyBytes
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|collect
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|float
name|boost
init|=
name|boostAtt
operator|.
name|getBoost
argument_list|()
decl_stmt|;
comment|// make sure within a single seg we always collect
comment|// terms in order
assert|assert
name|compareToLastTerm
argument_list|(
name|bytes
argument_list|)
assert|;
comment|//System.out.println("TTR.collect term=" + bytes.utf8ToString() + " boost=" + boost + " ord=" + readerContext.ord);
comment|// ignore uncompetitive hits
if|if
condition|(
name|stQueue
operator|.
name|size
argument_list|()
operator|==
name|maxSize
condition|)
block|{
specifier|final
name|ScoreTerm
name|t
init|=
name|stQueue
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|<
name|t
operator|.
name|boost
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|boost
operator|==
name|t
operator|.
name|boost
operator|&&
name|termComp
operator|.
name|compare
argument_list|(
name|bytes
argument_list|,
name|t
operator|.
name|bytes
argument_list|)
operator|>
literal|0
condition|)
return|return
literal|true
return|;
block|}
name|ScoreTerm
name|t
init|=
name|visitedTerms
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
specifier|final
name|TermState
name|state
init|=
name|termsEnum
operator|.
name|termState
argument_list|()
decl_stmt|;
assert|assert
name|state
operator|!=
literal|null
assert|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
comment|// if the term is already in the PQ, only update docFreq of term in PQ
assert|assert
name|t
operator|.
name|boost
operator|==
name|boost
operator|:
literal|"boost should be equal in all segment TermsEnums"
assert|;
name|t
operator|.
name|termState
operator|.
name|register
argument_list|(
name|state
argument_list|,
name|readerContext
operator|.
name|ord
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// add new entry in PQ, we must clone the term, else it may get overwritten!
name|st
operator|.
name|bytes
operator|.
name|copyBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|st
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|visitedTerms
operator|.
name|put
argument_list|(
name|st
operator|.
name|bytes
argument_list|,
name|st
argument_list|)
expr_stmt|;
assert|assert
name|st
operator|.
name|termState
operator|.
name|docFreq
argument_list|()
operator|==
literal|0
assert|;
name|st
operator|.
name|termState
operator|.
name|register
argument_list|(
name|state
argument_list|,
name|readerContext
operator|.
name|ord
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
name|stQueue
operator|.
name|offer
argument_list|(
name|st
argument_list|)
expr_stmt|;
comment|// possibly drop entries from queue
if|if
condition|(
name|stQueue
operator|.
name|size
argument_list|()
operator|>
name|maxSize
condition|)
block|{
name|st
operator|=
name|stQueue
operator|.
name|poll
argument_list|()
expr_stmt|;
name|visitedTerms
operator|.
name|remove
argument_list|(
name|st
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|st
operator|.
name|termState
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// reset the termstate!
block|}
else|else
block|{
name|st
operator|=
operator|new
name|ScoreTerm
argument_list|(
name|termComp
argument_list|,
operator|new
name|TermContext
argument_list|(
name|topReaderContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
name|stQueue
operator|.
name|size
argument_list|()
operator|<=
name|maxSize
operator|:
literal|"the PQ size must be limited to maxSize"
assert|;
comment|// set maxBoostAtt with values to help FuzzyTermsEnum to optimize
if|if
condition|(
name|stQueue
operator|.
name|size
argument_list|()
operator|==
name|maxSize
condition|)
block|{
name|t
operator|=
name|stQueue
operator|.
name|peek
argument_list|()
expr_stmt|;
name|maxBoostAtt
operator|.
name|setMaxNonCompetitiveBoost
argument_list|(
name|t
operator|.
name|boost
argument_list|)
expr_stmt|;
name|maxBoostAtt
operator|.
name|setCompetitiveTerm
argument_list|(
name|t
operator|.
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Q
name|q
init|=
name|getTopLevelQuery
argument_list|()
decl_stmt|;
specifier|final
name|ScoreTerm
index|[]
name|scoreTerms
init|=
name|stQueue
operator|.
name|toArray
argument_list|(
operator|new
name|ScoreTerm
index|[
name|stQueue
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|scoreTerms
argument_list|,
name|scoreTermSortByTermComp
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ScoreTerm
name|st
range|:
name|scoreTerms
control|)
block|{
specifier|final
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|query
operator|.
name|field
argument_list|,
name|st
operator|.
name|bytes
argument_list|)
decl_stmt|;
assert|assert
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
operator|==
name|st
operator|.
name|termState
operator|.
name|docFreq
argument_list|()
operator|:
literal|"reader DF is "
operator|+
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
operator|+
literal|" vs "
operator|+
name|st
operator|.
name|termState
operator|.
name|docFreq
argument_list|()
operator|+
literal|" term="
operator|+
name|term
assert|;
name|addClause
argument_list|(
name|q
argument_list|,
name|term
argument_list|,
name|st
operator|.
name|termState
operator|.
name|docFreq
argument_list|()
argument_list|,
name|query
operator|.
name|getBoost
argument_list|()
operator|*
name|st
operator|.
name|boost
argument_list|,
name|st
operator|.
name|termState
argument_list|)
expr_stmt|;
comment|// add to query
block|}
return|return
name|q
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
literal|31
operator|*
name|size
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
name|obj
operator|==
literal|null
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
specifier|final
name|TopTermsRewrite
argument_list|<
name|?
argument_list|>
name|other
init|=
operator|(
name|TopTermsRewrite
argument_list|<
name|?
argument_list|>
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|size
operator|!=
name|other
operator|.
name|size
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|field|scoreTermSortByTermComp
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|ScoreTerm
argument_list|>
name|scoreTermSortByTermComp
init|=
operator|new
name|Comparator
argument_list|<
name|ScoreTerm
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ScoreTerm
name|st1
parameter_list|,
name|ScoreTerm
name|st2
parameter_list|)
block|{
assert|assert
name|st1
operator|.
name|termComp
operator|==
name|st2
operator|.
name|termComp
operator|:
literal|"term comparator should not change between segments"
assert|;
return|return
name|st1
operator|.
name|termComp
operator|.
name|compare
argument_list|(
name|st1
operator|.
name|bytes
argument_list|,
name|st2
operator|.
name|bytes
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|class|ScoreTerm
specifier|static
specifier|final
class|class
name|ScoreTerm
implements|implements
name|Comparable
argument_list|<
name|ScoreTerm
argument_list|>
block|{
DECL|field|termComp
specifier|public
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
DECL|field|bytes
specifier|public
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|boost
specifier|public
name|float
name|boost
decl_stmt|;
DECL|field|termState
specifier|public
specifier|final
name|TermContext
name|termState
decl_stmt|;
DECL|method|ScoreTerm
specifier|public
name|ScoreTerm
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|,
name|TermContext
name|termState
parameter_list|)
block|{
name|this
operator|.
name|termComp
operator|=
name|termComp
expr_stmt|;
name|this
operator|.
name|termState
operator|=
name|termState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|ScoreTerm
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|boost
operator|==
name|other
operator|.
name|boost
condition|)
return|return
name|termComp
operator|.
name|compare
argument_list|(
name|other
operator|.
name|bytes
argument_list|,
name|this
operator|.
name|bytes
argument_list|)
return|;
else|else
return|return
name|Float
operator|.
name|compare
argument_list|(
name|this
operator|.
name|boost
argument_list|,
name|other
operator|.
name|boost
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
