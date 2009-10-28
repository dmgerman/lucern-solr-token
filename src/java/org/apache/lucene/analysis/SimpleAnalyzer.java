begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
begin_comment
comment|/** An {@link Analyzer} that filters {@link LetterTokenizer}   *  with {@link LowerCaseFilter} */
end_comment
begin_class
DECL|class|SimpleAnalyzer
specifier|public
specifier|final
class|class
name|SimpleAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|LowerCaseTokenizer
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Tokenizer
name|tokenizer
init|=
operator|(
name|Tokenizer
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenizer
operator|==
literal|null
condition|)
block|{
name|tokenizer
operator|=
operator|new
name|LowerCaseTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
block|}
else|else
name|tokenizer
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|tokenizer
return|;
block|}
block|}
end_class
end_unit
