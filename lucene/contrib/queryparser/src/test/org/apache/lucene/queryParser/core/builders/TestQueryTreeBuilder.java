begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.core.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|builders
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|queryParser
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|util
operator|.
name|UnescapedCharSequence
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestQueryTreeBuilder
specifier|public
class|class
name|TestQueryTreeBuilder
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testSetFieldBuilder
specifier|public
name|void
name|testSetFieldBuilder
parameter_list|()
throws|throws
name|QueryNodeException
block|{
name|QueryTreeBuilder
name|qtb
init|=
operator|new
name|QueryTreeBuilder
argument_list|()
decl_stmt|;
name|qtb
operator|.
name|setBuilder
argument_list|(
literal|"field"
argument_list|,
operator|new
name|DummyBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|result
init|=
name|qtb
operator|.
name|build
argument_list|(
operator|new
name|FieldQueryNode
argument_list|(
operator|new
name|UnescapedCharSequence
argument_list|(
literal|"field"
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyBuilder
specifier|private
specifier|static
class|class
name|DummyBuilder
implements|implements
name|QueryBuilder
block|{
DECL|method|build
specifier|public
name|Object
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
literal|"OK"
return|;
block|}
block|}
block|}
end_class
end_unit
