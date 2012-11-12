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
name|CountDownLatch
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
name|TimeUnit
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
begin_class
DECL|class|TestConcurrentMergeScheduler
specifier|public
class|class
name|TestConcurrentMergeScheduler
extends|extends
name|LuceneTestCase
block|{
DECL|class|FailOnlyOnFlush
specifier|private
class|class
name|FailOnlyOnFlush
extends|extends
name|MockDirectoryWrapper
operator|.
name|Failure
block|{
DECL|field|doFail
name|boolean
name|doFail
decl_stmt|;
DECL|field|hitExc
name|boolean
name|hitExc
decl_stmt|;
annotation|@
name|Override
DECL|method|setDoFail
specifier|public
name|void
name|setDoFail
parameter_list|()
block|{
name|this
operator|.
name|doFail
operator|=
literal|true
expr_stmt|;
name|hitExc
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearDoFail
specifier|public
name|void
name|clearDoFail
parameter_list|()
block|{
name|this
operator|.
name|doFail
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doFail
operator|&&
name|isTestThread
argument_list|()
condition|)
block|{
name|boolean
name|isDoFlush
init|=
literal|false
decl_stmt|;
name|boolean
name|isClose
init|=
literal|false
decl_stmt|;
name|StackTraceElement
index|[]
name|trace
init|=
operator|new
name|Exception
argument_list|()
operator|.
name|getStackTrace
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
name|trace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"flush"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
condition|)
block|{
name|isDoFlush
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
literal|"close"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
condition|)
block|{
name|isClose
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isDoFlush
operator|&&
operator|!
name|isClose
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|hitExc
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": now failing during flush"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|// Make sure running BG merges still work fine even when
comment|// we are hitting exceptions during flushing.
DECL|method|testFlushExceptions
specifier|public
name|void
name|testFlushExceptions
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|directory
init|=
name|newMockDirectory
argument_list|()
decl_stmt|;
name|FailOnlyOnFlush
name|failure
init|=
operator|new
name|FailOnlyOnFlush
argument_list|()
decl_stmt|;
name|directory
operator|.
name|failOn
argument_list|(
name|failure
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
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
literal|2
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
name|Field
name|idField
init|=
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|int
name|extraCount
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
literal|"TEST: iter="
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|*
literal|20
operator|+
name|j
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
comment|// must cycle here because sometimes the merge flushes
comment|// the doc we just added and so there's nothing to
comment|// flush, and we don't hit the exception
while|while
condition|(
literal|true
condition|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|failure
operator|.
name|setDoFail
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|failure
operator|.
name|hitExc
condition|)
block|{
name|fail
argument_list|(
literal|"failed to hit IOException"
argument_list|)
expr_stmt|;
block|}
name|extraCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
name|failure
operator|.
name|clearDoFail
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|assertEquals
argument_list|(
literal|20
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
name|extraCount
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
operator|+
name|extraCount
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Test that deletes committed after a merge started and
comment|// before it finishes, are correctly merged back:
DECL|method|testDeleteMerging
specifier|public
name|void
name|testDeleteMerging
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|LogDocMergePolicy
name|mp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
comment|// Force degenerate merging so we can get a mix of
comment|// merging of segments with and without deletes at the
comment|// start:
name|mp
operator|.
name|setMinMergeDocs
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
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
name|setMergePolicy
argument_list|(
name|mp
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
name|Field
name|idField
init|=
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
literal|"\nTEST: cycle"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|*
literal|100
operator|+
name|j
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
name|int
name|delID
init|=
name|i
decl_stmt|;
while|while
condition|(
name|delID
operator|<
literal|100
operator|*
operator|(
literal|1
operator|+
name|i
operator|)
condition|)
block|{
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
literal|"TEST: del "
operator|+
name|delID
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|delID
argument_list|)
argument_list|)
expr_stmt|;
name|delID
operator|+=
literal|10
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// Verify that we did not lose any deletes...
name|assertEquals
argument_list|(
literal|450
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNoExtraFiles
specifier|public
name|void
name|testNoExtraFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
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
literal|2
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|7
condition|;
name|iter
operator|++
control|)
block|{
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
literal|"TEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|21
condition|;
name|j
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
literal|"a b c"
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
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestIndexWriter
operator|.
name|assertNoUnreferencedFiles
argument_list|(
name|directory
argument_list|,
literal|"testNoExtraFiles"
argument_list|)
expr_stmt|;
comment|// Reopen
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
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
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNoWaitClose
specifier|public
name|void
name|testNoWaitClose
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|idField
init|=
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
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
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
condition|;
name|iter
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|201
condition|;
name|j
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|iter
operator|*
literal|201
operator|+
name|j
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
name|int
name|delID
init|=
name|iter
operator|*
literal|201
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|delID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|delID
operator|+=
literal|5
expr_stmt|;
block|}
comment|// Force a bunch of merge threads to kick off so we
comment|// stress out aborting them on close:
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
literal|3
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
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|1
operator|+
name|iter
operator|)
operator|*
literal|182
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Reopen
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
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
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-4544
DECL|method|testMaxMergeCount
specifier|public
name|void
name|testMaxMergeCount
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
name|IndexWriterConfig
name|iwc
init|=
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
decl_stmt|;
specifier|final
name|int
name|maxMergeCount
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxMergeThreads
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|maxMergeCount
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|enoughMergesWaiting
init|=
operator|new
name|CountDownLatch
argument_list|(
name|maxMergeCount
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|runningMergeCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
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
literal|"TEST: maxMergeCount="
operator|+
name|maxMergeCount
operator|+
literal|" maxMergeThreads="
operator|+
name|maxMergeThreads
argument_list|)
expr_stmt|;
block|}
name|ConcurrentMergeScheduler
name|cms
init|=
operator|new
name|ConcurrentMergeScheduler
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// Stall all incoming merges until we see
comment|// maxMergeCount:
name|int
name|count
init|=
name|runningMergeCount
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
literal|"count="
operator|+
name|count
operator|+
literal|" vs maxMergeCount="
operator|+
name|maxMergeCount
argument_list|,
name|count
operator|<=
name|maxMergeCount
argument_list|)
expr_stmt|;
name|enoughMergesWaiting
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// Stall this merge until we see exactly
comment|// maxMergeCount merges waiting
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|enoughMergesWaiting
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|||
name|failed
operator|.
name|get
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
comment|// Then sleep a bit to give a chance for the bug
comment|// (too many pending merges) to appear:
name|Thread
operator|.
name|sleep
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|super
operator|.
name|doMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|runningMergeCount
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|mergeFinish
argument_list|(
name|merge
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|maxMergeThreads
operator|>
name|cms
operator|.
name|getMaxMergeCount
argument_list|()
condition|)
block|{
name|cms
operator|.
name|setMaxMergeCount
argument_list|(
name|maxMergeCount
argument_list|)
expr_stmt|;
block|}
name|cms
operator|.
name|setMaxThreadCount
argument_list|(
name|maxMergeThreads
argument_list|)
expr_stmt|;
name|cms
operator|.
name|setMaxMergeCount
argument_list|(
name|maxMergeCount
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
name|cms
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|TieredMergePolicy
name|tmp
init|=
operator|new
name|TieredMergePolicy
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setMaxMergeAtOnce
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setSegmentsPerTier
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
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
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"field"
argument_list|,
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|enoughMergesWaiting
operator|.
name|getCount
argument_list|()
operator|!=
literal|0
operator|&&
operator|!
name|failed
operator|.
name|get
argument_list|()
condition|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|close
argument_list|(
literal|false
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
