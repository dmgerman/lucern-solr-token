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
name|EntityAnnotation
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
DECL|class|DummyEntityAnnotator
specifier|public
class|class
name|DummyEntityAnnotator
extends|extends
name|JCasAnnotator_ImplBase
block|{
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
literal|"np"
operator|.
name|equals
argument_list|(
name|tokenPOS
argument_list|)
operator|||
literal|"nps"
operator|.
name|equals
argument_list|(
name|tokenPOS
argument_list|)
condition|)
block|{
name|EntityAnnotation
name|entityAnnotation
init|=
operator|new
name|EntityAnnotation
argument_list|(
name|jcas
argument_list|)
decl_stmt|;
name|entityAnnotation
operator|.
name|setBegin
argument_list|(
name|annotation
operator|.
name|getBegin
argument_list|()
argument_list|)
expr_stmt|;
name|entityAnnotation
operator|.
name|setEnd
argument_list|(
name|annotation
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
name|entityAnnotation
operator|.
name|addToIndexes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
