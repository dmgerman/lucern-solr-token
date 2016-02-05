begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|TokenStream
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
name|util
operator|.
name|FilteringTokenFilter
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
name|CharTermAttribute
import|;
end_import
begin_comment
comment|/**  * Removes words that are too long or too short from the stream.  *<p>  * Note: Length is calculated as the number of UTF-16 code units.  *</p>  */
end_comment
begin_class
DECL|class|LengthFilter
specifier|public
specifier|final
class|class
name|LengthFilter
extends|extends
name|FilteringTokenFilter
block|{
DECL|field|min
specifier|private
specifier|final
name|int
name|min
decl_stmt|;
DECL|field|max
specifier|private
specifier|final
name|int
name|max
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a new {@link LengthFilter}. This will filter out tokens whose    * {@link CharTermAttribute} is either too short ({@link CharTermAttribute#length()}    *&lt; min) or too long ({@link CharTermAttribute#length()}&gt; max).    * @param in      the {@link TokenStream} to consume    * @param min     the minimum length    * @param max     the maximum length    */
DECL|method|LengthFilter
specifier|public
name|LengthFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimum length must be greater than or equal to zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|min
operator|>
name|max
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maximum length must not be greater than minimum length"
argument_list|)
throw|;
block|}
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|()
block|{
specifier|final
name|int
name|len
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
return|return
operator|(
name|len
operator|>=
name|min
operator|&&
name|len
operator|<=
name|max
operator|)
return|;
block|}
block|}
end_class
end_unit
