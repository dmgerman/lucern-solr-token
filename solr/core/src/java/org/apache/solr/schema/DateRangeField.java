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
name|com
operator|.
name|spatial4j
operator|.
name|core
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
name|spatial
operator|.
name|NumberRangePrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|DateRangePrefixTree
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
name|query
operator|.
name|SpatialArgs
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
name|query
operator|.
name|SpatialOperation
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
name|search
operator|.
name|QParser
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
name|Calendar
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
comment|/**  * @see NumberRangePrefixTreeStrategy  * @see DateRangePrefixTree  */
end_comment
begin_class
DECL|class|DateRangeField
specifier|public
class|class
name|DateRangeField
extends|extends
name|AbstractSpatialPrefixTreeFieldType
argument_list|<
name|NumberRangePrefixTreeStrategy
argument_list|>
block|{
DECL|field|OP_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OP_PARAM
init|=
literal|"op"
decl_stmt|;
comment|//local-param to resolve SpatialOperation
DECL|field|tree
specifier|private
specifier|final
name|DateRangePrefixTree
name|tree
init|=
name|DateRangePrefixTree
operator|.
name|INSTANCE
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
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|addDegrees
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addDegrees
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|addDegrees
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|args
operator|.
name|put
argument_list|(
literal|"units"
argument_list|,
literal|"degrees"
argument_list|)
expr_stmt|;
comment|//HACK!
return|return
name|args
return|;
block|}
annotation|@
name|Override
DECL|method|newPrefixTreeStrategy
specifier|protected
name|NumberRangePrefixTreeStrategy
name|newPrefixTreeStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|NumberRangePrefixTreeStrategy
argument_list|(
name|tree
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|List
argument_list|<
name|StorableField
argument_list|>
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|val
operator|instanceof
name|Date
operator|||
name|val
operator|instanceof
name|Calendar
condition|)
comment|//From URP
name|val
operator|=
name|tree
operator|.
name|toShape
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|createFields
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseShape
specifier|protected
name|Shape
name|parseShape
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
return|return
name|tree
operator|.
name|parseShape
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
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
literal|"Couldn't parse date because: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|shapeToString
specifier|protected
name|String
name|shapeToString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
return|return
name|shape
operator|.
name|toString
argument_list|()
return|;
comment|//generally round-trips for DateRangePrefixTree
block|}
annotation|@
name|Override
DECL|method|parseSpatialArgs
specifier|protected
name|SpatialArgs
name|parseSpatialArgs
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
comment|//We avoid SpatialArgsParser entirely because it isn't very Solr-friendly
specifier|final
name|Shape
name|shape
init|=
name|parseShape
argument_list|(
name|externalVal
argument_list|)
decl_stmt|;
specifier|final
name|SolrParams
name|localParams
init|=
name|parser
operator|.
name|getLocalParams
argument_list|()
decl_stmt|;
name|SpatialOperation
name|op
init|=
name|SpatialOperation
operator|.
name|Intersects
decl_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
condition|)
block|{
name|String
name|opStr
init|=
name|localParams
operator|.
name|get
argument_list|(
name|OP_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|opStr
operator|!=
literal|null
condition|)
name|op
operator|=
name|SpatialOperation
operator|.
name|get
argument_list|(
name|opStr
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SpatialArgs
argument_list|(
name|op
argument_list|,
name|shape
argument_list|)
return|;
block|}
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
if|if
condition|(
operator|!
name|minInclusive
operator|||
operator|!
name|maxInclusive
condition|)
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
literal|"exclusive range boundary not supported"
argument_list|)
throw|;
if|if
condition|(
name|part1
operator|==
literal|null
condition|)
name|part1
operator|=
literal|"*"
expr_stmt|;
if|if
condition|(
name|part2
operator|==
literal|null
condition|)
name|part2
operator|=
literal|"*"
expr_stmt|;
name|Shape
name|shape
init|=
name|tree
operator|.
name|toRangeShape
argument_list|(
name|parseShape
argument_list|(
name|part1
argument_list|)
argument_list|,
name|parseShape
argument_list|(
name|part2
argument_list|)
argument_list|)
decl_stmt|;
name|SpatialArgs
name|spatialArgs
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|shape
argument_list|)
decl_stmt|;
return|return
name|getQueryFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|spatialArgs
argument_list|)
return|;
block|}
block|}
end_class
end_unit
