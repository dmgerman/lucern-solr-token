begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|analysis
operator|.
name|CharFilter
import|;
end_import
begin_comment
comment|/**  * Abstract parent class for analysis factories that create {@link CharFilter}  * instances.  */
end_comment
begin_class
DECL|class|CharFilterFactory
specifier|public
specifier|abstract
class|class
name|CharFilterFactory
extends|extends
name|AbstractAnalysisFactory
block|{
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|AnalysisSPILoader
argument_list|<
name|CharFilterFactory
argument_list|>
name|loader
init|=
operator|new
name|AnalysisSPILoader
argument_list|<>
argument_list|(
name|CharFilterFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** looks up a charfilter by name from context classpath */
DECL|method|forName
specifier|public
specifier|static
name|CharFilterFactory
name|forName
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
return|return
name|loader
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/** looks up a charfilter class by name from context classpath */
DECL|method|lookupClass
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|CharFilterFactory
argument_list|>
name|lookupClass
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|loader
operator|.
name|lookupClass
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** returns a list of all available charfilter names */
DECL|method|availableCharFilters
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|availableCharFilters
parameter_list|()
block|{
return|return
name|loader
operator|.
name|availableServices
argument_list|()
return|;
block|}
comment|/**     * Reloads the factory list from the given {@link ClassLoader}.    * Changes to the factories are visible after the method ends, all    * iterators ({@link #availableCharFilters()},...) stay consistent.     *     *<p><b>NOTE:</b> Only new factories are added, existing ones are    * never removed or replaced.    *     *<p><em>This method is expensive and should only be called for discovery    * of new factories on the given classpath/classloader!</em>    */
DECL|method|reloadCharFilters
specifier|public
specifier|static
name|void
name|reloadCharFilters
parameter_list|(
name|ClassLoader
name|classloader
parameter_list|)
block|{
name|loader
operator|.
name|reload
argument_list|(
name|classloader
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize this factory via a set of key-value pairs.    */
DECL|method|CharFilterFactory
specifier|protected
name|CharFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/** Wraps the given Reader with a CharFilter. */
DECL|method|create
specifier|public
specifier|abstract
name|Reader
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
function_decl|;
block|}
end_class
end_unit
