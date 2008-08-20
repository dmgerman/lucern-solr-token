begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.th
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|th
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Character
operator|.
name|UnicodeBlock
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
name|TokenFilter
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
name|text
operator|.
name|BreakIterator
import|;
end_import
begin_comment
comment|/**  * TokenFilter that use java.text.BreakIterator to break each   * Token that is Thai into separate Token(s) for each Thai word.  * @author Samphan Raruenrom<samphan@osdev.co.th> for To-Be-One Technology Co., Ltd.  * @version 0.2  */
end_comment
begin_class
DECL|class|ThaiWordFilter
specifier|public
class|class
name|ThaiWordFilter
extends|extends
name|TokenFilter
block|{
DECL|field|breaker
specifier|private
name|BreakIterator
name|breaker
init|=
literal|null
decl_stmt|;
DECL|field|thaiToken
specifier|private
name|Token
name|thaiToken
init|=
literal|null
decl_stmt|;
DECL|method|ThaiWordFilter
specifier|public
name|ThaiWordFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|breaker
operator|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"th"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
if|if
condition|(
name|thaiToken
operator|!=
literal|null
condition|)
block|{
name|int
name|start
init|=
name|breaker
operator|.
name|current
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|breaker
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
block|{
name|reusableToken
operator|.
name|reinit
argument_list|(
name|thaiToken
argument_list|,
name|thaiToken
operator|.
name|termBuffer
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setStartOffset
argument_list|(
name|thaiToken
operator|.
name|startOffset
argument_list|()
operator|+
name|start
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setEndOffset
argument_list|(
name|thaiToken
operator|.
name|endOffset
argument_list|()
operator|+
name|end
argument_list|)
expr_stmt|;
return|return
name|reusableToken
return|;
block|}
name|thaiToken
operator|=
literal|null
expr_stmt|;
block|}
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextToken
operator|==
literal|null
operator|||
name|nextToken
operator|.
name|termLength
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|text
init|=
name|nextToken
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|text
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|!=
name|UnicodeBlock
operator|.
name|THAI
condition|)
block|{
name|nextToken
operator|.
name|setTermBuffer
argument_list|(
name|text
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nextToken
return|;
block|}
name|thaiToken
operator|=
operator|(
name|Token
operator|)
name|nextToken
operator|.
name|clone
argument_list|()
expr_stmt|;
name|breaker
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|int
name|end
init|=
name|breaker
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
block|{
name|nextToken
operator|.
name|setTermBuffer
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|nextToken
operator|.
name|setEndOffset
argument_list|(
name|nextToken
operator|.
name|startOffset
argument_list|()
operator|+
name|end
argument_list|)
expr_stmt|;
return|return
name|nextToken
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
