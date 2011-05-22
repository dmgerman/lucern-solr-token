begin_unit
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
operator|.
name|DocumentTestCase
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
name|ant
operator|.
name|TextDocument
import|;
end_import
begin_class
DECL|class|TextDocumentTest
specifier|public
class|class
name|TextDocumentTest
extends|extends
name|DocumentTestCase
block|{
DECL|field|doc
name|TextDocument
name|doc
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
name|doc
operator|=
operator|new
name|TextDocument
argument_list|(
name|getFile
argument_list|(
literal|"test.txt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoc
specifier|public
name|void
name|testDoc
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Contents"
argument_list|,
literal|"Test Contents"
argument_list|,
name|doc
operator|.
name|getContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|doc
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
