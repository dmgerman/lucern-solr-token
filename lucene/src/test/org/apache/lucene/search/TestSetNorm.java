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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|LuceneTestCase
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
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|*
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
name|IndexReader
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
name|IndexWriter
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
name|store
operator|.
name|Directory
import|;
end_import
begin_comment
comment|/** Document boost unit test.  *  *  * @version $Revision$  */
end_comment
begin_class
DECL|class|TestSetNorm
specifier|public
class|class
name|TestSetNorm
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSetNorm
specifier|public
name|void
name|testSetNorm
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|store
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|store
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// add the same document four times
name|Fieldable
name|f1
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// reset the boost of each instance of this document
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Similarity
name|similarity
init|=
operator|new
name|DefaultSimilarity
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setNorm
argument_list|(
literal|0
argument_list|,
literal|"field"
argument_list|,
name|similarity
operator|.
name|encodeNormValue
argument_list|(
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setNorm
argument_list|(
literal|1
argument_list|,
literal|"field"
argument_list|,
name|similarity
operator|.
name|encodeNormValue
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setNorm
argument_list|(
literal|2
argument_list|,
literal|"field"
argument_list|,
name|similarity
operator|.
name|encodeNormValue
argument_list|(
literal|4.0f
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setNorm
argument_list|(
literal|3
argument_list|,
literal|"field"
argument_list|,
name|similarity
operator|.
name|encodeNormValue
argument_list|(
literal|16.0f
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check that searches are ordered by this boost
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
literal|4
index|]
decl_stmt|;
name|IndexSearcher
name|is
init|=
operator|new
name|IndexSearcher
argument_list|(
name|store
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|is
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Collector
argument_list|()
block|{
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
specifier|private
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|scores
index|[
name|doc
operator|+
name|base
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|base
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|float
name|lastScore
init|=
literal|0.0f
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|scores
index|[
name|i
index|]
operator|>
name|lastScore
argument_list|)
expr_stmt|;
name|lastScore
operator|=
name|scores
index|[
name|i
index|]
expr_stmt|;
block|}
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
