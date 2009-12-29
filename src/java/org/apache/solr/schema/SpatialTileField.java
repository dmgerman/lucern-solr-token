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
name|document
operator|.
name|Fieldable
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|SortField
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
name|spatial
operator|.
name|tier
operator|.
name|projections
operator|.
name|CartesianTierPlotter
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
name|spatial
operator|.
name|tier
operator|.
name|projections
operator|.
name|IProjector
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
name|spatial
operator|.
name|tier
operator|.
name|projections
operator|.
name|SinusoidalProjector
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
name|ResourceLoader
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
name|common
operator|.
name|params
operator|.
name|MapSolrParams
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
name|request
operator|.
name|TextResponseWriter
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
name|XMLWriter
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
name|search
operator|.
name|QParser
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
name|search
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|function
operator|.
name|distance
operator|.
name|DistanceUtils
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
name|plugin
operator|.
name|ResourceLoaderAware
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
begin_comment
comment|/**  * Represents a Tiling system for spatial data representation (lat/lon).  A Tile is like a zoom level on an  * interactive map.  *<p/>  * Specify a lower and upper tile, and this will create tiles for all the levels in between, inclusive of the upper tile.  *<p/>  * Querying directly against this field is probably not all that useful unless you specifically know the box id  *<p/>  *<p/>  * See http://wiki.apache.org/solr/SpatialSearch  */
end_comment
begin_class
DECL|class|SpatialTileField
specifier|public
class|class
name|SpatialTileField
extends|extends
name|AbstractSubTypeFieldType
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|START_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|START_LEVEL
init|=
literal|"start"
decl_stmt|;
DECL|field|END_LEVEL
specifier|public
specifier|static
specifier|final
name|String
name|END_LEVEL
init|=
literal|"end"
decl_stmt|;
DECL|field|PROJECTOR_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|PROJECTOR_CLASS
init|=
literal|"projector"
decl_stmt|;
DECL|field|DEFAULT_END_LEVEL
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_END_LEVEL
init|=
literal|15
decl_stmt|;
DECL|field|DEFAULT_START_LEVEL
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_START_LEVEL
init|=
literal|4
decl_stmt|;
DECL|field|start
DECL|field|end
specifier|private
name|int
name|start
init|=
name|DEFAULT_START_LEVEL
decl_stmt|,
name|end
init|=
name|DEFAULT_END_LEVEL
decl_stmt|;
DECL|field|tileDiff
specifier|private
name|int
name|tileDiff
decl_stmt|;
comment|//we're going to need this over and over, so cache it.
DECL|field|projectorName
specifier|private
name|String
name|projectorName
decl_stmt|;
DECL|field|plotters
specifier|protected
name|List
argument_list|<
name|CartesianTierPlotter
argument_list|>
name|plotters
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|SolrParams
name|p
init|=
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|start
operator|=
name|p
operator|.
name|getInt
argument_list|(
name|START_LEVEL
argument_list|,
name|DEFAULT_START_LEVEL
argument_list|)
expr_stmt|;
name|end
operator|=
name|p
operator|.
name|getInt
argument_list|(
name|END_LEVEL
argument_list|,
name|DEFAULT_END_LEVEL
argument_list|)
expr_stmt|;
if|if
condition|(
name|end
operator|<
name|start
condition|)
block|{
comment|//flip them around
name|int
name|tmp
init|=
name|start
decl_stmt|;
name|start
operator|=
name|end
expr_stmt|;
name|end
operator|=
name|tmp
expr_stmt|;
block|}
name|args
operator|.
name|remove
argument_list|(
name|START_LEVEL
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
name|END_LEVEL
argument_list|)
expr_stmt|;
name|projectorName
operator|=
name|p
operator|.
name|get
argument_list|(
name|PROJECTOR_CLASS
argument_list|,
name|SinusoidalProjector
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
name|PROJECTOR_CLASS
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|tileDiff
operator|=
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|1
expr_stmt|;
comment|//add one since we are inclusive of the upper tier
name|createSuffixCache
argument_list|(
name|tileDiff
argument_list|)
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|IProjector
name|projector
init|=
operator|(
name|IProjector
operator|)
name|loader
operator|.
name|newInstance
argument_list|(
name|projectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|projector
operator|!=
literal|null
condition|)
block|{
name|plotters
operator|=
operator|new
name|ArrayList
argument_list|<
name|CartesianTierPlotter
argument_list|>
argument_list|(
name|tileDiff
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<=
name|end
condition|;
name|i
operator|++
control|)
block|{
name|plotters
operator|.
name|add
argument_list|(
operator|new
name|CartesianTierPlotter
argument_list|(
name|i
argument_list|,
name|projector
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not instantiate a Projector Instance for: "
operator|+
name|projectorName
operator|+
literal|". Make sure the "
operator|+
name|PROJECTOR_CLASS
operator|+
literal|" attribute is set properly in the schema"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|Fieldable
index|[]
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|Fieldable
index|[]
name|f
init|=
operator|new
name|Fieldable
index|[
operator|(
name|field
operator|.
name|indexed
argument_list|()
condition|?
name|tileDiff
else|:
literal|0
operator|)
operator|+
operator|(
name|field
operator|.
name|stored
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
index|]
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|double
index|[]
name|latLon
init|=
name|DistanceUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
literal|null
argument_list|,
name|externalVal
argument_list|)
decl_stmt|;
for|for
control|(
name|CartesianTierPlotter
name|plotter
range|:
name|plotters
control|)
block|{
name|double
name|boxId
init|=
name|plotter
operator|.
name|getTierBoxId
argument_list|(
name|latLon
index|[
literal|0
index|]
argument_list|,
name|latLon
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|f
index|[
name|i
index|]
operator|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|)
operator|.
name|createField
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|boxId
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
name|String
name|storedVal
init|=
name|externalVal
decl_stmt|;
comment|// normalize or not?
name|f
index|[
name|f
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|createField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|storedVal
argument_list|,
name|getFieldStore
argument_list|(
name|field
argument_list|,
name|storedVal
argument_list|)
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
comment|//The externalVal here is a box id, as it doesn't make sense to pick a specific tile since that requires a distance
comment|//so, just OR together a search against all the tile
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
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
name|tileDiff
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|sf
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|Query
name|tq
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|sf
argument_list|,
name|part1
argument_list|,
name|part2
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|bq
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
comment|//The externalVal here is a box id, as it doesn't make sense to pick a specific tile since that requires a distance
comment|//so, just OR together a search against all the tiles
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
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
name|tileDiff
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|sf
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|Query
name|tq
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getFieldQuery
argument_list|(
name|parser
argument_list|,
name|sf
argument_list|,
name|externalVal
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|bq
return|;
block|}
annotation|@
name|Override
DECL|method|isPolyField
specifier|public
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|xmlWriter
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Sorting not supported on SpatialTileField "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
comment|//TODO: Should this really throw UOE?  What does it mean for a function to use the values of a tier?  Let's leave it unsupported for now
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"SpatialTileField uses multiple fields and does not support ValueSource"
argument_list|)
throw|;
block|}
comment|//It never makes sense to create a single field, so make it impossible to happen
annotation|@
name|Override
DECL|method|createField
specifier|public
name|Field
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"SpatialTileField uses multiple fields.  field="
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
