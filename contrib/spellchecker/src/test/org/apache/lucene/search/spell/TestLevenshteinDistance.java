begin_unit
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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
name|TestCase
import|;
end_import
begin_class
DECL|class|TestLevenshteinDistance
specifier|public
class|class
name|TestLevenshteinDistance
extends|extends
name|TestCase
block|{
DECL|field|sd
specifier|private
name|StringDistance
name|sd
init|=
operator|new
name|LevensteinDistance
argument_list|()
decl_stmt|;
DECL|method|testGetDistance
specifier|public
name|void
name|testGetDistance
parameter_list|()
block|{
name|float
name|d
init|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"al"
argument_list|,
literal|"al"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|d
operator|==
literal|1.0f
argument_list|)
expr_stmt|;
name|d
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"martha"
argument_list|,
literal|"marhta"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d
operator|>
literal|0.66
operator|&&
name|d
operator|<
literal|0.67
argument_list|)
expr_stmt|;
name|d
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"jones"
argument_list|,
literal|"johnson"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d
operator|>
literal|0.199
operator|&&
name|d
operator|<
literal|0.201
argument_list|)
expr_stmt|;
name|d
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"abcvwxyz"
argument_list|,
literal|"cabvwxyz"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d
operator|>
literal|0.749
operator|&&
name|d
operator|<
literal|0.751
argument_list|)
expr_stmt|;
name|d
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"dwayne"
argument_list|,
literal|"duane"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d
operator|>
literal|0.599
operator|&&
name|d
operator|<
literal|0.601
argument_list|)
expr_stmt|;
name|d
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"dixon"
argument_list|,
literal|"dicksonx"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d
operator|>
literal|0.199
operator|&&
name|d
operator|<
literal|0.201
argument_list|)
expr_stmt|;
name|d
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"six"
argument_list|,
literal|"ten"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d
operator|==
literal|0f
argument_list|)
expr_stmt|;
name|float
name|d1
init|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"zac ephron"
argument_list|,
literal|"zac efron"
argument_list|)
decl_stmt|;
name|float
name|d2
init|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"zac ephron"
argument_list|,
literal|"kai ephron"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|d1
operator|<
name|d2
argument_list|)
expr_stmt|;
name|d1
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"brittney spears"
argument_list|,
literal|"britney spears"
argument_list|)
expr_stmt|;
name|d2
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
literal|"brittney spears"
argument_list|,
literal|"brittney startzman"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d1
operator|>
name|d2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
