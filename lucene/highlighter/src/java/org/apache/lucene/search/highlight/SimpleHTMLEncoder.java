begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Simple {@link Encoder} implementation to escape text for HTML output  *  */
end_comment
begin_class
DECL|class|SimpleHTMLEncoder
specifier|public
class|class
name|SimpleHTMLEncoder
implements|implements
name|Encoder
block|{
DECL|method|SimpleHTMLEncoder
specifier|public
name|SimpleHTMLEncoder
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|encodeText
specifier|public
name|String
name|encodeText
parameter_list|(
name|String
name|originalText
parameter_list|)
block|{
return|return
name|htmlEncode
argument_list|(
name|originalText
argument_list|)
return|;
block|}
comment|/**    * Encode string into HTML    */
DECL|method|htmlEncode
specifier|public
specifier|final
specifier|static
name|String
name|htmlEncode
parameter_list|(
name|String
name|plainText
parameter_list|)
block|{
if|if
condition|(
name|plainText
operator|==
literal|null
operator|||
name|plainText
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|(
name|plainText
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|plainText
operator|.
name|length
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|char
name|ch
init|=
name|plainText
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'"'
case|:
name|result
operator|.
name|append
argument_list|(
literal|"&quot;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'&'
case|:
name|result
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
name|result
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
name|result
operator|.
name|append
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\''
case|:
name|result
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
name|result
operator|.
name|append
argument_list|(
literal|"&#x2F;"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|result
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
