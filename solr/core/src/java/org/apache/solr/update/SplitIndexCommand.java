begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
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
name|cloud
operator|.
name|DocRouter
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
name|SolrQueryRequest
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
comment|/**  * A merge indexes command encapsulated in an object.  *  * @since solr 1.4  *  */
end_comment
begin_class
DECL|class|SplitIndexCommand
specifier|public
class|class
name|SplitIndexCommand
extends|extends
name|UpdateCommand
block|{
comment|// public List<Directory> dirs;
DECL|field|paths
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
DECL|field|cores
specifier|public
name|List
argument_list|<
name|SolrCore
argument_list|>
name|cores
decl_stmt|;
comment|// either paths or cores should be specified
DECL|field|ranges
specifier|public
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
decl_stmt|;
DECL|field|router
specifier|public
name|DocRouter
name|router
decl_stmt|;
DECL|method|SplitIndexCommand
specifier|public
name|SplitIndexCommand
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|List
argument_list|<
name|SolrCore
argument_list|>
name|cores
parameter_list|,
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
parameter_list|,
name|DocRouter
name|router
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
name|this
operator|.
name|cores
operator|=
name|cores
expr_stmt|;
name|this
operator|.
name|ranges
operator|=
name|ranges
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"split"
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",paths="
operator|+
name|paths
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",cores="
operator|+
name|cores
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",ranges="
operator|+
name|ranges
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",router="
operator|+
name|router
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
