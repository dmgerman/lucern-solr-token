begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cn
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|*
import|;
end_import
begin_comment
comment|/**  * Title: ChineseFilter  * Description: Filter with a stop word table  *              Rule: No digital is allowed.  *                    English word/token should larger than 1 character.  *                    One Chinese character as one Chinese word.  * TO DO:  *   1. Add Chinese stop words, such as \ue400  *   2. Dictionary based Chinese word extraction  *   3. Intelligent Chinese word extraction  *  * Copyright:    Copyright (c) 2001  * Company:  * @author Yiyi Sun  * @version 1.0  *  */
end_comment
begin_class
DECL|class|ChineseFilter
specifier|public
specifier|final
class|class
name|ChineseFilter
extends|extends
name|TokenFilter
block|{
comment|// Only English now, Chinese to be added later.
DECL|field|STOP_WORDS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|STOP_WORDS
init|=
block|{
literal|"and"
block|,
literal|"are"
block|,
literal|"as"
block|,
literal|"at"
block|,
literal|"be"
block|,
literal|"but"
block|,
literal|"by"
block|,
literal|"for"
block|,
literal|"if"
block|,
literal|"in"
block|,
literal|"into"
block|,
literal|"is"
block|,
literal|"it"
block|,
literal|"no"
block|,
literal|"not"
block|,
literal|"of"
block|,
literal|"on"
block|,
literal|"or"
block|,
literal|"such"
block|,
literal|"that"
block|,
literal|"the"
block|,
literal|"their"
block|,
literal|"then"
block|,
literal|"there"
block|,
literal|"these"
block|,
literal|"they"
block|,
literal|"this"
block|,
literal|"to"
block|,
literal|"was"
block|,
literal|"will"
block|,
literal|"with"
block|}
decl_stmt|;
DECL|field|stopTable
specifier|private
name|Hashtable
name|stopTable
decl_stmt|;
DECL|method|ChineseFilter
specifier|public
name|ChineseFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|stopTable
operator|=
operator|new
name|Hashtable
argument_list|(
name|STOP_WORDS
operator|.
name|length
argument_list|)
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
name|STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|stopTable
operator|.
name|put
argument_list|(
name|STOP_WORDS
index|[
name|i
index|]
argument_list|,
name|STOP_WORDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
for|for
control|(
name|Token
name|token
init|=
name|input
operator|.
name|next
argument_list|()
init|;
name|token
operator|!=
literal|null
condition|;
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
control|)
block|{
name|String
name|text
init|=
name|token
operator|.
name|termText
argument_list|()
decl_stmt|;
comment|// why not key off token type here assuming ChineseTokenizer comes first?
if|if
condition|(
name|stopTable
operator|.
name|get
argument_list|(
name|text
argument_list|)
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|Character
operator|.
name|getType
argument_list|(
name|text
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
case|case
name|Character
operator|.
name|LOWERCASE_LETTER
case|:
case|case
name|Character
operator|.
name|UPPERCASE_LETTER
case|:
comment|// English word/token should larger than 1 character.
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
return|return
name|token
return|;
block|}
break|break;
case|case
name|Character
operator|.
name|OTHER_LETTER
case|:
comment|// One Chinese character as one Chinese word.
comment|// Chinese word extraction to be added later here.
return|return
name|token
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
