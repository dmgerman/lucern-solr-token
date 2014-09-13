begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|handler
operator|.
name|RequestHandlerBase
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
name|handler
operator|.
name|component
operator|.
name|QueryComponent
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
name|handler
operator|.
name|component
operator|.
name|SpellCheckComponent
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
name|request
operator|.
name|SolrRequestHandler
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
name|response
operator|.
name|SolrQueryResponse
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
name|DefaultSolrThreadFactory
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
name|plugin
operator|.
name|SolrCoreAware
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
name|Callable
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|TimeUnit
import|;
end_import
begin_class
DECL|class|SolrCoreTest
specifier|public
class|class
name|SolrCoreTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|COLLECTION1
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION1
init|=
literal|"collection1"
decl_stmt|;
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveThenAddDefaultCore
specifier|public
name|void
name|testRemoveThenAddDefaultCore
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|COLLECTION1
argument_list|,
name|cores
operator|.
name|getDefaultCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|cores
operator|.
name|unload
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|CoreDescriptor
name|cd
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
name|COLLECTION1
argument_list|,
literal|"collection1"
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_DATADIR
argument_list|,
name|createTempDir
argument_list|(
literal|"dataDir2"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|cores
operator|.
name|create
argument_list|(
name|cd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|COLLECTION1
argument_list|,
name|cores
operator|.
name|getDefaultCoreName
argument_list|()
argument_list|)
expr_stmt|;
comment|// so we should be able to get a core with collection1
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
name|COLLECTION1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// and with ""
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRequestHandlerRegistry
specifier|public
name|void
name|testRequestHandlerRegistry
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|EmptyRequestHandler
name|handler1
init|=
operator|new
name|EmptyRequestHandler
argument_list|()
decl_stmt|;
name|EmptyRequestHandler
name|handler2
init|=
operator|new
name|EmptyRequestHandler
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/this/is A path /that won't be registered!"
decl_stmt|;
name|SolrRequestHandler
name|old
init|=
name|core
operator|.
name|registerRequestHandler
argument_list|(
name|path
argument_list|,
name|handler1
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|old
argument_list|)
expr_stmt|;
comment|// should not be anything...
name|assertEquals
argument_list|(
name|core
operator|.
name|getRequestHandlers
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|handler1
argument_list|)
expr_stmt|;
name|old
operator|=
name|core
operator|.
name|registerRequestHandler
argument_list|(
name|path
argument_list|,
name|handler2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|old
argument_list|,
name|handler1
argument_list|)
expr_stmt|;
comment|// should pop out the old one
name|assertEquals
argument_list|(
name|core
operator|.
name|getRequestHandlers
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|handler2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClose
specifier|public
name|void
name|testClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|ClosingRequestHandler
name|handler1
init|=
operator|new
name|ClosingRequestHandler
argument_list|()
decl_stmt|;
name|handler1
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|"/this/is A path /that won't be registered 2!!!!!!!!!!!"
decl_stmt|;
name|SolrRequestHandler
name|old
init|=
name|core
operator|.
name|registerRequestHandler
argument_list|(
name|path
argument_list|,
name|handler1
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|old
argument_list|)
expr_stmt|;
comment|// should not be anything...
name|assertEquals
argument_list|(
name|core
operator|.
name|getRequestHandlers
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|handler1
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Handler not closed"
argument_list|,
name|handler1
operator|.
name|closed
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRefCount
specifier|public
name|void
name|testRefCount
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount != 1"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|SolrCore
name|c1
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount != 2"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|ClosingRequestHandler
name|handler1
init|=
operator|new
name|ClosingRequestHandler
argument_list|()
decl_stmt|;
name|handler1
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|"/this/is A path /that won't be registered!"
decl_stmt|;
name|SolrRequestHandler
name|old
init|=
name|core
operator|.
name|registerRequestHandler
argument_list|(
name|path
argument_list|,
name|handler1
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|old
argument_list|)
expr_stmt|;
comment|// should not be anything...
name|assertEquals
argument_list|(
name|core
operator|.
name|getRequestHandlers
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|handler1
argument_list|)
expr_stmt|;
name|SolrCore
name|c2
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|c1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount< 1"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Handler is closed"
argument_list|,
name|handler1
operator|.
name|closed
operator|==
literal|false
argument_list|)
expr_stmt|;
name|c1
operator|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount< 2"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|>=
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Handler is closed"
argument_list|,
name|handler1
operator|.
name|closed
operator|==
literal|false
argument_list|)
expr_stmt|;
name|c2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount< 1"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Handler is closed"
argument_list|,
name|handler1
operator|.
name|closed
operator|==
literal|false
argument_list|)
expr_stmt|;
name|c1
operator|.
name|close
argument_list|()
expr_stmt|;
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount != 0"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Handler not closed"
argument_list|,
name|core
operator|.
name|isClosed
argument_list|()
operator|&&
name|handler1
operator|.
name|closed
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRefCountMT
specifier|public
name|void
name|testRefCountMT
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount != 1"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|ClosingRequestHandler
name|handler1
init|=
operator|new
name|ClosingRequestHandler
argument_list|()
decl_stmt|;
name|handler1
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|"/this/is A path /that won't be registered!"
decl_stmt|;
name|SolrRequestHandler
name|old
init|=
name|core
operator|.
name|registerRequestHandler
argument_list|(
name|path
argument_list|,
name|handler1
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|old
argument_list|)
expr_stmt|;
comment|// should not be anything...
name|assertEquals
argument_list|(
name|core
operator|.
name|getRequestHandlers
argument_list|()
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|handler1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|LOOP
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|MT
init|=
literal|16
decl_stmt|;
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|MT
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"refCountMT"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|callees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MT
argument_list|)
decl_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
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
name|MT
condition|;
operator|++
name|i
control|)
block|{
name|Callable
argument_list|<
name|Integer
argument_list|>
name|call
init|=
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|void
name|yield
parameter_list|(
name|int
name|n
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|0
argument_list|,
operator|(
name|n
operator|%
literal|13
operator|+
literal|1
operator|)
operator|*
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|xint
parameter_list|)
block|{           }
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
block|{
name|SolrCore
name|core
init|=
literal|null
decl_stmt|;
name|int
name|r
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|l
init|=
literal|0
init|;
name|l
operator|<
name|LOOP
condition|;
operator|++
name|l
control|)
block|{
name|r
operator|+=
literal|1
expr_stmt|;
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// sprinkle concurrency hinting...
name|yield
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount< 1"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|>=
literal|1
argument_list|)
expr_stmt|;
name|yield
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount> 17"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|<=
literal|17
argument_list|)
expr_stmt|;
name|yield
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Handler is closed"
argument_list|,
name|handler1
operator|.
name|closed
operator|==
literal|false
argument_list|)
expr_stmt|;
name|yield
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|core
operator|=
literal|null
expr_stmt|;
name|yield
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|callees
operator|.
name|add
argument_list|(
name|call
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Future
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|results
init|=
name|service
operator|.
name|invokeAll
argument_list|(
name|callees
argument_list|)
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Integer
argument_list|>
name|result
range|:
name|results
control|)
block|{
name|assertTrue
argument_list|(
literal|"loop="
operator|+
name|result
operator|.
name|get
argument_list|()
operator|+
literal|"< "
operator|+
name|LOOP
argument_list|,
name|result
operator|.
name|get
argument_list|()
operator|>=
name|LOOP
argument_list|)
expr_stmt|;
block|}
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Refcount != 0"
argument_list|,
name|core
operator|.
name|getOpenCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Handler not closed"
argument_list|,
name|core
operator|.
name|isClosed
argument_list|()
operator|&&
name|handler1
operator|.
name|closed
operator|==
literal|true
argument_list|)
expr_stmt|;
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Running for too long..."
argument_list|,
name|service
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfoRegistry
specifier|public
name|void
name|testInfoRegistry
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TEst that SolrInfoMBeans are registered, including SearchComponents
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|infoRegistry
init|=
name|core
operator|.
name|getInfoRegistry
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"infoRegistry Size: "
operator|+
name|infoRegistry
operator|.
name|size
argument_list|()
operator|+
literal|" is not greater than: "
operator|+
literal|0
argument_list|,
name|infoRegistry
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|//try out some that we know are in the config
name|SolrInfoMBean
name|bean
init|=
name|infoRegistry
operator|.
name|get
argument_list|(
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"bean not registered"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
comment|//try a default one
name|bean
operator|=
name|infoRegistry
operator|.
name|get
argument_list|(
name|QueryComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"bean not registered"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
comment|//try a Req Handler, which are stored by name, not clas
name|bean
operator|=
name|infoRegistry
operator|.
name|get
argument_list|(
literal|"standard"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"bean not registered"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|ClosingRequestHandler
class|class
name|ClosingRequestHandler
extends|extends
name|EmptyRequestHandler
implements|implements
name|SolrCoreAware
block|{
DECL|field|closed
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|core
operator|.
name|addCloseHook
argument_list|(
operator|new
name|CloseHook
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|preClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_comment
comment|/**  * An empty handler for testing  */
end_comment
begin_class
DECL|class|EmptyRequestHandler
class|class
name|EmptyRequestHandler
extends|extends
name|RequestHandlerBase
block|{
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// nothing!
block|}
DECL|method|getDescription
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
