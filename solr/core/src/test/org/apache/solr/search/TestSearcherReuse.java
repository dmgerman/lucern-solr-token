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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|FileUtils
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|ManagedIndexSchema
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
name|schema
operator|.
name|SchemaField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
begin_comment
comment|/**  * Requests to open a new searcher w/o any underlying change to the index exposed   * by the current searcher should result in the same searcher being re-used.  *  * Likewise, if there<em>is</em> in fact an underlying index change, we want to   * assert that a new searcher will in fact be opened.  */
end_comment
begin_class
DECL|class|TestSearcherReuse
specifier|public
class|class
name|TestSearcherReuse
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrHome
specifier|private
specifier|static
name|File
name|solrHome
decl_stmt|;
DECL|field|collection
specifier|private
specifier|static
specifier|final
name|String
name|collection
init|=
literal|"collection1"
decl_stmt|;
DECL|field|confPath
specifier|private
specifier|static
specifier|final
name|String
name|confPath
init|=
name|collection
operator|+
literal|"/conf"
decl_stmt|;
comment|/**    * We're using a Managed schema so we can confirm that opening a new searcher     * after a schema modification results in getting a new searcher with the new     * schema linked to it.    */
annotation|@
name|BeforeClass
DECL|method|setupTempDirAndCoreWithManagedSchema
specifier|private
specifier|static
name|void
name|setupTempDirAndCoreWithManagedSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHome
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|solrHome
operator|=
name|solrHome
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
name|confPath
argument_list|)
decl_stmt|;
name|File
name|testHomeConfDir
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
name|confPath
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"solrconfig-managed-schema.xml"
argument_list|)
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"schema-id-and-version-fields-only.xml"
argument_list|)
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
comment|// initCore will trigger an upgrade to managed schema, since the solrconfig has
comment|//<schemaFactory class="ManagedIndexSchemaFactory" ... />
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-id-and-version-fields-only.xml"
argument_list|,
name|solrHome
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|private
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHome
operator|=
literal|null
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|optimize
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
comment|// seed some docs& segments
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
comment|// NOTE: starting at "1", we'll use id=0 later
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
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
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// seed a single query into the cache
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
name|numDocs
operator|+
literal|"']"
argument_list|)
expr_stmt|;
specifier|final
name|SolrQueryRequest
name|baseReq
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// we make no index changes in this block, so the searcher should always be the same
comment|// NOTE: we *have* to call getSearcher() in advance, it's a delayed binding
specifier|final
name|SolrIndexSearcher
name|expectedSearcher
init|=
name|getMainSearcher
argument_list|(
name|baseReq
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearcherHasNotChanged
argument_list|(
name|expectedSearcher
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"openSearcher"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearcherHasNotChanged
argument_list|(
name|expectedSearcher
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearcherHasNotChanged
argument_list|(
name|expectedSearcher
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|,
literal|"openSearcher"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearcherHasNotChanged
argument_list|(
name|expectedSearcher
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"id:match_no_documents"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearcherHasNotChanged
argument_list|(
name|expectedSearcher
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// no doc has this id, yet
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearcherHasNotChanged
argument_list|(
name|expectedSearcher
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|baseReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// now do a variety of things that *should* always garuntee a new searcher
name|SolrQueryRequest
name|beforeReq
decl_stmt|;
name|beforeReq
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// NOTE: we *have* to call getSearcher() in advance: delayed binding
name|SolrIndexSearcher
name|before
init|=
name|getMainSearcher
argument_list|(
name|beforeReq
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearcherHasChanged
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|beforeReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|beforeReq
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// NOTE: we *have* to call getSearcher() in advance: delayed binding
name|SolrIndexSearcher
name|before
init|=
name|getMainSearcher
argument_list|(
name|beforeReq
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearcherHasChanged
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|beforeReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|beforeReq
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// NOTE: we *have* to call getSearcher() in advance: delayed binding
name|SolrIndexSearcher
name|before
init|=
name|getMainSearcher
argument_list|(
name|beforeReq
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"id:[0 TO 5]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearcherHasChanged
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|beforeReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|beforeReq
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// NOTE: we *have* to call getSearcher() in advance: delayed binding
name|SolrIndexSearcher
name|before
init|=
name|getMainSearcher
argument_list|(
name|beforeReq
argument_list|)
decl_stmt|;
comment|// create a new field& add it.
name|assertTrue
argument_list|(
literal|"schema not mutable"
argument_list|,
name|beforeReq
operator|.
name|getSchema
argument_list|()
operator|.
name|isMutable
argument_list|()
argument_list|)
expr_stmt|;
name|ManagedIndexSchema
name|oldSchema
init|=
operator|(
name|ManagedIndexSchema
operator|)
name|beforeReq
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|newField
init|=
name|oldSchema
operator|.
name|newField
argument_list|(
literal|"hoss"
argument_list|,
literal|"string"
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSchema
name|newSchema
init|=
name|oldSchema
operator|.
name|addField
argument_list|(
name|newField
argument_list|)
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
comment|// sanity check, later asserts assume this
name|assertNotSame
argument_list|(
name|oldSchema
argument_list|,
name|newSchema
argument_list|)
expr_stmt|;
comment|// the schema has changed - but nothing has requested a new Searcher yet
name|assertSearcherHasNotChanged
argument_list|(
name|before
argument_list|)
expr_stmt|;
comment|// only now should we get a new searcher...
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|,
literal|"openSearcher"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearcherHasChanged
argument_list|(
name|before
argument_list|)
expr_stmt|;
comment|// sanity that opening the new searcher was useful to get new schema...
name|SolrQueryRequest
name|afterReq
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
try|try
block|{
name|assertSame
argument_list|(
name|newSchema
argument_list|,
name|afterReq
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|newSchema
argument_list|,
name|getMainSearcher
argument_list|(
name|afterReq
argument_list|)
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|afterReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|beforeReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Helper method to get the searcher from a request, and assert that it's the main searcher    */
DECL|method|getMainSearcher
specifier|public
specifier|static
name|SolrIndexSearcher
name|getMainSearcher
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|SolrIndexSearcher
name|s
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|assertMainSearcher
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
comment|/**    * Sanity check that we didn't get a realtime (non-caching) searcher    */
DECL|method|assertMainSearcher
specifier|public
specifier|static
name|void
name|assertMainSearcher
parameter_list|(
name|SolrIndexSearcher
name|s
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Searcher isn't 'main': "
operator|+
name|s
operator|.
name|toString
argument_list|()
argument_list|,
comment|// TODO brittle, better solution?
name|s
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|" main{"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Searcher is non-caching"
argument_list|,
name|s
operator|.
name|isCachingEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given an existing searcher, creates a new SolrRequest, and verifies that the     * searcher in that request is<b>not</b> the same as the previous searcher --     * cleaningly closing the new SolrRequest either way.    */
DECL|method|assertSearcherHasChanged
specifier|public
specifier|static
name|void
name|assertSearcherHasChanged
parameter_list|(
name|SolrIndexSearcher
name|previous
parameter_list|)
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
try|try
block|{
name|SolrIndexSearcher
name|newSearcher
init|=
name|getMainSearcher
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|previous
argument_list|,
name|newSearcher
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Given an existing searcher, creates a new SolrRequest, and verifies that the     * searcher in that request is the same as the expected searcher -- cleaningly     * closing the new SolrRequest either way.    */
DECL|method|assertSearcherHasNotChanged
specifier|public
specifier|static
name|void
name|assertSearcherHasNotChanged
parameter_list|(
name|SolrIndexSearcher
name|expected
parameter_list|)
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
try|try
block|{
name|SolrIndexSearcher
name|newSearcher
init|=
name|getMainSearcher
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|expected
argument_list|,
name|newSearcher
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
