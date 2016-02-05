begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
package|;
end_package
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
comment|/**  * Simple tests for {@link FrenchLightStemFilter}  */
end_comment
begin_class
DECL|class|TestFrenchLightStemFilter
specifier|public
class|class
name|TestFrenchLightStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|analyzer
operator|=
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
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|FrenchLightStemFilter
argument_list|(
name|source
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** Test some examples from the paper */
DECL|method|testExamples
specifier|public
name|void
name|testExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"chevaux"
argument_list|,
literal|"cheval"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"cheval"
argument_list|,
literal|"cheval"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"hiboux"
argument_list|,
literal|"hibou"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"hibou"
argument_list|,
literal|"hibou"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"chantÃ©s"
argument_list|,
literal|"chant"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"chanter"
argument_list|,
literal|"chant"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"chante"
argument_list|,
literal|"chant"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"chant"
argument_list|,
literal|"chant"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"baronnes"
argument_list|,
literal|"baron"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"barons"
argument_list|,
literal|"baron"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"baron"
argument_list|,
literal|"baron"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"peaux"
argument_list|,
literal|"peau"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"peau"
argument_list|,
literal|"peau"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"anneaux"
argument_list|,
literal|"aneau"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"anneau"
argument_list|,
literal|"aneau"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"neveux"
argument_list|,
literal|"neveu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"neveu"
argument_list|,
literal|"neveu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"affreux"
argument_list|,
literal|"afreu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"affreuse"
argument_list|,
literal|"afreu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"investissement"
argument_list|,
literal|"investi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"investir"
argument_list|,
literal|"investi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"assourdissant"
argument_list|,
literal|"asourdi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"assourdir"
argument_list|,
literal|"asourdi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"pratiquement"
argument_list|,
literal|"pratiqu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"pratique"
argument_list|,
literal|"pratiqu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"administrativement"
argument_list|,
literal|"administratif"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"administratif"
argument_list|,
literal|"administratif"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"justificatrice"
argument_list|,
literal|"justifi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"justificateur"
argument_list|,
literal|"justifi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"justifier"
argument_list|,
literal|"justifi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"educatrice"
argument_list|,
literal|"eduqu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"eduquer"
argument_list|,
literal|"eduqu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"communicateur"
argument_list|,
literal|"comuniqu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"communiquer"
argument_list|,
literal|"comuniqu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"accompagnatrice"
argument_list|,
literal|"acompagn"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"accompagnateur"
argument_list|,
literal|"acompagn"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"administrateur"
argument_list|,
literal|"administr"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"administrer"
argument_list|,
literal|"administr"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"productrice"
argument_list|,
literal|"product"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"producteur"
argument_list|,
literal|"product"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"acheteuse"
argument_list|,
literal|"achet"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"acheteur"
argument_list|,
literal|"achet"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"planteur"
argument_list|,
literal|"plant"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"plante"
argument_list|,
literal|"plant"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"poreuse"
argument_list|,
literal|"poreu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"poreux"
argument_list|,
literal|"poreu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"plieuse"
argument_list|,
literal|"plieu"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"bijoutiÃ¨re"
argument_list|,
literal|"bijouti"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"bijoutier"
argument_list|,
literal|"bijouti"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"caissiÃ¨re"
argument_list|,
literal|"caisi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"caissier"
argument_list|,
literal|"caisi"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"abrasive"
argument_list|,
literal|"abrasif"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"abrasif"
argument_list|,
literal|"abrasif"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"folle"
argument_list|,
literal|"fou"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"fou"
argument_list|,
literal|"fou"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"personnelle"
argument_list|,
literal|"person"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"personne"
argument_list|,
literal|"person"
argument_list|)
expr_stmt|;
comment|// algo bug: too short length
comment|//checkOneTerm(analyzer, "personnel", "person");
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"complÃ¨te"
argument_list|,
literal|"complet"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"complet"
argument_list|,
literal|"complet"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"aromatique"
argument_list|,
literal|"aromat"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"faiblesse"
argument_list|,
literal|"faibl"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"faible"
argument_list|,
literal|"faibl"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"patinage"
argument_list|,
literal|"patin"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"patin"
argument_list|,
literal|"patin"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"sonorisation"
argument_list|,
literal|"sono"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"ritualisation"
argument_list|,
literal|"rituel"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"rituel"
argument_list|,
literal|"rituel"
argument_list|)
expr_stmt|;
comment|// algo bug: masked by rules above
comment|//checkOneTerm(analyzer, "colonisateur", "colon");
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"nomination"
argument_list|,
literal|"nomin"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"disposition"
argument_list|,
literal|"dispos"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"dispose"
argument_list|,
literal|"dispos"
argument_list|)
expr_stmt|;
comment|// SOLR-3463 : abusive compression of repeated characters in numbers
comment|// Trailing repeated char elision :
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"1234555"
argument_list|,
literal|"1234555"
argument_list|)
expr_stmt|;
comment|// Repeated char within numbers with more than 4 characters :
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"12333345"
argument_list|,
literal|"12333345"
argument_list|)
expr_stmt|;
comment|// Short numbers weren't affected already:
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"1234"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
comment|// Ensure behaviour is preserved for words!
comment|// Trailing repeated char elision :
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"abcdeff"
argument_list|,
literal|"abcdef"
argument_list|)
expr_stmt|;
comment|// Repeated char within words with more than 4 characters :
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"abcccddeef"
argument_list|,
literal|"abcdef"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"crÃ©Ã©es"
argument_list|,
literal|"cre"
argument_list|)
expr_stmt|;
comment|// Combined letter and digit repetition
name|checkOneTerm
argument_list|(
name|analyzer
argument_list|,
literal|"22hh00"
argument_list|,
literal|"22h00"
argument_list|)
expr_stmt|;
comment|// 10:00pm
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
name|getDataPath
argument_list|(
literal|"frlighttestdata.zip"
argument_list|)
argument_list|,
literal|"frlight.txt"
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
name|asSet
argument_list|(
literal|"chevaux"
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
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|MockTokenizer
argument_list|(
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
name|FrenchLightStemFilter
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
literal|"chevaux"
argument_list|,
literal|"chevaux"
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
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
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|FrenchLightStemFilter
argument_list|(
name|tokenizer
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
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
