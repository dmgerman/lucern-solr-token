begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
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
name|Arrays
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|DoubleDocValues
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
name|IndexSearcher
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
name|distance
operator|.
name|DistanceCalculator
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
begin_comment
comment|/**  * The distance from a provided Point to a Point retrieved from a ValueSource via  * {@link org.apache.lucene.queries.function.FunctionValues#objectVal(int)}. The distance  * is calculated via a {@link com.spatial4j.core.distance.DistanceCalculator}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DistanceToShapeValueSource
specifier|public
class|class
name|DistanceToShapeValueSource
extends|extends
name|ValueSource
block|{
DECL|field|shapeValueSource
specifier|private
specifier|final
name|ValueSource
name|shapeValueSource
decl_stmt|;
DECL|field|queryPoint
specifier|private
specifier|final
name|Point
name|queryPoint
decl_stmt|;
DECL|field|multiplier
specifier|private
specifier|final
name|double
name|multiplier
decl_stmt|;
DECL|field|distCalc
specifier|private
specifier|final
name|DistanceCalculator
name|distCalc
decl_stmt|;
comment|//TODO if FunctionValues returns NaN; will things be ok?
DECL|field|nullValue
specifier|private
specifier|final
name|double
name|nullValue
decl_stmt|;
comment|//computed
DECL|method|DistanceToShapeValueSource
specifier|public
name|DistanceToShapeValueSource
parameter_list|(
name|ValueSource
name|shapeValueSource
parameter_list|,
name|Point
name|queryPoint
parameter_list|,
name|double
name|multiplier
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|shapeValueSource
operator|=
name|shapeValueSource
expr_stmt|;
name|this
operator|.
name|queryPoint
operator|=
name|queryPoint
expr_stmt|;
name|this
operator|.
name|multiplier
operator|=
name|multiplier
expr_stmt|;
name|this
operator|.
name|distCalc
operator|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
expr_stmt|;
name|this
operator|.
name|nullValue
operator|=
operator|(
name|ctx
operator|.
name|isGeo
argument_list|()
condition|?
literal|180
operator|*
name|multiplier
else|:
name|Double
operator|.
name|MAX_VALUE
operator|)
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
literal|"distance("
operator|+
name|queryPoint
operator|+
literal|" to "
operator|+
name|shapeValueSource
operator|.
name|description
argument_list|()
operator|+
literal|")*"
operator|+
name|multiplier
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|shapeValueSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
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
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|shapeValues
init|=
name|shapeValueSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|Shape
name|shape
init|=
operator|(
name|Shape
operator|)
name|shapeValues
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|==
literal|null
operator|||
name|shape
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|nullValue
return|;
name|Point
name|pt
init|=
name|shape
operator|.
name|getCenter
argument_list|()
decl_stmt|;
return|return
name|distCalc
operator|.
name|distance
argument_list|(
name|queryPoint
argument_list|,
name|pt
argument_list|)
operator|*
name|multiplier
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
name|Explanation
name|exp
init|=
name|super
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Explanation
argument_list|>
name|details
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|exp
operator|.
name|getDetails
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|details
operator|.
name|add
argument_list|(
name|shapeValues
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|exp
operator|.
name|getValue
argument_list|()
argument_list|,
name|exp
operator|.
name|getDescription
argument_list|()
argument_list|,
name|details
argument_list|)
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
name|DistanceToShapeValueSource
name|that
init|=
operator|(
name|DistanceToShapeValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|queryPoint
operator|.
name|equals
argument_list|(
name|that
operator|.
name|queryPoint
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|multiplier
argument_list|,
name|multiplier
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|shapeValueSource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|shapeValueSource
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|distCalc
operator|.
name|equals
argument_list|(
name|that
operator|.
name|distCalc
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
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|result
operator|=
name|shapeValueSource
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|queryPoint
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|multiplier
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
