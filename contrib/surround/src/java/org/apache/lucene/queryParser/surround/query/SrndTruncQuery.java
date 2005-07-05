begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|index
operator|.
name|TermEnum
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
name|index
operator|.
name|IndexReader
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
name|regex
operator|.
name|Pattern
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
begin_class
DECL|class|SrndTruncQuery
specifier|public
class|class
name|SrndTruncQuery
extends|extends
name|SimpleTerm
block|{
DECL|method|SrndTruncQuery
specifier|public
name|SrndTruncQuery
parameter_list|(
name|String
name|truncated
parameter_list|,
name|char
name|unlimited
parameter_list|,
name|char
name|mask
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|/* not quoted */
name|this
operator|.
name|truncated
operator|=
name|truncated
expr_stmt|;
name|this
operator|.
name|unlimited
operator|=
name|unlimited
expr_stmt|;
name|this
operator|.
name|mask
operator|=
name|mask
expr_stmt|;
name|truncatedToPrefixAndPattern
argument_list|()
expr_stmt|;
block|}
DECL|field|truncated
specifier|private
specifier|final
name|String
name|truncated
decl_stmt|;
DECL|field|unlimited
specifier|private
specifier|final
name|char
name|unlimited
decl_stmt|;
DECL|field|mask
specifier|private
specifier|final
name|char
name|mask
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|pattern
specifier|private
name|Pattern
name|pattern
decl_stmt|;
DECL|method|getTruncated
specifier|public
name|String
name|getTruncated
parameter_list|()
block|{
return|return
name|truncated
return|;
block|}
DECL|method|toStringUnquoted
specifier|public
name|String
name|toStringUnquoted
parameter_list|()
block|{
return|return
name|getTruncated
argument_list|()
return|;
block|}
DECL|method|matchingChar
specifier|protected
name|boolean
name|matchingChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
operator|(
name|c
operator|!=
name|unlimited
operator|)
operator|&&
operator|(
name|c
operator|!=
name|mask
operator|)
return|;
block|}
DECL|method|appendRegExpForChar
specifier|protected
name|void
name|appendRegExpForChar
parameter_list|(
name|char
name|c
parameter_list|,
name|StringBuffer
name|re
parameter_list|)
block|{
if|if
condition|(
name|c
operator|==
name|unlimited
condition|)
name|re
operator|.
name|append
argument_list|(
literal|".*"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|==
name|mask
condition|)
name|re
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
else|else
name|re
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|truncatedToPrefixAndPattern
specifier|protected
name|void
name|truncatedToPrefixAndPattern
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|i
operator|<
name|truncated
operator|.
name|length
argument_list|()
operator|)
operator|&&
name|matchingChar
argument_list|(
name|truncated
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|prefix
operator|=
name|truncated
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|StringBuffer
name|re
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|truncated
operator|.
name|length
argument_list|()
condition|)
block|{
name|appendRegExpForChar
argument_list|(
name|truncated
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|re
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|re
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|visitMatchingTerms
specifier|public
name|void
name|visitMatchingTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|MatchingTermVisitor
name|mtv
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|expanded
init|=
literal|false
decl_stmt|;
name|int
name|prefixLength
init|=
name|prefix
operator|.
name|length
argument_list|()
decl_stmt|;
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|prefix
argument_list|)
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
name|String
name|text
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|text
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
operator|)
operator|||
operator|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|)
condition|)
block|{
break|break;
block|}
else|else
block|{
name|matcher
operator|.
name|reset
argument_list|(
name|text
operator|.
name|substring
argument_list|(
name|prefixLength
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|mtv
operator|.
name|visitMatchingTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|expanded
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
name|matcher
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|expanded
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No terms in "
operator|+
name|fieldName
operator|+
literal|" field for: "
operator|+
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
