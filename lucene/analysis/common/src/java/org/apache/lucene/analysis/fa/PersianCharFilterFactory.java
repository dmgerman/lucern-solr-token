begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.fa
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fa
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
name|fa
operator|.
name|PersianCharFilter
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
name|util
operator|.
name|AbstractAnalysisFactory
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
name|util
operator|.
name|CharFilterFactory
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
name|util
operator|.
name|MultiTermAwareComponent
import|;
end_import
begin_comment
comment|/**  * Factory for {@link PersianCharFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_fa" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;charFilter class="solr.PersianCharFilterFactory"/&gt;  *&lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment
begin_class
DECL|class|PersianCharFilterFactory
specifier|public
class|class
name|PersianCharFilterFactory
extends|extends
name|CharFilterFactory
implements|implements
name|MultiTermAwareComponent
block|{
comment|/** Creates a new PersianCharFilterFactory */
DECL|method|PersianCharFilterFactory
specifier|public
name|PersianCharFilterFactory
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
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|CharFilter
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|PersianCharFilter
argument_list|(
name|input
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMultiTermComponent
specifier|public
name|AbstractAnalysisFactory
name|getMultiTermComponent
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class
end_unit
