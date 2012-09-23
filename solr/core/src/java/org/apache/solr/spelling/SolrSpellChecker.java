begin_unit
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|Analyzer
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
name|Token
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
name|WhitespaceAnalyzer
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
name|search
operator|.
name|spell
operator|.
name|LevensteinDistance
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
name|search
operator|.
name|spell
operator|.
name|StringDistance
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
name|search
operator|.
name|spell
operator|.
name|SuggestWord
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
name|search
operator|.
name|spell
operator|.
name|SuggestWordQueue
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SpellCheckResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SpellingParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|SpellCheckMergeData
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  *<p>  * Refer to<a href="http://wiki.apache.org/solr/SpellCheckComponent">SpellCheckComponent</a>  * for more details.  *</p>  *   * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrSpellChecker
specifier|public
specifier|abstract
class|class
name|SolrSpellChecker
block|{
DECL|field|DICTIONARY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DICTIONARY_NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|DEFAULT_DICTIONARY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DICTIONARY_NAME
init|=
literal|"default"
decl_stmt|;
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_TYPE
init|=
literal|"fieldType"
decl_stmt|;
comment|/** Dictionary name */
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|analyzer
specifier|protected
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|field|fieldTypeName
specifier|protected
name|String
name|fieldTypeName
decl_stmt|;
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|name
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|DICTIONARY_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
name|DEFAULT_DICTIONARY_NAME
expr_stmt|;
block|}
name|field
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|field
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|analyzer
operator|=
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
operator|.
name|getQueryAnalyzer
argument_list|()
expr_stmt|;
block|}
name|fieldTypeName
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|FIELD_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|containsKey
argument_list|(
name|fieldTypeName
argument_list|)
condition|)
block|{
name|FieldType
name|fieldType
init|=
name|core
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
name|fieldTypeName
argument_list|)
decl_stmt|;
name|analyzer
operator|=
name|fieldType
operator|.
name|getQueryAnalyzer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|luceneMatchVersion
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
comment|/**    * Integrate spelling suggestions from the various shards in a distributed environment.    */
DECL|method|mergeSuggestions
specifier|public
name|SpellingResult
name|mergeSuggestions
parameter_list|(
name|SpellCheckMergeData
name|mergeData
parameter_list|,
name|int
name|numSug
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|extendedResults
parameter_list|)
block|{
name|float
name|min
init|=
literal|0.5f
decl_stmt|;
try|try
block|{
name|min
operator|=
name|getAccuracy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//just use .5 as a default
block|}
name|StringDistance
name|sd
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sd
operator|=
name|getStringDistance
argument_list|()
operator|==
literal|null
condition|?
operator|new
name|LevensteinDistance
argument_list|()
else|:
name|getStringDistance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
name|sd
operator|=
operator|new
name|LevensteinDistance
argument_list|()
expr_stmt|;
block|}
name|SpellingResult
name|result
init|=
operator|new
name|SpellingResult
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|mergeData
operator|.
name|origVsSuggested
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|original
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|//Only use this suggestion if all shards reported it as misspelled.
name|Integer
name|numShards
init|=
name|mergeData
operator|.
name|origVsShards
operator|.
name|get
argument_list|(
name|original
argument_list|)
decl_stmt|;
if|if
condition|(
name|numShards
operator|<
name|mergeData
operator|.
name|totalNumberShardResponses
condition|)
block|{
continue|continue;
block|}
name|HashSet
argument_list|<
name|String
argument_list|>
name|suggested
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|SuggestWordQueue
name|sugQueue
init|=
operator|new
name|SuggestWordQueue
argument_list|(
name|numSug
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|suggestion
range|:
name|suggested
control|)
block|{
name|SuggestWord
name|sug
init|=
name|mergeData
operator|.
name|suggestedVsWord
operator|.
name|get
argument_list|(
name|suggestion
argument_list|)
decl_stmt|;
name|sug
operator|.
name|score
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
name|original
argument_list|,
name|sug
operator|.
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
name|sug
operator|.
name|score
operator|<
name|min
condition|)
continue|continue;
name|sugQueue
operator|.
name|insertWithOverflow
argument_list|(
name|sug
argument_list|)
expr_stmt|;
if|if
condition|(
name|sugQueue
operator|.
name|size
argument_list|()
operator|==
name|numSug
condition|)
block|{
comment|// if queue full, maintain the minScore score
name|min
operator|=
name|sugQueue
operator|.
name|top
argument_list|()
operator|.
name|score
expr_stmt|;
block|}
block|}
comment|// create token
name|SpellCheckResponse
operator|.
name|Suggestion
name|suggestion
init|=
name|mergeData
operator|.
name|origVsSuggestion
operator|.
name|get
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|original
argument_list|,
name|suggestion
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|suggestion
operator|.
name|getEndOffset
argument_list|()
argument_list|)
decl_stmt|;
comment|// get top 'count' suggestions out of 'sugQueue.size()' candidates
name|SuggestWord
index|[]
name|suggestions
init|=
operator|new
name|SuggestWord
index|[
name|Math
operator|.
name|min
argument_list|(
name|count
argument_list|,
name|sugQueue
operator|.
name|size
argument_list|()
argument_list|)
index|]
decl_stmt|;
comment|// skip the first sugQueue.size() - count elements
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|sugQueue
operator|.
name|size
argument_list|()
operator|-
name|count
condition|;
name|k
operator|++
control|)
name|sugQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
comment|// now collect the top 'count' responses
for|for
control|(
name|int
name|k
init|=
name|Math
operator|.
name|min
argument_list|(
name|count
argument_list|,
name|sugQueue
operator|.
name|size
argument_list|()
argument_list|)
operator|-
literal|1
init|;
name|k
operator|>=
literal|0
condition|;
name|k
operator|--
control|)
block|{
name|suggestions
index|[
name|k
index|]
operator|=
name|sugQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|extendedResults
condition|)
block|{
name|Integer
name|o
init|=
name|mergeData
operator|.
name|origVsFreq
operator|.
name|get
argument_list|(
name|original
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
name|result
operator|.
name|addFrequency
argument_list|(
name|token
argument_list|,
name|o
argument_list|)
expr_stmt|;
for|for
control|(
name|SuggestWord
name|word
range|:
name|suggestions
control|)
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|word
operator|.
name|string
argument_list|,
name|word
operator|.
name|freq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|words
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|sugQueue
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SuggestWord
name|word
range|:
name|suggestions
control|)
name|words
operator|.
name|add
argument_list|(
name|word
operator|.
name|string
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|words
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|getDictionaryName
specifier|public
name|String
name|getDictionaryName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Reloads the index.  Useful if an external process is responsible for building the spell checker.    *    * @throws IOException If there is a low-level I/O error.    */
DECL|method|reload
specifier|public
specifier|abstract
name|void
name|reload
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * (re)Builds the spelling index.  May be a NOOP if the implementation doesn't require building, or can't be rebuilt.    */
DECL|method|build
specifier|public
specifier|abstract
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the value of {@link SpellingParams#SPELLCHECK_ACCURACY} if supported.      * Otherwise throws UnsupportedOperationException.    */
DECL|method|getAccuracy
specifier|protected
name|float
name|getAccuracy
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Get the distance implementation used by this spellchecker, or NULL if not applicable.    */
DECL|method|getStringDistance
specifier|protected
name|StringDistance
name|getStringDistance
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Get suggestions for the given query.  Tokenizes the query using a field appropriate Analyzer.    * The {@link SpellingResult#getSuggestions()} suggestions must be ordered by best suggestion first.    *<p/>    *    * @param options The {@link SpellingOptions} to use    * @return The {@link SpellingResult} suggestions    * @throws IOException if there is an error producing suggestions    */
DECL|method|getSuggestions
specifier|public
specifier|abstract
name|SpellingResult
name|getSuggestions
parameter_list|(
name|SpellingOptions
name|options
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|isSuggestionsMayOverlap
specifier|public
name|boolean
name|isSuggestionsMayOverlap
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
