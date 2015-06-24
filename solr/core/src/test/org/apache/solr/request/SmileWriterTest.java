begin_unit
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|InputStream
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Random
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|JsonNode
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|ArrayNode
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|BinaryNode
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|BooleanNode
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|NullNode
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|NumericNode
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|ObjectNode
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|dataformat
operator|.
name|smile
operator|.
name|SmileFactory
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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|SmileResponseWriter
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
name|search
operator|.
name|ReturnFields
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
name|search
operator|.
name|SolrReturnFields
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
name|noggit
operator|.
name|CharArr
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
name|JSONWriter
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
begin_class
DECL|class|SmileWriterTest
specifier|public
class|class
name|SmileWriterTest
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTypes
specifier|public
name|void
name|testTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data1"
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data2"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data3"
argument_list|,
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|SmileResponseWriter
name|smileResponseWriter
init|=
operator|new
name|SmileResponseWriter
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|smileResponseWriter
operator|.
name|write
argument_list|(
name|baos
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|decodeSmile
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|JSONWriter
name|jsonWriter
init|=
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|jsonWriter
operator|.
name|setIndentSize
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// indentation by default
name|jsonWriter
operator|.
name|write
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|ZkStateReader
operator|.
name|toUTF8
argument_list|(
name|out
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"data1\":NaN,\"data2\":-Infinity,\"data3\":Infinity}"
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJSON
specifier|public
name|void
name|testJSON
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"json.nl"
argument_list|,
literal|"arrarr"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SmileResponseWriter
name|w
init|=
operator|new
name|SmileResponseWriter
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|buf
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"data1"
argument_list|,
literal|"he\u2028llo\u2029!"
argument_list|)
expr_stmt|;
comment|// make sure that 2028 and 2029 are both escaped (they are illegal in javascript)
name|nl
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"nl"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"byte"
argument_list|,
name|Byte
operator|.
name|valueOf
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"short"
argument_list|,
name|Short
operator|.
name|valueOf
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|"{\"nl\":[[\"data1\",\"he\\u2028llo\\u2029!\"],[null,42]],byte:-3,short:-4}"
decl_stmt|;
name|w
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
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|decodeSmile
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|o2
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
name|expected
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ZkStateReader
operator|.
name|toJSONString
argument_list|(
name|m
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|toJSONString
argument_list|(
name|o2
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJSONSolrDocument
specifier|public
name|void
name|testJSONSolrDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id,score"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SmileResponseWriter
name|w
init|=
operator|new
name|SmileResponseWriter
argument_list|()
decl_stmt|;
name|ReturnFields
name|returnFields
init|=
operator|new
name|SolrReturnFields
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setReturnFields
argument_list|(
name|returnFields
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|buf
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|SolrDocument
name|solrDoc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|,
literal|"hello2"
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
literal|"hello3"
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"score"
argument_list|,
literal|"0.7"
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|list
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|list
operator|.
name|setNumFound
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|list
operator|.
name|setStart
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|list
operator|.
name|setMaxScore
argument_list|(
literal|0.7f
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|solrDoc
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|w
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
name|byte
index|[]
name|bytes
init|=
name|buf
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|decodeSmile
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|m
operator|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
expr_stmt|;
name|List
name|l
init|=
operator|(
name|List
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
decl_stmt|;
name|Map
name|doc
init|=
operator|(
name|Map
operator|)
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|containsKey
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|containsKey
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|containsKey
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|containsKey
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test10Docs
specifier|public
name|void
name|test10Docs
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrDocumentList
name|l
init|=
operator|new
name|SolrDocumentList
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|sampleDoc
argument_list|(
name|random
argument_list|()
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SolrQueryResponse
name|response
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|getValues
argument_list|()
operator|.
name|add
argument_list|(
literal|"results"
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
operator|new
name|SmileResponseWriter
argument_list|()
operator|.
name|write
argument_list|(
name|baos
argument_list|,
operator|new
name|LocalSolrQueryRequest
argument_list|(
literal|null
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|decodeSmile
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|m
operator|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"results"
argument_list|)
expr_stmt|;
name|List
name|lst
init|=
operator|(
name|List
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|lst
operator|.
name|size
argument_list|()
argument_list|,
literal|10
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
name|lst
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|m
operator|=
operator|(
name|Map
operator|)
name|lst
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|SolrDocument
name|d
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|d
operator|.
name|putAll
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|compareSolrDocument
argument_list|(
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sampleDoc
specifier|public
specifier|static
name|SolrDocument
name|sampleDoc
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|bufnum
parameter_list|)
block|{
name|SolrDocument
name|sdoc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"my_id_"
operator|+
name|bufnum
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"author"
argument_list|,
name|str
argument_list|(
name|r
argument_list|,
literal|10
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"address"
argument_list|,
name|str
argument_list|(
name|r
argument_list|,
literal|20
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"license"
argument_list|,
name|str
argument_list|(
name|r
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"title"
argument_list|,
name|str
argument_list|(
name|r
argument_list|,
literal|5
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"title_bin"
argument_list|,
name|str
argument_list|(
name|r
argument_list|,
literal|5
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"modified_dt"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"creation_dt"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"birthdate_dt"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"clean"
argument_list|,
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"dirty"
argument_list|,
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"employed"
argument_list|,
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"priority"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"dependents"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"level"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|101
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"education_level"
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// higher level of reuse for string values
name|sdoc
operator|.
name|put
argument_list|(
literal|"state"
argument_list|,
literal|"S"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"country"
argument_list|,
literal|"Country"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"some_boolean"
argument_list|,
literal|""
operator|+
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|sdoc
operator|.
name|put
argument_list|(
literal|"another_boolean"
argument_list|,
literal|""
operator|+
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sdoc
return|;
block|}
comment|// common-case ascii
DECL|method|str
specifier|static
name|String
name|str
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|sz
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|sz
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|128
operator|-
literal|'\n'
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|decodeSmile
specifier|public
specifier|static
name|Object
name|decodeSmile
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SmileFactory
name|smileFactory
init|=
operator|new
name|SmileFactory
argument_list|()
decl_stmt|;
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
name|mapper
init|=
operator|new
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
argument_list|(
name|smileFactory
argument_list|)
decl_stmt|;
name|JsonNode
name|jsonNode
init|=
name|mapper
operator|.
name|readTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
return|return
name|getVal
argument_list|(
name|jsonNode
argument_list|)
return|;
block|}
DECL|method|getVal
specifier|public
specifier|static
name|Object
name|getVal
parameter_list|(
name|JsonNode
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|NullNode
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|NumericNode
condition|)
block|{
return|return
operator|(
operator|(
name|NumericNode
operator|)
name|value
operator|)
operator|.
name|numberValue
argument_list|()
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|BooleanNode
condition|)
block|{
operator|(
operator|(
name|BooleanNode
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|ObjectNode
condition|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonNode
argument_list|>
argument_list|>
name|it
init|=
operator|(
operator|(
name|ObjectNode
operator|)
name|value
operator|)
operator|.
name|fields
argument_list|()
decl_stmt|;
name|Map
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonNode
argument_list|>
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|getVal
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|ArrayNode
condition|)
block|{
name|ArrayList
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|JsonNode
argument_list|>
name|it
init|=
operator|(
operator|(
name|ArrayNode
operator|)
name|value
operator|)
operator|.
name|elements
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|getVal
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|BinaryNode
condition|)
block|{
return|return
operator|(
operator|(
name|BinaryNode
operator|)
name|value
operator|)
operator|.
name|binaryValue
argument_list|()
return|;
block|}
return|return
name|value
operator|.
name|textValue
argument_list|()
return|;
block|}
block|}
end_class
end_unit
