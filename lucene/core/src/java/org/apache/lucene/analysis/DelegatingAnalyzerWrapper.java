begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An analyzer wrapper, that doesn't allow to wrap components or readers.  * By disallowing it, it means that the thread local resources can be delegated  * to the delegate analyzer, and not also be allocated on this analyzer.  * This wrapper class is the base class of all analyzers that just delegate to  * another analyzer, e.g. per field name.  *   *<p>This solves the problem of per field analyzer wrapper, where it also  * maintains a thread local per field token stream components, while it can  * safely delegate those and not also hold these data structures, which can  * become expensive memory wise.  *   *<p><b>Please note:</b> This analyzer uses a private {@link Analyzer.ReuseStrategy},  * which is returned by {@link #getReuseStrategy()}. This strategy is used when  * delegating. If you wrap this analyzer again and reuse this strategy, no  * delegation is done and the given fallback is used.  */
end_comment
begin_class
DECL|class|DelegatingAnalyzerWrapper
specifier|public
specifier|abstract
class|class
name|DelegatingAnalyzerWrapper
extends|extends
name|AnalyzerWrapper
block|{
comment|/**    * Constructor.    * @param fallbackStrategy is the strategy to use if delegation is not possible    *  This is to support the common pattern:    *  {@code new OtherWrapper(thisWrapper.getReuseStrategy())}     */
DECL|method|DelegatingAnalyzerWrapper
specifier|protected
name|DelegatingAnalyzerWrapper
parameter_list|(
name|ReuseStrategy
name|fallbackStrategy
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|DelegatingReuseStrategy
argument_list|(
name|fallbackStrategy
argument_list|)
argument_list|)
expr_stmt|;
comment|// hÃ¤ckidy-hick-hack, because we cannot call super() with a reference to "this":
operator|(
operator|(
name|DelegatingReuseStrategy
operator|)
name|getReuseStrategy
argument_list|()
operator|)
operator|.
name|wrapper
operator|=
name|this
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|wrapComponents
specifier|protected
specifier|final
name|TokenStreamComponents
name|wrapComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
return|return
name|super
operator|.
name|wrapComponents
argument_list|(
name|fieldName
argument_list|,
name|components
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|wrapReader
specifier|protected
specifier|final
name|Reader
name|wrapReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
name|super
operator|.
name|wrapReader
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|class|DelegatingReuseStrategy
specifier|private
specifier|static
specifier|final
class|class
name|DelegatingReuseStrategy
extends|extends
name|ReuseStrategy
block|{
DECL|field|wrapper
name|DelegatingAnalyzerWrapper
name|wrapper
decl_stmt|;
DECL|field|fallbackStrategy
specifier|private
specifier|final
name|ReuseStrategy
name|fallbackStrategy
decl_stmt|;
DECL|method|DelegatingReuseStrategy
name|DelegatingReuseStrategy
parameter_list|(
name|ReuseStrategy
name|fallbackStrategy
parameter_list|)
block|{
name|this
operator|.
name|fallbackStrategy
operator|=
name|fallbackStrategy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReusableComponents
specifier|public
name|TokenStreamComponents
name|getReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|analyzer
operator|==
name|wrapper
condition|)
block|{
specifier|final
name|Analyzer
name|wrappedAnalyzer
init|=
name|wrapper
operator|.
name|getWrappedAnalyzer
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
name|wrappedAnalyzer
operator|.
name|getReuseStrategy
argument_list|()
operator|.
name|getReusableComponents
argument_list|(
name|wrappedAnalyzer
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|fallbackStrategy
operator|.
name|getReusableComponents
argument_list|(
name|analyzer
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setReusableComponents
specifier|public
name|void
name|setReusableComponents
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
if|if
condition|(
name|analyzer
operator|==
name|wrapper
condition|)
block|{
specifier|final
name|Analyzer
name|wrappedAnalyzer
init|=
name|wrapper
operator|.
name|getWrappedAnalyzer
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|wrappedAnalyzer
operator|.
name|getReuseStrategy
argument_list|()
operator|.
name|setReusableComponents
argument_list|(
name|wrappedAnalyzer
argument_list|,
name|fieldName
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fallbackStrategy
operator|.
name|setReusableComponents
argument_list|(
name|analyzer
argument_list|,
name|fieldName
argument_list|,
name|components
argument_list|)
expr_stmt|;
block|}
block|}
block|}
empty_stmt|;
block|}
end_class
end_unit