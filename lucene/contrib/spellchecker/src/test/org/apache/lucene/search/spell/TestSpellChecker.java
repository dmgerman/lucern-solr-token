begin_unit
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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
name|Comparator
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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|index
operator|.
name|CorruptIndexException
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
name|store
operator|.
name|AlreadyClosedException
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
begin_comment
comment|/**  * Spell checker test case  */
end_comment
begin_class
DECL|class|TestSpellChecker
specifier|public
class|class
name|TestSpellChecker
extends|extends
name|LuceneTestCase
block|{
DECL|field|spellChecker
specifier|private
name|SpellCheckerMock
name|spellChecker
decl_stmt|;
DECL|field|userindex
DECL|field|spellindex
specifier|private
name|Directory
name|userindex
decl_stmt|,
name|spellindex
decl_stmt|;
DECL|field|searchers
specifier|private
name|List
argument_list|<
name|IndexSearcher
argument_list|>
name|searchers
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
comment|//create a user index
name|userindex
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
name|userindex
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
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
literal|"field1"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
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
literal|"field2"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
operator|+
literal|1
argument_list|)
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
comment|// + word thousand
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field3"
argument_list|,
literal|"fvei"
operator|+
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
literal|" five"
else|:
literal|""
operator|)
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
comment|// + word thousand
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searchers
operator|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|IndexSearcher
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// create the spellChecker
name|spellindex
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|spellChecker
operator|=
operator|new
name|SpellCheckerMock
argument_list|(
name|spellindex
argument_list|)
expr_stmt|;
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
name|userindex
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|spellChecker
operator|.
name|isClosed
argument_list|()
condition|)
name|spellChecker
operator|.
name|close
argument_list|()
expr_stmt|;
name|spellindex
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
DECL|method|testBuild
specifier|public
name|void
name|testBuild
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|userindex
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|spellChecker
operator|.
name|clearIndex
argument_list|()
expr_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
name|spellChecker
argument_list|,
literal|"field1"
argument_list|)
expr_stmt|;
name|int
name|num_field1
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
name|spellChecker
argument_list|,
literal|"field2"
argument_list|)
expr_stmt|;
name|int
name|num_field2
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|num_field2
argument_list|,
name|num_field1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertLastSearcherOpen
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|checkCommonSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|checkLevenshteinSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|setStringDistance
argument_list|(
operator|new
name|JaroWinklerDistance
argument_list|()
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|setAccuracy
argument_list|(
literal|0.8f
argument_list|)
expr_stmt|;
name|checkCommonSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|checkJaroWinklerSuggestions
argument_list|()
expr_stmt|;
comment|// the accuracy is set to 0.8 by default, but the best result has a score of 0.925
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fvie"
argument_list|,
literal|2
argument_list|,
literal|0.93f
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fvie"
argument_list|,
literal|2
argument_list|,
literal|0.92f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fiv"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|setStringDistance
argument_list|(
operator|new
name|NGramDistance
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|setAccuracy
argument_list|(
literal|0.5f
argument_list|)
expr_stmt|;
name|checkCommonSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|checkNGramSuggestions
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testComparator
specifier|public
name|void
name|testComparator
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
name|userindex
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Directory
name|compIdx
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|SpellChecker
name|compareSP
init|=
operator|new
name|SpellCheckerMock
argument_list|(
name|compIdx
argument_list|,
operator|new
name|LevensteinDistance
argument_list|()
argument_list|,
operator|new
name|SuggestWordFrequencyComparator
argument_list|()
argument_list|)
decl_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
name|compareSP
argument_list|,
literal|"field3"
argument_list|)
expr_stmt|;
name|String
index|[]
name|similar
init|=
name|compareSP
operator|.
name|suggestSimilar
argument_list|(
literal|"fvie"
argument_list|,
literal|2
argument_list|,
name|r
argument_list|,
literal|"field3"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|//five and fvei have the same score, but different frequencies.
name|assertEquals
argument_list|(
literal|"fvei"
argument_list|,
name|similar
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"five"
argument_list|,
name|similar
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|compareSP
operator|.
name|isClosed
argument_list|()
condition|)
name|compareSP
operator|.
name|close
argument_list|()
expr_stmt|;
name|compIdx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkCommonSuggestions
specifier|private
name|void
name|checkCommonSuggestions
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fvie"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"five"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|similar
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertFalse
argument_list|(
name|similar
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't suggest a word for itself
block|}
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fiv"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fives"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fie"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
comment|//  test restraint to a field
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there isn't the term thousand in the field field1
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there is the term thousand in the field field2
block|}
DECL|method|checkLevenshteinSuggestions
specifier|private
name|void
name|checkLevenshteinSuggestions
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
comment|// test small word
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fvie"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"five"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"nine"
argument_list|)
expr_stmt|;
comment|// don't suggest a word for itself
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fiv"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"ive"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|1
index|]
argument_list|,
literal|"nine"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fives"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fie"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|1
index|]
argument_list|,
literal|"nine"
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"fi"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"five"
argument_list|)
expr_stmt|;
comment|// test restraint to a field
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there isn't the term thousand in the field field1
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|"field2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// there is the term thousand in the field field2
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"onety"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"ninety"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|1
index|]
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
try|try
block|{
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"tousand"
argument_list|,
literal|10
argument_list|,
name|r
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"threw an NPE, and it shouldn't have"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkJaroWinklerSuggestions
specifier|private
name|void
name|checkJaroWinklerSuggestions
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"onety"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|1
index|]
argument_list|,
literal|"ninety"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNGramSuggestions
specifier|private
name|void
name|checkNGramSuggestions
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"onety"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|1
index|]
argument_list|,
literal|"ninety"
argument_list|)
expr_stmt|;
block|}
DECL|method|addwords
specifier|private
name|void
name|addwords
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|SpellChecker
name|sc
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|sc
operator|.
name|indexDictionary
argument_list|(
operator|new
name|LuceneDictionary
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
comment|//System.out.println("time to build " + field + ": " + time);
block|}
DECL|method|numdoc
specifier|private
name|int
name|numdoc
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|rs
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|spellindex
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|rs
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|num
operator|!=
literal|0
argument_list|)
expr_stmt|;
comment|//System.out.println("num docs: " + num);
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|num
return|;
block|}
DECL|method|testClose
specifier|public
name|void
name|testClose
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|userindex
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|spellChecker
operator|.
name|clearIndex
argument_list|()
expr_stmt|;
name|String
name|field
init|=
literal|"field1"
decl_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
name|spellChecker
argument_list|,
literal|"field1"
argument_list|)
expr_stmt|;
name|int
name|num_field1
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
name|spellChecker
argument_list|,
literal|"field2"
argument_list|)
expr_stmt|;
name|int
name|num_field2
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|num_field2
argument_list|,
name|num_field1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|checkCommonSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertLastSearcherOpen
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertSearchersClosed
argument_list|()
expr_stmt|;
try|try
block|{
name|spellChecker
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"spellchecker was already closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|checkCommonSuggestions
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"spellchecker was already closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|spellChecker
operator|.
name|clearIndex
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"spellchecker was already closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|spellChecker
operator|.
name|indexDictionary
argument_list|(
operator|new
name|LuceneDictionary
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"spellchecker was already closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|spellChecker
operator|.
name|setSpellIndex
argument_list|(
name|spellindex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"spellchecker was already closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|searchers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchersClosed
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/*    * tests if the internally shared indexsearcher is correctly closed     * when the spellchecker is concurrently accessed and closed.    */
DECL|method|testConcurrentAccess
specifier|public
name|void
name|testConcurrentAccess
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searchers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|userindex
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|spellChecker
operator|.
name|clearIndex
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|searchers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
name|spellChecker
argument_list|,
literal|"field1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|searchers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|num_field1
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|addwords
argument_list|(
name|r
argument_list|,
name|spellChecker
argument_list|,
literal|"field2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|searchers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|num_field2
init|=
name|this
operator|.
name|numdoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|num_field2
argument_list|,
name|num_field1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|int
name|numThreads
init|=
literal|5
operator|+
name|this
operator|.
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numThreads
argument_list|)
decl_stmt|;
name|SpellCheckWorker
index|[]
name|workers
init|=
operator|new
name|SpellCheckWorker
index|[
name|numThreads
index|]
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|SpellCheckWorker
name|spellCheckWorker
init|=
operator|new
name|SpellCheckWorker
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|spellCheckWorker
argument_list|)
expr_stmt|;
name|workers
index|[
name|i
index|]
operator|=
name|spellCheckWorker
expr_stmt|;
block|}
name|int
name|iterations
init|=
literal|5
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|5
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// concurrently reset the spell index
name|spellChecker
operator|.
name|setSpellIndex
argument_list|(
name|this
operator|.
name|spellindex
argument_list|)
expr_stmt|;
comment|// for debug - prints the internal open searchers
comment|// showSearchersOpen();
block|}
name|spellChecker
operator|.
name|close
argument_list|()
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// wait for 60 seconds - usually this is very fast but coverage runs could take quite long
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|60L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
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
name|workers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"worker thread %d failed"
argument_list|,
name|i
argument_list|)
argument_list|,
name|workers
index|[
name|i
index|]
operator|.
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"worker thread %d is still running but should be terminated"
argument_list|,
name|i
argument_list|)
argument_list|,
name|workers
index|[
name|i
index|]
operator|.
name|terminated
argument_list|)
expr_stmt|;
block|}
comment|// 4 searchers more than iterations
comment|// 1. at creation
comment|// 2. clearIndex()
comment|// 2. and 3. during addwords
name|assertEquals
argument_list|(
name|iterations
operator|+
literal|4
argument_list|,
name|searchers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchersClosed
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertLastSearcherOpen
specifier|private
name|void
name|assertLastSearcherOpen
parameter_list|(
name|int
name|numSearchers
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|numSearchers
argument_list|,
name|searchers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
index|[]
name|searcherArray
init|=
name|searchers
operator|.
name|toArray
argument_list|(
operator|new
name|IndexSearcher
index|[
literal|0
index|]
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
name|searcherArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
name|searcherArray
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
literal|"expected last searcher open but was closed"
argument_list|,
name|searcherArray
index|[
name|i
index|]
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"expected closed searcher but was open - Index: "
operator|+
name|i
argument_list|,
name|searcherArray
index|[
name|i
index|]
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertSearchersClosed
specifier|private
name|void
name|assertSearchersClosed
parameter_list|()
block|{
for|for
control|(
name|IndexSearcher
name|searcher
range|:
name|searchers
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// For debug
comment|//  private void showSearchersOpen() {
comment|//    int count = 0;
comment|//    for (IndexSearcher searcher : searchers) {
comment|//      if(searcher.getIndexReader().getRefCount()> 0)
comment|//        ++count;
comment|//    }
comment|//    System.out.println(count);
comment|//  }
DECL|class|SpellCheckWorker
specifier|private
class|class
name|SpellCheckWorker
implements|implements
name|Runnable
block|{
DECL|field|reader
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|terminated
specifier|volatile
name|boolean
name|terminated
init|=
literal|false
decl_stmt|;
DECL|field|failed
specifier|volatile
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
DECL|method|SpellCheckWorker
name|SpellCheckWorker
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|checkCommonSuggestions
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
return|return;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
return|return;
block|}
block|}
block|}
finally|finally
block|{
name|terminated
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|class|SpellCheckerMock
class|class
name|SpellCheckerMock
extends|extends
name|SpellChecker
block|{
DECL|method|SpellCheckerMock
specifier|public
name|SpellCheckerMock
parameter_list|(
name|Directory
name|spellIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|spellIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|SpellCheckerMock
specifier|public
name|SpellCheckerMock
parameter_list|(
name|Directory
name|spellIndex
parameter_list|,
name|StringDistance
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|spellIndex
argument_list|,
name|sd
argument_list|)
expr_stmt|;
block|}
DECL|method|SpellCheckerMock
specifier|public
name|SpellCheckerMock
parameter_list|(
name|Directory
name|spellIndex
parameter_list|,
name|StringDistance
name|sd
parameter_list|,
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|spellIndex
argument_list|,
name|sd
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSearcher
name|IndexSearcher
name|createSearcher
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
name|super
operator|.
name|createSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|TestSpellChecker
operator|.
name|this
operator|.
name|searchers
operator|.
name|add
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
return|return
name|searcher
return|;
block|}
block|}
block|}
end_class
end_unit
