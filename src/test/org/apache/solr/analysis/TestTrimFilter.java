begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
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
name|TokenStream
import|;
end_import
begin_comment
comment|/**  * @version $Id:$  */
end_comment
begin_class
DECL|class|TestTrimFilter
specifier|public
class|class
name|TestTrimFilter
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testTrim
specifier|public
name|void
name|testTrim
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
operator|new
name|TrimFilter
argument_list|(
operator|new
name|IterTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|" a "
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|"b   "
argument_list|,
literal|6
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|"cCc"
argument_list|,
literal|11
argument_list|,
literal|15
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|"   "
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
name|ts
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|token
operator|=
name|ts
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|token
operator|=
name|ts
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cCc"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|token
operator|=
name|ts
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|token
operator|=
name|ts
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|TrimFilter
argument_list|(
operator|new
name|IterTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|" a"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|"b "
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|" c "
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
literal|"   "
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Token
argument_list|>
name|expect
init|=
name|tokens
argument_list|(
literal|"a,1,1,2 b,1,0,1 c,1,1,2 ,1,3,3"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Token
argument_list|>
name|real
init|=
name|getTokens
argument_list|(
name|ts
argument_list|)
decl_stmt|;
for|for
control|(
name|Token
name|t
range|:
name|expect
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST:"
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Token
name|t
range|:
name|real
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"REAL:"
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
name|assertTokEqualOff
argument_list|(
name|expect
argument_list|,
name|real
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
