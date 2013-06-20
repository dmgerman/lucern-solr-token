begin_unit
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DirectoryReader
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
name|QueryUtils
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
name|search
operator|.
name|TopDocs
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
comment|/**  * Test FieldScoreQuery search.  *<p>  * Tests here create an index with a few documents, each having  * an int value indexed  field and a float value indexed field.  * The values of these fields are later used for scoring.  *<p>  * The rank tests use Hits to verify that docs are ordered (by score) as expected.  *<p>  * The exact score tests use TopDocs top to verify the exact score.    */
end_comment
begin_class
DECL|class|TestFieldScoreQuery
specifier|public
class|class
name|TestFieldScoreQuery
extends|extends
name|FunctionTestSetup
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.INT returns docs in expected order. */
annotation|@
name|Test
DECL|method|testRankInt
specifier|public
name|void
name|testRankInt
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRank
argument_list|(
name|INT_VALUESOURCE
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.FLOAT returns docs in expected order. */
annotation|@
name|Test
DECL|method|testRankFloat
specifier|public
name|void
name|testRankFloat
parameter_list|()
throws|throws
name|Exception
block|{
comment|// INT field can be parsed as float
name|doTestRank
argument_list|(
name|INT_AS_FLOAT_VALUESOURCE
argument_list|)
expr_stmt|;
comment|// same values, but in flot format
name|doTestRank
argument_list|(
name|FLOAT_VALUESOURCE
argument_list|)
expr_stmt|;
block|}
comment|// Test that FieldScoreQuery returns docs in expected order.
DECL|method|doTestRank
specifier|private
name|void
name|doTestRank
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|)
throws|throws
name|Exception
block|{
name|FunctionQuery
name|functionQuery
init|=
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
decl_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"test: "
operator|+
name|functionQuery
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|functionQuery
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|h
init|=
name|s
operator|.
name|search
argument_list|(
name|functionQuery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|h
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|prevID
init|=
literal|"ID"
operator|+
operator|(
name|N_DOCS
operator|+
literal|1
operator|)
decl_stmt|;
comment|// greater than all ids of docs in this test
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|h
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|resID
init|=
name|s
operator|.
name|doc
argument_list|(
name|h
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
decl_stmt|;
name|log
argument_list|(
name|i
operator|+
literal|".   score="
operator|+
name|h
index|[
name|i
index|]
operator|.
name|score
operator|+
literal|"  -  "
operator|+
name|resID
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|functionQuery
argument_list|,
name|h
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"res id "
operator|+
name|resID
operator|+
literal|" should be< prev res id "
operator|+
name|prevID
argument_list|,
name|resID
operator|.
name|compareTo
argument_list|(
name|prevID
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|prevID
operator|=
name|resID
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.INT returns the expected scores. */
annotation|@
name|Test
DECL|method|testExactScoreInt
specifier|public
name|void
name|testExactScoreInt
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestExactScore
argument_list|(
name|INT_VALUESOURCE
argument_list|)
expr_stmt|;
block|}
comment|/** Test that FieldScoreQuery of Type.FLOAT returns the expected scores. */
annotation|@
name|Test
DECL|method|testExactScoreFloat
specifier|public
name|void
name|testExactScoreFloat
parameter_list|()
throws|throws
name|Exception
block|{
comment|// INT field can be parsed as float
name|doTestExactScore
argument_list|(
name|INT_AS_FLOAT_VALUESOURCE
argument_list|)
expr_stmt|;
comment|// same values, but in flot format
name|doTestExactScore
argument_list|(
name|FLOAT_VALUESOURCE
argument_list|)
expr_stmt|;
block|}
comment|// Test that FieldScoreQuery returns docs with expected score.
DECL|method|doTestExactScore
specifier|private
name|void
name|doTestExactScore
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|)
throws|throws
name|Exception
block|{
name|FunctionQuery
name|functionQuery
init|=
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
decl_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|s
operator|.
name|search
argument_list|(
name|functionQuery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"All docs should be matched!"
argument_list|,
name|N_DOCS
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ScoreDoc
name|sd
index|[]
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|aSd
range|:
name|sd
control|)
block|{
name|float
name|score
init|=
name|aSd
operator|.
name|score
decl_stmt|;
name|log
argument_list|(
name|s
operator|.
name|explain
argument_list|(
name|functionQuery
argument_list|,
name|aSd
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|document
argument_list|(
name|aSd
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
decl_stmt|;
name|float
name|expectedScore
init|=
name|expectedFieldScore
argument_list|(
name|id
argument_list|)
decl_stmt|;
comment|// "ID7" --> 7.0
name|assertEquals
argument_list|(
literal|"score of "
operator|+
name|id
operator|+
literal|" shuould be "
operator|+
name|expectedScore
operator|+
literal|" != "
operator|+
name|score
argument_list|,
name|expectedScore
argument_list|,
name|score
argument_list|,
name|TEST_SCORE_TOLERANCE_DELTA
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
