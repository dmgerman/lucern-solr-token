begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|log
operator|.
name|Log
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
name|core
operator|.
name|SolrCore
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
name|TermsParams
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
name|util
operator|.
name|SimpleOrderedMap
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
name|util
operator|.
name|NamedList
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|SolrQueryResponse
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
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|TermsComponentTest
specifier|public
class|class
name|TermsComponentTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
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
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"a"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"a"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"aa"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"aaa"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"abbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ab"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"abb"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"bb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"abc"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"bbbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"b"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"baa"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"cccc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"bbb"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"ccccc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyLower
specifier|public
name|void
name|testEmptyLower
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
name|TermsComponent
name|tc
init|=
operator|(
name|TermsComponent
operator|)
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"termsComp"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tc is null and it shouldn't be"
argument_list|,
name|tc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|,
literal|"lowerfilt"
argument_list|)
expr_stmt|;
comment|//no lower bound
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_UPPER
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_ROWS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|NamedList
name|values
decl_stmt|;
name|NamedList
name|terms
decl_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/terms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|terms
operator|=
call|(
name|NamedList
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|6
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"a is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aaa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ab is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"ab"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abb is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abb"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abc is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abc"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoField
specifier|public
name|void
name|testNoField
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
name|TermsComponent
name|tc
init|=
operator|(
name|TermsComponent
operator|)
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"termsComp"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tc is null and it shouldn't be"
argument_list|,
name|tc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//no lower bound
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_LOWER
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_ROWS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/terms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|Exception
name|exception
init|=
name|rsp
operator|.
name|getException
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"exception is null and it shouldn't be"
argument_list|,
name|exception
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleFields
specifier|public
name|void
name|testMultipleFields
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
name|TermsComponent
name|tc
init|=
operator|(
name|TermsComponent
operator|)
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"termsComp"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tc is null and it shouldn't be"
argument_list|,
name|tc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"standardfilt"
argument_list|)
expr_stmt|;
comment|//no lower bound
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_UPPER
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_ROWS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|NamedList
name|values
decl_stmt|;
name|NamedList
name|terms
decl_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/terms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|NamedList
name|tmp
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tmp Size: "
operator|+
name|tmp
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|tmp
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|(
name|NamedList
operator|)
name|tmp
operator|.
name|get
argument_list|(
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|6
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|6
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|(
name|NamedList
operator|)
name|tmp
operator|.
name|get
argument_list|(
literal|"standardfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|testPastUpper
specifier|public
name|void
name|testPastUpper
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
name|TermsComponent
name|tc
init|=
operator|(
name|TermsComponent
operator|)
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"termsComp"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tc is null and it shouldn't be"
argument_list|,
name|tc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|,
literal|"lowerfilt"
argument_list|)
expr_stmt|;
comment|//no lower bound
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_LOWER
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_ROWS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|NamedList
name|values
decl_stmt|;
name|NamedList
name|terms
decl_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/terms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|terms
operator|=
call|(
name|NamedList
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|0
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
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
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|TermsComponent
name|tc
init|=
operator|(
name|TermsComponent
operator|)
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"termsComp"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tc is null and it shouldn't be"
argument_list|,
name|tc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|,
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_LOWER
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_UPPER
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_ROWS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|NamedList
name|values
decl_stmt|;
name|NamedList
name|terms
decl_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/terms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|terms
operator|=
call|(
name|NamedList
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|6
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aaa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ab is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"ab"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abb is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abb"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abc is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abc"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"a is null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"b is not null and it should be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_UPPER_INCLUSIVE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|terms
operator|=
call|(
name|NamedList
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|7
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|7
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ab is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"ab"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aaa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abb is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abb"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abc is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abc"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"b is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"a is null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"baa is not null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"baa"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_LOWER_INCLUSIVE
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|terms
operator|=
call|(
name|NamedList
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|6
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|6
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ab is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"ab"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aaa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abb is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abb"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abc is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abc"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"b is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"a is not null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"baa is not null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"baa"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_FIELD
argument_list|,
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_LOWER
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_UPPER
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_ROWS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|terms
operator|=
call|(
name|NamedList
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"terms Size: "
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"aaa is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"aa"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abb is not null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abb"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"abc is not null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"abc"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"b is null and it shouldn't be"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"baa is not null"
argument_list|,
name|terms
operator|.
name|get
argument_list|(
literal|"baa"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
