begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package
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
name|TokenStream
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
name|TokenFilterFactory
import|;
end_import
begin_comment
comment|/**  * Factory for {@link LimitTokenCountFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_lngthcnt" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.LimitTokenCountFilterFactory" maxTokenCount="10" consumeAllTokens="false" /&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *<p>  * The {@code consumeAllTokens} property is optional and defaults to {@code false}.    * See {@link LimitTokenCountFilter} for an explanation of its use.  */
end_comment
begin_class
DECL|class|LimitTokenCountFilterFactory
specifier|public
class|class
name|LimitTokenCountFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|MAX_TOKEN_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TOKEN_COUNT_KEY
init|=
literal|"maxTokenCount"
decl_stmt|;
DECL|field|CONSUME_ALL_TOKENS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CONSUME_ALL_TOKENS_KEY
init|=
literal|"consumeAllTokens"
decl_stmt|;
DECL|field|maxTokenCount
specifier|final
name|int
name|maxTokenCount
decl_stmt|;
DECL|field|consumeAllTokens
specifier|final
name|boolean
name|consumeAllTokens
decl_stmt|;
comment|/** Creates a new LimitTokenCountFilterFactory */
DECL|method|LimitTokenCountFilterFactory
specifier|public
name|LimitTokenCountFilterFactory
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
name|maxTokenCount
operator|=
name|requireInt
argument_list|(
name|args
argument_list|,
name|MAX_TOKEN_COUNT_KEY
argument_list|)
expr_stmt|;
name|consumeAllTokens
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
name|CONSUME_ALL_TOKENS_KEY
argument_list|,
literal|false
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
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|LimitTokenCountFilter
argument_list|(
name|input
argument_list|,
name|maxTokenCount
argument_list|,
name|consumeAllTokens
argument_list|)
return|;
block|}
block|}
end_class
end_unit
