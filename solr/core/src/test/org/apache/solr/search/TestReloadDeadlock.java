begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Nightly
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|update
operator|.
name|processor
operator|.
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
import|;
end_import
begin_comment
comment|// This test takes approx 30 seconds on a 2012 MacBook Pro running in IntelliJ. There should be a bunch of
end_comment
begin_comment
comment|// update threads dumped out all waiting on DefaultSolrCoreState.getIndexWriter,
end_comment
begin_comment
comment|// DistributedUpdateProcessor.versionAdd(DistributedUpdateProcessor.java:1016)
end_comment
begin_comment
comment|// and the like in a "real" failure. If we have false=fails we should probably bump this timeout.
end_comment
begin_comment
comment|// See SOLR-7836
end_comment
begin_class
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|5
operator|*
name|TimeUnits
operator|.
name|MINUTE
argument_list|)
annotation|@
name|Nightly
DECL|class|TestReloadDeadlock
specifier|public
class|class
name|TestReloadDeadlock
extends|extends
name|TestRTGBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestStressReorder
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|method|ifVerbose
specifier|public
specifier|static
name|void
name|ifVerbose
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|// if (!log.isDebugEnabled()) return;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"VERBOSE:"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|args
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|o
operator|==
literal|null
condition|?
literal|"(null)"
else|:
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReloadDeadlock
specifier|public
name|void
name|testReloadDeadlock
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
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|deleteByQueryPercent
init|=
literal|20
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
literal|50
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
literal|10
argument_list|)
decl_stmt|;
comment|// query variables
specifier|final
name|AtomicLong
name|reloads
init|=
operator|new
name|AtomicLong
argument_list|(
literal|50
argument_list|)
decl_stmt|;
comment|// number of reloads. Increase this number to force failure.
name|ifVerbose
argument_list|(
literal|"commitPercent"
argument_list|,
name|commitPercent
argument_list|,
literal|"deleteByQueryPercent"
argument_list|,
name|deleteByQueryPercent
argument_list|,
literal|"ndocs"
argument_list|,
name|ndocs
argument_list|,
literal|"nWriteThreads"
argument_list|,
name|nWriteThreads
argument_list|,
literal|"reloads"
argument_list|,
name|reloads
argument_list|)
expr_stmt|;
name|initModel
argument_list|(
name|ndocs
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|areCommitting
init|=
operator|new
name|AtomicBoolean
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
name|AtomicLong
name|testVersion
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
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
name|reloads
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
name|areCommitting
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
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
name|TestReloadDeadlock
operator|.
name|this
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
block|}
name|ifVerbose
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
name|ifVerbose
argument_list|(
literal|"hardCommit end"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|TestReloadDeadlock
operator|.
name|this
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
name|ifVerbose
argument_list|(
literal|"installing new committedModel version="
operator|+
name|committedModelClock
argument_list|)
expr_stmt|;
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
name|areCommitting
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
name|int
name|id
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|id
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|ndocs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|id
operator|=
name|lastId
expr_stmt|;
comment|// reuse the last ID half of the time to force more race conditions
block|}
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
name|long
name|version
init|=
name|testVersion
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
comment|// yield after getting the next version to increase the odds of updates happening out of order
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
if|if
condition|(
name|oper
operator|<
name|commitPercent
operator|+
name|deleteByQueryPercent
condition|)
block|{
name|deleteByQuery
argument_list|(
name|id
argument_list|,
name|nextVal
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addDoc
argument_list|(
name|id
argument_list|,
name|nextVal
argument_list|,
name|version
argument_list|)
expr_stmt|;
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
name|Throwable
name|e
parameter_list|)
block|{
name|reloads
operator|.
name|set
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
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
comment|// The reload operation really doesn't need to happen from multiple threads, we just want it firing pretty often.
while|while
condition|(
name|reloads
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|250
argument_list|)
argument_list|)
expr_stmt|;
name|reloads
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
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
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
comment|// Normally they'll all return immediately (or close to that).
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Sholdn't have sat around here this long waiting for the threads to join."
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
comment|// Probably a silly test, but what the heck.
name|assertFalse
argument_list|(
literal|"All threads shoul be dead, but at least thread "
operator|+
name|thread
operator|.
name|getName
argument_list|()
operator|+
literal|" is not"
argument_list|,
name|thread
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|int
name|id
parameter_list|,
name|long
name|nextVal
parameter_list|,
name|long
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|ifVerbose
argument_list|(
literal|"adding id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|,
literal|"version"
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|Long
name|returnedVersion
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
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
argument_list|,
name|params
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnedVersion
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|version
argument_list|,
name|returnedVersion
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|ifVerbose
argument_list|(
literal|"adding id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|,
literal|"version"
argument_list|,
name|version
argument_list|,
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteByQuery
specifier|private
name|void
name|deleteByQuery
parameter_list|(
name|int
name|id
parameter_list|,
name|long
name|nextVal
parameter_list|,
name|long
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|ifVerbose
argument_list|(
literal|"deleteByQuery id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|,
literal|"version"
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|Long
name|returnedVersion
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
name|params
argument_list|(
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
name|version
argument_list|)
argument_list|,
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO: returning versions for these types of updates is redundant
comment|// but if we do return, they had better be equal
if|if
condition|(
name|returnedVersion
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
operator|-
name|version
argument_list|,
name|returnedVersion
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|ifVerbose
argument_list|(
literal|"deleteByQuery id"
argument_list|,
name|id
argument_list|,
literal|"val="
argument_list|,
name|nextVal
argument_list|,
literal|"version"
argument_list|,
name|version
argument_list|,
literal|"DONE"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
