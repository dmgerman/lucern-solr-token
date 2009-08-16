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
name|tokenattributes
operator|.
name|TermAttribute
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|StringTokenizer
import|;
end_import
begin_comment
comment|/**  * A filter to apply normal capitalization rules to Tokens.  It will make the first letter  * capital and the rest lower case.  *<p/>  * This filter is particularly useful to build nice looking facet parameters.  This filter  * is not appropriate if you intend to use a prefix query.  *<p/>  * The factory takes parameters:<br/>  * "onlyFirstWord" - should each word be capitalized or all of the words?<br/>  * "keep" - a keep word list.  Each word that should be kept separated by whitespace.<br/>  * "keepIgnoreCase - true or false.  If true, the keep list will be considered case-insensitive.  * "forceFirstLetter" - Force the first letter to be capitalized even if it is in the keep list<br/>  * "okPrefix" - do not change word capitalization if a word begins with something in this list.  * for example if "McK" is on the okPrefix list, the word "McKinley" should not be changed to  * "Mckinley"<br/>  * "minWordLength" - how long the word needs to be to get capitalization applied.  If the  * minWordLength is 3, "and"> "And" but "or" stays "or"<br/>  * "maxWordCount" - if the token contains more then maxWordCount words, the capitalization is  * assumed to be correct.<br/>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|CapitalizationFilterFactory
specifier|public
class|class
name|CapitalizationFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|DEFAULT_MAX_WORD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_WORD_COUNT
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|KEEP
specifier|public
specifier|static
specifier|final
name|String
name|KEEP
init|=
literal|"keep"
decl_stmt|;
DECL|field|KEEP_IGNORE_CASE
specifier|public
specifier|static
specifier|final
name|String
name|KEEP_IGNORE_CASE
init|=
literal|"keepIgnoreCase"
decl_stmt|;
DECL|field|OK_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OK_PREFIX
init|=
literal|"okPrefix"
decl_stmt|;
DECL|field|MIN_WORD_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MIN_WORD_LENGTH
init|=
literal|"minWordLength"
decl_stmt|;
DECL|field|MAX_WORD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MAX_WORD_COUNT
init|=
literal|"maxWordCount"
decl_stmt|;
DECL|field|MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TOKEN_LENGTH
init|=
literal|"maxTokenLength"
decl_stmt|;
DECL|field|ONLY_FIRST_WORD
specifier|public
specifier|static
specifier|final
name|String
name|ONLY_FIRST_WORD
init|=
literal|"onlyFirstWord"
decl_stmt|;
DECL|field|FORCE_FIRST_LETTER
specifier|public
specifier|static
specifier|final
name|String
name|FORCE_FIRST_LETTER
init|=
literal|"forceFirstLetter"
decl_stmt|;
comment|//Map<String,String> keep = new HashMap<String, String>(); // not synchronized because it is only initialized once
DECL|field|keep
name|CharArraySet
name|keep
decl_stmt|;
DECL|field|okPrefix
name|Collection
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
comment|// for Example: McK
DECL|field|minWordLength
name|int
name|minWordLength
init|=
literal|0
decl_stmt|;
comment|// don't modify capitalization for words shorter then this
DECL|field|maxWordCount
name|int
name|maxWordCount
init|=
name|DEFAULT_MAX_WORD_COUNT
decl_stmt|;
DECL|field|maxTokenLength
name|int
name|maxTokenLength
init|=
name|DEFAULT_MAX_WORD_COUNT
decl_stmt|;
DECL|field|onlyFirstWord
name|boolean
name|onlyFirstWord
init|=
literal|true
decl_stmt|;
DECL|field|forceFirstLetter
name|boolean
name|forceFirstLetter
init|=
literal|true
decl_stmt|;
comment|// make sure the first letter is capitol even if it is in the keep list
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
name|String
name|k
init|=
name|args
operator|.
name|get
argument_list|(
name|KEEP
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|boolean
name|ignoreCase
init|=
literal|false
decl_stmt|;
name|String
name|ignoreStr
init|=
name|args
operator|.
name|get
argument_list|(
name|KEEP_IGNORE_CASE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|ignoreStr
argument_list|)
condition|)
block|{
name|ignoreCase
operator|=
literal|true
expr_stmt|;
block|}
name|keep
operator|=
operator|new
name|CharArraySet
argument_list|(
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|k
operator|=
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
name|keep
operator|.
name|add
argument_list|(
name|k
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|OK_PREFIX
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|okPrefix
operator|=
operator|new
name|ArrayList
argument_list|<
name|char
index|[]
argument_list|>
argument_list|()
expr_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|k
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|okPrefix
operator|.
name|add
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|MIN_WORD_LENGTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|minWordLength
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|MAX_WORD_COUNT
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|maxWordCount
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|maxTokenLength
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|ONLY_FIRST_WORD
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|onlyFirstWord
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|FORCE_FIRST_LETTER
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|forceFirstLetter
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processWord
specifier|public
name|void
name|processWord
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|wordCount
parameter_list|)
block|{
if|if
condition|(
name|length
operator|<
literal|1
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|onlyFirstWord
operator|&&
name|wordCount
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|offset
operator|+
name|i
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|buffer
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|keep
operator|!=
literal|null
operator|&&
name|keep
operator|.
name|contains
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
condition|)
block|{
if|if
condition|(
name|wordCount
operator|==
literal|0
operator|&&
name|forceFirstLetter
condition|)
block|{
name|buffer
index|[
name|offset
index|]
operator|=
name|Character
operator|.
name|toUpperCase
argument_list|(
name|buffer
index|[
name|offset
index|]
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|length
operator|<
name|minWordLength
condition|)
block|{
return|return;
block|}
for|for
control|(
name|char
index|[]
name|prefix
range|:
name|okPrefix
control|)
block|{
if|if
condition|(
name|length
operator|>=
name|prefix
operator|.
name|length
condition|)
block|{
comment|//don't bother checking if the buffer length is less than the prefix
name|boolean
name|match
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|prefix
index|[
name|i
index|]
operator|!=
name|buffer
index|[
name|offset
operator|+
name|i
index|]
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|match
operator|==
literal|true
condition|)
block|{
return|return;
block|}
block|}
block|}
comment|// We know it has at least one character
comment|/*char[] chars = w.toCharArray();     StringBuilder word = new StringBuilder( w.length() );     word.append( Character.toUpperCase( chars[0] ) );*/
name|buffer
index|[
name|offset
index|]
operator|=
name|Character
operator|.
name|toUpperCase
argument_list|(
name|buffer
index|[
name|offset
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|offset
operator|+
name|i
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|buffer
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|//return word.toString();
block|}
DECL|method|create
specifier|public
name|CapitalizationFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|CapitalizationFilter
argument_list|(
name|input
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class
begin_comment
comment|/**  * This relies on the Factory so that the difficult stuff does not need to be  * re-initialized each time the filter runs.  *<p/>  * This is package protected since it is not useful without the Factory  */
end_comment
begin_class
DECL|class|CapitalizationFilter
class|class
name|CapitalizationFilter
extends|extends
name|TokenFilter
block|{
DECL|field|factory
specifier|private
specifier|final
name|CapitalizationFilterFactory
name|factory
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|CapitalizationFilter
specifier|public
name|CapitalizationFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
specifier|final
name|CapitalizationFilterFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|char
index|[]
name|termBuffer
init|=
name|termAtt
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
name|int
name|termBufferLength
init|=
name|termAtt
operator|.
name|termLength
argument_list|()
decl_stmt|;
name|char
index|[]
name|backup
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|factory
operator|.
name|maxWordCount
operator|<
name|CapitalizationFilterFactory
operator|.
name|DEFAULT_MAX_WORD_COUNT
condition|)
block|{
comment|//make a backup in case we exceed the word count
name|System
operator|.
name|arraycopy
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|backup
argument_list|,
literal|0
argument_list|,
name|termBufferLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|termBufferLength
operator|<
name|factory
operator|.
name|maxTokenLength
condition|)
block|{
name|int
name|wordCount
init|=
literal|0
decl_stmt|;
name|int
name|lastWordStart
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termBufferLength
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|termBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|<=
literal|' '
operator|||
name|c
operator|==
literal|'.'
condition|)
block|{
name|int
name|len
init|=
name|i
operator|-
name|lastWordStart
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
name|lastWordStart
argument_list|,
name|len
argument_list|,
name|wordCount
operator|++
argument_list|)
expr_stmt|;
name|lastWordStart
operator|=
name|i
operator|+
literal|1
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|// process the last word
if|if
condition|(
name|lastWordStart
operator|<
name|termBufferLength
condition|)
block|{
name|factory
operator|.
name|processWord
argument_list|(
name|termBuffer
argument_list|,
name|lastWordStart
argument_list|,
name|termBufferLength
operator|-
name|lastWordStart
argument_list|,
name|wordCount
operator|++
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wordCount
operator|>
name|factory
operator|.
name|maxWordCount
condition|)
block|{
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|backup
argument_list|,
literal|0
argument_list|,
name|termBufferLength
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
