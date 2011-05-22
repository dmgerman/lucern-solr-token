begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
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
name|util
operator|.
name|Random
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
name|automaton
operator|.
name|CharacterRunAutomaton
import|;
end_import
begin_comment
comment|/**  * Analyzer for testing  *<p>  * This analyzer is a replacement for Whitespace/Simple/KeywordAnalyzers  * for unit tests. If you are testing a custom component such as a queryparser  * or analyzer-wrapper that consumes analysis streams, its a great idea to test  * it with this analyzer instead. MockAnalyzer has the following behavior:  *<ul>  *<li>By default, the assertions in {@link MockTokenizer} are turned on for extra  *       checks that the consumer is consuming properly. These checks can be disabled  *       with {@link #setEnableChecks(boolean)}.  *<li>Payload data is randomly injected into the stream for more thorough testing  *       of payloads.  *</ul>  * @see MockTokenizer  */
end_comment
begin_class
DECL|class|MockAnalyzer
specifier|public
specifier|final
class|class
name|MockAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|runAutomaton
specifier|private
specifier|final
name|CharacterRunAutomaton
name|runAutomaton
decl_stmt|;
DECL|field|lowerCase
specifier|private
specifier|final
name|boolean
name|lowerCase
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|CharacterRunAutomaton
name|filter
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
specifier|final
name|boolean
name|enablePositionIncrements
decl_stmt|;
DECL|field|positionIncrementGap
specifier|private
name|int
name|positionIncrementGap
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|previousMappings
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|previousMappings
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|enableChecks
specifier|private
name|boolean
name|enableChecks
init|=
literal|true
decl_stmt|;
comment|/**    * Creates a new MockAnalyzer.    *     * @param random Random for payloads behavior    * @param runAutomaton DFA describing how tokenization should happen (e.g. [a-zA-Z]+)    * @param lowerCase true if the tokenizer should lowercase terms    * @param filter DFA describing how terms should be filtered (set of stopwords, etc)    * @param enablePositionIncrements true if position increments should reflect filtered terms.    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|Random
name|random
parameter_list|,
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|,
name|CharacterRunAutomaton
name|filter
parameter_list|,
name|boolean
name|enablePositionIncrements
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|runAutomaton
operator|=
name|runAutomaton
expr_stmt|;
name|this
operator|.
name|lowerCase
operator|=
name|lowerCase
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enablePositionIncrements
expr_stmt|;
block|}
comment|/**    * Calls {@link #MockAnalyzer(Random, CharacterRunAutomaton, boolean, CharacterRunAutomaton, boolean)     * MockAnalyzer(random, runAutomaton, lowerCase, MockTokenFilter.EMPTY_STOPSET, false}).    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|Random
name|random
parameter_list|,
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|)
block|{
name|this
argument_list|(
name|random
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|,
name|MockTokenFilter
operator|.
name|EMPTY_STOPSET
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**     * Create a Whitespace-lowercasing analyzer with no stopwords removal.    *<p>    * Calls {@link #MockAnalyzer(Random, CharacterRunAutomaton, boolean, CharacterRunAutomaton, boolean)     * MockAnalyzer(random, MockTokenizer.WHITESPACE, true, MockTokenFilter.EMPTY_STOPSET, false}).    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|MockTokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
name|enableChecks
argument_list|)
expr_stmt|;
name|TokenFilter
name|filt
init|=
operator|new
name|MockTokenFilter
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|,
name|enablePositionIncrements
argument_list|)
decl_stmt|;
name|filt
operator|=
name|maybePayload
argument_list|(
name|filt
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|filt
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
DECL|field|tokenizer
name|MockTokenizer
name|tokenizer
decl_stmt|;
DECL|field|filter
name|TokenFilter
name|filter
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|SavedStreams
argument_list|>
name|map
init|=
operator|(
name|Map
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SavedStreams
argument_list|>
argument_list|()
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
name|SavedStreams
name|saved
init|=
name|map
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|saved
operator|==
literal|null
condition|)
block|{
name|saved
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|saved
operator|.
name|tokenizer
operator|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|)
expr_stmt|;
name|saved
operator|.
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
name|enableChecks
argument_list|)
expr_stmt|;
name|saved
operator|.
name|filter
operator|=
operator|new
name|MockTokenFilter
argument_list|(
name|saved
operator|.
name|tokenizer
argument_list|,
name|filter
argument_list|,
name|enablePositionIncrements
argument_list|)
expr_stmt|;
name|saved
operator|.
name|filter
operator|=
name|maybePayload
argument_list|(
name|saved
operator|.
name|filter
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|saved
argument_list|)
expr_stmt|;
return|return
name|saved
operator|.
name|filter
return|;
block|}
else|else
block|{
name|saved
operator|.
name|tokenizer
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|saved
operator|.
name|filter
return|;
block|}
block|}
DECL|method|maybePayload
specifier|private
specifier|synchronized
name|TokenFilter
name|maybePayload
parameter_list|(
name|TokenFilter
name|stream
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|Integer
name|val
init|=
name|previousMappings
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|val
operator|=
operator|-
literal|1
expr_stmt|;
comment|// no payloads
break|break;
case|case
literal|1
case|:
name|val
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// variable length payload
break|break;
case|case
literal|2
case|:
name|val
operator|=
name|random
operator|.
name|nextInt
argument_list|(
literal|12
argument_list|)
expr_stmt|;
comment|// fixed length payload
break|break;
block|}
name|previousMappings
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|val
argument_list|)
expr_stmt|;
comment|// save it so we are consistent for this field
block|}
if|if
condition|(
name|val
operator|==
operator|-
literal|1
condition|)
return|return
name|stream
return|;
elseif|else
if|if
condition|(
name|val
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
return|return
operator|new
name|MockVariableLengthPayloadFilter
argument_list|(
name|random
argument_list|,
name|stream
argument_list|)
return|;
else|else
return|return
operator|new
name|MockFixedLengthPayloadFilter
argument_list|(
name|random
argument_list|,
name|stream
argument_list|,
name|val
argument_list|)
return|;
block|}
DECL|method|setPositionIncrementGap
specifier|public
name|void
name|setPositionIncrementGap
parameter_list|(
name|int
name|positionIncrementGap
parameter_list|)
block|{
name|this
operator|.
name|positionIncrementGap
operator|=
name|positionIncrementGap
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|positionIncrementGap
return|;
block|}
comment|/**     * Toggle consumer workflow checking: if your test consumes tokenstreams normally you    * should leave this enabled.    */
DECL|method|setEnableChecks
specifier|public
name|void
name|setEnableChecks
parameter_list|(
name|boolean
name|enableChecks
parameter_list|)
block|{
name|this
operator|.
name|enableChecks
operator|=
name|enableChecks
expr_stmt|;
block|}
block|}
end_class
end_unit
