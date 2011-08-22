begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.en
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|en
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * Tests for {@link KStemmer}  */
end_comment
begin_class
DECL|class|TestKStemmer
specifier|public
class|class
name|TestKStemmer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
name|Analyzer
name|a
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
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|KStemFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
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
name|a
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|/**     * test the kstemmer optimizations against a bunch of words    * that were stemmed with the original java kstemmer (generated from    * testCreateMap, commented out below).    */
DECL|method|testVocabulary
specifier|public
name|void
name|testVocabulary
parameter_list|()
throws|throws
name|Exception
block|{
name|assertVocabulary
argument_list|(
name|a
argument_list|,
name|getDataFile
argument_list|(
literal|"kstemTestData.zip"
argument_list|)
argument_list|,
literal|"kstem_examples.txt"
argument_list|)
expr_stmt|;
block|}
comment|/****** requires original java kstem source code to create map   public void testCreateMap() throws Exception {     String input = getBigDoc();     Reader r = new StringReader(input);     TokenFilter tf = new LowerCaseFilter(new LetterTokenizer(r));     // tf = new KStemFilter(tf);      KStemmer kstem = new KStemmer();     Map<String,String> map = new TreeMap<String,String>();     for(;;) {       Token t = tf.next();       if (t==null) break;       String s = t.termText();       if (map.containsKey(s)) continue;       map.put(s, kstem.stem(s));     }      Writer out = new BufferedWriter(new FileWriter("kstem_examples.txt"));     for (String key : map.keySet()) {       out.write(key);       out.write('\t');       out.write(map.get(key));       out.write('\n');     }     out.close();   }   ******/
block|}
end_class
end_unit
