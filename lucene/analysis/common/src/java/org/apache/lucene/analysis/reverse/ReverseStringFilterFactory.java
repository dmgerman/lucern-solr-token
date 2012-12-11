begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.reverse
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|reverse
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
name|reverse
operator|.
name|ReverseStringFilter
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
comment|/**  * Factory for {@link ReverseStringFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_rvsstr" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.ReverseStringFilterFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|ReverseStringFilterFactory
specifier|public
class|class
name|ReverseStringFilterFactory
extends|extends
name|TokenFilterFactory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|ReverseStringFilter
name|create
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
return|return
operator|new
name|ReverseStringFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|in
argument_list|)
return|;
block|}
block|}
end_class
end_unit
