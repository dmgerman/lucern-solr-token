begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|standard
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|BenchmarkOptions
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|Constants
import|;
end_import
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *  * @deprecated Use the Task based stuff instead  **/
end_comment
begin_class
DECL|class|StandardOptions
specifier|public
class|class
name|StandardOptions
implements|implements
name|BenchmarkOptions
block|{
DECL|field|runCount
specifier|private
name|int
name|runCount
init|=
name|Constants
operator|.
name|DEFAULT_RUN_COUNT
decl_stmt|;
DECL|field|logStep
specifier|private
name|int
name|logStep
init|=
name|Constants
operator|.
name|DEFAULT_LOG_STEP
decl_stmt|;
DECL|field|scaleUp
specifier|private
name|int
name|scaleUp
init|=
name|Constants
operator|.
name|DEFAULT_SCALE_UP
decl_stmt|;
DECL|field|maximumDocumentsToIndex
specifier|private
name|int
name|maximumDocumentsToIndex
init|=
name|Constants
operator|.
name|DEFAULT_MAXIMUM_DOCUMENTS
decl_stmt|;
DECL|method|getMaximumDocumentsToIndex
specifier|public
name|int
name|getMaximumDocumentsToIndex
parameter_list|()
block|{
return|return
name|maximumDocumentsToIndex
return|;
block|}
DECL|method|setMaximumDocumentsToIndex
specifier|public
name|void
name|setMaximumDocumentsToIndex
parameter_list|(
name|int
name|maximumDocumentsToIndex
parameter_list|)
block|{
name|this
operator|.
name|maximumDocumentsToIndex
operator|=
name|maximumDocumentsToIndex
expr_stmt|;
block|}
comment|/**      * How often to print out log messages when in benchmark loops      */
DECL|method|getLogStep
specifier|public
name|int
name|getLogStep
parameter_list|()
block|{
return|return
name|logStep
return|;
block|}
DECL|method|setLogStep
specifier|public
name|void
name|setLogStep
parameter_list|(
name|int
name|logStep
parameter_list|)
block|{
name|this
operator|.
name|logStep
operator|=
name|logStep
expr_stmt|;
block|}
comment|/**      * The number of times to run the benchmark      */
DECL|method|getRunCount
specifier|public
name|int
name|getRunCount
parameter_list|()
block|{
return|return
name|runCount
return|;
block|}
DECL|method|setRunCount
specifier|public
name|void
name|setRunCount
parameter_list|(
name|int
name|runCount
parameter_list|)
block|{
name|this
operator|.
name|runCount
operator|=
name|runCount
expr_stmt|;
block|}
DECL|method|getScaleUp
specifier|public
name|int
name|getScaleUp
parameter_list|()
block|{
return|return
name|scaleUp
return|;
block|}
DECL|method|setScaleUp
specifier|public
name|void
name|setScaleUp
parameter_list|(
name|int
name|scaleUp
parameter_list|)
block|{
name|this
operator|.
name|scaleUp
operator|=
name|scaleUp
expr_stmt|;
block|}
block|}
end_class
end_unit
