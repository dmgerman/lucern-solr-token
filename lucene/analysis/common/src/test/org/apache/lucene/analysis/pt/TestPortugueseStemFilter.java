begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.pt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pt
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|VocabularyAssert
operator|.
name|assertVocabulary
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|BaseTokenStreamTestCase
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Tokenizer
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
name|core
operator|.
name|KeywordTokenizer
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
name|core
operator|.
name|LowerCaseFilter
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
name|StandardTokenizer
import|;
end_import
begin_comment
comment|/**  * Simple tests for {@link PortugueseStemFilter}  */
end_comment
begin_class
DECL|class|TestPortugueseStemFilter
specifier|public
class|class
name|TestPortugueseStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|LowerCaseFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|source
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|PortugueseStemFilter
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Test the example from the paper "Assessing the impact of stemming accuracy    * on information retrieval"    */
DECL|method|testExamples
specifier|public
name|void
name|testExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"O debate polÃ­tico, pelo menos o que vem a pÃºblico, parece, de modo nada "
operator|+
literal|"surpreendente, restrito a temas menores. Mas hÃ¡, evidentemente, "
operator|+
literal|"grandes questÃµes em jogo nas eleiÃ§Ãµes que se aproximam."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"o"
block|,
literal|"debat"
block|,
literal|"politic"
block|,
literal|"pel"
block|,
literal|"menos"
block|,
literal|"o"
block|,
literal|"que"
block|,
literal|"vem"
block|,
literal|"a"
block|,
literal|"public"
block|,
literal|"parec"
block|,
literal|"de"
block|,
literal|"mod"
block|,
literal|"nad"
block|,
literal|"surpreend"
block|,
literal|"restrit"
block|,
literal|"a"
block|,
literal|"tem"
block|,
literal|"men"
block|,
literal|"mas"
block|,
literal|"ha"
block|,
literal|"evid"
block|,
literal|"grand"
block|,
literal|"quest"
block|,
literal|"em"
block|,
literal|"jog"
block|,
literal|"na"
block|,
literal|"eleic"
block|,
literal|"que"
block|,
literal|"se"
block|,
literal|"aproxim"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test against a vocabulary from the reference impl */
DECL|method|testVocabulary
specifier|public
name|void
name|testVocabulary
parameter_list|()
throws|throws
name|IOException
block|{
name|assertVocabulary
argument_list|(
name|analyzer
argument_list|,
name|getDataFile
argument_list|(
literal|"ptrslptestdata.zip"
argument_list|)
argument_list|,
literal|"ptrslp.txt"
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|PortugueseStemFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
