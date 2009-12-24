begin_unit
begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geohash
operator|.
name|GeoHashUtils
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|DistanceFunctionTest
specifier|public
class|class
name|DistanceFunctionTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema11.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig-functionquery.xml"
return|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
DECL|method|testHaversine
specifier|public
name|void
name|testHaversine
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"0"
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|79.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|78.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"x_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
literal|"y_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|80.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"x_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
literal|"y_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|81.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"x_td"
argument_list|,
literal|"45.0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"45.0"
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|81.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//Get the haversine distance between the point 0,0 and the docs above assuming a radius of 1
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0471976'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, x_td, y_td, 0, 0, true)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0471976'"
argument_list|)
expr_stmt|;
comment|//Geo Hash Haversine
comment|//Can verify here: http://www.movable-type.co.uk/scripts/latlong.html, but they use a slightly different radius for the earth, so just be close
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}ghhsin("
operator|+
name|Constants
operator|.
name|EARTH_RADIUS_KM
operator|+
literal|", gh_s, \""
operator|+
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32
argument_list|,
operator|-
literal|79
argument_list|)
operator|+
literal|"\",)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='122.30894'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}ghhsin("
operator|+
name|Constants
operator|.
name|EARTH_RADIUS_KM
operator|+
literal|", gh_s, geohash(32, -79))"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='122.30894'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testVector
specifier|public
name|void
name|testVector
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"0"
argument_list|,
literal|"z_td"
argument_list|,
literal|"0"
argument_list|,
literal|"w_td"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"1"
argument_list|,
literal|"z_td"
argument_list|,
literal|"0"
argument_list|,
literal|"w_td"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"x_td"
argument_list|,
literal|"1"
argument_list|,
literal|"y_td"
argument_list|,
literal|"1"
argument_list|,
literal|"z_td"
argument_list|,
literal|"1"
argument_list|,
literal|"w_td"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"x_td"
argument_list|,
literal|"1"
argument_list|,
literal|"y_td"
argument_list|,
literal|"0"
argument_list|,
literal|"z_td"
argument_list|,
literal|"0"
argument_list|,
literal|"w_td"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"x_td"
argument_list|,
literal|"2.3"
argument_list|,
literal|"y_td"
argument_list|,
literal|"5.5"
argument_list|,
literal|"z_td"
argument_list|,
literal|"7.9"
argument_list|,
literal|"w_td"
argument_list|,
literal|"-2.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"point"
argument_list|,
literal|"1.0,0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"point"
argument_list|,
literal|"5.5,10.9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//two dimensions, notice how we only pass in 4 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|2.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//three dimensions, notice how we pass in 6 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|3.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
operator|+
literal|7.9
operator|*
literal|7.9
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//four dimensions, notice how we pass in 8 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|4.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
operator|+
literal|7.9
operator|*
literal|7.9
operator|+
literal|2.4
operator|*
literal|2.4
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//Pass in imbalanced list, throw exception
try|try
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should throw an exception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|SolrException
argument_list|)
expr_stmt|;
block|}
comment|//do one test of Euclidean
comment|//two dimensions, notice how we only pass in 4 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
literal|2.0
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
operator|)
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//do one test of Manhattan
comment|//two dimensions, notice how we only pass in 4 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
operator|(
name|float
operator|)
literal|2.0
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|+
literal|5.5
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//Do point tests:
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, toMultiVS(x_td, y_td), toMultiVS(0, 0))"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|+
literal|5.5
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, point, toMultiVS(0, 0))"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|0.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
