begin_unit
begin_package
DECL|package|org.apache.lucene.collation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
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
name|KeywordTokenizer
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
name|Locale
import|;
end_import
begin_class
DECL|class|TestICUCollationKeyFilter
specifier|public
class|class
name|TestICUCollationKeyFilter
extends|extends
name|CollationTestBase
block|{
DECL|field|collator
specifier|private
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"fa"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
init|=
operator|new
name|TestAnalyzer
argument_list|(
name|collator
argument_list|)
decl_stmt|;
DECL|field|firstRangeBeginning
specifier|private
name|String
name|firstRangeBeginning
init|=
name|encodeCollationKey
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|firstRangeBeginningOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|firstRangeEnd
specifier|private
name|String
name|firstRangeEnd
init|=
name|encodeCollationKey
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|firstRangeEndOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|secondRangeBeginning
specifier|private
name|String
name|secondRangeBeginning
init|=
name|encodeCollationKey
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|secondRangeBeginningOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|secondRangeEnd
specifier|private
name|String
name|secondRangeEnd
init|=
name|encodeCollationKey
argument_list|(
name|collator
operator|.
name|getCollationKey
argument_list|(
name|secondRangeEndOriginal
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
DECL|class|TestAnalyzer
specifier|public
class|class
name|TestAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|collator
specifier|private
name|Collator
name|collator
decl_stmt|;
DECL|method|TestAnalyzer
name|TestAnalyzer
parameter_list|(
name|Collator
name|collator
parameter_list|)
block|{
name|this
operator|.
name|collator
operator|=
name|collator
expr_stmt|;
block|}
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
name|KeywordTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|ICUCollationKeyFilter
argument_list|(
name|result
argument_list|,
name|collator
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|method|testFarsiQueryParserCollating
specifier|public
name|void
name|testFarsiQueryParserCollating
parameter_list|()
throws|throws
name|Exception
block|{
name|testFarsiQueryParserCollating
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
block|}
DECL|method|testFarsiRangeFilterCollating
specifier|public
name|void
name|testFarsiRangeFilterCollating
parameter_list|()
throws|throws
name|Exception
block|{
name|testFarsiRangeFilterCollating
argument_list|(
name|analyzer
argument_list|,
name|firstRangeBeginning
argument_list|,
name|firstRangeEnd
argument_list|,
name|secondRangeBeginning
argument_list|,
name|secondRangeEnd
argument_list|)
expr_stmt|;
block|}
DECL|method|testFarsiRangeQueryCollating
specifier|public
name|void
name|testFarsiRangeQueryCollating
parameter_list|()
throws|throws
name|Exception
block|{
name|testFarsiRangeQueryCollating
argument_list|(
name|analyzer
argument_list|,
name|firstRangeBeginning
argument_list|,
name|firstRangeEnd
argument_list|,
name|secondRangeBeginning
argument_list|,
name|secondRangeEnd
argument_list|)
expr_stmt|;
block|}
DECL|method|testFarsiTermRangeQuery
specifier|public
name|void
name|testFarsiTermRangeQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|testFarsiTermRangeQuery
argument_list|(
name|analyzer
argument_list|,
name|firstRangeBeginning
argument_list|,
name|firstRangeEnd
argument_list|,
name|secondRangeBeginning
argument_list|,
name|secondRangeEnd
argument_list|)
expr_stmt|;
block|}
comment|// Test using various international locales with accented characters (which
comment|// sort differently depending on locale)
comment|//
comment|// Copied (and slightly modified) from
comment|// org.apache.lucene.search.TestSort.testInternationalSort()
comment|//
DECL|method|testCollationKeySort
specifier|public
name|void
name|testCollationKeySort
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|usAnalyzer
init|=
operator|new
name|TestAnalyzer
argument_list|(
name|Collator
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
decl_stmt|;
name|Analyzer
name|franceAnalyzer
init|=
operator|new
name|TestAnalyzer
argument_list|(
name|Collator
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|FRANCE
argument_list|)
argument_list|)
decl_stmt|;
name|Analyzer
name|swedenAnalyzer
init|=
operator|new
name|TestAnalyzer
argument_list|(
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"sv"
argument_list|,
literal|"se"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Analyzer
name|denmarkAnalyzer
init|=
operator|new
name|TestAnalyzer
argument_list|(
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"da"
argument_list|,
literal|"dk"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// The ICU Collator and java.text.Collator implementations differ in their
comment|// orderings - "BFJHD" is the ordering for the ICU Collator for Locale.US.
name|testCollationKeySort
argument_list|(
name|usAnalyzer
argument_list|,
name|franceAnalyzer
argument_list|,
name|swedenAnalyzer
argument_list|,
name|denmarkAnalyzer
argument_list|,
literal|"BFJHD"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
