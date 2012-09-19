begin_unit
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
import|;
end_import
begin_comment
comment|/**  * Computes lambda as {@code docFreq+1 / numberOfDocuments+1}.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|LambdaDF
specifier|public
class|class
name|LambdaDF
extends|extends
name|Lambda
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|LambdaDF
specifier|public
name|LambdaDF
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|lambda
specifier|public
specifier|final
name|float
name|lambda
parameter_list|(
name|BasicStats
name|stats
parameter_list|)
block|{
return|return
operator|(
name|stats
operator|.
name|getDocFreq
argument_list|()
operator|+
literal|1F
operator|)
operator|/
operator|(
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
operator|+
literal|1F
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
specifier|final
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|)
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", computed from: "
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|lambda
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|stats
operator|.
name|getDocFreq
argument_list|()
argument_list|,
literal|"docFreq"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
argument_list|,
literal|"numberOfDocuments"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
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
literal|"D"
return|;
block|}
block|}
end_class
end_unit
