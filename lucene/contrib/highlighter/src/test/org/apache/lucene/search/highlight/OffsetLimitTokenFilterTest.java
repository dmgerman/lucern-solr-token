begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
begin_class
DECL|class|OffsetLimitTokenFilterTest
specifier|public
class|class
name|OffsetLimitTokenFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testFilter
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we disable MockTokenizer checks because we will forcefully limit the
comment|// tokenstream and call end() before incrementToken() returns false.
name|MockTokenizer
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"short toolong evenmuchlongertext a ab toolong foo"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|OffsetLimitTokenFilter
name|filter
init|=
operator|new
name|OffsetLimitTokenFilter
argument_list|(
name|stream
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"short"
block|,
literal|"toolong"
block|}
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"short toolong evenmuchlongertext a ab toolong foo"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|OffsetLimitTokenFilter
argument_list|(
name|stream
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"short"
block|,
literal|"toolong"
block|}
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"short toolong evenmuchlongertext a ab toolong foo"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|OffsetLimitTokenFilter
argument_list|(
name|stream
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"short"
block|,
literal|"toolong"
block|,
literal|"evenmuchlongertext"
block|}
argument_list|)
expr_stmt|;
comment|// TODO: This is not actually testing reuse! (reusableTokenStream is not implemented)
name|checkOneTermReuse
argument_list|(
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
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
name|MockTokenizer
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
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
operator|new
name|OffsetLimitTokenFilter
argument_list|(
name|tokenizer
argument_list|,
literal|10
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|"llenges"
argument_list|,
literal|"llenges"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
