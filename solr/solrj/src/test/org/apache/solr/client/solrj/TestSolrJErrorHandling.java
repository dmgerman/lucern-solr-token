begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Arrays
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
name|Iterator
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
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|commons
operator|.
name|io
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
name|solr
operator|.
name|SolrJettyTestBase
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|BinaryRequestWriter
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
name|HttpSolrClient
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
name|request
operator|.
name|RequestWriter
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
name|response
operator|.
name|QueryResponse
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
begin_class
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|TestSolrJErrorHandling
specifier|public
class|class
name|TestSolrJErrorHandling
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|unexpected
name|List
argument_list|<
name|Throwable
argument_list|>
name|unexpected
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|createJetty
argument_list|(
name|legacyExampleCollection1SolrHome
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|unexpected
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getChain
specifier|public
name|String
name|getChain
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|40
argument_list|)
decl_stmt|;
name|Throwable
name|lastCause
init|=
literal|null
decl_stmt|;
do|do
block|{
if|if
condition|(
name|lastCause
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"->"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|th
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|lastCause
operator|=
name|th
expr_stmt|;
name|th
operator|=
name|th
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|th
operator|!=
literal|null
condition|)
do|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
operator|+
name|lastCause
operator|.
name|getMessage
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|showExceptions
specifier|public
name|void
name|showExceptions
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|unexpected
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// dedup in case there are many clients or many exceptions
for|for
control|(
name|Throwable
name|e
range|:
name|unexpected
control|)
block|{
name|String
name|chain
init|=
name|getChain
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|Integer
name|prev
init|=
name|counts
operator|.
name|put
argument_list|(
name|chain
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
name|counts
operator|.
name|put
argument_list|(
name|chain
argument_list|,
name|prev
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"EXCEPTION LIST:"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|counts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n\t"
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|error
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithXml
specifier|public
name|void
name|testWithXml
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|getSolrClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|RequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|doIt
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithBinary
specifier|public
name|void
name|testWithBinary
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|getSolrClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|doIt
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|manyDocs
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|manyDocs
parameter_list|(
specifier|final
name|int
name|base
parameter_list|,
specifier|final
name|int
name|numDocs
parameter_list|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|count
operator|<
name|numDocs
return|;
block|}
annotation|@
name|Override
specifier|public
name|SolrInputDocument
name|next
parameter_list|()
block|{
name|int
name|id
init|=
name|base
operator|+
name|count
operator|++
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
comment|// first doc is legit, and will increment a counter
return|return
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"test"
argument_list|,
literal|"count_i"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|// include "ignore_exception" so the log doesn't fill up with known exceptions, and change the values for each doc
comment|// so binary format won't compress too much
return|return
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
literal|"ignore_exception_field_does_not_exist_"
operator|+
name|id
argument_list|,
literal|"fieldval"
operator|+
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{       }
block|}
return|;
block|}
empty_stmt|;
DECL|method|doThreads
name|void
name|doThreads
parameter_list|(
specifier|final
name|HttpSolrClient
name|client
parameter_list|,
specifier|final
name|int
name|numThreads
parameter_list|,
specifier|final
name|int
name|numRequests
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|tries
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
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
name|threadNum
init|=
name|i
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
name|int
name|reqLeft
init|=
name|numRequests
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
operator|--
name|reqLeft
operator|>=
literal|0
condition|)
block|{
name|tries
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|doSingle
argument_list|(
name|client
argument_list|,
name|threadNum
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Allow thread to exit, we should have already recorded the exception.
block|}
block|}
block|}
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
name|showExceptions
argument_list|()
expr_stmt|;
name|int
name|count
init|=
name|getCount
argument_list|(
name|client
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
name|tries
operator|.
name|get
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Number of requests was "
operator|+
name|tries
operator|.
name|get
argument_list|()
operator|+
literal|" but final count was "
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|tries
operator|.
name|get
argument_list|()
argument_list|,
name|getCount
argument_list|(
name|client
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got unexpected exceptions. "
argument_list|,
name|unexpected
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCount
name|int
name|getCount
parameter_list|(
name|HttpSolrClient
name|client
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|client
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"id:test"
argument_list|,
literal|"fl"
argument_list|,
literal|"count_i"
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
operator|(
operator|(
name|Number
operator|)
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"count_i"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
return|return
name|count
return|;
block|}
comment|// this always failed with the Jetty 9.3 snapshot
DECL|method|doIt
name|void
name|doIt
parameter_list|(
name|HttpSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|doThreads
argument_list|(
name|client
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// doSingle(client, 1);
block|}
DECL|method|doSingle
name|void
name|doSingle
parameter_list|(
name|HttpSolrClient
name|client
parameter_list|,
name|int
name|threadNum
parameter_list|)
block|{
try|try
block|{
name|client
operator|.
name|add
argument_list|(
name|manyDocs
argument_list|(
name|threadNum
operator|*
literal|1000000
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
literal|"field_does_not_exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|unexpected
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"unexpected exception:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"FAILING unexpected exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/***   @Test   public void testLive() throws Exception {     HttpSolrClient client = new HttpSolrClient("http://localhost:8983/techproducts/solr/");     client.add( sdoc() );     doiIt(client);   }   ***/
DECL|method|getJsonDocs
name|String
name|getJsonDocs
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|numDocs
operator|*
literal|20
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"["
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{ id : '"
operator|+
name|i
operator|+
literal|"' , unknown_field_"
operator|+
name|i
operator|+
literal|" : 'unknown field value' }"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|whitespace
name|char
index|[]
name|whitespace
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|n
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|arr
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
return|return
name|arr
return|;
block|}
DECL|method|getResponse
name|String
name|getResponse
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|100000
index|]
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
try|try
block|{
name|n
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// a real HTTP client probably wouldn't try to read past the end and would thus
comment|// not get an exception until the *next* http request.
name|log
operator|.
name|error
argument_list|(
literal|"CAUGHT IOException, but already read "
operator|+
name|sb
operator|.
name|length
argument_list|()
operator|+
literal|" : "
operator|+
name|getChain
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
break|break;
name|sb
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"BUFFER="
operator|+
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
comment|// for now, assume we got whole response in one read... otherwise we could block when trying to read again
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testHttpURLConnection
specifier|public
name|void
name|testHttpURLConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bodyString
init|=
name|getJsonDocs
argument_list|(
literal|200000
argument_list|)
decl_stmt|;
comment|// sometimes succeeds with this size, but larger can cause OOM from command line
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|getSolrClient
argument_list|()
decl_stmt|;
name|String
name|urlString
init|=
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/update"
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
literal|null
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|urlString
argument_list|)
decl_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json; charset=UTF-8"
argument_list|)
expr_stmt|;
name|OutputStreamWriter
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|conn
operator|.
name|getOutputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|bodyString
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|int
name|code
init|=
literal|1
decl_stmt|;
try|try
block|{
name|code
operator|=
name|conn
operator|.
name|getResponseCode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"ERROR DURING conn.getResponseCode():"
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
comment|/***  java.io.IOException: Error writing to server  at __randomizedtesting.SeedInfo.seed([2928C6EE314CD076:947A81A74F582526]:0)  at sun.net.www.protocol.http.HttpURLConnection.writeRequests(HttpURLConnection.java:665)  at sun.net.www.protocol.http.HttpURLConnection.writeRequests(HttpURLConnection.java:677)  at sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1533)  at sun.net.www.protocol.http.HttpURLConnection.getInputStream(HttpURLConnection.java:1440)  at java.net.HttpURLConnection.getResponseCode(HttpURLConnection.java:480)  */
name|log
operator|.
name|info
argument_list|(
literal|"CODE="
operator|+
name|code
argument_list|)
expr_stmt|;
name|InputStream
name|is
decl_stmt|;
if|if
condition|(
name|code
operator|==
literal|200
condition|)
block|{
name|is
operator|=
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Attempting to get error stream."
argument_list|)
expr_stmt|;
name|is
operator|=
name|conn
operator|.
name|getErrorStream
argument_list|()
expr_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Can't get error stream... try input stream?"
argument_list|)
expr_stmt|;
name|is
operator|=
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|rbody
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"RESPONSE BODY:"
operator|+
name|rbody
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRawSocket
specifier|public
name|void
name|testRawSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hostName
init|=
literal|"127.0.0.1"
decl_stmt|;
name|int
name|port
init|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|Socket
name|socket
init|=
operator|new
name|Socket
argument_list|(
name|hostName
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|socket
operator|.
name|getOutputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
name|InputStream
name|in
init|=
name|socket
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|String
name|body
init|=
name|getJsonDocs
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|int
name|bodyLen
init|=
name|body
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// bodyLen *= 10;  // make server wait for more
name|char
index|[]
name|whitespace
init|=
name|whitespace
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
name|bodyLen
operator|+=
name|whitespace
operator|.
name|length
expr_stmt|;
name|String
name|headers
init|=
literal|"POST /solr/collection1/update HTTP/1.1\n"
operator|+
literal|"Host: localhost:"
operator|+
name|port
operator|+
literal|"\n"
operator|+
comment|//        "User-Agent: curl/7.43.0\n" +
literal|"Accept: */*\n"
operator|+
literal|"Content-type:application/json\n"
operator|+
literal|"Content-Length: "
operator|+
name|bodyLen
operator|+
literal|"\n"
operator|+
literal|"Connection: Keep-Alive\n"
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
comment|// extra newline separates headers from body
name|out
operator|.
name|write
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Now what if I try to write more?  This doesn't seem to throw an exception!
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|whitespace
argument_list|)
expr_stmt|;
comment|// whitespace
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|rbody
init|=
name|getResponse
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// This will throw a connection reset exception if you try to read past the end of the HTTP response
name|log
operator|.
name|info
argument_list|(
literal|"RESPONSE BODY:"
operator|+
name|rbody
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rbody
operator|.
name|contains
argument_list|(
literal|"unknown_field"
argument_list|)
argument_list|)
expr_stmt|;
comment|/***     // can I reuse now?     // writing another request doesn't actually throw an exception, but the following read does     out.write(headers);     out.write("\n");  // extra newline separates headers from body     out.write(body);     out.flush();      rbody = getResponse(in);     log.info("RESPONSE BODY:" + rbody);     assertTrue(rbody.contains("unknown_field"));     ***/
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
