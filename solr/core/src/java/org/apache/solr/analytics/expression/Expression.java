begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.expression
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|expression
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|analytics
operator|.
name|request
operator|.
name|FieldFacetRequest
operator|.
name|FacetSortDirection
import|;
end_import
begin_comment
comment|/**  * Expressions map either zero, one, two or many inputs to a single value.   * They can be defined recursively to compute complex math.  */
end_comment
begin_class
DECL|class|Expression
specifier|public
specifier|abstract
class|class
name|Expression
block|{
DECL|method|getValue
specifier|public
specifier|abstract
name|Comparable
name|getValue
parameter_list|()
function_decl|;
DECL|method|comparator
specifier|public
name|Comparator
argument_list|<
name|Expression
argument_list|>
name|comparator
parameter_list|(
specifier|final
name|FacetSortDirection
name|direction
parameter_list|)
block|{
return|return
operator|new
name|Comparator
argument_list|<
name|Expression
argument_list|>
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
block|{
if|if
condition|(
name|direction
operator|==
name|FacetSortDirection
operator|.
name|ASCENDING
condition|)
block|{
return|return
name|a
operator|.
name|getValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|b
operator|.
name|getValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
