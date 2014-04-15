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
name|SortedDocValuesField
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
name|BaseDirectoryWrapper
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
operator|.
name|SuppressCodecs
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
name|TimeUnits
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|TimeoutSuite
import|;
end_import
begin_class
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|80
operator|*
name|TimeUnits
operator|.
name|HOUR
argument_list|)
annotation|@
name|Ignore
argument_list|(
literal|"very slow"
argument_list|)
DECL|class|Test2BSortedDocValues
specifier|public
class|class
name|Test2BSortedDocValues
extends|extends
name|LuceneTestCase
block|{
comment|// indexes Integer.MAX_VALUE docs with a fixed binary field
DECL|method|testFixedSorted
specifier|public
name|void
name|testFixedSorted
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BFixedSorted"
argument_list|)
argument_list|)
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
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|256.0
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|10
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
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
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
name|BytesRef
name|data
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|SortedDocValuesField
name|dvField
init|=
operator|new
name|SortedDocValuesField
argument_list|(
literal|"dv"
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvField
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
name|Integer
operator|.
name|MAX_VALUE
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"indexed: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"verifying..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|int
name|expectedValue
init|=
literal|0
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|r
operator|.
name|leaves
argument_list|()
control|)
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BinaryDocValues
name|dv
init|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
literal|"dv"
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|expectedValue
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|expectedValue
expr_stmt|;
name|dv
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|expectedValue
operator|++
expr_stmt|;
block|}
block|}
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
comment|// indexes Integer.MAX_VALUE docs with a fixed binary field
DECL|method|test2BOrds
specifier|public
name|void
name|test2BOrds
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
literal|"2BOrds"
argument_list|)
argument_list|)
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
name|setThrottling
argument_list|(
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|256.0
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|10
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
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
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|BytesRef
name|data
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|SortedDocValuesField
name|dvField
init|=
operator|new
name|SortedDocValuesField
argument_list|(
literal|"dv"
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|dvField
argument_list|)
expr_stmt|;
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
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
name|Integer
operator|.
name|MAX_VALUE
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"indexed: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"verifying..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|random
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|r
operator|.
name|leaves
argument_list|()
control|)
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BinaryDocValues
name|dv
init|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
literal|"dv"
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|random
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|dv
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// TODO: variable
block|}
end_class
end_unit
