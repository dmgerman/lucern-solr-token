begin_unit
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|RestTestBase
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
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|restlet
operator|.
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
import|;
end_import
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
name|io
operator|.
name|StringReader
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
name|SortedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import
begin_class
DECL|class|TestBulkSchemaAPI
specifier|public
class|class
name|TestBulkSchemaAPI
extends|extends
name|RestTestBase
block|{
DECL|field|tmpSolrHome
specifier|private
specifier|static
name|File
name|tmpSolrHome
decl_stmt|;
DECL|field|tmpConfDir
specifier|private
specifier|static
name|File
name|tmpConfDir
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
DECL|field|confDir
specifier|private
specifier|static
specifier|final
name|String
name|confDir
init|=
name|collection
operator|+
literal|"/conf"
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|tmpSolrHome
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|tmpConfDir
operator|=
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
argument_list|,
name|tmpSolrHome
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrSchemaRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrSchemaRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrRestApi
argument_list|,
literal|"/schema/*"
argument_list|)
expr_stmt|;
comment|// '/schema/*' matches '/schema', '/schema/', and '/schema/whatever...'
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|createJettyAndHarness
argument_list|(
name|tmpSolrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-rest.xml"
argument_list|,
literal|"/solr"
argument_list|,
literal|true
argument_list|,
name|extraServlets
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
block|}
name|server
operator|=
literal|null
expr_stmt|;
name|restTestHarness
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testMultipleAddFieldWithErrors
specifier|public
name|void
name|testMultipleAddFieldWithErrors
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
literal|"{\n"
operator|+
literal|"    'add-field' : {\n"
operator|+
literal|"                 'name':'a1',\n"
operator|+
literal|"                 'type': 'string1',\n"
operator|+
literal|"                 'stored':true,\n"
operator|+
literal|"                 'indexed':false\n"
operator|+
literal|"                 },\n"
operator|+
literal|"    'add-field' : {\n"
operator|+
literal|"                 'type': 'string',\n"
operator|+
literal|"                 'stored':true,\n"
operator|+
literal|"                 'indexed':true\n"
operator|+
literal|"                 }\n"
operator|+
literal|"   \n"
operator|+
literal|"    }"
argument_list|)
decl_stmt|;
name|String
name|response
init|=
name|restTestHarness
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|payload
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
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
decl_stmt|;
name|List
name|l
init|=
operator|(
name|List
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
decl_stmt|;
name|List
name|errorList
init|=
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"errorMessages"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|errorList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|String
operator|)
name|errorList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|contains
argument_list|(
literal|"No such field type"
argument_list|)
argument_list|)
expr_stmt|;
name|errorList
operator|=
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|l
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"errorMessages"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|errorList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|String
operator|)
name|errorList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|contains
argument_list|(
literal|"is a required field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleCommands
specifier|public
name|void
name|testMultipleCommands
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"          'add-field' : {\n"
operator|+
literal|"                       'name':'a1',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'indexed':false\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'add-field' : {\n"
operator|+
literal|"                       'name':'a2',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'indexed':true\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'add-dynamic-field' : {\n"
operator|+
literal|"                       'name' :'*_lol',\n"
operator|+
literal|"                        'type':'string',\n"
operator|+
literal|"                        'stored':true,\n"
operator|+
literal|"                        'indexed':true\n"
operator|+
literal|"                        },\n"
operator|+
literal|"          'add-copy-field' : {\n"
operator|+
literal|"                       'source' :'a1',\n"
operator|+
literal|"                        'dest':['a2','hello_lol']\n"
operator|+
literal|"                        },\n"
operator|+
literal|"          'add-field-type' : {\n"
operator|+
literal|"                       'name' :'mystr',\n"
operator|+
literal|"                       'class' : 'solr.StrField',\n"
operator|+
literal|"                        'sortMissingLast':'true'\n"
operator|+
literal|"                        },\n"
operator|+
literal|"          'add-field-type' : {"
operator|+
literal|"                     'name' : 'myNewTxtField',\n"
operator|+
literal|"                     'class':'solr.TextField','positionIncrementGap':'100',\n"
operator|+
literal|"                     'analyzer' : {\n"
operator|+
literal|"                                  'charFilters':[\n"
operator|+
literal|"                                            {'class':'solr.PatternReplaceCharFilterFactory','replacement':'$1$1','pattern':'([a-zA-Z])\\\\\\\\1+'}\n"
operator|+
literal|"                                         ],\n"
operator|+
literal|"                     'tokenizer':{'class':'solr.WhitespaceTokenizerFactory'},\n"
operator|+
literal|"                     'filters':[\n"
operator|+
literal|"                             {'class':'solr.WordDelimiterFilterFactory','preserveOriginal':'0'},\n"
operator|+
literal|"                             {'class':'solr.StopFilterFactory','words':'stopwords.txt','ignoreCase':'true'},\n"
operator|+
literal|"                             {'class':'solr.LowerCaseFilterFactory'},\n"
operator|+
literal|"                             {'class':'solr.ASCIIFoldingFilterFactory'},\n"
operator|+
literal|"                             {'class':'solr.KStemFilterFactory'}\n"
operator|+
literal|"                  ]\n"
operator|+
literal|"                }\n"
operator|+
literal|"              }"
operator|+
literal|"          }"
decl_stmt|;
name|RestTestHarness
name|harness
init|=
name|restTestHarness
decl_stmt|;
name|String
name|response
init|=
name|harness
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
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
decl_stmt|;
name|assertNull
argument_list|(
name|response
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a1"
argument_list|,
literal|"fields"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"field a1 not created"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"stored"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"indexed"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a2"
argument_list|,
literal|"fields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field a2 not created"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"stored"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"indexed"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"*_lol"
argument_list|,
literal|"dynamicFields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field *_lol not created"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"stored"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"indexed"
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|l
init|=
name|getCopyFields
argument_list|(
name|harness
argument_list|,
literal|"a1"
argument_list|)
decl_stmt|;
name|Set
name|s
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|l
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"dest"
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|l
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"dest"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
literal|"hello_lol"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|contains
argument_list|(
literal|"a2"
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"mystr"
argument_list|,
literal|"fieldTypes"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solr.StrField"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"sortMissingLast"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"myNewTxtField"
argument_list|,
literal|"fieldTypes"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
DECL|method|getObj
specifier|public
specifier|static
name|Map
name|getObj
parameter_list|(
name|RestTestHarness
name|restHarness
parameter_list|,
name|String
name|fld
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
name|map
init|=
name|getRespMap
argument_list|(
name|restHarness
argument_list|)
decl_stmt|;
name|List
name|l
init|=
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"schema"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|l
control|)
block|{
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|fld
operator|.
name|equals
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
condition|)
return|return
name|m
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getRespMap
specifier|public
specifier|static
name|Map
name|getRespMap
parameter_list|(
name|RestTestHarness
name|restHarness
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|response
init|=
name|restHarness
operator|.
name|query
argument_list|(
literal|"/schema?wt=json"
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
DECL|method|getCopyFields
specifier|public
specifier|static
name|List
name|getCopyFields
parameter_list|(
name|RestTestHarness
name|harness
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
name|map
init|=
name|getRespMap
argument_list|(
name|harness
argument_list|)
decl_stmt|;
name|List
name|l
init|=
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"schema"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"copyFields"
argument_list|)
decl_stmt|;
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|l
control|)
block|{
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|src
operator|.
name|equals
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"source"
argument_list|)
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
