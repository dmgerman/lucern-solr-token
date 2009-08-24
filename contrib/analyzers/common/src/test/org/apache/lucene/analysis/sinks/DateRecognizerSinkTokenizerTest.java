begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
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
name|text
operator|.
name|SimpleDateFormat
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
name|TeeSinkTokenFilter
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
name|TeeSinkTokenFilter
operator|.
name|SinkTokenStream
import|;
end_import
begin_class
DECL|class|DateRecognizerSinkTokenizerTest
specifier|public
class|class
name|DateRecognizerSinkTokenizerTest
extends|extends
name|TestCase
block|{
DECL|method|DateRecognizerSinkTokenizerTest
specifier|public
name|DateRecognizerSinkTokenizerTest
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{   }
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|DateRecognizerSinkFilter
name|sinkFilter
init|=
operator|new
name|DateRecognizerSinkFilter
argument_list|(
operator|new
name|SimpleDateFormat
argument_list|(
literal|"MM/dd/yyyy"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|test
init|=
literal|"The quick red fox jumped over the lazy brown dogs on 7/11/2006  The dogs finally reacted on 7/12/2006"
decl_stmt|;
name|TeeSinkTokenFilter
name|tee
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SinkTokenStream
name|sink
init|=
name|tee
operator|.
name|newSinkTokenStream
argument_list|(
name|sinkFilter
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|tee
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tee
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|+
literal|" does not equal: "
operator|+
literal|18
argument_list|,
name|count
operator|==
literal|18
argument_list|)
expr_stmt|;
name|int
name|sinkCount
init|=
literal|0
decl_stmt|;
name|sink
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|sink
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|sinkCount
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"sink Size: "
operator|+
name|sinkCount
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|sinkCount
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
