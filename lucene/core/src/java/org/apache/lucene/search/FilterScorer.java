begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**   * A {@code FilterScorer} contains another {@code Scorer}, which it  * uses as its basic source of data, possibly transforming the data along the  * way or providing additional functionality. The class  * {@code FilterScorer} itself simply implements all abstract methods  * of {@code Scorer} with versions that pass all requests to the  * contained scorer. Subclasses of {@code FilterScorer} may  * further override some of these methods and may also provide additional  * methods and fields.  */
end_comment
begin_class
DECL|class|FilterScorer
specifier|public
specifier|abstract
class|class
name|FilterScorer
extends|extends
name|Scorer
block|{
DECL|field|in
specifier|protected
specifier|final
name|Scorer
name|in
decl_stmt|;
comment|/**    * Create a new FilterScorer    * @param in the {@link Scorer} to wrap    */
DECL|method|FilterScorer
specifier|public
name|FilterScorer
parameter_list|(
name|Scorer
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/**    * Create a new FilterScorer with a specific weight    * @param in the {@link Scorer} to wrap    * @param weight a {@link Weight}    */
DECL|method|FilterScorer
specifier|public
name|FilterScorer
parameter_list|(
name|Scorer
name|in
parameter_list|,
name|Weight
name|weight
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|score
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
specifier|final
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
specifier|final
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
name|in
operator|.
name|asTwoPhaseIterator
argument_list|()
return|;
block|}
block|}
end_class
end_unit
