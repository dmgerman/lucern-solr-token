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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|index
operator|.
name|PayloadProcessorProvider
operator|.
name|DirPayloadProcessor
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
name|PayloadProcessorProvider
operator|.
name|PayloadProcessor
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
name|DocIdSetIterator
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
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestPayloadProcessorProvider
specifier|public
class|class
name|TestPayloadProcessorProvider
extends|extends
name|LuceneTestCase
block|{
DECL|class|PerDirPayloadProcessor
specifier|private
specifier|static
specifier|final
class|class
name|PerDirPayloadProcessor
extends|extends
name|PayloadProcessorProvider
block|{
DECL|field|processors
specifier|private
name|Map
argument_list|<
name|Directory
argument_list|,
name|DirPayloadProcessor
argument_list|>
name|processors
decl_stmt|;
DECL|method|PerDirPayloadProcessor
specifier|public
name|PerDirPayloadProcessor
parameter_list|(
name|Map
argument_list|<
name|Directory
argument_list|,
name|DirPayloadProcessor
argument_list|>
name|processors
parameter_list|)
block|{
name|this
operator|.
name|processors
operator|=
name|processors
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDirProcessor
specifier|public
name|DirPayloadProcessor
name|getDirProcessor
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|processors
operator|.
name|get
argument_list|(
name|dir
argument_list|)
return|;
block|}
block|}
DECL|class|PerTermPayloadProcessor
specifier|private
specifier|static
specifier|final
class|class
name|PerTermPayloadProcessor
extends|extends
name|DirPayloadProcessor
block|{
annotation|@
name|Override
DECL|method|getProcessor
specifier|public
name|PayloadProcessor
name|getProcessor
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
comment|// don't process payloads of terms other than "p:p1"
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|)
operator|||
operator|!
name|text
operator|.
name|bytesEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"p1"
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// All other terms are processed the same way
return|return
operator|new
name|DeletePayloadProcessor
argument_list|()
return|;
block|}
block|}
comment|/** deletes the incoming payload */
DECL|class|DeletePayloadProcessor
specifier|private
specifier|static
specifier|final
class|class
name|DeletePayloadProcessor
extends|extends
name|PayloadProcessor
block|{
annotation|@
name|Override
DECL|method|processPayload
specifier|public
name|void
name|processPayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
name|payload
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|class|PayloadTokenStream
specifier|private
specifier|static
specifier|final
class|class
name|PayloadTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|payload
specifier|private
specifier|final
name|PayloadAttribute
name|payload
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|CharTermAttribute
name|term
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|called
specifier|private
name|boolean
name|called
init|=
literal|false
decl_stmt|;
DECL|field|t
specifier|private
name|String
name|t
decl_stmt|;
DECL|method|PayloadTokenStream
specifier|public
name|PayloadTokenStream
parameter_list|(
name|String
name|t
parameter_list|)
block|{
name|this
operator|.
name|t
operator|=
name|t
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
if|if
condition|(
name|called
condition|)
block|{
return|return
literal|false
return|;
block|}
name|called
operator|=
literal|true
expr_stmt|;
name|byte
index|[]
name|p
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|}
decl_stmt|;
name|payload
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|term
operator|.
name|append
argument_list|(
name|t
argument_list|)
expr_stmt|;
return|return
literal|true
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
name|called
operator|=
literal|false
expr_stmt|;
name|term
operator|.
name|setEmpty
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|NUM_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS
init|=
literal|10
decl_stmt|;
DECL|method|getConfig
specifier|private
name|IndexWriterConfig
name|getConfig
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
DECL|method|populateDirs
specifier|private
name|void
name|populateDirs
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
index|[]
name|dirs
parameter_list|,
name|boolean
name|multipleCommits
parameter_list|)
throws|throws
name|IOException
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
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dirs
index|[
name|i
index|]
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|populateDocs
argument_list|(
name|random
argument_list|,
name|dirs
index|[
name|i
index|]
argument_list|,
name|multipleCommits
argument_list|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populateDocs
specifier|private
name|void
name|populateDocs
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|boolean
name|multipleCommits
parameter_list|)
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
name|getConfig
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setMergeFactor
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|TokenStream
name|payloadTS1
init|=
operator|new
name|PayloadTokenStream
argument_list|(
literal|"p1"
argument_list|)
decl_stmt|;
name|TokenStream
name|payloadTS2
init|=
operator|new
name|PayloadTokenStream
argument_list|(
literal|"p2"
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
name|NUM_DOCS
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
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"content"
argument_list|,
literal|"doc content "
operator|+
name|i
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
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
literal|"p"
argument_list|,
name|payloadTS1
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
literal|"p"
argument_list|,
name|payloadTS2
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
if|if
condition|(
name|multipleCommits
operator|&&
operator|(
name|i
operator|%
literal|4
operator|==
literal|0
operator|)
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyPayloadExists
specifier|private
name|void
name|verifyPayloadExists
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|text
parameter_list|,
name|int
name|numExpected
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|numPayloads
init|=
literal|0
decl_stmt|;
name|DocsAndPositionsEnum
name|tpe
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
name|field
argument_list|,
name|text
argument_list|)
decl_stmt|;
while|while
condition|(
name|tpe
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|tpe
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|tpe
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
name|BytesRef
name|payload
init|=
name|tpe
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|payload
operator|.
name|bytes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
operator|++
name|numPayloads
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|numExpected
argument_list|,
name|numPayloads
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|addToEmptyIndex
parameter_list|,
name|int
name|numExpectedPayloads
parameter_list|,
name|boolean
name|multipleCommits
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[
literal|2
index|]
decl_stmt|;
name|populateDirs
argument_list|(
name|random
argument_list|,
name|dirs
argument_list|,
name|multipleCommits
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|addToEmptyIndex
condition|)
block|{
name|populateDocs
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|multipleCommits
argument_list|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
expr_stmt|;
block|}
comment|// Add two source dirs. By not adding the dest dir, we ensure its payloads
comment|// won't get processed.
name|Map
argument_list|<
name|Directory
argument_list|,
name|DirPayloadProcessor
argument_list|>
name|processors
init|=
operator|new
name|HashMap
argument_list|<
name|Directory
argument_list|,
name|DirPayloadProcessor
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Directory
name|d
range|:
name|dirs
control|)
block|{
name|processors
operator|.
name|put
argument_list|(
name|d
argument_list|,
operator|new
name|PerTermPayloadProcessor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getConfig
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setPayloadProcessorProvider
argument_list|(
operator|new
name|PerDirPayloadProcessor
argument_list|(
name|processors
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers
init|=
operator|new
name|IndexReader
index|[
name|dirs
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|readers
index|[
name|i
index|]
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|readers
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|IndexReader
name|r
range|:
name|readers
control|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|numExpectedPayloads
argument_list|)
expr_stmt|;
comment|// the second term should always have all payloads
name|numExpectedPayloads
operator|=
name|NUM_DOCS
operator|*
name|dirs
operator|.
name|length
operator|+
operator|(
name|addToEmptyIndex
condition|?
literal|0
else|:
name|NUM_DOCS
operator|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|numExpectedPayloads
argument_list|)
expr_stmt|;
for|for
control|(
name|Directory
name|d
range|:
name|dirs
control|)
name|d
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
annotation|@
name|Test
DECL|method|testAddIndexes
specifier|public
name|void
name|testAddIndexes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// addIndexes - single commit in each
name|doTest
argument_list|(
name|random
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// addIndexes - multiple commits in each
name|doTest
argument_list|(
name|random
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddIndexesIntoExisting
specifier|public
name|void
name|testAddIndexesIntoExisting
parameter_list|()
throws|throws
name|Exception
block|{
comment|// addIndexes - single commit in each
name|doTest
argument_list|(
name|random
argument_list|,
literal|false
argument_list|,
name|NUM_DOCS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// addIndexes - multiple commits in each
name|doTest
argument_list|(
name|random
argument_list|,
literal|false
argument_list|,
name|NUM_DOCS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegularMerges
specifier|public
name|void
name|testRegularMerges
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
name|populateDocs
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
expr_stmt|;
comment|// Add two source dirs. By not adding the dest dir, we ensure its payloads
comment|// won't get processed.
name|Map
argument_list|<
name|Directory
argument_list|,
name|DirPayloadProcessor
argument_list|>
name|processors
init|=
operator|new
name|HashMap
argument_list|<
name|Directory
argument_list|,
name|DirPayloadProcessor
argument_list|>
argument_list|()
decl_stmt|;
name|processors
operator|.
name|put
argument_list|(
name|dir
argument_list|,
operator|new
name|PerTermPayloadProcessor
argument_list|()
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
name|getConfig
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setPayloadProcessorProvider
argument_list|(
operator|new
name|PerDirPayloadProcessor
argument_list|(
name|processors
argument_list|)
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
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p1"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verifyPayloadExists
argument_list|(
name|dir
argument_list|,
literal|"p"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"p2"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
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
