begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|DocsEnum
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
name|Collector
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
name|FixedBitSet
import|;
end_import
begin_class
DECL|class|DrillSidewaysScorer
class|class
name|DrillSidewaysScorer
extends|extends
name|Scorer
block|{
comment|//private static boolean DEBUG = false;
DECL|field|drillDownCollector
specifier|private
specifier|final
name|Collector
name|drillDownCollector
decl_stmt|;
DECL|field|dims
specifier|private
specifier|final
name|DocsEnumsAndFreq
index|[]
name|dims
decl_stmt|;
comment|// DrillDown DocsEnums:
DECL|field|baseScorer
specifier|private
specifier|final
name|Scorer
name|baseScorer
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|CHUNK
specifier|private
specifier|static
specifier|final
name|int
name|CHUNK
init|=
literal|2048
decl_stmt|;
DECL|field|MASK
specifier|private
specifier|static
specifier|final
name|int
name|MASK
init|=
name|CHUNK
operator|-
literal|1
decl_stmt|;
DECL|field|collectDocID
specifier|private
name|int
name|collectDocID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|collectScore
specifier|private
name|float
name|collectScore
decl_stmt|;
DECL|method|DrillSidewaysScorer
name|DrillSidewaysScorer
parameter_list|(
name|Weight
name|w
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|,
name|Scorer
name|baseScorer
parameter_list|,
name|Collector
name|drillDownCollector
parameter_list|,
name|DocsEnumsAndFreq
index|[]
name|dims
parameter_list|)
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|dims
operator|=
name|dims
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|baseScorer
operator|=
name|baseScorer
expr_stmt|;
name|this
operator|.
name|drillDownCollector
operator|=
name|drillDownCollector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("\nscore: reader=" + context.reader());
comment|//}
comment|//System.out.println("score r=" + context.reader());
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|drillDownCollector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|drillDownCollector
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
for|for
control|(
name|DocsEnumsAndFreq
name|dim
range|:
name|dims
control|)
block|{
name|dim
operator|.
name|sidewaysCollector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|dim
operator|.
name|sidewaysCollector
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|// TODO: if we ever allow null baseScorer ... it will
comment|// mean we DO score docs out of order ... hmm, or if we
comment|// change up the order of the conjuntions below
assert|assert
name|baseScorer
operator|!=
literal|null
assert|;
comment|// Position all scorers to their first matching doc:
name|int
name|baseDocID
init|=
name|baseScorer
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
for|for
control|(
name|DocsEnumsAndFreq
name|dim
range|:
name|dims
control|)
block|{
for|for
control|(
name|DocsEnum
name|docsEnum
range|:
name|dim
operator|.
name|docsEnums
control|)
block|{
if|if
condition|(
name|docsEnum
operator|!=
literal|null
condition|)
block|{
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|int
name|numDims
init|=
name|dims
operator|.
name|length
decl_stmt|;
name|DocsEnum
index|[]
index|[]
name|docsEnums
init|=
operator|new
name|DocsEnum
index|[
name|numDims
index|]
index|[]
decl_stmt|;
name|Collector
index|[]
name|sidewaysCollectors
init|=
operator|new
name|Collector
index|[
name|numDims
index|]
decl_stmt|;
name|int
name|maxFreq
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|docsEnums
index|[
name|dim
index|]
operator|=
name|dims
index|[
name|dim
index|]
operator|.
name|docsEnums
expr_stmt|;
name|sidewaysCollectors
index|[
name|dim
index|]
operator|=
name|dims
index|[
name|dim
index|]
operator|.
name|sidewaysCollector
expr_stmt|;
name|maxFreq
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxFreq
argument_list|,
name|dims
index|[
name|dim
index|]
operator|.
name|freq
argument_list|)
expr_stmt|;
block|}
comment|// TODO: if we add cost API to Scorer, switch to that!
name|int
name|estBaseHitCount
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
operator|/
operator|(
literal|1
operator|+
name|baseDocID
operator|)
decl_stmt|;
comment|/*     System.out.println("\nbaseDocID=" + baseDocID + " est=" + estBaseHitCount);     System.out.println("  maxDoc=" + context.reader().maxDoc());     System.out.println("  maxFreq=" + maxFreq);     System.out.println("  dims[0].freq=" + dims[0].freq);     if (numDims> 1) {       System.out.println("  dims[1].freq=" + dims[1].freq);     }     */
if|if
condition|(
name|estBaseHitCount
operator|<
name|maxFreq
operator|/
literal|10
condition|)
block|{
comment|//System.out.println("baseAdvance");
name|doBaseAdvanceScoring
argument_list|(
name|collector
argument_list|,
name|docsEnums
argument_list|,
name|sidewaysCollectors
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numDims
operator|>
literal|1
operator|&&
operator|(
name|dims
index|[
literal|1
index|]
operator|.
name|freq
operator|<
name|estBaseHitCount
operator|/
literal|10
operator|)
condition|)
block|{
comment|//System.out.println("drillDownAdvance");
name|doDrillDownAdvanceScoring
argument_list|(
name|collector
argument_list|,
name|docsEnums
argument_list|,
name|sidewaysCollectors
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("union");
name|doUnionScoring
argument_list|(
name|collector
argument_list|,
name|docsEnums
argument_list|,
name|sidewaysCollectors
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Used when drill downs are highly constraining vs    *  baseQuery. */
DECL|method|doDrillDownAdvanceScoring
specifier|private
name|void
name|doDrillDownAdvanceScoring
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|DocsEnum
index|[]
index|[]
name|docsEnums
parameter_list|,
name|Collector
index|[]
name|sidewaysCollectors
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDims
init|=
name|dims
operator|.
name|length
decl_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("  doDrillDownAdvanceScoring");
comment|//}
comment|// TODO: maybe a class like BS, instead of parallel arrays
name|int
index|[]
name|filledSlots
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|CHUNK
index|]
decl_stmt|;
name|int
index|[]
name|missingDims
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|docIDs
index|[
literal|0
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|int
name|nextChunkStart
init|=
name|CHUNK
decl_stmt|;
specifier|final
name|FixedBitSet
name|seen
init|=
operator|new
name|FixedBitSet
argument_list|(
name|CHUNK
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("\ncycle nextChunkStart=" + nextChunkStart + " docIds[0]=" + docIDs[0]);
comment|//}
comment|// First dim:
comment|//if (DEBUG) {
comment|//  System.out.println("  dim0");
comment|//}
for|for
control|(
name|DocsEnum
name|docsEnum
range|:
name|docsEnums
index|[
literal|0
index|]
control|)
block|{
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|int
name|docID
init|=
name|docsEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
while|while
condition|(
name|docID
operator|<
name|nextChunkStart
condition|)
block|{
name|int
name|slot
init|=
name|docID
operator|&
name|MASK
decl_stmt|;
if|if
condition|(
name|docIDs
index|[
name|slot
index|]
operator|!=
name|docID
condition|)
block|{
name|seen
operator|.
name|set
argument_list|(
name|slot
argument_list|)
expr_stmt|;
comment|// Mark slot as valid:
comment|//if (DEBUG) {
comment|//  System.out.println("    set docID=" + docID + " id=" + context.reader().document(docID).get("id"));
comment|//}
name|docIDs
index|[
name|slot
index|]
operator|=
name|docID
expr_stmt|;
name|missingDims
index|[
name|slot
index|]
operator|=
literal|1
expr_stmt|;
name|counts
index|[
name|slot
index|]
operator|=
literal|1
expr_stmt|;
block|}
name|docID
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Second dim:
comment|//if (DEBUG) {
comment|//  System.out.println("  dim1");
comment|//}
for|for
control|(
name|DocsEnum
name|docsEnum
range|:
name|docsEnums
index|[
literal|1
index|]
control|)
block|{
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|int
name|docID
init|=
name|docsEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
while|while
condition|(
name|docID
operator|<
name|nextChunkStart
condition|)
block|{
name|int
name|slot
init|=
name|docID
operator|&
name|MASK
decl_stmt|;
if|if
condition|(
name|docIDs
index|[
name|slot
index|]
operator|!=
name|docID
condition|)
block|{
comment|// Mark slot as valid:
name|seen
operator|.
name|set
argument_list|(
name|slot
argument_list|)
expr_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    set docID=" + docID + " missingDim=0 id=" + context.reader().document(docID).get("id"));
comment|//}
name|docIDs
index|[
name|slot
index|]
operator|=
name|docID
expr_stmt|;
name|missingDims
index|[
name|slot
index|]
operator|=
literal|0
expr_stmt|;
name|counts
index|[
name|slot
index|]
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: single-valued dims will always be true
comment|// below; we could somehow specialize
if|if
condition|(
name|missingDims
index|[
name|slot
index|]
operator|>=
literal|1
condition|)
block|{
name|missingDims
index|[
name|slot
index|]
operator|=
literal|2
expr_stmt|;
name|counts
index|[
name|slot
index|]
operator|=
literal|2
expr_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    set docID=" + docID + " missingDim=2 id=" + context.reader().document(docID).get("id"));
comment|//}
block|}
else|else
block|{
name|counts
index|[
name|slot
index|]
operator|=
literal|1
expr_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    set docID=" + docID + " missingDim=" + missingDims[slot] + " id=" + context.reader().document(docID).get("id"));
comment|//}
block|}
block|}
name|docID
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
comment|// After this we can "upgrade" to conjunction, because
comment|// any doc not seen by either dim 0 or dim 1 cannot be
comment|// a hit or a near miss:
comment|//if (DEBUG) {
comment|//  System.out.println("  baseScorer");
comment|//}
comment|// Fold in baseScorer, using advance:
name|int
name|filledCount
init|=
literal|0
decl_stmt|;
name|int
name|slot0
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|slot0
operator|<
name|CHUNK
operator|&&
operator|(
name|slot0
operator|=
name|seen
operator|.
name|nextSetBit
argument_list|(
name|slot0
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|ddDocID
init|=
name|docIDs
index|[
name|slot0
index|]
decl_stmt|;
assert|assert
name|ddDocID
operator|!=
operator|-
literal|1
assert|;
name|int
name|baseDocID
init|=
name|baseScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseDocID
operator|<
name|ddDocID
condition|)
block|{
name|baseDocID
operator|=
name|baseScorer
operator|.
name|advance
argument_list|(
name|ddDocID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|baseDocID
operator|==
name|ddDocID
condition|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("    keep docID=" + ddDocID + " id=" + context.reader().document(ddDocID).get("id"));
comment|//}
name|scores
index|[
name|slot0
index|]
operator|=
name|baseScorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|filledSlots
index|[
name|filledCount
operator|++
index|]
operator|=
name|slot0
expr_stmt|;
name|counts
index|[
name|slot0
index|]
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("    no docID=" + ddDocID + " id=" + context.reader().document(ddDocID).get("id"));
comment|//}
name|docIDs
index|[
name|slot0
index|]
operator|=
operator|-
literal|1
expr_stmt|;
comment|// TODO: we could jump slot0 forward to the
comment|// baseDocID ... but we'd need to set docIDs for
comment|// intervening slots to -1
block|}
name|slot0
operator|++
expr_stmt|;
block|}
name|seen
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|CHUNK
argument_list|)
expr_stmt|;
if|if
condition|(
name|filledCount
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|nextChunkStart
operator|>=
name|maxDoc
condition|)
block|{
break|break;
block|}
name|nextChunkStart
operator|+=
name|CHUNK
expr_stmt|;
continue|continue;
block|}
comment|// TODO: factor this out& share w/ union scorer,
comment|// except we start from dim=2 instead:
for|for
control|(
name|int
name|dim
init|=
literal|2
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("  dim=" + dim + " [" + dims[dim].dim + "]");
comment|//}
for|for
control|(
name|DocsEnum
name|docsEnum
range|:
name|docsEnums
index|[
name|dim
index|]
control|)
block|{
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|int
name|docID
init|=
name|docsEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
while|while
condition|(
name|docID
operator|<
name|nextChunkStart
condition|)
block|{
name|int
name|slot
init|=
name|docID
operator|&
name|MASK
decl_stmt|;
if|if
condition|(
name|docIDs
index|[
name|slot
index|]
operator|==
name|docID
operator|&&
name|counts
index|[
name|slot
index|]
operator|>=
name|dim
condition|)
block|{
comment|// TODO: single-valued dims will always be true
comment|// below; we could somehow specialize
if|if
condition|(
name|missingDims
index|[
name|slot
index|]
operator|>=
name|dim
condition|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("    set docID=" + docID + " count=" + (dim+2));
comment|//}
name|missingDims
index|[
name|slot
index|]
operator|=
name|dim
operator|+
literal|1
expr_stmt|;
name|counts
index|[
name|slot
index|]
operator|=
name|dim
operator|+
literal|2
expr_stmt|;
block|}
else|else
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("    set docID=" + docID + " missing count=" + (dim+1));
comment|//}
name|counts
index|[
name|slot
index|]
operator|=
name|dim
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|// TODO: sometimes use advance?
name|docID
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Collect:
comment|//if (DEBUG) {
comment|//  System.out.println("  now collect: " + filledCount + " hits");
comment|//}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filledCount
condition|;
name|i
operator|++
control|)
block|{
name|int
name|slot
init|=
name|filledSlots
index|[
name|i
index|]
decl_stmt|;
name|collectDocID
operator|=
name|docIDs
index|[
name|slot
index|]
expr_stmt|;
name|collectScore
operator|=
name|scores
index|[
name|slot
index|]
expr_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    docID=" + docIDs[slot] + " count=" + counts[slot]);
comment|//}
if|if
condition|(
name|counts
index|[
name|slot
index|]
operator|==
literal|1
operator|+
name|numDims
condition|)
block|{
name|collectHit
argument_list|(
name|collector
argument_list|,
name|sidewaysCollectors
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|counts
index|[
name|slot
index|]
operator|==
name|numDims
condition|)
block|{
name|collectNearMiss
argument_list|(
name|sidewaysCollectors
argument_list|,
name|missingDims
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nextChunkStart
operator|>=
name|maxDoc
condition|)
block|{
break|break;
block|}
name|nextChunkStart
operator|+=
name|CHUNK
expr_stmt|;
block|}
block|}
comment|/** Used when base query is highly constraining vs the    *  drilldowns; in this case we just .next() on base and    *  .advance() on the dims. */
DECL|method|doBaseAdvanceScoring
specifier|private
name|void
name|doBaseAdvanceScoring
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|DocsEnum
index|[]
index|[]
name|docsEnums
parameter_list|,
name|Collector
index|[]
name|sidewaysCollectors
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("  doBaseAdvanceScoring");
comment|//}
name|int
name|docID
init|=
name|baseScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDims
init|=
name|dims
operator|.
name|length
decl_stmt|;
name|nextDoc
label|:
while|while
condition|(
name|docID
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|failedDim
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
comment|// TODO: should we sort this 2nd dimension of
comment|// docsEnums from most frequent to least?
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|DocsEnum
name|docsEnum
range|:
name|docsEnums
index|[
name|dim
index|]
control|)
block|{
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|docsEnum
operator|.
name|docID
argument_list|()
operator|<
name|docID
condition|)
block|{
name|docsEnum
operator|.
name|advance
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docsEnum
operator|.
name|docID
argument_list|()
operator|==
name|docID
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
if|if
condition|(
name|failedDim
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// More than one dim fails on this document, so
comment|// it's neither a hit nor a near-miss; move to
comment|// next doc:
name|docID
operator|=
name|baseScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
continue|continue
name|nextDoc
continue|;
block|}
else|else
block|{
name|failedDim
operator|=
name|dim
expr_stmt|;
block|}
block|}
block|}
name|collectDocID
operator|=
name|docID
expr_stmt|;
comment|// TODO: we could score on demand instead since we are
comment|// daat here:
name|collectScore
operator|=
name|baseScorer
operator|.
name|score
argument_list|()
expr_stmt|;
if|if
condition|(
name|failedDim
operator|==
operator|-
literal|1
condition|)
block|{
name|collectHit
argument_list|(
name|collector
argument_list|,
name|sidewaysCollectors
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectNearMiss
argument_list|(
name|sidewaysCollectors
argument_list|,
name|failedDim
argument_list|)
expr_stmt|;
block|}
name|docID
operator|=
name|baseScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|collectHit
specifier|private
name|void
name|collectHit
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|Collector
index|[]
name|sidewaysCollectors
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("      hit");
comment|//}
name|collector
operator|.
name|collect
argument_list|(
name|collectDocID
argument_list|)
expr_stmt|;
name|drillDownCollector
operator|.
name|collect
argument_list|(
name|collectDocID
argument_list|)
expr_stmt|;
comment|// TODO: we could "fix" faceting of the sideways counts
comment|// to do this "union" (of the drill down hits) in the
comment|// end instead:
comment|// Tally sideways counts:
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|sidewaysCollectors
operator|.
name|length
condition|;
name|dim
operator|++
control|)
block|{
name|sidewaysCollectors
index|[
name|dim
index|]
operator|.
name|collect
argument_list|(
name|collectDocID
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|collectNearMiss
specifier|private
name|void
name|collectNearMiss
parameter_list|(
name|Collector
index|[]
name|sidewaysCollectors
parameter_list|,
name|int
name|dim
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("      missingDim=" + dim);
comment|//}
name|sidewaysCollectors
index|[
name|dim
index|]
operator|.
name|collect
argument_list|(
name|collectDocID
argument_list|)
expr_stmt|;
block|}
DECL|method|doUnionScoring
specifier|private
name|void
name|doUnionScoring
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|DocsEnum
index|[]
index|[]
name|docsEnums
parameter_list|,
name|Collector
index|[]
name|sidewaysCollectors
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("  doUnionScoring");
comment|//}
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDims
init|=
name|dims
operator|.
name|length
decl_stmt|;
comment|// TODO: maybe a class like BS, instead of parallel arrays
name|int
index|[]
name|filledSlots
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|CHUNK
index|]
decl_stmt|;
name|int
index|[]
name|missingDims
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
name|docIDs
index|[
literal|0
index|]
operator|=
operator|-
literal|1
expr_stmt|;
comment|// NOTE: this is basically a specialized version of
comment|// BooleanScorer, to the minShouldMatch=N-1 case, but
comment|// carefully tracking which dimension failed to match
name|int
name|nextChunkStart
init|=
name|CHUNK
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("\ncycle nextChunkStart=" + nextChunkStart + " docIds[0]=" + docIDs[0]);
comment|//}
name|int
name|filledCount
init|=
literal|0
decl_stmt|;
name|int
name|docID
init|=
name|baseScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("  base docID=" + docID);
comment|//}
while|while
condition|(
name|docID
operator|<
name|nextChunkStart
condition|)
block|{
name|int
name|slot
init|=
name|docID
operator|&
name|MASK
decl_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    docIDs[slot=" + slot + "]=" + docID + " id=" + context.reader().document(docID).get("id"));
comment|//}
comment|// Mark slot as valid:
assert|assert
name|docIDs
index|[
name|slot
index|]
operator|!=
name|docID
operator|:
literal|"slot="
operator|+
name|slot
operator|+
literal|" docID="
operator|+
name|docID
assert|;
name|docIDs
index|[
name|slot
index|]
operator|=
name|docID
expr_stmt|;
name|scores
index|[
name|slot
index|]
operator|=
name|baseScorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|filledSlots
index|[
name|filledCount
operator|++
index|]
operator|=
name|slot
expr_stmt|;
name|missingDims
index|[
name|slot
index|]
operator|=
literal|0
expr_stmt|;
name|counts
index|[
name|slot
index|]
operator|=
literal|1
expr_stmt|;
name|docID
operator|=
name|baseScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|filledCount
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|nextChunkStart
operator|>=
name|maxDoc
condition|)
block|{
break|break;
block|}
name|nextChunkStart
operator|+=
name|CHUNK
expr_stmt|;
continue|continue;
block|}
comment|// First drill-down dim, basically adds SHOULD onto
comment|// the baseQuery:
comment|//if (DEBUG) {
comment|//  System.out.println("  dim=0 [" + dims[0].dim + "]");
comment|//}
for|for
control|(
name|DocsEnum
name|docsEnum
range|:
name|docsEnums
index|[
literal|0
index|]
control|)
block|{
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|docID
operator|=
name|docsEnum
operator|.
name|docID
argument_list|()
expr_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    start docID=" + docID);
comment|//}
while|while
condition|(
name|docID
operator|<
name|nextChunkStart
condition|)
block|{
name|int
name|slot
init|=
name|docID
operator|&
name|MASK
decl_stmt|;
if|if
condition|(
name|docIDs
index|[
name|slot
index|]
operator|==
name|docID
condition|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("      set docID=" + docID + " count=2");
comment|//}
name|missingDims
index|[
name|slot
index|]
operator|=
literal|1
expr_stmt|;
name|counts
index|[
name|slot
index|]
operator|=
literal|2
expr_stmt|;
block|}
name|docID
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|dim
init|=
literal|1
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("  dim=" + dim + " [" + dims[dim].dim + "]");
comment|//}
for|for
control|(
name|DocsEnum
name|docsEnum
range|:
name|docsEnums
index|[
name|dim
index|]
control|)
block|{
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|docID
operator|=
name|docsEnum
operator|.
name|docID
argument_list|()
expr_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    start docID=" + docID);
comment|//}
while|while
condition|(
name|docID
operator|<
name|nextChunkStart
condition|)
block|{
name|int
name|slot
init|=
name|docID
operator|&
name|MASK
decl_stmt|;
if|if
condition|(
name|docIDs
index|[
name|slot
index|]
operator|==
name|docID
operator|&&
name|counts
index|[
name|slot
index|]
operator|>=
name|dim
condition|)
block|{
comment|// This doc is still in the running...
comment|// TODO: single-valued dims will always be true
comment|// below; we could somehow specialize
if|if
condition|(
name|missingDims
index|[
name|slot
index|]
operator|>=
name|dim
condition|)
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("      set docID=" + docID + " count=" + (dim+2));
comment|//}
name|missingDims
index|[
name|slot
index|]
operator|=
name|dim
operator|+
literal|1
expr_stmt|;
name|counts
index|[
name|slot
index|]
operator|=
name|dim
operator|+
literal|2
expr_stmt|;
block|}
else|else
block|{
comment|//if (DEBUG) {
comment|//  System.out.println("      set docID=" + docID + " missing count=" + (dim+1));
comment|//}
name|counts
index|[
name|slot
index|]
operator|=
name|dim
operator|+
literal|1
expr_stmt|;
block|}
block|}
name|docID
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
comment|// TODO: sometimes use advance?
comment|/*             int docBase = nextChunkStart - CHUNK;             for(int i=0;i<filledCount;i++) {               int slot = filledSlots[i];               docID = docBase + filledSlots[i];               if (docIDs[slot] == docID&& counts[slot]>= dim) {                 // This doc is still in the running...                 int ddDocID = docsEnum.docID();                 if (ddDocID< docID) {                   ddDocID = docsEnum.advance(docID);                 }                 if (ddDocID == docID) {                   if (missingDims[slot]>= dim&& counts[slot] == allMatchCount) {                   //if (DEBUG) {                   //    System.out.println("    set docID=" + docID + " count=" + (dim+2));                    // }                     missingDims[slot] = dim+1;                     counts[slot] = dim+2;                   } else {                   //if (DEBUG) {                   //    System.out.println("    set docID=" + docID + " missing count=" + (dim+1));                    // }                     counts[slot] = dim+1;                   }                 }               }             }                       */
block|}
block|}
comment|// Collect:
comment|//if (DEBUG) {
comment|//  System.out.println("  now collect: " + filledCount + " hits");
comment|//}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filledCount
condition|;
name|i
operator|++
control|)
block|{
name|int
name|slot
init|=
name|filledSlots
index|[
name|i
index|]
decl_stmt|;
name|collectDocID
operator|=
name|docIDs
index|[
name|slot
index|]
expr_stmt|;
name|collectScore
operator|=
name|scores
index|[
name|slot
index|]
expr_stmt|;
comment|//if (DEBUG) {
comment|//  System.out.println("    docID=" + docIDs[slot] + " count=" + counts[slot]);
comment|//}
comment|//System.out.println("  collect doc=" + collectDocID + " main.freq=" + (counts[slot]-1) + " main.doc=" + collectDocID + " exactCount=" + numDims);
if|if
condition|(
name|counts
index|[
name|slot
index|]
operator|==
literal|1
operator|+
name|numDims
condition|)
block|{
comment|//System.out.println("    hit");
name|collectHit
argument_list|(
name|collector
argument_list|,
name|sidewaysCollectors
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|counts
index|[
name|slot
index|]
operator|==
name|numDims
condition|)
block|{
comment|//System.out.println("    sw");
name|collectNearMiss
argument_list|(
name|sidewaysCollectors
argument_list|,
name|missingDims
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nextChunkStart
operator|>=
name|maxDoc
condition|)
block|{
break|break;
block|}
name|nextChunkStart
operator|+=
name|CHUNK
expr_stmt|;
block|}
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
name|collectDocID
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
name|collectScore
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
literal|1
operator|+
name|dims
operator|.
name|length
return|;
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
argument_list|()
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
argument_list|()
throw|;
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
name|baseScorer
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|baseScorer
argument_list|,
literal|"MUST"
argument_list|)
argument_list|)
return|;
block|}
DECL|class|DocsEnumsAndFreq
specifier|static
class|class
name|DocsEnumsAndFreq
implements|implements
name|Comparable
argument_list|<
name|DocsEnumsAndFreq
argument_list|>
block|{
DECL|field|docsEnums
name|DocsEnum
index|[]
name|docsEnums
decl_stmt|;
comment|// Max docFreq for all docsEnums for this dim:
DECL|field|freq
name|int
name|freq
decl_stmt|;
DECL|field|sidewaysCollector
name|Collector
name|sidewaysCollector
decl_stmt|;
DECL|field|dim
name|String
name|dim
decl_stmt|;
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|DocsEnumsAndFreq
name|other
parameter_list|)
block|{
return|return
name|freq
operator|-
name|other
operator|.
name|freq
return|;
block|}
block|}
block|}
end_class
end_unit
