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
name|index
operator|.
name|IndexWriterConfig
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
name|store
operator|.
name|MockRAMDirectory
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
name|util
operator|.
name|LuceneTestCase
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_comment
comment|/**  * This class tests the MultiPhraseQuery class.  *  *  */
end_comment
begin_class
DECL|class|TestMultiPhraseQuery
specifier|public
class|class
name|TestMultiPhraseQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestMultiPhraseQuery
specifier|public
name|TestMultiPhraseQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|testPhrasePrefix
specifier|public
name|void
name|testPhrasePrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|MockRAMDirectory
name|indexStore
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|add
argument_list|(
literal|"blueberry pie"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry strudel"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry pizza"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry chewing gum"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"bluebird pizza"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"bluebird foobar pizza"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"piccadilly circus"
argument_list|,
name|writer
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
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// search for "blueberry pi*":
name|MultiPhraseQuery
name|query1
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
comment|// search for "strawberry pi*":
name|MultiPhraseQuery
name|query2
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|query1
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry"
argument_list|)
argument_list|)
expr_stmt|;
name|query2
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"strawberry"
argument_list|)
argument_list|)
expr_stmt|;
name|LinkedList
argument_list|<
name|Term
argument_list|>
name|termsWithPrefix
init|=
operator|new
name|LinkedList
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// this TermEnum gives "piccadilly", "pie" and "pizza".
name|String
name|prefix
init|=
literal|"pi"
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|ir
argument_list|)
operator|.
name|terms
argument_list|(
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|te
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
do|do
block|{
name|String
name|s
init|=
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|termsWithPrefix
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
do|;
name|query1
operator|.
name|add
argument_list|(
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"body:\"blueberry (piccadilly pie pizza)\""
argument_list|,
name|query1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|query2
operator|.
name|add
argument_list|(
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"body:\"strawberry (piccadilly pie pizza)\""
argument_list|,
name|query2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|result
decl_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
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
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
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
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// search for "blue* pizza":
name|MultiPhraseQuery
name|query3
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|termsWithPrefix
operator|.
name|clear
argument_list|()
expr_stmt|;
name|prefix
operator|=
literal|"blue"
expr_stmt|;
name|te
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
do|do
block|{
if|if
condition|(
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|termsWithPrefix
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
do|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|query3
operator|.
name|add
argument_list|(
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|query3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"pizza"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query3
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
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// blueberry pizza, bluebird pizza
name|assertEquals
argument_list|(
literal|"body:\"(blueberry bluebird) pizza\""
argument_list|,
name|query3
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test slop:
name|query3
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query3
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
literal|3
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// blueberry pizza, bluebird pizza, bluebird foobar pizza
name|MultiPhraseQuery
name|query4
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
try|try
block|{
name|query4
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|query4
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// okay, all terms must belong to the same field
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|String
name|s
parameter_list|,
name|IndexWriter
name|writer
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
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
name|s
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
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanQueryContainingSingleTermPrefixQuery
specifier|public
name|void
name|testBooleanQueryContainingSingleTermPrefixQuery
parameter_list|()
throws|throws
name|IOException
block|{
comment|// this tests against bug 33161 (now fixed)
comment|// In order to cause the bug, the outer query must have more than one term
comment|// and all terms required.
comment|// The contained PhraseMultiQuery must contain exactly one term array.
name|MockRAMDirectory
name|indexStore
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|add
argument_list|(
literal|"blueberry pie"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blueberry chewing gum"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blue raspberry pie"
argument_list|,
name|writer
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
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// This query will be equivalent to +body:pie +body:"blue*"
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"pie"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|MultiPhraseQuery
name|trouble
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|trouble
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"blue"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|trouble
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|// exception will be thrown here without fix
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
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
literal|"Wrong number of hits"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPhrasePrefixWithBooleanQuery
specifier|public
name|void
name|testPhrasePrefixWithBooleanQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|MockRAMDirectory
name|indexStore
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|add
argument_list|(
literal|"This is a test"
argument_list|,
literal|"object"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"a note"
argument_list|,
literal|"note"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// This query will be equivalent to +type:note +body:"a t*"
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"note"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|MultiPhraseQuery
name|trouble
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|trouble
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|trouble
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"test"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"this"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|trouble
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|// exception will be thrown here without fix for #35626:
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
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
literal|"Wrong number of hits"
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNoDocs
specifier|public
name|void
name|testNoDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRAMDirectory
name|indexStore
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
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
name|add
argument_list|(
literal|"a note"
argument_list|,
literal|"note"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"nope"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"nope"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of hits"
argument_list|,
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testHashCodeAndEquals
specifier|public
name|void
name|testHashCodeAndEquals
parameter_list|()
block|{
name|MultiPhraseQuery
name|query1
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|MultiPhraseQuery
name|query2
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|query1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|query2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query1
argument_list|,
name|query2
argument_list|)
expr_stmt|;
name|Term
name|term1
init|=
operator|new
name|Term
argument_list|(
literal|"someField"
argument_list|,
literal|"someText"
argument_list|)
decl_stmt|;
name|query1
operator|.
name|add
argument_list|(
name|term1
argument_list|)
expr_stmt|;
name|query2
operator|.
name|add
argument_list|(
name|term1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|query2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query1
argument_list|,
name|query2
argument_list|)
expr_stmt|;
name|Term
name|term2
init|=
operator|new
name|Term
argument_list|(
literal|"someField"
argument_list|,
literal|"someMoreText"
argument_list|)
decl_stmt|;
name|query1
operator|.
name|add
argument_list|(
name|term2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|query1
operator|.
name|hashCode
argument_list|()
operator|==
name|query2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
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
name|query2
operator|.
name|add
argument_list|(
name|term2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|query2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query1
argument_list|,
name|query2
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|type
parameter_list|,
name|IndexWriter
name|writer
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
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
name|s
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
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"type"
argument_list|,
name|type
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
name|NOT_ANALYZED
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
block|}
block|}
end_class
end_unit
