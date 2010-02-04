begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hi
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hi
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Normalizer for Hindi.  *<p>  * Normalizes text to remove some differences in spelling variations.  *<p>  * Implements the Hindi-language specific algorithm specified in:  *<i>Word normalization in Indian languages</i>  * Prasad Pingali and Vasudeva Varma.  * http://web2py.iiit.ac.in/publications/default/download/inproceedings.pdf.3fe5b38c-02ee-41ce-9a8f-3e745670be32.pdf  *<p>  * with the following additions from<i>Hindi CLIR in Thirty Days</i>  * Leah S. Larkey, Margaret E. Connell, and Nasreen AbdulJaleel.  * http://maroo.cs.umass.edu/pub/web/getpdf.php?id=454:  *<ul>  *<li>Internal Zero-width joiner and Zero-width non-joiners are removed  *<li>In addition to chandrabindu, NA+halant is normalized to anusvara  *</ul>  *   */
end_comment
begin_class
DECL|class|HindiNormalizer
specifier|public
class|class
name|HindiNormalizer
block|{
comment|/**    * Normalize an input buffer of Hindi text    *     * @param s input buffer    * @param len length of input buffer    * @return length of input buffer after normalization    */
DECL|method|normalize
specifier|public
name|int
name|normalize
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|s
index|[
name|i
index|]
condition|)
block|{
comment|// dead n -> bindu
case|case
literal|'\u0928'
case|:
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|len
operator|&&
name|s
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'\u094D'
condition|)
block|{
name|s
index|[
name|i
index|]
operator|=
literal|'\u0902'
expr_stmt|;
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
break|break;
comment|// candrabindu -> bindu
case|case
literal|'\u0901'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0902'
expr_stmt|;
break|break;
comment|// nukta deletions
case|case
literal|'\u093C'
case|:
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
break|break;
case|case
literal|'\u0929'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0928'
expr_stmt|;
break|break;
case|case
literal|'\u0931'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0930'
expr_stmt|;
break|break;
case|case
literal|'\u0934'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0933'
expr_stmt|;
break|break;
case|case
literal|'\u0958'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0915'
expr_stmt|;
break|break;
case|case
literal|'\u0959'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0916'
expr_stmt|;
break|break;
case|case
literal|'\u095A'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0917'
expr_stmt|;
break|break;
case|case
literal|'\u095B'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u091C'
expr_stmt|;
break|break;
case|case
literal|'\u095C'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0921'
expr_stmt|;
break|break;
case|case
literal|'\u095D'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0922'
expr_stmt|;
break|break;
case|case
literal|'\u095E'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u092B'
expr_stmt|;
break|break;
case|case
literal|'\u095F'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u092F'
expr_stmt|;
break|break;
comment|// zwj/zwnj -> delete
case|case
literal|'\u200D'
case|:
case|case
literal|'\u200C'
case|:
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
break|break;
comment|// virama -> delete
case|case
literal|'\u094D'
case|:
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
break|break;
comment|// chandra/short -> replace
case|case
literal|'\u0945'
case|:
case|case
literal|'\u0946'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0947'
expr_stmt|;
break|break;
case|case
literal|'\u0949'
case|:
case|case
literal|'\u094A'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u094B'
expr_stmt|;
break|break;
case|case
literal|'\u090D'
case|:
case|case
literal|'\u090E'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u090F'
expr_stmt|;
break|break;
case|case
literal|'\u0911'
case|:
case|case
literal|'\u0912'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0913'
expr_stmt|;
break|break;
case|case
literal|'\u0972'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0905'
expr_stmt|;
break|break;
comment|// long -> short ind. vowels
case|case
literal|'\u0906'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0905'
expr_stmt|;
break|break;
case|case
literal|'\u0908'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0907'
expr_stmt|;
break|break;
case|case
literal|'\u090A'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0909'
expr_stmt|;
break|break;
case|case
literal|'\u0960'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u090B'
expr_stmt|;
break|break;
case|case
literal|'\u0961'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u090C'
expr_stmt|;
break|break;
case|case
literal|'\u0910'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u090F'
expr_stmt|;
break|break;
case|case
literal|'\u0914'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0913'
expr_stmt|;
break|break;
comment|// long -> short dep. vowels
case|case
literal|'\u0940'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u093F'
expr_stmt|;
break|break;
case|case
literal|'\u0942'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0941'
expr_stmt|;
break|break;
case|case
literal|'\u0944'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0943'
expr_stmt|;
break|break;
case|case
literal|'\u0963'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0962'
expr_stmt|;
break|break;
case|case
literal|'\u0948'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u0947'
expr_stmt|;
break|break;
case|case
literal|'\u094C'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'\u094B'
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
return|return
name|len
return|;
block|}
comment|/**    * Delete a character in-place    *     * @param s Input Buffer    * @param pos Position of character to delete    * @param len length of input buffer    * @return length of input buffer after deletion    */
DECL|method|delete
specifier|protected
name|int
name|delete
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
name|len
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|s
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|s
argument_list|,
name|pos
argument_list|,
name|len
operator|-
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|len
operator|-
literal|1
return|;
block|}
block|}
end_class
end_unit
