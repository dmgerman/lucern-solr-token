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
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|DocValues
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
name|MultiValueSource
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
begin_comment
comment|/**  * While not strictly a distance, the Sq. Euclidean Distance is often all that is needed in many applications  * that require a distance, thus saving a sq. rt. calculation  *  **/
end_comment
begin_class
DECL|class|SquaredEuclideanFunction
specifier|public
class|class
name|SquaredEuclideanFunction
extends|extends
name|VectorDistanceFunction
block|{
DECL|field|name
specifier|protected
name|String
name|name
init|=
literal|"sqedist"
decl_stmt|;
DECL|method|SquaredEuclideanFunction
specifier|public
name|SquaredEuclideanFunction
parameter_list|(
name|MultiValueSource
name|source1
parameter_list|,
name|MultiValueSource
name|source2
parameter_list|)
block|{
name|super
argument_list|(
operator|-
literal|1
argument_list|,
name|source1
argument_list|,
name|source2
argument_list|)
expr_stmt|;
comment|//overriding distance, so power doesn't matter here
block|}
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * @param doc The doc to score    */
DECL|method|distance
specifier|protected
name|double
name|distance
parameter_list|(
name|int
name|doc
parameter_list|,
name|DocValues
name|dv1
parameter_list|,
name|DocValues
name|dv2
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
name|double
index|[]
name|vals1
init|=
operator|new
name|double
index|[
name|source1
operator|.
name|dimension
argument_list|()
index|]
decl_stmt|;
name|double
index|[]
name|vals2
init|=
operator|new
name|double
index|[
name|source1
operator|.
name|dimension
argument_list|()
index|]
decl_stmt|;
name|dv1
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|,
name|vals1
argument_list|)
expr_stmt|;
name|dv2
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|,
name|vals2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|double
name|v
init|=
name|vals1
index|[
name|i
index|]
operator|-
name|vals2
index|[
name|i
index|]
decl_stmt|;
name|result
operator|+=
name|v
operator|*
name|v
expr_stmt|;
block|}
return|return
name|result
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
operator|!
operator|(
name|o
operator|instanceof
name|SquaredEuclideanFunction
operator|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|SquaredEuclideanFunction
name|that
init|=
operator|(
name|SquaredEuclideanFunction
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
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
name|super
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
name|name
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
