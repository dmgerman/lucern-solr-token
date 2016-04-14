begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.xml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
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
name|Analyzer
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
begin_class
DECL|class|TestCorePlusQueriesParser
specifier|public
class|class
name|TestCorePlusQueriesParser
extends|extends
name|TestCoreParser
block|{
DECL|method|newCoreParser
specifier|protected
name|CoreParser
name|newCoreParser
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
return|return
operator|new
name|CorePlusQueriesParser
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
DECL|method|testLikeThisQueryXML
specifier|public
name|void
name|testLikeThisQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"LikeThisQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"like this"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostingQueryXML
specifier|public
name|void
name|testBoostingQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BoostingQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"boosting "
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
