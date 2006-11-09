begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.snowball
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|snowball
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|standard
operator|.
name|*
import|;
end_import
begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|snowball
operator|.
name|ext
operator|.
name|*
import|;
end_import
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
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/** Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link  * LowerCaseFilter}, {@link StopFilter} and {@link SnowballFilter}.  *  * Available stemmers are listed in {@link net.sf.snowball.ext}.  The name of a  * stemmer is the part of the class name before "Stemmer", e.g., the stemmer in  * {@link EnglishStemmer} is named "English".  */
end_comment
begin_class
DECL|class|SnowballAnalyzer
specifier|public
class|class
name|SnowballAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|stopSet
specifier|private
name|Set
name|stopSet
decl_stmt|;
comment|/** Builds the named analyzer with no stop words. */
DECL|method|SnowballAnalyzer
specifier|public
name|SnowballAnalyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** Builds the named analyzer with the given stop words. */
DECL|method|SnowballAnalyzer
specifier|public
name|SnowballAnalyzer
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a {@link StandardTokenizer} filtered by a {@link       StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}. */
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
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopSet
operator|!=
literal|null
condition|)
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|SnowballFilter
argument_list|(
name|result
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
