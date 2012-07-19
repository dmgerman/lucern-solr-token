begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|perfield
operator|.
name|PerFieldPostingsFormat
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
name|BeforeClass
import|;
end_import
begin_class
DECL|class|TestCodecSupport
specifier|public
class|class
name|TestCodecSupport
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
literal|"solrconfig_codec.xml"
argument_list|,
literal|"schema_codec.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPostingsFormats
specifier|public
name|void
name|testPostingsFormats
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaField
argument_list|>
name|fields
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|SchemaField
name|schemaField
init|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_pulsing_f"
argument_list|)
decl_stmt|;
name|PerFieldPostingsFormat
name|format
init|=
operator|(
name|PerFieldPostingsFormat
operator|)
name|codec
operator|.
name|postingsFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Pulsing40"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|schemaField
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_simpletext_f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"SimpleText"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|schemaField
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_standard_f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lucene40"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|schemaField
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lucene40"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDynamicFields
specifier|public
name|void
name|testDynamicFields
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|PerFieldPostingsFormat
name|format
init|=
operator|(
name|PerFieldPostingsFormat
operator|)
name|codec
operator|.
name|postingsFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"SimpleText"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"foo_simple"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"SimpleText"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"bar_simple"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Pulsing40"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"foo_pulsing"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Pulsing40"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"bar_pulsing"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lucene40"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"foo_standard"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lucene40"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"bar_standard"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnknownField
specifier|public
name|void
name|testUnknownField
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|PerFieldPostingsFormat
name|format
init|=
operator|(
name|PerFieldPostingsFormat
operator|)
name|codec
operator|.
name|postingsFormat
argument_list|()
decl_stmt|;
try|try
block|{
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"notexisting"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"field is not existing"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
end_class
end_unit
