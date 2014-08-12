begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.serialized
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|serialized
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
name|io
operator|.
name|BinaryCodec
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
name|Point
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|BinaryDocValuesField
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
name|index
operator|.
name|AtomicReaderContext
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
name|BinaryDocValues
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|search
operator|.
name|Filter
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
name|SpatialStrategy
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
name|util
operator|.
name|DistanceToShapeValueSource
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
name|util
operator|.
name|ShapePredicateValueSource
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
name|Bits
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
name|BytesRef
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
name|BytesRefBuilder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterOutputStream
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
name|Map
import|;
end_import
begin_comment
comment|/**  * A SpatialStrategy based on serializing a Shape stored into BinaryDocValues.  * This is not at all fast; it's designed to be used in conjunction with another index based  * SpatialStrategy that is approximated (like {@link org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy})  * to add precision or eventually make more specific / advanced calculations on the per-document  * geometry.  * The serialization uses Spatial4j's {@link com.spatial4j.core.io.BinaryCodec}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SerializedDVStrategy
specifier|public
class|class
name|SerializedDVStrategy
extends|extends
name|SpatialStrategy
block|{
comment|/**    * A cache heuristic for the buf size based on the last shape size.    */
comment|//TODO do we make this non-volatile since it's merely a heuristic?
DECL|field|indexLastBufSize
specifier|private
specifier|volatile
name|int
name|indexLastBufSize
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
comment|//8KB default on first run
comment|/**    * Constructs the spatial strategy with its mandatory arguments.    */
DECL|method|SerializedDVStrategy
specifier|public
name|SerializedDVStrategy
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
DECL|method|createIndexableFields
specifier|public
name|Field
index|[]
name|createIndexableFields
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
name|int
name|bufSize
init|=
name|Math
operator|.
name|max
argument_list|(
literal|128
argument_list|,
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|indexLastBufSize
operator|*
literal|1.5
argument_list|)
argument_list|)
decl_stmt|;
comment|//50% headroom over last
name|ByteArrayOutputStream
name|byteStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|bufSize
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|//receiver of byteStream's bytes
try|try
block|{
name|ctx
operator|.
name|getBinaryCodec
argument_list|()
operator|.
name|writeShape
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|byteStream
argument_list|)
argument_list|,
name|shape
argument_list|)
expr_stmt|;
comment|//this is a hack to avoid redundant byte array copying by byteStream.toByteArray()
name|byteStream
operator|.
name|writeTo
argument_list|(
operator|new
name|FilterOutputStream
argument_list|(
literal|null
comment|/*not used*/
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|bytesRef
operator|.
name|bytes
operator|=
name|b
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
name|off
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|len
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexLastBufSize
operator|=
name|bytesRef
operator|.
name|length
expr_stmt|;
comment|//cache heuristic
return|return
operator|new
name|Field
index|[]
block|{
operator|new
name|BinaryDocValuesField
argument_list|(
name|getFieldName
argument_list|()
argument_list|,
name|bytesRef
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|makeDistanceValueSource
specifier|public
name|ValueSource
name|makeDistanceValueSource
parameter_list|(
name|Point
name|queryPoint
parameter_list|,
name|double
name|multiplier
parameter_list|)
block|{
comment|//TODO if makeShapeValueSource gets lifted to the top; this could become a generic impl.
return|return
operator|new
name|DistanceToShapeValueSource
argument_list|(
name|makeShapeValueSource
argument_list|()
argument_list|,
name|queryPoint
argument_list|,
name|multiplier
argument_list|,
name|ctx
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This strategy can't return a query that operates"
operator|+
literal|" efficiently. Instead try a Filter or ValueSource."
argument_list|)
throw|;
block|}
comment|/**    * Returns a Filter that should be used with {@link org.apache.lucene.search.FilteredQuery#QUERY_FIRST_FILTER_STRATEGY}.    * Use in another manner is likely to result in an {@link java.lang.UnsupportedOperationException}    * to prevent misuse because the filter can't efficiently work via iteration.    */
annotation|@
name|Override
DECL|method|makeFilter
specifier|public
name|Filter
name|makeFilter
parameter_list|(
specifier|final
name|SpatialArgs
name|args
parameter_list|)
block|{
name|ValueSource
name|shapeValueSource
init|=
name|makeShapeValueSource
argument_list|()
decl_stmt|;
name|ShapePredicateValueSource
name|predicateValueSource
init|=
operator|new
name|ShapePredicateValueSource
argument_list|(
name|shapeValueSource
argument_list|,
name|args
operator|.
name|getOperation
argument_list|()
argument_list|,
name|args
operator|.
name|getShape
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|PredicateValueSourceFilter
argument_list|(
name|predicateValueSource
argument_list|)
return|;
block|}
comment|/**    * Provides access to each shape per document as a ValueSource in which    * {@link org.apache.lucene.queries.function.FunctionValues#objectVal(int)} returns a {@link    * Shape}.    */
comment|//TODO raise to SpatialStrategy
DECL|method|makeShapeValueSource
specifier|public
name|ValueSource
name|makeShapeValueSource
parameter_list|()
block|{
return|return
operator|new
name|ShapeDocValueSource
argument_list|(
name|getFieldName
argument_list|()
argument_list|,
name|ctx
operator|.
name|getBinaryCodec
argument_list|()
argument_list|)
return|;
block|}
comment|/** This filter only supports returning a DocSet with a bits(). If you try to grab the    * iterator then you'll get an UnsupportedOperationException.    */
DECL|class|PredicateValueSourceFilter
specifier|static
class|class
name|PredicateValueSourceFilter
extends|extends
name|Filter
block|{
DECL|field|predicateValueSource
specifier|private
specifier|final
name|ValueSource
name|predicateValueSource
decl_stmt|;
comment|//we call boolVal(doc)
DECL|method|PredicateValueSourceFilter
specifier|public
name|PredicateValueSourceFilter
parameter_list|(
name|ValueSource
name|predicateValueSource
parameter_list|)
block|{
name|this
operator|.
name|predicateValueSource
operator|=
name|predicateValueSource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
specifier|final
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Iteration is too slow; instead try FilteredQuery.QUERY_FIRST_FILTER_STRATEGY"
argument_list|)
throw|;
comment|//Note that if you're truly bent on doing this, then see FunctionValues.getRangeScorer
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|bits
parameter_list|()
throws|throws
name|IOException
block|{
comment|//null Map context -- we simply don't have one. That's ok.
specifier|final
name|FunctionValues
name|predFuncValues
init|=
name|predicateValueSource
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|acceptDocs
operator|!=
literal|null
operator|&&
operator|!
name|acceptDocs
operator|.
name|get
argument_list|(
name|index
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|predFuncValues
operator|.
name|boolVal
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
block|}
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
name|PredicateValueSourceFilter
name|that
init|=
operator|(
name|PredicateValueSourceFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|predicateValueSource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|predicateValueSource
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|predicateValueSource
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|//PredicateValueSourceFilter
comment|/**    * Implements a ValueSource by deserializing a Shape in from BinaryDocValues using BinaryCodec.    * @see #makeShapeValueSource()    */
DECL|class|ShapeDocValueSource
specifier|static
class|class
name|ShapeDocValueSource
extends|extends
name|ValueSource
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|binaryCodec
specifier|private
specifier|final
name|BinaryCodec
name|binaryCodec
decl_stmt|;
comment|//spatial4j
DECL|method|ShapeDocValueSource
specifier|private
name|ShapeDocValueSource
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BinaryCodec
name|binaryCodec
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|binaryCodec
operator|=
name|binaryCodec
expr_stmt|;
block|}
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
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BinaryDocValues
name|docValues
init|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
name|int
name|bytesRefDoc
init|=
operator|-
literal|1
decl_stmt|;
name|BytesRefBuilder
name|bytesRef
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|boolean
name|fillBytes
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
name|bytesRefDoc
operator|!=
name|doc
condition|)
block|{
name|bytesRef
operator|.
name|copyBytes
argument_list|(
name|docValues
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|bytesRefDoc
operator|=
name|doc
expr_stmt|;
block|}
return|return
name|bytesRef
operator|.
name|length
argument_list|()
operator|!=
literal|0
return|;
block|}
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
return|return
name|fillBytes
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|bytesVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRefBuilder
name|target
parameter_list|)
block|{
name|target
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|fillBytes
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|target
operator|.
name|copyBytes
argument_list|(
name|bytesRef
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|fillBytes
argument_list|(
name|docId
argument_list|)
condition|)
return|return
literal|null
return|;
name|DataInputStream
name|dataInput
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytesRef
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|binaryCodec
operator|.
name|readShape
argument_list|(
name|dataInput
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
operator|new
name|Explanation
argument_list|(
name|Float
operator|.
name|NaN
argument_list|,
name|toString
argument_list|(
name|doc
argument_list|)
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
name|description
argument_list|()
operator|+
literal|"="
operator|+
name|objectVal
argument_list|(
name|doc
argument_list|)
return|;
comment|//TODO truncate?
block|}
block|}
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
name|ShapeDocValueSource
name|that
init|=
operator|(
name|ShapeDocValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|fieldName
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
name|result
return|;
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
literal|"shapeDocVal("
operator|+
name|fieldName
operator|+
literal|")"
return|;
block|}
block|}
comment|//ShapeDocValueSource
block|}
end_class
end_unit
