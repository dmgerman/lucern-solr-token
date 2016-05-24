begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|DocValues
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
name|MultiDocValues
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
name|SortedDocValues
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
name|ConstantScoreWeight
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
name|Explanation
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
name|Scorer
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
name|LongBitSet
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
name|LongValues
import|;
end_import
begin_class
DECL|class|GlobalOrdinalsQuery
specifier|final
class|class
name|GlobalOrdinalsQuery
extends|extends
name|Query
block|{
comment|// All the ords of matching docs found with OrdinalsCollector.
DECL|field|foundOrds
specifier|private
specifier|final
name|LongBitSet
name|foundOrds
decl_stmt|;
DECL|field|joinField
specifier|private
specifier|final
name|String
name|joinField
decl_stmt|;
DECL|field|globalOrds
specifier|private
specifier|final
name|MultiDocValues
operator|.
name|OrdinalMap
name|globalOrds
decl_stmt|;
comment|// Is also an approximation of the docs that will match. Can be all docs that have toField or something more specific.
DECL|field|toQuery
specifier|private
specifier|final
name|Query
name|toQuery
decl_stmt|;
comment|// just for hashcode and equals:
DECL|field|fromQuery
specifier|private
specifier|final
name|Query
name|fromQuery
decl_stmt|;
DECL|field|indexReader
specifier|private
specifier|final
name|IndexReader
name|indexReader
decl_stmt|;
DECL|method|GlobalOrdinalsQuery
name|GlobalOrdinalsQuery
parameter_list|(
name|LongBitSet
name|foundOrds
parameter_list|,
name|String
name|joinField
parameter_list|,
name|MultiDocValues
operator|.
name|OrdinalMap
name|globalOrds
parameter_list|,
name|Query
name|toQuery
parameter_list|,
name|Query
name|fromQuery
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|)
block|{
name|this
operator|.
name|foundOrds
operator|=
name|foundOrds
expr_stmt|;
name|this
operator|.
name|joinField
operator|=
name|joinField
expr_stmt|;
name|this
operator|.
name|globalOrds
operator|=
name|globalOrds
expr_stmt|;
name|this
operator|.
name|toQuery
operator|=
name|toQuery
expr_stmt|;
name|this
operator|.
name|fromQuery
operator|=
name|fromQuery
expr_stmt|;
name|this
operator|.
name|indexReader
operator|=
name|indexReader
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
return|return
operator|new
name|W
argument_list|(
name|this
argument_list|,
name|toQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
argument_list|)
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
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|GlobalOrdinalsQuery
name|other
parameter_list|)
block|{
return|return
name|fromQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fromQuery
argument_list|)
operator|&&
name|joinField
operator|.
name|equals
argument_list|(
name|other
operator|.
name|joinField
argument_list|)
operator|&&
name|toQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|toQuery
argument_list|)
operator|&&
name|indexReader
operator|.
name|equals
argument_list|(
name|other
operator|.
name|indexReader
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
name|int
name|result
init|=
name|classHash
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|joinField
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|toQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|fromQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|indexReader
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
return|return
literal|"GlobalOrdinalsQuery{"
operator|+
literal|"joinField="
operator|+
name|joinField
operator|+
literal|'}'
return|;
block|}
DECL|class|W
specifier|final
class|class
name|W
extends|extends
name|ConstantScoreWeight
block|{
DECL|field|approximationWeight
specifier|private
specifier|final
name|Weight
name|approximationWeight
decl_stmt|;
DECL|method|W
name|W
parameter_list|(
name|Query
name|query
parameter_list|,
name|Weight
name|approximationWeight
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|approximationWeight
operator|=
name|approximationWeight
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
block|{}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|values
init|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|joinField
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"Not a match"
argument_list|)
return|;
block|}
name|int
name|segmentOrd
init|=
name|values
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentOrd
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"Not a match"
argument_list|)
return|;
block|}
name|BytesRef
name|joinValue
init|=
name|values
operator|.
name|lookupOrd
argument_list|(
name|segmentOrd
argument_list|)
decl_stmt|;
name|int
name|ord
decl_stmt|;
if|if
condition|(
name|globalOrds
operator|!=
literal|null
condition|)
block|{
name|ord
operator|=
operator|(
name|int
operator|)
name|globalOrds
operator|.
name|getGlobalOrds
argument_list|(
name|context
operator|.
name|ord
argument_list|)
operator|.
name|get
argument_list|(
name|segmentOrd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|=
name|segmentOrd
expr_stmt|;
block|}
if|if
condition|(
name|foundOrds
operator|.
name|get
argument_list|(
name|ord
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"Not a match, join value "
operator|+
name|Term
operator|.
name|toString
argument_list|(
name|joinValue
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|()
argument_list|,
literal|"A match, join value "
operator|+
name|Term
operator|.
name|toString
argument_list|(
name|joinValue
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|values
init|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|joinField
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Scorer
name|approximationScorer
init|=
name|approximationWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|approximationScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|globalOrds
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|OrdinalMapScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|foundOrds
argument_list|,
name|values
argument_list|,
name|approximationScorer
operator|.
name|iterator
argument_list|()
argument_list|,
name|globalOrds
operator|.
name|getGlobalOrds
argument_list|(
name|context
operator|.
name|ord
argument_list|)
argument_list|)
return|;
block|}
block|{
return|return
operator|new
name|SegmentOrdinalScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|foundOrds
argument_list|,
name|values
argument_list|,
name|approximationScorer
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|OrdinalMapScorer
specifier|final
specifier|static
class|class
name|OrdinalMapScorer
extends|extends
name|BaseGlobalOrdinalScorer
block|{
DECL|field|foundOrds
specifier|final
name|LongBitSet
name|foundOrds
decl_stmt|;
DECL|field|segmentOrdToGlobalOrdLookup
specifier|final
name|LongValues
name|segmentOrdToGlobalOrdLookup
decl_stmt|;
DECL|method|OrdinalMapScorer
specifier|public
name|OrdinalMapScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|float
name|score
parameter_list|,
name|LongBitSet
name|foundOrds
parameter_list|,
name|SortedDocValues
name|values
parameter_list|,
name|DocIdSetIterator
name|approximationScorer
parameter_list|,
name|LongValues
name|segmentOrdToGlobalOrdLookup
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|values
argument_list|,
name|approximationScorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|foundOrds
operator|=
name|foundOrds
expr_stmt|;
name|this
operator|.
name|segmentOrdToGlobalOrdLookup
operator|=
name|segmentOrdToGlobalOrdLookup
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createTwoPhaseIterator
specifier|protected
name|TwoPhaseIterator
name|createTwoPhaseIterator
parameter_list|(
name|DocIdSetIterator
name|approximation
parameter_list|)
block|{
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
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
specifier|final
name|long
name|segmentOrd
init|=
name|values
operator|.
name|getOrd
argument_list|(
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentOrd
operator|!=
operator|-
literal|1
condition|)
block|{
specifier|final
name|long
name|globalOrd
init|=
name|segmentOrdToGlobalOrdLookup
operator|.
name|get
argument_list|(
name|segmentOrd
argument_list|)
decl_stmt|;
if|if
condition|(
name|foundOrds
operator|.
name|get
argument_list|(
name|globalOrd
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
literal|100
return|;
comment|// TODO: use cost of values.getOrd() and foundOrds.get()
block|}
block|}
return|;
block|}
block|}
DECL|class|SegmentOrdinalScorer
specifier|final
specifier|static
class|class
name|SegmentOrdinalScorer
extends|extends
name|BaseGlobalOrdinalScorer
block|{
DECL|field|foundOrds
specifier|final
name|LongBitSet
name|foundOrds
decl_stmt|;
DECL|method|SegmentOrdinalScorer
specifier|public
name|SegmentOrdinalScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|float
name|score
parameter_list|,
name|LongBitSet
name|foundOrds
parameter_list|,
name|SortedDocValues
name|values
parameter_list|,
name|DocIdSetIterator
name|approximationScorer
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|values
argument_list|,
name|approximationScorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|foundOrds
operator|=
name|foundOrds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createTwoPhaseIterator
specifier|protected
name|TwoPhaseIterator
name|createTwoPhaseIterator
parameter_list|(
name|DocIdSetIterator
name|approximation
parameter_list|)
block|{
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
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
specifier|final
name|long
name|segmentOrd
init|=
name|values
operator|.
name|getOrd
argument_list|(
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentOrd
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|foundOrds
operator|.
name|get
argument_list|(
name|segmentOrd
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
literal|100
return|;
comment|// TODO: use cost of values.getOrd() and foundOrds.get()
block|}
block|}
return|;
block|}
block|}
block|}
end_class
end_unit
