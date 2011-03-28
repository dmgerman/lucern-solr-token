begin_unit
begin_package
DECL|package|org.apache.solr.uima.processor.an
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|processor
operator|.
name|an
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|ts
operator|.
name|SentimentAnnotation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|TokenAnnotation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_component
operator|.
name|JCasAnnotator_ImplBase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngineProcessException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|JCas
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|tcas
operator|.
name|Annotation
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|DummySentimentAnnotator
specifier|public
class|class
name|DummySentimentAnnotator
extends|extends
name|JCasAnnotator_ImplBase
block|{
DECL|field|positiveAdj
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|positiveAdj
init|=
block|{
literal|"happy"
block|,
literal|"cool"
block|,
literal|"nice"
block|}
decl_stmt|;
DECL|field|negativeAdj
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|negativeAdj
init|=
block|{
literal|"bad"
block|,
literal|"sad"
block|,
literal|"ugly"
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|JCas
name|jcas
parameter_list|)
throws|throws
name|AnalysisEngineProcessException
block|{
for|for
control|(
name|Annotation
name|annotation
range|:
name|jcas
operator|.
name|getAnnotationIndex
argument_list|(
name|TokenAnnotation
operator|.
name|type
argument_list|)
control|)
block|{
name|String
name|tokenPOS
init|=
operator|(
operator|(
name|TokenAnnotation
operator|)
name|annotation
operator|)
operator|.
name|getPosTag
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"jj"
operator|.
name|equals
argument_list|(
name|tokenPOS
argument_list|)
condition|)
block|{
if|if
condition|(
name|Arrays
operator|.
name|asList
argument_list|(
name|positiveAdj
argument_list|)
operator|.
name|contains
argument_list|(
name|annotation
operator|.
name|getCoveredText
argument_list|()
argument_list|)
condition|)
block|{
name|SentimentAnnotation
name|sentimentAnnotation
init|=
name|createSentimentAnnotation
argument_list|(
name|jcas
argument_list|,
name|annotation
argument_list|)
decl_stmt|;
name|sentimentAnnotation
operator|.
name|setMood
argument_list|(
literal|"positive"
argument_list|)
expr_stmt|;
name|sentimentAnnotation
operator|.
name|addToIndexes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Arrays
operator|.
name|asList
argument_list|(
name|negativeAdj
argument_list|)
operator|.
name|contains
argument_list|(
name|annotation
operator|.
name|getCoveredText
argument_list|()
argument_list|)
condition|)
block|{
name|SentimentAnnotation
name|sentimentAnnotation
init|=
name|createSentimentAnnotation
argument_list|(
name|jcas
argument_list|,
name|annotation
argument_list|)
decl_stmt|;
name|sentimentAnnotation
operator|.
name|setMood
argument_list|(
literal|"negative"
argument_list|)
expr_stmt|;
name|sentimentAnnotation
operator|.
name|addToIndexes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|createSentimentAnnotation
specifier|private
name|SentimentAnnotation
name|createSentimentAnnotation
parameter_list|(
name|JCas
name|jcas
parameter_list|,
name|Annotation
name|annotation
parameter_list|)
block|{
name|SentimentAnnotation
name|sentimentAnnotation
init|=
operator|new
name|SentimentAnnotation
argument_list|(
name|jcas
argument_list|)
decl_stmt|;
name|sentimentAnnotation
operator|.
name|setBegin
argument_list|(
name|annotation
operator|.
name|getBegin
argument_list|()
argument_list|)
expr_stmt|;
name|sentimentAnnotation
operator|.
name|setEnd
argument_list|(
name|annotation
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sentimentAnnotation
return|;
block|}
block|}
end_class
end_unit
