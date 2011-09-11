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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A simple {@link Similarity} provider that returns in  * {@code get(String field)} the object passed to its constructor. This class  * is aimed at non-VSM models, and therefore both the {@link #coord} and  * {@link #queryNorm} methods return {@code 1}. Use  * {@link DefaultSimilarityProvider} for {@link DefaultSimilarity}.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BasicSimilarityProvider
specifier|public
class|class
name|BasicSimilarityProvider
implements|implements
name|SimilarityProvider
block|{
DECL|field|sim
specifier|private
specifier|final
name|Similarity
name|sim
decl_stmt|;
DECL|method|BasicSimilarityProvider
specifier|public
name|BasicSimilarityProvider
parameter_list|(
name|Similarity
name|sim
parameter_list|)
block|{
name|this
operator|.
name|sim
operator|=
name|sim
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
annotation|@
name|Override
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|sim
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
literal|"BasicSimilarityProvider("
operator|+
name|sim
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
