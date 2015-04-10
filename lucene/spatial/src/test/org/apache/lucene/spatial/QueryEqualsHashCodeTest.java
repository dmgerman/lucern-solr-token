begin_unit
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
operator|.
name|BBoxStrategy
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
name|prefix
operator|.
name|TermQueryPrefixTreeStrategy
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
name|GeohashPrefixTree
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
name|QuadPrefixTree
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
name|SpatialPrefixTree
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
name|lucene
operator|.
name|spatial
operator|.
name|vector
operator|.
name|PointVectorStrategy
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
name|LuceneTestCase
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
begin_class
DECL|class|QueryEqualsHashCodeTest
specifier|public
class|class
name|QueryEqualsHashCodeTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|ctx
specifier|private
specifier|final
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
DECL|field|predicate
specifier|private
name|SpatialOperation
name|predicate
decl_stmt|;
annotation|@
name|Test
DECL|method|testEqualsHashCode
specifier|public
name|void
name|testEqualsHashCode
parameter_list|()
block|{
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
comment|//0-3
case|case
literal|0
case|:
name|predicate
operator|=
name|SpatialOperation
operator|.
name|Contains
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|predicate
operator|=
name|SpatialOperation
operator|.
name|IsWithin
expr_stmt|;
break|break;
default|default:
name|predicate
operator|=
name|SpatialOperation
operator|.
name|Intersects
expr_stmt|;
break|break;
block|}
specifier|final
name|SpatialPrefixTree
name|gridQuad
init|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|SpatialPrefixTree
name|gridGeohash
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|SpatialStrategy
argument_list|>
name|strategies
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RecursivePrefixTreeStrategy
name|recursive_geohash
init|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|gridGeohash
argument_list|,
literal|"recursive_geohash"
argument_list|)
decl_stmt|;
name|strategies
operator|.
name|add
argument_list|(
name|recursive_geohash
argument_list|)
expr_stmt|;
name|strategies
operator|.
name|add
argument_list|(
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|gridQuad
argument_list|,
literal|"termquery_quad"
argument_list|)
argument_list|)
expr_stmt|;
name|strategies
operator|.
name|add
argument_list|(
operator|new
name|PointVectorStrategy
argument_list|(
name|ctx
argument_list|,
literal|"pointvector"
argument_list|)
argument_list|)
expr_stmt|;
name|strategies
operator|.
name|add
argument_list|(
operator|new
name|BBoxStrategy
argument_list|(
name|ctx
argument_list|,
literal|"bbox"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SerializedDVStrategy
name|serialized
init|=
operator|new
name|SerializedDVStrategy
argument_list|(
name|ctx
argument_list|,
literal|"serialized"
argument_list|)
decl_stmt|;
name|strategies
operator|.
name|add
argument_list|(
name|serialized
argument_list|)
expr_stmt|;
name|strategies
operator|.
name|add
argument_list|(
operator|new
name|CompositeSpatialStrategy
argument_list|(
literal|"composite"
argument_list|,
name|recursive_geohash
argument_list|,
name|serialized
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SpatialStrategy
name|strategy
range|:
name|strategies
control|)
block|{
name|testEqualsHashcode
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEqualsHashcode
specifier|private
name|void
name|testEqualsHashcode
parameter_list|(
specifier|final
name|SpatialStrategy
name|strategy
parameter_list|)
block|{
specifier|final
name|SpatialArgs
name|args1
init|=
name|makeArgs1
argument_list|()
decl_stmt|;
specifier|final
name|SpatialArgs
name|args2
init|=
name|makeArgs2
argument_list|()
decl_stmt|;
name|testEqualsHashcode
argument_list|(
name|args1
argument_list|,
name|args2
argument_list|,
operator|new
name|ObjGenerator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|gen
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
return|return
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|testEqualsHashcode
argument_list|(
name|args1
argument_list|,
name|args2
argument_list|,
operator|new
name|ObjGenerator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|gen
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
return|return
name|strategy
operator|.
name|makeFilter
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|testEqualsHashcode
argument_list|(
name|args1
argument_list|,
name|args2
argument_list|,
operator|new
name|ObjGenerator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|gen
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
return|return
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|args
operator|.
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqualsHashcode
specifier|private
name|void
name|testEqualsHashcode
parameter_list|(
name|SpatialArgs
name|args1
parameter_list|,
name|SpatialArgs
name|args2
parameter_list|,
name|ObjGenerator
name|generator
parameter_list|)
block|{
name|Object
name|first
decl_stmt|;
try|try
block|{
name|first
operator|=
name|generator
operator|.
name|gen
argument_list|(
name|args1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
return|return;
block|}
if|if
condition|(
name|first
operator|==
literal|null
condition|)
return|return;
comment|//unsupported op?
name|Object
name|second
init|=
name|generator
operator|.
name|gen
argument_list|(
name|args1
argument_list|)
decl_stmt|;
comment|//should be the same
name|assertEquals
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|first
operator|.
name|hashCode
argument_list|()
argument_list|,
name|second
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|args1
operator|.
name|equals
argument_list|(
name|args2
argument_list|)
operator|==
literal|false
argument_list|)
expr_stmt|;
name|second
operator|=
name|generator
operator|.
name|gen
argument_list|(
name|args2
argument_list|)
expr_stmt|;
comment|//now should be different
name|assertTrue
argument_list|(
name|first
operator|.
name|equals
argument_list|(
name|second
argument_list|)
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|first
operator|.
name|hashCode
argument_list|()
operator|!=
name|second
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeArgs1
specifier|private
name|SpatialArgs
name|makeArgs1
parameter_list|()
block|{
specifier|final
name|Shape
name|shape1
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpatialArgs
argument_list|(
name|predicate
argument_list|,
name|shape1
argument_list|)
return|;
block|}
DECL|method|makeArgs2
specifier|private
name|SpatialArgs
name|makeArgs2
parameter_list|()
block|{
specifier|final
name|Shape
name|shape2
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|20
argument_list|)
decl_stmt|;
return|return
operator|new
name|SpatialArgs
argument_list|(
name|predicate
argument_list|,
name|shape2
argument_list|)
return|;
block|}
DECL|interface|ObjGenerator
interface|interface
name|ObjGenerator
block|{
DECL|method|gen
name|Object
name|gen
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
function_decl|;
block|}
block|}
end_class
end_unit
