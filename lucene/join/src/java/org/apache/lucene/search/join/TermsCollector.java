begin_unit
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
name|BinaryDocValues
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
name|SortedSetDocValues
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
name|SimpleCollector
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
name|BytesRefHash
import|;
end_import
begin_comment
comment|/**  * A collector that collects all terms from a specified field matching the query.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermsCollector
specifier|abstract
class|class
name|TermsCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|collectorTerms
specifier|final
name|BytesRefHash
name|collectorTerms
init|=
operator|new
name|BytesRefHash
argument_list|()
decl_stmt|;
DECL|method|TermsCollector
name|TermsCollector
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getCollectorTerms
specifier|public
name|BytesRefHash
name|getCollectorTerms
parameter_list|()
block|{
return|return
name|collectorTerms
return|;
block|}
comment|/**    * Chooses the right {@link TermsCollector} implementation.    *    * @param field                     The field to collect terms for    * @param multipleValuesPerDocument Whether the field to collect terms for has multiple values per document.    * @return a {@link TermsCollector} instance    */
DECL|method|create
specifier|static
name|TermsCollector
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|)
block|{
return|return
name|multipleValuesPerDocument
condition|?
operator|new
name|MV
argument_list|(
name|field
argument_list|)
else|:
operator|new
name|SV
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|// impl that works with multiple values per document
DECL|class|MV
specifier|static
class|class
name|MV
extends|extends
name|TermsCollector
block|{
DECL|field|scratch
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|docTermOrds
specifier|private
name|SortedSetDocValues
name|docTermOrds
decl_stmt|;
DECL|method|MV
name|MV
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
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
throws|throws
name|IOException
block|{
name|docTermOrds
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|docTermOrds
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|docTermOrds
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|collectorTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docTermOrds
operator|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
comment|// impl that works with single value per document
DECL|class|SV
specifier|static
class|class
name|SV
extends|extends
name|TermsCollector
block|{
DECL|field|spare
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|fromDocTerms
specifier|private
name|BinaryDocValues
name|fromDocTerms
decl_stmt|;
DECL|method|SV
name|SV
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
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
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|term
init|=
name|fromDocTerms
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|collectorTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|fromDocTerms
operator|=
name|DocValues
operator|.
name|getBinary
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
