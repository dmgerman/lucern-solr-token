begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|LeafReaderContext
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
name|Query
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
name|spans
operator|.
name|FilterSpans
operator|.
name|AcceptStatus
import|;
end_import
begin_comment
comment|/**  * Base class for filtering a SpanQuery based on the position of a match.  **/
end_comment
begin_class
DECL|class|SpanPositionCheckQuery
specifier|public
specifier|abstract
class|class
name|SpanPositionCheckQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|match
specifier|protected
name|SpanQuery
name|match
decl_stmt|;
DECL|method|SpanPositionCheckQuery
specifier|public
name|SpanPositionCheckQuery
parameter_list|(
name|SpanQuery
name|match
parameter_list|)
block|{
name|this
operator|.
name|match
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the SpanQuery whose matches are filtered.    *    * */
DECL|method|getMatch
specifier|public
name|SpanQuery
name|getMatch
parameter_list|()
block|{
return|return
name|match
return|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|match
operator|.
name|getField
argument_list|()
return|;
block|}
comment|/**    * Implementing classes are required to return whether the current position is a match for the passed in    * "match" {@link SpanQuery}.    *    * This is only called if the underlying last {@link Spans#nextStartPosition()} for the    * match indicated a valid start position.    *    * @param spans The {@link Spans} instance, positioned at the spot to check    *    * @return whether the match is accepted, rejected, or rejected and should move to the next doc.    *    * @see Spans#nextDoc()    *    */
DECL|method|acceptPosition
specifier|protected
specifier|abstract
name|AcceptStatus
name|acceptPosition
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|SpanWeight
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
name|SpanWeight
name|matchWeight
init|=
name|match
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpanPositionCheckWeight
argument_list|(
name|matchWeight
argument_list|,
name|searcher
argument_list|,
name|needsScores
condition|?
name|getTermContexts
argument_list|(
name|matchWeight
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
DECL|class|SpanPositionCheckWeight
specifier|public
class|class
name|SpanPositionCheckWeight
extends|extends
name|SpanWeight
block|{
DECL|field|matchWeight
specifier|final
name|SpanWeight
name|matchWeight
decl_stmt|;
DECL|method|SpanPositionCheckWeight
specifier|public
name|SpanPositionCheckWeight
parameter_list|(
name|SpanWeight
name|matchWeight
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SpanPositionCheckQuery
operator|.
name|this
argument_list|,
name|searcher
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchWeight
operator|=
name|matchWeight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|matchWeight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTermContexts
specifier|public
name|void
name|extractTermContexts
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
parameter_list|)
block|{
name|matchWeight
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Postings
name|requiredPostings
parameter_list|)
throws|throws
name|IOException
block|{
name|Spans
name|matchSpans
init|=
name|matchWeight
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|requiredPostings
argument_list|)
decl_stmt|;
return|return
operator|(
name|matchSpans
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|FilterSpans
argument_list|(
name|matchSpans
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|Spans
name|candidate
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|acceptPosition
argument_list|(
name|candidate
argument_list|)
return|;
block|}
block|}
return|;
block|}
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
name|SpanQuery
name|rewritten
init|=
operator|(
name|SpanQuery
operator|)
name|match
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|match
condition|)
block|{
try|try
block|{
name|SpanPositionCheckQuery
name|clone
init|=
operator|(
name|SpanPositionCheckQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|match
operator|=
name|rewritten
expr_stmt|;
return|return
name|clone
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/** Returns true iff<code>other</code> is equal to this. */
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
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|match
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SpanPositionCheckQuery
operator|)
name|other
operator|)
operator|.
name|match
argument_list|)
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
name|classHash
argument_list|()
operator|^
name|match
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
