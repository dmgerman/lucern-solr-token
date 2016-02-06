begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
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
name|SolrParams
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_comment
comment|/**  * Creates a spatial Filter based on the type of spatial point used.  *<p>  * The field must implement {@link org.apache.solr.schema.SpatialQueryable}  *<p>  * All units are in Kilometers  *<p>  * Syntax:  *<pre>{!geofilt sfield=&lt;location_field&gt; pt=&lt;lat,lon&gt; d=&lt;distance&gt;}</pre>  *<p>  * Parameters:  *<ul>  *<li>sfield - The field to filter on. Required.</li>  *<li>pt - The point to use as a reference.  Must match the dimension of the field. Required.</li>  *<li>d - The distance in km.  Required.</li>  *</ul>  * The distance measure used currently depends on the FieldType.  LatLonType defaults to using haversine, PointType defaults to Euclidean (2-norm).  *<p>  * Examples:  *<pre>fq={!geofilt sfield=store pt=10.312,-20.556 d=3.5}</pre>  *<pre>fq={!geofilt sfield=store}&amp;pt=10.312,-20&amp;d=3.5</pre>  *<pre>fq={!geofilt}&amp;sfield=store&amp;pt=10.312,-20&amp;d=3.5</pre>  *<p>  * Note: The geofilt for LatLonType is capable of also producing scores equal to the computed distance from the point  * to the field, making it useful as a component of the main query or a boosting query.  */
end_comment
begin_class
DECL|class|SpatialFilterQParserPlugin
specifier|public
class|class
name|SpatialFilterQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"geofilt"
decl_stmt|;
annotation|@
name|Override
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|SpatialFilterQParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class
end_unit
