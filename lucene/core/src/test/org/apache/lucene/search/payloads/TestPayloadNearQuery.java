begin_unit
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PayloadAttribute
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
name|FieldInvertState
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
name|CollectionStatistics
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
name|Explanation
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
name|QueryUtils
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
name|TermStatistics
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
name|TopDocs
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|SpanTermQuery
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
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_class
DECL|class|TestPayloadNearQuery
specifier|public
class|class
name|TestPayloadNearQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|static
name|BoostingSimilarity
name|similarity
init|=
operator|new
name|BoostingSimilarity
argument_list|()
decl_stmt|;
DECL|field|payload2
specifier|private
specifier|static
name|byte
index|[]
name|payload2
init|=
operator|new
name|byte
index|[]
block|{
literal|2
block|}
decl_stmt|;
DECL|field|payload4
specifier|private
specifier|static
name|byte
index|[]
name|payload4
init|=
operator|new
name|byte
index|[]
block|{
literal|4
block|}
decl_stmt|;
DECL|class|PayloadAnalyzer
specifier|private
specifier|static
class|class
name|PayloadAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|result
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|result
argument_list|,
operator|new
name|PayloadFilter
argument_list|(
name|result
argument_list|,
name|fieldName
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|PayloadFilter
specifier|private
specifier|static
class|class
name|PayloadFilter
extends|extends
name|TokenFilter
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|numSeen
specifier|private
name|int
name|numSeen
init|=
literal|0
decl_stmt|;
DECL|field|payAtt
specifier|private
specifier|final
name|PayloadAttribute
name|payAtt
decl_stmt|;
DECL|method|PayloadFilter
specifier|public
name|PayloadFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|payAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
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
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|numSeen
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|payAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|payload2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|payload4
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numSeen
operator|++
expr_stmt|;
name|result
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|numSeen
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|newPhraseQuery
specifier|private
name|PayloadNearQuery
name|newPhraseQuery
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|phrase
parameter_list|,
name|boolean
name|inOrder
parameter_list|,
name|PayloadFunction
name|function
parameter_list|)
block|{
name|String
index|[]
name|words
init|=
name|phrase
operator|.
name|split
argument_list|(
literal|"[\\s]+"
argument_list|)
decl_stmt|;
name|SpanQuery
name|clauses
index|[]
init|=
operator|new
name|SpanQuery
index|[
name|words
operator|.
name|length
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
index|[
name|i
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|words
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PayloadNearQuery
argument_list|(
name|clauses
argument_list|,
literal|0
argument_list|,
name|inOrder
argument_list|,
name|function
argument_list|)
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
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
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|PayloadAnalyzer
argument_list|()
argument_list|)
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
argument_list|)
decl_stmt|;
comment|//writer.infoStream = System.out;
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
name|newTextField
argument_list|(
literal|"field"
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
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|txt
init|=
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|+
literal|' '
operator|+
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field2"
argument_list|,
name|txt
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
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
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"twenty two"
argument_list|,
literal|true
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// all 10 hits should have score = 3 because adjacent terms have payloads of 2,4
comment|// and all the similarity factors are set to 1
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
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be 10 hits"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|+
literal|" hundred"
argument_list|,
literal|true
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: run query="
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
comment|// all should have score = 3 because adjacent terms have payloads of 2,4
comment|// and all the similarity factors are set to 1
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
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"should be 100 hits"
argument_list|,
literal|100
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
comment|//        System.out.println("Doc: " + doc.toString());
comment|//        System.out.println("Explain: " + searcher.explain(query, doc.doc));
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testPayloadNear
specifier|public
name|void
name|testPayloadNear
parameter_list|()
throws|throws
name|IOException
block|{
name|SpanNearQuery
name|q1
decl_stmt|,
name|q2
decl_stmt|;
name|PayloadNearQuery
name|query
decl_stmt|;
comment|//SpanNearQuery(clauses, 10000, false)
name|q1
operator|=
name|spanNearQuery
argument_list|(
literal|"field2"
argument_list|,
literal|"twenty two"
argument_list|)
expr_stmt|;
name|q2
operator|=
name|spanNearQuery
argument_list|(
literal|"field2"
argument_list|,
literal|"twenty three"
argument_list|)
expr_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[
literal|2
index|]
decl_stmt|;
name|clauses
index|[
literal|0
index|]
operator|=
name|q1
expr_stmt|;
name|clauses
index|[
literal|1
index|]
operator|=
name|q2
expr_stmt|;
name|query
operator|=
operator|new
name|PayloadNearQuery
argument_list|(
name|clauses
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//System.out.println(query.toString());
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|/*     System.out.println(hits.totalHits);     for (int j = 0; j< hits.scoreDocs.length; j++) {       ScoreDoc doc = hits.scoreDocs[j];       System.out.println("doc: "+doc.doc+", score: "+doc.score);     }     */
block|}
DECL|method|testAverageFunction
specifier|public
name|void
name|testAverageFunction
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"twenty two"
argument_list|,
literal|true
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// all 10 hits should have score = 3 because adjacent terms have payloads of 2,4
comment|// and all the similarity factors are set to 1
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
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be 10 hits"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
name|Explanation
name|explain
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|exp
init|=
name|explain
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"AveragePayloadFunction"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|score
operator|+
literal|" explain value does not equal: "
operator|+
literal|3
argument_list|,
name|explain
operator|.
name|getValue
argument_list|()
operator|==
literal|3f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMaxFunction
specifier|public
name|void
name|testMaxFunction
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"twenty two"
argument_list|,
literal|true
argument_list|,
operator|new
name|MaxPayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// all 10 hits should have score = 4 (max payload value)
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
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be 10 hits"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|4
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|4
argument_list|)
expr_stmt|;
name|Explanation
name|explain
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|exp
init|=
name|explain
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"MaxPayloadFunction"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|score
operator|+
literal|" explain value does not equal: "
operator|+
literal|4
argument_list|,
name|explain
operator|.
name|getValue
argument_list|()
operator|==
literal|4f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMinFunction
specifier|public
name|void
name|testMinFunction
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"twenty two"
argument_list|,
literal|true
argument_list|,
operator|new
name|MinPayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// all 10 hits should have score = 2 (min payload value)
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
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should be 10 hits"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Explanation
name|explain
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|exp
init|=
name|explain
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"MinPayloadFunction"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|score
operator|+
literal|" explain value does not equal: "
operator|+
literal|2
argument_list|,
name|explain
operator|.
name|getValue
argument_list|()
operator|==
literal|2f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getClauses
specifier|private
name|SpanQuery
index|[]
name|getClauses
parameter_list|()
block|{
name|SpanNearQuery
name|q1
decl_stmt|,
name|q2
decl_stmt|;
name|q1
operator|=
name|spanNearQuery
argument_list|(
literal|"field2"
argument_list|,
literal|"twenty two"
argument_list|)
expr_stmt|;
name|q2
operator|=
name|spanNearQuery
argument_list|(
literal|"field2"
argument_list|,
literal|"twenty three"
argument_list|)
expr_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[
literal|2
index|]
decl_stmt|;
name|clauses
index|[
literal|0
index|]
operator|=
name|q1
expr_stmt|;
name|clauses
index|[
literal|1
index|]
operator|=
name|q2
expr_stmt|;
return|return
name|clauses
return|;
block|}
DECL|method|spanNearQuery
specifier|private
name|SpanNearQuery
name|spanNearQuery
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|words
parameter_list|)
block|{
name|String
index|[]
name|wordList
init|=
name|words
operator|.
name|split
argument_list|(
literal|"[\\s]+"
argument_list|)
decl_stmt|;
name|SpanQuery
name|clauses
index|[]
init|=
operator|new
name|SpanQuery
index|[
name|wordList
operator|.
name|length
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clauses
index|[
name|i
index|]
operator|=
operator|new
name|PayloadTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|wordList
index|[
name|i
index|]
argument_list|)
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SpanNearQuery
argument_list|(
name|clauses
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|testLongerSpan
specifier|public
name|void
name|testLongerSpan
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
name|query
operator|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"nine hundred ninety nine"
argument_list|,
literal|true
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
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
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
comment|//    System.out.println("Doc: " + doc.toString());
comment|//    System.out.println("Explain: " + searcher.explain(query, doc.doc));
name|assertTrue
argument_list|(
literal|"there should only be one hit"
argument_list|,
name|hits
operator|.
name|totalHits
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// should have score = 3 because adjacent terms have payloads of 2,4
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testComplexNested
specifier|public
name|void
name|testComplexNested
parameter_list|()
throws|throws
name|IOException
block|{
name|PayloadNearQuery
name|query
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
comment|// combine ordered and unordered spans with some nesting to make sure all payloads are counted
name|SpanQuery
name|q1
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"nine hundred"
argument_list|,
literal|true
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|SpanQuery
name|q2
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"ninety nine"
argument_list|,
literal|true
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|SpanQuery
name|q3
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"nine ninety"
argument_list|,
literal|false
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|SpanQuery
name|q4
init|=
name|newPhraseQuery
argument_list|(
literal|"field"
argument_list|,
literal|"hundred nine"
argument_list|,
literal|false
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|)
decl_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|PayloadNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q1
block|,
name|q2
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|PayloadNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|q3
block|,
name|q4
block|}
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
block|}
decl_stmt|;
name|query
operator|=
operator|new
name|PayloadNearQuery
argument_list|(
name|clauses
argument_list|,
literal|0
argument_list|,
literal|false
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
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// should be only 1 hit - doc 999
name|assertTrue
argument_list|(
literal|"should only be one hit"
argument_list|,
name|hits
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// the score should be 3 - the average of all the underlying payloads
name|ScoreDoc
name|doc
init|=
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
comment|//    System.out.println("Doc: " + doc.toString());
comment|//    System.out.println("Explain: " + searcher.explain(query, doc.doc));
name|assertTrue
argument_list|(
name|doc
operator|.
name|score
operator|+
literal|" does not equal: "
operator|+
literal|3
argument_list|,
name|doc
operator|.
name|score
operator|==
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|class|BoostingSimilarity
specifier|static
class|class
name|BoostingSimilarity
extends|extends
name|DefaultSimilarity
block|{
annotation|@
name|Override
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
comment|//we know it is size 4 here, so ignore the offset/length
return|return
name|payload
operator|.
name|bytes
index|[
name|payload
operator|.
name|offset
index|]
return|;
block|}
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
comment|//Make everything else 1 so we see the effect of the payload
comment|//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
annotation|@
name|Override
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
comment|// idf used for phrase queries
annotation|@
name|Override
DECL|method|idfExplain
specifier|public
name|Explanation
name|idfExplain
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
index|[]
name|termStats
parameter_list|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
literal|1.0f
argument_list|,
literal|"Inexplicable"
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
