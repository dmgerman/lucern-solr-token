begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|DataInputStream
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Files
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|OfflineSorter
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
name|OfflineSorter
operator|.
name|BufferSize
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
name|OfflineSorter
operator|.
name|ByteSequencesWriter
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
name|OfflineSorter
operator|.
name|SortInfo
import|;
end_import
begin_comment
comment|/**  * Tests for on-disk merge sorting.  */
end_comment
begin_class
DECL|class|TestOfflineSorter
specifier|public
class|class
name|TestOfflineSorter
extends|extends
name|LuceneTestCase
block|{
DECL|field|tempDir
specifier|private
name|Path
name|tempDir
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
name|tempDir
operator|=
name|createTempDir
argument_list|(
literal|"mergesort"
argument_list|)
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
name|tempDir
operator|!=
literal|null
condition|)
name|IOUtils
operator|.
name|rm
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSort
argument_list|(
operator|new
name|OfflineSorter
argument_list|()
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleLine
specifier|public
name|void
name|testSingleLine
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSort
argument_list|(
operator|new
name|OfflineSorter
argument_list|()
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{
literal|"Single line only."
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntermediateMerges
specifier|public
name|void
name|testIntermediateMerges
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sort 20 mb worth of data with 1mb buffer, binary merging.
name|SortInfo
name|info
init|=
name|checkSort
argument_list|(
operator|new
name|OfflineSorter
argument_list|(
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
argument_list|,
name|OfflineSorter
operator|.
name|defaultTempDir
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
operator|(
name|int
operator|)
name|OfflineSorter
operator|.
name|MB
operator|*
literal|20
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|info
operator|.
name|mergeRounds
operator|>
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|testSmallRandom
specifier|public
name|void
name|testSmallRandom
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sort 20 mb worth of data with 1mb buffer.
name|SortInfo
name|sortInfo
init|=
name|checkSort
argument_list|(
operator|new
name|OfflineSorter
argument_list|(
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
argument_list|,
name|OfflineSorter
operator|.
name|defaultTempDir
argument_list|()
argument_list|,
name|OfflineSorter
operator|.
name|MAX_TEMPFILES
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
operator|(
name|int
operator|)
name|OfflineSorter
operator|.
name|MB
operator|*
literal|20
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sortInfo
operator|.
name|mergeRounds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nightly
DECL|method|testLargerRandom
specifier|public
name|void
name|testLargerRandom
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sort 100MB worth of data with 15mb buffer.
name|checkSort
argument_list|(
operator|new
name|OfflineSorter
argument_list|(
name|OfflineSorter
operator|.
name|DEFAULT_COMPARATOR
argument_list|,
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|16
argument_list|)
argument_list|,
name|OfflineSorter
operator|.
name|defaultTempDir
argument_list|()
argument_list|,
name|OfflineSorter
operator|.
name|MAX_TEMPFILES
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
operator|(
name|int
operator|)
name|OfflineSorter
operator|.
name|MB
operator|*
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|generateRandom
specifier|private
name|byte
index|[]
index|[]
name|generateRandom
parameter_list|(
name|int
name|howMuchData
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|howMuchData
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|current
init|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|howMuchData
operator|-=
name|current
operator|.
name|length
expr_stmt|;
block|}
name|byte
index|[]
index|[]
name|bytes
init|=
name|data
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
name|data
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
decl_stmt|;
return|return
name|bytes
return|;
block|}
DECL|field|unsignedByteOrderComparator
specifier|static
specifier|final
name|Comparator
argument_list|<
name|byte
index|[]
argument_list|>
name|unsignedByteOrderComparator
init|=
operator|new
name|Comparator
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|left
parameter_list|,
name|byte
index|[]
name|right
parameter_list|)
block|{
specifier|final
name|int
name|max
init|=
name|Math
operator|.
name|min
argument_list|(
name|left
operator|.
name|length
argument_list|,
name|right
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
incr|,
name|j
operator|++
control|)
block|{
name|int
name|diff
init|=
operator|(
name|left
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|-
operator|(
name|right
index|[
name|j
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
return|return
name|diff
return|;
block|}
return|return
name|left
operator|.
name|length
operator|-
name|right
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Check sorting data on an instance of {@link OfflineSorter}.    */
DECL|method|checkSort
specifier|private
name|SortInfo
name|checkSort
parameter_list|(
name|OfflineSorter
name|sort
parameter_list|,
name|byte
index|[]
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|unsorted
init|=
name|writeAll
argument_list|(
literal|"unsorted"
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|data
argument_list|,
name|unsignedByteOrderComparator
argument_list|)
expr_stmt|;
name|Path
name|golden
init|=
name|writeAll
argument_list|(
literal|"golden"
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|Path
name|sorted
init|=
name|tempDir
operator|.
name|resolve
argument_list|(
literal|"sorted"
argument_list|)
decl_stmt|;
name|SortInfo
name|sortInfo
init|=
name|sort
operator|.
name|sort
argument_list|(
name|unsorted
argument_list|,
name|sorted
argument_list|)
decl_stmt|;
comment|//System.out.println("Input size [MB]: " + unsorted.length() / (1024 * 1024));
comment|//System.out.println(sortInfo);
name|assertFilesIdentical
argument_list|(
name|golden
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
return|return
name|sortInfo
return|;
block|}
comment|/**    * Make sure two files are byte-byte identical.    */
DECL|method|assertFilesIdentical
specifier|private
name|void
name|assertFilesIdentical
parameter_list|(
name|Path
name|golden
parameter_list|,
name|Path
name|sorted
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|Files
operator|.
name|size
argument_list|(
name|golden
argument_list|)
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|sorted
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf1
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
name|byte
index|[]
name|buf2
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
name|DataInputStream
name|is1
init|=
operator|new
name|DataInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|golden
argument_list|)
argument_list|)
decl_stmt|;
name|DataInputStream
name|is2
init|=
operator|new
name|DataInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|sorted
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|is1
operator|.
name|read
argument_list|(
name|buf1
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|is2
operator|.
name|readFully
argument_list|(
name|buf2
argument_list|,
literal|0
argument_list|,
name|len
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|buf1
index|[
name|i
index|]
argument_list|,
name|buf2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|is1
argument_list|,
name|is2
argument_list|)
expr_stmt|;
block|}
DECL|method|writeAll
specifier|private
name|Path
name|writeAll
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
name|tempDir
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|ByteSequencesWriter
name|w
init|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesWriter
argument_list|(
name|file
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|datum
range|:
name|data
control|)
block|{
name|w
operator|.
name|write
argument_list|(
name|datum
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|file
return|;
block|}
DECL|method|testRamBuffer
specifier|public
name|void
name|testRamBuffer
parameter_list|()
block|{
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|10000
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
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2047
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|2047
argument_list|)
expr_stmt|;
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"max mb is 2047"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{     }
try|try
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"min mb is 0.5"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{     }
try|try
block|{
name|BufferSize
operator|.
name|megabytes
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"min mb is 0.5"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class
end_unit
