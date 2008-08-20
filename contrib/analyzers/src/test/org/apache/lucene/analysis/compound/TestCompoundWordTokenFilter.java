begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|InputStream
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
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
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
name|Token
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
name|TokenFilter
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
name|WhitespaceTokenizer
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
name|compound
operator|.
name|CompoundWordTokenFilterBase
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
name|compound
operator|.
name|DictionaryCompoundWordTokenFilter
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
name|compound
operator|.
name|HyphenationCompoundWordTokenFilter
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
name|compound
operator|.
name|hyphenation
operator|.
name|HyphenationTree
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_class
DECL|class|TestCompoundWordTokenFilter
specifier|public
class|class
name|TestCompoundWordTokenFilter
extends|extends
name|TestCase
block|{
DECL|field|locations
specifier|private
specifier|static
name|String
index|[]
name|locations
init|=
block|{
literal|"http://dfn.dl.sourceforge.net/sourceforge/offo/offo-hyphenation.zip"
block|,
literal|"http://surfnet.dl.sourceforge.net/sourceforge/offo/offo-hyphenation.zip"
block|,
literal|"http://superb-west.dl.sourceforge.net/sourceforge/offo/offo-hyphenation.zip"
block|,
literal|"http://superb-east.dl.sourceforge.net/sourceforge/offo/offo-hyphenation.zip"
block|}
decl_stmt|;
DECL|field|patternsFileContent
specifier|private
name|byte
index|[]
name|patternsFileContent
decl_stmt|;
DECL|method|setUp
specifier|protected
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
name|getHyphenationPatternFileContents
argument_list|()
expr_stmt|;
block|}
DECL|method|testHyphenationCompoundWordsDE
specifier|public
name|void
name|testHyphenationCompoundWordsDE
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"Rind"
block|,
literal|"Fleisch"
block|,
literal|"Draht"
block|,
literal|"Schere"
block|,
literal|"Gesetz"
block|,
literal|"Aufgabe"
block|,
literal|"Ãberwachung"
block|}
decl_stmt|;
name|Reader
name|reader
init|=
name|getHyphenationReader
argument_list|(
literal|"de_DR.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
comment|// we gracefully die if we have no reader
return|return;
block|}
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz Drahtschere abba"
argument_list|)
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFiltersTo
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"RindfleischÃ¼berwachungsgesetz"
block|,
literal|"Rind"
block|,
literal|"fleisch"
block|,
literal|"Ã¼berwachung"
block|,
literal|"gesetz"
block|,
literal|"Drahtschere"
block|,
literal|"Draht"
block|,
literal|"schere"
block|,
literal|"abba"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|4
block|,
literal|11
block|,
literal|23
block|,
literal|30
block|,
literal|30
block|,
literal|35
block|,
literal|42
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|29
block|,
literal|4
block|,
literal|11
block|,
literal|22
block|,
literal|29
block|,
literal|41
block|,
literal|35
block|,
literal|41
block|,
literal|46
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHyphenationCompoundWordsDELongestMatch
specifier|public
name|void
name|testHyphenationCompoundWordsDELongestMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"Rind"
block|,
literal|"Fleisch"
block|,
literal|"Draht"
block|,
literal|"Schere"
block|,
literal|"Gesetz"
block|,
literal|"Aufgabe"
block|,
literal|"Ãberwachung"
block|,
literal|"Rindfleisch"
block|,
literal|"Ãberwachungsgesetz"
block|}
decl_stmt|;
name|Reader
name|reader
init|=
name|getHyphenationReader
argument_list|(
literal|"de_DR.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
comment|// we gracefully die if we have no reader
return|return;
block|}
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|)
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
literal|40
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertFiltersTo
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"RindfleischÃ¼berwachungsgesetz"
block|,
literal|"Rindfleisch"
block|,
literal|"fleisch"
block|,
literal|"Ã¼berwachungsgesetz"
block|,
literal|"gesetz"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|4
block|,
literal|11
block|,
literal|23
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|29
block|,
literal|11
block|,
literal|11
block|,
literal|29
block|,
literal|29
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDumbCompoundWordsSE
specifier|public
name|void
name|testDumbCompoundWordsSE
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"Bil"
block|,
literal|"DÃ¶rr"
block|,
literal|"Motor"
block|,
literal|"Tak"
block|,
literal|"Borr"
block|,
literal|"Slag"
block|,
literal|"Hammar"
block|,
literal|"Pelar"
block|,
literal|"Glas"
block|,
literal|"Ãgon"
block|,
literal|"Fodral"
block|,
literal|"Bas"
block|,
literal|"Fiol"
block|,
literal|"Makare"
block|,
literal|"GesÃ¤ll"
block|,
literal|"Sko"
block|,
literal|"Vind"
block|,
literal|"Rute"
block|,
literal|"Torkare"
block|,
literal|"Blad"
block|}
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BildÃ¶rr Bilmotor Biltak Slagborr Hammarborr Pelarborr GlasÃ¶gonfodral Basfiolsfodral BasfiolsfodralmakaregesÃ¤ll Skomakare Vindrutetorkare Vindrutetorkarblad abba"
argument_list|)
argument_list|)
argument_list|,
name|dict
argument_list|)
decl_stmt|;
name|assertFiltersTo
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"BildÃ¶rr"
block|,
literal|"Bil"
block|,
literal|"dÃ¶rr"
block|,
literal|"Bilmotor"
block|,
literal|"Bil"
block|,
literal|"motor"
block|,
literal|"Biltak"
block|,
literal|"Bil"
block|,
literal|"tak"
block|,
literal|"Slagborr"
block|,
literal|"Slag"
block|,
literal|"borr"
block|,
literal|"Hammarborr"
block|,
literal|"Hammar"
block|,
literal|"borr"
block|,
literal|"Pelarborr"
block|,
literal|"Pelar"
block|,
literal|"borr"
block|,
literal|"GlasÃ¶gonfodral"
block|,
literal|"Glas"
block|,
literal|"Ã¶gon"
block|,
literal|"fodral"
block|,
literal|"Basfiolsfodral"
block|,
literal|"Bas"
block|,
literal|"fiol"
block|,
literal|"fodral"
block|,
literal|"BasfiolsfodralmakaregesÃ¤ll"
block|,
literal|"Bas"
block|,
literal|"fiol"
block|,
literal|"fodral"
block|,
literal|"makare"
block|,
literal|"gesÃ¤ll"
block|,
literal|"Skomakare"
block|,
literal|"Sko"
block|,
literal|"makare"
block|,
literal|"Vindrutetorkare"
block|,
literal|"Vind"
block|,
literal|"rute"
block|,
literal|"torkare"
block|,
literal|"Vindrutetorkarblad"
block|,
literal|"Vind"
block|,
literal|"rute"
block|,
literal|"blad"
block|,
literal|"abba"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|3
block|,
literal|8
block|,
literal|8
block|,
literal|11
block|,
literal|17
block|,
literal|17
block|,
literal|20
block|,
literal|24
block|,
literal|24
block|,
literal|28
block|,
literal|33
block|,
literal|33
block|,
literal|39
block|,
literal|44
block|,
literal|44
block|,
literal|49
block|,
literal|54
block|,
literal|54
block|,
literal|58
block|,
literal|62
block|,
literal|69
block|,
literal|69
block|,
literal|72
block|,
literal|77
block|,
literal|84
block|,
literal|84
block|,
literal|87
block|,
literal|92
block|,
literal|98
block|,
literal|104
block|,
literal|111
block|,
literal|111
block|,
literal|114
block|,
literal|121
block|,
literal|121
block|,
literal|125
block|,
literal|129
block|,
literal|137
block|,
literal|137
block|,
literal|141
block|,
literal|151
block|,
literal|156
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|,
literal|3
block|,
literal|7
block|,
literal|16
block|,
literal|11
block|,
literal|16
block|,
literal|23
block|,
literal|20
block|,
literal|23
block|,
literal|32
block|,
literal|28
block|,
literal|32
block|,
literal|43
block|,
literal|39
block|,
literal|43
block|,
literal|53
block|,
literal|49
block|,
literal|53
block|,
literal|68
block|,
literal|58
block|,
literal|62
block|,
literal|68
block|,
literal|83
block|,
literal|72
block|,
literal|76
block|,
literal|83
block|,
literal|110
block|,
literal|87
block|,
literal|91
block|,
literal|98
block|,
literal|104
block|,
literal|110
block|,
literal|120
block|,
literal|114
block|,
literal|120
block|,
literal|136
block|,
literal|125
block|,
literal|129
block|,
literal|136
block|,
literal|155
block|,
literal|141
block|,
literal|145
block|,
literal|155
block|,
literal|160
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDumbCompoundWordsSELongestMatch
specifier|public
name|void
name|testDumbCompoundWordsSELongestMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"Bil"
block|,
literal|"DÃ¶rr"
block|,
literal|"Motor"
block|,
literal|"Tak"
block|,
literal|"Borr"
block|,
literal|"Slag"
block|,
literal|"Hammar"
block|,
literal|"Pelar"
block|,
literal|"Glas"
block|,
literal|"Ãgon"
block|,
literal|"Fodral"
block|,
literal|"Bas"
block|,
literal|"Fiols"
block|,
literal|"Makare"
block|,
literal|"GesÃ¤ll"
block|,
literal|"Sko"
block|,
literal|"Vind"
block|,
literal|"Rute"
block|,
literal|"Torkare"
block|,
literal|"Blad"
block|,
literal|"Fiolsfodral"
block|}
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BasfiolsfodralmakaregesÃ¤ll"
argument_list|)
argument_list|)
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertFiltersTo
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"BasfiolsfodralmakaregesÃ¤ll"
block|,
literal|"Bas"
block|,
literal|"fiolsfodral"
block|,
literal|"fodral"
block|,
literal|"makare"
block|,
literal|"gesÃ¤ll"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|3
block|,
literal|8
block|,
literal|14
block|,
literal|20
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|26
block|,
literal|3
block|,
literal|14
block|,
literal|14
block|,
literal|20
block|,
literal|26
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFiltersTo
specifier|private
name|void
name|assertFiltersTo
parameter_list|(
name|TokenFilter
name|tf
parameter_list|,
name|String
index|[]
name|s
parameter_list|,
name|int
index|[]
name|startOffset
parameter_list|,
name|int
index|[]
name|endOffset
parameter_list|,
name|int
index|[]
name|posIncr
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Token
name|nextToken
init|=
name|tf
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
index|[
name|i
index|]
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|startOffset
index|[
name|i
index|]
argument_list|,
name|nextToken
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|endOffset
index|[
name|i
index|]
argument_list|,
name|nextToken
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|posIncr
index|[
name|i
index|]
argument_list|,
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|tf
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getHyphenationPatternFileContents
specifier|private
name|void
name|getHyphenationPatternFileContents
parameter_list|()
block|{
try|try
block|{
name|List
name|urls
init|=
operator|new
name|LinkedList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|locations
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|urls
argument_list|)
expr_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
operator|(
name|String
operator|)
name|urls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|url
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|patternsFileContent
operator|=
name|out
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// we swallow all exceptions - the user might have no internet connection
block|}
block|}
DECL|method|getHyphenationReader
specifier|private
name|Reader
name|getHyphenationReader
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|patternsFileContent
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ZipInputStream
name|zipstream
init|=
operator|new
name|ZipInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|patternsFileContent
argument_list|)
argument_list|)
decl_stmt|;
name|ZipEntry
name|entry
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|zipstream
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"offo-hyphenation/hyph/"
operator|+
name|filename
argument_list|)
condition|)
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|outstream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|zipstream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|outstream
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|outstream
operator|.
name|close
argument_list|()
expr_stmt|;
name|zipstream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|StringReader
argument_list|(
operator|new
name|String
argument_list|(
name|outstream
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"ISO-8859-1"
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|// we never should get here
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
