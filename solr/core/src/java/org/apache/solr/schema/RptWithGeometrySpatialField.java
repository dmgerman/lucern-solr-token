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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|jts
operator|.
name|JtsGeometry
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
name|index
operator|.
name|LeafReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
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
name|lucene
operator|.
name|search
operator|.
name|Explanation
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
name|composite
operator|.
name|CompositeSpatialStrategy
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
name|RecursivePrefixTreeStrategy
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
name|SpatialArgsParser
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
name|serialized
operator|.
name|SerializedDVStrategy
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
name|core
operator|.
name|SolrCore
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
name|SolrRequestInfo
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
name|SolrCache
import|;
end_import
begin_comment
comment|/** A Solr Spatial FieldType based on {@link CompositeSpatialStrategy}.  * @lucene.experimental */
end_comment
begin_class
DECL|class|RptWithGeometrySpatialField
specifier|public
class|class
name|RptWithGeometrySpatialField
extends|extends
name|AbstractSpatialFieldType
argument_list|<
name|CompositeSpatialStrategy
argument_list|>
block|{
DECL|field|DEFAULT_DIST_ERR_PCT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DIST_ERR_PCT
init|=
literal|"0.15"
decl_stmt|;
DECL|field|rptFieldType
specifier|private
name|SpatialRecursivePrefixTreeFieldType
name|rptFieldType
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
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
comment|// Do NOT call super.init(); instead we delegate to an RPT field. Admittedly this is error prone.
comment|//TODO Move this check to a call from AbstractSpatialFieldType.createFields() so the type can declare
comment|// if it supports multi-valued or not. It's insufficient here; we can't see if you set multiValued on the field.
if|if
condition|(
name|isMultiValued
argument_list|()
condition|)
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
literal|"Not capable of multiValued: "
operator|+
name|getTypeName
argument_list|()
argument_list|)
throw|;
block|}
comment|// Choose a better default distErrPct if not configured
if|if
condition|(
name|args
operator|.
name|containsKey
argument_list|(
name|SpatialArgsParser
operator|.
name|DIST_ERR_PCT
argument_list|)
operator|==
literal|false
condition|)
block|{
name|args
operator|.
name|put
argument_list|(
name|SpatialArgsParser
operator|.
name|DIST_ERR_PCT
argument_list|,
name|DEFAULT_DIST_ERR_PCT
argument_list|)
expr_stmt|;
block|}
name|rptFieldType
operator|=
operator|new
name|SpatialRecursivePrefixTreeFieldType
argument_list|()
expr_stmt|;
name|rptFieldType
operator|.
name|setTypeName
argument_list|(
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|rptFieldType
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|rptFieldType
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|rptFieldType
operator|.
name|argsParser
operator|=
name|argsParser
operator|=
name|newSpatialArgsParser
argument_list|()
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|rptFieldType
operator|.
name|ctx
expr_stmt|;
name|this
operator|.
name|distanceUnits
operator|=
name|rptFieldType
operator|.
name|distanceUnits
expr_stmt|;
name|this
operator|.
name|units
operator|=
name|rptFieldType
operator|.
name|units
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSpatialStrategy
specifier|protected
name|CompositeSpatialStrategy
name|newSpatialStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// We use the same field name for both sub-strategies knowing there will be no conflict for these two
name|RecursivePrefixTreeStrategy
name|rptStrategy
init|=
name|rptFieldType
operator|.
name|newSpatialStrategy
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|SerializedDVStrategy
name|geomStrategy
init|=
operator|new
name|CachingSerializedDVStrategy
argument_list|(
name|ctx
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeSpatialStrategy
argument_list|(
name|fieldName
argument_list|,
name|rptStrategy
argument_list|,
name|geomStrategy
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|rptFieldType
operator|.
name|getQueryAnalyzer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexAnalyzer
specifier|public
name|Analyzer
name|getIndexAnalyzer
parameter_list|()
block|{
return|return
name|rptFieldType
operator|.
name|getIndexAnalyzer
argument_list|()
return|;
block|}
comment|// Most of the complexity of this field type is below, which is all about caching the shapes in a SolrCache
DECL|class|CachingSerializedDVStrategy
specifier|private
specifier|static
class|class
name|CachingSerializedDVStrategy
extends|extends
name|SerializedDVStrategy
block|{
DECL|method|CachingSerializedDVStrategy
specifier|public
name|CachingSerializedDVStrategy
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeShapeValueSource
specifier|public
name|ValueSource
name|makeShapeValueSource
parameter_list|()
block|{
return|return
operator|new
name|CachingShapeValuesource
argument_list|(
name|super
operator|.
name|makeShapeValueSource
argument_list|()
argument_list|,
name|getFieldName
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|CachingShapeValuesource
specifier|private
specifier|static
class|class
name|CachingShapeValuesource
extends|extends
name|ValueSource
block|{
DECL|field|targetValueSource
specifier|private
specifier|final
name|ValueSource
name|targetValueSource
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|method|CachingShapeValuesource
specifier|private
name|CachingShapeValuesource
parameter_list|(
name|ValueSource
name|targetValueSource
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|targetValueSource
operator|=
name|targetValueSource
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"cache("
operator|+
name|targetValueSource
operator|.
name|description
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|CachingShapeValuesource
name|that
init|=
operator|(
name|CachingShapeValuesource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|targetValueSource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|targetValueSource
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|targetValueSource
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|fieldName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|targetFuncValues
init|=
name|targetValueSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
comment|// The key is a pair of leaf reader with a docId relative to that reader. The value is a Map from field to Shape.
specifier|final
name|SolrCache
argument_list|<
name|PerSegCacheKey
argument_list|,
name|Shape
argument_list|>
name|cache
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
operator|.
name|getReq
argument_list|()
operator|.
name|getSearcher
argument_list|()
operator|.
name|getCache
argument_list|(
name|CACHE_KEY_PREFIX
operator|+
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
return|return
name|targetFuncValues
return|;
comment|// no caching; no configured cache
block|}
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
name|int
name|docId
init|=
operator|-
literal|1
decl_stmt|;
name|Shape
name|shape
init|=
literal|null
decl_stmt|;
specifier|private
name|void
name|setShapeFromDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
name|docId
operator|==
name|doc
condition|)
block|{
return|return;
block|}
name|docId
operator|=
name|doc
expr_stmt|;
comment|//lookup in cache
name|PerSegCacheKey
name|key
init|=
operator|new
name|PerSegCacheKey
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|doc
argument_list|)
decl_stmt|;
name|shape
operator|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
name|shape
operator|=
operator|(
name|Shape
operator|)
name|targetFuncValues
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|shape
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//optimize shape on a cache hit if possible. This must be thread-safe and it is.
if|if
condition|(
name|shape
operator|instanceof
name|JtsGeometry
condition|)
block|{
operator|(
operator|(
name|JtsGeometry
operator|)
name|shape
operator|)
operator|.
name|index
argument_list|()
expr_stmt|;
comment|// TODO would be nice if some day we didn't have to cast
block|}
block|}
block|}
comment|// Use the cache for exists& objectVal;
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|setShapeFromDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|shape
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|setShapeFromDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|shape
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|targetFuncValues
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|targetFuncValues
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
DECL|field|CACHE_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_KEY_PREFIX
init|=
literal|"perSegSpatialFieldCache_"
decl_stmt|;
comment|//then field name
comment|// Used in a SolrCache for the key
DECL|class|PerSegCacheKey
specifier|private
specifier|static
class|class
name|PerSegCacheKey
block|{
DECL|field|segCoreKeyRef
specifier|final
name|WeakReference
argument_list|<
name|Object
argument_list|>
name|segCoreKeyRef
decl_stmt|;
DECL|field|docId
specifier|final
name|int
name|docId
decl_stmt|;
DECL|field|hashCode
specifier|final
name|int
name|hashCode
decl_stmt|;
comment|//cached because we can't necessarily compute after construction
DECL|method|PerSegCacheKey
specifier|private
name|PerSegCacheKey
parameter_list|(
name|Object
name|segCoreKey
parameter_list|,
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|segCoreKeyRef
operator|=
operator|new
name|WeakReference
argument_list|<>
argument_list|(
name|segCoreKey
argument_list|)
expr_stmt|;
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|segCoreKey
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|docId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|PerSegCacheKey
name|that
init|=
operator|(
name|PerSegCacheKey
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|docId
operator|!=
name|that
operator|.
name|docId
condition|)
return|return
literal|false
return|;
comment|//compare by referent not reference
name|Object
name|segCoreKey
init|=
name|segCoreKeyRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|segCoreKey
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|segCoreKey
operator|.
name|equals
argument_list|(
name|that
operator|.
name|segCoreKeyRef
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Key{seg="
operator|+
name|segCoreKeyRef
operator|.
name|get
argument_list|()
operator|+
literal|", docId="
operator|+
name|docId
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class
end_unit
