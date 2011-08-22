begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ReusableAnalyzerBase
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
comment|/**  * Simple tests for {@link FrenchMinimalStemFilter}  */
end_comment
begin_class
DECL|class|TestFrenchMinimalStemFilter
specifier|public
class|class
name|TestFrenchMinimalStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
init|=
operator|new
name|ReusableAnalyzerBase
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
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|FrenchMinimalStemFilter
argument_list|(
name|source
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
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
literal|"hiboux"
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
literal|"frminimaltestdata.zip"
argument_list|)
argument_list|,
literal|"frminimal.txt"
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
argument_list|,
name|analyzer
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
