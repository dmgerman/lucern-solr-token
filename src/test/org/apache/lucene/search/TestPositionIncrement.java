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
name|queryParser
operator|.
name|QueryParser
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
name|PhraseQuery
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
name|Hits
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
name|analysis
operator|.
name|Analyzer
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
name|StopAnalyzer
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
name|StopFilter
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
name|Token
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
name|TokenStream
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
name|WhitespaceAnalyzer
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
name|io
operator|.
name|StringReader
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
comment|/**  * Term position unit test.  *  *  * @version $Revision$  */
end_comment
begin_class
DECL|class|TestPositionIncrement
specifier|public
class|class
name|TestPositionIncrement
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSetPosition
specifier|public
name|void
name|testSetPosition
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
specifier|public
name|TokenStream
name|tokenStream
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
name|TokenStream
argument_list|()
block|{
specifier|private
specifier|final
name|String
index|[]
name|TOKENS
init|=
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|,
literal|"5"
block|}
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|INCREMENTS
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
decl_stmt|;
specifier|private
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
block|{
if|if
condition|(
name|i
operator|==
name|TOKENS
operator|.
name|length
condition|)
return|return
literal|null
return|;
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
name|TOKENS
index|[
name|i
index|]
argument_list|,
name|i
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|INCREMENTS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|RAMDirectory
name|store
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|store
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"bogus"
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
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
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
name|store
argument_list|)
decl_stmt|;
name|PhraseQuery
name|q
decl_stmt|;
name|Hits
name|hits
decl_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// same as previous, just specify positions explicitely.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// specifying correct positions should find the phrase.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrase query would find it when correct positions are specified.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrase query should fail for non existing searched term
comment|// even if there exist another searched terms in the same searched position.
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"9"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// multi-phrase query should succed for non existing searched term
comment|// because there exist another searched terms in the same searched position.
name|MultiPhraseQuery
name|mq
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|mq
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
literal|"field"
argument_list|,
literal|"3"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"9"
argument_list|)
block|}
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|mq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// analyzer to introduce stopwords and increment gaps
name|Analyzer
name|stpa
init|=
operator|new
name|Analyzer
argument_list|()
block|{
specifier|final
name|WhitespaceAnalyzer
name|a
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|StopFilter
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stop"
block|}
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// should not find "1 2" because there is a gap of 1 in the index
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|"field"
argument_list|,
name|stpa
argument_list|)
decl_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// omitted stop word cannot help because stop filter swallows the increments.
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// query parser alone won't help, because stop filter swallows the increments.
name|qp
operator|.
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|dflt
init|=
name|StopFilter
operator|.
name|getEnablePositionIncrementsDefault
argument_list|()
decl_stmt|;
try|try
block|{
comment|// stop filter alone won't help, because query parser swallows the increments.
name|qp
operator|.
name|setEnablePositionIncrements
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|StopFilter
operator|.
name|setEnablePositionIncrementsDefault
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// when both qp qnd stopFilter propagate increments, we should find the doc.
name|qp
operator|.
name|setEnablePositionIncrements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q
operator|=
operator|(
name|PhraseQuery
operator|)
name|qp
operator|.
name|parse
argument_list|(
literal|"\"1 stop 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|StopFilter
operator|.
name|setEnablePositionIncrementsDefault
argument_list|(
name|dflt
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Basic analyzer behavior should be to keep sequential terms in one    * increment from one another.    */
DECL|method|testIncrementingPositions
specifier|public
name|void
name|testIncrementingPositions
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"field"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"one two three four five"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Token
name|token
init|=
name|ts
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
break|break;
name|assertEquals
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|,
literal|1
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
