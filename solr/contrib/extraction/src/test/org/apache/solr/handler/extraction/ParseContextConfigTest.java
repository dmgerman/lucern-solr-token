begin_unit
begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
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
name|core
operator|.
name|SolrResourceLoader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParseContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|pdf
operator|.
name|PDFParserConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_class
DECL|class|ParseContextConfigTest
specifier|public
class|class
name|ParseContextConfigTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testAll
specifier|public
name|void
name|testAll
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|document
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|entries
init|=
name|document
operator|.
name|createElement
argument_list|(
literal|"entries"
argument_list|)
decl_stmt|;
name|Element
name|entry
init|=
name|document
operator|.
name|createElement
argument_list|(
literal|"entry"
argument_list|)
decl_stmt|;
name|entry
operator|.
name|setAttribute
argument_list|(
literal|"class"
argument_list|,
literal|"org.apache.tika.parser.pdf.PDFParserConfig"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAttribute
argument_list|(
literal|"impl"
argument_list|,
literal|"org.apache.tika.parser.pdf.PDFParserConfig"
argument_list|)
expr_stmt|;
name|Element
name|property
init|=
name|document
operator|.
name|createElement
argument_list|(
literal|"property"
argument_list|)
decl_stmt|;
name|property
operator|.
name|setAttribute
argument_list|(
literal|"name"
argument_list|,
literal|"extractInlineImages"
argument_list|)
expr_stmt|;
name|property
operator|.
name|setAttribute
argument_list|(
literal|"value"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|appendChild
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|entries
operator|.
name|appendChild
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContextConfig
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|"."
argument_list|)
argument_list|,
name|entries
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|PDFParserConfig
name|pdfParserConfig
init|=
name|parseContext
operator|.
name|get
argument_list|(
name|PDFParserConfig
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|pdfParserConfig
operator|.
name|getExtractInlineImages
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
