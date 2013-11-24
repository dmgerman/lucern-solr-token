begin_unit
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
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
begin_class
DECL|class|SimpleFacetResult
specifier|public
specifier|final
class|class
name|SimpleFacetResult
block|{
comment|/** Total value for this path (sum of all child counts, or    *  sum of all child values), even those not included in    *  the topN. */
DECL|field|value
specifier|public
specifier|final
name|Number
name|value
decl_stmt|;
comment|/** How many labels were populated under the requested    *  path. */
DECL|field|childCount
specifier|public
specifier|final
name|int
name|childCount
decl_stmt|;
comment|/** Child counts. */
DECL|field|labelValues
specifier|public
specifier|final
name|LabelAndValue
index|[]
name|labelValues
decl_stmt|;
DECL|method|SimpleFacetResult
specifier|public
name|SimpleFacetResult
parameter_list|(
name|Number
name|value
parameter_list|,
name|LabelAndValue
index|[]
name|labelValues
parameter_list|,
name|int
name|childCount
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|labelValues
operator|=
name|labelValues
expr_stmt|;
name|this
operator|.
name|childCount
operator|=
name|childCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"value="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" childCount="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|childCount
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|labelValues
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
operator|+
name|labelValue
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
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
name|_other
parameter_list|)
block|{
if|if
condition|(
operator|(
name|_other
operator|instanceof
name|SimpleFacetResult
operator|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SimpleFacetResult
name|other
init|=
operator|(
name|SimpleFacetResult
operator|)
name|_other
decl_stmt|;
return|return
name|value
operator|.
name|equals
argument_list|(
name|other
operator|.
name|value
argument_list|)
operator|&&
name|childCount
operator|==
name|other
operator|.
name|childCount
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|labelValues
argument_list|,
name|other
operator|.
name|labelValues
argument_list|)
return|;
block|}
comment|// nocommit hashCode
block|}
end_class
end_unit
