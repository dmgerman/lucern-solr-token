begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
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
name|common
operator|.
name|luke
operator|.
name|FieldFlag
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
name|util
operator|.
name|AbstractSolrTestCase
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
name|Before
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import
begin_comment
comment|/**  * :TODO: currently only tests some of the utilities in the LukeRequestHandler  */
end_comment
begin_class
DECL|class|LukeRequestHandlerTest
specifier|public
class|class
name|LukeRequestHandlerTest
extends|extends
name|AbstractSolrTestCase
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"SOLR1000"
argument_list|,
literal|"name"
argument_list|,
literal|"Apache Solr"
argument_list|,
literal|"solr_si"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sl"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sf"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sd"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_s"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sI"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_sS"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_t"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_tt"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_b"
argument_list|,
literal|"true"
argument_list|,
literal|"solr_i"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_l"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_f"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_d"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_ti"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_tl"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_tf"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_td"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pi"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pl"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pf"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_pd"
argument_list|,
literal|"10"
argument_list|,
literal|"solr_dt"
argument_list|,
literal|"2000-01-01T01:01:01Z"
argument_list|,
literal|"solr_tdt"
argument_list|,
literal|"2000-01-01T01:01:01Z"
argument_list|,
literal|"solr_pdt"
argument_list|,
literal|"2000-01-01T01:01:01Z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHistogramBucket
specifier|public
name|void
name|testHistogramBucket
parameter_list|()
block|{
name|assertHistoBucket
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|3
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|3
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|3
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|4
argument_list|,
literal|9
argument_list|)
expr_stmt|;
specifier|final
name|int
name|MAX_VALID
init|=
operator|(
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|2
operator|)
operator|+
literal|1
operator|)
operator|/
literal|2
decl_stmt|;
name|assertHistoBucket
argument_list|(
literal|29
argument_list|,
name|MAX_VALID
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|29
argument_list|,
name|MAX_VALID
argument_list|)
expr_stmt|;
name|assertHistoBucket
argument_list|(
literal|30
argument_list|,
name|MAX_VALID
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|assertHistoBucket
specifier|private
name|void
name|assertHistoBucket
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|in
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"histobucket: "
operator|+
name|in
argument_list|,
name|slot
argument_list|,
literal|32
operator|-
name|Integer
operator|.
name|numberOfLeadingZeros
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|in
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLuke
specifier|public
name|void
name|testLuke
parameter_list|()
block|{
comment|// test that Luke can handle all of the field types
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"id"
argument_list|,
literal|"SOLR1000"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numFlags
init|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|FieldFlag
operator|.
name|class
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertQ
argument_list|(
literal|"Not all flags ("
operator|+
name|numFlags
operator|+
literal|") mentioned in info->key"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|)
argument_list|,
name|numFlags
operator|+
literal|"=count(//lst[@name='info']/lst[@name='key']/str)"
argument_list|)
expr_stmt|;
comment|// code should be the same for all fields, but just in case do several
for|for
control|(
name|String
name|f
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"solr_t"
argument_list|,
literal|"solr_s"
argument_list|,
literal|"solr_ti"
argument_list|,
literal|"solr_td"
argument_list|,
literal|"solr_pl"
argument_list|,
literal|"solr_dt"
argument_list|,
literal|"solr_b"
argument_list|,
literal|"solr_sS"
argument_list|,
literal|"solr_sI"
argument_list|)
control|)
block|{
specifier|final
name|String
name|xp
init|=
name|getFieldXPathPrefix
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Not as many schema flags as expected ("
operator|+
name|numFlags
operator|+
literal|") for "
operator|+
name|f
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
name|f
argument_list|)
argument_list|,
name|numFlags
operator|+
literal|"=string-length("
operator|+
name|xp
operator|+
literal|"[@name='schema'])"
argument_list|)
expr_stmt|;
block|}
comment|// diff loop for checking 'index' flags,
comment|// only valid for fields that are indexed& stored
for|for
control|(
name|String
name|f
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"solr_t"
argument_list|,
literal|"solr_s"
argument_list|,
literal|"solr_ti"
argument_list|,
literal|"solr_td"
argument_list|,
literal|"solr_pl"
argument_list|,
literal|"solr_dt"
argument_list|,
literal|"solr_b"
argument_list|)
control|)
block|{
specifier|final
name|String
name|xp
init|=
name|getFieldXPathPrefix
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Not as many index flags as expected ("
operator|+
name|numFlags
operator|+
literal|") for "
operator|+
name|f
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
name|f
argument_list|)
argument_list|,
name|numFlags
operator|+
literal|"=string-length("
operator|+
name|xp
operator|+
literal|"[@name='index'])"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|hxp
init|=
name|getFieldXPathHistogram
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Historgram field should be present for field "
operator|+
name|f
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
name|f
argument_list|)
argument_list|,
name|hxp
operator|+
literal|"[@name='histogram']"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFieldXPathHistogram
specifier|private
specifier|static
name|String
name|getFieldXPathHistogram
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"//lst[@name='fields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/lst"
return|;
block|}
DECL|method|getFieldXPathPrefix
specifier|private
specifier|static
name|String
name|getFieldXPathPrefix
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"//lst[@name='fields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/str"
return|;
block|}
DECL|method|field
specifier|private
specifier|static
name|String
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"//lst[@name='fields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/"
return|;
block|}
DECL|method|dynfield
specifier|private
specifier|static
name|String
name|dynfield
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"//lst[@name='dynamicFields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/"
return|;
block|}
annotation|@
name|Test
DECL|method|testFlParam
specifier|public
name|void
name|testFlParam
parameter_list|()
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
literal|"solr_t solr_s"
argument_list|,
literal|"show"
argument_list|,
literal|"all"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// First, determine that the two fields ARE there
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|getFieldXPathPrefix
argument_list|(
literal|"solr_t"
argument_list|)
operator|+
literal|"[@name='index']"
argument_list|,
name|getFieldXPathPrefix
argument_list|(
literal|"solr_s"
argument_list|)
operator|+
literal|"[@name='index']"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now test that the other fields are NOT there
for|for
control|(
name|String
name|f
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"solr_ti"
argument_list|,
literal|"solr_td"
argument_list|,
literal|"solr_pl"
argument_list|,
literal|"solr_dt"
argument_list|,
literal|"solr_b"
argument_list|)
control|)
block|{
name|assertNotNull
argument_list|(
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|getFieldXPathPrefix
argument_list|(
name|f
argument_list|)
operator|+
literal|"[@name='index']"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Insure * works
name|req
operator|=
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|response
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|f
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"solr_t"
argument_list|,
literal|"solr_s"
argument_list|,
literal|"solr_ti"
argument_list|,
literal|"solr_td"
argument_list|,
literal|"solr_pl"
argument_list|,
literal|"solr_dt"
argument_list|,
literal|"solr_b"
argument_list|)
control|)
block|{
name|assertNull
argument_list|(
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|getFieldXPathPrefix
argument_list|(
name|f
argument_list|)
operator|+
literal|"[@name='index']"
argument_list|)
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
name|fail
argument_list|(
literal|"Caught unexpected exception "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNumTerms
specifier|public
name|void
name|testNumTerms
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|f
init|=
literal|"name"
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
operator|new
name|String
index|[]
block|{
literal|"2"
block|,
literal|"3"
block|,
literal|"100"
block|,
literal|"99999"
block|}
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
name|f
argument_list|,
literal|"numTerms"
argument_list|,
name|n
argument_list|)
argument_list|,
name|field
argument_list|(
name|f
argument_list|)
operator|+
literal|"lst[@name='topTerms']/int[@name='Apache']"
argument_list|,
name|field
argument_list|(
name|f
argument_list|)
operator|+
literal|"lst[@name='topTerms']/int[@name='Solr']"
argument_list|,
literal|"count("
operator|+
name|field
argument_list|(
name|f
argument_list|)
operator|+
literal|"lst[@name='topTerms']/int)=2"
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
name|f
argument_list|,
literal|"numTerms"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
comment|// no garuntee which one we find
literal|"count("
operator|+
name|field
argument_list|(
name|f
argument_list|)
operator|+
literal|"lst[@name='topTerms']/int)=1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
name|f
argument_list|,
literal|"numTerms"
argument_list|,
literal|"0"
argument_list|)
argument_list|,
literal|"count("
operator|+
name|field
argument_list|(
name|f
argument_list|)
operator|+
literal|"lst[@name='topTerms']/int)=0"
argument_list|)
expr_stmt|;
comment|// field with no terms shouldn't error
for|for
control|(
name|String
name|n
range|:
operator|new
name|String
index|[]
block|{
literal|"0"
block|,
literal|"1"
block|,
literal|"2"
block|,
literal|"100"
block|,
literal|"99999"
block|}
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"fl"
argument_list|,
literal|"bogus_s"
argument_list|,
literal|"numTerms"
argument_list|,
name|n
argument_list|)
argument_list|,
literal|"count("
operator|+
name|field
argument_list|(
name|f
argument_list|)
operator|+
literal|"lst[@name='topTerms']/int)=0"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCopyFieldLists
specifier|public
name|void
name|testCopyFieldLists
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"show"
argument_list|,
literal|"schema"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|String
name|r
init|=
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|xml
argument_list|,
name|field
argument_list|(
literal|"text"
argument_list|)
operator|+
literal|"/arr[@name='copySources']/str[.='title']"
argument_list|,
name|field
argument_list|(
literal|"text"
argument_list|)
operator|+
literal|"/arr[@name='copySources']/str[.='subject']"
argument_list|,
name|field
argument_list|(
literal|"title"
argument_list|)
operator|+
literal|"/arr[@name='copyDests']/str[.='text']"
argument_list|,
name|field
argument_list|(
literal|"title"
argument_list|)
operator|+
literal|"/arr[@name='copyDests']/str[.='title_stemmed']"
argument_list|,
name|dynfield
argument_list|(
literal|"bar_copydest_*"
argument_list|)
operator|+
literal|"/arr[@name='copySources']/str[.='foo_copysource_*']"
argument_list|,
name|dynfield
argument_list|(
literal|"foo_copysource_*"
argument_list|)
operator|+
literal|"/arr[@name='copyDests']/str[.='bar_copydest_*']"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|xml
argument_list|,
literal|null
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
DECL|method|testCatchAllCopyField
specifier|public
name|void
name|testCatchAllCopyField
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema-copyfield-test.xml"
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"'*' should not be (or match) a dynamic field"
argument_list|,
name|schema
operator|.
name|getDynamicPattern
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|foundCatchAllCopyField
init|=
literal|false
decl_stmt|;
for|for
control|(
name|IndexSchema
operator|.
name|DynamicCopy
name|dcf
range|:
name|schema
operator|.
name|getDynamicCopyFields
argument_list|()
control|)
block|{
name|foundCatchAllCopyField
operator|=
name|dcf
operator|.
name|getRegex
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
operator|&&
name|dcf
operator|.
name|getDestFieldName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"catchall_t"
argument_list|)
expr_stmt|;
if|if
condition|(
name|foundCatchAllCopyField
condition|)
block|{
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"<copyField source=\"*\" dest=\"catchall_t\"/> is missing from the schema"
argument_list|,
name|foundCatchAllCopyField
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"show"
argument_list|,
literal|"schema"
argument_list|,
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|String
name|result
init|=
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|xml
argument_list|,
name|field
argument_list|(
literal|"bday"
argument_list|)
operator|+
literal|"/arr[@name='copyDests']/str[.='catchall_t']"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|xml
argument_list|,
name|result
argument_list|)
expr_stmt|;
comment|// Put back the configuration expected by the rest of the tests in this suite
name|deleteCore
argument_list|()
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
