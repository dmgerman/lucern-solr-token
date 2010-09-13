begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
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
name|response
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
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
name|List
import|;
end_import
begin_class
DECL|class|PivotField
specifier|public
class|class
name|PivotField
implements|implements
name|Serializable
block|{
DECL|field|_field
specifier|final
name|String
name|_field
decl_stmt|;
DECL|field|_value
specifier|final
name|Object
name|_value
decl_stmt|;
DECL|field|_count
specifier|final
name|int
name|_count
decl_stmt|;
DECL|field|_pivot
specifier|final
name|List
argument_list|<
name|PivotField
argument_list|>
name|_pivot
decl_stmt|;
DECL|method|PivotField
specifier|public
name|PivotField
parameter_list|(
name|String
name|f
parameter_list|,
name|Object
name|v
parameter_list|,
name|int
name|count
parameter_list|,
name|List
argument_list|<
name|PivotField
argument_list|>
name|pivot
parameter_list|)
block|{
name|_field
operator|=
name|f
expr_stmt|;
name|_value
operator|=
name|v
expr_stmt|;
name|_count
operator|=
name|count
expr_stmt|;
name|_pivot
operator|=
name|pivot
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|_field
return|;
block|}
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|_value
return|;
block|}
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|_count
return|;
block|}
DECL|method|getPivot
specifier|public
name|List
argument_list|<
name|PivotField
argument_list|>
name|getPivot
parameter_list|()
block|{
return|return
name|_pivot
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
return|return
name|_field
operator|+
literal|":"
operator|+
name|_value
operator|+
literal|" ["
operator|+
name|_count
operator|+
literal|"] "
operator|+
name|_pivot
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|int
name|indent
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indent
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|_field
operator|+
literal|"="
operator|+
name|_value
operator|+
literal|" ("
operator|+
name|_count
operator|+
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|_pivot
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PivotField
name|p
range|:
name|_pivot
control|)
block|{
name|p
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|indent
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
