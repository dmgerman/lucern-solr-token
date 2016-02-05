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
name|search
operator|.
name|DocIdSetIterator
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
name|ConjunctionDISI
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
name|TwoPhaseIterator
import|;
end_import
begin_comment
comment|/**  * Common super class for multiple sub spans required in a document.  */
end_comment
begin_class
DECL|class|ConjunctionSpans
specifier|abstract
class|class
name|ConjunctionSpans
extends|extends
name|Spans
block|{
DECL|field|subSpans
specifier|final
name|Spans
index|[]
name|subSpans
decl_stmt|;
comment|// in query order
DECL|field|conjunction
specifier|final
name|DocIdSetIterator
name|conjunction
decl_stmt|;
comment|// use to move to next doc with all clauses
DECL|field|atFirstInCurrentDoc
name|boolean
name|atFirstInCurrentDoc
decl_stmt|;
comment|// a first start position is available in current doc for nextStartPosition
DECL|field|oneExhaustedInCurrentDoc
name|boolean
name|oneExhaustedInCurrentDoc
decl_stmt|;
comment|// one subspans exhausted in current doc
DECL|method|ConjunctionSpans
name|ConjunctionSpans
parameter_list|(
name|List
argument_list|<
name|Spans
argument_list|>
name|subSpans
parameter_list|)
block|{
if|if
condition|(
name|subSpans
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Less than 2 subSpans.size():"
operator|+
name|subSpans
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|subSpans
operator|=
name|subSpans
operator|.
name|toArray
argument_list|(
operator|new
name|Spans
index|[
name|subSpans
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|conjunction
operator|=
name|ConjunctionDISI
operator|.
name|intersectSpans
argument_list|(
name|subSpans
argument_list|)
expr_stmt|;
name|this
operator|.
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
comment|// ensure for doc -1 that start/end positions are -1
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
name|conjunction
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|conjunction
operator|.
name|cost
argument_list|()
return|;
block|}
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
return|return
operator|(
name|conjunction
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
operator|)
condition|?
name|NO_MORE_DOCS
else|:
name|toMatchDoc
argument_list|()
return|;
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
return|return
operator|(
name|conjunction
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|==
name|NO_MORE_DOCS
operator|)
condition|?
name|NO_MORE_DOCS
else|:
name|toMatchDoc
argument_list|()
return|;
block|}
DECL|method|toMatchDoc
name|int
name|toMatchDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|oneExhaustedInCurrentDoc
operator|=
literal|false
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|twoPhaseCurrentDocMatches
argument_list|()
condition|)
block|{
return|return
name|docID
argument_list|()
return|;
block|}
if|if
condition|(
name|conjunction
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
block|}
block|}
DECL|method|twoPhaseCurrentDocMatches
specifier|abstract
name|boolean
name|twoPhaseCurrentDocMatches
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return a {@link TwoPhaseIterator} view of this ConjunctionSpans.    */
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
name|float
name|totalMatchCost
init|=
literal|0
decl_stmt|;
comment|// Compute the matchCost as the total matchCost/positionsCostant of the sub spans.
for|for
control|(
name|Spans
name|spans
range|:
name|subSpans
control|)
block|{
name|TwoPhaseIterator
name|tpi
init|=
name|spans
operator|.
name|asTwoPhaseIterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|tpi
operator|!=
literal|null
condition|)
block|{
name|totalMatchCost
operator|+=
name|tpi
operator|.
name|matchCost
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|totalMatchCost
operator|+=
name|spans
operator|.
name|positionsCost
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|float
name|matchCost
init|=
name|totalMatchCost
decl_stmt|;
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|conjunction
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|twoPhaseCurrentDocMatches
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|matchCost
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|positionsCost
specifier|public
name|float
name|positionsCost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// asTwoPhaseIterator never returns null here.
block|}
DECL|method|getSubSpans
specifier|public
name|Spans
index|[]
name|getSubSpans
parameter_list|()
block|{
return|return
name|subSpans
return|;
block|}
block|}
end_class
end_unit
