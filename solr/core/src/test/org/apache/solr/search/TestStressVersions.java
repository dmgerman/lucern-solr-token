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
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|TestHarness
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestStressVersions
specifier|public
class|class
name|TestStressVersions
extends|extends
name|TestRTGBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
block|}
comment|// This version doesn't synchronize on id to tell what update won, but instead uses versions
annotation|@
name|Test
DECL|method|testStressGetRealtimeVersions
specifier|public
name|void
name|testStressGetRealtimeVersions
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
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
name|optimisticPercent
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
decl_stmt|;
comment|// percent change that an update uses optimistic locking
specifier|final
name|int
name|optimisticCorrectPercent
init|=
literal|25
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|70
argument_list|)
decl_stmt|;
comment|// percent change that a version specified will be correct
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
comment|// query variables
specifier|final
name|int
name|percentRealtimeQuery
init|=
literal|75
decl_stmt|;
specifier|final
name|AtomicLong
name|operations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|50000
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
argument_list|<
name|Thread
argument_list|>
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
synchronized|synchronized
init|(
name|globalLock
init|)
block|{
name|newCommittedModel
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|DocInfo
argument_list|>
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
block|}
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|softCommitPercent
condition|)
block|{
name|verbose
argument_list|(
literal|"softCommit start"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|TestHarness
operator|.
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"softCommit end"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|verbose
argument_list|(
literal|"hardCommit start"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|verbose
argument_list|(
literal|"hardCommit end"
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|globalLock
init|)
block|{
comment|// install this model snapshot only if it's newer than the current one
if|if
condition|(
name|version
operator|>=
name|committedModelClock
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|verbose
argument_list|(
literal|"installing new committedModel version="
operator|+
name|committedModelClock
argument_list|)
expr_stmt|;
block|}
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
comment|// Even with versions, we can't remove the sync because increasing versions does not mean increasing vals.
comment|//
comment|// NOTE: versioning means we can now remove the sync and tell what update "won"
comment|// synchronized (sync) {
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
name|Long
name|version
init|=
name|deleteAndGetVersion
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|version
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// only update model if the version is newer
synchronized|synchronized
init|(
name|model
init|)
block|{
name|DocInfo
name|currInfo
init|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|version
argument_list|)
operator|>
name|Math
operator|.
name|abs
argument_list|(
name|currInfo
operator|.
name|version
argument_list|)
condition|)
block|{
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|DocInfo
argument_list|(
name|version
argument_list|,
operator|-
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|verbose
argument_list|(
literal|"deleteByQyery id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|)
expr_stmt|;
name|Long
name|version
init|=
name|deleteByQueryAndGetVersion
argument_list|(
literal|"id:"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|version
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// only update model if the version is newer
synchronized|synchronized
init|(
name|model
init|)
block|{
name|DocInfo
name|currInfo
init|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|version
argument_list|)
operator|>
name|Math
operator|.
name|abs
argument_list|(
name|currInfo
operator|.
name|version
argument_list|)
condition|)
block|{
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|DocInfo
argument_list|(
name|version
argument_list|,
operator|-
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|verbose
argument_list|(
literal|"deleteByQyery id"
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
comment|// assertU(adoc("id",Integer.toString(id), field, Long.toString(nextVal)));
name|Long
name|version
init|=
name|addAndGetVersion
argument_list|(
name|sdoc
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
name|field
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|nextVal
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|version
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// only update model if the version is newer
synchronized|synchronized
init|(
name|model
init|)
block|{
name|DocInfo
name|currInfo
init|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|>
name|currInfo
operator|.
name|version
condition|)
block|{
name|model
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|DocInfo
argument_list|(
name|version
argument_list|,
name|nextVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
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
comment|// }   // end sync
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
name|boolean
name|realTime
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|percentRealtimeQuery
decl_stmt|;
name|DocInfo
name|info
decl_stmt|;
if|if
condition|(
name|realTime
condition|)
block|{
name|info
operator|=
name|model
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|verbose
argument_list|(
literal|"querying id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|SolrQueryRequest
name|sreq
decl_stmt|;
if|if
condition|(
name|realTime
condition|)
block|{
name|sreq
operator|=
name|req
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sreq
operator|=
name|req
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
argument_list|,
literal|"omitHeader"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|sreq
argument_list|)
decl_stmt|;
name|Map
name|rsp
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|List
name|doclist
init|=
call|(
name|List
call|)
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|rsp
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|doclist
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// there's no info we can get back with a delete, so not much we can check without further synchronization
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doclist
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|foundVal
init|=
call|(
name|Long
call|)
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|doclist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|foundVer
init|=
call|(
name|Long
call|)
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|doclist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"_version_"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|foundVer
operator|<
name|Math
operator|.
name|abs
argument_list|(
name|info
operator|.
name|version
argument_list|)
operator|||
operator|(
name|foundVer
operator|==
name|info
operator|.
name|version
operator|&&
name|foundVal
operator|!=
name|info
operator|.
name|val
operator|)
condition|)
block|{
comment|// if the version matches, the val must
name|verbose
argument_list|(
literal|"ERROR, id="
argument_list|,
name|id
argument_list|,
literal|"found="
argument_list|,
name|response
argument_list|,
literal|"model"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
block|}
end_class
end_unit
