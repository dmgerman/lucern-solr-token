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
name|analysis
operator|.
name|TokenFilter
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
name|Fieldable
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
operator|.
name|Index
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
operator|.
name|Store
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
operator|.
name|TermVector
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
name|_TestUtil
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
begin_class
DECL|class|TestDocumentWriter
specifier|public
class|class
name|TestDocumentWriter
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|method|TestDocumentWriter
specifier|public
name|TestDocumentWriter
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
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
name|dir
operator|=
name|newDirectory
argument_list|()
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddDocument
specifier|public
name|void
name|testAddDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
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
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SegmentInfo
name|info
init|=
name|writer
operator|.
name|newestSegment
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Document: " + doc);
name|Fieldable
index|[]
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"textField2"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_2_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|isTermVectorStored
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"textField1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_1_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|isTermVectorStored
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"keyField"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|KEYWORD_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
name|DocHelper
operator|.
name|NO_NORMS_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|NO_NORMS_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_3_TEXT
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that the norms are not present in the segment if
comment|// omitNorms is true
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|reader
operator|.
name|core
operator|.
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|reader
operator|.
name|core
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
block|{
name|assertTrue
argument_list|(
name|fi
operator|.
name|omitNorms
operator|==
operator|!
name|reader
operator|.
name|hasNorms
argument_list|(
name|fi
operator|.
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPositionIncrementGap
specifier|public
name|void
name|testPositionIncrementGap
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
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
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|500
return|;
block|}
block|}
decl_stmt|;
name|IndexWriter
name|writer
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"repeated"
argument_list|,
literal|"repeated one"
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
literal|"repeated"
argument_list|,
literal|"repeated two"
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
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SegmentInfo
name|info
init|=
name|writer
operator|.
name|newestSegment
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|termPositions
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|"repeated"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"repeated"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|termPositions
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|int
name|freq
init|=
name|termPositions
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|502
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTokenReuse
specifier|public
name|void
name|testTokenReuse
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
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
name|TokenFilter
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
block|{
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|AttributeSource
operator|.
name|State
name|state
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|state
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
name|boolean
name|hasNext
init|=
name|input
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasNext
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
index|[
literal|0
index|]
operator|-
literal|'0'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|first
condition|)
block|{
comment|// set payload on first position only
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|100
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
block|}
comment|// index a "synonym" for every token
name|state
operator|=
name|captureState
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
return|;
block|}
block|}
decl_stmt|;
name|IndexWriter
name|writer
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"a 5 a a"
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
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SegmentInfo
name|info
init|=
name|writer
operator|.
name|newestSegment
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|termPositions
init|=
name|reader
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|docsAndPositions
argument_list|(
name|reader
operator|.
name|getDeletedDocs
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|termPositions
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|int
name|freq
init|=
name|termPositions
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|termPositions
operator|.
name|hasPayload
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|termPositions
operator|.
name|hasPayload
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|termPositions
operator|.
name|hasPayload
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPreAnalyzedField
specifier|public
name|void
name|testPreAnalyzedField
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
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
operator|new
name|MockAnalyzer
argument_list|()
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
operator|new
name|Field
argument_list|(
literal|"preanalyzed"
argument_list|,
operator|new
name|TokenStream
argument_list|()
block|{
specifier|private
name|String
index|[]
name|tokens
init|=
operator|new
name|String
index|[]
block|{
literal|"term1"
block|,
literal|"term2"
block|,
literal|"term3"
block|,
literal|"term2"
block|}
decl_stmt|;
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
specifier|private
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|index
operator|==
name|tokens
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|tokens
index|[
name|index
operator|++
index|]
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
argument_list|,
name|TermVector
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
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SegmentInfo
name|info
init|=
name|writer
operator|.
name|newestSegment
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|termPositions
init|=
name|reader
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"preanalyzed"
argument_list|)
operator|.
name|docsAndPositions
argument_list|(
name|reader
operator|.
name|getDeletedDocs
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"term1"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|termPositions
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|termPositions
operator|=
name|reader
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"preanalyzed"
argument_list|)
operator|.
name|docsAndPositions
argument_list|(
name|reader
operator|.
name|getDeletedDocs
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"term2"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|termPositions
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|termPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|termPositions
operator|=
name|reader
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"preanalyzed"
argument_list|)
operator|.
name|docsAndPositions
argument_list|(
name|reader
operator|.
name|getDeletedDocs
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"term3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|termPositions
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|termPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test adding two fields with the same name, but     * with different term vector setting (LUCENE-766).    */
DECL|method|testMixedTermVectorSettingsSameField
specifier|public
name|void
name|testMixedTermVectorSettingsSameField
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// f1 first without tv then with tv
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|TermVector
operator|.
name|NO
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
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
comment|// f2 first with tv then without tv
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
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
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
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
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
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
comment|// f1
name|TermFreqVector
name|tfv1
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
literal|"f1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tfv1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the 'with_tv' setting should rule!"
argument_list|,
literal|2
argument_list|,
name|tfv1
operator|.
name|getTerms
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// f2
name|TermFreqVector
name|tfv2
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
literal|"f2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tfv2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the 'with_tv' setting should rule!"
argument_list|,
literal|2
argument_list|,
name|tfv2
operator|.
name|getTerms
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test adding two fields with the same name, one indexed    * the other stored only. The omitNorms and omitTermFreqAndPositions setting    * of the stored field should not affect the indexed one (LUCENE-1590)    */
DECL|method|testLUCENE_1590
specifier|public
name|void
name|testLUCENE_1590
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// f1 has no norms
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|ANALYZED_NO_NORMS
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
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
comment|// f2 has no TF
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|f
operator|.
name|setOmitTermFreqAndPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
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
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
comment|// be sure to have a single segment
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|getOnlySegmentReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|FieldInfos
name|fi
init|=
name|reader
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
comment|// f1
name|assertFalse
argument_list|(
literal|"f1 should have no norms"
argument_list|,
name|reader
operator|.
name|hasNorms
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"omitTermFreqAndPositions field bit should not be set for f1"
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|omitTermFreqAndPositions
argument_list|)
expr_stmt|;
comment|// f2
name|assertTrue
argument_list|(
literal|"f2 should have norms"
argument_list|,
name|reader
operator|.
name|hasNorms
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"omitTermFreqAndPositions field bit should be set for f2"
argument_list|,
name|fi
operator|.
name|fieldInfo
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|omitTermFreqAndPositions
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
