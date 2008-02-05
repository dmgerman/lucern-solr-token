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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|SimpleAnalyzer
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
name|Analyzer
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
name|MockRAMDirectory
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
name|util
operator|.
name|LuceneTestCase
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
begin_class
DECL|class|TestConcurrentMergeScheduler
specifier|public
class|class
name|TestConcurrentMergeScheduler
extends|extends
name|LuceneTestCase
block|{
DECL|field|ANALYZER
specifier|private
specifier|static
specifier|final
name|Analyzer
name|ANALYZER
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
DECL|class|FailOnlyOnFlush
specifier|private
specifier|static
class|class
name|FailOnlyOnFlush
extends|extends
name|MockRAMDirectory
operator|.
name|Failure
block|{
DECL|field|doFail
name|boolean
name|doFail
init|=
literal|false
decl_stmt|;
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
block|}
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
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockRAMDirectory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doFail
condition|)
block|{
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
literal|"doFlush"
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
comment|//new RuntimeException().printStackTrace(System.out);
throw|throw
operator|new
name|IOException
argument_list|(
literal|"now failing during flush"
argument_list|)
throw|;
block|}
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
name|MockRAMDirectory
name|directory
init|=
operator|new
name|MockRAMDirectory
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
name|ANALYZER
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
name|ConcurrentMergeScheduler
name|cms
init|=
operator|new
name|ConcurrentMergeScheduler
argument_list|()
decl_stmt|;
name|writer
operator|.
name|setMergeScheduler
argument_list|(
name|cms
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
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
operator|new
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
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
name|setValue
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
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"failed to hit IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|failure
operator|.
name|clearDoFail
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
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
name|RAMDirectory
name|directory
init|=
operator|new
name|MockRAMDirectory
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
name|ANALYZER
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
name|ConcurrentMergeScheduler
name|cms
init|=
operator|new
name|ConcurrentMergeScheduler
argument_list|()
decl_stmt|;
name|writer
operator|.
name|setMergeScheduler
argument_list|(
name|cms
argument_list|)
expr_stmt|;
name|LogDocMergePolicy
name|mp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|writer
operator|.
name|setMergePolicy
argument_list|(
name|mp
argument_list|)
expr_stmt|;
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
operator|new
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
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
name|setValue
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
name|flush
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
name|IndexReader
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
name|RAMDirectory
name|directory
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
name|pass
operator|==
literal|0
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|autoCommit
argument_list|,
name|ANALYZER
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
name|ConcurrentMergeScheduler
name|cms
init|=
operator|new
name|ConcurrentMergeScheduler
argument_list|()
decl_stmt|;
name|writer
operator|.
name|setMergeScheduler
argument_list|(
name|cms
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
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
operator|new
name|Field
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
name|TestIndexWriter
operator|.
name|assertNoUnreferencedFiles
argument_list|(
name|directory
argument_list|,
literal|"testNoExtraFiles autoCommit="
operator|+
name|autoCommit
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
name|autoCommit
argument_list|,
name|ANALYZER
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|RAMDirectory
name|directory
init|=
operator|new
name|MockRAMDirectory
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
operator|new
name|Field
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
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
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
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
block|{
name|boolean
name|autoCommit
init|=
name|pass
operator|==
literal|0
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|autoCommit
argument_list|,
name|ANALYZER
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
name|ConcurrentMergeScheduler
name|cms
init|=
operator|new
name|ConcurrentMergeScheduler
argument_list|()
decl_stmt|;
name|writer
operator|.
name|setMergeScheduler
argument_list|(
name|cms
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|100
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
literal|201
condition|;
name|j
operator|++
control|)
block|{
name|idField
operator|.
name|setValue
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
name|writer
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
name|flush
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
name|IndexReader
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
name|autoCommit
argument_list|,
name|ANALYZER
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
