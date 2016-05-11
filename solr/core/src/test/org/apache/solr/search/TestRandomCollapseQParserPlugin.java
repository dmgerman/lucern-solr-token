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
name|SuppressCodecs
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
name|TestUtil
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
name|CursorPagingTest
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
name|SolrClient
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
name|embedded
operator|.
name|EmbeddedSolrServer
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
name|SolrDocument
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
name|SolrDocumentList
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|params
operator|.
name|SolrParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|search
operator|.
name|CollapsingQParserPlugin
operator|.
name|NULL_IGNORE
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
name|search
operator|.
name|CollapsingQParserPlugin
operator|.
name|NULL_COLLAPSE
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
name|search
operator|.
name|CollapsingQParserPlugin
operator|.
name|NULL_EXPAND
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
comment|//We want codecs that support DocValues, and ones supporting blank/empty values.
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Appending"
block|,
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|}
argument_list|)
DECL|class|TestRandomCollapseQParserPlugin
specifier|public
class|class
name|TestRandomCollapseQParserPlugin
extends|extends
name|SolrTestCaseJ4
block|{
comment|/** Full SolrServer instance for arbitrary introspection of response data and adding fqs */
DECL|field|SOLR
specifier|public
specifier|static
name|SolrClient
name|SOLR
decl_stmt|;
DECL|field|ALL_SORT_FIELD_NAMES
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|ALL_SORT_FIELD_NAMES
decl_stmt|;
DECL|field|ALL_COLLAPSE_FIELD_NAMES
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|ALL_COLLAPSE_FIELD_NAMES
decl_stmt|;
DECL|field|NULL_POLICIES
specifier|private
specifier|static
name|String
index|[]
name|NULL_POLICIES
init|=
operator|new
name|String
index|[]
block|{
name|NULL_IGNORE
block|,
name|NULL_COLLAPSE
block|,
name|NULL_EXPAND
block|}
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|buildIndexAndClient
specifier|public
specifier|static
name|void
name|buildIndexAndClient
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-minimal.xml"
argument_list|,
literal|"schema-sorts.xml"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|totalDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|totalDocs
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
name|CursorPagingTest
operator|.
name|buildRandomDocument
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// every doc will be in the same group for this (string) field
name|doc
operator|.
name|addField
argument_list|(
literal|"same_for_all_docs"
argument_list|,
literal|"xxx"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Don't close this client, it would shutdown the CoreContainer
name|SOLR
operator|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|,
name|h
operator|.
name|coreName
argument_list|)
expr_stmt|;
name|ALL_SORT_FIELD_NAMES
operator|=
name|CursorPagingTest
operator|.
name|pruneAndDeterministicallySort
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|ALL_COLLAPSE_FIELD_NAMES
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|ALL_SORT_FIELD_NAMES
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|candidate
range|:
name|ALL_SORT_FIELD_NAMES
control|)
block|{
if|if
condition|(
name|candidate
operator|.
name|startsWith
argument_list|(
literal|"str"
argument_list|)
operator|||
name|candidate
operator|.
name|startsWith
argument_list|(
literal|"float"
argument_list|)
operator|||
name|candidate
operator|.
name|startsWith
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
name|ALL_COLLAPSE_FIELD_NAMES
operator|.
name|add
argument_list|(
name|candidate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|AfterClass
DECL|method|cleanupStatics
specifier|public
specifier|static
name|void
name|cleanupStatics
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|SOLR
operator|=
literal|null
expr_stmt|;
name|ALL_SORT_FIELD_NAMES
operator|=
name|ALL_COLLAPSE_FIELD_NAMES
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testEveryIsolatedSortFieldOnSingleGroup
specifier|public
name|void
name|testEveryIsolatedSortFieldOnSingleGroup
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|sortField
range|:
name|ALL_SORT_FIELD_NAMES
control|)
block|{
for|for
control|(
name|String
name|dir
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|" asc"
argument_list|,
literal|" desc"
argument_list|)
control|)
block|{
specifier|final
name|String
name|sort
init|=
name|sortField
operator|+
name|dir
operator|+
literal|", id"
operator|+
name|dir
decl_stmt|;
comment|// need id for tie breaker
specifier|final
name|String
name|q
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
name|CursorPagingTest
operator|.
name|buildRandomQuery
argument_list|()
decl_stmt|;
specifier|final
name|SolrParams
name|sortedP
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"sort"
argument_list|,
name|sort
argument_list|)
decl_stmt|;
specifier|final
name|QueryResponse
name|sortedRsp
init|=
name|SOLR
operator|.
name|query
argument_list|(
name|sortedP
argument_list|)
decl_stmt|;
comment|// random data -- might be no docs matching our query
if|if
condition|(
literal|0
operator|!=
name|sortedRsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
condition|)
block|{
specifier|final
name|SolrDocument
name|firstDoc
init|=
name|sortedRsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// check forced array resizing starting from 1
for|for
control|(
name|String
name|p
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"{!collapse field="
argument_list|,
literal|"{!collapse size='1' field="
argument_list|)
control|)
block|{
for|for
control|(
name|String
name|fq
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|p
operator|+
literal|"same_for_all_docs sort='"
operator|+
name|sort
operator|+
literal|"'}"
argument_list|,
comment|// nullPolicy=expand shouldn't change anything since every doc has field
name|p
operator|+
literal|"same_for_all_docs sort='"
operator|+
name|sort
operator|+
literal|"' nullPolicy=expand}"
argument_list|,
comment|// a field in no docs with nullPolicy=collapse should have same effect as
comment|// collapsing on a field in every doc
name|p
operator|+
literal|"not_in_any_docs sort='"
operator|+
name|sort
operator|+
literal|"' nullPolicy=collapse}"
argument_list|)
control|)
block|{
specifier|final
name|SolrParams
name|collapseP
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fq"
argument_list|,
name|fq
argument_list|)
decl_stmt|;
comment|// since every doc is in the same group, collapse query should return exactly one doc
specifier|final
name|QueryResponse
name|collapseRsp
init|=
name|SOLR
operator|.
name|query
argument_list|(
name|collapseP
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"collapse should have produced exactly one doc: "
operator|+
name|collapseP
argument_list|,
literal|1
argument_list|,
name|collapseRsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SolrDocument
name|groupHead
init|=
name|collapseRsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// the group head from the collapse query should match the first doc of a simple sort
name|assertEquals
argument_list|(
name|sortedP
operator|+
literal|" => "
operator|+
name|firstDoc
operator|+
literal|" :VS: "
operator|+
name|collapseP
operator|+
literal|" => "
operator|+
name|groupHead
argument_list|,
name|firstDoc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|groupHead
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|testRandomCollpaseWithSort
specifier|public
name|void
name|testRandomCollpaseWithSort
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numMainQueriesPerCollapseField
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|collapseField
range|:
name|ALL_COLLAPSE_FIELD_NAMES
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMainQueriesPerCollapseField
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|topSort
init|=
name|CursorPagingTest
operator|.
name|buildRandomSort
argument_list|(
name|ALL_SORT_FIELD_NAMES
argument_list|)
decl_stmt|;
specifier|final
name|String
name|collapseSort
init|=
name|CursorPagingTest
operator|.
name|buildRandomSort
argument_list|(
name|ALL_SORT_FIELD_NAMES
argument_list|)
decl_stmt|;
specifier|final
name|String
name|q
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
name|CursorPagingTest
operator|.
name|buildRandomQuery
argument_list|()
decl_stmt|;
specifier|final
name|SolrParams
name|mainP
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|collapseField
argument_list|)
decl_stmt|;
specifier|final
name|String
name|csize
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|""
else|:
literal|" size="
operator|+
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|String
name|nullPolicy
init|=
name|randomNullPolicy
argument_list|()
decl_stmt|;
specifier|final
name|String
name|nullPs
init|=
name|NULL_IGNORE
operator|.
name|equals
argument_list|(
name|nullPolicy
argument_list|)
comment|// ignore is default, randomly be explicit about it
condition|?
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|""
else|:
literal|" nullPolicy=ignore"
operator|)
else|:
operator|(
literal|" nullPolicy="
operator|+
name|nullPolicy
operator|)
decl_stmt|;
specifier|final
name|SolrParams
name|collapseP
init|=
name|params
argument_list|(
literal|"sort"
argument_list|,
name|topSort
argument_list|,
literal|"rows"
argument_list|,
literal|"200"
argument_list|,
literal|"fq"
argument_list|,
operator|(
literal|"{!collapse"
operator|+
name|csize
operator|+
name|nullPs
operator|+
literal|" field="
operator|+
name|collapseField
operator|+
literal|" sort='"
operator|+
name|collapseSort
operator|+
literal|"'}"
operator|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|QueryResponse
name|mainRsp
init|=
name|SOLR
operator|.
name|query
argument_list|(
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|collapseP
argument_list|,
name|mainP
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|mainRsp
operator|.
name|getResults
argument_list|()
control|)
block|{
specifier|final
name|Object
name|groupHeadId
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|collapseVal
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|collapseField
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|collapseVal
condition|)
block|{
if|if
condition|(
name|NULL_EXPAND
operator|.
name|equals
argument_list|(
name|nullPolicy
argument_list|)
condition|)
block|{
comment|// nothing to check for this doc, it's in it's own group
continue|continue;
block|}
name|assertFalse
argument_list|(
name|groupHeadId
operator|+
literal|" has null collapseVal but nullPolicy==ignore; "
operator|+
literal|"mainP: "
operator|+
name|mainP
operator|+
literal|", collapseP: "
operator|+
name|collapseP
argument_list|,
name|NULL_IGNORE
operator|.
name|equals
argument_list|(
name|nullPolicy
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// workaround for SOLR-8082...
comment|//
comment|// what's important is that we already did the collapsing on the *real* collapseField
comment|// to verify the groupHead returned is really the best our verification filter
comment|// on docs with that value in a different field containing the exact same values
specifier|final
name|String
name|checkField
init|=
name|collapseField
operator|.
name|replace
argument_list|(
literal|"float_dv"
argument_list|,
literal|"float"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|checkFQ
init|=
operator|(
operator|(
literal|null
operator|==
name|collapseVal
operator|)
condition|?
operator|(
literal|"-"
operator|+
name|checkField
operator|+
literal|":[* TO *]"
operator|)
else|:
operator|(
literal|"{!field f="
operator|+
name|checkField
operator|+
literal|"}"
operator|+
name|collapseVal
operator|.
name|toString
argument_list|()
operator|)
operator|)
decl_stmt|;
specifier|final
name|SolrParams
name|checkP
init|=
name|params
argument_list|(
literal|"fq"
argument_list|,
name|checkFQ
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"sort"
argument_list|,
name|collapseSort
argument_list|)
decl_stmt|;
specifier|final
name|QueryResponse
name|checkRsp
init|=
name|SOLR
operator|.
name|query
argument_list|(
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|checkP
argument_list|,
name|mainP
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"not even 1 match for sanity check query? expected: "
operator|+
name|doc
argument_list|,
operator|!
name|checkRsp
operator|.
name|getResults
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SolrDocument
name|firstMatch
init|=
name|checkRsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|firstMatchId
init|=
name|firstMatch
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"first match for filtered group '"
operator|+
name|collapseVal
operator|+
literal|"' not matching expected group head ... "
operator|+
literal|"mainP: "
operator|+
name|mainP
operator|+
literal|", collapseP: "
operator|+
name|collapseP
operator|+
literal|", checkP: "
operator|+
name|checkP
argument_list|,
name|groupHeadId
argument_list|,
name|firstMatchId
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"BUG using params: "
operator|+
name|collapseP
operator|+
literal|" + "
operator|+
name|mainP
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|randomNullPolicy
specifier|private
name|String
name|randomNullPolicy
parameter_list|()
block|{
return|return
name|NULL_POLICIES
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|NULL_POLICIES
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
return|;
block|}
block|}
end_class
end_unit
