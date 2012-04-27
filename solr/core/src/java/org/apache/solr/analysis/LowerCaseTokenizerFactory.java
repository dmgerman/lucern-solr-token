begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|analysis
operator|.
name|core
operator|.
name|LowerCaseFilter
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
name|core
operator|.
name|LowerCaseTokenizer
import|;
end_import
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
begin_comment
comment|/**  * Factory for {@link LowerCaseTokenizer}.   *<pre class="prettyprint">  *&lt;fieldType name="text_lwrcase" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.LowerCaseTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>   *  */
end_comment
begin_class
DECL|class|LowerCaseTokenizerFactory
specifier|public
class|class
name|LowerCaseTokenizerFactory
extends|extends
name|BaseTokenizerFactory
implements|implements
name|MultiTermAwareComponent
block|{
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
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
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assureMatchVersion
argument_list|()
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|LowerCaseTokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|LowerCaseTokenizer
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMultiTermComponent
specifier|public
name|Object
name|getMultiTermComponent
parameter_list|()
block|{
name|LowerCaseFilterFactory
name|filt
init|=
operator|new
name|LowerCaseFilterFactory
argument_list|()
decl_stmt|;
name|filt
operator|.
name|setLuceneMatchVersion
argument_list|(
name|luceneMatchVersion
argument_list|)
expr_stmt|;
name|filt
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|filt
return|;
block|}
block|}
end_class
end_unit
