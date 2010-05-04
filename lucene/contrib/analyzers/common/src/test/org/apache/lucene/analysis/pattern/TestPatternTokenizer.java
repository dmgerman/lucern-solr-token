begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.pattern
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pattern
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
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|regex
operator|.
name|Pattern
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
name|CharReader
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
name|CharStream
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
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import
begin_class
DECL|class|TestPatternTokenizer
specifier|public
class|class
name|TestPatternTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testSplitting
specifier|public
name|void
name|testSplitting
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|qpattern
init|=
literal|"\\'([^\\']+)\\'"
decl_stmt|;
comment|// get stuff between "'"
name|String
index|[]
index|[]
name|tests
init|=
block|{
comment|// group  pattern        input                    output
block|{
literal|"-1"
block|,
literal|"--"
block|,
literal|"aaa--bbb--ccc"
block|,
literal|"aaa bbb ccc"
block|}
block|,
block|{
literal|"-1"
block|,
literal|":"
block|,
literal|"aaa:bbb:ccc"
block|,
literal|"aaa bbb ccc"
block|}
block|,
block|{
literal|"-1"
block|,
literal|"\\p{Space}"
block|,
literal|"aaa   bbb \t\tccc  "
block|,
literal|"aaa bbb ccc"
block|}
block|,
block|{
literal|"-1"
block|,
literal|":"
block|,
literal|"boo:and:foo"
block|,
literal|"boo and foo"
block|}
block|,
block|{
literal|"-1"
block|,
literal|"o"
block|,
literal|"boo:and:foo"
block|,
literal|"b :and:f"
block|}
block|,
block|{
literal|"0"
block|,
literal|":"
block|,
literal|"boo:and:foo"
block|,
literal|": :"
block|}
block|,
block|{
literal|"0"
block|,
name|qpattern
block|,
literal|"aaa 'bbb' 'ccc'"
block|,
literal|"'bbb' 'ccc'"
block|}
block|,
block|{
literal|"1"
block|,
name|qpattern
block|,
literal|"aaa 'bbb' 'ccc'"
block|,
literal|"bbb ccc"
block|}
block|}
decl_stmt|;
for|for
control|(
name|String
index|[]
name|test
range|:
name|tests
control|)
block|{
name|TokenStream
name|stream
init|=
operator|new
name|PatternTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
index|[
literal|2
index|]
argument_list|)
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|test
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|test
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|out
init|=
name|tsToString
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// System.out.println( test[2] + " ==> " + out );
name|assertEquals
argument_list|(
literal|"pattern: "
operator|+
name|test
index|[
literal|1
index|]
operator|+
literal|" with input: "
operator|+
name|test
index|[
literal|2
index|]
argument_list|,
name|test
index|[
literal|3
index|]
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// Make sure it is the same as if we called 'split'
comment|// test disabled, as we remove empty tokens
comment|/*if( "-1".equals( test[0] ) ) {         String[] split = test[2].split( test[1] );         stream = tokenizer.create( new StringReader( test[2] ) );         int i=0;         for( Token t = stream.next(); null != t; t = stream.next() )          {           assertEquals( "split: "+test[1] + " "+i, split[i++], new String(t.termBuffer(), 0, t.termLength()) );         }       }*/
block|}
block|}
DECL|method|testOffsetCorrection
specifier|public
name|void
name|testOffsetCorrection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|INPUT
init|=
literal|"G&uuml;nther G&uuml;nther is here"
decl_stmt|;
comment|// create MappingCharFilter
name|List
argument_list|<
name|String
argument_list|>
name|mappingRules
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|mappingRules
operator|.
name|add
argument_list|(
literal|"\"&uuml;\" => \"Ã¼\""
argument_list|)
expr_stmt|;
name|NormalizeCharMap
name|normMap
init|=
operator|new
name|NormalizeCharMap
argument_list|()
decl_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"&uuml;"
argument_list|,
literal|"Ã¼"
argument_list|)
expr_stmt|;
name|CharStream
name|charStream
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|INPUT
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create PatternTokenizer
name|TokenStream
name|stream
init|=
operator|new
name|PatternTokenizer
argument_list|(
name|charStream
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[,;/\\s]+"
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"GÃ¼nther"
block|,
literal|"GÃ¼nther"
block|,
literal|"is"
block|,
literal|"here"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|13
block|,
literal|26
block|,
literal|29
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|12
block|,
literal|25
block|,
literal|28
block|,
literal|33
block|}
argument_list|)
expr_stmt|;
name|charStream
operator|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|INPUT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|PatternTokenizer
argument_list|(
name|charStream
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"GÃ¼nther"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"GÃ¼nther"
block|,
literal|"GÃ¼nther"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|12
block|,
literal|25
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**     * TODO: rewrite tests not to use string comparison.    * @deprecated only tests TermAttribute!    */
DECL|method|tsToString
specifier|private
specifier|static
name|String
name|tsToString
parameter_list|(
name|TokenStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// extra safety to enforce, that the state is not preserved and also
comment|// assign bogus values
name|in
operator|.
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"bogusTerm"
argument_list|)
expr_stmt|;
while|while
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|out
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"bogusTerm"
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
