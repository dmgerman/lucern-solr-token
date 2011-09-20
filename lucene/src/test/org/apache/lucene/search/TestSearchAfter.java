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
name|English
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
name|util
operator|.
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Tests IndexSearcher's searchAfter() method  */
end_comment
begin_class
DECL|class|TestSearchAfter
specifier|public
class|class
name|TestSearchAfter
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|200
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"english"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"oddeven"
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
condition|?
literal|"even"
else|:
literal|"odd"
argument_list|,
name|StringField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
DECL|method|testQueries
specifier|public
name|void
name|testQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|Filter
name|odd
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"oddeven"
argument_list|,
literal|"odd"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|odd
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
argument_list|,
name|odd
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"oddeven"
argument_list|,
literal|"even"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertQuery
name|void
name|assertQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
name|TopDocs
name|all
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|searcher
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|pageSize
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|searcher
operator|.
name|maxDoc
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
name|int
name|pageStart
init|=
literal|0
decl_stmt|;
name|ScoreDoc
name|lastBottom
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|pageStart
operator|<
name|all
operator|.
name|totalHits
condition|)
block|{
name|TopDocs
name|paged
init|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|lastBottom
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|pageSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|paged
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|assertPage
argument_list|(
name|pageStart
argument_list|,
name|all
argument_list|,
name|paged
argument_list|)
expr_stmt|;
name|pageStart
operator|+=
name|paged
operator|.
name|scoreDocs
operator|.
name|length
expr_stmt|;
name|lastBottom
operator|=
name|paged
operator|.
name|scoreDocs
index|[
name|paged
operator|.
name|scoreDocs
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|all
operator|.
name|scoreDocs
operator|.
name|length
argument_list|,
name|pageStart
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPage
specifier|static
name|void
name|assertPage
parameter_list|(
name|int
name|pageStart
parameter_list|,
name|TopDocs
name|all
parameter_list|,
name|TopDocs
name|paged
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|all
operator|.
name|totalHits
argument_list|,
name|paged
operator|.
name|totalHits
argument_list|)
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
name|paged
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|all
operator|.
name|scoreDocs
index|[
name|pageStart
operator|+
name|i
index|]
operator|.
name|doc
argument_list|,
name|paged
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|all
operator|.
name|scoreDocs
index|[
name|pageStart
operator|+
name|i
index|]
operator|.
name|score
argument_list|,
name|paged
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
