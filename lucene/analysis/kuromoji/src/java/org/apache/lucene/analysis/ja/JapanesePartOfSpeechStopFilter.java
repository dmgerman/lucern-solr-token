begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ja
operator|.
name|tokenattributes
operator|.
name|PartOfSpeechAttribute
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
name|TokenStream
import|;
end_import
begin_comment
comment|/**  * Removes tokens that match a set of part-of-speech tags.  */
end_comment
begin_class
DECL|class|JapanesePartOfSpeechStopFilter
specifier|public
specifier|final
class|class
name|JapanesePartOfSpeechStopFilter
extends|extends
name|FilteringTokenFilter
block|{
DECL|field|stopTags
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|stopTags
decl_stmt|;
DECL|field|posAtt
specifier|private
specifier|final
name|PartOfSpeechAttribute
name|posAtt
init|=
name|addAttribute
argument_list|(
name|PartOfSpeechAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a new {@link JapanesePartOfSpeechStopFilter}.    * @param input    the {@link TokenStream} to consume    * @param stopTags the part-of-speech tags that should be removed    */
DECL|method|JapanesePartOfSpeechStopFilter
specifier|public
name|JapanesePartOfSpeechStopFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|stopTags
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|stopTags
operator|=
name|stopTags
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|boolean
name|accept
parameter_list|()
block|{
specifier|final
name|String
name|pos
init|=
name|posAtt
operator|.
name|getPartOfSpeech
argument_list|()
decl_stmt|;
return|return
name|pos
operator|==
literal|null
operator|||
operator|!
name|stopTags
operator|.
name|contains
argument_list|(
name|pos
argument_list|)
return|;
block|}
block|}
end_class
end_unit
