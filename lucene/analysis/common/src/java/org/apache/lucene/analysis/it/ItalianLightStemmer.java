begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.it
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|it
package|;
end_package
begin_comment
comment|/*   * This algorithm is updated based on code located at:  * http://members.unine.ch/jacques.savoy/clef/  *   * Full copyright for that code follows:  */
end_comment
begin_comment
comment|/*  * Copyright (c) 2005, Jacques Savoy  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without   * modification, are permitted provided that the following conditions are met:  *  * Redistributions of source code must retain the above copyright notice, this   * list of conditions and the following disclaimer. Redistributions in binary   * form must reproduce the above copyright notice, this list of conditions and  * the following disclaimer in the documentation and/or other materials   * provided with the distribution. Neither the name of the author nor the names   * of its contributors may be used to endorse or promote products derived from   * this software without specific prior written permission.  *   * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"   * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE   * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE   * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE   * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR   * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF   * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS   * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN   * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)   * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  * POSSIBILITY OF SUCH DAMAGE.  */
end_comment
begin_comment
comment|/**  * Light Stemmer for Italian.  *<p>  * This stemmer implements the algorithm described in:  *<i>Report on CLEF-2001 Experiments</i>  * Jacques Savoy  */
end_comment
begin_class
DECL|class|ItalianLightStemmer
specifier|public
class|class
name|ItalianLightStemmer
block|{
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|<
literal|6
condition|)
return|return
name|len
return|;
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
switch|switch
condition|(
name|s
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'Ã '
case|:
case|case
literal|'Ã¡'
case|:
case|case
literal|'Ã¢'
case|:
case|case
literal|'Ã¤'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
break|break;
case|case
literal|'Ã²'
case|:
case|case
literal|'Ã³'
case|:
case|case
literal|'Ã´'
case|:
case|case
literal|'Ã¶'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'Ã¨'
case|:
case|case
literal|'Ã©'
case|:
case|case
literal|'Ãª'
case|:
case|case
literal|'Ã«'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'Ã¹'
case|:
case|case
literal|'Ãº'
case|:
case|case
literal|'Ã»'
case|:
case|case
literal|'Ã¼'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'Ã¬'
case|:
case|case
literal|'Ã­'
case|:
case|case
literal|'Ã®'
case|:
case|case
literal|'Ã¯'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
block|}
switch|switch
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
condition|)
block|{
case|case
literal|'e'
case|:
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'i'
operator|||
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'h'
condition|)
return|return
name|len
operator|-
literal|2
return|;
else|else
return|return
name|len
operator|-
literal|1
return|;
case|case
literal|'i'
case|:
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'h'
operator|||
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'i'
condition|)
return|return
name|len
operator|-
literal|2
return|;
else|else
return|return
name|len
operator|-
literal|1
return|;
case|case
literal|'a'
case|:
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'i'
condition|)
return|return
name|len
operator|-
literal|2
return|;
else|else
return|return
name|len
operator|-
literal|1
return|;
case|case
literal|'o'
case|:
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'i'
condition|)
return|return
name|len
operator|-
literal|2
return|;
else|else
return|return
name|len
operator|-
literal|1
return|;
block|}
return|return
name|len
return|;
block|}
block|}
end_class
end_unit
