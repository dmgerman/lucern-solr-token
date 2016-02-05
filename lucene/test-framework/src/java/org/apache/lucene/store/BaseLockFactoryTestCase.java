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
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|AccessDeniedException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
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
name|Query
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
name|Constants
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
name|PrintStreamInfoStream
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
begin_comment
comment|/** Base class for per-LockFactory tests. */
end_comment
begin_class
DECL|class|BaseLockFactoryTestCase
specifier|public
specifier|abstract
class|class
name|BaseLockFactoryTestCase
extends|extends
name|LuceneTestCase
block|{
comment|/** Subclass returns the Directory to be tested; if it's    *  an FS-based directory it should point to the specified    *  path, else it can ignore it. */
DECL|method|getDirectory
specifier|protected
specifier|abstract
name|Directory
name|getDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Test obtaining and releasing locks, checking validity */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|tempPath
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|tempPath
argument_list|)
decl_stmt|;
name|Lock
name|l
init|=
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
try|try
block|{
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"succeeded in obtaining lock twice, didn't get exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|expected
parameter_list|)
block|{}
name|l
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make sure we can obtain first one again:
name|l
operator|=
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
name|l
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
comment|/** Test closing locks twice */
DECL|method|testDoubleClose
specifier|public
name|void
name|testDoubleClose
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|tempPath
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|tempPath
argument_list|)
decl_stmt|;
name|Lock
name|l
init|=
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
name|l
operator|.
name|close
argument_list|()
expr_stmt|;
name|l
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close again, should be no exception
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test ensureValid returns true after acquire */
DECL|method|testValidAfterAcquire
specifier|public
name|void
name|testValidAfterAcquire
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|tempPath
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|tempPath
argument_list|)
decl_stmt|;
name|Lock
name|l
init|=
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
name|l
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
comment|// no exception
name|l
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
comment|/** Test ensureValid throws exception after close */
DECL|method|testInvalidAfterClose
specifier|public
name|void
name|testInvalidAfterClose
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|tempPath
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|tempPath
argument_list|)
decl_stmt|;
name|Lock
name|l
init|=
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
name|l
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|l
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|expected
parameter_list|)
block|{}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testObtainConcurrently
specifier|public
name|void
name|testObtainConcurrently
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|Path
name|tempPath
init|=
name|createTempDir
argument_list|()
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|getDirectory
argument_list|(
name|tempPath
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|atomicCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|ReentrantLock
name|assertingLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
name|int
name|numThreads
init|=
literal|2
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|int
name|runs
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
name|numThreads
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
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
name|barrier
operator|.
name|await
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
while|while
condition|(
name|running
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
init|(
name|Lock
name|lock
init|=
name|directory
operator|.
name|obtainLock
argument_list|(
literal|"foo.lock"
argument_list|)
init|)
block|{
name|assertFalse
argument_list|(
name|assertingLock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|assertingLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
name|assertingLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
assert|assert
name|lock
operator|!=
literal|null
assert|;
comment|// stupid compiler
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//
block|}
if|if
condition|(
name|atomicCounter
operator|.
name|incrementAndGet
argument_list|()
operator|>
name|runs
condition|)
block|{
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
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
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Verify: do stress test, by opening IndexReaders and
comment|// IndexWriters over& over in 2 threads and making sure
comment|// no unexpected exceptions are raised:
DECL|method|testStressLocks
specifier|public
name|void
name|testStressLocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tempPath
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|assumeFalse
argument_list|(
literal|"cannot handle buggy Files.delete"
argument_list|,
name|TestUtil
operator|.
name|hasWindowsFS
argument_list|(
name|tempPath
argument_list|)
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|tempPath
argument_list|)
decl_stmt|;
comment|// First create a 1 doc index:
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
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|WriterThread
name|writer
init|=
operator|new
name|WriterThread
argument_list|(
literal|100
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|SearcherThread
name|searcher
init|=
operator|new
name|SearcherThread
argument_list|(
literal|100
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|writer
operator|.
name|start
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|writer
operator|.
name|isAlive
argument_list|()
operator|||
name|searcher
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"IndexWriter hit unexpected exceptions"
argument_list|,
operator|!
name|writer
operator|.
name|hitException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"IndexSearcher hit unexpected exceptions"
argument_list|,
operator|!
name|searcher
operator|.
name|hitException
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
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
literal|"aaa"
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
DECL|class|WriterThread
specifier|private
class|class
name|WriterThread
extends|extends
name|Thread
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|numIteration
specifier|private
name|int
name|numIteration
decl_stmt|;
DECL|field|hitException
specifier|public
name|boolean
name|hitException
init|=
literal|false
decl_stmt|;
DECL|method|WriterThread
specifier|public
name|WriterThread
parameter_list|(
name|int
name|numIteration
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|numIteration
operator|=
name|numIteration
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
DECL|method|toString
specifier|private
name|String
name|toString
parameter_list|(
name|ByteArrayOutputStream
name|baos
parameter_list|)
block|{
try|try
block|{
return|return
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
comment|// shouldn't happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|uee
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|IndexWriter
name|writer
init|=
literal|null
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
name|this
operator|.
name|numIteration
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
literal|"TEST: WriterThread iter="
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
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
comment|// We only print the IW infoStream output on exc, below:
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|iwc
operator|.
name|setInfoStream
argument_list|(
operator|new
name|PrintStreamInfoStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|,
literal|true
argument_list|,
literal|"UTF8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
comment|// shouldn't happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|uee
argument_list|)
throw|;
block|}
name|iwc
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|e
parameter_list|)
block|{
comment|// lock obtain timed out
comment|// NOTE: we should at some point
comment|// consider this a failure?  The lock
comment|// obtains, across IndexReader&
comment|// IndexWriters should be "fair" (ie
comment|// FIFO).
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
operator|&&
name|t
operator|instanceof
name|AccessDeniedException
condition|)
block|{
comment|// LUCENE-6684: suppress this: on Windows, a file in the curious "pending delete" state can
comment|// cause this exc on IW init, where one thread/process deleted an old
comment|// segments_N, but the delete hasn't finished yet because other threads/processes
comment|// still have it open
block|}
else|else
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Writer: creation hit unexpected exception: "
operator|+
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|toString
argument_list|(
name|baos
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|addDoc
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Writer: addDoc hit unexpected exception: "
operator|+
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|toString
argument_list|(
name|baos
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Writer: close hit unexpected exception: "
operator|+
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|toString
argument_list|(
name|baos
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|SearcherThread
specifier|private
class|class
name|SearcherThread
extends|extends
name|Thread
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|numIteration
specifier|private
name|int
name|numIteration
decl_stmt|;
DECL|field|hitException
specifier|public
name|boolean
name|hitException
init|=
literal|false
decl_stmt|;
DECL|method|SearcherThread
specifier|public
name|SearcherThread
parameter_list|(
name|int
name|numIteration
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|numIteration
operator|=
name|numIteration
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
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
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"aaa"
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
name|this
operator|.
name|numIteration
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Searcher: create hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
try|try
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Searcher: search hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// System.out.println(hits.length() + " total results");
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|hitException
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stress Test Index Searcher: close hit unexpected exception: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
