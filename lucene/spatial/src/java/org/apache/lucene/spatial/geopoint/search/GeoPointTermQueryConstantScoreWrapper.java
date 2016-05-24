begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.geopoint.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|search
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
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
name|PostingsEnum
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
name|SortedNumericDocValues
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
name|search
operator|.
name|ConstantScoreScorer
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
name|DocIdSet
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
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
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
name|BitSet
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
name|DocIdSetBuilder
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
name|FixedBitSet
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
name|SparseFixedBitSet
import|;
end_import
begin_comment
comment|/**  * Custom ConstantScoreWrapper for {@code GeoPointMultiTermQuery} that cuts over to DocValues  * for post filtering boundary ranges. Multi-valued GeoPoint documents are supported.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointTermQueryConstantScoreWrapper
specifier|final
class|class
name|GeoPointTermQueryConstantScoreWrapper
parameter_list|<
name|Q
extends|extends
name|GeoPointMultiTermQuery
parameter_list|>
extends|extends
name|Query
block|{
DECL|field|query
specifier|protected
specifier|final
name|Q
name|query
decl_stmt|;
DECL|method|GeoPointTermQueryConstantScoreWrapper
specifier|protected
name|GeoPointTermQueryConstantScoreWrapper
parameter_list|(
name|Q
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|/**    * Returns the encapsulated query.    */
DECL|method|getQuery
specifier|public
name|Q
name|getQuery
parameter_list|()
block|{
return|return
name|query
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
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
specifier|final
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
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|GeoPointTermQueryConstantScoreWrapper
argument_list|<
name|?
argument_list|>
operator|)
name|other
operator|)
operator|.
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|classHash
argument_list|()
operator|+
name|query
operator|.
name|hashCode
argument_list|()
return|;
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
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
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
specifier|final
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|GeoPointTermsEnum
name|termsEnum
init|=
call|(
name|GeoPointTermsEnum
call|)
argument_list|(
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|// approximation (postfiltering has not yet been applied)
name|DocIdSetBuilder
name|builder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|terms
argument_list|)
decl_stmt|;
comment|// subset of documents that need no postfiltering, this is purely an optimization
specifier|final
name|BitSet
name|preApproved
decl_stmt|;
comment|// dumb heuristic: if the field is really sparse, use a sparse impl
if|if
condition|(
name|terms
operator|.
name|getDocCount
argument_list|()
operator|*
literal|100L
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|preApproved
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|preApproved
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PostingsEnum
name|docs
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|docs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|// boundary terms need post filtering
if|if
condition|(
name|termsEnum
operator|.
name|boundaryTerm
argument_list|()
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|numDocs
init|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
decl_stmt|;
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|adder
init|=
name|builder
operator|.
name|grow
argument_list|(
name|numDocs
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|int
name|docId
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|adder
operator|.
name|add
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|preApproved
operator|.
name|set
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|DocIdSet
name|set
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// return two-phase iterator using docvalues to postfilter candidates
name|SortedNumericDocValues
name|sdv
init|=
name|reader
operator|.
name|getSortedNumericDocValues
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
name|TwoPhaseIterator
name|iterator
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|disi
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
name|int
name|docId
init|=
name|disi
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|preApproved
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|sdv
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|sdv
operator|.
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|long
name|hash
init|=
name|sdv
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|postFilter
argument_list|(
name|GeoPointField
operator|.
name|decodeLatitude
argument_list|(
name|hash
argument_list|)
argument_list|,
name|GeoPointField
operator|.
name|decodeLongitude
argument_list|(
name|hash
argument_list|)
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
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|20
return|;
comment|// TODO: make this fancier
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|iterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
