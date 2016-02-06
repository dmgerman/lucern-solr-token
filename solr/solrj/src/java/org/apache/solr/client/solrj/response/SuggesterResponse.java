begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
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
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_comment
comment|/**  * Encapsulates responses from the Suggester Component  */
end_comment
begin_class
DECL|class|SuggesterResponse
specifier|public
class|class
name|SuggesterResponse
block|{
DECL|field|SUGGESTIONS_NODE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SUGGESTIONS_NODE_NAME
init|=
literal|"suggestions"
decl_stmt|;
DECL|field|TERM_NODE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TERM_NODE_NAME
init|=
literal|"term"
decl_stmt|;
DECL|field|WEIGHT_NODE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|WEIGHT_NODE_NAME
init|=
literal|"weight"
decl_stmt|;
DECL|field|PAYLOAD_NODE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|PAYLOAD_NODE_NAME
init|=
literal|"payload"
decl_stmt|;
DECL|field|suggestionsPerDictionary
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Suggestion
argument_list|>
argument_list|>
name|suggestionsPerDictionary
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|SuggesterResponse
specifier|public
name|SuggesterResponse
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|suggestInfo
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|suggestInfo
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SimpleOrderedMap
name|suggestionsNode
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|>
name|suggestionListToParse
decl_stmt|;
name|List
argument_list|<
name|Suggestion
argument_list|>
name|suggestionList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|suggestionsNode
operator|!=
literal|null
condition|)
block|{
name|suggestionListToParse
operator|=
operator|(
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|>
operator|)
name|suggestionsNode
operator|.
name|get
argument_list|(
name|SUGGESTIONS_NODE_NAME
argument_list|)
expr_stmt|;
for|for
control|(
name|SimpleOrderedMap
name|suggestion
range|:
name|suggestionListToParse
control|)
block|{
name|String
name|term
init|=
operator|(
name|String
operator|)
name|suggestion
operator|.
name|get
argument_list|(
name|TERM_NODE_NAME
argument_list|)
decl_stmt|;
name|long
name|weight
init|=
operator|(
name|long
operator|)
name|suggestion
operator|.
name|get
argument_list|(
name|WEIGHT_NODE_NAME
argument_list|)
decl_stmt|;
name|String
name|payload
init|=
operator|(
name|String
operator|)
name|suggestion
operator|.
name|get
argument_list|(
name|PAYLOAD_NODE_NAME
argument_list|)
decl_stmt|;
name|Suggestion
name|parsedSuggestion
init|=
operator|new
name|Suggestion
argument_list|(
name|term
argument_list|,
name|weight
argument_list|,
name|payload
argument_list|)
decl_stmt|;
name|suggestionList
operator|.
name|add
argument_list|(
name|parsedSuggestion
argument_list|)
expr_stmt|;
block|}
name|suggestionsPerDictionary
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|suggestionList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * get the suggestions provided by each    *    * @return a Map dictionary name : List of Suggestion    */
DECL|method|getSuggestions
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Suggestion
argument_list|>
argument_list|>
name|getSuggestions
parameter_list|()
block|{
return|return
name|suggestionsPerDictionary
return|;
block|}
comment|/**    * This getter is lazily initialized and returns a simplified map dictionary : List of suggested terms    * This is useful for simple use cases when you simply need the suggested terms and no weight or payload    *    * @return a Map dictionary name : List of suggested terms    */
DECL|method|getSuggestedTerms
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getSuggestedTerms
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|suggestedTermsPerDictionary
init|=
operator|new
name|LinkedHashMap
argument_list|<>
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
name|List
argument_list|<
name|Suggestion
argument_list|>
argument_list|>
name|entry
range|:
name|suggestionsPerDictionary
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Suggestion
argument_list|>
name|suggestions
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|suggestionTerms
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Suggestion
name|s
range|:
name|suggestions
control|)
block|{
name|suggestionTerms
operator|.
name|add
argument_list|(
name|s
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|suggestedTermsPerDictionary
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|suggestionTerms
argument_list|)
expr_stmt|;
block|}
return|return
name|suggestedTermsPerDictionary
return|;
block|}
block|}
end_class
end_unit
