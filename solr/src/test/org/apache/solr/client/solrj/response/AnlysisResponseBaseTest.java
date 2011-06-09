begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
begin_comment
comment|/**  * A Test case for the {@link AnalysisResponseBase} class.  *  *  * @since solr 1.4  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AnlysisResponseBaseTest
specifier|public
class|class
name|AnlysisResponseBaseTest
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Tests the {@link AnalysisResponseBase#buildTokenInfo(org.apache.solr.common.util.NamedList)} method.    */
annotation|@
name|Test
DECL|method|testBuildTokenInfo
specifier|public
name|void
name|testBuildTokenInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|NamedList
name|tokenNL
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|tokenNL
operator|.
name|add
argument_list|(
literal|"text"
argument_list|,
literal|"JUMPING"
argument_list|)
expr_stmt|;
name|tokenNL
operator|.
name|add
argument_list|(
literal|"type"
argument_list|,
literal|"word"
argument_list|)
expr_stmt|;
name|tokenNL
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tokenNL
operator|.
name|add
argument_list|(
literal|"end"
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|tokenNL
operator|.
name|add
argument_list|(
literal|"position"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|AnalysisResponseBase
name|response
init|=
operator|new
name|AnalysisResponseBase
argument_list|()
decl_stmt|;
name|AnalysisResponseBase
operator|.
name|TokenInfo
name|tokenInfo
init|=
name|response
operator|.
name|buildTokenInfo
argument_list|(
name|tokenNL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"JUMPING"
argument_list|,
name|tokenInfo
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|tokenInfo
operator|.
name|getRawText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"word"
argument_list|,
name|tokenInfo
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tokenInfo
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|tokenInfo
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokenInfo
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenInfo
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
name|tokenNL
operator|.
name|add
argument_list|(
literal|"rawText"
argument_list|,
literal|"JUMPING1"
argument_list|)
expr_stmt|;
name|tokenNL
operator|.
name|add
argument_list|(
literal|"match"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tokenInfo
operator|=
name|response
operator|.
name|buildTokenInfo
argument_list|(
name|tokenNL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JUMPING"
argument_list|,
name|tokenInfo
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JUMPING1"
argument_list|,
name|tokenInfo
operator|.
name|getRawText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"word"
argument_list|,
name|tokenInfo
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tokenInfo
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|tokenInfo
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokenInfo
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenInfo
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the {@link AnalysisResponseBase#buildPhases(org.apache.solr.common.util.NamedList)} )} method.    */
annotation|@
name|Test
DECL|method|testBuildPhases
specifier|public
name|void
name|testBuildPhases
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AnalysisResponseBase
operator|.
name|TokenInfo
name|tokenInfo
init|=
operator|new
name|AnalysisResponseBase
operator|.
name|TokenInfo
argument_list|(
literal|"text"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"Tokenizer"
argument_list|,
name|buildFakeTokenInfoList
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"Filter1"
argument_list|,
name|buildFakeTokenInfoList
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"Filter2"
argument_list|,
name|buildFakeTokenInfoList
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"Filter3"
argument_list|,
name|buildFakeTokenInfoList
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|AnalysisResponseBase
name|response
init|=
operator|new
name|AnalysisResponseBase
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenInfo
name|buildTokenInfo
parameter_list|(
name|NamedList
name|tokenNL
parameter_list|)
block|{
return|return
name|tokenInfo
return|;
block|}
block|}
decl_stmt|;
name|List
argument_list|<
name|AnalysisResponseBase
operator|.
name|AnalysisPhase
argument_list|>
name|phases
init|=
name|response
operator|.
name|buildPhases
argument_list|(
name|nl
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|phases
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertPhase
argument_list|(
name|phases
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"Tokenizer"
argument_list|,
literal|6
argument_list|,
name|tokenInfo
argument_list|)
expr_stmt|;
name|assertPhase
argument_list|(
name|phases
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"Filter1"
argument_list|,
literal|5
argument_list|,
name|tokenInfo
argument_list|)
expr_stmt|;
name|assertPhase
argument_list|(
name|phases
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|"Filter2"
argument_list|,
literal|4
argument_list|,
name|tokenInfo
argument_list|)
expr_stmt|;
name|assertPhase
argument_list|(
name|phases
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|"Filter3"
argument_list|,
literal|3
argument_list|,
name|tokenInfo
argument_list|)
expr_stmt|;
block|}
comment|//================================================ Helper Methods ==================================================
DECL|method|buildFakeTokenInfoList
specifier|private
name|List
argument_list|<
name|NamedList
argument_list|>
name|buildFakeTokenInfoList
parameter_list|(
name|int
name|numberOfTokens
parameter_list|)
block|{
name|List
argument_list|<
name|NamedList
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|NamedList
argument_list|>
argument_list|(
name|numberOfTokens
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfTokens
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|assertPhase
specifier|private
name|void
name|assertPhase
parameter_list|(
name|AnalysisResponseBase
operator|.
name|AnalysisPhase
name|phase
parameter_list|,
name|String
name|expectedClassName
parameter_list|,
name|int
name|expectedTokenCount
parameter_list|,
name|AnalysisResponseBase
operator|.
name|TokenInfo
name|expectedToken
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedClassName
argument_list|,
name|phase
operator|.
name|getClassName
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AnalysisResponseBase
operator|.
name|TokenInfo
argument_list|>
name|tokens
init|=
name|phase
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedTokenCount
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AnalysisResponseBase
operator|.
name|TokenInfo
name|token
range|:
name|tokens
control|)
block|{
name|assertSame
argument_list|(
name|expectedToken
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
