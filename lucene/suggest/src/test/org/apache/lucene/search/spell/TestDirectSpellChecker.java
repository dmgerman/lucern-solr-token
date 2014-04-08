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
begin_class
DECL|class|TestDirectSpellChecker
specifier|public
class|class
name|TestDirectSpellChecker
extends|extends
name|LuceneTestCase
block|{
DECL|method|testInternalLevenshteinDistance
specifier|public
name|void
name|testInternalLevenshteinDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectSpellChecker
name|spellchecker
init|=
operator|new
name|DirectSpellChecker
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|termsToAdd
init|=
block|{
literal|"metanoia"
block|,
literal|"metanoian"
block|,
literal|"metanoiai"
block|,
literal|"metanoias"
block|,
literal|"metanoið"
block|}
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
name|termsToAdd
operator|.
name|length
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
name|newTextField
argument_list|(
literal|"repentance"
argument_list|,
name|termsToAdd
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|String
name|misspelled
init|=
literal|"metanoix"
decl_stmt|;
name|SuggestWord
index|[]
name|similar
init|=
name|spellchecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"repentance"
argument_list|,
name|misspelled
argument_list|)
argument_list|,
literal|4
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|similar
operator|.
name|length
operator|==
literal|4
argument_list|)
expr_stmt|;
name|StringDistance
name|sd
init|=
name|spellchecker
operator|.
name|getDistance
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|sd
operator|instanceof
name|LuceneLevenshteinDistance
argument_list|)
expr_stmt|;
for|for
control|(
name|SuggestWord
name|word
range|:
name|similar
control|)
block|{
name|assertTrue
argument_list|(
name|word
operator|.
name|score
operator|==
name|sd
operator|.
name|getDistance
argument_list|(
name|word
operator|.
name|string
argument_list|,
name|misspelled
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|word
operator|.
name|score
operator|==
name|sd
operator|.
name|getDistance
argument_list|(
name|misspelled
argument_list|,
name|word
operator|.
name|string
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSimpleExamples
specifier|public
name|void
name|testSimpleExamples
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectSpellChecker
name|spellChecker
init|=
operator|new
name|DirectSpellChecker
argument_list|()
decl_stmt|;
name|spellChecker
operator|.
name|setMinQueryLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
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
literal|20
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
name|newTextField
argument_list|(
literal|"numbers"
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
name|NO
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SuggestWord
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"fvie"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
literal|"five"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"five"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
name|string
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
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"fvie"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
literal|"five"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"fiv"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
literal|"five"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"fives"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
literal|"five"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
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
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"fie"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"five"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
argument_list|)
expr_stmt|;
comment|// add some more documents
for|for
control|(
name|int
name|i
init|=
literal|1000
init|;
name|i
operator|<
literal|1100
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
name|newTextField
argument_list|(
literal|"numbers"
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
name|NO
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
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
comment|// look ma, no spellcheck index rebuild
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"tousand"
argument_list|)
argument_list|,
literal|10
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
literal|"thousand"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testOptions
specifier|public
name|void
name|testOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
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
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text"
argument_list|,
literal|"foobar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text"
argument_list|,
literal|"foobar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text"
argument_list|,
literal|"foobaz"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text"
argument_list|,
literal|"fobar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|DirectSpellChecker
name|spellChecker
init|=
operator|new
name|DirectSpellChecker
argument_list|()
decl_stmt|;
name|spellChecker
operator|.
name|setMaxQueryFrequency
argument_list|(
literal|0F
argument_list|)
expr_stmt|;
name|SuggestWord
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"fobar"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|spellChecker
operator|=
operator|new
name|DirectSpellChecker
argument_list|()
expr_stmt|;
comment|// reset defaults
name|spellChecker
operator|.
name|setMinQueryLength
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"foba"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
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
name|spellChecker
operator|=
operator|new
name|DirectSpellChecker
argument_list|()
expr_stmt|;
comment|// reset defaults
name|spellChecker
operator|.
name|setMaxEdits
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"foobazzz"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
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
name|spellChecker
operator|=
operator|new
name|DirectSpellChecker
argument_list|()
expr_stmt|;
comment|// reset defaults
name|spellChecker
operator|.
name|setAccuracy
argument_list|(
literal|0.9F
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"foobazzz"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
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
name|spellChecker
operator|=
operator|new
name|DirectSpellChecker
argument_list|()
expr_stmt|;
comment|// reset defaults
name|spellChecker
operator|.
name|setMinPrefix
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"roobaz"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
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
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"roobaz"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
argument_list|)
expr_stmt|;
name|spellChecker
operator|=
operator|new
name|DirectSpellChecker
argument_list|()
expr_stmt|;
comment|// reset defaults
name|spellChecker
operator|.
name|setMinPrefix
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"roobaz"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
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
name|spellChecker
operator|=
operator|new
name|DirectSpellChecker
argument_list|()
expr_stmt|;
comment|// reset defaults
name|spellChecker
operator|.
name|setMaxEdits
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|similar
operator|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"fobar"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_ALWAYS
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
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testBogusField
specifier|public
name|void
name|testBogusField
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectSpellChecker
name|spellChecker
init|=
operator|new
name|DirectSpellChecker
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
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
literal|20
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
name|newTextField
argument_list|(
literal|"numbers"
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
name|NO
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SuggestWord
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"bogusFieldBogusField"
argument_list|,
literal|"fvie"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// simple test that transpositions work, we suggest five for fvie with ed=1
DECL|method|testTransposition
specifier|public
name|void
name|testTransposition
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectSpellChecker
name|spellChecker
init|=
operator|new
name|DirectSpellChecker
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
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
literal|20
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
name|newTextField
argument_list|(
literal|"numbers"
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
name|NO
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SuggestWord
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"fvie"
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
literal|"five"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// simple test that transpositions work, we suggest seventeen for seevntene with ed=2
DECL|method|testTransposition2
specifier|public
name|void
name|testTransposition2
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectSpellChecker
name|spellChecker
init|=
operator|new
name|DirectSpellChecker
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
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
literal|20
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
name|newTextField
argument_list|(
literal|"numbers"
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
name|NO
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SuggestWord
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"seevntene"
argument_list|)
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
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
literal|"seventeen"
argument_list|,
name|similar
index|[
literal|0
index|]
operator|.
name|string
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
