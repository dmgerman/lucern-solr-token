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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|codecs
operator|.
name|lucene41
operator|.
name|Lucene41PostingsFormat
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
name|store
operator|.
name|IOContext
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
name|IndexInput
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
name|MockDirectoryWrapper
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
name|junit
operator|.
name|Before
import|;
end_import
begin_comment
comment|/**  * This testcase tests whether multi-level skipping is being used  * to reduce I/O while skipping through posting lists.  *   * Skipping in general is already covered by several other  * testcases.  *   */
end_comment
begin_class
DECL|class|TestMultiLevelSkipList
specifier|public
class|class
name|TestMultiLevelSkipList
extends|extends
name|LuceneTestCase
block|{
DECL|class|CountingRAMDirectory
class|class
name|CountingRAMDirectory
extends|extends
name|MockDirectoryWrapper
block|{
DECL|method|CountingRAMDirectory
specifier|public
name|CountingRAMDirectory
parameter_list|(
name|Directory
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|()
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|in
init|=
name|super
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".frq"
argument_list|)
condition|)
name|in
operator|=
operator|new
name|CountingStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|in
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Before
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
name|counter
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|testSimpleSkip
specifier|public
name|void
name|testSimpleSkip
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|CountingRAMDirectory
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
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
operator|new
name|PayloadAnalyzer
argument_list|()
argument_list|)
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene41PostingsFormat
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"a"
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|term
operator|.
name|text
argument_list|()
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
name|d1
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
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|AtomicReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|counter
operator|=
literal|0
expr_stmt|;
name|DocsAndPositionsEnum
name|tp
init|=
name|reader
operator|.
name|termPositionsEnum
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|checkSkipTo
argument_list|(
name|tp
argument_list|,
literal|14
argument_list|,
literal|185
argument_list|)
expr_stmt|;
comment|// no skips
name|checkSkipTo
argument_list|(
name|tp
argument_list|,
literal|17
argument_list|,
literal|190
argument_list|)
expr_stmt|;
comment|// one skip on level 0
name|checkSkipTo
argument_list|(
name|tp
argument_list|,
literal|287
argument_list|,
literal|200
argument_list|)
expr_stmt|;
comment|// one skip on level 1, two on level 0
comment|// this test would fail if we had only one skip level,
comment|// because than more bytes would be read from the freqStream
name|checkSkipTo
argument_list|(
name|tp
argument_list|,
literal|4800
argument_list|,
literal|250
argument_list|)
expr_stmt|;
comment|// one skip on level 2
block|}
block|}
DECL|method|checkSkipTo
specifier|public
name|void
name|checkSkipTo
parameter_list|(
name|DocsAndPositionsEnum
name|tp
parameter_list|,
name|int
name|target
parameter_list|,
name|int
name|maxCounter
parameter_list|)
throws|throws
name|IOException
block|{
name|tp
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxCounter
operator|<
name|counter
condition|)
block|{
name|fail
argument_list|(
literal|"Too many bytes read: "
operator|+
name|counter
operator|+
literal|" vs "
operator|+
name|maxCounter
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong document "
operator|+
name|tp
operator|.
name|docID
argument_list|()
operator|+
literal|" after skipTo target "
operator|+
name|target
argument_list|,
name|target
argument_list|,
name|tp
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Frequency is not 1: "
operator|+
name|tp
operator|.
name|freq
argument_list|()
argument_list|,
literal|1
argument_list|,
name|tp
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|tp
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|BytesRef
name|b
init|=
name|tp
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong payload for the target "
operator|+
name|target
operator|+
literal|": "
operator|+
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
index|]
argument_list|,
operator|(
name|byte
operator|)
name|target
argument_list|,
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
index|]
argument_list|)
expr_stmt|;
block|}
DECL|class|PayloadAnalyzer
specifier|private
specifier|static
class|class
name|PayloadAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|payloadCount
specifier|private
specifier|final
name|AtomicInteger
name|payloadCount
init|=
operator|new
name|AtomicInteger
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
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
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|PayloadFilter
argument_list|(
name|payloadCount
argument_list|,
name|tokenizer
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
DECL|field|payloadAtt
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|field|payloadCount
specifier|private
name|AtomicInteger
name|payloadCount
decl_stmt|;
DECL|method|PayloadFilter
specifier|protected
name|PayloadFilter
parameter_list|(
name|AtomicInteger
name|payloadCount
parameter_list|,
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|payloadCount
operator|=
name|payloadCount
expr_stmt|;
name|payloadAtt
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
name|hasNext
init|=
name|input
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasNext
condition|)
block|{
name|payloadAtt
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
name|payloadCount
operator|.
name|incrementAndGet
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|hasNext
return|;
block|}
block|}
DECL|field|counter
specifier|private
name|int
name|counter
init|=
literal|0
decl_stmt|;
comment|// Simply extends IndexInput in a way that we are able to count the number
comment|// of bytes read
DECL|class|CountingStream
class|class
name|CountingStream
extends|extends
name|IndexInput
block|{
DECL|field|input
specifier|private
name|IndexInput
name|input
decl_stmt|;
DECL|method|CountingStream
name|CountingStream
parameter_list|(
name|IndexInput
name|input
parameter_list|)
block|{
name|super
argument_list|(
literal|"CountingStream("
operator|+
name|input
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
name|TestMultiLevelSkipList
operator|.
name|this
operator|.
name|counter
operator|++
expr_stmt|;
return|return
name|this
operator|.
name|input
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|TestMultiLevelSkipList
operator|.
name|this
operator|.
name|counter
operator|+=
name|len
expr_stmt|;
name|this
operator|.
name|input
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|this
operator|.
name|input
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|input
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|this
operator|.
name|input
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|CountingStream
name|clone
parameter_list|()
block|{
return|return
operator|new
name|CountingStream
argument_list|(
name|this
operator|.
name|input
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
