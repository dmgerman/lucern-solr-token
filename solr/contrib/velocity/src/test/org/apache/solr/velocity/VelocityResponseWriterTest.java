begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.velocity
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|velocity
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
name|response
operator|.
name|QueryResponseWriter
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
name|SolrParamResourceLoader
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
name|response
operator|.
name|VelocityResponseWriter
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
name|StringWriter
import|;
end_import
begin_class
DECL|class|VelocityResponseWriterTest
specifier|public
class|class
name|VelocityResponseWriterTest
extends|extends
name|SolrTestCaseJ4
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
name|getFile
argument_list|(
literal|"velocity/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getFile
argument_list|(
literal|"velocity/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVelocityResponseWriterRegistered
specifier|public
name|void
name|testVelocityResponseWriterRegistered
parameter_list|()
block|{
name|QueryResponseWriter
name|writer
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
literal|"velocity"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"VrW registered check"
argument_list|,
name|writer
operator|instanceof
name|VelocityResponseWriter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomParamTemplate
specifier|public
name|void
name|testCustomParamTemplate
parameter_list|()
throws|throws
name|Exception
block|{
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|VelocityResponseWriter
name|vrw
init|=
operator|new
name|VelocityResponseWriter
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|VelocityResponseWriter
operator|.
name|PARAMS_RESOURCE_LOADER_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|vrw
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"custom"
argument_list|,
name|SolrParamResourceLoader
operator|.
name|TEMPLATE_PARAM_PREFIX
operator|+
literal|"custom"
argument_list|,
literal|"$response.response.response_data"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"response_data"
argument_list|,
literal|"testing"
argument_list|)
expr_stmt|;
name|vrw
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testing"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParamResourceLoaderDisabled
specifier|public
name|void
name|testParamResourceLoaderDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|VelocityResponseWriter
name|vrw
init|=
operator|new
name|VelocityResponseWriter
argument_list|()
decl_stmt|;
comment|// by default param resource loader is disabled, no need to set it here
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"custom"
argument_list|,
name|SolrParamResourceLoader
operator|.
name|TEMPLATE_PARAM_PREFIX
operator|+
literal|"custom"
argument_list|,
literal|"$response.response.response_data"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
block|{
name|vrw
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception due to missing template"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
annotation|@
name|Test
DECL|method|testFileResourceLoader
specifier|public
name|void
name|testFileResourceLoader
parameter_list|()
throws|throws
name|Exception
block|{
name|VelocityResponseWriter
name|vrw
init|=
operator|new
name|VelocityResponseWriter
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"template.base.dir"
argument_list|,
name|getFile
argument_list|(
literal|"velocity"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|vrw
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|vrw
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testing"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSolrResourceLoaderTemplate
specifier|public
name|void
name|testSolrResourceLoaderTemplate
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEncoding
specifier|public
name|void
name|testEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"Ã©Ã±Ã§Ã¸âÃ®Ã±g"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"encoding"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMacros
specifier|public
name|void
name|testMacros
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that a macro in a custom macros.vm is visible
name|assertEquals
argument_list|(
literal|"test_macro_SUCCESS"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"test_macro_visible"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// tests that a builtin (_macros.vm) macro, #url_root in this case, can be overridden in a custom macros.vm
comment|// the macro is also defined in VM_global_library.vm, which should also be overridden by macros.vm
name|assertEquals
argument_list|(
literal|"Loaded from: macros.vm"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"test_macro_overridden"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// tests that macros defined in VM_global_library.vm are visible.  This file was where macros in pre-5.0 versions were defined
name|assertEquals
argument_list|(
literal|"legacy_macro_SUCCESS"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"test_macro_legacy_support"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitProps
specifier|public
name|void
name|testInitProps
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The test init properties file turns off being able to use $foreach.index (the implicit loop counter)
comment|// The foreach.vm template uses $!foreach.index, with ! suppressing the literal "$foreach.index" output
name|assertEquals
argument_list|(
literal|"01"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"foreach"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocityWithInitProps"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"foreach"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomTools
specifier|public
name|void
name|testCustomTools
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"custom_tool"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"** LATERALUS **"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocityWithCustomTools"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"t"
argument_list|,
name|SolrParamResourceLoader
operator|.
name|TEMPLATE_PARAM_PREFIX
operator|+
literal|"t"
argument_list|,
literal|"$mytool.star(\"LATERALUS\")"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Does $log get overridden?
name|assertEquals
argument_list|(
literal|"** log overridden **"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocityWithCustomTools"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"t"
argument_list|,
name|SolrParamResourceLoader
operator|.
name|TEMPLATE_PARAM_PREFIX
operator|+
literal|"t"
argument_list|,
literal|"$log.star(\"log overridden\")"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Does $response get overridden?  actual blank response because of the bang on $! reference that silences bogus $-references
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocityWithCustomTools"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"t"
argument_list|,
name|SolrParamResourceLoader
operator|.
name|TEMPLATE_PARAM_PREFIX
operator|+
literal|"t"
argument_list|,
literal|"$!response.star(\"response overridden??\")"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Custom tools can also have a SolrCore-arg constructor because they are instantiated with SolrCore.createInstance
comment|// TODO: do we really need to support this?  no great loss, as a custom tool could take a SolrCore object as a parameter to
comment|// TODO: any method, so one could do $mytool.my_method($request.core)
comment|// I'm currently inclined to make this feature undocumented/unsupported, as we may want to instantiate classes
comment|// in a different manner that only supports no-arg constructors, commented (passing) test case out
comment|//    assertEquals("collection1", h.query(req("q","*:*", "wt","velocityWithCustomTools",VelocityResponseWriter.TEMPLATE,"t",
comment|//        SolrParamResourceLoader.TEMPLATE_PARAM_PREFIX+"t", "$mytool.core.name")));
block|}
annotation|@
name|Test
DECL|method|testLocaleFeature
specifier|public
name|void
name|testLocaleFeature
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"Color"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"locale"
argument_list|,
name|VelocityResponseWriter
operator|.
name|LOCALE
argument_list|,
literal|"en_US"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Colour"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"locale"
argument_list|,
name|VelocityResponseWriter
operator|.
name|LOCALE
argument_list|,
literal|"en_UK"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test that $resource.get(key,baseName,locale) works with specified locale
name|assertEquals
argument_list|(
literal|"Colour"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocityWithCustomTools"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"t"
argument_list|,
name|SolrParamResourceLoader
operator|.
name|TEMPLATE_PARAM_PREFIX
operator|+
literal|"t"
argument_list|,
literal|"$resource.get(\"color\",\"resources\",\"en_UK\")"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLayoutFeature
specifier|public
name|void
name|testLayoutFeature
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"{{{0}}}"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|,
name|VelocityResponseWriter
operator|.
name|LAYOUT
argument_list|,
literal|"layout"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// even with v.layout specified, layout can be disabled explicitly
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|,
name|VelocityResponseWriter
operator|.
name|LAYOUT
argument_list|,
literal|"layout"
argument_list|,
name|VelocityResponseWriter
operator|.
name|LAYOUT_ENABLED
argument_list|,
literal|"false"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJSONWrapper
specifier|public
name|void
name|testJSONWrapper
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"foo({\"result\":\"0\"})"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|,
name|VelocityResponseWriter
operator|.
name|JSON
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now with layout, for good measure
name|assertEquals
argument_list|(
literal|"foo({\"result\":\"{{{0}}}\"})"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"velocity"
argument_list|,
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|,
name|VelocityResponseWriter
operator|.
name|JSON
argument_list|,
literal|"foo"
argument_list|,
name|VelocityResponseWriter
operator|.
name|LAYOUT
argument_list|,
literal|"layout"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContentType
specifier|public
name|void
name|testContentType
parameter_list|()
throws|throws
name|Exception
block|{
name|VelocityResponseWriter
name|vrw
init|=
operator|new
name|VelocityResponseWriter
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|vrw
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
comment|// with v.json=wrf, content type should default to application/json
name|assertEquals
argument_list|(
literal|"application/json;charset=UTF-8"
argument_list|,
name|vrw
operator|.
name|getContentType
argument_list|(
name|req
argument_list|(
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|,
name|VelocityResponseWriter
operator|.
name|JSON
argument_list|,
literal|"wrf"
argument_list|)
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
comment|// with no v.json specified, the default text/html should be returned
name|assertEquals
argument_list|(
literal|"text/html;charset=UTF-8"
argument_list|,
name|vrw
operator|.
name|getContentType
argument_list|(
name|req
argument_list|(
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|)
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
comment|// if v.contentType is specified, that should be used, even if v.json is specified
name|assertEquals
argument_list|(
literal|"text/plain"
argument_list|,
name|vrw
operator|.
name|getContentType
argument_list|(
name|req
argument_list|(
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|,
name|VelocityResponseWriter
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/plain"
argument_list|)
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain"
argument_list|,
name|vrw
operator|.
name|getContentType
argument_list|(
name|req
argument_list|(
name|VelocityResponseWriter
operator|.
name|TEMPLATE
argument_list|,
literal|"numFound"
argument_list|,
name|VelocityResponseWriter
operator|.
name|JSON
argument_list|,
literal|"wrf"
argument_list|,
name|VelocityResponseWriter
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/plain"
argument_list|)
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
