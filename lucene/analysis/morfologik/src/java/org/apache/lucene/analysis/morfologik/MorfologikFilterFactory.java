begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.morfologik
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|morfologik
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|morfologik
operator|.
name|stemming
operator|.
name|PolishStemmer
operator|.
name|DICTIONARY
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
name|morfologik
operator|.
name|MorfologikFilter
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
comment|/**  * Filter factory for {@link MorfologikFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_polish" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.MorfologikFilterFactory" dictionary="MORFOLOGIK" /&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   *<p>Any of Morfologik dictionaries can be used, these are at the moment:  *<code>MORFOLOGIK</code> (Morfologik's original dictionary),  *<code>MORFEUSZ</code> (Morfeusz-SIAT),  *<code>COMBINED</code> (both of the dictionaries above, combined).  *   * @see<a href="http://morfologik.blogspot.com/">Morfologik web site</a>  */
end_comment
begin_class
DECL|class|MorfologikFilterFactory
specifier|public
class|class
name|MorfologikFilterFactory
extends|extends
name|TokenFilterFactory
block|{
comment|/** Dictionary. */
DECL|field|dictionary
specifier|private
name|DICTIONARY
name|dictionary
init|=
name|DICTIONARY
operator|.
name|MORFOLOGIK
decl_stmt|;
comment|/** Schema attribute. */
DECL|field|DICTIONARY_SCHEMA_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|DICTIONARY_SCHEMA_ATTRIBUTE
init|=
literal|"dictionary"
decl_stmt|;
comment|/** Creates a new MorfologikFilterFactory */
DECL|method|MorfologikFilterFactory
specifier|public
name|MorfologikFilterFactory
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
name|String
name|dictionaryName
init|=
name|args
operator|.
name|remove
argument_list|(
name|DICTIONARY_SCHEMA_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|dictionaryName
operator|!=
literal|null
operator|&&
operator|!
name|dictionaryName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|DICTIONARY
name|dictionary
init|=
name|DICTIONARY
operator|.
name|valueOf
argument_list|(
name|dictionaryName
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|dictionary
operator|!=
literal|null
assert|;
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The "
operator|+
name|DICTIONARY_SCHEMA_ATTRIBUTE
operator|+
literal|" attribute accepts the "
operator|+
literal|"following constants: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|DICTIONARY
operator|.
name|values
argument_list|()
argument_list|)
operator|+
literal|", this value is invalid: "
operator|+
name|dictionaryName
argument_list|)
throw|;
block|}
block|}
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
name|ts
parameter_list|)
block|{
return|return
operator|new
name|MorfologikFilter
argument_list|(
name|ts
argument_list|,
name|dictionary
argument_list|,
name|luceneMatchVersion
argument_list|)
return|;
block|}
block|}
end_class
end_unit
