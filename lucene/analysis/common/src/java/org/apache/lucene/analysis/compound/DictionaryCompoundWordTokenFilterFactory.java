begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|*
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**   * Factory for {@link DictionaryCompoundWordTokenFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_dictcomp" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.DictionaryCompoundWordTokenFilterFactory" dictionary="dictionary.txt"  *         minWordSize="5" minSubwordSize="2" maxSubwordSize="15" onlyLongestMatch="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment
begin_class
DECL|class|DictionaryCompoundWordTokenFilterFactory
specifier|public
class|class
name|DictionaryCompoundWordTokenFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|dictionary
specifier|private
name|CharArraySet
name|dictionary
decl_stmt|;
DECL|field|dictFile
specifier|private
specifier|final
name|String
name|dictFile
decl_stmt|;
DECL|field|minWordSize
specifier|private
specifier|final
name|int
name|minWordSize
decl_stmt|;
DECL|field|minSubwordSize
specifier|private
specifier|final
name|int
name|minSubwordSize
decl_stmt|;
DECL|field|maxSubwordSize
specifier|private
specifier|final
name|int
name|maxSubwordSize
decl_stmt|;
DECL|field|onlyLongestMatch
specifier|private
specifier|final
name|boolean
name|onlyLongestMatch
decl_stmt|;
comment|/** Creates a new DictionaryCompoundWordTokenFilterFactory */
DECL|method|DictionaryCompoundWordTokenFilterFactory
specifier|public
name|DictionaryCompoundWordTokenFilterFactory
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
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|dictFile
operator|=
name|args
operator|.
name|remove
argument_list|(
literal|"dictionary"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|dictFile
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing required parameter: dictionary"
argument_list|)
throw|;
block|}
name|minWordSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"minWordSize"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|)
expr_stmt|;
name|minSubwordSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"minSubwordSize"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|)
expr_stmt|;
name|maxSubwordSize
operator|=
name|getInt
argument_list|(
name|args
argument_list|,
literal|"maxSubwordSize"
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|)
expr_stmt|;
name|onlyLongestMatch
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
literal|"onlyLongestMatch"
argument_list|,
literal|true
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
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|dictionary
operator|=
name|super
operator|.
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|dictFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
comment|// if the dictionary is null, it means it was empty
return|return
name|dictionary
operator|==
literal|null
condition|?
name|input
else|:
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|minWordSize
argument_list|,
name|minSubwordSize
argument_list|,
name|maxSubwordSize
argument_list|,
name|onlyLongestMatch
argument_list|)
return|;
block|}
block|}
end_class
end_unit
