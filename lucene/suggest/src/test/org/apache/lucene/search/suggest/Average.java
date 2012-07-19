begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_comment
comment|/**  * Average with standard deviation.  */
end_comment
begin_class
DECL|class|Average
specifier|final
class|class
name|Average
block|{
comment|/**      * Average (in milliseconds).      */
DECL|field|avg
specifier|public
specifier|final
name|double
name|avg
decl_stmt|;
comment|/**      * Standard deviation (in milliseconds).      */
DECL|field|stddev
specifier|public
specifier|final
name|double
name|stddev
decl_stmt|;
comment|/**      *       */
DECL|method|Average
name|Average
parameter_list|(
name|double
name|avg
parameter_list|,
name|double
name|stddev
parameter_list|)
block|{
name|this
operator|.
name|avg
operator|=
name|avg
expr_stmt|;
name|this
operator|.
name|stddev
operator|=
name|stddev
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.0f [+- %.2f]"
argument_list|,
name|avg
argument_list|,
name|stddev
argument_list|)
return|;
block|}
DECL|method|from
specifier|static
name|Average
name|from
parameter_list|(
name|List
argument_list|<
name|Double
argument_list|>
name|values
parameter_list|)
block|{
name|double
name|sum
init|=
literal|0
decl_stmt|;
name|double
name|sumSquares
init|=
literal|0
decl_stmt|;
for|for
control|(
name|double
name|l
range|:
name|values
control|)
block|{
name|sum
operator|+=
name|l
expr_stmt|;
name|sumSquares
operator|+=
name|l
operator|*
name|l
expr_stmt|;
block|}
name|double
name|avg
init|=
name|sum
operator|/
operator|(
name|double
operator|)
name|values
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
operator|new
name|Average
argument_list|(
operator|(
name|sum
operator|/
operator|(
name|double
operator|)
name|values
operator|.
name|size
argument_list|()
operator|)
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
name|sumSquares
operator|/
operator|(
name|double
operator|)
name|values
operator|.
name|size
argument_list|()
operator|-
name|avg
operator|*
name|avg
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
