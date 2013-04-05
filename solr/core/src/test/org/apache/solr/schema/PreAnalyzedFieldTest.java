begin_unit
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|document
operator|.
name|Field
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
name|util
operator|.
name|Base64
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
name|PreAnalyzedField
operator|.
name|PreAnalyzedParser
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
begin_class
DECL|class|PreAnalyzedFieldTest
specifier|public
class|class
name|PreAnalyzedFieldTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|valid
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|valid
init|=
block|{
literal|"1 one two three"
block|,
comment|// simple parsing
literal|"1  one  two   three "
block|,
comment|// spurious spaces
literal|"1 one,s=123,e=128,i=22  two three,s=20,e=22,y=foobar"
block|,
comment|// attribs
literal|"1 \\ one\\ \\,,i=22,a=\\, two\\=\n\r\t\\n,\\ =\\   \\"
block|,
comment|// escape madness
literal|"1 ,i=22 ,i=33,s=2,e=20 , "
block|,
comment|// empty token text, non-empty attribs
literal|"1 =This is the stored part with \\= \n \\n \t \\t escapes.=one two three  \u0001ÄÄÄÅÅÃ³ÅÅºÅ¼"
block|,
comment|// stored plus token stream
literal|"1 =="
block|,
comment|// empty stored, no token stream
literal|"1 =this is a test.="
block|,
comment|// stored + empty token stream
literal|"1 one,p=deadbeef two,p=0123456789abcdef three"
comment|// payloads
block|}
decl_stmt|;
DECL|field|validParsed
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|validParsed
init|=
block|{
literal|"1 one,s=0,e=3 two,s=4,e=7 three,s=8,e=13"
block|,
literal|"1 one,s=1,e=4 two,s=6,e=9 three,s=12,e=17"
block|,
literal|"1 one,i=22,s=123,e=128,y=word two,i=1,s=5,e=8,y=word three,i=1,s=20,e=22,y=foobar"
block|,
literal|"1 \\ one\\ \\,,i=22,s=0,e=6 two\\=\\n\\r\\t\\n,i=1,s=7,e=15 \\\\,i=1,s=17,e=18"
block|,
literal|"1 i=22,s=0,e=0 i=33,s=2,e=20 i=1,s=2,e=2"
block|,
literal|"1 =This is the stored part with = \n \\n \t \\t escapes.=one,s=0,e=3 two,s=4,e=7 three,s=8,e=13 \u0001ÄÄÄÅÅÃ³ÅÅºÅ¼,s=15,e=25"
block|,
literal|"1 =="
block|,
literal|"1 =this is a test.="
block|,
literal|"1 one,p=deadbeef,s=0,e=3 two,p=0123456789abcdef,s=4,e=7 three,s=8,e=13"
block|}
decl_stmt|;
DECL|field|invalid
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|invalid
init|=
block|{
literal|"one two three"
block|,
comment|// missing version #
literal|"2 one two three"
block|,
comment|// invalid version #
literal|"1 o,ne two"
block|,
comment|// missing escape
literal|"1 one t=wo"
block|,
comment|// missing escape
literal|"1 one,, two"
block|,
comment|// missing attribs, unescaped comma
literal|"1 one,s "
block|,
comment|// missing attrib value
literal|"1 one,s= val"
block|,
comment|// missing attrib value, unescaped space
literal|"1 one,s=,val"
block|,
comment|// unescaped comma
literal|"1 ="
block|,
comment|// unescaped equals
literal|"1 =stored "
block|,
comment|// unterminated stored
literal|"1 ==="
comment|// empty stored (ok), but unescaped = in token stream
block|}
decl_stmt|;
DECL|field|field
name|SchemaField
name|field
init|=
literal|null
decl_stmt|;
DECL|field|props
name|int
name|props
init|=
name|FieldProperties
operator|.
name|INDEXED
operator||
name|FieldProperties
operator|.
name|STORED
decl_stmt|;
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
name|field
operator|=
operator|new
name|SchemaField
argument_list|(
literal|"content"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidSimple
specifier|public
name|void
name|testValidSimple
parameter_list|()
block|{
name|PreAnalyzedField
name|paf
init|=
operator|new
name|PreAnalyzedField
argument_list|()
decl_stmt|;
comment|// use Simple format
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PreAnalyzedField
operator|.
name|PARSER_IMPL
argument_list|,
name|SimplePreAnalyzedParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|paf
operator|.
name|init
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|PreAnalyzedParser
name|parser
init|=
operator|new
name|SimplePreAnalyzedParser
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
name|valid
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|valid
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|Field
name|f
init|=
operator|(
name|Field
operator|)
name|paf
operator|.
name|fromString
argument_list|(
name|field
argument_list|,
name|s
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
comment|//System.out.println(" - toString: '" + sb.toString() + "'");
name|assertEquals
argument_list|(
name|validParsed
index|[
name|i
index|]
argument_list|,
name|parser
operator|.
name|toFormattedString
argument_list|(
name|f
argument_list|)
argument_list|)
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
name|fail
argument_list|(
literal|"Should pass: '"
operator|+
name|s
operator|+
literal|"', exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidSimple
specifier|public
name|void
name|testInvalidSimple
parameter_list|()
block|{
name|PreAnalyzedField
name|paf
init|=
operator|new
name|PreAnalyzedField
argument_list|()
decl_stmt|;
name|paf
operator|.
name|init
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|invalid
control|)
block|{
try|try
block|{
name|paf
operator|.
name|fromString
argument_list|(
name|field
argument_list|,
name|s
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail: '"
operator|+
name|s
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
comment|// "1 =test ÄÄÄÅÅÃ³ÅÅºÅ¼ \u0001=one,i=22,s=123,e=128,p=deadbeef,y=word two,i=1,s=5,e=8,y=word three,i=1,s=20,e=22,y=foobar"
DECL|field|jsonValid
specifier|private
specifier|static
specifier|final
name|String
name|jsonValid
init|=
literal|"{\"v\":\"1\",\"str\":\"test ÄÄÄÅÅÃ³ÅÅºÅ¼\",\"tokens\":["
operator|+
literal|"{\"e\":128,\"i\":22,\"p\":\"DQ4KDQsODg8=\",\"s\":123,\"t\":\"one\",\"y\":\"word\"},"
operator|+
literal|"{\"e\":8,\"i\":1,\"s\":5,\"t\":\"two\",\"y\":\"word\"},"
operator|+
literal|"{\"e\":22,\"i\":1,\"s\":20,\"t\":\"three\",\"y\":\"foobar\"}"
operator|+
literal|"]}"
decl_stmt|;
annotation|@
name|Test
DECL|method|testParsers
specifier|public
name|void
name|testParsers
parameter_list|()
block|{
name|PreAnalyzedField
name|paf
init|=
operator|new
name|PreAnalyzedField
argument_list|()
decl_stmt|;
comment|// use Simple format
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PreAnalyzedField
operator|.
name|PARSER_IMPL
argument_list|,
name|SimplePreAnalyzedParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|paf
operator|.
name|init
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
try|try
block|{
name|Field
name|f
init|=
operator|(
name|Field
operator|)
name|paf
operator|.
name|fromString
argument_list|(
name|field
argument_list|,
name|valid
index|[
literal|0
index|]
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should pass: '"
operator|+
name|valid
index|[
literal|0
index|]
operator|+
literal|"', exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
comment|// use JSON format
name|args
operator|.
name|put
argument_list|(
name|PreAnalyzedField
operator|.
name|PARSER_IMPL
argument_list|,
name|JsonPreAnalyzedParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|paf
operator|.
name|init
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
try|try
block|{
name|Field
name|f
init|=
operator|(
name|Field
operator|)
name|paf
operator|.
name|fromString
argument_list|(
name|field
argument_list|,
name|valid
index|[
literal|0
index|]
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Should fail JSON parsing: '"
operator|+
name|valid
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
name|byte
index|[]
name|deadbeef
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xd
block|,
operator|(
name|byte
operator|)
literal|0xe
block|,
operator|(
name|byte
operator|)
literal|0xa
block|,
operator|(
name|byte
operator|)
literal|0xd
block|,
operator|(
name|byte
operator|)
literal|0xb
block|,
operator|(
name|byte
operator|)
literal|0xe
block|,
operator|(
name|byte
operator|)
literal|0xe
block|,
operator|(
name|byte
operator|)
literal|0xf
block|}
decl_stmt|;
name|PreAnalyzedParser
name|parser
init|=
operator|new
name|JsonPreAnalyzedParser
argument_list|()
decl_stmt|;
try|try
block|{
name|Field
name|f
init|=
operator|(
name|Field
operator|)
name|paf
operator|.
name|fromString
argument_list|(
name|field
argument_list|,
name|jsonValid
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|jsonValid
argument_list|,
name|parser
operator|.
name|toFormattedString
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should pass: '"
operator|+
name|jsonValid
operator|+
literal|"', exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
