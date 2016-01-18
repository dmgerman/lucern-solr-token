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
name|AtomicBoolean
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
begin_class
DECL|class|TestTragicIndexWriterDeadlock
specifier|public
class|class
name|TestTragicIndexWriterDeadlock
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDeadlockExcNRTReaderCommit
specifier|public
name|void
name|testDeadlockExcNRTReaderCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newMockDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
if|if
condition|(
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
block|{
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SuppressingConcurrentMergeScheduler
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|isOK
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|Thread
name|commitThread
init|=
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
while|while
condition|(
name|done
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
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
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//System.out.println("commit exc:");
comment|//t.printStackTrace(System.out);
block|}
block|}
block|}
decl_stmt|;
name|commitThread
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|r0
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|Thread
name|nrtThread
init|=
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
name|DirectoryReader
name|r
init|=
name|r0
decl_stmt|;
try|try
block|{
try|try
block|{
name|startingGun
operator|.
name|await
argument_list|()
expr_stmt|;
while|while
condition|(
name|done
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
name|DirectoryReader
name|oldReader
init|=
name|r
decl_stmt|;
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|r2
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|r2
expr_stmt|;
name|oldReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|close
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
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//System.out.println("nrt exc:");
comment|//t.printStackTrace(System.out);
block|}
block|}
block|}
decl_stmt|;
name|nrtThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|dir
operator|.
name|setRandomIOExceptionRate
argument_list|(
literal|.1
argument_list|)
expr_stmt|;
name|startingGun
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|commitThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|nrtThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|dir
operator|.
name|setRandomIOExceptionRate
argument_list|(
literal|0.0
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
block|}
end_class
end_unit
