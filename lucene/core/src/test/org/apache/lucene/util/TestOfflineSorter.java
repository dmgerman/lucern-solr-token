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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|IndexOutput
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
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|newDirectoryNoVirusScanner
specifier|private
specifier|static
name|Directory
name|newDirectoryNoVirusScanner
parameter_list|()
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectoryNoVirusScanner
argument_list|()
init|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSingleLine
specifier|public
name|void
name|testSingleLine
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectoryNoVirusScanner
argument_list|()
init|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|)
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
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectoryNoVirusScanner
argument_list|()
init|)
block|{
name|SortInfo
name|info
init|=
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
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
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectoryNoVirusScanner
argument_list|()
init|)
block|{
name|SortInfo
name|sortInfo
init|=
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
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
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectoryNoVirusScanner
argument_list|()
init|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo"
argument_list|,
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
block|}
DECL|method|generateRandom
specifier|private
name|byte
index|[]
index|[]
name|generateRandom
parameter_list|(
name|int
name|howMuchDataInBytes
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
name|howMuchDataInBytes
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
name|howMuchDataInBytes
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
block|{
return|return
name|diff
return|;
block|}
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
name|Directory
name|dir
parameter_list|,
name|OfflineSorter
name|sorter
parameter_list|,
name|byte
index|[]
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|unsorted
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"unsorted"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|writeAll
argument_list|(
name|unsorted
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|IndexOutput
name|golden
init|=
name|dir
operator|.
name|createTempOutput
argument_list|(
literal|"golden"
argument_list|,
literal|"tmp"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
name|writeAll
argument_list|(
name|golden
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|String
name|sorted
init|=
name|sorter
operator|.
name|sort
argument_list|(
name|unsorted
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//System.out.println("Input size [MB]: " + unsorted.length() / (1024 * 1024));
comment|//System.out.println(sortInfo);
name|assertFilesIdentical
argument_list|(
name|dir
argument_list|,
name|golden
operator|.
name|getName
argument_list|()
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
return|return
name|sorter
operator|.
name|sortInfo
return|;
block|}
comment|/**    * Make sure two files are byte-byte identical.    */
DECL|method|assertFilesIdentical
specifier|private
name|void
name|assertFilesIdentical
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|golden
parameter_list|,
name|String
name|sorted
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|numBytes
init|=
name|dir
operator|.
name|fileLength
argument_list|(
name|golden
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numBytes
argument_list|,
name|dir
operator|.
name|fileLength
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
try|try
init|(
name|IndexInput
name|in1
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|golden
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|;
name|IndexInput
name|in2
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|sorted
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
init|)
block|{
name|long
name|left
init|=
name|numBytes
decl_stmt|;
while|while
condition|(
name|left
operator|>
literal|0
condition|)
block|{
name|int
name|chunk
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|buf1
operator|.
name|length
argument_list|,
name|left
argument_list|)
decl_stmt|;
name|left
operator|-=
name|chunk
expr_stmt|;
name|in1
operator|.
name|readBytes
argument_list|(
name|buf1
argument_list|,
literal|0
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|in2
operator|.
name|readBytes
argument_list|(
name|buf2
argument_list|,
literal|0
argument_list|,
name|chunk
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
name|chunk
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
block|}
block|}
comment|/** NOTE: closes the provided {@link IndexOutput} */
DECL|method|writeAll
specifier|private
name|void
name|writeAll
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|byte
index|[]
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ByteSequencesWriter
name|w
init|=
operator|new
name|OfflineSorter
operator|.
name|ByteSequencesWriter
argument_list|(
name|out
argument_list|)
init|)
block|{
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
block|}
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
DECL|method|testThreadSafety
specifier|public
name|void
name|testThreadSafety
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
index|]
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
try|try
init|(
name|Directory
name|dir
init|=
name|newDirectoryNoVirusScanner
argument_list|()
init|)
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|threadID
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
operator|&&
name|failed
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|;
name|iter
operator|++
control|)
block|{
name|checkSort
argument_list|(
name|dir
argument_list|,
operator|new
name|OfflineSorter
argument_list|(
name|dir
argument_list|,
literal|"foo_"
operator|+
name|threadID
operator|+
literal|"_"
operator|+
name|iter
argument_list|)
argument_list|,
name|generateRandom
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|failed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
