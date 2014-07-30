begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|analysis
operator|.
name|MockTokenizer
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
name|index
operator|.
name|TermsEnum
operator|.
name|SeekStatus
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
name|AutomatonQuery
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
name|CheckHits
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
name|TestUtil
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
name|TestUtil
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
name|automaton
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestTermsEnum2
specifier|public
class|class
name|TestTermsEnum2
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
DECL|field|terms
specifier|private
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
name|terms
decl_stmt|;
comment|// the terms we put in the index
DECL|field|termsAutomaton
specifier|private
name|Automaton
name|termsAutomaton
decl_stmt|;
comment|// automata of the same
DECL|field|numIterations
name|int
name|numIterations
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
name|numIterations
operator|=
name|atLeast
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|50
argument_list|,
literal|1000
argument_list|)
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
name|Field
name|field
init|=
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|int
name|num
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|s
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
name|termsAutomaton
operator|=
name|Automata
operator|.
name|makeStringUnion
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
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
comment|/** tests a pre-intersected automaton against the original */
DECL|method|testFiniteVersusInfinite
specifier|public
name|void
name|testFiniteVersusInfinite
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|String
name|reg
init|=
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Automaton
name|automaton
init|=
name|Operations
operator|.
name|determinize
argument_list|(
operator|new
name|RegExp
argument_list|(
name|reg
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|matchedTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|t
range|:
name|terms
control|)
block|{
if|if
condition|(
name|Operations
operator|.
name|run
argument_list|(
name|automaton
argument_list|,
name|t
operator|.
name|utf8ToString
argument_list|()
argument_list|)
condition|)
block|{
name|matchedTerms
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|Automaton
name|alternate
init|=
name|Automata
operator|.
name|makeStringUnion
argument_list|(
name|matchedTerms
argument_list|)
decl_stmt|;
comment|//System.out.println("match " + matchedTerms.size() + " " + alternate.getNumberOfStates() + " states, sigma=" + alternate.getStartPoints().length);
comment|//AutomatonTestUtil.minimizeSimple(alternate);
comment|//System.out.println("minmize done");
name|AutomatonQuery
name|a1
init|=
operator|new
name|AutomatonQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|)
argument_list|,
name|automaton
argument_list|)
decl_stmt|;
name|AutomatonQuery
name|a2
init|=
operator|new
name|AutomatonQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|)
argument_list|,
name|alternate
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|origHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|a1
argument_list|,
literal|25
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|ScoreDoc
index|[]
name|newHits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|a2
argument_list|,
literal|25
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|a1
argument_list|,
name|origHits
argument_list|,
name|newHits
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** seeks to every term accepted by some automata */
DECL|method|testSeeking
specifier|public
name|void
name|testSeeking
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|String
name|reg
init|=
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Automaton
name|automaton
init|=
name|Operations
operator|.
name|determinize
argument_list|(
operator|new
name|RegExp
argument_list|(
name|reg
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
argument_list|)
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
name|unsortedTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|unsortedTerms
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BytesRef
name|term
range|:
name|unsortedTerms
control|)
block|{
if|if
condition|(
name|Operations
operator|.
name|run
argument_list|(
name|automaton
argument_list|,
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
condition|)
block|{
comment|// term is accepted
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// seek exact
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// seek ceil
name|assertEquals
argument_list|(
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|te
operator|.
name|seekCeil
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|term
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/** mixes up seek and next for all terms */
DECL|method|testSeekingAndNexting
specifier|public
name|void
name|testSeekingAndNexting
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"field"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
range|:
name|terms
control|)
block|{
name|int
name|c
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|term
argument_list|,
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|te
operator|.
name|seekCeil
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|term
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** tests intersect: TODO start at a random term! */
DECL|method|testIntersect
specifier|public
name|void
name|testIntersect
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|String
name|reg
init|=
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Automaton
name|automaton
init|=
operator|new
name|RegExp
argument_list|(
name|reg
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|CompiledAutomaton
name|ca
init|=
operator|new
name|CompiledAutomaton
argument_list|(
name|automaton
argument_list|,
name|Operations
operator|.
name|isFinite
argument_list|(
name|automaton
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"field"
argument_list|)
operator|.
name|intersect
argument_list|(
name|ca
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Automaton
name|expected
init|=
name|Operations
operator|.
name|determinize
argument_list|(
name|Operations
operator|.
name|intersection
argument_list|(
name|termsAutomaton
argument_list|,
name|automaton
argument_list|)
argument_list|)
decl_stmt|;
name|TreeSet
argument_list|<
name|BytesRef
argument_list|>
name|found
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|found
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Automaton
name|actual
init|=
name|Operations
operator|.
name|determinize
argument_list|(
name|Automata
operator|.
name|makeStringUnion
argument_list|(
name|found
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Operations
operator|.
name|sameLanguage
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
