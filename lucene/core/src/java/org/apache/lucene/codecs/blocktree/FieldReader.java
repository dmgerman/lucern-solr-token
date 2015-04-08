begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.blocktree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktree
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
name|FieldInfo
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
name|IndexOptions
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
name|store
operator|.
name|ByteArrayDataInput
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
name|store
operator|.
name|IndexInput
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
name|Accountable
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
name|Accountables
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
name|RamUsageEstimator
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
name|automaton
operator|.
name|CompiledAutomaton
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
name|fst
operator|.
name|ByteSequenceOutputs
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
name|fst
operator|.
name|FST
import|;
end_import
begin_comment
comment|/**  * BlockTree's implementation of {@link Terms}.  * @lucene.internal  */
end_comment
begin_class
DECL|class|FieldReader
specifier|public
specifier|final
class|class
name|FieldReader
extends|extends
name|Terms
implements|implements
name|Accountable
block|{
comment|// private final boolean DEBUG = BlockTreeTermsWriter.DEBUG;
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|FieldReader
operator|.
name|class
argument_list|)
operator|+
literal|3
operator|*
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|BytesRef
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|numTerms
specifier|final
name|long
name|numTerms
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|final
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|final
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|indexStartFP
specifier|final
name|long
name|indexStartFP
decl_stmt|;
DECL|field|rootBlockFP
specifier|final
name|long
name|rootBlockFP
decl_stmt|;
DECL|field|rootCode
specifier|final
name|BytesRef
name|rootCode
decl_stmt|;
DECL|field|minTerm
specifier|final
name|BytesRef
name|minTerm
decl_stmt|;
DECL|field|maxTerm
specifier|final
name|BytesRef
name|maxTerm
decl_stmt|;
DECL|field|longsSize
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|field|parent
specifier|final
name|BlockTreeTermsReader
name|parent
decl_stmt|;
DECL|field|index
specifier|final
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|index
decl_stmt|;
comment|//private boolean DEBUG;
DECL|method|FieldReader
name|FieldReader
parameter_list|(
name|BlockTreeTermsReader
name|parent
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|numTerms
parameter_list|,
name|BytesRef
name|rootCode
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|,
name|long
name|indexStartFP
parameter_list|,
name|int
name|longsSize
parameter_list|,
name|IndexInput
name|indexIn
parameter_list|,
name|BytesRef
name|minTerm
parameter_list|,
name|BytesRef
name|maxTerm
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|numTerms
operator|>
literal|0
assert|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
comment|//DEBUG = BlockTreeTermsReader.DEBUG&& fieldInfo.name.equals("id");
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|this
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|this
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|indexStartFP
operator|=
name|indexStartFP
expr_stmt|;
name|this
operator|.
name|rootCode
operator|=
name|rootCode
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|longsSize
expr_stmt|;
name|this
operator|.
name|minTerm
operator|=
name|minTerm
expr_stmt|;
name|this
operator|.
name|maxTerm
operator|=
name|maxTerm
expr_stmt|;
comment|// if (DEBUG) {
comment|//   System.out.println("BTTR: seg=" + segment + " field=" + fieldInfo.name + " rootBlockCode=" + rootCode + " divisor=" + indexDivisor);
comment|// }
name|rootBlockFP
operator|=
operator|(
operator|new
name|ByteArrayDataInput
argument_list|(
name|rootCode
operator|.
name|bytes
argument_list|,
name|rootCode
operator|.
name|offset
argument_list|,
name|rootCode
operator|.
name|length
argument_list|)
operator|)
operator|.
name|readVLong
argument_list|()
operator|>>>
name|BlockTreeTermsReader
operator|.
name|OUTPUT_FLAGS_NUM_BITS
expr_stmt|;
if|if
condition|(
name|indexIn
operator|!=
literal|null
condition|)
block|{
specifier|final
name|IndexInput
name|clone
init|=
name|indexIn
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|//System.out.println("start=" + indexStartFP + " field=" + fieldInfo.name);
name|clone
operator|.
name|seek
argument_list|(
name|indexStartFP
argument_list|)
expr_stmt|;
name|index
operator|=
operator|new
name|FST
argument_list|<>
argument_list|(
name|clone
argument_list|,
name|ByteSequenceOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|)
expr_stmt|;
comment|/*         if (false) {         final String dotFileName = segment + "_" + fieldInfo.name + ".dot";         Writer w = new OutputStreamWriter(new FileOutputStream(dotFileName));         Util.toDot(index, w, false, false);         System.out.println("FST INDEX: SAVED to " + dotFileName);         w.close();         }       */
block|}
else|else
block|{
name|index
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMin
specifier|public
name|BytesRef
name|getMin
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|minTerm
operator|==
literal|null
condition|)
block|{
comment|// Older index that didn't store min/maxTerm
return|return
name|super
operator|.
name|getMin
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|minTerm
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMax
specifier|public
name|BytesRef
name|getMax
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|maxTerm
operator|==
literal|null
condition|)
block|{
comment|// Older index that didn't store min/maxTerm
return|return
name|super
operator|.
name|getMax
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|maxTerm
return|;
block|}
block|}
comment|/** For debugging -- used by CheckIndex too*/
annotation|@
name|Override
DECL|method|getStats
specifier|public
name|Stats
name|getStats
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: add auto-prefix terms into stats
return|return
operator|new
name|SegmentTermsEnum
argument_list|(
name|this
argument_list|)
operator|.
name|computeBlockStats
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasFreqs
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
return|return
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasPositions
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
return|return
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SegmentTermsEnum
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|numTerms
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
return|return
name|sumTotalTermFreq
return|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
block|{
return|return
name|sumDocFreq
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
block|{
return|return
name|docCount
return|;
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|TermsEnum
name|intersect
parameter_list|(
name|CompiledAutomaton
name|compiled
parameter_list|,
name|BytesRef
name|startTerm
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) System.out.println("  FieldReader.intersect startTerm=" + BlockTreeTermsWriter.brToString(startTerm));
comment|//System.out.println("intersect: " + compiled.type + " a=" + compiled.automaton);
comment|// TODO: we could push "it's a range" or "it's a prefix" down into IntersectTermsEnum?
comment|// can we optimize knowing that...?
return|return
operator|new
name|IntersectTermsEnum
argument_list|(
name|this
argument_list|,
name|compiled
operator|.
name|automaton
argument_list|,
name|compiled
operator|.
name|runAutomaton
argument_list|,
name|compiled
operator|.
name|commonSuffixRef
argument_list|,
name|startTerm
argument_list|,
name|compiled
operator|.
name|sinkState
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
operator|(
operator|(
name|index
operator|!=
literal|null
operator|)
condition|?
name|index
operator|.
name|ramBytesUsed
argument_list|()
else|:
literal|0
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"term index"
argument_list|,
name|index
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"BlockTreeTerms(terms="
operator|+
name|numTerms
operator|+
literal|",postings="
operator|+
name|sumDocFreq
operator|+
literal|",positions="
operator|+
name|sumTotalTermFreq
operator|+
literal|",docs="
operator|+
name|docCount
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
