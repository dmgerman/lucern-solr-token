begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|SolrException
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
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|io
operator|.
name|SupportedFormats
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|ObjectMapper
import|;
end_import
begin_class
DECL|class|TestGeoJSONResponseWriter
specifier|public
class|class
name|TestGeoJSONResponseWriter
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|jsonmapper
specifier|final
name|ObjectMapper
name|jsonmapper
init|=
operator|new
name|ObjectMapper
argument_list|()
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
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-spatial.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|()
block|{
comment|//<field name="srpt_geohash" type="srpt_geohash" multiValued="true" />
comment|//<field name="" type="srpt_quad" multiValued="true" />
comment|//<field name="" type="srpt_packedquad" multiValued="true" />
comment|//<field name="" type="stqpt_geohash" multiValued="true" />
comment|// multiple valued field
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"H.A"
argument_list|,
literal|"srpt_geohash"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"H.B"
argument_list|,
literal|"srpt_geohash"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|,
literal|"srpt_geohash"
argument_list|,
literal|"POINT( 3 4 )"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"H.C"
argument_list|,
literal|"srpt_geohash"
argument_list|,
literal|"LINESTRING (30 10, 10 30, 40 40)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"Q.A"
argument_list|,
literal|"srpt_quad"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"Q.B"
argument_list|,
literal|"srpt_quad"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|,
literal|"srpt_quad"
argument_list|,
literal|"POINT( 3 4 )"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"Q.C"
argument_list|,
literal|"srpt_quad"
argument_list|,
literal|"LINESTRING (30 10, 10 30, 40 40)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"P.A"
argument_list|,
literal|"srpt_packedquad"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"P.B"
argument_list|,
literal|"srpt_packedquad"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|,
literal|"srpt_packedquad"
argument_list|,
literal|"POINT( 3 4 )"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"P.C"
argument_list|,
literal|"srpt_packedquad"
argument_list|,
literal|"LINESTRING (30 10, 10 30, 40 40)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// single valued field
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"R.A"
argument_list|,
literal|"srptgeom"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|)
argument_list|)
expr_stmt|;
comment|// non-spatial field
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"S.X"
argument_list|,
literal|"str_shape"
argument_list|,
literal|"POINT( 1 2 )"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"S.A"
argument_list|,
literal|"str_shape"
argument_list|,
literal|"{\"type\":\"Point\",\"coordinates\":[1,2]}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readJSON
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readJSON
parameter_list|(
name|String
name|json
parameter_list|)
block|{
try|try
block|{
return|return
name|jsonmapper
operator|.
name|readValue
argument_list|(
name|json
argument_list|,
name|Map
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to read GeoJSON From: {}"
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Error"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Unable to parse JSON GeoJSON Response"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getFirstFeatureGeometry
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFirstFeatureGeometry
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rsp
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|json
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FeatureCollection"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|vals
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|rsp
operator|.
name|get
argument_list|(
literal|"features"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|vals
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|feature
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|vals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Feature"
argument_list|,
name|feature
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|feature
operator|.
name|get
argument_list|(
literal|"geometry"
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testRequestExceptions
specifier|public
name|void
name|testRequestExceptions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make sure we select the field
try|try
block|{
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
literal|"geojson"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should Require a parameter to select the field"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{}
comment|// non-spatial fields *must* be stored as JSON
try|try
block|{
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:S.X"
argument_list|,
literal|"wt"
argument_list|,
literal|"geojson"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|,
literal|"geojson.field"
argument_list|,
literal|"str_shape"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain about bad shape config"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{}
block|}
annotation|@
name|Test
DECL|method|testGeoJSONAtRoot
specifier|public
name|void
name|testGeoJSONAtRoot
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Try reading the whole resposne
name|String
name|json
init|=
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
literal|"geojson"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|,
literal|"geojson.field"
argument_list|,
literal|"stqpt_geohash"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Check that we have a normal solr response with 'responseHeader' and 'response'
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rsp
init|=
name|readJSON
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rsp
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|rsp
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
argument_list|)
expr_stmt|;
name|json
operator|=
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
literal|"geojson"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|,
literal|"omitHeader"
argument_list|,
literal|"true"
argument_list|,
literal|"geojson.field"
argument_list|,
literal|"stqpt_geohash"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check that we have a normal solr response with 'responseHeader' and 'response'
name|rsp
operator|=
name|readJSON
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rsp
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rsp
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FeatureCollection"
argument_list|,
name|rsp
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|rsp
operator|.
name|get
argument_list|(
literal|"features"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeoJSONOutput
specifier|public
name|void
name|testGeoJSONOutput
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Try reading the whole resposne
name|readJSON
argument_list|(
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
literal|"geojson"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|,
literal|"geojson.field"
argument_list|,
literal|"stqpt_geohash"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Multivalued Valued Point
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
name|readJSON
argument_list|(
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:H.B"
argument_list|,
literal|"wt"
argument_list|,
literal|"geojson"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|,
literal|"geojson.field"
argument_list|,
literal|"srpt_geohash"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|geo
init|=
name|getFirstFeatureGeometry
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
comment|// NOTE: not actual JSON, it is Map.toString()!
literal|"{type=GeometryCollection, geometries=["
operator|+
literal|"{type=Point, coordinates=[1, 2]}, "
operator|+
literal|"{type=Point, coordinates=[3, 4]}]}"
argument_list|,
literal|""
operator|+
name|geo
argument_list|)
expr_stmt|;
comment|// Check the same value encoded on different field types
name|String
index|[]
index|[]
name|check
init|=
operator|new
name|String
index|[]
index|[]
block|{
block|{
literal|"id:H.A"
block|,
literal|"srpt_geohash"
block|}
block|,
block|{
literal|"id:Q.A"
block|,
literal|"srpt_quad"
block|}
block|,
block|{
literal|"id:P.A"
block|,
literal|"srpt_packedquad"
block|}
block|,
block|{
literal|"id:R.A"
block|,
literal|"srptgeom"
block|}
block|,
block|{
literal|"id:S.A"
block|,
literal|"str_shape"
block|}
block|,     }
decl_stmt|;
for|for
control|(
name|String
index|[]
name|args
range|:
name|check
control|)
block|{
name|json
operator|=
name|readJSON
argument_list|(
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|,
literal|"wt"
argument_list|,
literal|"geojson"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|,
literal|"geojson.field"
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|geo
operator|=
name|getFirstFeatureGeometry
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Error reading point from: "
operator|+
name|args
index|[
literal|1
index|]
operator|+
literal|" ("
operator|+
name|args
index|[
literal|0
index|]
operator|+
literal|")"
argument_list|,
comment|// NOTE: not actual JSON, it is Map.toString()!
literal|"{type=Point, coordinates=[1, 2]}"
argument_list|,
literal|""
operator|+
name|geo
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readFirstDoc
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readFirstDoc
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|List
name|docs
init|=
call|(
name|List
call|)
argument_list|(
operator|(
name|Map
operator|)
name|readJSON
argument_list|(
name|json
argument_list|)
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
decl_stmt|;
return|return
operator|(
name|Map
operator|)
name|docs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|normalizeMapToJSON
specifier|public
specifier|static
name|String
name|normalizeMapToJSON
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|val
operator|=
name|val
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// remove quotes
name|val
operator|=
name|val
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'='
argument_list|)
expr_stmt|;
name|val
operator|=
name|val
operator|.
name|replace
argument_list|(
literal|", "
argument_list|,
literal|","
argument_list|)
expr_stmt|;
return|return
name|val
return|;
block|}
annotation|@
name|Test
DECL|method|testTransformToAllFormats
specifier|public
name|void
name|testTransformToAllFormats
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|wkt
init|=
literal|"POINT( 1 2 )"
decl_stmt|;
name|SupportedFormats
name|fmts
init|=
name|SpatialContext
operator|.
name|GEO
operator|.
name|getFormats
argument_list|()
decl_stmt|;
name|Shape
name|shape
init|=
name|fmts
operator|.
name|read
argument_list|(
name|wkt
argument_list|)
decl_stmt|;
name|String
index|[]
name|check
init|=
operator|new
name|String
index|[]
block|{
literal|"srpt_geohash"
block|,
literal|"srpt_geohash"
block|,
literal|"srpt_quad"
block|,
literal|"srpt_packedquad"
block|,
literal|"srptgeom"
block|,
comment|//       "str_shape",  // NEEDS TO BE A SpatialField!
block|}
decl_stmt|;
name|String
index|[]
name|checkFormats
init|=
operator|new
name|String
index|[]
block|{
literal|"GeoJSON"
block|,
literal|"WKT"
block|,
literal|"POLY"
block|}
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|check
control|)
block|{
comment|// Add a document with the given field
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"test"
argument_list|,
name|field
argument_list|,
name|wkt
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|fmt
range|:
name|checkFormats
control|)
block|{
name|String
name|json
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:test"
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"xxx:[geo f="
operator|+
name|field
operator|+
literal|" w="
operator|+
name|fmt
operator|+
literal|"]"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
init|=
name|readFirstDoc
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|Object
name|v
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"xxx"
argument_list|)
decl_stmt|;
name|String
name|expect
init|=
name|fmts
operator|.
name|getWriter
argument_list|(
name|fmt
argument_list|)
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|v
operator|instanceof
name|String
operator|)
condition|)
block|{
name|v
operator|=
name|normalizeMapToJSON
argument_list|(
name|v
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|expect
operator|=
name|normalizeMapToJSON
argument_list|(
name|expect
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Bad result: "
operator|+
name|field
operator|+
literal|"/"
operator|+
name|fmt
argument_list|,
name|expect
argument_list|,
name|v
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
