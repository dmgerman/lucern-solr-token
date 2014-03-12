begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
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
name|FieldType
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
name|FieldInfo
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
name|RandomIndexWriter
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
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|HashMap
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
name|Map
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
name|atomic
operator|.
name|AtomicLong
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
name|Lock
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
operator|.
name|verbose
import|;
end_import
begin_class
DECL|class|TestStressLucene
specifier|public
class|class
name|TestStressLucene
extends|extends
name|TestRTGBase
block|{
comment|// The purpose of this test is to roughly model how solr uses lucene
DECL|field|reader
name|DirectoryReader
name|reader
decl_stmt|;
annotation|@
name|Test
DECL|method|testStressLuceneNRT
specifier|public
name|void
name|testStressLuceneNRT
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|commitPercent
init|=
literal|5
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|softCommitPercent
init|=
literal|30
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|75
argument_list|)
decl_stmt|;
comment|// what percent of the commits are soft
specifier|final
name|int
name|deletePercent
init|=
literal|4
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
decl_stmt|;
specifier|final
name|int
name|deleteByQueryPercent
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ndocs
init|=
literal|5
operator|+
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|200
argument_list|)
operator|)
decl_stmt|;
name|int
name|nWriteThreads
init|=
literal|5
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxConcurrentCommits
init|=
name|nWriteThreads
decl_stmt|;
comment|// number of committers at a time... it should be<= maxWarmingSearchers
specifier|final
name|AtomicLong
name|operations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
comment|// number of query operations to perform in total
name|int
name|nReadThreads
init|=
literal|5
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|tombstones
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|syncCommits
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|verbose
argument_list|(
literal|"commitPercent="
argument_list|,
name|commitPercent
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"softCommitPercent="
argument_list|,
name|softCommitPercent
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"deletePercent="
argument_list|,
name|deletePercent
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"deleteByQueryPercent="
argument_list|,
name|deleteByQueryPercent
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"ndocs="
argument_list|,
name|ndocs
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"nWriteThreads="
argument_list|,
name|nWriteThreads
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"nReadThreads="
argument_list|,
name|nReadThreads
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"maxConcurrentCommits="
argument_list|,
name|maxConcurrentCommits
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"operations="
argument_list|,
name|operations
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"tombstones="
argument_list|,
name|tombstones
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"syncCommits="
argument_list|,
name|syncCommits
argument_list|)
expr_stmt|;
name|initModel
argument_list|(
name|ndocs
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|numCommitting
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|FieldType
name|idFt
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|idFt
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|idFt
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|idFt
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|idFt
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|idFt
operator|.
name|setIndexOptions
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
specifier|final
name|FieldType
name|ft2
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft2
operator|.
name|setIndexed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ft2
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// model how solr does locking - only allow one thread to do a hard commit at once, and only one thread to do a soft commit, but
comment|// a hard commit in progress does not stop a soft commit.
specifier|final
name|Lock
name|hardCommitLock
init|=
name|syncCommits
condition|?
operator|new
name|ReentrantLock
argument_list|()
else|:
literal|null
decl_stmt|;
specifier|final
name|Lock
name|reopenLock
init|=
name|syncCommits
condition|?
operator|new
name|ReentrantLock
argument_list|()
else|:
literal|null
decl_stmt|;
comment|// RAMDirectory dir = new RAMDirectory();
comment|// final IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(TEST_VERSION_CURRENT, new WhitespaceAnalyzer(TEST_VERSION_CURRENT)));
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setDoRandomForceMergeAssert
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// writer.commit();
comment|// reader = IndexReader.open(dir);
comment|// make this reader an NRT reader from the start to avoid the first non-writer openIfChanged
comment|// to only opening at the last commit point.
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
operator|.
name|w
argument_list|,
literal|true
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
name|nWriteThreads
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"WRITER"
operator|+
name|i
argument_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|operations
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|oper
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|oper
operator|<
name|commitPercent
condition|)
block|{
if|if
condition|(
name|numCommitting
operator|.
name|incrementAndGet
argument_list|()
operator|<=
name|maxConcurrentCommits
condition|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|DocInfo
argument_list|>
name|newCommittedModel
decl_stmt|;
name|long
name|version
decl_stmt|;
name|DirectoryReader
name|oldReader
decl_stmt|;
name|boolean
name|softCommit
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|softCommitPercent
decl_stmt|;
if|if
condition|(
operator|!
name|softCommit
condition|)
block|{
comment|// only allow one hard commit to proceed at once
if|if
condition|(
name|hardCommitLock
operator|!=
literal|null
condition|)
name|hardCommitLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|verbose
argument_list|(
literal|"hardCommit start"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reopenLock
operator|!=
literal|null
condition|)
name|reopenLock
operator|.
name|lock
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|globalLock
init|)
block|{
name|newCommittedModel
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|model
argument_list|)
expr_stmt|;
comment|// take a snapshot
name|version
operator|=
name|snapshotCount
operator|++
expr_stmt|;
name|oldReader
operator|=
name|reader
expr_stmt|;
name|oldReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
comment|// increment the reference since we will use this for reopening
block|}
if|if
condition|(
operator|!
name|softCommit
condition|)
block|{
comment|// must commit after taking a snapshot of the model
comment|// writer.commit();
block|}
name|verbose
argument_list|(
literal|"reopen start using"
argument_list|,
name|oldReader
argument_list|)
expr_stmt|;
name|DirectoryReader
name|newReader
decl_stmt|;
if|if
condition|(
name|softCommit
condition|)
block|{
name|newReader
operator|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|,
name|writer
operator|.
name|w
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// will only open to last commit
name|newReader
operator|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newReader
operator|==
literal|null
condition|)
block|{
name|oldReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|newReader
operator|=
name|oldReader
expr_stmt|;
block|}
name|oldReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|verbose
argument_list|(
literal|"reopen result"
argument_list|,
name|newReader
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|globalLock
init|)
block|{
assert|assert
name|newReader
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
assert|;
assert|assert
name|reader
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
assert|;
comment|// install the new reader if it's newest (and check the current version since another reader may have already been installed)
if|if
condition|(
name|newReader
operator|.
name|getVersion
argument_list|()
operator|>
name|reader
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newReader
expr_stmt|;
comment|// install this snapshot only if it's newer than the current one
if|if
condition|(
name|version
operator|>=
name|committedModelClock
condition|)
block|{
name|committedModel
operator|=
name|newCommittedModel
expr_stmt|;
name|committedModelClock
operator|=
name|version
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// close if unused
name|newReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|reopenLock
operator|!=
literal|null
condition|)
name|reopenLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|softCommit
condition|)
block|{
if|if
condition|(
name|hardCommitLock
operator|!=
literal|null
condition|)
name|hardCommitLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
name|numCommitting
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|int
name|id
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
decl_stmt|;
name|Object
name|sync
init|=
name|syncArr
index|[
name|id
index|]
decl_stmt|;
comment|// set the lastId before we actually change it sometimes to try and
comment|// uncover more race conditions between writing and reading
name|boolean
name|before
init|=
name|rand
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|before
condition|)
block|{
name|lastId
operator|=
name|id
expr_stmt|;
block|}
comment|// We can't concurrently update the same document and retain our invariants of increasing values
comment|// since we can't guarantee what order the updates will be executed.
synchronized|synchronized
init|(
name|sync
init|)
block|{
name|DocInfo
name|info
init|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|long
name|val
init|=
name|info
operator|.
name|val
decl_stmt|;
name|long
name|nextVal
init|=
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|oper
operator|<
name|commitPercent
operator|+
name|deletePercent
condition|)
block|{
comment|// add tombstone first
if|if
condition|(
name|tombstones
condition|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|idFt
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|,
name|ft2
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"adding tombstone for id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|verbose
argument_list|(
literal|"deleting id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
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
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|DocInfo
argument_list|(
literal|0
argument_list|,
operator|-
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"deleting id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|,
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oper
operator|<
name|commitPercent
operator|+
name|deletePercent
operator|+
name|deleteByQueryPercent
condition|)
block|{
comment|//assertU("<delete><query>id:" + id + "</query></delete>");
comment|// add tombstone first
if|if
condition|(
name|tombstones
condition|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|idFt
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|,
name|ft2
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"adding tombstone for id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
name|verbose
argument_list|(
literal|"deleteByQuery"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|TermQuery
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
name|id
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|DocInfo
argument_list|(
literal|0
argument_list|,
operator|-
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"deleteByQuery"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|,
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// model.put(id, nextVal);   // uncomment this and this test should fail.
comment|// assertU(adoc("id",Integer.toString(id), field, Long.toString(nextVal)));
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
name|idFt
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|,
name|ft2
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"adding id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
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
name|id
argument_list|)
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
name|tombstones
condition|)
block|{
comment|// remove tombstone after new addition (this should be optional?)
name|verbose
argument_list|(
literal|"deleting tombstone for id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"deleting tombstone for id"
argument_list|,
name|id
argument_list|,
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|DocInfo
argument_list|(
literal|0
argument_list|,
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"adding id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|,
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|before
condition|)
block|{
name|lastId
operator|=
name|id
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
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
name|nReadThreads
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"READER"
operator|+
name|i
argument_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|operations
operator|.
name|decrementAndGet
argument_list|()
operator|>=
literal|0
condition|)
block|{
comment|// bias toward a recently changed doc
name|int
name|id
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|25
condition|?
name|lastId
else|:
name|rand
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
decl_stmt|;
comment|// when indexing, we update the index, then the model
comment|// so when querying, we should first check the model, and then the index
name|DocInfo
name|info
decl_stmt|;
synchronized|synchronized
init|(
name|globalLock
init|)
block|{
name|info
operator|=
name|committedModel
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|long
name|val
init|=
name|info
operator|.
name|val
decl_stmt|;
name|IndexReader
name|r
decl_stmt|;
synchronized|synchronized
init|(
name|globalLock
init|)
block|{
name|r
operator|=
name|reader
expr_stmt|;
name|r
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
name|int
name|docid
init|=
name|getFirstMatch
argument_list|(
name|r
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|docid
operator|<
literal|0
operator|&&
name|tombstones
condition|)
block|{
comment|// if we couldn't find the doc, look for it's tombstone
name|docid
operator|=
name|getFirstMatch
argument_list|(
name|r
argument_list|,
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|docid
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|val
operator|==
operator|-
literal|1L
condition|)
block|{
comment|// expected... no doc was added yet
name|r
operator|.
name|decRef
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|verbose
argument_list|(
literal|"ERROR: Couldn't find a doc  or tombstone for id"
argument_list|,
name|id
argument_list|,
literal|"using reader"
argument_list|,
name|r
argument_list|,
literal|"expected value"
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No documents or tombstones found for id "
operator|+
name|id
operator|+
literal|", expected at least "
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docid
operator|<
literal|0
operator|&&
operator|!
name|tombstones
condition|)
block|{
comment|// nothing to do - we can't tell anything from a deleted doc without tombstones
block|}
else|else
block|{
if|if
condition|(
name|docid
operator|<
literal|0
condition|)
block|{
name|verbose
argument_list|(
literal|"ERROR: Couldn't find a doc for id"
argument_list|,
name|id
argument_list|,
literal|"using reader"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docid
operator|>=
literal|0
argument_list|)
expr_stmt|;
comment|// we should have found the document, or it's tombstone
name|StoredDocument
name|doc
init|=
name|r
operator|.
name|document
argument_list|(
name|docid
argument_list|)
decl_stmt|;
name|long
name|foundVal
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|foundVal
operator|<
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
condition|)
block|{
name|verbose
argument_list|(
literal|"ERROR: id"
argument_list|,
name|id
argument_list|,
literal|"model_val="
argument_list|,
name|val
argument_list|,
literal|" foundVal="
argument_list|,
name|foundVal
argument_list|,
literal|"reader="
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|foundVal
operator|>=
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|operations
operator|.
name|set
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
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
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
end_class
end_unit
