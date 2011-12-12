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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
begin_class
DECL|class|TestTermRangeQuery
specifier|public
class|class
name|TestTermRangeQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|docCount
specifier|private
name|int
name|docCount
init|=
literal|0
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
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
DECL|method|testExclusive
specifier|public
name|void
name|testExclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
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
literal|"A,B,C,D, only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,D, only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C added, still only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testInclusive
specifier|public
name|void
name|testInclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
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
literal|"A,B,C,D - A,B,C in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,D - A and B in range"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C added - A, B, C in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAllDocs
specifier|public
name|void
name|testAllDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TermRangeQuery
name|query
init|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
operator|instanceof
name|TermRangeTermsEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
operator|instanceof
name|TermRangeTermsEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|query
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
operator|instanceof
name|TermRangeTermsEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// and now anothe one
name|query
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"B"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|query
operator|.
name|getTermsEnum
argument_list|(
name|terms
argument_list|)
operator|instanceof
name|TermRangeTermsEnum
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** This test should not be here, but it tests the fuzzy query rewrite mode (TOP_TERMS_SCORING_BOOLEAN_REWRITE)    * with constant score and checks, that only the lower end of terms is put into the range */
DECL|method|testTopTermsRewrite
specifier|public
name|void
name|testTopTermsRewrite
parameter_list|()
throws|throws
name|Exception
block|{
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|,
literal|"E"
block|,
literal|"F"
block|,
literal|"G"
block|,
literal|"H"
block|,
literal|"I"
block|,
literal|"J"
block|,
literal|"K"
block|}
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TermRangeQuery
name|query
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"B"
argument_list|,
literal|"J"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|checkBooleanTerms
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|,
literal|"B"
argument_list|,
literal|"C"
argument_list|,
literal|"D"
argument_list|,
literal|"E"
argument_list|,
literal|"F"
argument_list|,
literal|"G"
argument_list|,
literal|"H"
argument_list|,
literal|"I"
argument_list|,
literal|"J"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|savedClauseCount
init|=
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
decl_stmt|;
try|try
block|{
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|checkBooleanTerms
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|,
literal|"B"
argument_list|,
literal|"C"
argument_list|,
literal|"D"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
name|savedClauseCount
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkBooleanTerms
specifier|private
name|void
name|checkBooleanTerms
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TermRangeQuery
name|query
parameter_list|,
name|String
modifier|...
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|query
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsScoringBooleanQueryRewrite
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|searcher
operator|.
name|rewrite
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allowedTerms
init|=
name|asSet
argument_list|(
name|terms
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|allowedTerms
operator|.
name|size
argument_list|()
argument_list|,
name|bq
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|bq
operator|.
name|clauses
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|c
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
specifier|final
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|c
operator|.
name|getQuery
argument_list|()
decl_stmt|;
specifier|final
name|String
name|term
init|=
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"invalid term: "
operator|+
name|term
argument_list|,
name|allowedTerms
operator|.
name|contains
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|allowedTerms
operator|.
name|remove
argument_list|(
name|term
argument_list|)
expr_stmt|;
comment|// remove to fail on double terms
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|allowedTerms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqualsHashcode
specifier|public
name|void
name|testEqualsHashcode
parameter_list|()
block|{
name|Query
name|query
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|Query
name|other
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|other
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"query equals itself is true"
argument_list|,
name|query
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries are equal"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode must return same value when equals is true"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different boost queries are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"notcontent"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different fields are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"X"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different lower terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"Z"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different upper terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries with null lowerterms are equal()"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode must return same value when equals is true"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries with null upperterms are equal()"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode returns same value"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"queries with different upper and lower terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|other
operator|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"queries with different inclusive are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|SingleCharAnalyzer
specifier|private
specifier|static
class|class
name|SingleCharAnalyzer
extends|extends
name|Analyzer
block|{
DECL|class|SingleCharTokenizer
specifier|private
specifier|static
class|class
name|SingleCharTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|buffer
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1
index|]
decl_stmt|;
DECL|field|done
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|termAtt
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|method|SingleCharTokenizer
specifier|public
name|SingleCharTokenizer
parameter_list|(
name|Reader
name|r
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|done
condition|)
return|return
literal|false
return|;
else|else
block|{
name|int
name|count
init|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
specifier|final
name|void
name|reset
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|done
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|SingleCharTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|initializeIndex
specifier|private
name|void
name|initializeIndex
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|initializeIndex
argument_list|(
name|values
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeIndex
specifier|private
name|void
name|initializeIndex
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|insertDoc
argument_list|(
name|writer
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// shouldnt create an analyzer for every doc?
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
argument_list|)
decl_stmt|;
name|insertDoc
argument_list|(
name|writer
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|insertDoc
specifier|private
name|void
name|insertDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|"id"
operator|+
name|docCount
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"content"
argument_list|,
name|content
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
block|}
comment|// LUCENE-38
DECL|method|testExclusiveLowerNull
specifier|public
name|void
name|testExclusiveLowerNull
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|SingleCharAnalyzer
argument_list|()
decl_stmt|;
comment|//http://issues.apache.org/jira/browse/LUCENE-38
name|Query
name|query
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|numHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,C,D => A, B&<empty string> are in range"
argument_list|,
literal|3
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert:
comment|//assertEquals("A,B,<empty string>,C,D => A, B&<empty string> are in range", 2, hits.length());
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,D => A, B&<empty string> are in range"
argument_list|,
literal|3
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert:
comment|//assertEquals("A,B,<empty string>,D => A, B&<empty string> are in range", 2, hits.length());
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"C added, still A, B&<empty string> are in range"
argument_list|,
literal|3
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("C added, still A, B&<empty string> are in range", 2, hits.length());
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-38
DECL|method|testInclusiveLowerNull
specifier|public
name|void
name|testInclusiveLowerNull
parameter_list|()
throws|throws
name|Exception
block|{
comment|//http://issues.apache.org/jira/browse/LUCENE-38
name|Analyzer
name|analyzer
init|=
operator|new
name|SingleCharAnalyzer
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|numHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,C,D => A,B,<empty string>,C in range"
argument_list|,
literal|4
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("A,B,<empty string>,C,D => A,B,<empty string>,C in range", 3, hits.length());
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,D - A, B and<empty string> in range"
argument_list|,
literal|3
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("A,B,<empty string>,D => A, B and<empty string> in range", 2, hits.length());
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"C added => A,B,<empty string>,C in range"
argument_list|,
literal|4
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("C added => A,B,<empty string>,C in range", 3, hits.length());
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
