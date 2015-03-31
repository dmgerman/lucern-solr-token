begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_comment
comment|/**  *  An descending field Comparator which compares a field of two Tuples and determines sort order.  **/
end_comment
begin_class
DECL|class|DescFieldComp
specifier|public
class|class
name|DescFieldComp
implements|implements
name|Comparator
argument_list|<
name|Tuple
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|method|DescFieldComp
specifier|public
name|DescFieldComp
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Tuple
name|t1
parameter_list|,
name|Tuple
name|t2
parameter_list|)
block|{
name|Comparable
name|o1
init|=
operator|(
name|Comparable
operator|)
name|t1
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|Comparable
name|o2
init|=
operator|(
name|Comparable
operator|)
name|t2
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|int
name|c
init|=
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
operator|-
name|c
return|;
block|}
block|}
block|}
end_class
end_unit
