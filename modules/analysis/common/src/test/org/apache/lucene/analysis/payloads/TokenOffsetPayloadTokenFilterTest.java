begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|core
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|PayloadAttribute
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
name|index
operator|.
name|Payload
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
name|StringReader
import|;
end_import
begin_class
DECL|class|TokenOffsetPayloadTokenFilterTest
specifier|public
class|class
name|TokenOffsetPayloadTokenFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|TokenOffsetPayloadTokenFilterTest
specifier|public
name|TokenOffsetPayloadTokenFilterTest
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|test
init|=
literal|"The quick red fox jumped over the lazy brown dogs"
decl_stmt|;
name|TokenOffsetPayloadTokenFilter
name|nptf
init|=
operator|new
name|TokenOffsetPayloadTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
name|nptf
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|nptf
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|nptf
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|Payload
name|pay
init|=
name|payloadAtt
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"pay is null and it shouldn't be"
argument_list|,
name|pay
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|pay
operator|.
name|getData
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|PayloadHelper
operator|.
name|decodeInt
argument_list|(
name|data
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|start
operator|+
literal|" does not equal: "
operator|+
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|start
operator|==
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|end
init|=
name|PayloadHelper
operator|.
name|decodeInt
argument_list|(
name|data
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|end
operator|+
literal|" does not equal: "
operator|+
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|,
name|end
operator|==
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
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
literal|10
argument_list|,
name|count
operator|==
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
