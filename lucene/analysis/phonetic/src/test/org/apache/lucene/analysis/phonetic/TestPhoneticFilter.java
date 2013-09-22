begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
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
name|StringReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Encoder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
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
name|WhitespaceTokenizer
import|;
end_import
begin_comment
comment|/**  * Tests {@link PhoneticFilter}  */
end_comment
begin_class
DECL|class|TestPhoneticFilter
specifier|public
class|class
name|TestPhoneticFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testAlgorithms
specifier|public
name|void
name|testAlgorithms
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAlgorithm
argument_list|(
operator|new
name|Metaphone
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"aaa"
block|,
literal|"B"
block|,
literal|"bbb"
block|,
literal|"KKK"
block|,
literal|"ccc"
block|,
literal|"ESKS"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|Metaphone
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"KKK"
block|,
literal|"ESKS"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|DoubleMetaphone
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"aaa"
block|,
literal|"PP"
block|,
literal|"bbb"
block|,
literal|"KK"
block|,
literal|"ccc"
block|,
literal|"ASKS"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|DoubleMetaphone
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"PP"
block|,
literal|"KK"
block|,
literal|"ASKS"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|Soundex
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A000"
block|,
literal|"aaa"
block|,
literal|"B000"
block|,
literal|"bbb"
block|,
literal|"C000"
block|,
literal|"ccc"
block|,
literal|"E220"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|Soundex
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A000"
block|,
literal|"B000"
block|,
literal|"C000"
block|,
literal|"E220"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|RefinedSoundex
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A0"
block|,
literal|"aaa"
block|,
literal|"B1"
block|,
literal|"bbb"
block|,
literal|"C3"
block|,
literal|"ccc"
block|,
literal|"E034034"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|RefinedSoundex
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A0"
block|,
literal|"B1"
block|,
literal|"C3"
block|,
literal|"E034034"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|Caverphone2
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|"Darda Karleen Datha Carlene"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TTA1111111"
block|,
literal|"Darda"
block|,
literal|"KLN1111111"
block|,
literal|"Karleen"
block|,
literal|"TTA1111111"
block|,
literal|"Datha"
block|,
literal|"KLN1111111"
block|,
literal|"Carlene"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
operator|new
name|Caverphone2
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"Darda Karleen Datha Carlene"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TTA1111111"
block|,
literal|"KLN1111111"
block|,
literal|"TTA1111111"
block|,
literal|"KLN1111111"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAlgorithm
specifier|static
name|void
name|assertAlgorithm
parameter_list|(
name|Encoder
name|encoder
parameter_list|,
name|boolean
name|inject
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|PhoneticFilter
name|filter
init|=
operator|new
name|PhoneticFilter
argument_list|(
name|tokenizer
argument_list|,
name|encoder
argument_list|,
name|inject
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
name|expected
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
name|IOException
block|{
name|Encoder
name|encoders
index|[]
init|=
operator|new
name|Encoder
index|[]
block|{
operator|new
name|Metaphone
argument_list|()
block|,
operator|new
name|DoubleMetaphone
argument_list|()
block|,
operator|new
name|Soundex
argument_list|()
block|,
operator|new
name|RefinedSoundex
argument_list|()
block|,
operator|new
name|Caverphone2
argument_list|()
block|}
decl_stmt|;
for|for
control|(
specifier|final
name|Encoder
name|e
range|:
name|encoders
control|)
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
name|tokenizer
argument_list|,
operator|new
name|PhoneticFilter
argument_list|(
name|tokenizer
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|Analyzer
name|b
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
name|tokenizer
argument_list|,
operator|new
name|PhoneticFilter
argument_list|(
name|tokenizer
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|b
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|Encoder
name|encoders
index|[]
init|=
operator|new
name|Encoder
index|[]
block|{
operator|new
name|Metaphone
argument_list|()
block|,
operator|new
name|DoubleMetaphone
argument_list|()
block|,
operator|new
name|Soundex
argument_list|()
block|,
operator|new
name|RefinedSoundex
argument_list|()
block|,
operator|new
name|Caverphone2
argument_list|()
block|}
decl_stmt|;
for|for
control|(
specifier|final
name|Encoder
name|e
range|:
name|encoders
control|)
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
name|PhoneticFilter
argument_list|(
name|tokenizer
argument_list|,
name|e
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
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
block|}
block|}
block|}
end_class
end_unit
