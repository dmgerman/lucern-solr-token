begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|List
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|search
operator|.
name|TermQuery
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
name|ArrayUtil
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
name|IOUtils
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
DECL|class|TestBufferedIndexInput
specifier|public
class|class
name|TestBufferedIndexInput
extends|extends
name|LuceneTestCase
block|{
DECL|field|TEST_FILE_LENGTH
specifier|private
specifier|static
specifier|final
name|long
name|TEST_FILE_LENGTH
init|=
literal|100
operator|*
literal|1024
decl_stmt|;
comment|// Call readByte() repeatedly, past the buffer boundary, and see that it
comment|// is working as expected.
comment|// Our input comes from a dynamically generated/ "file" - see
comment|// MyBufferedIndexInput below.
DECL|method|testReadByte
specifier|public
name|void
name|testReadByte
parameter_list|()
throws|throws
name|Exception
block|{
name|MyBufferedIndexInput
name|input
init|=
operator|new
name|MyBufferedIndexInput
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
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
operator|*
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|input
operator|.
name|readByte
argument_list|()
argument_list|,
name|byten
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Call readBytes() repeatedly, with various chunk sizes (from 1 byte to
comment|// larger than the buffer size), and see that it returns the bytes we expect.
comment|// Our input comes from a dynamically generated "file" -
comment|// see MyBufferedIndexInput below.
DECL|method|testReadBytes
specifier|public
name|void
name|testReadBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|MyBufferedIndexInput
name|input
init|=
operator|new
name|MyBufferedIndexInput
argument_list|()
decl_stmt|;
name|runReadBytes
argument_list|(
name|input
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|runReadBytesAndClose
specifier|private
name|void
name|runReadBytesAndClose
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Random
name|r
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|runReadBytes
argument_list|(
name|input
argument_list|,
name|bufferSize
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|runReadBytes
specifier|private
name|void
name|runReadBytes
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Random
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
comment|// gradually increasing size:
for|for
control|(
name|int
name|size
init|=
literal|1
init|;
name|size
operator|<
name|bufferSize
operator|*
literal|10
condition|;
name|size
operator|=
name|size
operator|+
name|size
operator|/
literal|200
operator|+
literal|1
control|)
block|{
name|checkReadBytes
argument_list|(
name|input
argument_list|,
name|size
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|size
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|TEST_FILE_LENGTH
condition|)
block|{
comment|// wrap
name|pos
operator|=
literal|0
expr_stmt|;
name|input
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wildly fluctuating size:
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|size
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|checkReadBytes
argument_list|(
name|input
argument_list|,
literal|1
operator|+
name|size
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|1
operator|+
name|size
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|TEST_FILE_LENGTH
condition|)
block|{
comment|// wrap
name|pos
operator|=
literal|0
expr_stmt|;
name|input
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
comment|// constant small size (7 bytes):
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bufferSize
condition|;
name|i
operator|++
control|)
block|{
name|checkReadBytes
argument_list|(
name|input
argument_list|,
literal|7
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|7
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|TEST_FILE_LENGTH
condition|)
block|{
comment|// wrap
name|pos
operator|=
literal|0
expr_stmt|;
name|input
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
DECL|method|checkReadBytes
specifier|private
name|void
name|checkReadBytes
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Just to see that "offset" is treated properly in readBytes(), we
comment|// add an arbitrary offset at the beginning of the array
name|int
name|offset
init|=
name|size
operator|%
literal|10
decl_stmt|;
comment|// arbitrary
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|size
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|input
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|left
init|=
name|TEST_FILE_LENGTH
operator|-
name|input
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|left
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|left
operator|<
name|size
condition|)
block|{
name|size
operator|=
operator|(
name|int
operator|)
name|left
expr_stmt|;
block|}
name|input
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pos
operator|+
name|size
argument_list|,
name|input
operator|.
name|getFilePointer
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"pos="
operator|+
name|i
operator|+
literal|" filepos="
operator|+
operator|(
name|pos
operator|+
name|i
operator|)
argument_list|,
name|byten
argument_list|(
name|pos
operator|+
name|i
argument_list|)
argument_list|,
name|buffer
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// This tests that attempts to readBytes() past an EOF will fail, while
comment|// reads up to the EOF will succeed. The EOF is determined by the
comment|// BufferedIndexInput's arbitrary length() value.
DECL|method|testEOF
specifier|public
name|void
name|testEOF
parameter_list|()
throws|throws
name|Exception
block|{
name|MyBufferedIndexInput
name|input
init|=
operator|new
name|MyBufferedIndexInput
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
comment|// see that we can read all the bytes at one go:
name|checkReadBytes
argument_list|(
name|input
argument_list|,
operator|(
name|int
operator|)
name|input
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// go back and see that we can't read more than that, for small and
comment|// large overflows:
name|int
name|pos
init|=
operator|(
name|int
operator|)
name|input
operator|.
name|length
argument_list|()
operator|-
literal|10
decl_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|checkReadBytes
argument_list|(
name|input
argument_list|,
literal|10
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|input
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
try|try
block|{
name|checkReadBytes
argument_list|(
name|input
argument_list|,
literal|11
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Block read past end of file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* success */
block|}
name|input
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
try|try
block|{
name|checkReadBytes
argument_list|(
name|input
argument_list|,
literal|50
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Block read past end of file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* success */
block|}
name|input
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
try|try
block|{
name|checkReadBytes
argument_list|(
name|input
argument_list|,
literal|100000
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Block read past end of file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* success */
block|}
block|}
comment|// byten emulates a file - byten(n) returns the n'th byte in that file.
comment|// MyBufferedIndexInput reads this "file".
DECL|method|byten
specifier|private
specifier|static
name|byte
name|byten
parameter_list|(
name|long
name|n
parameter_list|)
block|{
return|return
call|(
name|byte
call|)
argument_list|(
name|n
operator|*
name|n
operator|%
literal|256
argument_list|)
return|;
block|}
DECL|class|MyBufferedIndexInput
specifier|private
specifier|static
class|class
name|MyBufferedIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|field|pos
specifier|private
name|long
name|pos
decl_stmt|;
DECL|field|len
specifier|private
name|long
name|len
decl_stmt|;
DECL|method|MyBufferedIndexInput
specifier|public
name|MyBufferedIndexInput
parameter_list|(
name|long
name|len
parameter_list|)
block|{
name|super
argument_list|(
literal|"MyBufferedIndexInput(len="
operator|+
name|len
operator|+
literal|")"
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
name|this
operator|.
name|pos
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|MyBufferedIndexInput
specifier|public
name|MyBufferedIndexInput
parameter_list|()
block|{
comment|// an infinite file
name|this
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|offset
operator|+
name|length
condition|;
name|i
operator|++
control|)
name|b
index|[
name|i
index|]
operator|=
name|byten
argument_list|(
name|pos
operator|++
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|pos
operator|=
name|pos
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
block|{       }
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|len
return|;
block|}
annotation|@
name|Override
DECL|method|slice
specifier|public
name|IndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|method|testSetBufferSize
specifier|public
name|void
name|testSetBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|indexDir
init|=
name|createTempDir
argument_list|(
literal|"testSetBufferSize"
argument_list|)
decl_stmt|;
name|MockFSDirectory
name|dir
init|=
operator|new
name|MockFSDirectory
argument_list|(
name|indexDir
argument_list|,
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|)
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
literal|37
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
literal|"content"
argument_list|,
literal|"aaa bbb ccc ddd"
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
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
name|dir
operator|.
name|allIndexInputs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Term
name|aaa
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
argument_list|)
decl_stmt|;
name|Term
name|bbb
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bbb"
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|bbb
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|36
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|bbb
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|35
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"33"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|aaa
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|35
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MockFSDirectory
specifier|private
specifier|static
class|class
name|MockFSDirectory
extends|extends
name|FilterDirectory
block|{
DECL|field|allIndexInputs
specifier|final
name|List
argument_list|<
name|IndexInput
argument_list|>
name|allIndexInputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|rand
specifier|final
name|Random
name|rand
decl_stmt|;
DECL|method|MockFSDirectory
specifier|public
name|MockFSDirectory
parameter_list|(
name|Path
name|path
parameter_list|,
name|Random
name|rand
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|rand
operator|=
name|rand
expr_stmt|;
block|}
DECL|method|tweakBufferSizes
specifier|public
name|void
name|tweakBufferSizes
parameter_list|()
block|{
comment|//int count = 0;
for|for
control|(
specifier|final
name|IndexInput
name|ip
range|:
name|allIndexInputs
control|)
block|{
name|BufferedIndexInput
name|bii
init|=
operator|(
name|BufferedIndexInput
operator|)
name|ip
decl_stmt|;
name|int
name|bufferSize
init|=
literal|1024
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
operator|%
literal|32768
argument_list|)
decl_stmt|;
name|bii
operator|.
name|setBufferSize
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
comment|//count++;
block|}
comment|//System.out.println("tweak'd " + count + " buffer sizes");
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make random changes to buffer size
comment|//bufferSize = 1+Math.abs(rand.nextInt() % 10);
name|IndexInput
name|f
init|=
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|allIndexInputs
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
block|}
block|}
end_class
end_unit
