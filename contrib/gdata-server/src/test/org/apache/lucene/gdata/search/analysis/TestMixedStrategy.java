begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
package|;
end_package
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
name|document
operator|.
name|Field
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|TestMixedStrategy
specifier|public
class|class
name|TestMixedStrategy
extends|extends
name|TestCase
block|{
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"foo"
decl_stmt|;
DECL|field|BOOST
specifier|private
specifier|static
specifier|final
name|float
name|BOOST
init|=
literal|2.0f
decl_stmt|;
DECL|field|strategy
name|MixedContentStrategy
name|strategy
decl_stmt|;
DECL|field|field
specifier|private
name|IndexSchemaField
name|field
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|field
operator|=
operator|new
name|IndexSchemaField
argument_list|()
expr_stmt|;
name|field
operator|.
name|setName
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStore
argument_list|(
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
name|field
operator|.
name|setIndex
argument_list|(
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
expr_stmt|;
name|field
operator|.
name|setBoost
argument_list|(
name|BOOST
argument_list|)
expr_stmt|;
name|field
operator|.
name|setPath
argument_list|(
literal|"/path"
argument_list|)
expr_stmt|;
name|field
operator|.
name|setTypePath
argument_list|(
literal|"/path"
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|MixedContentStrategy
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/*      * Test method for      * 'org.apache.lucene.gdata.search.analysis.PlainTextStrategy.processIndexable(Indexable<?      * extends Node, ? extends ServerBaseEntry>)'      */
DECL|method|testProcessIndexable
specifier|public
name|void
name|testProcessIndexable
parameter_list|()
throws|throws
name|NotIndexableException
block|{
name|IndexableStub
name|stub
init|=
operator|new
name|IndexableStub
argument_list|()
decl_stmt|;
name|stub
operator|.
name|setReturnNull
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
name|stub
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"retun value is null must fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotIndexableException
name|e
parameter_list|)
block|{         }
name|assertNull
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|content
argument_list|)
expr_stmt|;
name|String
name|content
init|=
literal|"fooBar"
decl_stmt|;
name|stub
operator|.
name|setReturnNull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stub
operator|.
name|setReturnValueTextContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
name|stub
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|getClass
argument_list|()
argument_list|,
name|PlainTextStrategy
operator|.
name|class
argument_list|)
expr_stmt|;
name|content
operator|=
literal|"html"
expr_stmt|;
name|stub
operator|.
name|setReturnNull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stub
operator|.
name|setReturnValueTextContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
name|stub
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|getClass
argument_list|()
argument_list|,
name|HTMLStrategy
operator|.
name|class
argument_list|)
expr_stmt|;
name|content
operator|=
literal|"xhtml"
expr_stmt|;
name|stub
operator|.
name|setReturnNull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stub
operator|.
name|setReturnValueTextContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
name|stub
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
argument_list|,
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|strategy
operator|.
name|strategy
operator|.
name|getClass
argument_list|()
argument_list|,
name|XHtmlStrategy
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// test for xpath exc.
name|this
operator|.
name|field
operator|.
name|setPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|strategy
operator|.
name|processIndexable
argument_list|(
name|stub
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"path is null must fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotIndexableException
name|e
parameter_list|)
block|{         }
block|}
block|}
end_class
end_unit
