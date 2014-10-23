begin_unit
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * validate that assertions are enabled during tests  */
end_comment
begin_class
DECL|class|TestAssertions
specifier|public
class|class
name|TestAssertions
extends|extends
name|LuceneTestCase
block|{
DECL|class|TestTokenStream1
specifier|static
class|class
name|TestTokenStream1
extends|extends
name|TokenStream
block|{
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|TestTokenStream2
specifier|static
specifier|final
class|class
name|TestTokenStream2
extends|extends
name|TokenStream
block|{
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|TestTokenStream3
specifier|static
class|class
name|TestTokenStream3
extends|extends
name|TokenStream
block|{
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|testTokenStreams
specifier|public
name|void
name|testTokenStreams
parameter_list|()
block|{
operator|new
name|TestTokenStream1
argument_list|()
expr_stmt|;
operator|new
name|TestTokenStream2
argument_list|()
expr_stmt|;
try|try
block|{
operator|new
name|TestTokenStream3
argument_list|()
expr_stmt|;
if|if
condition|(
name|assertsAreEnabled
condition|)
block|{
name|fail
argument_list|(
literal|"TestTokenStream3 should fail assertion"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
end_unit
