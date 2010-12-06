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
begin_class
DECL|class|TestNRTReaderWithThreads
specifier|public
class|class
name|TestNRTReaderWithThreads
extends|extends
name|LuceneTestCase
block|{
DECL|field|random
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|seq
name|AtomicInteger
name|seq
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|testIndexing
specifier|public
name|void
name|testIndexing
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|mainDir
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
name|mainDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// start pooling readers
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|RunThread
index|[]
name|indexThreads
init|=
operator|new
name|RunThread
index|[
literal|4
index|]
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|indexThreads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|indexThreads
index|[
name|x
index|]
operator|=
operator|new
name|RunThread
argument_list|(
name|x
operator|%
literal|2
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|indexThreads
index|[
name|x
index|]
operator|.
name|setName
argument_list|(
literal|"Thread "
operator|+
name|x
argument_list|)
expr_stmt|;
name|indexThreads
index|[
name|x
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|duration
init|=
literal|1000
decl_stmt|;
while|while
condition|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|<
name|duration
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|int
name|delCount
init|=
literal|0
decl_stmt|;
name|int
name|addCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|indexThreads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|indexThreads
index|[
name|x
index|]
operator|.
name|run
operator|=
literal|false
expr_stmt|;
name|assertNull
argument_list|(
literal|"Exception thrown: "
operator|+
name|indexThreads
index|[
name|x
index|]
operator|.
name|ex
argument_list|,
name|indexThreads
index|[
name|x
index|]
operator|.
name|ex
argument_list|)
expr_stmt|;
name|addCount
operator|+=
name|indexThreads
index|[
name|x
index|]
operator|.
name|addCount
expr_stmt|;
name|delCount
operator|+=
name|indexThreads
index|[
name|x
index|]
operator|.
name|delCount
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|indexThreads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|indexThreads
index|[
name|x
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|indexThreads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|assertNull
argument_list|(
literal|"Exception thrown: "
operator|+
name|indexThreads
index|[
name|x
index|]
operator|.
name|ex
argument_list|,
name|indexThreads
index|[
name|x
index|]
operator|.
name|ex
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("addCount:"+addCount);
comment|//System.out.println("delCount:"+delCount);
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|mainDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|RunThread
specifier|public
class|class
name|RunThread
extends|extends
name|Thread
block|{
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|run
specifier|volatile
name|boolean
name|run
init|=
literal|true
decl_stmt|;
DECL|field|ex
specifier|volatile
name|Throwable
name|ex
decl_stmt|;
DECL|field|delCount
name|int
name|delCount
init|=
literal|0
decl_stmt|;
DECL|field|addCount
name|int
name|addCount
init|=
literal|0
decl_stmt|;
DECL|field|type
name|int
name|type
decl_stmt|;
DECL|method|RunThread
specifier|public
name|RunThread
parameter_list|(
name|int
name|type
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
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
while|while
condition|(
name|run
condition|)
block|{
comment|//int n = random.nextInt(2);
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
name|int
name|i
init|=
name|seq
operator|.
name|addAndGet
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|TestIndexWriterReader
operator|.
name|createDocument
argument_list|(
name|i
argument_list|,
literal|"index1"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|addCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|1
condition|)
block|{
comment|// we may or may not delete because the term may not exist,
comment|// however we're opening and closing the reader rapidly
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|int
name|id
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|seq
operator|.
name|intValue
argument_list|()
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
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
decl_stmt|;
name|int
name|count
init|=
name|TestIndexWriterReader
operator|.
name|count
argument_list|(
name|term
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|delCount
operator|+=
name|count
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|this
operator|.
name|ex
operator|=
name|ex
expr_stmt|;
name|run
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
