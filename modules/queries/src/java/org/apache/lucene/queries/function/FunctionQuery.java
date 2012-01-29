begin_unit
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|AtomicReader
operator|.
name|AtomicReaderContext
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
name|Bits
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
name|java
operator|.
name|util
operator|.
name|Set
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
begin_comment
comment|/**  * Returns a score for each document based on a ValueSource,  * often some function of the value of a field.  *  *<b>Note: This API is experimental and may change in non backward-compatible ways in the future</b>  *  *  */
end_comment
begin_class
DECL|class|FunctionQuery
specifier|public
class|class
name|FunctionQuery
extends|extends
name|Query
block|{
DECL|field|func
name|ValueSource
name|func
decl_stmt|;
comment|/**    * @param func defines the function to be used for scoring    */
DECL|method|FunctionQuery
specifier|public
name|FunctionQuery
parameter_list|(
name|ValueSource
name|func
parameter_list|)
block|{
name|this
operator|.
name|func
operator|=
name|func
expr_stmt|;
block|}
comment|/** @return The associated ValueSource */
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|()
block|{
return|return
name|func
return|;
block|}
annotation|@
name|Override
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
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
name|terms
parameter_list|)
block|{}
DECL|class|FunctionWeight
specifier|protected
class|class
name|FunctionWeight
extends|extends
name|Weight
block|{
DECL|field|searcher
specifier|protected
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|queryNorm
specifier|protected
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
specifier|protected
name|float
name|queryWeight
decl_stmt|;
DECL|field|context
specifier|protected
name|Map
name|context
decl_stmt|;
DECL|method|FunctionWeight
specifier|public
name|FunctionWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|func
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|func
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|FunctionQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|queryWeight
operator|=
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|norm
operator|*
name|topLevelBoost
expr_stmt|;
name|queryWeight
operator|*=
name|this
operator|.
name|queryNorm
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AllScorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|this
argument_list|,
name|queryWeight
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|AllScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
operator|)
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
DECL|class|AllScorer
specifier|protected
class|class
name|AllScorer
extends|extends
name|Scorer
block|{
DECL|field|reader
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|weight
specifier|final
name|FunctionWeight
name|weight
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|qWeight
specifier|final
name|float
name|qWeight
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|vals
specifier|final
name|FunctionValues
name|vals
decl_stmt|;
DECL|field|liveDocs
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|method|AllScorer
specifier|public
name|AllScorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|FunctionWeight
name|w
parameter_list|,
name|float
name|qWeight
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|w
expr_stmt|;
name|this
operator|.
name|qWeight
operator|=
name|qWeight
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|context
operator|.
name|reader
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|acceptDocs
expr_stmt|;
name|vals
operator|=
name|func
operator|.
name|getValues
argument_list|(
name|weight
operator|.
name|context
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|// instead of matching all docs, we could also embed a query.
comment|// the score could either ignore the subscore, or boost it.
comment|// Containment:  floatline(foo:myTerm, "myFloatField", 1.0, 0.0f)
comment|// Boost:        foo:myTerm^floatline("myFloatField",1.0,0.0f)
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
operator|++
name|doc
expr_stmt|;
if|if
condition|(
name|doc
operator|>=
name|maxDoc
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
continue|continue;
return|return
name|doc
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// this will work even if target==NO_MORE_DOCS
name|doc
operator|=
name|target
operator|-
literal|1
expr_stmt|;
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|score
init|=
name|qWeight
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// Current Lucene priority queues can't handle NaN and -Infinity, so
comment|// map to -Float.MAX_VALUE. This conditional handles both -infinity
comment|// and NaN since comparisons with NaN are always false.
return|return
name|score
operator|>
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|?
name|score
else|:
operator|-
name|Float
operator|.
name|MAX_VALUE
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|sc
init|=
name|qWeight
operator|*
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|Explanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|sc
argument_list|,
literal|"FunctionQuery("
operator|+
name|func
operator|+
literal|"), product of:"
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|vals
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|weight
operator|.
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FunctionQuery
operator|.
name|FunctionWeight
argument_list|(
name|searcher
argument_list|)
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
name|float
name|boost
init|=
name|getBoost
argument_list|()
decl_stmt|;
return|return
operator|(
name|boost
operator|!=
literal|1.0
condition|?
literal|"("
else|:
literal|""
operator|)
operator|+
name|func
operator|.
name|toString
argument_list|()
operator|+
operator|(
name|boost
operator|==
literal|1.0
condition|?
literal|""
else|:
literal|")^"
operator|+
name|boost
operator|)
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
annotation|@
name|Override
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
operator|!
name|FunctionQuery
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|FunctionQuery
name|other
init|=
operator|(
name|FunctionQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|&&
name|this
operator|.
name|func
operator|.
name|equals
argument_list|(
name|other
operator|.
name|func
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|func
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
