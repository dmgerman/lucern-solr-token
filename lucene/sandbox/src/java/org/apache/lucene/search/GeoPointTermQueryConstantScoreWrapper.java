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
name|GeoUtils
import|;
end_import
begin_comment
comment|/**  * Custom ConstantScoreWrapper for {@code GeoPointTermQuery} that cuts over to DocValues  * for post filtering boundary ranges. Multi-valued GeoPoint documents are supported.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointTermQueryConstantScoreWrapper
specifier|final
class|class
name|GeoPointTermQueryConstantScoreWrapper
parameter_list|<
name|Q
extends|extends
name|GeoPointTermQuery
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|GeoPointTermQueryConstantScoreWrapper
argument_list|<
name|?
argument_list|>
name|that
init|=
operator|(
name|GeoPointTermQueryConstantScoreWrapper
argument_list|<
name|?
argument_list|>
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|that
operator|.
name|query
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|that
operator|.
name|getBoost
argument_list|()
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
name|super
operator|.
name|hashCode
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
specifier|private
name|DocIdSet
name|getDocIDs
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
name|field
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
name|DocIdSet
operator|.
name|EMPTY
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
argument_list|)
decl_stmt|;
name|PostingsEnum
name|docs
init|=
literal|null
decl_stmt|;
name|SortedNumericDocValues
name|sdv
init|=
name|reader
operator|.
name|getSortedNumericDocValues
argument_list|(
name|query
operator|.
name|field
argument_list|)
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
comment|// boundary terms need post filtering by
if|if
condition|(
name|termsEnum
operator|.
name|boundaryTerm
argument_list|()
condition|)
block|{
name|int
name|docId
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
do|do
block|{
name|sdv
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
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
name|sdv
operator|.
name|count
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
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
specifier|final
name|double
name|lon
init|=
name|GeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|hash
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lat
init|=
name|GeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|hash
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|postFilter
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
operator|(
name|docId
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
do|;
block|}
else|else
block|{
name|builder
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|Scorer
name|scorer
parameter_list|(
name|DocIdSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
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
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|disi
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|scorer
init|=
name|scorer
argument_list|(
name|getDocIDs
argument_list|(
name|context
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|DefaultBulkScorer
argument_list|(
name|scorer
argument_list|)
return|;
block|}
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
return|return
name|scorer
argument_list|(
name|getDocIDs
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
