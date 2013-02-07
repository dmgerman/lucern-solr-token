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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|index
operator|.
name|FieldInvertState
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
name|PriorityQueue
import|;
end_import
begin_comment
comment|/**  * Holds all implementations of classes in the o.a.l.search package as a  * back-compatibility test. It does not run any tests per-se, however if   * someone adds a method to an interface or abstract method to an abstract  * class, one of the implementations here will fail to compile and so we know  * back-compat policy was violated.  */
end_comment
begin_class
DECL|class|JustCompileSearch
specifier|final
class|class
name|JustCompileSearch
block|{
DECL|field|UNSUPPORTED_MSG
specifier|private
specifier|static
specifier|final
name|String
name|UNSUPPORTED_MSG
init|=
literal|"unsupported: used for back-compat testing only !"
decl_stmt|;
DECL|class|JustCompileCollector
specifier|static
specifier|final
class|class
name|JustCompileCollector
extends|extends
name|Collector
block|{
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileDocIdSet
specifier|static
specifier|final
class|class
name|JustCompileDocIdSet
extends|extends
name|DocIdSet
block|{
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileDocIdSetIterator
specifier|static
specifier|final
class|class
name|JustCompileDocIdSetIterator
extends|extends
name|DocIdSetIterator
block|{
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileExtendedFieldCacheLongParser
specifier|static
specifier|final
class|class
name|JustCompileExtendedFieldCacheLongParser
implements|implements
name|FieldCache
operator|.
name|LongParser
block|{
annotation|@
name|Override
DECL|method|parseLong
specifier|public
name|long
name|parseLong
parameter_list|(
name|BytesRef
name|string
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|termsEnum
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileExtendedFieldCacheDoubleParser
specifier|static
specifier|final
class|class
name|JustCompileExtendedFieldCacheDoubleParser
implements|implements
name|FieldCache
operator|.
name|DoubleParser
block|{
annotation|@
name|Override
DECL|method|parseDouble
specifier|public
name|double
name|parseDouble
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|termsEnum
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFieldComparator
specifier|static
specifier|final
class|class
name|JustCompileFieldComparator
extends|extends
name|FieldComparator
argument_list|<
name|Object
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
argument_list|<
name|Object
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Object
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|compareDocToValue
specifier|public
name|int
name|compareDocToValue
parameter_list|(
name|int
name|doc
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFieldComparatorSource
specifier|static
specifier|final
class|class
name|JustCompileFieldComparatorSource
extends|extends
name|FieldComparatorSource
block|{
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFilter
specifier|static
specifier|final
class|class
name|JustCompileFilter
extends|extends
name|Filter
block|{
comment|// Filter is just an abstract class with no abstract methods. However it is
comment|// still added here in case someone will add abstract methods in the future.
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|JustCompileFilteredDocIdSet
specifier|static
specifier|final
class|class
name|JustCompileFilteredDocIdSet
extends|extends
name|FilteredDocIdSet
block|{
DECL|method|JustCompileFilteredDocIdSet
specifier|public
name|JustCompileFilteredDocIdSet
parameter_list|(
name|DocIdSet
name|innerSet
parameter_list|)
block|{
name|super
argument_list|(
name|innerSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|match
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|docid
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileFilteredDocIdSetIterator
specifier|static
specifier|final
class|class
name|JustCompileFilteredDocIdSetIterator
extends|extends
name|FilteredDocIdSetIterator
block|{
DECL|method|JustCompileFilteredDocIdSetIterator
specifier|public
name|JustCompileFilteredDocIdSetIterator
parameter_list|(
name|DocIdSetIterator
name|innerIter
parameter_list|)
block|{
name|super
argument_list|(
name|innerIter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|match
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileQuery
specifier|static
specifier|final
class|class
name|JustCompileQuery
extends|extends
name|Query
block|{
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileScorer
specifier|static
specifier|final
class|class
name|JustCompileScorer
extends|extends
name|Scorer
block|{
DECL|method|JustCompileScorer
specifier|protected
name|JustCompileScorer
parameter_list|(
name|Weight
name|weight
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileSimilarity
specifier|static
specifier|final
class|class
name|JustCompileSimilarity
extends|extends
name|Similarity
block|{
annotation|@
name|Override
DECL|method|computeWeight
specifier|public
name|SimWeight
name|computeWeight
parameter_list|(
name|float
name|queryBoost
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|exactSimScorer
specifier|public
name|ExactSimScorer
name|exactSimScorer
parameter_list|(
name|SimWeight
name|stats
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|sloppySimScorer
specifier|public
name|SloppySimScorer
name|sloppySimScorer
parameter_list|(
name|SimWeight
name|stats
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|long
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileTopDocsCollector
specifier|static
specifier|final
class|class
name|JustCompileTopDocsCollector
extends|extends
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
block|{
DECL|method|JustCompileTopDocsCollector
specifier|protected
name|JustCompileTopDocsCollector
parameter_list|(
name|PriorityQueue
argument_list|<
name|ScoreDoc
argument_list|>
name|pq
parameter_list|)
block|{
name|super
argument_list|(
name|pq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|JustCompileWeight
specifier|static
specifier|final
class|class
name|JustCompileWeight
extends|extends
name|Weight
block|{
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|UNSUPPORTED_MSG
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
