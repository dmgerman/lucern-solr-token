begin_unit
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
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
name|Document
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
name|StringField
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
name|TextField
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
name|index
operator|.
name|FieldNormModifier
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
name|MultiNorms
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
name|TermQuery
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
name|DefaultSimilarity
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
name|DefaultSimilarityProvider
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
name|search
operator|.
name|similarities
operator|.
name|SimilarityProvider
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
begin_comment
comment|/**  * Tests changing the norms after changing the simularity  */
end_comment
begin_class
DECL|class|TestLengthNormModifier
specifier|public
class|class
name|TestLengthNormModifier
extends|extends
name|LuceneTestCase
block|{
DECL|field|NUM_DOCS
specifier|public
specifier|static
name|int
name|NUM_DOCS
init|=
literal|5
decl_stmt|;
DECL|field|store
specifier|public
name|Directory
name|store
decl_stmt|;
comment|/** inverts the normal notion of lengthNorm */
DECL|field|s
specifier|public
specifier|static
name|SimilarityProvider
name|s
init|=
operator|new
name|DefaultSimilarityProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|encodeNormValue
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
operator|*
operator|(
name|discountOverlaps
condition|?
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
else|:
name|state
operator|.
name|getLength
argument_list|()
operator|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|store
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
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
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"nonorm"
argument_list|,
literal|"word"
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|i
condition|;
name|j
operator|++
control|)
block|{
name|d
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"crap"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"nonorm"
argument_list|,
literal|"more words"
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testMissingField
specifier|public
name|void
name|testMissingField
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldNormModifier
name|fnm
init|=
operator|new
name|FieldNormModifier
argument_list|(
name|store
argument_list|,
name|s
argument_list|)
decl_stmt|;
try|try
block|{
name|fnm
operator|.
name|reSetNorms
argument_list|(
literal|"nobodyherebutuschickens"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testFieldWithNoNorm
specifier|public
name|void
name|testFieldWithNoNorm
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|r
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
name|byte
index|[]
name|norms
init|=
name|MultiNorms
operator|.
name|norms
argument_list|(
name|r
argument_list|,
literal|"nonorm"
argument_list|)
decl_stmt|;
comment|// sanity check, norms should all be 1
name|assertTrue
argument_list|(
literal|"Whoops we have norms?"
argument_list|,
operator|!
name|r
operator|.
name|hasNorms
argument_list|(
literal|"nonorm"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|norms
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|FieldNormModifier
name|fnm
init|=
operator|new
name|FieldNormModifier
argument_list|(
name|store
argument_list|,
name|s
argument_list|)
decl_stmt|;
try|try
block|{
name|fnm
operator|.
name|reSetNorms
argument_list|(
literal|"nonorm"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// nothing should have changed
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|store
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|norms
operator|=
name|MultiNorms
operator|.
name|norms
argument_list|(
name|r
argument_list|,
literal|"nonorm"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Whoops we have norms?"
argument_list|,
operator|!
name|r
operator|.
name|hasNorms
argument_list|(
literal|"nonorm"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|norms
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testGoodCases
specifier|public
name|void
name|testGoodCases
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexSearcher
name|searcher
decl_stmt|;
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|NUM_DOCS
index|]
decl_stmt|;
name|float
name|lastScore
init|=
literal|0.0f
decl_stmt|;
comment|// default similarity should put docs with shorter length first
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|store
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
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
name|docBase
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
name|docBase
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
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|lastScore
operator|=
name|Float
operator|.
name|MAX_VALUE
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|msg
init|=
literal|"i="
operator|+
name|i
operator|+
literal|", "
operator|+
name|scores
index|[
name|i
index|]
operator|+
literal|"<= "
operator|+
name|lastScore
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|scores
index|[
name|i
index|]
operator|<=
name|lastScore
argument_list|)
expr_stmt|;
comment|//System.out.println(msg);
name|lastScore
operator|=
name|scores
index|[
name|i
index|]
expr_stmt|;
block|}
comment|// override the norms to be inverted
name|SimilarityProvider
name|s
init|=
operator|new
name|DefaultSimilarityProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|encodeNormValue
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
operator|*
operator|(
name|discountOverlaps
condition|?
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
else|:
name|state
operator|.
name|getLength
argument_list|()
operator|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|FieldNormModifier
name|fnm
init|=
operator|new
name|FieldNormModifier
argument_list|(
name|store
argument_list|,
name|s
argument_list|)
decl_stmt|;
name|fnm
operator|.
name|reSetNorms
argument_list|(
literal|"field"
argument_list|)
expr_stmt|;
comment|// new norm (with default similarity) should put longer docs first
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|store
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
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
name|docBase
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
name|docBase
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
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|lastScore
operator|=
literal|0.0f
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|msg
init|=
literal|"i="
operator|+
name|i
operator|+
literal|", "
operator|+
name|scores
index|[
name|i
index|]
operator|+
literal|">= "
operator|+
name|lastScore
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|scores
index|[
name|i
index|]
operator|>=
name|lastScore
argument_list|)
expr_stmt|;
comment|//System.out.println(msg);
name|lastScore
operator|=
name|scores
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
