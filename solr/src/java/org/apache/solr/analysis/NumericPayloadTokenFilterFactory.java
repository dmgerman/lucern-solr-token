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
name|payloads
operator|.
name|NumericPayloadTokenFilter
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**   * Factory for {@link NumericPayloadTokenFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_numpayload" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.NumericPayloadTokenFilterFactory" payload="24" typeMatch="word"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment
begin_class
DECL|class|NumericPayloadTokenFilterFactory
specifier|public
class|class
name|NumericPayloadTokenFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|payload
specifier|private
name|float
name|payload
decl_stmt|;
DECL|field|typeMatch
specifier|private
name|String
name|typeMatch
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
name|payload
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|"payload"
argument_list|)
argument_list|)
expr_stmt|;
name|typeMatch
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"typeMatch"
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|NumericPayloadTokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|NumericPayloadTokenFilter
argument_list|(
name|input
argument_list|,
name|payload
argument_list|,
name|typeMatch
argument_list|)
return|;
block|}
block|}
end_class
end_unit
