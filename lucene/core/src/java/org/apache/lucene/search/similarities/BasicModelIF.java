begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|SimilarityBase
operator|.
name|log2
import|;
end_import
begin_comment
comment|/**  * An approximation of the<em>I(n<sub>e</sub>)</em> model.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BasicModelIF
specifier|public
class|class
name|BasicModelIF
extends|extends
name|BasicModel
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|BasicModelIF
specifier|public
name|BasicModelIF
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|)
block|{
name|long
name|N
init|=
name|stats
operator|.
name|getNumberOfDocuments
argument_list|()
decl_stmt|;
name|long
name|F
init|=
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
decl_stmt|;
return|return
name|tfn
operator|*
call|(
name|float
call|)
argument_list|(
name|log2
argument_list|(
literal|1
operator|+
operator|(
name|N
operator|+
literal|1
operator|)
operator|/
operator|(
name|F
operator|+
literal|0.5
operator|)
argument_list|)
argument_list|)
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
literal|"I(F)"
return|;
block|}
block|}
end_class
end_unit
