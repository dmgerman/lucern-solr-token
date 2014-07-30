begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
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
name|Config
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
name|FieldType
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|tr
operator|.
name|TurkishAnalyzer
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
name|Version
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
comment|/**  * Tests for luceneMatchVersion property for analyzers  */
end_comment
begin_class
DECL|class|TestLuceneMatchVersion
specifier|public
class|class
name|TestLuceneMatchVersion
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
literal|"schema-luceneMatchVersion.xml"
argument_list|)
expr_stmt|;
block|}
comment|// this must match the solrconfig.xml version for this test
DECL|field|DEFAULT_VERSION
specifier|public
specifier|static
specifier|final
name|Version
name|DEFAULT_VERSION
init|=
name|Config
operator|.
name|parseLuceneVersionString
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.luceneMatchVersion"
argument_list|,
literal|"LUCENE_CURRENT"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|testStandardTokenizerVersions
specifier|public
name|void
name|testStandardTokenizerVersions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|solrConfig
operator|.
name|luceneMatchVersion
argument_list|)
expr_stmt|;
specifier|final
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
name|FieldType
name|type
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
literal|"textDefault"
argument_list|)
decl_stmt|;
name|TokenizerChain
name|ana
init|=
operator|(
name|TokenizerChain
operator|)
name|type
operator|.
name|getIndexAnalyzer
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|(
name|ana
operator|.
name|getTokenizerFactory
argument_list|()
operator|)
operator|.
name|getLuceneMatchVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|(
name|ana
operator|.
name|getTokenFilterFactories
argument_list|()
index|[
literal|2
index|]
operator|)
operator|.
name|getLuceneMatchVersion
argument_list|()
argument_list|)
expr_stmt|;
name|type
operator|=
name|schema
operator|.
name|getFieldType
argument_list|(
literal|"text40"
argument_list|)
expr_stmt|;
name|ana
operator|=
operator|(
name|TokenizerChain
operator|)
name|type
operator|.
name|getIndexAnalyzer
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_4_0
argument_list|,
operator|(
name|ana
operator|.
name|getTokenizerFactory
argument_list|()
operator|)
operator|.
name|getLuceneMatchVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_5_0
argument_list|,
operator|(
name|ana
operator|.
name|getTokenFilterFactories
argument_list|()
index|[
literal|2
index|]
operator|)
operator|.
name|getLuceneMatchVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// this is a hack to get the private matchVersion field in TurkishAnalyzer's class, may break in later lucene versions - we have no getter :(
specifier|final
name|Field
name|matchVersionField
init|=
name|TurkishAnalyzer
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"matchVersion"
argument_list|)
decl_stmt|;
name|matchVersionField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|type
operator|=
name|schema
operator|.
name|getFieldType
argument_list|(
literal|"textTurkishAnalyzerDefault"
argument_list|)
expr_stmt|;
name|Analyzer
name|ana1
init|=
name|type
operator|.
name|getIndexAnalyzer
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ana1
operator|instanceof
name|TurkishAnalyzer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|matchVersionField
operator|.
name|get
argument_list|(
name|ana1
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|=
name|schema
operator|.
name|getFieldType
argument_list|(
literal|"textTurkishAnalyzer40"
argument_list|)
expr_stmt|;
name|ana1
operator|=
name|type
operator|.
name|getIndexAnalyzer
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ana1
operator|instanceof
name|TurkishAnalyzer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_4_0
argument_list|,
name|matchVersionField
operator|.
name|get
argument_list|(
name|ana1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
