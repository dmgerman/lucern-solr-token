begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
comment|/**  * Factory for {@link CodepointCountFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_lngth" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.CodepointCountFilterFactory" min="0" max="1" /&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment
begin_class
DECL|class|CodepointCountFilterFactory
specifier|public
class|class
name|CodepointCountFilterFactory
extends|extends
name|TokenFilterFactory
block|{
DECL|field|min
specifier|final
name|int
name|min
decl_stmt|;
DECL|field|max
specifier|final
name|int
name|max
decl_stmt|;
DECL|field|MIN_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MIN_KEY
init|=
literal|"min"
decl_stmt|;
DECL|field|MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_KEY
init|=
literal|"max"
decl_stmt|;
comment|/** Creates a new CodepointCountFilterFactory */
DECL|method|CodepointCountFilterFactory
specifier|public
name|CodepointCountFilterFactory
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
name|min
operator|=
name|requireInt
argument_list|(
name|args
argument_list|,
name|MIN_KEY
argument_list|)
expr_stmt|;
name|max
operator|=
name|requireInt
argument_list|(
name|args
argument_list|,
name|MAX_KEY
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
name|CodepointCountFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|CodepointCountFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
block|}
end_class
end_unit
