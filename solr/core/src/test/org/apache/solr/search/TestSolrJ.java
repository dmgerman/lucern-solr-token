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
name|solr
operator|.
name|SolrTestCaseJ4
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|ConcurrentUpdateSolrServer
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrServer
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
name|common
operator|.
name|SolrInputDocument
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Random
import|;
end_import
begin_class
DECL|class|TestSolrJ
specifier|public
class|class
name|TestSolrJ
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testSolrJ
specifier|public
name|void
name|testSolrJ
parameter_list|()
throws|throws
name|Exception
block|{
comment|// docs, producers, connections, sleep_time
comment|//  main(new String[] {"1000000","4", "1", "0"});
comment|// doCommitPerf();
block|}
DECL|field|server
specifier|public
specifier|static
name|SolrServer
name|server
decl_stmt|;
DECL|field|idField
specifier|public
specifier|static
name|String
name|idField
init|=
literal|"id"
decl_stmt|;
DECL|field|ex
specifier|public
specifier|static
name|Exception
name|ex
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
comment|// String addr = "http://odin.local:80/solr";
comment|// String addr = "http://odin.local:8983/solr";
name|String
name|addr
init|=
literal|"http://127.0.0.1:8983/solr"
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|nDocs
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nProducers
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nConnections
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxSleep
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
operator|++
index|]
argument_list|)
decl_stmt|;
name|ConcurrentUpdateSolrServer
name|sserver
init|=
literal|null
decl_stmt|;
comment|// server = sserver = new ConcurrentUpdateSolrServer(addr,32,8);
name|server
operator|=
name|sserver
operator|=
operator|new
name|ConcurrentUpdateSolrServer
argument_list|(
name|addr
argument_list|,
literal|64
argument_list|,
name|nConnections
argument_list|)
expr_stmt|;
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docsPerThread
init|=
name|nDocs
operator|/
name|nProducers
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|nProducers
index|]
decl_stmt|;
for|for
control|(
name|int
name|threadNum
init|=
literal|0
init|;
name|threadNum
operator|<
name|nProducers
condition|;
name|threadNum
operator|++
control|)
block|{
specifier|final
name|int
name|base
init|=
name|threadNum
operator|*
name|docsPerThread
decl_stmt|;
name|threads
index|[
name|threadNum
index|]
operator|=
operator|new
name|Thread
argument_list|(
literal|"add-thread"
operator|+
name|i
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|indexDocs
argument_list|(
name|base
argument_list|,
name|docsPerThread
argument_list|,
name|maxSleep
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"###############################CAUGHT EXCEPTION"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|ex
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|threadNum
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// optional: wait for commit?
for|for
control|(
name|int
name|threadNum
init|=
literal|0
init|;
name|threadNum
operator|<
name|nProducers
condition|;
name|threadNum
operator|++
control|)
block|{
name|threads
index|[
name|threadNum
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sserver
operator|!=
literal|null
condition|)
block|{
name|sserver
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"time="
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|" throughput="
operator|+
operator|(
name|nDocs
operator|*
literal|1000
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|" Exception="
operator|+
name|ex
argument_list|)
expr_stmt|;
comment|// should server threads be marked as daemon?
comment|// need a server.close()!!!
block|}
DECL|method|getDocument
specifier|public
specifier|static
name|SolrInputDocument
name|getDocument
parameter_list|(
name|int
name|docnum
parameter_list|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|idField
argument_list|,
name|docnum
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docnum
operator|&
literal|0x0f
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"my name is "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|docnum
operator|&
literal|0xff
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"foo_t"
argument_list|,
literal|"now is the time for all good men to come to the aid of their country"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"foo_i"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docnum
operator|&
literal|0x0f
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"foo_s"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docnum
operator|&
literal|0xff
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"foo_b"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
operator|(
name|docnum
operator|&
literal|0x01
operator|)
operator|==
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"parent_s"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docnum
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"price"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docnum
operator|>>
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|golden
init|=
operator|(
name|int
operator|)
literal|2654435761L
decl_stmt|;
name|int
name|h
init|=
name|docnum
operator|*
name|golden
decl_stmt|;
name|int
name|n
init|=
operator|(
name|h
operator|&
literal|0xff
operator|)
operator|+
literal|1
decl_stmt|;
name|List
name|lst
init|=
operator|new
name|ArrayList
argument_list|(
name|n
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|h
operator|=
operator|(
name|h
operator|+
name|i
operator|)
operator|*
name|golden
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|h
operator|&
literal|0xfff
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|setField
argument_list|(
literal|"num_is"
argument_list|,
name|lst
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|indexDocs
specifier|public
specifier|static
name|void
name|indexDocs
parameter_list|(
name|int
name|base
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|maxSleep
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|base
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|base
init|;
name|i
operator|<
name|count
operator|+
name|base
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|i
operator|&
literal|0xfffff
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"\n% "
operator|+
operator|new
name|Date
argument_list|()
operator|+
literal|"\t"
operator|+
name|i
operator|+
literal|"\t"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|i
operator|&
literal|0xffff
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|SolrInputDocument
name|doc
init|=
name|getDocument
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxSleep
operator|>
literal|0
condition|)
block|{
name|int
name|sleep
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxSleep
argument_list|)
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
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
block|}
DECL|method|doCommitPerf
specifier|public
name|void
name|doCommitPerf
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrServer
name|client
init|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://127.0.0.1:8983/solr"
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|13
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TIME: "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
