begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|standard
operator|.
name|StandardTokenizer
import|;
end_import
begin_comment
comment|/**  * Test QueryParser's ability to deal with Analyzers that return more  * than one token per position.  *   * @author Daniel Naber  */
end_comment
begin_class
DECL|class|TestMultiAnalyzer
specifier|public
class|class
name|TestMultiAnalyzer
extends|extends
name|TestCase
block|{
DECL|field|multiToken
specifier|private
specifier|static
name|int
name|multiToken
init|=
literal|0
decl_stmt|;
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|ParseException
block|{
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|""
argument_list|,
operator|new
name|TestAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
comment|// trivial, no multiple tokens:
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo foobar"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo foobar"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// two tokens at the same position:
name|assertEquals
argument_list|(
literal|"(multi multi2) foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"multi foo"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo (multi multi2)"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo multi"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(multi multi2) (multi multi2)"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"multi multi"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(foo (multi multi2)) +(bar (multi multi2))"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"+(foo multi) +(bar multi)"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(foo (multi multi2)) field:\"bar (multi multi2)\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"+(foo multi) field:\"bar multi\""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrases:
name|assertEquals
argument_list|(
literal|"\"(multi multi2) foo\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"foo (multi multi2)\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"foo multi\""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"foo (multi multi2) foobar (multi multi2)\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"foo multi foobar multi\""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// fields:
name|assertEquals
argument_list|(
literal|"(field:multi field:multi2) field:foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"field:multi field:foo"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:\"(multi multi2) foo\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"field:\"multi foo\""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// three tokens at one position:
name|assertEquals
argument_list|(
literal|"triplemulti multi3 multi2"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"triplemulti"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo (triplemulti multi3 multi2) foobar"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo triplemulti foobar"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrase with non-default slop:
name|assertEquals
argument_list|(
literal|"\"(multi multi2) foo\"~10"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\"~10"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrase with non-default boost:
name|assertEquals
argument_list|(
literal|"\"(multi multi2) foo\"^2.0"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\"^2"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// non-default operator:
name|qp
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParser
operator|.
name|AND_OPERATOR
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(multi multi2) +foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"multi foo"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expands "multi" to "multi" and "multi2", both at the same position,    * and expands "triplemulti" to "triplemulti", "multi3", and "multi2".      */
DECL|class|TestAnalyzer
specifier|private
class|class
name|TestAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|TestAnalyzer
specifier|public
name|TestAnalyzer
parameter_list|()
block|{     }
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
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|TestFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|class|TestFilter
specifier|private
specifier|final
class|class
name|TestFilter
extends|extends
name|TokenFilter
block|{
DECL|field|prevToken
specifier|private
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
name|prevToken
decl_stmt|;
DECL|method|TestFilter
specifier|public
name|TestFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
if|if
condition|(
name|multiToken
operator|>
literal|0
condition|)
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
name|token
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
argument_list|(
literal|"multi"
operator|+
operator|(
name|multiToken
operator|+
literal|1
operator|)
argument_list|,
name|prevToken
operator|.
name|startOffset
argument_list|()
argument_list|,
name|prevToken
operator|.
name|endOffset
argument_list|()
argument_list|,
name|prevToken
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|token
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|multiToken
operator|--
expr_stmt|;
return|return
name|token
return|;
block|}
else|else
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
name|prevToken
operator|=
name|t
expr_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|text
init|=
name|t
operator|.
name|termText
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|equals
argument_list|(
literal|"triplemulti"
argument_list|)
condition|)
block|{
name|multiToken
operator|=
literal|2
expr_stmt|;
return|return
name|t
return|;
block|}
elseif|else
if|if
condition|(
name|text
operator|.
name|equals
argument_list|(
literal|"multi"
argument_list|)
condition|)
block|{
name|multiToken
operator|=
literal|1
expr_stmt|;
return|return
name|t
return|;
block|}
else|else
block|{
return|return
name|t
return|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
