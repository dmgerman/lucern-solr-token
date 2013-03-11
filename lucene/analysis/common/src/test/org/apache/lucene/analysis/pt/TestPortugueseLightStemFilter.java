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
name|MockTokenizer
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
name|Analyzer
operator|.
name|TokenStreamComponents
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
name|miscellaneous
operator|.
name|SetKeywordMarkerFilter
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
name|CharArraySet
import|;
end_import
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
name|*
import|;
end_import
begin_comment
comment|/**  * Simple tests for {@link PortugueseLightStemFilter}  */
end_comment
begin_class
DECL|class|TestPortugueseLightStemFilter
specifier|public
class|class
name|TestPortugueseLightStemFilter
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
name|PortugueseLightStemFilter
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
literal|"pelo"
block|,
literal|"meno"
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
literal|"modo"
block|,
literal|"nada"
block|,
literal|"surpreendent"
block|,
literal|"restrit"
block|,
literal|"a"
block|,
literal|"tema"
block|,
literal|"menor"
block|,
literal|"mas"
block|,
literal|"hÃ¡"
block|,
literal|"evident"
block|,
literal|"grand"
block|,
literal|"questa"
block|,
literal|"em"
block|,
literal|"jogo"
block|,
literal|"nas"
block|,
literal|"eleica"
block|,
literal|"que"
block|,
literal|"se"
block|,
literal|"aproximam"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test examples from the c implementation    */
DECL|method|testMoreExamples
specifier|public
name|void
name|testMoreExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"doutores"
argument_list|,
literal|"doutor"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"doutor"
argument_list|,
literal|"doutor"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"homens"
argument_list|,
literal|"homem"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"homem"
argument_list|,
literal|"homem"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"papÃ©is"
argument_list|,
literal|"papel"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"papel"
argument_list|,
literal|"papel"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"normais"
argument_list|,
literal|"normal"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"normal"
argument_list|,
literal|"normal"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"lencÃ³is"
argument_list|,
literal|"lencol"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"lencol"
argument_list|,
literal|"lencol"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"barris"
argument_list|,
literal|"barril"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"barril"
argument_list|,
literal|"barril"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"botÃµes"
argument_list|,
literal|"bota"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"botÃ£o"
argument_list|,
literal|"bota"
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
literal|"ptlighttestdata.zip"
argument_list|)
argument_list|,
literal|"ptlight.txt"
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeyword
specifier|public
name|void
name|testKeyword
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|CharArraySet
name|exclusionSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|asSet
argument_list|(
literal|"quilomÃ©tricas"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|source
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenStream
name|sink
init|=
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|source
argument_list|,
name|exclusionSet
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|PortugueseLightStemFilter
argument_list|(
name|sink
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"quilomÃ©tricas"
argument_list|,
literal|"quilomÃ©tricas"
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
name|PortugueseLightStemFilter
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
