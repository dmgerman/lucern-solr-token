begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
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
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|index
operator|.
name|StorableField
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
name|SolrConfig
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
name|solr
operator|.
name|util
operator|.
name|DateFormatUtil
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
name|DateMathParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_class
DECL|class|DateFieldTest
specifier|public
class|class
name|DateFieldTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|testInstanceDir
specifier|private
specifier|final
name|String
name|testInstanceDir
init|=
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
decl_stmt|;
DECL|field|testConfHome
specifier|private
specifier|final
name|String
name|testConfHome
init|=
name|testInstanceDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
DECL|field|f
specifier|private
name|TrieDateField
name|f
init|=
literal|null
decl_stmt|;
DECL|field|p
specifier|private
name|DateMathParser
name|p
init|=
operator|new
name|DateMathParser
argument_list|(
name|DateFormatUtil
operator|.
name|UTC
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
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
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|SolrConfig
name|config
init|=
operator|new
name|SolrConfig
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|testInstanceDir
argument_list|)
argument_list|)
argument_list|,
name|testConfHome
operator|+
literal|"solrconfig.xml"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|testConfHome
operator|+
literal|"schema.xml"
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|f
operator|=
operator|new
name|TrieDateField
argument_list|()
expr_stmt|;
name|f
operator|.
name|init
argument_list|(
name|schema
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
block|}
DECL|method|assertFormatParsed
specifier|public
name|void
name|assertFormatParsed
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|input
parameter_list|)
throws|throws
name|ParseException
block|{
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|DateFormatUtil
operator|.
name|formatDate
argument_list|(
name|DateFormatUtil
operator|.
name|parseMath
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|,
name|input
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFormatDate
specifier|public
name|void
name|assertFormatDate
parameter_list|(
name|String
name|expected
parameter_list|,
name|long
name|input
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|DateFormatUtil
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToInternal
specifier|public
name|void
name|testToInternal
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|"1995-12-31T23:59:59.999666Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|"1995-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59.99"
argument_list|,
literal|"1995-12-31T23:59:59.99Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|"1995-12-31T23:59:59.9Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59Z"
argument_list|)
expr_stmt|;
comment|// here the input isn't in the canonical form, but we should be forgiving
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59.99"
argument_list|,
literal|"1995-12-31T23:59:59.990Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|"1995-12-31T23:59:59.900Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|"1995-12-31T23:59:59.90Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59.000Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59.00Z"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59.0Z"
argument_list|)
expr_stmt|;
comment|// kind of kludgy, but we have other tests for the actual date math
name|assertFormatParsed
argument_list|(
name|DateFormatUtil
operator|.
name|formatDate
argument_list|(
name|p
operator|.
name|parseMath
argument_list|(
literal|"/DAY"
argument_list|)
argument_list|)
argument_list|,
literal|"NOW/DAY"
argument_list|)
expr_stmt|;
comment|// as of Solr 1.3
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T00:00:00"
argument_list|,
literal|"1995-12-31T23:59:59Z/DAY"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T00:00:00"
argument_list|,
literal|"1995-12-31T23:59:59.123Z/DAY"
argument_list|)
expr_stmt|;
name|assertFormatParsed
argument_list|(
literal|"1995-12-31T00:00:00"
argument_list|,
literal|"1995-12-31T23:59:59.123999Z/DAY"
argument_list|)
expr_stmt|;
block|}
DECL|method|testToInternalObj
specifier|public
name|void
name|testToInternalObj
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFormatDate
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|820454399999l
argument_list|)
expr_stmt|;
name|assertFormatDate
argument_list|(
literal|"1995-12-31T23:59:59.99"
argument_list|,
literal|820454399990l
argument_list|)
expr_stmt|;
name|assertFormatDate
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|820454399900l
argument_list|)
expr_stmt|;
name|assertFormatDate
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|820454399000l
argument_list|)
expr_stmt|;
block|}
DECL|method|assertParseMath
specifier|public
name|void
name|assertParseMath
parameter_list|(
name|long
name|expected
parameter_list|,
name|String
name|input
parameter_list|)
block|{
name|Date
name|d
init|=
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|DateFormatUtil
operator|.
name|parseMath
argument_list|(
name|d
argument_list|,
name|input
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// as of Solr1.3
DECL|method|testParseMath
specifier|public
name|void
name|testParseMath
parameter_list|()
block|{
name|assertParseMath
argument_list|(
literal|820454699999l
argument_list|,
literal|"1995-12-31T23:59:59.999765Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|820454699999l
argument_list|,
literal|"1995-12-31T23:59:59.999Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|820454699990l
argument_list|,
literal|"1995-12-31T23:59:59.99Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00Z/DAY"
argument_list|)
expr_stmt|;
comment|// here the input isn't in the canonical form, but we should be forgiving
name|assertParseMath
argument_list|(
literal|820454699990l
argument_list|,
literal|"1995-12-31T23:59:59.990Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00.0Z/DAY"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00.00Z/DAY"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00.000Z/DAY"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFormatter
specifier|public
name|void
name|testFormatter
parameter_list|()
block|{
comment|// just after epoch
name|assertFormat
argument_list|(
literal|"1970-01-01T00:00:00.005"
argument_list|,
literal|5L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"1970-01-01T00:00:00"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"1970-01-01T00:00:00.37"
argument_list|,
literal|370L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"1970-01-01T00:00:00.9"
argument_list|,
literal|900L
argument_list|)
expr_stmt|;
comment|// well after epoch
name|assertFormat
argument_list|(
literal|"1999-12-31T23:59:59.005"
argument_list|,
literal|946684799005L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"1999-12-31T23:59:59"
argument_list|,
literal|946684799000L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"1999-12-31T23:59:59.37"
argument_list|,
literal|946684799370L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"1999-12-31T23:59:59.9"
argument_list|,
literal|946684799900L
argument_list|)
expr_stmt|;
comment|// waaaay after epoch
name|assertFormat
argument_list|(
literal|"12345-12-31T23:59:59.005"
argument_list|,
literal|327434918399005L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"12345-12-31T23:59:59"
argument_list|,
literal|327434918399000L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"12345-12-31T23:59:59.37"
argument_list|,
literal|327434918399370L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"12345-12-31T23:59:59.9"
argument_list|,
literal|327434918399900L
argument_list|)
expr_stmt|;
comment|// well before epoch
name|assertFormat
argument_list|(
literal|"0299-12-31T23:59:59"
argument_list|,
operator|-
literal|52700112001000L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"0299-12-31T23:59:59.123"
argument_list|,
operator|-
literal|52700112000877L
argument_list|)
expr_stmt|;
name|assertFormat
argument_list|(
literal|"0299-12-31T23:59:59.09"
argument_list|,
operator|-
literal|52700112000910L
argument_list|)
expr_stmt|;
block|}
comment|/**     * Using dates in the canonical format, verify that parsing+formating     * is an identify function    */
DECL|method|testRoundTrip
specifier|public
name|void
name|testRoundTrip
parameter_list|()
throws|throws
name|Exception
block|{
comment|// typical dates, various precision
name|assertRoundTrip
argument_list|(
literal|"1995-12-31T23:59:59.987Z"
argument_list|)
expr_stmt|;
name|assertRoundTrip
argument_list|(
literal|"1995-12-31T23:59:59.98Z"
argument_list|)
expr_stmt|;
name|assertRoundTrip
argument_list|(
literal|"1995-12-31T23:59:59.9Z"
argument_list|)
expr_stmt|;
name|assertRoundTrip
argument_list|(
literal|"1995-12-31T23:59:59Z"
argument_list|)
expr_stmt|;
name|assertRoundTrip
argument_list|(
literal|"1976-03-06T03:06:00Z"
argument_list|)
expr_stmt|;
comment|// dates with atypical years
name|assertRoundTrip
argument_list|(
literal|"0001-01-01T01:01:01Z"
argument_list|)
expr_stmt|;
name|assertRoundTrip
argument_list|(
literal|"12021-12-01T03:03:03Z"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"SOLR-2773: Non-Positive years don't work"
argument_list|)
DECL|method|testRoundTripNonPositiveYear
specifier|public
name|void
name|testRoundTripNonPositiveYear
parameter_list|()
throws|throws
name|Exception
block|{
comment|// :TODO: ambiguity about year zero
comment|// assertRoundTrip("0000-04-04T04:04:04Z");
comment|// dates with negative years
name|assertRoundTrip
argument_list|(
literal|"-0005-05-05T05:05:05Z"
argument_list|)
expr_stmt|;
name|assertRoundTrip
argument_list|(
literal|"-2021-12-01T04:04:04Z"
argument_list|)
expr_stmt|;
name|assertRoundTrip
argument_list|(
literal|"-12021-12-01T02:02:02Z"
argument_list|)
expr_stmt|;
comment|// :TODO: assertFormat and assertToObject some negative years
block|}
DECL|method|assertFormat
specifier|protected
name|void
name|assertFormat
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|,
specifier|final
name|long
name|millis
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|DateFormatUtil
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
name|millis
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRoundTrip
specifier|protected
name|void
name|assertRoundTrip
parameter_list|(
name|String
name|canonicalDate
parameter_list|)
throws|throws
name|Exception
block|{
name|Date
name|d
init|=
name|DateFormatUtil
operator|.
name|parseDate
argument_list|(
name|canonicalDate
argument_list|)
decl_stmt|;
name|String
name|result
init|=
name|DateFormatUtil
operator|.
name|formatExternal
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"d:"
operator|+
name|d
operator|.
name|getTime
argument_list|()
argument_list|,
name|canonicalDate
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateField
specifier|public
name|void
name|testCreateField
parameter_list|()
block|{
name|int
name|props
init|=
name|FieldProperties
operator|.
name|INDEXED
operator|^
name|FieldProperties
operator|.
name|STORED
decl_stmt|;
name|SchemaField
name|sf
init|=
operator|new
name|SchemaField
argument_list|(
literal|"test"
argument_list|,
name|f
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|StorableField
name|out
init|=
name|f
operator|.
name|createField
argument_list|(
name|sf
argument_list|,
literal|"1995-12-31T23:59:59Z"
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|820454399000l
argument_list|,
name|f
operator|.
name|toObject
argument_list|(
name|out
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|=
name|f
operator|.
name|createField
argument_list|(
name|sf
argument_list|,
operator|new
name|Date
argument_list|(
literal|820454399000l
argument_list|)
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|820454399000l
argument_list|,
name|f
operator|.
name|toObject
argument_list|(
name|out
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
