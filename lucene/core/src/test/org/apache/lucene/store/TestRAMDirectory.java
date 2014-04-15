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
name|File
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
name|StoredDocument
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
name|util
operator|.
name|English
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * JUnit testcase to test RAMDirectory. RAMDirectory itself is used in many testcases,  * but not one of them uses an different constructor other than the default constructor.  */
end_comment
begin_class
DECL|class|TestRAMDirectory
specifier|public
class|class
name|TestRAMDirectory
extends|extends
name|BaseDirectoryTestCase
block|{
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|File
name|path
parameter_list|)
block|{
return|return
operator|new
name|RAMDirectory
argument_list|()
return|;
block|}
comment|// add enough document so that the index will be larger than RAMDirectory.READ_BUFFER_SIZE
DECL|field|docsToAdd
specifier|private
specifier|final
name|int
name|docsToAdd
init|=
literal|500
decl_stmt|;
DECL|method|buildIndex
specifier|private
name|File
name|buildIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|path
init|=
name|createTempDir
argument_list|(
literal|"buildIndex"
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|path
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
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
comment|// add some documents
name|Document
name|doc
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
name|docsToAdd
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"content"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|.
name|trim
argument_list|()
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
name|assertEquals
argument_list|(
name|docsToAdd
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|path
return|;
block|}
comment|// LUCENE-1468
DECL|method|testCopySubdir
specifier|public
name|void
name|testCopySubdir
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|path
init|=
name|createTempDir
argument_list|(
literal|"testsubdir"
argument_list|)
decl_stmt|;
name|Directory
name|fsDir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|path
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|path
argument_list|,
literal|"subdir"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|fsDir
operator|=
name|newFSDirectory
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|new
name|RAMDirectory
argument_list|(
name|fsDir
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TestUtil
operator|.
name|rm
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|fsDir
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRAMDirectory
specifier|public
name|void
name|testRAMDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|indexDir
init|=
name|buildIndex
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|ramDir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
argument_list|(
name|dir
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// close the underlaying directory
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Check size
name|assertEquals
argument_list|(
name|ramDir
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|ramDir
operator|.
name|getRecomputedSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// open reader to test document count
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|ramDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|docsToAdd
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// open search zo check if all doc's are there
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// search for all documents
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docsToAdd
condition|;
name|i
operator|++
control|)
block|{
name|StoredDocument
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
literal|"content"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// cleanup
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|numThreads
specifier|private
specifier|final
name|int
name|numThreads
init|=
literal|10
decl_stmt|;
DECL|field|docsPerThread
specifier|private
specifier|final
name|int
name|docsPerThread
init|=
literal|40
decl_stmt|;
DECL|method|testRAMDirectorySize
specifier|public
name|void
name|testRAMDirectorySize
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|File
name|indexDir
init|=
name|buildIndex
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
specifier|final
name|MockDirectoryWrapper
name|ramDir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
argument_list|(
name|dir
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ramDir
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
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ramDir
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|ramDir
operator|.
name|getRecomputedSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|num
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
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|docsPerThread
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
literal|"sizeContent"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|num
operator|*
name|docsPerThread
operator|+
name|j
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
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
name|numThreads
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
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
name|numThreads
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ramDir
operator|.
name|sizeInBytes
argument_list|()
argument_list|,
name|ramDir
operator|.
name|getRecomputedSizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
