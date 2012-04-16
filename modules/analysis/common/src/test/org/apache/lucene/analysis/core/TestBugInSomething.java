begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package
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
name|MockCharFilter
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
name|MockTokenFilter
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
name|charfilter
operator|.
name|MappingCharFilter
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
name|charfilter
operator|.
name|NormalizeCharMap
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
name|commongrams
operator|.
name|CommonGramsFilter
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestBugInSomething
specifier|public
class|class
name|TestBugInSomething
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CharArraySet
name|cas
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cas
operator|.
name|add
argument_list|(
literal|"jjp"
argument_list|)
expr_stmt|;
name|cas
operator|.
name|add
argument_list|(
literal|"wlmwoknt"
argument_list|)
expr_stmt|;
name|cas
operator|.
name|add
argument_list|(
literal|"tcgyreo"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizeCharMap
name|map
init|=
operator|new
name|NormalizeCharMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"mtqlpi"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"mwoknt"
argument_list|,
literal|"jjp"
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"tcgyreo"
argument_list|,
literal|"zpfpajyws"
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|""
argument_list|,
literal|"eethksv"
argument_list|)
expr_stmt|;
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
name|t
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|TestRandomChains
operator|.
name|CheckThatYouDidntReadAnythingReaderWrapper
argument_list|(
name|reader
argument_list|)
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|,
literal|false
argument_list|,
operator|-
literal|65
argument_list|)
decl_stmt|;
name|TokenFilter
name|f
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|t
argument_list|,
name|cas
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|reader
operator|=
operator|new
name|MockCharFilter
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|MappingCharFilter
argument_list|(
name|map
argument_list|,
name|reader
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
decl_stmt|;
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|false
argument_list|,
literal|"wmgddzunizdomqyj"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
