begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.classification.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|utils
package|;
end_package
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
name|Analyzer
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
name|Field
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
name|FieldType
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
name|MultiFields
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
name|RandomIndexWriter
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
name|MatchAllDocsQuery
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
name|ScoreDoc
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  * Testcase for {@link org.apache.lucene.classification.utils.DocToDoubleVectorUtils}  */
end_comment
begin_class
DECL|class|DocToDoubleVectorUtilsTest
specifier|public
class|class
name|DocToDoubleVectorUtilsTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|index
specifier|private
name|IndexReader
name|index
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|indexWriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Document
name|doc
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|" "
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|" "
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|index
operator|=
name|indexWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
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
annotation|@
name|Test
DECL|method|testDenseFreqDoubleArrayConversion
specifier|public
name|void
name|testDenseFreqDoubleArrayConversion
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|scoreDocs
control|)
block|{
name|Terms
name|docTerms
init|=
name|index
operator|.
name|getTermVector
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|Double
index|[]
name|vector
init|=
name|DocToDoubleVectorUtils
operator|.
name|toDenseLocalFreqDoubleArray
argument_list|(
name|docTerms
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSparseFreqDoubleArrayConversion
specifier|public
name|void
name|testSparseFreqDoubleArrayConversion
parameter_list|()
throws|throws
name|Exception
block|{
name|Terms
name|fieldTerms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|index
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldTerms
operator|!=
literal|null
operator|&&
name|fieldTerms
operator|.
name|size
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|scoreDocs
control|)
block|{
name|Terms
name|docTerms
init|=
name|index
operator|.
name|getTermVector
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|,
literal|"text"
argument_list|)
decl_stmt|;
name|Double
index|[]
name|vector
init|=
name|DocToDoubleVectorUtils
operator|.
name|toSparseLocalFreqDoubleArray
argument_list|(
name|docTerms
argument_list|,
name|fieldTerms
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
