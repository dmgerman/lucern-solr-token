begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_comment
comment|/**  * Group parameters  */
end_comment
begin_interface
DECL|interface|GroupParams
specifier|public
interface|interface
name|GroupParams
block|{
DECL|field|GROUP
specifier|public
specifier|static
specifier|final
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
DECL|field|GROUP_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_QUERY
init|=
name|GROUP
operator|+
literal|".query"
decl_stmt|;
DECL|field|GROUP_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_FIELD
init|=
name|GROUP
operator|+
literal|".field"
decl_stmt|;
DECL|field|GROUP_FUNC
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_FUNC
init|=
name|GROUP
operator|+
literal|".func"
decl_stmt|;
DECL|field|GROUP_SORT
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_SORT
init|=
name|GROUP
operator|+
literal|".sort"
decl_stmt|;
comment|/** the limit for the number of documents in each group */
DECL|field|GROUP_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_LIMIT
init|=
name|GROUP
operator|+
literal|".limit"
decl_stmt|;
comment|/** the offset for the doclist of each group */
DECL|field|GROUP_OFFSET
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_OFFSET
init|=
name|GROUP
operator|+
literal|".offset"
decl_stmt|;
comment|/** treat the first group result as the main result.  true/false */
DECL|field|GROUP_MAIN
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_MAIN
init|=
name|GROUP
operator|+
literal|".main"
decl_stmt|;
comment|/** treat the first group result as the main result.  true/false */
DECL|field|GROUP_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_FORMAT
init|=
name|GROUP
operator|+
literal|".format"
decl_stmt|;
comment|/**    * Whether to cache the first pass search (doc ids and score) for the second pass search.    * Also defines the maximum size of the group cache relative to maxdoc in a percentage.    * Values can be a positive integer, from 0 till 100. A value of 0 will disable the group cache.    * The default is 0.*/
DECL|field|GROUP_CACHE_PERCENTAGE
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_CACHE_PERCENTAGE
init|=
name|GROUP
operator|+
literal|".cache.percent"
decl_stmt|;
comment|// Note: Since you can supply multiple fields to group on, but only have a facets for the whole result. It only makes
comment|// sense to me to support these parameters for the first group.
comment|/** Whether the docSet (for example for faceting) should be based on plain documents (a.k.a UNGROUPED) or on the groups (a.k.a GROUPED). */
DECL|field|GROUP_COLLAPSE
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_COLLAPSE
init|=
name|GROUP
operator|+
literal|".collapse"
decl_stmt|;
comment|/** Whether the group count should be included in the response. */
DECL|field|GROUP_TOTAL_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_TOTAL_COUNT
init|=
name|GROUP
operator|+
literal|".ngroups"
decl_stmt|;
block|}
end_interface
end_unit
