begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping.term
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
operator|.
name|term
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FieldCache
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
name|Sort
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
name|grouping
operator|.
name|AbstractFirstPassGroupingCollector
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Concrete implementation of {@link org.apache.lucene.search.grouping.AbstractFirstPassGroupingCollector} that groups based on  * field values and more specifically uses {@link org.apache.lucene.search.FieldCache.DocTermsIndex}  * to collect groups.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermFirstPassGroupingCollector
specifier|public
class|class
name|TermFirstPassGroupingCollector
extends|extends
name|AbstractFirstPassGroupingCollector
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|scratchBytesRef
specifier|private
specifier|final
name|BytesRef
name|scratchBytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|index
specifier|private
name|FieldCache
operator|.
name|DocTermsIndex
name|index
decl_stmt|;
DECL|field|groupField
specifier|private
name|String
name|groupField
decl_stmt|;
comment|/**    * Create the first pass collector.    *    *  @param groupField The field used to group    *    documents. This field must be single-valued and    *    indexed (FieldCache is used to access its value    *    per-document).    *  @param groupSort The {@link Sort} used to sort the    *    groups.  The top sorted document within each group    *    according to groupSort, determines how that group    *    sorts against other groups.  This must be non-null,    *    ie, if you want to groupSort by relevance use    *    Sort.RELEVANCE.    *  @param topNGroups How many top groups to keep.    *  @throws IOException When I/O related errors occur    */
DECL|method|TermFirstPassGroupingCollector
specifier|public
name|TermFirstPassGroupingCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocGroupValue
specifier|protected
name|BytesRef
name|getDocGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|index
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|ord
operator|==
literal|0
condition|?
literal|null
else|:
name|index
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|scratchBytesRef
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyDocGroupValue
specifier|protected
name|BytesRef
name|copyDocGroupValue
parameter_list|(
name|BytesRef
name|groupValue
parameter_list|,
name|BytesRef
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|groupValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|.
name|copyBytes
argument_list|(
name|groupValue
argument_list|)
expr_stmt|;
return|return
name|reuse
return|;
block|}
else|else
block|{
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|groupValue
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|index
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
