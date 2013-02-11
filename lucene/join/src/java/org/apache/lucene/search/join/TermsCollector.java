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
name|DocTermOrds
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
name|Collector
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
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
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
DECL|field|docTermOrds
specifier|private
name|DocTermOrds
name|docTermOrds
decl_stmt|;
DECL|field|docTermsEnum
specifier|private
name|TermsEnum
name|docTermsEnum
decl_stmt|;
DECL|field|reuse
specifier|private
name|DocTermOrds
operator|.
name|TermOrdsIterator
name|reuse
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
name|reuse
operator|=
name|docTermOrds
operator|.
name|lookup
argument_list|(
name|doc
argument_list|,
name|reuse
argument_list|)
expr_stmt|;
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|5
index|]
decl_stmt|;
name|int
name|chunk
decl_stmt|;
do|do
block|{
name|chunk
operator|=
name|reuse
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|chunk
operator|==
literal|0
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|chunk
condition|;
name|idx
operator|++
control|)
block|{
name|int
name|key
init|=
name|buffer
index|[
name|idx
index|]
decl_stmt|;
name|docTermsEnum
operator|.
name|seekExact
argument_list|(
operator|(
name|long
operator|)
name|key
argument_list|)
expr_stmt|;
name|collectorTerms
operator|.
name|add
argument_list|(
name|docTermsEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|chunk
operator|>=
name|buffer
operator|.
name|length
condition|)
do|;
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
throws|throws
name|IOException
block|{
comment|// nocommit: cut over
name|DocTermOrds
operator|.
name|Iterator
name|iterator
init|=
operator|(
name|DocTermOrds
operator|.
name|Iterator
operator|)
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocTermOrds
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|docTermOrds
operator|=
name|iterator
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|docTermsEnum
operator|=
name|docTermOrds
operator|.
name|getOrdTermsEnum
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|reuse
operator|=
literal|null
expr_stmt|;
comment|// LUCENE-3377 needs to be fixed first then this statement can be removed...
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
name|fromDocTerms
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|collectorTerms
operator|.
name|add
argument_list|(
name|spare
argument_list|)
expr_stmt|;
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
throws|throws
name|IOException
block|{
name|fromDocTerms
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTerms
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
block|}
end_class
end_unit
