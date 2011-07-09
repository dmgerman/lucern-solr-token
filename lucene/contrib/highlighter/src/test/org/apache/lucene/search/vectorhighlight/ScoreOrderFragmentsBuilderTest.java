begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|TermQuery
import|;
end_import
begin_class
DECL|class|ScoreOrderFragmentsBuilderTest
specifier|public
class|class
name|ScoreOrderFragmentsBuilderTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|test3Frags
specifier|public
name|void
name|test3Frags
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|F
argument_list|,
literal|"c"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
name|query
argument_list|,
literal|"a b b b b b b b b b b b a b a b b b b b c a a b b"
argument_list|)
decl_stmt|;
name|ScoreOrderFragmentsBuilder
name|sofb
init|=
operator|new
name|ScoreOrderFragmentsBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|f
init|=
name|sofb
operator|.
name|createFragments
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|f
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// check score order
name|assertEquals
argument_list|(
literal|"<b>c</b><b>a</b><b>a</b> b b "
argument_list|,
name|f
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b b<b>a</b> b<b>a</b> b b b b b "
argument_list|,
name|f
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>a</b> b b b b b b b b b "
argument_list|,
name|f
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|ffl
specifier|private
name|FieldFragList
name|ffl
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|indexValue
parameter_list|)
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
name|indexValue
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimpleFragListBuilder
argument_list|()
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|20
argument_list|)
return|;
block|}
block|}
end_class
end_unit
