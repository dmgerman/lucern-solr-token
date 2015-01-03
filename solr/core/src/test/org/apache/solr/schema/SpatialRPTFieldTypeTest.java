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
name|Files
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
name|core
operator|.
name|AbstractBadConfigTestBase
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
begin_class
DECL|class|SpatialRPTFieldTypeTest
specifier|public
class|class
name|SpatialRPTFieldTypeTest
extends|extends
name|AbstractBadConfigTestBase
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
DECL|method|initManagedSchemaCore
specifier|private
name|void
name|initManagedSchemaCore
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
literal|"solrconfig-managed-schema.xml"
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
literal|"solrconfig-basic.xml"
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
literal|"solrconfig.snippet.randomindexconfig.xml"
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
literal|"schema-one-field-no-dynamic-field.xml"
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
literal|"schema-one-field-no-dynamic-field-unique-key.xml"
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
literal|"schema-minimal.xml"
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
literal|"schema_codec.xml"
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
literal|"schema-bm25.xml"
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
comment|// initCore will trigger an upgrade to managed schema, since the solrconfig has
comment|//<schemaFactory class="ManagedIndexSchemaFactory" ... />
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"false"
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
name|initCore
argument_list|(
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-minimal.xml"
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
DECL|method|afterClass
specifier|private
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"enable.update.log"
argument_list|)
expr_stmt|;
block|}
DECL|field|INDEXED_COORDINATES
specifier|final
name|String
name|INDEXED_COORDINATES
init|=
literal|"25,82"
decl_stmt|;
DECL|field|QUERY_COORDINATES
specifier|final
name|String
name|QUERY_COORDINATES
init|=
literal|"24,81"
decl_stmt|;
DECL|field|DISTANCE_DEGREES
specifier|final
name|String
name|DISTANCE_DEGREES
init|=
literal|"1.3520328"
decl_stmt|;
DECL|field|DISTANCE_KILOMETERS
specifier|final
name|String
name|DISTANCE_KILOMETERS
init|=
literal|"150.33939"
decl_stmt|;
DECL|field|DISTANCE_MILES
specifier|final
name|String
name|DISTANCE_MILES
init|=
literal|"93.416565"
decl_stmt|;
DECL|method|testUnitsDegrees
specifier|public
name|void
name|testUnitsDegrees
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test back compat behaviour
name|setupRPTField
argument_list|(
literal|"degrees"
argument_list|,
literal|null
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"str"
argument_list|,
literal|"X"
argument_list|,
literal|"geo"
argument_list|,
name|INDEXED_COORDINATES
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|q
decl_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=distance filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_DEGREES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=degrees filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_DEGREES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=kilometers filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_KILOMETERS
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=miles filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_MILES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnitsNonDegrees
specifier|public
name|void
name|testUnitsNonDegrees
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|setupRPTField
argument_list|(
literal|"kilometers"
argument_list|,
literal|null
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception for deprecated units parameter."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"units parameter is deprecated"
argument_list|)
condition|)
throw|throw
name|ex
throw|;
block|}
block|}
DECL|method|testDistanceUnitsDegrees
specifier|public
name|void
name|testDistanceUnitsDegrees
parameter_list|()
throws|throws
name|Exception
block|{
name|setupRPTField
argument_list|(
literal|null
argument_list|,
literal|"degrees"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"str"
argument_list|,
literal|"X"
argument_list|,
literal|"geo"
argument_list|,
name|INDEXED_COORDINATES
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|q
decl_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=distance filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_DEGREES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=degrees filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_DEGREES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=kilometers filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_KILOMETERS
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=miles filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_MILES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDistanceUnitsKilometers
specifier|public
name|void
name|testDistanceUnitsKilometers
parameter_list|()
throws|throws
name|Exception
block|{
name|setupRPTField
argument_list|(
literal|null
argument_list|,
literal|"kilometers"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"str"
argument_list|,
literal|"X"
argument_list|,
literal|"geo"
argument_list|,
name|INDEXED_COORDINATES
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|q
decl_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=distance filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_KILOMETERS
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=degrees filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_DEGREES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=kilometers filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_KILOMETERS
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|q
operator|=
literal|"geo:{!geofilt score=miles filter=false sfield=geo pt="
operator|+
name|QUERY_COORDINATES
operator|+
literal|" d=1000}"
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result/doc/float[@name='score'][.='"
operator|+
name|DISTANCE_MILES
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBothUnitsAndDistanceUnits
specifier|public
name|void
name|testBothUnitsAndDistanceUnits
parameter_list|()
throws|throws
name|Exception
block|{
comment|// distanceUnits should take precedence
try|try
block|{
name|setupRPTField
argument_list|(
literal|"degrees"
argument_list|,
literal|"kilometers"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception for deprecated units parameter."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"units parameter is deprecated"
argument_list|)
condition|)
throw|throw
name|ex
throw|;
block|}
block|}
DECL|method|testJunkValuesForDistanceUnits
specifier|public
name|void
name|testJunkValuesForDistanceUnits
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|setupRPTField
argument_list|(
literal|null
argument_list|,
literal|"rose"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception for bad value of distanceUnits."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Must specify distanceUnits as one of"
argument_list|)
condition|)
throw|throw
name|ex
throw|;
block|}
block|}
DECL|method|testMaxDistErrConversion
specifier|public
name|void
name|testMaxDistErrConversion
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|File
name|managedSchemaFile
init|=
operator|new
name|File
argument_list|(
name|tmpConfDir
argument_list|,
literal|"managed-schema"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|managedSchemaFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Delete managed-schema so it won't block parsing a new schema
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-one-field-no-dynamic-field.xml"
argument_list|,
name|tmpSolrHome
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
literal|"new_text_field"
decl_stmt|;
name|assertNull
argument_list|(
literal|"Field '"
operator|+
name|fieldName
operator|+
literal|"' is present in the schema"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSchema
name|oldSchema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SpatialRecursivePrefixTreeFieldType
name|rptFieldType
init|=
operator|new
name|SpatialRecursivePrefixTreeFieldType
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|rptMap
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
name|rptFieldType
operator|.
name|setTypeName
argument_list|(
literal|"location_rpt"
argument_list|)
expr_stmt|;
name|rptMap
operator|.
name|put
argument_list|(
literal|"geo"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// test km
name|rptMap
operator|.
name|put
argument_list|(
literal|"distanceUnits"
argument_list|,
literal|"kilometers"
argument_list|)
expr_stmt|;
name|rptMap
operator|.
name|put
argument_list|(
literal|"maxDistErr"
argument_list|,
literal|"0.001"
argument_list|)
expr_stmt|;
comment|// 1 meter
name|rptFieldType
operator|.
name|init
argument_list|(
name|oldSchema
argument_list|,
name|rptMap
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|rptFieldType
operator|.
name|grid
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
expr_stmt|;
comment|// test miles
name|rptMap
operator|.
name|put
argument_list|(
literal|"distanceUnits"
argument_list|,
literal|"miles"
argument_list|)
expr_stmt|;
name|rptMap
operator|.
name|put
argument_list|(
literal|"maxDistErr"
argument_list|,
literal|"0.001"
argument_list|)
expr_stmt|;
name|rptFieldType
operator|.
name|init
argument_list|(
name|oldSchema
argument_list|,
name|rptMap
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|rptFieldType
operator|.
name|grid
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
expr_stmt|;
comment|// test degrees
name|rptMap
operator|.
name|put
argument_list|(
literal|"distanceUnits"
argument_list|,
literal|"degrees"
argument_list|)
expr_stmt|;
name|rptMap
operator|.
name|put
argument_list|(
literal|"maxDistErr"
argument_list|,
literal|"0.001"
argument_list|)
expr_stmt|;
name|rptFieldType
operator|.
name|init
argument_list|(
name|oldSchema
argument_list|,
name|rptMap
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|rptFieldType
operator|.
name|grid
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeoDistanceFunctionWithBackCompat
specifier|public
name|void
name|testGeoDistanceFunctionWithBackCompat
parameter_list|()
throws|throws
name|Exception
block|{
name|setupRPTField
argument_list|(
literal|"degrees"
argument_list|,
literal|null
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"str"
argument_list|,
literal|"X"
argument_list|,
literal|"geo"
argument_list|,
literal|"1,2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// geodist() should return in km
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(3,4)"
argument_list|,
literal|"sfield"
argument_list|,
literal|"geo"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
argument_list|,
literal|1e-5
argument_list|,
literal|"/response/docs/[0]/score==314.4033"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeoDistanceFunctionWithKilometers
specifier|public
name|void
name|testGeoDistanceFunctionWithKilometers
parameter_list|()
throws|throws
name|Exception
block|{
name|setupRPTField
argument_list|(
literal|null
argument_list|,
literal|"kilometers"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"str"
argument_list|,
literal|"X"
argument_list|,
literal|"geo"
argument_list|,
literal|"1,2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(3,4)"
argument_list|,
literal|"sfield"
argument_list|,
literal|"geo"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
argument_list|,
literal|1e-5
argument_list|,
literal|"/response/docs/[0]/score==314.4033"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeoDistanceFunctionWithMiles
specifier|public
name|void
name|testGeoDistanceFunctionWithMiles
parameter_list|()
throws|throws
name|Exception
block|{
name|setupRPTField
argument_list|(
literal|null
argument_list|,
literal|"miles"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"str"
argument_list|,
literal|"X"
argument_list|,
literal|"geo"
argument_list|,
literal|"1,2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(3,4)"
argument_list|,
literal|"sfield"
argument_list|,
literal|"geo"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
argument_list|,
literal|1e-5
argument_list|,
literal|"/response/docs/[0]/score==195.36115"
argument_list|)
expr_stmt|;
block|}
DECL|method|setupRPTField
specifier|private
name|void
name|setupRPTField
parameter_list|(
name|String
name|units
parameter_list|,
name|String
name|distanceUnits
parameter_list|,
name|String
name|geo
parameter_list|)
throws|throws
name|Exception
block|{
name|deleteCore
argument_list|()
expr_stmt|;
name|File
name|managedSchemaFile
init|=
operator|new
name|File
argument_list|(
name|tmpConfDir
argument_list|,
literal|"managed-schema"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|managedSchemaFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Delete managed-schema so it won't block parsing a new schema
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-one-field-no-dynamic-field.xml"
argument_list|,
name|tmpSolrHome
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
literal|"new_text_field"
decl_stmt|;
name|assertNull
argument_list|(
literal|"Field '"
operator|+
name|fieldName
operator|+
literal|"' is present in the schema"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSchema
name|oldSchema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SpatialRecursivePrefixTreeFieldType
name|rptFieldType
init|=
operator|new
name|SpatialRecursivePrefixTreeFieldType
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|rptMap
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
if|if
condition|(
name|units
operator|!=
literal|null
condition|)
name|rptMap
operator|.
name|put
argument_list|(
literal|"units"
argument_list|,
name|units
argument_list|)
expr_stmt|;
if|if
condition|(
name|distanceUnits
operator|!=
literal|null
condition|)
name|rptMap
operator|.
name|put
argument_list|(
literal|"distanceUnits"
argument_list|,
name|distanceUnits
argument_list|)
expr_stmt|;
if|if
condition|(
name|geo
operator|!=
literal|null
condition|)
name|rptMap
operator|.
name|put
argument_list|(
literal|"geo"
argument_list|,
name|geo
argument_list|)
expr_stmt|;
name|rptFieldType
operator|.
name|init
argument_list|(
name|oldSchema
argument_list|,
name|rptMap
argument_list|)
expr_stmt|;
name|rptFieldType
operator|.
name|setTypeName
argument_list|(
literal|"location_rpt"
argument_list|)
expr_stmt|;
name|SchemaField
name|newField
init|=
operator|new
name|SchemaField
argument_list|(
literal|"geo"
argument_list|,
name|rptFieldType
argument_list|,
name|SchemaField
operator|.
name|STORED
operator||
name|SchemaField
operator|.
name|INDEXED
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexSchema
name|newSchema
init|=
name|oldSchema
operator|.
name|addField
argument_list|(
name|newField
argument_list|)
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
