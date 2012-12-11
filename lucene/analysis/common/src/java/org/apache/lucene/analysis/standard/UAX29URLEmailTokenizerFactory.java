begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|standard
operator|.
name|UAX29URLEmailTokenizer
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
name|TokenizerFactory
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
comment|/**  * Factory for {@link UAX29URLEmailTokenizer}.   *<pre class="prettyprint">  *&lt;fieldType name="text_urlemail" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.UAX29URLEmailTokenizerFactory" maxTokenLength="255"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>   *  *   */
end_comment
begin_class
DECL|class|UAX29URLEmailTokenizerFactory
specifier|public
class|class
name|UAX29URLEmailTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
decl_stmt|;
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
name|maxTokenLength
operator|=
name|getInt
argument_list|(
literal|"maxTokenLength"
argument_list|,
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|UAX29URLEmailTokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|UAX29URLEmailTokenizer
name|tokenizer
init|=
operator|new
name|UAX29URLEmailTokenizer
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
return|return
name|tokenizer
return|;
block|}
block|}
end_class
end_unit
