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
name|Closeable
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
name|HashSet
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
name|Set
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
name|CyclicBarrier
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
name|codecs
operator|.
name|Codec
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
name|store
operator|.
name|AlreadyClosedException
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
name|StringHelper
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
begin_class
DECL|class|TestIndexWriterThreadsToSegments
specifier|public
class|class
name|TestIndexWriterThreadsToSegments
extends|extends
name|LuceneTestCase
block|{
comment|// LUCENE-5644: for first segment, two threads each indexed one doc (likely concurrently), but for second segment, each thread indexed the
comment|// doc NOT at the same time, and should have shared the same thread state / segment
DECL|method|testSegmentCountOnFlushBasic
specifier|public
name|void
name|testSegmentCountOnFlushBasic
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
specifier|final
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|startDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|middleGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|finalGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|2
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
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
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
literal|"field"
argument_list|,
literal|"here is some text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|startDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|middleGun
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|threadID
operator|==
literal|0
condition|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|finalGun
operator|.
name|await
argument_list|()
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
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
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|startDone
operator|.
name|await
argument_list|()
expr_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numSegments
init|=
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// 1 segment if the threads ran sequentially, else 2:
name|assertTrue
argument_list|(
name|numSegments
operator|<=
literal|2
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|middleGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|threads
index|[
literal|0
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|finalGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|threads
index|[
literal|1
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Both threads should have shared a single thread state since they did not try to index concurrently:
name|assertEquals
argument_list|(
literal|1
operator|+
name|numSegments
argument_list|,
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
comment|/** Maximum number of simultaneous threads to use for each iteration. */
DECL|field|MAX_THREADS_AT_ONCE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_THREADS_AT_ONCE
init|=
literal|10
decl_stmt|;
DECL|class|CheckSegmentCount
specifier|static
class|class
name|CheckSegmentCount
implements|implements
name|Runnable
implements|,
name|Closeable
block|{
DECL|field|w
specifier|private
specifier|final
name|IndexWriter
name|w
decl_stmt|;
DECL|field|maxThreadCountPerIter
specifier|private
specifier|final
name|AtomicInteger
name|maxThreadCountPerIter
decl_stmt|;
DECL|field|indexingCount
specifier|private
specifier|final
name|AtomicInteger
name|indexingCount
decl_stmt|;
DECL|field|r
specifier|private
name|DirectoryReader
name|r
decl_stmt|;
DECL|method|CheckSegmentCount
specifier|public
name|CheckSegmentCount
parameter_list|(
name|IndexWriter
name|w
parameter_list|,
name|AtomicInteger
name|maxThreadCountPerIter
parameter_list|,
name|AtomicInteger
name|indexingCount
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|w
operator|=
name|w
expr_stmt|;
name|this
operator|.
name|maxThreadCountPerIter
operator|=
name|maxThreadCountPerIter
expr_stmt|;
name|this
operator|.
name|indexingCount
operator|=
name|indexingCount
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|setNextIterThreadCount
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|int
name|oldSegmentCount
init|=
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|r2
expr_stmt|;
name|int
name|maxExpectedSegments
init|=
name|oldSegmentCount
operator|+
name|maxThreadCountPerIter
operator|.
name|get
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
literal|"TEST: iter done; now verify oldSegCount="
operator|+
name|oldSegmentCount
operator|+
literal|" newSegCount="
operator|+
name|r2
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" maxExpected="
operator|+
name|maxExpectedSegments
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: it won't necessarily be ==, in case some threads were strangely scheduled and never conflicted with one another (should be uncommon...?):
name|assertTrue
argument_list|(
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|<=
name|maxExpectedSegments
argument_list|)
expr_stmt|;
name|setNextIterThreadCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|setNextIterThreadCount
specifier|private
name|void
name|setNextIterThreadCount
parameter_list|()
block|{
name|indexingCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|maxThreadCountPerIter
operator|.
name|set
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|MAX_THREADS_AT_ONCE
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"TEST: iter set maxThreadCount="
operator|+
name|maxThreadCountPerIter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// LUCENE-5644: index docs w/ multiple threads but in between flushes we limit how many threads can index concurrently in the next
comment|// iteration, and then verify that no more segments were flushed than number of threads:
DECL|method|testSegmentCountOnFlushRandom
specifier|public
name|void
name|testSegmentCountOnFlushRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
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
decl_stmt|;
comment|// Never trigger flushes (so we only flush on getReader):
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|100000000
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Never trigger merges (so we can simplistically count flushed segments):
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
specifier|final
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
comment|// How many threads are indexing in the current cycle:
specifier|final
name|AtomicInteger
name|indexingCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|// How many threads we will use on each cycle:
specifier|final
name|AtomicInteger
name|maxThreadCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|CheckSegmentCount
name|checker
init|=
operator|new
name|CheckSegmentCount
argument_list|(
name|w
argument_list|,
name|maxThreadCount
argument_list|,
name|indexingCount
argument_list|)
decl_stmt|;
comment|// We spin up 10 threads up front, but then in between flushes we limit how many can run on each iteration
specifier|final
name|int
name|ITERS
init|=
name|TEST_NIGHTLY
condition|?
literal|300
else|:
literal|10
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|MAX_THREADS_AT_ONCE
index|]
decl_stmt|;
comment|// We use this to stop all threads once they've indexed their docs in the current iter, and pull a new NRT reader, and verify the
comment|// segment count:
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
name|MAX_THREADS_AT_ONCE
argument_list|,
name|checker
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
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
name|ITERS
condition|;
name|iter
operator|++
control|)
block|{
if|if
condition|(
name|indexingCount
operator|.
name|incrementAndGet
argument_list|()
operator|<=
name|maxThreadCount
operator|.
name|get
argument_list|()
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
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": do index"
argument_list|)
expr_stmt|;
block|}
comment|// We get to index on this cycle:
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
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|"here is some text that is a bit longer than normal trivial text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|200
condition|;
name|j
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
else|else
block|{
comment|// We lose: no indexing for us on this cycle
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
literal|"TEST: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": don't index"
argument_list|)
expr_stmt|;
block|}
block|}
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
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
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|checker
argument_list|,
name|w
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testManyThreadsClose
specifier|public
name|void
name|testManyThreadsClose
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
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|r
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setCommitOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|w
operator|.
name|setDoRandomForceMerge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
literal|30
argument_list|)
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
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
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
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
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|"here is some text that is a bit longer than normal trivial text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1000
condition|;
name|j
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
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ace
parameter_list|)
block|{
comment|// ok
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
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
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
try|try
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// OK but not required
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|w
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
DECL|method|testDocsStuckInRAMForever
specifier|public
name|void
name|testDocsStuckInRAMForever
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|.2
argument_list|)
expr_stmt|;
name|Codec
name|codec
init|=
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
specifier|final
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
specifier|final
name|CountDownLatch
name|startingGun
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|2
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
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1000
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"threadID"
operator|+
name|threadID
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
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
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|segSeen
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|thread0Count
init|=
literal|0
decl_stmt|;
name|int
name|thread1Count
init|=
literal|0
decl_stmt|;
comment|// At this point the writer should have 2 thread states w/ docs; now we index with only 1 thread until we see all 1000 thread0& thread1
comment|// docs flushed.  If the writer incorrectly holds onto previously indexed docs forever then this will run forever:
name|long
name|counter
init|=
literal|0
decl_stmt|;
name|long
name|checkAt
init|=
literal|100
decl_stmt|;
while|while
condition|(
name|thread0Count
operator|<
literal|1000
operator|||
name|thread1Count
operator|<
literal|1000
condition|)
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"threadIDmain"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
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
name|counter
operator|++
operator|==
name|checkAt
condition|)
block|{
for|for
control|(
name|String
name|fileName
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".si"
argument_list|)
condition|)
block|{
name|String
name|segName
init|=
name|IndexFileNames
operator|.
name|parseSegmentName
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|segSeen
operator|.
name|contains
argument_list|(
name|segName
argument_list|)
operator|==
literal|false
condition|)
block|{
name|segSeen
operator|.
name|add
argument_list|(
name|segName
argument_list|)
expr_stmt|;
name|byte
name|id
index|[]
init|=
name|readSegmentInfoID
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|SegmentInfo
name|si
init|=
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
operator|.
name|segmentInfoFormat
argument_list|()
operator|.
name|read
argument_list|(
name|dir
argument_list|,
name|segName
argument_list|,
name|id
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|si
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|SegmentCommitInfo
name|sci
init|=
operator|new
name|SegmentCommitInfo
argument_list|(
name|si
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|SegmentReader
name|sr
init|=
operator|new
name|SegmentReader
argument_list|(
name|sci
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
try|try
block|{
name|thread0Count
operator|+=
name|sr
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"threadID0"
argument_list|)
argument_list|)
expr_stmt|;
name|thread1Count
operator|+=
name|sr
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"threadID1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|checkAt
operator|=
call|(
name|long
call|)
argument_list|(
name|checkAt
operator|*
literal|1.25
argument_list|)
expr_stmt|;
name|counter
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|w
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
comment|// TODO: remove this hack and fix this test to be better?
comment|// the whole thing relies on default codec too...
DECL|method|readSegmentInfoID
name|byte
index|[]
name|readSegmentInfoID
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|)
block|{
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// magic
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
comment|// codec name
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// version
name|byte
name|id
index|[]
init|=
operator|new
name|byte
index|[
name|StringHelper
operator|.
name|ID_LENGTH
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
block|}
block|}
end_class
end_unit
