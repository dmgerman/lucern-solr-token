begin_unit
begin_package
DECL|package|org.apache.lucene.search.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|regex
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
name|store
operator|.
name|RAMDirectory
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
name|analysis
operator|.
name|SimpleAnalyzer
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
name|index
operator|.
name|TermEnum
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanQuery
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
DECL|class|TestRegexQuery
specifier|public
class|class
name|TestRegexQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|FN
specifier|private
specifier|final
name|String
name|FN
init|=
literal|"field"
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
try|try
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
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
operator|new
name|Field
argument_list|(
name|FN
argument_list|,
literal|"the quick brown fox jumps over the lazy dog"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|newTerm
specifier|private
name|Term
name|newTerm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|FN
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|regexQueryNrHits
specifier|private
name|int
name|regexQueryNrHits
parameter_list|(
name|String
name|regex
parameter_list|,
name|RegexCapabilities
name|capability
parameter_list|)
throws|throws
name|Exception
block|{
name|RegexQuery
name|query
init|=
operator|new
name|RegexQuery
argument_list|(
name|newTerm
argument_list|(
name|regex
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|capability
operator|!=
literal|null
condition|)
name|query
operator|.
name|setRegexImplementation
argument_list|(
name|capability
argument_list|)
expr_stmt|;
return|return
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
return|;
block|}
DECL|method|spanRegexQueryNrHits
specifier|private
name|int
name|spanRegexQueryNrHits
parameter_list|(
name|String
name|regex1
parameter_list|,
name|String
name|regex2
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|ordered
parameter_list|)
throws|throws
name|Exception
block|{
name|SpanRegexQuery
name|srq1
init|=
operator|new
name|SpanRegexQuery
argument_list|(
name|newTerm
argument_list|(
name|regex1
argument_list|)
argument_list|)
decl_stmt|;
name|SpanRegexQuery
name|srq2
init|=
operator|new
name|SpanRegexQuery
argument_list|(
name|newTerm
argument_list|(
name|regex2
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNearQuery
name|query
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|srq1
block|,
name|srq2
block|}
argument_list|,
name|slop
argument_list|,
name|ordered
argument_list|)
decl_stmt|;
return|return
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
return|;
block|}
DECL|method|testMatchAll
specifier|public
name|void
name|testMatchAll
parameter_list|()
throws|throws
name|Exception
block|{
name|TermEnum
name|terms
init|=
operator|new
name|RegexQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FN
argument_list|,
literal|"jum."
argument_list|)
argument_list|)
operator|.
name|getEnum
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
comment|// no term should match
name|assertNull
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|terms
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegex1
specifier|public
name|void
name|testRegex1
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"^q.[aeiou]c.*$"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegex2
specifier|public
name|void
name|testRegex2
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"^.[aeiou]c.*$"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegex3
specifier|public
name|void
name|testRegex3
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"^q.[aeiou]c$"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanRegex1
specifier|public
name|void
name|testSpanRegex1
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|spanRegexQueryNrHits
argument_list|(
literal|"^q.[aeiou]c.*$"
argument_list|,
literal|"dog"
argument_list|,
literal|6
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanRegex2
specifier|public
name|void
name|testSpanRegex2
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|spanRegexQueryNrHits
argument_list|(
literal|"^q.[aeiou]c.*$"
argument_list|,
literal|"dog"
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|RegexQuery
name|query1
init|=
operator|new
name|RegexQuery
argument_list|(
name|newTerm
argument_list|(
literal|"foo.*"
argument_list|)
argument_list|)
decl_stmt|;
name|query1
operator|.
name|setRegexImplementation
argument_list|(
operator|new
name|JakartaRegexpCapabilities
argument_list|()
argument_list|)
expr_stmt|;
name|RegexQuery
name|query2
init|=
operator|new
name|RegexQuery
argument_list|(
name|newTerm
argument_list|(
literal|"foo.*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|query1
operator|.
name|equals
argument_list|(
name|query2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJakartaCaseSensativeFail
specifier|public
name|void
name|testJakartaCaseSensativeFail
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"^.*DOG.*$"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJavaUtilCaseSensativeFail
specifier|public
name|void
name|testJavaUtilCaseSensativeFail
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"^.*DOG.*$"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJakartaCaseInsensative
specifier|public
name|void
name|testJakartaCaseInsensative
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"^.*DOG.*$"
argument_list|,
operator|new
name|JakartaRegexpCapabilities
argument_list|(
name|JakartaRegexpCapabilities
operator|.
name|FLAG_MATCH_CASEINDEPENDENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJavaUtilCaseInsensative
specifier|public
name|void
name|testJavaUtilCaseInsensative
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"^.*DOG.*$"
argument_list|,
operator|new
name|JavaUtilRegexCapabilities
argument_list|(
name|JavaUtilRegexCapabilities
operator|.
name|FLAG_CASE_INSENSITIVE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
