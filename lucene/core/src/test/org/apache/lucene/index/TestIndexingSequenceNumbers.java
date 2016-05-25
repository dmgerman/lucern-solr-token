begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashSet
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
name|StoredField
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
name|StringField
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
name|search
operator|.
name|TopDocs
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
name|TestUtil
import|;
end_import
begin_class
DECL|class|TestIndexingSequenceNumbers
specifier|public
class|class
name|TestIndexingSequenceNumbers
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
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
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|a
init|=
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|b
init|=
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|>=
name|a
argument_list|)
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
DECL|method|testAfterRefresh
specifier|public
name|void
name|testAfterRefresh
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
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|a
init|=
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
decl_stmt|;
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|b
init|=
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|>
name|a
argument_list|)
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
DECL|method|testAfterCommit
specifier|public
name|void
name|testAfterCommit
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
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|a
init|=
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|long
name|b
init|=
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|>
name|a
argument_list|)
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
DECL|method|testStressUpdateSameID
specifier|public
name|void
name|testStressUpdateSameID
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
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
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
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
literal|2
argument_list|,
literal|5
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
specifier|final
name|long
index|[]
name|seqNos
init|=
operator|new
name|long
index|[
name|threads
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|Term
name|id
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
comment|// multiple threads update the same document
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
name|StoredField
argument_list|(
literal|"thread"
argument_list|,
name|threadID
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"id"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
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
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|seqNos
index|[
name|threadID
index|]
operator|=
name|w
operator|.
name|updateDocument
argument_list|(
name|id
argument_list|,
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
comment|// now confirm that the reported sequence numbers agree with the index:
name|int
name|maxThread
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|Long
argument_list|>
name|allSeqNos
init|=
operator|new
name|HashSet
argument_list|<>
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|allSeqNos
operator|.
name|add
argument_list|(
name|seqNos
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|seqNos
index|[
name|i
index|]
operator|>
name|seqNos
index|[
name|maxThread
index|]
condition|)
block|{
name|maxThread
operator|=
name|i
expr_stmt|;
block|}
block|}
comment|// make sure all sequence numbers were different
name|assertEquals
argument_list|(
name|threads
operator|.
name|length
argument_list|,
name|allSeqNos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|id
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|r
operator|.
name|document
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|maxThread
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"thread"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
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
block|}
DECL|class|Operation
specifier|static
class|class
name|Operation
block|{
comment|// 0 = update, 1 = delete, 2 = commit
DECL|field|what
name|byte
name|what
decl_stmt|;
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|threadID
name|int
name|threadID
decl_stmt|;
DECL|field|seqNo
name|long
name|seqNo
decl_stmt|;
block|}
DECL|method|testStressConcurrentCommit
specifier|public
name|void
name|testStressConcurrentCommit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|opCount
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|idCount
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|NoDeletionPolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
comment|// Cannot use RIW since it randomly commits:
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
name|int
name|numThreads
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
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
comment|//System.out.println("TEST: iter=" + iter + " opCount=" + opCount + " idCount=" + idCount + " threadCount=" + threads.length);
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
name|List
argument_list|<
name|List
argument_list|<
name|Operation
argument_list|>
argument_list|>
name|threadOps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Object
name|commitLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Operation
argument_list|>
name|commits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// multiple threads update the same set of documents, and we randomly commit
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
name|List
argument_list|<
name|Operation
argument_list|>
name|ops
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|threadOps
operator|.
name|add
argument_list|(
name|ops
argument_list|)
expr_stmt|;
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|opCount
condition|;
name|i
operator|++
control|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|op
operator|.
name|threadID
operator|=
name|threadID
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
operator|==
literal|17
condition|)
block|{
name|op
operator|.
name|what
operator|=
literal|2
expr_stmt|;
synchronized|synchronized
init|(
name|commitLock
init|)
block|{
name|op
operator|.
name|seqNo
operator|=
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|op
operator|.
name|seqNo
operator|!=
operator|-
literal|1
condition|)
block|{
name|commits
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|op
operator|.
name|id
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|idCount
argument_list|)
expr_stmt|;
name|Term
name|idTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|op
operator|.
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|1
condition|)
block|{
name|op
operator|.
name|what
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|op
operator|.
name|seqNo
operator|=
name|w
operator|.
name|deleteDocuments
argument_list|(
name|idTerm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|op
operator|.
name|seqNo
operator|=
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|idTerm
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|StoredField
argument_list|(
literal|"thread"
argument_list|,
name|threadID
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|op
operator|.
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|op
operator|.
name|seqNo
operator|=
name|w
operator|.
name|updateDocuments
argument_list|(
name|idTerm
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|op
operator|.
name|seqNo
operator|=
name|w
operator|.
name|updateDocument
argument_list|(
name|idTerm
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|op
operator|.
name|what
operator|=
literal|2
expr_stmt|;
block|}
name|ops
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
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
name|Operation
name|commitOp
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|commitOp
operator|.
name|seqNo
operator|=
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|commitOp
operator|.
name|seqNo
operator|!=
operator|-
literal|1
condition|)
block|{
name|commits
operator|.
name|add
argument_list|(
name|commitOp
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|indexCommits
init|=
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|commits
operator|.
name|size
argument_list|()
argument_list|,
name|indexCommits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
index|[]
name|expectedThreadIDs
init|=
operator|new
name|int
index|[
name|idCount
index|]
decl_stmt|;
name|long
index|[]
name|seqNos
init|=
operator|new
name|long
index|[
name|idCount
index|]
decl_stmt|;
comment|//System.out.println("TEST: " + commits.size() + " commits");
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|commits
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// this commit point should reflect all operations<= this seqNo
name|long
name|commitSeqNo
init|=
name|commits
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|seqNo
decl_stmt|;
comment|//System.out.println("  commit " + i + ": seqNo=" + commitSeqNo + " segs=" + indexCommits.get(i));
name|Arrays
operator|.
name|fill
argument_list|(
name|expectedThreadIDs
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|seqNos
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|threadID
init|=
literal|0
init|;
name|threadID
operator|<
name|threadOps
operator|.
name|size
argument_list|()
condition|;
name|threadID
operator|++
control|)
block|{
name|long
name|lastSeqNo
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Operation
name|op
range|:
name|threadOps
operator|.
name|get
argument_list|(
name|threadID
argument_list|)
control|)
block|{
if|if
condition|(
name|op
operator|.
name|seqNo
operator|<=
name|commitSeqNo
operator|&&
name|op
operator|.
name|seqNo
operator|>
name|seqNos
index|[
name|op
operator|.
name|id
index|]
condition|)
block|{
name|seqNos
index|[
name|op
operator|.
name|id
index|]
operator|=
name|op
operator|.
name|seqNo
expr_stmt|;
if|if
condition|(
name|op
operator|.
name|what
operator|==
literal|2
condition|)
block|{
name|expectedThreadIDs
index|[
name|op
operator|.
name|id
index|]
operator|=
name|threadID
expr_stmt|;
block|}
else|else
block|{
name|expectedThreadIDs
index|[
name|op
operator|.
name|id
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|op
operator|.
name|seqNo
operator|>=
name|lastSeqNo
argument_list|)
expr_stmt|;
name|lastSeqNo
operator|=
name|op
operator|.
name|seqNo
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexCommits
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|idCount
condition|;
name|id
operator|++
control|)
block|{
comment|//System.out.println("TEST: check id=" + id + " expectedThreadID=" + expectedThreadIDs[id]);
name|TopDocs
name|hits
init|=
name|s
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
literal|""
operator|+
name|id
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedThreadIDs
index|[
name|id
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|r
operator|.
name|document
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|int
name|actualThreadID
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"thread"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectedThreadIDs
index|[
name|id
index|]
operator|!=
name|actualThreadID
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAIL: id="
operator|+
name|id
operator|+
literal|" expectedThreadID="
operator|+
name|expectedThreadIDs
index|[
name|id
index|]
operator|+
literal|" vs actualThreadID="
operator|+
name|actualThreadID
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|threadID
init|=
literal|0
init|;
name|threadID
operator|<
name|threadOps
operator|.
name|size
argument_list|()
condition|;
name|threadID
operator|++
control|)
block|{
for|for
control|(
name|Operation
name|op
range|:
name|threadOps
operator|.
name|get
argument_list|(
name|threadID
argument_list|)
control|)
block|{
if|if
condition|(
name|id
operator|==
name|op
operator|.
name|id
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  threadID="
operator|+
name|threadID
operator|+
literal|" seqNo="
operator|+
name|op
operator|.
name|seqNo
operator|+
literal|" "
operator|+
operator|(
name|op
operator|.
name|what
operator|==
literal|2
condition|?
literal|"updated"
else|:
literal|"deleted"
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|"id="
operator|+
name|id
argument_list|,
name|expectedThreadIDs
index|[
name|id
index|]
argument_list|,
name|actualThreadID
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|hits
operator|.
name|totalHits
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAIL: id="
operator|+
name|id
operator|+
literal|" expectedThreadID="
operator|+
name|expectedThreadIDs
index|[
name|id
index|]
operator|+
literal|" vs totalHits="
operator|+
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|threadID
init|=
literal|0
init|;
name|threadID
operator|<
name|threadOps
operator|.
name|size
argument_list|()
condition|;
name|threadID
operator|++
control|)
block|{
for|for
control|(
name|Operation
name|op
range|:
name|threadOps
operator|.
name|get
argument_list|(
name|threadID
argument_list|)
control|)
block|{
if|if
condition|(
name|id
operator|==
name|op
operator|.
name|id
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  threadID="
operator|+
name|threadID
operator|+
literal|" seqNo="
operator|+
name|op
operator|.
name|seqNo
operator|+
literal|" "
operator|+
operator|(
name|op
operator|.
name|what
operator|==
literal|2
condition|?
literal|"updated"
else|:
literal|"del"
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
