begin_unit
begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Creates a formatted snippet from the top passages.  *<p>  * The default implementation marks the query terms as bold, and places  * ellipses between unconnected passages.  */
end_comment
begin_class
DECL|class|DefaultPassageFormatter
specifier|public
class|class
name|DefaultPassageFormatter
extends|extends
name|PassageFormatter
block|{
comment|/** text that will appear before highlighted terms */
DECL|field|preTag
specifier|protected
specifier|final
name|String
name|preTag
decl_stmt|;
comment|/** text that will appear after highlighted terms */
DECL|field|postTag
specifier|protected
specifier|final
name|String
name|postTag
decl_stmt|;
comment|/** text that will appear between two unconnected passages */
DECL|field|ellipsis
specifier|protected
specifier|final
name|String
name|ellipsis
decl_stmt|;
comment|/** true if we should escape for html */
DECL|field|escape
specifier|protected
specifier|final
name|boolean
name|escape
decl_stmt|;
comment|/**    * Creates a new DefaultPassageFormatter with the default tags.    */
DECL|method|DefaultPassageFormatter
specifier|public
name|DefaultPassageFormatter
parameter_list|()
block|{
name|this
argument_list|(
literal|"<b>"
argument_list|,
literal|"</b>"
argument_list|,
literal|"... "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new DefaultPassageFormatter with custom tags.    * @param preTag text which should appear before a highlighted term.    * @param postTag text which should appear after a highlighted term.    * @param ellipsis text which should be used to connect two unconnected passages.    * @param escape true if text should be html-escaped    */
DECL|method|DefaultPassageFormatter
specifier|public
name|DefaultPassageFormatter
parameter_list|(
name|String
name|preTag
parameter_list|,
name|String
name|postTag
parameter_list|,
name|String
name|ellipsis
parameter_list|,
name|boolean
name|escape
parameter_list|)
block|{
if|if
condition|(
name|preTag
operator|==
literal|null
operator|||
name|postTag
operator|==
literal|null
operator|||
name|ellipsis
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
name|this
operator|.
name|preTag
operator|=
name|preTag
expr_stmt|;
name|this
operator|.
name|postTag
operator|=
name|postTag
expr_stmt|;
name|this
operator|.
name|ellipsis
operator|=
name|ellipsis
expr_stmt|;
name|this
operator|.
name|escape
operator|=
name|escape
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|Passage
name|passages
index|[]
parameter_list|,
name|String
name|content
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Passage
name|passage
range|:
name|passages
control|)
block|{
comment|// don't add ellipsis if its the first one, or if its connected.
if|if
condition|(
name|passage
operator|.
name|startOffset
operator|>
name|pos
operator|&&
name|pos
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ellipsis
argument_list|)
expr_stmt|;
block|}
name|pos
operator|=
name|passage
operator|.
name|startOffset
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|passage
operator|.
name|numMatches
condition|;
name|i
operator|++
control|)
block|{
name|int
name|start
init|=
name|passage
operator|.
name|matchStarts
index|[
name|i
index|]
decl_stmt|;
name|int
name|end
init|=
name|passage
operator|.
name|matchEnds
index|[
name|i
index|]
decl_stmt|;
comment|// its possible to have overlapping terms
if|if
condition|(
name|start
operator|>
name|pos
condition|)
block|{
name|append
argument_list|(
name|sb
argument_list|,
name|content
argument_list|,
name|pos
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|end
operator|>
name|pos
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|preTag
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|sb
argument_list|,
name|content
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|pos
argument_list|,
name|start
argument_list|)
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|postTag
argument_list|)
expr_stmt|;
name|pos
operator|=
name|end
expr_stmt|;
block|}
block|}
comment|// its possible a "term" from the analyzer could span a sentence boundary.
name|append
argument_list|(
name|sb
argument_list|,
name|content
argument_list|,
name|pos
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|pos
argument_list|,
name|passage
operator|.
name|endOffset
argument_list|)
argument_list|)
expr_stmt|;
name|pos
operator|=
name|passage
operator|.
name|endOffset
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**     * Appends original text to the response.    * @param dest resulting text, possibly transformed or encoded    * @param content original text content    * @param start index of the first character in content    * @param end index of the character following the last character in content    */
DECL|method|append
specifier|protected
name|void
name|append
parameter_list|(
name|StringBuilder
name|dest
parameter_list|,
name|String
name|content
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
if|if
condition|(
name|escape
condition|)
block|{
comment|// note: these are the rules from owasp.org
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|content
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'&'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'<'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"&lt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'>'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"&quot;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\''
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"&#x27;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'/'
case|:
name|dest
operator|.
name|append
argument_list|(
literal|"&#x2F;"
argument_list|)
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|ch
operator|>=
literal|0x30
operator|&&
name|ch
operator|<=
literal|0x39
operator|||
name|ch
operator|>=
literal|0x41
operator|&&
name|ch
operator|<=
literal|0x5A
operator|||
name|ch
operator|>=
literal|0x61
operator|&&
name|ch
operator|<=
literal|0x7A
condition|)
block|{
name|dest
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|<
literal|0xff
condition|)
block|{
name|dest
operator|.
name|append
argument_list|(
literal|"&#"
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
operator|(
name|int
operator|)
name|ch
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dest
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|dest
operator|.
name|append
argument_list|(
name|content
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
