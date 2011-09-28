begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|core
operator|.
name|LowerCaseFilter
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
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|TermPositionVector
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
name|TermVectorOffsetInfo
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
name|AttributeSource
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
begin_comment
comment|/**  * tests for the TestTeeSinkTokenFilter  */
end_comment
begin_class
DECL|class|TestTeeSinkTokenFilter
specifier|public
class|class
name|TestTeeSinkTokenFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|buffer1
specifier|protected
name|StringBuilder
name|buffer1
decl_stmt|;
DECL|field|buffer2
specifier|protected
name|StringBuilder
name|buffer2
decl_stmt|;
DECL|field|tokens1
specifier|protected
name|String
index|[]
name|tokens1
decl_stmt|;
DECL|field|tokens2
specifier|protected
name|String
index|[]
name|tokens2
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
name|tokens1
operator|=
operator|new
name|String
index|[]
block|{
literal|"The"
block|,
literal|"quick"
block|,
literal|"Burgundy"
block|,
literal|"Fox"
block|,
literal|"jumped"
block|,
literal|"over"
block|,
literal|"the"
block|,
literal|"lazy"
block|,
literal|"Red"
block|,
literal|"Dogs"
block|}
expr_stmt|;
name|tokens2
operator|=
operator|new
name|String
index|[]
block|{
literal|"The"
block|,
literal|"Lazy"
block|,
literal|"Dogs"
block|,
literal|"should"
block|,
literal|"stay"
block|,
literal|"on"
block|,
literal|"the"
block|,
literal|"porch"
block|}
expr_stmt|;
name|buffer1
operator|=
operator|new
name|StringBuilder
argument_list|()
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
name|tokens1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer1
operator|.
name|append
argument_list|(
name|tokens1
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buffer2
operator|=
operator|new
name|StringBuilder
argument_list|()
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
name|tokens2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer2
operator|.
name|append
argument_list|(
name|tokens2
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|theFilter
specifier|static
specifier|final
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
name|theFilter
init|=
operator|new
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|AttributeSource
name|a
parameter_list|)
block|{
name|CharTermAttribute
name|termAtt
init|=
name|a
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|termAtt
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"The"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|dogFilter
specifier|static
specifier|final
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
name|dogFilter
init|=
operator|new
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|AttributeSource
name|a
parameter_list|)
block|{
name|CharTermAttribute
name|termAtt
init|=
name|a
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|termAtt
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Dogs"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// LUCENE-1448
comment|// TODO: instead of testing it this way, we can test
comment|// with BaseTokenStreamTestCase now...
DECL|method|testEndOffsetPositionWithTeeSinkTokenFilter
specifier|public
name|void
name|testEndOffsetPositionWithTeeSinkTokenFilter
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
name|Analyzer
name|analyzer
init|=
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
decl_stmt|;
name|IndexWriter
name|w
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
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|TokenStream
name|tokenStream
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
literal|"abcd   "
argument_list|)
argument_list|)
decl_stmt|;
name|TeeSinkTokenFilter
name|tee
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
name|tokenStream
argument_list|)
decl_stmt|;
name|TokenStream
name|sink
init|=
name|tee
operator|.
name|newSinkTokenStream
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|f1
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
name|ft
argument_list|,
name|tee
argument_list|)
decl_stmt|;
name|Field
name|f2
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
name|ft
argument_list|,
name|sink
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TermVectorOffsetInfo
index|[]
name|termOffsets
init|=
operator|(
operator|(
name|TermPositionVector
operator|)
name|r
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
literal|"field"
argument_list|)
operator|)
operator|.
name|getOffsets
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|termOffsets
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|termOffsets
index|[
literal|0
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|termOffsets
index|[
literal|0
index|]
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|termOffsets
index|[
literal|1
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|termOffsets
index|[
literal|1
index|]
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testGeneral
specifier|public
name|void
name|testGeneral
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|TeeSinkTokenFilter
name|source
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|sink1
init|=
name|source
operator|.
name|newSinkTokenStream
argument_list|()
decl_stmt|;
specifier|final
name|TokenStream
name|sink2
init|=
name|source
operator|.
name|newSinkTokenStream
argument_list|(
name|theFilter
argument_list|)
decl_stmt|;
name|source
operator|.
name|addAttribute
argument_list|(
name|CheckClearAttributesAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|sink1
operator|.
name|addAttribute
argument_list|(
name|CheckClearAttributesAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|sink2
operator|.
name|addAttribute
argument_list|(
name|CheckClearAttributesAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|source
argument_list|,
name|tokens1
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|sink1
argument_list|,
name|tokens1
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|sink2
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"The"
block|,
literal|"the"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleSources
specifier|public
name|void
name|testMultipleSources
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|TeeSinkTokenFilter
name|tee1
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TeeSinkTokenFilter
operator|.
name|SinkTokenStream
name|dogDetector
init|=
name|tee1
operator|.
name|newSinkTokenStream
argument_list|(
name|dogFilter
argument_list|)
decl_stmt|;
specifier|final
name|TeeSinkTokenFilter
operator|.
name|SinkTokenStream
name|theDetector
init|=
name|tee1
operator|.
name|newSinkTokenStream
argument_list|(
name|theFilter
argument_list|)
decl_stmt|;
name|tee1
operator|.
name|reset
argument_list|()
expr_stmt|;
specifier|final
name|TokenStream
name|source1
init|=
operator|new
name|CachingTokenFilter
argument_list|(
name|tee1
argument_list|)
decl_stmt|;
name|tee1
operator|.
name|addAttribute
argument_list|(
name|CheckClearAttributesAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|dogDetector
operator|.
name|addAttribute
argument_list|(
name|CheckClearAttributesAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|theDetector
operator|.
name|addAttribute
argument_list|(
name|CheckClearAttributesAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|TeeSinkTokenFilter
name|tee2
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer2
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|tee2
operator|.
name|addSinkTokenStream
argument_list|(
name|dogDetector
argument_list|)
expr_stmt|;
name|tee2
operator|.
name|addSinkTokenStream
argument_list|(
name|theDetector
argument_list|)
expr_stmt|;
specifier|final
name|TokenStream
name|source2
init|=
name|tee2
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|source1
argument_list|,
name|tokens1
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|source2
argument_list|,
name|tokens2
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|theDetector
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"The"
block|,
literal|"the"
block|,
literal|"The"
block|,
literal|"the"
block|}
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|dogDetector
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Dogs"
block|,
literal|"Dogs"
block|}
argument_list|)
expr_stmt|;
name|source1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|TokenStream
name|lowerCasing
init|=
operator|new
name|LowerCaseFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|source1
argument_list|)
decl_stmt|;
name|String
index|[]
name|lowerCaseTokens
init|=
operator|new
name|String
index|[
name|tokens1
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
name|tokens1
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|lowerCaseTokens
index|[
name|i
index|]
operator|=
name|tokens1
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|lowerCasing
argument_list|,
name|lowerCaseTokens
argument_list|)
expr_stmt|;
block|}
comment|/**    * Not an explicit test, just useful to print out some info on performance    *    * @throws Exception    */
DECL|method|performance
specifier|public
name|void
name|performance
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|tokCount
init|=
block|{
literal|100
block|,
literal|500
block|,
literal|1000
block|,
literal|2000
block|,
literal|5000
block|,
literal|10000
block|}
decl_stmt|;
name|int
index|[]
name|modCounts
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|5
block|,
literal|10
block|,
literal|20
block|,
literal|50
block|,
literal|100
block|,
literal|200
block|,
literal|500
block|}
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|tokCount
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-----Tokens: "
operator|+
name|tokCount
index|[
name|k
index|]
operator|+
literal|"-----"
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
name|tokCount
index|[
name|k
index|]
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
comment|//make sure we produce the same tokens
name|TeeSinkTokenFilter
name|teeStream
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|sink
init|=
name|teeStream
operator|.
name|newSinkTokenStream
argument_list|(
operator|new
name|ModuloSinkFilter
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|teeStream
operator|.
name|consumeAllTokens
argument_list|()
expr_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|ModuloTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|tfTok
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|sinkTok
init|=
name|sink
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|stream
operator|.
name|incrementToken
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|sink
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tfTok
operator|+
literal|" is not equal to "
operator|+
name|sinkTok
operator|+
literal|" at token: "
operator|+
name|i
argument_list|,
name|tfTok
operator|.
name|equals
argument_list|(
name|sinkTok
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
comment|//simulate two fields, each being analyzed once, for 20 documents
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|modCounts
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tfPos
init|=
literal|0
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|stream
operator|=
operator|new
name|StandardFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|tfPos
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
name|stream
operator|=
operator|new
name|ModuloTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|modCounts
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|tfPos
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|finish
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ModCount: "
operator|+
name|modCounts
index|[
name|j
index|]
operator|+
literal|" Two fields took "
operator|+
operator|(
name|finish
operator|-
name|start
operator|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|int
name|sinkPos
init|=
literal|0
decl_stmt|;
comment|//simulate one field with one sink
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|teeStream
operator|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sink
operator|=
name|teeStream
operator|.
name|newSinkTokenStream
argument_list|(
operator|new
name|ModuloSinkFilter
argument_list|(
name|modCounts
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|teeStream
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|teeStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|sinkPos
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("Modulo--------");
name|posIncrAtt
operator|=
name|sink
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
while|while
condition|(
name|sink
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|sinkPos
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
name|finish
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ModCount: "
operator|+
name|modCounts
index|[
name|j
index|]
operator|+
literal|" Tee fields took "
operator|+
operator|(
name|finish
operator|-
name|start
operator|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sinkPos
operator|+
literal|" does not equal: "
operator|+
name|tfPos
argument_list|,
name|sinkPos
operator|==
name|tfPos
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"- End Tokens: "
operator|+
name|tokCount
index|[
name|k
index|]
operator|+
literal|"-----"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ModuloTokenFilter
class|class
name|ModuloTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|modCount
name|int
name|modCount
decl_stmt|;
DECL|method|ModuloTokenFilter
name|ModuloTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|mc
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|modCount
operator|=
name|mc
expr_stmt|;
block|}
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|//return every 100 tokens
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
name|hasNext
decl_stmt|;
for|for
control|(
name|hasNext
operator|=
name|input
operator|.
name|incrementToken
argument_list|()
init|;
name|hasNext
operator|&&
name|count
operator|%
name|modCount
operator|!=
literal|0
condition|;
name|hasNext
operator|=
name|input
operator|.
name|incrementToken
argument_list|()
control|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
return|return
name|hasNext
return|;
block|}
block|}
DECL|class|ModuloSinkFilter
class|class
name|ModuloSinkFilter
extends|extends
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
block|{
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|modCount
name|int
name|modCount
decl_stmt|;
DECL|method|ModuloSinkFilter
name|ModuloSinkFilter
parameter_list|(
name|int
name|mc
parameter_list|)
block|{
name|modCount
operator|=
name|mc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|AttributeSource
name|a
parameter_list|)
block|{
name|boolean
name|b
init|=
operator|(
name|a
operator|!=
literal|null
operator|&&
name|count
operator|%
name|modCount
operator|==
literal|0
operator|)
decl_stmt|;
name|count
operator|++
expr_stmt|;
return|return
name|b
return|;
block|}
block|}
block|}
end_class
end_unit
