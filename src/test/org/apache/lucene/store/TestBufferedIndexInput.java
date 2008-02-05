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
name|File
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
name|Iterator
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
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
comment|// The following function generates a fluctuating (but repeatable)
comment|// size, sometimes small (<100) but sometimes large (>10000)
name|int
name|size1
init|=
call|(
name|int
call|)
argument_list|(
name|i
operator|%
literal|7
operator|+
literal|7
operator|*
operator|(
name|i
operator|%
literal|5
operator|)
operator|+
literal|7
operator|*
literal|5
operator|*
operator|(
name|i
operator|%
literal|3
operator|)
operator|+
literal|5
operator|*
literal|5
operator|*
literal|3
operator|*
operator|(
name|i
operator|%
literal|2
operator|)
argument_list|)
decl_stmt|;
name|int
name|size2
init|=
call|(
name|int
call|)
argument_list|(
name|i
operator|%
literal|11
operator|+
literal|11
operator|*
operator|(
name|i
operator|%
literal|7
operator|)
operator|+
literal|11
operator|*
literal|7
operator|*
operator|(
name|i
operator|%
literal|5
operator|)
operator|+
literal|11
operator|*
literal|7
operator|*
literal|5
operator|*
operator|(
name|i
operator|%
literal|3
operator|)
operator|+
literal|11
operator|*
literal|7
operator|*
literal|5
operator|*
literal|3
operator|*
operator|(
name|i
operator|%
literal|2
operator|)
argument_list|)
decl_stmt|;
name|int
name|size
init|=
operator|(
name|i
operator|%
literal|3
operator|==
literal|0
operator|)
condition|?
name|size2
operator|*
literal|10
else|:
name|size1
decl_stmt|;
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
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
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
block|}
block|}
DECL|method|checkReadBytes
specifier|private
name|void
name|checkReadBytes
parameter_list|(
name|BufferedIndexInput
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
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|offset
operator|+
name|size
index|]
decl_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|size
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
name|b
index|[
name|offset
operator|+
name|i
index|]
argument_list|,
name|byten
argument_list|(
name|pos
operator|+
name|i
argument_list|)
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
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{ 		}
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
block|}
DECL|method|testSetBufferSize
specifier|public
name|void
name|testSetBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
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
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
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
operator|new
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
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
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
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
name|Term
name|ccc
init|=
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ccc"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|reader
operator|.
name|docFreq
argument_list|(
name|ccc
argument_list|)
argument_list|,
literal|37
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reader
operator|.
name|docFreq
argument_list|(
name|aaa
argument_list|)
argument_list|,
literal|37
argument_list|)
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reader
operator|.
name|docFreq
argument_list|(
name|bbb
argument_list|)
argument_list|,
literal|37
argument_list|)
expr_stmt|;
name|dir
operator|.
name|tweakBufferSizes
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Hits
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
argument_list|)
decl_stmt|;
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
argument_list|()
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
argument_list|)
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
argument_list|()
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
argument_list|)
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
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
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
name|_TestUtil
operator|.
name|rmDir
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
name|Directory
block|{
DECL|field|allIndexInputs
name|List
name|allIndexInputs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|method|MockFSDirectory
specifier|public
name|MockFSDirectory
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|lockFactory
operator|=
operator|new
name|NoLockFactory
argument_list|()
expr_stmt|;
name|dir
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|openInput
argument_list|(
name|name
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
return|;
block|}
DECL|method|tweakBufferSizes
specifier|public
name|void
name|tweakBufferSizes
parameter_list|()
block|{
name|Iterator
name|it
init|=
name|allIndexInputs
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|BufferedIndexInput
name|bii
init|=
operator|(
name|BufferedIndexInput
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|bufferSize
init|=
literal|1024
operator|+
operator|(
name|int
operator|)
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
name|count
operator|++
expr_stmt|;
block|}
comment|//System.out.println("tweak'd " + count + " buffer sizes");
block|}
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make random changes to buffer size
name|bufferSize
operator|=
literal|1
operator|+
operator|(
name|int
operator|)
name|Math
operator|.
name|abs
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
operator|%
literal|10
argument_list|)
expr_stmt|;
name|IndexInput
name|f
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|bufferSize
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
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|touchFile
specifier|public
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|.
name|touchFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|fileModified
specifier|public
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dir
operator|.
name|fileModified
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dir
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|list
specifier|public
name|String
index|[]
name|list
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dir
operator|.
name|list
argument_list|()
return|;
block|}
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dir
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|renameFile
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|.
name|renameFile
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
