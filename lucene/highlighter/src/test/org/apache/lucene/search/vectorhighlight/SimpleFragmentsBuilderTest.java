begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|highlight
operator|.
name|SimpleHTMLEncoder
import|;
end_import
begin_class
DECL|class|SimpleFragmentsBuilderTest
specifier|public
class|class
name|SimpleFragmentsBuilderTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|test1TermIndex
specifier|public
name|void
name|test1TermIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<b>a</b>"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
comment|// change tags
name|sfb
operator|=
operator|new
name|SimpleFragmentsBuilder
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"["
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"]"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[a]"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Frags
specifier|public
name|void
name|test2Frags
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
literal|"a b b b b b b b b b b b a b a b"
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|f
init|=
name|sfb
operator|.
name|createFragments
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// 3 snippets requested, but should be 2
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|f
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>a</b> b b b b b b b b b b"
argument_list|,
name|f
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b b<b>a</b> b<b>a</b> b"
argument_list|,
name|f
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|test3Frags
specifier|public
name|void
name|test3Frags
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|booleanQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"a"
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
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"c"
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
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
name|booleanQuery
argument_list|,
literal|"a b b b b b b b b b b b a b a b b b b b c a a b b"
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|f
init|=
name|sfb
operator|.
name|createFragments
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|f
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>a</b> b b b b b b b b b b"
argument_list|,
name|f
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b b<b>a</b> b<b>a</b> b b b b b c"
argument_list|,
name|f
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>c</b><b>a</b><b>a</b> b b"
argument_list|,
name|f
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testTagsAndEncoder
specifier|public
name|void
name|testTagsAndEncoder
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
literal|"<h1> a</h1>"
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|preTags
init|=
block|{
literal|"["
block|}
decl_stmt|;
name|String
index|[]
name|postTags
init|=
block|{
literal|"]"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|"&lt;h1&gt; [a]&lt;/h1&gt;"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
operator|new
name|SimpleHTMLEncoder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ffl
specifier|private
name|FieldFragList
name|ffl
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|indexValue
parameter_list|)
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
name|indexValue
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimpleFragListBuilder
argument_list|()
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|20
argument_list|)
return|;
block|}
DECL|method|test1PhraseShortMV
specifier|public
name|void
name|test1PhraseShortMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexShortMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"d"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a b c<b>d</b> e"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseLongMV
specifier|public
name|void
name|test1PhraseLongMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"search"
argument_list|,
literal|"engines"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The most<b>search engines</b> use only one of these methods. Even the<b>search engines</b> that says they can use the"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseLongMVB
specifier|public
name|void
name|test1PhraseLongMVB
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMVB
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"sp"
argument_list|,
literal|"pe"
argument_list|,
literal|"ee"
argument_list|,
literal|"ed"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// "speed" -(2gram)-> "sp","pe","ee","ed"
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"processing<b>speed</b>, the"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnstoredField
specifier|public
name|void
name|testUnstoredField
parameter_list|()
throws|throws
name|Exception
block|{
name|makeUnstoredIndex
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"aaa"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeUnstoredIndex
specifier|protected
name|void
name|makeUnstoredIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzerW
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|F
argument_list|,
literal|"aaa"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
comment|//doc.add( new Field( F, "aaa", Store.NO, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS ) );
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|test1StrMV
specifier|public
name|void
name|test1StrMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexStrMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"defg"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|sfb
operator|.
name|setMultiValuedSeparator
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc/<b>defg</b>/hijkl"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMVSeparator
specifier|public
name|void
name|testMVSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexShortMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"d"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|sfb
operator|.
name|setMultiValuedSeparator
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"//a b c//<b>d</b> e"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
