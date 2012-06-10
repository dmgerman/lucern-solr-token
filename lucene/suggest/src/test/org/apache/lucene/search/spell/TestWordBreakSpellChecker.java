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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|search
operator|.
name|spell
operator|.
name|WordBreakSpellChecker
operator|.
name|BreakSuggestionSortMethod
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
DECL|class|TestWordBreakSpellChecker
specifier|public
class|class
name|TestWordBreakSpellChecker
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
init|=
literal|null
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
name|WHITESPACE
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
literal|900
init|;
name|i
operator|<
literal|1112
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
name|String
name|num
init|=
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"[-]"
argument_list|,
literal|" "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"[,]"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"numbers"
argument_list|,
name|num
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
literal|"thou hast sand betwixt thy toes"
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
literal|"hundredeight eightyeight yeight"
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
literal|"tres y cinco"
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
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
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
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testCombiningWords
specifier|public
name|void
name|testCombiningWords
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|ir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|WordBreakSpellChecker
name|wbsp
init|=
operator|new
name|WordBreakSpellChecker
argument_list|()
decl_stmt|;
block|{
name|Term
index|[]
name|terms
init|=
block|{
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"one"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"hun"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"dred"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"eight"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"y"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"eight"
argument_list|)
block|,         }
decl_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMaxCombineWordLength
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|CombineSuggestion
index|[]
name|cs
init|=
name|wbsp
operator|.
name|suggestWordCombinations
argument_list|(
name|terms
argument_list|,
literal|10
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_ALWAYS
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
operator|.
name|length
operator|==
literal|5
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|originalTermIndexes
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|originalTermIndexes
index|[
literal|1
index|]
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|suggestion
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"hundred"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|suggestion
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|originalTermIndexes
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
operator|==
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|originalTermIndexes
index|[
literal|1
index|]
operator|==
literal|4
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|suggestion
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"eighty"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|suggestion
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|2
index|]
operator|.
name|originalTermIndexes
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|2
index|]
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
operator|==
literal|4
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|2
index|]
operator|.
name|originalTermIndexes
index|[
literal|1
index|]
operator|==
literal|5
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|2
index|]
operator|.
name|suggestion
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"yeight"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|2
index|]
operator|.
name|suggestion
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
name|i
index|]
operator|.
name|originalTermIndexes
operator|.
name|length
operator|==
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
name|i
index|]
operator|.
name|suggestion
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|cs
index|[
name|i
index|]
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
operator|==
literal|1
operator|&&
name|cs
index|[
name|i
index|]
operator|.
name|originalTermIndexes
index|[
literal|1
index|]
operator|==
literal|2
operator|&&
name|cs
index|[
name|i
index|]
operator|.
name|originalTermIndexes
index|[
literal|2
index|]
operator|==
literal|3
operator|&&
name|cs
index|[
name|i
index|]
operator|.
name|suggestion
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"hundredeight"
argument_list|)
operator|)
operator|||
operator|(
name|cs
index|[
name|i
index|]
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
operator|==
literal|3
operator|&&
name|cs
index|[
name|i
index|]
operator|.
name|originalTermIndexes
index|[
literal|1
index|]
operator|==
literal|4
operator|&&
name|cs
index|[
name|i
index|]
operator|.
name|originalTermIndexes
index|[
literal|2
index|]
operator|==
literal|5
operator|&&
name|cs
index|[
name|i
index|]
operator|.
name|suggestion
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"eightyeight"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|cs
operator|=
name|wbsp
operator|.
name|suggestWordCombinations
argument_list|(
name|terms
argument_list|,
literal|5
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|originalTermIndexes
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|suggestion
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|originalTermIndexes
index|[
literal|1
index|]
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|suggestion
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"hundred"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|0
index|]
operator|.
name|suggestion
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|originalTermIndexes
operator|.
name|length
operator|==
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|suggestion
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|originalTermIndexes
index|[
literal|1
index|]
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|originalTermIndexes
index|[
literal|2
index|]
operator|==
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cs
index|[
literal|1
index|]
operator|.
name|suggestion
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"hundredeight"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{ }
block|}
block|}
DECL|method|testBreakingWords
specifier|public
name|void
name|testBreakingWords
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|ir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|WordBreakSpellChecker
name|wbsp
init|=
operator|new
name|WordBreakSpellChecker
argument_list|()
decl_stmt|;
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"ninetynine"
argument_list|)
decl_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinBreakWordLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|SuggestWord
index|[]
index|[]
name|sw
init|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|5
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"ninety"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"nine"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"onethousand"
argument_list|)
decl_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinBreakWordLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|SuggestWord
index|[]
index|[]
name|sw
init|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"thousand"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|sw
operator|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|1
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|sw
operator|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|sw
operator|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|2
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"thousand"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|score
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|freq
operator|>
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|.
name|freq
operator|>
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|freq
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
operator|.
name|length
operator|==
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"thou"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"sand"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|.
name|freq
operator|>
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|.
name|freq
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|.
name|freq
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"numbers"
argument_list|,
literal|"onethousandonehundredeleven"
argument_list|)
decl_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinBreakWordLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|SuggestWord
index|[]
index|[]
name|sw
init|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|5
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|sw
operator|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|5
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|5
argument_list|)
expr_stmt|;
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|sw
operator|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|term
argument_list|,
literal|5
argument_list|,
name|ir
argument_list|,
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
argument_list|,
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
operator|.
name|length
operator|==
literal|5
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"thousand"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
operator|.
name|length
operator|==
literal|6
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"thou"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sw
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|.
name|string
operator|.
name|equals
argument_list|(
literal|"sand"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{ }
block|}
block|}
block|}
end_class
end_unit
