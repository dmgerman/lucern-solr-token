begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  * Utility class for parsing CSV text  */
end_comment
begin_class
DECL|class|CSVUtil
specifier|public
specifier|final
class|class
name|CSVUtil
block|{
DECL|field|QUOTE
specifier|private
specifier|static
specifier|final
name|char
name|QUOTE
init|=
literal|'"'
decl_stmt|;
DECL|field|COMMA
specifier|private
specifier|static
specifier|final
name|char
name|COMMA
init|=
literal|','
decl_stmt|;
DECL|field|QUOTE_REPLACE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|QUOTE_REPLACE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\"([^\"]+)\"$"
argument_list|)
decl_stmt|;
DECL|field|ESCAPED_QUOTE
specifier|private
specifier|static
specifier|final
name|String
name|ESCAPED_QUOTE
init|=
literal|"\"\""
decl_stmt|;
DECL|method|CSVUtil
specifier|private
name|CSVUtil
parameter_list|()
block|{}
comment|// no instance!!!
comment|/**    * Parse CSV line    * @param line    * @return Array of values    */
DECL|method|parse
specifier|public
specifier|static
name|String
index|[]
name|parse
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|boolean
name|insideQuote
init|=
literal|false
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|quoteCount
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|line
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|line
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
name|QUOTE
condition|)
block|{
name|insideQuote
operator|=
operator|!
name|insideQuote
expr_stmt|;
name|quoteCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|==
name|COMMA
operator|&&
operator|!
name|insideQuote
condition|)
block|{
name|String
name|value
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|value
operator|=
name|unQuoteUnEscape
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Validate
if|if
condition|(
name|quoteCount
operator|%
literal|2
operator|!=
literal|0
condition|)
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|unQuoteUnEscape
specifier|private
specifier|static
name|String
name|unQuoteUnEscape
parameter_list|(
name|String
name|original
parameter_list|)
block|{
name|String
name|result
init|=
name|original
decl_stmt|;
comment|// Unquote
if|if
condition|(
name|result
operator|.
name|indexOf
argument_list|(
literal|'\"'
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|Matcher
name|m
init|=
name|QUOTE_REPLACE_PATTERN
operator|.
name|matcher
argument_list|(
name|original
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|result
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Unescape
if|if
condition|(
name|result
operator|.
name|indexOf
argument_list|(
name|ESCAPED_QUOTE
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
name|ESCAPED_QUOTE
argument_list|,
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Quote and escape input value for CSV    * @param original    */
DECL|method|quoteEscape
specifier|public
specifier|static
name|String
name|quoteEscape
parameter_list|(
name|String
name|original
parameter_list|)
block|{
name|String
name|result
init|=
name|original
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|indexOf
argument_list|(
literal|'\"'
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|result
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
name|ESCAPED_QUOTE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|indexOf
argument_list|(
name|COMMA
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|result
operator|=
literal|"\""
operator|+
name|result
operator|+
literal|"\""
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
