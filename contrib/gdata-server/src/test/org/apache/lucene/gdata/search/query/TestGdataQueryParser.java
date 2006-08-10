begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.query
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
name|query
package|;
end_package
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
name|StandardAnalyzer
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
name|IndexSchema
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
name|QueryParser
operator|.
name|Operator
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
begin_class
DECL|class|TestGdataQueryParser
specifier|public
class|class
name|TestGdataQueryParser
extends|extends
name|TestCase
block|{
DECL|method|testConstructor
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
name|String
name|field
init|=
literal|"someField"
decl_stmt|;
name|IndexSchema
name|s
init|=
operator|new
name|IndexSchema
argument_list|()
decl_stmt|;
name|s
operator|.
name|setDefaultSearchField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|GDataQueryParser
name|p
init|=
operator|new
name|GDataQueryParser
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|field
argument_list|,
name|p
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Operator
operator|.
name|AND
argument_list|,
name|p
operator|.
name|getDefaultOperator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandardAnalyzer
operator|.
name|class
argument_list|,
name|p
operator|.
name|getAnalyzer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
