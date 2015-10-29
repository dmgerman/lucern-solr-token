begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
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
name|distance
operator|.
name|DistanceUtils
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
name|Circle
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
name|Rectangle
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
name|SpatialRelation
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
name|impl
operator|.
name|Range
import|;
end_import
begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|CONTAINS
import|;
end_import
begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
operator|.
name|WITHIN
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
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * A base test class with utility methods to help test shapes.  * Extends from RandomizedTest.  */
end_comment
begin_class
DECL|class|RandomizedShapeTestCase
specifier|public
specifier|abstract
class|class
name|RandomizedShapeTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|EPS
specifier|protected
specifier|static
specifier|final
name|double
name|EPS
init|=
literal|10e-9
decl_stmt|;
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
comment|//needs to be set ASAP
comment|/** Used to reduce the space of numbers to increase the likelihood that    * random numbers become equivalent, and thus trigger different code paths.    * Also makes some random shapes easier to manually examine.    */
DECL|field|DIVISIBLE
specifier|protected
specifier|final
name|double
name|DIVISIBLE
init|=
literal|2
decl_stmt|;
comment|// even coordinates; (not always used)
DECL|method|RandomizedShapeTestCase
specifier|protected
name|RandomizedShapeTestCase
parameter_list|()
block|{   }
DECL|method|RandomizedShapeTestCase
specifier|public
name|RandomizedShapeTestCase
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|checkShapesImplementEquals
specifier|public
specifier|static
name|void
name|checkShapesImplementEquals
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|classes
parameter_list|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
range|:
name|classes
control|)
block|{
try|try
block|{
name|clazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"equals"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Shape needs to define 'equals' : "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|clazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"hashCode"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Shape needs to define 'hashCode' : "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//These few norm methods normalize the arguments for creating a shape to
comment|// account for the dateline. Some tests loop past the dateline or have offsets
comment|// that go past it and it's easier to have them coded that way and correct for
comment|// it here.  These norm methods should be used when needed, not frivolously.
DECL|method|normX
specifier|protected
name|double
name|normX
parameter_list|(
name|double
name|x
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|isGeo
argument_list|()
condition|?
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|x
argument_list|)
else|:
name|x
return|;
block|}
DECL|method|normY
specifier|protected
name|double
name|normY
parameter_list|(
name|double
name|y
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|isGeo
argument_list|()
condition|?
name|DistanceUtils
operator|.
name|normLatDEG
argument_list|(
name|y
argument_list|)
else|:
name|y
return|;
block|}
DECL|method|makeNormRect
specifier|protected
name|Rectangle
name|makeNormRect
parameter_list|(
name|double
name|minX
parameter_list|,
name|double
name|maxX
parameter_list|,
name|double
name|minY
parameter_list|,
name|double
name|maxY
parameter_list|)
block|{
if|if
condition|(
name|ctx
operator|.
name|isGeo
argument_list|()
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|maxX
operator|-
name|minX
argument_list|)
operator|>=
literal|360
condition|)
block|{
name|minX
operator|=
operator|-
literal|180
expr_stmt|;
name|maxX
operator|=
literal|180
expr_stmt|;
block|}
else|else
block|{
name|minX
operator|=
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|minX
argument_list|)
expr_stmt|;
name|maxX
operator|=
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|maxX
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|maxX
operator|<
name|minX
condition|)
block|{
name|double
name|t
init|=
name|minX
decl_stmt|;
name|minX
operator|=
name|maxX
expr_stmt|;
name|maxX
operator|=
name|t
expr_stmt|;
block|}
name|minX
operator|=
name|boundX
argument_list|(
name|minX
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|)
expr_stmt|;
name|maxX
operator|=
name|boundX
argument_list|(
name|maxX
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxY
operator|<
name|minY
condition|)
block|{
name|double
name|t
init|=
name|minY
decl_stmt|;
name|minY
operator|=
name|maxY
expr_stmt|;
name|maxY
operator|=
name|t
expr_stmt|;
block|}
name|minY
operator|=
name|boundY
argument_list|(
name|minY
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|)
expr_stmt|;
name|maxY
operator|=
name|boundY
argument_list|(
name|maxY
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|minX
argument_list|,
name|maxX
argument_list|,
name|minY
argument_list|,
name|maxY
argument_list|)
return|;
block|}
DECL|method|divisible
specifier|public
specifier|static
name|double
name|divisible
parameter_list|(
name|double
name|v
parameter_list|,
name|double
name|divisible
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|v
operator|/
name|divisible
argument_list|)
operator|*
name|divisible
argument_list|)
return|;
block|}
DECL|method|divisible
specifier|protected
name|double
name|divisible
parameter_list|(
name|double
name|v
parameter_list|)
block|{
return|return
name|divisible
argument_list|(
name|v
argument_list|,
name|DIVISIBLE
argument_list|)
return|;
block|}
comment|/** reset()'s p, and confines to world bounds. Might not be divisible if    * the world bound isn't divisible too.    */
DECL|method|divisible
specifier|protected
name|Point
name|divisible
parameter_list|(
name|Point
name|p
parameter_list|)
block|{
name|Rectangle
name|bounds
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
name|double
name|newX
init|=
name|boundX
argument_list|(
name|divisible
argument_list|(
name|p
operator|.
name|getX
argument_list|()
argument_list|)
argument_list|,
name|bounds
argument_list|)
decl_stmt|;
name|double
name|newY
init|=
name|boundY
argument_list|(
name|divisible
argument_list|(
name|p
operator|.
name|getY
argument_list|()
argument_list|)
argument_list|,
name|bounds
argument_list|)
decl_stmt|;
name|p
operator|.
name|reset
argument_list|(
name|newX
argument_list|,
name|newY
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|boundX
specifier|static
name|double
name|boundX
parameter_list|(
name|double
name|i
parameter_list|,
name|Rectangle
name|bounds
parameter_list|)
block|{
return|return
name|bound
argument_list|(
name|i
argument_list|,
name|bounds
operator|.
name|getMinX
argument_list|()
argument_list|,
name|bounds
operator|.
name|getMaxX
argument_list|()
argument_list|)
return|;
block|}
DECL|method|boundY
specifier|static
name|double
name|boundY
parameter_list|(
name|double
name|i
parameter_list|,
name|Rectangle
name|bounds
parameter_list|)
block|{
return|return
name|bound
argument_list|(
name|i
argument_list|,
name|bounds
operator|.
name|getMinY
argument_list|()
argument_list|,
name|bounds
operator|.
name|getMaxY
argument_list|()
argument_list|)
return|;
block|}
DECL|method|bound
specifier|static
name|double
name|bound
parameter_list|(
name|double
name|i
parameter_list|,
name|double
name|min
parameter_list|,
name|double
name|max
parameter_list|)
block|{
if|if
condition|(
name|i
operator|<
name|min
condition|)
return|return
name|min
return|;
if|if
condition|(
name|i
operator|>
name|max
condition|)
return|return
name|max
return|;
return|return
name|i
return|;
block|}
DECL|method|assertRelation
specifier|protected
name|void
name|assertRelation
parameter_list|(
name|SpatialRelation
name|expected
parameter_list|,
name|Shape
name|a
parameter_list|,
name|Shape
name|b
parameter_list|)
block|{
name|assertRelation
argument_list|(
literal|null
argument_list|,
name|expected
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRelation
specifier|protected
name|void
name|assertRelation
parameter_list|(
name|String
name|msg
parameter_list|,
name|SpatialRelation
name|expected
parameter_list|,
name|Shape
name|a
parameter_list|,
name|Shape
name|b
parameter_list|)
block|{
name|_assertIntersect
argument_list|(
name|msg
argument_list|,
name|expected
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
comment|//check flipped a& b w/ transpose(), while we're at it
name|_assertIntersect
argument_list|(
name|msg
argument_list|,
name|expected
operator|.
name|transpose
argument_list|()
argument_list|,
name|b
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
DECL|method|_assertIntersect
specifier|private
name|void
name|_assertIntersect
parameter_list|(
name|String
name|msg
parameter_list|,
name|SpatialRelation
name|expected
parameter_list|,
name|Shape
name|a
parameter_list|,
name|Shape
name|b
parameter_list|)
block|{
name|SpatialRelation
name|sect
init|=
name|a
operator|.
name|relate
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|sect
operator|==
name|expected
condition|)
return|return;
name|msg
operator|=
operator|(
operator|(
name|msg
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|msg
operator|+
literal|"\r"
operator|)
operator|+
name|a
operator|+
literal|" intersect "
operator|+
name|b
expr_stmt|;
if|if
condition|(
name|expected
operator|==
name|WITHIN
operator|||
name|expected
operator|==
name|CONTAINS
condition|)
block|{
if|if
condition|(
name|a
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|b
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
comment|// they are the same shape type
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
else|else
block|{
comment|//they are effectively points or lines that are the same location
name|assertTrue
argument_list|(
name|msg
argument_list|,
operator|!
name|a
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
operator|!
name|b
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
name|Rectangle
name|aBBox
init|=
name|a
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|Rectangle
name|bBBox
init|=
name|b
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
if|if
condition|(
name|aBBox
operator|.
name|getHeight
argument_list|()
operator|==
literal|0
operator|&&
name|bBBox
operator|.
name|getHeight
argument_list|()
operator|==
literal|0
operator|&&
operator|(
name|aBBox
operator|.
name|getMaxY
argument_list|()
operator|==
literal|90
operator|&&
name|bBBox
operator|.
name|getMaxY
argument_list|()
operator|==
literal|90
operator|||
name|aBBox
operator|.
name|getMinY
argument_list|()
operator|==
operator|-
literal|90
operator|&&
name|bBBox
operator|.
name|getMinY
argument_list|()
operator|==
operator|-
literal|90
operator|)
condition|)
empty_stmt|;
comment|//== a point at the pole
else|else
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|aBBox
argument_list|,
name|bBBox
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|expected
argument_list|,
name|sect
argument_list|)
expr_stmt|;
comment|//always fails
block|}
block|}
DECL|method|assertEqualsRatio
specifier|protected
name|void
name|assertEqualsRatio
parameter_list|(
name|String
name|msg
parameter_list|,
name|double
name|expected
parameter_list|,
name|double
name|actual
parameter_list|)
block|{
name|double
name|delta
init|=
name|Math
operator|.
name|abs
argument_list|(
name|actual
operator|-
name|expected
argument_list|)
decl_stmt|;
name|double
name|base
init|=
name|Math
operator|.
name|min
argument_list|(
name|actual
argument_list|,
name|expected
argument_list|)
decl_stmt|;
name|double
name|deltaRatio
init|=
name|base
operator|==
literal|0
condition|?
name|delta
else|:
name|Math
operator|.
name|min
argument_list|(
name|delta
argument_list|,
name|delta
operator|/
name|base
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
literal|0
argument_list|,
name|deltaRatio
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
block|}
DECL|method|randomIntBetweenDivisible
specifier|protected
name|int
name|randomIntBetweenDivisible
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|randomIntBetweenDivisible
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
operator|(
name|int
operator|)
name|DIVISIBLE
argument_list|)
return|;
block|}
comment|/** Returns a random integer between [start, end]. Integers between must be divisible by the 3rd argument. */
DECL|method|randomIntBetweenDivisible
specifier|protected
name|int
name|randomIntBetweenDivisible
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|divisible
parameter_list|)
block|{
comment|// DWS: I tested this
name|int
name|divisStart
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|start
operator|+
literal|1
operator|)
operator|/
operator|(
name|double
operator|)
name|divisible
argument_list|)
decl_stmt|;
name|int
name|divisEnd
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|end
operator|-
literal|1
operator|)
operator|/
operator|(
name|double
operator|)
name|divisible
argument_list|)
decl_stmt|;
name|int
name|divisRange
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|divisEnd
operator|-
name|divisStart
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|r
init|=
name|randomInt
argument_list|(
literal|1
operator|+
name|divisRange
argument_list|)
decl_stmt|;
comment|//remember that '0' is counted
if|if
condition|(
name|r
operator|==
literal|0
condition|)
return|return
name|start
return|;
if|if
condition|(
name|r
operator|==
literal|1
condition|)
return|return
name|end
return|;
return|return
operator|(
name|r
operator|-
literal|2
operator|+
name|divisStart
operator|)
operator|*
name|divisible
return|;
block|}
DECL|method|randomRectangle
specifier|protected
name|Rectangle
name|randomRectangle
parameter_list|(
name|Point
name|nearP
parameter_list|)
block|{
name|Rectangle
name|bounds
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
if|if
condition|(
name|nearP
operator|==
literal|null
condition|)
name|nearP
operator|=
name|randomPointIn
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
name|Range
name|xRange
init|=
name|randomRange
argument_list|(
name|rarely
argument_list|()
condition|?
literal|0
else|:
name|nearP
operator|.
name|getX
argument_list|()
argument_list|,
name|Range
operator|.
name|xRange
argument_list|(
name|bounds
argument_list|,
name|ctx
argument_list|)
argument_list|)
decl_stmt|;
name|Range
name|yRange
init|=
name|randomRange
argument_list|(
name|rarely
argument_list|()
condition|?
literal|0
else|:
name|nearP
operator|.
name|getY
argument_list|()
argument_list|,
name|Range
operator|.
name|yRange
argument_list|(
name|bounds
argument_list|,
name|ctx
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|makeNormRect
argument_list|(
name|divisible
argument_list|(
name|xRange
operator|.
name|getMin
argument_list|()
argument_list|)
argument_list|,
name|divisible
argument_list|(
name|xRange
operator|.
name|getMax
argument_list|()
argument_list|)
argument_list|,
name|divisible
argument_list|(
name|yRange
operator|.
name|getMin
argument_list|()
argument_list|)
argument_list|,
name|divisible
argument_list|(
name|yRange
operator|.
name|getMax
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|randomRange
specifier|private
name|Range
name|randomRange
parameter_list|(
name|double
name|near
parameter_list|,
name|Range
name|bounds
parameter_list|)
block|{
name|double
name|mid
init|=
name|near
operator|+
name|randomGaussian
argument_list|()
operator|*
name|bounds
operator|.
name|getWidth
argument_list|()
operator|/
literal|6
decl_stmt|;
name|double
name|width
init|=
name|Math
operator|.
name|abs
argument_list|(
name|randomGaussian
argument_list|()
argument_list|)
operator|*
name|bounds
operator|.
name|getWidth
argument_list|()
operator|/
literal|6
decl_stmt|;
comment|//1/3rd
return|return
operator|new
name|Range
argument_list|(
name|mid
operator|-
name|width
operator|/
literal|2
argument_list|,
name|mid
operator|+
name|width
operator|/
literal|2
argument_list|)
return|;
block|}
DECL|method|randomGaussianZeroTo
specifier|private
name|double
name|randomGaussianZeroTo
parameter_list|(
name|double
name|max
parameter_list|)
block|{
if|if
condition|(
name|max
operator|==
literal|0
condition|)
return|return
name|max
return|;
assert|assert
name|max
operator|>
literal|0
assert|;
name|double
name|r
decl_stmt|;
do|do
block|{
name|r
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|randomGaussian
argument_list|()
argument_list|)
operator|*
operator|(
name|max
operator|*
literal|0.50
operator|)
expr_stmt|;
block|}
do|while
condition|(
name|r
operator|>
name|max
condition|)
do|;
return|return
name|r
return|;
block|}
DECL|method|randomRectangle
specifier|protected
name|Rectangle
name|randomRectangle
parameter_list|(
name|int
name|divisible
parameter_list|)
block|{
name|double
name|rX
init|=
name|randomIntBetweenDivisible
argument_list|(
operator|-
literal|180
argument_list|,
literal|180
argument_list|,
name|divisible
argument_list|)
decl_stmt|;
name|double
name|rW
init|=
name|randomIntBetweenDivisible
argument_list|(
literal|0
argument_list|,
literal|360
argument_list|,
name|divisible
argument_list|)
decl_stmt|;
name|double
name|rY1
init|=
name|randomIntBetweenDivisible
argument_list|(
operator|-
literal|90
argument_list|,
literal|90
argument_list|,
name|divisible
argument_list|)
decl_stmt|;
name|double
name|rY2
init|=
name|randomIntBetweenDivisible
argument_list|(
operator|-
literal|90
argument_list|,
literal|90
argument_list|,
name|divisible
argument_list|)
decl_stmt|;
name|double
name|rYmin
init|=
name|Math
operator|.
name|min
argument_list|(
name|rY1
argument_list|,
name|rY2
argument_list|)
decl_stmt|;
name|double
name|rYmax
init|=
name|Math
operator|.
name|max
argument_list|(
name|rY1
argument_list|,
name|rY2
argument_list|)
decl_stmt|;
if|if
condition|(
name|rW
operator|>
literal|0
operator|&&
name|rX
operator|==
literal|180
condition|)
name|rX
operator|=
operator|-
literal|180
expr_stmt|;
return|return
name|makeNormRect
argument_list|(
name|rX
argument_list|,
name|rX
operator|+
name|rW
argument_list|,
name|rYmin
argument_list|,
name|rYmax
argument_list|)
return|;
block|}
DECL|method|randomPoint
specifier|protected
name|Point
name|randomPoint
parameter_list|()
block|{
return|return
name|randomPointIn
argument_list|(
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|)
return|;
block|}
DECL|method|randomPointIn
specifier|protected
name|Point
name|randomPointIn
parameter_list|(
name|Circle
name|c
parameter_list|)
block|{
name|double
name|d
init|=
name|c
operator|.
name|getRadius
argument_list|()
operator|*
name|randomDouble
argument_list|()
decl_stmt|;
name|double
name|angleDEG
init|=
literal|360
operator|*
name|randomDouble
argument_list|()
decl_stmt|;
name|Point
name|p
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|pointOnBearing
argument_list|(
name|c
operator|.
name|getCenter
argument_list|()
argument_list|,
name|d
argument_list|,
name|angleDEG
argument_list|,
name|ctx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CONTAINS
argument_list|,
name|c
operator|.
name|relate
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|randomPointIn
specifier|protected
name|Point
name|randomPointIn
parameter_list|(
name|Rectangle
name|r
parameter_list|)
block|{
name|double
name|x
init|=
name|r
operator|.
name|getMinX
argument_list|()
operator|+
name|randomDouble
argument_list|()
operator|*
name|r
operator|.
name|getWidth
argument_list|()
decl_stmt|;
name|double
name|y
init|=
name|r
operator|.
name|getMinY
argument_list|()
operator|+
name|randomDouble
argument_list|()
operator|*
name|r
operator|.
name|getHeight
argument_list|()
decl_stmt|;
name|x
operator|=
name|normX
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|y
operator|=
name|normY
argument_list|(
name|y
argument_list|)
expr_stmt|;
name|Point
name|p
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CONTAINS
argument_list|,
name|r
operator|.
name|relate
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|randomPointInOrNull
specifier|protected
name|Point
name|randomPointInOrNull
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shape
operator|.
name|hasArea
argument_list|()
condition|)
comment|// or try the center?
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Need area to define shape!"
argument_list|)
throw|;
name|Rectangle
name|bbox
init|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Point
name|p
init|=
name|randomPointIn
argument_list|(
name|bbox
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|.
name|relate
argument_list|(
name|p
argument_list|)
operator|.
name|intersects
argument_list|()
condition|)
block|{
return|return
name|p
return|;
block|}
block|}
return|return
literal|null
return|;
comment|//tried too many times and failed
block|}
block|}
end_class
end_unit
