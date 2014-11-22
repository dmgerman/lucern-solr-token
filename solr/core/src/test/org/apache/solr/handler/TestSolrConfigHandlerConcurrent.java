begin_unit
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|StringReader
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
name|text
operator|.
name|MessageFormat
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
name|Map
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
name|TimeUnit
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|cloud
operator|.
name|DocCollection
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
name|cloud
operator|.
name|Replica
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
name|cloud
operator|.
name|Slice
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
name|cloud
operator|.
name|ZkStateReader
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
name|core
operator|.
name|ConfigOverlay
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
name|RESTfulServerProvider
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
name|RestTestHarness
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|ConfigOverlay
operator|.
name|getObjectByPath
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
name|rest
operator|.
name|schema
operator|.
name|TestBulkSchemaAPI
operator|.
name|getAsMap
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
operator|.
name|getVal
import|;
end_import
begin_class
DECL|class|TestSolrConfigHandlerConcurrent
specifier|public
class|class
name|TestSolrConfigHandlerConcurrent
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSolrConfigHandlerConcurrent
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|restTestHarnesses
specifier|private
name|List
argument_list|<
name|RestTestHarness
argument_list|>
name|restTestHarnesses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|setupHarnesses
specifier|private
name|void
name|setupHarnesses
parameter_list|()
block|{
for|for
control|(
specifier|final
name|SolrServer
name|client
range|:
name|clients
control|)
block|{
name|RestTestHarness
name|harness
init|=
operator|new
name|RestTestHarness
argument_list|(
operator|new
name|RESTfulServerProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
operator|(
operator|(
name|HttpSolrServer
operator|)
name|client
operator|)
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|restTestHarnesses
operator|.
name|add
argument_list|(
name|harness
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
name|editable_prop_map
init|=
operator|(
name|Map
operator|)
operator|new
name|ObjectBuilder
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|ConfigOverlay
operator|.
name|MAPPING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|Map
name|caches
init|=
operator|(
name|Map
operator|)
name|editable_prop_map
operator|.
name|get
argument_list|(
literal|"query"
argument_list|)
decl_stmt|;
name|setupHarnesses
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|caches
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|List
argument_list|>
name|collectErrors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|caches
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
name|Thread
name|t
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
name|ArrayList
name|errs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|collectErrors
operator|.
name|add
argument_list|(
name|errs
argument_list|)
expr_stmt|;
name|invokeBulkCall
argument_list|(
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|errs
argument_list|,
operator|(
name|Map
operator|)
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
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
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|true
decl_stmt|;
for|for
control|(
name|List
name|e
range|:
name|collectErrors
control|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|collectErrors
operator|.
name|toString
argument_list|()
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|invokeBulkCall
specifier|private
name|void
name|invokeBulkCall
parameter_list|(
name|String
name|cacheName
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|,
name|Map
name|val
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{"
operator|+
literal|"'set-property' : {'query.CACHENAME.size':'CACHEVAL1',"
operator|+
literal|"                  'query.CACHENAME.initialSize':'CACHEVAL2'},"
operator|+
literal|"'set-property': {'query.CACHENAME.autowarmCount' : 'CACHEVAL3'}"
operator|+
literal|"}"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|errmessages
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
literal|1
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
comment|//make it  ahigher number
name|RestTestHarness
name|publisher
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replaceAll
argument_list|(
literal|"CACHENAME"
argument_list|,
name|cacheName
argument_list|)
expr_stmt|;
name|String
name|val1
init|=
name|String
operator|.
name|valueOf
argument_list|(
literal|10
operator|*
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"CACHEVAL1"
argument_list|,
name|val1
argument_list|)
expr_stmt|;
name|String
name|val2
init|=
name|String
operator|.
name|valueOf
argument_list|(
literal|10
operator|*
name|i
operator|+
literal|2
argument_list|)
decl_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"CACHEVAL2"
argument_list|,
name|val2
argument_list|)
expr_stmt|;
name|String
name|val3
init|=
name|String
operator|.
name|valueOf
argument_list|(
literal|10
operator|*
name|i
operator|+
literal|3
argument_list|)
decl_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"CACHEVAL3"
argument_list|,
name|val3
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|publisher
operator|.
name|post
argument_list|(
literal|"/config?wt=json"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|errors
init|=
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
decl_stmt|;
if|if
condition|(
name|errors
operator|!=
literal|null
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
operator|new
name|String
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|errors
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|DocCollection
name|coll
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|coll
operator|.
name|getSlices
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
name|urls
operator|.
name|add
argument_list|(
literal|""
operator|+
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
operator|+
literal|"/"
operator|+
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//get another node
name|String
name|url
init|=
name|urls
operator|.
name|get
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|maxTimeoutSeconds
init|=
literal|20
decl_stmt|;
while|while
condition|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|<
name|maxTimeoutSeconds
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|errmessages
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Map
name|respMap
init|=
name|getAsMap
argument_list|(
name|url
operator|+
literal|"/config/overlay?wt=json"
argument_list|)
decl_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|respMap
operator|.
name|get
argument_list|(
literal|"overlay"
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
name|m
operator|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"props"
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
block|{
name|errmessages
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"overlay does not exist for cache: {0} , iteration: {1} response {2} "
argument_list|,
name|cacheName
argument_list|,
name|i
argument_list|,
name|respMap
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Object
name|o
init|=
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|asList
argument_list|(
literal|"query"
argument_list|,
name|cacheName
argument_list|,
literal|"size"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|val1
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"'size' property not set, expected = {0}, actual {1}"
argument_list|,
name|val1
argument_list|,
name|o
argument_list|)
argument_list|)
expr_stmt|;
name|o
operator|=
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|asList
argument_list|(
literal|"query"
argument_list|,
name|cacheName
argument_list|,
literal|"initialSize"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|val2
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"'initialSize' property not set, expected = {0}, actual {1}"
argument_list|,
name|val2
argument_list|,
name|o
argument_list|)
argument_list|)
expr_stmt|;
name|o
operator|=
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|asList
argument_list|(
literal|"query"
argument_list|,
name|cacheName
argument_list|,
literal|"autowarmCount"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|val3
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"'autowarmCount' property not set, expected = {0}, actual {1}"
argument_list|,
name|val3
argument_list|,
name|o
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
break|break;
block|}
if|if
condition|(
operator|!
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|errs
operator|.
name|addAll
argument_list|(
name|errmessages
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
DECL|method|getAsMap
specifier|private
name|Map
name|getAsMap
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpGet
name|get
init|=
operator|new
name|HttpGet
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entity
operator|=
name|cloudClient
operator|.
name|getLbServer
argument_list|()
operator|.
name|getHttpClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|String
name|response
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
return|return
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
