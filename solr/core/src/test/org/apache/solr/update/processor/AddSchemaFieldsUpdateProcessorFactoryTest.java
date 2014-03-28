begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
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
name|TestManagedSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import
begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormat
import|;
end_import
begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|ISODateTimeFormat
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
name|util
operator|.
name|Date
import|;
end_import
begin_comment
comment|/**  * Tests for the field mutating update processors  * that parse Dates, Longs, Doubles, and Booleans.  */
end_comment
begin_class
DECL|class|AddSchemaFieldsUpdateProcessorFactoryTest
specifier|public
class|class
name|AddSchemaFieldsUpdateProcessorFactoryTest
extends|extends
name|UpdateProcessorTestBase
block|{
DECL|field|SOLRCONFIG_XML
specifier|private
specifier|static
specifier|final
name|String
name|SOLRCONFIG_XML
init|=
literal|"solrconfig-add-schema-fields-update-processor-chains.xml"
decl_stmt|;
DECL|field|SCHEMA_XML
specifier|private
specifier|static
specifier|final
name|String
name|SCHEMA_XML
init|=
literal|"schema-add-schema-fields-update-processor.xml"
decl_stmt|;
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
DECL|method|initManagedSchemaCore
specifier|private
name|void
name|initManagedSchemaCore
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|tmpSolrHomePath
init|=
name|createTempDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|tmpSolrHome
operator|=
operator|new
name|File
argument_list|(
name|tmpSolrHomePath
argument_list|)
operator|.
name|getAbsoluteFile
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
name|File
name|testHomeConfDir
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
name|confDir
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
name|SOLRCONFIG_XML
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
name|SCHEMA_XML
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
comment|// initCore will trigger an upgrade to managed schema, since the solrconfig*.xml has
comment|//<schemaFactory class="ManagedIndexSchemaFactory" ... />
name|initCore
argument_list|(
name|SOLRCONFIG_XML
argument_list|,
name|SCHEMA_XML
argument_list|,
name|tmpSolrHome
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|deleteCoreAndTempSolrHomeDirectory
specifier|private
name|void
name|deleteCoreAndTempSolrHomeDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tmpSolrHome
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleField
specifier|public
name|void
name|testSingleField
parameter_list|()
throws|throws
name|Exception
block|{
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
specifier|final
name|String
name|fieldName
init|=
literal|"newfield1"
decl_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|dateString
init|=
literal|"2010-11-12T13:14:15.168Z"
decl_stmt|;
name|DateTimeFormatter
name|dateTimeFormatter
init|=
name|ISODateTimeFormat
operator|.
name|dateTime
argument_list|()
decl_stmt|;
name|Date
name|date
init|=
name|dateTimeFormatter
operator|.
name|parseDateTime
argument_list|(
name|dateString
argument_list|)
operator|.
name|toDate
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"add-fields-no-run-processor"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName
argument_list|,
name|date
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|schema
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tdate"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleFieldRoundTrip
specifier|public
name|void
name|testSingleFieldRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
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
specifier|final
name|String
name|fieldName
init|=
literal|"newfield2"
decl_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|Float
name|floatValue
init|=
operator|-
literal|13258.992f
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"add-fields"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName
argument_list|,
name|floatValue
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|schema
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tfloat"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:2"
argument_list|)
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName
operator|+
literal|"']/float[.='"
operator|+
name|floatValue
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleFieldMixedFieldTypesRoundTrip
specifier|public
name|void
name|testSingleFieldMixedFieldTypesRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
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
specifier|final
name|String
name|fieldName
init|=
literal|"newfield3"
decl_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|Float
name|fieldValue1
init|=
operator|-
literal|13258.0f
decl_stmt|;
name|Double
name|fieldValue2
init|=
literal|8.4828800808E10
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"add-fields"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName
argument_list|,
name|fieldValue1
argument_list|,
name|fieldValue2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|schema
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tdouble"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:3"
argument_list|)
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName
operator|+
literal|"']/double[.='"
operator|+
name|fieldValue1
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName
operator|+
literal|"']/double[.='"
operator|+
name|fieldValue2
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleFieldDefaultFieldTypeRoundTrip
specifier|public
name|void
name|testSingleFieldDefaultFieldTypeRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
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
specifier|final
name|String
name|fieldName
init|=
literal|"newfield4"
decl_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|Float
name|fieldValue1
init|=
operator|-
literal|13258.0f
decl_stmt|;
name|Double
name|fieldValue2
init|=
literal|8.4828800808E10
decl_stmt|;
name|String
name|fieldValue3
init|=
literal|"blah blah"
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"add-fields"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName
argument_list|,
name|fieldValue1
argument_list|,
name|fieldValue2
argument_list|,
name|fieldValue3
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|schema
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:4"
argument_list|)
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName
operator|+
literal|"']/str[.='"
operator|+
name|fieldValue1
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName
operator|+
literal|"']/str[.='"
operator|+
name|fieldValue2
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName
operator|+
literal|"']/str[.='"
operator|+
name|fieldValue3
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleFieldsRoundTrip
specifier|public
name|void
name|testMultipleFieldsRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
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
specifier|final
name|String
name|fieldName1
init|=
literal|"newfield5"
decl_stmt|;
specifier|final
name|String
name|fieldName2
init|=
literal|"newfield6"
decl_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName2
argument_list|)
argument_list|)
expr_stmt|;
name|Float
name|field1Value1
init|=
operator|-
literal|13258.0f
decl_stmt|;
name|Double
name|field1Value2
init|=
literal|8.4828800808E10
decl_stmt|;
name|Long
name|field1Value3
init|=
literal|999L
decl_stmt|;
name|Integer
name|field2Value1
init|=
literal|55123
decl_stmt|;
name|Long
name|field2Value2
init|=
literal|1234567890123456789L
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"add-fields"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName1
argument_list|,
name|field1Value1
argument_list|,
name|field1Value2
argument_list|,
name|field1Value3
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName2
argument_list|,
name|field2Value1
argument_list|,
name|field2Value2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|schema
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tdouble"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName1
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tlong"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName2
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:5"
argument_list|)
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName1
operator|+
literal|"']/double[.='"
operator|+
name|field1Value1
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName1
operator|+
literal|"']/double[.='"
operator|+
name|field1Value2
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName1
operator|+
literal|"']/double[.='"
operator|+
name|field1Value3
operator|.
name|doubleValue
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName2
operator|+
literal|"']/long[.='"
operator|+
name|field2Value1
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName2
operator|+
literal|"']/long[.='"
operator|+
name|field2Value2
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseAndAddMultipleFieldsRoundTrip
specifier|public
name|void
name|testParseAndAddMultipleFieldsRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
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
specifier|final
name|String
name|fieldName1
init|=
literal|"newfield7"
decl_stmt|;
specifier|final
name|String
name|fieldName2
init|=
literal|"newfield8"
decl_stmt|;
specifier|final
name|String
name|fieldName3
init|=
literal|"newfield9"
decl_stmt|;
specifier|final
name|String
name|fieldName4
init|=
literal|"newfield10"
decl_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName3
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName4
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|field1String1
init|=
literal|"-13,258.0"
decl_stmt|;
name|Float
name|field1Value1
init|=
operator|-
literal|13258.0f
decl_stmt|;
name|String
name|field1String2
init|=
literal|"84,828,800,808.0"
decl_stmt|;
name|Double
name|field1Value2
init|=
literal|8.4828800808E10
decl_stmt|;
name|String
name|field1String3
init|=
literal|"999"
decl_stmt|;
name|Long
name|field1Value3
init|=
literal|999L
decl_stmt|;
name|String
name|field2String1
init|=
literal|"55,123"
decl_stmt|;
name|Integer
name|field2Value1
init|=
literal|55123
decl_stmt|;
name|String
name|field2String2
init|=
literal|"1,234,567,890,123,456,789"
decl_stmt|;
name|Long
name|field2Value2
init|=
literal|1234567890123456789L
decl_stmt|;
name|String
name|field3String1
init|=
literal|"blah-blah"
decl_stmt|;
name|String
name|field3Value1
init|=
name|field3String1
decl_stmt|;
name|String
name|field3String2
init|=
literal|"-5.28E-3"
decl_stmt|;
name|Double
name|field3Value2
init|=
operator|-
literal|5.28E
operator|-
literal|3
decl_stmt|;
name|String
name|field4String1
init|=
literal|"1999-04-17 17:42"
decl_stmt|;
name|DateTimeFormatter
name|dateTimeFormatter
init|=
name|DateTimeFormat
operator|.
name|forPattern
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|)
operator|.
name|withZoneUTC
argument_list|()
decl_stmt|;
name|DateTime
name|dateTime
init|=
name|dateTimeFormatter
operator|.
name|parseDateTime
argument_list|(
name|field4String1
argument_list|)
decl_stmt|;
name|Date
name|field4Value1
init|=
name|dateTime
operator|.
name|toDate
argument_list|()
decl_stmt|;
name|DateTimeFormatter
name|dateTimeFormatter2
init|=
name|DateTimeFormat
operator|.
name|forPattern
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss"
argument_list|)
operator|.
name|withZoneUTC
argument_list|()
decl_stmt|;
name|String
name|field4Value1String
init|=
name|dateTimeFormatter2
operator|.
name|print
argument_list|(
name|dateTime
argument_list|)
operator|+
literal|"Z"
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"parse-and-add-fields"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName1
argument_list|,
name|field1String1
argument_list|,
name|field1String2
argument_list|,
name|field1String3
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName2
argument_list|,
name|field2String1
argument_list|,
name|field2String2
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName3
argument_list|,
name|field3String1
argument_list|,
name|field3String2
argument_list|)
argument_list|,
name|f
argument_list|(
name|fieldName4
argument_list|,
name|field4String1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|schema
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName3
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tdouble"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName1
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tlong"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName2
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName3
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tdate"
argument_list|,
name|schema
operator|.
name|getFieldType
argument_list|(
name|fieldName4
argument_list|)
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:6"
argument_list|)
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName1
operator|+
literal|"']/double[.='"
operator|+
name|field1Value1
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName1
operator|+
literal|"']/double[.='"
operator|+
name|field1Value2
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName1
operator|+
literal|"']/double[.='"
operator|+
name|field1Value3
operator|.
name|doubleValue
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName2
operator|+
literal|"']/long[.='"
operator|+
name|field2Value1
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName2
operator|+
literal|"']/long[.='"
operator|+
name|field2Value2
operator|.
name|toString
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName3
operator|+
literal|"']/str[.='"
operator|+
name|field3String1
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName3
operator|+
literal|"']/str[.='"
operator|+
name|field3String2
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='"
operator|+
name|fieldName4
operator|+
literal|"']/date[.='"
operator|+
name|field4Value1String
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
