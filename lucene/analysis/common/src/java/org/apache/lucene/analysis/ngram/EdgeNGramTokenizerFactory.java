begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
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
name|util
operator|.
name|TokenizerFactory
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
name|util
operator|.
name|AttributeSource
operator|.
name|AttributeFactory
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
comment|/**  * Creates new instances of {@link EdgeNGramTokenizer}.  *<pre class="prettyprint">  *&lt;fieldType name="text_edgngrm" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.EdgeNGramTokenizerFactory" minGramSize="1" maxGramSize="1"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment
begin_class
DECL|class|EdgeNGramTokenizerFactory
specifier|public
class|class
name|EdgeNGramTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|maxGramSize
specifier|private
specifier|final
name|int
name|maxGramSize
decl_stmt|;
DECL|field|minGramSize
specifier|private
specifier|final
name|int
name|minGramSize
decl_stmt|;
comment|/** Creates a new EdgeNGramTokenizerFactory */
DECL|method|EdgeNGramTokenizerFactory
specifier|public
name|EdgeNGramTokenizerFactory
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
name|minGramSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"minGramSize"
argument_list|,
name|EdgeNGramTokenizer
operator|.
name|DEFAULT_MIN_GRAM_SIZE
argument_list|)
expr_stmt|;
name|maxGramSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"maxGramSize"
argument_list|,
name|EdgeNGramTokenizer
operator|.
name|DEFAULT_MAX_GRAM_SIZE
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
name|EdgeNGramTokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|EdgeNGramTokenizer
argument_list|(
name|luceneMatchVersion
argument_list|,
name|factory
argument_list|,
name|input
argument_list|,
name|minGramSize
argument_list|,
name|maxGramSize
argument_list|)
return|;
block|}
block|}
end_class
end_unit
